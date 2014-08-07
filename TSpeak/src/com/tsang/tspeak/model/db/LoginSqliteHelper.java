package com.tsang.tspeak.model.db;

import com.tsang.tspeak.model.LoginInfo;

import android.R.integer;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoginSqliteHelper extends SQLiteOpenHelper {

	public static final String TB_NAME = "login_account";

	public LoginSqliteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	String CreateTableSQL = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ("
			+ LoginInfo._ID + " integer primary key," + LoginInfo.USERID
			+ " varchar," + LoginInfo.TOKEN + " varchar," + LoginInfo.USERNAME
			+ " varchar, " + LoginInfo.ICONURL + " varchar,"
			+ LoginInfo.ICONURL_LARGE + " varchar" + ")";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CreateTableSQL);
		Log.d(TB_NAME, "onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
		onCreate(db);
		Log.d(TB_NAME, "onUpgrade");
	}

	/** ¸üÐÂÁÐ */
	public void updateColumn(SQLiteDatabase db, String oldColumn,
			String newColumn, String typeColumn) {
		try {
			db.execSQL("ALTER TABLE " + TB_NAME + " CHANGE " + oldColumn + " "
					+ newColumn + " " + typeColumn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
