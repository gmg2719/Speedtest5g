package cn.nokia.speedtest5g.wifi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.KeyValue;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.ClickTimeDifferenceUtil;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.PoiSearchGetUtil;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.wifi.other.WifiAnalysisInfo;
import cn.nokia.speedtest5g.wifi.other.WifiGlHintDialog;
import cn.nokia.speedtest5g.wifi.other.WifiItem;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

/**
 * 干扰分析
 * @author zwq
 */
public class WifiGlActivity extends BaseActionBarHandlerActivity implements Runnable{
	
	private final int TASK_WIFI_LIST = 200;
	
	private View mViewNotWifi,mViewWifi;
	//无wifi时提示，WIFI名称，当前地址，时间,分析进度
	private TextView mTvWifiMsg,mTvWifiName,mTvAddress,mTvTime,mTvIng;
	
	private ImageView mIvIng,mIvReadTp,mIvReadLp;
	
	private WifiManager mWifiManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_activity_gl_home);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		String title = "干扰分析";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("title",title);
		}
		initTimeRead();
		init(title, true);
	}
	
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		mViewNotWifi = findViewById(R.id.wifiGl_layout_notWifi);
		mTvWifiMsg	 = (TextView) findViewById(R.id.wifiGl_tv_wifiMsg);
		mTvWifiName	 = (TextView) findViewById(R.id.wifiGl_tv_wifiName);
		mTvAddress	 = (TextView) findViewById(R.id.wifiGl_tv_address);
		mTvTime	 	 = (TextView) findViewById(R.id.wifiGl_tv_time);
		mIvIng		 = (ImageView) findViewById(R.id.wifiGl_iv_ing);
		mViewWifi	 = findViewById(R.id.wifiGl_view_wifi);
		mTvIng 		 = (TextView) findViewById(R.id.wifiGl_tv_ing);
		mIvReadTp	 = (ImageView) findViewById(R.id.wifiGl_iv_read_tp);
		mIvReadLp	 = (ImageView) findViewById(R.id.wifiGl_iv_read_lp);
		
		if (mWifiManager == null){
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
		if (!NetInfoUtil.isWifiConnected(this)){
			updateUi(false,false);
		}else if (NetInfoUtil.GetNetype(this) != 1){
			updateUi(true,false);
		}

		IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, filter);
        //注册定位返回经纬度广播
        registerReceiver(_gpsReceiver, new IntentFilter(TypeKey.getInstance().PACKAGE_GPS));
        
		if (Build.VERSION.SDK_INT >= 26 && !GPSUtil.getInstances().isOpen(this)){
			new CommonDialog(this).setListener(new ListenerBack() {

				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					if (isTrue){
						GPSUtil.getInstances().open(WifiGlActivity.this);
					}
				}
			}).setButtonText("设置","取消").show("Android8.0系统及以上，若GPS没开启，无法获取WiFi数据！");
		}
		
		mTvTime.setText(TimeUtil.getInstance().getNowTimeSS());
	}
	
	private void updateUi(boolean isWifiOpen,boolean isTo){
		if (isWifiOpen && isTo) {
			mViewNotWifi.setVisibility(View.GONE);
			mViewWifi.setVisibility(View.VISIBLE);
			readWifi();
		}else {
			isReadWifiIng = false;
			mViewNotWifi.setVisibility(View.VISIBLE);
			mViewWifi.setVisibility(View.GONE);
			mTvWifiMsg.setText(isTo ? "请先连接Wi-Fi" : "请先打开您的Wi-Fi");
		}
	}
	
	public void onClickListener(View v){
		if (!ClickTimeDifferenceUtil.getInstances().isClickTo()) {
			return;
		}
        int id = v.getId();
        if (id == R.id.wifiGl_btn_openWifi) {//打开wifi
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else if (id == R.id.wifiGl_btn_help) {//帮助
            new WifiGlHintDialog(this).show();
        }
	}
	
	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()){
        case TASK:
            if (events.getType() == TASK_WIFI_LIST){
                if (mWifiManager.isWifiEnabled()) {
                    //扫描WIFI
                    mWifiManager.startScan();
                }
                initTimeRead();
            }else if (events.getType() == EnumRequest.OTHER_INIT.toInt()) {
				if (events.isOK) {
					mTvIng.setTag(100);
					mTvIng.setText("100");
					mIvReadLp.clearAnimation();
					mIvReadLp.setImageResource(R.drawable.icon_wifi_read_ok);
					isReadWifiIng = false;
					WifiAnalysisInfo mWifiAnalysisInfo = (WifiAnalysisInfo) events.getObject();
					if (mWifiAnalysisInfo.xd == 0) {
						new CommonDialog(WifiGlActivity.this).setListener(WifiGlActivity.this).setButtonText("重新测试", "退出").show("未能获取当前Wi-Fi", EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
					}else {
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("data", mWifiAnalysisInfo);
						mBundle.putString("title", actionBar.getTitileView().getText().toString());
						goIntent(WifiGlResultActivity.class,mBundle, true);
					}
				}else {
					int progress = (int) events.getObject();
					mTvIng.setText(String.valueOf(progress));
					mTvIng.setTag(progress);
					if (progress == 60) {
						mIvReadTp.clearAnimation();
						mIvReadTp.setImageResource(R.drawable.icon_wifi_read_ok);
						mIvReadLp.setVisibility(View.VISIBLE);
						startAnim(mIvReadLp, true);
					}
				}
			}
            break;
		default:
			break;
     }
}
	
	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		//经纬度转地址返回
		if (type == EnumRequest.OTHER_GEO_SEARCH.toInt()) {
			if (isTrue) {
				ReverseGeoCodeResult result = ((ReverseGeoCodeResult) object);
				List<PoiInfo> poiList = result.getPoiList();
				if (poiList != null && poiList.size() > 0) {
					mTvAddress.setText(poiList.get(0).name + "附近");
					return;
				}
				mTvAddress.setText(result.getAddress());
			}
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()) {
			if (isTrue) {
				readWifi();
			}else {
				finish();
			}
		}
	}
	
	private boolean isReadWifiIng;
	//扫描wifi分析
	private void readWifi(){
		if (isReadWifiIng) {
			return;
		}
		mTvIng.setTag(10);
		mTvIng.setText("10");
		startAnim(mIvIng,true);
		mIvReadTp.setImageResource(R.drawable.icon_wifi_read_ing);
		mIvReadLp.setVisibility(View.INVISIBLE);
		startAnim(mIvReadTp, true);
		isReadWifiIng = true;
		if (mWifiManager == null){
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        if (mWifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null){
                if (TextUtils.isEmpty(wifiInfo.getSSID()) || "<unknown ssid>".equals(wifiInfo.getSSID()) || "0x".equals(wifiInfo.getSSID())){
                	mTvWifiName.setText("");
                }else{
                    mTvWifiName.setText(WifiUtil.getInstance().getName(wifiInfo.getSSID()));
                }
            }
        }else {
        	mTvWifiName.setText("");
        }
        new Thread(this).start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
        	removeMessage(TASK_WIFI_LIST);
			unregisterReceiver(wifiStateReceiver);
			unregisterReceiver(_gpsReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null){
				WybLog.i(intent.getAction());
				if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
					int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
					switch (wifiState) {
					case WifiManager.WIFI_STATE_ENABLED:
						updateUi(true,false);
						break;
					case WifiManager.WIFI_STATE_DISABLED:
						updateUi(false,false);
						break;
					}
				}else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
					 Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			            if (null != parcelableExtra && parcelableExtra instanceof NetworkInfo) {    
			                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
			                State state = networkInfo.getState();
		                    updateUi(state == State.CONNECTED,true);
			            }
				}
			}
		}
	};
	
	/**
	 * 定位返回
	 */
	private BroadcastReceiver _gpsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent data) {
			Bundle bundle = data.getExtras();
			LocationBean mLocation = (LocationBean) bundle.getSerializable("location");
			if (mLocation == null) {
				return;
			}
			LatLng nowLatLng = GPSUtil.getInstances().toBdLatlng(new LatLng(mLocation.Latitude, mLocation.Longitude));
			if (nowLatLng != null) {
				unregisterReceiver(_gpsReceiver);
				//获取当前地图的中心点，然后去解析地理位置MapStatus
				PoiSearchGetUtil.getInstances().toGeoCoder(nowLatLng, WifiGlActivity.this);
			}
		}
	};
	
	//旋转动画
    private void startAnim(ImageView iv,boolean isStart) {
    	if (isStart) {
    		Animation mAnimation = AnimationUtils.loadAnimation(WifiGlActivity.this, R.anim.iv_rotation);  
    		LinearInterpolator lin = new LinearInterpolator();  
    		mAnimation.setInterpolator(lin); 
    		iv.startAnimation(mAnimation);
		}else {
			iv.clearAnimation();
		}
    }
    
    private void initTimeRead(){
        removeMessage(TASK_WIFI_LIST);
        if (Build.VERSION.SDK_INT >= 28){
        	sendMessage(new MyEvents(ModeEnum.TASK, TASK_WIFI_LIST),1000 * 30);
        }else{
        	sendMessage(new MyEvents(ModeEnum.TASK, TASK_WIFI_LIST),1000 * 5);
        }
    }

	@Override
	public void run() {
		WifiAnalysisInfo mWifiAnalysisInfo = new WifiAnalysisInfo();
		int positionFor = 0;
		while (positionFor < 3) {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				
			}
			if (!isReadWifiIng) {
				return;
			}
			if ((int)mTvIng.getTag() < 20) {
				sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt(),20));
			}
			//手机品牌
			mWifiAnalysisInfo.phoneModel = SystemUtil.getInstance().getSystemModel();
			String mSsid = "";
			if (mWifiManager.isWifiEnabled()){
	            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
	            if (wifiInfo != null){
	            	if (!TextUtils.isEmpty(wifiInfo.getSSID()) && !"<unknown ssid>".equals(wifiInfo.getSSID()) && !"0x".equals(wifiInfo.getSSID())) {
	            		mSsid = wifiInfo.getSSID();
	            		mWifiAnalysisInfo.wifiName = WifiUtil.getInstance().getName(mSsid);
	            		//手机IP
		        		mWifiAnalysisInfo.phoneIp = WifiUtil.getInstance().intToIpAddress(wifiInfo.getIpAddress());	
		        		//手机MAC
		        		mWifiAnalysisInfo.phoneMac = wifiInfo.getMacAddress();
		        		//路由器MAC
		        		mWifiAnalysisInfo.wifiMac = wifiInfo.getBSSID();
		        		//连接速度
		        		mWifiAnalysisInfo.speed = wifiInfo.getLinkSpeed();
		        		//信号强度
		        		mWifiAnalysisInfo.dbm = wifiInfo.getRssi();
	                    int frequency = WifiUtil.getInstance().getMethod(wifiInfo, "getFrequency", -1);
	                    if (frequency != -1) {
	                    	//频率
	    	        		mWifiAnalysisInfo.frequency = frequency;
	    	        		Object[] channelByFrequency = WifiUtil.getInstance().getChannelByFrequency(frequency);
	                    	//当前频道 2.4G,5G
	     	        		mWifiAnalysisInfo.pd = (String) channelByFrequency[1];
	     	        		//信道
	     	        		mWifiAnalysisInfo.xd = (int) channelByFrequency[0];
						}
		        		DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
	                    if (dhcpInfo != null) {
	                    	//路由器IP
	    	        		mWifiAnalysisInfo.wifiIp = WifiUtil.getInstance().intToIpAddress(dhcpInfo.serverAddress);
	    	        		//DNS
	    	        		mWifiAnalysisInfo.dns = WifiUtil.getInstance().intToIpAddress(dhcpInfo.dns1);
	    	        		//掩码
	    	        		mWifiAnalysisInfo.ym = WifiUtil.getInstance().intToIpAddress(dhcpInfo.netmask);
						}
					}
	            }
			}
			if ((int)mTvIng.getTag() < 40) {
				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					
				}
				if (!isReadWifiIng) {
					return;
				}
				sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt(),40));
			}
			//wifi列表
			List<ScanResult> scanWifiList = mWifiManager.getScanResults();
	        if (scanWifiList != null && scanWifiList.size() > 0) {
	        	mWifiAnalysisInfo.tpList = new ArrayList<>();
	        	mWifiAnalysisInfo.lpList = new ArrayList<>();
	            HashMap<String, Integer> signalStrength = new HashMap<>();
	            for (int i = 0; i < scanWifiList.size(); i++) {
	                ScanResult scanResult = scanWifiList.get(i);
	                if (!scanResult.SSID.isEmpty() && !scanResult.SSID.equals(mSsid)) {
	                    String key = scanResult.SSID + " " + scanResult.capabilities;
	                    if (!signalStrength.containsKey(key)) {
	                        signalStrength.put(key, i);
	    	        		Object[] channelByFrequency = WifiUtil.getInstance().getChannelByFrequency(scanResult.frequency);
	    	        		//判断所属
	    	        		if (channelByFrequency[1].toString().equals(mWifiAnalysisInfo.pd) && !scanResult.BSSID.equals(mWifiAnalysisInfo.wifiMac)) {
	    	        			//同频
		                        if ((int)channelByFrequency[0] == mWifiAnalysisInfo.xd) {
									mWifiAnalysisInfo.tpList.add(new WifiItem(scanResult.level,scanResult.frequency,scanResult.SSID,scanResult.BSSID,scanResult.capabilities));
		                        //邻频
								}else {
									mWifiAnalysisInfo.lpList.add(new WifiItem(scanResult.level,scanResult.frequency,scanResult.SSID,scanResult.BSSID,scanResult.capabilities));
								}
							}
	                    }
	                }
	            }
	        }
	        if ((int)mTvIng.getTag() < 60) {
	        	try {
					Thread.sleep(1500);
				} catch (Exception e) {
					
				}
	        	if (!isReadWifiIng) {
					return;
				}
				sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt(),60));
			}
	        //处理数据
	        if (mWifiAnalysisInfo.tpList != null && mWifiAnalysisInfo.tpList.size() > 0) {
				mWifiAnalysisInfo.tpCount = mWifiAnalysisInfo.tpList.size();
				for (WifiItem scanResult : mWifiAnalysisInfo.tpList) {
					if (Math.abs(scanResult.level - mWifiAnalysisInfo.dbm) <= 6) {
						mWifiAnalysisInfo.tpCounts += 1;
					}
				}
			}
	        if (mWifiAnalysisInfo.lpList != null && mWifiAnalysisInfo.lpList.size() > 0) {
				mWifiAnalysisInfo.lpCount = mWifiAnalysisInfo.lpList.size();
				for (WifiItem scanResult : mWifiAnalysisInfo.lpList) {
					if (Math.abs(scanResult.level - mWifiAnalysisInfo.dbm) <= 6) {
						mWifiAnalysisInfo.lpCounts += 1;
					}
				}
			}
	        //标星
	        mWifiAnalysisInfo.gradeList = new ArrayList<>();
	        if (!TextUtils.isEmpty(mWifiAnalysisInfo.pd)) {
	        	int[] arrXd;
	        	//总信道数
	        	int xdSum = 0;
				if ("2.4G".equals(mWifiAnalysisInfo.pd)) {
					arrXd = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13};
				}else {
					arrXd = new int[]{36,40,44,48,149,153,157,161,165};
//					arrXd = new int[]{36,38,40,44,46,48,52,54,56,60,62,64,149,151,153,157,159,161,165};
				}
				xdSum = mWifiAnalysisInfo.tpCount + mWifiAnalysisInfo.lpCount;
				mWifiAnalysisInfo.grade = Math.round( Math.abs((float)mWifiAnalysisInfo.tpCount - (float)xdSum)/(float)xdSum * 10f);
				for (int i = 0; i < arrXd.length; i++) {
					if (arrXd[i] == mWifiAnalysisInfo.xd) {
						mWifiAnalysisInfo.gradeList.add(new KeyValue(arrXd[i], mWifiAnalysisInfo.grade));
						continue;
					}
					mWifiAnalysisInfo.gradeList.add(new KeyValue(arrXd[i], Math.round(Math.abs((float)getXdCount(mWifiAnalysisInfo.lpList, arrXd[i]) - (float)xdSum)/(float)xdSum*10f)));
				}
			}
	        if ((int)mTvIng.getTag() < 80) {
	        	try {
					Thread.sleep(1500);
				} catch (Exception e) {
					
				}
	        	if (!isReadWifiIng) {
					return;
				}
				sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt(),80));
			}
	        if ((mWifiAnalysisInfo.tpList != null && mWifiAnalysisInfo.tpList.size() > 0) || (mWifiAnalysisInfo.lpList != null && mWifiAnalysisInfo.lpList.size() > 0)) {
				break;
			}
	        positionFor += 5;
		}
		try {
			Thread.sleep(1500);
		} catch (Exception e) {
			
		}
		if (!isReadWifiIng) {
			return;
		}
		sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt(),mWifiAnalysisInfo,true));
	}
	
	//获取信道数量
	private int getXdCount(List<WifiItem> list,int xd){
		int count = 0;
		if (list != null && list.size() > 0) {
			for (WifiItem scanResult : list) {
				Object[] channelByFrequency = WifiUtil.getInstance().getChannelByFrequency(scanResult.frequency);
				if ((int)channelByFrequency[0] == xd) {
					count += 1;
                }
			}
		}
		return count;
	}
}
