package cn.nokia.speedtest5g.app.request;

/**
 * 短信验证码登入请求信息
 *
 * @author jinhaizheng
 */
public class RequestSmsCodeLogin extends BaseRequest {

	public String mobile; //手机号
	public String uuid;
	public String imei;
	public String imsi;
	public boolean overdue; // true-超期（手机端需要重新获取验证码）， false-未超期否第一次发送短信，1：手机端第一次发送短信
	// 2：手机端已经发过短信，
	public String captcha; // 短信验证码
	public String smsId; // 短信序号
	public String sessionId;// 一次会话ID
	public String userId; //用户Id

}
