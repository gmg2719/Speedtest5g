package cn.nokia.speedtest5g.app.activity;

import java.lang.reflect.Field;

import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public abstract class MyTabActivity extends BaseActionBarFragmentActivity {

	// 定义FragmentTabHost对象
//	private FragmentTabHost mTabHost;
	/*---------单切换---------------------*/
	private LocalActivityManager lam;

	private TabHost mHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getContentView();
		setContentView(contentView);
		mHost = (TabHost) findViewById(android.R.id.tabhost);
		lam = new LocalActivityManager(MyTabActivity.this, false);
		lam.dispatchCreate(savedInstanceState);
		mHost.setup(lam);
		Bundle extras = getIntent().getExtras();
		for (int i = 0; i < getClassName().length; i++) {
			try {
				String[] split = getClassName()[i].split("\\.");
				Intent intent = new Intent(getBaseContext(), Class.forName(getClassName()[i]));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("position", i);
				//设置传递登录数据
				if (extras != null) {
					intent.putExtras(extras);
				}
				mHost.addTab(mHost
						.newTabSpec(split[split.length-1])
						.setIndicator(split[split.length-1])
						.setContent(intent));
			} catch (ClassNotFoundException e) {
				WybLog.syso("出错啦。。。。。" + e.getMessage());
			}
		}
	}

	@Override
	protected void onPause() {
		if (lam != null) {
			lam.dispatchPause(isFinishing());
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (lam != null) {
			lam.dispatchResume();
		}
		if (mHost != null) {
			if (getTag() == null || getTag().trim().length()<=0) {
				mHost.setCurrentTab(0);
			}else {
				mHost.setCurrentTabByTag(getTag());
			}
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		try {
//			if (lam != null) {
//				lam.removeAllActivities();
//			}
//		}catch (Exception e){
//
//		}
		super.onDestroy();
	}
	/**
	 * 布局 R.layout.tab_bottom
	 * R.layout.tab_top
	 * @return
	 */
	public abstract int getLayout();
	
	private View contentView;
	
	private void getContentView(){
		contentView = LayoutInflater.from(this).inflate(getLayout(), null);
	}
	
	/**
	 * 要跳转的类名 ,记得包名也写  如：com.zwq.mActivity;
	 * 数量与Tab名称对应
	 * @return
	 */
	public abstract String[] getClassName();
	
	private String tag = "";

	/**
	 * 当前tab的Tag
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 当前tab的Tag
	 * @param tag
	 * @param init 是否是初始状态
	 */
	public void setTag(String tag,boolean init) {
		this.tag = tag;
		if (!init) {
			mHost.setCurrentTabByTag(tag);
		}
	}
	
	public View getCurrentView(){
		if(mHost != null){
			return mHost.getCurrentView();
		}
		return null;
	}
	
	/**
	 * 获取当前子页面对象 通过view获取getContext()(Android7.0不支持view.getContext())
	 * Android10限制“非SDK接口”调用，如：getDeclaredField()、getMethod()denng
	 *
	 * @return 当前子页面对象
	 */
	public Activity getCurrentActivity() {
		Activity subActivity = null;
		if (mHost != null) {
			View currentView = mHost.getCurrentView();
			if (currentView.getContext().getClass().getName().contains("com.android.internal.policy.DecorContext")) {
				try {
					Field field = currentView.getContext().getClass().getDeclaredField("mPhoneWindow");
					field.setAccessible(true);
					Object obj = field.get(currentView.getContext());
					java.lang.reflect.Method m1 = obj.getClass().getMethod("getContext");
					subActivity = (Activity) (m1.invoke(obj));

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				subActivity = (Activity) currentView.getContext();
			}
		}
		return subActivity;
	}
}
