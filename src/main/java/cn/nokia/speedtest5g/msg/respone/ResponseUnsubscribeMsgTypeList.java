package cn.nokia.speedtest5g.msg.respone;

import java.util.List;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 消息列表
 *
 * @author jinhaizheng
 */
public class ResponseUnsubscribeMsgTypeList extends BaseRespon {
	public List<MsgDetail> datas;
}