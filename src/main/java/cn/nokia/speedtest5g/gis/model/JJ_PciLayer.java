package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;

import com.baidu.mapapi.model.LatLng;

/**
 * pci图层对象
 * 
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class JJ_PciLayer implements Serializable {
	// 天线挂高,机械下倾角,电下倾角
	private String LINEHIGH = "", JXXQJ = "", DXQJ = "", CI = "", LAC = "";
	// GSM == CI 规划==eNodeB_ID
	private String area_type = "", site_type = "";// 电信24G用
	// 类型---
	// 0 GSM小区
	// 1 TDS小区
	// 2 LTE小区
	// 3 LTE规划
	// 4 铁塔-移动
	// 5 铁塔-联通
	// 6 铁塔-电信
	// 7 铁塔-全量
	// 8 铁塔-移动联通
	// 9 铁塔-移动电信
	// 10 铁塔-联通电信
	// 11 铁塔-移动联通电信
	// 12 铁塔-其他
	// 13高铁
	// 14 基站告警
	// 15电信2G
	// 16电信4G
	// 18室分图纸
	private int type;
	// 对应的key值--如 宏站，室分，设计，在建
	private String key;
	// 主要取对应的图片 0GSM宏站 1GSM室分 2TDS宏站.....
	//
	private int keyId;
	// 对应的表
	private String tabName;
	// 基站名称
	private String CELLNAME = "";
	// 基站对应的城市
	private String city;
	private int _id;
	// GPS经纬度
	private double LON, LAT;
	// 百度经纬度
	private double LONbd, LATbd;
	// 角度--方位角
	private float DIRECTION;
	// 经纬度对应类
	private LatLng llGps, llBd;
	// CI列表适配器相关用
	private boolean isSeleck;
	// 与当前位置的距离
	private Integer distance = -1;
	// 基站告警图层使用
	// enodbid
	private String enodebId;
	// 更新时间
	private String updateTime;
	// 频段
	private String band = "";
	// 频点
	private String pindian = "";
	// pci
	private int pci;
	// 新pci
	private int newPci = -1;
	// TAC
	private String TAC;
	// 厂商
	private String deviceName;

	public String getLINEHIGH() {
		return LINEHIGH;
	}

	public String getJXXQJ() {
		return JXXQJ;
	}

	public String getDXQJ() {
		return DXQJ;
	}

	public String getCI() {
		return CI == null ? "" : CI;
	}

	public String getLAC() {
		return LAC;
	}

	public String getArea_type() {
		return area_type;
	}

	public String getSite_type() {
		return site_type;
	}

	public int getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public int getKeyId() {
		return keyId;
	}

	public String getTabName() {
		return tabName;
	}

	public String getCELLNAME() {
		return CELLNAME;
	}

	public String getCity() {
		return city;
	}

	public int get_id() {
		return _id;
	}

	public double getLON() {
		return LON;
	}

	public double getLAT() {
		return LAT;
	}

	public double getLONbd() {
		return LONbd;
	}

	public double getLATbd() {
		return LATbd;
	}

	public float getDIRECTION() {
		return DIRECTION;
	}

	public LatLng getLlGps() {
		return llGps;
	}

	public LatLng getLlBd() {
		return llBd;
	}

	public boolean isSeleck() {
		return isSeleck;
	}

	public Integer getDistance() {
		return distance;
	}

	public String getEnodebId() {
		return enodebId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public String getBand() {
		return band;
	}

	public void setLINEHIGH(String lINEHIGH) {
		LINEHIGH = lINEHIGH;
	}

	public void setJXXQJ(String jXXQJ) {
		JXXQJ = jXXQJ;
	}

	public void setDXQJ(String dXQJ) {
		DXQJ = dXQJ;
	}

	public void setCI(String cI) {
		CI = cI;
	}

	public void setLAC(String lAC) {
		LAC = lAC;
	}

	public void setArea_type(String area_type) {
		this.area_type = area_type;
	}

	public void setSite_type(String site_type) {
		this.site_type = site_type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public void setCELLNAME(String cELLNAME) {
		CELLNAME = cELLNAME;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public void setLON(double lON) {
		LON = lON;
	}

	public void setLAT(double lAT) {
		LAT = lAT;
	}

	public void setLONbd(double lONbd) {
		LONbd = lONbd;
	}

	public void setLATbd(double lATbd) {
		LATbd = lATbd;
	}

	public void setDIRECTION(float dIRECTION) {
		DIRECTION = dIRECTION;
	}

	public void setLlGps(LatLng llGps) {
		this.llGps = llGps;
	}

	public void setLlBd(LatLng llBd) {
		this.llBd = llBd;
		if (llBd != null) {
			setLATbd(llBd.latitude);
			setLONbd(llBd.longitude);
		}

	}

	public void setSeleck(boolean isSeleck) {
		this.isSeleck = isSeleck;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public void setEnodebId(String enodebId) {
		this.enodebId = enodebId;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getPindian() {
		return pindian;
	}

	public void setPindian(String pindian) {
		this.pindian = pindian;
	}

	public int getPci() {
		return pci;
	}

	public void setPci(int pci) {
		this.pci = pci;
	}

	public int getNewPci() {
		return newPci;
	}

	public void setNewPci(int newPci) {
		this.newPci = newPci;
	}

	public String getTAC() {
		return TAC;
	}

	public void setTAC(String tAC) {
		TAC = tAC;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}
