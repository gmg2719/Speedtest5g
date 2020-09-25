package cn.nokia.speedtest5g.app.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 手机应用
 *
 * @author jinhaizheng
 */
@Table("phoneAppInfoTab")
public class Db_PhoneAppInfo {
	// 设置为主键,自增
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	public Long id;
	public Long phonePerfParamId;

	public String name;// 应用名称
	public String packageName;// 包名
	public String versionName;// 应用版本名
	public int versionCode;// 应用版本号
	public boolean isSystemApp;// 是否系统应用
	public String firstInstallTime; // 首次安装时间
	public String lastUpdateTime;// 最后更新时间
	public boolean isRunning;// 运行状态
	public int uid;// 应用id
	public int pid;// 进程id，未运行pid为null

	public long uidRxBytes; // 接收字节数，总接收量Byte
	public long uidTxBytes; // 发送字节数，总发送量Byte
	public double realNetSpeed; // 实时流量Bytes/s

	public long maxRAM;// 最大可分配内存Bytes
	public long totalRAM;// 总内存Bytes
	public long freeRAM;// 剩余可用内存Bytes
	public long usedRAM;// 占用内存Bytes

	public long usedStorage;// 占用存储Bytes=应用大小+用户数据+用户数据
	public long codeSize;// 应用大小Bytes
	public long dataSize;// 用户数据Bytes
	public long cacheSize;// 缓存Bytes

	public String usedCPU;// 占用CPU

	public boolean isUploaded; // 已上传

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Db_PhoneAppInfo other = (Db_PhoneAppInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}
}
