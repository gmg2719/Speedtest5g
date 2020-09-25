package cn.nokia.speedtest5g.jzxh.fragment;

import java.util.ArrayList;
import java.util.List;
import org.xclcharts.chart.PointD;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.ClickTimeDifferenceUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.gis.activity.LayerDetailActivity;
import cn.nokia.speedtest5g.gis.model.WqGisLayer;
import cn.nokia.speedtest5g.gis.model.WqGisLayerInfo;
import cn.nokia.speedtest5g.gis.util.WQ_GisDbHandler;
import cn.nokia.speedtest5g.jzxh.adapter.JzxhLqAdapter;
import cn.nokia.speedtest5g.jzxh.adapter.JzxhLqSideAdapter;
import cn.nokia.speedtest5g.jzxh.ui.JzxhActivity;
import cn.nokia.speedtest5g.jzxh.ui.nsa.JzxhNsaActivity;
import cn.nokia.speedtest5g.jzxh.util.JzxhLqfxDialog;
import cn.nokia.speedtest5g.jzxh.util.JzxhSwitchInfoPopup;
import cn.nokia.speedtest5g.jzxh.util.SignalAdjItem;
import cn.nokia.speedtest5g.speedtest.SpeedTestMainActivity;
import cn.nokia.speedtest5g.view.JzxhDoubleSplineChartView;
import cn.nokia.speedtest5g.view.MyScrollyListView;
import cn.nokia.speedtest5g.view.viewpager.BaseFragment;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;
import com.fjmcc.wangyoubao.app.signal.bean.TDCell;

/**
 * 基站信号区县图与邻区
 * @author zwq
 *
 */
public class JzxhChartFrament extends BaseFragment implements OnClickListener,OnCheckedChangeListener,ListenerBack{

	public ScrollView mScrollView;
	//时间，SINR,RSRP
	private TextView mTvTime,mTvSinr,mTvRsrp,mTvSinr2,mTvRsrp2;
	//NR网络信息列表
	private MyScrollyListView mNrListView = null;
	//邻区列表
	private MyScrollyListView mLqListView = null;
	//NR网络信息列表适配器
	private JzxhLqSideAdapter mSideAdapter = null;
	//邻区列表适配器
	private JzxhLqAdapter mLqAdapter;
	//邻区标签-邻小区名，邻区标签-CI,邻区标签-频点，邻区标签-PCI,邻区标签-RSRP,邻区标签-RXLEV,邻区标签-LAC,邻区标签-BAND
	private TextView mTvLqSiteName,mTvLqCi,mTvLqEarfcn,mTvLqPci,mTvLqRsrp,mTvLqRxlev,mTvLqLac,mTvLqBand;
	//无邻区数据，邻区标签选择布局1，邻区选择标签布局2
	private View mViewLqNodata,mViewLqTag1,mViewLqTag2;
	//邻区标签选择-邻小区名，邻区标签选择-CI,邻区标签选择-频点，邻区标签选择-PCI,邻区标签选择-RSRP,邻区标签选择-RXLEV,邻区标签选择-LAC,邻区标签选择-BAND
	private CheckBox mCkLqSiteName,mCkLqCi,mCkLqEarfcn,mCkLqPci,mCkLqRsrp,mCkLqRxlev,mCkLqLac,mCkLqBand;
	//当前邻区标签
	private String[] mLqTagArr;
	//红色，微黑,蓝色
	private int mColorRed,mColorBlack,mColorBule;
	//曲线图控件
	private JzxhDoubleSplineChartView mJzxhDoubleSplineChartView = null;

	private CheckBox mCbSinr, mCbRsrp, mCbSinr2, mCbRsrp2;
	private ImageView mIvSettings = null;
	private LinearLayout mLlContent1, mLlContent2;
	private boolean isInit = false;

	private TelephonyManager mTelephonyManager;
	//通话状态
	private int mPhoneDial;
	//当前小区切换站列表
	private List<Signal> mArrSignal;
	//当前邻区类型  0LET 1TD 2GSM
	private int mLastLqNetType = -1;
	//Nr侧网络信息
	private ArrayList<SignalAdjItem> mArrNrInfo = new ArrayList<SignalAdjItem>();
	//历史邻区
	private ArrayList<SignalAdjItem> mArrOtherOld = new ArrayList<SignalAdjItem>();

	private Button mBtnNsa = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mArrSignal  = new ArrayList<>();
		mColorRed   = getResources().getColor(R.color.red);
		mColorBlack = getResources().getColor(R.color.black_micro);
		mColorBule  = Color.parseColor("#3C97E8");
		//增加电话通话监听状态
		mTelephonyManager = (TelephonyManager)getActivity().getSystemService(Service.TELEPHONY_SERVICE);   
		mTelephonyManager.listen(phoneDialListener, PhoneStateListener.LISTEN_CALL_STATE);   
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.jzxh_frament_chart, container, false);

		mTvTime		   = (TextView) mView.findViewById(R.id.jzxhChart_tv_times);
		mTvSinr 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_sinr);
		mTvRsrp 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_rsrp);
		mTvSinr2 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_sinr2);
		mTvRsrp2 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_rsrp2);
		mNrListView    = (MyScrollyListView) mView.findViewById(R.id.jzxhChart_listview_nr);
		mLqListView    = (MyScrollyListView) mView.findViewById(R.id.jzxhChart_listview_lq);
		mTvLqSiteName  = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_cellname);
		mTvLqCi 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_ci);
		mTvLqEarfcn    = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_earfcn);
		mTvLqPci 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_pci);
		mTvLqRsrp 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_rsrp);
		mTvLqRxlev 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_rxlev);
		mTvLqLac 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_lac);
		mTvLqBand 	   = (TextView) mView.findViewById(R.id.jzxhChart_tv_lq_band);
		mCkLqSiteName  = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_cellname);
		mCkLqCi 	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_ci);
		mCkLqEarfcn    = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_earfcn);
		mCkLqPci 	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_pci);
		mCkLqRsrp	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_rsrp);
		mCkLqRxlev	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_rxl);
		mCkLqLac	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_lac);
		mCkLqBand	   = (CheckBox) mView.findViewById(R.id.jzxhChart_ck_lq_band);
		mViewLqNodata  = mView.findViewById(R.id.jzxhChart_tv_lq_nodata);
		mViewLqTag1    = mView.findViewById(R.id.jzxhChart_layout_lq_tt);
		mViewLqTag2    = mView.findViewById(R.id.jzxhChart_layout_lq_tag);
		mScrollView    = (ScrollView) mView.findViewById(R.id.jzxhChart_scroll_super);

		mCbSinr        = (CheckBox) mView.findViewById(R.id.jzxhChart_cb_sinr); 
		mCbRsrp        = (CheckBox) mView.findViewById(R.id.jzxhChart_cb_rsrp); 
		mCbSinr2      = (CheckBox) mView.findViewById(R.id.jzxhChart_cb_sinr2); 
		mCbRsrp2      = (CheckBox) mView.findViewById(R.id.jzxhChart_cb_rsrp2); 
		mIvSettings    = (ImageView) mView.findViewById(R.id.jzxhChart_iv_settings);
		mIvSettings.setOnClickListener(this);
		mCbSinr.setOnClickListener(this);
		mCbRsrp.setOnClickListener(this);
		mCbSinr2.setOnClickListener(this);
		mCbRsrp2.setOnClickListener(this);

		mLlContent1    = (LinearLayout) mView.findViewById(R.id.jzxhChart_layout_content1);
		mLlContent2    = (LinearLayout) mView.findViewById(R.id.jzxhChart_layout_content2);

		//曲线图布局
		FrameLayout mFrameLayoutChart = (FrameLayout) mView.findViewById(R.id.jzxhChart_layout_chart);
		//曲线图控件
		mJzxhDoubleSplineChartView = new JzxhDoubleSplineChartView(getActivity());
		mJzxhDoubleSplineChartView.mListenerBack = this;
		mFrameLayoutChart.addView(mJzxhDoubleSplineChartView);

		initConfig();

		Button mBtnSlcs = (Button) mView.findViewById(R.id.jzxhChart_btn_slcs);
		mBtnNsa = (Button) mView.findViewById(R.id.jzxhChart_btn_nsa);
		mLqAdapter = new JzxhLqAdapter(inflater,getActivity());
		mLqListView.setAdapter(mLqAdapter);
		mSideAdapter = new JzxhLqSideAdapter(inflater,getActivity());
		mNrListView.setAdapter(mSideAdapter);
		mBtnSlcs.setText(Html.fromHtml("<u>速率测试</u>>>"));
		mBtnSlcs.setOnClickListener(this);
		//		mBtnNsa.setText(Html.fromHtml("<u>NSA信号</u>>>"));
		mBtnNsa.setText("NSA信号");
		mBtnNsa.setOnClickListener(this);
		mView.findViewById(R.id.jzxhChart_btn_siteInfo).setOnClickListener(this);
		mView.findViewById(R.id.jzxhChart_btn_lq_tagAdd).setOnClickListener(this);
		mView.findViewById(R.id.jzxhChart_btn_lq_tagOk).setOnClickListener(this);
		((RadioGroup)mView.findViewById(R.id.jzxhChart_rg_time)).setOnCheckedChangeListener(this);
		String strTagTo = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().KEY_LQ_TAG, "1,1,1,1,1,1,1,1");
		mLqTagArr = strTagTo.split(",");
		updateLqTag(mLqTagArr,true);
		initOnTouchListener();
		return mView;
	}

	@Override
	public void onDestroy() {
		mJzxhDoubleSplineChartView.onDestroy();
		if (mTelephonyManager != null) {
			mTelephonyManager.listen(phoneDialListener, PhoneStateListener.LISTEN_NONE);   
		}
		super.onDestroy();
	}

	private PhoneStateListener phoneDialListener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) 
		{
			// 注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
			super.onCallStateChanged(state, incomingNumber);
			mPhoneDial = state;
		}
	};

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.jzxhChart_cb_sinr || v.getId() == R.id.jzxhChart_cb_rsrp || 
				v.getId() == R.id.jzxhChart_cb_sinr2 || v.getId() == R.id.jzxhChart_cb_rsrp2){
			saveConfig(v.getId());
			return;
		}else if(v.getId() == R.id.jzxhChart_iv_settings){
			if(mLlContent1.getVisibility() == View.GONE){
				if(mJzxhDoubleSplineChartView != null){
					mJzxhDoubleSplineChartView.backResources();
					mJzxhDoubleSplineChartView.mIsCanTouch = false;
				}
				mIvSettings.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_jzxh_nsa_quit));
				mLlContent1.setVisibility(View.VISIBLE);
				mTvTime.setVisibility(View.GONE);
				mLlContent2.setVisibility(View.INVISIBLE);
			}else{
				mJzxhDoubleSplineChartView.mIsCanTouch = true;
				mIvSettings.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_jzxh_nsa_setting));
				mLlContent1.setVisibility(View.GONE);
				mTvTime.setVisibility(View.VISIBLE);
				mLlContent2.setVisibility(View.VISIBLE);
			}
			return;
		}

		if (!ClickTimeDifferenceUtil.getInstances().isClickTo()) {
			return;
		}
        int id = v.getId();
        if (id == R.id.jzxhChart_btn_slcs) {//速率测试
            Intent mIntent = new Intent(getActivity(), SpeedTestMainActivity.class);
            mIntent.putExtra("data", 11);
            startActivity(mIntent);
        } else if (id == R.id.jzxhChart_btn_nsa) {
            Intent mIntent;
            mIntent = getActivity().getIntent();
            mIntent.putExtra("position", ((JzxhActivity) getActivity()).mSimPosition);
            mIntent.setClass(getActivity(), JzxhNsaActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.jzxhChart_btn_siteInfo) {//邻区信息说明
            new JzxhLqfxDialog(JzxhChartFrament.this.getActivity()).show();
        } else if (id == R.id.jzxhChart_btn_lq_tagAdd) {//邻区标签选择
            mViewLqTag1.setVisibility(View.VISIBLE);
            mViewLqTag2.setVisibility(View.VISIBLE);
        } else if (id == R.id.jzxhChart_btn_lq_tagOk) {//邻区标签确定
            String[] arrStrSelect = new String[8];
            int isSelectCount = 0;
            //邻小区名
            if (mCkLqSiteName.isChecked()) {
                arrStrSelect[0] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[0] = "0";
            }
            //CI
            if (mCkLqCi.isChecked()) {
                arrStrSelect[1] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[1] = "0";
            }
            //EARFCN（频点）
            if (mCkLqEarfcn.isChecked()) {
                arrStrSelect[2] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[2] = "0";
            }
            //PCI
            if (mCkLqPci.isChecked()) {
                arrStrSelect[3] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[3] = "0";
            }
            //RSRP
            if (mCkLqRsrp.isChecked()) {
                arrStrSelect[4] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[4] = "0";
            }
            //RXLEV
            if (mCkLqRxlev.isChecked()) {
                arrStrSelect[5] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[5] = "0";
            }
            //LAC
            if (mCkLqLac.isChecked()) {
                arrStrSelect[6] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[6] = "0";
            }
            //BAND
            if (mCkLqBand.isChecked()) {
                arrStrSelect[7] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[7] = "0";
            }
            if (isSelectCount < 3) {
                showCommon("选中个数不能小于3个");
                return;
            }
            String strTagTo = "";
            for (int i = 0; i < arrStrSelect.length; i++) {
                if (i != 0) {
                    strTagTo += "," + arrStrSelect[i];
                } else {
                    strTagTo += arrStrSelect[i];
                }
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().KEY_LQ_TAG, strTagTo);
            mLqTagArr = arrStrSelect;
            updateLqTag(mLqTagArr, true);
            mViewLqTag1.setVisibility(View.GONE);
            mViewLqTag2.setVisibility(View.GONE);
        }
	}

	/**
	 * 初始化曲线图配置
	 */
	private void initConfig(){
		String strPicTag = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().KEY_JZXH_PIC_TAG, "1,1,1,1");
		String[] mPicTagArr = strPicTag.split(",");
		if("1".equals(mPicTagArr[0])){
			mCbSinr.setChecked(true);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_SINR , false);
			}
		}else{
			mCbSinr.setChecked(false);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_SINR , true);
			}
		}

		if("1".equals(mPicTagArr[1])){
			mCbRsrp.setChecked(true);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_RSRP , false);
			}
		}else{
			mCbRsrp.setChecked(false);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_RSRP , true);
			}
		}

		if("1".equals(mPicTagArr[2])){
			mCbSinr2.setChecked(true);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_SINR , false);
			}
		}else{
			mCbSinr2.setChecked(false);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_SINR , true);
			}
		}

		if("1".equals(mPicTagArr[3])){
			mCbRsrp2.setChecked(true);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_RSRP , false);
			}
		}else{
			mCbRsrp2.setChecked(false);
			if(mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_RSRP , true);
			}
		}

		isInit = true;
	}

	/**
	 * 刷新曲线图配置
	 */
	private void saveConfig(int viewId){
		StringBuffer buffer = new StringBuffer();
		if(mCbSinr.isChecked()){
			buffer.append("1");
		}else{
			buffer.append("0");
		}

		buffer.append(",");

		if(mCbRsrp.isChecked()){
			buffer.append("1");
		}else{
			buffer.append("0");
		}

		buffer.append(",");

		if(mCbSinr2.isChecked()){
			buffer.append("1");
		}else{
			buffer.append("0");
		}

		buffer.append(",");

		if(mCbRsrp2.isChecked()){
			buffer.append("1");
		}else{
			buffer.append("0");
		}

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().KEY_JZXH_PIC_TAG, buffer.toString());

		if(viewId == R.id.jzxhChart_cb_sinr){
			if(mCbSinr.isChecked() && mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_SINR , false);
			}else{
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_SINR , true);
			}
		}else if(viewId == R.id.jzxhChart_cb_rsrp){
			if(mCbRsrp.isChecked() && mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_RSRP , false);
			}else{
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_RSRP , true);
			}
		}else if(viewId == R.id.jzxhChart_cb_sinr2){
			if(mCbSinr2.isChecked() && mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_SINR , false);
			}else{
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_SINR , true);
			}
		}else if(viewId == R.id.jzxhChart_cb_rsrp2){
			if(mCbRsrp2.isChecked() && mJzxhDoubleSplineChartView != null){
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_RSRP , false);
			}else{
				mJzxhDoubleSplineChartView.hideOrShowLine(JzxhDoubleSplineChartView.TYPE_DOUBLE_RSRP , true);
			}
		}

		//重置曲线图
		if(mJzxhDoubleSplineChartView != null){
			mJzxhDoubleSplineChartView.backResources();
		}
	}

	/**
	 * 0false 1true
	 * @param arrTag 0邻小区名 1CI 2频点 3PCI 4RSRP 5RXLEV 6LAC 7BAND
	 */
	private void updateLqTag(String[] arrTag,boolean isUpdate){
		if (arrTag == null || arrTag.length < 8) {
			return;
		}
		if (isUpdate) {
			mLqAdapter.updateTag(arrTag);
			mSideAdapter.updateTag(arrTag);
		}
		//邻小区名
		mCkLqSiteName.setChecked("1".equals(arrTag[0]));
		if (mCkLqSiteName.isChecked()) {
			mTvLqSiteName.setVisibility(View.VISIBLE);
		}else {
			mTvLqSiteName.setVisibility(View.GONE);
		}
		//CI
		mCkLqCi.setChecked("1".equals(arrTag[1]));
		if (mCkLqCi.isChecked()) {
			mTvLqCi.setVisibility(View.VISIBLE);
		}else {
			mTvLqCi.setVisibility(View.GONE);
		}
		//频点
		mCkLqEarfcn.setChecked("1".equals(arrTag[2]));
		if (mCkLqEarfcn.isChecked() && mLastLqNetType == 0) {
			mTvLqEarfcn.setVisibility(View.VISIBLE);
		}else {
			mTvLqEarfcn.setVisibility(View.GONE);
		}
		//PCI
		mCkLqPci.setChecked("1".equals(arrTag[3]));
		if (mCkLqPci.isChecked() && mLastLqNetType == 0) {
			mTvLqPci.setVisibility(View.VISIBLE);
		}else {
			mTvLqPci.setVisibility(View.GONE);
		}
		//RSRP
		mCkLqRsrp.setChecked("1".equals(arrTag[4]));
		if (mCkLqRsrp.isChecked() && mLastLqNetType == 0) {
			mTvLqRsrp.setVisibility(View.VISIBLE);
		}else {
			mTvLqRsrp.setVisibility(View.GONE);
		}
		//RXLEV
		mCkLqRxlev.setChecked("1".equals(arrTag[5]));
		if (mCkLqRxlev.isChecked() && mLastLqNetType != 0) {
			mTvLqRxlev.setVisibility(View.VISIBLE);
		}else {
			mTvLqRxlev.setVisibility(View.GONE);
		}
		//LAC
		mCkLqLac.setChecked("1".equals(arrTag[6]));
		if (mCkLqLac.isChecked() && mLastLqNetType != 0) {
			mTvLqLac.setVisibility(View.VISIBLE);
		}else {
			mTvLqLac.setVisibility(View.GONE);
		}
		//BAND
		mCkLqBand.setChecked("1".equals(arrTag[7]));
		if (mCkLqBand.isChecked() && mLastLqNetType == 0) {
			mTvLqBand.setVisibility(View.VISIBLE);
		}else {
			mTvLqBand.setVisibility(View.GONE);
		}
	}

	//当前主基站信息
	private Signal mNowSignal;
	//是否通话切换，是否小区切换
	private boolean isQh,isWs;

	/**
	 * 添加信号
	 * @param signal
	 */
	public void addSignalItem(Signal signal){
		try{
			if(!isInit){
				return;
			}

			if (signal != null) {
				if ("LTE".equals(signal.getTypeNet()) || "NR".equals(signal.getTypeNet())) {
					isQh = false;
					isWs = false;
					if (mNowSignal != null) {
						if (!mNowSignal.getTypeNet().equals(signal.getTypeNet()) ||
								("LTE".equals(mNowSignal.getTypeNet()) && !mNowSignal.getLte_cgi().equals(signal.getLte_cgi()))) {
							//正在通话-判断是否有切换小区
							if (mPhoneDial == TelephonyManager.CALL_STATE_OFFHOOK) {
								isQh = true;
							}
						}
					}
					if (mPhoneDial != TelephonyManager.CALL_STATE_OFFHOOK) {
						if (mArrSignal.size() > 0 && !mArrSignal.get(mArrSignal.size() - 1).getLte_cgi().equals(signal.getLte_cgi())) {
							isWs = true;
						}
					}
					//如果站名不为空的话，则循环设置空站名的数据列表
					if (!TextUtils.isEmpty(signal.getLte_name())) {
						for (int i = 0; i < mArrSignal.size(); i++) {
							if (mArrSignal.get(i).getLte_cgi().equals(signal.getLte_cgi())) {
								if (TextUtils.isEmpty(mArrSignal.get(i).getLte_name())) {
									mArrSignal.get(i).setLte_name(signal.getLte_name());
									mArrSignal.get(i).DB_ID = signal.DB_ID;
									if (JzxhSwitchInfoPopup.getInstance().isShowing()) {
										JzxhSwitchInfoPopup.getInstance().updateItemName(signal);
									}
								}
							}
						}
					}
					int rsrp = UtilHandler.getInstance().toInt(signal.getLte_rsrp(), Integer.MAX_VALUE);
					int sinr = UtilHandler.getInstance().toInt(signal.getLte_sinr(), Integer.MAX_VALUE);
					if ("NR".equals(signal.getTypeNet()) && !TextUtils.isEmpty(signal.lte_nci_nr)) {
						rsrp = UtilHandler.getInstance().toInt(signal.lte_rsrp_nr, Integer.MAX_VALUE);
						sinr = UtilHandler.getInstance().toInt(signal.lte_sinr_nr, Integer.MAX_VALUE);
					}
					int[] addValueCode = mJzxhDoubleSplineChartView.addValue(
							rsrp
							,sinr
							//signal.subRsrp  signal.subSinr 为null则无副卡
							,(signal.subRsrp==null?Integer.MAX_VALUE:UtilHandler.getInstance().toInt(signal.subRsrp, Integer.MAX_VALUE))
							,(signal.subSinr==null?Integer.MAX_VALUE:UtilHandler.getInstance().toInt(signal.subSinr, Integer.MAX_VALUE))
							,signal.getTime(),isQh ? R.drawable.icon_ho_in_call : isWs ? R.drawable.icon_ho_no_in_call : 0);
					if (isQh || isWs || mArrSignal.size() <= 0) {
						signal.chartRsrpHashCode = addValueCode[0];
						signal.chartSinrHashCode = addValueCode[1];
						mArrSignal.add(signal);
						if (JzxhSwitchInfoPopup.getInstance().isShowing() && mArrSignal.size() > 1) {
							JzxhSwitchInfoPopup.getInstance().addItem(mArrSignal.get(mArrSignal.size() - 2), mArrSignal.get(mArrSignal.size() - 1));
						}
					}
				}else{
					mBtnNsa.setVisibility(View.INVISIBLE);
				}
			}else{
				mBtnNsa.setVisibility(View.INVISIBLE);
			}

			mArrNrInfo.clear();
			if(signal != null && "NR".equals(signal.getTypeNet())){
				SignalAdjItem item = new SignalAdjItem();
				item.NR_PCI = signal.lte_pci_nr;
				item.NR_RSRP = signal.lte_rsrp_nr;
				item.NR_SINR = signal.lte_sinr_nr;
				item.NR_BAND = signal.lte_band_nr;
				item.NR_EARFCN = signal.lte_earfcn_nr;

				int ltePci = UtilHandler.getInstance().toInt(item.NR_PCI, Integer.MAX_VALUE);
				if (ltePci != Integer.MAX_VALUE) {
					switch (ltePci % 3) {
					case 0:
						item.mo3	= "⓿";//⓿⓪#3C97E8
						break;
					case 1:
						item.mo3	= "❶";//❶①
						break;
					case 2:
						item.mo3	= "❷";//❷②
						break;
					default:
						item.mo3	= "";
						break;
					}
				}else {
					item.mo3	= "";
				}

				item.LTE_PD_COLOR   = mColorBlack;
				item.LTE_RSRP_COLOR = mColorBlack;
				item.LTE_PCI_COLOR  = mColorBlack;
				item.LTE_MO3_COLOR  = mColorBule;

				mArrNrInfo.add(item);
			}

			mSideAdapter.setData(mArrNrInfo);

			mNowSignal = signal;
		}catch(Exception e){
		}

	}

	/**
	 * 设置邻区数据
	 * @param arrList
	 */
	public void setAdjacentCell(List<Object> arrList){
		mArrOtherOld.clear();
		if (arrList != null && arrList.size() > 0) {
			SignalAdjItem mClickSignalItem;
			for (Object obj : arrList) {
				if (obj instanceof GSMCell) {
					mClickSignalItem = new SignalAdjItem();
					if (mLastLqNetType != 2) {
						mLastLqNetType = 2;
						updateLqTag(mLqTagArr, false);
					}else {
						mLastLqNetType = 2;
					}
					GSMCell mGsmItem = (GSMCell) obj;
					mClickSignalItem.cellName  = TextUtils.isEmpty(mGsmItem.name) ? "-" : mGsmItem.name;
					mClickSignalItem.GSM_CID   = TextUtils.isEmpty(mGsmItem.getCid()) || mGsmItem.cid == Integer.MAX_VALUE ? "-" : mGsmItem.getCid();
					mClickSignalItem.GSM_LAC   = TextUtils.isEmpty(mGsmItem.getLac()) || mGsmItem.lac == 0 || mGsmItem.lac == Integer.MAX_VALUE ? "-" : mGsmItem.getLac();
					mClickSignalItem.GSM_RXLEV = TextUtils.isEmpty(mGsmItem.getSign()) || mGsmItem.sign == Integer.MAX_VALUE ? "-" : mGsmItem.getSign();
					mClickSignalItem.DB_ID	   = mGsmItem.asu;
					mClickSignalItem.GSM_RXLEV_COLOR = mColorBlack;
					if (mNowSignal != null) {
						if (!TextUtils.isEmpty(mNowSignal.getGsm_rxl()) && mGsmItem.sign - (UtilHandler.getInstance().toInt(mNowSignal.getGsm_rxl(), 0)) > 6) {
							mClickSignalItem.GSM_RXLEV_COLOR = mColorRed;
						}
					}
					mArrOtherOld.add(mClickSignalItem);
				}else if(obj instanceof LTECell){
					mClickSignalItem = new SignalAdjItem();
					if (mLastLqNetType != 0) {
						mLastLqNetType = 0;
						updateLqTag(mLqTagArr, false);
					}else {
						mLastLqNetType = 0;
					}
					LTECell mLteItem = (LTECell) obj;
					mClickSignalItem.cellName       = TextUtils.isEmpty(mLteItem.name) ? "-" : mLteItem.name;
					mClickSignalItem.LTE_CELLID     = TextUtils.isEmpty(mLteItem.getCellid()) || mLteItem.cellid == Integer.MAX_VALUE ? "-" : mLteItem.getCellid();
					mClickSignalItem.LTE_ENB        = TextUtils.isEmpty(mLteItem.getEnb()) || mLteItem.enb == Integer.MAX_VALUE ? "-" : mLteItem.getEnb();
					mClickSignalItem.LTE_PCI        = TextUtils.isEmpty(mLteItem.getPci())|| mLteItem.pci == Integer.MAX_VALUE ? "-" : mLteItem.getPci();
					int ltePci = UtilHandler.getInstance().toInt(mLteItem.getPci(), Integer.MAX_VALUE);
					if (ltePci != Integer.MAX_VALUE) {
						switch (ltePci % 3) {
						case 0:
							mClickSignalItem.mo3	= "⓿";//⓿⓪#3C97E8
							break;
						case 1:
							mClickSignalItem.mo3	= "❶";//❶①
							break;
						case 2:
							mClickSignalItem.mo3	= "❷";//❷②
							break;
						default:
							mClickSignalItem.mo3	= "";
							break;
						}
					}else {
						mClickSignalItem.mo3	= "";
					}
					mClickSignalItem.LTE_RSRP       = TextUtils.isEmpty(mLteItem.getRsrp()) || mLteItem.rsrp == Integer.MAX_VALUE ? "-" : mLteItem.getRsrp();
					mClickSignalItem.LTE_PD         = TextUtils.isEmpty(mLteItem.band) ? "-" : mLteItem.band;
					mClickSignalItem.LTE_BAND 		= TextUtils.isEmpty(mLteItem.band1) ? "-" : mLteItem.band1;
					mClickSignalItem.LTE_TAC        = TextUtils.isEmpty(mLteItem.getTac()) || mLteItem.tac == Integer.MAX_VALUE ? "-" : mLteItem.getTac();
					mClickSignalItem.DB_ID	   		= mLteItem.asu;
					mClickSignalItem.LTE_PD_COLOR   = mColorBlack;
					mClickSignalItem.LTE_RSRP_COLOR = mColorBlack;
					mClickSignalItem.LTE_PCI_COLOR  = mColorBlack;
					mClickSignalItem.LTE_MO3_COLOR  = mColorBule;
					if (mNowSignal != null) {
						if (mNowSignal.lte_pd.equals(mClickSignalItem.LTE_PD)) {
							if (!TextUtils.isEmpty(mNowSignal.getLte_pci()) && UtilHandler.getInstance().toInt(mNowSignal.getLte_pci(), 0) % 3 == mLteItem.pci % 3) {
								mClickSignalItem.LTE_PD_COLOR = mColorRed;
								mClickSignalItem.LTE_PCI_COLOR = mColorRed;
								mClickSignalItem.LTE_MO3_COLOR = mColorRed;
							}
							if (!TextUtils.isEmpty(mNowSignal.getLte_rsrp()) && Math.abs(mLteItem.rsrp - (UtilHandler.getInstance().toInt(mNowSignal.getLte_rsrp(), 0))) <= 3) {
								mClickSignalItem.LTE_PD_COLOR = mColorRed;
								mClickSignalItem.LTE_RSRP_COLOR = mColorRed;
							}
						}
						if(!TextUtils.isEmpty(mNowSignal.getLte_rsrp()) && mLteItem.rsrp - (UtilHandler.getInstance().toInt(mNowSignal.getLte_rsrp(), 0)) > 3){
							mClickSignalItem.LTE_RSRP_COLOR = mColorRed;
						}
					}
					mArrOtherOld.add(mClickSignalItem);
				}else if (obj instanceof TDCell) {
					mClickSignalItem = new SignalAdjItem();
					if (mLastLqNetType != 1) {
						mLastLqNetType = 1;
						updateLqTag(mLqTagArr, false);
					}else {
						mLastLqNetType = 1;
					}
					TDCell mTdItem = (TDCell) obj;
					mClickSignalItem.cellName  = TextUtils.isEmpty(mTdItem.name) ? "-" : mTdItem.name;
					mClickSignalItem.TD_CI     = TextUtils.isEmpty(mTdItem.getCi()) || mTdItem.ci == Integer.MAX_VALUE ? "-" : mTdItem.getCi();
					mClickSignalItem.TD_LAC    = TextUtils.isEmpty(mTdItem.getLac()) || mTdItem.lac == 0 || mTdItem.lac == Integer.MAX_VALUE ? "-" : mTdItem.getLac();
					mClickSignalItem.TD_PCCPCH = TextUtils.isEmpty(mTdItem.getSign()) || mTdItem.sign == Integer.MAX_VALUE ? "-" : mTdItem.getSign();
					mClickSignalItem.TD_PCCPCH_COLOR = mColorBlack;
					if (mNowSignal != null) {
						if (!TextUtils.isEmpty(mNowSignal.getTd_pccpch()) && mTdItem.sign - (UtilHandler.getInstance().toInt(mNowSignal.getTd_pccpch(), 0)) > 6) {
							mClickSignalItem.TD_PCCPCH_COLOR = mColorRed;
						}
					}
					mArrOtherOld.add(mClickSignalItem);
				}else if(obj instanceof NrCell){
					mClickSignalItem = new SignalAdjItem();
					if (mLastLqNetType != 0) {
						mLastLqNetType = 0;
						updateLqTag(mLqTagArr, false);
					}else {
						mLastLqNetType = 0;
					}
					NrCell mNrCell = (NrCell) obj;
					mClickSignalItem.cellName       = TextUtils.isEmpty(mNrCell.name) ? "-" : mNrCell.name;
					mClickSignalItem.LTE_CELLID     = TextUtils.isEmpty(mNrCell.getCellid()) || mNrCell.cellid == Integer.MAX_VALUE ? "-" : mNrCell.getCellid();
					mClickSignalItem.LTE_ENB        = TextUtils.isEmpty(mNrCell.getGnb()) || mNrCell.gnb == Long.MAX_VALUE ? "-" : mNrCell.getGnb();
					mClickSignalItem.LTE_PCI        = TextUtils.isEmpty(mNrCell.getPci())|| mNrCell.pci == Integer.MAX_VALUE ? "-" : mNrCell.getPci();
					int ltePci = UtilHandler.getInstance().toInt(mNrCell.getPci(), Integer.MAX_VALUE);
					if (ltePci != Integer.MAX_VALUE) {
						switch (ltePci % 3) {
						case 0:
							mClickSignalItem.mo3	= "⓿";//⓿⓪#3C97E8
							break;
						case 1:
							mClickSignalItem.mo3	= "❶";//❶①
							break;
						case 2:
							mClickSignalItem.mo3	= "❷";//❷②
							break;
						default:
							mClickSignalItem.mo3	= "";
							break;
						}
					}else {
						mClickSignalItem.mo3	= "";
					}
					mClickSignalItem.LTE_RSRP       = TextUtils.isEmpty(mNrCell.getRsrp()) || mNrCell.rsrp == Integer.MAX_VALUE ? "-" : mNrCell.getRsrp();
					mClickSignalItem.LTE_PD         = TextUtils.isEmpty(mNrCell.earfcn) ? "-" : mNrCell.earfcn;
					mClickSignalItem.LTE_BAND 		= TextUtils.isEmpty(mNrCell.band) ? "-" : mNrCell.band;
					mClickSignalItem.LTE_TAC        = TextUtils.isEmpty(mNrCell.getTac()) || mNrCell.tac == Integer.MAX_VALUE ? "-" : mNrCell.getTac();
					mClickSignalItem.DB_ID	   		= mNrCell.asu;
					mClickSignalItem.LTE_PD_COLOR   = mColorBlack;
					mClickSignalItem.LTE_RSRP_COLOR = mColorBlack;
					mClickSignalItem.LTE_PCI_COLOR  = mColorBlack;
					mClickSignalItem.LTE_MO3_COLOR  = mColorBule;
					if (mNowSignal != null) {
						if (mNowSignal.lte_pd.equals(mClickSignalItem.LTE_PD)) {
							if (!TextUtils.isEmpty(mNowSignal.getLte_pci()) && UtilHandler.getInstance().toInt(mNowSignal.getLte_pci(), 0) % 3 == mNrCell.pci % 3) {
								mClickSignalItem.LTE_PD_COLOR = mColorRed;
								mClickSignalItem.LTE_PCI_COLOR = mColorRed;
								mClickSignalItem.LTE_MO3_COLOR = mColorRed;
							}
							if (!TextUtils.isEmpty(mNowSignal.getLte_rsrp()) && Math.abs(mNrCell.rsrp - (UtilHandler.getInstance().toInt(mNowSignal.getLte_rsrp(), 0))) <= 3) {
								mClickSignalItem.LTE_PD_COLOR = mColorRed;
								mClickSignalItem.LTE_RSRP_COLOR = mColorRed;
							}
						}
						if(!TextUtils.isEmpty(mNowSignal.getLte_rsrp()) && mNrCell.rsrp - (UtilHandler.getInstance().toInt(mNowSignal.getLte_rsrp(), 0)) > 3){
							mClickSignalItem.LTE_RSRP_COLOR = mColorRed;
						}
					}
					mArrOtherOld.add(mClickSignalItem);
				}
			}
		}
		if(mLqAdapter != null){
			mLqAdapter.setData(mArrOtherOld,mLastLqNetType);
		}
		if(mViewLqNodata != null){
			mViewLqNodata.setVisibility(mArrOtherOld.size() > 0 ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int ids) {
        if (ids == R.id.jzxhChart_rb_time1) {//1分钟
            mJzxhDoubleSplineChartView.setLab(1);
        } else if (ids == R.id.jzxhChart_rb_time3) {//3分钟
            mJzxhDoubleSplineChartView.setLab(3);
        } else if (ids == R.id.jzxhChart_rb_time5) {//5分钟
            mJzxhDoubleSplineChartView.setLab(5);
        } else if (ids == R.id.jzxhChart_rb_time10) {//10分钟
            mJzxhDoubleSplineChartView.setLab(10);
        } else if (ids == R.id.jzxhChart_rb_time20) {//20分钟
            mJzxhDoubleSplineChartView.setLab(20);
        } else if (ids == R.id.jzxhChart_rb_time30) {//30分钟
            mJzxhDoubleSplineChartView.setLab(30);
        }
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (getActivity() == null || getActivity().isFinishing()) {
			return;
		}
		//点击RSRP回调
		if (type == EnumRequest.OTHER_CHART_RSRP_RETURN.toInt()) {
			if (isTrue) {
				PointD mPointD = (PointD) object;
				if(mPointD.y == Integer.MAX_VALUE){
					mTvRsrp.setText("");
				}else{
					mTvRsrp.setText("(" + String.valueOf((int)mPointD.y) + ")");
				}
				mTvTime.setText(mPointD.tag);
				if (showDetailsSwitch(mPointD)) {
					JzxhSwitchInfoPopup.getInstance().mClickPointDRsrp = mPointD;
					mJzxhDoubleSplineChartView.updateValue(mPointD.hashCode(), R.drawable.cell_reselect_select,JzxhDoubleSplineChartView.TYPE_RSRP);
				}else {
					JzxhSwitchInfoPopup.getInstance().mClickPointDRsrp = null;
				}
			}else {
				JzxhSwitchInfoPopup.getInstance().mClickPointDRsrp = null;
				mTvRsrp.setText("");
				mTvTime.setText("");
			}
			//点击SINR回调	
		}else if (type == EnumRequest.OTHER_CHART_SINR_RETURN.toInt()) {
			if (isTrue) {
				PointD mPointD = (PointD) object;
				if(mPointD.y == Integer.MAX_VALUE){
					mTvSinr.setText("");
				}else{
					mTvSinr.setText("(" + String.valueOf((int)mPointD.y) + ")");
				}
				mTvTime.setText(mPointD.tag);
				if (showDetailsSwitch(mPointD)) {
					JzxhSwitchInfoPopup.getInstance().mClickPointDSinr = mPointD;
					mJzxhDoubleSplineChartView.updateValue(mPointD.hashCode(), R.drawable.cell_reselect_select,JzxhDoubleSplineChartView.TYPE_SINR);
				}else {
					JzxhSwitchInfoPopup.getInstance().mClickPointDSinr = null;
				}
			}else {
				JzxhSwitchInfoPopup.getInstance().mClickPointDSinr = null;
				mTvSinr.setText("");
				mTvTime.setText("");
			}
			//点击NR_RSRP回调
		}else if (type == EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt()) {
			if (isTrue) {
				PointD mPointD = (PointD) object;
				if(mPointD.y == Integer.MAX_VALUE){
					mTvRsrp2.setText("");
				}else{
					mTvRsrp2.setText("(" + String.valueOf((int)mPointD.y) + ")");
				}
				mTvTime.setText(mPointD.tag);
			}else {
				mTvRsrp2.setText("");
				mTvTime.setText("");
			}
			//点击NR_SINR回调	
		}else if (type == EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt()) {
			if (isTrue) {
				PointD mPointD = (PointD) object;
				if(mPointD.y == Integer.MAX_VALUE){
					mTvSinr2.setText("");
				}else{
					mTvSinr2.setText("(" + String.valueOf((int)mPointD.y) + ")");
				}
				mTvTime.setText(mPointD.tag);
			}else {
				mTvSinr2.setText("");
				mTvTime.setText("");
			}
			//切换详情点击工参跳转	
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()) {
			if ("00".equals(getUserID())) {
				showCommon("游客暂无法获取参数信息");
				return;
			}
			Signal mClickSignal = (Signal) object;
			if (TextUtils.isEmpty(mClickSignal.getLte_name())) {
				showCommon("暂无法获取参数信息，请检查工参是否更新！");
				return;
			}
			WqGisLayerInfo mWqGisLayerInfo = new WqGisLayerInfo();
			mWqGisLayerInfo.setList(new ArrayList<WqGisLayer>());
			WqGisLayer mWqGisLayer = new WqGisLayer();
			mWqGisLayer.set_id(mClickSignal.DB_ID);
			mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_LTE_CELL());
			mWqGisLayer.setType(2);
			mWqGisLayerInfo.getList().add(mWqGisLayer);
			Intent intentDetails = new Intent(getActivity(), LayerDetailActivity.class);
			intentDetails.putExtra("data", mWqGisLayerInfo);
			startActivity(intentDetails);
			//切换详情关闭后恢复初始状态
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_TWO.toInt()) {
			mJzxhDoubleSplineChartView.backResources();
			//对话框切换时图表更新切换
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_THREE.toInt()) {
			mJzxhDoubleSplineChartView.backResources();
			Integer[] arrHashCode = (Integer[]) object;
			mJzxhDoubleSplineChartView.setNextOrLastClick(arrHashCode[0], arrHashCode[1]);
		}
	}

	//显示切换信息
	private boolean showDetailsSwitch(PointD pointD){
		if (pointD.switchTypeResources != 0) {
			for (int i = 1; i < mArrSignal.size(); i++) {
				if (mArrSignal.get(i).chartRsrpHashCode == pointD.hashCode() || mArrSignal.get(i).chartSinrHashCode == pointD.hashCode()) {
					if (!JzxhSwitchInfoPopup.getInstance().isShowing()) {
						JzxhSwitchInfoPopup.getInstance().show(getActivity(), mTvTime, mArrSignal, i - 1, this);
					}
					return true;
				}
			}
		}
		return false;
	}

	private int scrollY = 0;

	@Override
	public void onResume() {
		super.onResume();
		//		setScrollTo(300,scrollY);
	}

	public void setScrollTo(long time,final int y){
		if (mScrollView != null) {
			mScrollView.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (y != mScrollView.getScrollY()) {
						mScrollView.scrollTo(0, y);
					}
				}
			}, time);
		}
	}

	private OnTouchListener mOnFocusChangeListener = null;

	private void initOnTouchListener(){
		if (mOnFocusChangeListener == null && mScrollView != null) {
			mOnFocusChangeListener = new OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility") 
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (arg1.getAction() == MotionEvent.ACTION_UP) {
						mScrollView.postDelayed(new Runnable() {

							@Override
							public void run() {
								scrollY = mScrollView.getScrollY();
								if (scrollY < 0) {
									scrollY = 0;
								}
							}
						}, 300);

					}
					return false;
				}
			};
			mScrollView.setOnTouchListener(mOnFocusChangeListener);
		}
	}
}
