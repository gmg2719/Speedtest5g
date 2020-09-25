package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 删除日志请求类
 * @author JQJ
 *
 */
public class RequestRemoveLog extends BaseRequest{

	public long id; //速率测试LogID
	public String type; //删除标识  SINGLE：删除一个，ALL：删除全部
	public long userId; //用户ID

}
