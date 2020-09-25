package cn.nokia.speedtest5g.speedtest.util;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 
 * @author JQJ
 *
 */
public class SpeedTestMenuPopup {

	private PopupWindow mPopup;
	private View mPopupView;
	//按钮事件返回回调事件
	private ListenerBack mListener;
	private android.view.WindowManager.LayoutParams windowLp;
	private Activity mActivity;

	/**
	 * @param activity
	 * @param v
	 * @param listener
	 */
	@SuppressLint("InflateParams") 
	public void show(Activity activity,View v,ListenerBack listener){
		this.mListener = listener;
		this.mActivity = activity;
		if (mPopup == null || mPopupView == null) {
			mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.jqj_speed_test_menu_popup, null);
			mPopupView.findViewById(R.id.speed_test_popup_btn_pz).setOnClickListener(clickListener);
			mPopupView.findViewById(R.id.speed_test_popup_btn_xc).setOnClickListener(clickListener);
			mPopupView.findViewById(R.id.popup_camear_tv_cancel).setOnClickListener(clickListener);

			mPopup = new PopupWindow(mPopupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mPopup.setBackgroundDrawable(new ColorDrawable(0x90000000));//点击PopupWindow 外的屏幕，PopupWindow依然会消失
			mPopup.setFocusable(true);
			mPopup.update();
			mPopup.setAnimationStyle(R.style.PopupBottomAnimation);
			mPopup.setOnDismissListener(onDismissListener);
		}

		windowLp = activity.getWindow().getAttributes();
		windowLp.alpha = 0.6f;
		mActivity.getWindow().setAttributes(windowLp);
		mPopup.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onListener(v.getId(), "", true);
			}
			if (mPopup != null && mPopup.isShowing()) {
				mPopup.dismiss();
			}
		}
	};

	private OnDismissListener onDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss() {
			try {
				windowLp.alpha = 1f;
				mActivity.getWindow().setAttributes(windowLp);
			} catch (Exception e) {
			}
		}
	};
}
