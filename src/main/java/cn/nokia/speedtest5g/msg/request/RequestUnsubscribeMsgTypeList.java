package cn.nokia.speedtest5g.msg.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 已退订消息类型列表
 * 
 * @author jinhaizheng
 */
public class RequestUnsubscribeMsgTypeList extends BaseRequest {
	public int page_num;
	public int page_size = 15;
}
