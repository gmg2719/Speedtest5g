package cn.nokia.speedtest5g.speedtest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.util.SharedPreHandler;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.Db_CustomFtpConfig;
import cn.nokia.speedtest5g.app.bean.HistoryDb;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.ResponseAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.UtilChinese;
import cn.nokia.speedtest5g.view.MyAutoCompleteTextView;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 自定义服务器
 * @author JQJ
 *
 */
public class SpeedTestCustomSetActivity extends BaseActionBarActivity{

    private CheckBox mCbDown = null;
    private CheckBox mCbUp = null;

    private MyAutoCompleteTextView mEtDownIp = null;
    private EditText mEtDownPath = null;
    private EditText mEtDownThread = null;
    private EditText mEtDownPort = null;
    private EditText mEtDownAccout = null;
    private EditText mEtDownPassword = null;

    private MyAutoCompleteTextView mEtUpIp = null;
    private EditText mEtUpSize = null;
    private EditText mEtUpThread = null;
    private EditText mEtUpPort = null;
    private EditText mEtUpAccout = null;
    private EditText mEtUpPassword = null;

    private ArrayList<Db_CustomFtpConfig> mDownloadList = new ArrayList<Db_CustomFtpConfig>();
    private ArrayList<Db_CustomFtpConfig> mUploadList = new ArrayList<Db_CustomFtpConfig>();
    private int mCount = 1000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jqj_speed_test_custom_set_activity);
        mBgTopColor = R.color.bg_color;
        mBgParentColor = R.color.bg_color;
        mTitleTextColor = R.color.gray_c0c0c3;

        init("个性化配置服务器", true);

        initData();
        loadData();
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);

        actionBar.addAction(new MyActionBar.MenuAction(-1, EnumRequest.MENU_SELECT_ONE.toInt(), "保存生效"));

        mCbDown = (CheckBox)findViewById(R.id.speed_test_custom_set_ck_down);
        mCbUp = (CheckBox)findViewById(R.id.speed_test_custom_set_ck_up);

        mEtDownIp = (MyAutoCompleteTextView)findViewById(R.id.speed_test_custom_set_et_down_ip);
        mEtDownPath = (EditText)findViewById(R.id.speed_test_custom_set_et_down_path);
        mEtDownThread = (EditText)findViewById(R.id.speed_test_custom_set_et_down_thread);
        mEtDownPort = (EditText)findViewById(R.id.speed_test_custom_set_et_down_port);
        mEtDownAccout = (EditText)findViewById(R.id.speed_test_custom_set_et_down_user);
        mEtDownPassword = (EditText)findViewById(R.id.speed_test_custom_set_et_down_passwd);
        mEtDownIp.setFirstShow(false);
        mEtDownIp.initHistory(TypeKey.getInstance().HISTORY_CUSTOM_SET_DOWNLOAD);
        mEtDownIp.setListenerBack(this);

        mEtUpIp = (MyAutoCompleteTextView)findViewById(R.id.speed_test_custom_set_et_up_ip);
        mEtUpSize = (EditText)findViewById(R.id.speed_test_custom_set_et_up_size);
        mEtUpThread = (EditText)findViewById(R.id.speed_test_custom_set_et_up_thread);
        mEtUpPort = (EditText)findViewById(R.id.speed_test_custom_set_et_up_port);
        mEtUpAccout = (EditText)findViewById(R.id.speed_test_custom_set_et_up_user);
        mEtUpPassword = (EditText)findViewById(R.id.speed_test_custom_set_et_up_passwd);
        mEtUpIp.setFirstShow(false);
        mEtUpIp.initHistory(TypeKey.getInstance().HISTORY_CUSTOM_SET_UPLOAD);
        mEtUpIp.setListenerBack(this);
    }

    /**
     * 初始化数据库数据
     */
    private void initData(){
        new Thread(){
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                ArrayList<Db_CustomFtpConfig> downList = (ArrayList<Db_CustomFtpConfig>) DbHandler.getInstance().queryObj(Db_CustomFtpConfig.class, "ftpType=?", new Object[]{"DOWNLOAD"}, "time ASC");
                ArrayList<String> downStrList = queryHistory(TypeKey.getInstance().HISTORY_CUSTOM_SET_DOWNLOAD, false);
                ArrayList<Db_CustomFtpConfig> tempDownList = new ArrayList<Db_CustomFtpConfig>();
                if(downStrList.size() > 0){
                    for(String downIp : downStrList){
                        for(Db_CustomFtpConfig downConfig : downList){
                            if(downIp.equals(downConfig.customDownIp)){
                                tempDownList.add(downConfig);
                                break;
                            }
                        }
                    }
                }
                if(tempDownList.size() > 0){
                    for(Db_CustomFtpConfig downConfig : tempDownList){
                        downList.remove(downConfig);
                    }
                    for(Db_CustomFtpConfig downConfig : downList){
                        DbHandler.getInstance().deleteObj(downConfig); //删除数据库无用配置数据
                    }

                    if(tempDownList.size() <= mCount){
                        mDownloadList.addAll(tempDownList);
                    }else{
                        for(int i=0; i<mCount; i++){
                            mDownloadList.add(tempDownList.get(i));
                        }
                    }
                }

                ArrayList<Db_CustomFtpConfig> upList = (ArrayList<Db_CustomFtpConfig>) DbHandler.getInstance().queryObj(Db_CustomFtpConfig.class, "ftpType=?", new Object[]{"UPLOAD"}, "time ASC");
                ArrayList<String> upStrList = queryHistory(TypeKey.getInstance().HISTORY_CUSTOM_SET_UPLOAD, false);
                ArrayList<Db_CustomFtpConfig> tempUpList = new ArrayList<Db_CustomFtpConfig>();
                if(upStrList.size() > 0){
                    for(String upIp : upStrList){
                        for(Db_CustomFtpConfig upConfig : upList){
                            if(upIp.equals(upConfig.customUpIp)){
                                tempUpList.add(upConfig);
                                break;
                            }
                        }
                    }
                }
                if(tempUpList.size() > 0){
                    for(Db_CustomFtpConfig upConfig : tempUpList){
                        upList.remove(upConfig);
                    }
                    for(Db_CustomFtpConfig upConfig : upList){
                        DbHandler.getInstance().deleteObj(upConfig); //删除数据库无用配置数据
                    }

                    if(tempUpList.size() <= mCount){
                        mUploadList.addAll(tempUpList);
                    }else{
                        for(int i=0; i<mCount; i++){
                            mUploadList.add(tempUpList.get(i));
                        }
                    }
                }
            };
        }.start();
    }

    /**
     * 加载数据
     */
    private void loadData(){

        boolean CUSTOM_FTP_DOWN_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_DOWN_OPEN, false);
        boolean CUSTOM_FTP_UPLOAD_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_UPLOAD_OPEN, false);
        mCbDown.setChecked(CUSTOM_FTP_DOWN_OPEN);
        mCbUp.setChecked(CUSTOM_FTP_UPLOAD_OPEN);

        String downIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_IP, "");
        String downPath = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PATH, "");
        String downThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_THREAD, "");
        String downPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PORT, "");
        String downUser = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_USER, "");
        String downPasswd = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PASSWD, "");

        String uploadIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_IP, "");
        String uploadSize = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PATH, "");
        String uploadThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, "");
        String uploadPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PORT, "");
        String uploadUser = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
        String uploadPasswd = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PASSWD, "");

        mEtDownIp.setText(downIp);
        mEtDownPath.setText(downPath);
        mEtDownThread.setText(downThread);
        mEtDownPort.setText(downPort);
        mEtDownAccout.setText(downUser);
        mEtDownPassword.setText(downPasswd);

        mEtUpIp.setText(uploadIp);
        mEtUpSize.setText(uploadSize);
        mEtUpThread.setText(uploadThread);
        mEtUpPort.setText(uploadPort);
        mEtUpAccout.setText(uploadUser);
        mEtUpPassword.setText(uploadPasswd);
    }

    //保存下载配置数据
    private void saveDownload(){
        new Thread(){
            @Override
            public void run() {
                String downIp = mEtDownIp.getText().toString();
                mEtDownIp.saveHistory(downIp, TypeKey.getInstance().HISTORY_CUSTOM_SET_DOWNLOAD);

                if(mDownloadList.size() > 0){
                    for(Db_CustomFtpConfig config : mDownloadList){
                        if(downIp.equals(config.customDownIp)){ //存在则覆盖
                            config.customDownPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_DOWN_PORT, "");
                            config.customDownPath = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_DOWN_PATH, "");
                            config.customDownThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_DOWN_THREAD, "");
                            config.customDownAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_DOWN_USER, "");
                            config.customDownPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_DOWN_PASSWD, "");
                            DbHandler.getInstance().updateObj(config);
                            return;
                        }
                    }
                    saveNewDownConfig();//插入
                }else{
                    saveNewDownConfig();
                }
            };
        }.start();
    }

    private void saveNewDownConfig(){
        Db_CustomFtpConfig newConfig = new Db_CustomFtpConfig();
        newConfig.customDownIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_IP, "");
        newConfig.customDownPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PORT, "");
        newConfig.customDownPath = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PATH, "");
        newConfig.customDownThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_THREAD, "");
        newConfig.customDownAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_USER, "");
        newConfig.customDownPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_DOWN_PASSWD, "");
        newConfig.userId = getUserID();
        newConfig.ftpType = "DOWNLOAD";
        newConfig.time = TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis());
        mDownloadList.add(newConfig);
        DbHandler.getInstance().save(newConfig);
    }

    //保存上传配置数据
    private void saveUpload(){
        new Thread(){
            @Override
            public void run() {
                String upIp = mEtUpIp.getText().toString();
                mEtUpIp.saveHistory(upIp, TypeKey.getInstance().HISTORY_CUSTOM_SET_UPLOAD);

                if(mUploadList.size() > 0){
                    for(Db_CustomFtpConfig config : mUploadList){
                        if(upIp.equals(config.customUpIp)){ //存在则覆盖
                            config.customUpPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PORT, "");
                            config.customUpSize = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PATH, "");
                            config.customUpThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, "");
                            config.customUpAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
                            config.customUpPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                    .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PASSWD, "");
                            DbHandler.getInstance().updateObj(config);
                            return;
                        }
                    }
                    saveNewUpConfig();
                }else{
                    saveNewUpConfig();
                }
            };
        }.start();
    }

    private void saveNewUpConfig(){
        Db_CustomFtpConfig newConfig = new Db_CustomFtpConfig();
        newConfig.customUpIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_IP, "");
        newConfig.customUpPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PORT, "");
        newConfig.customUpSize = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PATH, "");
        newConfig.customUpThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, "");
        newConfig.customUpAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
        newConfig.customUpPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PASSWD, "");
        newConfig.userId = getUserID();
        newConfig.ftpType = "UPLOAD";
        newConfig.time = TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis());
        mUploadList.add(newConfig);
        DbHandler.getInstance().save(newConfig);
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        if(type == EnumRequest.MENU_SELECT_ONE.toInt()){ //保存
            try{
                boolean isDownOk = saveDownData();
                if(!isDownOk){
                    return;
                }
                boolean isUpOk = saveUpData();
                if(!isUpOk){
                    return;
                }

                if(isDownOk){
                    saveDownload();
                }

                if(isUpOk){
                    saveUpload();
                }

                if(isDownOk || isUpOk){ //有一个配置成功即可
                    //这里判断是走自定义服务器还是走测速点  自定义的话就模拟一个BeanAppFtpConfig返回
                    boolean CUSTOM_FTP_DOWN_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_DOWN_OPEN, false);
                    boolean CUSTOM_FTP_UPLOAD_OPEN = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().CUSTOM_FTP_UPLOAD_OPEN, false);
                    if(CUSTOM_FTP_DOWN_OPEN || CUSTOM_FTP_UPLOAD_OPEN){ //有一个勾选就是走自定义
                        BeanAppFtpConfig beanAppFtpConfig = new BeanAppFtpConfig();
                        beanAppFtpConfig.iD = "-1";//服务器id
                        //自定义服务器   下面数值不传
                        beanAppFtpConfig.operator = "";//运营商
                        beanAppFtpConfig.province = "";//省
                        beanAppFtpConfig.city = "";//地市
                        beanAppFtpConfig.hostType = "自定义服务器";

                        beanAppFtpConfig.customDownIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_IP, "");
                        beanAppFtpConfig.customDownPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_PORT, "");
                        beanAppFtpConfig.customDownPath = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_PATH, "");
                        beanAppFtpConfig.customDownThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_THREAD, "");
                        beanAppFtpConfig.customDownAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_USER, "");
                        beanAppFtpConfig.customDownPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_DOWN_PASSWD, "");

                        beanAppFtpConfig.customUpIp = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_IP, "");
                        beanAppFtpConfig.customUpPort = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PORT, "");
                        beanAppFtpConfig.customUpSize = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PATH, "");
                        beanAppFtpConfig.customUpThread = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, "");
                        beanAppFtpConfig.customUpAccount = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_USER, "");
                        beanAppFtpConfig.customUpPassword = SharedPreHandler.getShared(SpeedTest5g.getContext())
                                .getStringShared(TypeKey.getInstance().FTP_UPLOAD_PASSWD, "");
                        String ftpType = "ALL";
                        if(CUSTOM_FTP_DOWN_OPEN && CUSTOM_FTP_UPLOAD_OPEN){
                            ftpType = "ALL";
                        }else if(CUSTOM_FTP_DOWN_OPEN && !CUSTOM_FTP_UPLOAD_OPEN ){
                            ftpType = "DOWNLOAD";
                        }else if(!CUSTOM_FTP_DOWN_OPEN && CUSTOM_FTP_UPLOAD_OPEN ){
                            ftpType = "UPLOAD";
                        }
                        beanAppFtpConfig.ftpType = ftpType;//类型 上传下载：ALL, 上传：UPLOAD, 下载：DOWNLOAD
                        beanAppFtpConfig.serverType = false; //apn

                        SpeedTestDataSet.mBeanAppFtpConfig = beanAppFtpConfig;
                    }else{ //都没勾选  就走默认配置
                        ResponseAppFtpConfig response = SpeedTestDataSet.mResponseAppFtpConfig;
                        if(response != null){
                            ArrayList<BeanAppFtpConfig> list = response.datas;
                            //获取用户手动选择的ip
                            String ID = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("SPEED_TEST_HOST_ID", "");
                            if(TextUtils.isEmpty(ID)){
                                //设置默认第一条选中
                                if(list != null && list.size() > 0){
                                    BeanAppFtpConfig beanAppFtpConfig = list.get(0);
                                    SpeedTestDataSet.mBeanAppFtpConfig = beanAppFtpConfig;
                                }
                            }else{
                                if(list != null && list.size() > 0){
                                    for(BeanAppFtpConfig beanAppFtpConfig : list){
                                        if(ID.equals(beanAppFtpConfig.iD)){
                                            SpeedTestDataSet.mBeanAppFtpConfig = beanAppFtpConfig;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //发广播更新
                    Intent intent = new Intent(SpeedTestMainActivity.ON_UPDATE_FTP);
                    sendBroadcast(intent);

                    showCommon("保存生效成功");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(type == TypeKey.getInstance().HISTORY_CUSTOM_SET_DOWNLOAD){ //下载选中返回值
            if(isTrue){
                String downIp = object.toString();
                for(Db_CustomFtpConfig config : mDownloadList){
                    if(downIp.equals(config.customDownIp)){
                        mEtDownPath.setText(config.customDownPath);
                        mEtDownThread.setText(config.customDownThread);
                        mEtDownPort.setText(config.customDownPort);
                        mEtDownAccout.setText(config.customDownAccount);
                        mEtDownPassword.setText(config.customDownPassword);
                        break;
                    }
                }
            }
        }else if(type == TypeKey.getInstance().HISTORY_CUSTOM_SET_UPLOAD){ //上传选中返回值
            if(isTrue){
                String upIp = object.toString();
                for(Db_CustomFtpConfig config : mUploadList){
                    if(upIp.equals(config.customUpIp)){
                        mEtUpSize.setText(config.customUpSize);
                        mEtUpThread.setText(config.customUpThread);
                        mEtUpPort.setText(config.customUpPort);
                        mEtUpAccout.setText(config.customUpAccount);
                        mEtUpPassword.setText(config.customUpPassword);
                        break;
                    }
                }
            }
        }
    }

    /*
     * 保存下载
     */
    private boolean saveDownData() {
        if(mCbDown.isChecked()){
            String downIpStr = mEtDownIp.getText().toString();
            if (!UtilChinese.getInstance().isIP(downIpStr)) {
                showCommon("请输入正确的下载IP");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_IP, downIpStr);

            String downPathStr = mEtDownPath.getText().toString();
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
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_PATH, downPathStr);

            String downThreadStr = mEtDownThread.getText().toString();
            if (!downThreadStr.isEmpty()) {
                if (UtilHandler.getInstance().toInt(downThreadStr, 0) > 10) {
                    showCommon("下载线程数最大不能超过10个");
                    return false;
                } else if (UtilHandler.getInstance().toInt(downThreadStr, 0) <= 0) {
                    showCommon("下载线程数不能等于0");
                    return false;
                }
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_THREAD, downThreadStr);

            String downPort = mEtDownPort.getText().toString();
            if (downPort.isEmpty()) {
                showCommon("请输入正确的下载端口号");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_PORT, downPort);

            String downUserStr = mEtDownAccout.getText().toString();
            if (downUserStr.isEmpty()) {
                showCommon("请输入正确的下载帐号");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_USER, downUserStr);

            String downPasswdStr = mEtDownPassword.getText().toString();
            if (downPasswdStr.isEmpty()) {
                showCommon("请输入正确的下载密码");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_DOWN_PASSWD, downPasswdStr);
        }

        //最后才保存勾选项
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().CUSTOM_FTP_DOWN_OPEN, mCbDown.isChecked());

        return true;
    }

    private boolean saveUpData() {
        if(mCbUp.isChecked()){
            String uploadIpStr = mEtUpIp.getText().toString();
            if (!UtilChinese.getInstance().isIP(uploadIpStr)) {
                showCommon("请输入正确的上传IP");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_IP, uploadIpStr);

            String uploadPathStr = mEtUpSize.getText().toString();
            if (!uploadPathStr.isEmpty()
                    && UtilHandler.getInstance().toInt(uploadPathStr, 0) <= 0) {
                showCommon("上传文件大小必须大于0");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_PATH, uploadPathStr);

            String uploadThreadStr = mEtUpThread.getText().toString();
            if (!uploadThreadStr.isEmpty()) {
                if (UtilHandler.getInstance().toInt(uploadThreadStr, 0) > 5) {
                    showCommon("上传线程数最大不能超过5个");
                    return false;
                } else if (UtilHandler.getInstance().toInt(uploadThreadStr, 0) <= 0) {
                    showCommon("上传线程数不能等于0");
                    return false;
                }
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_THREAD, uploadThreadStr);

            String upPort = mEtUpPort.getText().toString();
            if (upPort.isEmpty()) {
                showCommon("请输入正确的上传端口号");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_PORT, upPort);

            String uploadUserStr = mEtUpAccout.getText().toString();
            if (uploadUserStr.isEmpty()) {
                showCommon("请输入正确的上传帐号");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_USER, uploadUserStr);

            String uploadPasswdStr = mEtUpPassword.getText().toString();
            if (uploadPasswdStr.isEmpty()) {
                showCommon("请输入正确的上传密码");
                return false;
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext())
                    .setStrShared(TypeKey.getInstance().FTP_UPLOAD_PASSWD, uploadPasswdStr);
        }

        //最后才保存勾选项
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().CUSTOM_FTP_UPLOAD_OPEN, mCbUp.isChecked());

        return true;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> queryHistory(int historyType, boolean isEncryption) {
        ArrayList<HistoryDb> queryObj = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND timeDate>?", new Object[] { historyType, TimeUtil.getInstance().getNowTimeSS(TimeUtil.getInstance().getDataTimeToLong(-30)) }, "timeDate DESC");
        ArrayList<String> arrHistory = new ArrayList<String>();
        if (queryObj != null && queryObj.size() > 0) {
            if (isEncryption) {
                for (HistoryDb historyDb : queryObj) {
                    arrHistory.add(Base64Utils.decryptorDes3(historyDb.getValueName()));
                }
            } else {
                for (HistoryDb historyDb : queryObj) {
                    arrHistory.add(historyDb.getValueName());
                }
            }
        }
        return arrHistory;
    }
}
