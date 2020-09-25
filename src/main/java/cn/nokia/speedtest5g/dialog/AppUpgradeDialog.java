package cn.nokia.speedtest5g.dialog;

import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 应用升级弹窗
 *
 * @author jinhaizheng
 */
public class AppUpgradeDialog extends BaseAlertDialog{

	private TextView tv_content,tvOne,tvTwo;
	
	private int mType;
	
	private Object mObject;
	
	private Button btnOk,btnNo;
	
	private String strOk = "",strNo = "";
	
	private View viewScorlly;
	
	public AppUpgradeDialog(Context context) {
		super(context,true);
	}
	/**
	 * @param context
	 */
	public AppUpgradeDialog(Context context,boolean isDiss) {
		super(context,true);
		if (!isDiss) {
			//第二种方法
			setCancelable(false);
		}
	}

	private boolean hasMeasured;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_app_upgrade);
		tv_content = (TextView) findViewById(R.id.tv_dialog_toast_content);
		tvOne = (TextView) findViewById(R.id.tv_dialog_toast_content_one);
		tvTwo = (TextView) findViewById(R.id.tv_dialog_toast_content_two);
		viewScorlly = findViewById(R.id.viewscroll_dialog);
		btnOk = (Button) findViewById(R.id.btn_dialog_toast_ok);
		btnOk.setOnClickListener(listener);
		if (!strOk.isEmpty()) {
			btnOk.setText(strOk);
		}
		if (!strNo.isEmpty()) {
			findViewById(R.id.btn_dialog_toast_sp).setVisibility(View.VISIBLE);
			btnNo = (Button) findViewById(R.id.btn_dialog_toast_no);
			btnNo.setText(strNo);
			btnNo.setOnClickListener(listener);
			btnNo.setVisibility(View.VISIBLE);
		}
		
		viewScorlly.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if (hasMeasured == false) {
	                 int heightGroup = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().HEIGHT(), 0);
	                 if (viewScorlly.getMeasuredHeight() > heightGroup/2) {
						LayoutParams layoutParams = viewScorlly.getLayoutParams();
						layoutParams.height = heightGroup/2;
						viewScorlly.setLayoutParams(layoutParams);
					 }
	                 //获取到宽度和高度后，可用于计算                    
	                 hasMeasured = true;
	             }
				 return true;
			}
		});
	}
	
	/**
	 * @param content 提示内容
	 * @param type 提示类型
	 */
	public void show(String content,int type) {
		super.show();
		this.mType = type;
		this.mObject = content;
		tvTwo.setText(content);
	}
	
	/**
	 * @param content 提示内容
	 * @param type 提示类型
	 */
	public void show(String strOne,String strTwo,String content,int type) {
		super.show();
		this.mType = type;
		tv_content.setText(content);
		tvOne.setText(strOne);
		tvTwo.setText(strTwo);
	}

	private ListenerBack mListener;
	
	public AppUpgradeDialog setListener(ListenerBack listerne){
		this.mListener = listerne;
		return this;
	}

	/**
	 * @param content 提示内容
	 */
	public void show(String content) {
		try {
			super.show();
			this.mObject = content;
			tvTwo.setText(content);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void show(Object content) {
		try {
			super.show();
			this.mObject = content;
			if (content instanceof String) {
				tvTwo.setText(content.toString());
			}else if (content instanceof Spanned) {
				tvTwo.setText((Spanned)content);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Html.fromHtml(source)
	 * @param content
	 */
	public void show(Spanned content){
		try {
			super.show();
			this.mObject = content;
			tvTwo.setText(content);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public AppUpgradeDialog setButtom(String ok,String no){
		this.strOk = ok;
		this.strNo = no;
		return this;
	}
	
	public AppUpgradeDialog showNo(){
		this.strOk = getContext().getString(R.string.ok);
		this.strNo = getContext().getString(R.string.cancel);
		return this;
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onListener(mType, mObject,v.getId() == R.id.btn_dialog_toast_ok ? true : false);
			}
			if (isShowing()) {
				dismiss();
			}
		}
	};
}
