package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 预估流量请求
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanFlowConfig implements Serializable {

	public String operator; //运营商
	public String netType; //网络类型
	public String hostAddr; //IP地址
	public float upFlow; //上传预估流量
	public float downFlow; //下载预估流量
	public String updateTime; //更新时间

}
