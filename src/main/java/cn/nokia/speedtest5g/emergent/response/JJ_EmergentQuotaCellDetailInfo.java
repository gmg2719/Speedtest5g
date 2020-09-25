package cn.nokia.speedtest5g.emergent.response;

import java.io.Serializable;
/**
 * 应急站指标小区详细数据
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class JJ_EmergentQuotaCellDetailInfo implements Serializable{
	private String enbha05;//RRC连接尝试次数
	private String enbha06;//RRC连接建立成功次数
	private String eu0101;//RRC连接建立成功率
	private String enbhb05;//E-RAB建立请求数
	private String enbhb06;//E-RAB建立成功数
	private String eu0102;//E-RAB建立成功率
	private String eu0103;//无线接通率>96%
	private String eu0223;//无线掉线率<3%
	private String eu0202;//E-RAB掉线率<5%
	private String eu0304;//eNB间切换成功率
	private String eu0305;//eNB内切换成功率
	private String eu0306;//切换成功率>98%
	private String eu0505;//小区用户面上行字节数(KByte)
	private String eu0506;//小区用户面下行字节数(KByte)
	private String eu0535;//上行用户平均速率
	private String eu0536;//下行用户平均速率
	private String pmCellTime;//开始时间
	private String eu0416;//VOLTE上行丢包率
	private String enbhh061;//上行RTP丢包数（个）
	private String enbha04;//最大RRC连接数
	private String eu0529;//上行PRB利用率%
	private String eu0530;//下行PRB利用率%
	
	public String getEnbha05() {
		return enbha05;
	}
	public String getEnbha06() {
		return enbha06;
	}
	public String getEu0101() {
		return eu0101;
	}
	public String getEnbhb05() {
		return enbhb05;
	}
	public String getEnbhb06() {
		return enbhb06;
	}
	public String getEu0102() {
		return eu0102;
	}
	public String getEu0103() {
		return eu0103;
	}
	public String getEu0223() {
		return eu0223;
	}
	public String getEu0202() {
		return eu0202;
	}
	public String getEu0304() {
		return eu0304;
	}
	public String getEu0305() {
		return eu0305;
	}
	public String getEu0306() {
		return eu0306;
	}
	public String getEu0505() {
		return eu0505;
	}
	public String getEu0506() {
		return eu0506;
	}
	public String getEu0535() {
		return eu0535;
	}
	public String getEu0536() {
		return eu0536;
	}
	public String getPmCellTime() {
		return pmCellTime;
	}
	public String getEu0416() {
		return eu0416;
	}
	public String getEnbhh061() {
		return enbhh061;
	}
	public String getEnbha04() {
		return enbha04;
	}
	public String getEu0529() {
		return eu0529;
	}
	public String getEu0530() {
		return eu0530;
	}
	public void setEnbha05(String enbha05) {
		this.enbha05 = enbha05;
	}
	public void setEnbha06(String enbha06) {
		this.enbha06 = enbha06;
	}
	public void setEu0101(String eu0101) {
		this.eu0101 = eu0101;
	}
	public void setEnbhb05(String enbhb05) {
		this.enbhb05 = enbhb05;
	}
	public void setEnbhb06(String enbhb06) {
		this.enbhb06 = enbhb06;
	}
	public void setEu0102(String eu0102) {
		this.eu0102 = eu0102;
	}
	public void setEu0103(String eu0103) {
		this.eu0103 = eu0103;
	}
	public void setEu0223(String eu0223) {
		this.eu0223 = eu0223;
	}
	public void setEu0202(String eu0202) {
		this.eu0202 = eu0202;
	}
	public void setEu0304(String eu0304) {
		this.eu0304 = eu0304;
	}
	public void setEu0305(String eu0305) {
		this.eu0305 = eu0305;
	}
	public void setEu0306(String eu0306) {
		this.eu0306 = eu0306;
	}
	public void setEu0505(String eu0505) {
		this.eu0505 = eu0505;
	}
	public void setEu0506(String eu0506) {
		this.eu0506 = eu0506;
	}
	public void setEu0535(String eu0535) {
		this.eu0535 = eu0535;
	}
	public void setEu0536(String eu0536) {
		this.eu0536 = eu0536;
	}
	public void setPmCellTime(String pmCellTime) {
		this.pmCellTime = pmCellTime;
	}
	public void setEu0416(String eu0416) {
		this.eu0416 = eu0416;
	}
	public void setEnbhh061(String enbhh061) {
		this.enbhh061 = enbhh061;
	}
	public void setEnbha04(String enbha04) {
		this.enbha04 = enbha04;
	}
	public void setEu0529(String eu0529) {
		this.eu0529 = eu0529;
	}
	public void setEu0530(String eu0530) {
		this.eu0530 = eu0530;
	}
	
	
}
