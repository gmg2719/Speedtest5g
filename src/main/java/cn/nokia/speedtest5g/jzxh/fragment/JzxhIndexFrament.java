package cn.nokia.speedtest5g.jzxh.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseHandlerFragment;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.ClickTimeDifferenceUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellDetailInfo;
import cn.nokia.speedtest5g.jzxh.request.JzxhXqzbRequest;
import cn.nokia.speedtest5g.jzxh.response.JzxhXqzbData;
import cn.nokia.speedtest5g.jzxh.response.JzxhXqzbResponse;
import cn.nokia.speedtest5g.jzxh.ui.JzxhIndexDetailsActivity;
import cn.nokia.speedtest5g.jzxh.util.JzxhNetRunnable;
import cn.nokia.speedtest5g.view.JJ_EmergentQuotaTimeView;

/**
 * 基站信号小区指标性能
 * @author zwq
 *
 */
public class JzxhIndexFrament extends BaseHandlerFragment implements OnClickListener,ListenerBack{

	//接入类-RRC建立成功率，RRC连接建立成功率，E-RAB建立成功率，E-RAB建立请求数
	private TextView mTvJrlRrcjlcgl,mTvJrlRrcljjlcgl,mTvJrlErabjlcgl,mTvJrlErabjlqqs;
	//保持类-切换成功率，无线掉线率，E-RAB掉线率
	private TextView mTvBclQhcgl,mTvBclWxdxl,mTvBclErabdxl;
	//质量类-小区用户面上行字节数，上行用户平均速率，小区用户面下行字节数，下行用户平均速率，VOLTE上行丢包率，上行RTP丢包数
	private TextView mTvZllXqyhmsxzjs,mTvZllSxyhpjsl,mTvZllXqyhmxxzjs,mTvZllXxyhpjsl,mTvZllVoltesxdbl,mTvZllSxrtpdbs;
	//容量类-最大RRC连接数,上行PRB利用率,下行PRB利用率
	private TextView mTvRllZdrrcljs,mTvRllSxprblyl,mTvRllXxprblyl;
	//第一天，第二天，第三天，第四天，第五天，第六天，第七天
	private List<JJ_EmergentQuotaTimeView> mArrViewTime;
	//表格布局,加载进度
	private View mViewGroup,mViewLoading;
	//无数据显示
	private TextView mTvMsg;
	//当前指标集合数据
	private JzxhXqzbData mJzxhXqzbData;

	private JzxhNetRunnable mJzxhNetRunnable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.jzxh_frament_index, container, false);

		mTvJrlRrcjlcgl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_jrl_rrcjlcgs);
		mTvJrlRrcljjlcgl = (TextView) mView.findViewById(R.id.jzxhIndex_tv_jrl_rrcljjlcgl);
		mTvJrlErabjlcgl  = (TextView) mView.findViewById(R.id.jzxhIndex_tv_jrl_erabjlcgl);
		mTvJrlErabjlqqs  = (TextView) mView.findViewById(R.id.jzxhIndex_tv_jrl_erabjlqqs);
		mTvZllXqyhmsxzjs = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_xqyhmsxzjs);
		mTvZllSxyhpjsl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_sxyhpjsl);
		mTvZllXqyhmxxzjs = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_xqyhmxxzjs);
		mTvZllXxyhpjsl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_xxyhpjsl);
		mTvZllVoltesxdbl = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_voltesxdbl);
		mTvZllSxrtpdbs 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_zll_sxrtpdbs);
		mTvRllZdrrcljs 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_rll_zdrrcljs);
		mTvRllSxprblyl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_rll_sxprblyl);
		mTvRllXxprblyl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_rll_xxprblyl);
		mTvBclQhcgl		 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_bcl_qhcgl);
		mTvBclWxdxl		 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_bcl_wxdxl);
		mTvBclErabdxl 	 = (TextView) mView.findViewById(R.id.jzxhIndex_tv_bcl_erabdxl);
		mViewGroup 		 = mView.findViewById(R.id.jzxhIndex_view_group);
		mViewLoading	 = mView.findViewById(R.id.jzxhIndex_view_loading);
		mTvMsg  		 = (TextView) mView.findViewById(R.id.jzxhIndex_view_nodata);

		mArrViewTime     = new ArrayList<>();
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day7));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day6));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day5));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day4));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day3));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day2));
		mArrViewTime.add((JJ_EmergentQuotaTimeView) mView.findViewById(R.id.jzxhIndex_timev_day1));

		mView.findViewById(R.id.jzxhIndex_view_jrl_rrcjlcgs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_jrl_rrcljjlcgl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_jrl_erabjlcgl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_jrl_erabjlqqs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_bcl_qhcgl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_bcl_wxdxl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_bcl_erabdxl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_xqyhmsxzjs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_sxyhpjsl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_xqyhmxxzjs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_xxyhpjsl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_voltesxdbl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_zll_sxrtpdbs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_rll_zdrrcljs).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_rll_sxprblyl).setOnClickListener(this);
		mView.findViewById(R.id.jzxhIndex_view_rll_xxprblyl).setOnClickListener(this);
		for (int i = 0; i < mArrViewTime.size(); i++) {
			mArrViewTime.get(i).setOnClickListener(this);
		}
		readNetData();
		return mView;
	}

	//当前主基站信息
	private Signal mNowSignal;
	//当同一个站点获取失败时，需间隔多久才能继续获取
	private int mReadError = 0;

	/**
	 * 添加信号
	 * @param signal
	 */
	public void addSignalItem(Signal signal){
		try{
			if ("00".equals(getUserID())) {
				if(mTvMsg != null){
					mTvMsg.setText("游客暂无法获取小区指标");
					mTvMsg.setVisibility(View.VISIBLE);
				}
				if(mViewGroup != null){
					mViewGroup.setVisibility(View.GONE);
				}
				if(mViewLoading != null){
					mViewLoading.setVisibility(View.GONE);
				}
				return;
			}
			if (signal != null && ("NR".equals(signal.getTypeNet()) || "LTE".equals(signal.getTypeNet()))) {
				if (NetWorkStateUtil.isCmccNet(signal.getMnc()) > 0 || !TextUtils.isEmpty(signal.lte_nci_nr)) {
					if(mTvMsg != null){
						mTvMsg.setText(!TextUtils.isEmpty(signal.lte_nci_nr) ? "暂只支持LTE小区指标查询" : "暂只支持移动小区指标查询");
						mTvMsg.setVisibility(View.VISIBLE);
					}
					if(mViewGroup != null){
						mViewGroup.setVisibility(View.GONE);
					}
					if(mViewLoading != null){
						mViewLoading.setVisibility(View.GONE);
					}
					return;
				}
				mReadError += 1;
				if (mNowSignal != null){
					boolean isNeq = !signal.getLte_cgi().equals(mNowSignal.getLte_cgi());
					if (isNeq || mTvMsg.getVisibility() == View.VISIBLE){
						mNowSignal = signal;
						if (!isNeq && mReadError <= 15) {
							return;
						}
						readNetData();
					}
				}else {
					mNowSignal = signal;
					readNetData();
				}
			}else if (signal != null) {
				mNowSignal = signal;
				if (mJzxhNetRunnable != null) {
					mJzxhNetRunnable.close();
				}
				if(mTvMsg != null){
					mTvMsg.setText("暂只支持LTE小区指标查询");
					mTvMsg.setVisibility(View.VISIBLE);
				}
				if(mViewGroup != null){
					mViewGroup.setVisibility(View.GONE);
				}
				if(mViewLoading != null){
					mViewLoading.setVisibility(View.GONE);
				}
			}else {
				mNowSignal = signal;
				if (mJzxhNetRunnable != null) {
					mJzxhNetRunnable.close();
				}
				if(mTvMsg != null){
					mTvMsg.setText("未能采集到小区信息");
					mTvMsg.setVisibility(View.VISIBLE);
				}
				if(mViewGroup != null){
					mViewGroup.setVisibility(View.GONE);
				}
				if(mViewLoading != null){
					mViewLoading.setVisibility(View.GONE);
				}
			}
		}catch(Exception e){
		}

	}

	//获取指标数据
	private void readNetData(){
		mReadError = 0;
		if (mNowSignal == null || TextUtils.isEmpty(mNowSignal.getLte_enb()) || TextUtils.isEmpty(mNowSignal.getLte_cid())) {
			if(mTvMsg != null){
				mTvMsg.setText("未能采集到小区信息");
				mTvMsg.setVisibility(View.VISIBLE);
			}
			if(mViewGroup != null){
				mViewGroup.setVisibility(View.GONE);
			}
			if(mViewLoading != null){
				mViewLoading.setVisibility(View.GONE);
			}
		}else {
			if(mTvMsg != null){
				mTvMsg.setVisibility(View.GONE);
			}
			//			mViewGroup.setVisibility(View.GONE);
			if(mViewLoading != null){
				mViewLoading.setVisibility(View.VISIBLE);
			}
			JzxhXqzbRequest mJzxhXqzbRequest = new JzxhXqzbRequest();
			mJzxhXqzbRequest.userid = getUserID();
			mJzxhXqzbRequest.ci = "460-00-" + mNowSignal.getLte_enb() + "-" + mNowSignal.getLte_cid();
			if (mJzxhNetRunnable != null) {
				mJzxhNetRunnable.close();
			}
			mJzxhNetRunnable = new JzxhNetRunnable(mHandler, EnumRequest.NET_EMERGENT_CELL_QUOTA_LIST.toInt(), JsonHandler.getHandler().toJson(mJzxhXqzbRequest));
			new Thread(mJzxhNetRunnable).start();
		}
	}

	@Override
	public void onDestroy() {
		if (mJzxhNetRunnable != null) {
			mJzxhNetRunnable.close();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!ClickTimeDifferenceUtil.getInstances().isClickTo() || mViewLoading.getVisibility() == View.VISIBLE) {
			return;
		}
        int id = v.getId();
        if (id == R.id.jzxhIndex_timev_day1) {//第1天
            setDataUpdate(6, true);
        } else if (id == R.id.jzxhIndex_timev_day2) {//第2天
            setDataUpdate(5, true);
        } else if (id == R.id.jzxhIndex_timev_day3) {//第3天
            setDataUpdate(4, true);
        } else if (id == R.id.jzxhIndex_timev_day4) {//第4天
            setDataUpdate(3, true);
        } else if (id == R.id.jzxhIndex_timev_day5) {//第5天
            setDataUpdate(2, true);
        } else if (id == R.id.jzxhIndex_timev_day6) {//第6天
            setDataUpdate(1, true);
        } else if (id == R.id.jzxhIndex_timev_day7) {//第7天
            setDataUpdate(0, true);
        } else if (id == R.id.jzxhIndex_view_jrl_rrcjlcgs) {//接入类-RRC建立成功数
            goHourDetaisl(0);
        } else if (id == R.id.jzxhIndex_view_jrl_rrcljjlcgl) {//接入类-RRC连接建立成功率，
            goHourDetaisl(1);
        } else if (id == R.id.jzxhIndex_view_jrl_erabjlcgl) {//接入类-E-RAB建立成功率，
            goHourDetaisl(3);
        } else if (id == R.id.jzxhIndex_view_jrl_erabjlqqs) {//接入类-E-RAB建立请求数
            goHourDetaisl(2);
        } else if (id == R.id.jzxhIndex_view_bcl_qhcgl) {//保持类-切换成功率
            goHourDetaisl(6);
        } else if (id == R.id.jzxhIndex_view_bcl_wxdxl) {//保持类-无线掉线率
            goHourDetaisl(4);
        } else if (id == R.id.jzxhIndex_view_bcl_erabdxl) {//保持类-E-RAB掉线率
            goHourDetaisl(5);
        } else if (id == R.id.jzxhIndex_view_zll_xqyhmsxzjs) {//质量类-小区用户面上行字节数，
            goHourDetaisl(7);
        } else if (id == R.id.jzxhIndex_view_zll_sxyhpjsl) {//质量类-上行用户平均速率，
            goHourDetaisl(8);
        } else if (id == R.id.jzxhIndex_view_zll_xqyhmxxzjs) {//质量类-小区用户面下行字节数，
            goHourDetaisl(9);
        } else if (id == R.id.jzxhIndex_view_zll_xxyhpjsl) {//质量类-下行用户平均速率，
            goHourDetaisl(10);
        } else if (id == R.id.jzxhIndex_view_zll_voltesxdbl) {//质量类-VOLTE上行丢包率，
            goHourDetaisl(11);
        } else if (id == R.id.jzxhIndex_view_zll_sxrtpdbs) {//质量类-上行RTP丢包数
            goHourDetaisl(12);
        } else if (id == R.id.jzxhIndex_view_rll_zdrrcljs) {//容量类-最大RRC连接数,
            goHourDetaisl(13);
        } else if (id == R.id.jzxhIndex_view_rll_sxprblyl) {//容量类-上行PRB利用率,
            goHourDetaisl(14);
        } else if (id == R.id.jzxhIndex_view_rll_xxprblyl) {//容量类-下行PRB利用率
            goHourDetaisl(15);
        }
	}

	private void goHourDetaisl(int type){
		Bundle mBundle = new Bundle();
		mBundle.putInt("type", type);
		mBundle.putSerializable("dataItem", mClickItem);
		mBundle.putSerializable("data", mJzxhXqzbData);
		goIntent(JzxhIndexDetailsActivity.class, mBundle);
	}

	private JJ_EmergentQuotaCellDetailInfo mClickItem;
	/*
	 * 更新指标数据
	 */
	private void setDataUpdate(int position,boolean isToCk){
		if (mJzxhXqzbData == null || mJzxhXqzbData.pmList == null || mJzxhXqzbData.pmList.size() <= position) {
			showCommon("暂无数据");
			return;
		}
		if (isToCk && mArrViewTime.get(position).isCheck) {
			return;
		}
		if (isToCk) {
			for (int i = 0; i < mArrViewTime.size(); i++) {
				mArrViewTime.get(i).setCheck(false);
			}
			mArrViewTime.get(position).setCheck(true);
		}
		mClickItem = mJzxhXqzbData.pmList.get(position);
		//接入类
		mTvJrlRrcjlcgl.setText(mClickItem.getEnbha06());
		mTvJrlRrcljjlcgl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0101(), 0), "00"));
		mTvJrlErabjlcgl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0102(), 0), "00"));
		mTvJrlErabjlqqs.setText(mClickItem.getEnbhb05());
		//保持类
		double rate = UtilHandler.getInstance().toDouble(mClickItem.getEu0306(), 0);
		mTvBclQhcgl.setText(UtilHandler.getInstance().toDfStr(rate, "00"));
		if (rate <= 98) {
			mTvBclQhcgl.setTextColor(getResources().getColor(R.color.red));
		} else {
			mTvBclQhcgl.setTextColor(getResources().getColor(R.color.black_small));
		}
		rate = UtilHandler.getInstance().toDouble(mClickItem.getEu0223(), 0);
		mTvBclWxdxl.setText(UtilHandler.getInstance().toDfStr(rate,"00"));
		if (rate >= 3) {
			mTvBclWxdxl.setTextColor(getResources().getColor(R.color.red));
		} else {
			mTvBclWxdxl.setTextColor(getResources().getColor(R.color.black_small));
		}

		rate = UtilHandler.getInstance().toDouble(mClickItem.getEu0202(), 0);
		mTvBclErabdxl.setText(UtilHandler.getInstance().toDfStr(rate, "00"));
		if (rate >= 5) {
			mTvBclErabdxl.setTextColor(getResources().getColor(R.color.red));
		} else {
			mTvBclErabdxl.setTextColor(getResources().getColor(R.color.black_small));
		}
		//质量类
		double value = UtilHandler.getInstance().toDouble(mClickItem.getEu0505(), 0);
		if (value <= 0) {
			mTvZllXqyhmsxzjs.setText(String.valueOf("0.0"));
		}else {
			value = UtilHandler.getInstance().toDfSum(value/1000d/1000d, "00");
			if (value <= 0) {
				mTvZllXqyhmsxzjs.setText(String.valueOf("0.01"));
			}else {
				mTvZllXqyhmsxzjs.setText(String.valueOf(value));
			}
		}
		mTvZllSxyhpjsl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0535(), 0), "00"));
		value = UtilHandler.getInstance().toDouble(mClickItem.getEu0506(), 0);
		if (value <= 0) {
			mTvZllXqyhmxxzjs.setText(String.valueOf("0.0"));
		}else {
			value = UtilHandler.getInstance().toDfSum(value/1000d/1000d, "00");
			if (value <= 0) {
				mTvZllXqyhmxxzjs.setText(String.valueOf("0.01"));
			}else {
				mTvZllXqyhmxxzjs.setText(String.valueOf(value));
			}
		}
		mTvZllXxyhpjsl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0536(), 0), "00"));
		mTvZllVoltesxdbl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0416(), 0), "00"));
		mTvZllSxrtpdbs.setText(mClickItem.getEnbhh061());
		//容量类
		mTvRllZdrrcljs.setText(mClickItem.getEnbha04());
		mTvRllSxprblyl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0529(), 0), "00"));
		mTvRllXxprblyl.setText(UtilHandler.getInstance().toDfStr(UtilHandler.getInstance().toDouble(mClickItem.getEu0530(), 0), "00"));
	}

	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case NETWORK:
			//小区指标返回--网络
			if (events.getType() == EnumRequest.NET_EMERGENT_CELL_QUOTA_LIST.toInt()) {
				mViewLoading.setVisibility(View.GONE);
				if (events.isOK()) {
					JzxhXqzbResponse mJzxhXqzbResponse = JsonHandler.getHandler().getTarget(events.getObject().toString(), JzxhXqzbResponse.class);
					if (mJzxhXqzbResponse != null && mJzxhXqzbResponse.isRs() && mJzxhXqzbResponse.datas != null && mJzxhXqzbResponse.datas.size() > 0) {
						mJzxhXqzbData = mJzxhXqzbResponse.datas.get(0);
						if (mJzxhXqzbData.pmList != null && mJzxhXqzbData.pmList.size() > 0) {
							mTvMsg.setVisibility(View.GONE);
							mViewGroup.setVisibility(View.VISIBLE);

							for (int i = 0; i < mArrViewTime.size(); i++) {
								mArrViewTime.get(i).setVisibility(View.INVISIBLE);
								mArrViewTime.get(i).setCheck(false);
							}
							for (int i = 0; i < mJzxhXqzbData.pmList.size(); i++) {
								//设置时间
								mArrViewTime.get(i).setVisibility(View.VISIBLE);
								mArrViewTime.get(i).setData(mJzxhXqzbData.pmList.get(i));
								if (i == 0) {
									mArrViewTime.get(i).setCheck(true);
									//指标数据
									setDataUpdate(i,false);
								}
							}
						}
						return;
					}
				}
				mTvMsg.setText("暂无法查询到该小区指标");
				mTvMsg.setVisibility(View.VISIBLE);
				mViewGroup.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
}
