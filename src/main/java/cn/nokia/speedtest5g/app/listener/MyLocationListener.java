package cn.nokia.speedtest5g.app.listener;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.util.SharedPreHandler;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.math.BigDecimal;
import java.util.Map;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;

/**
 * 百度定位相关类
 * @author zwq
 *
 */
public class MyLocationListener extends BDAbstractLocationListener{

	private LocationClient mLocClient;
	
	private static MyLocationListener mlListener = null;
	
	public synchronized static MyLocationListener getInstances(){
		if (mlListener == null) {
			mlListener = new MyLocationListener();
		}
		return mlListener;
	}
	
	private MyLocationListener(){
		
	}
	
	/**
	 * 启动定位
	 */
	private void start(){
		if (mLocClient == null) {
			mLocClient = new LocationClient(SpeedTest5g.getContext());
			mLocClient.registerLocationListener(this);
		}
		setParameter(1*1000);
		mLocClient.start();
	}
	
	/**
	 * 重新定位
	 */
	public void requestLocation(){
		if (mLocClient == null || !mLocClient.isStarted()) {
			start();
		}else {
			mLocClient.requestLocation();
		}
	}
	
	public void onStarts(){
		if (mLocClient == null || !mLocClient.isStarted()) {
			stop();
			start();
		}
	}
	
	/**
	 * 停止定位
	 */
	public void stop(){
		if (mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
		}
		mLocClient = null;
	}
	
	protected LocationBean mLocation;
	/**
	 * 设置更新时间，默认1秒
	 * @param time
	 */
	public void setTime(int time){
		setParameter(time);
	}
	
	/***
	 * 定位返回
	 *61 ： GPS定位结果，GPS定位成功。
	 *62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
	 *63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
	 *65 ： 定位缓存的结果。
	 *66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
	 *67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
	 *68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
	 *161： 网络定位结果，网络定位定位成功。
	 *162： 请求串密文解析失败。
	 *167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
	 *502： key参数错误，请按照说明文档重新申请KEY。
	 *505： key不存在或者非法，请按照说明文档重新申请KEY。
	 *601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
	 *602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
	 *501～700：key验证失败，请按照说明文档重新申请KEY。
	 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		Map<String, Double> wgs = GPSUtil.getInstances().gcj2wgs(location.getLongitude(), location.getLatitude());
		
		double lat = wgs.get("lat");
		double lon = wgs.get("lon");

		BigDecimal blat = new BigDecimal(lat);
		lat = blat.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue();
		BigDecimal blon = new BigDecimal(lon);
		lon = blon.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue();
		
		if (mLocation == null) {
			mLocation = new LocationBean();
		}
		if(location.getLocType() == 61){
			mLocation.Provider = "gps";
			mLocation.speed = location.getSpeed();
			mLocation.Direction = location.getDirection();
			if (mLocation.speed < 0) {
				mLocation.speed = 0;
			}
		}else if(location.getLocType() == 161){
			mLocation.Provider = "network";
		}else{
			mLocation.Provider = "null";
		}
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_LON(), String.valueOf(lon));
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_LAT(), String.valueOf(lat));
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_TYPE(), mLocation.Provider);
//		最小经度:115.847634167583  最大经度:120.721499279968  最小纬度:23.5234791896131  最大纬度:28.3162026601248
//		if ((lon >= 115.847634167583 && lon <= 120.721499279968 &&
//				lat >= 23.5234791896131 && lat <= 28.3162026601248) ||
//				(lon == 0 || lat == 0)) {
//			SharedPreHandler.getShared(BDGPSService.this).setStrShared(TypeKey.getInstance().GPS_LON(), String.valueOf(lon));
//			SharedPreHandler.getShared(BDGPSService.this).setStrShared(TypeKey.getInstance().GPS_LAT(), String.valueOf(lat));
//			SharedPreHandler.getShared(BDGPSService.this).setStrShared(TypeKey.getInstance().GPS_TYPE(), mLocation.Provider);
//		}else {
//			SharedPreHandler.getShared(BDGPSService.this).setStrShared(TypeKey.getInstance().GPS_TYPE(), "other");
//		}
		
		mLocation.Latitude = lat;
		mLocation.Longitude = lon;
		mLocation.Accuracy = location.getRadius();
		mLocation.Altitude = location.getAltitude();
		
		Intent intent = new Intent(TypeKey.getInstance().PACKAGE_GPS);
		Bundle bundle = new Bundle();
		bundle.putSerializable("location", mLocation);
		intent.putExtras(bundle);
		SpeedTest5g.getContext().sendBroadcast(intent);
	}
	
	private LocationClientOption option;
	private void setParameter(int time){
		if (option == null) {
			option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
//			返回国测局经纬度坐标系 coor=gcj02
//			返回百度墨卡托坐标系 coor=bd09
//			返回百度经纬度坐标系 coor=bd09ll
			option.setCoorType("gcj02");// 设置坐标类型
			option.disableCache(true);//true表示禁用缓存定位，false表示启用缓存定位。
			option.setIsNeedAddress(false);//返回的定位结果包含地址信息
			option.setNeedDeviceDirect(false);//返回的地址描述定位结果包含手机机头的方向
			option.setLocationMode(LocationMode.Hight_Accuracy);//定位模式
			option.setLocationNotify(true);//设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
			option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
			option.setEnableSimulateGps(false);//设置是否允许模拟GPS true:允许； false:不允许，默认为false
//			option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
			option.setIgnoreKillProcess(false);//可选，默认true，设置在stop的时候是否不杀死这个进程，默认true-不杀死
		}
		option.setScanSpan(time);// 设置定时定位的时间间隔。
		if (mLocClient == null) {
			mLocClient = new LocationClient(SpeedTest5g.getContext());
			mLocClient.registerLocationListener(this);
		}
		if (mLocClient != null) {
			mLocClient.setLocOption(option);
		}
	}
}
