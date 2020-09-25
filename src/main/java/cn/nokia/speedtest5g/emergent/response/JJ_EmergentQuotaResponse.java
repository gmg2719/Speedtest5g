package cn.nokia.speedtest5g.emergent.response;

import java.util.List;

import cn.nokia.speedtest5g.app.respon.BaseRespon;

/**
 * 应急站指标 响应
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class JJ_EmergentQuotaResponse extends BaseRespon {
	private List<JJ_EmergentQuotaCellInfo> datas;

	public List<JJ_EmergentQuotaCellInfo> getDatas() {
		return datas;
	}

	public void setDatas(List<JJ_EmergentQuotaCellInfo> datas) {
		this.datas = datas;
	}
}
