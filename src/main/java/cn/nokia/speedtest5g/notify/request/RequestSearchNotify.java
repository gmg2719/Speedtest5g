package cn.nokia.speedtest5g.notify.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 请求车辆信息
 *
 * @author jinhaizheng
 */
public class RequestSearchNotify extends BaseRequest {
	// 搜索关键字
	private String notice_type;
	// 搜索关键字
	private String key;
	// 当前页
	private int page_num;
	// 每页条数:=0时，服务端默认为10条
	private int page_size;

	public String getNotice_type() {
		return notice_type;
	}

	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getPage_num() {
		return page_num;
	}

	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

}
