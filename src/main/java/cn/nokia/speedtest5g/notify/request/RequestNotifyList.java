package cn.nokia.speedtest5g.notify.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 公告列表请求信息
 *
 * @author jinhaizheng
 */
public class RequestNotifyList extends BaseRequest {
	// 当前页
	private int page_num;
	// 每页条数:=0时，服务端默认为10条
	private int page_size;
	// 公告类型:若为"0"：默认获取全部类型的公告，若传入具体类型则为加载该类型的更多公告
	private String notice_type;

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

	public String getNotice_type() {
		return notice_type;
	}

	public void setNotice_type(String notice_type) {
		this.notice_type = notice_type;
	}
}
