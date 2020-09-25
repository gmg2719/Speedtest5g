package cn.nokia.speedtest5g.speedtest.bean;

import java.util.List;

import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 速来测试历史详情数据响应
 *
 * @author jinhaizheng
 */
@SuppressWarnings("serial")
public class ResponseSpeedTestDetail extends BaseRespon {
	public Datas datas;

	public class Datas {
	    public String shareCode;
		// 上传下载信号信息列表
		public List<Signal> downloadTestList;
		public List<Signal> uploadTestList;
	}
}
