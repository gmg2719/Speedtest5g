package cn.nokia.speedtest5g.wifi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.KeyValue;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.uitl.ClickTimeDifferenceUtil;
import cn.nokia.speedtest5g.wifi.other.WifiAnalysisInfo;
import cn.nokia.speedtest5g.wifi.other.WifiItem;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

/**
 * wifi干扰分析详情
 * @author zwq
 *
 */
public class WifiGlDetailsActivity extends BaseActionBarActivity {

	private WifiAnalysisInfo mWifiAnalysisInfo;
	//当前连接，ip,mac,路由型号，路由ip,路由mac,信号强度，连接速度，频率，DNS，掩码，信道，手机品牌,同频数量，同频强干扰，邻频数量，邻频强干扰
	private TextView mTvRead,mTvPhoneIp,mTvPhoneMac,mTvWifiIp,mTvWifiMac,mTvDbm,mTvSpeed,mTvFrequency,mTvDns,mTvCode,mTvXd,mTvPhone,mTvTpCount,mTvTpQgr,mTvLpCount,mTvLpQgr;
	//当前星级评估
	private RatingBar mRatingBarNow;
	//分析结果
	private View mViewHint;
	//建议信道
	private TextView mTvXdHint;
	//同频干扰,邻频干扰
	private LinearLayout mLayoutTpGroup,mLayoutLpGroup;

	private ImageView mIvTpArrow,mIvLpArrow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_activity_gl_details);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		String title = "干扰分析详情";
		title = getIntent().getExtras().getString("title") + "详情";
		mWifiAnalysisInfo = (WifiAnalysisInfo) getIntent().getExtras().getSerializable("data");
		if (mWifiAnalysisInfo == null) {
			showCommon("出错拉！");
			finish();
			return;
		}
		init(title, true,true);
	}
	
	@Override
	public void init(Object titleId, boolean isBack, boolean isScroll) {
		// TODO Auto-generated method stub
		super.init(titleId, isBack,isScroll);
		mTvRead 	  = findViewById(R.id.wifiGlDetails_tv_name);
		mTvPhoneIp 	  = findViewById(R.id.wifiGlDetails_tv_phoneIp);
		mTvPhoneMac   = findViewById(R.id.wifiGlDetails_tv_phoneMac);
		mTvWifiIp 	  = findViewById(R.id.wifiGlDetails_tv_wifiIp);
		mTvWifiMac 	  = findViewById(R.id.wifiGlDetails_tv_wifiMac);
		mTvDbm 		  = findViewById(R.id.wifiGlDetails_tv_wifiDmb);
		mTvSpeed 	  = findViewById(R.id.wifiGlDetails_tv_wifiSpeed);
		mTvFrequency  = findViewById(R.id.wifiGlDetails_tv_wifiFrequency);
		mTvDns 		  = findViewById(R.id.wifiGlDetails_tv_wifiDns);
		mTvCode		  = findViewById(R.id.wifiGlDetails_tv_wifiCode);
		mTvXd 		  = findViewById(R.id.wifiGlDetails_tv_wifiXd);
		mTvPhone	  = findViewById(R.id.wifiGlDetails_tv_phoneName);
		mTvXdHint	  = findViewById(R.id.wifiGlDetails_tv_hint);
		mRatingBarNow = findViewById(R.id.wifiGlDetails_ratingBar_now);
		mViewHint	  = findViewById(R.id.wifiGlDetails_view_hint);
		mLayoutTpGroup = findViewById(R.id.wifiGlResult_view_tpgrGroup);
		mLayoutLpGroup = findViewById(R.id.wifiGlResult_view_lpgrGroup);
		mIvTpArrow	   = findViewById(R.id.wifiGlResult_iv_tpgrArrow);
		mIvLpArrow	   = findViewById(R.id.wifiGlResult_iv_lpgrArrow);
		mTvTpCount	   = findViewById(R.id.wifiGlResult_tv_tpgr);
		mTvTpQgr	   = findViewById(R.id.wifiGlResult_tv_tpqgr);
		mTvLpCount	   = findViewById(R.id.wifiGlResult_tv_lpgr);
		mTvLpQgr	   = findViewById(R.id.wifiGlResult_tv_lpqgr);

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

		mTvTpCount.setText(mWifiAnalysisInfo.tpCount + "个");
		mTvTpQgr.setText(mWifiAnalysisInfo.tpCounts + "个");
		mTvLpCount.setText(mWifiAnalysisInfo.lpCount + "个");
		mTvLpQgr.setText(mWifiAnalysisInfo.lpCounts + "个");
		if (mWifiAnalysisInfo.tpList != null && mWifiAnalysisInfo.tpList.size() > 0){
			Collections.sort(mWifiAnalysisInfo.tpList, new Comparator<WifiItem>() {
				@Override
				public int compare(WifiItem o1, WifiItem o2) {
					if (o1.level < o2.level) {
						return 1;
					}else if (o1.level > o2.level) {
						return -1;
					}
					return 0;
				}
			});
			for (int i = 0; i < mWifiAnalysisInfo.tpList.size(); i++) {
				addItem(mLayoutTpGroup,mWifiAnalysisInfo.tpList.get(i),i == mWifiAnalysisInfo.tpList.size() - 1);
			}
		}

		if (mWifiAnalysisInfo.lpList != null && mWifiAnalysisInfo.lpList.size() > 0){
			Collections.sort(mWifiAnalysisInfo.lpList, new Comparator<WifiItem>() {
				@Override
				public int compare(WifiItem o1, WifiItem o2) {
					if (o1.level < o2.level) {
						return 1;
					}else if (o1.level > o2.level) {
						return -1;
					}
					return 0;
				}
			});
			for (int i = 0; i < mWifiAnalysisInfo.lpList.size(); i++) {
				addItem(mLayoutLpGroup,mWifiAnalysisInfo.lpList.get(i),i == mWifiAnalysisInfo.lpList.size() - 1);
			}
		}
	}

	private void addItem(LinearLayout layout,WifiItem item,boolean isLast){
		View mViewItem = getLayoutInflater().inflate(R.layout.wifi_view_gl_details_item,null);

		((TextView)mViewItem.findViewById(R.id.wifiGlDetailsItem_tv_name)).setText(item.SSID);
		((TextView)mViewItem.findViewById(R.id.wifiGlDetailsItem_tv_mac)).setText(getMacTo(item.BSSID));
		((TextView)mViewItem.findViewById(R.id.wifiGlDetailsItem_tv_dbm)).setText(String.valueOf(item.level));
		((TextView)mViewItem.findViewById(R.id.wifiGlDetailsItem_tv_xd)).setText(String.valueOf(WifiUtil.getInstance().getChannelByFrequency(item.frequency)[0]));
		((TextView)mViewItem.findViewById(R.id.wifiGlDetailsItem_tv_pd)).setText(String.valueOf(item.frequency));
		if (isLast){
			mViewItem.findViewById(R.id.wifiGlDetailsItem_view_sp).setVisibility(View.GONE);
		}
		layout.addView(mViewItem);
	}

	private String getMacTo(String mac){
		if (!TextUtils.isEmpty(mac)){
			String[] sp = mac.split(":");
			if (sp != null && sp.length > 2){
				return "*:" + sp[sp.length-2] + ":" + sp[sp.length-1];
			}
		}
		return mac;
	}
	
	public void onClickListener(View v){
		if (!ClickTimeDifferenceUtil.getInstances().isClickTo()) {
			return;
		}
        int id = v.getId();
        if (id == R.id.wifiGlResult_view_tpgr) {//同频干扰标题
            if (mLayoutTpGroup.getVisibility() == View.VISIBLE) {
                mLayoutTpGroup.setVisibility(View.GONE);
                mIvTpArrow.setImageResource(R.drawable.arrow_small_down);
            } else {
                mLayoutTpGroup.setVisibility(View.VISIBLE);
                mIvTpArrow.setImageResource(R.drawable.arrow_small_up);
            }
        } else if (id == R.id.wifiGlResult_view_lpgr) {//邻频干扰标题
            if (mLayoutLpGroup.getVisibility() == View.VISIBLE) {
                mLayoutLpGroup.setVisibility(View.GONE);
                mIvLpArrow.setImageResource(R.drawable.arrow_small_down);
            } else {
                mLayoutLpGroup.setVisibility(View.VISIBLE);
                mIvLpArrow.setImageResource(R.drawable.arrow_small_up);
            }
        } else if (id == R.id.wifiGlDetails_btn_test) {//再次测试
            sendBroadcast(new Intent("com.speedtest.wifi.finish"));
            goIntent(WifiGlActivity.class, null, true);
        }
	}
}
