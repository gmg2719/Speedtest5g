package cn.nokia.speedtest5g.speedtest.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedRanking;
import cn.nokia.speedtest5g.view.MyCornerImageView;

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
 * 测速排行适配器
 * @author JQJ
 *
 */
public class SpeedRankingAdapter extends BaseAdapter {

	private Activity mActivity = null;
	private List<BeanSpeedRanking> mList = null;
	private LayoutInflater mInflater = null;

	public SpeedRankingAdapter(Activity activity) {
		this.mActivity = activity;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	public void updateData(List<BeanSpeedRanking> list){
		if(list != null){
			this.mList = list;
			notifyDataSetChanged();
		}
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.jqj_speed_ranking_adapter, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BeanSpeedRanking config = (BeanSpeedRanking) getItem(position);
		if(position == 0){
			holder.mIvRank.setImageResource(R.drawable.icon_speed_ranking_one_flag);
			holder.mIvRank.setVisibility(View.VISIBLE);
			holder.mTvRank.setVisibility(View.GONE);
		}else if(position == 1){
			holder.mIvRank.setImageResource(R.drawable.icon_speed_ranking_two_flag);
			holder.mIvRank.setVisibility(View.VISIBLE);
			holder.mTvRank.setVisibility(View.GONE);
		}else if(position == 2){
			holder.mIvRank.setImageResource(R.drawable.icon_speed_ranking_three_flag);
			holder.mIvRank.setVisibility(View.VISIBLE);
			holder.mTvRank.setVisibility(View.GONE);
		}else{
			holder.mIvRank.setVisibility(View.GONE);
			holder.mTvRank.setVisibility(View.VISIBLE);
			holder.mTvRank.setText(String.valueOf(position + 1));
		}

		//头像处理
		if(!TextUtils.isEmpty(config.headAddr)){
			ImageLoader.getInstance().displayImage(config.headAddr, holder.mIvHead, ImageOptionsUtil.getInstances().getOptionsDisk());
		}else{
			//加载本地未登录头像
			holder.mIvHead.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_speed_test_not_login_head));
		}

		holder.mTvOperator.setText(config.operator);
		if(!TextUtils.isEmpty(config.userName) && UtilHandler.getInstance().isMobileNO(config.userName)){
			holder.mTvUserName.setText(UtilHandler.getInstance().hidePhoneNoMid4(config.userName));
		}else{
			holder.mTvUserName.setText(config.userName);
		}
		holder.mTvPhoneModel.setText(config.phoneModel);
		holder.mTvDownSpeedAvg.setText(config.downSpeedAvg);

		return convertView;
	}

	private class ViewHolder{

		private ImageView mIvRank;
		private TextView mTvRank;
		private MyCornerImageView mIvHead;
		private TextView mTvOperator;
		private TextView mTvUserName;
		private TextView mTvPhoneModel;
		private TextView mTvDownSpeedAvg;

		public ViewHolder(View v){
			mTvRank = (TextView) v.findViewById(R.id.speed_ranking_adapter_tv_rank);
			mIvRank = (ImageView) v.findViewById(R.id.speed_ranking_adapter_iv_rank);

			mIvHead = (MyCornerImageView) v.findViewById(R.id.speed_ranking_adapter_iv_head);
			mTvOperator = (TextView) v.findViewById(R.id.speed_ranking_adapter_tv_operator);
			mTvUserName = (TextView) v.findViewById(R.id.speed_ranking_adapter_tv_userName);
			mTvPhoneModel = (TextView) v.findViewById(R.id.speed_ranking_adapter_tv_phoneModel);
			mTvDownSpeedAvg = (TextView) v.findViewById(R.id.speed_ranking_adapter_tv_downSpeedAvg);
		}
	}
}
