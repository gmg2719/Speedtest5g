package cn.nokia.speedtest5g.speedtest.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class SpeedTestNetStateChangeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
			intent.setAction(SpeedTestUtil.NETWORK_CHANGE_ACTION);
			context.sendBroadcast(intent);
		}
	}
}
