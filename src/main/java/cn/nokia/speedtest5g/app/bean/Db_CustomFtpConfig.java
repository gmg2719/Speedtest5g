package cn.nokia.speedtest5g.app.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 自定义服务器配置数据
 * @author JQJ
 *
 */
@Table("customFtpConfig")
public class Db_CustomFtpConfig {

	// 设置为主键,自增
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	public Long id;

	public String userId;

	public String customDownIp;//下载IP
	public String customDownPort;//下载端口号
	public String customDownPath;//下载文件路径
	public String customDownThread;//下载线程
	public String customDownAccount;//下载用户名
	public String customDownPassword;//下载密码

	public String customUpIp;//上传IP
	public String customUpPort;//上传端口号
	public String customUpSize;//上传大小
	public String customUpThread;//下载线程
	public String customUpAccount;//上传用户名
	public String customUpPassword;//上传密码

	public String ftpType;//类型 上传：UPLOAD, 下载：DOWNLOAD

	public String time; // 时间

}
