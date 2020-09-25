package cn.nokia.speedtest5g.app.request;

/**
 * 子票据验证请求数据
 * @author zwq
 *
 */
public class TokenRequest extends BaseRequest {
	public String imei;//	String		是		手机imei
	public String imsi;//	String		是		手机imsi
	public String subToken;//	String		是		全网平台传递过来的token
}
