package cn.nokia.speedtest5g.app.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 速测满意评价 用户弹窗日志
 * @author JQJ
 *
 */
@Table("userEvaluateLog")
public class Db_UserEvaluateLog {

	// 设置为主键,自增
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	public Long id;

	public String userId;
	public long time; // 时间
	public boolean isHaveShow;// 是否当天已经弹窗
	public boolean isUpload; //是否上传

}
