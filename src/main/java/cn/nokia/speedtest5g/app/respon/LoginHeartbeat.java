package cn.nokia.speedtest5g.app.respon;
/**
 * 登录成功后请求心跳持续判断是否在线
 * @author zwq
 *
 */
public class LoginHeartbeat {
	
	public String session_id;//一次会话ID
	
	public String status;//状态  0未登录 1已登录 2已过期 3已退出登录 4被退出 418会话超时，请重新登录
	
	public LoginHeartbeat(String status){
		this.status = status;
	}
}
