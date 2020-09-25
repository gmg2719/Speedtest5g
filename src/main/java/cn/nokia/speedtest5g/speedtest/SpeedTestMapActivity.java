package cn.nokia.speedtest5g.speedtest;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.model.LatLng;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.gis.model.JJ_LatLng;
import cn.nokia.speedtest5g.util.gps.GPSUtil;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 速测 地图 用于打点
 * @author JQJ
 *
 */
public class SpeedTestMapActivity extends BaseActionBarActivity{

	private MapView _mapView = null;
	private BaiduMap mBaiduMap = null;
	private LatLng bdLatLng = null;
	// 是否第一次加载map地图
	private boolean isLoadMapSucceed = false;

	// 打点经纬度
	private List<LatLng> dottingLatlngList = null;
	// 经纬度打点
	private BitmapDescriptor dottingBitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_map_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("GIS呈现", true);

		initData();

		initMap();
	}

	private void initData(){
		if (getIntent().getExtras() != null) {
			Object data = getIntent().getExtras().getSerializable("data");
			if (data instanceof JJ_LatLng) {
				boolean isDot = getIntent().getExtras().getBoolean("isDot", true);
				JJ_LatLng jjLatlng = (JJ_LatLng) data;
				LatLng localLatLng = new LatLng(jjLatlng.getLat(), jjLatlng.getLng());
				bdLatLng = GPSUtil.getInstances().toBdLatlng(localLatLng);
				if (isDot) {
					if (dottingLatlngList == null) {
						dottingLatlngList = new ArrayList<LatLng>();
					}
					dottingLatlngList.add(localLatLng);
				}
			}
		}
	}

	private void initMap(){
		_mapView = (MapView) findViewById(R.id.speed_test_map_mapview);
		// 获取上一次定位的位置
		String strLL = SharedPreHandler.getShared(mActivity).getStringShared(TypeKey.getInstance().GPS_LAT(), "");
		double lat = Double.parseDouble(strLL.isEmpty() ? "119.3" : strLL);
		strLL = SharedPreHandler.getShared(mActivity).getStringShared(TypeKey.getInstance().GPS_LON(), "");
		double lng = Double.parseDouble(strLL.isEmpty() ? "26.08" : strLL);
		// GPS转百度经纬度方法
		LatLng oldLastLatlng = GPSUtil.getInstances().toBdLatlng(new LatLng(lat, lng));
		// 不显示缩放按钮
		_mapView.showZoomControls(false);

		// 隐藏百度地图logo
		for (int i = 0; i < _mapView.getChildCount(); i++) {
			View baiDuMapChildView = _mapView.getChildAt(i);
			if (baiDuMapChildView != null) {
				if (baiDuMapChildView instanceof ImageView) { // 百度地图logo
					_mapView.removeViewAt(i);
				}
			}
		}

		mBaiduMap = _mapView.getMap();
		// 初始化地图缩放级别,中心点位置
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(oldLastLatlng, 18.0f));
		// 设置最大缩放级别
		mBaiduMap.setMaxAndMinZoomLevel(19, 3);
		// 显示指南针
		UiSettings mapUiSettings = mBaiduMap.getUiSettings();
		mapUiSettings.setCompassEnabled(true);

		mBaiduMap.setMyLocationEnabled(true);// 允许定位图层
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				// 设置比例尺位置
				Point scaleControlPoint = new Point(UtilHandler.getInstance().dpTopx(10), _mapView.getHeight() - UtilHandler.getInstance().dpTopx(20));
				_mapView.setScaleControlPosition(scaleControlPoint);

				if (bdLatLng != null) {
					toMoveLatLng(bdLatLng);
				}

				if (!isLoadMapSucceed) {
					isLoadMapSucceed = true;
					mHanlder.sendEmptyMessageDelayed(1000, 500);
				}
			}
		});
	}

	@SuppressLint("HandlerLeak") 
	private Handler mHanlder = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1000){
				if (dottingLatlngList != null && dottingLatlngList.size() > 0) {
					mapDotting(dottingLatlngList);
				}
			}
		};
	};

	// 经纬度打点
	private void mapDotting(List<LatLng> latlngList) {
		if (dottingBitmap == null) {
			dottingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gis_tool_dotting);
		}

		if (latlngList != null && latlngList.size() > 0) {
			for (int i = 0; i < latlngList.size(); i++) {
				Point point = GPSUtil.getInstances().toScreenLocation(mBaiduMap, GPSUtil.getInstances().toBdLatlng(latlngList.get(i)));
				if (point != null) {
					LatLng latlngDotOpiton = GPSUtil.getInstances().proToLatLng(mBaiduMap, point.x, point.y + (dottingBitmap.getBitmap().getHeight() / 2));
					OverlayOptions option = new MarkerOptions().position(latlngDotOpiton).icon(dottingBitmap);
					mBaiduMap.addOverlay(option);
				}
			}

			mapDottingInfoWindow(dottingLatlngList.get(dottingLatlngList.size() - 1));
		}
	}

	// 经纬度infoWindow
	@SuppressLint("InflateParams") 
	private void mapDottingInfoWindow(LatLng latlng) {
		View view = getLayoutInflater().inflate(R.layout.jj_infowindow_tool_dotting, null);
		TextView tvLat = (TextView) view.findViewById(R.id.tv_gis_tool_dotting_lat);
		TextView tvLng = (TextView) view.findViewById(R.id.tv_gis_tool_dotting_lng);
		ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_gis_tool_dotting_delete);
		TextView tvOk = (TextView) view.findViewById(R.id.tv_gis_tool_dotting_ok);
		ivDelete.setVisibility(View.GONE);
		tvOk.setVisibility(View.GONE);

		tvLng.setText("经度：" + UtilHandler.getInstance().toDfLl(latlng.longitude));
		tvLat.setText("纬度：" + UtilHandler.getInstance().toDfLl(latlng.latitude));

		// 定义用于显示该InfoWindow的坐标点
		// 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
		InfoWindow mInfoWindow = new InfoWindow(view, GPSUtil.getInstances().toBdLatlng(latlng), -47);// -47
		// 显示InfoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	// 移动到指定位置
	private void toMoveLatLng(LatLng movell) {
		if (movell == null) {
			return;
		}
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(movell));
	}
}
