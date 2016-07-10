package com.moershizhi.ddc.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.moershizhi.ddc.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class COMMON {
	
	public static Date str2date(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		
		try {
			date = (Date) sdf.parse(str);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

	public static void newThread(final Handler handler, final View view) {
		new Thread() {
			@Override
			public void run() {
				Message message = handler.obtainMessage();
				handler.sendMessage(message);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						view.setVisibility(View.GONE);
					}
				}, CONSTANT.DELAYED_TIME);
			}
		}.start();
	}

	public static LinearLayout setProgressBar(Context context) {
		LinearLayout ll_pb = new LinearLayout(context);
		((Activity) context).addContentView(ll_pb, CONSTANT.MATCH_PARENT);

		ll_pb.setOrientation(LinearLayout.VERTICAL);
		ll_pb.setGravity(Gravity.CENTER);

		TextView tv = new TextView(context);
		ll_pb.addView(tv);

		tv.setText(R.string.loading);
		tv.setTextSize(20);
		tv.setTextColor(Color.YELLOW);
		tv.setGravity(Gravity.CENTER);

		ImageView iv = new ImageView(context);
		ll_pb.addView(iv);

		iv.setImageResource(R.drawable.pbar);

		return ll_pb;
	}

	/**
	 * 获取输入流数据
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream content) throws Exception {

		byte[] buffer = new byte[1024];
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		int len = -1;
		while ((len = content.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}

		outStream.close();
		content.close();

		return outStream.toByteArray();
	}

	public final static String MD5(String s) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z' };

		try {
			byte[] strTemp = s.getBytes();

			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();

			int j = md.length;
			char str[] = new char[j * 2];

			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}

			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把dip单位转成px单位
	 * 
	 * @param context
	 *            context对象
	 * @param dip
	 *            dip数值
	 * 
	 * @return
	 * */
	@SuppressLint("FloatMath")
	public static int dip2px(Context context, int dip) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		return (int) Math.ceil(dip * dm.density);
	}

	// 格式化时间 yyyy/MM/dd
	public static String get_time_d(String str) {
		Timestamp date = Timestamp.valueOf(str);
		return new SimpleDateFormat("yyyy/MM/dd").format(date);
	}

	// 格式化时间
	public static String format(Object time) {

		if (time == null) {
			return "";
		} else {
			String value = time.toString().replaceAll("/", "-");
			value = dealString(value);
			try {
				Timestamp date = Timestamp.valueOf(value);

				return new SimpleDateFormat("yyyy/MM/dd").format(date);

			} catch (Exception e) {

				try {
					System.out.println("time:" + value);
					Timestamp date = Timestamp.valueOf(value + " 00:00:00.0");

					return new SimpleDateFormat("yyyy/MM/dd").format(date);
				} catch (Exception e1) {

					return "";
				}
			}
		}
	}

	// 格式化时间
	public static String formatTimeString(int year, int month, int day) {
		String y = year + "";
		String m = month + "";
		String d = day + "";
		while (year < 1000) {
			year *= 10;
			y = "0" + y;
		}
		if (month < 10) {
			m = "0" + m;
		}
		if (day < 10) {
			d = "0" + d;
		}
		return y + "/" + m + "/" + d;
	}

	// 处理日期格式的位数
	public static String dealString(String s) {
		try {
			String year = s.substring(0, s.indexOf("-"));
			String month = s.substring(s.indexOf("-") + 1, s.lastIndexOf("-"));
			String day = s.substring(s.lastIndexOf("-") + 1).split(" ")[0];

			if (month.length() < 2) {
				month = "-0" + month;
			} else if (month.length() == 2) {
				month = "-" + month;
			}
			if (day.length() < 2) {
				day = "-0" + day;
			} else if (day.length() == 2) {
				day = "-" + day;
			}
			s = year + month + day;

		} catch (Exception e) {
		}

		return s;
	}

	/**
	 * 将List<Map<String, Object>>转换为数组
	 * 
	 * @param list
	 * @return
	 */
	public static Object[] list2StringArray(List<Map<String, Object>> list) {
		if (list == null) {
			return new String[] { "无" };
		}
		Object[] array = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			array[i] = map.get("name");
		}

		return array;
	}

	/**
	 * 将List<Map<String, Object>>转换为List<String>
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> list2ArrayList(List<Map<String, Object>> listMap) {
		List<String> list = new ArrayList<String>();
		list.add("所有");

		if (null != listMap) {
			for (int i = 0; i < listMap.size(); i++) {
				Map<String, Object> map = listMap.get(i);
				list.add(map.get("name") + "");
			}
		}

		return list;
	}

	/**
	 * 在array中搜索str，返回位置
	 * 
	 * @param array
	 * @param str
	 * @return
	 */
	public static int search(String[] array, String str) {
		if (str == null)
			return 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(str)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 通知
	 * 
	 * @param context
	 * @param msg
	 */
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 格式化浮点数,默认保留2位小数
	 */
	public static float round(float v, int scale) {
		if (scale < 0)
			return v;

		String temp = "#####0.";
		for (int i = 0; i < scale; i++) {
			temp += "0";
		}

		return Float.valueOf(new java.text.DecimalFormat(temp).format(v));
	}

	/**
	 * 信息提示
	 * 
	 * @return
	 */
	public static AlertDialog alertDialog(Context context, int tID, int mID) {
		return new AlertDialog.Builder(context).setTitle(tID).setMessage(mID)
				.setPositiveButton(R.string.ok, null).show();
	}

	public static AlertDialog alertDialog(Context context, int tID,
			String message) {
		return new AlertDialog.Builder(context).setTitle(tID)
				.setMessage(message).setPositiveButton(R.string.ok, null)
				.show();
	}

	@SuppressLint({ "FloatMath", "FloatMath" })
	public static int dp2Px(Context context, int dp) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		return (int) Math.ceil(dp * dm.density);
	}

	public static InputStream String2InputStream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	public static String inputStream2String(InputStream is) {

		StringBuffer buffer = null;

		if (null != is) {
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			String line = null;
			buffer = new StringBuffer();

			try {
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}

				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return buffer.toString();
	}

	/**
	 * StrictMode 2.3 及以后版本特性，增加流畅度
	 */
	public static void setStrictMode() {
		if (CONSTANT.SDK_VERSION >= 9) {
			// or .detectAll() for all detectable problems
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());

//			if (CONSTANT.SDK_VERSION >= 11) {
//				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//						.detectLeakedSqlLiteObjects()
//						.detectLeakedClosableObjects().penaltyLog()
//						.penaltyDeath().build());
//			}
//			else {
				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects().penaltyLog()
						.penaltyDeath().build());
//			}
		}
	}

	public static String getGender(int position) {
		String code;
		if (1 == position) {
			code = CONSTANT.MALE_CODE;
		} else {
			code = CONSTANT.FEMALE_CODE;
		}

		return code;
	}
	
	public static String getJobKind(int position) {
		String code = "";
		if (1 == position) {
			code = CONSTANT.GZ_QZ;
		}
		else if (2 == position) {
			code = CONSTANT.GZ_JZ;
		}

		return code;
	}
	
	public static String getDegree(int position) {
		String code = "";
		if (1 == position) {
			code = CONSTANT.XL_QT;
		}
		else if (2 == position) {
			code = CONSTANT.XL_BS;
		}
		else if (3 == position) {
			code = CONSTANT.XL_SS;
		}
		else if (4 == position) {
			code = CONSTANT.XL_BK;
		}
		else if (5 == position) {
			code = CONSTANT.XL_DZ;
		}
		else if (6 == position) {
			code = CONSTANT.XL_ZZ;
		}
		else if (7 == position) {
			code = CONSTANT.XL_GJ;
		}
		else if (8 == position) {
			code = CONSTANT.XL_ZJ;
		}
		else if (9 == position) {
			code = CONSTANT.XL_GZ;
		}
		else if (10 == position) {
			code = CONSTANT.XL_CZ;
		}

		return code;
	}

	public static String getCertType(int position) {
		String code;
		if (1 == position) {
			code = CONSTANT.ID_CODE;
		} else if (2 == position) {
			code = CONSTANT.PASSPORT_CODE;
		} else if (3 == position) {
			code = CONSTANT.CERT_CODE;
		} else if (4 == position) {
			code = CONSTANT.MILITARY_CODE;
		} else {
			code = CONSTANT.CERT_CODE;
		}

		return code;
	}

	public static String getPersonStatus(int position) {
		String code;
		if (1 == position) {
			code = CONSTANT.PS_YJ_CODE;
		} else if (2 == position) {
			code = CONSTANT.PS_QC_CODE;
		} else if (3 == position) {
			code = CONSTANT.PS_SW_CODE;
		} else {
			code = CONSTANT.PS_CODE;
		}

		return code;
	}

	public static String getPoliticalType(int position) {
		String code;
		if (1 == position) {
			code = CONSTANT.POLITICAL_T_CODE;
		} else if (2 == position) {
			code = CONSTANT.POLITICAL_D_CODE;
		} else if (3 == position) {
			code = CONSTANT.POLITICAL_M_CODE;
		} else {
			code = CONSTANT.POLITICAL_CODE;
		}

		return code;
	}

	public static String getWwdxType(int position) {
		String code;
		if (1 == position) {
			code = CONSTANT.WW_XJ_CODE;
		} else if (2 == position) {
			code = CONSTANT.WW_XM_CODE;
		} else {
			code = CONSTANT.WW_CODE;
		}

		return code;
	}

	public static void showDialog(final Context context, int title, int layout,
			View parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(layout, null, false);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);

		builder.setTitle(title)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).setNegativeButton(R.string.cancel, null).create()
				.show();
	}

}
