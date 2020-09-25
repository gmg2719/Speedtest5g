package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 预估流量请求
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanSpeedRanking implements Serializable {

	public String netType; //网络类型
	public String speedId; //ID
	public String headAddr; //头像
	public String operator; //运营商
	public String userName; //用户名
	public String phoneModel; //手机型号
	public String downSpeedAvg; //下载平均速率

}
