package cn.nokia.speedtest5g.wifi.other;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import jcifs.netbios.NbtAddress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.wifi.bean.WifiArpBean;
import cn.nokia.speedtest5g.wifi.bean.WifiMacManu;

public class WifiUtil {

	private static final int MIN_RSSI = -110;
	private static final int MAX_RSSI = -40;

	private static final int SECURITY_NONE = 0;
	private static final int SECURITY_WEP = 1;
	private static final int SECURITY_PSK = 2;
	private static final int SECURITY_EAP = 3;

	private static WifiUtil wUtil = null;

	public static synchronized WifiUtil getInstance(){
		if (wUtil == null) {
			wUtil = new WifiUtil();
		}
		return wUtil;
	}

	//过滤WIFI名称
	public String getName(String ssid){
		if (TextUtils.isEmpty(ssid)){
			return "";
		}else if (ssid.length() > 1 && ssid.startsWith("\"") && ssid.endsWith("\"")){
			return ssid.substring(1,ssid.length() - 1);
		}
		return ssid;
	}

	//获取IP地址
	public String intToIpAddress(long ipInt) {
		StringBuffer sb = new StringBuffer();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	public Integer getMethod(Object object,String key,int v){
		try {
			Method method = object.getClass().getMethod(key,new Class[0]);
			Object invoke = method.invoke(object, new Object[0]);
			return (Integer)invoke;
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
			return v;
		}
	}

	/**
	 * 根据频率获得信道
	 *
	 * @param frequency
	 * @return [0]信道 [1]2.4G或5G
	 */
	public Object[] getChannelByFrequency(int frequency) {
		int channel = -1;
		String type = "";
		switch (frequency) {
		case 2412:
			channel = 1;
			type = "2.4G";
			break;
		case 2417:
			channel = 2;
			type = "2.4G";
			break;
		case 2422:
			channel = 3;
			type = "2.4G";
			break;
		case 2427:
			channel = 4;
			type = "2.4G";
			break;
		case 2432:
			channel = 5;
			type = "2.4G";
			break;
		case 2437:
			channel = 6;
			type = "2.4G";
			break;
		case 2442:
			channel = 7;
			type = "2.4G";
			break;
		case 5035://5G
			type = "5G";
			channel = 7;
			break;
		case 2447:
			channel = 8;
			type = "2.4G";
			break;
		case 5040://5G
			type = "5G";
			channel = 8;
			break;
		case 2452:
			channel = 9;
			type = "2.4G";
			break;
		case 5045://5G
			type = "5G";
			channel = 9;
			break;
		case 2457:
			type = "2.4G";
			channel = 10;
			break;
		case 2462:
			type = "2.4G";
			channel = 11;
			break;
		case 5055://5G
			type = "5G";
			channel = 11;
			break;
		case 2467:
			channel = 12;
			type = "2.4G";
			break;
		case 5060://5G
			type = "5G";
			channel = 12;
			break;
		case 2472:
			type = "2.4G";
			channel = 13;
			break;
		case 2484:
			type = "2.4G";
			channel = 14;
			break;
		case 5080://5G
			type = "5G";
			channel = 16;
			break;
		case 5170://5G
			type = "5G";
			channel = 34;
			break;
		case 5180://5G
			type = "5G";
			channel = 36;
			break;
		case 5190://5G
			type = "5G";
			channel = 38;
			break;
		case 5200://5G
			type = "5G";
			channel = 40;
			break;
		case 5210://5G
			type = "5G";
			channel = 42;
			break;
		case 5220://5G
			type = "5G";
			channel = 44;
			break;
		case 5230://5G
			type = "5G";
			channel = 46;
			break;
		case 5240://5G
			type = "5G";
			channel = 48;
			break;
		case 5250://5G
			type = "5G";
			channel = 50;
			break;
		case 5260://5G
			type = "5G";
			channel = 52;
			break;
		case 5270://5G
			type = "5G";
			channel = 54;
			break;
		case 5280://5G
			type = "5G";
			channel = 56;
			break;
		case 5290://5G
			type = "5G";
			channel = 58;
			break;
		case 5300://5G
			type = "5G";
			channel = 60;
			break;
		case 5310://5G
			type = "5G";
			channel = 62;
			break;
		case 5320://5G
			type = "5G";
			channel = 64;
			break;
		case 5500://5G
			type = "5G";
			channel = 100;
			break;
		case 5510://5G
			type = "5G";
			channel = 102;
			break;
		case 5520://5G
			type = "5G";
			channel = 104;
			break;
		case 5530://5G
			type = "5G";
			channel = 106;
			break;
		case 5540://5G
			type = "5G";
			channel = 108;
			break;
		case 5550://5G
			type = "5G";
			channel = 110;
			break;
		case 5560://5G
			type = "5G";
			channel = 112;
			break;
		case 5570://5G
			type = "5G";
			channel = 114;
			break;
		case 5580://5G
			type = "5G";
			channel = 116;
			break;
		case 5590://5G
			type = "5G";
			channel = 118;
			break;
		case 5600://5G
			type = "5G";
			channel = 120;
			break;
		case 5610://5G
			type = "5G";
			channel = 122;
			break;
		case 5620://5G
			type = "5G";
			channel = 124;
			break;
		case 5630://5G
			type = "5G";
			channel = 126;
			break;
		case 5640://5G
			type = "5G";
			channel = 128;
			break;
		case 5660://5G
			type = "5G";
			channel = 132;
			break;
		case 5670://5G
			type = "5G";
			channel = 134;
			break;
		case 5680://5G
			type = "5G";
			channel = 136;
			break;
		case 5690://5G
			type = "5G";
			channel = 138;
			break;
		case 5700://5G
			type = "5G";
			channel = 140;
			break;
		case 5710://5G
			type = "5G";
			channel = 142;
			break;
		case 5720://5G
			type = "5G";
			channel = 144;
			break;
		case 5745://5G
			type = "5G";
			channel = 149;
			break;
		case 5755://5G
			type = "5G";
			channel = 151;
			break;
		case 5765://5G
			type = "5G";
			channel = 153;
			break;
		case 5775://5G
			type = "5G";
			channel = 155;
			break;
		case 5785://5G
			type = "5G";
			channel = 157;
			break;
		case 5795://5G
			type = "5G";
			channel = 159;
			break;
		case 5805://5G
			type = "5G";
			channel = 161;
			break;
		case 5825://5G
			type = "5G";
			channel = 165;
			break;
		case 4915://5G
			type = "5G";
			channel = 183;
			break;
		case 4920://5G
			type = "5G";
			channel = 184;
			break;
		case 4925://5G
			type = "5G";
			channel = 185;
			break;
		case 4935://5G
			type = "5G";
			channel = 187;
			break;
		case 4940://5G
			type = "5G";
			channel = 188;
			break;
		case 4945://5G
			type = "5G";
			channel = 189;
			break;
		case 4960://5G
			type = "5G";
			channel = 192;
			break;
		case 4980://5G
			type = "5G";
			channel = 196;
			break;
		}
		return new Object[]{channel,type};
	}

	private int[] mArrColor = null;
	public int[] getArrColor(){
		if (mArrColor == null) {
			mArrColor = new int[]{
					Color.parseColor("#282CFF"),
					Color.parseColor("#40FF1E"),
					Color.parseColor("#9D1EFF"),
					Color.parseColor("#CC9909"),
					Color.parseColor("#FFCC00"),
					Color.parseColor("#28D0FF"),
					Color.parseColor("#663333"),
					Color.parseColor("#98CCA5"),
					Color.parseColor("#FF6666"),
					Color.parseColor("#CC3300"),
					Color.parseColor("#660000"),
					Color.parseColor("#FF0000"),
					Color.parseColor("#33CCCC"),
					Color.parseColor("#9933CC"),
					Color.parseColor("#3333FF"),
					Color.parseColor("#999966"),
					Color.parseColor("#333300"),
					Color.parseColor("#666666"),
					Color.parseColor("#336633"),
					Color.parseColor("#000000")
			};
		}
		return mArrColor;
	}

	private int[] mArrColorApply = null;
	public int[] getArrColorApply(){
		if (mArrColorApply == null) {
			mArrColorApply = new int[]{
					Color.parseColor("#26282CFF"),
					Color.parseColor("#2640FF1E"),
					Color.parseColor("#269D1EFF"),
					Color.parseColor("#26CC9909"),
					Color.parseColor("#26FFCC00"),
					Color.parseColor("#2628D0FF"),
					Color.parseColor("#26663333"),
					Color.parseColor("#2698CCA5"),
					Color.parseColor("#26FF6666"),
					Color.parseColor("#26CC3300"),
					Color.parseColor("#26660000"),
					Color.parseColor("#26FF0000"),
					Color.parseColor("#2633CCCC"),
					Color.parseColor("#269933CC"),
					Color.parseColor("#263333FF"),
					Color.parseColor("#26999966"),
					Color.parseColor("#26333300"),
					Color.parseColor("#26666666"),
					Color.parseColor("#26336633"),
					Color.parseColor("#26000000")
			};
		}
		return mArrColorApply;
	}

	//获取wifi列表
	public List<ScanResult> getWifiList(WifiManager wifiManager) {
		List<ScanResult> scanWifiList = wifiManager.getScanResults();
		//		List<ScanResult> wifiList = new ArrayList<ScanResult>();
		//		if (scanWifiList != null && scanWifiList.size() > 0) {
		//			HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
		//			for (int i = 0; i < scanWifiList.size(); i++) {
		//				ScanResult scanResult = scanWifiList.get(i);
		//				if (!scanResult.SSID.isEmpty()) {
		//					String key = scanResult.SSID + " " + scanResult.capabilities;
		//					if (!signalStrength.containsKey(key)) {
		//						signalStrength.put(key, i);
		//						wifiList.add(scanResult);
		//					}
		//				}
		//			}
		//		}
		return scanWifiList;
	}

	/**
	 * mac获取厂家
	 * @param mac
	 * @return
	 */
	@SuppressLint("DefaultLocale") 
	public String getManufactor(String mac){
		if(TextUtils.isEmpty(mac)){
			return null;
		}

		if(SpeedTestDataSet.mWifiMacManuList.size() > 0){
			String[] macArr = mac.toUpperCase().split("\\:");
			String manuPart = macArr[0] + macArr[1] + macArr[2];
			for(WifiMacManu wifiMacManu : SpeedTestDataSet.mWifiMacManuList){
				if(wifiMacManu.assignment.equals(manuPart)){
					return wifiMacManu.organizationName;
				}
			}
		}
		return null;
	}

	/**
	 * ip获取主机名  需在进程中执行
	 * @param mac
	 * @return
	 */
	public void getHostName(WifiArpBean wifiArpBean){
		if(TextUtils.isEmpty(wifiArpBean.ip)){
			return;
		}

		try{
			NbtAddress nbtAddress = NbtAddress.getByName(wifiArpBean.ip);
			System.out.println("nbtAddress.firstCalledName():" + nbtAddress.firstCalledName());
			String hostName = nbtAddress.nextCalledName();
			System.out.println("nbtAddress.nextCalledName():" + hostName);
			wifiArpBean.hostName = hostName;
		}catch(Exception e){
			e.printStackTrace();
		}
		return;
	}

	/**
	 * channelWidth 获取信道宽度
	 * @param channelWidth
	 * @return
	 */
	public String getChannelWidth(int channelWidth){
		if(channelWidth == 0){
			return "20MHZ";
		}else if(channelWidth == 1){
			return "40MHZ";
		}else if(channelWidth == 2){
			return "80MHZ";
		}else if(channelWidth == 3){
			return "160MHZ";
		}else if(channelWidth == 4){
			return "80PlusMHZ";
		}

		return "";
	}

	public int getProgressValue(int rssi){
		if(rssi > MAX_RSSI){
			return 100;
		}

		if(rssi < MIN_RSSI){
			return 0;
		}

		int all = Math.abs(MAX_RSSI - MIN_RSSI);
		int value = Math.abs(rssi - MIN_RSSI);

		return (int)(value * 100 / all);
	}

	/**
	 * 获取本机 ip地址
	 *
	 * @return
	 */
	public String getHostIP() {
		String hostIp = null;
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return hostIp;
	}

	public int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	/**
	 * wifi获取 路由ip地址
	 *
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getWifiRouteIPAddress(Context context) {
		WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
		//        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
		//        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
		//        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
		//        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
		//DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
		String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
		return routeIp;
	}

	/**
	 * 移动网络ip
	 * @return
	 */
	public String getLocalIpAddress() {  
		try {  
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {  
				NetworkInterface intf = en.nextElement();  
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {  
					InetAddress inetAddress = enumIpAddr.nextElement();  
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  
						return inetAddress.getHostAddress().toString();  
					}  
				}  
			}  
		} catch (SocketException ex) {  
			return "";
		}  
		return "";  
	} 
}
