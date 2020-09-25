package cn.nokia.speedtest5g.app.uitl;

import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * 常用的输入监听回调类
 * @author zwq
 */
public class MyTextWatcherCommon implements TextWatcher {

	private int mViewId;
	
	private Object mObjTwo;
	
	private Handler mHandler;
	//间隔发送的时间，默认500毫秒
	private long mSleep = 500;
	//上一次文本
	private String lastStr;
	
	/**
	 * @param viewID 当前监听的控件ID
	 * @param handler 是否要回调，nulll代表不用
	 */
	public MyTextWatcherCommon(int viewID,Handler handler){
		this.mViewId  = viewID;
		this.mHandler = handler;
	}
	
	public void setDelayedSleep(long sleep){
		this.mSleep = sleep;
	}
	
	/**
	 * @param viewID 当前监听的控件ID
	 * @param objTwo 主要区分如果viewID一样时处理
	 * @param handler 是否要回调，nulll代表不用
	 * @param sleep 更新间隔时间
	 */
	public MyTextWatcherCommon(int viewID,Object objTwo,Handler handler,long sleep){
		this.mViewId  = viewID;
		this.mObjTwo  = objTwo;
		this.mHandler = handler;
		this.mSleep = sleep;
	}
	
	/**
	 * @param viewID 当前监听的控件ID
	 * @param objTwo 主要区分如果viewID一样时处理
	 * @param handler 是否要回调，nulll代表不用
	 */
	public MyTextWatcherCommon(int viewID,Object objTwo,Handler handler){
		this.mViewId  = viewID;
		this.mObjTwo  = objTwo;
		this.mHandler = handler;
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		lastStr = s.toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!lastStr.equals(s.toString())) {
			sendMessage(s.toString());
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	private void sendMessage(String str){
		if (mHandler != null) {
			mHandler.removeMessages(TypeKey.getInstance().TEXTWATCHER_WHAT);
			Message msg = mHandler.obtainMessage();
			msg.obj = new MyEvents(ModeEnum.OTHER,mViewId,str,mObjTwo,true);
			msg.what = TypeKey.getInstance().TEXTWATCHER_WHAT;
			mHandler.sendMessageDelayed(msg, mSleep);
		}
	}
}
