package com.tsang.tspeak.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Weibo {
	/** ΢��id */
	private String ID;
	/** ����ʱ�� */
	private String CreatedTime;
	/** ���� */
	private String Text;
	/** ��Դ */
	private String Source;
	/** ת���� */
	private String Reposts_count;
	/** ������ */
	private String Comment_count;
	/** ����uid */
	private String UID;
	/** �����ǳ� */
	private String Screen_name;
	/** ͷ���ַ */
	private String IconUrl;
	/** ����ͼ��ַ���� */
	private  String[] Pic_Urls;
	/** �еȳߴ�ͼƬ��ַ */
	private String Midpic_Url;
	/** �Ƿ���ת�� */
	private boolean Hasretweeted_status = false;
	/** ת������ */
	private String Retweeted_Text;
	/** ת�������� */
	private String Retweeted_Screen_name;
	/** ת����������ͼ��ַ���� */
	private String[] Retweeted_Pic_Urls;
	/** ת�������еȳߴ�ͼƬ��ַ */
	private String Retweeted_Midpic_Url;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getCreatedTime() {
		return CreatedTime;
	}

	public void setCreatedTime(String createdTime) {
		CreatedTime = createdTime;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getReposts_count() {
		return Reposts_count;
	}

	public void setReposts_count(String reposts_count) {
		Reposts_count = reposts_count;
	}

	public String getComment_count() {
		return Comment_count;
	}

	public void setComment_count(String comment_count) {
		Comment_count = comment_count;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public String getScreen_name() {
		return Screen_name;
	}

	public void setScreen_name(String screen_name) {
		Screen_name = screen_name;
	}

	public String getIconUrl() {
		return IconUrl;
	}

	public void setIconUrl(String iconUrl) {
		IconUrl = iconUrl;
	}

	public String[] getPic_Urls() {
		return Pic_Urls;
	}

	public void setPic_Urls(String[] pic_Urls) {
		Pic_Urls = pic_Urls;
	}

	public String getMidpic_Url() {
		return Midpic_Url;
	}

	public void setMidpic_Url(String midpic_Url) {
		Midpic_Url = midpic_Url;
	}

	public boolean isHasretweeted_status() {
		return Hasretweeted_status;
	}

	public void setHasretweeted_status(boolean hasretweeted_status) {
		Hasretweeted_status = hasretweeted_status;
	}

	public String getRetweeted_Text() {
		return Retweeted_Text;
	}

	public void setRetweeted_Text(String retweeted_Text) {
		Retweeted_Text = retweeted_Text;
	}

	public String getRetweeted_Screen_name() {
		return Retweeted_Screen_name;
	}

	public void setRetweeted_Screen_name(String retweeted_Screen_name) {
		Retweeted_Screen_name = retweeted_Screen_name;
	}

	public String[] getRetweeted_Pic_Urls() {
		return Retweeted_Pic_Urls;
	}

	public void setRetweeted_Pic_Urls(String[] retweeted_Pic_Urls) {
		Retweeted_Pic_Urls = retweeted_Pic_Urls;
	}

	public String getRetweeted_Midpic_Url() {
		return Retweeted_Midpic_Url;
	}

	public void setRetweeted_Midpic_Url(String retweeted_Midpic_Url) {
		Retweeted_Midpic_Url = retweeted_Midpic_Url;
	}

}
