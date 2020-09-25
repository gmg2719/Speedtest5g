package cn.nokia.speedtest5g.app.respon;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseRespon implements Serializable{
	//返回数据形式--成功、失败
	private boolean rs;
	//失败提示内容
	private String msg;
	
	public boolean isRs() {
		return rs;
	}
	public void setRs(boolean rs) {
		this.rs = rs;
	}
	public String getMsg() {
		return msg == null ? "" : msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
