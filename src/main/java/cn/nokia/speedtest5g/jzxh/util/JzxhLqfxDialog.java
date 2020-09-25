package cn.nokia.speedtest5g.jzxh.util;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 基站信号-邻区分析
 */
public class JzxhLqfxDialog extends BaseAlertDialog {

	public JzxhLqfxDialog(Context context) {
		super(context, true);
	}

	/**
	 * @param context
	 */
	public JzxhLqfxDialog(Context context, boolean cancelable) {
		super(context, true);
		setCancelable(cancelable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jzxh_dialog_lqfx);
		TextView mTvTitle = (TextView) findViewById(R.id.jzxhDialogLqfx_tv_title);
		TextView mTvLteMsg1  = (TextView) findViewById(R.id.jzxhDialogLqfx_tv_LteMsg1);
		TextView mTvLteMsg2  = (TextView) findViewById(R.id.jzxhDialogLqfx_tv_LteMsg2);
		TextView mTvGsmMsg1  = (TextView) findViewById(R.id.jzxhDialogLqfx_tv_gsmMsg1);
		TextView mTvGsmMsg2  = (TextView) findViewById(R.id.jzxhDialogLqfx_tv_gsmMsg2);
		mTvTitle.setText("邻区分析");
		mTvLteMsg1.setText("4G分析规则");
		mTvLteMsg2.setText("(出现以下情况，则标红以提醒)");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine11)).setText("同频模三干扰：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine12)).setText("同频，邻区PCI与主控PCI模三值一致；");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine21)).setText("同频电平干扰：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine22)).setText("同频，邻区RSRP与主控RSRP正负3dB以内；");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine31)).setText("切换问题：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine32)).setText("邻区RSRP大于等于主控RSRP值3dB；");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine41)).setText("信息不全：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_LteLine42)).setText("如手机解析出来的邻区小区，未在邻区配置信息里面。");
		
		mTvGsmMsg1.setText("2G分析规则");
		mTvGsmMsg2.setText("(出现以下情况，则标红以提醒)");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine11)).setText("同频干扰：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine12)).setText("邻区BCCH与主控BCCH指标一致；(BCCH为查表所得，非实际测量)");
		
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine21)).setText("同频干扰：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine22)).setText("邻区的BCCH参数和主控参数一致并且邻区的BSIC参数与主控参数一致；(BCCH为查表所得，非实际测量)");
		
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine31)).setText("切换问题：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine32)).setText("邻区RXL大于等于主控RXL6dB以上；");
		
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine41)).setText("信息不全：");
		((TextView)findViewById(R.id.jzxhDialogLqfx_tv_GsmLine42)).setText("如手机解析出来的邻区小区，未在邻区配置信息里面。");
		
		findViewById(R.id.jzxhDialogLqfx_btn_ok).setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isShowing()) {
				dismiss();
			}
		}
	};
}
