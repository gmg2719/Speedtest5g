package cn.nokia.speedtest5g.speedtest.bean;

import java.util.List;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 速率PK组列表
 *
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class ResponseSpeedPkHistoryList extends BaseRespon {
	public List<BeanSpeedPkGroupInfo> datas;
}
