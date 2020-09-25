package cn.nokia.speedtest5g.app.thread;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.MainHomeSuperActivity;
import cn.nokia.speedtest5g.app.respon.LoginHeartbeat;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

/**
 * 计时退出登录
 * @author zwq
 *
 */
public class MyLoginExitThread{

	//当前操作的时间
	public static long nowTouchTime,nowTime;

	public static String mClassNameTouch = "";

	private static Activity mNowActivity;
	//是否标记为不限时退出
	public static boolean isNotTagExit = false;

	public static void start(Activity activity,String className){
		mClassNameTouch = className;
		if (activity != null && !activity.isFinishing()) {
			activity.startService(new Intent("com.action.cwwyb.single"));
		}
	}

	public static void stop(){
		if (SpeedTest5g.getContext() != null) {
			SpeedTest5g.getContext().stopService(new Intent("com.action.cwwyb.single"));
		}
		NetWorkUtilNow.getInstances().clearToIp();
	}

	public static void setNewonResume(Activity activity){
		mNowActivity = activity;
	}

	public static void setNewTouch(String className){
		nowTouchTime = System.currentTimeMillis();
		mClassNameTouch = className;
	}

	/**
	 * 是否标记为不限时退出
	 * @param notTagExit true不限时  false限时
	 */
	public static void setNotTatExit(boolean notTagExit){
		nowTouchTime = System.currentTimeMillis();
		isNotTagExit =  notTagExit;
	}

	/**
	 * 跳转到登录页面
	 * @param heartbeat 不为空代表已经被其他手机登录或其他  null未任何操作超时
	 */
	public static void toGo(LoginHeartbeat heartbeat){
		toGo(heartbeat, -1);
	}

	/**
	 * 跳转到登录页面
	 * @param heartbeat 不为空代表已经被其他手机登录或其他  null未任何操作超时
	 * @param toType -1默认 0跳转到登录页面
	 */
	public static void toGo(final LoginHeartbeat heartbeat,final int toType){
		if (mNowActivity != null && !mNowActivity.isFinishing()) {
			mNowActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Intent mMainIntent;
					if (toType == -1) {
						mMainIntent = new Intent(mNowActivity,MainHomeSuperActivity.class);
						mMainIntent.putExtra(TypeKey.getInstance().IS_GO_LOGIN, true);
						if (heartbeat != null) {
							mMainIntent.putExtra(TypeKey.getInstance().IS_EXIT_USER_STATUS,UtilHandler.getInstance().toInt(heartbeat.status,0));
						}else {
							mMainIntent.putExtra(TypeKey.getInstance().IS_EXIT_USER_STATUS,-1);
						}
						mNowActivity.startActivity(mMainIntent);
					}else {
						//						mMainIntent = new Intent(mNowActivity,LoginActivity.class);
					}
				}
			});
		}else if (SpeedTest5g.getContext() != null && Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
			Intent mMainIntent;
			if (toType == -1) {
				mMainIntent = new Intent(SpeedTest5g.getContext(),MainHomeSuperActivity.class);
				mMainIntent.putExtra(TypeKey.getInstance().IS_GO_LOGIN, true);
				if (heartbeat != null) {
					mMainIntent.putExtra(TypeKey.getInstance().IS_EXIT_USER_STATUS,UtilHandler.getInstance().toInt(heartbeat.status,0));
				}else {
					mMainIntent.putExtra(TypeKey.getInstance().IS_EXIT_USER_STATUS,-1);
				}
				SpeedTest5g.getContext().startActivity(mMainIntent);
			}else {
				//				mMainIntent = new Intent(MyApplication.getContext(),LoginActivity.class);
			}
		}
	}
}
