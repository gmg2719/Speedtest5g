package cn.nokia.speedtest5g.app.thread;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.toolbox.MyVolleyError;
import com.android.volley.util.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.downdprogress.MyProgressDialog;
import cn.nokia.speedtest5g.dialog.loading.LoadingDialog;

/**
 *  作者	：zwq
 *  描述	:文件下载线程
 */
public class DownFileAsyncTask extends AsyncTask<String, Integer, Boolean>{
	
	private Context mContext;
	/**
	 * 文件文件大小
	 */
	private int fileLength;
	/**
	 * 剩余控件不足
	 */
	private final int MINUS = -1;
	/**
	 * 创建下载进度条
	 */
	private final int CREATE = 0;
	/**
	 * 异常情况
	 */
	private final int ERROR = 1;
	/**
	 * 更新进度
	 */
	private final int REFRESH = 2;
	/**
	 * 关闭
	 */
	private final int CLOSE = 3;
	/**
	 * 下载失败
	 */
	private final int FAILURE = 4;
	/**
	 * 安装程序apk
	 */
	private final int FINISH = 5;
	/**
	 * 错误提示
	 */
	private final int ERRORSTR = 6;
	/**
	 * 提示内容
	 */
	private String msg;
	/**
	 * 文件输出流- 读取网络文件到本地
	 */
	private FileOutputStream out;
	private InputStream inputStream;
	/**
	 * 是否取消下载
	 */
	private boolean isClose;
	/**
	 * 加载进度条
	 */
	private LoadingDialog loadingDialog;
	/**
	 * 下载进度对话框
	 */
	private MyProgressDialog progressDialog;
	private File file;
	/**
	 * apk名称：如(demo.apk,abc.text)
	 */
	private String fileName;
	/**
	 * 下载地址
	 */
	private String strUrl;
	/**
	 * 下载存放路径 
	 */
	private String strLocPath;
	/**
	 * 当前下载是否是apk
	 */
	private boolean isApk;
	
	private HttpURLConnection conn = null;
	/**
	 * 下载完是否退出程序回调标识
	 */
	private boolean mIsExit;
	
	private ListenerBack mListenerBack;
	/**
	 * 是否显示加载对话框及进度
	 */
	public boolean isShowLoading = true;
	/**
	 * 下载进度更新对象，当isShowLoading=false时有值
	 */
	public Object mProUpdateObj;
	//是否提示错误或成功消息,默认显示
	private boolean isShowToast = true;
	//是点击取消操作
	private int isCloseDialog = 1001;
	//不是点击取消操作
	private int notCloseDialog = 1002;
	//当成功或失败时返回的what状态
	private int mWhat = -1;
	
	/**
	 * @param context 上下文
	 * @param locPath 下载保存路径---空为默认自带，否则以/结束
	 * @param locFileName 文件名称（必须带后缀）
	 * @param strUrl 下载地址
	 * @param isExit 取消是否退出程序
	 * @param listenerBack 回调
	 */
	public DownFileAsyncTask(Context context,String locPath,String locFileName,String strUrl,boolean isExit,ListenerBack listenerBack){
		this.mContext 	   = context;
		this.strLocPath    = locPath;
		this.fileName 	   = locFileName;
		this.strUrl		   = strUrl;
		this.mIsExit 	   = isExit;
		this.mListenerBack = listenerBack;
		this.isApk         = fileName.endsWith(".apk");
	}
	
	/**
	 * 回调用的类型
	 * @param what
	 * @return
	 */
	public DownFileAsyncTask setWhat(int what){
		this.mWhat = what;
		return this;
	} 
	
	/**
	 * 是否显示消息提示内容
	 */
	public DownFileAsyncTask showToastType(boolean isShow){
		this.isShowToast = isShow;
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		if (isShowLoading) {
			loadingDialog = new LoadingDialog(mContext);
			loadingDialog.setListener(mListenerBack);
			loadingDialog.show();
		}
		super.onPreExecute();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected Boolean doInBackground(String... params) {
		//判断剩余空间
		StatFs statfs;
		//判断是否有Sd卡
		if (TextUtils.isEmpty(strLocPath)) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				strLocPath = Environment.getExternalStorageDirectory().getPath() + "/apkdown/";
					file = new File(strLocPath);
					statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			}else {
				file = mContext.getFilesDir();
				strLocPath = file.getPath() + "/";
				statfs = new StatFs("/data");
			}
			//目录是否存在,若不存在就创建
			if (!file.exists()) {
				file.mkdirs();
			}
		}else {
			file = new File(strLocPath);
//			if (strLocPath.endsWith("/")) statfs = new StatFs("/data");
			//目录是否存在,若不存在就创建
			if (!file.exists()) {
				file.mkdirs();
			}
			statfs = new StatFs(strLocPath);
		}
		file = new File(strLocPath + fileName);
		
		//判断当前文件是否存在,若存在就删除
		if (file.exists()) {
			file.delete();
		}
		strUrl = getUtf8Url(strUrl);
		WybLog.i("DownApkAsyncTask", "下载保存路径： "+file.getPath() + "下载地址：" + strUrl);
		
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (conn!=null) {
				//设置连接主机超时（单位：毫秒） 
				conn.setConnectTimeout(8*1000); 
				conn.setRequestMethod("GET");
				//设置从主机读取数据超时（单位：毫秒） 
//				conn.setReadTimeout(1800000); 
				conn.setRequestProperty("Accept-Encoding", "identity");
				if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
					conn.setRequestProperty("sid",NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
					conn.setRequestProperty("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
				}
				//连接
				conn.connect();
				if (loadingDialog != null && !loadingDialog.isShowing() && isShowLoading) {
					return false;
				}
				//获得文件流
				inputStream = conn.getInputStream();
				//获得文件长度
				fileLength = conn.getContentLength();
				
				if (fileLength == -1) {
					msg = "无法获取文件大小";
					publishProgress(ERRORSTR);
					return false;
				}
				
				//获取手机上剩余空间大小
				long blockSize = statfs.getBlockSize(); 
				long availableBlocks = statfs.getAvailableBlocks();
				long size = availableBlocks * blockSize;
				WybLog.i("DownApkAsyncTask", "剩余空间： " + size + "文件大小：" + fileLength);
				if (fileLength*2>size) {
					publishProgress(MINUS);
					return false;
				}
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					out = new FileOutputStream(file);
				}else {
					out = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
				}
				//创建进度条
				if (isShowLoading) {
					publishProgress(CREATE);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				//开始下载
				boolean isDowm = startDowmApk();
				if (isDowm) {
					publishProgress(CLOSE);
					return true;
				}else {
					//下载错误，删除未完整的.APK
					if (file.exists()) file.delete();
				}
			}else {
				publishProgress(ERROR);
			}
		}catch (IOException e) {
			if (!isClose) {
				try {
				msg = MyVolleyError.read(String.valueOf(conn.getResponseCode()));
					publishProgress(ERRORSTR);
				} catch (IOException e1) {
					publishProgress(ERROR);
				}
			}
		}finally{
			try {
				publishProgress(CLOSE);
				if (out!=null) {
					//关闭流
					out.close();
				}
				if (inputStream!=null) {
					inputStream.close();
				}
				if (conn!=null) {
					//关闭网络
					conn.disconnect();
				}
			} catch (IOException e) {}
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			if (isApk) {
				InstallationApk(file.getPath());
			}else {
				if (mListenerBack != null) {
					if (mWhat != -1) {
						mListenerBack.onListener(mWhat,null,true);
					//如果文件下载完成后回调	
					}else if (strUrl.contains("buildnet")) {
						mListenerBack.onListener(EnumRequest.TASK_DOWN_FILE_COMPLETE.toInt(),strLocPath + fileName,mIsExit);
					} else {
						mListenerBack.onListener(EnumRequest.TASK_DOWN_FILE_COMPLETE.toInt(),mProUpdateObj,mIsExit);
					}
				}
			}
		}
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
		switch (values[0]) {
		case MINUS:
			palyerToast("手机剩余空间不足,请删除部分文件后再下载.");
			break;
		/*创建进度条*/
		case CREATE:
			progressDialog = new MyProgressDialog(mContext);
			progressDialog.showProgress(btnClickListener);
			progressDialog.setData(0, "0/" + UtilHandler.getInstance().toDfSum(fileLength/(1024f*1024f), "00") + "M");
			break;
		/*下载错误*/
		case ERROR:
			if (progressDialog!=null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			palyerToast("服务器连接异常,请检查网络是否通畅.");
			closeError(notCloseDialog);
			break;
		case REFRESH:
			int value = values[1];
			if (progressDialog != null) {
				progressDialog.setData((int)((Float.intBitsToFloat(value)/Float.intBitsToFloat(fileLength))*100),
						   UtilHandler.getInstance().toDfStr(value/1024f/1024f, "00") + "/" + 
								   UtilHandler.getInstance().toDfSum(fileLength/(1024f*1024f), "00") + "M");
			}
			if (mListenerBack != null) {
				if (mProUpdateObj != null) {
//					if (mProUpdateObj instanceof DbInfo) {
//						DbInfo mDbInfo = (DbInfo) mProUpdateObj;
//						mDbInfo.setSize((int)((Float.intBitsToFloat(value)/Float.intBitsToFloat(fileLength))*100));
//						if (mDbInfo.getSize() >= mDbInfo.getSizeLast() + 1) {
//							mDbInfo.setSizeLast(mDbInfo.getSize());
//							mListenerBack.onListener(EnumRequest.TASK_DOWN_PROGRESS.toInt(), mDbInfo, false);
//						}
//					}
				}else {
					mListenerBack.onListener(EnumRequest.TASK_DOWN_PROGRESS.toInt(), (int)((Float.intBitsToFloat(value)/Float.intBitsToFloat(fileLength))*100), false);
				}
			}
			break;
		case CLOSE:
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			break;
		case FAILURE:
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			palyerToast("下载失败,请检查网络是否通畅.");
			closeError(notCloseDialog);
			break;
		case FINISH:
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
//			if (mContext != null && mContext instanceof Activity) {
//				if (!((Activity)mContext).isFinishing()) {
//					((Activity)mContext).finish();
//				}
//			}
			break;
		case ERRORSTR:
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			palyerToast(msg);
			closeError(notCloseDialog);
			break;
		}
	}
	
	/**
	 * 处理url包含中文问题
	 * @param url
	 * @return
	 */
	private String getUtf8Url(String url) {
		char[] chars = url.toCharArray();
		StringBuilder utf8Url = new StringBuilder();
		final int charCount = chars.length;
		for (int i = 0; i < charCount; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 1) {
				utf8Url.append(chars[i]);
			}else{
				try {
					utf8Url.append(URLEncoder.encode(String.valueOf(chars[i]), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		return utf8Url.toString();
	}
	
	/**
	 * 开始下载
	 */
	private boolean startDowmApk() throws IOException{
		int count = 0;
		int totalLength = 0;
		byte[] buffer = new byte[1024];
		while((count=inputStream.read(buffer))>0){
			if (isClose){
				//取消下载，删除未完整的.APK
				if (file.exists()) file.delete();
				return false;
			} 
			out.write(buffer, 0, count);
			totalLength+=count;
			publishProgress(REFRESH,totalLength);
			WybLog.d("DowmApkAsyncTsk", "已下载：" + totalLength+"---总长：" + fileLength);
		}
		//下载成功
		if (totalLength == fileLength) return true;
		
		//可能丢包，所以下载不成功
		publishProgress(FAILURE);
		return false;
		
	}
	
	/**
	 * 安装apk
	 */
	private void InstallationApk(String apkPath){
		publishProgress(FINISH);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(Build.VERSION.SDK_INT >= 24) {
		    String packageName = SystemUtil.getInstance().getPackageName(mContext);
            Uri contentUri = FileProvider.getUriForFile(mContext, String.format("%s"+".fileProvider", packageName), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
     	}else{
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
	}
	
	/**
	 *  描述	：提示消息
	 */
	private void palyerToast(String text){
		if (isShowToast) {
			MyToast.getInstance(mContext).showCommon(text,Gravity.CENTER);
		}
	}

	private OnClickListener btnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isClose = true;
			publishProgress(CLOSE);
			closeError(isCloseDialog);
		}
	};
	
	private void closeError(int isCancle){
		if (mListenerBack != null) {
			if (mWhat != -1) {
				mListenerBack.onListener(mWhat,null,false);
			}else if (isShowToast) {
				mListenerBack.onListener(EnumRequest.TASK_DOWN_APK.toInt(),mProUpdateObj,mIsExit);
			} else {
				if (isCancle == isCloseDialog) {
					mListenerBack.onListener(EnumRequest.DIALOG_NET_DISMISS.toInt(),mProUpdateObj,mIsExit);
				} else {
					mListenerBack.onListener(EnumRequest.TASK_DOWN_APK.toInt(),mProUpdateObj,mIsExit);
				}
			}
		}
	}

	/**
	 * 退出下载
	 */
	public void dissLoading() {
		isClose = true;
		publishProgress(CLOSE);
		closeError(notCloseDialog);
	}
}
