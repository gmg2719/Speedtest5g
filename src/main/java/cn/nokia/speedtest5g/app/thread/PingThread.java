package cn.nokia.speedtest5g.app.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class PingThread extends Thread {

	private int mPosition;

	private PingInfo mInfo;

	private Handler mHandler;
	// ping 次数 默认为1
	private int pingTimes = 1;
	// 默认超时时间为30s
	private int outTime = 30;

	/**
	 * ping地址, 默认ping 1次 延迟30秒
	 * 
	 * @param position
	 *            游标
	 */
	public PingThread(PingInfo info, int position, Handler handler) {
		this.mInfo = info;
		this.mPosition = position;
		this.mHandler = handler;
	}

	/**
	 * ping地址, ping times次 延迟30秒
	 * 
	 * @param info
	 * @param position
	 * @param handler
	 * @param times
	 */
	public PingThread(PingInfo info, int position, Handler handler, int times) {
		this.mInfo = info;
		this.mPosition = position;
		this.mHandler = handler;
		this.pingTimes = times;
	}

	/**
	 * ping地址, ping times次 超时时间为outTime
	 * 
	 * @param info
	 * @param position
	 * @param handler
	 * @param times
	 * @param outTime
	 */
	public PingThread(PingInfo info, int position, Handler handler, int times, int outTime) {
		this.mInfo = info;
		this.mPosition = position;
		this.mHandler = handler;
		this.pingTimes = times;
		this.outTime = outTime;
	}

	public void clearHandler() {
		mHandler = null;
	}

	@Override
	public void run() {
		Runtime run = Runtime.getRuntime();
		Process proc = null;
		long startTime = System.currentTimeMillis();
		try {
			String ipOrDomainName = mInfo.getIp();
			if (TextUtils.isEmpty(ipOrDomainName)) {// 如果ip为空则用域名
				ipOrDomainName = mInfo.getDomainName();
			}
			String str = "ping -c " + pingTimes + " -w " + outTime + " " + ipOrDomainName;
			proc = run.exec(str);
			if (mHandler != null) {
				int status = proc.waitFor();
				WybLog.syso("---ping proc:" + status);
				mInfo.setState((status==0)||(status ==1));
				if (mInfo.isState()) {
					long[] value = getTarget(proc, startTime);
					mInfo.setTimes(value[0]);
					mInfo.shake = value[1];
					mInfo.packetLoss = Float.valueOf(value[2]);
				} else {
					mInfo.setTimes(-2);
				}
				Message msg = mHandler.obtainMessage();
				msg.what = EnumRequest.TASK_PING.toInt();
				msg.arg1 = mPosition;
				msg.obj = mInfo;
				mHandler.sendMessage(msg);
			}

		} catch (Exception e) {
			WybLog.syso("执行ping -c命令错误：" + e.getMessage());
			e.printStackTrace();
			if (mInfo != null) {
				handlePingProcessException();
			}

		} finally {
			if (proc != null) {
				proc.destroy();
			}
		}
	}

	/**
	 * ping异常处理
	 */
	private void handlePingProcessException() {
		boolean reachable = false;
		long pingTotalTime = 0;
		int successPingTimes = 0;// ping通次数
		for (int i = 1; i <= pingTimes; i++) {
			String ip = mInfo.getIp();
			WybLog.syso("ping mInfo.getIp()： " + mInfo.getIp());
			WybLog.syso("ping mInfo.getDomainName()： " + mInfo.getDomainName());
			if (TextUtils.isEmpty(ip)) {// 如果ip为空则通过域名解析
				try {
					ip = UtilHandler.getInstance().parseDomainName(mInfo.getDomainName());

				} catch (UnknownHostException e) {
					WybLog.syso("域名解析错误：" + e.getMessage());
					e.printStackTrace();
					reachable = false;
				}
			}
			long startTime = 0;
			long endTime = 0;
			try {
				// 尝试使用InetAddress进行ping测试
				startTime = System.currentTimeMillis();
				reachable = InetAddress.getByName(ip).isReachable(outTime*1000);// 手机型号：华为CAZ-AL10 Android版本6.0调isReachable总是返回false
				endTime = System.currentTimeMillis();
				WybLog.syso("InetAddress.getByName(" + ip + ").isReachable(10000) == " + reachable + "，时延 == " + (endTime - startTime));

			} catch (IOException e) {
				WybLog.syso("InetAddress：ping错误：" + e.getMessage());
				e.printStackTrace();
				reachable = false;
			}

			if (reachable) {
				pingTotalTime += (endTime - startTime);
				successPingTimes++;
			}
		}
		mInfo.setState(successPingTimes > 0);// ping通次数大于0，认为连接状态是正常的
		if (mHandler != null) {
			if (mInfo.isState()) {
				WybLog.syso("ping总时间 == " + pingTotalTime + "ms");
				WybLog.syso("ping测试次数 == " + successPingTimes);
				WybLog.syso("ping平均时间 == " + (pingTotalTime / successPingTimes) + "ms");
				mInfo.setTimes(pingTotalTime / successPingTimes);

			} else {// 还是ping不通
				mInfo.setTimes(-2);
			}
			Message msg = mHandler.obtainMessage();
			msg.what = EnumRequest.TASK_PING.toInt();
			msg.arg1 = mPosition;
			msg.obj = mInfo;
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 通过ping回获取 ping的平均时间
	 * 
	 * @param p
	 * @param startTime
	 * @return
	 * @throws IOException
	 */
	private long[] getTarget(Process p, long startTime) throws IOException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String str;
		long[] value = new long[3];
		long avgTime = 0;
		long shake = 0;
		long packetLoss = 0;
		while ((str = buf.readLine()) != null) {
			WybLog.syso("---ping result:" + str);
			if (str.contains("min/avg/max/mdev")) {
				try {
					str = str.replaceAll(" ", "");// 去空格
					int i = str.indexOf("=");
					int j = str.indexOf("ms");
					str = str.substring(i + 1, j);
					String[] temp = str.split("/");
					String min = temp[0].substring(0, temp[0].length() - 1);
					String avg = temp[1].substring(0, temp[1].length() - 1);
					String max = temp[2].substring(0, temp[2].length() - 1);
					avgTime = (long) Float.parseFloat(avg);
					shake = (long) Float.parseFloat(max) - (long) Float.parseFloat(min);
				} catch (Exception e) {
					avgTime = System.currentTimeMillis() - startTime;
				}
				value[0] = avgTime;
				value[1] = shake;
			}
			if(str.contains("packet loss")){
				String data0 = str.substring(str.indexOf("received"), str.indexOf("packet loss"));
				if(str.contains("errors")){
					data0 = str.substring(str.indexOf("errors"), str.indexOf("packet loss"));
				}
				String[] data1 = data0.split(",");
				if(data1.length > 1){
					String data2 = data1[1].replace(" ", "");
					String data3 = data2.replace("%", "");
					packetLoss = (long) Float.parseFloat(data3);
				}
				value[2] = packetLoss;
			}
			if (str.contains("0 packets received") || str.contains("0 received") || str.contains("100.0% packet loss")) {// 若接收包为0或丢包率为100%，则认为是网络不通
				mInfo.setState(false);
			}

			WybLog.syso("---ping:avgPingTime,shake,packetLoss:" + avgTime+","+shake+","+packetLoss);
		}
		return value;
	}
}
