package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 * 速率测试APN配置提示弹窗
 *
 * @author jinhaizheng
 */
public class SpeedTestApnSettingDialog extends BaseAlertDialog implements View.OnClickListener {
	private Context context;

	private TextView title;
	private TextView pingStatus;
	private TextView btnStratSpeedTestOrGotoPkHint;
	private Button btnStratSpeedTestOrGotoPk;
	
	private ListenerBack mListenerBack;
	private String ipAddress;
	private boolean isClickCancel;
	// 是否ping结束
	private boolean isPingTesting;
	public boolean isGotoSpeedPk;

	public SpeedTestApnSettingDialog(Context context, ListenerBack listenerBack, String ipAddress) {
		super(context, true);
		this.context = context;
		setCancelable(false);
		this.mListenerBack = listenerBack;
		this.ipAddress = ipAddress;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_speed_test_apn_setting);

		title = (TextView) findViewById(R.id.tv_apn_setting_title_ip_address);
		btnStratSpeedTestOrGotoPkHint =  (TextView) findViewById(R.id.tv_apn_setting_strat_speed_test_or_goto_pk_hint);
		btnStratSpeedTestOrGotoPk = (Button) findViewById(R.id.btn_apn_setting_strat_speed_test_or_goto_pk);
		pingStatus = (TextView) findViewById(R.id.tv_apn_setting_ping_status);
		
		findViewById(R.id.ibtn_apn_setting_close_dialog).setOnClickListener(this);
		findViewById(R.id.btn_apn_setting).setOnClickListener(this);
		btnStratSpeedTestOrGotoPk.setOnClickListener(this);
		findViewById(R.id.btn_apn_setting_ping).setOnClickListener(this);
		
		if (isGotoSpeedPk) {
			btnStratSpeedTestOrGotoPkHint.setText("直接进入速率PK");
			btnStratSpeedTestOrGotoPk.setText(context.getString(R.string.speed_pk));
		}else {
			btnStratSpeedTestOrGotoPkHint.setText("直接开始速率测试");
			btnStratSpeedTestOrGotoPk.setText(context.getString(R.string.speedTest));
		}
		title.setText("当前所择APN服务器" + ipAddress);
	}

	@Override
	public void dismiss() {
		if (mListenerBack != null && !isClickCancel) {
			mListenerBack.onListener(EnumRequest.OTHER_POPUP_CLOSE.toInt(), null, false);
		}
		super.dismiss();
	}

	@Override
	public void onClick(View v) {
		isClickCancel = true;
        int id = v.getId();
        if (id == R.id.btn_apn_setting) {
            mListenerBack.onListener(v.getId(), null, true);
        } else if (id == R.id.btn_apn_setting_strat_speed_test_or_goto_pk) {
            mListenerBack.onListener(v.getId(), null, isGotoSpeedPk);
        } else if (id == R.id.btn_apn_setting_ping) {
            String netType = NetInfoUtil.getCurrentNetworkType(context);
            WybLog.syso("---netType:" + netType);
            if (!netType.trim().equals("WiFi") && !netType.trim().equals("无")) {
                if (isPingTesting) {
                    ((BaseActionBarActivity) context).showCommon("正在进行ping测试");

                } else {
                    PingInfo info = new PingInfo();
                    // ipAddress = "112.5.186.163";// 集团公司服务器
                    info.setIp(ipAddress);
                    info.setTimes(-1);
                    info.toStrState2();
                    updateView(info, true);

                    PingThread pingThread = new PingThread(info, -1, mHandler, 1, 5);
                    pingThread.start();
                }

            } else {
                ((BaseActionBarActivity) context).showCommon("请关闭wifi，并开启移动数据。");
            }
        }
		if (isShowing() && v.getId() != R.id.btn_apn_setting_ping) {
			dismiss();
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == EnumRequest.TASK_PING.toInt()) {
				PingInfo pingInfo = (PingInfo) msg.obj;
				pingInfo.toStrState2();
				updateView(pingInfo, false);
			}
			return false;
		}
	});

	private void updateView(PingInfo pingInfo, boolean isPingTesting) {
		this.isPingTesting = isPingTesting;
		pingStatus.setText("ping " + ipAddress + "  " + pingInfo.getStrIp() + (TextUtils.isEmpty(pingInfo.getStrState()) ? "" : "  " + pingInfo.getStrState()));
	}

	@Override
	public void show() {
		super.show();
		// 解决宽度铺满
		try {
			Window window = getWindow();
			window.getDecorView().setPadding(0, 0, 0, 0);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);

		} catch (Exception e) {

		}
	}
}
