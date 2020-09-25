package cn.nokia.speedtest5g.msg.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 消息管理
 * 
 * @author jinhaizheng
 */
public class RequestMsgManage extends BaseRequest {
	// 操作类型:1：删除 2：退订 3 订阅
	public int opType;
	public long msgId;
}
