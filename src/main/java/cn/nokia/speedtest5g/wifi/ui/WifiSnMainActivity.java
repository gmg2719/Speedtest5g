package cn.nokia.speedtest5g.wifi.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Locale;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.view.RoundProgressBar;
import cn.nokia.speedtest5g.wifi.adapter.WifiSnScanAdapter;
import cn.nokia.speedtest5g.wifi.bean.WifiArpBean;
import cn.nokia.speedtest5g.wifi.other.WifiUtil;

/**
 * wifi蹭网检测
 * @author JQJ
 *
 */
public class WifiSnMainActivity extends BaseActionBarActivity implements OnClickListener, OnItemClickListener{

    private final int STATUS_WIFI_STATE_DISABLED = 0; //wifi关闭
    private final int STATUS_WIFI_STATE_ENABLED = 1; //wifi打开

    private final int STATUS_INIT_OVER = 2; //初始化结束
    private final int STATUS_SCAN_START = 3; //开始扫描
    private final int STATUS_SCAN_FAIL = 4; //扫描失败
    private final int STATUS_SHOW_LAST_ONE = 5; //最后个设备展示

    private final int STATUS_SHOW_RESULT = 6; //展示结果

    private Handler mHandler = null;
    private WifiManager mWifiManager = null;

    private LinearLayout mLlNoWifiModule = null; //无wifi模块
    private LinearLayout mLlScanModule = null; //扫描模块
    private LinearLayout mLlResultModule = null; //结果模块
    private LinearLayout mLlMyModule = null;

    private Button mBtnOpenWifi = null;
    private Button mBtnRetry = null;
    private ListView mListView = null;
    private TextView mTvCount = null;
    private TextView mTvResultCount = null;
    private LinearLayout mLlInit = null;
    private TextView mTvInit = null;
    private TextView mTvSsid = null;
    private RoundProgressBar mRpbRate = null;

    private TextView mTvMyName = null; //设备主机名
    private TextView mTvMyIp = null; //ip
    private TextView mTvMyManufactor = null; //厂家
    private TextView mTvMyMac = null; //mac

    private String mHostName = null;
    private String mManufactor= null;
    private String mIp = null;
    private String mMac = null;

    private WifiSnScanAdapter mAdapter = null;
    private ArrayList<WifiArpBean> mArpBeanList = new ArrayList<WifiArpBean>();
    private WifiArpBean mWifiArpBean = null;
    private boolean mIsOver = false;
    private int mSize = 0; //总个数
    private boolean mTestOver = false; //测试是否结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_wifi_sn_main_activity);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        init("WiFi-蹭网检测", true);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        intHandler();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);

        if(!mWifiManager.isWifiEnabled()){
            updateUi(STATUS_WIFI_STATE_DISABLED);
        }
    }

    @SuppressLint("HandlerLeak")
    private void intHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == STATUS_INIT_OVER){ //初始化结束
                    readArp();
                }else if(msg.what == STATUS_SCAN_START){
                    mTvInit.setVisibility(View.GONE);
                    mLlInit.setVisibility(View.VISIBLE);
                    pingOneByOne(0); //开始测试
                }else if(msg.what == STATUS_SCAN_FAIL){ //显示自身
                    updateUi(STATUS_SHOW_RESULT);
//					showCommon("获取arp失败，请稍后再试！");
                }else if(msg.what == STATUS_SHOW_RESULT){
                    updateUi(STATUS_SHOW_RESULT);
                }else if(msg.what == EnumRequest.TASK_PING.toInt()){
                    PingInfo pingInfo = (PingInfo)msg.obj;
                    int index = msg.arg1;

                    WifiArpBean wifiArpBean = mArpBeanList.get(index);
                    wifiArpBean.pingTime = pingInfo.getTimes();
                    wifiArpBean.packetLoss = pingInfo.packetLoss;

                    index++;
                    int progress = (int)((index/(float)mSize) * 100);
                    mTvCount.setText(String.valueOf(index));
                    mRpbRate.setProgress(progress);

                    if(index >= mArpBeanList.size() && !mIsOver){
                        mIsOver = true;
                        mHandler.sendEmptyMessageDelayed(STATUS_SHOW_LAST_ONE, 700);
                    }else{
                        pingOneByOne(index);
                    }
                }else if(msg.what == STATUS_SHOW_LAST_ONE){
                    mTvCount.setText(String.valueOf(mSize + 1));
                    mHandler.sendEmptyMessageDelayed(STATUS_SHOW_RESULT, 300);
                }
            };
        };
    }

    private void updateUi(int status){
        if(status == STATUS_WIFI_STATE_ENABLED){
            mLlNoWifiModule.setVisibility(View.GONE);
            mLlScanModule.setVisibility(View.VISIBLE);
            mLlResultModule.setVisibility(View.GONE);

            startScan();
        }else if(status == STATUS_WIFI_STATE_DISABLED){
            mLlNoWifiModule.setVisibility(View.VISIBLE);
            mLlScanModule.setVisibility(View.GONE);
            mLlResultModule.setVisibility(View.GONE);
        }else if(status == STATUS_SHOW_RESULT){
            mLlNoWifiModule.setVisibility(View.GONE);
            mLlScanModule.setVisibility(View.GONE);
            mLlResultModule.setVisibility(View.VISIBLE);
            mLlMyModule.setVisibility(View.VISIBLE);

            mWifiArpBean = new WifiArpBean();
            mWifiArpBean.hostName = mHostName;
            mWifiArpBean.manufactor = mManufactor;
            mWifiArpBean.ip = mIp;
            mWifiArpBean.mac = mMac;

            mTvMyName.setText(mHostName);
            mTvMyManufactor.setText(mManufactor);
            mTvMyIp.setText(mIp);
            mTvMyMac.setText(mMac);

            mTvResultCount.setText(String.valueOf(mSize + 1)); //加上自己本身设备
            mAdapter.setData(mArpBeanList);
        }
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);

        mLlScanModule = (LinearLayout)findViewById(R.id.wifi_sn_main_ll_scan_module);
        mLlNoWifiModule = (LinearLayout)findViewById(R.id.wifi_sn_main_ll_no_wifi_module);
        mLlResultModule = (LinearLayout)findViewById(R.id.wifi_sn_main_ll_result_module);
        mLlMyModule = (LinearLayout)findViewById(R.id.wifi_sn_main_ll_my_module);
        mBtnOpenWifi = (Button)findViewById(R.id.wifi_sn_main_btn_openwifi);
        mBtnRetry = (Button)findViewById(R.id.wifi_sn_main_btn_retry);
        mLlMyModule.setOnClickListener(this);
        mBtnOpenWifi.setOnClickListener(this);
        mBtnRetry.setOnClickListener(this);

        mListView = (ListView)findViewById(R.id.wifi_sn_main_lv_content);
        mListView.setEmptyView(findViewById(R.id.wifi_sn_main_tv_nodata));
        mListView.setOnItemClickListener(this);
        mAdapter = new WifiSnScanAdapter(this);
        mListView.setAdapter(mAdapter);

        mTvMyName = (TextView)findViewById(R.id.wifi_sn_main_result_tv_name);
        mTvMyIp = (TextView)findViewById(R.id.wifi_sn_main_result_tv_ip);
        mTvMyManufactor = (TextView)findViewById(R.id.wifi_sn_main_result_tv_manufactor);
        mTvMyMac = (TextView)findViewById(R.id.wifi_sn_main_result_tv_mac);

        mLlInit = (LinearLayout)findViewById(R.id.wifi_sn_main_ll_init);
        mTvInit = (TextView)findViewById(R.id.wifi_sn_main_tv_init);
        mTvCount = (TextView)findViewById(R.id.wifi_sn_main_tv_count);
        mTvResultCount = (TextView)findViewById(R.id.wifi_sn_main_result_tv_count);
        mTvSsid = (TextView)findViewById(R.id.wifi_sn_main_tv_ssid);
        mRpbRate = (RoundProgressBar)findViewById(R.id.wifi_sn_main_rpb_progressbar);
        mRpbRate.setProgress(0);

        if (Build.VERSION.SDK_INT >= 26 && !GPSUtil.getInstances().isOpen(this)){
            new CommonDialog(this).setListener(new ListenerBack() {

                @Override
                public void onListener(int type, Object object, boolean isTrue) {
                    if (isTrue){
                        GPSUtil.getInstances().open(WifiSnMainActivity.this);
                    }
                }
            }).setButtonText("设置","取消").show("因Android8.0系统及以上，若GPS没开启，则无法获取WiFi数据？");
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
                            updateUi(STATUS_WIFI_STATE_ENABLED);
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            updateUi(STATUS_WIFI_STATE_DISABLED);
                            break;
                    }
                }
            }
        }
    };

    /**
     * 开始扫描
     */
    private void startScan(){
        if (mWifiManager.isWifiEnabled()) {
            mIsOver = false;
            mSize = 0;
            if(mWifiManager != null){
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo != null){
                    String wifiName = WifiUtil.getInstance().getName(wifiInfo.getSSID());
                    mHostName = android.os.Build.MODEL;
                    mManufactor = android.os.Build.BRAND;
                    mIp = WifiUtil.getInstance().getHostIP();
                    mMac = wifiInfo.getBSSID();
                    mTvSsid.setText(wifiName);
                }
            }

            mTvInit.setVisibility(View.VISIBLE);
            mLlInit.setVisibility(View.GONE);
            mTvCount.setText("0");
            mRpbRate.setProgress(0);

            sendDataToLoacl();
            //隔5秒读取rap
            mHandler.sendEmptyMessageDelayed(STATUS_INIT_OVER, 5000);
        }
    }

    /**
     * 开始测试  一个接一个ping
     */
    private void pingOneByOne(final int index){
        new Thread(){
            @Override
            public void run() {
                try {
                    if(mTestOver){
                        return;
                    }
                    if(index < mArpBeanList.size()){
                        PingInfo pingInfo = new PingInfo();
                        WifiArpBean wifiArpBean = mArpBeanList.get(index);
                        pingInfo.setIp(wifiArpBean.ip);
                        wifiArpBean.manufactor = WifiUtil.getInstance().getManufactor(wifiArpBean.mac);

                        PingThread pingThread  = new PingThread(pingInfo, index, mHandler, 2, 2);
                        pingThread.start();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            };
        }.start();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.wifi_sn_main_btn_openwifi){
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }else if(v.getId() == R.id.wifi_sn_main_btn_retry){
            mIsOver = false;
            mSize = 0;
            updateUi(STATUS_WIFI_STATE_ENABLED);
        }else if(v.getId() == R.id.wifi_sn_main_ll_my_module){
            Intent intent = new Intent(WifiSnMainActivity.this, WifiSnDetailsActivity.class);
            intent.putExtra("WifiArpBean", mWifiArpBean);
            intent.putExtra("IsFrom", false);
            WifiSnMainActivity.this.startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if(mAdapter != null){
            WifiArpBean wifiArpBean = (WifiArpBean)mAdapter.getItem(position);
            if(wifiArpBean != null){
                Intent intent = new Intent(WifiSnMainActivity.this, WifiSnDetailsActivity.class);
                intent.putExtra("WifiArpBean", wifiArpBean);
                intent.putExtra("IsFrom", true);
                WifiSnMainActivity.this.startActivity(intent);
            }
        }
    }

    private void sendDataToLoacl() {
        //创建线程池
        //        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取本机所在的局域网地址
                    String hostIP = null;
                    int count = 0;
                    while(true){
                        hostIP = WifiUtil.getInstance().getHostIP();
                        if(TextUtils.isEmpty(hostIP)){
                            count++;
                            if(count >= 9){
                                break;
                            }
                            SystemClock.sleep(500);
                        }else{
                            break;
                        }
                    }
                    if(!TextUtils.isEmpty(hostIP)){
                        int lastIndexOf = hostIP.lastIndexOf(".");
                        String substring = hostIP.substring(0, lastIndexOf + 1);

                        DatagramPacket dp = new DatagramPacket(new byte[0], 0, 0);
                        DatagramSocket socket;

                        socket = new DatagramSocket();
                        int position = 1;
                        while (position < 255) {
                            //							Log.e("Scanner ", "run: udp-" + substring + position);
                            dp.setAddress(InetAddress.getByName(substring + String.valueOf(position)));
                            socket.send(dp);
                            position++;
                            if (position == 125) {//分两段掉包，一次性发的话，达到236左右，会耗时3秒左右再往下发
                                socket.close();
                                socket = new DatagramSocket();
                            }
                        }
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void readArp() {
        new Thread(){
            @Override
            public void run() {
                try {
                    mArpBeanList.clear();
                    //android 10 以上 无法读取
                    BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line = "";
                    String ip = "";
                    String flag = "";
                    String mac = "";
                    if (br.readLine() == null) {
                        Log.e("scanner", "readArp: null");
                    }
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.length() < 63) continue;
                        if (line.toUpperCase(Locale.US).contains("IP")){
                            continue;
                        }
                        ip = line.substring(0, 17).trim();
                        flag = line.substring(29, 32).trim();
                        mac = line.substring(41, 63).trim();
                        if (mac.contains("00:00:00:00:00:00")){
                            continue;
                        }
                        if(flag.contains("0x0")){
                            continue;
                        }
                        //				Log.e("scanner", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);
                        WifiArpBean arpBean = new WifiArpBean();
                        arpBean.ip = ip;
                        arpBean.mac = mac;
                        mArpBeanList.add(arpBean);
                    }
                    br.close();

                    mSize = mArpBeanList.size();

                    getHostNameInThread();
                    mHandler.sendEmptyMessage(STATUS_SCAN_START);
                } catch (Exception ignored) {
                    mHandler.sendEmptyMessage(STATUS_SCAN_FAIL);
                }
            };
        }.start();
    }

    private void getHostNameInThread(){
        for(final WifiArpBean wifiArpBean : mArpBeanList){
            new Thread(){
                @Override
                public void run() {
                    WifiUtil.getInstance().getHostName(wifiArpBean);
                };
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        try{
            mTestOver = true;
            unregisterReceiver(mWifiReceiver);
        }catch (Exception e){
        }
        super.onDestroy();
    }

    @Override
    public void initStatistics() {
        installStatistics(R.string.code_wifi_sn);
    }
}
