package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 网络配置
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanWlConfig implements Serializable {

	public int id; //序号
	public int rid;
	public String netType; //网络类型
	public float threshold; //起始阈值
	public float endThreshold; //结束阈值
	public String showDesc;
	public String img;//对应图片
	public String updateTime;
}
