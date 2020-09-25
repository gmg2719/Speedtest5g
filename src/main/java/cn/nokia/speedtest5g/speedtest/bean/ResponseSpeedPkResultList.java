package cn.nokia.speedtest5g.speedtest.bean;

import java.util.List;

import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 速率PK组列表
 *
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class ResponseSpeedPkResultList extends BaseRespon {
	public List<Db_JJ_FTPTestInfo> datas;
}
