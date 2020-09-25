package cn.nokia.speedtest5g.app.request;

import java.util.List;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import com.android.volley.util.SharedPreHandler;

public class JJ_RequestFtpTest extends BaseRequest{
	private int userid = UtilHandler.getInstance().toInt(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), ""), 0);
	private List<Db_JJ_FTPTestInfo> datas;
	
	public long getUserid() {
		return userid;
	}
	public List<Db_JJ_FTPTestInfo> getDatas() {
		return datas;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public void setDatas(List<Db_JJ_FTPTestInfo> datas) {
		this.datas = datas;
	}
}
