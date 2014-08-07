package com.tsang.tspeak.model;

/** 微博发布人 */
public class User {

	private String UID;
	private String Screen_Name;
	private String IconUrl;
	private String LargeIconUrl;
	private String Location;
	private String Gender;// 性别
	private String Description;
	private String Followers_count;
	private String Friends_count;
	private String Statuses_count;
	private String Created_at;
	private boolean Following;
	private boolean Follow_me;

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public String getScreen_Name() {
		return Screen_Name;
	}

	public void setScreen_Name(String screen_Name) {
		Screen_Name = screen_Name;
	}

	public String getIconUrl() {
		return IconUrl;
	}

	public void setIconUrl(String iconUrl) {
		IconUrl = iconUrl;
	}

	public String getLargeIconUrl() {
		return LargeIconUrl;
	}

	public void setLargeIconUrl(String largeIconUrl) {
		LargeIconUrl = largeIconUrl;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getGender() {
		return Gender;
	}

	public void setGender(String gender) {
		Gender = gender;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getFollowers_count() {
		return Followers_count;
	}

	public void setFollowers_count(String followers_count) {
		Followers_count = followers_count;
	}

	public String getFriends_count() {
		return Friends_count;
	}

	public void setFriends_count(String friends_count) {
		Friends_count = friends_count;
	}

	public String getStatuses_count() {
		return Statuses_count;
	}

	public void setStatuses_count(String statuses_count) {
		Statuses_count = statuses_count;
	}

	public String getCreated_at() {
		return Created_at;
	}

	public void setCreated_at(String created_at) {
		Created_at = created_at;
	}

	public boolean isFollowing() {
		return Following;
	}

	public void setFollowing(boolean following) {
		Following = following;
	}

	public boolean isFollow_me() {
		return Follow_me;
	}

	public void setFollow_me(boolean follow_me) {
		Follow_me = follow_me;
	}

}
