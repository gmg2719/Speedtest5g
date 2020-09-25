package cn.nokia.speedtest5g.app.respon;

/**
 * 获取短信验证码响应信息
 * 
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class SmsCodeResponse extends BaseRespon {

	private SmsCodeResponseDatas datas;

	public SmsCodeResponseDatas getDatas() {
		return datas;
	}

	public void setDatas(SmsCodeResponseDatas datas) {
		this.datas = datas;
	}

	public class SmsCodeResponseDatas extends SessionBean {
		public String sms_id; //短信ID
		public String user_id; //用户Id
		public String smsCode; //验证码
		public String mobile; //手机号
	}
}
