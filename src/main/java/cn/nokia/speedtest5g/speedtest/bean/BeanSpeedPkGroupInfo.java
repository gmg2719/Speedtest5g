package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;

/**
 * 速率PK组信息
 *
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class BeanSpeedPkGroupInfo implements Serializable {
	public long id = -1; // 每个组的唯一标识(用于查询改组下的各个成员的测试详情)
	public String rateGroupName;// 组名
	public int rateGroupStatus = 1; // 速率组状态（1：即将开始 2：正在进行中 3：测试完成）
	public String creator; // pk组创建者
	public String createTime; // pk组创建时间
	public String startTime; // 开始pk时间
	public String downloadMax;// 下载最大值
	public String downloadName;// 下载最大值组员姓名
	public String downloadLoginName;// 下载最大值组员登录账号
	public String downloadOperator;// 下载最大值运营商
	public String uploadMax;// 上传最大值
	public String uploadName;// 上传最大值组员姓名
	public String uploadLoginName;// 上传最大值组员登录账号
	public String uploadOperator;// 上传最大值运营商
	public List<Db_JJ_FTPTestInfo> groupMemberList = new ArrayList<>();// 组员列表
	public boolean isStart;// 是否开始PK
	public boolean isCancel;// PK组是否被撤销
	public boolean isJoined;// 是否已加入PK组
}
