package cn.nokia.speedtest5g.app.request;

import java.util.List;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_JJ_AppWarningLog;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import com.android.volley.util.SharedPreHandler;

/**
 * APP告警数据请求类
 * @author xujianjun
 *
 */
public class JJ_RequestAppWarning extends BaseRequest{
	
	private int userid = UtilHandler.getInstance().toInt(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), ""), 0);
	private List<Db_JJ_AppWarningLog> datas;
	public int getUserid() {
		return userid;
	}
	public List<Db_JJ_AppWarningLog> getDatas() {
		return datas;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public void setDatas(List<Db_JJ_AppWarningLog> datas) {
		this.datas = datas;
	}
}
