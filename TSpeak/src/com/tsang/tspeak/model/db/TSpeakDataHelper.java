package com.tsang.tspeak.model.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.tsang.tspeak.model.LoginInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class TSpeakDataHelper {

	private static final String DB_NAME = "tspeakweibo.db";
	private static int DB_VERSION = 1;
	private SQLiteDatabase db;
	private LoginSqliteHelper dbHelper;

	public TSpeakDataHelper(Context context) {
		dbHelper = new LoginSqliteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public void Close() {
		db.close();
		dbHelper.close();
	}

	/** ��ȡ login���е� UserID��Access Token��Access Secret �ļ�¼ */
	public List<LoginInfo> GetLoginList() {
		List<LoginInfo> mList = new ArrayList<LoginInfo>();
		Cursor mCursor = db.query(LoginSqliteHelper.TB_NAME, null, null, null,
				null, null, LoginInfo._ID + " DESC");
		LoginInfo loginInfo;
		while (mCursor.moveToNext()) {
			loginInfo = new LoginInfo();
			loginInfo.setID(mCursor.getString(0));
			loginInfo.setUserID(mCursor.getString(1));
			loginInfo.setToken(mCursor.getString(2));
			loginInfo.setUserName(mCursor.getString(3));
			loginInfo.setIconUrl(mCursor.getString(4));
			loginInfo.setLargeIconUrl(mCursor.getString(5));
			mList.add(loginInfo);
		}
		mCursor.close();
		return mList;
	}

	/** �жϱ����Ƿ���ĳ��userid�ļ�¼ */
	public Boolean HaveUserInfo(String UserID) {
		Boolean result = false;
		Cursor mCursor = db.query(LoginSqliteHelper.TB_NAME, null,
				LoginInfo.USERID + "=" + UserID, null, null, null, null);
		result = mCursor.moveToFirst();
		Log.e("HaveLoginInfo", result.toString());
		mCursor.close();
		return result;
	}

	/** �����û�id��ȡ��¼�û���¼ */
	public LoginInfo GetOneLoginRecord(String UserID) {
		LoginInfo loginInfo=null;
		Cursor mCursor = db.query(LoginSqliteHelper.TB_NAME, null,
				LoginInfo.USERID + "=" + UserID, null, null, null, null);
		while (mCursor.moveToNext()) {
			loginInfo = new LoginInfo();
			loginInfo.setID(mCursor.getString(0));
			loginInfo.setUserID(mCursor.getString(1));
			loginInfo.setToken(mCursor.getString(2));
			loginInfo.setUserName(mCursor.getString(3));
			loginInfo.setIconUrl(mCursor.getString(4));
			loginInfo.setLargeIconUrl(mCursor.getString(5));
		}
		mCursor.close();
		return loginInfo;
	}

	/** ����login��ļ�¼������userid�����û�����ͷ���ַ */
	public int UpdateLoginInfo(String userName, String iconUrl,
			String largeIconUrl, String userId) {
		ContentValues values = new ContentValues();
		values.put(LoginInfo.USERNAME, userName);
		values.put(LoginInfo.ICONURL, iconUrl);
		values.put(LoginInfo.ICONURL_LARGE, largeIconUrl);
		int id = db.update(LoginSqliteHelper.TB_NAME, values, LoginInfo.USERID
				+ "=" + userId, null);
		Log.e("UpdateLoginInfo2", "==========>" + id);
		return id;
	}

	/** ����login��ļ�¼ */
	public int UpdateLoginInfo(LoginInfo loginInfo) {
		ContentValues values = new ContentValues();
		values.put(LoginInfo.USERID, loginInfo.getUserID());
		values.put(LoginInfo.TOKEN, loginInfo.getToken());
		int id = db.update(LoginSqliteHelper.TB_NAME, values, LoginInfo.USERID
				+ "=" + loginInfo.getUserID(), null);
		Log.e("UpdateLoginInfo", "==========>" + id);
		return id;
	}

	/** ����login��ļ�¼ */
	public long InsertLoginInfo(LoginInfo loginInfo) {
		ContentValues values = new ContentValues();
		values.put(LoginInfo.USERID, loginInfo.getUserID());
		values.put(LoginInfo.TOKEN, loginInfo.getToken());
		long id = db.insert(LoginSqliteHelper.TB_NAME, LoginInfo._ID, values);
		Log.e("InsertLoginInfo", "==========>" + id);
		return id;
	}

	/** ɾ��login��ļ�¼ */
	public int DeleteLoginInfo(String userId) {
		int id = db.delete(LoginSqliteHelper.TB_NAME, LoginInfo.USERID + "="
				+ userId, null);
		Log.e("DeleteLoginInfo", "==========>" + id);
		return id;
	}
}
