package cn.nokia.speedtest5g.speedtest.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.TimeZone;

import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * SpeedTestUtil
 * @author JQJ
 *
 */
public class SpeedTestUtil {

    public static final String NETWORK_CHANGE_ACTION = "cn.nokia.speedtest5g.network.change";

    private static SpeedTestUtil instance;

    private SpeedTestUtil(){}

    public static SpeedTestUtil getInstance(){
        if(instance == null){
            instance = new SpeedTestUtil();
        }

        return instance;
    }

    /**
     * 判断是否同一天
     * @param millis1
     * @param millis2
     * @return
     */
    public boolean isSameDay(long millis1, long millis2) {
        long interval = millis1 - millis2;
        return interval < 86400000 && interval > -86400000 &&
                millis2Days(millis1, TimeZone.getDefault()) == millis2Days(millis2, TimeZone.getDefault());
    }

    private long millis2Days(long millis, TimeZone timeZone) {
        return (((long) timeZone.getOffset(millis)) + millis) / 86400000;
    }

    public boolean isNetwork(final Activity mActivity){
        if(!NetInfoUtil.isNetworkConnected(mActivity)){ //无网络提示
            new CommonDialog(mActivity).setListener(new ListenerBack() {

                @Override
                public void onListener(int type, Object object, boolean isTrue) {
                    if(isTrue){
                        mActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }
            })
                    .setButtonText("去设置", "取消").setTitle("当前网络已断开！").show( "请检查网络连接，然后重试");
            return false;
        }
        return true;
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 根据服务器性能获取描述
     * @param performance
     * @return
     */
    public String getLevelByPerformance(int performance){
        if(performance <= 30){
            return "很差";
        }else if(performance > 30 && performance <= 60){
            return "一般";
        }else if(performance > 60 && performance <= 80){
            return "良好";
        }else if(performance > 80){
            return "流畅";
        }
        return "很差";
    }

    public double to2Double(double f){ //保留两位小数点
        try{
            BigDecimal bd = new BigDecimal(f);
            BigDecimal setScale = bd.setScale(2, BigDecimal.ROUND_DOWN);
            return setScale.doubleValue();
        }catch(Exception e){
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 调试日志
     * @param result1
     * @param result2
     */
    @SuppressLint("SdCardPath")
    public void writeToText(String result1 ,String result2){
        try{
            String path = "/mnt/sdcard/speedtestLog.text";
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            PrintStream ps = new PrintStream(new FileOutputStream(file, true));
            StringBuffer buffer = new StringBuffer();
            buffer.append(TimeUtil.getInstance().getNowTimeSS());
            buffer.append("----------------" + result1);
            buffer.append("\r\n");
            buffer.append(result2);
            buffer.append("\r\n");
            ps.println(buffer.toString());// 往文件里写入字符串
            ps.flush();
            ps.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
