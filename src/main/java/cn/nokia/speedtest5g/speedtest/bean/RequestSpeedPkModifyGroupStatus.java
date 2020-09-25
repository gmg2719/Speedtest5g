package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 速率PK历史详情数据请求
 *
 * @author jinhaizheng
 */
public class RequestSpeedPkModifyGroupStatus extends BaseRequest {
	public long id;// pk组id
	public String regionName;// 地市
	/**
	 * 0:解散 2：退出 3：开始 4：参与 5:完成（速率测试开始后，中途退出测试）
	 */
	public int status;
}
