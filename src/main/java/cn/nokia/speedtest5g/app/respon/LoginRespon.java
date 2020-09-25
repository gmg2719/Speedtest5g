package cn.nokia.speedtest5g.app.respon;

import java.io.Serializable;

/**
 * 登录返回内容
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class LoginRespon implements Serializable{
//	"user_id ":"1803","login_name":"test88","user_name":"test88","phone":"18859232938",
//	"menu_ids":"10,1001",
//	"depart_codes":"10,1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013"}
	
	private String user_id,login_name,user_name,phone,menu_ids,depart_codes;
//	private String 4aUserName;
	//是否是4a登录
    private boolean is4a;
    //是否需要登录
    private boolean need_sms_check;
    //一次登录会话标识
    private String session_id;
    
    public String bind_ip;//	String	datas	否		服务器类型，1-正式地址，2-预发布地址，3-测试地址
    
    public String provinceTag;//登录区域区分
    //菜单编码--传输第三方应用使用
    public String menuCode;
    
	public boolean isNeed_sms_check() {
		return need_sms_check;
	}

	public void setNeed_sms_check(boolean need_sms_check) {
		this.need_sms_check = need_sms_check;
	}

	public String getSession_id() {
		return session_id == null ? "" : session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public boolean isIs4a() {
		return is4a;
	}

	public void setIs4a(boolean is4a) {
		this.is4a = is4a;
	}
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMenu_ids() {
		return menu_ids == null ? "" : menu_ids;
	}

	public void setMenu_ids(String menu_ids) {
		this.menu_ids = menu_ids;
	}

	public String getDepart_codes() {
		return depart_codes == null ? "" : depart_codes;
	}

	public void setDepart_codes(String depart_codes) {
		this.depart_codes = depart_codes;
	}
}
