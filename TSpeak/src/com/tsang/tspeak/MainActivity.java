package com.tsang.tspeak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsang.tspeak.fragment.HomeFragment;
import com.tsang.tspeak.model.Emotions;
import com.tsang.tspeak.model.LoginInfo;
import com.tsang.tspeak.model.db.TSpeakDataHelper;
import com.tsang.tspeak.util.Utils;
import com.tsang.tspeak.view.drawer.ActionBarDrawerToggle;
import com.tsang.tspeak.view.drawer.DrawerArrowDrawable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	static final String TAG = "MainActivity";

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	private LinearLayout mDrawerContent;
	private ListView mDrawerMenuList;
	private int mCurrentMenuItemPosition = -1;
	public String[] MENU_ITEMS;
	private TSpeakDataHelper dataHelper;
	private ImageView userIcon;
	private TextView userName;
	public static String mToken;
	public static String mUId;
	private String mUserName;

	private ImageLoader mImageLoader;

	private static boolean isExit = false;

	public static List<Emotions> emotionList = new ArrayList<Emotions>();
	RelativeLayout mLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLayout = (RelativeLayout) findViewById(R.id.layout_main);
		dataHelper = new TSpeakDataHelper(this);
		MENU_ITEMS = getResources().getStringArray(R.array.menu_list);
		mImageLoader = ImageLoader.getInstance();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drw_layout);
		Utils.setStatusBarTheme(this, mLayout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				drawerArrow, // Drawer 的 Icon
				R.string.open_left_drawer, R.string.close_left_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);

				getSupportActionBar().setTitle(R.string.homepage);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(R.string.app_all_name);
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		setDrawerMenu();
		if (savedInstanceState == null) {
			selectMenuItem(1);
		}

		initEmotionsList();

	}

	/** 初始化表情列表 */
	private void initEmotionsList() {
		String[] emotions_phrase = getResources().getStringArray(
				R.array.emotions_phrase);
		String[] emotions_imagename = getResources().getStringArray(
				R.array.emotions_imagename);
		Emotions emotions;
		for (int i = 0; i < 72; i++) {
			emotions = new Emotions();
			emotions.setImageName(emotions_imagename[i]);
			emotions.setPhrase(emotions_phrase[i]);
			emotionList.add(emotions);
		}
	}

	private void setDrawerMenu() {

		mDrawerContent = (LinearLayout) findViewById(R.id.left_drawer);
		mDrawerMenuList = (ListView) findViewById(R.id.left_drawer_menu);
		View view = LayoutInflater.from(this).inflate(
				R.layout.view_drawerlist_head, null);
		userIcon = (ImageView) view.findViewById(R.id.left_icon);
		userName = (TextView) view.findViewById(R.id.left_username);
		mDrawerMenuList.addHeaderView(view);

		Intent intent = getIntent();
		mToken = intent.getStringExtra("token");
		mUId = intent.getStringExtra("uid");

		// initEmotionData(mToken);

		LoginInfo info = dataHelper.GetOneLoginRecord(mUId);
		mUserName = info.getUserName();
		userName.setText(mUserName);
		mImageLoader.displayImage(info.getLargeIconUrl(), userIcon);

		mDrawerMenuList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.item_drawer_menu, MENU_ITEMS));
		mDrawerMenuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectMenuItem(position);
			}
		});
	}

	private void selectMenuItem(int position) {
		mCurrentMenuItemPosition = position;
		if (position == 1) {
			Fragment fragment = HomeFragment.newInstance(MainActivity.this,
					mToken, 20, 1);
			Bundle bundle = new Bundle();
			fragment.setArguments(bundle);
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.content_frame, fragment)
					.commit();
			getSupportActionBar().setTitle(
					MENU_ITEMS[mCurrentMenuItemPosition - 1]);
		} else if (position == 2) {

			Intent intent = new Intent(MainActivity.this, PersonActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("token", mToken);
			bundle.putString("uid", mUId);
			intent.putExtras(bundle);
			startActivity(intent);

		} else if (position == 3) {

		} else if (position == 4) {
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
		} else if (position == 5) {
			finish();
		}

		mDrawerLayout.closeDrawer(mDrawerContent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.write_new_status, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.isCheckable()) {
			item.setChecked(true);
		}
		switch (item.getItemId()) {
//		case R.id.write_new_status:
//			Intent intent = new Intent(this, NewStatusActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putString("token", mToken);
//			bundle.putString("uid", mUId);
//			bundle.putInt("type", NewStatusActivity.TYPE_NEW);
//			intent.putExtras(bundle);
//			startActivity(intent);
//			break;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		return false;
	}

	/** 双击返回退出程序 */
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出
			Toast.makeText(this,
					getResources().getString(R.string.exitBy2Click),
					Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					isExit = false;
				}
			}, 2000);// 如果2秒内没有按下返回键，启动定时器取消刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}
}
