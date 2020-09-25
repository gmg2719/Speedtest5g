package cn.nokia.speedtest5g.wifi.other;

import java.io.Serializable;
import java.util.List;
import cn.nokia.speedtest5g.app.KeyValue;

/**
 * 信号分析结果
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class WifiAnalysisInfo implements Serializable{

	//当前连接
	public String wifiName;
	//手机品牌
	public String phoneModel;
	//手机IP
	public String phoneIp;
	//手机MAC
	public String phoneMac;
	//路由器IP
	public String wifiIp;
	//路由器MAC
	public String wifiMac;
	//当前频道 2.4G,5G
	public String pd;
	//信号强度
	public int dbm;
	//连接速度
	public int speed;
	//频率
	public int frequency;
	//DNS
	public String dns;
	//掩码
	public String ym;
	//信道
	public int xd;
	//星级
	public int grade;
	//同频干扰个数
	public int tpCount = 0;
	//同频强干扰个数
	public int tpCounts = 0;
	//同频干扰列表
	public List<WifiItem> tpList;
	//邻频干扰个数
	public int lpCount = 0;
	//邻频强干扰个数
	public int lpCounts = 0;
	//邻频干扰列表
	public List<WifiItem> lpList;
	//标星列表
	public List<KeyValue> gradeList;
}
