package com.tsang.tspeak;

import java.io.IOException;
import java.util.ArrayList;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsang.tspeak.PersonActivity.LoadDataTask;
import com.tsang.tspeak.adapter.CommentsAdapter;
import com.tsang.tspeak.model.Comments;
import com.tsang.tspeak.model.Weibo;
import com.tsang.tspeak.util.Constants;
import com.tsang.tspeak.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleStatusActivity extends BaseActivity implements
		OnScrollListener {

	private final static String TAG = "SingleStatusActivity";
	Weibo weibo = new Weibo();
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	ImageView ivUserIcon;
	TextView tvUserName;
	TextView tvScreen_name;
	TextView tvReposts_count;
	TextView tvComment_count;
	TextView tvCreatedtime;
	TextView tvSource;
	TextView tvContent;
	ImageView ivMidImage;
	GridLayout mGridLayout;
	RelativeLayout Retweeted_layout;
	TextView tvRetweeted_content;
	ImageView ivRetweeted_midImage;
	GridLayout Retweeted_gridLayout;
	ListView mCommentList;
	CommentsAdapter commentsAdapter;
	Button btnRepost;
	Button btnComment;

	private static String mToken;
	private static int mPage = 1;
	private final static int PULL_TO_REFRESH = 0xc1;
	private final static int PUSH_TO_LOADMORE = 0xc2;
	
	LoadDataTask dataTask = new LoadDataTask(this, PUSH_TO_LOADMORE);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlestatus);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.single_status));
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		btnRepost = (Button) findViewById(R.id.single_repost);
		btnComment = (Button) findViewById(R.id.single_comment);

		View view = LayoutInflater.from(this).inflate(
				R.layout.view_comments_head, null);
		ivUserIcon = (ImageView) view.findViewById(R.id.single_login_usericon);
		tvUserName = (TextView) view.findViewById(R.id.single_login_username);
		tvScreen_name = (TextView) view.findViewById(R.id.single_name);
		tvComment_count = (TextView) view
				.findViewById(R.id.single_comment_count);
		tvReposts_count = (TextView) view
				.findViewById(R.id.single_reposts_count);
		tvContent = (TextView) view.findViewById(R.id.single_content);
		tvCreatedtime = (TextView) view.findViewById(R.id.single_createdtime);
		tvSource = (TextView) view.findViewById(R.id.single_source);
		tvRetweeted_content = (TextView) view
				.findViewById(R.id.single_re_content);
		ivMidImage = (ImageView) view.findViewById(R.id.single_midpic);
		ivRetweeted_midImage = (ImageView) view
				.findViewById(R.id.single_re_midpic);
		mGridLayout = (GridLayout) view.findViewById(R.id.single_gridlayout);
		Retweeted_gridLayout = (GridLayout) view
				.findViewById(R.id.single_re_gridlayout);
		Retweeted_layout = (RelativeLayout) view
				.findViewById(R.id.single_retweeted_layout);
		mCommentList = (ListView) findViewById(R.id.single_listview);
		mCommentList.addHeaderView(view);

		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

		getWeiboData();
		loadWeiboData();

		commentsAdapter = new CommentsAdapter(this);
		AnimationAdapter animationAdapter = new ScaleInAnimationAdapter(
				commentsAdapter);
		animationAdapter.setAbsListView(mCommentList);
		mCommentList.setAdapter(animationAdapter);
		mCommentList.setOnScrollListener(this);
		AddItemToContainer(1, PULL_TO_REFRESH);

		btnRepost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("token", mToken);
				bundle.putInt("type", NewStatusActivity.TYPE_REPOST);
				bundle.putString("weiboID", weibo.getID());
				bundle.putBoolean("repost_hasretweet",
						weibo.isHasretweeted_status());
				if (weibo.isHasretweeted_status()) {
					bundle.putString("repost_name", weibo.getScreen_name());
					bundle.putString("repost_text", weibo.getText());
				}
				Intent intent = new Intent(SingleStatusActivity.this,
						NewStatusActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("token", mToken);
				bundle.putInt("type", NewStatusActivity.TYPE_COMMENT);
				bundle.putString("weiboID", weibo.getID());
				Intent intent = new Intent(SingleStatusActivity.this,
						NewStatusActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void loadWeiboData() {
		mImageLoader.displayImage(weibo.getIconUrl(), ivUserIcon, options);
		tvUserName.setText(weibo.getScreen_name());
		tvScreen_name.setText(weibo.getScreen_name());
		tvSource.setText(weibo.getSource());
		tvReposts_count.setText(weibo.getReposts_count());
		tvComment_count.setText(weibo.getComment_count());
		tvCreatedtime.setText(weibo.getCreatedTime());
		SpannableString text = Utils.setTextHighLightAndTxtToImg(
				weibo.getText(), this);
		tvContent.setText(text);

		if (!weibo.isHasretweeted_status()) {
			Retweeted_layout.setVisibility(View.GONE);
			int count = weibo.getPic_Urls().length;
			if (count > 1) {
				ivMidImage.setVisibility(View.GONE);
				mGridLayout.setVisibility(View.VISIBLE);
				for (int i = 0; i < count; i++) {
					ImageView pic = (ImageView) mGridLayout.getChildAt(i);
					pic.setVisibility(View.VISIBLE);

					mImageLoader.displayImage(weibo.getPic_Urls()[i], pic,
							options);

					final int pagerPosition = i;
					pic.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Utils.startImagePagerActivity(
									SingleStatusActivity.this,
									weibo.getPic_Urls(), pagerPosition);
						}
					});
				}
			} else if (count <= 1) {
				if (weibo.getMidpic_Url() != null) {
					ivMidImage.setVisibility(View.VISIBLE);
					mGridLayout.setVisibility(View.GONE);
					mImageLoader.displayImage(weibo.getMidpic_Url(),
							ivMidImage, options);
				} else {
					ivMidImage.setVisibility(View.GONE);
				}
			}
		} else {
			Retweeted_layout.setVisibility(View.VISIBLE);
			ivMidImage.setVisibility(View.GONE);
			mGridLayout.setVisibility(View.GONE);
			SpannableString s = Utils.setTextHighLightAndTxtToImg(
					"@" + weibo.getRetweeted_Screen_name() + ": "
							+ weibo.getRetweeted_Text(), this);
			tvRetweeted_content.setText(s);

			int count = weibo.getRetweeted_Pic_Urls().length;
			if (count > 1) {
				Retweeted_gridLayout.setVisibility(View.VISIBLE);
				ivRetweeted_midImage.setVisibility(View.GONE);
				for (int i = 0; i < count; i++) {
					ImageView pic = (ImageView) Retweeted_gridLayout
							.getChildAt(i);
					pic.setVisibility(View.VISIBLE);
					mImageLoader.displayImage(weibo.getRetweeted_Pic_Urls()[i],
							pic, options);
					final int pagerPosition = i;
					pic.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Utils.startImagePagerActivity(
									SingleStatusActivity.this,
									weibo.getRetweeted_Pic_Urls(),
									pagerPosition);
						}
					});
				}
			} else if (count <= 1) {
				if (weibo.getRetweeted_Midpic_Url() != "") {
					ivRetweeted_midImage.setVisibility(View.VISIBLE);
					Retweeted_gridLayout.setVisibility(View.GONE);

					mImageLoader.displayImage(weibo.getRetweeted_Midpic_Url(),
							ivRetweeted_midImage, options);

				} else {
					ivRetweeted_midImage.setVisibility(View.GONE);
				}
			}
		}

		ivMidImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.startImagePagerActivity(SingleStatusActivity.this,
						weibo.getMidpic_Url());
			}
		});

		ivRetweeted_midImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.startImagePagerActivity(SingleStatusActivity.this,
						weibo.getRetweeted_Midpic_Url());
			}
		});
	}

	/** 接收首页传递过来的Weibo类的参数 */
	private void getWeiboData() {
		Intent intent = getIntent();
		mToken = intent.getStringExtra("token");
		weibo.setID(intent.getStringExtra("weiboID"));
		weibo.setHasretweeted_status(intent.getBooleanExtra(
				"weiboHasRetweetedStatus", false));
		weibo.setPic_Urls(intent.getStringArrayExtra("weiboPic_Urls"));
		weibo.setRetweeted_Pic_Urls(intent
				.getStringArrayExtra("weiboRetweeted_Pic_Urls"));
		weibo.setComment_count(intent.getStringExtra("weiboComment_count"));
		weibo.setCreatedTime(intent.getStringExtra("weiboCreatedTime"));
		weibo.setIconUrl(intent.getStringExtra("weiboIconUrl"));
		weibo.setMidpic_Url(intent.getStringExtra("weiboMidpic_Url"));
		weibo.setReposts_count(intent.getStringExtra("weiboReposts_count"));
		weibo.setRetweeted_Midpic_Url(intent
				.getStringExtra("weiboRetweeted_Midpic_Url"));
		weibo.setRetweeted_Screen_name(intent
				.getStringExtra("weiboRetweeted_Screen_name"));
		weibo.setRetweeted_Text(intent.getStringExtra("weiboRetweeted_Text"));
		weibo.setScreen_name(intent.getStringExtra("weiboScreen_name"));
		weibo.setText(intent.getStringExtra("weiboText"));
		weibo.setUID(intent.getStringExtra("weiboUID"));
		weibo.setSource(intent.getStringExtra("weiboSource"));
		Log.e(TAG, "weiboID====>" + weibo.getID());
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

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

	private String returnTheUserTimeLineUrl(String token, int page, long id) {
		String result;
		result = Constants.COMMENTS_SHOW_URL + "?access_token=" + token + "&page=" + page
				+ "&id=" + id;
		return result;
	}

	private void AddItemToContainer(int pageNum, int type) {
		if (dataTask.getStatus() != Status.RUNNING) {
			String url = returnTheUserTimeLineUrl(mToken, pageNum,
					Long.parseLong(weibo.getID()));
			LoadDataTask task = new LoadDataTask(this, type);
			task.execute(url);
		}
	}

	class LoadDataTask extends AsyncTask<String, Integer, List<Comments>> {

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

			}
			if (mType == PUSH_TO_LOADMORE) {

			}
		}

		@Override
		protected void onPostExecute(List<Comments> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mType == PULL_TO_REFRESH) {
				commentsAdapter.removeAllItem();
				commentsAdapter.addItemAtLast(result);
				commentsAdapter.notifyDataSetChanged();
			}
			if (mType == PUSH_TO_LOADMORE) {

				commentsAdapter.addItemAtLast(result);
				commentsAdapter.notifyDataSetChanged();
			}

		}

		@Override
		protected List<Comments> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return parseJsonData(params[0]);
		}

		private List<Comments> parseJsonData(String url) {
			// TODO Auto-generated method stub
			List<Comments> commentsList = new ArrayList<Comments>();
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

			try {
				if (null != json) {
					Comments comments;
					JSONObject jsonObject = new JSONObject(json);
					JSONArray commentsArray = jsonObject
							.getJSONArray("comments");
					for (int i = 0; i < commentsArray.length(); i++) {
						JSONObject commentsObject = commentsArray
								.getJSONObject(i);
						comments = new Comments();
						comments.setID(commentsObject.isNull("id") ? ""
								: commentsObject.getString("id"));
						String time = commentsObject.isNull("created_at") ? ""
								: commentsObject.getString("created_at");
						comments.setCreateTime(Utils.getCSTDate(time));
						String source = commentsObject.isNull("source") ? ""
								: commentsObject.getString("source");
						Pattern p = Pattern.compile("<a.*?>(.*?)</a>");
						Matcher m = p.matcher(source);
						while (m.find()) {
							comments.setSource("来自:" + m.group(1));
							// Log.e(TAG, "来自====>" + comments.getSource());
						}
						comments.setContent(commentsObject.isNull("text") ? ""
								: commentsObject.getString("text"));
						// Log.e(TAG, "text====>" + comments.getContent());
						JSONObject userObject = commentsObject
								.getJSONObject("user");
						comments.setUserName(userObject.isNull("screen_name") ? ""
								: userObject.getString("screen_name"));
						comments.setIconUrl(userObject
								.isNull("profile_image_url") ? "" : userObject
								.getString("profile_image_url"));
						commentsList.add(comments);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return commentsList;
		}

	}
}
