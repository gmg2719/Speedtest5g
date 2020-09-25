package cn.nokia.speedtest5g.speedtest.bean;

import cn.nokia.speedtest5g.app.request.BaseRequest;

/**
 * 周边网速
 * @author JQJ
 *
 */
public class RequestPeriphery extends BaseRequest {
    
    public double longitude; //经度
    public double latitude; //纬度
	public String dis; //比例尺

}
