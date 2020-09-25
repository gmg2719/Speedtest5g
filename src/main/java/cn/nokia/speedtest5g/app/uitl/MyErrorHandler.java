package cn.nokia.speedtest5g.app.uitl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import cn.nokia.speedtest5g.app.activity.LoadingActivity;
import com.android.volley.util.MyToast;
import com.fjmcc.wangyoubao.app.signal.SignalServiceUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.Gravity;

/**
 * 注册全局异常捕获
 * @author zwq
 * 
 */
public class MyErrorHandler implements UncaughtExceptionHandler {
	
	private static MyErrorHandler myErrorHandler ;  
	
	public static synchronized MyErrorHandler getInstance(){  
        if(myErrorHandler!=null){   
            return myErrorHandler;  
        }else {  
        	myErrorHandler  = new MyErrorHandler();  
            return myErrorHandler;  
        }  
    } 
	
	private Context mContext;
	public void init(Context context){
		this.mContext = context;
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		try {
			SignalServiceUtil.getInstances().stopService(mContext);
		} catch (Exception e) {
			WybLog.syso("erroe:" + e.getMessage());
		}
		WybLog.syso("程序挂掉了 ");  
        // 3.把错误的堆栈信息 获取出来   
        String errorinfo = getErrorInfo(arg1);  
        WybLog.e("error", errorinfo);
        if(errorinfo.contains("OutOfMemoryError")){
        	handleException(arg1);
        	// killProcess会导致软件重启，友好提醒显示失败：“数据量大，处理异常,即将退”。故要sleep2秒
        	try {
        		Thread.interrupted();
        		Thread.sleep(2000);
			} catch (Exception e) {
			}
        }
        // 重新启动程序，注释上面的退出程序
        Intent intent = new Intent();
        intent.setClass(mContext, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        //使用Toast来显示异常信息  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                MyToast.getInstance(mContext).showCommon( "数据量大，处理异常,即将退出!",Gravity.CENTER);
                Looper.loop();  
            }  
        }.start();  
        return true;  
    }  

    /** 
     * 获取错误的信息  
     * @param arg1 
     * @return 
     */  
    private String getErrorInfo(Throwable arg1) {  
        Writer writer = new StringWriter();  
        PrintWriter pw = new PrintWriter(writer);  
        arg1.printStackTrace(pw);  
        pw.close();  
        String error= writer.toString();  
        return error;  
    }
}
