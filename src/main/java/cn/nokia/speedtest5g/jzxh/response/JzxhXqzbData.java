package cn.nokia.speedtest5g.jzxh.response;

import java.io.Serializable;
import java.util.List;

import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellDetailInfo;

/**
 * 基站信号小区指标查询返回
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class JzxhXqzbData implements Serializable{

	public String enodebId;//	String	datas	是		基站号
	public String ci;//	String	datas	是		小区号
	public String siteName;//	String	datas	是		基站名称
	public String cellName;//	String	datas	是		小区名称
	public String azimuth;//	String	datas	是		方位角
	public String pci;//	String	datas	是		小区pci
	public String frqband;//	String	datas	是		工作频段
	public String earfcn;//	String	datas	是		中心载频信道号
	public String dual;//	String	datas	是		双工制式
	public String coverType;//	String	datas	是		覆盖类型
	public String tac;//	String	datas	是		小区tac
	public List<JJ_EmergentQuotaCellDetailInfo> pmList;//	Array	datas	否		参数列表

}
