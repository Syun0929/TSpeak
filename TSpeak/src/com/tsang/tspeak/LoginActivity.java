package com.tsang.tspeak;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.UIUtils;
import com.tsang.tspeak.adapter.LoginAdapter;
import com.tsang.tspeak.model.LoginInfo;
import com.tsang.tspeak.model.db.TSpeakDataHelper;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

public class LoginActivity extends BaseActivity implements OnDismissCallback {

	private static final String TAG = "LoginActivity";
	private ListView mListView;
	private LoginAdapter mLoginAdapter;
	private ImageButton mBtnAdd;
	private TSpeakDataHelper dataHelper;
	private List<LoginInfo> mLoginList;
	private String mToken;
	private String mUserId;
	private AdapterContextMenuInfo menuInfo;

	/** 消息 */
	private static final int MSG_DATA_CHANGE = 1;
	private static final int MSG_GETTOKEN_FAILED = 11;
	private static final int MSG_ = 2;

	/** 微博 Web 授权接口类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;
	/** 获取到的 Code */
	private String mCode;
	/** 获取到的 AccessToken */
	private Oauth2AccessToken mAccessToken;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_DATA_CHANGE:
				mLoginAdapter.setmList(mLoginList);

				break;
			case MSG_GETTOKEN_FAILED:
				Toast.makeText(LoginActivity.this,
						R.string.weibosdk_demo_toast_obtain_token_failed,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	RelativeLayout mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mLayout = (RelativeLayout) findViewById(R.id.login_layout);
		Utils.setStatusBarTheme(this, mLayout);
		getSupportActionBar().setTitle(R.string.login);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		mListView = (ListView) findViewById(R.id.login_list);
		mBtnAdd = (ImageButton) findViewById(R.id.login_btn_adduser);
		dataHelper = new TSpeakDataHelper(this);
		mLoginList = dataHelper.GetLoginList();
		mLoginAdapter = new LoginAdapter(LoginActivity.this, mLoginList);
		AnimationAdapter animationAdapter = new AlphaInAnimationAdapter(
				new SwipeDismissAdapter(mLoginAdapter, this));
		animationAdapter.setAbsListView(mListView);
		mListView.setAdapter(animationAdapter);

		// 初始化微博对象
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		mBtnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWeiboAuth.authorize(new AuthListener(),
						WeiboAuth.OBTAIN_AUTH_CODE);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LoginInfo info = mLoginList.get(position);
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				Bundle data = new Bundle();
				data.putString("token", info.getToken());
				data.putString("uid", info.getUserID());
				intent.putExtras(data);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		mListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						// TODO Auto-generated method stub
						menu.add(Menu.NONE, 0, 0,
								getResources().getString(R.string.deleteuser));
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			// Intent intent = new Intent(this, MainActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// startActivity(intent);
			finish();
			break;
		case R.id.action_login:
			mWeiboAuth
					.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
		case 0:
			menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			LoginInfo info = mLoginList.get(menuInfo.position);
			dataHelper.DeleteLoginInfo(info.getUserID());
			mLoginList = dataHelper.GetLoginList();
			mLoginAdapter.setmList(mLoginList);

			break;
		}
		return true;
	}

	/**
	 * 微博认证授权回调类。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Toast.makeText(LoginActivity.this,
					R.string.weibosdk_demo_toast_auth_canceled,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle bundle) {
			// TODO Auto-generated method stub
			if (null == bundle) {
				return;
			}

			mCode = bundle.getString("code");
			Log.e(TAG, "code=======>" + mCode);
			fetchTokenAsync(mCode, Constants.WEIBO_DEMO_APP_SECRET);

		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			UIUtils.showToast(LoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG);
		}

	}

	/**
	 * 异步获取 Token。
	 * 
	 * @param authCode
	 *            授权 Code，该 Code 是一次性的，只能被获取一次 Token
	 * @param appSecret
	 *            应用程序的 APP_SECRET
	 */
	public void fetchTokenAsync(String authCode, String appSecret) {

		WeiboParameters requestParams = new WeiboParameters();
		requestParams.add(WBConstants.AUTH_PARAMS_CLIENT_ID, Constants.APP_KEY);
		requestParams.add(WBConstants.AUTH_PARAMS_CLIENT_SECRET, appSecret);
		requestParams.add(WBConstants.AUTH_PARAMS_GRANT_TYPE,
				"authorization_code");
		requestParams.add(WBConstants.AUTH_PARAMS_CODE, authCode);
		requestParams.add(WBConstants.AUTH_PARAMS_REDIRECT_URL,
				Constants.REDIRECT_URL);

		/**
		 * 请注意： {@link RequestListener} 对应的回调是运行在后台线程中的， 因此，需要使用 Handler 来配合更新
		 * UI。
		 */
		AsyncWeiboRunner.request(Constants.OAUTH2_ACCESS_TOKEN_URL,
				requestParams, "POST", new RequestListener() {

					@Override
					public void onComplete(String response) {
						// TODO Auto-generated method stub
						mAccessToken = Oauth2AccessToken
								.parseAccessToken(response);
						if (mAccessToken.isSessionValid()) {
							mToken = mAccessToken.getToken();
							mUserId = mAccessToken.getUid();
							String date = new SimpleDateFormat(
									"yyyy/MM/dd HH:mm:ss")
									.format(new java.util.Date(mAccessToken
											.getExpiresTime()));
							Log.e(TAG, "token==========>" + mToken);
							Log.e(TAG, "uid==========>" + mUserId);
							Log.e(TAG, "有效期==========>" + date);

							try {
								LoginInfo loginInfo = parseLoginJsonData(
										LoginActivity.this,
										Constants.USER_SHOW_URL, mToken,
										mUserId);
								if (!dataHelper.HaveUserInfo(mUserId)) {
									dataHelper.InsertLoginInfo(loginInfo);
								}
								String name = loginInfo.getUserName();
								String imageUrl = loginInfo.getIconUrl();
								String imageUrl_Large = loginInfo
										.getLargeIconUrl();
								dataHelper.UpdateLoginInfo(name, imageUrl,
										imageUrl_Large, mUserId);
								// mLoginAdapter.addItem(loginInfo);
								// mLoginAdapter.notifyDataSetChanged();
								mLoginList = dataHelper.GetLoginList();
								Message message = new Message();
								message.what = MSG_DATA_CHANGE;
								mHandler.sendMessage(message);

							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(MSG_GETTOKEN_FAILED);

					}

					@Override
					public void onIOException(IOException e) {
						// TODO Auto-generated method stub

						mHandler.sendEmptyMessage(MSG_GETTOKEN_FAILED);
					}

					@Override
					public void onError(WeiboException e) {
						// TODO Auto-generated method stub

						mHandler.sendEmptyMessage(MSG_GETTOKEN_FAILED);
					}

				});

	}

	/** 解析登录用户的信息 */
	public LoginInfo parseLoginJsonData(Context context, String url,
			String token, String uid) throws ClientProtocolException,
			IOException, JSONException {
		LoginInfo loginInfos = new LoginInfo();
		String json = "";
		if (Utils.checkConnection(context)) {
			json = Utils.getLoginStringFromUrl(url, token, uid);
			Log.e("getLoginInfo", "json========>" + json);
		}
		loginInfos.setToken(token);
		loginInfos.setUserID(uid);
		JSONObject userObject = new JSONObject(json);
		loginInfos.setUserName(userObject.getString("screen_name"));
		Log.e("getLoginInfo", "screen_name========>" + loginInfos.getUserName());
		loginInfos.setIconUrl(userObject.getString("profile_image_url"));
		Log.e("getLoginInfo",
				"profile_image_url======>" + loginInfos.getIconUrl());
		loginInfos.setLargeIconUrl(userObject.getString("avatar_large"));
		Log.e("getLoginInfo",
				"avatar_large======>" + loginInfos.getLargeIconUrl());
		return loginInfos;
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		// TODO Auto-generated method stub
		for (int position : reverseSortedPositions) {
			// mLoginAdapter.remove(position);
			LoginInfo info = mLoginList.get(position);
			dataHelper.DeleteLoginInfo(info.getUserID());
			mLoginList = dataHelper.GetLoginList();
			mLoginAdapter.setmList(mLoginList);
		}

	}
}
