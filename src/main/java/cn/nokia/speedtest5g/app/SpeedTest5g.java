package cn.nokia.speedtest5g.app;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.SDKInitializer;
import com.fjmcc.wangyoubao.app.signal.ServiceSignalSim1;
import com.fjmcc.wangyoubao.app.signal.ServiceSignalSim2;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

import cn.nokia.speedtest5g.app.activity.LoadingActivity;
import cn.nokia.speedtest5g.app.cast.SimSignalReceiver;
import cn.nokia.speedtest5g.app.uitl.MyErrorHandler;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.loading.LoadingDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestNetStateChangeReceiver;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;

/**
 * 主Application
 */
public class SpeedTest5g extends Application {

    public static final String ACTION_CALLER_FINISH = "cn.nokia.speedtest5g.caller.finish";

    /**
     * 正在启动
     */
    public static final int STATUS_STARTINT = -2;
    /**
     * 启动失败
     */
    public static final int STATUS_START_FAIL = -1;
    /**
     * 启动成功
     */
    public static final int STATUS_START_SUCCESS = 0;
    /**
     * 未初始化 需调用initApp()
     */
    public static final int STATUS_NOT_INIT = 1;
    /**
     * 参数错误
     */
    public static final int STATUS_PARAMETER_ERROR = 2;
    /**
     * 登陆失败
     */
    public static final int STATUS_LOGIN_FAIL = 3;

    private static Context mContext = null;
    private static SpeedTest5g mSpeedTest5g = new SpeedTest5g();
    private SimSignalReceiver mSimSignalReceiver = null;
    private SpeedTestNetStateChangeReceiver mSpeedTestNetStateChangeReceiver = null;

    //加载对话框
    public LoadingDialog ivDialog = null;
    private boolean mStarting = false; //是否正在启动
    private String TEST = "TEST"; //测试环境
    private String PREPRODUCT = "PREPRODUCT"; //预发布环境
    private String PRODUCT = "PRODUCT"; //正式环境

    public void restStarting(){
        mStarting = false;
    }

    public static SpeedTest5g getInstance(){
        return mSpeedTest5g;
    }

    /**
     *
     * @param context
     * @param runType "TEST" 测试环境 "PRODUCT"正式环境 "PREPRODUCT"预发布环境
     */
    public void initApp(Context context, String runType){
        if(context == null){
            return;
        }
        if(mContext != null){
            return;
        }
        this.mContext = context;

        NetWorkUtilNow.getInstances().clearToIp();
        if(TEST.equals(runType)){
            SpeedTestDataSet.mBindIp = "3";
        }else if(PREPRODUCT.equals(runType)){
            SpeedTestDataSet.mBindIp = "2";
        }else if(PRODUCT.equals(runType)){
            SpeedTestDataSet.mBindIp = "";
        }

        onCreate();
    }

    /**
     * 注销
     */
    public void terminateApp(){
        onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * * 启动类方法
     * @param activity
     * @param appKey  appKey
     * @param secureKey  安全密钥
     * @param outerUserId  UserId
     * @param outerUserName  用户名
     * @param outerUserMobile  手机号
     * @param colorType  WHITE：白色；BLUE：蓝色；BLACK：黑色
     * @param affairType 1:测速；2:wifi信号检测
     * @param isShowLoading  是否显示进度条
     * @return
     */
    public synchronized int startActivity(Activity activity, String appKey, String secureKey, String outerUserId, String outerUserName, String outerUserMobile,
                                          String colorType, int affairType, boolean isShowLoading){
        try{
            if(mStarting){
                return STATUS_STARTINT;
            }
            mStarting = true;

            if(mContext == null){
                mStarting = false;
                return STATUS_NOT_INIT;
            }

            if(activity == null){
                mStarting = false;
                return STATUS_PARAMETER_ERROR;
            }

            Intent intent = new Intent(activity, LoadingActivity.class);
            intent.putExtra("appKey", appKey);
            intent.putExtra("secureKey", secureKey);
            intent.putExtra("outerUserId", outerUserId);
            intent.putExtra("outerUserName", outerUserName);
            intent.putExtra("outerUserMobile", outerUserMobile);
            intent.putExtra("colorType", colorType);
            intent.putExtra("affairType", affairType);
            intent.putExtra("isShowLoading", isShowLoading);
            activity.startActivity(intent);

            return STATUS_START_SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
        }
        return STATUS_START_FAIL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SDKInitializer.setHttpsEnable(true);
            SDKInitializer.initialize(mContext.getApplicationContext());
            initImageLoader(mContext.getApplicationContext());

            //注册异常
            MyErrorHandler errorHandler = MyErrorHandler.getInstance();
            errorHandler.init(mContext);
            Thread.setDefaultUncaughtExceptionHandler(errorHandler);
            initSystemListener();
            SharedPreHandler.getShared(mContext).setIntShared(TypeKey.getInstance().OFF_STATE(), 0);
            registerSiganl();
            registerNetStateChangeReceiver();//注册网络变化接收器
            SpeedTestServerRequestUtil.getInstance().setContext(mContext); //实例化
            WybLog.setDebug(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            //注销
            unregisterSignal();
            unregisterNetStateChangeReceiver();
            unregisterReceiver(mSystemReceiver);
        } catch (Exception e) {
        }
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new ImageLoaderWithCookie(context))
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    //信号，GPS广播
    private void registerSiganl(){
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceSignalSim1.ACTION_SIGNAL_SIM1);
        filter.addAction(ServiceSignalSim2.ACTION_SIGNAL_SIM2);
        filter.addAction(TypeKey.getInstance().PACKAGE_GPS);
        mSimSignalReceiver = new SimSignalReceiver();
        mContext.registerReceiver(mSimSignalReceiver, filter);
    }

    private void registerNetStateChangeReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mSpeedTestNetStateChangeReceiver = new SpeedTestNetStateChangeReceiver();
        mContext.registerReceiver(mSpeedTestNetStateChangeReceiver, filter);
    }

    private void unregisterSignal(){
        if (mSimSignalReceiver == null) {
            return;
        }
        mContext.unregisterReceiver(mSimSignalReceiver);
    }

    private void unregisterNetStateChangeReceiver(){
        if (mSpeedTestNetStateChangeReceiver == null) {
            return;
        }
        mContext.unregisterReceiver(mSpeedTestNetStateChangeReceiver);
    }

    /**
     * 屏幕状态监测
     */
    private void initSystemListener(){
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        mContext.registerReceiver(mSystemReceiver, filter);
    }

    private BroadcastReceiver mSystemReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context,Intent intent) {
            String action = intent.getAction();
            //开
            if(Intent.ACTION_SCREEN_ON.equals(action)){
                SharedPreHandler.getShared(context).setIntShared(TypeKey.getInstance().OFF_STATE(), 0);
                WybLog.sysoD("\n手机屏幕 亮屏");
                //关
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){
                SharedPreHandler.getShared(context).setIntShared(TypeKey.getInstance().OFF_STATE(), 1);
                WybLog.sysoD("\n手机屏幕 暗屏");
            }
        }
    };

    private class ImageLoaderWithCookie extends BaseImageDownloader {

        public ImageLoaderWithCookie(Context context) {
            super(context);
        }

        @Override
        protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
            HttpURLConnection connection = super.createConnection(url, extra);
            if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
                connection.setRequestProperty("sid",NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);//extra就是sid
                connection.setRequestProperty("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
            }
            return connection;
        }
    }
}
