package cn.nokia.speedtest5g.jzxh.util;

/**
 * 邻区信息
 * @author zwq
 *
 */
public class SignalAdjItem {

	//小区名称---Integer.MAX_VALUE
	public String cellName = "-";
	//GSM-cid
	public String GSM_CID = "-";
	//GSM-LAC
	public String GSM_LAC = "-";
	//GSM_RXLEV
	public String GSM_RXLEV = "-";
	//TD-CI
	public String TD_CI = "-";
	//TD-LAC
	public String TD_LAC = "-";
	//TD-PCCPCH
	public String TD_PCCPCH = "-";
	//LTE_TIME
	public String LTE_TIME = "-";
	//LTE_TAC
	public String LTE_TAC = "-";
	//LTE_PCI
	public String LTE_PCI = "-";
	//LTE_ENB
	public String LTE_ENB = "-";
	//LTE_CELLID
	public String LTE_CELLID = "-";
	//LTE_RSRP
	public String LTE_RSRP = "-";
	//LTE_SINR
	public String LTE_SINR = "-";
	//LTE_频点
	public String LTE_PD = "-";
	//LTE_BAND
	public String LTE_BAND = "-";
	//LTE-RSRP颜色
	public int LTE_RSRP_COLOR;
	//LTE_频点颜色
	public int LTE_PD_COLOR;
	//LTE_PCI颜色
	public int LTE_PCI_COLOR;
	//GSM_RXLEV颜色
	public int GSM_RXLEV_COLOR;
	//TD-PCCPCH颜色
	public int TD_PCCPCH_COLOR;
	
	//NR_PCI
	public String NR_PCI = "-";
	//NR_RSRP
	public String NR_RSRP = "-";
	//NR_SINR
	public String NR_SINR = "-";
	//NR_EARFCN
	public String NR_EARFCN = "-";
	//NR_BAND
	public String NR_BAND = "-";
	
	public int DB_ID = 0;
	//模3余值
	public String mo3 = "";
	//LTE_模3颜色
	public int LTE_MO3_COLOR;
	
}
