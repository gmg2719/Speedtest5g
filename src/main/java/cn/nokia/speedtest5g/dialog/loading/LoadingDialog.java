package cn.nokia.speedtest5g.dialog.loading;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 *	版权所有  (c)2013, ridgetech<p>	
 *  作者	：zwq
 *  创建时间	：2013-3-14
 *  描述	:加载对话框
 */
public class LoadingDialog extends BaseAlertDialog implements DialogInterface.OnKeyListener{
	
	private boolean diss = true;
	private boolean isDismiss = true; //是否按键返回关闭加载对话框
	private TextView mTvMsg;
	
	public LoadingDialog(Context context) {
		super(context);
		//不允许点击对话框之外区域dismiss,且可以按
		//第一种方法
//		setCancelable(true);
//		setCanceledOnTouchOutside(false);
		//第二种方法
		setCancelable(false);
		//目的为了监听返回事件
		setOnKeyListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		mTvMsg = (TextView) findViewById(R.id.loadingDialog_tv_msg);
	}
	
	public void setTextMsg(String msg){
		if (msg == null || msg.isEmpty()) {
			mTvMsg.setText("");
			mTvMsg.setVisibility(View.GONE);
		}else {
			mTvMsg.setText(msg);
			mTvMsg.setVisibility(View.VISIBLE);
		}
	}
	
	
	@Override
	public void show() {
		try {
			diss = true;
			super.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private ListenerBack mListenerBack;
	public void setListener(ListenerBack listenerBack){
		this.mListenerBack = listenerBack;
	}
	
	@Override
	public void dismiss() {
		try {
			mListenerBack = null;
			super.dismiss();
			this.diss = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void setDismiss(boolean isDismiss) {
		this.isDismiss = isDismiss;
	}

	@Override
	public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (diss) {
				if (mListenerBack != null) {
					mListenerBack.onListener(EnumRequest.DIALOG_NET_DISMISS.toInt(), "", true);
				}
			}
            if(isDismiss) dismiss();
        }
		return false;
	}
}
