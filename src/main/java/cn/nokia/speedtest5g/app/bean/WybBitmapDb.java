package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 网优宝图片附件表---2.4.2新增集合对象(用于HTTP传输)
 * @author zwq
 */
@SuppressWarnings("serial")
@Table("wybbitmap")
public class WybBitmapDb implements Serializable{

	// 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int _id;
    //第一级的主键ID
    private int superId;
    //上一级的主键ID
    private int lastId;
    //上一级类型
    private int lastType = -1;
    //用户ID
  	private String userId;
  	//图片地址
    private String path;
    //图片名称
    private String name;
    //图片对应的网络地址
    private String netUrl;
    //附件生成时间
    private String time;  
    //图片状态--- 0 未上传  1已上传  
    private int uploadStateIv;
    /**
     * 图片类型 
     * 500完整图
     * 501局部图
     * 200测试工具-遍历测试log
     * 201测试工具-遍历测试RSRP
     * 202测试工具-遍历测试SINR
     * 203测试工具-遍历测试RXL
     * 204遍历工具-遍历测试当前页截图
     * ---------网优自动化相关-----------
     * 600 干扰问题区域拍照图片
     * 601 覆盖测试--干扰页
     * 602 遍历测试--干扰页
     * 603 道路测试--干扰页
     * 604 覆盖测试--PCI工具
     * 605 遍历测试--PCI工具
     * 606 道路测试--PCI工具
     * 607 参数修改 签名图
     * 608 遍历测试--PCI工具 RSRP
     * 609 遍历测试--PCI工具 SINR
     * --------------------------------
     * ---------网优计费系统-----------
     * 700 签名图
     * 701 定点测试
     * 702 遍历测试
     * 703 道路测试
     * 705 里程测试经纬度
     * 706 天馈调整-方位角
     * 707 天馈调整-下倾角
     * 
     * 710 定点测试截图
     * 711 遍历测试截图-RSRP
     * 712 道路测试截图
     * 713 地铁测试截图-RSRP
     * 714 现场拍照
     * 715 存在问题拍照
     * 716 天馈覆盖前拍照
     * 717 天馈覆盖后拍照
     * 718 遍历测试截图-SINR 
     * 719 操作日志csv
     * 720 地铁测试LOG
     * 721 地铁测试截图-SINR
     * 
     * 731 主场景照片 -投诉测试
     * 732 投诉地点地图截屏 -投诉测试
     * 733 遍历测试4g截图rsrp -投诉测试
     * 734 遍历测试4g截图sinr -投诉测试
     * 735 遍历测试2g截图  -投诉测试
     * 736 道路测试4g截图 -投诉测试
     * 737 道路测试2g截图  -投诉测试
     * 738 测试结论 -现场环境拍照
     * 739 道路测试4g -测试日志 csv(投诉测试)
     * 740 道路测试2g -测试日志 csv(投诉测试)
     * 741 4g基础信号测试 -测试日志 csv(投诉测试)
     * 742 2g基础信号测试 -测试日志 csv(投诉测试)
     * 743 遍历测试4g -测试日志 csv(投诉测试)
     * 744 遍历测试4g -测试日志 csv(投诉测试)
     * 745 遍历测试2g -测试日志 csv(投诉测试)
     * 746 4g上传下载速率测试日志
     * --------------------------------
     * 
     * 747 满格宝 设备图片
     * 748 满格宝 现场拍照图片
     * 749 满格宝 地图截屏
     * 
     * 750 遍历测试工具-遍历测试 背景图  共用
     * 751 遍历测试工具-分享码导入csv类型
     * 752 遍历测试工具-室分单验 csv类型
     * 753 遍历测试工具-室分单验 截屏类型rsrp
     * 754 遍历测试工具-室分单验 截屏类型sinr
     * 755 遍历测试工具-定点测试 csv类型
     * 756 遍历测试工具-定点测试 截屏类型rsrp
     * 757 遍历测试工具-定点测试 截屏类型sinr
     * 
     * 758 道路测试工具-csv上传
     * 
     * ---------NB测试-----------
     * 770 NB测试 记录附件csv
     * 
     * ---------属地化巡检-----------
     * 801 属地化巡检-方位角
     * 802 属地化巡检-下倾角
     * --------------------------------
     * ---------考勤系统-----------
     * 811 人员打卡异常
     * 812 车辆定位
     * 813 车辆定位异常
     * 814 仪表管理图片
     * --------------------------------
     * ---------应急站-----------
     * 901 应急站现场拍照照片上传
     * 902 应急盒子巡检照片 近景
     * 903 应急盒子巡检照片 远景
     * ------------基站导航(1001-1049)--------------------
     * 1001 基站导航-线路截图
     * 1002 基站导航-脚印图
     * 1003 基站导航-线路log
     * 1004 基站导航-基站全景图（服务器图片）
     * -------------简单硬扩(1050-1149)----------------------
     * 1050 产品码-诺西
     * 1051 扩展板型号-诺西
     * 1052 硬件序列号-诺西
     * 1053 扩展板所在BBU的位置-诺西
     * 1054 基带板槽位号-华为
     * 1055 链/环头槽位号-华为
     * 1056 链/环头光口号-华为
     * 1057 环尾光口号-华为
     * 1058 BBU侧基带板槽位号-中兴
     * 1059 BBU基带板光口号-中兴
     * -------------自动开站4g(1150-1249)----------------------
     * 1150 BBU型号
     * 1151 RRU型号
     * 1152 主控板型号
     * 1153 基带板型号
     * 1154 扩展板图片(诺西子数据)
     * 1155 RRU型号图片(小区信息数据)--扇区下的RRU
     * 1156 FS板型号
     * 1157 主控板槽位号
     * 1158 基带板槽位号
     * 
     * -------------自动开站5g(1250-1349)----------------------
     * 1250 小区信息-RRU型号
     * 1251 机框EID-中兴
     * 1252 基带板型号-中兴
     * 
     * -------------工参自优化(1350-1449)----------------------
     * 1350 工参自优化签名
     * 1351 工参自优化方位角
     * 1352 工参自优化下倾角
     * 
     * -------------集中入网5G(1450-1549)----------------------
     * 1450 集中入网5G签名-建设人员
     * 1451 集中入网5G签名-维护人员
     * 1452 集中入网5G签名-网优人员
     * 1453-1483见DbCreatOrUpdate.addJzrwDetailsItem
     * 1484 集中入网5G-AAU-序列号
     * 1485 集中入网5G-AAU-设备
     * 1486 集中入网5G-AAU-方位角
     * 1487 集中入网5G-AAU-下倾角
     * 1488 集中入网5G-excel文件
     * 
     * -------------集中入网4G(1550-1649)----------------------
     * 1550 集中入网5G签名-建设人员
     * 1551 集中入网5G签名-维护人员
     * 1552 集中入网5G签名-网优人员
     * 1553-1483见DbCreatOrUpdate.addJzrwDetailsItem
     * 1585 集中入网4G-RRU-设备
     * 1586 集中入网4G-RRU-方位角
     * 1587 集中入网4G-RRU-下倾角
     * 1588 集中入网4G-excel文件
     */
    private int type;
    //经度
    private double lon;
    //纬度
    private double lat;
 	//备注
    private String remarks;
    //方位角
    private String azimuth;
    //下倾角
    private String downtilt;
    //是否手动 0自动 1手动
    private int intAuto;
    //备用字段1
    private String msg1;
    //备用字段2
    private String msg2;
    //定位类型 gps.network.null
    private String locProvider;
    //精准距离
    private float accuracy;
    //当前占用的小区名称
    private String cellName;
    //网络类型 LTE,GSM
    private String netType;
    //当前占用的小区CI（LTE取enbId字段 GSM取LAC字段）
    private String enbId;
    //当前占有的小区cellid （LTE取cellId字段 GSM取CID字段）
    private String cellId;
    //当前占有的小区rsrp值(lte取rsrp字段 GSM取Sign字段)
    private String rsrp;
    //当前占有的小区LTE-ci
    private String ci;
    //拍照标题
    @Ignore
    private String titleName;
    //0普通 1方位角 2下倾角
    @Ignore
    private int cameraMode;
    //是否要定位
    @Ignore
    private boolean isGps;
    //是否允许网络定位
    @Ignore
    private boolean isNetLocation;
    //是否需要采集小区信息
    @Ignore
    private boolean isSignal;
    //是否当前级别只能一张图片 true一张 false多张
    @Ignore
    private boolean isOne;
    //是否保存数据库
    @Ignore
    private boolean isSaveDb = true;
    //如果图片不保存，则是否发送广播更新
    @Ignore
    private String actionUpdate;
    //水印
    @Ignore
    private String strMsg;
    //是否备注
    @Ignore
    private boolean isRemarks = true;
    //本地图片是否存在
    @Ignore
    private boolean isExist;
    //是否显示时间
    @Ignore
    private boolean isShowTime = true;
    //RSRP值传递---自动纠偏用
    @Ignore
    private String rsrpOld;
    @Ignore
    private String sinrOld;
    
	public String getCi() {
		return ci;
	}
	public void setCi(String ci) {
		this.ci = ci;
	}
	public String getNetType() {
		return netType;
	}
	public void setNetType(String netType) {
		this.netType = netType;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public String getLocProvider() {
		return locProvider;
	}
	public void setLocProvider(String locProvider) {
		this.locProvider = locProvider;
	}
	public String getCellName() {
		return cellName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public String getEnbId() {
		return enbId;
	}
	public void setEnbId(String enbId) {
		this.enbId = enbId;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getRsrp() {
		return rsrp;
	}
	public void setRsrp(String rsrp) {
		this.rsrp = rsrp;
	}
	public boolean isNetLocation() {
		return isNetLocation;
	}
	public void setNetLocation(boolean isNetLocation) {
		this.isNetLocation = isNetLocation;
	}
	public boolean isSignal() {
		return isSignal;
	}
	public void setSignal(boolean isSignal) {
		this.isSignal = isSignal;
	}
	public String getDowntilt() {
		return downtilt;
	}
	public void setDowntilt(String downtilt) {
		this.downtilt = downtilt;
	}
	public String getMsg1() {
		return msg1;
	}
	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}
	public String getMsg2() {
		return msg2;
	}
	public void setMsg2(String msg2) {
		this.msg2 = msg2;
	}
	public String getRsrpOld() {
		return rsrpOld == null ? "0" : rsrpOld;
	}
	public void setRsrpOld(String rsrpOld) {
		this.rsrpOld = rsrpOld;
	}
	public String getSinrOld() {
		return sinrOld == null ? "0" : sinrOld;
	}
	public void setSinrOld(String sinrOld) {
		this.sinrOld = sinrOld;
	}
	public boolean isShowTime() {
		return isShowTime;
	}
	public void setShowTime(boolean isShowTime) {
		this.isShowTime = isShowTime;
	}
	public int getLastType() {
		return lastType;
	}
	public void setLastType(int lastType) {
		this.lastType = lastType;
	}
	public boolean isExist() {
		return isExist;
	}
	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	public String getNetUrl() {
		return netUrl == null ? "" : netUrl;
	}
	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}
	public boolean isRemarks() {
		return isRemarks;
	}
	public void setRemarks(boolean isRemarks) {
		this.isRemarks = isRemarks;
	}
	public String getActionUpdate() {
		return actionUpdate == null ? "" : actionUpdate;
	}
	public void setActionUpdate(String actionUpdate) {
		this.actionUpdate = actionUpdate;
	}
	public String getStrMsg() {
		return strMsg == null ? "" : strMsg;
	}
	public void setStrMsg(String strMsg) {
		this.strMsg = strMsg;
	}
	public boolean isSaveDb() {
		return isSaveDb;
	}
	public void setSaveDb(boolean isSaveDb) {
		this.isSaveDb = isSaveDb;
	}
	public String getTitleName() {
		return titleName == null ? "" : titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public boolean isGps() {
		return isGps;
	}
	public void setGps(boolean isGps) {
		this.isGps = isGps;
	}
	public int getCameraMode() {
		return cameraMode;
	}
	public void setCameraMode(int cameraMode) {
		this.cameraMode = cameraMode;
	}
	public int getIntAuto() {
		return intAuto;
	}
	public void setIntAuto(int intAuto) {
		this.intAuto = intAuto;
	}
	public boolean isOne() {
		return isOne;
	}
	public void setOne(boolean isOne) {
		this.isOne = isOne;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getAzimuth() {
		return azimuth == null ? "" : azimuth;
	}
	public void setAzimuth(String azimuth) {
		this.azimuth = azimuth;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getSuperId() {
		return superId;
	}
	public void setSuperId(int superId) {
		this.superId = superId;
	}
	public int getLastId() {
		return lastId;
	}
	public void setLastId(int lastId) {
		this.lastId = lastId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPath() {
		return path == null ? "" : path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name == null ? "" : name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getUploadStateIv() {
		return uploadStateIv;
	}
	public void setUploadStateIv(int uploadStateIv) {
		this.uploadStateIv = uploadStateIv;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
