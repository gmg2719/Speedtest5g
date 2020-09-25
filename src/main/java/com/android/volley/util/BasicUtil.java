package com.android.volley.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 一些手机本身基本的数据获取
 * @author Administrator
 *
 */
public class BasicUtil {
	
	private static BasicUtil util = null;
	
	public synchronized static BasicUtil getUtil(){
		synchronized (BasicUtil.class) {
			if (util == null) {
				util = new BasicUtil();
			}
			return util;
		}
	}

	/**
	 * 获取SD卡目录若无SD卡则获取本地
	 * @param context
	 * @return
	 */
	public String getPath(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getPath() + "/";
		}else {
			return context.getFilesDir().getPath() + "/";
		}
	}
	
	/**
	 * 函数名称 : getNetworkState
	 * 功能描述 :  获取当前网络状态
	 * 	需要添加权限： <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * @param context
	 * @return null当前网络未连接
	 */
	public String getNetworkState(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conn != null) {
			NetworkInfo activeNetworkInfo = conn.getActiveNetworkInfo();
			if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
				if (activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
					if (activeNetworkInfo.isAvailable()) {
						//判断当前连接方式
						if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return "WIFI";
						}
						return "GPRS";
					}
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 *  参数说明：
	 *  	@param isName true名称(String型)   false版本号(int型)
	 *  	@return
	 *  描述	：获取版本名称或版本号
	 */
	public Object getVersion(Context context,boolean isName) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			if (isName) {
				return packageInfo.versionName;
			}
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			return null;
		}
	}
}
