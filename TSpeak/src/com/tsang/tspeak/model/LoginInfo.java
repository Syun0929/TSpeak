package com.tsang.tspeak.model;

import android.graphics.drawable.Drawable;

/**
 * 登录帐户的信息
 * @author zengjiyang
 *
 */
public class LoginInfo {

	public static final String _ID = "id";
	public static final String USERID = "userid";
	public static final String TOKEN = "token";
	public static final String USERNAME = "username";
	public static final String ICONURL = "iconurl";
	public static final String ICONURL_LARGE = "largeiconurl";

	private String ID;
	private String UserID;
	private String Token;
	private String UserName;
	private String LargeIconUrl;
	private String IconUrl;

	public String getIconUrl() {
		return IconUrl;
	}

	public void setIconUrl(String iconUrl) {
		IconUrl = iconUrl;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getLargeIconUrl() {
		return LargeIconUrl;
	}

	public void setLargeIconUrl(String largeIconUrl) {
		LargeIconUrl = largeIconUrl;
	}



}
