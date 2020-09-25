package cn.nokia.speedtest5g.pingtest.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;

/**
 * ping测试结果adapter
 * @author JQJ
 *
 */
public class PingTestResultAdapter extends BaseAdapter {

	private Activity mActivity = null;
	private List<PingInfo> mList = null;
	private LayoutInflater mInflater = null;
	private ListenerBack mListenerBack = null;

	public PingTestResultAdapter(Activity a, List<PingInfo> list, ListenerBack listenerBack) {
		this.mActivity = a;
		this.mList = list;
		this.mInflater = LayoutInflater.from(mActivity);
		this.mListenerBack = listenerBack;
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

	/**
	 * 更新所有数据
	 * @param list
	 */
	public void update(List<PingInfo> list) {
		mList = list;
		notifyDataSetChanged();
	}

	/**
	 * 更新单条数据
	 * @param position
	 */
	public void updateChangedItem(ListView listView, int position, PingInfo pingInfo) {
		if(listView != null && mList != null){
			int firstVisiableIndex = listView.getFirstVisiblePosition();
			int lastVisiableIndex = listView.getLastVisiblePosition();
			for(int i = firstVisiableIndex, j = lastVisiableIndex; i <= j; i++){
				PingInfo item = (PingInfo)listView.getItemAtPosition(i);
				String value1 = pingInfo.getIp();
				if(value1 == null){
					value1 = pingInfo.getDomainName();
				}
				String value2 = item.getIp();
				if(value2 == null){
					value2 = item.getDomainName();
				}

				if(value1 != null){
					if(value1.equals(value2)){ //ip地址一致
						View view = listView.getChildAt(i - firstVisiableIndex);
						mList.set(position, pingInfo);
						this.getView(position, view, listView);
					}
				}
			}
		}
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.jqj_ping_test_result_adapter, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PingInfo pingInfo = (PingInfo) getItem(position);
		holder.mTvName.setText(pingInfo.getName());
		ImageLoader.getInstance().displayImage(pingInfo.imgUrl, holder.mIvHead, 
				ImageOptionsUtil.getInstances().getOptionsDiskForTscs());

		holder.mTvResult.setText(pingInfo.getStrState());

		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.RECTANGLE);
		drawable.setColor(pingInfo.getColorState());
		drawable.setCornerRadius(5);
		holder.mTvResult.setBackground(drawable);

		holder.mTvTime.setText(String.valueOf(pingInfo.getTimes()));

		GradientDrawable drawable2 = new GradientDrawable();
		drawable2.setShape(GradientDrawable.RECTANGLE);
		drawable2.setColor(Color.parseColor("#5787ED"));
		drawable2.setCornerRadius(10);
		holder.mTvRetry.setBackground(drawable2);

		if(pingInfo.status == PingInfo.STATUS_2){ //测一测
			holder.mTvResult.setVisibility(View.GONE);
			holder.mTvTime.setVisibility(View.GONE);
			holder.mIvRefresh.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.GONE);
			holder.mTvRetry.setVisibility(View.VISIBLE);
		}else if(pingInfo.status == PingInfo.STATUS_1){ //正在刷新
			holder.mTvResult.setVisibility(View.GONE);
			holder.mTvTime.setVisibility(View.GONE);
			holder.mIvRefresh.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mTvRetry.setVisibility(View.GONE);
		}else if(pingInfo.status == PingInfo.STATUS_0){ //刷新完成
			holder.mTvResult.setVisibility(View.VISIBLE);
			holder.mTvTime.setVisibility(View.VISIBLE);
			holder.mIvRefresh.setVisibility(View.VISIBLE);
			holder.mProgressBar.setVisibility(View.GONE);
			holder.mTvRetry.setVisibility(View.GONE);
		} 

		holder.setClickListener(position);

		return convertView;
	}

	private class ViewHolder implements View.OnClickListener{

		private ImageView mIvHead,mIvRefresh;
		private TextView mTvResult;
		private TextView mTvName,mTvTime;
		private TextView mTvRetry;
		private ProgressBar mProgressBar;
		private int mPosition;

		public ViewHolder(View v){
			mIvHead = (ImageView) v.findViewById(R.id.pingtest_result_adapter_iv_head);
			mTvName = (TextView) v.findViewById(R.id.pingtest_result_adapter_tv_name);
			mTvResult = (TextView) v.findViewById(R.id.pingtest_result_adapter_tv_result);
			mTvTime = (TextView) v.findViewById(R.id.pingtest_result_adapter_tv_time);
			mIvRefresh = (ImageView) v.findViewById(R.id.pingtest_result_adapter_iv_refresh);
			mTvRetry = (TextView) v.findViewById(R.id.pingtest_result_adapter_tv_retry);
			mProgressBar = (ProgressBar) v.findViewById(R.id.pingtest_result_adapter_pb_refresh);
		}

		public void setClickListener(int position){
			mPosition = position;
			mIvRefresh.setOnClickListener(this);
			mTvRetry.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.pingtest_result_adapter_iv_refresh || id == R.id.pingtest_result_adapter_tv_retry) {
                if (mListenerBack != null) {
                    mListenerBack.onListener(v.getId(), mPosition, true);
                }
            }
		}
	}
}
