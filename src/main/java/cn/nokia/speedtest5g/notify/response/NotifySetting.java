package cn.nokia.speedtest5g.notify.response;

/**
 * 公告提醒开关设置
 *
 * @author jinhaizheng
 */
public class NotifySetting {
	// 公告类型
	private String noticeType;
	// 公告类型名称
	private String noticeTypeName;
	// 是否接受该类型公告
	private boolean isReceive;

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getNoticeTypeName() {
		return noticeTypeName;
	}

	public void setNoticeTypeName(String noticeTypeName) {
		this.noticeTypeName = noticeTypeName;
	}

	public boolean isReceive() {
		return isReceive;
	}

	public void setReceive(boolean isReceive) {
		this.isReceive = isReceive;
	}
}
