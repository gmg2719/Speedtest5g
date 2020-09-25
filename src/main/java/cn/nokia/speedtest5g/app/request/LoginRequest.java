package cn.nokia.speedtest5g.app.request;
/**
 * 登录请求信息
 * @author zwq
 *
 */
public class LoginRequest extends BaseRequest {
	private String loginName;//	String		是		登录用户名
	private String password;//	String		是		明文密码
	private String password1;//	String		是		密文密码
	private String imsi;//		String	是		手机imsi
	private String imei;//
	private boolean is4a;//	Boolean		否		是否启用4A帐号登录

	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public boolean isIs4a() {
		return is4a;
	}
	public void setIs4a(boolean is4a) {
		this.is4a = is4a;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword1() {
		return password1;
	}
	public void setPassword1(String password1) {
		this.password1 = password1;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
