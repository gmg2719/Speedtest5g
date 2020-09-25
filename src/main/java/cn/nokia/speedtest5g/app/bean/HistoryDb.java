package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 历史搜索记录表
 * @author zwq
 */
@SuppressWarnings("serial")
@Table("historyEt")
public class HistoryDb implements Serializable{
	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long _id;
    /**
     * 类型
     * 1001：
     * 1002：
     * 1003：
     * .....
     * 具体数值在TypeKey
     */
    private int type;
    
    //记录名称
    private String valueName;
    //记录时间(格式：yyyy-MM-dd HH:mm:ss)
    private String timeDate;
    //状态(默认0 可不输入)
    private int state;
    
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	public String getTimeDate() {
		return timeDate;
	}
	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
