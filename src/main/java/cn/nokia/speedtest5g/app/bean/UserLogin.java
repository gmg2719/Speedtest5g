package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import android.content.Context;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.util.Base64Utils;

import com.android.volley.util.SharedPreHandler;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 记录用户登录表信息
 * @author zwq
 *
 */
@SuppressWarnings("serial")
@Table("userlogin")
public class UserLogin implements Serializable{

	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int _id;
    
    //用户ID
    private String user_id;
    
    //当前登录手机号码
    private String phone;
    
    //手机类型
    private String model;
    
    //系统版本
    private String release;
    
    private String imei;
    
    private String imsi;
    
    public UserLogin(Context context){
    	setModel(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_MODEL(), ""));
    	setRelease(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_RELEASE(), ""));
    	setImei(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
    	setImsi(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
    	setUser_id(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().LOGIN_ID(),"").isEmpty() ? "00" : SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().LOGIN_ID(),""));
    	setPhoneDes3(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
    }

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getUser_id() {
		return user_id == null ? "" : user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getPhone() {
		return phone == null ? "" : Base64Utils.decryptorDes3(phone);
	}
	public String getPhoneOld() {
		return phone == null ? "" : phone;
	}
	public void setPhoneDes3(String phone) {
		this.phone = phone;
	}
	
	public void setPhone(String phone) {
		this.phone = Base64Utils.encrytorDes3(phone);
	}
	
	public String getModel() {
		return model == null ? "" : model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRelease() {
		return release == null ? "" : release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getImei() {
		return imei == null ? "" : imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi  == null ? "" : imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
