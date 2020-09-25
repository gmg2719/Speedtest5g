package cn.nokia.speedtest5g.jzxh.ui.nsa;

import java.io.File;

import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.DownFileAsyncTask;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class JzxhNsaDownloadModule {

	private static final int WHAT_DOWNLOAD = 0;

	private String strPathBit = PathUtil.getInstances().getCurrentPath() + File.separator
			+ "jzxhDownload" + File.separator; 
	private String strPathName = "ZGJZ.mp4";
	private String strDownloadUrl = "http://xunlei.xiazai-zuida.com/1910/Z国机长.HC清晰版.mp4";

	private static JzxhNsaDownloadModule instances = null;
	private Handler mHandler = null;

	private DownFileAsyncTask mDownFileAsyncTask = null;
	private Context mContext = null;

	public static JzxhNsaDownloadModule getInstances(Context context){
		if(instances == null){
			instances = new JzxhNsaDownloadModule(context);
		}
		return instances;
	}

	private JzxhNsaDownloadModule(Context context){
		this.mContext = context;
		initHandler();
	}

	public void startDownload(){
		mHandler.sendEmptyMessageDelayed(WHAT_DOWNLOAD, 1000);
	}

	/**
	 * 开始下载
	 */
	public void download(){
		//下载csv
		mDownFileAsyncTask = new DownFileAsyncTask(mContext, strPathBit, strPathName, strDownloadUrl, false, new ListenerBack() {
			@Override
			public void onListener(int type, Object object, boolean isTrue) {
				if (type == 1000) {
					if (isTrue) {
						mDownFileAsyncTask = null;
						//删除下载好的文件
						File file = new File(strPathBit + strPathName);
						if(file.exists()){
							file.delete();
						}
						//重新下载
						mHandler.sendEmptyMessageDelayed(WHAT_DOWNLOAD, 1000);
					}else{ //下载失败
						WybLog.syso("download csv fail...");
					}
				}					
			}
		});
		mDownFileAsyncTask.isShowLoading = false;
		mDownFileAsyncTask.showToastType(false).setWhat(1000).execute();
	}

	public void stopDownload(){
		File file = new File(strPathBit + strPathName);
		if(file.exists()){
			file.delete();
		}
		mHandler.removeMessages(WHAT_DOWNLOAD);

		if(mDownFileAsyncTask != null){
			mDownFileAsyncTask.dissLoading();
		}
	}

	@SuppressLint("HandlerLeak")
	private void initHandler(){

		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
				case WHAT_DOWNLOAD:
					download();
					break;
				}
			}
		};
	}



}
