package com.tsang.tspeak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.tsang.tspeak.adapter.FriendsAdapter;
import com.tsang.tspeak.model.User;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration.Status;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FriendsActivity extends ActionBarActivity implements
		OnItemClickListener, OnScrollListener, OnRefreshListener {

	private static final String TAG = "FriendsActivity";
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView mListView;
	private FriendsAdapter mAdapter;

	private String mToken;
	private String mUid;
	private int mCursor = 0;// сн╠Й
	private static int total_number;
	private static int next_cursor;
	private static int previous_cursor;
	private List<User> userList = new ArrayList<User>();

	private final static int PULL_TO_REFRESH = 0xb1;
	private final static int PUSH_TO_LOADMORE = 0xb2;
	LoadFriendsDataTask loadFriendsData = new LoadFriendsDataTask(this, PUSH_TO_LOADMORE);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);

		getSupportActionBar().setTitle(
				getResources().getString(R.string.at_friends));
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.ptr_layout_friends);
		ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(this).setup(mPullToRefreshLayout);

		mListView = (ListView) findViewById(R.id.friends_list);

		Intent intent = getIntent();
		mToken = intent.getStringExtra("token");
		mUid = intent.getStringExtra("uid");

		mAdapter = new FriendsAdapter(this);
		AnimationAdapter mAnimationAdapter = new ScaleInAnimationAdapter(
				mAdapter);
		mAnimationAdapter.setAbsListView(mListView);
		mListView.setAdapter(mAnimationAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);

		addItemToContainer(PULL_TO_REFRESH, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, NewStatusActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String returnFriendsUrl(String token, Long uid, int cursor) {
		String result = null;
		result = Constants.FRIENDS_URL + "?access_token=" + token + "&uid="
				+ uid + "&count=" + 20 + "&cursor=" + cursor;
		return result;
	}

	private void addItemToContainer(int type, int cursor) {
		if (loadFriendsData.getStatus() != android.os.AsyncTask.Status.RUNNING) {
			String url = returnFriendsUrl(mToken, Long.parseLong(mUid), cursor);
			LoadFriendsDataTask dataTask = new LoadFriendsDataTask(this, type);
			dataTask.execute(url);
		}
	}

	class LoadFriendsDataTask extends AsyncTask<String, Integer, List<User>> {

		private Context mContext;
		private int mType = PULL_TO_REFRESH;

		public LoadFriendsDataTask(Context mContext, int mType) {
			super();
			this.mContext = mContext;
			this.mType = mType;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mType == PULL_TO_REFRESH) {

			}
			if (mType == PUSH_TO_LOADMORE) {

			}
		}

		@Override
		protected void onPostExecute(List<User> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mType == PULL_TO_REFRESH) {
				mAdapter.removeAllItem();
				mAdapter.addItemAtLast(result);
				mAdapter.notifyDataSetChanged();

				userList.clear();
			}
			if (mType == PUSH_TO_LOADMORE) {

				mAdapter.addItemAtLast(result);
				mAdapter.notifyDataSetChanged();
			}
			userList.addAll(result);
		}

		@Override
		protected List<User> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return parseJsonData(params[0]);
		}

		private List<User> parseJsonData(String url) {
			// TODO Auto-generated method stub
			List<User> mList = new ArrayList<User>();
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
			if (json.contains("error")) {
				Log.e(TAG, "json======>" + json);
			}
			try {
				if (null != json) {
					User user;
					JSONObject jsonObject = new JSONObject(json);
					JSONArray userJsonArray = jsonObject.getJSONArray("users");
					total_number = jsonObject.getInt("total_number");
					next_cursor = jsonObject.getInt("next_cursor");
					previous_cursor = jsonObject.getInt("previous_cursor");
					Log.e(TAG, "total_number=========>" + total_number + "&&"
							+ userJsonArray.length());
					Log.e(TAG, "next_cursor=========>" + next_cursor);
					Log.e(TAG, "previous_cursor=========>" + previous_cursor);
					for (int i = 0; i < userJsonArray.length(); i++) {
						JSONObject userObject = userJsonArray.getJSONObject(i);
						user = new User();
						user.setScreen_Name(userObject.isNull("screen_name") ? ""
								: userObject.getString("screen_name"));
						user.setIconUrl(userObject.isNull("profile_image_url") ? ""
								: userObject.getString("profile_image_url"));
						mList.add(user);
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return mList;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if (lastInScreen >= totalItemCount) {
			if (mCursor <= total_number) {
				addItemToContainer(PUSH_TO_LOADMORE, mCursor);
				mCursor += 20;
			}
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
		User user = userList.get(position);
		String name = user.getScreen_Name();
		Intent intent = new Intent(FriendsActivity.this,
				NewStatusActivity.class);
		intent.putExtra("name", name);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		addItemToContainer(PULL_TO_REFRESH, 0);
		mCursor = 0;
	}

}
