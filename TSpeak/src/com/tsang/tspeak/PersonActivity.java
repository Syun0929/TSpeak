package com.tsang.tspeak;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.haarman.listviewanimations.view.DynamicListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.tsang.tspeak.adapter.PersonAdapter;
import com.tsang.tspeak.model.User;
import com.tsang.tspeak.model.Weibo;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonActivity extends BaseActivity  implements
		OnRefreshListener, OnScrollListener, OnItemClickListener,
		OnDismissCallback, OnCreateContextMenuListener, RequestListener {
	public static final String TAG = "PersonActivity";

	private PullToRefreshLayout mPullToRefreshLayout;
	private ImageView userIcon;
	private TextView tvUsername;
	private TextView tvFollowers_count;
	private TextView tvFriends_count;
	private TextView tvStatuses_count;
	private TextView tvDescription;
	private ListView mListView;
	private ImageLoader imageLoader;
	private String mToken;
	private String mUID;
	private User author = null;
	private PersonAdapter mPersonAdapter;
	private LoadDataTask loadDataTask = new LoadDataTask(this, PUSH_TO_LOADMORE);
	private static int mPage = 1;
	private static final int MSG_PERSON_DATA = 0xc1;
	private final static int PULL_TO_REFRESH = 0x11;
	private final static int PUSH_TO_LOADMORE = 0x12;
	private static final int MSG_SEEN_SECCESS = 0x23;
	private static final int MSG_SEEN_FAILED = 0x24;
	static final int NOTIFICATION_SENDING = 0xe1;
	static final int NOTIFICATION_SEND_DONE = 0xe2;
	static final int NOTIFICATION_SEND_FAILED = 0xe3;
	private List<Weibo> weiboList = new ArrayList<Weibo>();
	private View view;
	private AdapterContextMenuInfo menuInfo;
	NotificationManager manager;
	Notification notify_sending;
	Notification notify_send_done;
	Notification notify_send_failed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		getSupportActionBar().setTitle(R.string.person_data_page);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notify_sending = new Notification.Builder(this).setAutoCancel(true)
				.setTicker(getResources().getString(R.string.notify_deleting))
				.setSmallIcon(R.drawable.ic_action_send).build();
		notify_send_done = new Notification.Builder(this)
				.setAutoCancel(true)
				.setTicker(
						getResources().getString(R.string.notify_delete_done))
				.setSmallIcon(R.drawable.notify_done).build();
		notify_send_failed = new Notification.Builder(this)
				.setAutoCancel(true)
				.setTicker(
						getResources().getString(R.string.notify_delete_failed))
				.setContentTitle(
						getResources().getString(R.string.notify_delete_failed))
				.setContentText(
						getResources().getString(R.string.network_error))
				.setSmallIcon(R.drawable.notify_cancel).build();
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout_person);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
		view = LayoutInflater.from(this).inflate(R.layout.view_person_data,
				null);
		userIcon = (ImageView) view.findViewById(R.id.person_headicon);
		tvUsername = (TextView) view.findViewById(R.id.person_username);
		tvDescription = (TextView) view.findViewById(R.id.person_description);
		tvFollowers_count = (TextView) view
				.findViewById(R.id.person_followers_count);
		tvFriends_count = (TextView) view
				.findViewById(R.id.person_friends_count);
		tvStatuses_count = (TextView) view
				.findViewById(R.id.person_statuses_count);
		mListView = (ListView) findViewById(R.id.person_status_list);
		mListView.addHeaderView(view);
		imageLoader = ImageLoader.getInstance();

		Intent intent = getIntent();
		mToken = intent.getStringExtra("token");
		mUID = intent.getStringExtra("uid");
		new userInfoThread().start();

		mPersonAdapter = new PersonAdapter(this);
		// AnimationAdapter mAnimationAdapter = new
		// SwingBottomInAnimationAdapter(
		// new SwipeDismissAdapter(mPersonAdapter, this));
		// mAnimationAdapter.setAbsListView(mListView);
		mListView.setAdapter(new SwipeDismissAdapter(mPersonAdapter, this));

		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnCreateContextMenuListener(this);

		AddItemToContainer(1, PULL_TO_REFRESH);

	}

	private String returnTheUserTimeLineUrl(String token, int page) {
		String result;
		result = Constants.USER_TIMELINE_URL + "?access_token=" + token
				+ "&page=" + page;
		return result;
	}

	private void AddItemToContainer(int pageNum, int type) {
		if (loadDataTask.getStatus() != Status.RUNNING) {
			String url = returnTheUserTimeLineUrl(mToken, pageNum);
			LoadDataTask dataTask = new LoadDataTask(this, type);
			dataTask.execute(url);
		}
	}

	class LoadDataTask extends AsyncTask<String, Integer, List<Weibo>> {

		private Context mContext;
		private int mType = PULL_TO_REFRESH;

		public LoadDataTask(Context mContext, int mType) {
			super();
			this.mContext = mContext;
			this.mType = mType;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mType == PULL_TO_REFRESH) {

				mPullToRefreshLayout.setRefreshing(true);
			}
			if (mType == PUSH_TO_LOADMORE) {

			}
		}

		@Override
		protected List<Weibo> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return parseJsonData(params[0]);
		}

		@Override
		protected void onPostExecute(List<Weibo> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mType == PULL_TO_REFRESH) {
				mPersonAdapter.setmList((LinkedList<Weibo>) result);
				mPersonAdapter.notifyDataSetChanged();
				mPullToRefreshLayout.setRefreshComplete();
				weiboList.clear();
			}
			if (mType == PUSH_TO_LOADMORE) {
				mPersonAdapter.addItemAtLast(result);
				mPersonAdapter.notifyDataSetChanged();
			}
			weiboList.addAll(result);
		}

		private List<Weibo> parseJsonData(String url) {
			// TODO Auto-generated method stub

			List<Weibo> weiboList = new LinkedList<Weibo>();
			String json = "";
			if (Utils.checkConnection(mContext)) {
				try {
					json = Utils.getStringFromUrl(url);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Log.e(TAG, "json========>" + json);

			try {
				if (null != json) {
					Weibo weibo;
					JSONObject jsonObject = new JSONObject(json);
					JSONArray statusArray = jsonObject.getJSONArray("statuses");
					for (int i = 0; i < statusArray.length(); i++) {
						JSONObject weiboObject = statusArray.getJSONObject(i);
						weibo = new Weibo();
						weibo.setID(weiboObject.isNull("id") ? "" : weiboObject
								.getString("id"));
						String createdtime = weiboObject.isNull("created_at") ? ""
								: weiboObject.getString("created_at");
						weibo.setCreatedTime(Utils.getCSTDate(createdtime));
						// Log.e(TAG, "time===>" +
						// Utils.getCSTDate(createdtime));

						weibo.setReposts_count(weiboObject
								.isNull("reposts_count") ? "" : weiboObject
								.getString("reposts_count"));
						weibo.setComment_count(weiboObject
								.isNull("comments_count") ? "" : weiboObject
								.getString("comments_count"));

						String source = weiboObject.isNull("source") ? ""
								: weiboObject.getString("source");
						Pattern p = Pattern.compile("<a.*?>(.*?)</a>");
						Matcher m = p.matcher(source);
						while (m.find()) {
							// Log.e(TAG, "Source2===>" + m.group(1));
							weibo.setSource("À´×Ô:" + m.group(1));
						}
						weibo.setText(weiboObject.isNull("text") ? ""
								: weiboObject.getString("text"));

						weibo.setMidpic_Url(weiboObject.isNull("bmiddle_pic") ? ""
								: weiboObject.getString("bmiddle_pic"));

						JSONObject userObject = weiboObject
								.getJSONObject("user");

						weibo.setScreen_name(userObject.isNull("screen_name") ? ""
								: userObject.getString("screen_name"));
						weibo.setIconUrl(userObject.isNull("profile_image_url") ? ""
								: userObject.getString("profile_image_url"));

						JSONArray urlsArray = weiboObject
								.getJSONArray("pic_urls");
						String[] thumbnail_pics = new String[urlsArray.length()];

						for (int j = 0; j < urlsArray.length(); j++) {
							thumbnail_pics[j] = urlsArray.getJSONObject(j)
									.getString("thumbnail_pic");
						}
						weibo.setPic_Urls(thumbnail_pics);

						if (weiboObject.isNull("retweeted_status")) {
							// Log.e(TAG, "retweetObject=============>null");
							weibo.setHasretweeted_status(false);
						} else {
							// Log.e(TAG,
							// "retweetObject=============>not null");
							weibo.setHasretweeted_status(true);
						}

						if (weibo.isHasretweeted_status()) {
							JSONObject retweetObject = weiboObject
									.getJSONObject("retweeted_status");
							weibo.setRetweeted_Text(retweetObject
									.isNull("text") ? "" : retweetObject
									.getString("text"));
							weibo.setRetweeted_Midpic_Url(retweetObject
									.isNull("bmiddle_pic") ? "" : retweetObject
									.getString("bmiddle_pic"));
							// Log.e(TAG,
							// "re_bmiddle_pic======>"
							// + weibo.getRetweeted_Midpic_Url());
							JSONObject retweetuserObject = retweetObject
									.getJSONObject("user");
							weibo.setRetweeted_Screen_name(retweetuserObject
									.isNull("screen_name") ? ""
									: retweetuserObject
											.getString("screen_name"));

							JSONArray reurlsArray = retweetObject
									.getJSONArray("pic_urls");
							String[] re_thumbnail_pics = new String[reurlsArray
									.length()];
							for (int n = 0; n < reurlsArray.length(); n++) {
								re_thumbnail_pics[n] = reurlsArray
										.getJSONObject(n).getString(
												"thumbnail_pic");
							}
							weibo.setRetweeted_Pic_Urls(re_thumbnail_pics);
						}

						weiboList.add(weibo);
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return weiboList;
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_PERSON_DATA:
				imageLoader.displayImage(author.getLargeIconUrl(), userIcon);
				tvDescription.setText(author.getDescription());
				tvFollowers_count.setText("¹Ø×¢ " + author.getFriends_count());
				tvFriends_count.setText("·ÛË¿ " + author.getFollowers_count());
				tvUsername.setText(author.getScreen_Name());
				tvStatuses_count.setText("Î¢²© " + author.getStatuses_count());
				break;
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
		}

	};

	public User getPersonInfo(Context context, String url, String token,
			String uid) throws IOException, Exception {
		User author = new User();
		String json = "";
		if (Utils.checkConnection(context)) {
			json = Utils.getLoginStringFromUrl(url, token, uid);
			Log.e("getLoginInfo", "json========>" + json);
		}

		author.setUID(uid);
		JSONObject userObject = new JSONObject(json);
		author.setScreen_Name(userObject.getString("screen_name"));
		author.setIconUrl(userObject.getString("profile_image_url"));
		author.setLargeIconUrl(userObject.getString("avatar_large"));
		String createdtime = userObject.getString("created_at");
		author.setCreated_at(Utils.getCSTDate(createdtime));
		author.setDescription(userObject.getString("description"));
		author.setGender(userObject.getString("gender"));
		author.setFollowers_count(userObject.getString("followers_count"));
		author.setFriends_count(userObject.getString("friends_count"));
		author.setStatuses_count(userObject.getString("statuses_count"));
		author.setLocation(userObject.getString("location"));
		author.setFollowing(userObject.getBoolean("following"));
		author.setFollow_me(userObject.getBoolean("follow_me"));

		return author;
	}

	class userInfoThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				author = getPersonInfo(PersonActivity.this,
						Constants.USER_SHOW_URL, mToken, mUID);
				handler.sendEmptyMessage(MSG_PERSON_DATA);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void destoryStatus(String token, long id) {
		WeiboParameters requestParams = new WeiboParameters();
		requestParams.add("access_token", token);
		requestParams.add("id", id);
		AsyncWeiboRunner.request(Constants.STATUS_DESTROY_URL, requestParams,
				"POST", this);
		manager.notify(NOTIFICATION_SENDING, notify_sending);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, 0, 0, getResources().getString(R.string.delete));
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
			int position = menuInfo.position;
			if (position == 0) {
			}
			Weibo weibo = weiboList.get(position - 1);
			long weiboid = Long.parseLong(weibo.getID());
			mPersonAdapter.remove(position - 1);
			mPersonAdapter.notifyDataSetChanged();
			destoryStatus(mToken, weiboid);

			break;
		}
		return true;
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

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		new userInfoThread().start();
		AddItemToContainer(1, PULL_TO_REFRESH);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if (lastInScreen >= totalItemCount) {
			AddItemToContainer(++mPage, PUSH_TO_LOADMORE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		if (position == 0) {
			return;
		}
		Weibo weibo = weiboList.get(position - 1);
		Bundle bundle = new Bundle();
		bundle.putString("weiboID", weibo.getID());
		bundle.putString("weiboCreatedTime", weibo.getCreatedTime());
		bundle.putString("weiboIconUrl", weibo.getIconUrl());
		bundle.putString("weiboComment_count", weibo.getComment_count());
		bundle.putString("weiboMidpic_Url", weibo.getMidpic_Url());
		bundle.putString("weiboReposts_count", weibo.getReposts_count());
		bundle.putString("weiboRetweeted_Midpic_Url",
				weibo.getRetweeted_Midpic_Url());
		bundle.putString("weiboRetweeted_Screen_name",
				weibo.getRetweeted_Screen_name());
		bundle.putString("weiboRetweeted_Text", weibo.getRetweeted_Text());
		bundle.putString("weiboScreen_name", weibo.getScreen_name());
		bundle.putString("weiboSource", weibo.getSource());
		bundle.putString("weiboText", weibo.getText());
		bundle.putString("weiboUID", weibo.getUID());
		bundle.putBoolean("weiboHasRetweetedStatus",
				weibo.isHasretweeted_status());
		bundle.putStringArray("weiboPic_Urls", weibo.getPic_Urls());
		bundle.putStringArray("weiboRetweeted_Pic_Urls",
				weibo.getRetweeted_Pic_Urls());
		bundle.putString("token", mToken);
		Intent intent = new Intent(PersonActivity.this,
				SingleStatusActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		// TODO Auto-generated method stub
		for (int position : reverseSortedPositions) {
			if (position == 0) {
				return;
			}
			mPersonAdapter.remove(position - 1);
			mPersonAdapter.notifyDataSetChanged();

		}
	}

	@Override
	public void onComplete(String response) {
		// TODO Auto-generated method stub
		if (response.contains("error")) {
			handler.sendEmptyMessage(MSG_SEEN_FAILED);
			Log.e(TAG, "error:======>" + response);
		} else {
			handler.sendEmptyMessage(MSG_SEEN_SECCESS);
		}
	}

	@Override
	public void onComplete4binary(ByteArrayOutputStream responseOS) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(MSG_SEEN_SECCESS);
	}

	@Override
	public void onIOException(IOException e) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(MSG_SEEN_FAILED);
		e.printStackTrace();
		Log.e(TAG, "exception=====>" + e.getMessage());
	}

	@Override
	public void onError(WeiboException e) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(MSG_SEEN_FAILED);
		e.printStackTrace();
		Log.e(TAG, "error=======>" + e.getMessage());
	}

}
