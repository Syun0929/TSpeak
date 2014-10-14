package com.tsang.tspeak.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tsang.tspeak.ImagePagerActivity;
import com.tsang.tspeak.LoginActivity;
import com.tsang.tspeak.MainActivity;
import com.tsang.tspeak.R;
import com.tsang.tspeak.model.Emotions;
import com.tsang.tspeak.model.LoginInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 公共方法类
 * 
 * @author zengjiyang
 * 
 */
public class Utils {

	/** 检查网络是否连接 */
	public static boolean checkConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
			return true;
		}
		return false;
	}

	public static String getLoginStringFromUrl(String url, String token,
			String uid) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url + "?access_token=" + token + "&uid="
				+ uid);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}

	/** 通过get方式获取内容 */
	public static String getStringFromUrl(String url)
			throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity, "UTF-8");
	}

	/**
	 * 把GMT时间转化为UTC时间
	 * 
	 * @param gmtDatetime
	 *            GMT时间
	 * @return yyyy-MM-dd HH:mm
	 */
	public static String getCSTDate(String gmtDatetime) {
		SimpleDateFormat df = new SimpleDateFormat(
				"EEE MMM dd hh:mm:ss zzzzz yyyy", Locale.ENGLISH);
		Date dd;
		try {
			dd = df.parse(gmtDatetime);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
			sdf.setTimeZone(TimeZone.getDefault());
			return sdf.format(dd);
		} catch (ParseException e) {
			e.printStackTrace();
			return gmtDatetime;
		}
	}

	/**
	 * 访问网络获取类
	 * 
	 * @param imageURL
	 * @return
	 */
	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;

	}

	/**
	 * 动态设置listview高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView != null) {
			ListAdapter listAdapter = listView.getAdapter();
			int totalHeight = 0;
			int count = listAdapter.getCount();
			for (int i = 0; i < count; i++) {
				View child = listAdapter.getView(i, null, listView);
				child.measure(0, 0);
				totalHeight += child.getMeasuredHeight();
			}
			if (count > 0) {
				totalHeight += (count - 1) * listView.getDividerHeight();
			}
			LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight;
			listView.setLayoutParams(params);
		}
	}

	/**
	 * 高亮部分文本
	 * 
	 * @param content
	 *            文本内容
	 * @return
	 */
	public static SpannableString setTextHighLightAndTxtToImg(String content,
			Context context) {
		SpannableString result = new SpannableString(content);

		if (content.contains("@")) {
			Pattern p = Pattern.compile(Constants.regex_at);
			Matcher m = p.matcher(result);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				result.setSpan(
						(new ForegroundColorSpan(Color.parseColor("#33b5e5"))),
						start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}

		if (content.contains("#")) {
			Pattern p = Pattern.compile(Constants.regex_sharp);
			Matcher m = p.matcher(result);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				result.setSpan(
						(new ForegroundColorSpan(Color.parseColor("#ff7d00"))),
						start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}

		if (content.contains("http://")) {
			Pattern p = Pattern.compile(Constants.regex_http);
			Matcher m = p.matcher(result);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				result.setSpan(
						(new ForegroundColorSpan(Color.parseColor("#33b5e5"))),
						start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}

		if (content.contains("[") && content.contains("]")) {
			Pattern p = Pattern.compile(Constants.regex_emoji);
			Matcher m = p.matcher(result);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				String phrase = content.substring(start, end);
				String imageName = "";
				List<Emotions> list = MainActivity.emotionList;
				for (Emotions emotions : list) {
					if (emotions.getPhrase().equals(phrase)) {
						imageName = emotions.getImageName();
					}
				}
				try {
					Field f = (Field) R.drawable.class
							.getDeclaredField(imageName);
					int i = f.getInt(R.drawable.class);
					Drawable drawable = context.getResources().getDrawable(i);
					if (drawable != null) {
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
								drawable.getIntrinsicHeight());
						ImageSpan span = new ImageSpan(drawable,
								ImageSpan.ALIGN_BASELINE);
						result.setSpan(span, start, end,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
				}

			}

		}

		return result;
	}

	public static SpannableString txtToImg(String content, Context context) {
		SpannableString result = new SpannableString(content);
		int starts = 0;
		int end = 0;

		if (content.indexOf("[", starts) != -1
				&& content.indexOf("]", end) != -1) {
			starts = content.indexOf("[", starts);
			end = content.indexOf("]", end);
			String phrase = content.substring(starts, end + 1);
			String imageName = "";
			List<Emotions> list = MainActivity.emotionList;
			for (Emotions emotions : list) {
				if (emotions.getPhrase().equals(phrase)) {
					imageName = emotions.getImageName();
				}
			}

			try {
				Field f = (Field) R.drawable.class.getDeclaredField(imageName);
				int i = f.getInt(R.drawable.class);
				Drawable drawable = context.getResources().getDrawable(i);
				if (drawable != null) {
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					ImageSpan span = new ImageSpan(drawable,
							ImageSpan.ALIGN_BASELINE);
					result.setSpan(span, starts, end + 1,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {

			}

		}
		return result;
	}

	/**
	 * 压缩图片，避免内存不足报错
	 */
	public static Bitmap decodeFile(File f) {
		Bitmap b = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			FileInputStream fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int scale = 1;
			if (o.outHeight > 100 || o.outWidth > 100) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(100 / (double) Math.max(
								o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/** 计算汉字个数 */
	public static int length(String paramString) {
		int i = 0;
		for (int j = 0; j < paramString.length(); j++) {
			if (paramString.substring(j, j + 1).matches("[Α-￥]")) {
				i += 2;
			} else {
				i++;
			}
		}

		if (i % 2 > 0) {
			i = 1 + i / 2;
		} else {
			i = i / 2;
		}

		return i;
	}

	/**
	 * 打开图片浏览大图
	 * 
	 * @param context
	 * @param url
	 */
	public static void startImagePagerActivity(Context context, String url) {
		String result = url.replace("bmiddle", "large");
		Intent intent = new Intent(context, ImagePagerActivity.class);
		String[] urls = { result };
		intent.putExtra("images", urls);
		context.startActivity(intent);
	}

	/**
	 * 打开图片浏览大图
	 * 
	 * @param context
	 * @param urls
	 * @param position
	 */
	public static void startImagePagerActivity(Context context, String[] urls,
			int position) {
		String[] original_urls = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			original_urls[i] = urls[i].replace("thumbnail", "large");
		}
		Intent intent = new Intent(context, ImagePagerActivity.class);
		intent.putExtra("images", original_urls);
		intent.putExtra("pagerPosition", position);
		context.startActivity(intent);
	}

	public static boolean isFilePath(String path) {
		String regex = "[a-zA-Z]:(?:[/\\\\][^/\\\\:*?\"<>|]{1,255})+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(path);
		return matcher.matches();
	}
	/**
	 * 设置在android4.4以上的系统中，设置状态栏和虚拟按键透明，扩大可视区域。状态栏可以设置成与actionbar一样的颜色
	 */
	public static void setStatusBarTheme(Activity activity, View v) {
		if (android.os.Build.VERSION.SDK_INT > 18) {
			// 设置状态栏和虚拟按键透明
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// window.setFlags(0x04000000, 0x04000000);
			// window.setFlags(0x08000000, 0x08000000);

			v.setPadding(0, getActionBarHeight(activity)
					+ getStatusBarHeight(activity), 0, 0);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.actionbar_bg);

	}

	public static int getStatusBarHeight(Activity activity) {
		int x = 0, statusBarHeight = 0;
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = activity.getResources().getDimensionPixelSize(x);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	public static int getActionBarHeight(Activity activity) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize,
				tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					activity.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}
}
