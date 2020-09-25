package cn.nokia.speedtest5g.jzxh.adapter;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
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
 * 最近LTE列表适配器
 * @author zwq
 *
 */
public class JzxhLteListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private List<Signal> mListLte;

	private int mColorWhite,mColorMicroBlue,mLastColor;

	//头标题
	private boolean[] mArrTag = {true,true,true,true,true,true,true,true};

	public JzxhLteListAdapter(LayoutInflater inflater){
		this.mInflater = inflater;
		this.mListLte  = new ArrayList<>();
		this.mColorWhite = ContextCompat.getColor(SpeedTest5g.getContext(), R.color.bg_color);
		this.mColorMicroBlue = ContextCompat.getColor(SpeedTest5g.getContext(), R.color.emergent_bg_blue);
	}

	/**
	 * 更新头标题
	 * @param arrTag
	 */
	public void updateTag(String[] arrTag){
		for (int i = 0; i < arrTag.length; i++) {
			mArrTag[i] = "1".equals(arrTag[i]);
		}
		notifyDataSetChanged();
	}

	public void addLteItem(Signal signal){
		if (mListLte.size() >= 100) {
			mListLte.remove(0);
		}
		if (mListLte.size() <= 0) {
			mLastColor = mColorWhite;
		}else if(!mListLte.get(mListLte.size() - 1).getLte_cgi().equals(signal.getLte_cgi())){
			if (mLastColor == mColorWhite) {
				mLastColor = mColorMicroBlue;
			}else {
				mLastColor = mColorWhite;
			}
		}
		signal.colorBg = mLastColor;
		mListLte.add(signal);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mListLte == null ? 0 : mListLte.size();
	}

	@Override
	public Object getItem(int position) {
		return mListLte.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View conterView, ViewGroup arg2) {
		ViewHolder vh = null;
		if (conterView == null) {
			conterView = mInflater.inflate(R.layout.jzxh_adapter_lte_list, null);
			vh = new ViewHolder(conterView);
			conterView.setTag(vh);
		}else {
			vh = (ViewHolder) conterView.getTag();
		}
		Signal mItem = (Signal)getItem(getCount() - position - 1);
		//time
		if (mArrTag[0]) {
			vh.tvTime.setText(mItem.timeHH);
			vh.tvTime.setVisibility(View.VISIBLE);
		}else {
			vh.tvTime.setVisibility(View.GONE);
		}
		if (mItem.isSA()) {
			//ci
			if (mArrTag[1]) {
				vh.tvCi.setText(mItem.lte_gnb_nr + "-" + mItem.lte_cellid_nr);
				vh.tvCi.setVisibility(View.VISIBLE);
			}else {
				vh.tvCi.setVisibility(View.GONE);
			}
			//rsrp
			if (mArrTag[2]) {
				vh.tvRsrp.setText(mItem.lte_rsrp_nr);
				vh.tvRsrp.setVisibility(View.VISIBLE);
			}else {
				vh.tvRsrp.setVisibility(View.GONE);
			}
			//sinr
			if (mArrTag[3]) {
				vh.tvSinr.setText(mItem.lte_sinr_nr);
				vh.tvSinr.setVisibility(View.VISIBLE);
			}else {
				vh.tvSinr.setVisibility(View.GONE);
			}
			//pci
			if (mArrTag[4]) {
				vh.tvPci.setText(mItem.lte_pci_nr);
				vh.tvPci.setVisibility(View.VISIBLE);
			}else {
				vh.tvPci.setVisibility(View.GONE);
			}
			//band
			if (mArrTag[7]) {
				vh.tvBand.setText(mItem.lte_band_nr);
				vh.tvBand.setVisibility(View.VISIBLE);
			}else {
				vh.tvBand.setVisibility(View.GONE);
			}
		}else {
			//ci
			if (mArrTag[1]) {
				vh.tvCi.setText(mItem.getLte_enb() + "-" + mItem.getLte_cid());
				vh.tvCi.setVisibility(View.VISIBLE);
			}else {
				vh.tvCi.setVisibility(View.GONE);
			}
			//rsrp
			if (mArrTag[2]) {
				vh.tvRsrp.setText(mItem.getLte_rsrp());
				vh.tvRsrp.setVisibility(View.VISIBLE);
			}else {
				vh.tvRsrp.setVisibility(View.GONE);
			}
			//sinr
			if (mArrTag[3]) {
				vh.tvSinr.setText(mItem.getLte_sinr());
				vh.tvSinr.setVisibility(View.VISIBLE);
			}else {
				vh.tvSinr.setVisibility(View.GONE);
			}
			//pci
			if (mArrTag[4]) {
				vh.tvPci.setText(mItem.getLte_pci());
				vh.tvPci.setVisibility(View.VISIBLE);
			}else {
				vh.tvPci.setVisibility(View.GONE);
			}
			//band
			if (mArrTag[7]) {
				vh.tvBand.setText(mItem.getLte_band());
				vh.tvBand.setVisibility(View.VISIBLE);
			}else {
				vh.tvBand.setVisibility(View.GONE);
			}
		}
		//subRsrp
		if (mArrTag[5]) {
			vh.tvRsrp2.setText(mItem.subRsrp);
			vh.tvRsrp2.setVisibility(View.VISIBLE);
		}else {
			vh.tvRsrp2.setVisibility(View.GONE);
		}
		//subSinr
		if (mArrTag[6]) {
			vh.tvSinr2.setText(mItem.subSinr);
			vh.tvSinr2.setVisibility(View.VISIBLE);
		}else {
			vh.tvSinr2.setVisibility(View.GONE);
		}
		conterView.setBackgroundColor(mItem.colorBg);
		return conterView;
	}

	private class ViewHolder{

		MarqueesTextView tvTime;

		TextView tvCi,tvRsrp,tvSinr,tvPci,tvRsrp2,tvSinr2,tvBand;

		public ViewHolder(View v) {
			tvTime 	 = (MarqueesTextView) v.findViewById(R.id.jzxhAdapterLteList_tv_time);
			tvCi 	 = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_ci);
			tvRsrp 	 = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_rsrp);
			tvSinr	 = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_sinr);
			tvPci 	 = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_pci);
			tvRsrp2  = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_rsrp2);
			tvSinr2  = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_sinr2);
			tvBand   = (TextView) v.findViewById(R.id.jzxhAdapterLteList_tv_band);
		}	
	}
}
