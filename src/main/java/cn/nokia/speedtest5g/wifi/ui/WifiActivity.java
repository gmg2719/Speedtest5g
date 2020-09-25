package cn.nokia.speedtest5g.wifi.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import org.xclcharts.chart.PointD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.view.MyScrollyListView;
import cn.nokia.speedtest5g.view.WifiHistoryChartView;
import cn.nokia.speedtest5g.view.WifiXdChartView;
import cn.nokia.speedtest5g.wifi.adapter.WifiListAdapter;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

/**
 * wifi页面
 * @author zwq
 */
public class WifiActivity extends BaseActionBarHandlerActivity implements OnCheckedChangeListener{
	
	private final int TASK_WIFI_LIST = 200;
	
	private final int TASK_WIFI_UPDATE = 201;
	
	//当前连接，ip,mac,路由型号，路由ip,路由mac,信号强度，连接速度，频率，DNS，掩码，信道,历史强度，手机品牌
	private TextView mTvRead,mTvPhoneIp,mTvPhoneMac,mTvWifiIp,mTvWifiMac,mTvDbm,mTvSpeed,mTvFrequency,mTvDns,mTvCode,mTvXd,mTvHistory,mTvPhone;
	//WIFI布局，当前无连接
	private View mViewWifi,mViewNot;
	//WIFI列表
	private MyScrollyListView mListView;
	
	private WifiListAdapter mAdapterList;
	//连接历史图表
	private WifiHistoryChartView mWifiHistoryChartView;
	//信道图表
	private WifiXdChartView mWifiXdChartView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_activity_home);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		String title = "信道分析";
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("title",title);
		}
		init(title, true);
		initTimeRead();
        initWifi();
	}
	
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		mTvRead 	  = (TextView) findViewById(R.id.wifi_tv_name);
		mTvPhoneIp 	  = (TextView) findViewById(R.id.wifi_tv_phoneIp);
		mTvPhoneMac   = (TextView) findViewById(R.id.wifi_tv_phoneMac);
		mTvWifiIp 	  = (TextView) findViewById(R.id.wifi_tv_wifiIp);
		mTvWifiMac 	  = (TextView) findViewById(R.id.wifi_tv_wifiMac);
		mTvDbm 		  = (TextView) findViewById(R.id.wifi_tv_wifiDmb);
		mTvSpeed 	  = (TextView) findViewById(R.id.wifi_tv_wifiSpeed);
		mTvFrequency  = (TextView) findViewById(R.id.wifi_tv_wifiFrequency);
		mTvDns 		  = (TextView) findViewById(R.id.wifi_tv_wifiDns);
		mTvCode		  = (TextView) findViewById(R.id.wifi_tv_wifiCode);
		mTvXd 		  = (TextView) findViewById(R.id.wifi_tv_wifiXd);
		mListView	  = (MyScrollyListView) findViewById(R.id.wifi_listview);
		mTvHistory	  = (TextView) findViewById(R.id.wifi_tv_hisotry);
		mTvPhone	  = (TextView) findViewById(R.id.wifi_tv_phoneName);
		
		mViewNot	  = findViewById(R.id.wifi_tv_not);
		mViewWifi	  = findViewById(R.id.wifi_view_now);
		
		//历史连接曲线图布局
		FrameLayout mFrameLayoutChart = (FrameLayout) findViewById(R.id.wifi_layout_chartHistory);
		//曲线图控件
		mWifiHistoryChartView = new WifiHistoryChartView(this);
		mWifiHistoryChartView.mListenerBack = this;
		mFrameLayoutChart.addView(mWifiHistoryChartView);
		
		//信道曲线图布局
		FrameLayout mFrameLayoutChartXd = (FrameLayout) findViewById(R.id.wifi_layout_chartXd);
		mWifiXdChartView = new WifiXdChartView(this);
		mFrameLayoutChartXd.addView(mWifiXdChartView);
		
		mAdapterList = new WifiListAdapter(getLayoutInflater());
		mListView.setAdapter(mAdapterList);
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, filter);
        
        if (Build.VERSION.SDK_INT >= 26 && !GPSUtil.getInstances().isOpen(this)){
            new CommonDialog(this).setListener(new ListenerBack() {
				
				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					if (isTrue){
                        GPSUtil.getInstances().open(WifiActivity.this);
                    }
				}
			}).setButtonText("设置","取消").show("Android8.0系统及以上，若GPS没开启，无法获取WiFi数据！");
        }
        
		((RadioGroup)findViewById(R.id.wifi_rg_time)).setOnCheckedChangeListener(this);
		
		mTvPhone.setText(SystemUtil.getInstance().getSystemModel());
	}
	
	@Override
    protected void onDestroy() {
        try{
        	removeMessage(TASK_WIFI_UPDATE);
        	removeMessage(TASK_WIFI_LIST);
            unregisterReceiver(wifiStateReceiver);
            mWifiHistoryChartView.onDestroy();
        }catch (Exception e){

        }
        super.onDestroy();
    }

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		// TODO Auto-generated method stub
		super.onListener(type, object, isTrue);
		//历史连接记录
		if (type == EnumRequest.OTHER_CHART_Wifi_RETURN.toInt()) {
			if (isTrue) {
				PointD mPointD = (PointD) object;
				if(mPointD.y == Integer.MAX_VALUE){
					mTvHistory.setText(mPointD.tag + "");
				}else{
					mTvHistory.setText(String.valueOf((int)mPointD.y) + "dbm\t\t" + mPointD.tag);
				}
			}else {
				mTvHistory.setText("");
			}
		}
	}
	
	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()){
	        case TASK:
	            if (events.getType() == TASK_WIFI_UPDATE){
	                initWifi();
	            }else if (events.getType() == TASK_WIFI_LIST){
	                if (mWifiManager.isWifiEnabled()) {
	                    //扫描WIFI
	                    mWifiManager.startScan();
	                }
	                initTimeRead();
	            }
	            break;
			default:
				break;
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

    private WifiManager mWifiManager = null;

    private void initWifi(){
        if (mWifiManager == null){
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }
        String mBssid = null;
        if (mWifiManager.isWifiEnabled()){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null){
                if (TextUtils.isEmpty(wifiInfo.getSSID()) || "<unknown ssid>".equals(wifiInfo.getSSID()) || "0x".equals(wifiInfo.getSSID())){
                	mViewNot.setVisibility(View.VISIBLE);
                	mViewWifi.setVisibility(View.GONE);
                	mTvRead.setText("");
                }else{
                	mViewNot.setVisibility(View.GONE);
                	mViewWifi.setVisibility(View.VISIBLE);
                    mBssid = wifiInfo.getBSSID();
                    mTvRead.setText(WifiUtil.getInstance().getName(wifiInfo.getSSID()));
                    mTvPhoneMac.setText("MAC：" + wifiInfo.getMacAddress());
                    mTvWifiMac.setText("路由MAC：" + mBssid);
                    mTvSpeed.setText(wifiInfo.getLinkSpeed() + "Mbps");
                    mTvDbm.setText(String.valueOf(wifiInfo.getRssi()) + "dBm");
                    addValueHistory(wifiInfo.getRssi());
                    mTvPhoneIp.setText("IP：" + WifiUtil.getInstance().intToIpAddress(wifiInfo.getIpAddress()));
                    int frequency = WifiUtil.getInstance().getMethod(wifiInfo, "getFrequency", -1);
                    if (frequency != -1) {
                    	 mTvFrequency.setText(frequency + "MHz");
                    	 mTvXd.setText("" + WifiUtil.getInstance().getChannelByFrequency(frequency)[0]);
					}else {
						mTvFrequency.setText("");
                   	 	mTvXd.setText("");
					}
                    DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
                    if (dhcpInfo != null) {
						mTvWifiIp.setText("路由IP：" + WifiUtil.getInstance().intToIpAddress(dhcpInfo.serverAddress));
						mTvDns.setText(WifiUtil.getInstance().intToIpAddress(dhcpInfo.dns1));
						mTvCode.setText(WifiUtil.getInstance().intToIpAddress(dhcpInfo.netmask));
					}else {
						mTvWifiIp.setText("路由IP：");
						mTvDns.setText("");
						mTvCode.setText("");
					}
                }
            }
        }else {
        	mTvRead.setText("");
        	mViewNot.setVisibility(View.VISIBLE);
        	mViewWifi.setVisibility(View.GONE);
        }
        mAdapterList.setData(getWifiList());
        mWifiXdChartView.addValue(mAdapterList.mArrList, mBssid);
        removeMessage(TASK_WIFI_UPDATE);
        sendMessage(new MyEvents(ModeEnum.TASK, TASK_WIFI_UPDATE),1000);
    }
    
    //添加记录
    private void addValueHistory(int value){
    	mWifiHistoryChartView.addValue(value , TimeUtil.getInstance().getNowTime("HH:mm:ss"));
    }

  //获取wifi列表
    private List<ScanResult> getWifiList() {
        List<ScanResult> scanWifiList = mWifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        return wifiList;
    }
    
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null){
                if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
                    initTimeRead();
                    initWifi();
                }else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                    WybLog.syso(mWifiManager.getWifiState() + "状态...");
                }
            }
        }
    };

	@Override
	public void onCheckedChanged(RadioGroup arg0, int ids) {
        if (ids == R.id.wifi_rb_time1) {//1分钟
            mWifiHistoryChartView.setLab(1);
        } else if (ids == R.id.wifi_rb_time3) {//3分钟
            mWifiHistoryChartView.setLab(3);
        } else if (ids == R.id.wifi_rb_time5) {//5分钟
            mWifiHistoryChartView.setLab(5);
        } else if (ids == R.id.wifi_rb_time10) {//10分钟
            mWifiHistoryChartView.setLab(10);
        } else if (ids == R.id.wifi_rb_time20) {//20分钟
            mWifiHistoryChartView.setLab(20);
        } else if (ids == R.id.wifi_rb_time30) {//30分钟
            mWifiHistoryChartView.setLab(30);
        }
	}
}
