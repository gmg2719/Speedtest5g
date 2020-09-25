package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;

import cn.nokia.speedtest5g.util.Base64Utils;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 统计模块点击率
 * @author zwq
 *
 */
@SuppressWarnings("serial")
@Table("statistics")
public class StatisticsInfo implements Serializable{
	
	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;
    
    //模块名称----如：MainActivity
    private String nameClass;
    
    //用数字代表模块Id
    private String module;
    
    //模块点击时间
    private String times;
    
    //操作模块的用户ID
    private String userId;
    
    //操作模块的用户号码
    private String userPhone;
    
    //手机串号
    private String imei;
    
    //上传状态 --0未上传  1已上传
    private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getNameClass() {
		return nameClass;
	}

	public void setNameClass(String nameClass) {
		this.nameClass = nameClass;
	}

	public String getModule() {
		return module == null ? "" : module;
	}

	public void setModule(String module) {
		this.module = module;
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
}
