package cn.nokia.speedtest5g.speedtest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.respon.ResponseHomeInitData;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.ResponseAppFtpConfig;
import cn.nokia.speedtest5g.wifi.bean.WifiMacManu;

/**
 * 数据集合类
 * @author JQJ
 *
 */
public class SpeedTestDataSet {

	//存放服务器数据记录  全用服务器数据  有id 可用于更改备注。。.
	public static List<Db_JJ_FTPTestInfo> mServerList = new ArrayList<Db_JJ_FTPTestInfo>();
	//配置数据
	public static BeanBaseConfig mBeanBaseConfig = null;
	//50.全国版二维码图片  51.全国版下载地址  52.资料分享 53.版本信息 54.帮助信息 55.福建版二维码图片  56.福建版下载地址  57.闪屏页-小-地址 4642.闪屏页-大-地址
	//4623 4624 4625 配置流量大中小包文案
	//4702 全国版下载广告地址
    //4703 测速日志分享地址
	public static HashMap<String, String> mPersonalCenterMap = null;
	//通知数据
	public static ResponseHomeInitData mResponseHomeInitData = null;
	//测速ftp配置  列表数据
	public static ResponseAppFtpConfig mResponseAppFtpConfig = null;
	//选中的ftp配置
	public static BeanAppFtpConfig mBeanAppFtpConfig = null;
	//mac获取厂家
	public static List<WifiMacManu> mWifiMacManuList = new ArrayList<WifiMacManu>();
	//实时下载预估流量
	public static float mDownFlow = 0; 
	//实时上传预估流量
	public static float mUpFlow = 0;
    //服务器地址配置
	public static String mBindIp = ""; //默认正式服配置
}
