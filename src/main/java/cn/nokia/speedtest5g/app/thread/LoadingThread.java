package cn.nokia.speedtest5g.app.thread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.volley.util.BasicUtil;
import com.android.volley.util.EncryptionUtil;
import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_JJ_AppWarningLog;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.bean.Db_Modular;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.request.AppModularRequest;
import cn.nokia.speedtest5g.app.request.JJ_RequestAppWarning;
import cn.nokia.speedtest5g.app.request.JJ_RequestFtpTest;
import cn.nokia.speedtest5g.app.respon.AppModularResponse;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.HttpPostUtil;
import cn.nokia.speedtest5g.app.uitl.MyToSpile;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;

/**
 * 初始化线程...
 *
 * @author zwq
 *
 */
@SuppressWarnings("unchecked")
public class LoadingThread extends Thread {

    private Handler mHandler;

    public LoadingThread(Handler handler) {
        this.mHandler = handler;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        try {
            SharedPreHandler.getShared(SpeedTest5g.getContext()).initEncryption();
            MyToSpile.getInstances().clear();
            Context context = SpeedTest5g.getContext();
            getPhoneBaseInfo(context);
            String filePath = PathUtil.getInstances().getCurrentPath() + "/head/";
            cleanFileList(filePath);
            TypeKey.getInstance().VerName = BasicUtil.getUtil().getVersion(context, true).toString();
            //本次版本号
            Integer nowVersion = (Integer) BasicUtil.getUtil().getVersion(context, false);
            SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().VERSION_NAME,TypeKey.getInstance().VerName);
            //上次版本号
            Integer lastVersion = UtilHandler.getInstance().toInt(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().VERSION_CODE, ""), nowVersion);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_IMEI(), "");
            if (TextUtils.isEmpty(imei) ||
                    (imei.length() != 15 && imei.length() != 32)) {
                imei = tm.getDeviceId();
                SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_IMEI(), imei);
                if (TextUtils.isEmpty(imei) ||
                        (imei.length() != 15 && imei.length() != 32)) {
                    try {
                        //引用反射获取
                        imei = (String) tm.getClass().getMethod("getDeviceId", int.class).invoke(tm, TelephonyManager.PHONE_TYPE_GSM);
                        SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_IMEI(), imei);
                    } catch (Exception e) {

                    }
                }

                if (TextUtils.isEmpty(imei)) {
                    //读取本地文件是否存在,如果不存在则生成新的UUID作为IMEI并保存在本地
                    imei = readImei();
                    SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().PHONE_IMEI(), imei);
                }
            }
            
            SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_IMSI(), tm.getSubscriberId());
            SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_RELEASE(),android.os.Build.VERSION.RELEASE);
            if (SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_MODEL(), "").isEmpty()) {
                SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_MODEL(),android.os.Build.MODEL);
            }

            File file = new File(PathUtil.getInstances().getCurrentPath() + PathUtil.getInstances().getDbPath());
            if (file.exists() && file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (File f : listFiles) {
                    if (f.getName().endsWith(".db")) {
                        f.delete();
                    }
                }
            }

            //上传测试数据
            uploadFtpTestData();
            saveAppWarningLog();

            SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().VERSION_CODE,String.valueOf(nowVersion));
            if(TextUtils.isEmpty(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().PHONE_UUID(), ""))){
                SharedPreHandler.getShared(context).setStrShared(TypeKey.getInstance().PHONE_UUID(),UUID.randomUUID().toString());
            }
            readAppModular();

            Message msg = mHandler.obtainMessage();
            msg.obj = new MyEvents(ModeEnum.TASK, EnumRequest.OTHER_INIT_IMSI.toInt());
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除本地头像数据
    private void cleanFileList(String filePath){
        File dir = new File(filePath);
        if(!dir.exists()){
            return;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0){
            return;
        }
        for(File f : files){
            if (f.isDirectory()) {
                cleanFileList(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    private void getPhoneBaseInfo(Context context) {
        try {
            Camera camera = Camera.open();
            TypeKey.getInstance().cameraMaxZoom = camera.getParameters().getMaxZoom();
            camera.release();
            camera = null;
            TypeKey.getInstance().hasCamera = true;

            PackageManager pm = context.getPackageManager();
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            for (FeatureInfo f : features) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                    TypeKey.getInstance().hasFlashLight = true;
                    break;
                }
            }
        } catch (Exception e) {
            TypeKey.getInstance().hasCamera = false;
        }
    }

    //获取所有模块数据
    private boolean readAppModular(){
        String doPostJson = HttpPostUtil.getHttpGetUtil().doPostJson(NetWorkUtilNow.getInstances().getToIp() + SpeedTest5g.getContext().getString(R.string.URL_APP_MODULAR), JsonHandler.getHandler().toJson(new AppModularRequest()));
        if (!TextUtils.isEmpty(doPostJson)) {
            WybLog.i("readAppModular", doPostJson);
            AppModularResponse mAppModularResponse = JsonHandler.getHandler().getTarget(doPostJson, AppModularResponse.class);
            if (mAppModularResponse != null && mAppModularResponse.isRs()) {
                //删除本地已存模块数据
                DbHandler.getInstance().deleteClass(Db_Modular.class);
                if (mAppModularResponse.datas != null && mAppModularResponse.datas.size() > 0) {
                    for (Db_Modular item : mAppModularResponse.datas) {
                        DbHandler.getInstance().insert(item);
                    }
                }
                return true;
            }
        }
        return DbHandler.getInstance().queryCount(Db_Modular.class) > 0;
    }

    // 获取当前登录的用户ID
    private String getUserID(Context context) {
        return SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
    }

    private void uploadFtpTestData(){
        List<Db_JJ_FTPTestInfo> uploadFtpTestList = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance().queryObj(Db_JJ_FTPTestInfo.class, "isUpload =? and userid=?", new Object[]{false,UtilHandler.getInstance().toInt(getUserID(SpeedTest5g.getContext()), 0)});
        if(uploadFtpTestList!=null &&uploadFtpTestList.size()>0){
            for (int i = 0; i < uploadFtpTestList.size(); i++) {
                Db_JJ_FTPTestInfo ftpTestInfo = uploadFtpTestList.get(i);
                // 查找每秒下载(和上传)详情
                ftpTestInfo.downloadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
                        "lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
                        new Object[] { ftpTestInfo.get_id(), getUserID(SpeedTest5g.getContext()), 1 , ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd()});
                ftpTestInfo.uploadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
                        "lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
                        new Object[] { ftpTestInfo.get_id(), getUserID(SpeedTest5g.getContext()), 2 , ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd()});
            }

            JJ_RequestFtpTest request = new JJ_RequestFtpTest();
            request.setDatas(uploadFtpTestList);
            String doPost = HttpPostUtil.getHttpGetUtil().doPostJson(NetWorkUtilNow.getInstances().getToIp() + SpeedTest5g.getContext().getString(R.string.URL_SAVE_SPEED_TEST_LOG),JsonHandler.getHandler().toJson(request));

            if(!TextUtils.isEmpty(doPost)){
                BaseRespon response = JsonHandler.getHandler().getTarget(doPost, BaseRespon.class);
                if(response!=null){
                    if(response.isRs()){
                        List<Db_JJ_FTPTestInfo> list = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance().queryObj(Db_JJ_FTPTestInfo.class,"isUpload =? and userid=?",new Object[]{false,UtilHandler.getInstance().toInt(getUserID(SpeedTest5g.getContext()), 0)});
                        if(list!=null &&list.size()>0){
                            for(Db_JJ_FTPTestInfo info :list){
                                info.setUpload(true);
                                DbHandler.getInstance().updateObj(info);
                            }
                        }
                    }
                }
            }
        }

    }

    private void saveAppWarningLog() {
        List<Db_JJ_AppWarningLog> appWarningList = (List<Db_JJ_AppWarningLog>) DbHandler.getInstance().queryObj(Db_JJ_AppWarningLog.class, "isUpload =?", new Object[]{false});
        if(appWarningList!=null &&appWarningList.size()>0){
            JJ_RequestAppWarning request = new JJ_RequestAppWarning();
            request.setDatas(appWarningList);

            String doPost = HttpPostUtil.getHttpGetUtil().doPostJson(NetWorkUtilNow.getInstances().getToIp() + SpeedTest5g.getContext().getString(R.string.URL_SAVE_APP_WARNING_LOG),JsonHandler.getHandler().toJson(request));
            if(!TextUtils.isEmpty(doPost)){
                BaseRespon response = JsonHandler.getHandler().getTarget(doPost, BaseRespon.class);
                if(response!=null){
                    if(response.isRs()){
                        List<Db_JJ_AppWarningLog> list = (List<Db_JJ_AppWarningLog>) DbHandler.getInstance().queryObj(Db_JJ_AppWarningLog.class,"isUpload =?",new Object[]{false});
                        if(list!=null &&list.size()>0){
                            for(Db_JJ_AppWarningLog info :list){
                                info.setUpload(true);
                                DbHandler.getInstance().updateObj(info);
                            }
                        }
                    }
                }
            }
        }
    }

    private String readImei(){
        String imei = "";
        File file = new File(PathUtil.getInstances().getCurrentPath() + "/.android/.wyb");
        if (file == null || !file.exists() || !file.isFile()){
            imei = UUID.randomUUID().toString().replace("-", "");
            outFile(imei);
            return imei;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            String text = null;
            while((text = bufferedReader.readLine()) != null){
                sb.append(text);
            }
            imei = EncryptionUtil.decode(sb.toString());
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        }catch (Exception e){

        }
        if (TextUtils.isEmpty(imei)){
            imei = String.valueOf(System.currentTimeMillis());
        }
        return imei;
    }

    private void outFile(String content){
        try {
            File file = new File(PathUtil.getInstances().getExPath() + "/.android/");
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(PathUtil.getInstances().getCurrentPath() + "/.android/.wyb");
            //文件不存在时候，主动创建文件。
            file.createNewFile();
            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(EncryptionUtil.encode(content));
            bw.close();
            fw.close();
        }catch (Exception e){

        }
    }
}
