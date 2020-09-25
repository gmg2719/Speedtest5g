package cn.nokia.speedtest5g.app.respon;

/**
 * 短信验证码登入响应信息
 * 
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class SmsCodeLoginResponse extends BaseRespon {

	private SmsCodeLoginResponseDatas datas;

	public SmsCodeLoginResponseDatas getDatas() {
		return datas;
	}

	public void setDatas(SmsCodeLoginResponseDatas datas) {
		this.datas = datas;
	}

	public class SmsCodeLoginResponseDatas extends LoginRespon {
		// 超期时间（天）
		private int overdue_time;
		// 服务器当前时间
		private long now_time;
		// 短信码验证登入时间
		private long msgLogin_time;
		// 是否需要刷新视图
		private boolean ifRefresh;

		public int getOverdue_time() {
			return overdue_time;
		}

		public void setOverdue_time(int overdue_time) {
			this.overdue_time = overdue_time;
		}

		public long getNow_time() {
			return now_time;
		}

		public void setNow_time(long now_time) {
			this.now_time = now_time;
		}

		public long getMsgLogin_time() {
			return msgLogin_time;
		}

		public void setMsgLogin_time(long msgLogin_time) {
			this.msgLogin_time = msgLogin_time;
		}

		public boolean isRefresh() {
			return ifRefresh;
		}

		public void setIfRefresh(boolean ifRefresh) {
			this.ifRefresh = ifRefresh;
		}

	}
}
