package cn.nokia.speedtest5g.app.activity2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.utils.DistanceUtil;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.adapter.SystemGpsSatelliteAdapter;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.listener.MyLocationListener;
import cn.nokia.speedtest5g.app.uitl.PoiSearchGetUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.view.MyScrollyListView;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * GPS位置 卫星信息页
 * @author JQJ
 *
 */
@SuppressLint("MissingPermission")
public class SystemGpsActivity extends BaseActionBarActivity implements ListenerBack {

	private TextView tvGpsLng = null;
	private TextView tvGpsLat = null;
	private TextView tvGpsLocationMode = null;
	private TextView tvGpsAddress = null;
	private TextView tvGpsUseSatelliteCount = null;
	private MyScrollyListView listView = null;
	private SystemGpsSatelliteAdapter mAdapter = null;

	private LatLng bdLatLng,lastLatlng, wgsLatLng;
	// 位置管理器
	private LocationManager manager = null;
	//是否打开Gps设置页面
	private boolean isOpenGps = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_system_gps_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("位置信息", true);

		initView();
		// 启动GPS定位服务
		MyLocationListener.getInstances().setTime(1000);
		// 注册定位返回经纬度广播
		registerReceiver(_gpsReceiver, new IntentFilter(TypeKey.getInstance().PACKAGE_GPS));
		initLocation();
	}

	private void initView() {
		tvGpsLng = (TextView) findViewById(R.id.tv_system_device_gps_lng);
		tvGpsLat =(TextView) findViewById(R.id.tv_system_device_gps_lat);
		tvGpsLocationMode =(TextView) findViewById(R.id.tv_system_device_gps_location_mode);
		tvGpsAddress = (TextView) findViewById(R.id.tv_system_device_gps_address);
		tvGpsUseSatelliteCount = (TextView) findViewById(R.id.tv_system_gps_use_count);

		listView = (MyScrollyListView) findViewById(R.id.lv_satellite);
		mAdapter = new SystemGpsSatelliteAdapter(mActivity);
		listView.setAdapter(mAdapter);
	}

	/**
	 * 初始化定位管理
	 */
	private void initLocation() {
		// 判断GPS是否正常启动
		if(!GPSUtil.getInstances().isOpen(mActivity)){
			new CommonDialog(mActivity)
			.setListener(SystemGpsActivity.this)
			.setButtonText("立即开启", "稍后再说")
			.show("GPS定位未开启，请点击打开", EnumRequest.OTHER_OPEN_GPS.toInt());
			return;
		}
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// 添加卫星状态改变监听
		manager.addGpsStatusListener(gpsStatusListener);
		// 1000位最小的时间间隔，1为最小位移变化；也就是说每隔1000ms会回调一次位置信息
	}

	private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			try{
				switch (event) {
				// 卫星状态改变
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					// 获取当前状态
					GpsStatus gpsStatus = manager.getGpsStatus(null);
					// 获取卫星颗数的默认最大值
					int maxSatellites = gpsStatus.getMaxSatellites();
					// 获取所有的卫星
					Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
					// 卫星颗数统计
					int count = 0;
					int useCount=0;
					List<GpsSatellite> list = new ArrayList<>();
					while (iters.hasNext() && count <= maxSatellites) {
						count++;
						GpsSatellite s = iters.next();
						if(s.getSnr()>0){
							list.add(s);
							//						WybLog.syso("---卫星:"+s.getSnr());
							//						WybLog.syso("---卫星 "+count+"颗"+s.hasEphemeris()+","+s.hasEphemeris()+","+s.usedInFix()+",");
							if(s.usedInFix()){
								useCount++;
							}
						}

						//s.getSnr();卫星的信噪比
						// s.getAzimuth();方位角
						// s.getElevation();仰角
						// s.getPrn();
					}
					tvGpsUseSatelliteCount.setText("可用卫星"+useCount+"个,共识别卫星"+list.size()+"个");
					mAdapter.setData(list);
					break;
				default:
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	/*------------------------------gps定位返回的广播---------------start----------------------*/
	private BroadcastReceiver _gpsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent data) {
			try{
				Bundle bundle = data.getExtras();
				LocationBean mLocation = (LocationBean) bundle.getSerializable("location");

				wgsLatLng = new LatLng(mLocation.Latitude, mLocation.Longitude);
				tvGpsLat.setText(wgsLatLng.latitude+"");
				tvGpsLng.setText(wgsLatLng.longitude+"");
				if(mLocation.Provider.equals("gps")){
					tvGpsLocationMode.setText("GPS");
				}else if(mLocation.Provider.equals("network")){
					tvGpsLocationMode.setText("Network");
				}else{
					tvGpsLocationMode.setText(mLocation.Provider);
				}

				if (bdLatLng == null) {
					bdLatLng = GPSUtil.getInstances().toBdLatlng(wgsLatLng);
					lastLatlng = bdLatLng;
					PoiSearchGetUtil.getInstances().toGeoCoder(bdLatLng, SystemGpsActivity.this);
				} else {
					bdLatLng = GPSUtil.getInstances().toBdLatlng(wgsLatLng);
					if (DistanceUtil.getDistance(lastLatlng, bdLatLng) > 100) {
						// 获取当前地图的中心点，然后去解析地理位置MapStatus
						lastLatlng = bdLatLng;
						PoiSearchGetUtil.getInstances().toGeoCoder(bdLatLng, SystemGpsActivity.this);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (type == EnumRequest.OTHER_GEO_SEARCH.toInt()) {
			if (isTrue) {
				AddressComponent address = ((ReverseGeoCodeResult) object).getAddressDetail();
				if(address != null){
					StringBuffer buffer = new StringBuffer();
					buffer.append(address.province).append(address.city).append(address.district).append(address.city)
					.append(address.street).append(address.streetNumber);
					tvGpsAddress.setText(buffer.toString());
				}
			}
		}else if(type == EnumRequest.OTHER_OPEN_GPS.toInt()){
			if (isTrue) {
				isOpenGps = true;
				GPSUtil.getInstances().open(mActivity);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isOpenGps){
			isOpenGps = false;
			initLocation();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(_gpsReceiver != null){
			unregisterReceiver(_gpsReceiver);
		}
	}
}
