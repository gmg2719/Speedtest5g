package cn.nokia.speedtest5g.app.activity;

import android.os.Handler;
import android.os.Message;

import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.view.viewpager.BaseFragment;

public abstract class BaseHandlerFragment extends BaseFragment implements ListenerBack{
	
	public Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			if (getActivity() == null || getActivity().isFinishing()) {
				return true;
			}
			MyEvents events = (MyEvents) msg.obj;
			if (events != null) {
				onHandleMessage(events);
			}
			return true;
		}
	});

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		
	}
	
	public abstract void onHandleMessage(MyEvents events);

	public void sendMessage(MyEvents events) {
		Message msg = mHandler.obtainMessage();
		msg.obj = events;
		mHandler.sendMessage(msg);
	}

	public void sendMessage(MyEvents events, long time) {
		Message msg = mHandler.obtainMessage();
		msg.obj = events;
		mHandler.sendMessageDelayed(msg, time);
	}
	
	public void sendMessage(MyEvents events,int what, long time) {
		Message msg = mHandler.obtainMessage();
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
