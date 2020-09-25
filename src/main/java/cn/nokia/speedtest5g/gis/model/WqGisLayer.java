package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;
import com.baidu.mapapi.model.LatLng;

/**
 * 查询后的数据，用来显示图层相关信息
 * 
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class WqGisLayer implements Serializable {
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI", "LAC"},
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI", "LAC"},
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI", "LAC"},
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI", "LAC"},
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI"},
	// new String[]{"_id", "LON", "LAT", "CELLNAME", "DIRECTION", "JXXQJ",
	// "DXQJ", "LINEHIGH", "CI"},
	// new String[]{"_id", "LON", "LAT", "eNodeB_NAME", "DIRECTION", "XQJ",
	// "LINEHIGH", "eNodeB_ID"},

	//因邻区功能
	private LinquInfo mLinqu;

	// 天线挂高,机械下倾角,电下倾角
	private String LINEHIGH = "", JXXQJ = "", DXQJ = "", CI = "", LAC = "";
	// GSM == CI 规划==eNodeB_ID
	
	private String area_type = "", site_type = "";//电信24G用
	
	public String PCI = "";
	
	public String TAC = "";

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
	// 17邻区关系
	// 18室分图纸
	// 19RRU
	// 23应急站
	// 24应急盒子
	// 25共站
	// 26-5G小区
	// 27应急盒子-网桥
	private int type;
	// 对应的key值--如 宏站，室分，设计，在建
	private String key;

	// 主要取对应的图片 0GSM宏站 1GSM室分 2TDS宏站.....50RRU ,额外复用应急宝 200~203,传输盒子 204
	//
	private int keyId;

	// 对应的表
	private String tabName;

	// 基站名称
	private String CELLNAME = "";

	// 基站对应的城市
	private String city;
	
	// 基站对应的地市
	private String country;
	
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
	//告警类型 1为基站 2为小区
	private int alarmType;
	//网络类型
	private String netType;

	// 更新时间
	private String updateTime;
	
    //是否是邻区
    private boolean isLinqu = false;
    
    //频段
    private String band = "";
    //频点
    public String earfcn = "";
    //厂商
    private String vender="";
    
    //其他类型 预留字段
    private Object object;
    
	public String getPCI() {
		return PCI == null ? "" : PCI;
	}

	public String getTAC() {
		return TAC == null ? "" : TAC;
	}

	public String getEarfcn() {
		return earfcn == null ? "" : earfcn;
	}

	public String getBand() {
		return band == null ? "" : band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public LinquInfo getmLinqu() {
		return mLinqu;
	}

	public void setmLinqu(LinquInfo mLinqu) {
		this.mLinqu = mLinqu;
	}
	
	public boolean isLinqu() {
		return isLinqu;
	}

	public void setLinqu(boolean isLinqu) {
		this.isLinqu = isLinqu;
	}

	public String getArea_type() {
		return area_type;
	}

	public void setArea_type(String area_type) {
		this.area_type = area_type;
	}

	public String getSite_type() {
		return site_type;
	}

	public void setSite_type(String site_type) {
		this.site_type = site_type;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public boolean isSeleck() {
		return isSeleck;
	}

	public void setSeleck(boolean isSeleck) {
		this.isSeleck = isSeleck;
	}

	public String getLAC() {
		return LAC;
	}

	public void setLAC(String lAC) {
		LAC = lAC;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getLINEHIGH() {
		return LINEHIGH == null ? "" : LINEHIGH;
	}

	public void setLINEHIGH(String lINEHIGH) {
		LINEHIGH = lINEHIGH;
	}

	public String getJXXQJ() {
		return JXXQJ == null ? "" : JXXQJ;
	}

	public void setJXXQJ(String jXXQJ) {
		JXXQJ = jXXQJ;
	}

	public String getDXQJ() {
		return DXQJ == null ? "" : DXQJ;
	}

	public void setDXQJ(String dXQJ) {
		DXQJ = dXQJ;
	}

	public String getCI() {
		return CI == null ? "" : CI;
	}

	public void setCI(String cI) {
		CI = cI;
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public double getLONbd() {
		return LONbd;
	}

	public void setLONbd(double lONbd) {
		LONbd = lONbd;
	}

	public double getLATbd() {
		return LATbd;
	}

	public void setLATbd(double lATbd) {
		LATbd = lATbd;
	}

	public LatLng getLlGps() {
		return llGps;
	}

	public void setLlGps(LatLng llGps) {
		this.llGps = llGps;
	}

	public LatLng getLlBd() {
		return llBd;
	}

	public void setLlBd(LatLng llBd) {
		this.llBd = llBd;
		if (llBd != null) {
			setLATbd(llBd.latitude);
			setLONbd(llBd.longitude);
		}
	}

	public String getCELLNAME() {
		return CELLNAME;
	}

	public void setCELLNAME(String cELLNAME) {
		CELLNAME = cELLNAME;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public double getLON() {
		return LON;
	}

	public void setLON(double lON) {
		LON = lON;
	}

	public double getLAT() {
		return LAT;
	}

	public void setLAT(double lAT) {
		LAT = lAT;
	}

	public float getDIRECTION() {
		return DIRECTION;
	}

	public void setDIRECTION(float dIRECTION) {
		DIRECTION = dIRECTION;
	}

	public String getEnodebId() {
		if (enodebId == null) {
			enodebId = "";
		}
		return enodebId;
	}

	public void setEnodebId(String enodebId) {
		this.enodebId = enodebId;
	}

	public String getUpdateTime() {
		if (updateTime == null) {
			updateTime = "";
		}
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getVender() {
		return vender;
	}

	public void setVender(String vender) {
		this.vender = vender;
	}
	
	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	
	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}



}
