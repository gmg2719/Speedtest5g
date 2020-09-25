package cn.nokia.speedtest5g.wifi.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fjmcc.wangyoubao.app.signal.OnChangSignalListener;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;
import com.fjmcc.wangyoubao.app.signal.bean.TDCell;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.cast.SimSignalHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;

/**
 * 网络诊断 改造成一次性测完全部 后面做数据展示
 * @author JQJ
 *
 */
public class NetworkDiagnoseActivity extends BaseActionBarActivity implements OnClickListener, OnChangSignalListener{

    private final int WHAT_STATUS_SCAN = 1; //扫描
    private final int WHAT_STATUS_SUCCESS_RESULT = 2; //结果  成功的
    private final int WHAT_STATUS_FAIL_RESULT = 3; //结果 失败的

    private final String STATUS_SUCCESS = "SUCCESS";
    private final String STATUS_FAIL = "FAIL";

    //执行检测代码步骤
    private final int WHAT_STEP_ONE = 1; //步骤1
    private final int WHAT_STEP_TWO = 2; //步骤2
    private final int WHAT_STEP_THREE = 3; //步骤3
    private final int WHAT_STEP_FOUR = 4; //步骤4

    //更新进度步骤
    private final int WHAT_STEP_UPDATE_TWO = 6; //步骤2
    private final int WHAT_STEP_UPDATE_THREE = 7; //步骤3
    private final int WHAT_STEP_UPDATE_FOUR = 8; //步骤4

    private final int WHAT_STEP_TEST_OVER = 9; //检测结束
    private final int WHAT_STEP_TEST_START = 10; //检测开始
    private final int WHAT_UPDATE_PROGRESS = 11; //更新进度


    private RelativeLayout mRlModule1 = null; //扫描模块
    private LinearLayout mLlModule2 = null; //结果模块
    private LinearLayout mLlModule3 = null; //按钮模块

    private TextView mTvCount = null; //百分比
    private ProgressBar mProgressBar = null;
    private TextView mTvProgressBar = null;
    private ImageView mIvStatus = null; //状态
    private TextView mTvTip = null; //提示语

    private ImageView mIvStatus1 = null; //状态
    private TextView mTvTitle1 = null; //标题
    private TextView mTvValue1 = null; //数值
    private TextView mTvTip1 = null; //提示语
    private ImageView mIvStatus2 = null; //状态
    private TextView mTvTitle2 = null; //标题
    private TextView mTvValue2 = null; //数值
    private TextView mTvTip2 = null; //提示语
    private ImageView mIvStatus3 = null; //状态
    private TextView mTvTitle3 = null; //标题
    private TextView mTvValue3 = null; //数值
    private TextView mTvTip3 = null; //提示语
    private ImageView mIvStatus4 = null; //状态
    private TextView mTvTitle4 = null; //标题
    private TextView mTvValue4 = null; //数值

    private Button mBtn1 = null; //开始测速 或者设置网络
    private Button mBtn2 = null; //网络诊断

    private Handler mHandler = null;
    private Handler mHandlerInThread = null;
    private HandlerThread mHandlerThread = null;

    private String mNetType = ""; //当前信号类型
    private String mSignalValue = ""; //当前信号强度值
    private int mWifiSsidValue = -120; //当前wifi信号强度值
    private String mPingIp = "www.baidu.com";
    private PingInfo mPingInfo = null;

    private boolean mIsTesting = true;
    private int mProgress = 0;
    private WifiManager mWifiManager = null;

    //数据集
    private String[] objectOne = new String[3];
    private String[] objectTwo = new String[3];
    private String[] objectThree = new String[3];
    private String[] objectFour = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_network_diagnose_activity);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        init("网络诊断", true);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        initHandler();

        SimSignalHandler.getInstances().addListener(this, null);

        retry();
    }

    /**
     * 线程中执行
     * @param operate
     */
    private void updateProgress(int operate){
        int progress = mProgressBar.getProgress();
        long time = 3000; //3秒
        int allStep = 35;
        int step = 1;
        long sleepTime = time / (allStep / step);
        while(true){
            if(!mIsTesting){
                return;
            }
            step++;
            mProgress = progress + step;
            if(operate == WHAT_STEP_UPDATE_TWO && mProgress >= 35){
                mHandler.sendEmptyMessage(WHAT_STEP_UPDATE_TWO);
                return;
            }else if(operate == WHAT_STEP_UPDATE_THREE && mProgress >= 70){
                mHandler.sendEmptyMessage(WHAT_STEP_UPDATE_THREE);
                return;
            }else if(operate == WHAT_STEP_UPDATE_FOUR && mProgress >= 100){
                mHandler.sendEmptyMessage(WHAT_STEP_UPDATE_FOUR);
                return;
            }

            mHandler.sendEmptyMessage(WHAT_UPDATE_PROGRESS);
            SystemClock.sleep(sleepTime);
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler(){
        mHandlerThread = new HandlerThread("NetworkDiagnoseActivity");
        mHandlerThread.start();
        mHandlerInThread = new Handler(mHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                if(!mIsTesting){
                    return;
                }
                if(msg.what == WHAT_STEP_ONE){ //获取网络类型
                    String[] netArr = NetInfoUtil.getOperatorNetworkType();
                    if(TextUtils.isEmpty(netArr[1])){ //无网络
                        objectOne[0] = STATUS_FAIL;
                        objectOne[1] = "移动数据网络";
                        objectOne[2] = "网络连接已断开";
                        mHandler.sendEmptyMessageDelayed(WHAT_STEP_TEST_START, 2000);
                    }else{
                        if("WiFi".equals(netArr[1])){ //wifi
                            //							mPingIp = WifiUtil.getInstance().getWifiRouteIPAddress(NetworkDiagnoseActivity.this);
                            WifiInfo wifiInfo = mWifiManager.getConnectionInfo(); //获取已连接wifi信息
                            if(wifiInfo != null){
                                mWifiSsidValue = wifiInfo.getRssi();
                            }
                            objectOne[0] = STATUS_SUCCESS;
                            objectOne[1] = "Wi-Fi无线网络";
                            objectOne[2] = netArr[0];
                        }else{
                            //							mPingIp = WifiUtil.getInstance().getLocalIpAddress();
                            objectOne[0] = STATUS_SUCCESS;
                            objectOne[1] = "移动数据网络" + "(" + netArr[1] + ")";
                            objectOne[2] = netArr[0];
                        }
                        mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_TWO, 50);
                    }
                }else if(msg.what == WHAT_STEP_TWO){ //比较信号强度
                    //数值对比放在检测中执行
                    mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_THREE, 50);
                }else if(msg.what == WHAT_STEP_THREE){ //ping网址
                    PingInfo pingInfo = new PingInfo();
                    pingInfo.setIp(mPingIp);
                    PingThread pingThread  = new PingThread(pingInfo, -1, mHandler, 2, 2);
                    pingThread.start();
                }else if(msg.what == WHAT_STEP_FOUR){ //延时计算
                    if(mPingInfo != null){
                        if(mPingInfo.isState()){
                            objectFour[0] = STATUS_SUCCESS;
                            objectFour[1] = "服务器通讯";
                            objectFour[2] = getLevelByPing(mPingInfo.getTimes());
                        }else{
                            objectFour[0] = STATUS_FAIL;
                            objectFour[1] = "服务器通讯";
                            objectFour[2] = "通讯失败";
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(WHAT_STEP_TEST_START, 2000);
                }else if(msg.what == WHAT_STEP_UPDATE_TWO){
                    updateProgress(WHAT_STEP_UPDATE_TWO);
                }else if(msg.what == WHAT_STEP_UPDATE_THREE){
                    updateProgress(WHAT_STEP_UPDATE_THREE);
                }else if(msg.what == WHAT_STEP_UPDATE_FOUR){
                    updateProgress(WHAT_STEP_UPDATE_FOUR);
                }
            }
        };

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(!mIsTesting){
                    return;
                }

                if(msg.what == WHAT_STEP_TEST_START){ //开始展示数据
                    getSignalValue();
                    showResult(WHAT_STEP_ONE);
                    updateTip(WHAT_STEP_ONE);
                }else if(msg.what == WHAT_STEP_UPDATE_TWO){
                    showResult(WHAT_STEP_TWO);
                    updateTip(WHAT_STEP_TWO);
                    mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_UPDATE_THREE, 1000);
                }else if(msg.what == WHAT_STEP_UPDATE_THREE){
                    showResult(WHAT_STEP_THREE);
                    updateTip(WHAT_STEP_THREE);
                    mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_UPDATE_FOUR, 1000);
                }else if(msg.what == WHAT_STEP_UPDATE_FOUR){
                    showResult(WHAT_STEP_FOUR);
                    mHandler.sendEmptyMessageDelayed(WHAT_STEP_TEST_OVER, 500);
                }else if(msg.what == WHAT_STEP_TEST_OVER){
                    updateUi(WHAT_STATUS_SUCCESS_RESULT);
                }else if(msg.what == WHAT_UPDATE_PROGRESS){
                    mTvCount.setText(mProgress + "%");
                    mProgressBar.setProgress(mProgress);
                }else if(msg.what == EnumRequest.TASK_PING.toInt()){ //ping结果
                    mPingInfo = (PingInfo)msg.obj;
                    if(mPingInfo.isState()){
                        objectThree[0] = STATUS_SUCCESS;
                        objectThree[1] = "网络连通性";
                        objectThree[2] = "优良";
                    }else{
                        objectThree[0] = STATUS_FAIL;
                        objectThree[1] = "网络连通性";
                        objectThree[2] = "极差";
                    }
                    mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_FOUR, 50);
                }
            }
        };
    }

    /**
     * 展示结果
     * @param step
     */
    private void showResult(int step){
        if(step == WHAT_STEP_ONE){
            if(STATUS_SUCCESS.equals(objectOne[0])){ //成功
                mIvStatus1.setImageResource(R.drawable.icon_check_circle_light_blue);
                mTvTitle1.setTextColor(getResources().getColor(R.color.white_edeeee));
                mHandlerInThread.sendEmptyMessageDelayed(WHAT_STEP_UPDATE_TWO, 1000);
            }else if(STATUS_FAIL.equals(objectOne[0])){ //失败
                updateUi(WHAT_STATUS_FAIL_RESULT);
            }
            mTvTitle1.setText(objectOne[1]);
            mTvValue1.setText(objectOne[2]);
        }else if(step == WHAT_STEP_TWO){
            if(STATUS_SUCCESS.equals(objectTwo[0])){ //成功
                mIvStatus2.setImageResource(R.drawable.icon_check_circle_light_blue);
                mTvTitle2.setTextColor(getResources().getColor(R.color.white_edeeee));
            }else if(STATUS_FAIL.equals(objectTwo[0])){ //失败
                mIvStatus2.setImageResource(R.drawable.icon_check_circle_warning);
                mTvTitle2.setTextColor(Color.parseColor("#ff4646"));
            }
            mTvTitle2.setText(objectTwo[1]);
            mTvValue2.setText(objectTwo[2]);
        }else if(step == WHAT_STEP_THREE){
            if(STATUS_SUCCESS.equals(objectThree[0])){ //成功
                mIvStatus3.setImageResource(R.drawable.icon_check_circle_light_blue);
                mTvTitle3.setTextColor(getResources().getColor(R.color.white_edeeee));
            }else if(STATUS_FAIL.equals(objectThree[0])){ //失败
                mIvStatus3.setImageResource(R.drawable.icon_check_circle_warning);
                mTvTitle3.setTextColor(Color.parseColor("#ff4646"));
            }
            mTvTitle3.setText(objectThree[1]);
            mTvValue3.setText(objectThree[2]);
        }else if(step == WHAT_STEP_FOUR){
            if(STATUS_SUCCESS.equals(objectFour[0])){ //成功
                mIvStatus4.setImageResource(R.drawable.icon_check_circle_light_blue);
                mTvTitle4.setTextColor(getResources().getColor(R.color.white_edeeee));
            }else if(STATUS_FAIL.equals(objectFour[0])){ //失败
                mIvStatus4.setImageResource(R.drawable.icon_check_circle_warning);
                mTvTitle4.setTextColor(Color.parseColor("#ff4646"));
            }
            mTvTitle4.setText(objectFour[1]);
            mTvValue4.setText(objectFour[2]);
        }
    }

    /**
     * 更新 正在检测 提示语
     */
    private void updateTip(int step){
        if(step == WHAT_STEP_ONE){
            mTvTip1.setVisibility(View.VISIBLE);
            mTvTip2.setVisibility(View.GONE);
            mTvTip3.setVisibility(View.GONE);
        }else if(step == WHAT_STEP_TWO){
            mTvTip1.setVisibility(View.GONE);
            mTvTip2.setVisibility(View.VISIBLE);
            mTvTip3.setVisibility(View.GONE);
        }else if(step == WHAT_STEP_THREE){
            mTvTip1.setVisibility(View.GONE);
            mTvTip2.setVisibility(View.GONE);
            mTvTip3.setVisibility(View.VISIBLE);
        }
    }

    private void updateUi(int status){
        if(status == WHAT_STATUS_SCAN){
            mRlModule1.setVisibility(View.VISIBLE);
            mLlModule2.setVisibility(View.GONE);
            mLlModule3.setVisibility(View.GONE);

            mNetType = ""; //当前信号类型
            mWifiSsidValue = 0;
            mSignalValue = ""; //当前信号强度值
            mPingInfo = null;
            mProgress = 0;
            mTvCount.setText("0%");
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
            mTvProgressBar.setVisibility(View.GONE);

            mIvStatus1.setImageResource(R.drawable.icon_check_circle_black);
            mTvTitle1.setTextColor(getResources().getColor(R.color.white_edeeee));
            mTvTitle1.setText("移动数据网络");
            mTvValue1.setText("");
            mTvTip1.setVisibility(View.GONE);
            mIvStatus2.setImageResource(R.drawable.icon_check_circle_black);
            mTvTitle2.setTextColor(getResources().getColor(R.color.white_edeeee));
            mTvValue2.setText("");
            mTvTip2.setVisibility(View.GONE);
            mIvStatus3.setImageResource(R.drawable.icon_check_circle_black);
            mTvTitle3.setTextColor(getResources().getColor(R.color.white_edeeee));
            mTvValue3.setText("");
            mTvTip3.setVisibility(View.GONE);
            mIvStatus4.setImageResource(R.drawable.icon_check_circle_black);
            mTvTitle4.setTextColor(getResources().getColor(R.color.white_edeeee));
            mTvValue4.setText("");
        }else if(status == WHAT_STATUS_SUCCESS_RESULT){
            mRlModule1.setVisibility(View.GONE);
            mLlModule2.setVisibility(View.VISIBLE);
            mLlModule3.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.VISIBLE);
            mTvProgressBar.setVisibility(View.GONE);
            mTvTip1.setVisibility(View.GONE);
            mTvTip2.setVisibility(View.GONE);
            mTvTip3.setVisibility(View.GONE);

            //判断是否能正常上网
            if(STATUS_FAIL.equals(objectThree[0])){ //ping不通
                mIvStatus.setImageResource(R.drawable.icon_network_diagnose_no_ok_flag);
                mTvTip.setText("当前网络已断开\n请先连接网络后重试");
                mBtn1.setText("设置网络");
            }else if(STATUS_SUCCESS.equals(objectThree[0])){
                mIvStatus.setImageResource(R.drawable.icon_network_diagnose_ok_flag);
                mTvTip.setText("当前网络连接正常\n可正常上网");
                mBtn1.setText("开始测速");
            }else if(STATUS_FAIL.equals(objectTwo[0])){
                mIvStatus.setImageResource(R.drawable.icon_network_diagnose_ok_flag);
                mTvTip.setText("当前网络连接正常\n但上网体验较差");
                mBtn1.setText("开始测速");
            }
        }else if(status == WHAT_STATUS_FAIL_RESULT){
            mRlModule1.setVisibility(View.GONE);
            mLlModule2.setVisibility(View.VISIBLE);
            mLlModule3.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.GONE);
            mTvProgressBar.setVisibility(View.VISIBLE);
            mTvTip1.setVisibility(View.GONE);
            mTvTip2.setVisibility(View.GONE);
            mTvTip3.setVisibility(View.GONE);

            mTvValue2.setText("请联网后重试");
            mTvValue3.setText("请联网后重试");
            mTvValue4.setText("请联网后重试");
            mIvStatus1.setImageResource(R.drawable.icon_check_circle_warning);
            mTvTitle1.setTextColor(Color.parseColor("#ff4646"));
            mIvStatus2.setImageResource(R.drawable.icon_check_circle_warning);
            mTvTitle2.setTextColor(Color.parseColor("#ff4646"));
            mIvStatus3.setImageResource(R.drawable.icon_check_circle_warning);
            mTvTitle3.setTextColor(Color.parseColor("#ff4646"));
            mIvStatus4.setImageResource(R.drawable.icon_check_circle_warning);
            mTvTitle4.setTextColor(Color.parseColor("#ff4646"));

            mIvStatus.setImageResource(R.drawable.icon_network_diagnose_no_ok_flag);
            mTvTip.setText("当前网络已断开\n请先连接网络后重试");
            mBtn1.setText("设置网络");
        }
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);

        mRlModule1 = (RelativeLayout)findViewById(R.id.network_diagnose_rl_module_1);
        mLlModule2 = (LinearLayout)findViewById(R.id.network_diagnose_ll_module_2);
        mLlModule3 = (LinearLayout)findViewById(R.id.network_diagnose_ll_module_3);

        mTvCount = (TextView)findViewById(R.id.network_diagnose_tv_count);
        mIvStatus = (ImageView)findViewById(R.id.network_diagnose_iv_status);
        mTvTip = (TextView)findViewById(R.id.network_diagnose_tv_tip);

        mProgressBar = (ProgressBar)findViewById(R.id.network_diagnose_pb_prgressBar);
        mTvProgressBar = (TextView)findViewById(R.id.network_diagnose_tv_prgressBar);

        mIvStatus1 = (ImageView)findViewById(R.id.network_diagnose_iv_status_1);
        mTvTitle1 = (TextView)findViewById(R.id.network_diagnose_tv_title_1);
        mTvValue1 = (TextView)findViewById(R.id.network_diagnose_tv_value_1);
        mTvTip1 = (TextView)findViewById(R.id.network_diagnose_tv_tip_1);
        mIvStatus2 = (ImageView)findViewById(R.id.network_diagnose_iv_status_2);
        mTvTitle2 = (TextView)findViewById(R.id.network_diagnose_tv_title_2);
        mTvValue2 = (TextView)findViewById(R.id.network_diagnose_tv_value_2);
        mTvTip2 = (TextView)findViewById(R.id.network_diagnose_tv_tip_2);
        mIvStatus3 = (ImageView)findViewById(R.id.network_diagnose_iv_status_3);
        mTvTitle3 = (TextView)findViewById(R.id.network_diagnose_tv_title_3);
        mTvValue3 = (TextView)findViewById(R.id.network_diagnose_tv_value_3);
        mTvTip3 = (TextView)findViewById(R.id.network_diagnose_tv_tip_3);
        mIvStatus4 = (ImageView)findViewById(R.id.network_diagnose_iv_status_4);
        mTvTitle4 = (TextView)findViewById(R.id.network_diagnose_tv_title_4);
        mTvValue4 = (TextView)findViewById(R.id.network_diagnose_tv_value_4);

        mBtn1 = (Button)findViewById(R.id.network_diagnose_btn_1);
        mBtn2 = (Button)findViewById(R.id.network_diagnose_btn_2);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
    }

    /**
     * 重新诊断
     */
    private void retry(){
        updateUi(WHAT_STATUS_SCAN);
        mHandlerInThread.sendEmptyMessage(WHAT_STEP_ONE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.network_diagnose_btn_1){
            Button btn = (Button)v;
            if(btn.getText().toString().equals("开始测速")){ //跳转至测速
                Intent intent = new Intent(TypeKey.getInstance().ACTION_HOME_TAG_CHANGE);
                intent.putExtra("type", 1000);
                NetworkDiagnoseActivity.this.sendBroadcast(intent);

                NetworkDiagnoseActivity.this.finish();
            }else if(btn.getText().toString().equals("设置网络")){ //打开设置界面
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }else if(v.getId() == R.id.network_diagnose_btn_2){
            retry();
        }
    }

    private void getSignalValue(){
        if(TextUtils.isEmpty(mNetType) && TextUtils.isEmpty(mSignalValue) && mWifiSsidValue == 0){
            objectTwo[0] = STATUS_FAIL;
            objectTwo[1] = "网络信号强度";
            objectTwo[2] = getLevelBySignal(mSignalValue);
        }else{
            if(mWifiSsidValue != 0){
                objectTwo[0] = STATUS_SUCCESS;
                objectTwo[1] = "网络信号强度";
                objectTwo[2] = getLevelByRssi(mWifiSsidValue);
            }else{
                if(mNetType.equals("NR") || mNetType.equals("LTE")){ //5g 或 4g
                    objectTwo[0] = STATUS_SUCCESS;
                    objectTwo[1] = "网络信号强度";
                    objectTwo[2] = getLevelBySignal(mSignalValue);
                }else if(mNetType.equals("TD")){ //3g
                    objectTwo[0] = STATUS_SUCCESS;
                    objectTwo[1] = "网络信号强度";
                    objectTwo[2] = getLevelBySignal(mSignalValue);
                }else if(mNetType.equals("GSM")){ //2g
                    objectTwo[0] = STATUS_SUCCESS;
                    objectTwo[1] = "网络信号强度";
                    objectTwo[2] = getLevelBySignal(mSignalValue);
                }
            }
        }
    }

    private String getLevelByRssi(int rssi){
        if(rssi > -40){
            return "优良";
        }else if(rssi > -80 && rssi <= -40){
            return "良好";
        }else if(rssi > -110 && rssi <= -80){
            return "一般";
        }else if(rssi <= -110){
            return "较差";
        }

        return "极差";
    }

    private String getLevelBySignal(String signalValue){
        if(TextUtils.isEmpty(signalValue)){
            return "极差";
        }
        try{
            int dbm = Integer.parseInt(signalValue);
            if(dbm > -50){
                return "优良";
            }else if(dbm <= -50 && dbm > -70){
                return "良好";
            }else if(dbm <= -70 && dbm > -110){
                return "一般";
            }else if(dbm <= -110){
                return "较差";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "极差";
    }

    private String getLevelByPing(long pingTime){
        try{
            if(pingTime < 10 && pingTime >= 0){
                return "优良";
            }else if(pingTime < 30 && pingTime >= 10){
                return "良好";
            }else if(pingTime < 60 && pingTime >= 30){
                return "一般";
            }else if(pingTime >=60){
                return "较差";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "极差";
    }

    @Override
    public void onSignalListener(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj) {
        if (!isTestSim) {
            return;
        }
        switch (netType) {
            case CellUtil.NETWORK_NR:
                NrCell mNrCell = (NrCell) obj[0];
                if (mNrCell != null) {
                    mNetType = "NR";
                    mSignalValue = mNrCell.getRsrp();
                }
                break;
            case CellUtil.NETWORK_LTE:
                LTECell mLTECell = (LTECell) obj[0];
                if (mLTECell != null) {
                    if (mLTECell.enb * 256 + mLTECell.cellid < 0) {
                        return;
                    }
                    mNetType = "LTE";
                    mSignalValue = mLTECell.getRsrp();
                }
                break;
            case CellUtil.NETWORK_TD:
                TDCell mTDCell = (TDCell) obj[0];
                if (mTDCell != null) {
                    mNetType = "TD";
                    mSignalValue = mTDCell.getSign();
                }
                break;
            case CellUtil.NETWORK_GSM:
                GSMCell mGSMCell = (GSMCell) obj[0];
                if (mGSMCell != null) {
                    mNetType = "GSM";
                    mSignalValue = mGSMCell.getSign();
                }
                break;
            default:
                mNetType = "";
                mSignalValue = "";
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsTesting = false;
        if(mHandlerThread != null){
            mHandlerThread.quit();
        }
        SimSignalHandler.getInstances().removeListener(this, null);
    }

    @Override
    public void initStatistics() {
        installStatistics(R.string.code_network_diagnose);
    }

}
