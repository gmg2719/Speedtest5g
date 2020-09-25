package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;

import com.android.volley.util.SharedPreHandler;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * app告警信息
 * 
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
@Table("AppWarningLogSuperDb")
public class Db_JJ_AppWarningLog implements Serializable {
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private int _id;// 设置为主键,自增
	private int userid;// 用户ID
	private String warningTime;//app当前时间，格式如2018-05-10 18:12:22
	private String className;//	 当前类名
	private String operateInfo;//当前操作描述
	private String mobileType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_MODEL(), "");//手机设备型号
	private String warningInfo;//告警信息
	private String imei =SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(),"");//手机imei
	private boolean isUpload;//是否上传
	public int get_id() {
		return _id;
	}
	public int getUserid() {
		return userid;
	}
	public String getWarningTime() {
		return warningTime;
	}
	public String getClassName() {
		return className;
	}
	public String getOperateInfo() {
		return operateInfo;
	}
	public String getMobileType() {
		return mobileType;
	}
	public String getWarningInfo() {
		return warningInfo;
	}
	public String getImei() {
		return imei;
	}
	public boolean isUpload() {
		return isUpload;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public void setWarningTime(String warningTime) {
		this.warningTime = warningTime;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public void setOperateInfo(String operateInfo) {
		this.operateInfo = operateInfo;
	}
	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}
	public void setWarningInfo(String warningInfo) {
		this.warningInfo = warningInfo;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}
}
