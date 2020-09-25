package cn.nokia.speedtest5g.speedtest;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.cast.SimSignalHandler;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.speedtest.bean.BeanPeriphery;
import cn.nokia.speedtest5g.speedtest.bean.RequestPeriphery;
import cn.nokia.speedtest5g.speedtest.bean.ResponsePeriphery;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import cn.nokia.speedtest5g.speedtest.util.SpeedWideDisplayLayerRunnable;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.util.gps.OnLocationListener;

/**
 * 周边网速
 * @author JQJ
 *
 */
public class SpeedWideMainActivity extends BaseActionBarActivity implements OnMapStatusChangeListener, View.OnClickListener, OnLocationListener {

    private static final int MSG_CLEAR_MAP = 100;
    private static final int MSG_LOAD_MARKER_OVER = 101;

    public int MARKER_ZOOM = 8;
    // 地图缩放级别对应的大小
    private Integer[] zoomData = new Integer[] { 0, 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000, 50000,
            25000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 5 };

    private MapView _mapView = null;
    private BaiduMap mBaiduMap = null;
    private LatLng bdLatLng = null;
    private ProgressBar mProgressBar = null;
    private Button mBtnGps = null;

    private HandlerThread _ht = null;
    private Handler mHandlerInThread = null;
    private Handler mHandler = null;

    private ArrayList<BeanPeriphery> mPeripheryList = new ArrayList<BeanPeriphery>();
    private LatLng mLlMarker = null;
    //上一次缩放倍数
    private float lastZoom = 0;
    private double mCurrentRadius;// 获取中心半径  公里
    private Context mContext = null;
    private int mWidth, mHeight; //屏幕宽高
    private boolean mIsFrist = true;
    private boolean mIsLoadOver = false;
    private SpeedWideDisplayLayerRunnable mDisplayLayerRunnable = null;
    private Button mBtnWifi = null;
    private Button mBtn4g = null;
    private Button mBtn5g = null;
    private boolean mIsWifi = true;
    private boolean mIs4g = false;
    private boolean mIs5g = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_speed_wide_main_activity);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        mContext = this;
        initHandler();
        init("周边网速", true);

        mWidth = SharedPreHandler.getShared(mContext).getIntShared(TypeKey.getInstance().WIDTH(), 0);
        mHeight = SharedPreHandler.getShared(mContext).getIntShared(TypeKey.getInstance().HEIGHT(), 0);

        initMap();
    }

    private void initHandler(){
        _ht = new HandlerThread("SpeedWideMainActivity");
        _ht.start();
        mHandlerInThread = new Handler(_ht.getLooper());

        mHandler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == MSG_CLEAR_MAP){
                    mBaiduMap.clear();
                }
                return true;
            }
        });
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);
        mProgressBar = (ProgressBar)findViewById(R.id.speed_wide_main_pb_processBar);
        mBtnGps = (Button)findViewById(R.id.speed_wide_main_btn_gps);
        mBtnGps.setOnClickListener(this);

        mBtnWifi = (Button)findViewById(R.id.speed_wide_main_btn_wifi);
        mBtn4g = (Button)findViewById(R.id.speed_wide_main_btn_4g);
        mBtn5g = (Button)findViewById(R.id.speed_wide_main_btn_5g);
        mBtnWifi.setOnClickListener(this);
        mBtn4g.setOnClickListener(this);
        mBtn5g.setOnClickListener(this);
    }

    private void initMap(){
        _mapView = (MapView) findViewById(R.id.speed_wide_main_map_mapview);
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
        mBaiduMap.setOnMapStatusChangeListener(this);
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                SimSignalHandler.getInstances().addListener(null, SpeedWideMainActivity.this);

                // 设置比例尺位置
                Point scaleControlPoint = new Point(UtilHandler.getInstance().dpTopx(10), _mapView.getHeight() - UtilHandler.getInstance().dpTopx(20));
                _mapView.setScaleControlPosition(scaleControlPoint);

                mIsLoadOver = true;
                loadData();
            }
        });
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if(mIsFrist){
            mIsFrist = false;
            return;
        }
        if(!mIsLoadOver){
            return;
        }
        loadData();
    }

    /**
     * 服务端获取数据
     */
    private void loadData(){
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        // 如果小于一定级别，则不显示任何图层
        if (mapStatus.zoom < MARKER_ZOOM || getNowZoomDis() > 10000) {
            NetWorkUtilNow.getInstances().cancelRquest();
            mProgressBar.setVisibility(View.INVISIBLE);
            mBaiduMap.clear();
            return;
        }

        //计算中心点半径
        if(mBaiduMap.getProjection() != null){
            Point pt = new Point();
            pt.x = 0;
            pt.y = 0;
            LatLng llStart = GPSUtil.getInstances().fromScreenLocation(mBaiduMap, pt);

            Point ptr = new Point();
            ptr.x = mWidth;
            ptr.y = mHeight;
            LatLng llEnd = GPSUtil.getInstances().fromScreenLocation(mBaiduMap, ptr);

            double distance = DistanceUtil.getDistance(llStart, llEnd); //单位米 屏幕对角线距离
            mCurrentRadius = SpeedTestUtil.getInstance().to2Double(distance / 2); // 单位 米
        }

        if (!isReadDataFromNet(mapStatus.zoom, mapStatus.target)) {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mLlMarker = mapStatus.target;
        lastZoom = mapStatus.zoom;

        String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_PERIPHERY_SPEED_TEST);
        RequestPeriphery request = new RequestPeriphery();
        LatLng target = GPSUtil.getInstances().toGpsLatLng(mBaiduMap.getMapStatus().target);
        request.longitude = target.longitude;
        request.latitude = target.latitude;
        request.dis = String.valueOf(getNowZoomDis());
        String jsonData = JsonHandler.getHandler().toJson(request);
        NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(requestUrl, jsonData,
                -1, new ListenerBack() {

                    @Override
                    public void onListener(int type, Object object, boolean isTrue) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        if(isTrue){
                            ResponsePeriphery responsePeriphery = JsonHandler.getHandler().getTarget(object.toString(), ResponsePeriphery.class);
                            if(responsePeriphery.isRs()){
                                mPeripheryList = responsePeriphery.datas;
                                loadLayer();
                            }
                        }
                    }
                });
    }

    /**
     * 判断是否需要重新网络数据
     * @param coreZoom 当前地图缩放倍数
     * @param coreLatlng 当前地图中心点
     * @return
     */
    private boolean isReadDataFromNet(float coreZoom, LatLng coreLatlng){
        if(lastZoom != mBaiduMap.getMapStatus().zoom){
            return true;
        }
        if (mLlMarker != null) {
            WybLog.syso("移动距离：" + DistanceUtil.getDistance(mLlMarker, coreLatlng));
            WybLog.syso("缩放倍数差值：" + (coreZoom - lastZoom));
            //如果移动距离小于当前范围半径一半
            if (DistanceUtil.getDistance(mLlMarker, coreLatlng) <= (mCurrentRadius / 2)) {
                //不加载
                return false;
            }else if(DistanceUtil.getDistance(mLlMarker, coreLatlng) > mCurrentRadius &&
                    DistanceUtil.getDistance(mLlMarker, coreLatlng) < mCurrentRadius * 2){
                //加载数据
                loadLayer();
            }else{
                return true; //其他则重新请求
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.speed_wide_main_btn_gps){
            if (bdLatLng == null) {
                showCommon("正在定位中…");
            } else {
                toMoveLatLng(bdLatLng);
            }
        }else if(view.getId() == R.id.speed_wide_main_btn_wifi){
            updateBtn(view);
        }else if(view.getId() == R.id.speed_wide_main_btn_4g){
            updateBtn(view);
        }else if(view.getId() == R.id.speed_wide_main_btn_5g){
            updateBtn(view);
        }
    }

    private void updateBtn(View view){
        if(view.getId() == R.id.speed_wide_main_btn_wifi){
            if(mIsWifi){
                return;
            }
            mIsWifi = true;
            mIs4g = false;
            mIs5g = false;
            mBtnWifi.setBackgroundResource(R.drawable.drawable_speed_wide_btn_left_select);
            mBtnWifi.setTextColor(getResources().getColor(R.color.white_edeeee));
            mBtn4g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_middle_unselect);
            mBtn4g.setTextColor(getResources().getColor(R.color.black_micro));
            mBtn5g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_right_unselect);
            mBtn5g.setTextColor(getResources().getColor(R.color.black_micro));
        }else if(view.getId() == R.id.speed_wide_main_btn_4g){
            if(mIs4g){
                return;
            }
            mIsWifi = false;
            mIs4g = true;
            mIs5g = false;
            mBtnWifi.setBackgroundResource(R.drawable.drawable_speed_wide_btn_left_unselect);
            mBtnWifi.setTextColor(getResources().getColor(R.color.black_micro));
            mBtn4g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_middle_select);
            mBtn4g.setTextColor(getResources().getColor(R.color.white_edeeee));
            mBtn5g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_right_unselect);
            mBtn5g.setTextColor(getResources().getColor(R.color.black_micro));
        }else if(view.getId() == R.id.speed_wide_main_btn_5g){
            if(mIs5g){
                return;
            }
            mIsWifi = false;
            mIs4g = false;
            mIs5g = true;
            mBtnWifi.setBackgroundResource(R.drawable.drawable_speed_wide_btn_left_unselect);
            mBtnWifi.setTextColor(getResources().getColor(R.color.black_micro));
            mBtn4g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_middle_unselect);
            mBtn4g.setTextColor(getResources().getColor(R.color.black_micro));
            mBtn5g.setBackgroundResource(R.drawable.drawable_speed_wide_btn_right_select);
            mBtn5g.setTextColor(getResources().getColor(R.color.white_edeeee));
        }

        loadLayer();
    }

    private void loadLayer(){
        try {
            if(mDisplayLayerRunnable != null) {
                mHandlerInThread.removeCallbacks(mDisplayLayerRunnable);
            }
            mDisplayLayerRunnable = new SpeedWideDisplayLayerRunnable(mBaiduMap, mPeripheryList, mIsWifi, mIs4g, mIs5g,
                    mHandler, MSG_CLEAR_MAP, MSG_LOAD_MARKER_OVER);
            mHandlerInThread.post(mDisplayLayerRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocation(LocationBean location) {
        LatLng wgsLatLng = new LatLng(location.Latitude, location.Longitude);
        if (bdLatLng == null) {
            bdLatLng = GPSUtil.getInstances().toBdLatlng(wgsLatLng);
            toMoveLatLng(bdLatLng);
        }else {
            bdLatLng = GPSUtil.getInstances().toBdLatlng(wgsLatLng);
        }

        if (bdLatLng == null) {
            return;
        }

//        MyLocationData locData = new MyLocationData.Builder().accuracy(location.Accuracy)
//                // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(0).latitude(bdLatLng.latitude).longitude(bdLatLng.longitude).build();
//        // 设置定位数据
//        mBaiduMap.setMyLocationData(locData);
    }

    // 移动到指定位置
    private void toMoveLatLng(LatLng movell) {
        if (movell == null) {
            return;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(movell));
    }

    /**
     * 获取当前比例的距离
     * @return
     */
    private int getNowZoomDis(){
        int zoom = (int) mBaiduMap.getMapStatus().zoom;
        if (zoom >= zoomData.length) {
            zoom = zoomData.length - 1;
        }
        return zoomData[zoom];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimSignalHandler.getInstances().removeListener(null, this);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onAnalysis(int status, Object... obj) {

    }
}
