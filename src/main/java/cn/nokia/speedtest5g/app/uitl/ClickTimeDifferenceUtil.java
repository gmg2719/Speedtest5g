package cn.nokia.speedtest5g.app.uitl;

import com.android.volley.util.MyToast;
import android.content.Context;
import android.view.Gravity;

/**
 * 事件点击时间差
 */
public class ClickTimeDifferenceUtil {

	private static ClickTimeDifferenceUtil ctd = null;
	
	public synchronized static ClickTimeDifferenceUtil getInstances(){
		if (ctd == null) {
			ctd = new ClickTimeDifferenceUtil();
		}
		return ctd;
	}
	
	private long mLastClickTime = 0;
	/**
	 * 500毫秒内只能点击一次按钮操作
	 * @return
	 */
	public boolean isClickTo(Context context){
		if (System.currentTimeMillis() - mLastClickTime <= 500) {
			MyToast.getInstance(context).showCommon("操作太快了",Gravity.CENTER);
			return false;
		}
		mLastClickTime = System.currentTimeMillis();
		return true;
	}

	/**
	 * 500毫秒内只能点击一次按钮操作
	 * @return
	 */
	public boolean isClickTo(){
		if (System.currentTimeMillis() - mLastClickTime <= 500) {
			return false;
		}
		mLastClickTime = System.currentTimeMillis();
		return true;
	}
}
