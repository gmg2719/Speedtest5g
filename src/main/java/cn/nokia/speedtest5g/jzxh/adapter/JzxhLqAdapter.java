package cn.nokia.speedtest5g.jzxh.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.util.MarqueesTextView;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.gis.activity.LayerDetailActivity;
import cn.nokia.speedtest5g.gis.activity.LayerDetailGsmActivity;
import cn.nokia.speedtest5g.gis.model.WqGisLayer;
import cn.nokia.speedtest5g.gis.model.WqGisLayerInfo;
import cn.nokia.speedtest5g.gis.util.WQ_GisDbHandler;
import cn.nokia.speedtest5g.jzxh.util.SignalAdjItem;

/**
 * 邻区列表适配器
 * @author zwq
 *
 */
public class JzxhLqAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	//当前邻区类型  0LET 1TD 2GSM
	private int mLqNetType = 0;
	//邻区数据
	private ArrayList<SignalAdjItem> mArrAdjList;
	//标签
	private boolean[] mArrTag = {true,true,true,true,true,true,true,true};
	//白色，浅蓝色
	private int mColorWhite,mColorBuleSmall;
	
	private Activity mActivity;
	
	public JzxhLqAdapter(LayoutInflater inflater,Activity activity){
		this.mInflater = inflater;
		this.mActivity = activity;
		this.mColorWhite = ContextCompat.getColor(SpeedTest5g.getContext(), R.color.bg_color);
		this.mColorBuleSmall = ContextCompat.getColor(SpeedTest5g.getContext(), R.color.bg_color);
	}
	
	/**
	 * 设置当前邻区数据
	 * @param arrAdj 
	 * @param netType 0LET 1TD 2GSM
	 */
	public void setData(ArrayList<SignalAdjItem> arrAdj,int netType){
		this.mArrAdjList = arrAdj;
		this.mLqNetType  = netType;
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
			if (!vh.mTvName.getText().toString().equals(item.cellName)) {
				vh.mTvName.setText(item.cellName);
				vh.mViewName.setVisibility(View.VISIBLE);
			}
		}else {
			vh.mViewName.setVisibility(View.GONE);
		}
		//CI
		if (mArrTag[1]) {
			if (mLqNetType == 0) {
				if ("-".equals(item.LTE_ENB) && "-".equals(item.LTE_CELLID)) {
					vh.mTvCi.setText("-");
				}else {
					vh.mTvCi.setText(item.LTE_ENB + "-" + item.LTE_CELLID);
				}
			}else if (mLqNetType == 1) {
				vh.mTvCi.setText(item.TD_CI);
			}else {
				vh.mTvCi.setText(item.GSM_CID);
			}
			vh.mTvCi.setVisibility(View.VISIBLE);
		}else {
			vh.mTvCi.setVisibility(View.GONE);
		}
		//频点
		if (mArrTag[2] && mLqNetType == 0) {
			vh.mTvEarfcn.setText(item.LTE_PD);
			vh.mTvEarfcn.setTextColor(item.LTE_PD_COLOR);
			vh.mTvEarfcn.setVisibility(View.VISIBLE);
		}else {
			vh.mTvEarfcn.setVisibility(View.GONE);
		}
		//PCI
		if (mArrTag[3] && mLqNetType == 0) {
			vh.mTvPci.setText(item.LTE_PCI);
			vh.mTvPciMo3.setText(item.mo3);
			vh.mTvPci.setTextColor(item.LTE_PCI_COLOR);
			vh.mTvPciMo3.setTextColor(item.LTE_MO3_COLOR);
			vh.mViewPci.setVisibility(View.VISIBLE);
		}else {
			vh.mViewPci.setVisibility(View.GONE);
		}
		//RSRP
		if (mArrTag[4] && mLqNetType == 0) {
			vh.mTvRsrp.setText(item.LTE_RSRP);
			vh.mTvRsrp.setTextColor(item.LTE_RSRP_COLOR);
			vh.mTvRsrp.setVisibility(View.VISIBLE);
		}else {
			vh.mTvRsrp.setVisibility(View.GONE);
		}
		//RXLEV
		if (mArrTag[5] && mLqNetType != 0) {
			if (mLqNetType == 1) {
				vh.mTvRxlev.setText(item.TD_PCCPCH);
				vh.mTvRxlev.setTextColor(item.TD_PCCPCH_COLOR);
			}else {
				vh.mTvRxlev.setText(item.GSM_RXLEV);
				vh.mTvRxlev.setTextColor(item.GSM_RXLEV_COLOR);
			}
			vh.mTvRxlev.setVisibility(View.VISIBLE);
		}else {
			vh.mTvRxlev.setVisibility(View.GONE);
		}
		//LAC
		if (mArrTag[6] && mLqNetType != 0) {
			vh.mTvLac.setText(mLqNetType == 1 ? item.TD_LAC : item.GSM_LAC);
			vh.mTvLac.setVisibility(View.VISIBLE);
		}else {
			vh.mTvLac.setVisibility(View.GONE);
		}
		//BAND
		if (mArrTag[7] && mLqNetType == 0) {
			vh.mTvBand.setText(item.LTE_BAND);
			vh.mTvBand.setVisibility(View.VISIBLE);
		}else {
			vh.mTvBand.setVisibility(View.GONE);
		}
		vh.setListener(position);
		conterView.setBackgroundColor(position%2 == 0 ? mColorWhite : mColorBuleSmall);
		return conterView;
	}
	
	private class ViewHolder implements OnClickListener{
		private MarqueesTextView mTvName;
		private TextView mTvCi,mTvEarfcn,mTvPci,mTvPciMo3,mTvRsrp,mTvRxlev,mTvLac,mTvBand;
		private View mView,mViewPci,mViewName;
		
		public ViewHolder(View v){
			mTvName   = (MarqueesTextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_cellname);
			mTvCi 	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_ci);
			mTvEarfcn = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_earfcn);
			mTvPci	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_pci);
			mTvRsrp	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_rsrp);
			mTvRxlev  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_rxlev);
			mTvLac 	  = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_lac);
			mTvBand   = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_band);
			mTvPciMo3 = (TextView) v.findViewById(R.id.jzxhLqAdapter_tv_lq_pci_mo3);
			mViewPci  = v.findViewById(R.id.jzxhLqAdapter_view_lq_pci);
			mViewName = v.findViewById(R.id.jzxhLqAdapter_tv_lqView_cellname);
			mView	  = v;
		}
		
		public void setListener(int position){
			mView.setTag(R.id.idPosition, position);
			mView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			SignalAdjItem item = (SignalAdjItem) getItem((int) v.getTag(R.id.idPosition));
			if (item.DB_ID > 0 && !TextUtils.isEmpty(item.cellName) && !"-".equals(item.cellName)) {
				WqGisLayerInfo mWqGisLayerInfo = new WqGisLayerInfo();
				mWqGisLayerInfo.setList(new ArrayList<WqGisLayer>());
				WqGisLayer mWqGisLayer = new WqGisLayer();
				mWqGisLayer.set_id(item.DB_ID);
				if (mLqNetType == 0) {
					mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_LTE_CELL());
					mWqGisLayer.setType(2);
				}else if (mLqNetType == 2) {
					mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_GSM_CELL());
					mWqGisLayer.setType(0);
				}else {
					mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_TDS_CELL());
					mWqGisLayer.setType(1);
				}
				mWqGisLayerInfo.getList().add(mWqGisLayer);
				Intent intent = new Intent(mActivity, mLqNetType == 0 ? LayerDetailActivity.class : LayerDetailGsmActivity.class);
				intent.putExtra("data", mWqGisLayerInfo);
				mActivity.startActivity(intent);
			}
		}
	}
}
