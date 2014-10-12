package com.tsang.tspeak;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.net.RequestListener;
import com.tsang.tspeak.model.Emotions;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NewStatusActivity extends BaseActivity  implements
		RequestListener {

	public static final String TAG = "NewStatusActivity";
	/** 发微博 */
	public static final int TYPE_NEW = 0xd1;
	/** 转发微博 */
	public static final int TYPE_REPOST = 0xd2;
	/** 评论微博 */
	public static final int TYPE_COMMENT = 0xd3;

	private ImageButton ibtnAddPic;
	private ImageButton ibtnAt;
	private ImageButton ibtnEmotion;
	private ImageView ivPicPreview;
	private Button btnSent;
	private GridView gdEmotionView;
	private EditText etStatusContent;
	private CheckBox mCheckBox;
	private String mToken;
	private int mType;
	private Long mWeiboID;

	NotificationManager manager;
	Notification notify_sending;
	Notification notify_send_done;
	Notification notify_send_failed;

	static final int NOTIFICATION_SENDING = 0xc1;
	static final int NOTIFICATION_SEND_DONE = 0xc2;
	static final int NOTIFICATION_SEND_FAILED = 0xc3;

	private static final int MSG_SEEN_SECCESS = 0x23;
	private static final int MSG_SEEN_FAILED = 0x24;

	/** 本地图片路径 */
	private String picPath = "";
	private static final int PHOTO_WITH_CAMERA = 1010;// 拍摄照片
	private static final int PHOTO_WITH_DATA = 1020;// 从SD中得到照片
	private static final int MESSAGE_AT = 1030;// 拍摄照片
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");// 拍摄照片存储的文件夹路劲
	private File capturefile;// 拍摄的照片文件

	String[] choices;

	List<Emotions> emotionList;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_SEEN_SECCESS:

				manager.cancel(NOTIFICATION_SENDING);

				manager.notify(NOTIFICATION_SEND_DONE, notify_send_done);
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

					}
				}, 2000);
				manager.cancel(NOTIFICATION_SEND_DONE);
				break;
			case MSG_SEEN_FAILED:

				manager.cancel(NOTIFICATION_SENDING);
				manager.notify(NOTIFICATION_SEND_FAILED, notify_send_failed);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writeweibo);

		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notify_sending = new Notification.Builder(this).setAutoCancel(true)
				.setTicker(getResources().getString(R.string.notify_sending))
				.setSmallIcon(R.drawable.ic_action_send).build();
		notify_send_done = new Notification.Builder(this).setAutoCancel(true)
				.setTicker(getResources().getString(R.string.notify_send_done))
				.setSmallIcon(R.drawable.notify_done).build();
		notify_send_failed = new Notification.Builder(this)
				.setAutoCancel(true)
				.setTicker(
						getResources().getString(R.string.notify_send_failed))
				.setContentTitle(
						getResources().getString(R.string.notify_send_failed))
				.setContentText(
						getResources().getString(R.string.network_error))
				.setSmallIcon(R.drawable.notify_cancel).build();

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ibtnAddPic = (ImageButton) findViewById(R.id.write_menu_add_pic);
		ibtnAt = (ImageButton) findViewById(R.id.write_menu_at);
		ibtnEmotion = (ImageButton) findViewById(R.id.write_menu_emoji);
		btnSent = (Button) findViewById(R.id.write_menu_send);
		etStatusContent = (EditText) findViewById(R.id.write_new_content);
		ivPicPreview = (ImageView) findViewById(R.id.write_image_preview);
		mCheckBox = (CheckBox) findViewById(R.id.write_cb_repost_or_comment);
		gdEmotionView = (GridView) findViewById(R.id.write_gd_emotion);

		Intent intent = getIntent();
		mToken = intent.getStringExtra("token");

		mType = intent.getIntExtra("type", TYPE_NEW);
		switch (mType) {
		case TYPE_NEW:
			getSupportActionBar().setTitle(
					getResources().getString(R.string.write));
			break;
		case TYPE_REPOST:
			getSupportActionBar().setTitle(
					getResources().getString(R.string.repost));
			ibtnAddPic.setVisibility(View.GONE);
			mCheckBox.setVisibility(View.VISIBLE);
			mCheckBox.setText(getResources().getString(
					R.string.comment_in_the_sametime));
			mWeiboID = Long.parseLong(intent.getStringExtra("weiboID"));
			boolean hasRetweet = intent.getBooleanExtra("repost_hasretweet",
					false);
			if (hasRetweet) {
				String name = intent.getStringExtra("repost_name");
				String text = intent.getStringExtra("repost_text");
				etStatusContent.setText("//@" + name + ":" + text);
			}
			etStatusContent.setSelection(0);

			break;
		case TYPE_COMMENT:
			getSupportActionBar().setTitle(
					getResources().getString(R.string.comment));
			ibtnAddPic.setVisibility(View.GONE);
			mCheckBox.setVisibility(View.VISIBLE);
			mCheckBox.setText(getResources().getString(
					R.string.repost_in_the_sametime));
			mWeiboID = Long.parseLong(intent.getStringExtra("weiboID"));

			break;
		default:
			break;
		}

		emotionList = MainActivity.emotionList;
		choices = getResources().getStringArray(R.array.photo_list);
		setLinsener();

	}

	private void setLinsener() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Dialog);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		final ListAdapter listAdapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);
		builder.setSingleChoiceItems(listAdapter, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						switch (which) {
						case 0:
							if (Environment.getExternalStorageState().equals(
									Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								capturefile = new File(PHOTO_DIR,
										getPhotoFileName());
								try {
									capturefile.createNewFile();
									Uri uri = Uri.fromFile(capturefile);
									intent.putExtra(MediaStore.EXTRA_OUTPUT,
											uri);
								} catch (Exception e) {
									// TODO: handle exception
								}
								startActivityForResult(intent,
										PHOTO_WITH_CAMERA);
							} else {
								Toast.makeText(getApplicationContext(),
										"没有SD卡", Toast.LENGTH_SHORT).show();
							}
							break;
						case 1:// 从相册中去获取
							Intent intent = new Intent();
							/* 开启Pictures画面Type设定为image */
							intent.setType("image/*");
							/* 使用Intent.ACTION_GET_CONTENT这个Action */
							intent.setAction(Intent.ACTION_PICK);
							/* 取得相片后返回本画面 */
							startActivityForResult(intent, PHOTO_WITH_DATA);
							break;
						}
					}
				});

		ibtnAddPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.create().show();
			}
		});

		etStatusContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int sum = Utils.length(etStatusContent.getText().toString());
				int num = 140 - sum;
				boolean contentNumBelow140 = (num >= 0);
				if (!contentNumBelow140) {
					etStatusContent
							.setError(getString(R.string.content_words_number_more_than_140));
				}
			}
		});

		etStatusContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (gdEmotionView.getVisibility() == View.VISIBLE) {
					gdEmotionView.setVisibility(View.GONE);
					ibtnEmotion.setImageResource(R.drawable.ic_action_user);
				}
			}
		});

		btnSent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (mType) {
				case TYPE_NEW:
					if (canSend()) {
						if (picPath == null || picPath == "") {
							sendStatus(mToken, etStatusContent.getText()
									.toString());
						} else {
							new Thread(sendRunnable).start();
						}
						manager.notify(NOTIFICATION_SENDING, notify_sending);
						finish();
					}
					break;
				case TYPE_REPOST:
					if (canRepost()) {
						if (mCheckBox.isChecked()) {
							sendRepostStatus(mToken, mWeiboID, etStatusContent
									.getText().toString(), 1);
						} else {
							sendRepostStatus(mToken, mWeiboID, etStatusContent
									.getText().toString(), 0);
						}
						manager.notify(NOTIFICATION_SENDING, notify_sending);
						finish();
					}

					break;
				case TYPE_COMMENT:
					if (canSend()) {
						if (mCheckBox.isChecked()) {
							sendRepostStatus(mToken, mWeiboID, etStatusContent
									.getText().toString(), 1);
						} else {
							sendComment(mToken, mWeiboID, etStatusContent
									.getText().toString());
						}
						manager.notify(NOTIFICATION_SENDING, notify_sending);
						finish();
					}
					break;
				}

			}

		});

		ibtnEmotion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (gdEmotionView.getVisibility() == View.GONE) {

					ibtnEmotion.setImageResource(R.drawable.ic_action_keyboard);
					// 隐藏软键盘
					imm.hideSoftInputFromWindow(v.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					gdEmotionView.setVisibility(View.VISIBLE);

					ArrayList<HashMap<String, Object>> listImageItem = new ArrayList<HashMap<String, Object>>();
					Emotions emotions;
					if (emotionList.isEmpty()) {

					} else {
						for (int i = 0; i < 72; i++) {
							emotions = emotionList.get(i);
							if (emotions != null) {
								HashMap<String, Object> map = new HashMap<String, Object>();

								Field f;
								try {
									f = (Field) R.drawable.class
											.getDeclaredField(emotions
													.getImageName());
									int j = f.getInt(R.drawable.class);
									map.put("ItemImage", j);// 添加图像资源的ID
									listImageItem.add(map);
								} catch (NoSuchFieldException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						SimpleAdapter emotionAdapter = new SimpleAdapter(
								NewStatusActivity.this, listImageItem,
								R.layout.item_emotion,
								new String[] { "ItemImage" },
								new int[] { R.id.item_emotion });
						gdEmotionView.setAdapter(emotionAdapter);
					}

				} else {
					gdEmotionView.setVisibility(View.GONE);
					ibtnEmotion.setImageResource(R.drawable.ic_action_user);
					// 显示软键盘
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		gdEmotionView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Emotions emotions = emotionList.get(position);
				int cursor = etStatusContent.getSelectionStart();
				etStatusContent.getText().insert(
						cursor,
						Utils.txtToImg(emotions.getPhrase(),
								NewStatusActivity.this));
			}
		});

		ibtnAt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewStatusActivity.this,
						FriendsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("token", mToken);
				bundle.putString("uid", MainActivity.mUId);
				intent.putExtras(bundle);
				startActivityForResult(intent, MESSAGE_AT);
			}
		});
	}

	/** 可以发送 */
	private boolean canSend() {
		boolean haveContent = !TextUtils.isEmpty(etStatusContent.getText()
				.toString());
		boolean haveToken = !TextUtils.isEmpty(mToken);

		int sum = Utils.length(etStatusContent.getText().toString());
		int num = 140 - sum;
		boolean contentNumBelow140 = (num >= 0);

		if (haveContent && haveToken && contentNumBelow140) {
			return true;
		} else {
			if (!haveContent && !haveToken) {

			} else if (!haveContent) {
				etStatusContent
						.setError(getString(R.string.content_cant_be_empty));
			} else if (!haveToken) {
				etStatusContent
						.setError(getString(R.string.donot_have_account));
			}

		}
		return false;
	}

	/** 可以转发 */
	private boolean canRepost() {

		boolean haveToken = !TextUtils.isEmpty(mToken);

		int sum = Utils.length(etStatusContent.getText().toString());
		int num = 140 - sum;
		boolean contentNumBelow140 = (num >= 0);

		if (haveToken && contentNumBelow140) {
			return true;
		} else if (!haveToken) {
			etStatusContent.setError(getString(R.string.donot_have_account));
		}

		return false;
	}

	/*
	 * 通过相机回传图片的文件名
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		File file = null;
		Bitmap pic = null;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PHOTO_WITH_CAMERA:// 获取拍摄的文件
				picPath = capturefile.getAbsolutePath();
				Log.e(TAG, "CamaraPicPath=========>" + picPath);
				file = new File(picPath);
				pic = Utils.decodeFile(file);

				// Uri uris = Uri.fromFile(file);
				// ivPicPreview.setImageURI(uris);
				ivPicPreview.setImageBitmap(pic);

				break;
			case PHOTO_WITH_DATA:// 获取从图库选择的文件
				Uri uri = data.getData();
				Log.e(TAG, "uri=========>" + uri);
				String scheme = uri.getScheme();
				Log.e(TAG, "scheme=========>" + scheme);
				if (scheme.equalsIgnoreCase("file")) {
					picPath = uri.getPath();
					Log.e(TAG, "picPath=========>" + picPath);
					file = new File(picPath);
					pic = Utils.decodeFile(file);
					ivPicPreview.setImageBitmap(pic);

				} else if (scheme.equalsIgnoreCase("content")) {
					Cursor cursor = getContentResolver().query(uri, null, null,
							null, null);
					cursor.moveToFirst();
					picPath = cursor.getString(1);
					Log.e(TAG, "picPath=========>" + picPath);
					file = new File(picPath);
					// pic = Utils.decodeFile(file);
					ivPicPreview.setImageURI(uri);

				}
				break;
			case MESSAGE_AT:
				Bundle bundle = data.getExtras();
				String name = "@"+bundle.getString("name")+" ";
				int cursor = etStatusContent.getSelectionStart();
				etStatusContent.getText().insert(cursor, name);
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 对一条微博进行评论
	 * 
	 * @param token
	 * @param weiboid
	 * @param comment
	 *            评论内容
	 */
	private void sendComment(String token, long weiboid, String comment) {
		WeiboParameters requestParams = new WeiboParameters();
		requestParams.add("access_token", token);
		requestParams.add("id", weiboid);
		requestParams.add("comment", comment);

		AsyncWeiboRunner.request(Constants.COMMENT_STATUS_URL, requestParams,
				"POST", this);
	}

	/**
	 * 转发微博
	 * 
	 * @param token
	 * @param weiboid
	 * @param status
	 *            添加的转发文本
	 * @param is_comment
	 *            是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论
	 */
	private void sendRepostStatus(String token, long weiboid, String status,
			int is_comment) {
		WeiboParameters requestParams = new WeiboParameters();
		requestParams.add("access_token", token);
		requestParams.add("id", weiboid);
		requestParams.add("status", status);
		requestParams.add("is_comment", is_comment);

		AsyncWeiboRunner.request(Constants.REPOST_STATUS_URL, requestParams,
				"POST", this);
	}

	/**
	 * 发送文字微博
	 * 
	 * @param token
	 * @param status
	 */
	private void sendStatus(String token, String status) {
		WeiboParameters requestParams = new WeiboParameters();
		requestParams.add("access_token", token);
		requestParams.add("status", status);

		AsyncWeiboRunner.request(Constants.UPDATE_STATUS_URL, requestParams,
				"POST", this);
	}

	/**
	 * 发送带图片的微博
	 */
	Runnable sendRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			WeiboParameters requestParams = new WeiboParameters();
			requestParams.add("access_token", mToken);
			requestParams.add("status", etStatusContent.getText().toString());
			requestParams.add("pic", "");

			String result = null;
			JSONObject resultJson;
			try {
				result = HttpManager.openUrl(Constants.UPLOAD_STATUS_URL,
						"POST", requestParams, picPath);
				resultJson = new JSONObject(result);
				// Log.e(TAG, "resultJson======>" + resultJson);
				if (resultJson.has("error")) {
					mHandler.sendEmptyMessage(MSG_SEEN_FAILED);
				} else {
					mHandler.sendEmptyMessage(MSG_SEEN_SECCESS);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	@Override
	public void onComplete(String response) {
		// TODO Auto-generated method stub
		// manager.notify(NOTIFICATION_SENDING, notify_sending);
		if (response.contains("error")) {
			mHandler.sendEmptyMessage(MSG_SEEN_FAILED);
			Log.e(TAG, "error:======>" + response);
		} else {
			mHandler.sendEmptyMessage(MSG_SEEN_SECCESS);
		}
	}

	@Override
	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(MSG_SEEN_SECCESS);
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(MSG_SEEN_FAILED);
		e.printStackTrace();
		Log.e(TAG, "exception=====>" + e.getMessage());
	}

	@Override
	public void onError(WeiboException e) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(MSG_SEEN_FAILED);
		e.printStackTrace();
		Log.e(TAG, "error=======>" + e.getMessage());
	}

}
