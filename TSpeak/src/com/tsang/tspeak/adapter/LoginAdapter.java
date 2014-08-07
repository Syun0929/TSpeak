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
import com.tsang.tspeak.R;

import com.tsang.tspeak.model.LoginInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginAdapter extends BaseAdapter {

	private Context mContext;
	private List<LoginInfo> mList;

	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	ImageLoader imageLoader;

	public LoginAdapter(Context mContext, List<LoginInfo> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
	}

	public List<LoginInfo> getmList() {
		return mList;
	}

	public void setmList(List<LoginInfo> mList) {
		this.mList = mList;
		notifyDataSetChanged();
	}

	public void addItem(LoginInfo info) {
		this.mList.add(info);
		
	}

	/**
	 * Removes the element at the specified position in the list
	 */
	public void remove(int position) {
		this.mList.remove(position);
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
		LoginInfo mInfo = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_login, null);
			holder = new ViewHolder();
			holder.userIcon = (ImageView) convertView
					.findViewById(R.id.item_login_usericon);
			holder.userName = (TextView) convertView
					.findViewById(R.id.item_login_username);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.userName.setText(mInfo.getUserName());
		holder.userIcon.setImageResource(R.drawable.empty_photo);
		if (!mInfo.getIconUrl().equals("")) {
			// mImageLoader.DisplayImage(mInfo.getIconUrl(), holder.userIcon,
			// false);
			imageLoader.displayImage(mInfo.getIconUrl(), holder.userIcon,
					options, animateFirstListener);

		}
		return convertView;
	}

	class ViewHolder {
		ImageView userIcon;
		TextView userName;
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
