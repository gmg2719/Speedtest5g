package cn.nokia.speedtest5g.view.viewpager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.util.MyToast;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.dialog.loading.LoadingDialog;
import cn.nokia.speedtest5g.util.Base64Utils;

public class BaseFragment extends Fragment {
	
	public final int RESULT_CODE_101 = 101;
	public final int RESULT_CODE_102 = 102;
	public final int RESULT_CODE_103 = 103;
	protected LayoutInflater inflater;
	private View contentView;
	private Context context;
	private ViewGroup container;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		onCreateView(savedInstanceState);
		if (contentView == null)
			return super.onCreateView(inflater, container, savedInstanceState);
		return contentView;
	}

	protected void onCreateView(Bundle savedInstanceState) {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		contentView = null;
		container = null;
		inflater = null;
	}

	public Context getApplicationContext() {
		return context;
	}

	public void setContentView(int layoutResID) {
		setContentView(inflater.inflate(layoutResID, container, false));
	}

	public void setContentView(View view) {
		contentView = view;
	}
	
	public void setContentView(LayoutInflater inflater, int layoutResID) {
		this.inflater = inflater;
		setContentView(inflater.inflate(layoutResID, null));
	}

	public View getContentView() {
		return contentView;
	}

	public View findViewById(int id) {
		if (contentView != null)
			return contentView.findViewById(id);
		return null;
	}

	// http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
	@Override
	public void onDetach() {
		super.onDetach();
//		try {
//			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//			childFragmentManager.setAccessible(true);
//			childFragmentManager.set(this, null);
//
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public LoadingDialog mLoadingDialog;

	public void showDialog(){
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(BaseFragment.this.getContext());
		}else if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		mLoadingDialog.show();
	}
	
	public void dismissDialog(){
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}
	
	public void setDialogDismiss(boolean isDismiss){
		if (mLoadingDialog == null) {
			mLoadingDialog = new LoadingDialog(BaseFragment.this.getContext());
		}
		mLoadingDialog.setDismiss(isDismiss);
	}

	/**
	 * 提示内容
	 * 
	 * @param msg
	 */
	public void showCommon(String msg){
		if ("thread interrupted".equals(msg)) {
			return;
		}
		MyToast.getInstance(BaseFragment.this.getContext()).showCommon(msg,Gravity.CENTER);
	}
	
	public void showCommonBottom(String msg){
		MyToast.getInstance(BaseFragment.this.getContext()).showCommon(msg);
	}
	
	//获取当前登录的用户名称s
	public String getUser(){
		return Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_NAME(), ""));
	}
	
	// 获取当前登录的用户ID
	public String getUserID() {
		return SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
	}
	
	private String mLoginName = "";
	public String getUserName(){
		if (TextUtils.isEmpty(mLoginName)) {
			mLoginName = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_MZ(), "");
		}
		return mLoginName;
	}
	
	/**
	 * 跳转UI页面
	 * @param c 下一个UI类
	 * @param extras 传递参数
	 */
	public void goIntent(Class<?> c,Bundle extras){
		Intent intent = new Intent(BaseFragment.this.getContext(),c);
		if (extras != null) intent.putExtras(extras);
		startActivity(intent);
	}
	
	/**
	 * 跳转UI页面
	 * @param c 下一个UI类
	 * @param extras 传递参数
	 */
	public void goIntent(Class<?> c,Bundle extras,int requestCode){
		Intent intent = new Intent(BaseFragment.this.getContext(),c);
		if (extras != null) intent.putExtras(extras);
		startActivityForResult(intent, requestCode);
	}
}
