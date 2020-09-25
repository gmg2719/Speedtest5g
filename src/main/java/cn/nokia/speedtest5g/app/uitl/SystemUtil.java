package cn.nokia.speedtest5g.app.uitl;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.util.SharedPreHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_PhoneAppInfo;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 系统工具类
 * 
 * @author xujianjun
 *
 */
public class SystemUtil {

	private static SystemUtil mInstance;

	public static SystemUtil getInstance() {
		if (mInstance == null) {
			mInstance = new SystemUtil();
		}
		return mInstance;
	}

	/**
	 * 获取当前手机系统版本号
	 * 
	 * @return 系统版本号
	 */
	public String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}
	
	public int getSystemToV(){
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取手机型号
	 * 
	 * @return 手机型号
	 */
	public String getSystemModel() {
		return Build.MODEL;
	}

	/**
	 * 获取手机厂商
	 * 
	 * @return 手机厂商
	 */
	public String getDeviceBrand() {
		return Build.BRAND;
	}

	/**
	 * 获取手机IMEI
	 * 
	 * @return 手机IMEI
	 */
	public String getIMEI(Activity a) {
		// TelephonyManager tm = (TelephonyManager)
		// a.getSystemService(Activity.TELEPHONY_SERVICE);
		// if (tm != null) {
		// return tm.getDeviceId();
		// }
		return SharedPreHandler.getShared(a).getStringShared(TypeKey.getInstance().PHONE_IMEI(), "");
	}

	/**
	 * 获取手机IMSI
	 * 
	 * @return 手机IMEI
	 */
	public String getIMSI(Activity a) {
		return SharedPreHandler.getShared(a).getStringShared(TypeKey.getInstance().PHONE_IMSI(), "");
	}

	/**
	 * 获取运营商
	 * 
	 * @return
	 */
	public String getISP(Context a) {
		TelephonyManager telManager = (TelephonyManager) a.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		String isp = "";
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
				isp = "中国移动";
			} else if (operator.equals("46001") || operator.equals("46006")) {
				isp = "中国联通";
			} else if (operator.equals("46003") || operator.equals("46005") || operator.equals("46011")) {
				isp = "中国电信";
			} else {
				isp = "未知";
			}
		}
		return isp;
	}

	/**
	 * 获取运营商(去除中国)
	 * 
	 * @return
	 */
	public String getISPNew(Context a) {
		String isp = getISP(a);
//		if (isp.contains("中国")) {
//			isp = isp.replace("中国", "");
//		}
		return isp;
	}

	/**
	 * 获取运营商白底Logo资源Id
	 * 
	 * @param operator
	 *            运营商
	 * @return Logo资源Id
	 */
	public int getOperatorLogoWhiteBgResId(String operator) {
		int operatorLogoResId;
		if ("中国移动".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_mobile_white_bg;
		} else if ("中国电信".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_telecom_white_bg;
		} else if ("中国联通".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_unicom_white_bg;
		} else {
			operatorLogoResId = R.drawable.icon_operator_logo_unknow_white_bg;
		}
		return operatorLogoResId;
	}

	/**
	 * 获取运营商蓝底Logo资源Id
	 * 
	 * @param operator
	 *            运营商
	 * @return Logo资源Id
	 */
	public int getOperatorLogoBlueBgResId(String operator) {
		int operatorLogoResId = -1;
		if ("中国移动".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_mobile_blue_bg;
		} else if ("中国电信".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_telecom_blue_bg;
		} else if ("中国联通".equals(operator)) {
			operatorLogoResId = R.drawable.icon_operator_logo_china_unicom_blue_bg;
		} else {
			operatorLogoResId = R.drawable.icon_operator_logo_unknow_blue_bg;
		}
		return operatorLogoResId;
	}

	/**
	 * 获取iccid
	 * 
	 * @param a
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public String getIccid(Activity a) {
		TelephonyManager tm = (TelephonyManager) a.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getSimSerialNumber();// 取出ICCID
		}
		return "";
	}

	/**
	 * 获取电话号码
	 * 
	 * @param a
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public String getPhoneNum(Activity a) {
		// 创建电话管理
		TelephonyManager tm = (TelephonyManager) a.getSystemService(Context.TELEPHONY_SERVICE);
		// 与手机建立连接
		if (tm != null) {
			// 获取手机号码
			String number = tm.getLine1Number();
			if (TextUtils.isEmpty(number)) {
				number = "不可获取";
			}
			return number;
		}

		return "未知";
	}

	public boolean isRootSystem() {
		File f = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
		        "/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 内存总容量
	 * 
	 * @param context
	 * @return 返回值单位为Byte 展示时需要自行转换Formatter.formatFileSize(context,long)
	 */
	@SuppressLint("NewApi")
	public long getMemoryTotal(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.totalMem;
	}

	/**
	 * 可用内存大小
	 * 
	 * @param context
	 * @return 返回值单位为Byte 展示时需要自行转换Formatter.formatFileSize(context,long)
	 */
	public long getMemoryAvail(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	/**
	 * 已用内存大小
	 * 
	 * @param context
	 * @return 返回值单位为Byte 展示时需要自行转换Formatter.formatFileSize(context,long)
	 */
	@SuppressLint("NewApi")
	public long getMemoryUsed(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.totalMem - mi.availMem;
	}

	/**
	 * 可用内存阈值：可用内存低于该阈值系统认为内存不足，并开始杀死后台服务和其他非外部进程
	 * 
	 * @param context
	 * @return 返回值单位为Byte 展示时需要自行转换Formatter.formatFileSize(context,long)
	 */
	public long getMemoryThreshold(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.threshold;
	}

	/**
	 * 获取存储信息
	 * 
	 * @param context
	 * @param type=1获取总容量/2获取可用存储/3获取已用存储
	 * @return 返回值单位为Byte 展示时需要自行转换Formatter.formatFileSize(context,long)
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public long getStorageInfo(Context context, int type) {
		long totalStorager = 0;
		long availStorager = 0;
		long usedStorager = 0;
		// SDCard存储使用情况
		String state = Environment.getExternalStorageState();
		long blockSize = 0;
		long blockCount = 0;
		long availCount = 0;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				blockSize = sf.getBlockSizeLong();
				blockCount = sf.getBlockCountLong();
				availCount = sf.getAvailableBlocksLong();

			} else {
				blockSize = sf.getBlockSize();
				blockCount = sf.getBlockCount();
				availCount = sf.getAvailableBlocks();
			}
			totalStorager += blockSize * blockCount;
			availStorager += blockSize * availCount;
		}
		// 内置存储使用情况
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			blockSize = sf.getBlockSizeLong();
			blockCount = sf.getBlockCountLong();
			availCount = sf.getAvailableBlocksLong();

		} else {
			blockSize = sf.getBlockSize();
			blockCount = sf.getBlockCount();
			availCount = sf.getAvailableBlocks();
		}
		totalStorager += blockSize * blockCount;
		availStorager += blockSize * availCount;
		usedStorager = totalStorager - availStorager;
		switch (type) {
		case 1:
			return totalStorager;
		case 2:
			return availStorager;
		case 3:
			return usedStorager;
		default:
			return -1;
		}
	}

	/**
	 * 获取App存储大小:缓存大小，数据大小，应用大小，总计占用存储大小
	 * 
	 * @param context
	 * @param pkgName
	 * @param listenerBack
	 */
	public void getAppStorageSize(final Context context, final String pkgName, final ListenerBack listenerBack) {
		try {
			// getPackageSizeInfo是PackageManager中的一个private方法，所以需要通过反射的机制来调用
			Method method = PackageManager.class.getMethod("getPackageSizeInfo", new Class[] { String.class, IPackageStatsObserver.class });
			// 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
			method.invoke(context.getPackageManager(), pkgName, new IPackageStatsObserver.Stub() {
				@Override
				public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
					// 从pStats中提取各个所需数据
					Db_PhoneAppInfo appInfo = new Db_PhoneAppInfo();
					if(pStats != null){
						appInfo.codeSize = pStats.codeSize + pStats.externalCodeSize;
						appInfo.dataSize = pStats.dataSize + pStats.externalDataSize;
						appInfo.cacheSize = pStats.cacheSize + pStats.externalCacheSize;
					}
					appInfo.usedStorage = appInfo.codeSize + appInfo.dataSize + appInfo.cacheSize;
					if (listenerBack != null) {
						listenerBack.onListener(0, appInfo, succeeded);
					}
				}
			});

		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			if (listenerBack != null) {
				listenerBack.onListener(-1, null, false);
			}
		}
	}

	/**
	 * 获取屏幕信息
	 * 
	 * @param mContext
	 * @param type
	 *            1获取屏幕尺寸，2获取屏幕分辨率px，3获取屏幕像素密度
	 * @return
	 */
	public String getScreenInfo(Context mContext, int type) {
		int widthPixels;
		int heightPixels;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		widthPixels = displayMetrics.widthPixels;
		heightPixels = displayMetrics.heightPixels;
		double screenSizeD = Math.sqrt(Math.pow(widthPixels / displayMetrics.xdpi, 2) + Math.pow(heightPixels / displayMetrics.ydpi, 2));
		String screenSizeStr = String.format(Locale.CHINESE, "%.2f", screenSizeD);
		String screenDpi = String.format(Locale.CHINESE, "%.0f", Math.sqrt(Math.pow(widthPixels, 2) + Math.pow(heightPixels, 2)) / screenSizeD);
		switch (type) {
		case 1:// 屏幕尺寸
			return screenSizeStr + "英寸";
		case 2:// 屏幕分辨率
			return widthPixels + " X " + heightPixels;
		case 3:// 屏幕像素密度（国内主流手机厂商用ppi为单位）
			return screenDpi + "ppi";
		default:
			return "";
		}
	}

	public int getWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		// int height = dm.heightPixels;
		return width;
	}

	public int getHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		// int width = dm.widthPixels;
		int height = dm.heightPixels;
		return height;
	}

	/**
	 * 获取电池容量 mAh
	 *
	 * 源头文件:frameworks/base/core/res\res/xml/power_profile.xml
	 *
	 * Java
	 * 反射文件：frameworks\base\core\java\com\android\internal\os\PowerProfile.java
	 */
	public String getBatteryCapacity(Context context) {
		Object mPowerProfile;
		double batteryCapacity = 0;
		final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
		try {
			mPowerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
			batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(mPowerProfile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf((int) batteryCapacity + "mAh");
	}

	/**
	 * 电池状态类型转换成中文描述
	 * 
	 * @param statusType
	 *            状态类型
	 * @return
	 */
	public String converBatteryStatus(int statusType) {
		String result = "";
		if (statusType == BatteryManager.BATTERY_STATUS_CHARGING) {
			result = "充电";
		} else if (statusType == BatteryManager.BATTERY_STATUS_DISCHARGING) {
			result = "放电";
		} else if (statusType == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			result = "未在充电";
		} else if (statusType == BatteryManager.BATTERY_STATUS_FULL) {
			result = "充满";
		} else {
			result = "未知";
		}
		return result;
	}

	/**
	 * 电源连接类型换成中文描述
	 * 
	 * @param pluggedType
	 *            状态类型
	 * @return
	 */
	public String converBatteryPlugged(int pluggedType) {
		String result = "";
		if (pluggedType == BatteryManager.BATTERY_PLUGGED_AC) {
			result = "充电器充电";
		} else if (pluggedType == BatteryManager.BATTERY_PLUGGED_USB) {
			result = "USB充电";
		} else if (pluggedType == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
			result = "无线充电";
		} else {
			result = "电池";
		}
		return result;
	}

	/**
	 * 电源健康类型换成中文描述
	 * 
	 *            状态类型
	 * @return
	 */
	public String converBatteryHealth(int healthType) {
		String result = "";
		if (healthType == BatteryManager.BATTERY_HEALTH_GOOD) {
			result = "良好";
		} else if (healthType == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
			result = "过热";
		} else if (healthType == BatteryManager.BATTERY_HEALTH_DEAD) {
			result = "没电";
		} else if (healthType == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
			result = "超电压";
		} else if (healthType == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
			result = "功能衰竭";
		} else if (healthType == BatteryManager.BATTERY_HEALTH_COLD) {
			result = "冷";
		} else {
			result = "未知";
		}
		return result;
	}

	/**
	 * 获取双卡手机的两个卡的IMSI 需要 READ_PHONE_STATE 权限
	 * 
	 * @param context
	 *            上下文
	 * @return 下标0为一卡的IMSI，下标1为二卡的IMSI
	 */
	@SuppressLint("MissingPermission")
	public String[] getIMSISS(Activity context) {
		// 双卡imsi的数组
		String[] imsis = new String[2];
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			// 先使用默认的获取方式获取一卡IMSI
			imsis[0] = tm.getSubscriberId();

			// 然后进行二卡IMSI的获取,默认先获取展讯的IMSI
			try {
				Method method = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", int.class);
				method.setAccessible(true);
				// 0 表示 一卡，1 表示二卡，下方获取相同
				imsis[1] = (String) method.invoke(tm, 1);
			} catch (Exception e) {
				// 异常清空数据，继续获取下一个
				imsis[1] = null;
			}
			if (imsis[1] == null || "".equals(imsis[1])) { // 如果二卡为空就获取mtk
				try {
					Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
					Method m = c.getMethod("getServiceName", String.class, int.class);
					String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
					TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
					imsis[1] = tm1.getSubscriberId();
				} catch (Exception ex) {
					imsis[1] = null;
				}
			}
			if (imsis[1] == null || "".equals(imsis[1])) { // 如果二卡为空就获取高通 IMSI获取
				try {
					Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", int.class);
					addMethod2.setAccessible(true);
					imsis[1] = (String) addMethod2.invoke(tm, 1);
				} catch (Exception ex) {
					imsis[1] = null;
				}
			}
		} catch (IllegalArgumentException e) {
		}
		return imsis;
	}

	public void getSimInfo() {

		Uri uri = Uri.parse("content://telephony/siminfo");
		Cursor cursor = null;
		ContentResolver contentResolver = SpeedTest5g.getContext().getContentResolver();
		cursor = contentResolver.query(uri, new String[] { "_id", "sim_id", "icc_id", "display_name", "number" }, "0=0", new String[] {}, null);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				String icc_id = cursor.getString(cursor.getColumnIndex("icc_id"));
				String display_name = cursor.getString(cursor.getColumnIndex("display_name"));
				int sim_id = cursor.getInt(cursor.getColumnIndex("sim_id"));
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				String number = cursor.getString(cursor.getColumnIndex("number"));

				Log.d("Q_M", "number-->" + number);
				Log.d("Q_M", "icc_id-->" + icc_id);
				Log.d("Q_M", "sim_id-->" + sim_id);
				Log.d("Q_M", "display_name-->" + display_name);
				Log.d("Q_M", "subId或者说是_id->" + _id);
				Log.d("Q_M", "---------------------------------");
			}
		}
	}

	/**
	 * 手机CPU信息
	 * 
	 * @return
	 */
	public String[] getCpuInfo() {
		BufferedReader localBufferedReader = null;
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "", "" }; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			cpuInfo[2] = str2;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeBufferedReader(localBufferedReader);
		}
		return cpuInfo;
	}

	/**
	 * 获取CPU技术型号
	 * 
	 * @return
	 */
	public String getCpuModel() {
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new FileReader("/proc/cpuinfo"));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.contains("Hardware")) {
					String[] array = readLine.split(": ");
					result = array[1].replace(",", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeBufferedReader(br);
		}
		return result;
	}

	/**
	 * 获取cpu核心数
	 * 
	 * @return
	 */
	public String getCpuCores() {
		String result = Runtime.getRuntime().availableProcessors() + "核";
		return result;
	}

	/**
	 * 获取cpu最低频率
	 * 
	 * @return
	 */
	public String getCpuMinfreq() {
		BufferedReader br = null;
		String readLine = null;
		String result = "";
		int i = Runtime.getRuntime().availableProcessors() - 1;
		try {
			br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq"));
			readLine = null;
			if ((readLine = br.readLine()) != null) {
				double minFreq = Double.valueOf(readLine) / 1000000;
				result = minFreq + "GHz";
			}

		} catch (Exception e) {

		} finally {
			if (TextUtils.isEmpty(result)) {
				try {
					br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
					readLine = null;
					if ((readLine = br.readLine()) != null) {
						double minFreq = Double.valueOf(readLine) / 1000000;
						result = minFreq + "GHz";
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			closeBufferedReader(br);
		}
		return result;

	}

	/**
	 * 获取cpu最高频率
	 *
	 * @return
	 */
	public String getCupMaxfreq() {
		BufferedReader br = null;
		String readLine = null;
		String result = "";

		int i = Runtime.getRuntime().availableProcessors() - 1;
		try {
			br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq"));
			readLine = null;
			if ((readLine = br.readLine()) != null) {
				double minFreq = Double.valueOf(readLine) / 1000000;
				result = minFreq + "GHz";
			}

		} catch (Exception e) {

		} finally {
			if (TextUtils.isEmpty(result)) {
				try {
					br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
					readLine = null;
					if ((readLine = br.readLine()) != null) {
						double minFreq = Double.valueOf(readLine) / 1000000;
						result = minFreq + "GHz";
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			closeBufferedReader(br);
		}
		return result;
	}

	/**
	 * 获取cpu启动时间
	 * 
	 * @return
	 */
	public String getCpuBootTime() {
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new FileReader("/proc/stat"));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.contains("btime")) {
					String[] array = readLine.split("\\s");
					result = TimeUtil.getInstance().getNowTimeSS(Long.valueOf(array[1]) * 1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeBufferedReader(br);
		}
		return result;
	};

	/**
	 * 获取手机cpu使用率
	 * 
	 * @return
	 */
	public String getCpuUsageRate() {
		String result = "0%";
		// 采样第一次cpu信息快照
		String[] cupStatInfos = getCupStatInfos();
		if (cupStatInfos == null) {
			return result;
		}
		// 总的CPU时间totalTime
		long totalTime = getCpuTotalTime(cupStatInfos);
		// 获取idleTime
		long idleTime = Long.parseLong(cupStatInfos[3]);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 第二次cpu统计信息采样
		String[] cupStatInfos1 = getCupStatInfos();
		// 总的CPU时间totalTime
		long totalTime1 = getCpuTotalTime(cupStatInfos1);
		// 获取idleTime
		long idleTime1 = Long.parseLong(cupStatInfos1[3]);
		float cpuRate = 0;
		try {
			cpuRate = ((totalTime1 - idleTime1) - (totalTime - idleTime)) * 100 / (totalTime1 - totalTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// float cpuRate = (totalTime - idleTime) * 100 / totalTime;
		result = cpuRate + "%";
		return result;
	}

	/**
	 * 获取某个应用cpu占用率
	 * 
	 * @return
	 */
	public String getPidCpuUsageRate(int pid) {
		String result = "0%";
		// 采样第一次cpu信息快照
		String[] cupStatInfos = getCupStatInfos();
		if (cupStatInfos == null) {
			return result;
		}
		// 总的CPU时间totalTime
		long cputotalTime = getCpuTotalTime(cupStatInfos) - Long.parseLong(cupStatInfos[3]);
		// app占用CPU时间
		long appCpuTime = getRuningAppUsedCpuTime(pid);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 第二次cpu统计信息采样
		String[] cupStatInfos1 = getCupStatInfos();
		// 总的CPU时间totalTime
		long cputotalTime1 = getCpuTotalTime(cupStatInfos1) - Long.parseLong(cupStatInfos[3]);
		// app占用CPU时间
		long appCpuTime1 = getRuningAppUsedCpuTime(pid);
		float cpuRate = 0;
		try {
			cpuRate = (appCpuTime1 - appCpuTime) * 100 / (cputotalTime1 - cputotalTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// float cpuRate = (totalTime - idleTime) * 100 / totalTime;
		result = cpuRate + "%";
		return result;
	}

	/**
	 * 获取CPU统计信息：user、nice、system、idle、iowait、irq、softirq、stealstealon、guest……
	 * 
	 * @return
	 */
	private String[] getCupStatInfos() {
		String[] cpuStatInfos = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")));
			String cupLine = "cpu  ";
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.startsWith(cupLine)) {
					cpuStatInfos = readLine.replace(cupLine, "").split(" ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeBufferedReader(br);
		}
		return cpuStatInfos;
	}

	/**
	 * 获取CPU总时间
	 * 
	 * @param cpuStatInfos
	 * @return
	 */
	private long getCpuTotalTime(String[] cpuStatInfos) {
		long totalTime = 0;
		for (int i = 0; i < cpuStatInfos.length; i++) {
			totalTime += Long.parseLong(cpuStatInfos[i]);
		}
		return totalTime;
	}

	/**
	 * 获取正在运行的App占用CPU总时间
	 * 
	 * @return
	 */
	private long getRuningAppUsedCpuTime(int pid) {
		String[] cpuInfos = null;
		BufferedReader br = null;
		long appCpuTime = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = br.readLine();
			// WybLog.i("zjh", "/proc/" + pid + "/stat == " + load);
			cpuInfos = load.split(" ");
			appCpuTime = Long.parseLong(cpuInfos[13]) +
			        Long.parseLong(cpuInfos[14]) +
			        Long.parseLong(cpuInfos[15]) +
			        Long.parseLong(cpuInfos[16]);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			closeBufferedReader(br);
		}
		return appCpuTime;
	}

	/**
	 * 获取正在运行的App信息
	 * 
	 * @param context
	 * @return
	 */
	public List<Db_PhoneAppInfo> getRunningAppInfoList(Context context) {
		// 获取正在运行的进程（Android5.0之后getRunningAppProcesses无法获取）
		List<RunningAppProcessInfo> allrunningAppProcessInfoList = null;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		allrunningAppProcessInfoList = activityManager.getRunningAppProcesses();
		// 查询所有已经安装的应用程序
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> allInstalledPackageInfoList = packageManager.getInstalledPackages(0);

		List<Db_PhoneAppInfo> appInfoList = new ArrayList<>();
		for (int i = 0; i < allrunningAppProcessInfoList.size(); i++) {
			RunningAppProcessInfo runningAppProcessInfo = allrunningAppProcessInfoList.get(i);
			String[] runPkgArray = runningAppProcessInfo.pkgList;
			for (int j = 0; j < runPkgArray.length; j++) {
				String runPkgName = runPkgArray[j];
				for (int k = 0; k < allInstalledPackageInfoList.size(); k++) {
					PackageInfo installedPackageInfo = allInstalledPackageInfoList.get(k);
					if (runPkgName.equals(installedPackageInfo.packageName)) {
						final Db_PhoneAppInfo appInfo = new Db_PhoneAppInfo();
						appInfo.name = packageManager.getApplicationLabel(installedPackageInfo.applicationInfo).toString();
						appInfo.packageName = installedPackageInfo.packageName;
						if (appInfoList.contains(appInfo)) {
							break;
						}
						appInfo.versionName = installedPackageInfo.versionName;
						appInfo.versionCode = installedPackageInfo.versionCode;
						if ((installedPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
							appInfo.isSystemApp = true;
						}
						appInfo.firstInstallTime = TimeUtil.getInstance().getNowTimeSS(installedPackageInfo.firstInstallTime);
						appInfo.lastUpdateTime = TimeUtil.getInstance().getNowTimeSS(installedPackageInfo.lastUpdateTime);
						appInfo.isRunning = true;
						appInfo.uid = runningAppProcessInfo.uid;
						appInfo.pid = runningAppProcessInfo.pid;
						appInfo.uidRxBytes = TrafficStats.getUidRxBytes(appInfo.uid);
						appInfo.uidTxBytes = TrafficStats.getUidTxBytes(appInfo.uid);
						getAppStorageSize(SpeedTest5g.getContext(), appInfo.packageName, new ListenerBack() {
							@Override
							public void onListener(int type, Object object, boolean isTrue) {
								if (object != null) {
									Db_PhoneAppInfo info = (Db_PhoneAppInfo) object;
									appInfo.usedStorage = info.usedStorage;
									appInfo.codeSize = info.codeSize;
									appInfo.dataSize = info.dataSize;
									appInfo.cacheSize = info.cacheSize;
								}
							}
						});
						appInfo.usedCPU = getPidCpuUsageRate(appInfo.pid);

						appInfoList.add(appInfo);
					}
				}
			}
		}
		return appInfoList;
	}

    /**
     * [获取应用程序packageName]
     * @return 当前应用的版本名称
     */
    public String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void closeBufferedReader(BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
