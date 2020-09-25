package cn.nokia.speedtest5g.app.uitl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.bean.WybBitmapDb;
import cn.nokia.speedtest5g.app.db.DbHandler;
import com.android.volley.util.SharedPreHandler;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * 获取路径
 * @author zwq
 *
 */
public class PathUtil {
	
	private static PathUtil pu = null;
	
	public static synchronized PathUtil getInstances(){
		if (pu == null) {
			pu = new PathUtil();
		}
		return pu;
	}
	
	//下载目录
	private final String downLoad = "/Download";
	//LOG保存目录---如主动采集
	private final String logPath = "/log";
	//DB下载目录
	private final String dbPath = "/downDb";
	//规划设计目录
	private final String ghsjPath = "/design";
	//工程施工目录
	private final String gcsgPath = "/sf";
	//巡检目录--优化
	private final String gcxjPath = "/inspection";
	//代维（工参）巡检目录-工参
	private final String gcxj_dw_Path = "/gcxj";
	//性能测试
	private final String xncsSuperPath = "/xncsSuper";
	//性能测试
	private final String xncsPath = "/xncs";
	//入网验收目录---单验等
	private final String rwysPath = "/Records";
	//首级目录
	private final String superPath = "/SpeedTest5G";
	//工参巡检---工参调整主
	private final String gctzSuperPath = "/gctzSuper";
	//工参巡检---工参调整子
	private final String gctzPath = "/gctz";
	//室内扫楼
	private final String snslPath = "/snsl";
	//室内道路测试截图目录
	private final String snslScreenshotPath = "/screenshot";
	//室内道路测试log目录
	private final String snslLogPath = "/log";
	//室分电子化
	private final String electronic = "/electronic";
	
	private final String electronicTemp = "/electronictemp";
	//自动偏置目录
	public final String ZDPZ_PATH = "/zdpz";
	//基站信号
	public final String SIGNAL_PATH = "/signal";
	//网优自动化
	public final String WYZDH_PATH = "/wyzdh";
	//网优计费系统
	public final String WYJFXT_PATH = "/wyjfxt";
	//参数修改
	public final String CSXG_PATH = "/csxg";
	//现场拍照
	public final String XCPZ_PATH = "/xcpz";
	//应急站开通照片
	public final String YJZKT_PATH = "/yjzkt";
	//传输盒子巡检
	public final String YJHZXJ_PATH = "/yjhzxj";
	//考勤系统相关-照片
	public final String ATTENDANCE_PATH = "/attendance/";
	//基站导航照片
	public final String JZDH_PATH = "/jzdh";
	//简单硬扩照片
	public final String JDYK_PATH = "/jdyk/";
	//自动开站照片
	public final String ZDKZ_PATH = "/zdkz/";
	//工参自优化
	public final String GCZYH_PATH = "/gczyh/";
	//集中入网5G
	public final String JZRW_NR = "/jzrw/nr/";
	//集中入网4G
	public final String JZRW_LTE = "/jzrw/lte/";

	/**
	 * 获取当前目录
	 * @return
	 */
	public String getCurrentPath(){
		String path = "";
		try {
			path = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("path", "");
			if (path == null || path.isEmpty()) {
				path = getExPath() + superPath;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return path;
	}
	
	/**
	 * 根目录
	 * @return
	 */
	public String getExPath(){
		//判断SD卡是否存在
		if (isSdcard()) {
			return  Environment.getExternalStorageDirectory().getPath();
		}else {
			return SpeedTest5g.getContext().getFilesDir().getPath();
		}
	}
	
	/**
	 * 当前目录
	 * @return true SD卡   false手机内存
	 */
	public boolean getIsCurrentSd(){
		try {
			String path = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("path", "");
			if (path == null || path.isEmpty()) {
				return isSdcard();
			}else if (path.contains(SpeedTest5g.getContext().getFilesDir().getPath())) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}
	
	/**
	 * 保存路径
	 * @param isSdcard true SD卡    false手机内容
	 */
	public void setCurrentPath(boolean isSdcard){
		try {
			if (isSdcard) {
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("path", Environment.getExternalStorageDirectory().getPath() + superPath);
			}else {
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("path", SpeedTest5g.getContext().getFilesDir().getPath() + superPath);
			}
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 是否有SD卡存在
	 * @return
	 */
	public boolean isSdcard(){
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 移动目录以及目录下的文件
	 * @param lodPath 旧的目录
	 * @param nowPath 新的目录
	 */
	public void MovePath(String lodPath,String nowPath){
		File file = new File(lodPath);
		nextPathSplit(file, nowPath);
	}
	
	/**
	 * 判断文件是否存在(非目录)
	 * @param pathName
	 * @return
	 */
	public boolean isExitFile(String pathName){
		if (TextUtils.isEmpty(pathName)) {
			return false;
		}
		File file = new File(pathName);
		return file.exists() && file.isFile();
	}
	
	/**
	 * 移动目录以及目录下的文件
	 * @param lodPath 旧的目录
	 * @param nowPath 新的目录
	 */
	public void moveToPath(String lodPath,String nowPath){
		File file = new File(lodPath);
		nextPath(file, nowPath);
	}
	
	/**
	 * 遍历目录
	 * @param file
	 * @param nowPath
	 */
	private void nextPathSplit(File file,String nowPath){
		if (file.exists()) {
			//如果是目录，继续遍历
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File f : listFiles) {
					nextPathSplit(f,nowPath);
				}
				file.delete();
			//如果是文件，则直接拷贝
			}else if (file.isFile()) {
				String[] split = file.getParent().split(superPath);
				if (split.length > 1) {
					String p = "";
					for (int i = 1; i < split.length; i++) {
						p += split[i];
					}
					//新的目录地址
					File nowFile = new File(nowPath + p);
					if (!nowFile.exists()) {
						nowFile.mkdirs();
					}
					//新的文件
					int state = CopySdcardFile(file.getPath(),nowPath + p + "/" + file.getName());
					if (state == 0) file.delete();
				}
			}
		}
	}
	
	/**
	 * 遍历目录
	 * @param file
	 * @param nowPath
	 */
	private void nextPath(File file,String nowPath){
		if (file.exists()) {
			//如果是目录，继续遍历
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File f : listFiles) {
					nextPath(f,nowPath);
				}
				file.delete();
			//如果是文件，则直接拷贝
			}else if (file.isFile()) {
				//新的目录地址
				File nowFile = new File(nowPath);
				if (!nowFile.exists()) {
					nowFile.mkdirs();
				}
				//新的文件
				int state = CopySdcardFile(file.getPath(),nowPath + "/" + file.getName());
				if (state == 0) file.delete();
			}
		}
	}
	
	/**
	 * 删除对应的目录及目录下的文件
	 * @param path
	 */
	public void deteleDirectory(String path){
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			deteleMove(file);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * 删除对应目录下的空目录
	 * @param path
	 */
	public void deleteDirectoryNull(String path){
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file != null && file.exists() && file.isDirectory()) {
				File[] listFiles = file.listFiles();
				if (listFiles != null && listFiles.length > 0) {
					for (File childFile : listFiles) {
						deleteDirectoryNull(childFile.getPath());
					}
					String[] list = file.list();
					if (list == null || list.length <= 0) {
						file.delete();
					}
				}else {
					file.delete();
				}
			}
		}
	}
	
	private void deteleMove(File file){
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				if (listFiles != null && listFiles.length > 0) {
					for (File f : listFiles) {
						deteleMove(f);
					}
				}
				file.delete();
			}else if (file.isFile()) {
				file.delete();
			}
		}
	}
	
	/**
	 * 移动之前excel文件到新目录
	 */
//	public void toExcel(){
//		//获取历史目录
//		File file = new File(getEXPath() + "/WangYouBao/excel/");
//		if (file.exists() && file.isDirectory()) {
//			String pathNewExcel = getEXPath() + "/WangYouBao/Records/";
//			String pathMl;
//			File[] listFiles = file.listFiles();
//			for (File f : listFiles) {
//				if (f.getName().endsWith(".xls")) {
//					pathMl = pathNewExcel + f.getName().replace(".xls", "") + "/excel/";
//					File fPath = new File(pathMl);
//					if (!fPath.exists()) {
//						fPath.mkdirs();
//					}
//					int state = CopySdcardFile(f.getPath(),pathMl + "现场验证表-" + f.getName().split("-")[2]);
//					WybLog.syso(state);
//					if (state == 0) f.delete();
//				}
//			}
//			file.delete();
//		}
//	}
	
	/**
	 * 移动文件
	 */
	public void toFile(String oldPath,String newPath,String newName){
		//获取历史目录
		File file = new File(oldPath);
		if (file.exists() && file.isFile()) {
			File fPath = new File(newPath);
			if (!fPath.exists()) {
				fPath.mkdirs();
			}
			int state = CopySdcardFile(oldPath,newPath + newName);
			WybLog.syso(state);
			if (state == 0) file.delete();
		}
	}
	
//	public String getSdPath(){
//		String extSDCardPath = getExtSDCardPath();
//		//如果外置SD卡不存在，则获取内卡
//		if (extSDCardPath == null) {
//			return Environment.getExternalStorageDirectory().getPath();
//		}
//		return extSDCardPath;
//	}
	
//	public String getEXPath(){
//		return Environment.getExternalStorageDirectory().getPath();
//	}
	
//	/**
//	 * 获取外置SD卡路径
//	 * @return	应该就一条记录或空
//	 */
//	public String getExtSDCardPath()
//	{
//		try {
//			Runtime rt = Runtime.getRuntime();
//			Process proc = rt.exec("mount");
//			InputStream is = proc.getInputStream();
//			InputStreamReader isr = new InputStreamReader(is);
//			BufferedReader br = new BufferedReader(isr);
//			String line;
//			while ((line = br.readLine()) != null) {
//				if (line.contains("extSdCard"))
//				{
//					String [] arr = line.split(" ");
//					String path = arr[1];
//					File file = new File(path);
//					if (file.isDirectory())
//					{
//						return path;
//					}
//				}
//			}
//			isr.close();
//		} catch (Exception e) {
//		}
//		return "/mnt/sdcard";
//	}
	
//	/**
//	 * 将之前的文件剪切到隐藏目录
//	 * @return true 成功  false表示控件不足
//	 */
//	@SuppressWarnings("deprecation")
//	public boolean MobileFile(){
//		//旧地址
//		String path = getEXPath() + "/WangYouBao/downDb/";
//		String pathTo = getSdPath() + newPath;
//		File file = new File(pathTo);
//		
//		if (!file.exists()) file.mkdirs();
//		
//		file = new File(path);
//		//获取指定目录所用空间大小
//		long sizeSum = getlist(file);
//		//获取要存放的空间所剩大小
//		StatFs statfs = new StatFs( getSdPath());
//		long blockSize = statfs.getBlockSize(); 
//		long availableBlocks = statfs.getAvailableBlocks();
//		long size = availableBlocks * blockSize;
//		//当前位置空间不足，判断是否是外SD卡，如果是设置为内sd卡是否有空间
//		if (sizeSum > size && !getSdPath().equals(getEXPath())){
//			statfs = new StatFs( getEXPath());
//			blockSize = statfs.getBlockSize(); 
//			availableBlocks = statfs.getAvailableBlocks();
//			size = availableBlocks * blockSize;
//			if (sizeSum > size) {
//				return false;
//			}
//		};
//		if (file.exists()) {
//			File[] listFiles = file.listFiles();
//			for (File f : listFiles) {
//				int state = CopySdcardFile(f.getPath(),pathTo + f.getName());
//				if (state == 0) f.delete();
//			}
//		}
//		return true;
//	}
	
	//删除文件
	public void deteleFile(String pathName){
		if (!TextUtils.isEmpty(pathName)) {
			File file = new File(pathName);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * 删除WybBitmap对应数据及附件
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void deleteWybBitmapFile(String key,Object[] value){
		ArrayList<WybBitmapDb> arrWybBitmapDb = (ArrayList<WybBitmapDb>) DbHandler.getInstance().queryObj(WybBitmapDb.class, key, value);
		if (arrWybBitmapDb != null && arrWybBitmapDb.size() > 0) {
			for (WybBitmapDb item : arrWybBitmapDb) {
				if (item.getPath().endsWith("/")) {
					deteleFile(item.getPath() + item.getName());
				}else {
					deteleFile(item.getPath() + "/" + item.getName());
				}
				DbHandler.getInstance().deleteObj(item);
			}
		}
	}
	
	/**
	 * 获取指定目录的剩余空间大小
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getPathLenght(){
		StatFs statfs = new StatFs(getCurrentPath());
		//获取手机上剩余空间大小
		long blockSize = statfs.getBlockSize(); 
		long availableBlocks = statfs.getAvailableBlocks();
		long size = availableBlocks * blockSize;
		return size;
	}
	
	//文件拷贝
	//要复制的目录下的所有非子目录(文件夹)文件拷贝
	public int CopySdcardFile(String fromFile, String toFile){
		InputStream fosfrom = null;
		OutputStream fosto = null;
		try{
			fosfrom = new FileInputStream(fromFile);
			fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0){
				fosto.write(bt, 0, c);
			}
			
			return 0;
		} catch (Exception e){
			WybLog.syso("错误：" + e.getMessage());
			return -1;
		}finally{
			try {
				fosfrom.close();
				fosto.close();
			} catch (Exception e2) {
			}
		}
	}
	
//	//获取直接文件目录的大小
//	private long getlist(File file){// 递归求取目录文件个数
//		  long size = 0;
//		  if (file.isDirectory()) {
//			  File[] flist = file.listFiles();
//			  for (File f : flist) {
//				  size+=f.length();
//			  }
//		  }
//		  return size;
//	 }

	/**
	 * /design
	 * 规划设计目录
	 * @return
	 */
	public String getGhsjPath() {
		return ghsjPath;
	}
	
	/**
	 * /electronic
	 * 室分电子化目录
	 * @return
	 */
	public String getElectronic() {
		return electronic;
	}
	
	/**
	 * /electronictemp
	 * 室分电子化临时目录
	 * @return
	 */
	public String getElectronicTemp() {
		return electronicTemp;
	}
	
	/**
	 * /sf
	 * 工程施工目录
	 * @return
	 */
	public String getGcsgPath() {
		return gcsgPath;
	}

	/**
	 * /Records
	 * 入网验收目录
	 * @return
	 */
	public String getRwysPath() {
		return rwysPath;
	}
	
	/**
	 * /gcxj
	 * 代维（工参）巡检目录--工参
	 * @return
	 */
	public String getGcxjDwPath() {
		return gcxj_dw_Path;
	}
	
	/**
	 * /xncsSuper
	 * 性能测试
	 * @return
	 */
	public String getXncsSuperPath() {
		return xncsSuperPath;
	}
	
	/**
	 * /xncs
	 * 性能测试-子
	 * @return
	 */
	public String getXncsPath() {
		return xncsPath;
	}
	
	/**
	 * /inspection
	 * 代维（工参）巡检目录
	 * @return
	 */
	public String getGcxjPath() {
		return gcxjPath;
	}
	
	/**
	 * /snsl
	 * 室内扫楼目录
	 * @return
	 */
	public String getSnslPath() {
		return snslPath;
	}
	
	/**
	 * /snslScreenshotPath
	 * 室内扫楼截图目录
	 * @return
	 */
	public String getSnslScreenshotPath() {
		return snslScreenshotPath;
	}
	
	/**
	 * /snslLogPath
	 * 室内扫楼道理测试log目录
	 * @return
	 */
	public String getSnslLogPath() {
		return snslLogPath;
	}

	/**
	 * /downDb
	 * DB下载目录
	 * @return
	 */
	public String getDbPath() {
		return dbPath;
	}
	
	/**
	 * /log
	 * /LOG保存目录---如主动采集    
	 * @return
	 */
	public String getLogPath() {
		return logPath;
	}

	/**
	 * /Download
	 * 下载目录 --apk
	 * @return
	 */
	public String getDownLoad() {
		return downLoad;
	}
	
	/**
	 * /gctzSuper
	 * 工参巡检-工参调整主
	 * @return
	 */
	public String getGctzSuperPath() {
		return gctzSuperPath;
	}
	
	/**
	 * /gctz
	 * 工参巡检-工参调整子
	 * @return
	 */
	public String getGctzPath() {
		return gctzPath;
	}

	public String getSuperPath() {
		return superPath;
	}
}
