package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 详情页ping数值排行配置
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanNpConfig implements Serializable {

	public String confType; //类型
	public int id; //序号
	public int rid;
	public String type;
	public float threshold; //起始阈值
	public float endThreshold; //结束阈值
	public String updateTime;
}
