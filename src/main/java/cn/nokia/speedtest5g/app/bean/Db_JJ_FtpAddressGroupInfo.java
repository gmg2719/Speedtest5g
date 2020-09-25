package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FTP地址 组
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
@Table("FtpAddressGroupSuperDb")
public class Db_JJ_FtpAddressGroupInfo implements Serializable, Parcelable {
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	private int _id;// 设置为主键,自增  
	private String hostType;//服务器类型  1集团公网服务器  2省公司公网服务器 3地市公网服务器 4省公司APN服务器 5地市公司APN服务器 6个性化配置APN服务器
	private String hostTypeName;//服务器类型名称	
	private boolean isDownSelect;//下载是否选中
	private boolean isUpSelect;//上传是否选中
	private boolean isCustom;//是否是用户自定义
	private int ftpType;//ftp测试类型 1 ftp测试工具， 2 网络挑刺，3 定点测试，4 室内扫楼，5 属地化巡检,6 道路测试 ,7 室分单验 ,8 宏站CQT单验,10投诉测试
	@Ignore
	private List<Db_JJ_FtpAddressInfo> ftpList = new ArrayList<>();//ftp配置列表
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(hostType);
		dest.writeString(hostTypeName);
		dest.writeInt(isDownSelect ? 1 : 0);
		dest.writeInt(isUpSelect ? 1 : 0);
		dest.writeInt(isCustom ? 1 : 0);
		dest.writeInt(ftpType);
		dest.writeList(ftpList);
	}

	public static final Creator<Db_JJ_FtpAddressGroupInfo> CREATOR = new Creator<Db_JJ_FtpAddressGroupInfo>() {

		@Override
		public Db_JJ_FtpAddressGroupInfo createFromParcel(Parcel source) {
			Db_JJ_FtpAddressGroupInfo info = new Db_JJ_FtpAddressGroupInfo();
			info._id = source.readInt();
			info.hostType = source.readString();
			info.hostTypeName = source.readString();
			info.isDownSelect = source.readInt() == 1;
			info.isUpSelect = source.readInt() == 1;
			info.isCustom = source.readInt() == 1;
			info.ftpType = source.readInt();
			source.readList(info.ftpList, Db_JJ_FtpAddressInfo.class.getClassLoader());
			return info;
		}

		@Override
		public Db_JJ_FtpAddressGroupInfo[] newArray(int size) {
			return new Db_JJ_FtpAddressGroupInfo[size];
		}
	};

	public int get_id() {
		return _id;
	}
	public String getHostType() {
		return hostType;
	}
	public String getHostTypeName() {
		return hostTypeName;
	}
	public List<Db_JJ_FtpAddressInfo> getFtpList() {
		return ftpList;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public void setHostType(String hostType) {
		this.hostType = hostType;
	}
	public void setHostTypeName(String hostTypeName) {
		this.hostTypeName = hostTypeName;
	}
	public void setFtpList(List<Db_JJ_FtpAddressInfo> ftpList) {
		this.ftpList = ftpList;
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
