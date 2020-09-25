package cn.nokia.speedtest5g.app.activity;

import android.os.Handler;
import android.os.Message;

import cn.nokia.speedtest5g.app.enums.MyEvents;

public abstract class BaseFragmentHandlerActivity extends BaseFragmentActivity {

	public Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (isFinishing()) {
				return true;
			}
			if (isExecute() && msg.obj != null && msg.obj instanceof MyEvents) {
				onHandleMessage((MyEvents) msg.obj);
			}
			return true;
		}
	});

	public abstract void onHandleMessage(MyEvents events);

	public void sendMessage(MyEvents events) {
		Message msg = new Message();
		msg.obj = events;
		mHandler.sendMessage(msg);
	}

	public void sendMessage(MyEvents events, long time) {
		Message msg = new Message();
		msg.obj = events;
		mHandler.sendMessageDelayed(msg, time);
	}
	
	public void sendMessage(MyEvents events,int what, long time) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = events;
		if (time > 0) {
			mHandler.sendMessageDelayed(msg, time);
		}else {
			mHandler.sendMessage(msg);
		}
	}
	
	public void removeMessage(int what) {
		mHandler.removeMessages(what);
	}
}
