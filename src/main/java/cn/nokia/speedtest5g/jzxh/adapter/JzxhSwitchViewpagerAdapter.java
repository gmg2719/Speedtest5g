package cn.nokia.speedtest5g.jzxh.adapter;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.util.MarqueesTextView;

import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.jzxh.util.JzxhSwitchInfoPopup;
import cn.nokia.speedtest5g.jzxh.util.JzxhSwitchInfoPopup.SwitchJzxhBean;

/**
 * 小区切换详情适配器
 * @author zwq
 *
 */
public class JzxhSwitchViewpagerAdapter extends PagerAdapter {
	
	private LayoutInflater mInflater;
	
	private ListenerBack mListenerBack;
	
	public JzxhSwitchViewpagerAdapter(LayoutInflater inflater,ListenerBack listenerBack){
		this.mInflater = inflater;
		this.mListenerBack = listenerBack;
	}

	private List<SwitchJzxhBean> mArrList;
	
	private HashMap<Integer, View> mMapViewList = new HashMap<>();
	
	public void setData(List<SwitchJzxhBean> arrList){
		this.mArrList = arrList;
		this.mMapViewList.clear();
		notifyDataSetChanged();
	}
	
	public void updateItemDataNoNotify(SwitchJzxhBean switchJzxhBean,int position){
		if (getCount() > position) {
			mArrList.get(position).signalNew = switchJzxhBean.signalNew;
			mArrList.get(position).signalOld = switchJzxhBean.signalOld;
		}
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return mArrList == null ? 0 : mArrList.size();
	}

	@SuppressLint("InflateParams") 
	@Override
	public Object instantiateItem(ViewGroup viewGroup, int position) {
		View convertView = mMapViewList.get(position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.jzxh_adapter_switch, null);
			mMapViewList.put(position, convertView);
			viewGroup.addView(convertView);
		}
		//源小区CI，源小区名称，目标小区CI，目标小区名称,目标小区时间
		TextView mTvCiOld   = (TextView) convertView.findViewById(R.id.jzxhSwitch_tv_ciOld);
		MarqueesTextView mTvCellOld = (MarqueesTextView) convertView.findViewById(R.id.jzxhSwitch_tv_cellOld);
		TextView mTvCiNew   = (TextView) convertView.findViewById(R.id.jzxhSwitch_tv_ciNew);
		MarqueesTextView mTvCellNew = (MarqueesTextView) convertView.findViewById(R.id.jzxhSwitch_tv_cellNew);
		TextView mTvTimeNew = (TextView) convertView.findViewById(R.id.jzxhSwitch_tv_timeNew);
		
		SwitchJzxhBean item = mArrList.get(position);
		mTvCiOld.setText("源小区：" + item.signalOld.getLte_enb() + "-" + item.signalOld.getLte_cid());
		mTvCellOld.setText(item.signalOld.getLte_name());
		mTvCellOld.setVisibility(TextUtils.isEmpty(item.signalOld.getLte_name()) ? View.GONE : View.VISIBLE);
		mTvCiNew.setText("目标小区：" + item.signalNew.getLte_enb() + "-" + item.signalNew.getLte_cid());
		mTvCellNew.setText(item.signalNew.getLte_name());
		mTvCellNew.setVisibility(TextUtils.isEmpty(item.signalNew.getLte_name()) ? View.GONE : View.VISIBLE);
		mTvTimeNew.setText(item.signalNew.getTime());
		
		View mViewClick = convertView.findViewById(R.id.jzxhSwitch_btn_detailsNew);
		mViewClick.setTag(R.id.idPosition, position);
		mViewClick.setOnClickListener(clickListener);
		mViewClick = convertView.findViewById(R.id.jzxhSwitch_btn_detailsOld);
		mViewClick.setTag(R.id.idPosition, position);
		mViewClick.setOnClickListener(clickListener);
		
		return convertView;
	}

	// PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
		mMapViewList.remove(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (JzxhSwitchInfoPopup.getInstance().mListener != null) {
				SwitchJzxhBean mItem = mArrList.get((Integer)v.getTag(R.id.idPosition));
				mListenerBack.onListener(EnumRequest.DIALOG_TOAST_BTN_ONE.toInt(), v.getId() == R.id.jzxhSwitch_btn_detailsOld ? mItem.signalOld : mItem.signalNew, true);
			}
		}
	};
}
