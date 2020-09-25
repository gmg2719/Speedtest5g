package cn.nokia.speedtest5g.speedtest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.MarqueesTextView;
import com.android.volley.util.SharedPreHandler;
import com.fjmcc.wangyoubao.app.signal.SignalServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.LoginActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_AppWarningLog;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.bean.Db_UserEvaluateLog;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.listener.MyLocationListener;
import cn.nokia.speedtest5g.app.request.JJ_RequestAppWarning;
import cn.nokia.speedtest5g.app.request.JJ_RequestFtpTest;
import cn.nokia.speedtest5g.app.respon.ResponseHomeInitData;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.PowerManagerUtil;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.msg.respone.MsgDetail;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanFlowConfig;
import cn.nokia.speedtest5g.speedtest.bean.RequestAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.RequestInitiateSpeedTest;
import cn.nokia.speedtest5g.speedtest.bean.ResponseAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.ResponseInitiateSpeedTest;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSaveLog;
import cn.nokia.speedtest5g.speedtest.pk.SpeedPkGroupListActivity;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDialView;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestEvaluateDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestPackSelectDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestPresentDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestSuggestDialog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.SpeedTestCurveView;
import cn.nokia.speedtest5g.view.WaveButton;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar.Action;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar.MenuAction;

/**
 * 速率测试主类  业务处理
 * @author JQJ
 *
 */
@SuppressLint("NewApi")
@SuppressWarnings("unchecked")
public class SpeedTestMainActivity extends SpeedTestBaseActivity implements OnClickListener{

    public static final String ON_RESUME_UPDATE = "cn.nokia.speedtest5g.speedtestmain.onresume";
    public static final String ON_UPDATE_FTP = "cn.nokia.speedtest5g.speedtestmain.update.ftp";

    private final int HANDLER_QUERY_FTP_UPLOAD_DATA = 2000;
    private final int HANDLER_QUERY_APP_WARNING_UPLOAD_DATA = 2001;
    private final int HANDLER_START_FTP_TEST_1 = 2002; //左右小模块动画
    private final int HANDLER_START_FTP_TEST_2 = 2003; //开始ftp测试
    private final int HANDLER_SHOW_EVALUATE = 2004; //显示满意评价
    private final int HANDLER_FTP_LOAD_OVER = 2005; //ftp加载完成

    private Vibrator mVibrator = null;  //声明一个振动器对象

    // 通知
    private LinearLayout mLlNotifyModule = null;
    private MarqueesTextView mMarqueesTextView = null;
    private List<MsgDetail> msgList = null;
    private long mCurrentNotifyId = -1;
    private boolean mIsShow = false;

    private Action mPkAction = null;
    private Action mDetailsAction = null;
    private Action mShareAction = null; //分享
    private Action mHistoryAction = null; //历史记录

    // 采集点信息上传
    private ArrayList<HashMap<String, Object>> mArrSignalUp = new ArrayList<HashMap<String, Object>>();
    // 采集点信息下载
    private ArrayList<HashMap<String, Object>> mArrSignalDown = new ArrayList<HashMap<String, Object>>();

    private Db_JJ_AppWarningLog mAppWarningInfo;// APP告警数据
    private List<Db_JJ_AppWarningLog> mAppWarningList = null;
    private StringBuffer appTest = null;// FTP测试信息
    private long mCurrentTime = 0;
    // FTP测试结果
    private Db_JJ_FTPTestInfo mFtpTestInfo = null;
    private List<Db_JJ_FTPTestInfo> mUploadFtpTestList = null;// 需要上传的测试结果列表
    private UpdateConfigReceiver mReceiver = new UpdateConfigReceiver();
    private OnResumeReceiver mOnResumeReceiver = new OnResumeReceiver();
    private OnUpdateFtpReceiver mOnUpdateFtpReceiver = new OnUpdateFtpReceiver();
    private boolean mIsAbnormal = false;
    private int mPackageType = PACAKGE_SIZE_LARGE; //默认推荐大包
    private String mNetType = ""; //网络制式
    private String mOperator = "";//运营商
    private String mProvince = "";//省
    private String mNowCity = "";//地

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_speed_test_main_activity);
        mBgTopColor = R.color.bm_bg_color;
        mBgParentColor = R.color.bm_bg_color;
        mTitleTextColor = R.color.black_small;

        initOther();

        init("速率测试", false);
        initHandler();

        mProvince = SharedPreHandler.getShared(mActivity).getStringShared("Province", "");
        mNowCity = SharedPreHandler.getShared(mActivity).getStringShared("City", "");

        setTimeOut(mAllTestTimeForLarge); //默认大包
        setUpdateTime(mUpdateTimeForLarge);

        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        registerReceiver(mReceiver, new IntentFilter(TypeKey.getInstance().ACTION_MAINHOME_SUPER));
        registerReceiver(mOnResumeReceiver, new IntentFilter(ON_RESUME_UPDATE));
        registerReceiver(mOnUpdateFtpReceiver, new IntentFilter(ON_UPDATE_FTP));
        registerReceiver(mNetworkChangeReceiver, new IntentFilter(SpeedTestUtil.NETWORK_CHANGE_ACTION));
        if(!SpeedTestUtil.getInstance().isLocServiceEnable(mActivity)){
            openLocService();
            requestAppFtpConfig();
        }
    }

    private void initOther(){
        MyLocationListener.getInstances().onStarts();
        // 这里启动信号采集服务
        SignalServiceUtil.getInstances().startService(this);
        //开启电源锁
        PowerManagerUtil.acquireWakeLock(SpeedTestMainActivity.this);
        //防止未请求到数据
//        SpeedTestServerRequestUtil.getInstance().requestServerData();
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);
        mFtpTestType = getIntent().getIntExtra("data", 1);

        mPkAction = new MenuAction(R.drawable.icon_speed_test_pk_entry, EnumRequest.MENU_SELECT_ONE.toInt(), "");
        mDetailsAction = new MenuAction(R.drawable.icon_speed_test_details_flag, EnumRequest.MENU_SELECT_TWO.toInt(), "");
        mShareAction = new MenuAction(R.drawable.icon_speed_test_share_flag, EnumRequest.MENU_SELECT_THREE.toInt(), "");
        mHistoryAction = new MenuAction(R.drawable.icon_speed_test_history_flag, EnumRequest.MENU_SELECT_FOUR.toInt(), "");

        mLlApModule = (LinearLayout)findViewById(R.id.speed_test_main_ll_ap_content);
        mLlDlModule = (FrameLayout)findViewById(R.id.speed_test_main_fl_download);
        mLlUlModule = (FrameLayout)findViewById(R.id.speed_test_main_fl_upload);

        mIvRight = (ImageView)findViewById(R.id.speed_test_main_iv_right);
        mPbRight = (ProgressBar)findViewById(R.id.speed_test_main_pb_right);

        //详情模块
        mLlModule_Details = findViewById(R.id.speed_test_main_details_ll_details);
        mWbReStart = (WaveButton)findViewById(R.id.speed_test_main_details_wb_restart);
        mTvDetailsAvgDownload = (TextView) findViewById(R.id.speed_test_main_details_tv_avg_download);
        mTvDetailsAvgUpload = (TextView) findViewById(R.id.speed_test_main_details_tv_avg_upload);
        mLlDetailsDlModule = (FrameLayout)findViewById(R.id.speed_test_main_details_fl_download);
        mLlDetailsUlModule = (FrameLayout)findViewById(R.id.speed_test_main_details_fl_upload);
        mTvGotoSignalTest = (TextView) findViewById(R.id.speed_test_main_details_tv_goto_signaltest);

        mTvDetailsPing = (TextView) findViewById(R.id.speed_test_main_details_tv_ping);
        mTvDetailsShake = (TextView) findViewById(R.id.speed_test_main_details_tv_shake);
        mTvDetailsPacketLoss = (TextView) findViewById(R.id.speed_test_main_details_tv_packetloss);

        mTvTitleLeft = (TextView)findViewById(R.id.speed_test_main_tv_left_title);
        mTvTitleLeft1 = (TextView)findViewById(R.id.speed_test_main_tv_left_title1);
        mTvTitleLeft2 = (TextView)findViewById(R.id.speed_test_main_tv_left_title2);
        mTvTitleRight1 = (TextView)findViewById(R.id.speed_test_main_tv_right_title1);
        mTvTitleRight2 = (TextView)findViewById(R.id.speed_test_main_tv_right_title2);

        mLlBaseInfo = (LinearLayout)findViewById(R.id.speed_test_main_ll_baseinfo);
        mLlBottomModule = (LinearLayout)findViewById(R.id.speed_test_main_ll_module_bottom);
        mLlFlow = (LinearLayout)findViewById(R.id.speed_test_main_ll_module_flow);
        mLlLogo = (LinearLayout)findViewById(R.id.speed_test_main_ll_logo);
        mLlViewNotNr = (View)findViewById(R.id.speed_test_main_ll_view_not_nr);
        mLlViewNr = (View)findViewById(R.id.speed_test_main_ll_view_nr);

        mTvNet = (TextView)findViewById(R.id.speed_test_main_tv_net);
        mTvCellId = (TextView)findViewById(R.id.speed_test_main_tv_cellid);
        mTvRsrpOrLac = (TextView)findViewById(R.id.speed_test_main_tv_rsrp_or_lac);
        mTvSinrOrRxl = (TextView)findViewById(R.id.speed_test_main_tv_sinr_or_rxlev);
        mTvNet4g = (TextView)findViewById(R.id.speed_test_main_tv_4g);
        mTvNrEarfcn = (TextView)findViewById(R.id.speed_test_main_tv_nr_earfcn);
        mTvNrRsrp = (TextView)findViewById(R.id.speed_test_main_tv_nr_rsrp);
        mTvNrSinr = (TextView)findViewById(R.id.speed_test_main_tv_nr_sinr);
        mTvTip = (TextView)findViewById(R.id.speed_test_main_tv_all_flow);
        mTvWlRank = (TextView)findViewById(R.id.speed_test_main_details_tv_value1); //宽带排行
        mTvRank = (TextView)findViewById(R.id.speed_test_main_details_tv_value2);
        mLlSignal = (LinearLayout)findViewById(R.id.speed_test_main_ll_signal);
        mTvSignalValue = (TextView)findViewById(R.id.speed_test_main_details_tv_signal_value);

        mTvPing = (TextView) findViewById(R.id.speed_test_main_tv_ping);
        mTvShake = (TextView) findViewById(R.id.speed_test_main_tv_shake);
        mTvPacketLoss = (TextView) findViewById(R.id.speed_test_main_tv_packetloss);

        mDownloadCharView = new SpeedTestCurveView(mActivity);
        mLlDlModule.addView(mDownloadCharView);// 初始化下载曲线图
        mUploadCharView = new SpeedTestCurveView(mActivity);
        mLlUlModule.addView(mUploadCharView);// 初始化上传曲线图
        mDownloadCharView.setEnabled(false);
        mUploadCharView.setEnabled(false);

        mDetailsDownloadCharView = new SpeedTestCurveView(mActivity);
        mLlDetailsDlModule.addView(mDetailsDownloadCharView);//详情页  初始化下载曲线图
        mDetailsUploadCharView = new SpeedTestCurveView(mActivity);
        mLlDetailsUlModule.addView(mDetailsUploadCharView);//详情页 初始化上传曲线图
        mDetailsDownloadCharView.setEnabled(false);
        mDetailsUploadCharView.setEnabled(false);

        mSpeedTestDialView = (SpeedTestDialView) findViewById(R.id.speed_test_main_dial_view);
        mTvDownOrUpIcon = (TextView) findViewById(R.id.speed_test_main_tv_down_or_up_icon);
        mTvSpeedTestMsg = (TextView) findViewById(R.id.speed_test_main_tv_msg);
        mTvAvgDownload = (TextView) findViewById(R.id.speed_test_main_tv_avg_download);
        mTvAvgUpload = (TextView) findViewById(R.id.speed_test_main_tv_avg_upload);
        mWbStart = (WaveButton)findViewById(R.id.speed_test_main_wb_start);
        mLlModule_Testing = findViewById(R.id.speed_test_main_ll_module_testing);
        mLlModule_Dashboard = findViewById(R.id.speed_test_main_ll_module_dashboard);
        mLlModule_Curve = findViewById(R.id.speed_test_main_ll_module_curve);
        mLlModule_left = findViewById(R.id.speed_test_main_ll_module_left);
        mLlModule_right = findViewById(R.id.speed_test_main_ll_module_right);
        mLlModule_right.setEnabled(true);
        mLlFlow.setEnabled(true);

        findViewById(R.id.speed_test_main_ll_ping).setOnClickListener(this);
        findViewById(R.id.speed_test_main_ll_shake).setOnClickListener(this);
        findViewById(R.id.speed_test_main_ll_packetloss).setOnClickListener(this);
        findViewById(R.id.speed_test_main_details_ll_ping).setOnClickListener(this);
        findViewById(R.id.speed_test_main_details_ll_shake).setOnClickListener(this);
        findViewById(R.id.speed_test_main_details_ll_packetloss).setOnClickListener(this);

        //开始按钮居中处理
        int screenHeight = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().HEIGHT(), 0);
        int dpValue = UtilHandler.getInstance().pxTodp(screenHeight);
        LayoutParams params = (LayoutParams)mWbStart.getLayoutParams();
        params.topMargin = dpValue/2 - 350/2;
        mWbStart.setLayoutParams(params);

        mWbStart.setType(1);
        mWbReStart.setType(2);

        //		mLlFlow.setOnClickListener(this);
        mWbStart.setOnClickListener(this);
        mWbReStart.setOnClickListener(this);
        mLlModule_left.setOnClickListener(this);
        mLlModule_right.setOnClickListener(this);
        mTvGotoSignalTest.setOnClickListener(this);

        findViewById(R.id.speed_test_main_ll_avg_download).setOnClickListener(this);
        findViewById(R.id.speed_test_main_ll_avg_upload).setOnClickListener(this);
        findViewById(R.id.speed_test_main_details_ll_avg_download).setOnClickListener(this);
        findViewById(R.id.speed_test_main_details_ll_avg_upload).setOnClickListener(this);

        // 未读消息
        mLlNotifyModule   = (LinearLayout)findViewById(R.id.speed_test_main_ll_notice);
        mLlNotifyModule.setOnClickListener(this);
        mMarqueesTextView = (MarqueesTextView)findViewById(R.id.speed_test_main_mtv_notice);
        findViewById(R.id.speed_test_main_iv_close).setOnClickListener(this);

        updateWaveView();

        initUi();
    }

    private void initUi(){
        if(NetInfoUtil.isNetworkConnected(mActivity)){ //有网络
            SpeedTestServerRequestUtil.getInstance().requestServerData();
            mTvTip.setText("移动网络下测速会消耗相应的流量，流量消耗情况请到测试详情中查询！");
            mLlBottomModule.setVisibility(View.VISIBLE);
            mTvTip.setTextColor(Color.parseColor("#f98c5e"));
            updateUi(STATUS_INIT);
        }else{ //无网络
            String loginType = SharedPreHandler.getShared(mActivity).getStringShared("QgLoginType", "");
            if(TextUtils.isEmpty(loginType)){ //未登录
                mTvTip.setText("无法连接到互联网，请确保网络连接后，重新启动App！");
            }else{
                mTvTip.setText("无法连接到互联网，请检查网络连接！");
            }
            mTvTip.setTextColor(Color.parseColor("#f49524"));
            mLlBottomModule.setVisibility(View.INVISIBLE);
            updateUi(STATUS_INIT);
        }
    }

    /**
     * 获取ftp配置
     */
    private boolean mIsFrist = true;
    private long mTime = System.currentTimeMillis();
    private void requestAppFtpConfig(){
        try{
            if(!NetInfoUtil.isNetworkConnected(mActivity)){ //没网络返回
                return;
            }
            if(TextUtils.isEmpty(mProvince) && TextUtils.isEmpty(mNowCity)){ //未获取到地市
                if(System.currentTimeMillis() - mTime >= 30000){ //30S超时
                    mPbRight.setVisibility(View.GONE);
                    mIvRight.setVisibility(View.VISIBLE);
                    mTvTitleRight1.setText("定位获取");
                    mTvTitleRight2.setText("失败");
                    mTime = System.currentTimeMillis();
                    return;
                }
                return;
            }
            mIsFrist = false;
            mPbRight.setVisibility(View.VISIBLE);
            mIvRight.setVisibility(View.GONE);
            mTvTitleRight1.setText("正在查找");
            mTvTitleRight2.setText("测速点");
            RequestAppFtpConfig request = new RequestAppFtpConfig();
            request.operator=(mOperator);
            request.province=(mProvince);
            request.city=(mNowCity);
            NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
                    NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_APP_FTP_CONFIG),
                    JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

                        @Override
                        public void onListener(int type, Object object, boolean isTrue) {
                            ResponseAppFtpConfig response = JsonHandler.getHandler().getTarget(object.toString(),
                                    ResponseAppFtpConfig.class);
                            if(response != null && response.isRs()){
                                ArrayList<BeanAppFtpConfig> list = response.datas;
                                for(BeanAppFtpConfig config : list){
                                    config.serverType = true; //设置类型
                                }
                                //获取用户手动选择的ip
                                String ID = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("SPEED_TEST_HOST_ID", "");
                                if(TextUtils.isEmpty(ID)){
                                    //设置默认第一条选中
                                    if(list != null && list.size() > 0){
                                        BeanAppFtpConfig config = list.get(0);
                                        SpeedTestDataSet.mBeanAppFtpConfig = config;
                                    }
                                }else{
                                    if(list != null && list.size() > 0){
                                        for(BeanAppFtpConfig config : list){
                                            if(ID.equals(config.iD)){
                                                SpeedTestDataSet.mBeanAppFtpConfig = config;
                                                break;
                                            }
                                        }
                                        //没匹配到, 设置默认第一条选中
                                        if(SpeedTestDataSet.mBeanAppFtpConfig == null){
                                            BeanAppFtpConfig config = list.get(0);
                                            SpeedTestDataSet.mBeanAppFtpConfig = config;
                                        }
                                    }
                                }
                                SpeedTestDataSet.mResponseAppFtpConfig = response;
                                mChildHandler.sendEmptyMessageDelayed(HANDLER_FTP_LOAD_OVER, 1000);
                            }else{
                                mIsFrist = true; //获取失败
                                mPbRight.setVisibility(View.GONE);
                                mIvRight.setVisibility(View.VISIBLE);
                                mTvTitleRight1.setText("查找测速点");
                                mTvTitleRight2.setText("失败");
                            }
                        }
                    });
        }catch(Exception e){
            WybLog.i("错误：" + e.getMessage());
        }
    }

    /**
     * 开始测试
     */
    private void startSpeedTest() {
        if(!SpeedTestUtil.getInstance().isNetwork(mActivity)){
            return;
        }

        BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
        switch (mFtpTestType) {
            case 1:
                if (beanAppFtpConfig == null) { //判断是否有服务器配置
                    return;
                }
                break;
            default:
                break;
        }

        updateUi(STATUS_CONNECT);
        mVibrator.vibrate(new long[]{0, 100}, -1);
        clearSpeedTestData();

        mTvAvgDownload.setText("——");
        mTvAvgUpload.setText("——");
        mTvDetailsAvgDownload.setText("——");
        mTvDetailsAvgUpload.setText("——");

        mIsAbnormal = false;
        mShareCode = "";
        isStartDownloadFlag = false;//重置
        isStartUploadFlag = false;

        mTvSpeedTestMsg.setText("");

        //自定义服务器 只ping下载的
        startPing();

        if(beanAppFtpConfig.serverType){
            initiateSpeedTest();
        }else{//自定义服务器 不调用
            //判断参数是否合法
            if(!judgment()){
                return;
            }
            //			showApnSettingDialog(false);
            //进入测试
            mChildHandler.sendEmptyMessageDelayed(HANDLER_START_FTP_TEST_1, 1000);
        }
    }

    /**
     * 开始ping
     */
    private void startPing(){
        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(500);

                BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
                if(beanAppFtpConfig != null){
                    mCurrentPingCount++;
                    mPingInfo = new PingInfo();
                    if(beanAppFtpConfig.serverType){
                        mPingInfo.setIp(beanAppFtpConfig.ip);
                    }else{
                        mPingInfo.setIp(beanAppFtpConfig.customDownIp);
                    }

                    PingThread pingThread  = new PingThread(mPingInfo, 0, mChildHandler, 4, 5);//发包4次 超时5S
                    pingThread.start();
                }
            };
        }.start();
    }

    /**
     * app发起速率测试
     */
    private void initiateSpeedTest(){
        try{
            BeanAppFtpConfig config = getSelectServer();
            RequestInitiateSpeedTest request = new RequestInitiateSpeedTest();
            request.apnftpId = config.iD;
            request.userId = getUserID();
            request.operator = config.operator;
            request.province = config.province;
            request.city = config.city;
            request.ip = config.ip;
            request.port = config.port;
            request.ftpType = config.ftpType;
            NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
                    NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_INITIATE_SPEED_TEST), JsonHandler.getHandler().toJson(request),
                    -1, new ListenerBack() {

                        @Override
                        public void onListener(int type, Object object, boolean isTrue) {
                            try{
                                ResponseInitiateSpeedTest response = JsonHandler.getHandler().getTarget(object.toString(), ResponseInitiateSpeedTest.class);
                                if (response != null) {
                                    if (response.isRs()) {
                                        mLogId = response.datas.logId;

                                        mChildHandler.sendEmptyMessageDelayed(HANDLER_START_FTP_TEST_1, 1000);
                                    } else {
                                        showCommon(response.getMsg());
                                    }
                                } else {
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始测试FTP 延迟1000执行
     */
    private void startFtpTest() {
        updateUi(STATUS_TESTING);
        mSpeedTestDialView.setCurrentStatus(0);
        stopAnimatorModule_4();
        initializationFtp();
    }

    /**
     * 停止测试
     */
    private void stopSpeedTest() {
        updateUi(STATUS_INIT);
        stopAnimatorModule_4();
        mTvSpeedTestMsg.setText("");

        closeFtp(true);
    }

    /**
     * 根据不同状态 更新Ui
     */
    private void updateUi(int status){
        mStatus = status;
        if(mStatus == STATUS_INIT){
            clearSpeedTestData();
            actionBar.clearHomeAction();
            actionBar.removeAllActions();
            actionBar.addAction(mHistoryAction);
            mWbStart.setText("开始");
            mWbStart.setVisibility(View.VISIBLE);
            mLlFlow.setVisibility(View.VISIBLE);
            mLlModule_Testing.setVisibility(View.INVISIBLE);
            mLlModule_Details.setVisibility(View.GONE);
            mLlFlow.setEnabled(true);
            mLlModule_right.setEnabled(true);
            animatorModule_1(false);
            animatorModule_2(false);
            animatorModule_3(false);
            mLlApModule.removeAllViews();
        }else if(mStatus == STATUS_CONNECT){
            actionBar.removeAllActions();
            actionBar.setHomeAction(new MenuAction(R.drawable.icon_speed_test_close_flag, EnumRequest.MENU_BACK.toInt(), ""));
            mWbStart.setText("正在连接");
            mWbStart.setVisibility(View.VISIBLE);
            mLlFlow.setVisibility(View.VISIBLE);
            mLlModule_Testing.setVisibility(View.INVISIBLE);
            mLlModule_Details.setVisibility(View.GONE);
            mLlFlow.setEnabled(false);
            mLlModule_right.setEnabled(false);
        }else if(mStatus == STATUS_TESTING){
            mWbStart.setVisibility(View.GONE);
            mLlFlow.setVisibility(View.GONE);
            mLlModule_Testing.setVisibility(View.VISIBLE);
            mLlModule_Details.setVisibility(View.GONE);
            mLlFlow.setEnabled(false);
            mLlModule_right.setEnabled(false);
            animatorModule_1(true);
            animatorModule_2(true);
        }else if(mStatus == STATUS_DETAILS){
            actionBar.removeAllActions();
            actionBar.addAction(mHistoryAction);
            actionBar.addAction(mShareAction);
            actionBar.addAction(mDetailsAction);
            mWbStart.setVisibility(View.GONE);
            mLlFlow.setVisibility(View.GONE);
            mLlModule_Testing.setVisibility(View.GONE);
            mLlModule_Details.setVisibility(View.VISIBLE);
            mLlFlow.setEnabled(true);
            mLlModule_right.setEnabled(true);
            animatorModule_3(false);
        }

        refreshSignalModule();
        refreshLogoModule();
    }

    /**
     * handler处理
     */
    private void initHandler(){
        mChildHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                dismissMyDialog();
                if (msg.what == HANDLER_QUERY_FTP_UPLOAD_DATA) {// 上传测试
                    if (NetInfoUtil.isNetworkConnected(mActivity)) {
                        if (mUploadFtpTestList != null && mUploadFtpTestList.size() > 0) {
                            JJ_RequestFtpTest request = new JJ_RequestFtpTest();
                            request.setDatas(mUploadFtpTestList);
                            NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
                                    NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SAVE_SPEED_TEST_LOG),
                                    JsonHandler.getHandler().toJson(request), EnumRequest.NET_SAVE_FTP_SPEED_TEST_LOG.toInt(),
                                    SpeedTestMainActivity.this);
                        }
                    }
                } else if (msg.what == HANDLER_QUERY_APP_WARNING_UPLOAD_DATA) {
                    JJ_RequestAppWarning request = new JJ_RequestAppWarning();
                    request.setDatas(mAppWarningList);
                    NetWorkUtilNow.getInstances().readNetworkPostJsonObjectNoCancel(
                            NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SAVE_APP_WARNING_LOG),
                            JsonHandler.getHandler().toJson(request), EnumRequest.NET_SAVE_APP_WARNING_LOG.toInt(),
                            SpeedTestMainActivity.this);
                } else if(msg.what == HANDLER_START_FTP_TEST_1){
                    animatorModule_3(true); //启动动画
                    mChildHandler.sendEmptyMessageDelayed(HANDLER_START_FTP_TEST_2, 2000);
                } else if(msg.what == HANDLER_START_FTP_TEST_2){
                    if(mStatus == STATUS_CONNECT){
                        startFtpTest();
                    }
                }else if(msg.what == HANDLER_SHOW_EVALUATE){
                    new SpeedTestEvaluateDialog(mContext).setValue(mCurrentAvgDownload, mCurrentAvgUpload, mRankValue).show();
                }else if(msg.what == HANDLER_FTP_LOAD_OVER){
                    mPbRight.setVisibility(View.GONE);
                    mIvRight.setVisibility(View.VISIBLE);
                    loadFtpData();
                }else if(msg.what == EnumRequest.TASK_PING.toInt()){
                    mPingInfo = (PingInfo)msg.obj;

                    if(mPingInfo == null && mCurrentPingCount < mPingCount){
                        startPing();
                        return true;
                    }

                    //ping失败  或值为0 则重试
                    if(!mPingInfo.isState() && mCurrentPingCount < mPingCount){
                        startPing();
                        return true;
                    }

                    if(mPingInfo.getTimes() == -2 && mPingInfo.shake == 0 &&
                            mCurrentPingCount < mPingCount){
                        startPing();
                        return true;
                    }

                    mTvPing.setText(String.valueOf(mPingInfo.getTimes()));
                    mTvDetailsPing.setText(String.valueOf(mPingInfo.getTimes()));
                    mTvShake.setText(String.valueOf(mPingInfo.shake));
                    mTvDetailsShake.setText(String.valueOf(mPingInfo.shake));
                }
                return true;
            }
        });
    }

    /**
     * 加载显示ftp配置  默认显示第一条
     */
    protected void loadFtpData(){
        updateFlowConfig();
        BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
        if(beanAppFtpConfig != null){
            if(beanAppFtpConfig.serverType){
                mTvTitleRight1.setText(beanAppFtpConfig.hostType + ">>");
//				mTvTitleRight2.setText(beanAppFtpConfig.province + "-" + beanAppFtpConfig.city);
                mTvTitleRight2.setText(SpeedTestUtil.getInstance().getLevelByPerformance(beanAppFtpConfig.performance));
            }else{
                mTvTitleRight1.setText("自定义服务器");
                String ip = null; //ip根据测试勾选项获取
                String ftpType = beanAppFtpConfig.ftpType;
                if(ftpType.equals("ALL")){
                    if(!TextUtils.isEmpty(beanAppFtpConfig.customDownIp)){
                        ip = beanAppFtpConfig.customDownIp;
                    }else if(!TextUtils.isEmpty(beanAppFtpConfig.customUpIp)){
                        ip = beanAppFtpConfig.customUpIp;
                    }
                }else if(ftpType.equals("DOWNLOAD")){
                    ip = beanAppFtpConfig.customDownIp;
                }else if(ftpType.equals("UPLOAD")){
                    ip = beanAppFtpConfig.customUpIp;
                }

                if(!TextUtils.isEmpty(ip)){
                    mTvTitleRight2.setText(UtilHandler.getInstance().ipFormat(ip));
                }
            }
        }
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        if (type == EnumRequest.MENU_SELECT_ONE.toInt()){ //PK入口
            //游客不可进入
            String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
            if("Tourist".equals(loginType)){
                //提示登录
                new CommonDialog(SpeedTestMainActivity.this).setListener(SpeedTestMainActivity.this)
                        .setButtonText("立即登录", "稍后再说").show("测速PK为同时间，同位置的多人测速服务。登录5G网速测试，发现更多精彩！",
                        EnumRequest.DIALOG_TOAST_BTN_THREE.toInt());
                return;
            }
            BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
            if (beanAppFtpConfig == null) {
                return;
            }

            //pk不支持apn
            //			if(!beanAppFtpConfig.serverType){
            //				showApnSettingDialog(true);
            //			}

            startSpeekPkGroupListActivity();
        } else if(type == EnumRequest.MENU_SELECT_TWO.toInt()){ //跳转至详情
            Bundle bundle = new Bundle();
            bundle.putSerializable("fromWhere", false);
            bundle.putSerializable("mFtpTestInfo",  mFtpTestInfo);
            goIntent(SpeedTestDetailActivity.class, bundle, false);
        } else if(type == EnumRequest.MENU_SELECT_THREE.toInt()){ //分享
            if(SpeedTestDataSet.mPersonalCenterMap == null){
                showCommon("缺失配置");
                return;
            }

            if(TextUtils.isEmpty(mShareCode)){
                showCommon("无分享码");
                return;
            }

            String url = SpeedTestDataSet.mPersonalCenterMap.get("4703");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
            intent.putExtra(Intent.EXTRA_TEXT, url + mShareCode);
            goIntent(Intent.createChooser(intent, "分享测试详情"), false);
        }else if (type == EnumRequest.NET_SAVE_FTP_SPEED_TEST_LOG.toInt()) {// 速率测试上传结果
            dismissMyDialog();
            if (isTrue) {
                ResponseSaveLog response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSaveLog.class);
                if (response != null) {
                    if (response.isRs()) {
                        mShareCode = response.shareCode;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<Db_JJ_FTPTestInfo> list = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance()
                                        .queryObj(Db_JJ_FTPTestInfo.class, "isUpload =? and userid=?", new Object[] {
                                                false, UtilHandler.getInstance().toInt(getUserID(), 0) });
                                if (list != null && list.size() > 0) {
                                    for (Db_JJ_FTPTestInfo info : list) {
                                        info.setUpload(true);
                                        DbHandler.getInstance().updateObj(info);
                                    }
                                }
                            }
                        }).start();
                    } else {
                        showCommon(response.getMsg());
                    }
                } else {
                    showCommon(object.toString());
                }
            }
        }else if(type == -10000){ //更新大小包配置
            updateFlowConfig();
        }else if(type == EnumRequest.MENU_BACK.toInt()){ //关闭测试
            stopSpeedTest();
        }else if(type == EnumRequest.DIALOG_TOAST_BTN_THREE.toInt()){
            if(isTrue){
                goIntent(LoginActivity.class, null, false);
            }
        }else if(type == EnumRequest.MENU_SELECT_FOUR.toInt()){
            if(isTrue){
                goIntent(SpeedTestHistoryActivity.class, null, false);
            }
        }
    }

    /**
     * Apn设置提示对话框
     */
    //	private void showApnSettingDialog(boolean isGotoSpeedPk) {
    //		if (!isExecute()) {
    //			return;
    //		}
    //		if (isGotoSpeedPk) {
    //			new MyToastDialog(SpeedTestMainActivity.this).setButtom("前往设置", "取消").setListener(new ListenerBack() {
    //				@Override
    //				public void onListener(int type, Object object, boolean isTrue) {
    //					if (isTrue) {
    //						goIntent(new Intent(SpeedTestMainActivity.this, SpeedTestSetActivity.class), false);
    //					}
    //				}
    //			}).show(getString(R.string.speed_pk_apn_hint));
    //		} else {
    //			//判断环境是否满足apn
    //			if (mNetType.equalsIgnoreCase("WiFi") || mNetType.equals("无") || mNetType.equals("")) {
    //				new MyToastDialog(SpeedTestMainActivity.this).setButtom("前往设置", "取消").setListener(new ListenerBack() {
    //					@Override
    //					public void onListener(int type, Object object, boolean isTrue) {
    //						if (isTrue) {
    //							try {
    //								startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
    //							} catch (Exception e) {
    //								startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    //							}
    //						}
    //					}
    //				}).show("进行APN FTP测试前：请关闭wifi，并设置好APN");
    //			}else{
    //				//进入测试
    //				mChildHandler.sendEmptyMessageDelayed(HANDLER_START_FTP_TEST_1, 1000);
    //			}
    //		}
    //	}

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.speed_test_main_wb_start) { //开始测试
            if (mStatus == STATUS_CONNECT) {
                return;
            }
            startSpeedTest();
        } else if (id == R.id.speed_test_main_details_wb_restart) {//重测
            startSpeedTest();
        } else if (id == R.id.speed_test_main_ll_module_flow) { //流量设置
            HashMap<String, String> hashMap = SpeedTestDataSet.mPersonalCenterMap;
            new SpeedTestPackSelectDialog(mContext).setListener(this).show(hashMap);
        } else if (id == R.id.speed_test_main_ll_module_left) { //ip查询
            //			startActivity(new Intent(this, WiFiTestActivity.class));
        } else if (id == R.id.speed_test_main_iv_close) { //关闭通知
            mIsShow = false;
            mLlNotifyModule.setVisibility(View.GONE);
        } else if (id == R.id.speed_test_main_ll_module_right) {
            if (SpeedTestDataSet.mResponseAppFtpConfig == null) {
                showCommon("缺少测速点");
                return;
            }
            goIntent(new Intent(SpeedTestMainActivity.this, SpeedTestSetActivity.class), false);
        } else if (id == R.id.speed_test_main_details_tv_goto_signaltest) { //测信号
            showCommon("功能后续开放");
        } else if (id == R.id.speed_test_main_ll_avg_download || id == R.id.speed_test_main_details_ll_avg_download) {
            if (mCurrentAvgDownload != 0) {
                SpeedTestPresentDialog dialog = new SpeedTestPresentDialog(mActivity);
                dialog.setCancelable(false);
                dialog.show(mCurrentAvgDownload, mNetType);
            }
        } else if (id == R.id.speed_test_main_ll_avg_upload || id == R.id.speed_test_main_details_ll_avg_upload) {
            new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_UPLOAD);
        } else if (id == R.id.speed_test_main_ll_ping || id == R.id.speed_test_main_details_ll_ping) {
            new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_PING);
        } else if (id == R.id.speed_test_main_ll_shake || id == R.id.speed_test_main_details_ll_shake) {
            new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_SHAKE);
        } else if (id == R.id.speed_test_main_ll_packetloss || id == R.id.speed_test_main_details_ll_packetloss) {
            new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_PAKETLOSS);
        }
    }

    private void startSpeekPkGroupListActivity() {
        Bundle bundle = new Bundle();
        String defaultGroupName = "";
        if (mLastSignal != null) {
            if (mLastSignal.getTypeNet().equals("NR") || mLastSignal.getTypeNet().equals("LTE")) {
                defaultGroupName = mLastSignal.getLte_enb();
            } else {
                defaultGroupName = mLastSignal.getGsm_cid();
            }
        }
        bundle.putString("defaultGroupName", defaultGroupName);
        goIntent(SpeedPkGroupListActivity.class, bundle , false);
    }

    /**
     * 网络变化接收
     */
    private BroadcastReceiver mNetworkChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SpeedTestUtil.NETWORK_CHANGE_ACTION)){
//                if(mStatus == STATUS_TESTING){ //测试中 不做处理
//                    return;
//                }
                initUi();
            }
        }
    };

    /**
     * 更新ftp配置
     * @author JQJ
     *
     */
    private class OnUpdateFtpReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ON_UPDATE_FTP)){
                loadFtpData();
            }
        }
    }

    /**
     * 更新广播   onResume  高版本系统  在tabActivit框架中不会调用
     * @author JQJ
     *
     */
    private class OnResumeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if(intent.getAction().equals(ON_RESUME_UPDATE)){
                SpeedTestServerRequestUtil.getInstance().requestNotice();
                showNotice();
            }
        }
    }

    /**
     * 显示测速结束界面
     */
    private void showDeatails(){
        updateUi(STATUS_DETAILS);

        //5G环境下 不显示信号指标
        if(mLastSignal != null && mLastSignal.getTypeNet().equals("NR")){
            mLlSignal.setVisibility(View.GONE);
        }else{
            mLlSignal.setVisibility(View.VISIBLE);
        }

        //wifi环境下 不弹窗
        if(mNetType.equalsIgnoreCase("WiFi")){
            return;
        }

        //评价弹窗  (一个用户一天限制弹窗一次)
        new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    long time = System.currentTimeMillis();
                    boolean isShow = false; //默认不弹窗
                    ArrayList<Db_UserEvaluateLog> list = (ArrayList<Db_UserEvaluateLog>)DbHandler.getInstance().queryObj(Db_UserEvaluateLog.class, "userId=?",
                            new Object[]{getUserID()});
                    if(list == null || (list != null && list.size() == 0)){ //无用户
                        isShow = true;

                        Db_UserEvaluateLog log = new Db_UserEvaluateLog();
                        log.userId = getUserID();
                        log.time = time;
                        log.isHaveShow = true;
                        DbHandler.getInstance().save(log);
                    }else{ //有用户
                        Db_UserEvaluateLog log = list.get(0);
                        if(SpeedTestUtil.getInstance().isSameDay(time, log.time)){ //同一天
                            if(!log.isHaveShow){
                                isShow = true;
                            }
                        }else{
                            isShow = true;
                        }
                        if(isShow){
                            log.time = time;
                            log.isHaveShow = true;
                            DbHandler.getInstance().updateObj(log);
                        }
                    }

                    if(isShow){
                        Message msg = mChildHandler.obtainMessage();
                        msg.what = HANDLER_SHOW_EVALUATE;
                        msg.obj = time;
                        mChildHandler.sendMessage(msg);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void showNotice(){
        ResponseHomeInitData responseHomeInitData = SpeedTestDataSet.mResponseHomeInitData;
        if(responseHomeInitData != null && responseHomeInitData.datas != null){
            msgList = responseHomeInitData.datas.msgList;
            if(msgList != null && msgList.size() > 0){
                MsgDetail msgDetail = msgList.get(0);
                if(mCurrentNotifyId != msgDetail.id){
                    mMarqueesTextView.setText(msgDetail.title);
                    mCurrentNotifyId = msgDetail.id;
                    mIsShow = true;
                }
            }else{
                mIsShow = false;
            }
        }else{
            mIsShow = false;
        }
        updateMsgLayout();
    }

    @Override
    public void onSignalReceiver(Signal signal) {
        if (!isFallback()) {

            boolean isOperatorChange = false; //是否更新预估
            boolean isNetTypeChange = false; //是否更新预估
            long newTime = System.currentTimeMillis();
            if(newTime - mCurrentTime > 3000){
                //获取运营商
                String isp = SystemUtil.getInstance().getISPNew(SpeedTest5g.getContext());
                mTvTitleLeft1.setText(TextUtils.isEmpty(isp)?"——":isp);
                String temp = isp;
                if (isp.contains("中国")) {
                    temp = isp.replace("中国", "");
                }
                if(!mOperator.equals(temp)){
                    isOperatorChange = true;
                    mOperator = temp;
                }

                String[] netArr = NetInfoUtil.getOperatorNetworkType();
                if(!mNetType.equals(netArr[1])){
                    isNetTypeChange = true;
                    mNetType = netArr[1];
                }
                if("WiFi".equals(netArr[1])){ //下面显示WIFI名称
                    mTvTitleLeft.setText("wifi");
                    mTvTitleLeft2.setText(TextUtils.isEmpty(netArr[0])?"——":netArr[0]);
                }else{
                    mTvTitleLeft.setText(TextUtils.isEmpty(netArr[1])?"——":netArr[1]);
                    mTvTitleLeft2.setText("我的网络");

                }
                mTvNet.setText(TextUtils.isEmpty(netArr[1])?"——":netArr[1]);
                mCurrentTime = newTime;

                if(isOperatorChange || mIsFrist){ //运营商有变更 或者未获取
                    //获取ftp配置
                    requestAppFtpConfig();
                }
                if(isOperatorChange || isNetTypeChange){ //运营商有变更或者网络类型有变更
                    updateFlowConfig();
                }
            }

            if(signal != null){
                mLastSignal = signal;
                mTvNet4g.setVisibility(View.GONE);
                if ("NR".equals(signal.getTypeNet())) {
                    if(signal.isSA()){
                        mLlViewNotNr.setVisibility(View.GONE);
                        //显示NR站点数据
                        mLlViewNr.setVisibility(View.VISIBLE);
                        mTvNrEarfcn.setText("460-00-" + signal.lte_gnb_nr + "-" + signal.lte_cellid_nr);
                        mTvNrRsrp.setText("RSRP:" + signal.lte_rsrp_nr);
                        mTvNrSinr.setText("SINR:" + signal.lte_sinr_nr);
                    }else{
                        mTvNet4g.setVisibility(View.VISIBLE);
                        mLlViewNotNr.setVisibility(View.VISIBLE);
                        mTvCellId.setText("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                        mTvRsrpOrLac.setText("RSRP:" + signal.getLte_rsrp());
                        mTvSinrOrRxl.setText("SINR:" + signal.getLte_sinr());
                        //显示NR站点数据
                        mLlViewNr.setVisibility(View.VISIBLE);
                        mTvNrEarfcn.setText("EARFCH:" + signal.getLte_earfcn_nr());
                        mTvNrRsrp.setText("RSRP:" + signal.getLte_rsrp_nr());
                        mTvNrSinr.setText("SINR:" + signal.getLte_sinr_nr());
                    }
                }else if ("LTE".equals(signal.getTypeNet())) {
                    mTvCellId.setText("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                    mTvRsrpOrLac.setText("RSRP:" + signal.getLte_rsrp());
                    mTvSinrOrRxl.setText("SINR:" + signal.getLte_sinr());
                    mLlViewNr.setVisibility(View.GONE);
                } else if ("TD".equals(signal.getTypeNet())) {
                    mTvCellId.setText("CELLID:" + signal.getTd_ci());
                    mTvRsrpOrLac.setText("LAC:" + signal.getTd_lac());
                    mTvSinrOrRxl.setText("PCCPCH:" + signal.getTd_pccpch());
                    mLlViewNr.setVisibility(View.GONE);
                }else if ("GSM".equals(signal.getTypeNet())) {
                    mTvCellId.setText("CELLID:" + signal.getGsm_cid());
                    mTvRsrpOrLac.setText("LAC:" + signal.getGsm_lac());
                    mTvSinrOrRxl.setText("RXL:" + signal.getGsm_rxl());
                    mLlViewNr.setVisibility(View.GONE);
                }else{
                    //未识别信号类型
                    refreshSignalModule();
                }
            }else{
                //无信号 不显示模块
                //				refresh(false);
            }
        }
    }

    @Override
    public void onAnalysis(int status, Object... obj) {
        super.onAnalysis(status, obj);
        if(status == 100){ //获取省地市
            if(!mProvince.equals((String)obj[0])){
                mProvince = (String)obj[0];
                SharedPreHandler.getShared(mActivity).setStrShared("Province", mProvince);
            }

            if(!mNowCity.equals((String)obj[1])){
                mNowCity = (String)obj[1];
                SharedPreHandler.getShared(mActivity).setStrShared("City", mNowCity);
            }
        }
    }

    @Override
    public void initializationFtp() {
        try{
            String downIp = "", downUser = "", downPasswd = "", downPath = "", uploadIp = "", uploadUser = "",
                    uploadPasswd = "", downHostType = "", upHostType = "", downHostName = "", upHostName = "";
            int downPort = -1, downThread = SystemUtil.getInstance().getSystemToV() >= 26 ?8 : 3, uploadPort = -1, uploadThread = 1, uploadSize = -1, downLenght = -1;

            BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
            boolean FTP_DOWN_OPEN = false;
            boolean FTP_UPLOAD_OPEN = false;
            if(beanAppFtpConfig != null){
                if(FTP_TYPE_ALL.equals(beanAppFtpConfig.ftpType)){
                    FTP_DOWN_OPEN = true;
                    FTP_UPLOAD_OPEN = true;
                }else if(FTP_TYPE_DOWNLOAD.equals(beanAppFtpConfig.ftpType)){
                    FTP_DOWN_OPEN = true;
                    FTP_UPLOAD_OPEN = false;
                }else if(FTP_TYPE_UPLOAD.equals(beanAppFtpConfig.ftpType)){
                    FTP_DOWN_OPEN = false;
                    FTP_UPLOAD_OPEN = true;
                }

                //保存是否下载上传
                SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().FTP_DOWN_OPEN, FTP_DOWN_OPEN);
                SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().FTP_UPLOAD_OPEN, FTP_UPLOAD_OPEN);

                if(beanAppFtpConfig.serverType){
                    downUser = beanAppFtpConfig.downAccount;
                    downPasswd = beanAppFtpConfig.downPassword;
                    downIp = beanAppFtpConfig.ip;
                    downPath = beanAppFtpConfig.filename;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.port)){
                        downPort = Integer.parseInt(beanAppFtpConfig.port);
                    }
                    downHostType = "";
                    downHostName = beanAppFtpConfig.hostType;
                    uploadUser = beanAppFtpConfig.upAccount;
                    uploadPasswd = beanAppFtpConfig.upPassword;
                    uploadIp = beanAppFtpConfig.ip;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.port)){
                        uploadPort = Integer.parseInt(beanAppFtpConfig.port);
                    }
                    upHostType = "";
                    upHostName = beanAppFtpConfig.hostType;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.uploadSize)){
                        uploadSize = Integer.parseInt(beanAppFtpConfig.uploadSize);
                    }
                }else{ //自定义服务器
                    downUser = beanAppFtpConfig.customDownAccount;
                    downPasswd = beanAppFtpConfig.customDownPassword;
                    downIp = beanAppFtpConfig.customDownIp;
                    downPath = beanAppFtpConfig.customDownPath;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.customDownPort)){
                        downPort = Integer.parseInt(beanAppFtpConfig.customDownPort);
                    }
                    downHostType = "";
                    downHostName = beanAppFtpConfig.hostType;
                    uploadUser = beanAppFtpConfig.customUpAccount;
                    uploadPasswd = beanAppFtpConfig.customUpPassword;
                    uploadIp = beanAppFtpConfig.customUpIp;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.customUpPort)){
                        uploadPort = Integer.parseInt(beanAppFtpConfig.customUpPort);
                    }
                    upHostType = "";
                    upHostName = beanAppFtpConfig.hostType;
                    if(!TextUtils.isEmpty(beanAppFtpConfig.customUpSize)){
                        uploadSize = Integer.parseInt(beanAppFtpConfig.customUpSize);
                    }
                }
            }

            initFtpTestLogInfo(downIp, downPath, downPort, uploadIp, uploadPort, downHostType, upHostType, downHostName, upHostName);
            appTest = new StringBuffer();
            appTest.append(" downIp=" + downIp);
            appTest.append(" DownPort=" + downPort);
            appTest.append(" DownUser=" + downUser);
            appTest.append(" DownPasswd=" + downPasswd);
            appTest.append(" DownPath=" + downPath);
            appTest.append(" downHostType=" + downHostType);
            appTest.append(" UploadIp=" + uploadIp);
            appTest.append(" UploadPort=" + uploadPort);
            appTest.append(" UploadUser=" + uploadUser);
            appTest.append(" UploadPasswd=" + uploadPasswd);
            appTest.append(" upHostType=" + upHostType);

            WybLog.syso("下载ip：" + downIp + "-下载端口：" + downPort + "-下载帐号：" + downUser + "-下载密码：" + downPasswd + "-下载线程："
                    + downThread + "-下载地址：" + downPath + "-下载大小：" + downLenght + "上传IP：" + uploadIp + "-上传端口：" + uploadPort
                    + "-上传帐号：" + uploadUser + "-上传密码：" + uploadPasswd + "-上传线程：" + uploadThread + "-上传大小：" + uploadSize);
            boolean isSucceed = initFtp(downIp, downPort, downUser, downPasswd, downThread, downPath, downLenght, uploadIp,
                    uploadPort, uploadUser, uploadPasswd, uploadThread, uploadSize);
            if (!isSucceed) {
                showCommon("请勿输入符号 \\");
                closeFtp(true);
                mTvSpeedTestMsg.setText("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initFtpTestLogInfo(String downHostAddr, String downPath, Integer downPort, String upHostAddr,
                                    Integer upPort, String downHostType, String upHostType, String donwHostName, String upHostName) {
        mFtpTestInfo = new Db_JJ_FTPTestInfo();
        mFtpTestInfo.setUserId(UtilHandler.getInstance().toInt(getUserID(), 0));
        mFtpTestInfo.setDownHostAddr(downHostAddr);
        mFtpTestInfo.setDownPath(downPath);
        mFtpTestInfo.setDownPort(downPort == null ? 21 : downPort);
        mFtpTestInfo.setSourceType(getModule(mFtpTestType));
        mFtpTestInfo.setTestBegin(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));
        mFtpTestInfo.setUpHostAddr(upHostAddr);
        mFtpTestInfo.setUpPort(upPort == null ? 21 : upPort);
        mFtpTestInfo.setDownHostType(downHostType);
        mFtpTestInfo.setUpHostType(upHostType);
        mFtpTestInfo.setDownHostTypeName(donwHostName);
        mFtpTestInfo.setUpHostTypeName(upHostName);
        mFtpTestInfo.testName = "";
    }

    @Override
    public void connected(boolean isDown, int connectedType) {
        if (!isExecute()) {
            return;
        }
        // 0准备下载 1连接成功 2下载错误/上传错误 -1超时 3正在上传下载 4完成
        switch (connectedType) {
            case 0:
                mTvSpeedTestMsg.setText(isDown ? "下载正在连接ftp" : "上传正在连接ftp");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.ui_gray));
                break;
            case 1:
                mTvSpeedTestMsg.setText(isDown ? "正在下载测试" : "正在上传测试");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.ui_gray));
                break;
            case 2:
                mTvSpeedTestMsg.setText(isDown ? "下载错误" : "上传错误");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.red));
                saveAppWarningLog(isDown ? "下载错误" : "上传错误");
                break;
            case 3:
                mTvSpeedTestMsg.setText(isDown ? "正在下载" : "正在上传");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.ui_gray));
                if(isDown && !isStartDownloadFlag && mStatus == STATUS_TESTING){
                    isStartDownloadFlag = true;
//                    startAnimatorModule_4(isDown);
                }else if(!isDown && !isStartUploadFlag && mStatus == STATUS_TESTING){
                    isStartUploadFlag = true;
//                    startAnimatorModule_4(isDown);
                }
                break;
            case 4:
                stopAnimatorModule_4();
                mTvSpeedTestMsg.setText("测试完成");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.ui_gray));
                break;
            case 5:
                mTvSpeedTestMsg.setText("下载文件不存在");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.red));
                saveAppWarningLog("下载文件不存在");
                break;
            case 6:
                mTvSpeedTestMsg.setText("上传数据准备中");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.ui_gray));
                break;
            case -1:
                mTvSpeedTestMsg.setText("测试超时");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.red));
                saveAppWarningLog("测试超时");
                break;
            case -2:
                mTvSpeedTestMsg.setText("ftp连接失败");
                mTvSpeedTestMsg.setTextColor(getResources().getColor(R.color.red));
                saveAppWarningLog("ftp连接失败");
                break;
            default:
                break;
        }
    }

    @Override
    public void updateProgress(boolean isDown, float secondSize) {
        if (mLastSignal == null) {
            mLastSignal = new Signal(this,getUserID());
        }
        mLastSignal.setUserId(getUserID());
        mLastSignal.rate = (float) UtilHandler.getInstance().toDfSum(secondSize * 8 / 1024f / 1024f, "00");
        String[] netArr = NetInfoUtil.getOperatorNetworkType();
        mLastSignal.netType = netArr[0]+ "," + netArr[1];
        if (mLastSignal.getTypeNet().equals("NR") || mLastSignal.getTypeNet().equals("LTE")) {
            if(mLastSignal.getTypeNet().equals("NR")){
                if(mLastSignal.isSA()){
                    mLastSignal.cellId = "460-00-" + mLastSignal.lte_gnb_nr + "-" + mLastSignal.lte_cellid_nr;
                }else{
                    mLastSignal.cellId = "460-00-" + mLastSignal.getLte_enb() + "-" + mLastSignal.getLte_cid();
                }
            }else{
                mLastSignal.cellId = "460-00-" + mLastSignal.getLte_enb() + "-" + mLastSignal.getLte_cid();
            }
        } else if (mLastSignal.getTypeNet().equals("GSM")) {
            mLastSignal.cellId = mLastSignal.getGsm_cid();
        }
        if (isDown) {
            mLastSignal.testType = 1;
            try {
                mSignalTestDownList.add((Signal)mLastSignal.clone());
            } catch (CloneNotSupportedException e) {
                mSignalTestDownList.add(mLastSignal);
            }
            mTvDownOrUpIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_download, 0, 0, 0);
            mDownloadCharView.notifyDataSetChange(secondSize * 8 /1024 / 1024, isDown);
            mDetailsDownloadCharView.notifyDataSetChange(secondSize * 8 /1024 / 1024, isDown);
        } else {
            mLastSignal.testType = 2;
            try {
                mSignalTestUploadList.add((Signal)mLastSignal.clone());
            } catch (CloneNotSupportedException e) {
                mSignalTestUploadList.add(mLastSignal);
            }
            mTvDownOrUpIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_upload, 0, 0, 0);
            mSpeedTestDialView.setRoundColor(getResources().getColor(R.color.upload_text_color));
            mUploadCharView.notifyDataSetChange(secondSize * 8 /1024 / 1024, isDown);
            mDetailsUploadCharView.notifyDataSetChange(secondSize * 8 /1024 / 1024, isDown);
        }
        mSpeedTestDialView.setCurrentStatus(secondSize * 8 / 1024f / 1024f);
    }

    @Override
    public void updateSignal(boolean isDown, long[] result) {
        String value = String.valueOf((float) UtilHandler.getInstance().toDfSum(result[1] / 1024 * 8.0, "00"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("flow_time", result[0]);
        map.put("flow", result[1]);
        if (mLastSignal != null) {
            if (isDown) {
                mLastSignal.setDl(value);
                map.put("signal", mLastSignal);
                mArrSignalDown.add(map);
            } else {
                mLastSignal.setUl(value);
                map.put("signal", mLastSignal);
                mArrSignalUp.add(map);
            }
        }
    }

    @Override
    public void singleResult(boolean isDown, float avg, float sum) {
        super.singleResult(isDown, avg, sum);
        if(isDown){
            mTvAvgDownload.setText(String.valueOf(avg));
            mTvDetailsAvgDownload.setText(String.valueOf(avg));
            mCurrentAvgDownload = avg;
            mAllDownload = sum;

            //计算排行 只计算下载
            updateRank();
        }else{
            mTvAvgUpload.setText(String.valueOf(avg));
            mTvDetailsAvgUpload.setText(String.valueOf(avg));
            mCurrentAvgUpload = avg;
            mAllUpload = sum;
        }
        handAbnormal(avg);
    }

    @Override
    public void progressOk(float maxDown, float minDown, float avgDown, int downTime, float sumDowns,
                           float maxUpload, float minUpload, float avgUpload, int uploadTime, float sumUploads) {
        if(mIsAbnormal){
            return;
        }
        //显示结束页面
        showDeatails();
        mSpeedTestDialView.setRoundColor(getResources().getColor(R.color.download_text_color));
        mSpeedTestDialView.setCurrentStatus(0);

        if(mPingInfo != null){
            mTvPacketLoss.setText(String.valueOf(mPingInfo.packetLoss));
            mTvDetailsPacketLoss.setText(String.valueOf(mPingInfo.packetLoss));
        }

        calculateAvgRsrp();

        saveFtpTestLog(mLastSignal, avgDown, maxDown, avgUpload, maxUpload);
    }

    /**
     * 测速值异常 界面关闭处理
     * @param avg 数值
     */
    private void handAbnormal(float avg){
        boolean isClose = false;
        if(mNetType.equalsIgnoreCase("WiFi") || mNetType.equalsIgnoreCase("5G")){
            if(avg > 2000){
                isClose = true;
            }
        }else if(mNetType.equalsIgnoreCase("4G")){
            if(avg > 200){
                isClose = true;
            }
        }else if(mNetType.equalsIgnoreCase("3G") || mNetType.equalsIgnoreCase("2G")){
            if(avg > 50){
                isClose = true;
            }
        }
        if(isClose){ //返回测试初始界面
            mIsAbnormal = true;
            stopSpeedTest();
        }
    }

    /**
     * 计算平均rsrp
     */
    private void calculateAvgRsrp(){
        try{
            //统计平均sinr rsrp
            float sumDownRsrp = 0, sumDownSinr = 0;
            float sumUploadRsrp = 0, sumUploadSinr = 0;
            int sumCountDownRsrp = 0, sumCountDownSinr = 0;
            int sumCountUploadRsrp = 0, sumCountUploadSinr = 0;
            for(Signal downSignal : mSignalTestDownList){
                if(!TextUtils.isEmpty(downSignal.getLte_rsrp())){
                    sumDownRsrp += Float.parseFloat(downSignal.getLte_rsrp());
                    sumCountDownRsrp++;
                }
                if(!TextUtils.isEmpty(downSignal.getLte_sinr())){
                    sumDownSinr += Float.parseFloat(downSignal.getLte_sinr());
                    sumCountDownSinr++;
                }
            }
            for(Signal uploadSignal : mSignalTestUploadList){
                if(!TextUtils.isEmpty(uploadSignal.getLte_rsrp())){
                    sumUploadRsrp += Float.parseFloat(uploadSignal.getLte_rsrp());
                    sumCountUploadRsrp++;
                }
                if(!TextUtils.isEmpty(uploadSignal.getLte_sinr())){
                    sumUploadSinr += Float.parseFloat(uploadSignal.getLte_sinr());
                    sumCountUploadSinr++;
                }
            }

            float averageDownRsrp = 0;
            float averageDownSinr = 0;
            float averageUploadRsrp = 0;
            float averageUploadSinr = 0;
            if(sumCountDownRsrp > 0){
                averageDownRsrp = sumDownRsrp / sumCountDownRsrp;
            }
            if(sumCountDownSinr > 0){
                averageDownSinr = sumDownSinr / sumCountDownSinr;
            }
            if(sumCountUploadRsrp > 0){
                averageUploadRsrp = sumUploadRsrp / sumCountUploadRsrp;
            }
            if(sumCountUploadSinr > 0){
                averageUploadSinr = sumUploadSinr / sumCountUploadSinr;
            }

            float avgRsrp = (averageDownRsrp + averageUploadRsrp) / 2;
            updateRsrp(avgRsrp);

            if(DEBUG){
                SpeedTestUtil.getInstance().writeToText("主测速--下载平均Rsrp,Sinr:" + averageDownRsrp+","+averageDownSinr+
                        "上传平均Rsrp,Sinr:" + averageUploadRsrp+","+averageUploadSinr, "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 保存上传日志
     * @param signal
     * @param downSpeedAvg
     * @param downSpeedMax
     * @param upSpeedAvg
     * @param upSpeedMax
     */
    private void saveFtpTestLog(final Signal signal, final double downSpeedAvg, final double downSpeedMax,
                                final double upSpeedAvg, final double upSpeedMax) {
        showMyDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                double mDownSpeedMax = downSpeedMax;
                double mUpSpeedMax = upSpeedMax;

                if (mSignalTestDownList != null && mSignalTestDownList.size() > 0) {
                    if(isAlgorithm){
                        algorithm1(mSignalTestDownList);
                    }else{
                        algorithm2(mSignalTestDownList);
                    }

                    Signal signalRrateMax = Collections.max(mSignalTestDownList, new Comparator<Signal>() {
                        @Override
                        public int compare(Signal lhs, Signal rhs) {
                            if (lhs.rate > rhs.rate) {
                                return 1;
                            } else if (lhs.rate < rhs.rate) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    WybLog.i("speedtest", "downSpeedMax == " + mDownSpeedMax);
                    WybLog.i("speedtest", "signalRrateMax.rate == " + signalRrateMax.rate);
                    if (mDownSpeedMax > signalRrateMax.rate) {
                        mDownSpeedMax = signalRrateMax.rate;
                    }
                }

                if (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {
                    if(isAlgorithm){
                        algorithm1(mSignalTestUploadList);
                    }else{
                        algorithm2(mSignalTestUploadList);
                    }

                    Signal signalRrateMax = Collections.max(mSignalTestUploadList, new Comparator<Signal>() {
                        @Override
                        public int compare(Signal lhs, Signal rhs) {
                            if (lhs.rate > rhs.rate) {
                                return 1;
                            } else if (lhs.rate < rhs.rate) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    WybLog.i("speedtest", "upSpeedMax == " + mUpSpeedMax);
                    WybLog.i("speedtest", "signalRrateMax.rate == " + signalRrateMax.rate);
                    if (mUpSpeedMax > signalRrateMax.rate) {
                        mUpSpeedMax = signalRrateMax.rate;
                    }
                }

                BeanAppFtpConfig config = getSelectServer();
                mFtpTestInfo.logId = mLogId;
                mFtpTestInfo.operator = config.operator;
                mFtpTestInfo.downEstimateFlow = String.valueOf(SpeedTestDataSet.mDownFlow);
                mFtpTestInfo.upEstimateFlow = String.valueOf(SpeedTestDataSet.mUpFlow);
                mFtpTestInfo.downRealFlow = String.valueOf(mAllDownload);
                mFtpTestInfo.upRealFlow = String.valueOf(mAllUpload);
                mFtpTestInfo.setDownSpeedAvg(UtilHandler.getInstance().toDfSum(downSpeedAvg, "00"));
                mFtpTestInfo.setDownSpeedMax(UtilHandler.getInstance().toDfSum(mDownSpeedMax, "00"));
                mFtpTestInfo.setUpSpeedAvg(UtilHandler.getInstance().toDfSum(upSpeedAvg, "00"));
                mFtpTestInfo.setUpSpeedMax(UtilHandler.getInstance().toDfSum(mUpSpeedMax, "00"));
                mFtpTestInfo.downloadTestList = mSignalTestDownList;
                mFtpTestInfo.uploadTestList = mSignalTestUploadList;
                if(mPingInfo != null){
                    mFtpTestInfo.ping = String.valueOf(mPingInfo.getTimes());
                    mFtpTestInfo.jitter= String.valueOf(mPingInfo.shake);
                    mFtpTestInfo.packetLoss = String.valueOf(mPingInfo.packetLoss);
                }

                if (signal != null) {
                    if (signal.getTypeNet().equals("NR") || signal.getTypeNet().equals("LTE")) {
                        if(signal.getTypeNet().equals("NR")){
                            if(signal.isSA()){
                                mFtpTestInfo.setCellId("460-00-" + signal.lte_gnb_nr + "-" + signal.lte_cellid_nr);
                                mFtpTestInfo.setLac(signal.lte_tac_nr);// Lac/Tac
                                mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.lte_pci_nr, 0));// pci
                                mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.lte_band_nr, 0));// 频点
                                mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.lte_rsrp_nr, 0));// rsrp/exl
                                mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.lte_sinr_nr, 0));// sinr
                                mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.lte_rsrq_nr, 0));// rsrq
                                mFtpTestInfo.setCellName(signal.lte_name_nr);
                            }else{
                                mFtpTestInfo.setCellId("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                                mFtpTestInfo.setLac(signal.getLte_tac());// Lac/Tac
                                mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.getLte_pci(), 0));// pci
                                mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getLte_band(), 0));// 频点
                                mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getLte_rsrp(), 0));// rsrp/exl
                                mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.getLte_sinr(), 0));// sinr
                                mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.getLte_rsrq(), 0));// rsrq
                                mFtpTestInfo.setCellName(signal.getLte_name());
                            }
                        }else{
                            mFtpTestInfo.setCellId("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                            mFtpTestInfo.setLac(signal.getLte_tac());// Lac/Tac
                            mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.getLte_pci(), 0));// pci
                            mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getLte_band(), 0));// 频点
                            mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getLte_rsrp(), 0));// rsrp/exl
                            mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.getLte_sinr(), 0));// sinr
                            mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.getLte_rsrq(), 0));// rsrq
                            mFtpTestInfo.setCellName(signal.getLte_name());
                        }
                    } else if (signal.getTypeNet().equals("GSM")) {
                        mFtpTestInfo.setCellId(signal.getGsm_cid());
                        mFtpTestInfo.setLac(signal.getGsm_lac());
                        mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getTd_pccpch(), 0));
                        mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getGsm_rxl(), 0));
                        mFtpTestInfo.setCellName(signal.getGsm_name());
                    }
                    mFtpTestInfo.setSignalType(signal.getTypeNet());
                }
                mFtpTestInfo.setNetType(NetInfoUtil.getOperatorNetworkType(mContext));
                mFtpTestInfo.phoneModel = SystemUtil.getInstance().getSystemModel();

                if (gpsLatlng != null) {
                    mFtpTestInfo.setLatitude(gpsLatlng.latitude);
                    mFtpTestInfo.setLongitude(gpsLatlng.longitude);
                }

                mFtpTestInfo.setTestEnd(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));

                //以下做上传数据处理
                List<Db_JJ_FTPTestInfo> list = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance()
                        .queryObj(Db_JJ_FTPTestInfo.class, "testBegin =? and isUpload =? and userid =?", new Object[] {
                                mFtpTestInfo.getTestBegin(), false, UtilHandler.getInstance().toInt(getUserID(), 0) });
                if (list == null || list.size() == 0) {
                    if (TextUtils.isEmpty(mFtpTestInfo.testName)) {
                        mFtpTestInfo.testName = "";
                    }
                    DbHandler.getInstance().insert(mFtpTestInfo);
                    mUploadFtpTestList = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance().queryObj(
                            Db_JJ_FTPTestInfo.class, "isUpload =? and userid =?",
                            new Object[] { false, UtilHandler.getInstance().toInt(getUserID(), 0) });

                    for (int i = 0; i < mUploadFtpTestList.size(); i++) {
                        Db_JJ_FTPTestInfo ftpTestInfo = mUploadFtpTestList.get(i);
                        if (ftpTestInfo.getTestBegin().equals(mFtpTestInfo.getTestBegin())) {
                            // 存储每秒下载(和上传)详情
                            for (Signal signal : mSignalTestDownList) {
                                signal.lastId = ftpTestInfo.get_id();
                                signal.testBegin = ftpTestInfo.getTestBegin();
                                signal.testEnd = ftpTestInfo.getTestEnd();
                                DbHandler.getInstance().save(signal);

                            }
                            for (Signal signal : mSignalTestUploadList) {
                                signal.lastId = ftpTestInfo.get_id();
                                signal.testBegin = ftpTestInfo.getTestBegin();
                                signal.testEnd = ftpTestInfo.getTestEnd();
                                DbHandler.getInstance().save(signal);
                            }
                        }
                        SystemClock.sleep(10);
                        ftpTestInfo.downloadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
                                "lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
                                new Object[] { ftpTestInfo.get_id(), getUserID(), 1 , ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd()});
                        ftpTestInfo.uploadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
                                "lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
                                new Object[] { ftpTestInfo.get_id(), getUserID(), 2 , ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd()});

                        WybLog.i("speedtest", "ftpTestInfo.downloadTestList.size = "+ ftpTestInfo.downloadTestList.size());
                        WybLog.i("speedtest", "ftpTestInfo.uploadTestList.size = "+ ftpTestInfo.uploadTestList.size());
                    }
                    mChildHandler.sendEmptyMessage(HANDLER_QUERY_FTP_UPLOAD_DATA);
                }
            }
        }).start();
    }

    @Override
    public void initialization(String downName, String sDownPath,
                               String sUploadPath) {
        inits(downName, sDownPath, sUploadPath);
    }

    @Override
    public void initializationFtp(int position) {

    }

    /**
     * 登陆登出更新广播
     * @author JQJ
     *
     */
    private class UpdateConfigReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if(arg1.getAction().equals(TypeKey.getInstance().ACTION_MAINHOME_SUPER)){
                int type = arg1.getIntExtra("type", -1);
                if(type == -1000){
                    refreshSignalModule();
                    refreshLogoModule();

                    //刷新水印
                    WaterMarkUtil.showWatermarkView(SpeedTestMainActivity.this, getUser());
                }
            }
        }
    }

    /**
     * 告警
     * @param reason
     */
    private void saveAppWarningLog(String reason) {
        new SpeedTestSuggestDialog(mActivity).show(SpeedTestSuggestDialog.TYPE_NETWORK_NOT_GOOD);

        mAppWarningInfo = new Db_JJ_AppWarningLog();
        mAppWarningInfo.setClassName(getClass().getSimpleName());
        mAppWarningInfo.setOperateInfo(getModule(mFtpTestType));
        mAppWarningInfo.setMobileType(android.os.Build.MODEL);
        mAppWarningInfo.setWarningInfo(reason + " " + (appTest == null ? "" : appTest.toString()));
        mAppWarningInfo.setWarningTime(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));
        new Thread(new Runnable() {

            @Override
            public void run() {
                DbHandler.getInstance().insert(mAppWarningInfo);
                mAppWarningList = (List<Db_JJ_AppWarningLog>) DbHandler.getInstance()
                        .queryObj(Db_JJ_AppWarningLog.class, "isUpload =?", new Object[] { false });
                mChildHandler.sendEmptyMessage(HANDLER_QUERY_APP_WARNING_UPLOAD_DATA);
            }
        }).start();
    }

    /**
     * 更新流量预估数值
     */
    private void updateFlowConfig(){
        try{
            if(mNetType.equalsIgnoreCase("WiFi") || mNetType.equalsIgnoreCase("5G")){ //根据环境自动选择大小包
                mPackageType = PACAKGE_SIZE_LARGE;
            }else if(mNetType.equalsIgnoreCase("4G")){
                mPackageType = PACAKGE_SIZE_MIDDLE;
            }else if(mNetType.equalsIgnoreCase("3G") || mNetType.equalsIgnoreCase("2G")){
                mPackageType = PACAKGE_SIZE_SMALL;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setIntShared("PackageType", mPackageType);

            long allTestTime = mAllTestTimeForLarge;
            long updateTime = mUpdateTimeForLarge;
            if(mPackageType == PACAKGE_SIZE_LARGE){ //大包
                allTestTime = mAllTestTimeForLarge;
                updateTime = mUpdateTimeForLarge;
            }else if(mPackageType == PACAKGE_SIZE_MIDDLE){ //中包
                allTestTime = mAllTestTimeForMiddle;
                updateTime = mUpdateTimeForMiddle;
            }else if(mPackageType == PACAKGE_SIZE_SMALL){ //小包
                allTestTime = mAllTestTimeForSmall;
                updateTime = mUpdateTimeForSmall;
            }

            //设置测速配置
            setTimeOut(allTestTime);
            setUpdateTime(updateTime);

            //设置曲线X轴个数
            int xCount = (int)(allTestTime / updateTime);
            mDownloadCharView.setCountForX(xCount-1);
            mUploadCharView.setCountForX(xCount-1);
            mDetailsDownloadCharView.setCountForX(xCount-1);
            mDetailsUploadCharView.setCountForX(xCount-1);

            //计算流量预估值
            BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
            if(beanBaseConfig != null){
                boolean isGotoDown = false;
                boolean isGotoUpload = false;
                boolean FTP_DOWN_OPEN = false;
                boolean FTP_UPLOAD_OPEN = false;
                BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
                ArrayList<BeanFlowConfig> list = beanBaseConfig.llConf;
                if(beanAppFtpConfig != null){
                    if(FTP_TYPE_ALL.equals(beanAppFtpConfig.ftpType)){
                        FTP_DOWN_OPEN = true;
                        FTP_UPLOAD_OPEN = true;
                    }else if(FTP_TYPE_DOWNLOAD.equals(beanAppFtpConfig.ftpType)){
                        FTP_DOWN_OPEN = true;
                        FTP_UPLOAD_OPEN = false;
                    }else if(FTP_TYPE_UPLOAD.equals(beanAppFtpConfig.ftpType)){
                        FTP_DOWN_OPEN = false;
                        FTP_UPLOAD_OPEN = true;
                    }
                }else{
                    SpeedTestDataSet.mDownFlow = 0;
                    SpeedTestDataSet.mUpFlow = 0;
                }

                if (FTP_DOWN_OPEN) {
                    String ip = beanAppFtpConfig.ip;
                    if(beanAppFtpConfig.serverType){
                        ip = beanAppFtpConfig.ip;
                    }else{
                        ip = beanAppFtpConfig.customDownIp;
                    }
                    for(BeanFlowConfig config : list){
                        if(config.hostAddr.equals(ip)){ //ip地址相同
                            if("WiFi".equalsIgnoreCase(mNetType)){ //wifi情况下
                                if(config.netType.equalsIgnoreCase(mNetType)){
                                    isGotoDown = true;
                                    SpeedTestDataSet.mDownFlow = config.downFlow;
                                    break;
                                }
                            }else if(!TextUtils.isEmpty(mNetType) && !TextUtils.isEmpty(mOperator)){//不是wifi情况下
                                if(config.netType.equals(mNetType) && config.operator.equals(mOperator)){
                                    isGotoDown = true;
                                    SpeedTestDataSet.mDownFlow = config.downFlow;
                                    break;
                                }
                            }
                        }
                    }
                    if(!isGotoDown){
                        SpeedTestDataSet.mDownFlow = 0;
                    }
                }else{
                    SpeedTestDataSet.mDownFlow = 0;
                }

                if (FTP_UPLOAD_OPEN) {
                    String ip = beanAppFtpConfig.ip;
                    if(beanAppFtpConfig.serverType){
                        ip = beanAppFtpConfig.ip;
                    }else{
                        ip = beanAppFtpConfig.customUpIp;
                    }
                    for(BeanFlowConfig config : list){
                        if(config.hostAddr.equals(ip)){ //ip地址相同
                            if("WiFi".equalsIgnoreCase(mNetType)){ //wifi情况下
                                if(config.netType.equalsIgnoreCase(mNetType)){
                                    isGotoUpload = true;
                                    SpeedTestDataSet.mUpFlow = config.upFlow;
                                    break;
                                }
                            }else if(!TextUtils.isEmpty(mNetType) && !TextUtils.isEmpty(mOperator)){//不是wifi情况下
                                if(config.netType.equals(mNetType) && config.operator.equals(mOperator)){
                                    isGotoUpload = true;
                                    SpeedTestDataSet.mUpFlow = config.upFlow;
                                    break;
                                }
                            }
                        }
                    }
                    if(!isGotoUpload){ //没有对应数据
                        SpeedTestDataSet.mUpFlow = 0;
                    }
                }else{
                    SpeedTestDataSet.mUpFlow = 0;
                }

                //				double temp = (mDownFlow + mUpFlow) / 8 * allTestTime / 1000; //总流量
                //				DecimalFormat df = new DecimalFormat("#.##");
                //				String value = df.format(temp);
                //				mTvAllFlow.setText("本次测速预计消耗"+value+"MB流量");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //新通知才展现
    private void updateMsgLayout() {
        if (mIsShow) {
            mLlNotifyModule.setVisibility(View.VISIBLE);
        } else {
            mLlNotifyModule.setVisibility(View.GONE);
        }
    }

    private long lastTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if(System.currentTimeMillis() - lastTime >= 2000){
                    lastTime = System.currentTimeMillis();
                    showCommonBottom(getResources().getString(R.string.app_exit));
                    return false;
                }else{
                    this.finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(mReceiver != null){
                unregisterReceiver(mReceiver);
            }
            if(mOnResumeReceiver != null){
                unregisterReceiver(mOnResumeReceiver);
            }
            if(mOnUpdateFtpReceiver != null){
                unregisterReceiver(mOnUpdateFtpReceiver);
            }
            if(mNetworkChangeReceiver != null){
                unregisterReceiver(mNetworkChangeReceiver);
            }
            SignalServiceUtil.getInstances().stopService(this);
            MyLocationListener.getInstances().stop();
            //关闭电源锁
            PowerManagerUtil.releaseWakeLock();
        }catch(Exception e){
        }
    }
}
