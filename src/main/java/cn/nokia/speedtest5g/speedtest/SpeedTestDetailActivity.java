package cn.nokia.speedtest5g.speedtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.MarqueesTextView;
import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.ModeClickHandler;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.PoiSearchGetUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanNpConfig;
import cn.nokia.speedtest5g.speedtest.bean.RequestRemoveLog;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedTestDetail;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedTestDetail;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDetailChartView;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.view.SpeedTestCurveView;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 速测详情
 * @author JQJ
 *
 */
public class SpeedTestDetailActivity extends BaseActionBarActivity implements OnClickListener{

    public static final String ON_UPDATE_REMARK = "cn.nokia.speedtest5g.speedtest.detail.update.remark";

    private TextView mTvTime = null; //时间
    private TextView mTvRemark = null; //备注
    private TextView mTvIp = null; //IP
    private TextView mTvAddress = null; //地址
    private TextView mTvTitle1 = null;
    private TextView mTvTitle2 = null;
    private TextView mTvAvgDownload = null;//平均下载
    private TextView mTvAvgUpload = null;//平均上传
    private TextView mTvRealDownload = null;//消耗下载流量
    private TextView mTvRealUpload = null;//消耗上传流量
    private ProgressBar mPbYxcsValue = null;//游戏测速
    private ProgressBar mPbSpcsValue = null;//视频测速
    private ProgressBar mPbZxcsValue = null;//资讯测速
    private ProgressBar mPbDscsValue = null;//电商测速
    private TextView mTvYxcsValue = null;//游戏测速
    private TextView mTvSpcsValue = null;//视频测速
    private TextView mTvZxcsValue = null;//资讯测速
    private TextView mTvDscsValue = null;//电商测速
    private LinearLayout mLlYxcs = null;//游戏测速
    private LinearLayout mLlSpcs = null;//视频测速
    private LinearLayout mLlZxcs = null;//资讯测速
    private LinearLayout mLlDscs = null;//电商测速

    private FrameLayout mLlDetailsDlModule = null;
    private FrameLayout mLlDetailsUlModule = null;

    private LinearLayout mLlNoLoginModule = null;//未登陆模块
    private LinearLayout mLlLoginModule = null;//登陆模块

    private TextView mTvNoLoginPingTime = null;//未登陆
    private TextView mTvNoLoginPingShake = null;
    private TextView mTvNoLoginPacketLoss = null;
    private TextView mTvLoginPingTime = null;//登陆
    private TextView mTvLoginPingShake = null;
    private TextView mTvLoginPacketLoss = null;

    //登陆后模块控件
    private TextView mTvSpeedDownAvg = null; //下载平均
    private TextView mTvSpeedDownMax = null; //下载最大
    private TextView mTvSpeedUpAvg = null;//上传平均
    private TextView mTvSpeedUpMax = null;//上传最大
    //切换按钮-下载，上传
    private TextView mTvSwitchDown,mTvSwitchUpload = null;
    private MarqueesTextView mTvPlayBackCellName = null;
    // LTE信号
    private TableLayout mTlLte = null;
    private TextView mTvPlayBackTac = null;
    private TextView mTvPlayBackPci = null;
    private TextView mTvPlayBackCGI = null;
    private TextView mTvPlayBackENB = null;
    private TextView mTvPlayBackCELLID = null;
    private TextView mTvPlayBackPindian = null;
    private TextView mTvPlayBackRSRP = null;
    private TextView mTvPlayBackSINR = null;
    // GSM信号
    private TableLayout mTlGsm = null;
    private TextView mTvPlayBackTacGSM = null;
    private TextView mTvPlayBackCELLIDGSM = null;
    private TextView mTvPlayBackBcchPDGSM = null;
    private TextView mTvPlayBackRXLGSM = null;

    private View mViewDownUpFlag = null;//下载还是上传
    private TextView mTvType = null; //下载还是上传
    private TextView mTvData = null;//数值

    //锚点提示布局,NR布局,NR提示布局
    private View mViewMdHint,mViewNrHint,mViewNr;
    //NR信号-PCI,EARFCN,BAND,RSRP,SINR
    private TextView mTvPciNr,mTvEarfcnNr,mTvBandNr,mTvRsrpNr,mTvSinrNr;
    //曲线标记图标-SINR,RSRP,SINR_NR,RSRP_NR
    private View mViewIconSinr,mViewIconRsrp,mViewIconSinrNr,mViewIconRsrpNr;
    private View mViewDownUpColor = null;
    private TextView mTvDownUp = null;
    private FrameLayout mFlSpeedView = null;
    private SpeedTestDetailChartView mCharView = null;
    //SINR和RSRP指标切换按钮
    private Button mBtnSwitchSinr,mBtnSwitchRsrp;
    //指标按钮选择-SINR,RSRP,SINR-NR,RSRP-NR
    private CheckBox mCkValueSinr,mCkValueRsrp,mCkValueSinrNr,mCkValueRsrpNr;
    private ImageButton mIbSettings = null;
    private LinearLayout mLlModule1 = null;

    private List<Signal> mSignalTestDownList = new ArrayList<Signal>();
    private List<Signal> mSignalTestUploadList = new ArrayList<Signal>();
    private Db_JJ_FTPTestInfo mFtpTestInfo = null;
    private boolean mFromWhere = true; //false 测速完成后详情跳转或 pk明细跳转     true 历史记录列表跳转
    private String mLoginType = null;
    private boolean mIsDownload = true;
    private boolean mIsInitDownload;
    private boolean mIsInitUpload;
    private String mShareCode = "";
    private OnUpdateRemarkReceiver mOnUpdateRemarkReceiver = new OnUpdateRemarkReceiver();
    private int mCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_speed_test_detail_activity);
        mBgTopColor = R.color.bm_bg_color;
        mBgParentColor = R.color.bm_bg_color;
        mTitleTextColor = R.color.black_small;

        ModeClickHandler.getInstances(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                //为测试详情 ping intent获取
                ModeClickHandler.getInstances(SpeedTestDetailActivity.this).initModularAllData();
            }
        }).start();

        mLoginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");

        init("测试详情", true);

        initView();

        if("Phone".equals(mLoginType)){ //已登录
            mLlLoginModule.setVisibility(View.VISIBLE);
            mLlNoLoginModule.setVisibility(View.GONE);
        }else{
            mLlLoginModule.setVisibility(View.GONE);
            mLlNoLoginModule.setVisibility(View.VISIBLE);
        }

        mFtpTestInfo = (Db_JJ_FTPTestInfo) getIntent().getSerializableExtra("mFtpTestInfo");
        mFromWhere = getIntent().getBooleanExtra("fromWhere", true);

        if(mFromWhere){
            actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_speed_test_share_flag, EnumRequest.MENU_SELECT_TWO.toInt(), ""));
            actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_speed_test_delete_flag, EnumRequest.MENU_SELECT_ONE.toInt(), ""));
        }else{
            if(TextUtils.isEmpty(mFtpTestInfo.getTestEnd())){ //从PK明细跳转过来的
                mLlModule1.setVisibility(View.GONE);
            }
            mTvRemark.setVisibility(View.GONE);
        }

        if (mFtpTestInfo != null) {
            mSignalTestDownList = mFtpTestInfo.downloadTestList;
            mSignalTestUploadList = mFtpTestInfo.uploadTestList;
            filterData();

            loadDataBaseInfo();
            //加载ping
            loadPing();

            //两个数据都没有 就请求
            if((mSignalTestDownList == null && mSignalTestUploadList == null) ||
                    (mSignalTestDownList.size() == 0 && mSignalTestUploadList.size() == 0)){
                requestSpeedTestLogDetail();
            } else {
                loadOtherData();
            }
        } else {
            showCommon("数据不存在");
            finish();
        }

        registerReceiver(mOnUpdateRemarkReceiver, new IntentFilter(ON_UPDATE_REMARK));
    }

    /**
     * 点数超过10个处理  只留10个
     */
    private void filterData(){
        try{
            if(mSignalTestDownList != null){
                if(mSignalTestDownList.size() > mCount){
                    ArrayList<Signal> tempDownList = new ArrayList<Signal>();
                    for(int i = 0; i < mCount; i++){
                        tempDownList.add(mSignalTestDownList.get(i));
                    }
                    mSignalTestDownList.clear();
                    mSignalTestDownList.addAll(tempDownList);
                }
            }

            if(mSignalTestUploadList != null){
                if(mSignalTestUploadList.size() > mCount){
                    ArrayList<Signal> tempUpList = new ArrayList<Signal>();
                    for(int i = 0; i < mCount; i++){
                        tempUpList.add(mSignalTestUploadList.get(i));
                    }
                    mSignalTestUploadList.clear();
                    mSignalTestUploadList.addAll(tempUpList);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新备注
     * @author JQJ
     *
     */
    private class OnUpdateRemarkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if(intent.getAction().equals(ON_UPDATE_REMARK)){
                String remark = intent.getStringExtra("REMARK");
                mTvRemark.setText(remark);
            }
        }
    }

    private void initView(){
        mTvTime = (TextView)findViewById(R.id.speed_test_detail_tv_time);
        mTvRemark = (TextView)findViewById(R.id.speed_test_detail_tv_remark);
        mTvIp = (TextView)findViewById(R.id.speed_test_detail_tv_ip);
        mTvAddress = (TextView)findViewById(R.id.speed_test_detail_tv_address);
        mTvTitle1 = (TextView)findViewById(R.id.speed_test_detail_tv_title1);
        mTvTitle2 = (TextView)findViewById(R.id.speed_test_detail_tv_title2);
        mTvAvgDownload = (TextView)findViewById(R.id.speed_test_detail_tv_avg_download);
        mTvAvgUpload = (TextView)findViewById(R.id.speed_test_detail_tv_avg_upload);
        mTvRealDownload = (TextView)findViewById(R.id.speed_test_detail_tv_all_download);
        mTvRealUpload = (TextView)findViewById(R.id.speed_test_detail_tv_all_upload);
        mTvYxcsValue = (TextView)findViewById(R.id.speed_test_detail_tv_yxcs);
        mTvSpcsValue = (TextView)findViewById(R.id.speed_test_detail_tv_spcs);
        mTvZxcsValue = (TextView)findViewById(R.id.speed_test_detail_tv_zxcs);
        mTvDscsValue = (TextView)findViewById(R.id.speed_test_detail_tv_dscs);
        mPbYxcsValue = (ProgressBar)findViewById(R.id.speed_test_detail_pb_yxcs);
        mPbSpcsValue = (ProgressBar)findViewById(R.id.speed_test_detail_pb_spcs);
        mPbZxcsValue = (ProgressBar)findViewById(R.id.speed_test_detail_pb_zxcs);
        mPbDscsValue = (ProgressBar)findViewById(R.id.speed_test_detail_pb_dscs);
        mLlYxcs = (LinearLayout)findViewById(R.id.speed_test_detail_ll_yxcs);
        mLlSpcs = (LinearLayout)findViewById(R.id.speed_test_detail_ll_spcs);
        mLlZxcs = (LinearLayout)findViewById(R.id.speed_test_detail_ll_zxcs);
        mLlDscs = (LinearLayout)findViewById(R.id.speed_test_detail_ll_dscs);

        mLlModule1 = (LinearLayout)findViewById(R.id.speed_test_detail_ll_module_1);

        mLlLoginModule = (LinearLayout)findViewById(R.id.speed_test_detail_ll_login_module);
        mLlNoLoginModule = (LinearLayout)findViewById(R.id.speed_test_detail_ll_nologin_module);

        mTvNoLoginPingTime = (TextView) findViewById(R.id.speed_test_detail_tv_ping);
        mTvNoLoginPingShake = (TextView) findViewById(R.id.speed_test_detail_tv_shake);
        mTvNoLoginPacketLoss = (TextView) findViewById(R.id.speed_test_detail_tv_packetloss);
        mTvLoginPingTime = (TextView) findViewById(R.id.speed_test_detail_login_tv_ping);
        mTvLoginPingShake = (TextView) findViewById(R.id.speed_test_detail_login_tv_shake);
        mTvLoginPacketLoss = (TextView) findViewById(R.id.speed_test_detail_login_tv_packetloss);

        mLlDetailsDlModule = (FrameLayout)findViewById(R.id.speed_test_detail_fl_download);
        mLlDetailsUlModule = (FrameLayout)findViewById(R.id.speed_test_detail_fl_upload);

        //登陆模块控件
        mTvSpeedDownAvg   = (TextView) findViewById(R.id.speed_test_detail_login_tv_avg_download);
        mTvSpeedDownMax   = (TextView) findViewById(R.id.speed_test_detail_login_tv_max_download);
        mTvSpeedUpAvg 	 = (TextView) findViewById(R.id.speed_test_detail_login_tv_avg_upload);
        mTvSpeedUpMax 	 = (TextView) findViewById(R.id.speed_test_detail_login_tv_max_upload);
        mTvSwitchDown   = (TextView) findViewById(R.id.speed_test_detail_login_btn_switch_download);
        mTvSwitchUpload = (TextView) findViewById(R.id.speed_test_detail_login_btn_switch_upload);
        mTvPlayBackCellName = (MarqueesTextView) findViewById(R.id.speed_test_detail_login_tv_playCellName);
        mTlLte = (TableLayout) findViewById(R.id.speed_test_detail_login_tl_lte);
        mTvPlayBackTac = (TextView) findViewById(R.id.speed_test_detail_login_tv_tac);
        mTvPlayBackPci = (TextView) findViewById(R.id.speed_test_detail_login_tv_pci);
        mTvPlayBackCGI = (TextView) findViewById(R.id.speed_test_detail_login_tv_cgi);
        mTvPlayBackENB = (TextView) findViewById(R.id.speed_test_detail_login_tv_enb);
        mTvPlayBackCELLID = (TextView) findViewById(R.id.speed_test_detail_login_tv_cellid);
        mTvPlayBackPindian = (TextView) findViewById(R.id.speed_test_detail_login_tv_pingdian);
        mTvPlayBackRSRP = (TextView) findViewById(R.id.speed_test_detail_login_tv_rsrp);
        mTvPlayBackSINR = (TextView) findViewById(R.id.speed_test_detail_login_tv_sinr);
        mTlGsm = (TableLayout) findViewById(R.id.speed_test_detail_login_tl_gsm);
        mTvPlayBackTacGSM = (TextView) findViewById(R.id.speed_test_detail_login_tv_tac_gsm);
        mTvPlayBackCELLIDGSM = (TextView) findViewById(R.id.speed_test_detail_login_tv_cellid_gsm);
        mTvPlayBackBcchPDGSM = (TextView) findViewById(R.id.speed_test_detail_login_tv_bcchPD_gsm);
        mTvPlayBackRXLGSM = (TextView) findViewById(R.id.speed_test_detail_login_tv_rxl_gsm);

        mViewDownUpFlag = findViewById(R.id.speed_test_detail_login_view_downup_flag);
        mTvType = (TextView) findViewById(R.id.speed_test_detail_login_tv_type);
        mTvData = (TextView) findViewById(R.id.speed_test_detail_login_tv_data);

        mViewMdHint = findViewById(R.id.speed_test_detail_login_tv_infoMd);
        mViewNrHint = findViewById(R.id.speed_test_detail_login_tv_infoNr);
        mViewNr		= findViewById(R.id.speed_test_detail_login_tl_nr);
        mTvPciNr	= (TextView) findViewById(R.id.speed_test_detail_login_tv_pciNr);
        mTvEarfcnNr = (TextView) findViewById(R.id.speed_test_detail_login_tv_earfcnNr);
        mTvBandNr	= (TextView) findViewById(R.id.speed_test_detail_login_tv_bandNr);
        mTvRsrpNr	= (TextView) findViewById(R.id.speed_test_detail_login_tv_rsrpNr);
        mTvSinrNr	= (TextView) findViewById(R.id.speed_test_detail_login_tv_sinrNr);

        mViewDownUpColor 	 = findViewById(R.id.speed_test_detail_login_view_downup);
        mTvDownUp 			 = (TextView) findViewById(R.id.speed_test_detail_login_tv_downup);
        mBtnSwitchSinr	   				 = (Button) findViewById(R.id.speed_test_detail_login_btn_switchSinr);
        mBtnSwitchRsrp	   				 = (Button) findViewById(R.id.speed_test_detail_login_btn_switchRsrp);
        mFlSpeedView 					 = (FrameLayout) findViewById(R.id.speed_test_detail_login_fl_speedview);
        mCkValueSinr					 = (CheckBox) findViewById(R.id.speed_test_detail_login_ck_valueSinr);
        mCkValueRsrp					 = (CheckBox) findViewById(R.id.speed_test_detail_login_ck_valueRsrp);
        mCkValueSinrNr					 = (CheckBox) findViewById(R.id.speed_test_detail_login_ck_valueSinrNr);
        mCkValueRsrpNr					 = (CheckBox) findViewById(R.id.speed_test_detail_login_ck_valueRsrpNr);
        mViewIconSinr					 = findViewById(R.id.speed_test_detail_login_view_iconSinr);
        mViewIconRsrp					 = findViewById(R.id.speed_test_detail_login_view_iconRsrp);
        mViewIconSinrNr					 = findViewById(R.id.speed_test_detail_login_view_iconSinrNr);
        mViewIconRsrpNr					 = findViewById(R.id.speed_test_detail_login_view_iconRsrpNr);
        mIbSettings 					 = (ImageButton)findViewById(R.id.speed_test_detail_login_ibtn_selectSet);

        mCharView = new SpeedTestDetailChartView(mActivity, 10, this);
        mFlSpeedView.addView(mCharView);// 初始化曲线图

        mTvRemark.setOnClickListener(this);
        mLlYxcs.setOnClickListener(this);
        mLlSpcs.setOnClickListener(this);
        mLlZxcs.setOnClickListener(this);
        mLlDscs.setOnClickListener(this);

        mTvSwitchDown.setOnClickListener(this);
        mTvSwitchUpload.setOnClickListener(this);
        mBtnSwitchSinr.setOnClickListener(this);
        mBtnSwitchRsrp.setOnClickListener(this);
        mIbSettings.setOnClickListener(this);
    }

    private void loadOtherData(){
        if("Phone".equals(mLoginType)){
            loadLoginData();
        }else{
            //加载曲线
            loadCurve();
        }
    }

    /**
     * 加载登陆模块数据
     */
    private void loadLoginData(){
        if ((mSignalTestDownList == null || mSignalTestDownList.size() == 0) &&
                (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0)) { //无下载数据且有上传数据
            initCharView(false);
        } else {
            initCharView(true);
        }
    }

    private void initCharView(boolean isDown) {
        mIsDownload = isDown;
        if (isDown) {
            if (mSignalTestDownList != null && mSignalTestDownList.size() > 0) {
                for (int i = 0; i < mSignalTestDownList.size(); i++) {
                    Signal signal = mSignalTestDownList.get(i);
                    mCharView.addValue(signal, signal.rate, isDown);
                    if (i == 0) {
                        setSignalDataPlayBack(signal, i);
                    }
                }
                mCharView.setTestedDownOrUpData(mSignalTestDownList, isDown);
            }
            mIsInitDownload = true;
        } else {
            if (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {
                for (int i = 0; i < mSignalTestUploadList.size(); i++) {
                    Signal signal = mSignalTestUploadList.get(i);
                    mCharView.addValue(signal, signal.rate, isDown);
                    if (i == 0) {
                        setSignalDataPlayBack(signal, i);
                    }
                }
                mCharView.setTestedDownOrUpData(mSignalTestUploadList, isDown);
            }
            mIsInitUpload = true;
        }
        updateView(isDown);
    }

    /**
     * 基础信息
     */
    private void loadDataBaseInfo(){
        try{
            mTvTime.setText(mFtpTestInfo.getTestEnd());
            if(TextUtils.isEmpty(mFtpTestInfo.remarks)){
                mTvRemark.setText("添加备注");
            }else{
                mTvRemark.setText(mFtpTestInfo.remarks);
            }
            //			mTvIp.setText("IP:" + mFtpTestInfo.getDownHostAddr());
            //获取地址信息
            if(mFtpTestInfo.getLatitude() != 0 && mFtpTestInfo.getLongitude() != 0){
                LatLng latLng = GPSUtil.getInstances().toBdLatlng(new LatLng(mFtpTestInfo.getLatitude(), mFtpTestInfo.getLongitude()));
                if (latLng != null) {
                    PoiSearchGetUtil.getInstances().toGeoCoder(latLng, new ListenerBack() {
                        @Override
                        public void onListener(int type, Object object, boolean isTrue) {
                            if (type == EnumRequest.OTHER_GEO_SEARCH.toInt()) {
                                if (isTrue) {
                                    ReverseGeoCodeResult.AddressComponent address = ((ReverseGeoCodeResult) object).getAddressDetail();
                                    if(address != null){
                                        StringBuffer buffer = new StringBuffer();
                                        buffer.append(address.city).append(address.district).append(address.street).append(address.streetNumber);
                                        mTvAddress.setText(buffer.toString());
                                    }
                                }
                            }
                        }
                    });
                }
            }
            mTvIp.setText("IP:" + SpeedTestUtil.getInstance().getIPAddress(SpeedTestDetailActivity.this)); //显示本机ip
            mTvTitle1.setText(mFtpTestInfo.getDownHostTypeName());
            mTvTitle2.setText(mFtpTestInfo.operator);

            if(mFtpTestInfo.getDownSpeedAvg() == 0){
                mTvAvgDownload.setText("——");
                mTvSpeedDownAvg.setText("——");
            }else{
                mTvAvgDownload.setText(String.valueOf(mFtpTestInfo.getDownSpeedAvg()));
                mTvSpeedDownAvg.setText(String.valueOf(mFtpTestInfo.getDownSpeedAvg()));
            }
            if(mFtpTestInfo.getUpSpeedAvg() == 0){
                mTvAvgUpload.setText("——");
                mTvSpeedUpAvg.setText("——");
            }else{
                mTvAvgUpload.setText(String.valueOf(mFtpTestInfo.getUpSpeedAvg()));
                mTvSpeedUpAvg.setText(String.valueOf(mFtpTestInfo.getUpSpeedAvg()));
            }

            if(mFtpTestInfo.getDownSpeedMax() == 0){
                mTvSpeedDownMax.setText("——");
            }else{
                mTvSpeedDownMax.setText(String.valueOf(mFtpTestInfo.getDownSpeedMax()));
            }

            if(mFtpTestInfo.getUpSpeedMax() == 0){
                mTvSpeedUpMax.setText("——");
            }else{
                mTvSpeedUpMax.setText(String.valueOf(mFtpTestInfo.getUpSpeedMax()));
            }

            if(mFtpTestInfo.downRealFlow == null || Double.parseDouble(mFtpTestInfo.downRealFlow) == 0){
                mTvRealDownload.setText("--");
            }else{
                mTvRealDownload.setText(String.valueOf(mFtpTestInfo.downRealFlow) + "MB");
            }

            if(mFtpTestInfo.upRealFlow == null || Double.parseDouble(mFtpTestInfo.upRealFlow) == 0){
                mTvRealUpload.setText("--");
            }else{
                mTvRealUpload.setText(String.valueOf(mFtpTestInfo.upRealFlow) + "MB");
            }

            if(TextUtils.isEmpty(mFtpTestInfo.ping)){
                mTvNoLoginPingTime.setText("——");
                mTvLoginPingTime.setText("——");
            }else{
                mTvNoLoginPingTime.setText(String.valueOf(mFtpTestInfo.ping));
                mTvLoginPingTime.setText(String.valueOf(mFtpTestInfo.ping));
            }

            if(TextUtils.isEmpty(mFtpTestInfo.jitter)){
                mTvNoLoginPingShake.setText("——");
                mTvLoginPingShake.setText("——");
            }else{
                mTvNoLoginPingShake.setText(String.valueOf(mFtpTestInfo.jitter));
                mTvLoginPingShake.setText(String.valueOf(mFtpTestInfo.jitter));
            }

            if(TextUtils.isEmpty(mFtpTestInfo.packetLoss)){
                mTvNoLoginPacketLoss.setText("——");
                mTvLoginPacketLoss.setText("——");
            }else{
                mTvNoLoginPacketLoss.setText(String.valueOf(mFtpTestInfo.packetLoss));
                mTvLoginPacketLoss.setText(String.valueOf(mFtpTestInfo.packetLoss));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 加载曲线 未登陆模块
     */
    private void loadCurve(){
        SpeedTestCurveView detailsDownloadCharView = new SpeedTestCurveView(mActivity);
        mLlDetailsDlModule.addView(detailsDownloadCharView);//详情页  初始化下载曲线图
        SpeedTestCurveView detailsUploadCharView = new SpeedTestCurveView(mActivity);
        mLlDetailsUlModule.addView(detailsUploadCharView);//详情页 初始化上传曲线图

        if(mSignalTestDownList.size() > 0){
            ArrayList<Double> dlList = new ArrayList<Double>();
            for(Signal signal : mSignalTestDownList){
                dlList.add(Double.parseDouble(String.valueOf(signal.rate)));
            }
            detailsDownloadCharView.loadDataSet(dlList, true);
        }
        if(mSignalTestUploadList.size() > 0){
            ArrayList<Double> ulList = new ArrayList<Double>();
            for(Signal signal : mSignalTestUploadList){
                ulList.add(Double.parseDouble(String.valueOf(signal.rate)));
            }
            detailsUploadCharView.loadDataSet(ulList, false);
        }
    }

    private int getIndex(String code){
        try{
            int id = -1;
            BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
            if(beanBaseConfig != null){
                ArrayList<BeanNpConfig> npConfig = beanBaseConfig.npConf;
                if(npConfig != null){
                    double avgDownload = mFtpTestInfo.getDownSpeedAvg();
                    for(BeanNpConfig config : npConfig){
                        if(code.equals(config.confType)){
                            if(avgDownload >= config.threshold && avgDownload < config.endThreshold){
                                id = config.id;
                                return id;
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    private Object[] getResult(int index){
        Object[] result = new Object[2];
        String value = "极慢";
        int progress = 25;
        if(index == 1){
            value = "极慢";
            progress = 25;
        }else if(index == 2){
            value = "一般";
            progress = 50;
        }else if(index == 3){
            value = "流畅";
            progress = 75;
        }else if(index == 4){
            value = "极快";
            progress = 100;
        }
        result[0] = value;
        result[1] = progress;

        return result;
    }

    /**
     * 根据规则加载ping结果    根据配置数据计算数值
     */
    private void loadPing(){
        try{
            int indexYxcs = getIndex(getString(R.string.permissionsYxcs));
            int indexSpcs = getIndex(getString(R.string.permissionsSpcs));
            int indexZxcs = getIndex(getString(R.string.permissionsZxcs));
            int indexDscs = getIndex(getString(R.string.permissionsDscs));

            Object[] objectYxcs = getResult(indexYxcs);
            Object[] objectSpcs = getResult(indexSpcs);
            Object[] objectZxcs = getResult(indexZxcs);
            Object[] objectDscs = getResult(indexDscs);

            mTvYxcsValue.setText((String)objectYxcs[0]);
            mTvSpcsValue.setText((String)objectSpcs[0]);
            mTvZxcsValue.setText((String)objectZxcs[0]);
            mTvDscsValue.setText((String)objectDscs[0]);
            mPbYxcsValue.setProgress((int)objectYxcs[1]);
            mPbSpcsValue.setProgress((int)objectSpcs[1]);
            mPbZxcsValue.setProgress((int)objectZxcs[1]);
            mPbDscsValue.setProgress((int)objectDscs[1]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void requestSpeedTestLogDetail() {
        showMyDialog();
        RequestSpeedTestDetail request = new RequestSpeedTestDetail();
        request.id = mFtpTestInfo.id;

        String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_SPEED_TEST_LOG_DETAIL);
        NetWorkUtilNow.getInstances().readNetworkPostJsonObject(requestUrl,
                JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

                    @Override
                    public void onListener(int type, Object object, boolean isTrue) {
                        dismissMyDialog();
                        ResponseSpeedTestDetail response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedTestDetail.class);
                        if (response != null && response.isRs()) {
                            mShareCode = response.datas.shareCode;
                            mSignalTestDownList = response.datas.downloadTestList;
                            mSignalTestUploadList = response.datas.uploadTestList;
                            filterData();

                            loadOtherData();
                        } else {
                            String errorMsg = response != null ? response.getMsg() : "获取详情失败，请先检查网络";
                            showCommon(errorMsg);
                        }
                    }
                });
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        if (type == EnumRequest.OTHER_SPEED_CLICK.toInt()) {
            int position = (int) object;
            if (mIsDownload) {
                if (mSignalTestDownList != null && mSignalTestDownList.size() > position) {
                    setSignalDataPlayBack(mSignalTestDownList.get(position), position);
                }

            } else {
                if (mSignalTestUploadList != null && mSignalTestUploadList.size() > position) {
                    setSignalDataPlayBack(mSignalTestUploadList.get(position), position);
                }
            }
        } else if (type == EnumRequest.OTHER_SPEED_SWITCH.toInt()) {
            mCharView.setTestDownOrUp();
            updateView(isTrue);
        } else if(type == EnumRequest.MENU_SELECT_ONE.toInt()){ //删除
            new CommonDialog(SpeedTestDetailActivity.this).setListener(SpeedTestDetailActivity.this)
                    .setButtonText("取消", "确定").show( "是否删除当前记录？",
                    EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
        } else if(type == EnumRequest.MENU_SELECT_TWO.toInt()){
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
        }else if(type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()){
            if(!isTrue){
                removeLog();
            }
        }
    }

    /**
     * 删除日志
     */
    private void removeLog(){
        showMyDialog();
        RequestRemoveLog request = new RequestRemoveLog();
        request.id = mFtpTestInfo.id;
        request.type=("SINGLE");
        request.userId=(UtilHandler.getInstance().toLong(getUserID(), 0));
        NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(
                NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_REMOVE_SPEED_TEST_LOG),
                JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

                    @Override
                    public void onListener(int type, Object object,
                                           boolean isTrue) {
                        dismissMyDialog();
                        if(isTrue){
                            BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
                            if(response != null){
                                if(response.isRs()){ //删除成功
                                    List<Db_JJ_FTPTestInfo> list = SpeedTestDataSet.mServerList;
                                    if(list != null){
                                        for(Db_JJ_FTPTestInfo info : list){ //移除本地日志
                                            if(info.id == mFtpTestInfo.id){
                                                list.remove(info);
                                                break;
                                            }
                                        }
                                    }
                                    showCommon("删除成功!");
                                    //通知刷新
                                    Intent intent = new Intent(SpeedTestHistoryActivity.ON_UPDATE_HISTORY);
                                    SpeedTestDetailActivity.this.sendBroadcast(intent);
                                    SpeedTestDetailActivity.this.finish();
                                }else{
                                    showCommon(response.getMsg());
                                }
                            }else{
                                showCommon("删除失败");
                            }
                        }else{
                            showCommon("删除失败");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.speed_test_detail_login_btn_switch_download) {//下载切换
            if (mSignalTestDownList == null || mSignalTestDownList.size() == 0) {
                showCommon("本次未进行下载速率测试");
                return;
            }
            if (mIsInitDownload) {
                updateView(true);
            } else {
                initCharView(true);
            }
        } else if (id == R.id.speed_test_detail_login_btn_switch_upload) {//上传切换
            if (mSignalTestUploadList == null || mSignalTestUploadList.size() == 0) {
                showCommon("本次未进行上传速率测试");
                return;
            }
            if (mIsInitUpload) {
                updateView(false);
            } else {
                initCharView(false);
            }
        } else if (id == R.id.speed_test_detail_login_btn_switchSinr) {//SINR切换
            mCharView.showRightDataAxis(R.id.ll_speed_test_sinr);
            mBtnSwitchSinr.setBackgroundResource(R.drawable.drawable_edging_2dabf5_10);
            mBtnSwitchRsrp.setBackgroundResource(android.R.color.transparent);
            mBtnSwitchSinr.setTextColor(getResources().getColor(R.color.white_edeeee));
            mBtnSwitchRsrp.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
        } else if (id == R.id.speed_test_detail_login_btn_switchRsrp) {//RSRP切换
            mCharView.showRightDataAxis(R.id.ll_speed_test_rsrp);
            mBtnSwitchSinr.setBackgroundResource(android.R.color.transparent);
            mBtnSwitchRsrp.setBackgroundResource(R.drawable.drawable_edging_2dabf5_10);
            mBtnSwitchSinr.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
            mBtnSwitchRsrp.setTextColor(getResources().getColor(R.color.white_edeeee));
        } else if (id == R.id.speed_test_detail_login_ibtn_selectSet) {//指标查看设置
            if (mBtnSwitchSinr.getVisibility() == View.VISIBLE) {
                mBtnSwitchSinr.setVisibility(View.GONE);
                mBtnSwitchRsrp.setVisibility(View.GONE);
                mCkValueSinr.setVisibility(View.VISIBLE);
                mCkValueRsrp.setVisibility(View.VISIBLE);
                mCkValueSinrNr.setVisibility(View.VISIBLE);
                mCkValueRsrpNr.setVisibility(View.VISIBLE);
                ((ImageButton) v).setImageResource(R.drawable.icon_jzxh_nsa_quit);
            } else {
                if (!mCkValueRsrp.isChecked() && !mCkValueSinr.isChecked() &&
                        !mCkValueRsrpNr.isChecked() && !mCkValueSinrNr.isChecked()) {
                    showCommon("至少选择一项曲线数据");
                    return;
                }
                mViewIconSinr.setVisibility(mCkValueSinr.isChecked() ? View.VISIBLE : View.GONE);
                mViewIconSinrNr.setVisibility(mCkValueSinrNr.isChecked() ? View.VISIBLE : View.GONE);
                mViewIconRsrp.setVisibility(mCkValueRsrp.isChecked() ? View.VISIBLE : View.GONE);
                mViewIconRsrpNr.setVisibility(mCkValueRsrpNr.isChecked() ? View.VISIBLE : View.GONE);
                mBtnSwitchSinr.setVisibility(View.VISIBLE);
                mBtnSwitchRsrp.setVisibility(View.VISIBLE);
                mCkValueSinr.setVisibility(View.GONE);
                mCkValueRsrp.setVisibility(View.GONE);
                mCkValueSinrNr.setVisibility(View.GONE);
                mCkValueRsrpNr.setVisibility(View.GONE);
                ((ImageButton) v).setImageResource(R.drawable.icon_jzxh_nsa_setting);
                mCharView.showChartType(mCkValueSinr.isChecked(), mCkValueRsrp.isChecked(), mCkValueSinrNr.isChecked(), mCkValueRsrpNr.isChecked());
            }
        } else if (id == R.id.speed_test_detail_tv_remark) {
            Intent intent = new Intent(SpeedTestDetailActivity.this, SpeedTestRemarkActivity.class);
            intent.putExtra("Db_JJ_FTPTestInfo", mFtpTestInfo);
            SpeedTestDetailActivity.this.startActivity(intent);
        } else if (id == R.id.speed_test_detail_ll_yxcs) {
            Intent yxcs = ModeClickHandler.getInstances(this).getPingIntent(getString(R.string.permissionsYxcs));
            if (yxcs != null) {
                goIntent(yxcs, false);
            }
        } else if (id == R.id.speed_test_detail_ll_spcs) {
            Intent spcs = ModeClickHandler.getInstances(this).getPingIntent(getString(R.string.permissionsSpcs));
            if (spcs != null) {
                goIntent(spcs, false);
            }
        } else if (id == R.id.speed_test_detail_ll_zxcs) {
            Intent zxcs = ModeClickHandler.getInstances(this).getPingIntent(getString(R.string.permissionsZxcs));
            if (zxcs != null) {
                goIntent(zxcs, false);
            }
        } else if (id == R.id.speed_test_detail_ll_dscs) {
            Intent dscs = ModeClickHandler.getInstances(this).getPingIntent(getString(R.string.permissionsDscs));
            if (dscs != null) {
                goIntent(dscs, false);
            }
        }
    }

    private void updateView(boolean isDown) {
        mIsDownload = isDown;
        if (isDown) {// 下载
            mTvSwitchDown.setBackgroundResource(R.drawable.drawable_speed_test_switch_btn_select);
            mTvSwitchDown.setTextColor(getResources().getColor(R.color.white_edeeee));
            mTvSwitchUpload.setBackgroundResource(R.drawable.drawable_speed_test_switch_btn_unselect);
            mTvSwitchUpload.setTextColor(Color.parseColor("#2f73f5"));

            mCharView.setTestedDownOrUpData(mSignalTestDownList, isDown);
            if (mSignalTestDownList != null && mSignalTestDownList.size() > 0) {
                setSignalDataPlayBack(mSignalTestDownList.get(0), 0);
            }
//            mTvType.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
            mTvData.setTextColor(getResources().getColor(R.color.download_text_color));
            mViewDownUpFlag.setBackgroundColor(getResources().getColor(R.color.download_text_color));
            mViewDownUpColor.setBackgroundResource(R.color.ftp_download_fea916);
            mTvDownUp.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
            mTvType.setText("下载");
            mTvDownUp.setText("下载");
        } else {// 上传
            mTvSwitchDown.setBackgroundResource(R.drawable.drawable_speed_test_switch_btn_unselect);
            mTvSwitchDown.setTextColor(Color.parseColor("#2f73f5"));
            mTvSwitchUpload.setBackgroundResource(R.drawable.drawable_speed_test_switch_btn_select);
            mTvSwitchUpload.setTextColor(getResources().getColor(R.color.white_edeeee));

            mCharView.setTestedDownOrUpData(mSignalTestUploadList, mIsDownload);
            if (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {
                setSignalDataPlayBack(mSignalTestUploadList.get(0), 0);
            }
//            mTvType.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
            mTvData.setTextColor(getResources().getColor(R.color.upload_text_color));
            mViewDownUpFlag.setBackgroundColor(getResources().getColor(R.color.upload_text_color));
            mViewDownUpColor.setBackgroundResource(R.color.ftp_upload_65c40b);
            mTvDownUp.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
            mTvType.setText("上传");
            mTvDownUp.setText("上传");
        }
    }

    private void setSignalDataPlayBack(Signal signal, int position) {
        String currentSecond = "第" + (position + 1) + "秒(" + signal.netType + ")：";
        mTvPlayBackCellName.setText(currentSecond);
        if (signal != null) {
            float rate = signal.rate;
            if (signal.getTypeNet() != null && "NR".equals(signal.getTypeNet())) {
                mTvPlayBackCellName.setText(currentSecond + signal.getLte_name());
                mTvPlayBackTac.setText(signal.getLte_tac());
                mTvPlayBackPci.setText(signal.getLte_pci());
                mTvPlayBackCGI.setText(signal.getLte_cgi());
                mTvPlayBackENB.setText(signal.getLte_enb());
                mTvPlayBackCELLID.setText(signal.getLte_cid());
                mTvPlayBackPindian.setText(signal.getLte_band());
                mTvPlayBackRSRP.setText(signal.getLte_rsrp());
                mTvPlayBackSINR.setText(signal.getLte_sinr());
                mTlLte.setVisibility(View.VISIBLE);
                mTlGsm.setVisibility(View.GONE);
                mViewMdHint.setVisibility(View.VISIBLE);
                mViewNr.setVisibility(View.VISIBLE);
                mViewNrHint.setVisibility(View.VISIBLE);
                mTvPciNr.setText(signal.getLte_pci_nr());
                mTvEarfcnNr.setText(signal.getLte_earfcn_nr());
                mTvBandNr.setText(signal.getLte_band_nr());
                mTvSinrNr.setText(signal.getLte_sinr_nr());
                mTvRsrpNr.setText(signal.getLte_rsrp_nr());
            }else if (signal.getTypeNet() != null && "LTE".equals(signal.getTypeNet())) {
                mTvPlayBackCellName.setText(currentSecond + signal.getLte_name());
                mTvPlayBackTac.setText(signal.getLte_tac());
                mTvPlayBackPci.setText(signal.getLte_pci());
                mTvPlayBackCGI.setText(signal.getLte_cgi());
                mTvPlayBackENB.setText(signal.getLte_enb());
                mTvPlayBackCELLID.setText(signal.getLte_cid());
                mTvPlayBackPindian.setText(signal.getLte_band());
                mTvPlayBackRSRP.setText(signal.getLte_rsrp());
                mTvPlayBackSINR.setText(signal.getLte_sinr());
                mTlLte.setVisibility(View.VISIBLE);
                mTlGsm.setVisibility(View.GONE);
                mViewMdHint.setVisibility(View.GONE);
                mViewNr.setVisibility(View.GONE);
                mViewNrHint.setVisibility(View.GONE);
            } else if (signal.getTypeNet() != null && "GSM".equals(signal.getTypeNet())) {
                mTvPlayBackCellName.setText(currentSecond + signal.getGsm_name());
                mTvPlayBackTacGSM.setText(signal.getGsm_lac());
                mTvPlayBackCELLIDGSM.setText(signal.getGsm_cid());
                // 频段
                if (!TextUtils.isEmpty(signal.getTd_pccpch())) {
                    mTvPlayBackBcchPDGSM.setText(signal.getTd_pccpch());
                } else {
                    mTvPlayBackBcchPDGSM.setText("");
                }
                mTvPlayBackRXLGSM.setText(signal.getGsm_rxl());
                mTlLte.setVisibility(View.GONE);
                mTlGsm.setVisibility(View.VISIBLE);
                mViewMdHint.setVisibility(View.GONE);
                mViewNr.setVisibility(View.GONE);
                mViewNrHint.setVisibility(View.GONE);
            }else {
                mViewMdHint.setVisibility(View.GONE);
                mViewNr.setVisibility(View.GONE);
                mViewNrHint.setVisibility(View.GONE);
            }
            mTvData.setText((rate > 0 ? rate : 0) + "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mOnUpdateRemarkReceiver != null){
            unregisterReceiver(mOnUpdateRemarkReceiver);
        }
    }
}
