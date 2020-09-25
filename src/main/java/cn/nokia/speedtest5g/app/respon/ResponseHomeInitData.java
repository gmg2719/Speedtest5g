package cn.nokia.speedtest5g.app.respon;

import java.util.List;

import cn.nokia.speedtest5g.msg.respone.MsgDetail;

/**
 * 首页
 *
 * @author jinhaizheng
 */
public class ResponseHomeInitData extends BaseRespon {
	public Datas datas;

	public class Datas {

		public boolean isTip;// 是否显示引导
		public String menuCodes;// 我的应用编码，用逗号隔开，如：73,75,610,5800,5900,6000,6200
		public List<MsgDetail> msgList; // 消息列表
	}

}
