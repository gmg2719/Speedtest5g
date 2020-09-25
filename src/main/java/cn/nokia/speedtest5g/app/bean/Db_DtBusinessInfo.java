package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 道路测试速率测试数据表
 * @author zwq
 */
@SuppressWarnings("serial")
@Table("dtBusinessTab")
public class Db_DtBusinessInfo implements Serializable{

	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public Long _id;
    //对应LOG文件名
    public String logName;
    //测试用户
    public String userId;
    //下载最大速率
    public float dMax;
    //下载最小速率
    public float dMin;
    //下载平均速率
    public float dAvg;
    //下载时长(毫秒)
    public long dDuration;
    //下载总大小
    public double dLength;
    //上传最大速率
    public float uMax;
    //上传最小速率
    public float uMin;
    //上传平均速率
    public float uAvg;
    //上传时长(毫秒)
    public long uDuration;
    //上传总大小
    public double uLength;
    //切换成功率
    public double pSwitch;
    //掉话率
    public double pDrop;
    //接通率
    public double pConnect;
    //ping时延
    public double pingDelay;
    
    public Db_DtBusinessInfo(String userId){
    	this.userId = userId;
    }
}
