package cn.nokia.speedtest5g.wifi.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 扫描结果adapter
 * @author JQJ
 *
 */
public class WifiSdScanAdapter extends BaseAdapter {

	private Activity mActivity = null;
	private ArrayList<ScanResult> mScanResultList = new ArrayList<ScanResult>();
	private LayoutInflater mInflater = null;

	public WifiSdScanAdapter(Activity activity) {
		this.mActivity = activity;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	public void setData(ArrayList<ScanResult> mList){
		mScanResultList.clear();
		mScanResultList.addAll(mList);
		//排序 按dbm  值大放前面
		Collections.sort(mScanResultList, new SortByDbm());

		notifyDataSetChanged();
	}

	private class SortByDbm implements Comparator<ScanResult> {

		@Override
		public int compare(ScanResult sr1, ScanResult sr2) {
			int level1 = sr1.level;
			int level2 = sr2.level;
			if (level1 < level2){
				return 1;
			}else if(level1 == level2){
				return 0;
			}
			return -1;
		}
	}

	@Override
	public int getCount() {
		return mScanResultList == null ? 0 : mScanResultList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position >= getCount()) {
			return null;
		}
		return mScanResultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.jqj_wifi_sd_scan_adapter, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ScanResult scanResult = (ScanResult) getItem(position);
		holder.mTvName.setText(WifiUtil.getInstance().getName(scanResult.SSID));
		holder.mTvDbm.setText(String.valueOf(scanResult.level) + "dBm");
		holder.mProgressBar.setProgress(WifiUtil.getInstance().getProgressValue(scanResult.level));

		return convertView;
	}

	private class ViewHolder{

		private TextView mTvName = null;
		private TextView mTvDbm = null;
		private ProgressBar mProgressBar = null;

		public ViewHolder(View v){
			mTvName = (TextView) v.findViewById(R.id.wifi_sd_scan_adapter_tv_name);
			mTvDbm = (TextView) v.findViewById(R.id.wifi_sd_scan_adapter_tv_dbm);
			mProgressBar = (ProgressBar) v.findViewById(R.id.wifi_sd_scan_adapter_pb_value);
		}
	}
}
