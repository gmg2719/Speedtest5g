package cn.nokia.speedtest5g.app.activity2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.util.InputMethodUtil;
import com.android.volley.util.JsonHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.dialog.CommonDialog;

/**
 * 意见反馈
 * @author JQJ
 *
 */
public class FeedBackActivity extends BaseActionBarActivity {

	//反馈内容
	private EditText etMsg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("意见反馈", true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		etMsg = (EditText) findViewById(R.id.feedback_et);
		if (getUserID().equals("00")) {
			etMsg.setHint("请输入您要反馈的意见内容");
		}else {
			etMsg.setHint("请输入您要反馈的意见内容");
		}
	}

	public void onTitleListener(View v){
		FeedBackActivity.this.finish();
	}

	public void onBtnListener(View v){
        if (v.getId() == R.id.btn_feedback_ok) {//确定
            if (etMsg.getText().toString().trim().isEmpty()) {
                showCommon("请输入要反馈的意见内容");
            } else {
                InputMethodUtil.getInstances().inputMethod(FeedBackActivity.this, v.getWindowToken());
                showMyDialog();
                NetWorkUtilNow.getInstances().readNetworkGet(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_FEEDBACK) + getUserID() + "&content=" + NetWorkUtilNow.getInstances().getUtf(etMsg.getText().toString()),
                        EnumRequest.NET_FEED_BACK.toInt(), FeedBackActivity.this);
            }
        }
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		//意见反馈
		if (type == EnumRequest.NET_FEED_BACK.toInt()) {
			dismissMyDialog();
			if (isTrue) {
				BaseRespon dataJson = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
				if (dataJson != null && dataJson.isRs()) {
					new CommonDialog(FeedBackActivity.this, false).setListener(new ListenerBack() {

						@Override
						public void onListener(int type, Object object, boolean isTrue) {
							FeedBackActivity.this.finish();
						}
					}).show("感谢您的宝贵建议!");
				}
			}else {
				showCommon(object.toString());
			}
		}
	}
}
