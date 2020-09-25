package cn.nokia.speedtest5g.notify.response;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 系统公告消息
 *
 * @author jinhaizheng
 */
public class ResponseSystemUnreadMsg extends BaseRespon {

	private Datas datas;

	public Datas getDatas() {
		return datas;
	}

	public void setDatas(Datas datas) {
		this.datas = datas;
	}

	public class Datas {
		// 系统公告是否展示
		private boolean isShow;
		// 公告功能入口右上角否标红
		private boolean isRed;
		// 公告详情
		private NotifyInfo noticeInfo;

		public boolean isShow() {
			return isShow;
		}

		public void setShow(boolean isShow) {
			this.isShow = isShow;
		}

		public boolean isRed() {
			return isRed;
		}

		public void setRed(boolean isRed) {
			this.isRed = isRed;
		}

		public NotifyInfo getNoticeInfo() {
			return noticeInfo;
		}

		public void setNoticeInfo(NotifyInfo noticeInfo) {
			this.noticeInfo = noticeInfo;
		}
	}
}
