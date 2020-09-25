package cn.nokia.speedtest5g.jzxh.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.util.MarqueesTextView;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.bean.Signal;

/**
 * NSA测试 最近记录列表适配器
 * @author JQJ
 *
 */
public class JzxhNsaListAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	//数据
	private List<Signal> mNrList = null;
	//标签
	private boolean[] mArrTag = {true,true,true,true,true,true,true,true,true,true};

	private int mColorWhite,mColorMicroBlue,mLastColor;

	public JzxhNsaListAdapter(LayoutInflater inflater, Activity activity){
		this.mInflater = inflater;
		this.mNrList  = new ArrayList<>();
		this.mColorWhite = ContextCompat.getColor(SpeedTest5g.getContext(), R.color.bg_color);
		this.mColorMicroBlue = ContextCompat.getColor(activity, R.color.emergent_bg_blue);
	}

	/**
	 * 添加数据
	 * @param signal
	 */
	public void addNrItem(Signal signal){
		if (mNrList.size() >= 100) {
			mNrList.remove(0);
		}
		if (mNrList.size() <= 0) {
			mLastColor = mColorWhite;
		}else if(!TextUtils.isEmpty(mNrList.get(mNrList.size() - 1).getLte_cgi()) &&
				!mNrList.get(mNrList.size() - 1).getLte_cgi().equals(signal.getLte_cgi())){
			if (mLastColor == mColorWhite) {
				mLastColor = mColorMicroBlue;
			}else {
				mLastColor = mColorWhite;
			}
		}
		signal.colorBg = mLastColor;
		mNrList.add(signal);
		notifyDataSetChanged();
	}

	/**
	 * 更新标签
	 * @param arrTag
	 */
	public void updateTag(String[] arrTag){
		for (int i = 0; i < arrTag.length; i++) {
			mArrTag[i] = "1".equals(arrTag[i]);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mNrList == null ? 0 : mNrList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNrList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View conterView, ViewGroup arg2) {
		ViewHolder vh = null;
		if (conterView == null) {
			conterView = mInflater.inflate(R.layout.jqj_jzxh_nsa_list_adapter, null);
			vh = new ViewHolder(conterView);
			conterView.setTag(vh);
		}else {
			vh = (ViewHolder) conterView.getTag();
		}

		Signal item = (Signal)getItem(getCount() - position - 1);
		//Time
		if (mArrTag[0]) {
			vh.mTvTime.setText(item.timeHH);
			vh.mTvTime.setVisibility(View.VISIBLE);
		}else {
			vh.mTvTime.setVisibility(View.GONE);
		}
		//CI
		if (mArrTag[1]) {
			vh.mTvCi.setText(item.getLte_enb() + "-" + item.getLte_cid());
			vh.mTvCi.setVisibility(View.VISIBLE);
		}else {
			vh.mTvCi.setVisibility(View.GONE);
		}
		//PCI
		if (mArrTag[2]) {
			vh.mTvPci.setText(item.getLte_pci());
			vh.mTvPci.setVisibility(View.VISIBLE);
		}else {
			vh.mTvPci.setVisibility(View.GONE);
		}
		//RSRP
		if (mArrTag[3]) {
			vh.mTvRsrp.setText(item.getLte_rsrp());
			vh.mTvRsrp.setVisibility(View.VISIBLE);
		}else {
			vh.mTvRsrp.setVisibility(View.GONE);
		}
		//SINR
		if (mArrTag[4]) {
			vh.mTvSinr.setText(item.getLte_sinr());
			vh.mTvSinr.setVisibility(View.VISIBLE);
		}else {
			vh.mTvSinr.setVisibility(View.GONE);
		}
		//BAND
		if (mArrTag[5]) {
			vh.mTvBand.setText(item.getLte_band());
			vh.mTvBand.setVisibility(View.VISIBLE);
		}else {
			vh.mTvBand.setVisibility(View.GONE);
		}
		//PCI_NR
		if (mArrTag[6]) {
			vh.mTvPciNr.setText(item.lte_pci_nr);
			vh.mTvPciNr.setVisibility(View.VISIBLE);
		}else {
			vh.mTvPciNr.setVisibility(View.GONE);
		}
		//RSRP_NR
		if (mArrTag[7]) {
			vh.mTvRsrpNr.setText(item.lte_rsrp_nr);
			vh.mTvRsrpNr.setVisibility(View.VISIBLE);
		}else {
			vh.mTvRsrpNr.setVisibility(View.GONE);
		}
		//SINR_NR
		if (mArrTag[8]) {
			vh.mTvSinrNr.setText(item.lte_sinr_nr);
			vh.mTvSinrNr.setVisibility(View.VISIBLE);
		}else {
			vh.mTvSinrNr.setVisibility(View.GONE);
		}
		//BAND_NR
		if (mArrTag[9]) {
			vh.mTvBandNr.setText(item.lte_band_nr);
			vh.mTvBandNr.setVisibility(View.VISIBLE);
		}else {
			vh.mTvBandNr.setVisibility(View.GONE);
		}
		conterView.setBackgroundColor(item.colorBg);
		return conterView;
	}

	private class ViewHolder{

		private MarqueesTextView mTvTime;
		private TextView mTvCi,mTvPci,mTvRsrp,mTvSinr,mTvBand,mTvPciNr,mTvRsrpNr,mTvSinrNr,mTvBandNr;

		public ViewHolder(View v){
			mTvTime    = (MarqueesTextView) v.findViewById(R.id.jzxh_nsa_list_tv_time);
			mTvCi 	   = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_ci);
			mTvPci     = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_pci);
			mTvRsrp	   = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_rsrp);
			mTvSinr	   = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_sinr);
			mTvBand    = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_band);
			mTvPciNr   = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_pci_nr);
			mTvRsrpNr  = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_rsrp_nr);
			mTvSinrNr  = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_sinr_nr);
			mTvBandNr  = (TextView) v.findViewById(R.id.jzxh_nsa_list_tv_band_nr);
		}
	}
}
