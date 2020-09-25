package cn.nokia.speedtest5g.app.uitl;

import java.io.File;
import java.io.PrintStream;
import cn.nokia.speedtest5g.util.TimeUtil;
import com.android.volley.VolleyLog;
import com.litesuits.orm.log.OrmLog;
import com.nostra13.universalimageloader.utils.L;
import android.util.Log;

/**
 * 日志打印管理类
 * @author zwq
 *
 */
public class WybLog {

	private static boolean DEBUG = true;
	
	/**
	 * 设置是否打印日志
	 * @param isDebug
	 */
	public static void setDebug(boolean isDebug){
		//设置Volley是否打印日志
		VolleyLog.DEBUG = isDebug;
		//是否打印imageloader日志
		L.writeLogs(isDebug);
		DEBUG = isDebug;
		//关闭数据库日志
		OrmLog.isPrint = isDebug;
//		if (!isDebug) {
//			System.setErr(null);
//		}
//		if (isDebug) {
//			isFilePrin();
//		}
	}
	
	public static boolean isDebug(){
		return DEBUG;
	}
	
	public static void syso(Object msg){
		if (DEBUG) {
			System.out.println(String.valueOf(msg));
		}
	}
	
	public static void syso(String tag,String msg){
		if (DEBUG) {
			System.out.println(tag + "：" + msg);
		}
	}
	
	public static void syse(String msg){
		if (DEBUG) {
			System.err.println(msg);
		}
	}
	
	public static void syse(String tag,String msg){
		if (DEBUG) {
			System.err.println(tag + "：" + msg);
		}
	}
	
	public static void i(String tag,String msg){
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void i(String msg){
		if (DEBUG) {
			Log.i("LogUtil", msg);
		}
	}

	public static void e(String tag,String msg){
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}
	
	public static void d(String tag,String msg){
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}
	
	public static void w(String tag,String msg){
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}
	
	public synchronized static void sysoD(String msg){
		if (DEBUG) {
			System.out.println(TimeUtil.getInstance().getNowTimeGis()+" "+msg);
		}
	}
	
	public static void isFilePrin(){
		try {
			File file = new File(PathUtil.getInstances().getCurrentPath() + "/LogSystem");
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(file.getPath() + "/" + TimeUtil.getInstance().getNowTime(System.currentTimeMillis()) + ".txt");
			if (!file.exists()) {
				PrintStream mPrintStream = new PrintStream(file);
				System.setOut(mPrintStream);
			}
		} catch (Exception e) {
			syse("错误：" + e.getMessage());
		}
	}
}
