package cn.nokia.speedtest5g.wifi.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.view.RadarView;
import cn.nokia.speedtest5g.wifi.adapter.WifiSdScanAdapter;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

/**
 * wifi信号检测
 * @author JQJ
 *
 */
public class WifiSdMainActivity extends BaseActionBarActivity implements OnClickListener, OnItemClickListener{

    private final int STATUS_WIFI_STATE_DISABLED = 0; //wifi关闭
    private final int STATUS_WIFI_STATE_ENABLED = 1; //wifi打开
    private final int STATUS_SCAN_OVER = 2; //扫描结束
    private final int STATUS_SHOW_RESULT = 3; //展示结果

    private final int STATUS_TIME_OUT = 4; //超时

    private long UPDATE_TIME = 2000;
    private Handler mHandler = null;
    private WifiManager mWifiManager = null;

    private LinearLayout mLlNoWifiModule = null; //无wifi模块
    private LinearLayout mLlScanModule = null; //扫描模块
    private LinearLayout mLlResultModule = null; //结果模块
    private LinearLayout mLlMyModule = null;
    private Button mBtnOpenWifi = null;
    private Button mBtnRetry = null;
    private ListView mListView = null;

    private TextView mTvName = null;
    private TextView mTvCount = null;
    private TextView mTvDbm = null;
    private ProgressBar mPbValue = null;
    private RadarView mRadarView = null;

    private String mWifiName; //连接wifi ssid
    private int mWifiDbm; //dbm
    private WifiSdScanAdapter mAdapter = null;
    private ArrayList<ScanResult> mScanResultList = new ArrayList<ScanResult>();
    private ScanRunnable mRunnable = null;
    private long mTime = 5000; //至少等待5S 获取结果
    private boolean mIsTimeOut = false; //是否超时
    private boolean mIsScanOver = false; //是否扫描结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_wifi_sd_main_activity);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        init("WiFi-信号检测", true);

        intHandler();
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiReceiver, filter);

        if(!mWifiManager.isWifiEnabled()){
            updateUi(STATUS_WIFI_STATE_DISABLED, true);
        }
    }

    @SuppressLint("HandlerLeak")
    private void intHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == STATUS_SHOW_RESULT){
                    updateUi(STATUS_SHOW_RESULT, (boolean)msg.obj);
                }else if(msg.what == STATUS_SCAN_OVER){
                    stopUpdate();
                    startUpdate();
                }else if(msg.what == STATUS_TIME_OUT){ //超时
                    mIsTimeOut = true;
                    if(mIsScanOver){
                        mHandler.sendEmptyMessage(STATUS_SCAN_OVER);
                    }
                }
            };
        };
    }

    private void updateUi(int status, boolean isHasConnect){
        if(status == STATUS_WIFI_STATE_ENABLED){
            if(mRunnable != null){
                mRunnable.mIsStop = false;
            }

            mRadarView.b();
            mLlNoWifiModule.setVisibility(View.GONE);
            mLlScanModule.setVisibility(View.VISIBLE);
            mLlResultModule.setVisibility(View.GONE);
        }else if(status == STATUS_WIFI_STATE_DISABLED){
            if(mRunnable != null){
                mRunnable.mIsStop = true;
            }

            mRadarView.c();
            mLlNoWifiModule.setVisibility(View.VISIBLE);
            mLlScanModule.setVisibility(View.GONE);
            mLlResultModule.setVisibility(View.GONE);
        }else if(status == STATUS_SHOW_RESULT){
            mLlNoWifiModule.setVisibility(View.GONE);
            mLlScanModule.setVisibility(View.GONE);
            mLlResultModule.setVisibility(View.VISIBLE);
            if(isHasConnect){
                mLlMyModule.setVisibility(View.VISIBLE);
            }else{
                mLlMyModule.setVisibility(View.GONE);
            }

            mRadarView.c();
            mTvCount.setText(String.valueOf(mScanResultList.size()));
            mTvName.setText(mWifiName);
            mTvDbm.setText(String.valueOf(mWifiDbm) + "dBm");
            mPbValue.setProgress(WifiUtil.getInstance().getProgressValue(mWifiDbm));

            mAdapter.setData(mScanResultList);
        }
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);

        mRadarView = (RadarView)findViewById(R.id.wifi_sd_main_radar);

        mLlScanModule = (LinearLayout)findViewById(R.id.wifi_sd_main_ll_scan_module);
        mLlNoWifiModule = (LinearLayout)findViewById(R.id.wifi_sd_main_ll_no_wifi_module);
        mLlResultModule = (LinearLayout)findViewById(R.id.wifi_sd_main_ll_result_module);
        mLlMyModule = (LinearLayout)findViewById(R.id.wifi_sd_main_ll_my_module);
        mBtnOpenWifi = (Button)findViewById(R.id.wifi_sd_main_btn_openwifi);
        mBtnRetry = (Button)findViewById(R.id.wifi_sd_main_btn_retry);
        mLlMyModule.setOnClickListener(this);
        mBtnOpenWifi.setOnClickListener(this);
        mBtnRetry.setOnClickListener(this);

        mListView = (ListView)findViewById(R.id.wifi_sd_main_lv_content);
        mListView.setEmptyView(findViewById(R.id.wifi_sd_main_tv_nodata));
        mListView.setOnItemClickListener(this);
        mAdapter = new WifiSdScanAdapter(this);
        mListView.setAdapter(mAdapter);

        mTvCount = (TextView)findViewById(R.id.wifi_sd_main_tv_count);
        mTvName = (TextView)findViewById(R.id.wifi_sd_main_tv_name);
        mTvDbm = (TextView)findViewById(R.id.wifi_sd_main_tv_dbm);
        mPbValue = (ProgressBar)findViewById(R.id.wifi_sd_main_pb_value);

        if (Build.VERSION.SDK_INT >= 26 && !GPSUtil.getInstances().isOpen(this)){
            new CommonDialog(this).setListener(new ListenerBack() {

                @Override
                public void onListener(int type, Object object, boolean isTrue) {
                    if (isTrue){
                        GPSUtil.getInstances().open(WifiSdMainActivity.this);
                    }
                }
            }).setButtonText("设置","取消").show("Android8.0系统及以上，若GPS没开启，无法获取WiFi数据！");
        }
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null){
                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_ENABLED:
                            startScan();
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            updateUi(STATUS_WIFI_STATE_DISABLED, true);
                            break;
                    }
                }else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())){
                    mIsScanOver = true;
                    if(mIsTimeOut){
                        mHandler.sendEmptyMessage(STATUS_SCAN_OVER);
                    }
                }
            }
        }
    };

    private class ScanRunnable implements Runnable{

        public boolean mIsStop = false;

        @Override
        public void run() {
            try{
                while(!mIsStop){
                    boolean isHasConnect = false;
                    if(!mIsStop){
                        mScanResultList.clear();
                        List<ScanResult> list = WifiUtil.getInstance().getWifiList(mWifiManager);

                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo(); //获取已连接wifi信息
                        if (wifiInfo != null){
                            if (TextUtils.isEmpty(wifiInfo.getSSID()) || "<unknown ssid>".equals(wifiInfo.getSSID()) || "0x".equals(wifiInfo.getSSID())){
                            }else{
                                isHasConnect = true;
                                mWifiName = WifiUtil.getInstance().getName(wifiInfo.getSSID());
                                mWifiDbm = wifiInfo.getRssi();
                            }
                        }else{
                            isHasConnect = false;
                        }

                        //排除掉已连接的
                        ArrayList<ScanResult> tempList = new ArrayList<ScanResult>();
                        for(ScanResult scanResult : list){
                            if(WifiUtil.getInstance().getName(scanResult.SSID).equals(mWifiName)){
                                tempList.add(scanResult);
                            }
                        }
                        for(ScanResult scanResult : tempList){
                            list.remove(scanResult);
                        }
                        mScanResultList.addAll(list);
                    }

                    if(!mIsStop){
                        Message msg = mHandler.obtainMessage();
                        msg.what = STATUS_SHOW_RESULT;
                        msg.obj = isHasConnect;
                        msg.sendToTarget();
                    }

                    SystemClock.sleep(UPDATE_TIME);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始扫描
     */
    private void startScan(){
        if (mWifiManager.isWifiEnabled()) {
            mIsTimeOut = false;
            mIsScanOver = false;
            updateUi(STATUS_WIFI_STATE_ENABLED, true);
            stopUpdate();
            boolean isSucceeded = mWifiManager.startScan(); //调用扫描
            Log.e("WifiSdMainActivity", "mWifiManager.startScan():" + isSucceeded);
            if(!isSucceeded){ //调用失败 则隔5秒获取数据
                mHandler.sendEmptyMessageDelayed(STATUS_SCAN_OVER, mTime);
            }else{
                mHandler.sendEmptyMessageDelayed(STATUS_TIME_OUT, mTime);
            }
        }
    }

    /**
     * 开始更新
     */
    private void startUpdate(){
        mRunnable = new ScanRunnable();
        new Thread(mRunnable).start();
    }

    /**
     * 停止更新
     */
    private void stopUpdate(){
        if(mRunnable != null){
            mRunnable.mIsStop = true;
            mRunnable = null;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.wifi_sd_main_btn_openwifi){
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }else if(v.getId() == R.id.wifi_sd_main_btn_retry){
            startScan();
        }else if(v.getId() == R.id.wifi_sd_main_ll_my_module){
            Intent intent = new Intent(WifiSdMainActivity.this, WifiSdDetailsActivity.class);
            WifiSdMainActivity.this.startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(mAdapter != null){
            ScanResult scanResult = (ScanResult)mAdapter.getItem(position);
            if(scanResult != null){
                Intent intent = new Intent(WifiSdMainActivity.this, WifiSdDetailsActivity.class);
                intent.putExtra("ScanResult", scanResult);
                WifiSdMainActivity.this.startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        try{
            stopUpdate();
            unregisterReceiver(mWifiReceiver);
        }catch (Exception e){
        }
        super.onDestroy();
    }

    @Override
    public void initStatistics() {
        installStatistics(R.string.code_wifi_sd);
    }
}
