package cn.nokia.speedtest5g.jzxh.adapter;

import java.util.ArrayList;
import com.android.volley.util.MarqueesTextView;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.jzxh.util.SignalAdjItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * NSA测试界面 邻区头部增加 Nr侧网络信息适配器
 * @author JQJ
 *
 */
public class JzxhNsaLqSideAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	//邻区数据
	private ArrayList<SignalAdjItem> mArrAdjList = null;
	//标签
	private boolean[] mArrTag = {true,true,true,true,true,true};

	public JzxhNsaLqSideAdapter(LayoutInflater inflater,Activity activity){
		this.mInflater = inflater;
	}

	/**
	 * 设置当前邻区数据
	 * @param arrAdj 
	 */
	public void setData(ArrayList<SignalAdjItem> arrAdj){
		this.mArrAdjList = arrAdj;
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
		return mArrAdjList == null ? 0 : mArrAdjList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrAdjList.get(position);
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
			conterView = mInflater.inflate(R.layout.jzxh_adapter_lq, null);
			vh = new ViewHolder(conterView);
			conterView.setTag(vh);
		}else {
			vh = (ViewHolder) conterView.getTag();
		}

		SignalAdjItem item = (SignalAdjItem) getItem(position);
		//小区名
		if (mArrTag[0]) {
			vh.mTvName.setText("-");
			vh.mViewName.setVisibility(View.VISIBLE);
		}else {
			vh.mViewName.setVisibility(View.GONE);
		}
		//CI
		if (mArrTag[1]) {
			vh.mTvCi.setText("-");
			vh.mTvCi.setVisibility(View.VISIBLE);
		}else {
			vh.mTvCi.setVisibility(View.GONE);
		}
		//频点
		if (mArrTag[2]) {
			vh.mTvEarfcn.setText(item.NR_EARFCN);
			vh.mTvEarfcn.setTextColor(item.LTE_PD_COLOR);
			vh.mTvEarfcn.setVisibility(View.VISIBLE);
		}else {
			vh.mTvEarfcn.setVisibility(View.GONE);
		}
		//PCI
		if (mArrTag[3]) {
			vh.mTvPci.setText(item.NR_PCI);
			vh.mTvPciMo3.setText(item.mo3);
			vh.mTvPci.setTextColor(item.LTE_PCI_COLOR);
			vh.mTvPciMo3.setTextColor(item.LTE_MO3_COLOR);
			vh.mViewPci.setVisibility(View.VISIBLE);
		}else {
			vh.mViewPci.setVisibility(View.GONE);
		}
		//RSRP
		if (mArrTag[4]) {
			vh.mTvRsrp.setText(item.NR_RSRP);
			vh.mTvRsrp.setTextColor(item.LTE_RSRP_COLOR);
			vh.mTvRsrp.setVisibility(View.VISIBLE);
		}else {
			vh.mTvRsrp.setVisibility(View.GONE);
		}
		//BAND
		if (mArrTag[5]) {
			vh.mTvBand.setText(item.NR_BAND);
			vh.mTvBand.setVisibility(View.VISIBLE);
		}else {
			vh.mTvBand.setVisibility(View.GONE);
		}
		return conterView;
	}

	private class ViewHolder{
		private MarqueesTextView mTvName;
		private TextView mTvCi,mTvEarfcn,mTvPci,mTvPciMo3,mTvRsrp,mTvBand;
		private View mViewPci,mViewName;

		public ViewHolder(View v){
			mTvName   = (MarqueesTextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_cellname);
			mTvCi 	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_ci);
			mTvEarfcn = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_earfcn);
			mTvPci	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_pci);
			mTvRsrp	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_rsrp);
			mTvBand   = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_band);
			mTvPciMo3 = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_pci_mo3);
			mViewPci  = v.findViewById(R.id.jzxhLqAdapter_view_lq_pci);
			mViewName = v.findViewById(R.id.jzxhLqAdapter_tv_lqView_cellname);
		}
	}
}
