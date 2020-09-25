package cn.nokia.speedtest5g.wifi.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * WIFI列表适配器
 * @author zwq
 *
 */
public class WifiListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	
	public List<ScanResult> mArrList;
	
	private int colorT = Color.parseColor("#24272C");
	
	public WifiListAdapter(LayoutInflater inflater){
		this.mInflater = inflater;
	}
	
	public void setData(List<ScanResult> list){
        this.mArrList = list;
        if (getCount() > 0){
            Collections.sort(mArrList, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult o1, ScanResult o2) {
                    if (o1.level < o2.level) {
                        return 1;
                    }else if (o1.level > o2.level) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArrList == null ? 0 : mArrList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArrList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View contenView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (contenView == null) {
			contenView = mInflater.inflate(R.layout.wifi_adapter_list, null);
			viewHolder = new ViewHolder(contenView);
			contenView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) contenView.getTag();
		}
		contenView.setBackgroundColor(position%2 == 0 ? colorT : Color.TRANSPARENT);
		ScanResult item = mArrList.get(position);
        viewHolder.mTvName.setText(item.SSID);
        viewHolder.mTvMac.setText(item.BSSID);
        viewHolder.mTvDbm.setText(String.valueOf(item.level));
        viewHolder.mTvXd.setText(String.valueOf(WifiUtil.getInstance().getChannelByFrequency(item.frequency)[0]));
        viewHolder.mTvBand.setText(String.valueOf(item.frequency));
		return contenView;
	}

	private class ViewHolder{

        private TextView mTvName,mTvMac,mTvDbm,mTvXd,mTvBand;

        public ViewHolder(View itemView) {
            this.mTvName = (TextView) itemView.findViewById(R.id.wifiAdapter_tv_name);
            this.mTvMac  = (TextView) itemView.findViewById(R.id.wifiAdapter_tv_mac);
            this.mTvDbm  = (TextView) itemView.findViewById(R.id.wifiAdapter_tv_dbm);
            this.mTvBand = (TextView) itemView.findViewById(R.id.wifiAdapter_tv_band);
            this.mTvXd   = (TextView) itemView.findViewById(R.id.wifiAdapter_tv_xd);
        }
    }
}
