package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 信号排行配置
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanRsrpConfig implements Serializable {

	public int id; //序号
	public int rid;
	public float threshold; //起始阈值
	public float endThreshold; //结束阈值
	public String showDesc;
	public String signalDesc;
	public String updateTime;
}
