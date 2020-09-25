package cn.nokia.speedtest5g.dialog.downdprogress;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 *  描述	:下载进度条对话框
 */
public class MyProgressDialog extends BaseAlertDialog {

	private Button btnOn;
	
	private MyTextProgressBar progressBar;
	
	private TextView tvVlaue;
	
	public MyProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.dialog_download_progressbar);
		btnOn = (Button) findViewById(R.id.btn_progressbar_no);
		progressBar = (MyTextProgressBar) findViewById(R.id.progressBar_down);
		tvVlaue = (TextView) findViewById(R.id.tv_progressbar_value);
	}
	
	/**
	 *  描述	：显示进度对话框
	 */
	public void showProgress(android.view.View.OnClickListener clickListener){
		setCancelable(false);
		show();
		btnOn.setOnClickListener(clickListener);
	}
	
	/**
	 *  参数说明：
	 *  	@param value 如：1.2/2.6M
	 */
	public void setData(int progress,String value){
		progressBar.setProgress(progress);
		tvVlaue.setText(value);
	}
}
