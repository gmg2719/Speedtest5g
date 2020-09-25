package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 速率PK组列表
 *
 * @author jinhaizheng
 */
public class RequestSpeedPkGroupList extends BaseRequest {
	public int page_num;
	public int page_size;
	public String key;// 搜索关键字
}
