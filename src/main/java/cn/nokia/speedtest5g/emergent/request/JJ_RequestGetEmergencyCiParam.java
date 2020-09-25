package cn.nokia.speedtest5g.emergent.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 获取应急站单小区小时指标请求类
 * @author xujianjun
 *
 */
public class JJ_RequestGetEmergencyCiParam extends BaseRequest{
	private int userid;
	private String ci;
	private String cellTime;//小区时间，如2018-11-01
	public int getUserid() {
		return userid;
	}
	public String getCi() {
		return ci;
	}
	public String getCellTime() {
		return cellTime;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public void setCellTime(String cellTime) {
		this.cellTime = cellTime;
	}
}
