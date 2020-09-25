package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

/**
 * 速率PK创建组
 *
 * @author jinhaizheng
 */
public class RequestSpeedPkCreateGroup extends BaseRequest {
	public String testId = UtilHandler.getInstance().getTestId();
	public String rateGroupName;
}
