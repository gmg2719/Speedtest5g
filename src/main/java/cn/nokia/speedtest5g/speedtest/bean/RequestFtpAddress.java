package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 获取FPT服务器地址
 *
 * @author jinhaizheng
 */
public class RequestFtpAddress extends BaseRequest {
	public String uploadHostType;
	public String downloadHostType;
	public String uploadFtpIp;
	public String downloadFtpIp;
}
