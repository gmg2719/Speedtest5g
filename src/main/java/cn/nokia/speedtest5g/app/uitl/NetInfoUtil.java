package cn.nokia.speedtest5g.app.uitl;

import java.util.List;
import java.util.Locale;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FtpAddressGroupInfo;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.thread.PingThread;

import com.android.volley.util.SharedPreHandler;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 获取网络基本信息
 * 
 * @author xujianjun
 *
 */
public class NetInfoUtil {

	private static final int NETWORK_TYPE_UNAVAILABLE = -1;
	// private static final int NETWORK_TYPE_MOBILE = -100;
	private static final int NETWORK_TYPE_WIFI = -101;

	private static final int NETWORK_CLASS_WIFI = -101;
	private static final int NETWORK_CLASS_UNAVAILABLE = -1;

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断网络类型
	 * 
	 * @param context
	 * @return 返回值 -1：没有网络 1：WIFI网络2：wap网络3：net网络
	 */
	@SuppressLint("DefaultLocale")
	public static int GetNetype(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
		}
		return netType;
	}

	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public static String getCurrentNetworkType(Context myCxt) {
		int networkClass = getNetworkClass(myCxt);
		String type = "未知";
		switch (networkClass) {
		case NETWORK_CLASS_UNAVAILABLE:
			type = "无";
			break;
		case NETWORK_CLASS_WIFI:
			type = "WiFi";
			break;
		case CellUtil.NETWORK_GSM:
			type = "2G";
			break;
		case CellUtil.NETWORK_TD:
			type = "3G";
			break;
		case CellUtil.NETWORK_LTE:
			type = "4G";
			break;
		case CellUtil.NETWORK_NR:
			type = "5G";
			break;
		case CellUtil.NETWORK_UNKNOW:
			type = "未知";
			break;
		}
		return type;
	}

	/**
	 * 获取运营商+网络类型
	 * 
	 * @return
	 */
	public static String getOperatorNetworkType(Context myCxt) {
		String type = getCurrentNetworkType(myCxt);
		if ("未知".equals(type)) {
			type = "未知网络";
		} else if ("无".equals(type)) {
			type = "无网络";
		} else {
			if (!"WiFi".equals(type)) {
				type = SystemUtil.getInstance().getISPNew(myCxt) + type;
			}
		}
		return type;
	}

	/**
	 * 获取运营商+网络类型
	 * 
	 * @return
	 */
	public static String[] getOperatorNetworkType() {
		String type = getCurrentNetworkType(SpeedTest5g.getContext());
		if ("未知".equals(type)) {
			return new String[] { "", "" };
		} else if ("无".equals(type)) {
			return new String[] { "", "" };
		} else {
			if (!"WiFi".equals(type)) {
				return new String[] { SystemUtil.getInstance().getISPNew(SpeedTest5g.getContext()), type };
			}else{ //获取连接wifi名称
				@SuppressLint("WifiManagerLeak")
				WifiManager wifiMgr = (WifiManager) SpeedTest5g.getContext().getSystemService(Context.WIFI_SERVICE);
				String wifiId = null;
				if(wifiMgr != null){
					WifiInfo info = wifiMgr.getConnectionInfo();
					wifiId = info != null ? info.getSSID() : null;
					if(!TextUtils.isEmpty(wifiId)){
						wifiId = wifiId.substring(1, wifiId.length()-1);
					}
					return new String[] { wifiId, type };
				}else{
					return new String[] { "", type };
				}
			}
		}
	}

	private static int getNetworkClass(Context myCxt) {
		int networkType = CellUtil.NETWORK_UNKNOW;
		try {
			final NetworkInfo network = ((ConnectivityManager) myCxt.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if (network != null && network.isAvailable() && network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					networkType = NetWorkStateUtil.getNetType((TelephonyManager) myCxt.getSystemService(Context.TELEPHONY_SERVICE));
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return NetWorkStateUtil.getNetworkClassByType(networkType);
	}

	/**
	 * 宏站单验-APN判断
	 * 
	 * @return
	 */
	public static int isAPN(Context mContext, Handler handler, int what, boolean isDown) {
		boolean isDownProperty = true;
		boolean isUploadProperty = true;
		if (isDown) {
			isDownProperty = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().FTP_DOWN_PROPERTYS_HZ, 0) == 0;
		} else {
			isUploadProperty = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().FTP_UPLOAD_PROPERTYS_HZ, 0) == 0;
		}

		if (!isDownProperty || !isUploadProperty) {
			String netType = getCurrentNetworkType(mContext);
			WybLog.syso("---netType:" + netType);
			if (!netType.trim().equals("WiFi") && !netType.trim().equals("无")) {
				PingInfo info = new PingInfo();
				info.setIp("111.13.100.91");
				PingThread pingThread = new PingThread(info, what, handler, 1, 1);
				pingThread.start();
				return 2;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 当前是否为APN type 0为速率测试，1为道路测试内的速率测试
	 * 
	 * @return 0 为不是apn测试。1为是apn测试，提示设置对话框，2为等待ping返回是否提示对话框（当ping不通时，认为是apn网络）
	 */
	public static int isAPNNew(Context mContext, Handler handler, int what, List<Db_JJ_FtpAddressGroupInfo> list) {
		boolean isDownProperty = isApnGroup(true, list);
		boolean isUploadProperty = isApnGroup(false, list);

		if (isDownProperty || isUploadProperty) {
			String netType = getCurrentNetworkType(mContext);
			WybLog.syso("---netType:" + netType);
			if (!netType.trim().equals("WiFi") && !netType.trim().equals("无")) {
				PingInfo info = new PingInfo();
				info.setIp("111.13.100.91");
				PingThread pingThread = new PingThread(info, what, handler, 1, 1);
				pingThread.start();
				return 2;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 当前是否为APN(速率测试模块使用)
	 * 
	 * @param mContext
	 * @param list
	 * @return false 为不是apn测试。true为是apn测试
	 */
	public static boolean isAPN(Context mContext, List<Db_JJ_FtpAddressGroupInfo> list) {
		boolean isDownProperty = isApnGroup(true, list);
		boolean isUploadProperty = isApnGroup(false, list);
		if (isDownProperty || isUploadProperty) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isApnGroup(Boolean isDown, List<Db_JJ_FtpAddressGroupInfo> list) {
		boolean isApn = false;
		if (list == null) {
			return isApn;
		}
		if (isDown) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isDownSelect()) {
					if (list.get(i).isCustom() || list.get(i).getHostType().equals("4") || list.get(i).getHostType().equals("5") || list.get(i).getHostType().equals("6")) {
						isApn = true;
					}
					break;
				}
			}
			return isApn;
		} else {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isUpSelect()) {
					if (list.get(i).isCustom() || list.get(i).getHostType().equals("4") || list.get(i).getHostType().equals("5") || list.get(i).getHostType().equals("6")) {
						isApn = true;
					}
					break;
				}
			}
			return isApn;
		}
	}

	/**
	 * 网速单位转换
	 * 
	 * @param bytesPerSecond
	 *            每秒字节数
	 * @return
	 */
	public static String converNetSpeed(double bytesPerSecond) {
		String result = "";
		if (bytesPerSecond < (1024 * 1024)) {
			result = String.format(Locale.CHINESE, "%.2f", bytesPerSecond / 1024) + "K/s";
		} else {
			result = String.format(Locale.CHINESE, "%.2f", bytesPerSecond / 1024 / 1024) + "M/s";
		}
		return result;
	}
}
