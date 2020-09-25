package cn.nokia.speedtest5g.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.android.volley.util.SharedPreHandler;
import com.fjmcc.wangyoubao.app.signal.SignalServiceUtil;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.listener.MyLocationListener;
import cn.nokia.speedtest5g.app.thread.LoadingThread;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.pingtest.PingTestActivity;
import cn.nokia.speedtest5g.speedtest.SpeedTestMainActivity;
import cn.nokia.speedtest5g.speedtest.SpeedWideMainActivity;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;
import cn.nokia.speedtest5g.wifi.ui.NetworkDiagnoseActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiGlActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiSdMainActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiSnMainActivity;

/**
 * 窗口化 loading
 * @author JQJ
 */
public class LoadingActivity extends BaseActionBarHandlerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String mAppKey = null;
    private String mSecureKey = null;
    private String mOuterUserId = null;
    private String mOuterUserName = null;
    private String mOuterUserMobile = null;
    private String mColorType = null;
    private int mAffairType = 0;
    private boolean mIsShowLoading = true; //是否显示加载条  默认显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(TypeKey.getInstance().ACTION_FINISH));
        try {
            SignalServiceUtil.getInstances().stopService(this);
        } catch (Exception e) {
        }
        initData();
    }

    private void initData(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new CommonDialog(LoadingActivity.this,false)
                    .setButtonText("立即授权","退出应用")
                    .setListener(LoadingActivity.this)
                    .show("为了保证您正常、安全地使用，需要向您申请以下权限：\n1.访问设备位置信息权限(定位服务)\n2.访问SD卡存储权限(存储功能)\n3.访问电话状态权限(网络强度)\n4.访问摄像头权限(更换头像)",EnumRequest.DIALOG_TOAST_BTN_TWO.toInt());
            return;
        }

        init("", false);
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);

        Intent intent = getIntent();
        mAppKey = intent.getStringExtra("appKey");
        mSecureKey = intent.getStringExtra("secureKey");
        mOuterUserId = intent.getStringExtra("outerUserId");
        mOuterUserName = intent.getStringExtra("outerUserName");
        mOuterUserMobile = intent.getStringExtra("outerUserMobile");
        mColorType = intent.getStringExtra("colorType");
        mAffairType = intent.getIntExtra("affairType", 0);
        mIsShowLoading = intent.getBooleanExtra("isShowLoading", true);

        if(mIsShowLoading){
            showMyDialog();
        }

        MyLocationListener.getInstances().requestLocation();
        //获取屏幕高度
        if (SharedPreHandler.getShared(LoadingActivity.this).getIntShared(TypeKey.getInstance().WIDTH(), 0) <= 0 ||
                SharedPreHandler.getShared(LoadingActivity.this).getIntShared(TypeKey.getInstance().HEIGHT(), 0) <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            SharedPreHandler.getShared(LoadingActivity.this).setIntShared(TypeKey.getInstance().WIDTH(), dm.widthPixels);
            SharedPreHandler.getShared(LoadingActivity.this).setIntShared(TypeKey.getInstance().HEIGHT(), dm.heightPixels);
        }

        //初始化数据线程
        new LoadingThread(mHandler).start();
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        if (type == EnumRequest.DIALOG_TOAST_BTN_TWO.toInt()){
            if (isTrue){
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        RESULT_CODE_101);
            }else {
                LoadingActivity.this.finish();

                //发广播关闭调用页面者 finish自身
                Intent intent = new Intent(SpeedTest5g.ACTION_CALLER_FINISH);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onHandleMessage(MyEvents events) {
        if (events.getType() == EnumRequest.OTHER_INIT_IMSI.toInt()) {
            loadToServer();
        }
    }

    /**
     * 登陆处理
     */
    private void loadToServer(){
        if (TypeKey.getInstance().VerName.equals("Unkonw")) {
            //关闭所有页面
            sendBroadcast(new Intent(TypeKey.getInstance().ACTION_FINISH));
            return;
        }

        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("QgLoginType", "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_ID(), "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_DEPART(), "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_MENU(), "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_LON(), "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_LAT(), "");
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().GPS_TYPE(), "");

        //登陆处理
        SpeedTestServerRequestUtil.getInstance().loadToServerFromBamin(mAffairType, mAppKey, mSecureKey, mOuterUserId, mOuterUserName, mOuterUserMobile, new ListenerBack(){

            @Override
            public void onListener(int type, Object object, boolean isTrue) {
                if(isTrue){
                    delayGotoMain(500);
                }else{
                    dismissMyDialog();
                    showCommon("登陆失败，请稍后再试");
                    LoadingActivity.this.finish();
                }
            }
        });
    }

    /**
     * 延迟 进入主页
     */
    private void delayGotoMain(long time){

        SpeedTestServerRequestUtil.getInstance().requestServerData();

        //倒计时机制
        CountDownTimer timer = new CountDownTimer(time, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
                Intent intent = null;
                if(mAffairType == 1){ //测速
                    intent = new Intent(mActivity, SpeedTestMainActivity.class);
                }else if(mAffairType == 2){ //wifi信号检测
                    intent = new Intent(mActivity, WifiSdMainActivity.class);
                }else if(mAffairType == 3){ //wifi干扰
                    intent = new Intent(mActivity, WifiGlActivity.class);
                }else if(mAffairType == 4){ //wifi信道分析
                    intent = new Intent(mActivity, WifiActivity.class);
                }else if(mAffairType == 5){ //wifi蹭网检测
                    intent = new Intent(mActivity, WifiSnMainActivity.class);
                }else if(mAffairType == 6){ //网络诊断
                    intent = new Intent(mActivity, NetworkDiagnoseActivity.class);
                }else if(mAffairType == 7){ //ping测试
                    intent = new Intent(mActivity, PingTestActivity.class);
                }else if(mAffairType == 8){ //周边网速
                    intent = new Intent(mActivity, SpeedWideMainActivity.class);
                }
                if(intent != null){
                    mActivity.startActivity(intent);
                }else{
                    showCommon("启动类型错误");
                }
                LoadingActivity.this.finish();
            }
        };
        timer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        initData();
    }
}