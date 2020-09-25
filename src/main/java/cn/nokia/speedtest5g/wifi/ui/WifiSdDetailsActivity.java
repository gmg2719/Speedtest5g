package cn.nokia.speedtest5g.wifi.ui;

import java.io.Serializable;
import java.util.List;

import com.android.volley.util.JsonHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestSuggestDialog;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * wifi详情页面
 * @author JQJ
 *
 */
public class WifiSdDetailsActivity extends BaseActionBarActivity {

	private final int MSG_WHAT_UPDATE = 100; //更新信息
	private final long UPDATE_TIME = 2000;

	private ScanResult mScanResult = null;
	private WifiManager mWifiManager = null;
	private WifiInfo mWifiInfo = null;
	private TextView mTvName;
	private TextView mTvStatus, mTvDbm;
	private TextView mTvMac, mTvEncryption, mTvChannel, mTvFrequency, mTvKey, mTvValue, mTvEarfcn;

	private LinearLayout mLlMyModule = null;
	private TextView mTvMyBssid, mTvMyDbm;
	private ProgressBar mTvMyPbValue = null;

	private Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_wifi_sd_details_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("Wi-Fi详情", true);
		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_xqzb_help, EnumRequest.MENU_SELECT_ONE.toInt(), ""));
		initView();

		mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

		mScanResult = getIntent().getParcelableExtra("ScanResult");
		if(mScanResult == null){ //显示已连接的
			mWifiInfo = mWifiManager.getConnectionInfo();
			if(mWifiInfo != null){
				mLlMyModule.setVisibility(View.VISIBLE);
			}
		}else{
			mLlMyModule.setVisibility(View.GONE);
		}

		updateResult();
		intHandler();
		mHandler.sendEmptyMessageDelayed(MSG_WHAT_UPDATE, UPDATE_TIME);
	}

	@SuppressLint("HandlerLeak") 
	private void intHandler(){
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_WHAT_UPDATE){
					if(mWifiInfo != null){
						mWifiInfo = mWifiManager.getConnectionInfo();
					}else if(mScanResult != null){
						List<ScanResult> list = WifiUtil.getInstance().getWifiList(mWifiManager);
						//获取最新值
						for(ScanResult scanResult : list){
							if(!TextUtils.isEmpty(mScanResult.BSSID) && mScanResult.BSSID.equals(scanResult.BSSID)){
								mScanResult = scanResult;
							}
						}
					}

					updateResult();
					mHandler.sendEmptyMessageDelayed(MSG_WHAT_UPDATE, UPDATE_TIME);
				}
			};
		};
	}

	private void initView(){
		mTvName = (TextView)findViewById(R.id.wifi_sd_details_tv_name);
		mTvStatus = (TextView)findViewById(R.id.wifi_sd_details_tv_status); 
		mTvDbm = (TextView)findViewById(R.id.wifi_sd_details_tv_dbm); 

		mLlMyModule = (LinearLayout)findViewById(R.id.wifi_sd_details_ll_my_module); 
		mTvMyBssid = (TextView)findViewById(R.id.wifi_sd_details_my_tv_bssid);
		mTvMyDbm = (TextView)findViewById(R.id.wifi_sd_details_my_tv_dbm);
		mTvMyPbValue = (ProgressBar)findViewById(R.id.wifi_sd_details_my_pb_value);

		mTvMac = (TextView)findViewById(R.id.wifi_sd_details_tv_mac); 
		mTvEncryption = (TextView)findViewById(R.id.wifi_sd_details_tv_encryption);  
		mTvChannel = (TextView)findViewById(R.id.wifi_sd_details_tv_channel);  
		mTvFrequency = (TextView)findViewById(R.id.wifi_sd_details_tv_frequency);  
		mTvKey = (TextView)findViewById(R.id.wifi_sd_details_tv_key);  
		mTvValue = (TextView)findViewById(R.id.wifi_sd_details_tv_value); 
		mTvEarfcn = (TextView)findViewById(R.id.wifi_sd_details_tv_earfcn);
	}

	private void updateResult(){
		try{
			if(mWifiInfo != null){
				mTvName.setText(WifiUtil.getInstance().getName(mWifiInfo.getSSID()));
				mTvDbm.setText(String.valueOf(mWifiInfo.getRssi()));

				mTvMyBssid.setText(mWifiInfo.getBSSID() + "（已连接）");
				mTvMyDbm.setText(mWifiInfo.getRssi()+"dBm");

				//进度条
				mTvMyPbValue.setProgress(WifiUtil.getInstance().getProgressValue(mWifiInfo.getRssi()));

				//加密
				List<ScanResult> list = WifiUtil.getInstance().getWifiList(mWifiManager);
				for(ScanResult scanResult : list){
					if(scanResult.BSSID.equals(mWifiInfo.getBSSID())){
						if(TextUtils.isEmpty(scanResult.capabilities)){
							mTvEncryption.setText("未加密");
						}else{
							if("[ESS]".equals(scanResult.capabilities)){
								mTvEncryption.setText("未加密");
							}else{
								if(scanResult.capabilities.contains("[ESS]")){
									mTvEncryption.setText(scanResult.capabilities.replace("[ESS]", ""));
								}else{
									mTvEncryption.setText(scanResult.capabilities);
								}
							}
						}
						break;
					}
				}

				mTvMac.setText(mWifiInfo.getBSSID());
				mTvKey.setText("连接速度：");
				mTvValue.setText(mWifiInfo.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
				int frequency = WifiUtil.getInstance().getMethod(mWifiInfo, "getFrequency", -1);
				if (frequency != -1) {
					mTvEarfcn.setText(frequency + "MHz");
					Object[] object = WifiUtil.getInstance().getChannelByFrequency(frequency);
					mTvChannel.setText(object[0]+"");
					mTvFrequency.setText(object[1]+"");
				}
				updateStatus(mWifiInfo.getRssi());
			}else if(mScanResult != null){
				mTvName.setText(WifiUtil.getInstance().getName(mScanResult.SSID));
				mTvDbm.setText(String.valueOf(mScanResult.level));

				mTvMac.setText(mScanResult.BSSID);
				mTvKey.setText("信道宽度：");
				ScanResultBean bean = JsonHandler.getHandler().getTarget(JsonHandler.getHandler().toJson(mScanResult),
						ScanResultBean.class);
				if(bean != null){
					mTvValue.setText(WifiUtil.getInstance().getChannelWidth(bean.channelWidth));
				}

				//加密
				if(TextUtils.isEmpty(bean.capabilities)){
					mTvEncryption.setText("未加密");
				}else{
					if("[ESS]".equals(bean.capabilities)){
						mTvEncryption.setText("未加密");
					}else{
						if(bean.capabilities.contains("[ESS]")){
							mTvEncryption.setText(bean.capabilities.replace("[ESS]", ""));
						}else{
							mTvEncryption.setText(bean.capabilities);
						}
					}
				}

				mTvEarfcn.setText(bean.frequency + "MHz");
				Object[] object = WifiUtil.getInstance().getChannelByFrequency(bean.frequency);
				mTvChannel.setText(object[0]+"");
				mTvFrequency.setText(object[1]+"");

				updateStatus(mScanResult.level);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateStatus(int rssi){
		if(rssi > -40){
			mTvStatus.setText("信号极强");
		}else if(rssi > -80 && rssi <= -40){
			mTvStatus.setText("信号正常");
		}else if(rssi > -110 && rssi <= -80){
			mTvStatus.setText("信号较弱");
		}else if(rssi <= -110){
			mTvStatus.setText("信号微弱");
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if(type == EnumRequest.MENU_SELECT_ONE.toInt()){ //打开提示
			new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_WIFI_TIP);
		}
	}

	@SuppressWarnings("serial")
	private class ScanResultBean implements Serializable{
		int channelWidth;
		String capabilities; //加密方式
		int frequency;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mHandler != null){
			mHandler.removeMessages(MSG_WHAT_UPDATE);
			mHandler = null;
		}
	}
}
