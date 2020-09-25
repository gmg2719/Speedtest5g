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
//import com.baidu.mobstat.StatService;

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

/**
 * FragmentActivity的父类（有标题的父类）
 * @author zwq
 *
 */
public class BaseActionBarFragmentActivity extends FragmentActivity implements ListenerBack{

	public LoadingDialog ivDialog;

	public MyActionBar actionBar;

	public final int REQUEST_CODE_SCAN = 0x0000;
	protected BaseActionBarFragmentActivity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		if (savedInstanceState != null) {
			getIntent().putExtras(savedInstanceState);
		}
		initStatistics();
		if (savedInstanceState != null) {
			getIntent().putExtras(savedInstanceState);
		}
		registerReceiver(myFinishCast, new IntentFilter(TypeKey.getInstance().ACTION_FINISH));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			MyLoginExitThread.setNewTouch(BaseActionBarFragmentActivity.this.getClass().getName());
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 统计点击
	 */
	public void initStatistics(){};

	/**
	 * 统计点击
	 * @param groupCod 主 如登录1000  退出1100
	 */
	public void installStatistics(int groupCod){
		new StatisticeAsyncTask().execute(getClass().getSimpleName(),BaseActionBarFragmentActivity.this.getString(groupCod));
	}

	private boolean isInitWater = true;

	@Override
	protected void onResume() {
		super.onResume();
		MyLoginExitThread.setNewonResume(this);
//		StatService.onResume(this);
		if (isInitWater) {
			isInitWater = false;
			if (getClass().getName().indexOf("MainHomeSuperActivity") == -1 &&
					getClass().getName().indexOf("TscsHomeSuperActivity") == -1 &&
					getClass().getName().indexOf("AttendanceSystemTabActivity") == -1 &&
					getClass().getName().indexOf("JJ_EmergentStatisticActivity") == -1 &&
					getClass().getName().indexOf("JzxhTabActivity") == -1) {
				WaterMarkUtil.showWatermarkView(this,getUser());
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
//		StatService.onPause(this);
	}

	@Override
	protected void onDestroy() {
		try {
            SpeedTest5g.getInstance().restStarting();
			unregisterReceiver(myFinishCast);
			super.onDestroy();
		} catch (Exception e) {
		}
	}

	/**
	 * 初始化...
	 * @param titleId 标题名称
	 */
	public void init(Object titleId,boolean isBack){
		actionBar = (MyActionBar) findViewById(R.id.actionbar);
		if (actionBar != null) {
			//			actionBar.setBackgroundColor(getResources().getColor(R.color.titleBarBg));
			actionBar.setBackgroundResource(R.drawable.drawable_supter_title_bg);
			actionBar.setIsMenuSpilx(View.GONE);
			if (titleId == null) {
				actionBar.setTitle(R.string.app_name);
			}else if (titleId instanceof String) {
				actionBar.setTitle((String)titleId);
			}else {
				actionBar.setTitle((Integer)titleId);
			}
			actionBar.setListenerBackSuper(BaseActionBarFragmentActivity.this);
			actionBar.setIsHomeSpilx(View.GONE);
			actionBar.setTitleColor(getResources().getColor(R.color.white));
			if (isBack) {
				actionBar.setHomeAction(new MenuAction(R.drawable.btn_title_back,EnumRequest.MENU_BACK.toInt(), ""));
			}
		}
		initBarTitle();
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void initBarTitle(){
		//透明状态栏  
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
			//透明状态栏  
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
			//透明导航栏  
			//	       getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  
			View titleView = findViewById(R.id.bar_super_title);
			if (titleView != null) {
				titleView.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 是否可执行的操作,当在子线程或已销毁的UI则不可操作
	 * @return 
	 */
	public boolean isExecute(){
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
	public void showCommon(String msg){
		if (isExecute()) {
			MyToast.getInstance(BaseActionBarFragmentActivity.this).showCommon(msg,Gravity.CENTER);
		}
	}

	public void showCommonBottom(String msg){
		if (isExecute()) {
			MyToast.getInstance(BaseActionBarFragmentActivity.this).showCommon(msg);
		}
	}

	//获取当前登录的用户名称s
	public String getUser(){
		return Base64Utils.decryptorDes3(SharedPreHandler.getShared(BaseActionBarFragmentActivity.this).getStringShared(TypeKey.getInstance().LOGIN_NAME(), ""));
	}

	//获取当前登录的用户ID
	public String getUserID(){
		return SharedPreHandler.getShared(BaseActionBarFragmentActivity.this).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
	}

	/**
	 * 获取当前登录用户的手机号码
	 * @return
	 */
	public String getUserPhone(){
		return Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
	}

	private BroadcastReceiver myFinishCast = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}

	};

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

	public void goIntent(Intent intent,boolean isFinish){
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

	public void showMyDialog(){
		if (isExecute()) {
			if (ivDialog == null) {
				ivDialog = new LoadingDialog(this);
			}
			if (!ivDialog.isShowing()) {
				ivDialog.show();
			}
			ivDialog.setListener(this);
		}
	}

	public void dismissMyDialog(){
		if (ivDialog != null && ivDialog.isShowing()) {
			ivDialog.dismiss();
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == EnumRequest.MENU_BACK.toInt()) {
			if (isTrue) {
				BaseActionBarFragmentActivity.this.finish();
			}
		}
	}
}
