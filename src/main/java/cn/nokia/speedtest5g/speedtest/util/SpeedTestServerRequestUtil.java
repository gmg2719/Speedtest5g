package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.request.BaseRequest;
import cn.nokia.speedtest5g.app.request.RequestSmsCodeLogin;
import cn.nokia.speedtest5g.app.respon.Login;
import cn.nokia.speedtest5g.app.respon.PersonalCenterBean;
import cn.nokia.speedtest5g.app.respon.PersonalCenterRespon;
import cn.nokia.speedtest5g.app.respon.ResponseHomeInitData;
import cn.nokia.speedtest5g.app.respon.SmsCodeLoginResponse;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.RequestLoginFromBamin;
import cn.nokia.speedtest5g.speedtest.bean.ResponseBaseConfig;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.wifi.bean.WifiMacManu;

/**
 * 服务器配置 登录等请求集合类
 * @author JQJ
 *
 */
public class SpeedTestServerRequestUtil {

    private Context mContext = null;
    //消息提示框
    public CommonDialog mToastDialog = null;

    // 免验登入超期
    private boolean overdue = true;
    private long currentTime;
    private long lastSmsCodeLoginTime;
    // 短信登入免验证期限
    private int limitDays;
    private String mPhoneNo = "";
    //手机登陆  用户ID sessionID
    private String mPhoneUserId = "";
    private String mPhoneSessionId = "";
    //游客登陆  用户ID sessionID
    private String mTouristUserId = "";
    private String mTouristSessionId = "";

    private long mLastUpdateTime = 0;

    private static SpeedTestServerRequestUtil instance = new SpeedTestServerRequestUtil();

    private SpeedTestServerRequestUtil(){
    }

    public static SpeedTestServerRequestUtil getInstance(){
        return instance;
    }

    public void setContext(Context context){
        this.mContext = context;
    }

    public void requestServerData(){
        getMacManuInfo();
        requestCenterConfig();
        requestBaseConfig();
    }

    public void getMacManuInfo() {
        if(mContext == null){
            return;
        }

        if(SpeedTestDataSet.mWifiMacManuList.size() > 0){
            return;
        }

        new Thread(){
            @Override
            public void run() {
                try{
                    AssetManager manager = mContext.getAssets();
                    InputStream is = null;
                    is = manager.open("oui.csv");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    int loop = 0;
                    while ((line = reader.readLine()) != null) {
                        loop++;
                        String[] info = line.split(",");
                        if(info.length > 1){
                            WifiMacManu mm = new WifiMacManu();
                            mm.id=(loop);
                            mm.assignment=info[0];
                            mm.organizationName=info[1];
                            SpeedTestDataSet.mWifiMacManuList.add(mm);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    /**
     * 请求通知信息
     */
    public void requestNotice() {
        try{
            if(mContext == null){
                return;
            }
            if(NetInfoUtil.isNetworkConnected(mContext)){
                if (System.currentTimeMillis() - mLastUpdateTime >= 10000) { //至少10S请求次
                    String requestUrl = NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_HOME_DATA_INIT_QG);
                    String jsonData = JsonHandler.getHandler().toJson(new BaseRequest());
                    NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(requestUrl, jsonData,
                            -1, new ListenerBack() {

                                @Override
                                public void onListener(int type, Object object, boolean isTrue) {
                                    if(isTrue){
                                        ResponseHomeInitData response = JsonHandler.getHandler().getTarget(object.toString(), ResponseHomeInitData.class);
                                        SpeedTestDataSet.mResponseHomeInitData = response;
                                        mLastUpdateTime = System.currentTimeMillis();
                                    }
                                }
                            });
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //请求配置
    public void requestBaseConfig(){
        try{
            if(mContext == null){
                return;
            }
            BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
            if(beanBaseConfig != null){
                return;
            }
            if(NetInfoUtil.isNetworkConnected(mContext)){
                BaseRequest request = new BaseRequest();
                NetWorkUtilNow.getInstances().readNetworkPostJsonObjectNoCancel(
                        NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_APP_GET_SPEED_TEST_RANK_CONFIG),
                        JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

                            @Override
                            public void onListener(int type, Object object, boolean isTrue) {
                                if(isTrue){
                                    ResponseBaseConfig responseBaseConfig = JsonHandler.getHandler().getTarget(object.toString(),
                                            ResponseBaseConfig.class);
                                    if(responseBaseConfig != null){
                                        SpeedTestDataSet.mBeanBaseConfig = responseBaseConfig.datas;
                                    }
                                }
                            }
                        });
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 请求个人中心配置地址
     */
    public void requestCenterConfig(){
        try{
            if(mContext == null){
                return;
            }
            HashMap<String, String> personalCenterMap = SpeedTestDataSet.mPersonalCenterMap;
            if(personalCenterMap != null){
                return;
            }
            if(NetInfoUtil.isNetworkConnected(mContext)){
                String requestUrl = NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_APP_PERSONAL_CENTER);
                String jsonData = JsonHandler.getHandler().toJson(new BaseRequest());
                NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(requestUrl, jsonData,
                        -1, new ListenerBack() {

                            @Override
                            public void onListener(int type, Object object, boolean isTrue) {
                                try{
                                    PersonalCenterRespon response = JsonHandler.getHandler().getTarget(object.toString(), PersonalCenterRespon.class);
                                    if (response != null) {
                                        if (response.isRs()) {
                                            ArrayList<PersonalCenterBean> personalCenterList = response.datas;
                                            if(personalCenterList != null && personalCenterList.size() > 0){
                                                SpeedTestDataSet.mPersonalCenterMap = new HashMap<String, String>();
                                                for(PersonalCenterBean bean : personalCenterList){
                                                    SpeedTestDataSet.mPersonalCenterMap.put(bean.ITEM_ID, bean.ITEM_VALUE);
                                                }
                                                HashMap<String, String> map = SpeedTestDataSet.mPersonalCenterMap;
                                                //获取屏幕大小
                                                int height = SharedPreHandler.getShared(mContext).getIntShared(TypeKey.getInstance().HEIGHT(), 0);
                                                String url = SharedPreHandler.getShared(mContext).getStringShared("SplashScreenUrl", null);
                                                if(url == null){
                                                    //设置闪屏图片地址
                                                    if(height >= 2000){ //大屏
                                                        SharedPreHandler.getShared(mContext).setStrShared("SplashScreenUrl", map.get("4642"));
                                                    }else{
                                                        SharedPreHandler.getShared(mContext).setStrShared("SplashScreenUrl", map.get("57"));
                                                    }
                                                }else{ //判断是否有变更
                                                    if(height >= 2000){ //大屏
                                                        if(!url.equals(map.get("4642"))){
                                                            SharedPreHandler.getShared(mContext).setStrShared("SplashScreenUrl", map.get("4642"));
                                                        }
                                                    }else{
                                                        if(!url.equals(map.get("57"))){
                                                            SharedPreHandler.getShared(mContext).setStrShared("SplashScreenUrl", map.get("57"));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 八闽登陆
     * @param appKey
     * @param secureKey
     * @param outerUserId
     * @param outerUserName
     * @param outerUserMobile
     * @return  true 登陆成功 false 登陆失败
     */
    public void loadToServerFromBamin(int affairType, String appKey, String secureKey, String outerUserId, String outerUserName,
                                      String outerUserMobile, ListenerBack listenerBack){
        try{
            mListenerBack = listenerBack;
            if(NetInfoUtil.isNetworkConnected(mContext)) { //有网络直接登录
                RequestLoginFromBamin request = new RequestLoginFromBamin();
                request.appType = "OUTER";
                request.affairType = String.valueOf(affairType);
                request.appKey = appKey; //appKey
                request.secureKey = secureKey; //安全密匙
                request.outerUserId = outerUserId; //八闽UserId
                request.outerUserName = outerUserName; //八闽用户名
                request.outerUserMible = outerUserMobile; //八闽手机号
                request.phoneModel = SystemUtil.getInstance().getSystemModel(); //机型
                NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_AAR_LOGIN_CHECK),
                        JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

                            @Override
                            public void onListener(int type, Object object, boolean isTrue) {
                                if (isTrue) {
                                    Login response = JsonHandler.getHandler().getTarget(object.toString(), Login.class);
                                    if (response != null) {
                                        if (response.isRs()) {
                                            SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Phone");
                                            SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, response.getDatas().getSession_id());
                                            SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_NAME(), response.getDatas().getUser_name());
                                            SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_MENU(), response.getDatas().getMenu_ids());
                                            SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_ID(), response.getDatas().getUser_id());
                                            SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().USER_PHONE(), response.getDatas().getPhone());

                                            mListenerBack.onListener(-1, null, true);
                                        }else{
                                            mListenerBack.onListener(-1, null, false);
                                        }
                                    }else{
                                        mListenerBack.onListener(-1, null, false);
                                    }
                                }else{
                                    mListenerBack.onListener(-1, null, false);
                                }
                            }
                        }, 8 * 1000);
            }else{
                mListenerBack.onListener(-1, null, false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 登陆处理
     */
    private ListenerBack mListenerBack = null;
    public void loadToServer(ListenerBack listenerBack){
        try{
            mListenerBack = listenerBack;
            if(NetInfoUtil.isNetworkConnected(mContext)){ //有网络直接登录
                mPhoneNo = Base64Utils.decryptorDes3(SharedPreHandler.getShared(mContext).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
                mPhoneUserId = SharedPreHandler.getShared(mContext).getStringShared("PhoneUserId", "");
                mPhoneSessionId = SharedPreHandler.getShared(mContext).getStringShared("PhoneSessionId", "");
                mTouristUserId = SharedPreHandler.getShared(mContext).getStringShared("TouristUserId", "");
                mTouristSessionId = SharedPreHandler.getShared(mContext).getStringShared("TouristSessionId", "");
                currentTime  = SharedPreHandler.getShared(mContext).getLongShared(TypeKey.getInstance().NOW_TIME, System.currentTimeMillis());
                lastSmsCodeLoginTime = SharedPreHandler.getShared(mContext).getLongShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_TIME, 0);
                limitDays = SharedPreHandler.getShared(mContext).getIntShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_OVERDUE_TIME, 0);
                if (TimeUtil.getInstance().isOverDays(currentTime, lastSmsCodeLoginTime, limitDays)) {// 短信验证登录过期
                    overdue = true;
                }else {
                    overdue = false;
                }

                //优先手机号登陆    这里判断是游客登陆还是手机号登陆  (免验过期走游客登陆)
                if(!TextUtils.isEmpty(mPhoneUserId) && !TextUtils.isEmpty(mPhoneSessionId)
                        && !TextUtils.isEmpty(mPhoneNo) && !overdue){ //手机号免验登陆
                    RequestSmsCodeLogin smsCodeLoginRequset = new RequestSmsCodeLogin();
                    smsCodeLoginRequset.mobile=(mPhoneNo);
                    smsCodeLoginRequset.imei=(SharedPreHandler.getShared(mContext).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
                    smsCodeLoginRequset.imsi=(SharedPreHandler.getShared(mContext).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
                    smsCodeLoginRequset.overdue=(overdue);
                    smsCodeLoginRequset.userId=mPhoneUserId;
                    smsCodeLoginRequset.sessionId=mPhoneSessionId;
                    NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_APP_LOGIN_QG),
                            JsonHandler.getHandler().toJson(smsCodeLoginRequset), -1, new ListenerBack() {

                                @Override
                                public void onListener(int type, Object object, boolean isTrue) {
                                    if (isTrue) {
                                        SmsCodeLoginResponse smsCodeLoginResponse = JsonHandler.getHandler().getTarget(object.toString(),
                                                SmsCodeLoginResponse.class);
                                        if (smsCodeLoginResponse != null) {
                                            if (smsCodeLoginResponse.isRs()) {
                                                smsCodeLoginSuccess(smsCodeLoginResponse);
                                            } else {
                                                loginErr(smsCodeLoginResponse.getMsg());
                                            }
                                        } else {
                                            loginErr("网络开小差了");
                                        }
                                    }else {
                                        loginErr(object.toString());
                                    }
                                }
                            }, 30 * 1000);
                }else{
                    RequestSmsCodeLogin smsCodeLoginRequset = new RequestSmsCodeLogin();
                    smsCodeLoginRequset.imsi=(SharedPreHandler.getShared(mContext).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
                    smsCodeLoginRequset.imei=(SharedPreHandler.getShared(mContext).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
                    smsCodeLoginRequset.userId=mTouristUserId;
                    smsCodeLoginRequset.sessionId=mTouristSessionId;
                    smsCodeLoginRequset.mobile=("-1");
                    smsCodeLoginRequset.uuid=UUID.randomUUID().toString();
                    NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + mContext.getString(R.string.URL_APP_LOGIN_QG),
                            JsonHandler.getHandler().toJson(smsCodeLoginRequset), -1, new ListenerBack() {

                                @Override
                                public void onListener(int type, Object object, boolean isTrue) {
                                    if (isTrue) {
                                        Login mResponseLogin = JsonHandler.getHandler().getTarget(object.toString(), Login.class);
                                        if (mResponseLogin != null){
                                            if(mResponseLogin.isRs()) {
                                                saveTouristUserData(mResponseLogin);
                                            }else{
                                                loginErr(mResponseLogin.getMsg());
                                            }
                                        }
                                    }else {
                                        loginErr(object.toString());
                                    }
                                }
                            }, 30 * 1000);
                }
            }else{
                mListenerBack.onListener(-1, null, false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 游客登录
     * @param info
     */
    private void saveTouristUserData(Login info){

//        SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Tourist");
        SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Phone");

        SharedPreHandler.getShared(mContext).setStrShared("TouristUserId", info.getDatas().getUser_id());
        SharedPreHandler.getShared(mContext).setStrShared("TouristSessionId", info.getDatas().getSession_id());

        SharedPreHandler.getShared(mContext).setStrShared("PhoneUserId", "");
        SharedPreHandler.getShared(mContext).setStrShared("PhoneSessionId", "");
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().USER_PHONE(), "");

        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, info.getDatas().getSession_id());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_ID(), info.getDatas().getUser_id());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_NAME(), Base64Utils.encrytorDes3(info.getDatas().getUser_name()));
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_MENU(), info.getDatas().getMenu_ids());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_DEPART(), info.getDatas().getDepart_codes());

        SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Phone");

        mListenerBack.onListener(-1, null, true);
    }

    /**
     * 短信验证码登入成功
     *
     * @param smsCodeLoginResponse
     */
    private void smsCodeLoginSuccess(SmsCodeLoginResponse smsCodeLoginResponse){

        //判断TouristUserId与PhoneUserId   一样的话 不处理 ，不一样的话 清掉TouristUserId  TouristSessionId
        String touristUserId = SharedPreHandler.getShared(mContext).getStringShared("TouristUserId", "");
        if(!touristUserId.equals(smsCodeLoginResponse.getDatas().getUser_id())){
            SharedPreHandler.getShared(mContext).setStrShared("TouristUserId", "");
            SharedPreHandler.getShared(mContext).setStrShared("TouristSessionId", "");
        }

        SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Phone");

        SharedPreHandler.getShared(mContext).setStrShared("PhoneUserId", smsCodeLoginResponse.getDatas().getUser_id());
        SharedPreHandler.getShared(mContext).setStrShared("PhoneSessionId", smsCodeLoginResponse.getDatas().getSession_id());

        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, smsCodeLoginResponse.getDatas().getSession_id());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_NAME(), Base64Utils.encrytorDes3(smsCodeLoginResponse.getDatas().getPhone()));
        SharedPreHandler.getShared(mContext).setLongShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_TIME, smsCodeLoginResponse.getDatas().getMsgLogin_time());
        SharedPreHandler.getShared(mContext).setLongShared(TypeKey.getInstance().NOW_TIME, smsCodeLoginResponse.getDatas().getNow_time());
        SharedPreHandler.getShared(mContext).setIntShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_OVERDUE_TIME, smsCodeLoginResponse.getDatas().getOverdue_time());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_DEPART(), smsCodeLoginResponse.getDatas().getDepart_codes());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_ID(), smsCodeLoginResponse.getDatas().getUser_id());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_MENU(), smsCodeLoginResponse.getDatas().getMenu_ids());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().LOGIN_MZ(), smsCodeLoginResponse.getDatas().getUser_name());
        SharedPreHandler.getShared(mContext).setStrShared(TypeKey.getInstance().USER_PHONE(), Base64Utils.encrytorDes3(smsCodeLoginResponse.getDatas().getPhone()));

        SharedPreHandler.getShared(mContext).setStrShared("QgLoginType", "Phone");

        mListenerBack.onListener(-1, null, true);
    }

    //显示错误信息提示
    private void loginErr(String strMsg){
        mListenerBack.onListener(-1, null, false);
        if (TextUtils.isEmpty(strMsg) || strMsg.length() > 200) {
            strMsg = "网络开小差了";
        }
        if (mToastDialog == null) {
            mToastDialog = new CommonDialog(mContext);
        }
        mToastDialog.show(strMsg);
    }
}
