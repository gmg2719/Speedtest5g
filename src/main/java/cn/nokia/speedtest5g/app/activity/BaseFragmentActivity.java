package cn.nokia.speedtest5g.app.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.util.MyToast;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.MyLoginExitThread;
import cn.nokia.speedtest5g.app.thread.StatisticeAsyncTask;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.dialog.loading.LoadingDialog;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar.MenuAction;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BaseFragmentActivity extends FragmentActivity implements ListenerBack {

	public final int RESULT_CODE_ONE = 0x0001;

	public final int RESULT_CODE_TWO = 0x0002;

	public MyActionBar actionBar;
	// 加载对话框
	public LoadingDialog ivDialog;
	protected FragmentActivity mActivity;

	protected int mBgTopColor = -1;
	protected int mBgParentColor = -1;
	protected int mTitleTextColor = -1;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			MyLoginExitThread.setNewTouch(BaseFragmentActivity.this.getClass().getName());
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			getIntent().putExtras(savedInstanceState);
		}
		mActivity = this;
		try {
			initStatistics();
			registerReceiver(myFinishCast, new IntentFilter(TypeKey.getInstance().ACTION_FINISH));
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		try {
            SpeedTest5g.getInstance().restStarting();
			unregisterReceiver(myFinishCast);
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	private BroadcastReceiver myFinishCast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			BaseFragmentActivity.this.finish();
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		MyLoginExitThread.setNewonResume(this);
		if (!isInitTitle) {
			isInitTitle = true;
			initBarTitle();
			if (getClass().getName().indexOf("AttendanceSystemTabActivity") == -1 &&
					getClass().getName().indexOf("TraverseActivity") == -1) {
				WaterMarkUtil.showWatermarkView(this,getUser());
			}
		}
	}

	// 是否已经初始化过标题一体化
	private boolean isInitTitle = false;

	/**
	 * 初始化...
	 * @param titleId 标题名称
	 */
	public void init(Object titleId,boolean isBack){
		actionBar = (MyActionBar) findViewById(R.id.actionbar);
		if (actionBar != null) {
			//			actionBar.setBackgroundColor(getResources().getColor(R.color.titleBarBg));
			if(mBgParentColor != -1){
				actionBar.setBackgroundColor(getResources().getColor(mBgParentColor));
			}else{
				actionBar.setBackgroundResource(R.drawable.drawable_supter_title_bg);
			}
			actionBar.setIsMenuSpilx(View.GONE);
			if (titleId == null) {
				actionBar.setTitle(R.string.app_name);
			}else if (titleId instanceof String) {
				actionBar.setTitle((String)titleId);
			}else {
				actionBar.setTitle((Integer)titleId);
			}
			actionBar.setListenerBackSuper(BaseFragmentActivity.this);
			actionBar.setIsHomeSpilx(View.GONE);
			if(mTitleTextColor != -1){
				actionBar.setTitleColor(getResources().getColor(mTitleTextColor));
			}else{
				actionBar.setTitleColor(getResources().getColor(R.color.white));
			}
			if(mBgTopColor != -1){
				actionBar.setTopColor(mBgTopColor);
			}
			if (isBack) {
				actionBar.setHomeAction(new MenuAction(R.drawable.btn_title_back,EnumRequest.MENU_BACK.toInt(), ""));
			}
		}
		initBarTitle();
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void initBarTitle() {
		// 透明状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			View titleView = findViewById(R.id.bar_super_title);
			if (titleView != null) {
				titleView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 获取当前登录用户的手机号码
	 * 
	 * @return
	 */
	public String getUserPhone() {
		return Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext())
				.getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
	}

	public void showMyDialog() {
		if (isExecute()) {
			if (ivDialog == null) {
				ivDialog = new LoadingDialog(mActivity);
			} else {
				ivDialog.setTextMsg("");
			}
			if (!ivDialog.isShowing()) {
				ivDialog.show();
			}
			ivDialog.setListener(BaseFragmentActivity.this);
		}
	}

	public void dismissMyDialog() {
		if (ivDialog != null && ivDialog.isShowing()) {
			ivDialog.dismiss();
		}
	}

	/**
	 * 是否可执行的操作,当在子线程或已销毁的UI则不可操作
	 * 
	 * @return
	 */
	public boolean isExecute() {
		if (isFinishing()) {
			return false;
		}
		if (Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
			return false;
		}
		return true;
	}

	/**
	 * 提示内容
	 * 
	 * @param msg
	 */
	public void showCommon(String msg) {
		if ("thread interrupted".equals(msg)) {
			return;
		}
		if (isExecute()) {
			MyToast.getInstance(mActivity).showCommon(msg, Gravity.CENTER);
		}
	}

	public void showCommonBottom(String msg) {
		if (isExecute()) {
			MyToast.getInstance(mActivity).showCommon(msg);
		}
	}

	// 获取当前登录的用户名称s
	public String getUser() {
		return Base64Utils.decryptorDes3(SharedPreHandler.getShared(mActivity).getStringShared(TypeKey.getInstance().LOGIN_NAME(), ""));
	}

	// 获取当前登录的用户ID
	public String getUserID() {
		return SharedPreHandler.getShared(mActivity).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
	}

	public String getUserName(){
		return SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_MZ(), "");
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == EnumRequest.MENU_BACK.toInt()) {
			if (isTrue) {
				mActivity.finish();
			}
		}
	}

	/**
	 * 统计点击
	 */
	public void initStatistics() {
	};

	/**
	 * 统计点击
	 * 
	 * @param groupCod
	 *            主 如登录1000 退出1100
	 */
	public void installStatistics(int groupCod) {
		new StatisticeAsyncTask().execute(getClass().getSimpleName(), mActivity.getString(groupCod));
	}

	/**
	 * 跳转UI页面
	 * @param c 下一个UI类
	 * @param extras 传递参数
	 * @param isFinish 是否关闭当前UI
	 */
	public void goIntent(Class<?> c,Bundle extras,boolean isFinish){
		Intent intent = new Intent(this,c);

		if (extras != null) intent.putExtras(extras);

		startActivity(intent);

		if (isFinish) finish();
	}

	/**
	 * 跳转下一个UI页面，且有返回数据的
	 * @param c 下一个UI类
	 * @param extras 传递参数
	 * @param requestCode 标识
	 */
	public void goIntentRequest(Class<?> c,Bundle extras,int requestCode){
		Intent intent = new Intent(this,c);

		if (extras != null) intent.putExtras(extras);

		startActivityForResult(intent, requestCode);
	}
}
