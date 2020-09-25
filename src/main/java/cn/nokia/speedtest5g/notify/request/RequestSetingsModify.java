package cn.nokia.speedtest5g.notify.request;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.app.request.BaseRequest;
import cn.nokia.speedtest5g.notify.response.NotifySetting;

/**
 * 设置修改
 *
 * @author jinhaizheng
 */
public class RequestSetingsModify extends BaseRequest {
	private List<NotifySetting> notifySettings = new ArrayList<>();

	public List<NotifySetting> getNotifySettings() {
		return notifySettings;
	}

	public void setNotifySettings(List<NotifySetting> notifySettings) {
		this.notifySettings = notifySettings;
	}
}
