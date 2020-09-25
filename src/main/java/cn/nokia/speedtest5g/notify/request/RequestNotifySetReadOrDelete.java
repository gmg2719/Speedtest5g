package cn.nokia.speedtest5g.notify.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 公告标记为已读或删除请求参数
 *
 * @author jinhaizheng
 */
public class RequestNotifySetReadOrDelete extends BaseRequest {
	// 公告ID
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
