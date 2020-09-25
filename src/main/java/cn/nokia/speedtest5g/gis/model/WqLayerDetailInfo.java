package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;

/**
 * 基站详细信息数据
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class WqLayerDetailInfo implements Serializable{
	//GSM-
	//CI,小区名称，设备厂家名称，LAC，信源类型，所属地市，所属区县，所属区域，经度，纬度，网络色码，基站色码，BCCH频点，方位角，机械下倾角，电下倾角，天线挂高
	private String CI,CELLNAME,DEVICENAME,LAC,XYTYPE,CITY,AREA,DISCT,LON,LAT,WLNUM,JZNUM,bcchPD,DIRECTION,JXXQJ,DXQJ,LINEHIGH;
	
	//TDS
	//CI,小区名称，设备厂家名称，信源类型，所属地市，所属区县，所属区域，经度，纬度，主扰码号，频点,方位角,机械下倾角,电下倾角,天线挂高,LAC
	//主扰码号，
	private String ZRNUM;
	
	//LTE
	//CI,小区名称，设备厂家名称，信源类型，所属地市，所属区县，所属区域，经度，纬度，工作频点,方位角,机械下倾角,电下倾角,天线挂高,频点，PCI,
	//工作频点,PCI，频点,参考信号功率
	private String WORKPD,PCI,earfcn,ENBAJ12 ;
	public String TAC;
	//规划
	//CI,小区名称，设备厂家名称，站型，站阶段，小区数量，所属地市，所属区县，所属区域，项目名称，经度，纬度，方位角，下倾角，天线挂高，频点
	//---站型，站阶段，小区数量，项目名称，下倾角，频点
	private String eNodeB_TYPE,eNodeB_STATE,CELLCOUNT,XMCHNAME,XQJ,PD;
	
	//铁塔
	//站址名称、站址编码、<所属地市，所属区县>，站址类型，<经度，纬度>，所在地址（详细地址）、站址等级、维护状态、覆盖场景、站址地形、产权单位、是否共享、共享单位
	private String SITE_CODE,SITE_TYPE,SITE_ADDR,SITE_LEVEL,MAINTAIN_STATE,SCENE,TERRAIN,EQUITIES_ORG,IS_SHARE,SHARE_ORG;
	private String AREA_TYPE,LTE_TYPE,CREATE_TIME,NEAREST_SITE_NAME,NEAREST_DIS,NEAREST_LAT,NEAREST_LON;//电信2/4G
	
	//高铁
	//<CI,小区名称>，基站名称,<所属地市，所属区县>,Enodeb_id,<设备厂家名称>,是否拉远小区,RRU名称,RRU序列号,RRU支持频段,RRU位置,<经度，纬度>，
	//RRU覆盖类型,RRU方位角1,RRU下倾角1,RRU天线挂高1(米),RRU方位角2,RRU下倾角2,RRU天线挂高2(米),RRU方位角3,RRU下倾角3,RRU天线挂高3(米),
	//是否拉远RRU,RRU平均发射功率,RRU最大发射功率
	private String ENODEBNAME,ENBAG05,IS_REMOTE,ENBAE02,ENBAE06,ENBAE11,POSITION,COVER_TYPE,AZIMUTH1,TILT1,ANT_HEIGHT1,AZIMUTH2,TILT2,ANT_HEIGHT2,AZIMUTH3,TILT3,ANT_HEIGHT3,IF_ZOOMOUT,ENBHQ01,ENBHQ02;
	
	//5G-GNB,宽带
	public String gnb,band_width;
	
	//类型---
    //0  GSM小区 、
	//1  TDS小区、
	//2  LTE小区  
    //3  LTE规划
    //4 - 12 铁塔
	//26 5G小区
    private int type;

	public String getNEAREST_SITE_NAME() {
		return NEAREST_SITE_NAME;
	}

	public void setNEAREST_SITE_NAME(String nEAREST_SITE_NAME) {
		NEAREST_SITE_NAME = nEAREST_SITE_NAME;
	}

	public String getNEAREST_DIS() {
		return NEAREST_DIS;
	}

	public void setNEAREST_DIS(String nEAREST_DIS) {
		NEAREST_DIS = nEAREST_DIS;
	}

	public String getNEAREST_LAT() {
		return NEAREST_LAT;
	}

	public void setNEAREST_LAT(String nEAREST_LAT) {
		NEAREST_LAT = nEAREST_LAT;
	}

	public String getNEAREST_LON() {
		return NEAREST_LON;
	}

	public void setNEAREST_LON(String nEAREST_LON) {
		NEAREST_LON = nEAREST_LON;
	}

	public String getAREA_TYPE() {
		return AREA_TYPE;
	}

	public void setAREA_TYPE(String aREA_TYPE) {
		AREA_TYPE = aREA_TYPE;
	}

	public String getLTE_TYPE() {
		return LTE_TYPE;
	}

	public void setLTE_TYPE(String lTE_TYPE) {
		LTE_TYPE = lTE_TYPE;
	}

	public String getCREATE_TIME() {
		return CREATE_TIME;
	}

	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}

	public String getENBAJ12() {
		return ENBAJ12;
	}

	public void setENBAJ12(String eNBAJ12) {
		ENBAJ12 = eNBAJ12;
	}

	public String getENODEBNAME() {
		return ENODEBNAME;
	}

	public void setENODEBNAME(String eNODEBNAME) {
		ENODEBNAME = eNODEBNAME;
	}

	public String getENBAG05() {
		return ENBAG05;
	}

	public void setENBAG05(String eNBAG05) {
		ENBAG05 = eNBAG05;
	}

	public String getIS_REMOTE() {
		return IS_REMOTE;
	}

	public void setIS_REMOTE(String iS_REMOTE) {
		IS_REMOTE = iS_REMOTE;
	}

	public String getENBAE02() {
		return ENBAE02;
	}

	public void setENBAE02(String eNBAE02) {
		ENBAE02 = eNBAE02;
	}

	public String getENBAE06() {
		return ENBAE06;
	}

	public void setENBAE06(String eNBAE06) {
		ENBAE06 = eNBAE06;
	}

	public String getENBAE11() {
		return ENBAE11;
	}

	public void setENBAE11(String eNBAE11) {
		ENBAE11 = eNBAE11;
	}

	public String getPOSITION() {
		return POSITION;
	}

	public void setPOSITION(String pOSITION) {
		POSITION = pOSITION;
	}

	public String getCOVER_TYPE() {
		return COVER_TYPE;
	}

	public void setCOVER_TYPE(String cOVER_TYPE) {
		COVER_TYPE = cOVER_TYPE;
	}

	public String getAZIMUTH1() {
		return AZIMUTH1;
	}

	public void setAZIMUTH1(String aZIMUTH1) {
		AZIMUTH1 = aZIMUTH1;
	}

	public String getTILT1() {
		return TILT1;
	}

	public void setTILT1(String tILT1) {
		TILT1 = tILT1;
	}

	public String getANT_HEIGHT1() {
		return ANT_HEIGHT1;
	}

	public void setANT_HEIGHT1(String aNT_HEIGHT1) {
		ANT_HEIGHT1 = aNT_HEIGHT1;
	}

	public String getAZIMUTH2() {
		return AZIMUTH2;
	}

	public void setAZIMUTH2(String aZIMUTH2) {
		AZIMUTH2 = aZIMUTH2;
	}

	public String getTILT2() {
		return TILT2;
	}

	public void setTILT2(String tILT2) {
		TILT2 = tILT2;
	}

	public String getANT_HEIGHT2() {
		return ANT_HEIGHT2;
	}

	public void setANT_HEIGHT2(String aNT_HEIGHT2) {
		ANT_HEIGHT2 = aNT_HEIGHT2;
	}

	public String getAZIMUTH3() {
		return AZIMUTH3;
	}

	public void setAZIMUTH3(String aZIMUTH3) {
		AZIMUTH3 = aZIMUTH3;
	}

	public String getTILT3() {
		return TILT3;
	}

	public void setTILT3(String tILT3) {
		TILT3 = tILT3;
	}

	public String getANT_HEIGHT3() {
		return ANT_HEIGHT3;
	}

	public void setANT_HEIGHT3(String aNT_HEIGHT3) {
		ANT_HEIGHT3 = aNT_HEIGHT3;
	}

	public String getIF_ZOOMOUT() {
		return IF_ZOOMOUT;
	}

	public void setIF_ZOOMOUT(String iF_ZOOMOUT) {
		IF_ZOOMOUT = iF_ZOOMOUT;
	}

	public String getENBHQ01() {
		return ENBHQ01 == null ? "" : ENBHQ01;
	}

	public void setENBHQ01(String eNBHQ01) {
		ENBHQ01 = eNBHQ01;
	}

	public String getENBHQ02() {
		return ENBHQ02 == null ? "" : ENBHQ02;
	}

	public void setENBHQ02(String eNBHQ02) {
		ENBHQ02 = eNBHQ02;
	}

	public String getSITE_CODE() {
		return SITE_CODE;
	}

	public void setSITE_CODE(String sITE_CODE) {
		SITE_CODE = sITE_CODE;
	}

	public String getSITE_TYPE() {
		return SITE_TYPE;
	}

	public void setSITE_TYPE(String sITE_TYPE) {
		SITE_TYPE = sITE_TYPE;
	}

	public String getSITE_ADDR() {
		return SITE_ADDR;
	}

	public void setSITE_ADDR(String sITE_ADDR) {
		SITE_ADDR = sITE_ADDR;
	}

	public String getSITE_LEVEL() {
		return SITE_LEVEL;
	}

	public void setSITE_LEVEL(String sITE_LEVEL) {
		SITE_LEVEL = sITE_LEVEL;
	}

	public String getMAINTAIN_STATE() {
		return MAINTAIN_STATE;
	}

	public void setMAINTAIN_STATE(String mAINTAIN_STATE) {
		MAINTAIN_STATE = mAINTAIN_STATE;
	}

	public String getSCENE() {
		return SCENE;
	}

	public void setSCENE(String sCENE) {
		SCENE = sCENE;
	}

	public String getTERRAIN() {
		return TERRAIN;
	}

	public void setTERRAIN(String tERRAIN) {
		TERRAIN = tERRAIN;
	}

	public String getEQUITIES_ORG() {
		return EQUITIES_ORG;
	}

	public void setEQUITIES_ORG(String eQUITIES_ORG) {
		EQUITIES_ORG = eQUITIES_ORG;
	}

	public String getIS_SHARE() {
		return IS_SHARE;
	}

	public void setIS_SHARE(String iS_SHARE) {
		IS_SHARE = iS_SHARE;
	}

	public String getSHARE_ORG() {
		return SHARE_ORG;
	}

	public void setSHARE_ORG(String sHARE_ORG) {
		SHARE_ORG = sHARE_ORG;
	}

	public String getZRNUM() {
		return ZRNUM;
	}

	public void setZRNUM(String zRNUM) {
		ZRNUM = zRNUM;
	}

	public String getWORKPD() {
		return WORKPD;
	}

	public void setWORKPD(String wORKPD) {
		WORKPD = wORKPD;
	}

	public String getPCI() {
		return PCI;
	}

	public void setPCI(String pCI) {
		PCI = pCI;
	}

	public String getEarfcn() {
		return earfcn;
	}

	public void setEarfcn(String earfcn) {
		this.earfcn = earfcn;
	}

	public String getCI() {
		return CI;
	}

	public void setCI(String cI) {
		CI = cI;
	}

	public String getCELLNAME() {
		return CELLNAME;
	}

	public void setCELLNAME(String cELLNAME) {
		CELLNAME = cELLNAME;
	}

	public String getDEVICENAME() {
		return DEVICENAME;
	}

	public void setDEVICENAME(String dEVICENAME) {
		DEVICENAME = dEVICENAME;
	}

	public String getLAC() {
		return LAC;
	}

	public void setLAC(String lAC) {
		LAC = lAC;
	}

	public String getXYTYPE() {
		return XYTYPE;
	}

	public void setXYTYPE(String xYTYPE) {
		XYTYPE = xYTYPE;
	}

	public String getCITY() {
		return CITY;
	}

	public void setCITY(String cITY) {
		CITY = cITY;
	}

	public String getAREA() {
		return AREA;
	}

	public void setAREA(String aREA) {
		AREA = aREA;
	}

	public String getDISCT() {
		return DISCT;
	}

	public void setDISCT(String dISCT) {
		DISCT = dISCT;
	}

	public String getLON() {
		return LON;
	}

	public void setLON(String lON) {
		LON = lON;
	}

	public String getLAT() {
		return LAT;
	}

	public void setLAT(String lAT) {
		LAT = lAT;
	}

	public String getWLNUM() {
		return WLNUM;
	}

	public void setWLNUM(String wLNUM) {
		WLNUM = wLNUM;
	}

	public String getJZNUM() {
		return JZNUM;
	}

	public void setJZNUM(String jZNUM) {
		JZNUM = jZNUM;
	}

	public String getBcchPD() {
		return bcchPD;
	}

	public void setBcchPD(String bcchPD) {
		this.bcchPD = bcchPD;
	}

	public String getDIRECTION() {
		return DIRECTION;
	}

	public void setDIRECTION(String dIRECTION) {
		DIRECTION = dIRECTION;
	}

	public String getJXXQJ() {
		return JXXQJ;
	}

	public void setJXXQJ(String jXXQJ) {
		JXXQJ = jXXQJ;
	}

	public String getDXQJ() {
		return DXQJ;
	}

	public void setDXQJ(String dXQJ) {
		DXQJ = dXQJ;
	}

	public String getLINEHIGH() {
		return LINEHIGH;
	}

	public void setLINEHIGH(String lINEHIGH) {
		LINEHIGH = lINEHIGH;
	}

	public String geteNodeB_TYPE() {
		return eNodeB_TYPE;
	}

	public void seteNodeB_TYPE(String eNodeB_TYPE) {
		this.eNodeB_TYPE = eNodeB_TYPE;
	}

	public String geteNodeB_STATE() {
		return eNodeB_STATE;
	}

	public void seteNodeB_STATE(String eNodeB_STATE) {
		this.eNodeB_STATE = eNodeB_STATE;
	}

	public String getCELLCOUNT() {
		return CELLCOUNT;
	}

	public void setCELLCOUNT(String cELLCOUNT) {
		CELLCOUNT = cELLCOUNT;
	}

	public String getXMCHNAME() {
		return XMCHNAME;
	}

	public void setXMCHNAME(String xMCHNAME) {
		XMCHNAME = xMCHNAME;
	}

	public String getXQJ() {
		return XQJ;
	}

	public void setXQJ(String xQJ) {
		XQJ = xQJ;
	}

	public String getPD() {
		return PD;
	}

	public void setPD(String pD) {
		PD = pD;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String geteNodeB_TYPE_STR() {
		return "站型";
	}

	public String geteNodeB_STATE_STR() {
		return "站阶段";
	}

	public String getCELLCOUNT_STR() {
		return "小区数量";
	}

	public String getXMCHNAME_STR() {
		return "项目名称";
	}

	public String getXQJ_STR() {
		return "下倾角";
	}

	public String getPD_STR() {
		return "频点";
	}

	public String getCI_STR() {
		return "CI";
	}

	public String getCELLNAME_STR() {
		return "小区名称";
	}

	public String getDEVICENAME_STR() {
		return "设备厂家名称";
	}

	public String getLAC_STR() {
		return "LAC";
	}

	public String getXYTYPE_STR() {
		return "信源类型";
	}

	public String getCITY_STR() {
		return "所属地市";
	}

	public String getAREA_STR() {
		return "所属区县";
	}

	public String getDISCT_STR() {
		return "所属区域";
	}

	public String getLON_STR() {
		return "经度";
	}

	public String getLAT_STR() {
		return "纬度";
	}

	public String getWLNUM_STR() {
		return "网络色码";
	}

	public String getJZNUM_STR() {
		return "基站色码";
	}

	public String getBcchPD_STR() {
		return "BCCH频点";
	}

	public String getDIRECTION_STR() {
		return "方位角";
	}

	public String getJXXQJ_STR() {
		return "机械下倾角";
	}

	public String getDXQJ_STR() {
		return "电下倾角";
	}

	public String getLINEHIGH_STR() {
		return "天线挂高";
	}
	public String getWORKPD_STR() {
		return "工作频点";
	}

	public String getPCI_STR() {
		return "PCI";
	}
	
	public String getTAC_STR() {
		return "TAC";
	}

	public String getEarfcn_STR() {
		return "频点";
	}

	public String getZRNUM_STR() {
		return "主扰码号";
	}

	public String getSITE_NAME_STR() {
		return "站址名称";
	}

	public String getSITE_CODE_STR() {
		return "站址编码";
	}

	public String getSITE_TYPE_STR() {
		return "站址类型";
	}

	public String getSITE_ADDR_STR() {
		return "所在地址";
	}

	public String getSITE_LEVEL_STR() {
		return "站址等级";
	}

	public String getMAINTAIN_STATE_STR() {
		return "维护状态";
	}

	public String getSCENE_STR() {
		return "覆盖场景";
	}

	public String getTERRAIN_STR() {
		return "站址地形";
	}

	public String getEQUITIES_ORG_STR() {
		return "产权单位";
	}

	public String getIS_SHARE_STR() {
		return "是否共享";
	}

	public String getSHARE_ORG_STR() {
		return "共享单位";
	}

	public String getENODEBNAME_STR() {
		return "基站名称";
	}

	public String getENBAG05_STR() {
		return "Enodeb_id";
	}

	public String getIS_REMOTE_STR() {
		return "是否拉远小区";
	}

	public String getENBAE02_STR() {
		return "RRU名称";
	}

	public String getENBAE06_STR() {
		return "RRU序列号";
	}

	public String getENBAE11_STR() {
		return "RRU支持频段";
	}

	public String getPOSITION_STR() {
		return "RRU位置";
	}

	public String getCOVER_TYPE_STR() {
		return "RRU覆盖类型";
	}

	public String getAZIMUTH1_STR() {
		return "RRU方位角1";
	}

	public String getTILT1_STR() {
		return "RRU下倾角1";
	}

	public String getANT_HEIGHT1_STR() {
		return "RRU天线挂高1";
	}

	public String getAZIMUTH2_STR() {
		return "RRU方位角2";
	}

	public String getTILT2_STR() {
		return "RRU下倾角2";
	}

	public String getANT_HEIGHT2_STR() {
		return "RRU天线挂高2";
	}

	public String getAZIMUTH3_STR() {
		return "RRU方位角3";
	}

	public String getTILT3_STR() {
		return "RRU下倾角3";
	}

	public String getANT_HEIGHT3_STR() {
		return "RRU天线挂高3";
	}

	public String getIF_ZOOMOUT_STR() {
		return "是否拉远RRU";
	}

	public String getENBHQ01_STR() {
		return "RRU平均发射功率";
	}

	public String getENBHQ02_STR() {
		return "RRU最大发射功率";
	}
	
	public String getENBAJ12_STR() {
		return "参考信号功率";
	}
	
	public String getBAND_WIDTH_STR() {
		return "带宽";
	}
	
	public String getLTE_TYPE_STR() {
		return "LTE制式";
	}
	
	public String getNEAREST_SITE_NAME_STR() {
		return "最近站点名称";
	}
	
	public String getNEAREST_DIS_STR() {
		return "最近站点距离";
	}
	
	public String getNEAREST_LAT_STR() {
		return "最近站点纬度";
	}
	
	public String getNEAREST_LON_STR() {
		return "最近站点经度";
	}
}
