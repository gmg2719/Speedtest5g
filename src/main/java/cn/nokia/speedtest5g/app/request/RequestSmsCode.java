package cn.nokia.speedtest5g.app.request;

/**
 * 短信验证码请求
 * @author JQJ
 *
 */
public class RequestSmsCode extends BaseRequest{
	public String mobile;
	public String imei;
	public String imsi;
	public String userId; //游客id
	@Override
	public String toString() {
		return "RequestSmsCode [mobile=" + mobile + ", imei=" + imei + ", imsi=" + imsi + "]";
	}
}
