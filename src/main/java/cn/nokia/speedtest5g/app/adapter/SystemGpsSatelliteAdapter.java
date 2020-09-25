package cn.nokia.speedtest5g.app.adapter;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.view.HorizontalProgressBarWithNumber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.GpsSatellite;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 卫星信息适配器
 * @author xujianjun
 *
 */
public class SystemGpsSatelliteAdapter extends BaseAdapter{
	private Activity mActivity;
	private LayoutInflater mInflater;
	private List<GpsSatellite> mList;

	public SystemGpsSatelliteAdapter(Activity a) {
		this.mActivity =a;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	@Override
	public int getCount() {
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(List<GpsSatellite> list){
		this.mList = list;
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView ==null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.jqj_system_gps_satellite_adapter, null);
			holder.tvAzimuth = (TextView) convertView.findViewById(R.id.tv_system_gps_satellite_azimuth);
			holder.tvAltitude = (TextView) convertView.findViewById(R.id.tv_system_gps_satellite_altitude);
			holder.tvSnr = (TextView) convertView.findViewById(R.id.tv_system_gps_satellite_snr);
			holder.hProSnr = (HorizontalProgressBarWithNumber) convertView.findViewById(R.id.hpro_system_gps_satellite_snr);
			holder.tvUse = (TextView) convertView.findViewById(R.id.tv_system_gps_satellite_use);

			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		//		if(position%2==0){
		//			convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
		//		}else{
		//			convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.ui_bg_body));
		//		}

		if(mList.get(position).usedInFix()){
			holder.tvAzimuth.setTextColor(mActivity.getResources().getColor(R.color.ui_bg_blue));
			holder.tvAltitude.setTextColor(mActivity.getResources().getColor(R.color.ui_bg_blue));
			holder.tvUse.setTextColor(mActivity.getResources().getColor(R.color.ui_bg_blue));
			holder.hProSnr.setBackgroundColor(mActivity.getResources().getColor(R.color.module_bg_color));
			holder.hProSnr.setTextColor(mActivity.getResources().getColor(R.color.system_blue),mActivity.getResources().getColor(R.color.module_bg_color));
		}else{
			holder.tvAzimuth.setTextColor(mActivity.getResources().getColor(R.color.gray_c0c0c3));
			holder.tvAltitude.setTextColor(mActivity.getResources().getColor(R.color.gray_c0c0c3));
			holder.tvUse.setTextColor(mActivity.getResources().getColor(R.color.gray_c0c0c3));
			holder.hProSnr.setBackgroundColor(mActivity.getResources().getColor(R.color.module_bg_color));
			holder.hProSnr.setTextColor(mActivity.getResources().getColor(R.color.gray_c0c0c3),mActivity.getResources().getColor(R.color.module_bg_color));
		}
		holder.tvAzimuth.setText((int)mList.get(position).getAzimuth()+"°");
		holder.tvAltitude.setText((int)mList.get(position).getElevation()+"°");
		holder.tvSnr.setText(mList.get(position).getSnr()+"");
		holder.tvUse.setText(mList.get(position).usedInFix()?"√":"-");
		holder.hProSnr.setProgress((int)mList.get(position).getSnr());

		return convertView;
	}

	private class Holder{
		private TextView tvAzimuth;
		private TextView tvAltitude;
		private TextView tvSnr;
		private HorizontalProgressBarWithNumber hProSnr;
		private TextView tvUse;
	}

}
