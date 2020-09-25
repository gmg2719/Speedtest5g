package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.request.BaseRequest;

import com.android.volley.util.SharedPreHandler;

/**
 * ftp历史测试数据请求
 * @author xujianjun
 *
 */
public class RequestFtpHistory extends BaseRequest{
	private String imei = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), "");
	public int pageSize = 15;
	private int pageNo;
	private long userid;
	public String testId;
	public String key;
	public String startTime;
	public String endTime;
	
	public String getImei() {
		return imei;
	}
	public int getPageNo() {
		return pageNo;
	}
	public long getUserid() {
		return userid;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
}
