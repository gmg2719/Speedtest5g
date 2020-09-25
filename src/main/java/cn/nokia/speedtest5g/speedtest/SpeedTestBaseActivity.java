package cn.nokia.speedtest5g.speedtest;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.util.SharedPreHandler;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.JJ_BaseSignalActivity;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanDkConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanRkConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanRsrpConfig;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDialView;
import cn.nokia.speedtest5g.util.UtilChinese;
import cn.nokia.speedtest5g.view.SpeedTestCurveView;
import cn.nokia.speedtest5g.view.WaveButton;

/**
 * 测速基类
 * @author JQJ
 *
 */
public abstract class SpeedTestBaseActivity  extends JJ_BaseSignalActivity{

    protected static final int STATUS_INIT = 1; //初始状态
    protected static final int STATUS_CONNECT = 2; //正在连接状态
    protected static final int STATUS_TESTING = 3; //测试中状态
    protected static final int STATUS_DETAILS = 4; //显示详情状态
    protected int mStatus = STATUS_INIT; //默认 初始状态

    protected List<Signal> mSignalTestDownList = new ArrayList<Signal>();
    protected List<Signal> mSignalTestUploadList = new ArrayList<Signal>();

    protected boolean isStartDownloadFlag = false; //是否已经启动
    protected boolean isStartUploadFlag = false; //是否已经启动
    protected float mCurrentAvgDownload = 0; //当前下载平均值
    protected float mCurrentAvgUpload = 0; //当前上传平均值
    protected String mRankValue = "1"; //当前排行
    protected String mShareCode = ""; //分享码

    /**
     * ftp测试类型 1 速率测试工具， 2 网络挑刺，3 定点测试，4 室内扫楼，5 属地化巡检,6 道路测试 ,7 室分单验 ,8
     * 宏站CQT单验,9宏站DT单验,10投诉测试,11基站信号,12 满格宝测试
     */
    protected int mFtpTestType = 1;

    protected LinearLayout mLlApModule = null; //下载或上传箭头
    protected LinearLayout mLlBottomModule = null; //底部模块
    protected ImageView mIvFlag = null; //箭头
    protected ObjectAnimator mObjectAnimator = null;

    protected ProgressBar mPbRight = null; //右边加载滚动
    protected ImageView mIvRight = null;

    protected WaveButton mWbStart = null;
    protected View mLlModule_Testing = null; //测试中显示仪表盘 曲线模块
    protected View mLlModule_Dashboard = null; //模块1 仪表盘
    protected View mLlModule_Curve = null; //模块2 曲线模块
    protected View mLlModule_left = null; //模块3
    protected View mLlModule_right = null; //模块4
    protected FrameLayout mLlDlModule = null; //下载曲线模块
    protected FrameLayout mLlUlModule = null; //上传曲线模块

    //测速结束详情模块
    protected View mLlModule_Details = null;
    protected WaveButton mWbReStart = null; //重测
    protected TextView mTvDetailsAvgDownload = null;
    protected TextView mTvDetailsAvgUpload = null;
    protected FrameLayout mLlDetailsDlModule = null; //详情下载曲线模块
    protected FrameLayout mLlDetailsUlModule = null; //详情上传曲线模块
    protected SpeedTestCurveView mDetailsDownloadCharView = null;
    protected SpeedTestCurveView mDetailsUploadCharView = null;
    protected TextView mTvGotoSignalTest = null; //测信号
    protected TextView mTvDetailsPing = null;
    protected TextView mTvDetailsShake = null;
    protected TextView mTvDetailsPacketLoss = null;

    //正常测试界面
    protected SpeedTestCurveView mDownloadCharView = null;
    protected SpeedTestCurveView mUploadCharView = null;
    protected SpeedTestDialView mSpeedTestDialView = null; //仪表盘
    protected Signal mLastSignal = null;
    protected TextView mTvSpeedTestMsg = null;
    protected boolean isFtpConnectFail = false;
    protected TextView mTvDownOrUpIcon = null;
    protected TextView mTvAvgDownload = null;
    protected TextView mTvAvgUpload = null;
    protected LinearLayout mLlSignal = null; //信号指标模块
    protected TextView mTvSignalValue = null; //信号结果值
    protected LinearLayout mLlLogo = null; //logo模块
    protected TextView mTvTitleLeft = null;
    protected TextView mTvTitleLeft1 = null; //当前运营商名称
    protected TextView mTvTitleLeft2 = null; //当前网络类型
    protected TextView mTvTitleRight1 = null; //当前服务器名称
    protected TextView mTvTitleRight2 = null;
    protected LinearLayout mLlBaseInfo = null; //信号模块
    protected LinearLayout mLlFlow = null; //流量预估模块
    protected View mLlViewNotNr = null;
    protected View mLlViewNr = null;
    protected TextView mTvNet4g = null;
    protected TextView mTvNrEarfcn = null;
    protected TextView mTvNrRsrp = null;
    protected TextView mTvNrSinr = null;
    protected TextView mTvNet = null;
    protected TextView mTvCellId = null;
    protected TextView mTvRsrpOrLac = null;
    protected TextView mTvSinrOrRxl = null;
    protected TextView mTvTip = null;
    protected TextView mTvWlRank = null; //宽带排行
    protected TextView mTvRank = null; //排行数值
    protected TextView mTvPing = null;
    protected TextView mTvShake = null;
    protected TextView mTvPacketLoss = null;

    protected Handler mChildHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * gps权限打开提示
     */
    protected void openLocService(){
        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setCancelable(false);
        commonDialog.setListener(new ListenerBack() {

            @Override
            public void onListener(int type, Object object, boolean isTrue) {
                if(type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()){
                    if(isTrue){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try{
                            startActivity(intent);
                        } catch(ActivityNotFoundException ex){
                            intent.setAction(Settings.ACTION_SETTINGS);
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        })
                .setButtonText("去开启", "取消").show("为了精准获取运营商信息，准确分配测速点，请你先在\"设置\"中开启GPS权限后，重新启动App", EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
    }

    /**
     * 计算排行
     */
    protected void updateRank(){
        try{
            if(mCurrentAvgDownload <= 0){
                mRankValue = "1";
                mTvWlRank.setText("无网络");
                mTvRank.setText("1%");
                return;
            }

            //超过用户数
            boolean isGoto1 = false;
            BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
            if(beanBaseConfig != null){
                ArrayList<BeanRkConfig> list = beanBaseConfig.rkConf;
                if(list != null){
                    for(BeanRkConfig config : list){
                        if(mCurrentAvgDownload >= config.threshold && mCurrentAvgDownload < config.endThreshold){ //在这区间
                            isGoto1 = true;
                            mRankValue = config.id;
                            mTvRank.setText(config.id+"%");
                            break;
                        }
                    }
                    if(!isGoto1){
                        mRankValue = "99";
                        mTvRank.setText("99%");
                    }
                }
            }

            //宽带换算
            boolean isGoto2 = false;
            if(beanBaseConfig != null){
                ArrayList<BeanDkConfig> list = beanBaseConfig.dkConf;
                if(list != null){
                    for(BeanDkConfig config : list){
                        if(mCurrentAvgDownload >= config.threshold && mCurrentAvgDownload < config.endThreshold){ //在这区间
                            isGoto2 = true;
                            int threshold = (int)config.threshold;
                            int endThreshold = (int)config.endThreshold;
                            if(threshold == 1000 && endThreshold == 100000){
                                mTvWlRank.setText("千兆");
                            }else{
                                mTvWlRank.setText(threshold + "~" + endThreshold + "M");
                            }
                            break;
                        }
                    }
                    if(!isGoto2){
                        mTvWlRank.setText("无网络");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 计算信号强度
     */
    protected void updateRsrp(float avgRsrp){
        try{
            boolean isGoto = false;
            BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
            if(beanBaseConfig != null){
                ArrayList<BeanRsrpConfig> list = beanBaseConfig.rsrpConf;
                if(list != null){
                    for(BeanRsrpConfig config : list){
                        if(avgRsrp >= config.threshold && avgRsrp < config.endThreshold){ //在这区间
                            isGoto = true;
                            if(TextUtils.isEmpty(config.showDesc)){
                                mTvSignalValue.setText(String.format("当前信号%s", config.signalDesc));
                            }else{
                                mTvSignalValue.setText(String.format("当前信号%s,%s", config.signalDesc, config.showDesc));
                            }
                            return;
                        }
                    }
                    if(!isGoto){
                        mTvSignalValue.setText("当前信号较弱");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新波纹按钮状态
     */
    protected void updateWaveView() {
        if (mWbStart.isPlayAnimation()) {
            mWbStart.stopAnimation();
        } else {
            mWbStart.startAnimation();
        }

        if (mWbReStart.isPlayAnimation()) {
            mWbReStart.stopAnimation();
        } else {
            mWbReStart.startAnimation();
        }
    }

    /**
     * 模块1仪表盘缩放 放大
     */
    protected void animatorModule_1(boolean isStart){
        if(isStart){
            mLlModule_Dashboard.setVisibility(View.VISIBLE);
            mLlModule_Dashboard.setAlpha(0f);

            PropertyValuesHolder ValuesHolder1 = PropertyValuesHolder.ofFloat("alpha",0f,1f);
            PropertyValuesHolder ValuesHolder2 = PropertyValuesHolder.ofFloat("scaleX", 0f,1f);
            PropertyValuesHolder ValuesHolder3 = PropertyValuesHolder.ofFloat("scaleY", 0f,1f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mLlModule_Dashboard, ValuesHolder1, ValuesHolder2, ValuesHolder3);
            objectAnimator.setDuration(800);
            objectAnimator.start();
        }else{
            mLlModule_Dashboard.setVisibility(View.GONE);
        }
    }

    /**
     * 模块2图例从下到上移动
     */
    protected void animatorModule_2(boolean isStart){
        if(isStart){
            mLlModule_Curve.setVisibility(View.VISIBLE);
            mLlModule_Curve.setAlpha(0f);

            PropertyValuesHolder ValuesHolder1 = PropertyValuesHolder.ofFloat("alpha",0f,1f);
            PropertyValuesHolder ValuesHolder2 = PropertyValuesHolder.ofFloat("translationY", 300f,0f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mLlModule_Curve, ValuesHolder1, ValuesHolder2);
            objectAnimator.setDuration(800);
            objectAnimator.start();
        }else{
            mLlModule_Curve.setVisibility(View.GONE);
        }
    }

    /**
     * 模块3水平移动
     */
    protected void animatorModule_3(boolean isStart){
        if(isStart){
            ObjectAnimator.ofFloat(mLlModule_left, "translationX", -25f).setDuration(1500).start();
            ObjectAnimator.ofFloat(mLlModule_right, "translationX", 25f).setDuration(1500).start();
        }else{
            ObjectAnimator.ofFloat(mLlModule_left, "translationX", 0f).setDuration(1500).start();
            ObjectAnimator.ofFloat(mLlModule_right, "translationX", 0f).setDuration(1500).start();
        }
    }

    /**
     * 模块4箭头动画
     * @param isDownload 开始上传或下载箭头
     */
    protected void startAnimatorModule_4(boolean isDownload){
        mLlApModule.removeAllViews();

        mIvFlag = new ImageView(mContext);
        mIvFlag.setAlpha(0f);
        float xValue1 = 200f;
        float xValue2 = -250f;
        if(isDownload){
            mLlApModule.setGravity(Gravity.END);
            mIvFlag.setImageResource(R.drawable.icon_speed_test_download_flag);
            mLlApModule.addView(mIvFlag);
            xValue1 = 200f;
            xValue2 = -250f;
        }else{
            mLlApModule.setGravity(Gravity.LEFT);
            mIvFlag.setImageResource(R.drawable.icon_speed_test_upload_flag);
            mLlApModule.addView(mIvFlag);
            xValue1 = -200f;
            xValue2 = 250f;
        }

        PropertyValuesHolder ValuesHolder1 = PropertyValuesHolder.ofFloat("alpha",0f,1f);
        PropertyValuesHolder ValuesHolder2 = PropertyValuesHolder.ofFloat("translationX", xValue1, xValue2);
        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mIvFlag, ValuesHolder1, ValuesHolder2);
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
        TimeInterpolator value = new AccelerateInterpolator();
        mObjectAnimator.setInterpolator(value);
        mObjectAnimator.setDuration(1300);
        mObjectAnimator.start();
    }

    /**
     * 停止 箭头动画
     */
    protected void stopAnimatorModule_4(){
        if(mObjectAnimator != null){
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
        if(mIvFlag != null){
            mLlApModule.removeAllViews();
            mIvFlag = null;
        }
    }

    /**
     * 刷新信号模块
     */
    protected void refreshSignalModule(){
        //游客登陆  不显示rsrp模块
        String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
        if("Phone".equals(loginType)){
            if(mStatus == STATUS_INIT || mStatus == STATUS_CONNECT || mStatus == STATUS_DETAILS){
                mLlBaseInfo.setVisibility(View.VISIBLE);
            }else{
                mLlBaseInfo.setVisibility(View.GONE);
            }
        }else if("Tourist".equals(loginType)){
            mLlBaseInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新logo模块
     */
    protected void refreshLogoModule(){
        //游客登陆  显示logo模块
        String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
        if("Phone".equals(loginType)){
            mLlLogo.setVisibility(View.GONE);
        }else if(TextUtils.isEmpty(loginType) || "Tourist".equals(loginType)){
            if(mStatus == STATUS_INIT || mStatus == STATUS_CONNECT){
                mLlLogo.setVisibility(View.VISIBLE);
            }else{
                mLlLogo.setVisibility(View.GONE);
            }
        }
    }

    /*
     * 测试参数判断
     */
    protected boolean judgment() {
        boolean CUSTOM_FTP_DOWN_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_DOWN_OPEN, false);
        boolean CUSTOM_FTP_UPLOAD_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_UPLOAD_OPEN, false);

        if (CUSTOM_FTP_DOWN_OPEN) {
            String downIpStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_IP, "");
            if (!UtilChinese.getInstance().isIP(downIpStr)) {
                showCommon("请输入正确的下载IP");
                return false;
            }
            String downPathStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_PATH, "");
            if (!downPathStr.startsWith("/") || !downPathStr.contains(".")) {
                showCommon("请输入正确的下载地址");
                return false;
            } else {
                String[] split = downPathStr.split("/");
                if (!split[split.length - 1].contains(".")) {
                    showCommon("请输入正确的下载地址");
                    return false;
                }
            }
            String downThreadStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_THREAD, "");
            if (!downThreadStr.isEmpty()) {
                if (UtilHandler.getInstance().toInt(downThreadStr, 0) > 10) {
                    showCommon("下载线程数最大不能超过10个");
                    return false;
                } else if (UtilHandler.getInstance().toInt(downThreadStr, 0) <= 0) {
                    showCommon("下载线程数不能等于0");
                    return false;
                }
            }

            String downPort = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_PORT, "");
            if (downPort.isEmpty()) {
                showCommon("请输入正确的下载端口号");
                return false;
            }

            String downUserStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_USER, "");
            if (downUserStr.isEmpty()) {
                showCommon("请输入正确的下载帐号");
                return false;
            }
            String downPasswdStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_DOWN_PASSWD, "");
            if (downPasswdStr.isEmpty()) {
                showCommon("请输入正确的下载密码");
                return false;
            }
        }

        if (CUSTOM_FTP_UPLOAD_OPEN) {
            String uploadIpStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_IP, "");
            if (!UtilChinese.getInstance().isIP(uploadIpStr)) {
                showCommon("请输入正确的上传IP");
                return false;
            }
            String uploadPathStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_PATH, "");
            if (!uploadPathStr.isEmpty()
                    && UtilHandler.getInstance().toInt(uploadPathStr, 0) <= 0) {
                showCommon("上传文件大小必须大于0");
                return false;
            }
            String uploadThreadStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, "");
            if (!uploadThreadStr.isEmpty()) {
                if (UtilHandler.getInstance().toInt(uploadThreadStr, 0) > 5) {
                    showCommon("上传线程数最大不能超过5个");
                    return false;
                } else if (UtilHandler.getInstance().toInt(uploadThreadStr, 0) <= 0) {
                    showCommon("上传线程数不能等于0");
                    return false;
                }
            }
            String upPort = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_PORT, "");
            if (upPort.isEmpty()) {
                showCommon("请输入正确的上传端口号");
                return false;
            }
            String uploadUserStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
            if (uploadUserStr.isEmpty()) {
                showCommon("请输入正确的上传帐号");
                return false;
            }
            String uploadPasswdStr = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
            if (uploadPasswdStr.isEmpty()) {
                showCommon("请输入正确的上传密码");
                return false;
            }
        }
        return true;
    }

    /**
     * 清空测试数据
     */
    protected void clearSpeedTestData() {
        mPingInfo = null;
        mCurrentPingCount = 0;
        mLogId = 0;
        mRankValue = "1";
        mCurrentAvgDownload = 0;
        mCurrentAvgUpload = 0;
        mAllDownload = 0;
        mAllUpload = 0;

        mTvPing.setText("—");
        mTvDetailsPing.setText("—");
        mTvShake.setText("—");
        mTvDetailsShake.setText("—");
        mTvPacketLoss.setText("—");
        mTvDetailsPacketLoss.setText("—");

        mDownloadCharView.clearData();
        mUploadCharView.clearData();
        mDetailsDownloadCharView.clearData();
        mDetailsUploadCharView.clearData();

        mSignalTestDownList.clear();
        mSignalTestUploadList.clear();
    }

    protected String getModule(int type) {
        String moduleName = "";
        /**
         * ftp测试类型 1 ftp测试工具， 2 网络挑刺，3 定点测试，4 室内扫楼，5 属地化巡检,6 道路测试 ,7 室分单验 ,8
         * 宏站CQT单验 10 投诉测试 11基站信号 12满格宝
         */
        switch (type) {
            case 1:
                moduleName = "速率测试工具";
                break;
            case 2:
                moduleName = "网络挑刺";
                break;
            case 3:
                moduleName = "定点测试";
                break;
            case 4:
                moduleName = "室内扫楼";
                break;
            case 5:
                moduleName = "属地化巡检";
                break;
            case 6:
                moduleName = "道路测试";
                break;
            case 7:
                moduleName = "室分单验";
                break;
            case 8:
                moduleName = "宏站CQT单验";
                break;
            case 10:
                moduleName = "投诉测试";
                break;
            case 11:
                moduleName = "基站信号";
                break;
            case 12:
                moduleName = "满格宝";
                break;
            default:
                break;
        }

        return moduleName;
    }

    @Override
    public void initStatistics() {
        installStatistics(R.string.code_sl);
    }

}
