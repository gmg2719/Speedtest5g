package com.android.volley.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 *  描述	:消息提示类
 */
public class MyToast {

	private static MyToast myToast;
	
	private static Toast mToast;
	
	private static Context mContext;
	
	public static synchronized MyToast getInstance(Context context){
		if (myToast == null) {
			myToast = new MyToast();
		}
		mContext = context;
		return myToast;
	}
	
	//显示内容的textview
	private ToastTextView tv_content;
	/**
	 * 默认底部显示
	 * @param text
	 */
	public void showCommon(String text){
		setAttribute(text,Gravity.BOTTOM, 0, 20);
	}
	
	/**
	 * 位置可设置
	 * @param text
	 * @param gravity
	 */
	public void showCommon(String text,int gravity){
		setAttribute(text,gravity, 0, 0);
	}
	
	/**
	 * 显示中间
	 * @param text
	 */
	public void showCommonCenter(String text){
		setAttribute(text,Gravity.CENTER, 0, 0);
	}
	
	/**
	 * 位置可设置
	 * @param text
	 * @param gravity
	 * @param xOffset
	 * @param yOffset
	 */
	public void showCommon(String text,int gravity, int xOffset, int yOffset){
		setAttribute(text,gravity,xOffset,yOffset);
	}

	/**
	 * 设置一些基本属性
	 * @param text
	 */
	private void setAttribute(String text,int gravity, int xOffset, int yOffset) {
		if (mContext == null) {
			return;
		}
		initTo();
		if (tv_content == null) {
			tv_content = new ToastTextView(mContext);
		}
		tv_content.setText(text);
		mToast.setView(tv_content);
		mToast.setGravity(gravity, xOffset, yOffset);
		mToast.show();
	}

	private void initTo(){
		if (mToast != null){
			mToast.cancel();
			mToast = null;
		}
		mToast = new Toast(mContext);
		mToast.setDuration(Toast.LENGTH_LONG);
	}
}
