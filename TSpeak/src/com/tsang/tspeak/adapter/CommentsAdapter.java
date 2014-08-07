package com.tsang.tspeak.adapter;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Comment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsang.tspeak.R;

import com.tsang.tspeak.adapter.PersonAdapter.ViewHolder;
import com.tsang.tspeak.model.Comments;
import com.tsang.tspeak.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {

	private Context mContext;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	private LinkedList<Comments> mList;

	public CommentsAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.mList = new LinkedList<Comments>();
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.empty_photo)
				.showImageForEmptyUri(null)
				.showImageOnFail(null).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

	}

	public void removeAllItem() {
		mList.clear();

	}

	public void addItemAtLast(List<Comments> data) {
		mList.addAll(data);

	}

	public void addItemOnTop(List<Comments> data) {
		Comments comments = null;
		for (int i = data.size() - 1; i >= 0; i--) {
			comments = data.get(i);
			mList.addFirst(comments);
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
		final Comments comments = mList.get(position);
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
			holder = new ViewHolder();
			holder.ivUserIcon = (ImageView)convertView.findViewById(R.id.item_comment_headicon);
			holder.tvUserName = (TextView)convertView.findViewById(R.id.item_comment_name);
			holder.tvTime = (TextView)convertView.findViewById(R.id.item_comment_createdtime);
			holder.tvSource = (TextView)convertView.findViewById(R.id.item_comment_source);
			holder.tvContent = (TextView)convertView.findViewById(R.id.item_comment_content);
			convertView.setTag(holder);
		}		
		holder = (ViewHolder) convertView.getTag();
		mImageLoader.displayImage(comments.getIconUrl(), holder.ivUserIcon);
		holder.tvUserName.setText(comments.getUserName());
		holder.tvTime.setText(comments.getCreateTime());
		holder.tvSource.setText(comments.getSource());
		SpannableString text = Utils.setTextHighLightAndTxtToImg(comments.getContent(),mContext);
		holder.tvContent.setText(text);
		return convertView;
	}

	class ViewHolder {
		ImageView ivUserIcon;
		TextView tvUserName;
		TextView tvTime;
		TextView tvSource;
		TextView tvContent;
	}

}
