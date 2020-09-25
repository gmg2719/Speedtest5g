package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FTP地址列表数据
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
@Table("FtpAddressSuperDb")
public class Db_JJ_FtpAddressInfo implements Serializable, Parcelable {
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private int _id;// 设置为主键,自增  
	private int superId;//上一级id
	private String cityName;//地市
	private String ip;//IP
	private Integer port;//端口
	private String account;//ftp账户
	private String password;//ftp密码
	private String filename;//ftp文件路径
	private Integer	uploadSize;//上传大小
	private int threadNum;//线程数---针对自定义地址
	private boolean isDownSelect;//下载是否选中
	private boolean isUpSelect;//上传是否选中
	private boolean isCustom;//是否个性化配置
	private int ftpType;//ftp测试类型 1 ftp测试工具， 2 网络挑刺，3 定点测试，4 室内扫楼，5 属地化巡检,6 道路测试 ,7 室分单验 ,8 宏站CQT单验,10投诉测试
	public String loadType; //ALL DOWNLOAD UPLOAD 
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeInt(superId);
		dest.writeString(cityName);
		dest.writeString(ip);
		dest.writeInt(port);
		dest.writeString(account);
		dest.writeString(password);
		dest.writeString(filename);
		dest.writeInt(uploadSize);
		dest.writeInt(threadNum);
		dest.writeInt(isDownSelect ? 1 : 0);
		dest.writeInt(isUpSelect ? 1 : 0);
		dest.writeInt(isCustom ? 1 : 0);
		dest.writeInt(ftpType);
		dest.writeString(loadType);
	}

	public static final Creator<Db_JJ_FtpAddressInfo> CREATOR = new Creator<Db_JJ_FtpAddressInfo>() {
		@Override
		public Db_JJ_FtpAddressInfo createFromParcel(Parcel source) {
			Db_JJ_FtpAddressInfo info = new Db_JJ_FtpAddressInfo();
			info._id = source.readInt();
			info.superId = source.readInt();
			info.cityName = source.readString();
			info.ip = source.readString();
			info.port = source.readInt();
			info.account = source.readString();
			info.password = source.readString();
			info.filename = source.readString();
			info.uploadSize = source.readInt();
			info.threadNum = source.readInt();
			info.isDownSelect = source.readInt() == 1;
			info.isUpSelect = source.readInt() == 1;
			info.isCustom = source.readInt() == 1;
			info.ftpType = source.readInt();
			info.loadType = source.readString();
			return info;
		}

		@Override
		public Db_JJ_FtpAddressInfo[] newArray(int size) {
			return new Db_JJ_FtpAddressInfo[size];
		}
	};

	public int get_id() {
		return _id;
	}
	public String getCityName() {
		return cityName;
	}
	public String getIp() {
		return ip;
	}
	public Integer getPort() {
		return port;
	}
	public String getAccount() {
		return account;
	}
	public String getPassword() {
		return password;
	}
	public String getFilename() {
		return filename;
	}
	public Integer getUploadSize() {
		return uploadSize;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setUploadSize(Integer uploadSize) {
		this.uploadSize = uploadSize;
	}
	public int getSuperId() {
		return superId;
	}
	public void setSuperId(int superId) {
		this.superId = superId;
	}
	public boolean isDownSelect() {
		return isDownSelect;
	}
	public boolean isUpSelect() {
		return isUpSelect;
	}
	public void setDownSelect(boolean isDownSelect) {
		this.isDownSelect = isDownSelect;
	}
	public void setUpSelect(boolean isUpSelect) {
		this.isUpSelect = isUpSelect;
	}
	public int getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	public boolean isCustom() {
		return isCustom;
	}
	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}
	public int getFtpType() {
		return ftpType;
	}
	public void setFtpType(int ftpType) {
		this.ftpType = ftpType;
	}
}
