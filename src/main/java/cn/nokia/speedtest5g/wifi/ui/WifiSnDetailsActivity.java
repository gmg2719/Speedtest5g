package cn.nokia.speedtest5g.wifi.ui;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.wifi.bean.WifiArpBean;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 蹭网 详情页面
 * @author JQJ
 *
 */
public class WifiSnDetailsActivity extends BaseActionBarActivity {

	private WifiArpBean mWifiArpBean = null;
	private boolean mIsFrom = false;

	private LinearLayout mLlMyModule = null;

	private TextView mTvName = null;
	private TextView mTvPing, mTvPacketLoss;
	private TextView mTvIp, mTvMac;
	private ImageView mIvType = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_wifi_sn_details_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("WiFi-蹭网检测", true);
		initView();

		mIsFrom = getIntent().getBooleanExtra("IsFrom", false);
		mWifiArpBean = (WifiArpBean)(getIntent().getSerializableExtra("WifiArpBean"));
		updateResult();
	}

	private void initView(){
		mLlMyModule = (LinearLayout)findViewById(R.id.wifi_sn_details_ll_my_module); 

		mIvType = (ImageView)findViewById(R.id.wifi_sn_details_iv_status);
		mTvName = (TextView)findViewById(R.id.wifi_sn_details_tv_name);

		mTvMac = (TextView)findViewById(R.id.wifi_sn_details_tv_mac); 
		mTvIp = (TextView)findViewById(R.id.wifi_sn_details_tv_ip);  
		mTvPing = (TextView)findViewById(R.id.wifi_sn_details_my_tv_ping_value);  
		mTvPacketLoss = (TextView)findViewById(R.id.wifi_sn_details_my_tv_packetLoss_value); 
	}

	private void updateResult(){
		try{
			if(!mIsFrom){
				mLlMyModule.setVisibility(View.GONE);
			}else{
				mLlMyModule.setVisibility(View.VISIBLE);
			}

			if(mWifiArpBean != null){
				mTvIp.setText(mWifiArpBean.ip);
				mTvMac.setText(mWifiArpBean.mac);

				if(mWifiArpBean.ip.endsWith(".1")){ //路由
					mTvName.setText("路由");
					mIvType.setImageResource(R.drawable.icon_wifi_sn_route_flag_big);
				}else{
					if(TextUtils.isEmpty(mWifiArpBean.hostName) && TextUtils.isEmpty(mWifiArpBean.manufactor)){
						mTvName.setText("未知设备");
						mIvType.setImageResource(R.drawable.icon_wifi_sn_phone_flag_big);
					}else{
						if(TextUtils.isEmpty(mWifiArpBean.hostName)){
							mTvName.setText("移动设备");
							mIvType.setImageResource(R.drawable.icon_wifi_sn_phone_flag_big);
						}else{
							mTvName.setText(mWifiArpBean.hostName);
							mIvType.setImageResource(R.drawable.icon_wifi_sn_computer_flag_big);
						}
					}
				}

				if(mWifiArpBean.packetLoss == 100){
					mTvPing.setText("--");
				}else{
					mTvPing.setText(String.valueOf(mWifiArpBean.pingTime));
				}
				mTvPacketLoss.setText(String.valueOf(mWifiArpBean.packetLoss));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
