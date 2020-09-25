package cn.nokia.speedtest5g.emergent.response;

import java.io.Serializable;
import java.util.List;
/**
 * 应急站 指标数据
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class JJ_EmergentQuotaCellInfo implements Serializable {
	private String enodebId;//基站号
	private String ci;//小区号
	private String siteName;//基站名称
	private String cellName;//小区名称
	private String azimuth;//方位角
	private String cellState;//小区状态
	private String pci;//小区pci
	private String frqband;//工作频段
	private String earfcn;//中心载频信道号
	private String dual;//双工制式
	private String coverSence;//覆盖场景
	private String coverType;//覆盖类型	
	private String sfType;//室分类型
	private String belongOmc;//所属OMC	
	private String tac;//小区tac
	private List<JJ_EmergentQuotaCellDetailInfo>pmList;//参数列表
	private int selectPosition ;//默认选中位置数据---指标选择时间点
	public String getEnodebId() {
		return enodebId==null?"":enodebId;
	}
	public String getCi() {
		return ci==null?"":ci;
	}
	public String getSiteName() {
		return siteName==null?"":siteName;
	}
	public String getCellName() {
		return cellName==null?"":cellName;
	}
	public String getAzimuth() {
		return azimuth==null?"":azimuth;
	}
	public String getCellState() {
		return cellState==null?"":cellState;
	}
	public List<JJ_EmergentQuotaCellDetailInfo> getPmList() {
		return pmList;
	}
	public void setEnodebId(String enodebId) {
		this.enodebId = enodebId;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public void setAzimuth(String azimuth) {
		this.azimuth = azimuth;
	}
	public void setCellState(String cellState) {
		this.cellState = cellState;
	}
	public void setPmList(List<JJ_EmergentQuotaCellDetailInfo> pmList) {
		this.pmList = pmList;
	}
//	public int getSelectPosition() {
//		return selectPosition;
//	}
//	public void setSelectPosition(int selectPosition) {
//		this.selectPosition = selectPosition;
//	}
	public String getPci() {
		return pci==null?"":pci;
	}
	public String getFrqband() {
		return frqband==null?"":frqband;
	}
	public String getEarfcn() {
		return earfcn==null?"":earfcn;
	}
	public String getDual() {
		return dual==null?"":dual;
	}
	public String getCoverSence() {
		return coverSence==null?"":coverSence;
	}
	public String getCoverType() {
		return coverType==null?"":coverType;
	}
	public void setPci(String pci) {
		this.pci = pci;
	}
	public void setFrqband(String frqband) {
		this.frqband = frqband;
	}
	public void setEarfcn(String earfcn) {
		this.earfcn = earfcn;
	}
	public void setDual(String dual) {
		this.dual = dual;
	}
	public void setCoverSence(String coverSence) {
		this.coverSence = coverSence;
	}
	public String getSfType() {
		return sfType==null?"":sfType;
	}
	public String getBelongOmc() {
		return belongOmc==null?"":belongOmc;
	}
	public void setSfType(String sfType) {
		this.sfType = sfType;
	}
	public void setBelongOmc(String belongOmc) {
		this.belongOmc = belongOmc;
	}
	public void setCoverType(String coverType) {
		this.coverType = coverType;
	}
	public String getTac() {
		return tac==null?"":tac;
	}
	public void setTac(String tac) {
		this.tac = tac;
	}
	public int getSelectPosition() {
		return selectPosition;
	}
	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}
}
