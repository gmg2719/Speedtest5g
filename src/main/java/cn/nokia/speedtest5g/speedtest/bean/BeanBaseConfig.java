package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanBaseConfig implements Serializable {
	
	public ArrayList<BeanFlowConfig> llConf;

	public ArrayList<BeanRkConfig> rkConf;

	public ArrayList<BeanNpConfig> npConf;
	
	public ArrayList<BeanRsrpConfig> rsrpConf; //信号
	
	public ArrayList<BeanDkConfig> dkConf; //宽带
	
	public ArrayList<BeanPingConfig> pingConf; //ping
	
	public ArrayList<BeanWlConfig> wlConfigs; //网络

}
