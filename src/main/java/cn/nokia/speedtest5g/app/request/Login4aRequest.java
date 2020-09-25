package cn.nokia.speedtest5g.app.request;
/**
 * 登录请求信息
 * @author zwq
 *
 */
public class Login4aRequest extends BaseRequest {
	
	private int user_id;//	Integer		是		用户id
	private String login_name_4a;//	String		是		4A帐号名
	private String session_id;//一次登录会话标识
	
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getLogin_name_4a() {
		return login_name_4a;
	}
	public void setLogin_name_4a(String login_name_4a) {
		this.login_name_4a = login_name_4a;
	}
}
