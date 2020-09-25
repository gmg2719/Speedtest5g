package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 地市对应编码属性
 * @author zwq
 *
 */
@SuppressWarnings("serial")
@Table("citycode")
public class CityInfo implements Serializable{
	
	@NotNull 
    private String name; 
	
	@PrimaryKey(AssignType.BY_MYSELF)
	private String code;
	
	/**
	 * 构造函数，传入数据
	 * @param c 编码
	 * @param n 名称
	 */
	public CityInfo(String c,String n) {
		this.name = n;
		this.code = c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
