package cn.nokia.speedtest5g.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.InputMethodUtil;
import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.request.RequestSmsCode;
import cn.nokia.speedtest5g.app.request.RequestSmsCodeLogin;
import cn.nokia.speedtest5g.app.respon.SmsCodeLoginResponse;
import cn.nokia.speedtest5g.app.respon.SmsCodeResponse;
import cn.nokia.speedtest5g.app.uitl.AnimationUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.view.MyAutoCompleteTextView;

/**
 * 登录页面
 * @author zwq
 *
 */
public class LoginActivity extends BaseActionBarHandlerActivity implements OnFocusChangeListener{

    private LinearLayout layoutContent;
    // 分割线：用户，密码，验证码
    private View lineUser, lineCode;
    //用户
    private MyAutoCompleteTextView etPhone;
    //验证码，密码
    private EditText etCode;
    // 获取验证码按钮--短信
    private Button mBtnCode;
    // 密码输入布局，验证码布局根部，验证码子布局
    private View mLayoutCode;
    //错误消息提示
    private TextView mTvMsg;
    // 获取短信验证码响应信息
    private TextView tvSmsCodeResponseMsg;
    // 获取短信验证码响应信息
    private SmsCodeResponse smsCodeResponse;
    private TextView mTvTilteAcount, mTvTilteCode;
    //游客登陆  用户ID sessionID
    private String mTouristUserId = "";

    private TextView mTvPersonalPolicy , mTvServiceUsageAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        init("", false);

        SpeedTestServerRequestUtil.getInstance().requestCenterConfig();
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);
        showNeedHijack();
        int heightD = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().HEIGHT(), 0);
        layoutContent = (LinearLayout) findViewById(R.id.login_layout);
        LayoutParams layoutParams = layoutContent.getLayoutParams();
        layoutParams.height = heightD - heightD/10;
        layoutContent.setLayoutParams(layoutParams);

        lineUser = findViewById(R.id.login_et_user_line);
        lineCode = findViewById(R.id.login_code_line);
        tvSmsCodeResponseMsg = (TextView) findViewById(R.id.login_tv_sms_code_response_msg);

        mTvMsg   	= (TextView) findViewById(R.id.login_tv_msg);
        mBtnCode 	= (Button) findViewById(R.id.login_btn_code);
        mLayoutCode = findViewById(R.id.login_layout_code);
        etPhone   	= (MyAutoCompleteTextView) findViewById(R.id.login_et_user);
        etCode 		= (EditText) findViewById(R.id.login_et_code);
        etPhone.setDrawableRightClose(R.drawable.clear2);

        mTvTilteAcount = (TextView) findViewById(R.id.login_et_tv_acount);
        mTvTilteCode = (TextView) findViewById(R.id.login_et_tv_code);

        mTvPersonalPolicy = (TextView) findViewById(R.id.login_tv_grys);
        mTvServiceUsageAgreement = (TextView) findViewById(R.id.login_tv_fwsy);
        mTvPersonalPolicy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvPersonalPolicy.getPaint().setAntiAlias(true);//抗锯齿
        mTvServiceUsageAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvServiceUsageAgreement.getPaint().setAntiAlias(true);//抗锯齿

        etPhone.setOnFocusChangeListener(this);
        etCode.setOnFocusChangeListener(this);
        etPhone.setListenerBack(this);

        mTouristUserId = SharedPreHandler.getShared(this).getStringShared("TouristUserId", "");

        initData();
    }

    private void initData(){
        etPhone.setFirstShow(false);
        etPhone.initHistory(TypeKey.getInstance().HISTORY_LOGIN_WANGYOUBAO);
        mLayoutCode.setVisibility(View.VISIBLE);
        tvSmsCodeResponseMsg.setVisibility(View.INVISIBLE);
        etPhone.setHint("请输入手机号");
        etCode.setHint("请输入验证码");

        String no = getPhoneNumFromLocal();
        etPhone.setText(no);
    }

    public void onBtnListener(View v){
        int id = v.getId();
        if (id == R.id.login_btn_code) {//验证码获取--短信
            if (etPhone.getText().toString().isEmpty()) {
                showMsgToast("请输入手机号", etPhone);
                return;
            }

            InputMethodUtil.getInstances().inputMethod(this, etPhone.getWindowToken());
            showMyDialog();
            RequestSmsCode smsCodeRequest = new RequestSmsCode();
            smsCodeRequest.mobile = (etPhone.getText().toString());
            smsCodeRequest.imei = (SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
            smsCodeRequest.imsi = (SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
            smsCodeRequest.userId = mTouristUserId;
            NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_SMS_CODE_QG),
                    JsonHandler.getHandler().toJson(smsCodeRequest), EnumRequest.NET_GET_SMS_VERIFICATION_CODE.toInt(), this, 30 * 1000);
        } else if (id == R.id.login_btn_ok) {//登录
            if (etPhone.getText().toString().isEmpty()) {
                showMsgToast("请输入手机号", etPhone);
            } else {
                if (etPhone.getText().toString().length() != 11) {
                    showMsgToast("请输入正确手机号", etPhone);
                    return;
                } else if (etCode.getText().toString().isEmpty()) {
                    showMsgToast(etCode.getHint().toString(), etCode);
                    return;
                } else if ((etCode.getText().toString().trim().length() < 6 ||
                        smsCodeResponse == null || smsCodeResponse.getDatas() == null ||
                        !etCode.getText().toString().equals(smsCodeResponse.getDatas().smsCode))) {
                    loginErr("验证码错误,请重新输入");
                    return;
                }
                showMyDialog();
                RequestSmsCodeLogin smsCodeLoginRequset = new RequestSmsCodeLogin();
                smsCodeLoginRequset.mobile = (etPhone.getText().toString());
                smsCodeLoginRequset.imei = (SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
                smsCodeLoginRequset.imsi = (SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
                smsCodeLoginRequset.overdue = (true);
                if (smsCodeResponse != null) {
                    smsCodeLoginRequset.captcha = (etCode.getText().toString());
                    smsCodeLoginRequset.smsId = (smsCodeResponse.getDatas().sms_id);
                    smsCodeLoginRequset.userId = smsCodeResponse.getDatas().user_id;
                    smsCodeLoginRequset.sessionId = (smsCodeResponse.getDatas().getSession_id());
                }
                NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_APP_LOGIN_QG),
                        JsonHandler.getHandler().toJson(smsCodeLoginRequset), EnumRequest.NET_LOGIN_BY_SMS_CODE.toInt(), LoginActivity.this, 30 * 1000);
            }
        } else if (id == R.id.login_tv_grys) { //个人隐私保护政策
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            Intent intent = new Intent(this, BookWebActivity.class);
            intent.putExtra("Url", SpeedTestDataSet.mPersonalCenterMap.get("4682"));
            intent.putExtra("Title", "个人隐私保护政策");
            goIntent(intent, false);
        } else if (id == R.id.login_tv_fwsy) { //服务使用协议
            Intent intent;
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            intent = new Intent(this, BookWebActivity.class);
            intent.putExtra("Url", SpeedTestDataSet.mPersonalCenterMap.get("4663"));
            intent.putExtra("Title", "服务使用协议");
            goIntent(intent, false);
        }
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        if (type == EnumRequest.NET_GET_SMS_VERIFICATION_CODE.toInt()) {// 获取手机号及验证码(新)
            dismissMyDialog();
            tvSmsCodeResponseMsg.setVisibility(View.VISIBLE);;
            if (isTrue) {
                smsCodeResponse = JsonHandler.getHandler().getTarget(object.toString(), SmsCodeResponse.class);
                if (smsCodeResponse != null) {
                    if (smsCodeResponse.isRs()) {
                        mBtnCode.setEnabled(false);
                        tvSmsCodeResponseMsg.setText("验证码发送至" + UtilHandler.getInstance().hidePhoneNoMid4(smsCodeResponse.getDatas().mobile));
                        tvSmsCodeResponseMsg.setTextColor(getResources().getColor(R.color.ui_green));
                        sendMessage(new MyEvents(ModeEnum.OTHER,RESULT_CODE_OK_TWO), RESULT_CODE_OK_TWO);
                    }else {
                        tvSmsCodeResponseMsg.setTextColor(getResources().getColor(R.color.red));
                        tvSmsCodeResponseMsg.setText(smsCodeResponse.getMsg());
                    }
                }else {
                    loginErr("获取验证码失败");
                }
            }else {
                loginErr(object.toString());
            }
        } else if (type == EnumRequest.NET_LOGIN_BY_SMS_CODE.toInt()) {// 短信验证码登入(新)
            dismissMyDialog();
            if (isTrue) {
                SmsCodeLoginResponse smsCodeLoginResponse = JsonHandler.getHandler().getTarget(object.toString(), SmsCodeLoginResponse.class);
                if (smsCodeLoginResponse != null) {
                    if (smsCodeLoginResponse.isRs()) {
                        smsCodeLoginSuccess(smsCodeLoginResponse);
                    } else {
                        loginErr(smsCodeLoginResponse.getMsg());
                        if (smsCodeLoginResponse.getDatas().isRefresh()) {// 需要重新验证
                            SharedPreHandler.getShared(SpeedTest5g.getContext()).setLongShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_TIME, 0);
                        }
                    }
                } else {
                    loginErr("网络开小差了");
                }
            }else {
                loginErr(object.toString());
            }
        }
    }

    /**
     * 短信验证码登入成功
     *
     * @param smsCodeLoginResponse
     */
    private void smsCodeLoginSuccess(SmsCodeLoginResponse smsCodeLoginResponse){
        etPhone.saveHistory(etPhone.getText().toString(), TypeKey.getInstance().HISTORY_LOGIN_WANGYOUBAO);

        //判断TouristUserId与PhoneUserId   一样的话 不处理 ，不一样的话 清掉TouristUserId  TouristSessionId
        String touristUserId = SharedPreHandler.getShared(this).getStringShared("TouristUserId", "");
        if(!touristUserId.equals(smsCodeLoginResponse.getDatas().getUser_id())){
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristUserId", "");
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristSessionId", "");
        }

        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("QgLoginType", "Phone");

        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneUserId", smsCodeLoginResponse.getDatas().getUser_id());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneSessionId", smsCodeLoginResponse.getDatas().getSession_id());

        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, smsCodeLoginResponse.getDatas().getSession_id());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_NAME(), Base64Utils.encrytorDes3(smsCodeLoginResponse.getDatas().getPhone()));
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setLongShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_TIME, smsCodeLoginResponse.getDatas().getMsgLogin_time());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setLongShared(TypeKey.getInstance().NOW_TIME, smsCodeLoginResponse.getDatas().getNow_time());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setIntShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_OVERDUE_TIME, smsCodeLoginResponse.getDatas().getOverdue_time());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_DEPART(), smsCodeLoginResponse.getDatas().getDepart_codes());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_ID(), smsCodeLoginResponse.getDatas().getUser_id());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_MENU(), smsCodeLoginResponse.getDatas().getMenu_ids());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_MZ(), smsCodeLoginResponse.getDatas().getUser_name());
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().USER_PHONE(), Base64Utils.encrytorDes3(smsCodeLoginResponse.getDatas().getPhone()));

        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("QgLoginType", "Phone");

        //发广播更新 radiobButton
        Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
        intent.putExtra("type", -1000);
        LoginActivity.this.sendBroadcast(intent);
        LoginActivity.this.finish();

    }

    //显示错误信息提示
    private void loginErr(String strMsg){
        if (isFinishing()) {
            return;
        }
        if (TextUtils.isEmpty(strMsg) || strMsg.length() > 200) {
            strMsg = "网络开小差了";
        }
        isTemporary = true;
        if (mToastDialog == null) {
            mToastDialog = new CommonDialog(LoginActivity.this);
        }
        mToastDialog.show(strMsg);
    }

    //信息提示-替换toast模式
    private void showMsgToast(String strMsg,EditText et){
        if (isFinishing()) {
            return;
        }
        mHandler.removeMessages(0);
        mTvMsg.setText(strMsg);
        AnimationUtil.getInstances().startTopAnim(mTvMsg);
        sendMessage(new MyEvents(ModeEnum.OTHER,RESULT_CODE_OK),RESULT_CODE_OK, 3000);
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodUtil.getInstances().show(LoginActivity.this);
        }
    }

    //剩余秒
    private int second = 60;
    @Override
    public void onHandleMessage(MyEvents events) {
        switch (events.getMode()) {
            case OTHER:
                if (events.getType() == RESULT_CODE_OK) {
                    mTvMsg.setVisibility(View.GONE);
                    //短信验证时间
                }else if (events.getType() == RESULT_CODE_OK_TWO) {
                    mBtnCode.setText(second + "秒后重试");
                    if (0 == second) {
                        mBtnCode.setText("重新获取验证码");
                        mBtnCode.setEnabled(true);
                        second = 60;
                    }else {
                        second--;
                        sendMessage(events, RESULT_CODE_OK_TWO, 1000);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.login_et_user) {
            if (hasFocus) {
                mTvTilteAcount.setTextColor(getResources().getColor(R.color.white));
                lineUser.setBackgroundColor(getResources().getColor(R.color.blue_layer));
                try {
                    if (TextUtils.isEmpty(etPhone.getText().toString())) {
                        etPhone.showDropDown();
                    }
                } catch (Exception e) {
                }
            } else {
                mTvTilteAcount.setTextColor(getResources().getColor(R.color.black_micro));
                lineUser.setBackgroundColor(getResources().getColor(R.color.black_micro));
            }
        } else if (id == R.id.login_et_code) {
            if (hasFocus) {
                mTvTilteCode.setTextColor(getResources().getColor(R.color.white));
                lineCode.setBackgroundColor(getResources().getColor(R.color.blue_layer));
            } else {
                mTvTilteCode.setTextColor(getResources().getColor(R.color.black_micro));
                lineCode.setBackgroundColor(getResources().getColor(R.color.black_micro));
            }
        }
    }

    //获取本机手机号
    private String getPhoneNumFromLocal(){
        try{
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String ret = tm.getLine1Number();
            if(TextUtils.isEmpty(ret)){
                return null;
            }else{
                ret = ret.substring(3, 14);
                return ret;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
