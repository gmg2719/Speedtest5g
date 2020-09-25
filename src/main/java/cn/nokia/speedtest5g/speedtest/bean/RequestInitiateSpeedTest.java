package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 速率测试发起请求
 * @author JQJ
 *
 */
public class RequestInitiateSpeedTest extends BaseRequest {

	public String apnftpId; //服务器id
	public String userId;//用户ID
	public String operator;//运营商
	public String province;//省
	public String city;//地市
	public String ip;//IP
	public String port;//端口
	public String ftpType;//类型

}
