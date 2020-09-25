package cn.nokia.speedtest5g.msg.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 请求消息列表报文
 * 
 * @author jinhaizheng
 */
public class RequestMsgList extends BaseRequest {
	public int page_num;
	public int page_size = 20;
}
