package cn.nokia.speedtest5g.app.uitl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerManagerUtil {
	
	private static PowerManager mPowerManager;
	
	private static WakeLock mWakeLock = null;
	/**
	 * 保持不休眠状态
	 * @param activity
	 */
	public static void acquireWakeLock(Activity activity){
		mPowerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,activity.getClass().getName());
		mWakeLock.acquire();
	}
	
	/**
	 * 保持不休眠状态
	 */
	public static void acquireWakeLock(Application application){
		mPowerManager = (PowerManager) application.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,application.getClass().getName());
		mWakeLock.acquire();
	}
	
	/**
	 * 恢复正常休眠状态
	 */
	public static void releaseWakeLock(){
		if (mWakeLock != null) {
			try {
				mWakeLock.release();
			} catch (Exception e) {
				
			}
		}
	}
	
	/**
	 * 判断屏幕是否亮起
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isScreenOn(Context context){
		if(mPowerManager==null){
			mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		}
		return mPowerManager.isScreenOn();
	}
	

}
