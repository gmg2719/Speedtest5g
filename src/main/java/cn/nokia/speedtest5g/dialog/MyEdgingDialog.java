package cn.nokia.speedtest5g.dialog;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *	版权所有  (c)2013, ridgetech<p>	
 *  作者	：zwq
 *  创建时间	：2013-3-14
 *  描述	:提示对话框
 */
public class MyEdgingDialog extends BaseAlertDialog{

	private TextView tv_content;
	
	private Button btn_ok,btn_no;
	
	private LinearLayout layout;
	
	private int mType;
	
	private Object mObject;
	
	private boolean isDismiss = true;//是否隐藏对话框，默认隐藏
	
	public MyEdgingDialog(Context context) {
		super(context,true);
	}
	/**
	 * @param context
	 */
	public MyEdgingDialog(Context context,boolean isDiss) {
		super(context,true);
		if (!isDiss) {
			//第二种方法
			setCancelable(false);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置有EditText控件时对话框不显示的处理
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		setContentView(R.layout.dialog_edging_ui);
		layout = (LinearLayout) findViewById(R.id.layout_dialog_edging_content);
		btn_ok = (Button) findViewById(R.id.btn_dialog_edging_ok);
		btn_no = (Button) findViewById(R.id.btn_dialog_edging_no);
		tv_content = (TextView) findViewById(R.id.tv_dialog_edging_content);
		btn_ok.setOnClickListener(listener);
		btn_no.setOnClickListener(listener);
		setN();
	}
	
	/**
	 * @param content 提示内容
	 * @param type 提示类型
	 */
	public void show(String content,int type) {
		super.show();
		this.mType = type;
		this.mObject = content;
		tv_content.setText(content);
	}

	private ListenerBack mListener;
	
	public MyEdgingDialog setListener(ListenerBack listerne){
		this.mListener = listerne;
		return this;
	}

	/**
	 * @param content 提示内容
	 */
	public void show(String content) {
		super.show();
		this.mObject = content;
		tv_content.setText(content);
	}
	
	/**
	 * @param content 内容
	 * @param type 类型
	 */
	public void show(View content ,int type) {
		super.show();
		this.mType = type;
		this.mObject = content;
		layout.removeAllViews();
		layout.addView(content);
	}
	public void show(View content,int type,ListenerBack back){
		show(content, type);
		setListener(back);
	}
	
	public MyEdgingDialog show(View content,int type,ListenerBack back,boolean isDismiss){
		show(content, type);
		setListener(back);
		this.isDismiss = isDismiss;
		return this;
	}
	
	private String strOK,strNO;
	/**
	 * @param ok 
	 * @param no
	 * 设置按钮名称
	 */
	public MyEdgingDialog setButtonText(String ok,String no){
		this.strOK = ok;
		this.strNO = no;
		return this;
	}

	private void setN(){
		if (strOK != null && strOK.length() > 0) {
			btn_ok.setText(strOK);
		}
		if (strNO != null && strNO.length() > 0) {
			btn_no.setText(strNO);
		}
	}
	
	View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onListener(mType, mObject,v.getId() == R.id.btn_dialog_edging_ok ? true : false);
			}
			if (isShowing() &&isDismiss) {
				dismiss();
			}
		}
	};

	@Override
	public void dismiss() {
		super.dismiss();
		layout.removeAllViews();
		
	}
	
}
