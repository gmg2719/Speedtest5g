package cn.nokia.speedtest5g.wifi.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.KeyValue;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.uitl.ClickTimeDifferenceUtil;
import cn.nokia.speedtest5g.wifi.other.WifiAnalysisInfo;

/**
 * wifi干扰分析结果
 * @author zwq
 *
 */
@SuppressLint("InflateParams")
public class WifiGlResultActivity extends BaseActionBarActivity {

	private WifiAnalysisInfo mWifiAnalysisInfo;
	//当前连接，ip,mac,路由型号，路由ip,路由mac,信号强度，连接速度，频率，DNS，掩码，信道，手机品牌
	private TextView mTvRead,mTvPhoneIp,mTvPhoneMac,mTvWifiIp,mTvWifiMac,mTvDbm,mTvSpeed,mTvFrequency,mTvDns,mTvCode,mTvXd,mTvPhone;
	//当前星级评估
	private RatingBar mRatingBarNow;
	//信道星级
	private TableLayout mLayoutLevelGroup;
	//分析结果
	private View mViewHint;
	//建议信道
	private TextView mTvXdHint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_activity_gl_result);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		String title = "干扰分析结果";
		title = getIntent().getExtras().getString("title") + "结果";
		mWifiAnalysisInfo = (WifiAnalysisInfo) getIntent().getExtras().getSerializable("data");
		if (mWifiAnalysisInfo == null) {
			showCommon("出错拉！");
			finish();
			return;
		}
		init(title, true,true);

		registerReceiver(receiverFinish,new IntentFilter("com.speedtest.wifi.finish"));
	}
	
	@Override
	public void init(Object titleId, boolean isBack, boolean isScroll) {
		// TODO Auto-generated method stub
		super.init(titleId, isBack,isScroll);
		mTvRead 	  = (TextView) findViewById(R.id.wifiGlResult_tv_name);
		mTvPhoneIp 	  = (TextView) findViewById(R.id.wifiGlResult_tv_phoneIp);
		mTvPhoneMac   = (TextView) findViewById(R.id.wifiGlResult_tv_phoneMac);
		mTvWifiIp 	  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiIp);
		mTvWifiMac 	  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiMac);
		mTvDbm 		  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiDmb);
		mTvSpeed 	  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiSpeed);
		mTvFrequency  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiFrequency);
		mTvDns 		  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiDns);
		mTvCode		  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiCode);
		mTvXd 		  = (TextView) findViewById(R.id.wifiGlResult_tv_wifiXd);
		mTvPhone	  = (TextView) findViewById(R.id.wifiGlResult_tv_phoneName);
		mTvXdHint	  = (TextView) findViewById(R.id.wifiGlResult_tv_hint);
		mRatingBarNow = (RatingBar) findViewById(R.id.wifiGlResult_ratingBar_now);
		mViewHint	  = findViewById(R.id.wifiGlResult_view_hint);
		mLayoutLevelGroup = (TableLayout) findViewById(R.id.wifiGlResult_layout_levelGroup);
		
		mTvRead.setText(mWifiAnalysisInfo.wifiName);
		mTvPhone.setText(mWifiAnalysisInfo.phoneModel);
		mTvPhoneIp.setText(mWifiAnalysisInfo.phoneIp);
		mTvPhoneMac.setText(mWifiAnalysisInfo.phoneMac);
		mTvWifiIp.setText(mWifiAnalysisInfo.wifiIp);
		mTvWifiMac.setText(mWifiAnalysisInfo.wifiMac);
		mTvDbm.setText(mWifiAnalysisInfo.dbm + "dbm");
		mTvSpeed.setText(mWifiAnalysisInfo.speed + "Mbps");
		mTvFrequency.setText(mWifiAnalysisInfo.frequency + "MHZ");
		mTvDns.setText(mWifiAnalysisInfo.dns);
		mTvCode.setText(mWifiAnalysisInfo.ym);
		mTvXd.setText(String.valueOf(mWifiAnalysisInfo.xd));
		mRatingBarNow.setRating(mWifiAnalysisInfo.grade);
		
		if (mWifiAnalysisInfo.gradeList != null && mWifiAnalysisInfo.gradeList.size() > 0) {
			View mViewItem;
			for (KeyValue kValue : mWifiAnalysisInfo.gradeList) {
				mViewItem = getLayoutInflater().inflate(R.layout.wifi_view_gl_result_level_item, null);
				((TextView)mViewItem.findViewById(R.id.wifiGlResultLevelItem_tv_xd)).setText(String.valueOf(kValue.key));
				((RatingBar) mViewItem.findViewById(R.id.wifiGlResultLevelItem_ratingBar)).setRating((int)kValue.value);
				mLayoutLevelGroup.addView(mViewItem);
			}
		}
		//判断当前信道是不是最好的
		Collections.sort(mWifiAnalysisInfo.gradeList, new Comparator<KeyValue>() {
            @Override
            public int compare(KeyValue o1, KeyValue o2) {
                if ((int)o1.value < (int)o2.value) {
                    return 1;
                }else if ((int)o1.value > (int)o2.value) {
                    return -1;
                }
                return 0;
            }
        });
		if (mWifiAnalysisInfo.grade < (int)mWifiAnalysisInfo.gradeList.get(0).value) {
			String tjXd = "信道" + (int)mWifiAnalysisInfo.gradeList.get(0).key;
			for (int i = 1; i < 3; i++) {
				if (mWifiAnalysisInfo.grade < (int)mWifiAnalysisInfo.gradeList.get(i).value) {
					tjXd += "、信道" + (int)mWifiAnalysisInfo.gradeList.get(i).key;
				}
			}
			mTvXdHint.setText(tjXd);
		}else {
			mViewHint.setVisibility(View.GONE);
		}
	}
	
	public void onClickListener(View v){
		if (!ClickTimeDifferenceUtil.getInstances().isClickTo()) {
			return;
		}
        int id = v.getId();
        if (id == R.id.wifiGlResult_btn_xdfx) {//信道分析
            goIntent(WifiActivity.class, null, false);
        } else if (id == R.id.wifiGlResult_btn_details) {//详情
            goIntent(WifiGlDetailsActivity.class, getIntent().getExtras(), false);
        }
	}

	@Override
	protected void onDestroy() {
		try{
			unregisterReceiver(receiverFinish);
		}catch (Exception e){

		}
		super.onDestroy();
	}

	private BroadcastReceiver receiverFinish = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};
}
