package cn.nokia.speedtest5g.jzxh.util;

import android.os.Handler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.thread.BaseRunnable;
import cn.nokia.speedtest5g.app.uitl.HttpPostUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;

/**
 * 基站信号相关网络获取线程
 * @author zwq
 *
 */
public class JzxhNetRunnable extends BaseRunnable {

	public JzxhNetRunnable(Handler handler, int what, Object... object) {
		super(handler, what, object);
	}
	
	private HttpPostUtil mHttpPostUtil;

	public void close(){
		if (mHttpPostUtil != null) {
			mHttpPostUtil.close();
		}
	}
	
	@Override
	public void run() {
		super.run();
		//基站信号数据上传
		if (mWhat == EnumRequest.NET_GIS_SIGNAL.toInt()) {
			if (mHttpPostUtil != null) {
				mHttpPostUtil.close();
			}
			mHttpPostUtil = new HttpPostUtil();
			String doPostJson = mHttpPostUtil.doPostJson(NetWorkUtilNow.getInstances().getToIp() + SpeedTest5g.getContext().getString(R.string.URL_Station_GISSIGNAL),
					mObject[0].toString(), EnumRequest.NET_GIS_SIGNAL.toInt());
			sendMsg(new MyEvents(ModeEnum.NETWORK, mWhat, doPostJson, doPostJson != null));
		//小区指标获取	
		}else if (mWhat == EnumRequest.NET_EMERGENT_CELL_QUOTA_LIST.toInt()) {
			if (mHttpPostUtil != null) {
				mHttpPostUtil.close();
			}
			mHttpPostUtil = new HttpPostUtil();
			String doPostJson = mHttpPostUtil.doPostJson(NetWorkUtilNow.getInstances().getToIp() + SpeedTest5g.getContext().getString(R.string.URL_JZXH_XQZB),
					mObject[0].toString());
			sendMsg(new MyEvents(ModeEnum.NETWORK, mWhat, doPostJson, doPostJson != null));
		}
		mHttpPostUtil = null;
	}
}
