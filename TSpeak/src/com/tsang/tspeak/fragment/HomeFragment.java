package com.tsang.tspeak.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.tsang.tspeak.R;
import com.tsang.tspeak.SingleStatusActivity;
import com.tsang.tspeak.adapter.WeiboAdapter;
import com.tsang.tspeak.model.Weibo;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HomeFragment extends Fragment implements OnRefreshListener,
		OnScrollListener, OnItemClickListener {

	private static final String TAG = "HomeFragment";
	private PullToRefreshLayout mPullToRefreshLayout;
	private ListView mHomeList;
	private WeiboAdapter mWeiboAdapter;
	private static Context mContext;
	private static String mToken;
	private static int mCount = 0;
	private static int mPage = 0;
	private List<Weibo> weiboList = new ArrayList<Weibo>();

	public final static int PULL_TO_REFRESH = 0x11;
	public final static int PUSH_TO_LOADMORE = 0x12;

	private LoadDataTask loadDataTask = new LoadDataTask(mContext,
			PUSH_TO_LOADMORE);

	public HomeFragment() {
		super();
	}

	public static HomeFragment newInstance(Context context, String token,
			int count, int page) {
		HomeFragment hf = new HomeFragment();
		mContext = context;
		mToken = token;
		mCount = count;
		mPage = page;
		Log.e(TAG, "token========>" + token + "\n count========>" + count
				+ "\n page===>" + page);
		return hf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		mHomeList = (ListView) view.findViewById(R.id.ptr_homelist);
		mWeiboAdapter = new WeiboAdapter(mContext);
		AnimationAdapter AnimationAdapter = new SwingBottomInAnimationAdapter(
				mWeiboAdapter);
		AnimationAdapter.setAbsListView(mHomeList);
		mHomeList.setAdapter(AnimationAdapter);
		mHomeList.setOnScrollListener(this);
		mHomeList.setOnItemClickListener(this);

		AddItemToContainer(1, PULL_TO_REFRESH);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
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
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
		case 0:

			break;
		}
		return true;
	}

	private String returnTheHomeTimeLineUrl(String token, int count, int page) {
		String result;
		result = Constants.HOME_TIMELINE_URL + "?access_token=" + token
				+ "&count=" + count + "&page=" + page;
		return result;
	}

	private void AddItemToContainer(int pageNum, int type) {
		if (loadDataTask.getStatus() != Status.RUNNING) {
			String url = returnTheHomeTimeLineUrl(mToken, mCount, pageNum);
			LoadDataTask task = new LoadDataTask(mContext, type);
			task.execute(url);
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
				mWeiboAdapter.setmList((LinkedList<Weibo>) result);
				mWeiboAdapter.notifyDataSetChanged();
				mPullToRefreshLayout.setRefreshComplete();
				weiboList.clear();
			}
			if (mType == PUSH_TO_LOADMORE) {
				mWeiboAdapter.addItemAtLast(result);
			}
			weiboList.addAll(result);
		}

		public List<Weibo> parseJsonData(String url) {
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
					Log.e(TAG, "IOException=====>" + e.getMessage());
				}
			} else {
				Log.e(TAG, "NoNetWork!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			if (json.contains("error")) {
				Log.e(TAG, "json========>" + json);
			}
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
							weibo.setSource("ю╢вт:" + m.group(1));
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
				Log.e(TAG, "JSONException=====>" + e.getMessage());
			}

			return weiboList;
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			mWeiboAdapter.setFlagBusy(true);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			mWeiboAdapter.setFlagBusy(false);
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			mWeiboAdapter.setFlagBusy(false);
			break;
		default:
			break;
		}
		mWeiboAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		Weibo weibo = weiboList.get(position);
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
		Intent intent = new Intent(mContext, SingleStatusActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		// Log.e(TAG, "weiboID====>" + weibo.getID());
	}

}
