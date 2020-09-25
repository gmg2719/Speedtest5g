package cn.nokia.speedtest5g.app.activity;

import cn.nokia.speedtest5g.app.enums.MyEvents;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 新增handler父类
 * @author zwq
 *
 */
public abstract class BaseActionBarHandlerActivity extends BaseActionBarActivity {
	//设定查询当前topActivity类型的what值，用于handler传输
	public final int NEED_HIJACK_WHAT = 999;
	
	public Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (isFinishing()) {
				return true;
			}
			if (isExecute() && msg.obj != null && msg.obj instanceof MyEvents) {
				onHandleMessage((MyEvents) msg.obj);
			}
//			MyEvents events = (MyEvents) msg.obj;
//			switch (events.getMode()) {
//			case OTHER:
//				//查询当前栈顶是否是当前页面
//				if (events.getType() == EnumRequest.OTHER_ACTIVITY_TOP_QUERY.toInt() && msg.what == NEED_HIJACK_WHAT) {
//					try {
//						//获取activity管理类
//						ActivityManager mActivityManager = (ActivityManager) BaseActionBarHandlerActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
//						//获取最顶部的activity
//						RunningTaskInfo info = mActivityManager.getRunningTasks(1).get(0);
//						if (!info.topActivity.getClassName().contains(BaseActionBarHandlerActivity.this.getClass().getName())) {
//							if (isNeedHijackToast) {
//								showCommonBottom("您当前应用正在切换，请谨慎输入敏感内容");
//							}
//						}else {
//							sendMessage(new MyEvents(ModeEnum.OTHER,EnumRequest.OTHER_ACTIVITY_TOP_QUERY.toInt()), NEED_HIJACK_WHAT, 1500);
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//				}else {
//					onHandleMessage(events);
//				}
//				break;
//			default:
//				onHandleMessage(events);
//				break;
//			}
			return true;
		}
	});

	public abstract void onHandleMessage(MyEvents events);

	public void sendMessage(MyEvents events) {
		Message msg = new Message();
		msg.obj = events;
		msg.what = events.getType();
		mHandler.sendMessage(msg);
	}

	public void sendMessage(MyEvents events, long time) {
		Message msg = new Message();
		msg.obj = events;
		msg.what = events.getType();
		mHandler.sendMessageDelayed(msg, time);
	}
	
	public void sendMessage(MyEvents events,int what, long time) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = events;
		msg.what = events.getType();
		if (time > 0) {
			mHandler.sendMessageDelayed(msg, time);
		}else {
			mHandler.sendMessage(msg);
		}
	}
	
	public void removeMessage(int what) {
		mHandler.removeMessages(what);
	}
	
	@Override
	protected void onResume() {
		if (isNeedHijack) {
//			sendMessage(new MyEvents(ModeEnum.OTHER,EnumRequest.OTHER_ACTIVITY_TOP_QUERY.toInt()), NEED_HIJACK_WHAT, 1500);
			isNeedHijackToast = true;
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isNeedHijack) {
			//获取activity管理类
			ActivityManager mActivityManager = (ActivityManager) BaseActionBarHandlerActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
			//获取最顶部的activity
			if (mActivityManager != null && mActivityManager.getRunningTasks(1) != null && mActivityManager.getRunningTasks(1).size() > 0) {
				RunningTaskInfo info = mActivityManager.getRunningTasks(1).get(0);
				if (info.topActivity.getClassName().contains(BaseActionBarHandlerActivity.this.getClass().getName())) {
					isNeedHijackToast = false;
				}
			}
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (isNeedHijackToast && isNeedHijack && !hasFocus) {
			if (ivDialog != null && ivDialog.isShowing()) {
				return;
			}
			if (mToastDialog != null && mToastDialog.isShowing()) {
				return;
			}
			if (isTemporary) {
				isTemporary = false;
				return;
			}
			//您当前已离开网优宝，请谨慎操作，勿在非官方版客户端界面输入账号、密码等信息。
			//获取activity管理类
//			ActivityManager mActivityManager = (ActivityManager) BaseActionBarHandlerActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
//			//获取最顶部的activity
//			RunningTaskInfo info = mActivityManager.getRunningTasks(1).get(0);
//			if (!info.topActivity.getClassName().contains(BaseActionBarHandlerActivity.this.getClass().getName())) {
//				showCommonBottom("您当前应用正在切换，请谨慎输入敏感内容");
//				mHandler.removeMessages(NEED_HIJACK_WHAT);
//			}
			showCommonBottom("您当前应用正在切换，请谨慎输入敏感内容");
		}
	}
	
	@Override
	public void onBackPressed() {
		isNeedHijackToast = false;
		if (isNeedHijack) {
			mHandler.removeMessages(NEED_HIJACK_WHAT);
		}
		super.onBackPressed();
	}
}
