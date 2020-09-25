package cn.nokia.speedtest5g.jzxh.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import org.xclcharts.chart.PointD;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.jzxh.adapter.JzxhSwitchViewpagerAdapter;

/**
 * 基站信号-小区切换详情对话框
 * @author zwq
 *
 */
public class JzxhSwitchInfoPopup implements ViewPager.OnPageChangeListener {
	
	private static JzxhSwitchInfoPopup rp = null;
	
	public static synchronized JzxhSwitchInfoPopup getInstance(){
		synchronized (JzxhSwitchInfoPopup.class) {
			if (rp == null) {
				rp = new JzxhSwitchInfoPopup();
			}
			return rp;
		}
	}
	
	public boolean isShowing(){
		return mPopup != null && mPopup.isShowing();
	}
	
	private PopupWindow mPopup;
	
	private View mPopupView;
	
	//按钮事件返回回调事件
	public ListenerBack mListener;
	
	private android.view.WindowManager.LayoutParams windowLp;
	
	private FragmentActivity mActivity;
	
	public PointD mClickPointDRsrp,mClickPointDSinr;
	
	private ViewPager mViewPager;
	//适配器
	private JzxhSwitchViewpagerAdapter mAdapter;
	
	private List<SwitchJzxhBean> mListBean;
	//左箭头，右箭头
	private View mViewArrowLeft,mViewArrowRight;
	
	/**
	 * @param activity
	 * @param v
	 * @param arrSignal 总数据
	 * @param clickPosition 当前点击的游标
	 * @param listener
	 */
	@SuppressLint("InflateParams") 
	public void show(FragmentActivity activity,final View v,List<Signal> arrSignal,int clickPosition,ListenerBack listener){
		this.mListener   = listener;
		this.mActivity   = activity;
		this.mListBean   = new ArrayList<>();
		if (mPopup == null || mPopupView == null) {
			mPopupView 	    = LayoutInflater.from(mActivity).inflate(R.layout.jzxh_popup_switch, null);
			mViewPager 	    = (ViewPager) mPopupView.findViewById(R.id.jzxhSwitchPopup_viewPager);
			mViewArrowLeft  = mPopupView.findViewById(R.id.jzxhSwitchPopup_iv_left);;
			mViewArrowRight = mPopupView.findViewById(R.id.jzxhSwitchPopup_iv_right);;
			mAdapter   		= new JzxhSwitchViewpagerAdapter(mActivity.getLayoutInflater(),listener);
			mPopup 	   		= new PopupWindow(mPopupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mPopup.setBackgroundDrawable(new ColorDrawable(0x00000000));//点击PopupWindow 外的屏幕，PopupWindow依然会消失
			mPopup.setFocusable(true);
			mPopup.update();
			mPopup.setAnimationStyle(R.style.PopupBottomAnimation);
			mPopup.setOnDismissListener(onDismissListener);
			mViewPager.setAdapter(mAdapter);
			mViewPager.addOnPageChangeListener(this);
		}
		for (int i = 1; i < arrSignal.size(); i++) {
			mListBean.add(new SwitchJzxhBean(arrSignal.get(i - 1), arrSignal.get(i)));
		}
		showArrow(clickPosition);
		mAdapter.setData(mListBean);
		mViewPager.setCurrentItem(clickPosition);
		windowLp = activity.getWindow().getAttributes();
		windowLp.alpha = 0.6f;
		mActivity.getWindow().setAttributes(windowLp);
		mPopup.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}
	
	public void addItem(Signal signalOld,Signal signalNew){
		mListBean.add(new SwitchJzxhBean(signalOld, signalNew));
		mAdapter.setData(mListBean);
		showArrow(mViewPager.getCurrentItem());
	}
	
	public void updateItemName(Signal signal){
		if (mListBean != null && mListBean.size() > 0) {
			for (int i = 0; i < mListBean.size(); i++) {
				if (mListBean.get(i).signalNew.getLte_cgi().equals(signal.getLte_cgi())) {
					mListBean.get(i).signalNew.setLte_name(signal.getLte_name());
					mListBean.get(i).signalNew.DB_ID = signal.DB_ID;
				}else if (mListBean.get(i).signalOld.getLte_cgi().equals(signal.getLte_cgi())) {
					mListBean.get(i).signalOld.setLte_name(signal.getLte_name());
					mListBean.get(i).signalOld.DB_ID = signal.DB_ID;
					mAdapter.updateItemDataNoNotify(mListBean.get(i),i);
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private int mLastToPosition;
	//设置是否有左右箭头
	private void showArrow(int position){
		this.mLastToPosition = position;
		mViewArrowLeft.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
		mViewArrowRight.setVisibility(position >= mListBean.size() - 1 ? View.INVISIBLE : View.VISIBLE);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		if (mLastToPosition != position) {
			mListener.onListener(EnumRequest.DIALOG_TOAST_BTN_THREE.toInt(),  new Integer[]{mListBean.get(position).signalNew.chartRsrpHashCode,mListBean.get(position).signalNew.chartSinrHashCode} , true);
			showArrow(position);
		}
	}
	
	private OnDismissListener onDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			try {
				windowLp.alpha = 1f;
				mActivity.getWindow().setAttributes(windowLp);
				if (mListener != null) {
					mListener.onListener(EnumRequest.DIALOG_TOAST_BTN_TWO.toInt(),  null , true);
				}
				mClickPointDRsrp = null;
				mClickPointDSinr = null;
			} catch (Exception e) {
			}
		}
	};
	
	public class SwitchJzxhBean{
			
		public Signal signalOld;
		public Signal signalNew;
		
		public SwitchJzxhBean(Signal signalOld,Signal signalNew){
			this.signalNew = signalNew;
			this.signalOld = signalOld;
		}
	}
}
