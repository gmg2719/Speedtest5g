package cn.nokia.speedtest5g.wifi.bean;

import java.io.Serializable;

/**
 * WifiArpBean
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class WifiArpBean implements Serializable{

	public String ip; //ip

	public String mac; //mac

	public long pingTime; //pingTime

	public float packetLoss; //丢包

	public String manufactor; //厂家

	public String hostName; //主机名
}
