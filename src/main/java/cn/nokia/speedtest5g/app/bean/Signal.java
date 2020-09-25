package cn.nokia.speedtest5g.app.bean;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.util.SharedPreHandler;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.util.Base64Utils;

/**
 * 基站信号----被动采集数据
 * 
 * @author zwq
 *
 */
@SuppressWarnings("serial")
@Table("signal")
public class Signal implements Serializable, Cloneable{

	// 设置为主键,自增
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private long _id;
	//上一级的主键ID
    public int lastId;
	// 状态---0未提交 1已提交 2修改
	private int state;
	// 0被动测试 1主动测试
	private int type;
	// 当前登录用户联系电话
	private String phone;
	// 用户ID
	private String userId;
	// 时间--2015-06-07 13:18:07.
	private String time;
	//小时时间
	@Ignore
	public String timeHH;
	// 经度
	private String lon;
	// 纬度
	private String lat;
	// 定位类型 GPS定位、网络定位、无定位；
	private String typeGps;
	// 网络类型 LTE_GSM、LTE、TD、GSM；
	private String typeNet;

	private String mcc;

	private String mnc;

	private String lte_pci;

	private String lte_sinr;

	private String lte_tac;

	private String lte_cgi;

	private String lte_rsrp;// 信号强度

	private String lte_rsrq;

	private String lte_enb;

	private String lte_cid;

	private String gsm_lac;

	private String gsm_cid;
	// 即Gsm_sign
	private String gsm_rxl;// 信号

	private String td_lac;

	private String td_ci;

	private String td_pccpch; // 信号
	//------------5G---------------
	public String lte_pci_nr;
	public String lte_rsrp_nr;
	public String lte_sinr_nr;
	public String lte_rsrq_nr;
	public String lte_rssi_nr;
	public String lte_gnb_nr;
	public String lte_cellid_nr;
	public String lte_nci_nr;
	public String lte_tac_nr;
	//频率
	public String lte_frequency_nr;
	//频点
	public String lte_earfcn_nr;
	//频段
	public String lte_band_nr;
	//-------------------------------
	// 版本号
	private String numberCode;
	// 当前测试的颜色类型 0rsrp 1sinr
	private int rsrp_sinr;
	// 测试类型 1LTE 2TD 3GSM
	private int signal_type;
	// ho 切换状态 0 无切换 1切换成功 2重选
	private int hoState;
	// 上传速率
	private String ul;
	// 下载速率
	private String dl;
	// 移动速度
	private String speed;
	// 最近一次通话时长
	private String call_duration;
	// 频段
	private String lte_band;
	//频点
	@Ignore
	public String lte_pd = "";
	// 当前基站名称
	private String lte_name;
	private String td_name;
	private String gsm_name;
	public String lte_name_nr;
	// 采集时长时间
	@Ignore
	private String timeTraverse;
	//海拔
	public String altitude;
	//室分宏站 1为宏站 2为室分 0为未查询
	private int xyType;
	//道路测试增加频段字段
	private String band;
	//网络制式 FDD TDD GSM
	@Ignore
	private String DUAL;
	//对应数据库主键ID
	@Ignore
	public int DB_ID;
	//基站信号列表颜色
	@Ignore
	public int colorBg;
	@Ignore
	public int chartRsrpHashCode;
	@Ignore
	public int chartSinrHashCode;
	// 下载/上传速率
	public float rate;
	// 小区编号：4G= 4600-00-getLte_cid-getLte_cid , 2G=getGsm_cid;
	public String cellId;
	/**
	 * 测试类型：1下载，2上传
	 */
	public int testType;
	public String testBegin;//测试开始时间，格式如2018-05-10 18:12:22
	public String testEnd;//测试结束时间，格式如2018-05-10 18:12:22
	//add for ergodic test at 20190622
	//ping ip
	public String pingIp;
	//ping 时延
	public String pingTime;
	//ul增量
	public String ul_add;
	//dl增量
	public String dl_add;
	//时间差
	public String time_dif;
	//add for road test at 20190729
	//运营商
	public String isp;
	//频点
	public String earfcn;
	//测试类型  当前事件
	public String eventType; 
	//上传地址
	public String ul_ip;
	//下载地址
	public String dl_ip;
	//通话类型
	public String callType;
	//寻呼号码
	public String callNo;
	//切换次数
	public String switchNum;
	//呼叫时延
	public String callDelayTime;
	//是否事件触发
	public String isEventTouch;
	//add at 20200301
	public String subSinr = ""; //副卡
	public String subRsrp = "";
	
	//测试时间
	public String testTime;//用于继续测试时间保存
	public String netType;//测试时网络类型，WIFI、运营商4G/、、未知网络、无网络

	public Signal(Context context, String userId) {
		setPhoneDes3(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
		setUserId(userId);
		setNumberCode(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().VERSION_CODE, ""));
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean isSA(){
		if ("NR".equals(typeNet)) {
			if (TextUtils.isEmpty(lte_cgi) && TextUtils.isEmpty(lte_rsrp) && TextUtils.isEmpty(lte_sinr)) {
				return true;
			}
		}
		return false;
	}
	
	public Signal() {
	}

	public String getLte_earfcn_nr(){
		return lte_earfcn_nr == null ? "" : lte_earfcn_nr;
	}
	
	public String getLte_rsrp_nr(){
		return lte_rsrp_nr == null ? "" : lte_rsrp_nr;
	}
	
	public String getLte_sinr_nr(){
		return lte_sinr_nr == null ? "" : lte_sinr_nr;
	}
	
	public String getTimeTraverse() {
		return timeTraverse;
	}

	public void setTimeTraverse(String timeTraverse) {
		this.timeTraverse = timeTraverse;
	}

	public String getLte_band() {
		return lte_band == null ? "" : lte_band;
	}

	public void setLte_band(String lte_band) {
		this.lte_band = lte_band;
	}

	public String getLte_name() {
		return lte_name == null ? "" : lte_name;
	}

	public void setLte_name(String lte_name) {
		this.lte_name = lte_name;
	}

	public String getTd_name() {
		return td_name == null ? "" : td_name;
	}

	public void setTd_name(String td_name) {
		this.td_name = td_name;
	}

	public String getGsm_name() {
		return gsm_name == null ? "" : gsm_name;
	}

	public void setGsm_name(String gsm_name) {
		this.gsm_name = gsm_name;
	}

	public int getRsrp_sinr() {
		return rsrp_sinr;
	}

	public void setRsrp_sinr(int rsrp_sinr) {
		this.rsrp_sinr = rsrp_sinr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNumberCode() {
		return numberCode == null ? "" : numberCode;
	}

	public void setNumberCode(String numberCode) {
		this.numberCode = numberCode;
	}

	public long get_id() {
		return _id;
	}

	public int getState() {
		return state;
	}

	public String getPhoneOld() {
		return phone == null ? "" : phone;
	}
	
	public String getPhone() {
		return phone == null ? "" : Base64Utils.decryptorDes3(phone);
	}

	public String getUserId() {
		return userId == null ? "" : userId;
	}

	public String getTime() {
		return time == null ? "" : time;
	}

	public String getLon() {
		return lon == null ? "" : lon;
	}

	public String getLat() {
		return lat == null ? "" : lat;
	}

	public String getTypeGps() {
		return typeGps == null ? "" : typeGps;
	}

	public String getTypeNet() {
		return typeNet == null ? "" : typeNet;
	}

	public String getMcc() {
		return mcc == null ? "" : mcc;
	}

	public String getMnc() {
		return mnc == null ? "" : mnc;
	}

	public String getLte_pci() {
		return lte_pci == null ? "" : lte_pci;
	}

	public String getLte_sinr() {
		return lte_sinr == null ? "" : lte_sinr;
	}

	public String getLte_tac() {
		return lte_tac == null ? "" : lte_tac;
	}

	public String getLte_cgi() {
		return lte_cgi == null ? "" : lte_cgi;
	}

	public String getLte_rsrp() {
		return lte_rsrp == null ? "" : lte_rsrp;
	}

	public String getLte_rsrq() {
		return lte_rsrq == null ? "" : lte_rsrq;
	}

	public String getLte_enb() {
		return lte_enb == null ? "" : lte_enb;
	}

	public String getLte_cid() {
		return lte_cid == null ? "" : lte_cid;
	}

	public String getGsm_lac() {
		return gsm_lac == null ? "" : gsm_lac;
	}

	public String getGsm_cid() {
		return gsm_cid == null ? "" : gsm_cid;
	}

	public String getGsm_rxl() {
		return gsm_rxl == null ? "" : gsm_rxl;
	}

	public String getTd_lac() {
		return td_lac == null ? "" : td_lac;
	}

	public String getTd_ci() {
		return td_ci == null ? "" : td_ci;
	}

	public String getTd_pccpch() {
		return td_pccpch == null ? "" : td_pccpch;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public void setPhoneDes3(String phone) {
		this.phone = phone;
	}

	public void setPhone(String phone) {
		this.phone = Base64Utils.encrytorDes3(phone);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setTypeGps(String typeGps) {
		this.typeGps = typeGps;
	}

	public void setTypeNet(String typeNet) {
		this.typeNet = typeNet;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public void setMnc(String mnc) {
		this.mnc = mnc;
	}

	public void setLte_pci(String lte_pci) {
		this.lte_pci = lte_pci;
	}

	public void setLte_sinr(String lte_sinr) {
		this.lte_sinr = lte_sinr;
	}

	public void setLte_tac(String lte_tac) {
		this.lte_tac = lte_tac;
	}

	public void setLte_cgi(String lte_cgi) {
		this.lte_cgi = lte_cgi;
	}

	public void setLte_rsrp(String lte_rsrp) {
		this.lte_rsrp = lte_rsrp;
	}

	public void setLte_rsrq(String lte_rsrq) {
		this.lte_rsrq = lte_rsrq;
	}

	public void setLte_enb(String lte_enb) {
		this.lte_enb = lte_enb;
	}

	public void setLte_cid(String lte_cid) {
		this.lte_cid = lte_cid;
	}

	public void setGsm_lac(String gsm_lac) {
		this.gsm_lac = gsm_lac;
	}

	public void setGsm_cid(String gsm_cid) {
		this.gsm_cid = gsm_cid;
	}

	public void setGsm_rxl(String gsm_rxl) {
		this.gsm_rxl = gsm_rxl;
	}

	public void setTd_lac(String td_lac) {
		this.td_lac = td_lac;
	}

	public void setTd_ci(String td_ci) {
		this.td_ci = td_ci;
	}

	public void setTd_pccpch(String td_pccpch) {
		this.td_pccpch = td_pccpch;
	}

	public int getSignal_type() {
		return signal_type;
	}

	public void setSignal_type(int signal_type) {
		this.signal_type = signal_type;
	}

	public int getHoState() {
		return hoState;
	}

	public void setHoState(int hoState) {
		this.hoState = hoState;
	}

	public String getUl() {
		return ul == null ? "0" : ul;
	}

	public void setUl(String ul) {
		this.ul = ul;
	}

	public String getDl() {
		return dl == null ? "0" : dl;
	}

	public void setDl(String dl) {
		this.dl = dl;
	}

	public String getSpeed() {
		return speed == null ? "0" : speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getCall_duration() {
		return call_duration == null ? "0" : call_duration;
	}

	public void setCall_duration(String call_duration) {
		this.call_duration = call_duration;
	}

	public int getXyType() {
		return xyType;
	}

	public void setXyType(int xyType) {
		this.xyType = xyType;
	}

	public String getBand() {
		return band == null ? "" : band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getDUAL() {
		return DUAL;
	}

	public void setDUAL(String dUAL) {
		DUAL = dUAL;
	}

	public String getPingIp() {
		return pingIp == null ? "" : pingIp;
	}

	public String getPingTime() {
		return pingTime == null ? "" : pingTime;
	}

	public String getUl_add() {
		return ul_add == null ? "" : ul_add;
	}

	public String getDl_add() {
		return dl_add == null ? "" : dl_add;
	}

	public String getTime_dif() {
		return time_dif == null ? "" : time_dif;
	}

	public String getAltitude() {
		return altitude == null ? "" : altitude;
	}

	public String getEventType() {
		return eventType == null ? "" : eventType;
	}

	public String getUl_ip() {
		return ul_ip == null ? "" : ul_ip;
	}

	public String getDl_ip() {
		return dl_ip == null ? "" : dl_ip;
	}

	public String getCallType() {
		return callType == null ? "" : callType;
	}

	public String getCallNo() {
		return callNo == null ? "" : callNo;
	}

	public String getSwitchNum() {
		return switchNum == null ? "" : switchNum;
	}

	public String getCallDelayTime() {
		return callDelayTime == null ? "" : callDelayTime;
	}

	public String getTestTime() {
		return testTime == null ? "" : testTime;
	}

	public String getIsEventTouch() {
		return isEventTouch == null ? "" : isEventTouch;
	}

	public String getLte_pci_nr() {
		return lte_pci_nr == null ? "" : lte_pci_nr;
	}

	public String getLte_band_nr() {
		return lte_band_nr == null ? "" : lte_band_nr;
	}
}
