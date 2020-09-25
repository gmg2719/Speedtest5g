package cn.nokia.speedtest5g.dialog;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

@SuppressLint("InflateParams")
public class PopHeightMoreUtil {

	private static PopHeightMoreUtil popUtil;

	private View mView;
	private PopupWindow mPopupWindow = null;
	private ListenerBack mListener;

	public static synchronized PopHeightMoreUtil getInstances() {
		if (popUtil == null) {
			popUtil = new PopHeightMoreUtil();
		}
		return popUtil;
	}

	public void show(Context context, View v, ListenerBack listener, int xoff, int yoff) {
		this.mListener = listener;
		if (mPopupWindow == null || mView == null) {
			mView = LayoutInflater.from(context).inflate(R.layout.xjj_popup_menu, null);
			mView.findViewById(R.id.tv_guide).setOnClickListener(clickListener);
			mView.findViewById(R.id.tv_about).setOnClickListener(clickListener);

			mPopupWindow = new PopupWindow(mView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(-00000));// 点击PopupWindow
																			// 外的屏幕，PopupWindow依然会消失
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();
			mPopupWindow.setAnimationStyle(R.style.PopupTitleAnimation);
		}
		mPopupWindow.showAsDropDown(v, xoff, yoff);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onListener(v.getId(), "", true);
			}
			if (mPopupWindow != null || mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		}
	};

}
