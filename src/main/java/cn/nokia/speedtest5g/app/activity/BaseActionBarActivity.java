package cn.nokia.speedtest5g.app.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.util.MyToast;
import com.android.volley.util.SharedPreHandler;
//import com.baidu.mobstat.StatService;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.MyLoginExitThread;
import cn.nokia.speedtest5g.app.thread.StatisticeAsyncTask;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.dialog.loading.LoadingDialog;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar.MenuAction;

public class BaseActionBarActivity extends Activity implements ListenerBack{
    //消息提示框
    public CommonDialog mToastDialog;
    //加载对话框
    public LoadingDialog ivDialog;

    public MyActionBar actionBar;

    public final int REQUEST_CODE_SCAN = 0x0000;

    public final int RESULT_CODE_OK = 0x0001;

    public final int RESULT_CODE_OK_TWO = 0x0002;

    public final int RESULT_CODE_OK_THREE = 0x0003;

    public final int RESULT_CODE_101 = 101;
    //是否启用防钓鱼劫持，默认不启用
    public boolean isNeedHijack = false;
    //是否需要提示切换应用
    public boolean isNeedHijackToast = false;
    //临时不显示劫持提示
    public boolean isTemporary = false;
    //当前活动的Activity
    public BaseActionBarActivity mActivity;

    protected int mBgTopColor = -1;
    protected int mBgParentColor = -1;
    protected int mTitleTextColor = -1;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            MyLoginExitThread.setNewTouch(BaseActionBarActivity.this.getClass().getName());
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            getIntent().putExtras(savedInstanceState);
        }
        try {
            initStatistics();
            if (getClass().getSimpleName().indexOf("LoadingActivity") == -1 && getClass().getSimpleName().indexOf("LoadingOfficialActivity") == -1) {
                registerReceiver(myFinishCast, new IntentFilter(TypeKey.getInstance().ACTION_FINISH));
            }
        } catch (Exception e) {
        }
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        try{
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//透明模式activity 在android8.0以上（包含）不能加下面代码
                if(getClass().getSimpleName().indexOf("LoadingActivity") > -1){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                }
            }
        }catch(Exception e){
        }

//        StatService.setDebugOn(false);
        mActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //发广播关闭调用页面者 finish自身
        if (getClass().getSimpleName().indexOf("SpeedTestMainActivity") > -1 ||
                getClass().getSimpleName().indexOf("WifiSdMainActivity") > -1 ||
                getClass().getSimpleName().indexOf("WifiGlActivity") > -1 ||
                getClass().getSimpleName().indexOf("WifiSnMainActivity") > -1 ||
                getClass().getSimpleName().indexOf("NetworkDiagnoseActivity") > -1 ||
                getClass().getSimpleName().indexOf("PingTestActivity") > -1 ||
                getClass().getSimpleName().indexOf("SpeedWideMainActivity") > -1) {
            Intent intent = new Intent(SpeedTest5g.ACTION_CALLER_FINISH);
            sendBroadcast(intent);
        }
    }

    /**
     * 统计点击
     */
    public void initStatistics(){};

    /**
     * 统计点击
     * @param groupCod 主 如登录1000  退出1100
     */
    public void installStatistics(int groupCod){
        new StatisticeAsyncTask().execute(getClass().getSimpleName(),BaseActionBarActivity.this.getString(groupCod));
    }

    /**
     * 启用防钓鱼劫持
     */
    public void showNeedHijack(){
        isNeedHijack = true;
    }

    /**
     * 关闭防钓鱼劫持
     */
    public void hideNeedHijack(){
        isNeedHijack = false;
    }

    @Override
    protected void onDestroy() {
        try {
            SpeedTest5g.getInstance().restStarting();
            if (getClass().getName().indexOf("LoadingActivity") == -1) {
                unregisterReceiver(myFinishCast);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        super.onDestroy();
    }

    private boolean isInitWater = true;

    @Override
    protected void onResume() {
        super.onResume();
        MyLoginExitThread.setNewonResume(this);
//        StatService.onResume(this);
        if (isInitWater) {
            isInitWater = false;
            if (getClass().getName().indexOf("LoadingActivity") == -1 &&
                    getClass().getName().indexOf("LoadingOfficialActivity") == -1 &&
                    getClass().getName().indexOf("LoginActivity") == -1 &&
                    getClass().getName().indexOf("AddStationActivity") == -1 &&
                    getClass().getName().indexOf("NearCellListActivity") == -1 &&
                    getClass().getName().indexOf("CarInfoListActivity") == -1) {// 登入页面、弹窗样式的页面不显示水印
                WaterMarkUtil.showWatermarkView(this, getUser());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        StatService.onPause(this);
    }

    /**
     * 初始化...
     * @param titleId 标题名称
     */
    public void init(Object titleId, boolean isBack) {
        init(titleId, isBack, false);
    }

    /**
     * 初始化标题栏
     *
     * @param titleId
     *            标题名称
     * @param isBack
     *            是否显示返回键
     * @param isScroll
     *            标题名称是否滚动显示
     */
    public void init(Object titleId, boolean isBack, boolean isScroll) {
        actionBar = (MyActionBar) findViewById(R.id.actionbar);
        if (actionBar != null) {
            if(mBgParentColor != -1){
                actionBar.setBackgroundColor(getResources().getColor(mBgParentColor));
            }else{
                actionBar.setBackgroundResource(R.drawable.drawable_supter_title_bg);
            }
            actionBar.setIsMenuSpilx(View.GONE);
            if (titleId == null) {
                actionBar.setTitle(R.string.app_name);
            }else if (titleId instanceof String) {
                actionBar.setTitle((String) titleId, isScroll);
            }else {
                actionBar.setTitle((Integer)titleId);
            }
            actionBar.setListenerBackSuper(BaseActionBarActivity.this);
            actionBar.setIsHomeSpilx(View.GONE);
            if(mTitleTextColor != -1){
                actionBar.setTitleColor(getResources().getColor(mTitleTextColor));
            }else{
                actionBar.setTitleColor(getResources().getColor(R.color.white));
            }
            if(mBgTopColor != -1){
                actionBar.setTopColor(mBgTopColor);
            }
            if (isBack) {
                actionBar.setHomeAction(new MenuAction(R.drawable.btn_title_back,EnumRequest.MENU_BACK.toInt(), ""));
            }
        }
        initBarTitle();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public View initBarTitle(){
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //	       getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            View titleView = findViewById(R.id.bar_super_title);
            if (titleView != null) {
                titleView.setVisibility(View.VISIBLE);
            }
            return titleView;
        }
        return null;
    }

    /**
     * 是否可执行的操作,当在子线程或已销毁的UI则不可操作
     * @return
     */
    public boolean isExecute(){
        if (isFinishing()) {
            return false;
        }
        if (Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
            return false;
        }
        return true;
    }

    /**
     * 提示内容
     *
     * @param msg
     */
    public void showCommon(String msg){
        if ("thread interrupted".equals(msg)) {
            return;
        }
        if (isExecute()) {
            MyToast.getInstance(BaseActionBarActivity.this).showCommon(msg,Gravity.CENTER);
        }
    }

    public void showCommonBottom(String msg){
        if (isExecute()) {
            MyToast.getInstance(BaseActionBarActivity.this).showCommon(msg);
        }
    }

    //获取当前登录的用户账号
    public String getUser(){
        return Base64Utils.decryptorDes3(SharedPreHandler.getShared(BaseActionBarActivity.this).getStringShared(TypeKey.getInstance().LOGIN_NAME(), ""));
    }

    public String getUserName(){
        return SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_MZ(), "");
    }

    //获取当前登录的用户ID
    public String getUserID(){
        return SharedPreHandler.getShared(BaseActionBarActivity.this).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
    }

    /**
     * 获取当前登录用户的手机号码
     * @return
     */
    public String getUserPhone(){
        return Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().USER_PHONE(), ""));
    }

    private String appVersionCode;
    public String getVersionCode(){
        if (TextUtils.isEmpty(appVersionCode)) {
            appVersionCode = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().VERSION_CODE, "");
        }
        return appVersionCode;
    }

    /**
     * 获取当前登录用户的手机号码
     * @return
     */
    public String getUserPhoneDes(){
        return SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().USER_PHONE(), "");
    }

    private BroadcastReceiver myFinishCast = new BroadcastReceiver(){

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            finish();
        }

    };

    /**
     * 跳转UI页面
     * @param c 下一个UI类
     * @param extras 传递参数
     * @param isFinish 是否关闭当前UI
     */
    public void goIntent(Class<?> c,Bundle extras,boolean isFinish){
        isNeedHijackToast = false;
        Intent intent = new Intent(this,c);

        if (extras != null) intent.putExtras(extras);

        startActivity(intent);

        if (isFinish) finish();
    }

    /**
     * 根据intent启动新的activity
     * @param intent
     * @param isFinish
     */
    public void goIntent(Intent intent,boolean isFinish){
        isNeedHijackToast = false;

        startActivity(intent);

        if (isFinish) finish();
    }

    /**
     * 跳转下一个UI页面，且有返回数据的
     * @param c 下一个UI类
     * @param extras 传递参数
     * @param requestCode 标识
     */
    public void goIntentRequest(Class<?> c,Bundle extras,int requestCode){
        isNeedHijackToast = false;
        Intent intent = new Intent(this,c);

        if (extras != null) intent.putExtras(extras);

        startActivityForResult(intent, requestCode);
    }

    public void showMyDialog(){
        if (isExecute()) {
            if (ivDialog == null) {
                ivDialog = new LoadingDialog(BaseActionBarActivity.this);
            }else {
                ivDialog.setTextMsg("");
            }
            if (!ivDialog.isShowing()) {
                ivDialog.show();
            }
            ivDialog.setListener(BaseActionBarActivity.this);
        }
    }

    public void setLoadingMsg(String msg){
        if (ivDialog != null && ivDialog.isShowing()) {
            ivDialog.setTextMsg(msg);
        }
    }

    public void dismissMyDialog(){
        if (ivDialog != null && ivDialog.isShowing()) {
            ivDialog.dismiss();
        }
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        if (type == EnumRequest.MENU_BACK.toInt()) {
            isNeedHijackToast = false;
            if (isTrue) {
                BaseActionBarActivity.this.finish();
            }
        }
    }

    public String[] getArrStr(int id){
        return BaseActionBarActivity.this.getResources().getStringArray(id);
    }
}
