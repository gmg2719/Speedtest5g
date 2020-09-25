package com.android.volley.util;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘
 */
public class InputMethodUtil {

	private static InputMethodUtil imu = null;
	
	public static synchronized InputMethodUtil getInstances(){
		if (imu == null) {
			imu = new InputMethodUtil();
		}
		return imu;
	}
	
	/**
	 * 关闭软键盘
	 * @param ib
	 */
	public void inputMethod(Context context,IBinder ib){
		//关闭软键盘
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);       
	    imm.hideSoftInputFromWindow(ib, 0); 
	}
	
	public void show(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	}
}
