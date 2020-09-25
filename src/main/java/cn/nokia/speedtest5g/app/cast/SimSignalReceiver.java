package cn.nokia.speedtest5g.app.cast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 卡1卡2信号回调广播事件
 * @author zwq
 *
 */
public class SimSignalReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent != null) {
			SimSignalHandler.getInstances().onReceive(intent);
		}
	}
}
