package com.tsang.tspeak.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tsang.tspeak.ImagePagerActivity;
import com.tsang.tspeak.MainActivity;
import com.tsang.tspeak.R;
import com.tsang.tspeak.model.Weibo;
import com.tsang.tspeak.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeiboAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<Weibo> mList;
	private boolean mBusy = false;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public WeiboAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.mList = new LinkedList<Weibo>();

		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

	}

	public void setmList(LinkedList<Weibo> mList) {
		this.mList = mList;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public void removeAllItem() {
		mList.clear();

	}

	public void addItemAtLast(List<Weibo> data) {
		mList.addAll(data);
		notifyDataSetChanged();
	}

	public void addItemOnTop(List<Weibo> data) {
		Weibo weibo = null;
		for (int i = data.size() - 1; i >= 0; i--) {
			weibo = data.get(i);
			// if(!mList.contains(weibo))
			mList.addFirst(weibo);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		Weibo weibo = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_timeline_card, null);
			holder = new ViewHolder();
			holder.userIcon = (ImageView) convertView
					.findViewById(R.id.item_home_headicon);
			holder.userName = (TextView) convertView
					.findViewById(R.id.item_home_name);
			holder.createdtime = (TextView) convertView
					.findViewById(R.id.item_home_createdtime);
			holder.source = (TextView) convertView
					.findViewById(R.id.item_home_source);
			holder.reposts_count = (TextView) convertView
					.findViewById(R.id.item_home_reposts_count);
			holder.comment_count = (TextView) convertView
					.findViewById(R.id.item_home_comment_count);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_home_content);
			holder.midImage = (ImageView) convertView
					.findViewById(R.id.item_home_midpic);
			holder.gridLayout = (GridLayout) convertView
					.findViewById(R.id.item_home_gridlayout);
			holder.retweeted_layout = (RelativeLayout) convertView
					.findViewById(R.id.item_home_retweeted_layout);
			holder.retweeted_content = (TextView) convertView
					.findViewById(R.id.item_home_re_content);
			holder.retweeted_midImage = (ImageView) convertView
					.findViewById(R.id.item_home_re_midpic);
			holder.retweeted_gridLayout = (GridLayout) convertView
					.findViewById(R.id.item_home_re_gridlayout);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.userName.setText(weibo.getScreen_name());
		holder.createdtime.setText(weibo.getCreatedTime());
		holder.reposts_count.setText(weibo.getReposts_count());
		holder.comment_count.setText(weibo.getComment_count());
		holder.source.setText(weibo.getSource());
		SpannableString text = Utils.setTextHighLightAndTxtToImg(
				weibo.getText(), mContext);
		holder.content.setText(text);

		if (weibo.getIconUrl() != "") {
			mImageLoader.displayImage(weibo.getIconUrl(), holder.userIcon,
					options, animateFirstListener);
		}

		if (!weibo.isHasretweeted_status()) {
			holder.retweeted_layout.setVisibility(View.GONE);
			int count = weibo.getPic_Urls().length;
			if (count > 1) {
				holder.gridLayout.setVisibility(View.VISIBLE);
				holder.midImage.setVisibility(View.GONE);
				for (int i = 0; i < count; i++) {
					ImageView pic = (ImageView) holder.gridLayout.getChildAt(i);

					mImageLoader.displayImage(weibo.getPic_Urls()[i], pic,
							options, animateFirstListener);
					pic.setVisibility(View.VISIBLE);
					final int pagerPosition = i;
					final String[] urls = weibo.getPic_Urls();
					pic.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Utils.startImagePagerActivity(mContext, urls,
									pagerPosition);
						}
					});
				}
				for (int j = 8; j >= count; j--) {
					ImageView pic = (ImageView) holder.gridLayout.getChildAt(j);
					pic.setVisibility(View.GONE);
				}
			} else if (count <= 1 && count > 0) {
				if (weibo.getMidpic_Url() != "") {
					holder.midImage.setVisibility(View.VISIBLE);
					holder.gridLayout.setVisibility(View.GONE);

					mImageLoader.displayImage(weibo.getMidpic_Url(),
							holder.midImage, options, animateFirstListener);

				}
			} else {
				holder.midImage.setVisibility(View.GONE);
				holder.gridLayout.setVisibility(View.GONE);
			}
		} else {
			holder.midImage.setVisibility(View.GONE);
			holder.gridLayout.setVisibility(View.GONE);
			holder.retweeted_layout.setVisibility(View.VISIBLE);
			SpannableString s = Utils.setTextHighLightAndTxtToImg(
					"@" + weibo.getRetweeted_Screen_name() + ": "
							+ weibo.getRetweeted_Text(), mContext);
			holder.retweeted_content.setText(s);

			int count = weibo.getRetweeted_Pic_Urls().length;
			if (count > 1) {
				holder.retweeted_gridLayout.setVisibility(View.VISIBLE);
				holder.retweeted_midImage.setVisibility(View.GONE);
				for (int i = 0; i < count; i++) {
					ImageView pic = (ImageView) holder.retweeted_gridLayout
							.getChildAt(i);

					mImageLoader.displayImage(weibo.getRetweeted_Pic_Urls()[i],
							pic, options, animateFirstListener);
					pic.setVisibility(View.VISIBLE);
					final int pagerPosition = i;
					final String[] urls = weibo.getRetweeted_Pic_Urls();
					pic.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Utils.startImagePagerActivity(mContext, urls,
									pagerPosition);
						}
					});
				}
				for (int j = 8; j >= count; j--) {
					ImageView pic = (ImageView) holder.retweeted_gridLayout
							.getChildAt(j);
					pic.setVisibility(View.GONE);
				}
			} else if (count <= 1 && count > 0) {
				if (weibo.getRetweeted_Midpic_Url() != "") {
					holder.retweeted_midImage.setVisibility(View.VISIBLE);
					holder.retweeted_gridLayout.setVisibility(View.GONE);

					mImageLoader.displayImage(weibo.getRetweeted_Midpic_Url(),
							holder.retweeted_midImage, options,
							animateFirstListener);
				}
			} else {
				holder.retweeted_midImage.setVisibility(View.GONE);
				holder.retweeted_gridLayout.setVisibility(View.GONE);
			}
		}

		final String url = weibo.getMidpic_Url();
		final String re_url = weibo.getRetweeted_Midpic_Url();

		holder.midImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.startImagePagerActivity(mContext, url);
			}
		});

		holder.retweeted_midImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.startImagePagerActivity(mContext, re_url);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView userIcon;
		TextView userName;
		TextView reposts_count;
		TextView comment_count;
		TextView createdtime;
		TextView source;
		TextView content;
		ImageView midImage;
		GridLayout gridLayout;
		RelativeLayout retweeted_layout;
		TextView retweeted_content;
		ImageView retweeted_midImage;
		GridLayout retweeted_gridLayout;
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
