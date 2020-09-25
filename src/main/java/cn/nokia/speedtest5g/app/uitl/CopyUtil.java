package cn.nokia.speedtest5g.app.uitl;

import cn.nokia.speedtest5g.app.SpeedTest5g;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
/**
 * 文本复制到剪切板
 * @author xujianjun
 *
 */
public class CopyUtil {
	
	private static CopyUtil mInstance;
	
	public static CopyUtil getInstance(){
		if(mInstance==null){
			mInstance = new CopyUtil();
		}
		return mInstance;
	}
	
	/**
	 * 复制文本
	 * @param content
	 */
	public void copyContent(String content){
		ClipboardManager cm = (ClipboardManager) SpeedTest5g.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData mClipData  = ClipData.newPlainText("testLable", content);  
		cm.setPrimaryClip(mClipData);
	}
	
	

}
