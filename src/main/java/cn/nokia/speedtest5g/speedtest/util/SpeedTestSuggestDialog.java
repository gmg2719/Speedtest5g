package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 * 介绍弹窗
 * @author JQJ
 *
 */
public class SpeedTestSuggestDialog extends BaseAlertDialog{

	public static final String TYPE_UPLOAD = "TYPE_UPLOAD"; //上传速度

	public static final String TYPE_PING = "TYPE_PING"; //ping
	public static final String TYPE_SHAKE = "TYPE_SHAKE"; //抖动
	public static final String TYPE_PAKETLOSS = "TYPE_PAKETLOSS"; //丢包

	public static final String TYPE_WIFI_TIP = "TYPE_WIFI_TIP"; //wifi信号强度提示
	public static final String TYPE_NETWORK_NOT_GOOD = "TYPE_NETWORK_NOT_GOOD"; //测速网络不好提示

	private TextView mTvTip = null;
	private TextView mTvContent = null;
	private Button mBtn = null;

	public SpeedTestSuggestDialog(Context context) {
		super(context);
	}

	public void show(String type) {
		super.show();
		if(TYPE_PING.equals(type)){
			mTvTip.setText("什么是Ping？");
			mTvContent.setText("ping值是指从PC对网络服务器发送数据到服务器接收反馈数据的时间。一般以毫秒计算。玩网络游戏的时候，" +
					"如果ping值高就会感觉操作延迟，所以ping值越低就会感觉游戏越顺畅。");
		}else if(TYPE_SHAKE.equals(type)){
			mTvTip.setText("什么是抖动？");
			mTvContent.setText("网络中的延迟是指信息从发送到接收经过的延迟时间；而抖动是指最大延迟与最小延迟的时间差，如最大延迟是20毫秒，最小延迟为5毫秒，" +
					"那么网络抖动就是15毫秒，它主要标识一个网络的稳定性。");
		}else if(TYPE_PAKETLOSS.equals(type)){
			mTvTip.setText("什么是丢包？");
			mTvContent.setText("丢包是指一个或多个数据包的数据无法通过网络到达目的地。可能原因是多方面的，或是网络中多路径衰落造成信号衰减；或是通道阻塞造成丢包；" +
					"或是损坏的数据包被拒绝通过；或是网上硬件有缺陷，网上驱动程序有故障。");
		}else if(TYPE_UPLOAD.equals(type)){
			mTvTip.setText("什么是上传速度？");
			mTvContent.setText("一般是从你的终端到服务器的上传速度。\n" +
                    "1B=8b  1B/s=8b/s（或1Bps=8bps）\n" +
                    "1KB=1024B  1KB/s=1024B/s\n" +
                    "1MB=1024KB  1MB/s=1024KB/s。");
		}else if(TYPE_WIFI_TIP.equals(type)){
			mTvTip.setText("什么是Wi-Fi信号？");
			mTvContent.setText("一般情况下：大于-40dBm是信号强度极强。-80dBm~-40dBm这个区间信号正常，-110dBm~-80dBm这个区间就是信号较弱，可能是距离" +
					"路由器较远或中间穿越障碍物较多，建议换个位置试试。再低于-110dBm以后的就是超级微弱了，就是说从发射器发出的信号被完全阻挡了。");
		}else if(TYPE_NETWORK_NOT_GOOD.equals(type)){
			mTvTip.setText("温馨提示");
			mTvContent.setText("当前网络似乎不是很好，请关闭后重试哦！");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_suggest_dialog);

		setCanceledOnTouchOutside(false);

		mTvTip = (TextView)findViewById(R.id.speed_test_suggest_dialog_tv_tip);
		mTvContent = (TextView)findViewById(R.id.speed_test_suggest_dialog_tv_content);

		mBtn = (Button)findViewById(R.id.speed_test_suggest_dialog_btn);

		mBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SpeedTestSuggestDialog.this.dismiss();
			}
		});
	}

}
