package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import java.util.List;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.util.TimeUtil;
import com.android.volley.util.SharedPreHandler;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * FTP测试结果
 * 
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
@Table("FtpTestSuperDb")
public class Db_JJ_FTPTestInfo implements Serializable, Comparable<Db_JJ_FTPTestInfo> {
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private int _id;// 设置为主键,自增
	private int userid = UtilHandler.getInstance().toInt(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), ""), 0);;// 用户ID
	public String loginName;// 用户登入账号
	public String userName;// 用户名称
	private String imei = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), "");// 手机imei
	private String testBegin;// 测试开始时间，格式如2018-05-10 18:12:22
	private String testEnd;// 测试结束时间，格式如2018-05-10 18:12:22
	private double upSpeedMax;// 上传最大速率
	private double upSpeedAvg;// 上传平均速率
	private String upHostAddr;// 上传服务器地址，IP或域名
	private Integer upPort;// 上传端口
	private double downSpeedMax;// 下载最大速率
	private double downSpeedAvg;// 下载平均速率
	private String downHostAddr;// 下载服务器地址，IP或域名
	private Integer downPort;// 下载服务器端口
	private String downPath;// 下载文件路径
	private String sourceType;// “0” 测试来源类型，默认”0”
	private String netType;// 测试时网络类型，WIFI、LTE、GSM
	private String cellId;// 测试时所连接的小区号
	private double longitude;// 测试时经度
	private double latitude;// 测试时纬度
	private String signalType;// 手机信号类型，LTE、GSM
	private String lac;// Lac或者Tac
	private Integer pci;// pci
	private Integer earfcn;// 频点
	private Integer rsrp;// rsrp 或exl
	private Integer sinr;// sinr
	private Integer rsrq;// rsrq
	private String upHostType;// 上传服务器类型，详见附录
	private String upHostTypeName;// 上传服务器类型名称
	private String downHostType;// 下载服务器类型
	private String downHostTypeName;// 下载服务器类型名称
	private String cellName;// 小区名
	private boolean isUpload;// 是否上传
	public long id = -1;// 测试详情id，用于服务端查找测试详情用
	public String operator;// 中国移动、联通、电信
	public String phoneModel;// 手机设备型号
	public String regionName;// 所在地市
	public String testId; // 批次号
	public int testTimes; // 测试次数
	public String testName;//自定义测试名称
	
	public int logId; //日志id
	public String remarks; //备注
	public String downEstimateFlow; //预估下载流量值
	public String upEstimateFlow; //预估上传流量值
	public String downRealFlow; //实际下载流量值
	public String upRealFlow; //实际上传流量值
	
	public String ping; //ping时延
	public String jitter;//抖动
	public String packetLoss; //丢包
	
	@Ignore
	public List<Signal> downloadTestList;// 下载信号信息列表
	@Ignore
	public List<Signal> uploadTestList;// 上传信号信息列表
	//模块图标
	@Ignore
	public int modelIcon;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getUserId() {
		return userid;
	}

	public String getTestBegin() {
		return testBegin;
	}

	public String getTestEnd() {
		return testEnd;
	}

	public double getUpSpeedMax() {
		return upSpeedMax;
	}

	public double getUpSpeedAvg() {
		return upSpeedAvg;
	}

	public String getUpHostAddr() {
		return upHostAddr;
	}

	public Integer getUpPort() {
		return upPort;
	}

	public double getDownSpeedMax() {
		return downSpeedMax;
	}

	public double getDownSpeedAvg() {
		return downSpeedAvg;
	}

	public String getDownHostAddr() {
		return downHostAddr;
	}

	public Integer getDownPort() {
		return downPort;
	}

	public String getDownPath() {
		return downPath;
	}

	public String getSourceType() {
		return sourceType;
	}

	public String getNetType() {
		return netType;
	}

	public String getCellId() {
		return cellId;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getImei() {
		return imei;
	}

	public void setUserId(int userid) {
		this.userid = userid;
	}

	public void setTestBegin(String testBegin) {
		this.testBegin = testBegin;
	}

	public void setTestEnd(String testEnd) {
		this.testEnd = testEnd;
	}

	public void setUpSpeedMax(double upSpeedMax) {
		this.upSpeedMax = upSpeedMax;
	}

	public void setUpSpeedAvg(double upSpeedAvg) {
		this.upSpeedAvg = upSpeedAvg;
	}

	public void setUpHostAddr(String upHostAddr) {
		this.upHostAddr = upHostAddr;
	}

	public void setUpPort(Integer upPort) {
		this.upPort = upPort;
	}

	public void setDownSpeedMax(double downSpeedMax) {
		this.downSpeedMax = downSpeedMax;
	}

	public void setDownSpeedAvg(double downSpeedAvg) {
		this.downSpeedAvg = downSpeedAvg;
	}

	public void setDownHostAddr(String downHostAddr) {
		this.downHostAddr = downHostAddr;
	}

	public void setDownPort(Integer downPort) {
		this.downPort = downPort;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	public String getSignalType() {
		return signalType;
	}

	public String getLac() {
		return lac;
	}

	public Integer getPci() {
		return pci;
	}

	public Integer getEarfcn() {
		return earfcn;
	}

	public Integer getRsrp() {
		return rsrp;
	}

	public Integer getSinr() {
		return sinr;
	}

	public Integer getRsrq() {
		return rsrq;
	}

	public void setSignalType(String signalType) {
		this.signalType = signalType;
	}

	public void setLac(String lac) {
		this.lac = lac;
	}

	public void setPci(Integer pci) {
		this.pci = pci;
	}

	public void setEarfcn(Integer earfcn) {
		this.earfcn = earfcn;
	}

	public void setRsrp(Integer rsrp) {
		this.rsrp = rsrp;
	}

	public void setSinr(Integer sinr) {
		this.sinr = sinr;
	}

	public void setRsrq(Integer rsrq) {
		this.rsrq = rsrq;
	}

	public String getUpHostType() {
		return upHostType;
	}

	public String getUpHostTypeName() {
		return upHostTypeName;
	}

	public String getDownHostType() {
		return downHostType;
	}

	public String getDownHostTypeName() {
		return downHostTypeName;
	}

	public String getCellName() {
		return cellName;
	}

	public void setUpHostType(String upHostType) {
		this.upHostType = upHostType;
	}

	public void setUpHostTypeName(String upHostTypeName) {
		this.upHostTypeName = upHostTypeName;
	}

	public void setDownHostType(String downHostType) {
		this.downHostType = downHostType;
	}

	public void setDownHostTypeName(String downHostTypeName) {
		this.downHostTypeName = downHostTypeName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	@Override
	public int compareTo(Db_JJ_FTPTestInfo another) {
		long testBegin = TimeUtil.getInstance().stringToLong(this.getTestBegin());
		long anotherTestBegin = TimeUtil.getInstance().stringToLong(another.getTestBegin());
		int i = 0;
		if (anotherTestBegin > testBegin) {
			i = 1;
		} else if (anotherTestBegin < testBegin) {
			i = -1;
		}
		return i;
	}
}
