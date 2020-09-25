package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * ping配置
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanPingConfig implements Serializable {

	public int id; //序号
	public int rid;
	public String showDesc; //描述
	public float threshold; //起始阈值
	public float endThreshold; //结束阈值
	public String updateTime;
}
