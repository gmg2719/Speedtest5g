package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import android.text.Spanned;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.util.Base64Utils;

import com.android.volley.util.SharedPreHandler;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 主动采集信号log对应属性
 * @author zwq
 *
 */
@SuppressWarnings("serial")
@Table("logSignal")
public class LogInfo implements Serializable{
	
	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int _id;
	//0:道路测试 1：基站信号测试
    private int testType = 0;
    //测试类型---0LTE,1TD,2GSM
	private int netType;
	//名称
	private String logName;
	//是否上传  0未上传  1已上传
	private int state;
    //log保存时间
    private String times;
    //测试log的用户ID
    private String userId;
    //测试log的用户手机号码
    private String userPhone;
    //手机类型
    private String model;
    //系统版本
    private String release;
    
    private String imei;
    
    private String imsi;
    @Ignore
    public String showName = "";
    @Ignore
    public String path;
    @Ignore
    public Spanned mSpanned = null;
    //log类型  0测试LOG(道路测试 基站信号测试) 1目录LOG 2室分单验 3室内扫楼 4网友计费系统（投诉测试）
    @Ignore
    public int logType;
    //操作的游标
    @Ignore
    public int clickPosition;
    
    public String shareCode; //分享码 
    
    public LogInfo() {
    	setModel(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_MODEL(), ""));
    	setRelease(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_RELEASE(), ""));
    	setImei(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
    	setImsi(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
	}
    
    public LogInfo(int logType) {
    	this.logType = logType;
    }

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public int getTestType() 
	{
		return testType;
	}

	public void setTestType(int testType)
	{
		this.testType = testType;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPhoneOld() {
		return userPhone;
	}
	
	public String getUserPhone() {
		return Base64Utils.decryptorDes3(userPhone);
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = Base64Utils.encrytorDes3(userPhone);
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
