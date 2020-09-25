package cn.nokia.speedtest5g.app.respon;

public class Apk {
	private int code_name,upgrade_status;
	private String version_name,up_content,download_url,software_size,update_time;
	// 服务器当前时间System.currentTimeMillis();
	private long now_time;
	
	public int getCode_name() {
		return code_name;
	}
	public void setCode_name(int code_name) {
		this.code_name = code_name;
	}
	public int getUpgrade_status() {
		return upgrade_status;
	}
	public void setUpgrade_status(int upgrade_status) {
		this.upgrade_status = upgrade_status;
	}
	public String getVersion_name() {
		return version_name == null ? "" : version_name.replace("Beta", "");
	}
	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}
	public String getUp_content() {
		return up_content;
	}
	public void setUp_content(String up_content) {
		this.up_content = up_content;
	}
	public String getDownload_url() {
		return download_url;
	}
	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}
	public String getSoftware_size() {
		return software_size;
	}
	public void setSoftware_size(String software_size) {
		this.software_size = software_size;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public long getNow_time() {
		return now_time;
	}
	public void setNow_time(long now_time) {
		this.now_time = now_time;
	}
}
