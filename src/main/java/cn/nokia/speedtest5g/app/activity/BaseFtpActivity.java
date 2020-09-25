package cn.nokia.speedtest5g.app.activity;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.nokia.speedtest5g.app.KeyValue;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Db_DtBusinessInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.app.uitl.ftp.FTP;
import cn.nokia.speedtest5g.app.uitl.ftp.FtpState;
import cn.nokia.speedtest5g.app.uitl.ftp.FtpUtil;
import cn.nokia.speedtest5g.app.uitl.ftp.FTP.DeleteFileProgressListener;
import cn.nokia.speedtest5g.app.uitl.ftp.FTP.DownLoadProgressListener;
import cn.nokia.speedtest5g.app.uitl.ftp.FTP.UploadProgressListener;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import com.android.volley.util.SharedPreHandler;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * FTP上传下载速率父类公用
 * @author zwq
 */
public abstract class BaseFtpActivity extends BaseActionBarActivity {

	protected static final int PACAKGE_SIZE_LARGE = 3;
	protected static final int PACAKGE_SIZE_MIDDLE = 2;
	protected static final int PACAKGE_SIZE_SMALL = 1;

	public Context mContext;
	//要下载的文件名
	private String strDownName;
	//要上传的文件名
	private String strUploadName;
	//服务器下载目录
	private String serverDownPath;
	//服务器上传目录
	private String serverUploadPath = "/upload/";
	//更新频率
	private long updateTime = 1000;
	//每次上传下载的大小 1024=1kb
	private int sizeEvery = 1024 * 2;
	//支持最大线程数量
	private int maxDownThread = 3 ,maxUploadThread = 1;
	//最大下载上传时长
	private long timeOut = 20 * 1000;
	//最大支持下载大小
	private long maxDownSize = 1024*1024*4,maxUploadSize = 1024*1024*4;
	//本地保存目录
	private String mLocalPath = "";

	//处理主线程
	private Handler mHandler,mTHandler;

	private HandlerThread handlerThread;

	//FTP连接成功
	private final int FTP_OK = 1;
	//正在下载中
	private final int DOWN_PROGRESS = 2;
	//下载成功
	private final int DOWN_OK = 3;
	//下载错误或者其他
	private final int DOWN_ERROR = 4;
	//下载错误或者其他
	private final int DOWN_ERROR_NOFILE = 13;

	//上传中
	private final int UPLOAD_PROGRESS = 5;
	//上传成功
	private final int UPLOAD_OK = 6;
	//上传错误或者其他
	private final int UPLOAD_ERROR = 7;
	//上传连接FTP失败
	private final int UPLOAD_ERROR_CON = 8;

	//下载超时
	private final int DOWN_TIMEOUT = 10;
	//上传超时
	private final int UPLOAD_TIMEOUT = 11;
	//连接超时
	private final int CONN_TIME = 12;
	//上传文件创建完成
	private final int UPLOAD_FILE_OVER = 14;
	// 下载链接FTP失败
	private final int DOWN_ERROR_CON = 15;

	//上传下载的线程
	private List<FtpRunnable> mArrFtpList;
	//当前模式，是否有要求启动下载，是否有要求启动上传
	private boolean isNowDown,enabledDown,enabledUpload;
	//下载到本地的文件名时间
	private String[] downNames,uploadNames;
	protected int mDeleteCount = 4;
	protected long mAllTestTimeForLarge = 10 * 1000; //大包
	protected long mUpdateTimeForLarge = 500;
	protected long mAllTestTimeForMiddle = 8 * 1000; //中包
	protected long mUpdateTimeForMiddle = 400;
	protected long mAllTestTimeForSmall = 6 * 1000; //小包
	protected long mUpdateTimeForSmall = 300;
	protected boolean isAlgorithm = true; //默认算法1
	protected final boolean DEBUG = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mLocalPath = PathUtil.getInstances().getCurrentPath() + "/down/";
		this.mContext = BaseFtpActivity.this;
	}

	/**
	 * 选择选中的服务器  包括自定义服务器
	 * @return
	 */
	protected BeanAppFtpConfig getSelectServer(){
		if(SpeedTestDataSet.mBeanAppFtpConfig == null){
			//			showCommon("缺少测速点");
			return null;
		}
		return SpeedTestDataSet.mBeanAppFtpConfig;
	}

	/**
	 * @param downName 下载文件名
	 * @param sDownPath 服务器下载地址
	 * @param sUploadPath 服务器上传地址
	 */
	public abstract void initialization(String downName,String sDownPath,String sUploadPath);

	/**
	 * 初始化FTP并启动下载---旧接口
	 * @param position
	 */
	public abstract void initializationFtp(int position);

	/**
	 * 初始化FTP并启动下载---新接口
	 */
	public abstract void initializationFtp();

	/**
	 * 连接状态
	 * @param isDown true下载  false上传
	 * @param connectedType  -1上传下载超时，-2FTP连接失败（连接超时也认为失败）， 0上传下载就绪，1连接成功，2下载错误/上传错误，3正在上传下载，4完成，5文件不存在，6正在上传准备 7上传下载完成
	 */
	public abstract void connected(boolean isDown,int connectedType);

	/**
	 * @param isDown
	 * @param secondSize 当前速度(以秒为单位)
	 */
	public abstract void updateProgress(boolean isDown,float secondSize);

	/**
	 * @param isDown
	 * @param result 时间 流量
	 */
	public abstract void updateSignal(boolean isDown,long[] result);

	/**
	 * 测完次立即回传
	 * @param isDown
	 * @param avg
	 * @param sum
	 */
	public void singleResult(boolean isDown, float avg, float sum){};

	/**
	 * @param maxDown 最大下载值
	 * @param minDown 最小下载值
	 * @param avgDown 平均下载值
	 * @param downTime 下载时长
	 * @param sumDowns  下载总大小
	 * @param maxUpload 最大上传值
	 * @param minUpload 最小上传值
	 * @param avgUpload 平均上传值
	 * @param uploadTime 上传时长
	 * @param sumUploads 上传总大小
	 */
	public abstract void progressOk(float maxDown, float minDown, float avgDown, int downTime, float sumDowns, float maxUpload, float minUpload, float avgUpload, int uploadTime, float sumUploads);

	//速率测试统计
	public Db_DtBusinessInfo mDb_DtBusinessInfo = null;

	/**
	 * 每一秒的实时数据
	 * @param isDown true下载 false上传
	 * @param max 本次最大值
	 * @param min 本次最小值
	 * @param avg 本次平均值
	 * @param durationEvery 每一次时长
	 * @param sizeEvery 每一次大小
	 */
	public void onProgressSecond(boolean isDown,float max, float min, float avg,long durationEvery,double sizeEvery){
		//		if (isDown) {
		//			System.out.println("每一次的记录*****下载：==最大值=" + (max) +
		//					   "》》最小值 =" + (min) +
		//					   "》》平均值 =" + (avg) +
		//					   "》》时长 =" + durationEvery +
		//					   "》》总量 =" + sizeEvery);
		//		}else {
		//			System.out.println("每一次的记录*****上传：==最大值=" + (max) +
		//					   "》》最小值 =" + (min) +
		//					   "》》平均值 =" + (avg) +
		//					   "》》时长 =" + durationEvery +
		//					   "》》总量 =" + sizeEvery);
		//		}
		if (mDb_DtBusinessInfo != null) {
			if (isDown) {
				if (mDb_DtBusinessInfo.dMax < max) {
					mDb_DtBusinessInfo.dMax = max;
				}
				if (mDb_DtBusinessInfo.dMin > min || mDb_DtBusinessInfo.dMin == 0) {
					mDb_DtBusinessInfo.dMin = min;
				}
				mDb_DtBusinessInfo.dDuration += durationEvery;
				mDb_DtBusinessInfo.dLength += sizeEvery;
				//下载平均速率
				if (mDb_DtBusinessInfo.dLength > 0 && mDb_DtBusinessInfo.dDuration > 0) {
					mDb_DtBusinessInfo.dAvg = (float)UtilHandler.getInstance().toDfSum(mDb_DtBusinessInfo.dLength/1024f/1024f*8f/(mDb_DtBusinessInfo.dDuration/1000f),"00");
				}
			}else {
				if (mDb_DtBusinessInfo.uMax < max) {
					mDb_DtBusinessInfo.uMax = max;
				}
				if (mDb_DtBusinessInfo.uMin > min || mDb_DtBusinessInfo.uMin == 0) {
					mDb_DtBusinessInfo.uMin = min;
				}
				mDb_DtBusinessInfo.uDuration += durationEvery;
				mDb_DtBusinessInfo.uLength += sizeEvery;
				//上传平均速率
				if (mDb_DtBusinessInfo.uLength > 0 && mDb_DtBusinessInfo.uDuration > 0) {
					mDb_DtBusinessInfo.uAvg = (float)UtilHandler.getInstance().toDfSum(mDb_DtBusinessInfo.uLength/1024f/1024f*8f/(mDb_DtBusinessInfo.uDuration/1000f),"00");
				}
			}
		}
	};

	/**
	 * @param downName 下载文件名
	 * @param sDownPath 服务器下载地址
	 * @param sUploadPath 服务器上传地址
	 */
	public void inits(String downName,String sDownPath,String sUploadPath){
		this.strDownName      = downName;
		this.serverDownPath   = sDownPath;
		this.serverUploadPath = sUploadPath;
		this.strUploadName 	  = "test.zip";
		this.enabledDown 	  = true;
		this.enabledUpload 	  = true;
		initHandler();
	};

	/**
	 * @param ip ftp地址
	 * @param port 默认-1
	 * @param user 登录用户名
	 * @param passwd 登录密码
	 */
	public void initFtp(String ip,int port,String user,String passwd){
		closeFtp(false);
		this.downIp       = ip;
		this.downPort     = port;
		this.downUser     = user;
		this.downPasswd   = passwd;
		this.uploadIp	  = ip;
		this.uploadPort	  = port;
		this.uploadUser	  = user;
		this.uploadPasswd = passwd;
		setMaxDownSize(5 * 1024 * 1024);
		setMaxUploadSize(5 * 1024 * 1024);
		mHandler.sendEmptyMessage(0);
	}

	/**
	 * @param DownIp       下载对应IP
	 * @param DownPort     下载对应端口
	 * @param DownUser	      下载对应帐号
	 * @param DownPasswd   下载对应密码
	 * @param DownThread   下载对应最大线程数
	 * @param DownPath	      下载对应目录及文件名字  /down/500m.rar
	 * @param DownLenght   下载对应最大长度
	 * @param UploadIp     上传对应IP
	 * @param UploadPort   上传对应端口
	 * @param UploadUser   上传对应帐号
	 * @param UploadPasswd 上传对应密码
	 * @param UploadThread 上传对应最大线程数量
	 * @param UploadSize   上传对应最多长度
	 */
	public boolean initFtpNew(String DownIp,int DownPort,String DownUser,String DownPasswd,int DownThread,String DownPath,int DownLenght,
			String UploadIp,int UploadPort,String UploadUser,String UploadPasswd,int UploadThread,int UploadSize){
		closeFtp(true);
		setMaxThread(DownThread);
		setMaxUploadThread(UploadThread);

		String[] split = DownPath.split("/");
		this.strDownName = split[split.length - 1];
		if (this.strDownName.contains(".")) {
			split = this.strDownName.split("\\.");
			String strNames = split[0];
			if (DownLenght <= 0) {
				setMaxDownSize(UtilHandler.getInstance().toLong(sizeConvert(strNames), 500) * 1024 * 1024);
			}else {
				setMaxDownSize(DownLenght * 1024 * 1024);
			}
		}
		setMaxUploadSize(UploadSize <= 0 ? 100 * 1024 * 1024 : UploadSize * 1024 * 1024);

		this.strUploadName 	  = UploadSize + "test.zip";
		try {
			this.serverDownPath   = DownPath.replaceAll(this.strDownName, "");
		} catch (Exception e) {
			return false;
		}
		this.enabledDown 	  = true;
		this.enabledUpload 	  = true;
		this.downIp 		  = DownIp;
		this.downPort 		  = DownPort;
		this.downUser		  = DownUser;
		this.downPasswd		  = DownPasswd;
		this.uploadIp		  = UploadIp;
		this.uploadPort		  = UploadPort;
		this.uploadUser		  = UploadUser;
		this.uploadPasswd 	  = UploadPasswd;

		initHandler();
		mHandler.sendEmptyMessage(0);
		return true;
	}

	private String downIp,downUser,downPasswd,uploadIp,uploadUser,uploadPasswd;
	private int downPort,uploadPort;
	/**
	 * @param DownIp       下载对应IP
	 * @param DownPort     下载对应端口
	 * @param DownUser	      下载对应帐号
	 * @param DownPasswd   下载对应密码
	 * @param DownThread   下载对应最大线程数
	 * @param DownPath	      下载对应目录及文件名字  /down/500m.rar
	 * @param DownLenght   下载对应最大长度
	 * @param UploadIp     上传对应IP
	 * @param UploadPort   上传对应端口
	 * @param UploadUser   上传对应帐号
	 * @param UploadPasswd 上传对应密码
	 * @param UploadThread 上传对应最大线程数量
	 * @param UploadSize   上传对应最多长度
	 */
	public boolean initFtp(String DownIp,int DownPort,String DownUser,String DownPasswd,int DownThread,String DownPath,int DownLenght,
			String UploadIp,int UploadPort,String UploadUser,String UploadPasswd,int UploadThread,int UploadSize){
		closeFtp(true);
		setMaxThread(DownThread);
		setMaxUploadThread(UploadThread);

		String[] split = DownPath.split("/");
		this.strDownName = split[split.length - 1];
		if (this.strDownName.contains(".")) {
			split = this.strDownName.split("\\.");
			String strNames = split[0];
			if (DownLenght <= 0) {
				setMaxDownSize(UtilHandler.getInstance().toLong(sizeConvert(strNames), 500) * 1024 * 1024);
			}else {
				setMaxDownSize(DownLenght * 1024 * 1024);
			}
		}
		setMaxUploadSize(UploadSize <= 0 ? 100 : UploadSize * 1024 * 1024);

		this.strUploadName 	  = UploadSize + "test.zip";
		try {
			this.serverDownPath   = DownPath.replaceAll(this.strDownName, "");
		} catch (Exception e) {
			return false;
		}
		this.enabledDown 	  = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().FTP_DOWN_OPEN, true);
		this.enabledUpload 	  = SharedPreHandler.getShared(SpeedTest5g.getContext()).getBooleanShared(TypeKey.getInstance().FTP_UPLOAD_OPEN, true);
		this.downIp 		  = DownIp;
		this.downPort 		  = DownPort;
		this.downUser		  = DownUser;
		this.downPasswd		  = DownPasswd;
		this.uploadIp		  = UploadIp;
		this.uploadPort		  = UploadPort;
		this.uploadUser		  = UploadUser;
		this.uploadPasswd 	  = UploadPasswd;

		initHandler();
		mHandler.sendEmptyMessage(0);
		return true;
	}

	/**
	 * @param DownIp       下载对应IP
	 * @param DownPort     下载对应端口
	 * @param DownUser	      下载对应帐号
	 * @param DownPasswd   下载对应密码
	 * @param DownThread   下载对应最大线程数
	 * @param DownPath	      下载对应目录及文件名字  /down/500m.rar
	 * @param DownLenght   下载对应最大长度
	 * @param UploadIp     上传对应IP
	 * @param UploadPort   上传对应端口
	 * @param UploadUser   上传对应帐号
	 * @param UploadPasswd 上传对应密码
	 * @param UploadThread 上传对应最大线程数量
	 * @param UploadSize   上传对应最多长度
	 */
	public boolean initFtpGis(String DownIp,int DownPort,String DownUser,String DownPasswd,int DownThread,String DownPath,int DownLenght,
			String UploadIp,int UploadPort,String UploadUser,String UploadPasswd,int UploadThread,int UploadSize){
		closeFtp(true);
		setMaxThread(DownThread);
		setMaxUploadThread(UploadThread);

		String[] split = DownPath.split("/");
		this.strDownName = split[split.length - 1];
		if (this.strDownName.contains(".")) {
			split = this.strDownName.split("\\.");
			String strNames = split[0];
			if (DownLenght <= 0) {
				setMaxDownSize(UtilHandler.getInstance().toLong(sizeConvert(strNames), 500) * 1024 * 1024);
			}else {
				setMaxDownSize(DownLenght * 1024 * 1024);
			}
		}
		setMaxUploadSize(UploadSize <= 0 ? 100 : UploadSize * 1024 * 1024);

		this.strUploadName 	  = UploadSize + "test.zip";
		try {
			this.serverDownPath   = DownPath.replaceAll(this.strDownName, "");
		} catch (Exception e) {
			this.serverDownPath= "";
			return false;
		}
		this.enabledDown 	  = true;
		this.enabledUpload 	  = true;
		this.downIp 		  = DownIp;
		this.downPort 		  = DownPort;
		this.downUser		  = DownUser;
		this.downPasswd		  = DownPasswd;
		this.uploadIp		  = UploadIp;
		this.uploadPort		  = UploadPort;
		this.uploadUser		  = UploadUser;
		this.uploadPasswd 	  = UploadPasswd;

		initHandler();
		mHandler.sendEmptyMessage(0);
		return true;
	}

	/**
	 * 道路测试-速率测试
	 * @param DownIp       下载对应IP
	 * @param DownPort     下载对应端口
	 * @param DownUser	      下载对应帐号
	 * @param DownPasswd   下载对应密码
	 * @param DownThread   下载对应最大线程数
	 * @param DownPath	      下载对应目录及文件名字  /down/500m.rar
	 * @param DownLenght   下载对应最大长度
	 * @param UploadIp     上传对应IP
	 * @param UploadPort   上传对应端口
	 * @param UploadUser   上传对应帐号
	 * @param UploadPasswd 上传对应密码
	 * @param UploadThread 上传对应最大线程数量
	 * @param UploadSize   上传对应最多长度
	 */
	public boolean initFtpGisTest(String DownIp,int DownPort,String DownUser,String DownPasswd,int DownThread,String DownPath,int DownLenght,
			String UploadIp,int UploadPort,String UploadUser,String UploadPasswd,int UploadThread,int UploadSize,boolean enabledDown,boolean enabledUpload){
		closeFtp(true);
		setMaxThread(DownThread);
		setMaxUploadThread(UploadThread);

		String[] split = DownPath.split("/");
		this.strDownName = split[split.length - 1];
		if (this.strDownName.contains(".")) {
			split = this.strDownName.split("\\.");
			String strNames = split[0];
			if (DownLenght <= 0) {
				setMaxDownSize(UtilHandler.getInstance().toLong(sizeConvert(strNames), 500) * 1024 * 1024);
			}else {
				setMaxDownSize(DownLenght * 1024 * 1024);
			}
		}
		setMaxUploadSize(UploadSize <= 0 ? 100 : UploadSize * 1024 * 1024);

		this.strUploadName 	  = UploadSize + "test.zip";
		try {
			this.serverDownPath   = DownPath.replaceAll(this.strDownName, "");
		} catch (Exception e) {
			this.serverDownPath= "";
			return false;
		}
		this.enabledDown 	  = enabledDown;
		this.enabledUpload 	  = enabledUpload;
		this.downIp 		  = DownIp;
		this.downPort 		  = DownPort;
		this.downUser		  = DownUser;
		this.downPasswd		  = DownPasswd;
		this.uploadIp		  = UploadIp;
		this.uploadPort		  = UploadPort;
		this.uploadUser		  = UploadUser;
		this.uploadPasswd 	  = UploadPasswd;

		initHandler();
		mHandler.sendEmptyMessage(0);
		return true;
	}

	/**
	 * 设置最多下载大小 默认 1024*1024*4  = 4M
	 * @param maxDownSize
	 */
	public void setMaxDownSize(long maxDownSize) {
		this.maxDownSize = maxDownSize;
		//兼容旧版本大小
		setMaxUploadSize(maxDownSize);
	}

	/**
	 * 设置最多上传大小 默认 1024*1024*4  = 4M
	 * @param maxUploadSize
	 */
	public void setMaxUploadSize(long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public long getMaxDownSize() {
		return maxDownSize;
	}

	public long getMaxUploadSize() {
		return maxUploadSize;
	}

	/**
	 * 更新时长--默认每秒更新一次速率
	 * @param updateTime
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 每次下载大小  每次读取的字节大小 默认1024 * 2
	 * 1024=1kb
	 * @param sizeEvery
	 */
	public void setSizeEvery(int sizeEvery) {
		this.sizeEvery = sizeEvery;
	}

	/**
	 * 允许最大下载线程数
	 * @param maxThread
	 */
	public void setMaxThread(int maxThread) {
		this.maxDownThread = maxThread;
	}

	public void setMaxUploadThread(int maxThread){
		this.maxUploadThread = maxThread;
	}

	/**
	 * 最大下载上传时长(超时)--默认20秒
	 * @param timeOut
	 */
	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public String[] getArrStr(int id){
		return BaseFtpActivity.this.getResources().getStringArray(id);
	}

	public int[] getArrInt(int id){
		return BaseFtpActivity.this.getResources().getIntArray(id);
	}

	//	private long getTotalRxBytes() {
	//	    return TrafficStats.getUidRxBytes(getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getUidRxBytes(getApplicationInfo().uid));//转为KB
	//	}

	private long lastTotalRxBytes,lastTimeStampU,lastTimeStampD,nowTimeStamp,speed,nowTotalRxBytes,speedTem;
	//0下载总时长 1本次下载量 2本次下载时长
	private long[] result = new long[]{0, 0, 0};
	//下载速率
	private long[] showNetSpeedDownload() {
		nowTotalRxBytes = TrafficStats.getUidRxBytes(getApplicationInfo().uid);
		nowTimeStamp = System.currentTimeMillis();
		if (lastTimeStampD == 0) {
			downTimes = 0;
			downSumSys = 0;
			mListDownOnes.clear();
			lastTimeStampD = nowTimeStamp;
			lastTotalRxBytes = nowTotalRxBytes;
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
			return result;
		}
		downTimes += (nowTimeStamp - lastTimeStampD);
		//获取当前流量
		speed = nowTotalRxBytes - lastTotalRxBytes;

		if (downTimes > 0) {
			downSumSys += speed;
			if (nowTimeStamp - lastTimeStampD > 0) {
				speedTem = (speed * 1000) / (nowTimeStamp - lastTimeStampD);//毫秒转换
			}
			//判断最大值与最小值
			if (downMax < speedTem) {
				downMax = speedTem;
			}else if (downMin > speedTem) {
				downMin = speedTem;
			}
			if (downMin <= 0) {
				downMin = speedTem;
			}
		}
		result[0] = downTimes;
		result[1] = speed;
		result[2] = nowTimeStamp - lastTimeStampD;
		mListDownOnes.add(new KeyValue(speed, result[2]));
		lastTotalRxBytes = nowTotalRxBytes;
		lastTimeStampD = nowTimeStamp;
		updateInitSecond();
		return result;
	}

	//上传速率
	private long[] showNetSpeedUpload() {
		nowTotalRxBytes = TrafficStats.getUidTxBytes(getApplicationInfo().uid);
		nowTimeStamp = System.currentTimeMillis();
		if (lastTimeStampU == 0) {
			uploadTimes = 0;
			uploadSumSys = 0;
			mListUploadOnes.clear();
			lastTimeStampU = nowTimeStamp;
			lastTotalRxBytes = nowTotalRxBytes;
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
			return result;
		}
		uploadTimes += (nowTimeStamp - lastTimeStampU);
		//获取当前流量
		speed = nowTotalRxBytes - lastTotalRxBytes;

		if (uploadTimes > 0) {
			uploadSumSys += speed;
			if (nowTimeStamp - lastTimeStampU > 0) {
				speedTem = (speed * 1000) / (nowTimeStamp - lastTimeStampU);//毫秒转换
			}
			//最大值与最小值
			if (uploadMax < speedTem) {
				uploadMax = speedTem;
			}else if (uploadMin > speedTem) {
				uploadMin = speedTem;
			}

			if (uploadMin <= 0) {
				uploadMin = speedTem;
			}
		}
		result[0] = uploadTimes;
		result[1] = speed;
		result[2] = nowTimeStamp - lastTimeStampU;
		mListUploadOnes.add(new KeyValue(speed, result[2]));
		lastTimeStampU = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;
		updateInitSecond();
		return result;
	}

	//计算更新每秒数据
	private void updateInitSecond(){
		if (isNowDown) {
			double roundDown = UtilHandler.getInstance().toDfSum(downSumSys/1024f/1024f,"00");
			float timeDown = (downTimes/1000f);
			onProgressSecond(isNowDown, 
					downMax > 0 ? (float)UtilHandler.getInstance().toDfSum(downMax*8/1024f/1024f,"00") : 0, 
							downMin > 0 ? (float)UtilHandler.getInstance().toDfSum(downMin/1024f/1024f,"00") : 0, 
									roundDown > 0 && timeDown > 0 ? (float)UtilHandler.getInstance().toDfSum(roundDown*8f/timeDown,"00") : 0,
											result[2],
											result[1]);
		}else {
			double roundUploads = UtilHandler.getInstance().toDfSum(uploadSumSys/1024f/1024f,"00");
			float timeUpload = (uploadTimes/1000f);
			onProgressSecond(isNowDown, 
					uploadMax > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMax*8/1024f/1024f,"00") : 0, 
							uploadMin > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMin/1024f/1024f,"00") : 0, 
									roundUploads > 0 && timeUpload > 0 ? (float)UtilHandler.getInstance().toDfSum(roundUploads*8f/timeUpload,"00") : 0, 
											result[2],
											result[1]);
		}
	}

	//最大与最小
	private long downMax,downMin,uploadMax,uploadMin;
	//下载时间长度,上传的时间长度
	private long downTimes,uploadTimes;
	//上一秒速度
	private long lastSecondSize;
	private void initHandler(){
		if (mHandler == null) {
			handlerThread = new HandlerThread("ftpThread");
			handlerThread.start();
			mTHandler = new Handler(handlerThread.getLooper());

			mHandler = new Handler(new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					switch (msg.what) {
					case 0://准备下载操作
						downSumSys = 0;
						downTimes = 0;
						uploadSumSys = 0;
						uploadTimes = 0;
						mListDownOnes.clear();
						mListUploadOnes.clear();
						downMax = 0;
						downMin = 0;
						uploadMax = 0;
						uploadMin = 0; 
						lastTotalRxBytes = 0;
						lastTimeStampU = 0;
						lastTimeStampD = 0;
						nowTimeStamp = 0;
						speed = 0;
						nowTotalRxBytes = 0;
						speedTem = 0;
						//						sumSecond = 0; 
						//						sumUpSecond = 0;
						if (enabledDown && getPathLenght(Environment.getExternalStorageDirectory().getPath()) <= 1024*1024*1024) {//1G
							showCommon("手机剩余空间不足,请清理后再试!");
						}else if (enabledUpload && getPathLenght(Environment.getExternalStorageDirectory().getPath()) <= 1024*1024*1024) {
							showCommon("手机剩余空间不足,请清理后再试!");
						}else {
							if (enabledDown) {
								startFtp(true);
								connected(isNowDown,0);
							}else if (enabledUpload) {
								connected(false, 6);
								startFtp(false);
								connected(isNowDown,0);
							}
						}
						break;
					case FTP_OK://FTP连接成功
						if (isNowDown) {
							lastTimeStampD = 0;
							showNetSpeedDownload();
						}else {
							lastTimeStampU = 0;
							showNetSpeedUpload();
						}
						connected(isNowDown,1);
						break;
					case DOWN_PROGRESS://下载时每秒更新一次速率
						mHandler.removeMessages(DOWN_PROGRESS);
						//						downTime += updateTime;
						//sumSecond * (1000f/updateTime)
						long[] secondSize = showNetSpeedDownload();
						//						sumSecond = 0;
						//每秒更新一次
						mHandler.sendEmptyMessageDelayed(DOWN_PROGRESS, updateTime);

						if (secondSize[1] <= 0) {
							break;
						}else if(secondSize[1] < lastSecondSize/3){
							//							secondSize[1] = lastSecondSize/3;
						}
						lastSecondSize = secondSize[1];
						connected(isNowDown,3);
						if (secondSize[2] == 0) {
							lastSecondSize = 0;
						}else {
							lastSecondSize = (long) UtilHandler.getInstance().toDfSum(lastSecondSize/(secondSize[2]/1000f),"00");
						}
						updateProgress(isNowDown, lastSecondSize);
						updateSignal(isNowDown, secondSize);
						if (downSumSys >= maxDownSize) {
							updateProgress(isNowDown, 0);
							mHandler.removeMessages(DOWN_TIMEOUT);
							mHandler.removeMessages(DOWN_PROGRESS);
							mHandler.sendEmptyMessage(DOWN_OK);
							//							downTimeSys -= updateTime;
						}
						break;
					case DOWN_ERROR://下载错误或者其他失败问题
						connected(isNowDown,2);
						break;
					case DOWN_ERROR_NOFILE://下载文件不存在
						closeFtp(true);
						connected(isNowDown,5);
						break;
					case DOWN_OK://下载完成
						//						downTime += updateTime;
						showNetSpeedDownload();
						updateProgress(isNowDown, 0);
						calculateSingle();
						if (enabledUpload) {
							startFtp(false);
							connected(isNowDown,0);
						}else {
							calculate();
						}
						break;
					case UPLOAD_PROGRESS://正在上传中
						mHandler.removeMessages(UPLOAD_PROGRESS);
						//						uploadTime += updateTime;
						//						float secondSizeUp = sumUpSecond * (1000f/updateTime);
						//						sumUpSecond = 0;
						long[] secondSizeUp = showNetSpeedUpload();
						mHandler.sendEmptyMessageDelayed(UPLOAD_PROGRESS, updateTime);
						if (secondSizeUp[1] <= 0) {
							break;
						}else if (secondSizeUp[1] < lastSecondSize/3) {
							//							secondSizeUp[1] = lastSecondSize/3;
						}
						lastSecondSize = secondSizeUp[1];
						connected(isNowDown,3);
						if (secondSizeUp[2] == 0) {
							lastSecondSize = 0;
						}else {
							lastSecondSize = (long) UtilHandler.getInstance().toDfSum(lastSecondSize/(secondSizeUp[2]/1000f),"00");
						}
						updateProgress(isNowDown, lastSecondSize);
						updateSignal(isNowDown, secondSizeUp);
						break;
					case UPLOAD_ERROR://上传出错
						connected(isNowDown,2);
						break;
					case DOWN_ERROR_CON:// 下载FTP连接失败
						connected(isNowDown, -2);
						break;
					case UPLOAD_ERROR_CON:// 上传ftp连接失败
						connected(isNowDown, -2);
						break;
					case UPLOAD_OK://上传成功
						connected(isNowDown,7);
						//						uploadTime += updateTime;
						showNetSpeedUpload();
						updateProgress(isNowDown, 0);
						closeFtp(true);
						calculateSingle();
						calculate();
						break;
					case DOWN_TIMEOUT://下载超时
						connected(isNowDown,7);
						mHandler.removeMessages(DOWN_PROGRESS);
						updateProgress(isNowDown, 0);
						if (downSumSys <= 0) {
							connected(isNowDown,-1);
						}else {
							//							downTime = timeOut;
							showNetSpeedDownload();
							calculateSingle();
							if (enabledUpload) {
								startFtp(false);
								connected(isNowDown,0);
							}else {
								calculate();
							}
						}
						break;
					case UPLOAD_TIMEOUT://上传超时
						connected(isNowDown,7);
						mHandler.removeMessages(UPLOAD_PROGRESS);
						updateProgress(isNowDown, 0);
						//						uploadTime = timeOut;
						showNetSpeedUpload();
						calculateSingle();
						if(uploadSumSys <= 0){
							connected(isNowDown,-1);
						}
						closeFtp(true);
						calculate();
						break;
					case CONN_TIME:// ftp连接超时
						closeFtp(true);
						connected(isNowDown,-2);
						break;
					case UPLOAD_FILE_OVER://上传的文件准备就绪完成
						//						dismissMyDialog();
						//						connected(isNowDown,0);
						startInit((Boolean)msg.obj);
						break;
					default:
						break;
					}
					return true;
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		if (handlerThread != null) {
			handlerThread.quit();
		}
		closeFtp(true);
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private long getPathLenght(String path){
		StatFs statfs = new StatFs(path);
		//获取手机上剩余空间大小
		long blockSize = statfs.getBlockSize(); 
		long availableBlocks = statfs.getAvailableBlocks();
		long size = availableBlocks * blockSize;
		return size;
	}

	/*
	 * 启动下载FTP
	 */
	public void startFtp(boolean isDown){
		lastTotalRxBytes = 0;
		if (isDown) {
			lastTimeStampD = 0;
		}else {
			lastTimeStampU = 0;
		}
		nowTimeStamp = 0;
		speed = 0;
		nowTotalRxBytes = 0;
		if (isDown) {
			FtpUtil.getInstances().setIpPort(downIp, downPort, downUser, downPasswd);
		}else {
			FtpUtil.getInstances().setIpPort(uploadIp, uploadPort, uploadUser, uploadPasswd);
		}
		mHandler.sendEmptyMessageDelayed(CONN_TIME, timeOut);
		lastSecondSize = 0;
		failNumber = 0;
		isSendOK = false;
		isNowDown = isDown;
		closeFtp(true);
		if (mArrFtpList == null) {
			mArrFtpList = new ArrayList<FtpRunnable>();
		}
		if (isDown) {
			downNames = new String[maxDownThread];
			startInit(isDown);
		}else {
			uploadNames = new String[maxUploadThread];
			initUploadFile(isDown);
		}
	}

	private void startInit(boolean isDown){
		try {
			if (isDown) {//下载线程数量
				for (int i = 0; i < maxDownThread; i++) {
					downNames[i] = String.valueOf(System.currentTimeMillis());
					Thread.sleep(5);
					mArrFtpList.add(new FtpRunnable(isDown, mLocalPath, downNames[i] + strDownName));
				}
			}else {//上传线程数量
				for (int i = 0; i < maxUploadThread; i++) {
					mArrFtpList.add(new FtpRunnable(isDown, mLocalPath, uploadNames[i] + strUploadName));
				}
			}
		} catch (Exception e) {
		}

		for (FtpRunnable ftpRunnable : mArrFtpList) {
			new Thread(ftpRunnable).start();
		}
	}

	private Runnable mFileRunnable;
	/*
	 * 初始化要上传的文件
	 */
	private void initUploadFile(final boolean isDown){
		//		showMyDialog();
		if (mFileRunnable != null) {
			mTHandler.removeCallbacks(mFileRunnable);
			mFileRunnable = null;
		}
		mFileRunnable = new Runnable() {

			@Override
			public void run() {
				File filename = new File(mLocalPath);
				if (!filename.exists()) {
					filename.mkdirs();
				}
				for (int i = 0; i < maxUploadThread; i++) {
					RandomAccessFile raf = null;
					try {
						uploadNames[i] = String.valueOf( System.currentTimeMillis());
						String strName = mLocalPath + uploadNames[i] + strUploadName;
						//建立一个指定大小的空文件
						raf = new RandomAccessFile(strName, "rw");
						raf.setLength(maxUploadSize);
						Thread.sleep(10);
					} catch (Exception e) {
						WybLog.syso("错误创建文件" + e.getMessage());
					} finally {
						if ( raf != null ) {
							try {
								raf.close();
							} catch (Exception e2) {
							}
						}
					}
				}
				Message msg = mHandler.obtainMessage();
				msg.what = UPLOAD_FILE_OVER;
				msg.obj  = isDown;
				mHandler.sendMessage(msg);
			}
		};
		mTHandler.post(mFileRunnable);
	}

	/*
	 * 关闭当前所有下载
	 */
	public void closeFtp(boolean detele){
		if (mHandler != null) {
			mHandler.removeMessages(CONN_TIME);
		}
		if (mArrFtpList != null) {
			for (FtpRunnable ftpRunnable : mArrFtpList) {
				ftpRunnable.closeNet();
			}
			mArrFtpList.clear();
		}
		if (detele) {
			PathUtil.getInstances().deteleDirectory(mLocalPath);
		}
	}

	private void calculateSingle(){
		if(DEBUG){
			if(isNowDown){ //下载
				StringBuffer bufferDown = new StringBuffer();
				for(KeyValue value : mListDownOnes){
					long downS = (long)value.key;
					float timeS = (long)value.value;
					double roundS = UtilHandler.getInstance().toDfSum(downS/1024f/1024f,"00");
					String avg = String.valueOf((float)UtilHandler.getInstance().toDfSum(roundS*8f/(timeS/1000f),"00"));

					String data = "下载每点-总量：" + String.valueOf(downS) + ",时间：" + String.valueOf(timeS) + ",平均值：" + avg + "\r\n";
					bufferDown.append(data);
				}
				SpeedTestUtil.getInstance().writeToText(isAlgorithm?"算法1":"算法2(凤)", bufferDown.toString());
			}else{
				StringBuffer bufferUpload = new StringBuffer();
				for(KeyValue value : mListUploadOnes){
					long uploadS = (long)value.key;
					float timeS = (long)value.value;
					double roundS = UtilHandler.getInstance().toDfSum(uploadS/1024f/1024f,"00");
					String avg = String.valueOf((float)UtilHandler.getInstance().toDfSum(roundS*8f/(timeS/1000f),"00"));

					String data = "上传每点-总量：" + String.valueOf(uploadS) + ",时间：" + String.valueOf(timeS) + ",平均值：" + avg + "\r\n";
					bufferUpload.append(data);
				}
				SpeedTestUtil.getInstance().writeToText(isAlgorithm?"算法1":"算法2(凤)", bufferUpload.toString());
			}
		}

		if(isAlgorithm){
			algorithm1();
		}else{
			algorithm2();
		}

		double round = 0;
		float time = 0;
		if(isNowDown){
			round = UtilHandler.getInstance().toDfSum(downSumSys/1024f/1024f,"00");
			time = (downTimes/1000f);
		}else{
			round = UtilHandler.getInstance().toDfSum(uploadSumSys/1024f/1024f,"00");
			time = (uploadTimes/1000f);
		}

		singleResult(isNowDown, 
				round > 0 && time > 0 ? (float)UtilHandler.getInstance().toDfSum(round*8f/time,"00") : 0,
						(float)round);
	}

	/*
	 * 计算当前上传下载相关值
	 */
	private void calculate(){
		closeFtp(true);
		try {
			connected(isNowDown, 4);

			double roundDown = UtilHandler.getInstance().toDfSum(downSumSys/1024f/1024f,"00");
			float timeDown = (downTimes/1000f);

			double roundUploads = UtilHandler.getInstance().toDfSum(uploadSumSys/1024f/1024f,"00");
			float timeUpload = (uploadTimes/1000f);

			progressOk(downMax > 0 ? (float)UtilHandler.getInstance().toDfSum(downMax*8/1024f/1024f,"00") : 0, 
					downMin > 0 ? (float)UtilHandler.getInstance().toDfSum(downMin/1024f/1024f,"00") : 0,
							roundDown > 0 && timeDown > 0 ? (float)UtilHandler.getInstance().toDfSum(roundDown*8f/timeDown,"00") : 0,
									(int)timeDown,(float)roundDown,
									uploadMax > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMax*8/1024f/1024f,"00") : 0, 
											uploadMin > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMin/1024f/1024f,"00") : 0, 
													roundUploads > 0 && timeUpload > 0 ? (float)UtilHandler.getInstance().toDfSum(roundUploads*8f/timeUpload,"00") : 0, 
															(int)timeUpload,(float)roundUploads);

			//			System.out.println("下载：==最大值=" + (downMax > 0 ? (float)UtilHandler.getInstance().toDfSum(downMax*8/1024f/1024f,"00") : 0) +
			//							   "》》最小值 =" + (downMin > 0 ? (float)UtilHandler.getInstance().toDfSum(downMin/1024f/1024f,"00") : 0) +
			//							   "》》平均值 =" + (roundDown > 0 && timeDown > 0 ? (float)UtilHandler.getInstance().toDfSum(roundDown*8f/timeDown,"00") : 0) +
			//							   "》》时长 =" + timeDown +
			//							   "》》总量 =" + roundDown +
			//							   "《《&&&《《上传：==最大值=" + (uploadMax > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMax*8/1024f/1024f,"00") : 0) +
			//							   "》》最小值 =" + (uploadMin > 0 ? (float)UtilHandler.getInstance().toDfSum(uploadMin/1024f/1024f,"00") : 0) +
			//							   "》》平均值 =" + (roundUploads > 0 && timeUpload > 0 ? (float)UtilHandler.getInstance().toDfSum(roundUploads*8f/timeUpload,"00") : 0) +
			//							   "》》时长 =" + timeUpload +
			//							   "》》总量 =" + roundUploads);
		} catch (Exception e) {
			WybLog.syso("---ftp异常："+e.getMessage());
		}
	}

	/**
	 * 算法1
	 */
	private void algorithm1(){
		if(isNowDown){
			//(n-mDeleteCount)*30% + mDeleteCount 算出需要去除的点数,去除点数个最低值   n为总数
			int downSize = mListDownOnes.size();
			if(downSize > mDeleteCount){
				int overDownSize = Math.round((downSize-mDeleteCount) * 30 /100f) + mDeleteCount;
				ArrayList<KeyValue> tempDownList = new ArrayList<KeyValue>();
				tempDownList.addAll(mListDownOnes);
				Collections.sort(tempDownList, new Comparator<KeyValue>(){
					public int compare(KeyValue arg0, KeyValue arg1) {
						long downS0 = (long)arg0.key;
						float timeS0 = ((long)arg0.value/1000f);
						double roundS0 = UtilHandler.getInstance().toDfSum(downS0/1024f/1024f,"00");
						float avg0 = (float)UtilHandler.getInstance().toDfSum(roundS0*8f/timeS0,"00");

						long downS1 = (long)arg1.key;
						float timeS1 = ((long)arg1.value/1000f);
						double roundS1 = UtilHandler.getInstance().toDfSum(downS1/1024f/1024f,"00");
						float avg1 = (float)UtilHandler.getInstance().toDfSum(roundS1*8f/timeS1,"00");

						return avg0>avg1?1:-1;
					}
				});

				for(int downInt = 0; downInt < overDownSize; downInt++){
					KeyValue tempKeyValue = tempDownList.get(downInt);
					downSumSys -= (long)tempKeyValue.key;
					downTimes -= (long)tempKeyValue.value; 
				}
			}

		}else{
			//(n-mDeleteCount)*30% + mDeleteCount 算出需要去除的点数,去除点数个最低值   n为总数
			int uploadSize = mListUploadOnes.size();
			if(uploadSize > mDeleteCount){
				int overUploadSize = Math.round((uploadSize-mDeleteCount) * 30 /100f) + mDeleteCount;
				ArrayList<KeyValue> tempUploadList = new ArrayList<KeyValue>();
				tempUploadList.addAll(mListUploadOnes);
				Collections.sort(tempUploadList, new Comparator<KeyValue>(){
					public int compare(KeyValue arg0, KeyValue arg1) {
						long uploadS0 = (long)arg0.key;
						float timeS0 = ((long)arg0.value/1000f);
						double roundS0 = UtilHandler.getInstance().toDfSum(uploadS0/1024f/1024f,"00");
						float avg0 = (float)UtilHandler.getInstance().toDfSum(roundS0*8f/timeS0,"00");

						long uploadS1 = (long)arg1.key;
						float timeS1 = ((long)arg1.value/1000f);
						double roundS1 = UtilHandler.getInstance().toDfSum(uploadS1/1024f/1024f,"00");
						float avg1 = (float)UtilHandler.getInstance().toDfSum(roundS1*8f/timeS1,"00");

						return avg0>avg1?1:-1;
					}
				});

				for(int downInt = 0; downInt < overUploadSize; downInt++){
					KeyValue tempKeyValue = tempUploadList.get(downInt);
					uploadSumSys -= (long)tempKeyValue.key;
					uploadTimes -= (long)tempKeyValue.value; 
				}
			}
		}
	}

	/**
	 * 算法2
	 */
	private void algorithm2(){
		if(isNowDown){
			//去除前3个
			if (mListDownOnes.size() > 3) {
				KeyValue keyValue0 = mListDownOnes.remove(2);
				downSumSys -= (long)keyValue0.key;
				downTimes -= (long)keyValue0.value; 
				KeyValue keyValue1 = mListDownOnes.remove(1);
				downSumSys -= (long)keyValue1.key;
				downTimes -= (long)keyValue1.value; 
				KeyValue keyValue2 = mListDownOnes.remove(0);
				downSumSys -= (long)keyValue2.key;
				downTimes -= (long)keyValue2.value; 
			}
			//去除最后一个
			if (mListDownOnes.size() > 1) {
				KeyValue keyValue = mListDownOnes.remove(mListDownOnes.size() - 1);
				downSumSys -= (long)keyValue.key;
				downTimes -= (long)keyValue.value; 
			}
			int downSize = mListDownOnes.size();
			if(downSize > mDeleteCount){
				int overDownSize = Math.round(downSize * 30 /100f);
				ArrayList<KeyValue> tempDownList = new ArrayList<KeyValue>();
				tempDownList.addAll(mListDownOnes);
				Collections.sort(tempDownList, new Comparator<KeyValue>(){
					public int compare(KeyValue arg0, KeyValue arg1) {
						long downS0 = (long)arg0.key;
						float timeS0 = ((long)arg0.value/1000f);
						double roundS0 = UtilHandler.getInstance().toDfSum(downS0/1024f/1024f,"00");
						float avg0 = (float)UtilHandler.getInstance().toDfSum(roundS0*8f/timeS0,"00");

						long downS1 = (long)arg1.key;
						float timeS1 = ((long)arg1.value/1000f);
						double roundS1 = UtilHandler.getInstance().toDfSum(downS1/1024f/1024f,"00");
						float avg1 = (float)UtilHandler.getInstance().toDfSum(roundS1*8f/timeS1,"00");

						return avg0>avg1?1:-1;
					}
				});

				for(int downInt = 0; downInt < overDownSize; downInt++){
					KeyValue tempKeyValue = tempDownList.get(downInt);
					downSumSys -= (long)tempKeyValue.key;
					downTimes -= (long)tempKeyValue.value; 
				}
			}
		}else{
			//去除前3个
			if (mListUploadOnes.size() > 3) {
				KeyValue keyValue0 = mListUploadOnes.remove(2);
				uploadSumSys -= (long)keyValue0.key;
				uploadTimes -= (long)keyValue0.value; 
				KeyValue keyValue1 = mListUploadOnes.remove(1);
				uploadSumSys -= (long)keyValue1.key;
				uploadTimes -= (long)keyValue1.value; 
				KeyValue keyValue2 = mListUploadOnes.remove(0);
				uploadSumSys -= (long)keyValue2.key;
				uploadTimes -= (long)keyValue2.value; 
			}
			//去除最后一个
			if (mListUploadOnes.size() > 1) {
				KeyValue keyValue = mListUploadOnes.remove(mListUploadOnes.size() - 1);
				uploadSumSys -= (long)keyValue.key;
				uploadTimes -= (long)keyValue.value;
			}
			int uploadSize = mListUploadOnes.size();
			if(uploadSize > mDeleteCount){
				int overUploadSize = Math.round(uploadSize * 30 / 100f);
				ArrayList<KeyValue> tempUploadList = new ArrayList<KeyValue>();
				tempUploadList.addAll(mListUploadOnes);
				Collections.sort(tempUploadList, new Comparator<KeyValue>(){
					public int compare(KeyValue arg0, KeyValue arg1) {
						long uploadS0 = (long)arg0.key;
						float timeS0 = ((long)arg0.value/1000f);
						double roundS0 = UtilHandler.getInstance().toDfSum(uploadS0/1024f/1024f,"00");
						float avg0 = (float)UtilHandler.getInstance().toDfSum(roundS0*8f/timeS0,"00");

						long uploadS1 = (long)arg1.key;
						float timeS1 = ((long)arg1.value/1000f);
						double roundS1 = UtilHandler.getInstance().toDfSum(uploadS1/1024f/1024f,"00");
						float avg1 = (float)UtilHandler.getInstance().toDfSum(roundS1*8f/timeS1,"00");

						return avg0>avg1?1:-1;
					}
				});

				for(int uploadInt = 0; uploadInt < overUploadSize; uploadInt++){
					KeyValue tempKeyValue = tempUploadList.get(uploadInt);
					uploadSumSys -= (long)tempKeyValue.key;
					uploadTimes -= (long)tempKeyValue.value; 
				}
			}
		}
	}

	/**
	 * 算法1 采样点去除
	 */
	protected void algorithm1(List<Signal> list){
		//(n-4)*30% + 4  去除的点数  个数中最低的值   n为总数
		int size = list.size();
		if(size > mDeleteCount){
			int overSize = Math.round((size-mDeleteCount) * 30 /100f) + mDeleteCount;
			ArrayList<Signal> tempList = new ArrayList<Signal>();
			tempList.addAll(list);
			Collections.sort(tempList, new Comparator<Signal>(){
				public int compare(Signal arg0, Signal arg1) {
					return arg0.rate>(arg1.rate)?1:-1;
				}
			});

			for(int index = 0; index < overSize; index++){
				Signal tempSignal = tempList.get(index);
				list.remove(tempSignal);
			}
		}
	}

	/**
	 * 算法2 采样点去除
	 */
	protected void algorithm2(List<Signal> list){
		//去掉前3个
		if (list.size() > 3) {
			list.remove(2);
			list.remove(1);
			list.remove(0);
		}
		//再去掉最后一个
		if (list.size() > 1) {
			list.remove(list.size() - 1);
		}

		int size = list.size();
		if(size > mDeleteCount){
			int overSize = Math.round(size * 30 / 100f);
			ArrayList<Signal> tempList = new ArrayList<Signal>();
			tempList.addAll(list);
			Collections.sort(tempList, new Comparator<Signal>(){
				public int compare(Signal arg0, Signal arg1) {
					return arg0.rate>(arg1.rate)?1:-1;
				}
			});

			for(int index = 0; index < overSize; index++){
				Signal tempSignal = tempList.get(index);
				list.remove(tempSignal);
			}
		}
	}

	//是否发送连接成功
	private boolean isSendOK = false;
	//下载失败个数线程
	private int failNumber = 0;
	/**
	 * 上传下载runnbale类
	 * @author zwqPC
	 *
	 */
	class FtpRunnable implements Runnable{

		//当前是否是下载线程
		private boolean mIsDown,isClose;

		private FTP mFtp;
		//本地路径,本地名称
		private String localPath,localName;

		public FtpRunnable(boolean isDown,String localPath,String localName){
			this.mIsDown = isDown;
			this.mFtp = new FTP(sizeEvery);
			this.localPath = localPath;
			this.localName = localName;
			this.isClose = false;
		}

		public void closeNet(){
			isClose = true;
			if (!mIsDown) {
				//				deleteServiceName(serverUploadPath, localName);
			}
			if (mFtp != null) {
				try {
					mFtp.closeConnect();
				} catch (Exception e) {
					WybLog.syso("错误:" + e.getMessage());
				}finally{
					try {
						mFtp.closeConnect();
					} catch (Exception e2) {
					}
					if (mHandler != null) {
						mHandler.removeMessages(CONN_TIME);
						if (mIsDown) {
							mHandler.removeMessages(DOWN_PROGRESS);
							mHandler.removeMessages(DOWN_TIMEOUT);
						}else {
							mHandler.removeMessages(UPLOAD_PROGRESS);
							mHandler.removeMessages(UPLOAD_TIMEOUT);
						}
					}
				}
			}
		}

		@Override
		public void run() {
			try {
				if (mIsDown) {//下载
					mFtp.downloadSingleFile(serverDownPath  + strDownName, localPath, localName,
							new DownLoadProgressListener() {

						@Override
						public void onDownLoadSumProgress(long downSize,long downProcess, long fileSize) {
							//							addSecond(downSize , downProcess);
							//							WybLog.syso(sumSecond + "正在下载" + downSize);
						}

						@Override
						public void onDownLoadProgress(int state, long downProcess, File file) {
							if (isClose) {
								closeNet();
							}
							//							WybLog.syso("FTP返回状态:" + state);
							if (state == FtpState.FTP_CONNECT_SUCCESSS) {
								if (!isSendOK) {
									isSendOK = true;
									mHandler.removeMessages(CONN_TIME);
									mHandler.sendEmptyMessageDelayed(DOWN_TIMEOUT, timeOut);
									mHandler.sendEmptyMessage(FTP_OK);
									mHandler.sendEmptyMessageDelayed(DOWN_PROGRESS, updateTime);
								}

							} else if (state == FtpState.FTP_CONNECT_FAIL) {
								failNumber += 1;
								if (maxDownThread <= failNumber) {
									mHandler.removeMessages(DOWN_PROGRESS);
									mHandler.removeMessages(DOWN_TIMEOUT);
									mHandler.sendEmptyMessage(DOWN_ERROR_CON);
								}

							} else if (state == FtpState.FTP_DOWN_FAIL) {
								failNumber += 1;
								if (maxDownThread <= failNumber) {
									mHandler.removeMessages(DOWN_PROGRESS);
									mHandler.removeMessages(DOWN_TIMEOUT);
									mHandler.sendEmptyMessage(DOWN_ERROR);
								}
							}else if (state == FtpState.FTP_FILE_NOTEXISTS) {
								mHandler.removeMessages(DOWN_PROGRESS);
								mHandler.removeMessages(DOWN_TIMEOUT);
								mHandler.sendEmptyMessage(DOWN_ERROR_NOFILE);
							}else if (state == FtpState.FTP_DOWN_SUCCESS) {
								failNumber += 1;
								if (maxDownThread <= failNumber) {
									mHandler.removeMessages(DOWN_TIMEOUT);
									mHandler.sendEmptyMessage(DOWN_OK);
									mHandler.removeMessages(DOWN_PROGRESS);
								}
							}
						}
					});
				}else {//上传
					File file = new File(localPath + localName);
					if (file.exists()) {
						mFtp.uploadSingleFile(file, serverUploadPath, new UploadProgressListener() {

							@Override
							public void onUploadProgress(int state, long uploadSize, File file) {
								if (isClose) {
									closeNet();
								}
								if (state == FtpState.FTP_CONNECT_SUCCESSS) {
									if (!isSendOK) {
										isSendOK = true;
										mHandler.removeMessages(CONN_TIME);
										mHandler.sendEmptyMessageDelayed(UPLOAD_TIMEOUT, timeOut);
										mHandler.sendEmptyMessage(FTP_OK);
										mHandler.sendEmptyMessageDelayed(UPLOAD_PROGRESS, updateTime);
									}
								}else if (state == FtpState.FTP_UPLOAD_FAIL) {
									deleteServiceName(serverUploadPath, localName);
									//									WybLog.syso("失败FTP返回状态:" + state);
									failNumber += 1;
									if (maxUploadThread <= failNumber) {
										mHandler.removeMessages(UPLOAD_PROGRESS);
										mHandler.removeMessages(UPLOAD_TIMEOUT);
										mHandler.sendEmptyMessage(UPLOAD_ERROR);
									}
								}else if (state == FtpState.FTP_CONNECT_FAIL) {
									//									WybLog.syso("失败FTP返回状态:" + state);
									failNumber += 1;
									if (maxUploadThread <= failNumber) {
										mHandler.removeMessages(UPLOAD_PROGRESS);
										mHandler.removeMessages(UPLOAD_TIMEOUT);
										mHandler.sendEmptyMessage(UPLOAD_ERROR_CON);
									}
								}else if (state == FtpState.FTP_UPLOAD_SUCCESS) {
									deleteServiceName(serverUploadPath, localName);
									failNumber += 1;
									if (maxUploadThread <= failNumber) {
										mHandler.removeMessages(UPLOAD_TIMEOUT);
										mHandler.sendEmptyMessage(UPLOAD_OK);
										mHandler.removeMessages(UPLOAD_PROGRESS);
									}
								}
							}

							@Override
							public void onUploadSumProgress(long uploadSize,long uploadProcess, long fileSize) {
								//								addUpSecond(uploadSize, uploadProcess);
								//								WybLog.syso(sumUpSecond + "正在上传" + uploadSize);
							}
						});
					}
				}
			} catch (Exception e) {
				WybLog.syso("错误" + e.getMessage());
			}
		}

		//删除服务器文件
		private void deleteServiceName(String serverPath,String serverName){
			if (mFtp != null) {
				try {
					mFtp.deleteSingleFile(serverPath + serverName, new DeleteFileProgressListener() {

						@Override
						public void onDeleteProgress(int state) {

						}
					});
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 *M G T单位转换 500M->500   20G->20*1024  1T->1*1024*1024
	 */
	private String sizeConvert(String src){
		if(TextUtils.isEmpty(src)){
			return null;
		}
		try{
			int value = 0;
			if(src.contains("M") || src.contains("m")){
				value = Integer.parseInt(src.substring(0, src.length() - 1));
			}else if(src.contains("G") || src.contains("g")){
				value = Integer.parseInt(src.substring(0, src.length() - 1)) * 1024;
			}else if(src.contains("T") || src.contains("t")){
				value = Integer.parseInt(src.substring(0, src.length() - 1)) * 1024 * 1024;
			}
			return value==0?null:String.valueOf(value);
		}catch(Exception e){
			return null;
		}
	}

	//当前时间下载量，总下载量，当前时间上传量，总上传量
	//sumSecond,sumUpSecond
	private long downSumSys,uploadSumSys;
	//记录每一秒的数据
	private List<KeyValue> mListDownOnes = new ArrayList<KeyValue>();
	private List<KeyValue> mListUploadOnes = new ArrayList<KeyValue>();
	//	private synchronized void addSecond(long downSize,long downProcess){
	//		downSum += downSize;
	//	}
	//	
	//	private synchronized void addUpSecond(long uploadSize,long uploadProcess){
	//		uploadSum += uploadSize;
	//	}
}
