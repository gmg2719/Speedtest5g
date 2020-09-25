package cn.nokia.speedtest5g.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;

import cn.nokia.speedtest5g.R;

public class BaseAlertDialog extends AlertDialog {

	protected BaseAlertDialog(Context context) {
		super(context);
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		setAnim(false);
	}
	
	protected BaseAlertDialog(Context context,boolean isAnim) {
		super(context);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
		setAnim(isAnim);
	}
	
	private void setAnim(boolean isAnim){
		Window window = getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        if (isAnim) {
        	 window.setWindowAnimations(R.style.dialogAnimStyle);  //添加动画
		}
	}
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		//设置有EditText控件时对话框不显示的处理
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//	}
}
