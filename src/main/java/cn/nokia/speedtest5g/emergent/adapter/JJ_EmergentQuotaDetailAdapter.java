package cn.nokia.speedtest5g.emergent.adapter;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellDetailInfo;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.HorizontalProgressBarWithNumber;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 应急站详情适配器
 * @author xujianjun
 *
 */
public class JJ_EmergentQuotaDetailAdapter extends BaseAdapter{
	private Activity mActivity;
	private LayoutInflater mInflater;
	private List<JJ_EmergentQuotaCellDetailInfo> mList;
	private double max;
	// 0 RRC建立成功数 1RRC连接建立成功率（%）2E-RAB建立请求数 3E-RAB建立成功率（%）
	// 4 无限掉线率（%）5 E-RAB掉线率（%） 6切换成功率(含ENB内和ENB间）
	// 7小区用户面上行字节数（Kbyte）8上行用户平均速率（Mbytes） 9小区用户面下行字节数（Kbyte）
	// 10下行用户平均速率（Mbytes）11VOLTE上行丢包率（%） 12上行RTP丢包数（万个）
	// 13最大RRC连接数 14上行PRB利用率（%）15下行PRB利用率（%）
	private int mType;//指标类型
	
	public JJ_EmergentQuotaDetailAdapter(Activity a,List<JJ_EmergentQuotaCellDetailInfo> list,int type,int max) {
		// TODO Auto-generated constructor stub
		this.mActivity = a;
		this.mInflater = LayoutInflater.from(mActivity);
		this.mList = list;
		this.mType = type;
		this.max = max;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList==null?0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder =null;
		if(convertView ==null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.jj_adapter_emergent_quota_detail, null);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_emergent_quota_detail_item_time);
			holder.tvValue = (TextView) convertView.findViewById(R.id.tv_emergent_quota_detail_item_value);
			holder.hpnValue = (HorizontalProgressBarWithNumber) convertView.findViewById(R.id.hpro_emergent_quota_detail_item_value);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		String time = TimeUtil.getInstance().getToTime(TimeUtil.getInstance().stringToLong(mList.get(position).getPmCellTime()),"HH");
		int time2 = UtilHandler.getInstance().toInt(time, 0)+1;
		holder.tvTime.setText(time+"点-"+(time2<10?"0"+time2:time2)+"点");
		
		String value ="";
		// 0 RRC建立成功数 1RRC连接建立成功率（%）2E-RAB建立请求数 3E-RAB建立成功率（%）
		// 4 无限掉线率（%）5 E-RAB掉线率（%） 6切换成功率(含ENB内和ENB间）
		// 7小区用户面上行字节数（Kbyte）8上行用户平均速率（Mbytes） 9小区用户面下行字节数（Kbyte）
		// 10下行用户平均速率（Mbytes）11VOLTE上行丢包率（%） 12上行RTP丢包数（万个）
		// 13最大RRC连接数 14上行PRB利用率（%）15下行PRB利用率（%）
		double rate;
		switch (mType) {
		case 0:
			value =mList.get(position).getEnbha06();
			break;
		case 1:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0101(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 2:
			value =mList.get(position).getEnbhb05();
			break;
		case 3:
			rate =UtilHandler.getInstance().toDouble(mList.get(position).getEu0102(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 4:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0223(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 5:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0202(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 6:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0306(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 7:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0505(), 0);
			value =(long)rate+ "";
			break;
		case 8:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0535(), 0);
			value =UtilHandler.getInstance().toDfStr(rate, "00") + "";
			break;
		case 9:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0506(), 0);
			value =(long)rate+ "";
			break;
		case 10:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0536(), 0);
			value =UtilHandler.getInstance().toDfStr(rate, "00") + "";
			break;
		case 11:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0416(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 12:
			value =mList.get(position).getEnbhh061();
			break;
		case 13:
			value =mList.get(position).getEnbha04();
			break;
		case 14:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0529(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		case 15:
			rate = UtilHandler.getInstance().toDouble(mList.get(position).getEu0530(), 0);
			value =UtilHandler.getInstance().toDfStr(rate>100?100:rate, "00") + "";
			break;
		default:
			break;
		}
		holder.tvValue.setText(value);
		int hpnValue = (int) ((UtilHandler.getInstance().toDouble(value, 0)/max)*100);
		holder.hpnValue.setProgress(hpnValue==100?99:hpnValue);
		
		return convertView;
	}
	
	private class Holder{
		private TextView tvTime;
		private TextView tvValue;
		private HorizontalProgressBarWithNumber hpnValue;
	}

}
