package cn.nokia.speedtest5g.wifi.adapter;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.wifi.bean.WifiArpBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 蹭网 扫描结果adapter
 * @author JQJ
 *
 */
public class WifiSnScanAdapter extends BaseAdapter {

	private Activity mActivity = null;
	private ArrayList<WifiArpBean> mList = new ArrayList<WifiArpBean>();
	private LayoutInflater mInflater = null;

	public WifiSnScanAdapter(Activity activity) {
		this.mActivity = activity;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	public void setData(ArrayList<WifiArpBean> list){
		mList.clear();
		mList.addAll(list);

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position >= getCount()) {
			return null;
		}
		return mList.get(position);
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
			convertView = mInflater.inflate(R.layout.jqj_wifi_sn_scan_adapter, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		WifiArpBean wifiArpBean = (WifiArpBean) getItem(position);
		if(wifiArpBean.ip.endsWith(".1")){ //路由
			holder.mTvName.setText("路由");
			holder.mIvType.setImageResource(R.drawable.icon_wifi_sn_route_flag);
		}else{
			if(TextUtils.isEmpty(wifiArpBean.hostName) && TextUtils.isEmpty(wifiArpBean.manufactor)){
				holder.mTvName.setText("未知设备");
				holder.mIvType.setImageResource(R.drawable.icon_wifi_sn_phone_flag);
			}else{
				if(TextUtils.isEmpty(wifiArpBean.hostName)){
					holder.mTvName.setText("移动设备");
					holder.mIvType.setImageResource(R.drawable.icon_wifi_sn_phone_flag);
				}else{
					holder.mTvName.setText(wifiArpBean.hostName);
					holder.mIvType.setImageResource(R.drawable.icon_wifi_sn_computer_flag);
				}
			}
		}

		holder.mTvIp.setText(wifiArpBean.ip);
		holder.mTvManufactor.setText(wifiArpBean.manufactor);
		holder.mTvMac.setText(wifiArpBean.mac);

		return convertView;
	}

	private class ViewHolder{

		private ImageView mIvType = null; //类型
		private TextView mTvName = null; //主机名
		private TextView mTvIp = null; //ip
		private TextView mTvManufactor = null; //厂家
		private TextView mTvMac = null; //mac

		public ViewHolder(View v){
			mIvType = (ImageView) v.findViewById(R.id.wifi_sn_scan_adapter_iv_type);
			mTvName = (TextView) v.findViewById(R.id.wifi_sn_scan_adapter_tv_name);
			mTvIp = (TextView) v.findViewById(R.id.wifi_sn_scan_adapter_tv_ip);
			mTvManufactor = (TextView) v.findViewById(R.id.wifi_sn_scan_adapter_tv_manufactor);
			mTvMac = (TextView) v.findViewById(R.id.wifi_sn_scan_adapter_tv_mac);
		}
	}
}
