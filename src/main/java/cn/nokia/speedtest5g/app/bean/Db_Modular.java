package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.util.Base64Utils;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 模块管理--暂无启用此功能
 * @author zwq
 */
@SuppressWarnings("serial")
@Table("modularTab")
public class Db_Modular implements Serializable{

	// 设置为主键,自增  
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public Long _id;
    //包名
    public String FIRING_PACKAGE;
    //启动类
    public String FIRING_CLASS;
    //模块名称
    public String MENU_NAME;
    //模块编码（风云榜）
    public String DICT_CODE;
    //模块权限编码
    public String CODE;
    //模块说明
    public String OUTLINE;
    //模块下载地址
    public String MENU_APP_URL;
    //模块版本（v1.0.1）
    public String VERSION_UPGRADE;
    //模块代号(版本号跟VERSION_UPGRADE对应)
    public String VERSION_NUMBER;
    //模块所属-上一级（1001：集中规划 1002：集中入网 1003：集中工参管理 1004：集中分析优化 1005：业务助手 1006：基础工具 1007：三方应用）
    public String ADSCRIPTION_TYPE;
    //模块升级模式（2强制升级，其他则无需强制-但如果无应用情况下，需强制升级）
    public String UPGRADE_TYPE;
    //模块更新内容
    public String UPGRADE_CONTENT;
    //菜单ID
    public String MENU_ID;
    //模块关键字
    public String KEYWORDS;
    //图标地址
    public String MENU_IMG_URL;
    //是否判断工参文件是否下载 1是，0否
    public String IF_PARAMETER;
    
    public boolean isParameter(){
    	return "1".equals(IF_PARAMETER);
    }
    
    public String getMENU_IMG_URL(){
    	return Base64Utils.decryptorDes3(MENU_IMG_URL);
    }
    
    public String getFIRING_PACKAGE(){
    	return Base64Utils.decryptorDes3(FIRING_PACKAGE);
    }
    
    public String getFIRING_CLASS(){
    	return Base64Utils.decryptorDes3(FIRING_CLASS);
    }
    
    public int getVERSION_NUMBER(){
    	return UtilHandler.getInstance().toInt(VERSION_NUMBER, 0);
    }
}
