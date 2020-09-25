package cn.nokia.speedtest5g.app.respon;

import java.util.List;

import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;

/**
 * FTP测试历史响应
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class FtpSpeedHistoryResponse extends BaseRespon{
	private List<Db_JJ_FTPTestInfo> datas;

	public List<Db_JJ_FTPTestInfo> getDatas() {
		return datas;
	}

	public void setDatas(List<Db_JJ_FTPTestInfo> datas) {
		this.datas = datas;
	}

}
