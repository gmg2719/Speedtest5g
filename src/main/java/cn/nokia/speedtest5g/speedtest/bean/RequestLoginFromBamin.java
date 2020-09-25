package cn.nokia.speedtest5g.speedtest.bean;

import com.android.volley.util.BasicUtil;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;

/**
 *  八闽,一键登录登陆请求
 * @author JQJ
 *
 */
public class RequestLoginFromBamin{

    private String imei = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""); //imei
    private String imsi = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""); //imsi
    private String versionCode = BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false).toString(); //客户端版本
    private String versionName = BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), true).toString();; //客户端版本

    public String appType; //OUTER：标识八闽   ONEKEY一键登录
    public String affairType; //模块标识
    public String appKey; //appKey
    public String secureKey; //安全密匙
    public String outerUserId; //八闽UserId
    public String outerUserName; //八闽用户名
    public String outerUserMible; //八闽手机号
    public String phoneModel; //机型
    public String token; //一键登录token
}
