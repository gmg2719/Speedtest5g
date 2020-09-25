package cn.nokia.speedtest5g.dialog;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.view.time.WheelHandler;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

/**
 * -SPINNER类型适配器
 * @author zwq
 *
 */
public class WheelOtherStrPopuo {

	private static WheelOtherStrPopuo wop = null;
	
	public static synchronized WheelOtherStrPopuo getInstances(){
		if (wop == null) {
			wop = new WheelOtherStrPopuo();
		}
		return wop;
	}
	
	private ListenerBack mBack;
	private PopupWindow mPopupWindow;
	private View mView;
	private int mType;
	//标题
	private TextView tvTitle;
	private ImageButton btnClear;
	private boolean isPostion;
	private android.view.WindowManager.LayoutParams windowLp;
	
	private Activity mActivity;
	
	/**
	 * 显示选择项popup
	 * @param v 显示在V
	 * @param arr 数据
	 * @param title 标题
	 * @param type 标记
	 */
	public WheelOtherStrPopuo show(Activity activity,View v,String[] arr,String title,int type,ListenerBack listener,boolean isShowClean){
		this.mBack = listener;
		this.mType = type;
		this.mActivity = activity;
		this.isPostion = false;
		if (mPopupWindow == null || mView == null) {
			mView = LayoutInflater.from(activity).inflate(R.layout.view_wheel_other, null);
			tvTitle = (TextView) mView.findViewById(R.id.wheel_title_tv);
			mView.findViewById(R.id.wheel_title_btn).setOnClickListener(clickListener);
			btnClear = (ImageButton) mView.findViewById(R.id.wheel_title_clean);
			btnClear.setOnClickListener(clickListener);
			mPopupWindow = new PopupWindow(mView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(-00000));//点击PopupWindow 外的屏幕，PopupWindow依然会消失
			mPopupWindow.setFocusable(true);
			mPopupWindow.setAnimationStyle(R.style.PopupBottomAnimation);
			mPopupWindow.update();
			mPopupWindow.setOnDismissListener(onDismissListener);
		}
		tvTitle.setText(title);
		btnClear.setVisibility(isShowClean ? View.VISIBLE : View.GONE);
		WheelHandler.getHandler().initOther(mView, arr,false);
		
		windowLp = activity.getWindow().getAttributes();
		windowLp.alpha = 0.7f;
		mActivity.getWindow().setAttributes(windowLp);
		
		mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		return wop;
	}
	
	public void isPostion(){
		isPostion = true;
	}
	
	private OnClickListener clickListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			if (mBack != null) {
				if (arg0.getId() == R.id.wheel_title_clean) {
					mBack.onListener(mType, isPostion ? -1 : "", false);
				}else {
					mBack.onListener(mType,isPostion ? WheelHandler.getHandler().getWheelPosition(R.id.wheel_other) : WheelHandler.getHandler().getWheelValue(R.id.wheel_other), true);
				}
			}
			if (mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		}
	};
	
	private OnDismissListener onDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			try {
				windowLp.alpha = 1f;
				mActivity.getWindow().setAttributes(windowLp);
				if (mBack != null) {
					mBack.onListener(-1, "", true);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
}
