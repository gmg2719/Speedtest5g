package cn.nokia.speedtest5g.jzxh.request;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 根据小区返回最近的小区指标列表
 * @author zwq
 *
 */
public class JzxhXqzbRequest extends BaseRequest {
	public String userid;//	Integer	root	是		用户ID
	public String ci;//	String	root	否		应急站小区号，如460-00-123456-130
	public Integer pageNo = 1;//	Integer	root	否		默认1，页码
	public Integer pageSize = 7;//	Integer	root	否		默认7，单页记录数
}
