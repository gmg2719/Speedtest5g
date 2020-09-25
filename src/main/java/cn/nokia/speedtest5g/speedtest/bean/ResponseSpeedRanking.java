package cn.nokia.speedtest5g.speedtest.bean;

import java.util.ArrayList;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 测速排行
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class ResponseSpeedRanking extends BaseRespon {

	public ArrayList<BeanSpeedRanking> datas; //列表数据

}
