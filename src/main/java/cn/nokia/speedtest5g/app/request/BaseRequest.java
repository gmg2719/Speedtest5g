package cn.nokia.speedtest5g.app.request;

import cn.nokia.speedtest5g.app.SpeedTest5g;

import com.android.volley.util.BasicUtil;

/**
 * 请求信息公用类
 * @author zwq
 *
 */
public class BaseRequest {
	//当前软件版本号
	private int versioncode;
	//当前软件版本名称
	private String versionname;
	
	private int version_code;
	
	private String version_name;
	
	public BaseRequest(){
		this.version_code = (Integer) BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false);
		this.versioncode  = this.version_code;
		this.version_name = BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), true).toString();
		this.versionname  = this.version_name;
	}
	
	public int getVersion_code() {
		return version_code;
	}
	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}
	public String getVersion_name() {
		return version_name;
	}
	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}
	public int getVersioncode() {
		return versioncode;
	}
	public void setVersioncode(int versioncode) {
		this.versioncode = versioncode;
	}
	public String getVersionname() {
		return versionname;
	}
	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}
}
