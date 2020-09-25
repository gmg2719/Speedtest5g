package cn.nokia.speedtest5g.wifi.other;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 * 干扰分析提示
 */
public class WifiGlHintDialog extends BaseAlertDialog implements View.OnClickListener {

	public WifiGlHintDialog(Context context) {
		super(context, true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_dialog_gl_hint);

		((TextView)findViewById(R.id.glHintDialog_tv_content)).setText(Html.fromHtml("上网慢？？？<font color= '#C4AD3B'>干扰分析仪让您家里的干扰源无处遁形</font>"));

		findViewById(R.id.glHintDialog_btn_ok).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		dismiss();
	}
}
