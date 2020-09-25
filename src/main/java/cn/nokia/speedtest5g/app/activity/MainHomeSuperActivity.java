package cn.nokia.speedtest5g.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.volley.util.SharedPreHandler;
import com.fjmcc.wangyoubao.app.signal.SignalServiceUtil;

import java.io.Serializable;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.listener.MyLocationListener;
import cn.nokia.speedtest5g.app.thread.JzgcDataUpdateRunnable;
import cn.nokia.speedtest5g.app.uitl.PowerManagerUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.SpeedTestHistoryActivity;
import cn.nokia.speedtest5g.speedtest.SpeedTestMainActivity;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;

/**
 * 网优宝主页框架tab
 * @author zwq
 *
 */
public class MainHomeSuperActivity extends MyTabActivity {

    private RadioGroup mRadioGroup = null;
    private RadioButton mRadioButtonCs = null;
    private RadioButton mRadioButtonWd = null;
    private View mViewToastPersona = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initBarTitle();
        ((RadioButton) findViewById(R.id.mianhomeSuper_rb_cs)).setChecked(true);
        mRadioGroup = (RadioGroup)findViewById(R.id.mianhomeSuper_tab_rg);
        mRadioGroup.setOnCheckedChangeListener(checkedChangeListener);

        mRadioButtonCs = (RadioButton)findViewById(R.id.mianhomeSuper_rb_cs);
        mRadioButtonWd = (RadioButton)findViewById(R.id.mianhomeSuper_rb_wd);

        MyLocationListener.getInstances().onStarts();
        // 这里启动信号采集服务
        SignalServiceUtil.getInstances().startService(this);
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().IS_MAIN_FINISH(), false);
        if (!"00".equals(getUserID())) {
            new Thread(new JzgcDataUpdateRunnable(null, -1)).start();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
        filter.addAction(TypeKey.getInstance().ACTION_HOME_TAG_CHANGE);
        registerReceiver(mainhomeSuperCast, filter);

        //开启电源锁
        PowerManagerUtil.acquireWakeLock(MainHomeSuperActivity.this);
        onNewIntent(getIntent());

        refreshRadioButton();
        //防止未请求到数据
        SpeedTestServerRequestUtil.getInstance().requestServerData();
    }

    /**
     * 刷新按钮
     */
    private void refreshRadioButton(){
        String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
        if("Phone".equals(loginType)){
            mRadioButtonWd.setText("我的");
        }else if("Tourist".equals(loginType)){
            mRadioButtonWd.setText("未登录");
        }
    }

    @Override
    public void onDestroy() {
        if (isFinishExit) {
            installStatistics(R.string.code_exit_no);
        } else {
            installStatistics(R.string.code_exit);
        }
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().IS_MAIN_FINISH(), true);
        if (isFinishExit) {
            //			System.exit(0);
            //			int pid = android.os.Process.myPid();
            //			android.os.Process.killProcess(pid);
//            ((MyApplication)getApplication()).onTerminate();
        }else {
            // 关闭被动测试
            SignalServiceUtil.getInstances().stopService(this);
            MyLocationListener.getInstances().stop();
        }
        try {
            unregisterReceiver(mainhomeSuperCast);
        } catch (Exception e) {
        }
        //关闭电源锁
        PowerManagerUtil.releaseWakeLock();

        super.onDestroy();
    }

    private long lastTime = 0;
    // 是否是退出程序
    private boolean isFinishExit = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if(System.currentTimeMillis() - lastTime >= 2000){
                    lastTime = System.currentTimeMillis();
                    showCommonBottom(getResources().getString(R.string.app_exit));
                    return false;
                }
                else{
                    isFinishExit = true;
                    SharedPreHandler.getShared(this).setStrShared(TypeKey.getInstance().YJZD_LAST_DIAGNOSIS_CONDITION_BY_CI, "");
                    this.finish();
                }
            }
            return true;
        }
        return true;
    }

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup arg0, int arg1) {
            Intent intent = null;
            if (arg1 == R.id.mianhomeSuper_rb_cs) {//测速
                setTag("SpeedTestMainActivity", false);
                //广播刷新
                intent = new Intent(SpeedTestMainActivity.ON_RESUME_UPDATE);
                sendBroadcast(intent);
            } else if (arg1 == R.id.mianhomeSuper_rb_ls) {//历史
                setTag("SpeedTestHistoryActivity", false);
                //广播刷新
                intent = new Intent(SpeedTestHistoryActivity.ON_RESUME_UPDATE);
                sendBroadcast(intent);
            } else if (arg1 == R.id.mianhomeSuper_rb_gj) {//工具
                setTag("MainHomeWorkflowActivity", false);
                sendMainHomeType(EnumRequest.OTHER_MAINHOME_TYPE_OPTIMIZER.toInt(), null);
            } else if (arg1 == R.id.mianhomeSuper_rb_wd) {//我的
                setTag("MainHomePersonalActivity", false);
                if (mViewToastPersona != null && mViewToastPersona.getTag() != null) {
                    intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
                    intent.putExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_PERSONAL.toInt());
                    intent.putExtra("updateDay", (Integer) mViewToastPersona.getTag());
                    sendBroadcast(intent);
                }
                //广播刷新
                intent = new Intent(MainHomePersonalActivity.ON_RESUME_UPDATE);
                sendBroadcast(intent);
            }
        }
    };

    private void sendMainHomeType(int type,Object obj){
        Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_WORKFLOW);
        intent.putExtra("type", type);
        if (obj != null && obj instanceof Serializable) {
            intent.putExtra("data", (Serializable)obj);
        }
        MainHomeSuperActivity.this.sendBroadcast(intent);
    }

    private void sendMainHomeType(int type, int requestCode) {
        Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_WORKFLOW);
        intent.putExtra("type", type);
        intent.putExtra("requestCode", requestCode);
        MainHomeSuperActivity.this.sendBroadcast(intent);
    }

    private void sendMainHomeBroadcast(int requestCode) {
        Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_HOME);
        intent.putExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_HOME.toInt());
        intent.putExtra("requestCode", requestCode);
        sendBroadcast(intent);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_mainhome_super;
    }

    @Override
    public String[] getClassName() {
        return new String[] {
                "cn.nokia.speedtest5g.speedtest.SpeedTestMainActivity",
                "cn.nokia.speedtest5g.speedtest.SpeedTestHistoryActivity",
                "cn.nokia.speedtest5g.app.activity.MainHomeWorkflowActivity",
                "cn.nokia.speedtest5g.app.activity.MainHomePersonalActivity"
        };
    }

    @Override
    public void initStatistics() {
        installStatistics(R.string.code_login);
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        //游客身份提示对话框选择返回
        if (type == EnumRequest.OTHER_MAINHOME_TYPE_USER_TOURIST.toInt()) {
            if (isTrue) {
                //				toLoginUi();
                goIntent(LoginActivity.class, null, false);
            }
        }
    }

    /**
     * 主页框架页面广播
     */
    private BroadcastReceiver mainhomeSuperCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if(intent.getAction().equals(TypeKey.getInstance().ACTION_MAINHOME_SUPER)){
                    int type = intent.getIntExtra("type", -1);
                    if(type == -1000){
                        refreshRadioButton();
                        //游客身份提示
                    }else if (type == EnumRequest.OTHER_MAINHOME_TYPE_USER_TOURIST.toInt()) {
                        new CommonDialog(MainHomeSuperActivity.this).setListener(MainHomeSuperActivity.this).setButtonText("立即登录", "稍后再说").show("您当前是游客身份无权限访问\n是否进行登录？",
                                EnumRequest.OTHER_MAINHOME_TYPE_USER_TOURIST.toInt());
                        //跳转到登录页面
                    }else if (type == EnumRequest.OTHER_MAINHOME_TO_LOGIN.toInt()) {
                        toLoginUi();
                        //工参未下载或N天未更新时提示红点
                    }else if (type == EnumRequest.OTHER_MAINHOME_TYPE_PERSONAL.toInt()) {
                        //					if (mViewToastPersona == null) {
                        //						mViewToastPersona = findViewById(R.id.mianhomeSuper_ivToast_personal);
                        //					}
                        //					int updateDay = intent.getIntExtra("updateDay", 0);
                        //					mViewToastPersona.setVisibility(updateDay >= 7 ? View.VISIBLE : View.GONE);
                        //					mViewToastPersona.setTag(updateDay);
                    }
                }else if(intent.getAction().equals(TypeKey.getInstance().ACTION_HOME_TAG_CHANGE)){
                    int type = intent.getIntExtra("type", -1);
                    if(type == 1000){
                        mRadioButtonCs.setChecked(true);
                        setTag("SpeedTestMainActivity", false);
                    }
                }
            }
        }
    };

    private void toLoginUi(){
        //		SharedPreHandler.getShared(MyApplication.getContext()).setIntShared(TypeKey.getInstance().LOGIN_SELECT_ID, 0);
        //		isFinishExit = false;
        //		goIntent(LoginActivity.class, null, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == MainHomePersonalActivity.RESULT_CODE_PZ){ //拍照
            Intent intent = new Intent(MainHomePersonalActivity.ON_SELECT);
            intent.putExtra("SELECT", MainHomePersonalActivity.RESULT_CODE_PZ);
            MainHomeSuperActivity.this.sendBroadcast(intent);
        }else if(requestCode == MainHomePersonalActivity.RESULT_CODE_CJ){ //裁剪
            Intent intent = new Intent(MainHomePersonalActivity.ON_SELECT);
            intent.putExtra("SELECT", MainHomePersonalActivity.RESULT_CODE_CJ);
            MainHomeSuperActivity.this.sendBroadcast(intent);
        }else if (requestCode == EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt()) {
            sendMainHomeType(EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt(), null);

        } else if (requestCode == EnumRequest.OTHER_MAINHOME_UPDATE_VIEW.toInt()) {
            if (data != null) {
                sendMainHomeType(EnumRequest.OTHER_MAINHOME_UPDATE_VIEW.toInt(), data.getIntExtra("requestCode", 0));
            }
        } else if (requestCode == EnumRequest.OTHER_MAINHOME_UPDATE_HOME_INIT_DATA.toInt()) {
            sendMainHomeBroadcast(requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
