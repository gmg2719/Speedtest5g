package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * FTP地址返回
 *
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class ResponseFtpAddress extends BaseRespon {
	public Datas datas;

	public class Datas {
		public String downIp = "";
		public String downUser = "";
		public String downPasswd = "";
		public String downPath = "";
		public String uploadIp = "";
		public String uploadUser = "";
		public String uploadPasswd = "";
		public String downHostType = "";
		public String upHostType = "";
		public String downHostName = "";
		public String upHostName = "";
		public int downPort = -1;
		public int downThread = 3;
		public int uploadPort = -1;
		public int uploadThread = 1;
		public int uploadSize = -1;
		public int downLenght = -1;
	}
}
