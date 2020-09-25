package cn.nokia.speedtest5g.app.respon;


/**
 * 版本升级
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class ApkInfo extends BaseRespon {

	private Apk datas;

	public Apk getDatas() {
		return datas;
	}

	public void setDatas(Apk datas) {
		this.datas = datas;
	}
}
