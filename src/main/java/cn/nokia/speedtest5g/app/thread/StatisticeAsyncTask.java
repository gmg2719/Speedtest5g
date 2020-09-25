package cn.nokia.speedtest5g.app.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.request.NetStatistics;
import cn.nokia.speedtest5g.app.bean.StatisticsInfo;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.HttpPostUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 统计页面功能模块的点击量
 * @author zwq
 *
 */
@SuppressWarnings("unchecked")
public class StatisticeAsyncTask extends AsyncTask<String, Object, Boolean> {

	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			String code = arg0[1];
			StatisticsInfo info = new StatisticsInfo();
			info.setNameClass(arg0[0]);
			info.setModule(code.equals(getString(R.string.code_exit_no)) ? getString(R.string.code_exit) : code);
			info.setTimes(TimeUtil.getInstance().getNowTimeSS());
			String userId = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
			if (userId == null || userId.isEmpty()) {
				userId = "0";
			}
			info.setUserId(userId);
			String userPhone = Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
			if (userPhone == null || userPhone.isEmpty()) {
				userPhone = "0";
			}
			info.setUserPhone(userPhone);
			info.setImei(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
			info.setState(0);
			
			boolean isContains = false;
			String times;
			//同一分钟不做统计
			ArrayList<StatisticsInfo> queryObj = (ArrayList<StatisticsInfo>)DbHandler.getInstance().queryAll(StatisticsInfo.class);
			if (queryObj != null && queryObj.size() > 0) {
				for (int i = 0; i < queryObj.size(); i++) {
					if (queryObj.get(i).getModule().equals(code)) {
						times = queryObj.get(i).getTimes();
						times = times.substring(0, times.length() - 3);
						if (info.getTimes().startsWith(times)) {
							isContains = true;
							break;
						}
					}
				}
			}
			if (!isContains) {
				DbHandler.getInstance().insert(info);
			}
			if (code.equals(getString(R.string.code_login)) || code.equals(getString(R.string.code_exit_no)) || code.equals(getString(R.string.code_exit)) ) {
				uploadData(code);
			}
		} catch (Exception e) {
			WybLog.syso("错误" + e.getMessage());
		}
		return null;
	}
	
	private String getString(int id){
		return SpeedTest5g.getContext().getString(id);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
	
//	/**message_type=1消息类型暂时为1*/
//	private String messagetype =  "1";		
//	//读取入网消息内容
//	private void readRwMsg(String userId) {
//		if (userId == null || userId.equals("0") || userId.equals("00")) {
//			return;
//		}
//		String doGet = HttpGetUtil.getHttpGetUtil().doGet(MyApplication.getContext().NetWorkUtilNow.getInstances().getToIp() + MyApplication.getContext().getString(R.string.URL_RW_TOAST) + userId);
//		if (doGet != null) {
//			RwToastInfo dataJson = JsonHandler.getHandler().getTarget(doGet, RwToastInfo.class);
//			if (dataJson != null && dataJson.isRs()) {
//				if (dataJson.getDatas() != null || dataJson.getDatas().size() > 0) {
//					new MyToastThread(dataJson, 0, userId).start();
//				}
//			}
//		}
//		
//		String timeLastReq = SharedPreHandler.getShared(MyApplication.getContext()).getStringShared(TypeKey.getInstance().KEY_TOAST, "1970-01-01 08:00:00");
//		String read = HttpGetUtil.getHttpGetUtil().doGet(MyApplication.getContext().NetWorkUtilNow.getInstances().getToIp() +
//									MyApplication.getContext().getString(R.string.URL_TOAST) + userId +
//				 							"&update_time=" + NetWorkUtilNow.getInstances().getUtf(timeLastReq) +
//				 							"&message_type=" + messagetype);
//		if (read != null) {
//			ToastRespon dataJson = JsonHandler.getHandler().getTarget(read, ToastRespon.class);
//			if (dataJson != null && dataJson.isRs()) {
//				if (dataJson.getDatas() != null || dataJson.getDatas().size() > 0) {
//					new MyToastThread(dataJson, 0, userId).start();
//				}
//			}
//		}
//	}
	
	/**
	 * 上传模块统计数据
	 */
	private void uploadData(String code){
		ArrayList<StatisticsInfo> queryStatistics = (ArrayList<StatisticsInfo>) DbHandler.getInstance().queryObj(StatisticsInfo.class, "state=?", new Integer[]{0});
		if (queryStatistics != null && queryStatistics.size() > 0) {
			ArrayList<NetStatistics> arrNet = new ArrayList<NetStatistics>();
			NetStatistics netInfo;
			for (StatisticsInfo mInfo : queryStatistics) {
				netInfo = new NetStatistics();
				netInfo.setMobileIdentity(mInfo.getImei());
				netInfo.setModuleId(mInfo.getModule());
				netInfo.setOptionTime(mInfo.getTimes());
				netInfo.setUserId(mInfo.getUserId());
				arrNet.add(netInfo);
			}
			
			if (arrNet.size() > 0) {
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("datas", JsonHandler.getHandler().toJson(arrNet)));
				String doGet = HttpPostUtil.getHttpGetUtil().doPost(NetWorkUtilNow.getInstances().getToIp() + 
							SpeedTest5g.getContext().getString(R.string.URL_STATISTICE), nameValuePair );
				//提交成功，更新状态
				if (doGet != null && doGet.length() > 0) {
					BaseRespon dataJson = JsonHandler.getHandler().getTarget(doGet, BaseRespon.class);
					if (dataJson != null && dataJson.isRs()) {
						//上传成功更新状态
						for (StatisticsInfo statistics : queryStatistics) {
							statistics.setState(1);
							DbHandler.getInstance().updateObj(statistics);
						}
						if (code.equals(getString(R.string.code_exit_no))) {
							System.exit(0);
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					}
				}
			}
		//退出登录...
		}else if (code.equals(getString(R.string.code_exit_no))) {
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
