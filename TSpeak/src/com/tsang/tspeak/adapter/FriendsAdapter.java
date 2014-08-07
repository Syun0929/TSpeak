package com.tsang.tspeak.adapter;

import java.util.LinkedList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsang.tspeak.R;
import com.tsang.tspeak.adapter.LoginAdapter.ViewHolder;
import com.tsang.tspeak.model.User;
import com.tsang.tspeak.model.Weibo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<User> mList;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	public FriendsAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.mList = new LinkedList<User>();
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

	}

	public void removeAllItem() {
		mList.clear();

	}

	public void addItemAtLast(List<User> data) {
		mList.addAll(data);
	}

	public void addItemOnTop(List<User> data) {
		User author = null;
		for (int i = data.size() - 1; i >= 0; i--) {
			author = data.get(i);
			// if(!mList.contains(weibo))
			mList.addFirst(author);
		}

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
		User author = mList.get(position);
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
		holder.userName.setText(author.getScreen_Name());
		if (author.getIconUrl() != "") {
			mImageLoader.displayImage(author.getIconUrl(), holder.userIcon,
					options);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView userIcon;
		TextView userName;
	}

}
