package cn.nokia.speedtest5g.app.thread;

import cn.nokia.speedtest5g.app.enums.MyEvents;

import android.os.Handler;
import android.os.Message;

/**
 * 线程操作父类--主要取本地数据及发送数据
 * @author zwq
 */
public class BaseRunnable implements Runnable {

	public Handler mHandler;
	
	public int mWhat;
	
	public Object[] mObject;
	
	public BaseRunnable(Handler handler,int what){
		this.mHandler = handler;
		this.mWhat	  = what;
	}
	
	public BaseRunnable(Handler handler,int what,Object... object){
		this.mObject  = object;
		this.mHandler = handler;
		this.mWhat	  = what;
	}
	
	@Override
	public void run() {

	}

	public void sendMsg(MyEvents events){
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage();
			msg.what = mWhat;
			msg.obj  = events;
			mHandler.sendMessage(msg);
		}
	}
}
