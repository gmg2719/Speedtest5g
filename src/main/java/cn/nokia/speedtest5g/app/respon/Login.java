package cn.nokia.speedtest5g.app.respon;


/**
 * 登录返回的所有信息
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class Login extends BaseRespon{
	
	private LoginRespon datas;
	
	public LoginRespon getDatas() {
		return datas;
	}

	public void setDatas(LoginRespon datas) {
		this.datas = datas;
	}
}
