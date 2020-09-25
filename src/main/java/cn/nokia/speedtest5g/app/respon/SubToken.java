package cn.nokia.speedtest5g.app.respon;

import java.io.Serializable;


/**
 * 子票据返回的所有信息
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class SubToken implements Serializable{
	
	
	public String user_id;//	Integer	datas	否		用户ID
	public String login_name;//	String	datas	否		登录名
	public String user_name;//	String	datas	否		用户名
	public String phone;//	String	datas	否		手机号
	public String menu_ids;//	String	datas	否		菜单id
	public String depart_codes;//	String	datas	否		区域id
	public String bind_ip;//	String	datas	否		服务器类型，1-正式地址，2-预发布地址，3-测试地址
	public String account;//	String	datas	是		全网手机平台账号
	public String mobile;//	String	datas	是		全网手机平台账号对应手机号
	public String salveAccount;//	String	datas	是		全网手机平台账号的从账号
	public String session_id;//	String	datas	是		一次登录会话标识
	public String provinceTag;//登录区域区分
}
