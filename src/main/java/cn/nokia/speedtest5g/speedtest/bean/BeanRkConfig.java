package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * 测速结果页下载超过多少用户排行配置
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanRkConfig implements Serializable {

	public String id; //序号
	public float threshold; //起始阈值
	public float endThreshold; //结束阈值
	public String updateTime;
}
