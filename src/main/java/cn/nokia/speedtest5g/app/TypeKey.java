package cn.nokia.speedtest5g.app;

/**
 * 存放一些key值以及一些常用的常量
 * 
 * @author Administrator
 *
 */
public class TypeKey {

	private static TypeKey tk = null;

	public static synchronized TypeKey getInstance() {

		if (tk == null) {
			tk = new TypeKey();
		}
		return tk;
	}

	/*-----------------------String---------------------------*/
	public final String KEY_TO_IP_TYPE = "keyToIpType";//当前要连接的地址类型 1-正式地址、2-预发布地址、3-测试地址、默认空也是正式地址
	public final String LOGIN_PROVINCE_TAG = "keyToprovinceTag";//当前要连接的地址区域
	public final String ACTION_FINISH = "com.wyb.ui.finish";//关闭当前activity
	public final String BAIDU_ACCESS_TOKEN = "accessTokenBd";// 百度图片识别Token
	public final String BAIDU_ACCESS_TOKEN_TIME = "accessTokenBdTime";// 百度图片识别时效
	public final String BAIDU_ACCESS_TOKEN_JDYK = "accessTokenBdToJdyk";// 百度图片识别Token-简单硬扩
	public final String BAIDU_ACCESS_TOKEN_TIME_JDYK = "accessTokenBdTimeToJdyk";// 百度图片识别时效-简单硬扩
	private final String CODE_PATH = "codePath";
	private final String CODE_PCI_PATH = "codePciPath";
	private final String LOGIN_TOKEN = "SubToken";
	private final String LOGIN_ID = "login_id";
	private final String LOGIN_NAME = "loginname";
	private final String LOGIN_PASSWD = "passwd";
	public final String LOGIN_SELECT_ID = "login_select_id";//当前登录4A选择的用户ID
	public final String LOGIN_SELECT_USER = "login_select_user";//当前登录4A选择的用户（从账户-手机号）
	public final String LOGIN_TO_USER = "login_to_user";//如果是4A登录则取从账户
	public final String LOGIN_SESSION_ID = "login_session_id";//当前登录帐号的会话ID
	public final String HOME_INIT_DATA = "home_init_data";// 首页初始化数据
	private final String LOGIN_MENU = "menu_id";
	private final String USER_PHONE = "phone";
	private final String LOGIN_MZ = "username";
	private final String USER_DEPART = "depart";
	private final String SET_UPLOAD_WIFI = "wifiOf";
	private final String SET_UPLOAD_TIME = "timeSet";
	private final String SET_UPLOAD_TIME_START = "timeStart";
	private final String SET_UPLOAD_TIME_END = "timeEnd";
	public final String VERSION_CODE = "codeVersion";//当前软件版本号 如：50
	public final String VERSION_NAME = "nameVersion";//当前软件版本名称 如：v2.3.3
	private final String SPLIT_JD = "、";
	private final String WIDTH = "width";
	private final String HEIGHT = "height";
	private final String CAMERA_DX_SIZE = "cameraDxSize";
	private final String CAMERA_XS_SIZE = "cameraXsSize";
	private final String CAMERA_XS_HEIGHT = "cameraXsHeight";

	private final String PHONE_MODEL = "model";
	private final String PHONE_RELEASE = "release";
	private final String PHONE_IMEI = "imei";
	private final String PHONE_IMSI = "imsi";
	private final String PHONE_UUID = "uuid";

	private final String OFF_STATE = "isoff";

	private final String GPS_LON = "lonGps";
	private final String GPS_LAT = "latGps";
	private final String GPS_TYPE = "typeGps";

	private final String OBJECT_LOCK = "locks";

	private final String GCSJ = "gcsj";

	private final String SIGN_GIS = "signgis";

	private final String IS_UPDATE_GCXJ = "update_gcxj";
	private final String IS_LAUNCHER = "isLauncher";
	private final String IS_MAIN_FINISH = "isMainFinish";
	private final String CHANNEL_TYPE = "channelType";

	private final String TEST_TYPENTE = "test_typenet";
	private final String TEST_CGI = "test_cgi";
	private final String TEST_RSRP = "test_rsrp";
	private final String TEST_SINR = "test_sinr";

	private final String GCXJ_CAMERA = "gcxj_camera";

	private final String GCXJ_VALUE = "gcxj_value";

	private final String GCXJ_NOWCAMERA_RRUID = "camera_rruid";

	private final String XNCS_SL_DOWN = "sldown";
	private final String XNCS_SL_UPLOAD = "slupload";
	private final String XNCS_SL_DRAW = "xs_drawing";
	private final String HZDY_SL_DOWN = "hzdySldown";
	private final String HZDY_SL_UPLOAD = "hzdySlupload";
	private final String HZDY_IS_SPECIAL = "hzdyIsSpecial";// 特殊场景

	public final String FTP_POSITION = "ftpPosition";//FTP当前选择地址游标
	private final String TIME_RSRP = "rsrptime";
	//	public final String FTP_DOWN_PROPERTYS = "ftpDownPropertys";//当前下载选择项 0省公司 1自动选择 2手动设置apn 3省公司公网
	//	public final String FTP_UPLOAD_PROPERTYS = "ftpUploadPropertys";//当前上传选择项 0省公司 1自动选择 2手动设置apn 3省公司公网
	public final String CUSTOM_FTP_DOWN_OPEN = "customFtpDownOpen";//自定义是否要测试下载
	public final String CUSTOM_FTP_UPLOAD_OPEN = "customFtpUploadOpen";//自定义是否要测试上传
	public final String FTP_DOWN_OPEN = "ftpDownOpen";//是否要测试下载
	public final String FTP_UPLOAD_OPEN = "ftpUploadOpen";//是否要测试上传
	public final String FTP_UPLOAD_IP = "ftpUploadIp";//apn设置上传ip
	public final String FTP_UPLOAD_PORT = "ftpUploadPort";//apn设置上传端口
	public final String FTP_UPLOAD_PATH = "ftpUploadPath";//apn设置上传地址
	public final String FTP_UPLOAD_USER = "ftpUploadUser";//apn设置上传用户
	public final String FTP_UPLOAD_PASSWD = "ftpUploadPasswd";//apn设置上传密码
	public final String FTP_UPLOAD_THREAD = "ftpUploadThread";//apn设置上传线程数
	public final String FTP_DOWN_IP = "ftpDownIp"; //apn设置下载ip
	public final String FTP_DOWN_PORT = "ftpDownPort";//apn设置下载端口
	public final String FTP_DOWN_PATH = "ftpDownPath";//apn设置下载地址
	public final String FTP_DOWN_USER = "ftpDownUser";//apn设置下载用户
	public final String FTP_DOWN_PASSWD = "ftpDownPasswd";//apn设置下载密码
	public final String FTP_DOWN_THREAD = "ftpDownThread";//apn设置下载线程数
	public final String FTP_APN_IP_DOWN = "ftpapnIpDwon";//自动选择apn服务器的ip地址
	public final String FTP_APN_IP_UPLOAD = "ftpapnIpUpload";//自动选择apn服务器的ip地址
	public final String FTP_DOWN_PROPERTYS_HZ = "ftpDownPropertysHz";//宏站单验-当前下载选择项 0省公司 1自动选择 2手动设置apn
	public final String FTP_UPLOAD_PROPERTYS_HZ = "ftpUploadPropertysHz";//宏站单验-当前上传选择项 0省公司 1自动选择 2手动设置apn

	private final String ACTION_GCWH_SIGN = "com.wyb.gcwh.sign";
	private final String ACTION_XNCS_SIGN = "com.wyb.xncs.sign";
	private final String ACTION_PARAMETER_SIGN = "com.wyb.parameter.sign";
	private final String ACTION_XNCS_DATAUPDATE = "com.wyb.xncs.dataupdate";
	private final String ACTION_SNSL_DATAUPDATE = "com.wyb.snsl.update";
	private final String ACTION_PARAMETER_DATAUPDATE = "com.wyb.parameter.dataupdate";//参数修改 数据更新广播
	private final String ACTION_PARAMETER_CONFIG_DATAUPDATE = "com.wyb.parameter.config.dataupdate";//参数配置
	private final String ACTION_PARAMETER_MAP_DATAUPDATE = "com.wyb.parameter.map.dataupdate";//参数配置---地图页面数据更新
	private final String HZDY_ENB = "hzdy_enb";

	public final int MARKER_ZOOM = 13;// 最小放大倍数
	public final int CLUSTER_COUNT = 40;
	public final int CLUSTER_DIAMETER = 200;
	public final String PACKAGE_GPS = "com.fjmcc.wangyoubao.GPSInfoService";// 定位广播
	public final String COMPLAINTEST_TEST_UPDATEUI = "cn.nokia.speedtest5g.complaintest.updateui";// 投诉测试ui更新
	public final String TEST_PCI_TRAVERSE = "test.pci.traverse";// 遍历测试广播
	public final String PACKAGE_TRANSMIT_BOX_INSPECTION = "cn.nokia.speedtest5g.transmitBoxInspection";// 巡检盒子广播
	public final String PACKAGE_EMERGENT_CHOOSE = "cn.nokia.speedtest5g.emergentChoose";// 应急宝选择广播
	public String VerName = "Unkonw";
	public boolean hasCamera = false;// 照相机
	public boolean hasFlashLight = false;// 闪光灯
	public int cameraMaxZoom = 0;
	// 是否需要纠偏---针对单验LTE信号
	public final String IS_RECTIFY = "isRectify";
	public final String IS_CAIYANG = "iscaiyang";// 是否正在采集遍历测试
	public final String TIME_RATE = "rate";// 采样点的更新频率
	//遍历测试 new
	public final String ERGODIC_SETTING_TIME_RATE = "ergodic_time_rate";// 采样点的更新频率
	public final String ERGODIC_SETTING_LOG_TIME = "ergodic_log_time";// 日志保存时长
	public final String ERGODIC_SETTING_SIGNAL_MODE = "ergodic_signal_mode";// 当前测试模式
	public final String ERGODIC_SETTING_TEST_SPEED_TIMES = "ergodic_test_speed_times";// 速率测试次数
	public final String ERGODIC_SETTING_TIME_SPEED_CELL = "ergodic_time_speed_cell";// 速率测试间隔
	public final String ERGODIC_SETTING_TEST_PING_TIMES = "ergodic_test_ping_times";// ping测试次数
	public final String ERGODIC_SETTING_TIME_PING_CELL = "ergodic_time_ping_cell";// ping测试间隔
	public final String ERGODIC_SETTING_TEST_PING_IS_CUSTOM = "ergodic_test_ping_is_custom";// 是否自定义ping地址
	public final String ERGODIC_SETTING_TEST_PING_ADDRESS = "ergodic_test_ping_address";// ping地址
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_IP = "ergodic_ftp_download_ip";// 下载地址
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_PATH = "ergodic_ftp_download_path";// 下载路径
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_THREAD = "ergodic_ftp_download_thread";// 下载线程数
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_PORT = "ergodic_ftp_download_port";// 下载端口
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_USER = "ergodic_ftp_download_user";// 下载用户
	public final String ERGODIC_SETTING_FTP_DOWNLOAD_PASSWORD = "ergodic_ftp_download_password";// 下载密码
	public final String ERGODIC_SETTING_FTP_UPLOAD_IP = "ergodic_ftp_upload_ip";// 上传地址
	public final String ERGODIC_SETTING_FTP_UPLOAD_SIZE = "ergodic_ftp_upload_size";// 上传大小
	public final String ERGODIC_SETTING_FTP_UPLOAD_THREAD = "ergodic_ftp_upload_thread";// 上传线程数
	public final String ERGODIC_SETTING_FTP_UPLOAD_PORT = "ergodic_ftp_upload_port";// 上传端口
	public final String ERGODIC_SETTING_FTP_UPLOAD_USER = "ergodic_ftp_upload_user";// 上传用户
	public final String ERGODIC_SETTING_FTP_UPLOAD_PASSWORD = "ergodic_ftp_upload_password";// 下载密码
	//室分图纸图层是否选中
	public final String IS_SHOW_DRAWING = "station_drawing";
	// RRU图层是否选中
	public final String IS_STATION_RRU = "station_rru";

	// 业务测试设置项
	public final String ROAD_SETTING_LOG_TIME = "road_setting_save_log_time";//DT-log保存时长
	public final String ROAD_SETTING_SPEED_TEST_SIZE = "road_setting_speed_test_size";//DT-下载包大小
	public final String ROAD_SETTING_SPEED_TEST_TIMES = "road_setting_speed_test_times";//DT-测试次数
	public final String ROAD_SETTING_SPEED_TEST_TIME_CELL = "road_setting_speed_test_time_cell";//DT-测试间隔
	//	public final String SETTING_SPEED_TEST_DOWN_TYPE = "setting_speed_test_download_type";//DT-下载类型-0为省公司固定FTP服务器，1为APNFTP服务器,2为自定义服务器,3自动选择apn
	//	public final String SETTING_SPEED_TEST_DOWN_IP = "setting_speed_test_download_ip";//DT-apn设置下载ip
	//	public final String SETTING_SPEED_TEST_DOWN_PATH = "setting_speed_test_download_path";//DT-apn设置下载地址
	//	public final String SETTING_SPEED_TEST_DOWN_THREAD_NUM = "setting_speed_test_download_thread_num";//DT-apn设置下载线程数
	//	public final String SETTING_SPEED_TEST_DOWN_PORT = "setting_speed_test_download_port";//DT-apn设置下载端口
	//	public final String SETTING_SPEED_TEST_DOWN_USER = "setting_speed_test_download_user";//DT-apn设置下载用户
	//	public final String SETTING_SPEED_TEST_DOWN_PWD = "setting_speed_test_download_pwd";//DT-apn设置下载密码
	//	public final String SETTING_SPEED_TEST_UPLOAD_TYPE = "setting_speed_test_upload_type";//DT-上传类型-0为省公司固定FTP服务器，1为APNFTP服务器,2为自定义服务器 ,3自动选择apn
	//	public final String SETTING_SPEED_TEST_UPLOAD_IP = "setting_speed_test_upload_ip";//DT-apn设置上传ip
	//	public final String SETTING_SPEED_TEST_UPLOAD_SIZE = "setting_speed_test_upload_size";//DT-apn设置上传大小
	//	public final String SETTING_SPEED_TEST_UPLOAD_THREAD_NUM = "setting_speed_test_upload_thread_num";//DT-apn设置上传线程数
	//	public final String SETTING_SPEED_TEST_UPLOAD_PORT = "setting_speed_test_upload_port";//DT-apn设置上传端口
	//	public final String SETTING_SPEED_TEST_UPLOAD_USER = "setting_speed_test_upload_user";//DT-apn设置上传用户
	//	public final String SETTING_SPEED_TEST_UPLOAD_PWD = "setting_speed_test_upload_pwd";//DT-apn设置上传密码
	//	public final String ROAD_SETTING_VOICE_TEST_NET_TYPE = "road_setting_voice_test_net_type";//DT-拨打网络
	public final String ROAD_SETTING_VOICE_TEST_PHONE_NUM = "road_setting_voice_test_phone_num";//DT-拨打电话
	public final String ROAD_SETTING_VOICE_TEST_CALL_TIMES = "road_setting_voice_test_call_times";//DT-拨打次数
	public final String ROAD_SETTING_VOICE_TEST_CALL_TIME_CELL = "road_setting_voice_test_call_time_cell_new";//DT-拨打间隔
	public final String ROAD_SETTING_VOICE_TEST_CALL_TIME = "road_setting_voice_test_call_time";//DT-拨打时长(S)
	public final String ROAD_SETTING_PING_TEST_ADDRESS = "road_setting_ping_test_time_address";//DT-ping次数
	public final String ROAD_SETTING_PING_TEST_IS_CUSTOM = "road_setting_ping_test_is_custom";//DT_ping是否自定义地址
	public final String ROAD_SETTING_PING_TEST_TIMES = "road_setting_ping_test_time_times";//DT-ping次数
	public final String ROAD_SETTING_PING_TEST_TIME_CELL = "road_setting_ping_test_time_cell";//DT-ping间隔

	//投诉测试规则设置配置
	public final String TSCS_RULE_SETTING_GSM_CB_YJCS = "tscs_rule_setting_gsm_cb_yjcs"; 
	public final String TSCS_RULE_SETTING_GSM_CB_JCCS = "tscs_rule_setting_gsm_cb_jccs"; 
	public final String TSCS_RULE_SETTING_GSM_CB_BDCS = "tscs_rule_setting_gsm_cb_bdcs"; 
	public final String TSCS_RULE_SETTING_GSM_ET_CSSC = "tscs_rule_setting_gsm_et_cssc"; //测试时长
	public final String TSCS_RULE_SETTING_GSM_ET_BDHM = "tscs_rule_setting_gsm_et_bdhm"; //拨打号码
	public final String TSCS_RULE_SETTING_GSM_ET_BDCS = "tscs_rule_setting_gsm_et_bdcs"; //拨打次数
	public final String TSCS_RULE_SETTING_GSM_ET_BDJG = "tscs_rule_setting_gsm_et_bdjg"; //拨打间隔
	public final String TSCS_RULE_SETTING_GSM_ET_BDSC = "tscs_rule_setting_gsm_et_bdsc"; //拨打时长
	public final String TSCS_RULE_SETTING_LTE_CB_YJCS = "tscs_rule_setting_lte_cb_yjcs";
	public final String TSCS_RULE_SETTING_LTE_CB_JCCS = "tscs_rule_setting_lte_cb_jccs";
	public final String TSCS_RULE_SETTING_LTE_CB_VOLTE_BDCS = "tscs_rule_setting_lte_cb_volte_bdcs";
	public final String TSCS_RULE_SETTING_LTE_CB_VOLTE_VIDEO_BDCS = "tscs_rule_setting_lte_cb_volte_video_bdcs";
	public final String TSCS_RULE_SETTING_LTE_CB_CSFB_BDCS = "tscs_rule_setting_lte_cb_csfb_bdcs";
	public final String TSCS_RULE_SETTING_LTE_CB_PING = "tscs_rule_setting_lte_cb_ping";

	public final String TSCS_RULE_SETTING_LTE_ET_CSSC = "tscs_rule_setting_lte_et_cssc"; //测试时长
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_BDHM = "tscs_rule_setting_lte_et_volte_bdhm"; //拨打号码
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_BDCS = "tscs_rule_setting_lte_et_volte_bdcs"; //拨打次数
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_BDJG = "tscs_rule_setting_lte_et_volte_bdjg"; //拨打间隔
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_BDSC = "tscs_rule_setting_lte_et_volte_bdsc"; //拨打时长
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_VIDEO_BDHM = "tscs_rule_setting_lte_et_volte_video_bdhm"; //拨打号码
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_VIDEO_BDCS = "tscs_rule_setting_lte_et_volte_video_bdcs"; //拨打次数
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_VIDEO_BDJG = "tscs_rule_setting_lte_et_volte_video_bdjg"; //拨打间隔
	public final String TSCS_RULE_SETTING_LTE_ET_VOLTE_VIDEO_BDSC = "tscs_rule_setting_lte_et_volte_video_bdsc"; //拨打时长
	public final String TSCS_RULE_SETTING_LTE_ET_CSFB_BDHM = "tscs_rule_setting_lte_et_csfb_bdhm"; //拨打号码
	public final String TSCS_RULE_SETTING_LTE_ET_CSFB_BDCS = "tscs_rule_setting_lte_et_csfb_bdcs"; //拨打次数
	public final String TSCS_RULE_SETTING_LTE_ET_CSFB_BDJG = "tscs_rule_setting_lte_et_csfb_bdjg"; //拨打间隔
	public final String TSCS_RULE_SETTING_LTE_ET_CSFB_BDSC = "tscs_rule_setting_lte_et_csfb_bdsc"; //拨打时长
	public final String TSCS_RULE_SETTING_LTE_ET_PING_CS = "tscs_rule_setting_lte_et_ping_cs"; //ping次数
	public final String TSCS_RULE_SETTING_LTE_ET_PING_JG = "tscs_rule_setting_lte_et_ping_jg"; //ping间隔
	public final String TSCS_RULE_SETTING_LTE_ET_PING_FWQ = "tscs_rule_setting_lte_et_ping_fwq"; //ping服务器
	//	public final String TSCS_IS_CAN_MODIFY = "tscs_is_can_modify"; //是否可以修改

	//是否超时退出
	public final String IS_GO_LOGIN = "goLogin";
	//是否当前用户在其他手机登录等状态  -1未任何操作超时   0未登录 1已登录 2已过期 3已退出登录 4被退出
	public final String IS_EXIT_USER_STATUS = "isExitUser";

	//pci 
	public final String PCI_PINDIAN = "pciPindian";
	//应急宝设备类型
	public final String EMERGENCY_DEVICE_TYPE = "emergencyDeviceType";

	//pci 显示圈选提示
	public final String PCI_SHOW_BRACER_HINT = "pciShowBracerHint";
	//参数修改--- 配置数据
	public final String PARAMETER_CONF = "parameterConf";
	//字典配置最新时间
	public final String APP_CONF_DICT_TIME = "appConfDictTimes";
	//字典配置数据
	public final String APP_CONF_DICT_DATA = "appConfDictData";
	//ftp配置最新时间
	public final String APP_CONF_FTP_TIME = "appConfFtpTIme";

	// 一键诊断相关常量
	// 最近一次按CI诊断的条件
	public final String YJZD_LAST_DIAGNOSIS_CONDITION_BY_CI = "yjzd_last_diagnosis_condition_by_ci";
	// 当前诊断条件
	public final String YJZD_CURRENT_DIAGNOSIS_CONDITION = "yjzd_current_diagnosis_condition";
	// fu'qu当前时间
	public final String NOW_TIME = "now_time";
	// 最近一次短信验证码登入成功的时间
	public final String LAST_SMS_CODE_LONG_TIME = "last_sms_code_long_time";
	// 短信验证码登入超期时间
	public final String LAST_SMS_CODE_LONG_OVERDUE_TIME = "last_sms_code_long_overdue_time";
	// 最近一次登入类型
	public final String LAST_LOGIN_TYPE = "last_login_type";
	//邻区标签选择类型
	public final String KEY_LQ_TAG = "keyLqTag";
	//基站信号曲线图配置
	public final String KEY_JZXH_PIC_TAG = "keyJzxhPicTag";
	//基站信号列表头标题设置
	public final String KEY_JZXH_LIST_TITLE_TAG = "keyJzxhListTitleTag";
	//NSA邻区标签选择类型
	public final String KEY_NSA_LQ_TAG = "keyNsaLqTag";
	//NSA信号曲线图配置
	public final String KEY_NSA_PIC_TAG = "keyNsaPicTag";
	//NSA数据标签选择类型
	public final String KEY_NSA_DATA_TAG = "keyNsaDataTag";
	//图层-应急宝
	public final String LAYER_EMERGENT_DEVICE = "layerEmergentDevice";
	//图层-应急宝
	public final String LAYER_EMERGENT_CAR = "layerEmergentCar";
	//图层-应急盒子
	public final String LAYER_EMERGENT_BOX = "layerEmergentBox";
	//道路测试邻区标签选择类型
	public final String KEY_LQ_TAG_FOR_ROADTEST = "keyLqTagForRoadtest";
	// 我的应用 菜单ID
	public final String KEY_MY_APPS_MENU_CODES = "keyMyAppsMenuCodes ";

	/**
	 * 当前操作MyTextWatcherCommon回调标记
	 */
	public final int TEXTWATCHER_WHAT = 1001;
	/**
	 * 查看照片---wybBitmapDb类（传递字段为type,附件类型字段imgType）
	 */
	public final int PHOTO_SHOW_WYBBITMAP = 11;
	/**
	 * 消息通知Key
	 */
	public final String KEY_TOAST = "toast";
	/**
	 * 集中入网宏站
	 */
	public final int TYPE_OUTSITE = 0;
	/**
	 * 集中入网室分
	 */
	public final int TYPE_INSITE = 1;

	/**
	 * 入网列表 获取数据
	 */
	public final int RWLIST_MSG_LIST = 1;

	/**
	 * 入网列表 获取网络数据
	 */
	public final int RWLIST_MSG_LIST_NETWORK = 2;

	/**
	 * 入网列表 item点击事件
	 */
	public final int RWLIST_MSG_LIST_CLICK = 3;

	/**
	 * 入网列表 item删除
	 */
	public final int RWLIST_MSG_LIST_DETELE = 4;

	/**
	 * 入网消息提示
	 */
	public final int RWLIST_MSG_TOAST = 5;

	/**
	 * 入网列表数据上传
	 */
	public final int RWLIST_MSG_UPLOAD = 6;

	/**
	 * 入网载入历史记录适配器
	 */
	public final int RWLIST_MSG_LOADLIST = 7;

	/**
	 * 投诉测试 获取工单列表
	 */
	public final int TSCS_MSG_LIST = 1;

	/**
	 * 投诉测试 工单详情
	 */
	public final int TSCS_MSG_LIST_CLICK = 2;
	/**
	 * 投诉测试 工单搜索
	 */
	public final int TSCS_MSG_LIST_SEARCH = 3;
	/**
	 * 考勤类型 - 车辆
	 */
	public final int ATTENDANCE_TYPE_CAR = 2;
	/**
	 * 考勤类型 - 仪表
	 */
	public final int ATTENDANCE_TYPE_DEVICE = 3;
	/**
	 * 上传成功广播
	 */
	public final String ACTION_UPLOAD_OK_UPDATE = "com.fjmcc.wyb.uploadok";
	/**
	 * 上传广播
	 */
	public final String ACTION_UPLOAD_UPDATE = "com.fjmcc.wyb.uploadupdate";
	/**
	 * 上传完成后更新本地库的广播
	 */
	public final String ACTION_UPLOAD_SUCCESS = "com.fjmcc.wyb.uploadsuccess";

	/**
	 * 遍历测试广播
	 */
	public final String ACTION_TRAVERSE = "com.fjmcc.wyb.traverse";
	/**
	 * 遍历测试完成后的广播
	 */
	public final String ACTION_DATAUPDATE = "com.fjmcc.wyb.datauptra";
	/**
	 * 主页-首页
	 */
	public final String ACTION_MAINHOME_HOME = "com.fjmcc.wyb.mainhome.homepage";
	/**
	 * 主页-作业流程广播
	 */
	public final String ACTION_MAINHOME_WORKFLOW = "com.fjmcc.wyb.mainhome.workflow";
	/**
	 * 主页框架页面广播--type 
	 * EnumRequest.OTHER_MAINHOME_TYPE_USER_TOURIST 提示游客权限相关  
	 * EnumRequest.OTHER_MAINHOME_TO_LOGIN 跳转登录页面
	 * EnumRequest.OTHER_MAINHOME_TYPE_PERSONAL 个人中心红点提示
	 */
	public final String ACTION_MAINHOME_SUPER = "com.fjmcc.wyb.mainhome.super";
	
	/**
	 * 主界面分页跳转
	 */
	public final String ACTION_HOME_TAG_CHANGE = "com.fjmcc.wyb.mainhome.tag.change";
	/**
	 * 主页-GIS页面广播
	 */
	public final String ACTION_MAINHOME_GIS = "com.fjmcc.wyb.mainhome.gis";
	/**
	 * 消息提示通知数据广播
	 */
	public final String ACTION_TOAST_NOTICE = "com.fjmcc.wyb.toastnotice";
	/**
	 * 工参巡检、工参调整 GcxjInfo对象更新
	 */
	public final String ACTION_GCXJANDGCTZ_UPDATE = "com.fjmcc.wyb.gcxjgctzinfo.update";
	/**
	 * 定时器更新时间广播-地铁测试
	 */
	public final String ACTION_DT_TIMER_UPDATE = "com.fjmcc.wyb.dttimer";
	/**
	 * TabActivity页面结果fang'hui
	 */
	public final String ACTION_ON_TAB_ACTIVITY_RESULT = "com.fjmcc.wyb.on.tabactivity.result";
	/**
	 * 室内LTE覆盖率1
	 */
	public final int RATE_LTE_ONE = -108;
	/**
	 * 室内LTE覆盖率2
	 */
	public final int RATE_LTE_TWO = -105;
	/**
	 * 遍历测试的LTE覆盖率SINR
	 */
	public final int RATE_LTE_SINR = 15;
	// 信号曲线图Y轴最小值和最大值
	public final int RSRP_MIN = -130;
	public final int RSRP_MAX = -50;
	public final int SINR_MIN = -40;
	public final int SINR_MAX = 40;
	public final int RXLEV_MIN = -120;
	public final int RXLEV_MAX = -40;

	/**
	 * 道路测试完成后的广播
	 */
	public final String ACTION_DLCS_SAVE = "com.fjmcc.wyb.dlcsupdata";
	// ------------------------------------历史记录-搜索记录KEY-----start----------------
	public final int XNCS_SEARCH = 1001;// 定点测试搜索

	public final int SNSL_SEARCH = 1002;// 室内扫楼搜索

	public final int SNSL_LOCATION = 1003;// 室内扫楼具体位置

	public final int DDCSADD_NAME = 1004;// 定点测试添加站名

	public final int DDCSADD_ENBID = 1005;// 定点测试添加enbid

	public final int GCXJ_SIGN = 1006;// 工参巡检签名

	public final int FAULT_SIGN = 1007;// 室分电子化签名

	public final int FAULT_SEARCH = 1008;// 室分电子化搜索

	public final int GCXJ_SEARCH = 1009;// 工参巡检搜索

	public final int GCXJ_NAME = 1010;// 工参巡检增加站名

	public final int GCXJ_ENB = 1011;// 工参巡检增加EnbId

	public final int RW_LIST_SEARCH = 1012;// 入网搜索

	public final int RW_LIST_NAME = 1013;// 入网新增基站名

	public final int RW_LIST_ENB = 1014;// 入网新增基站ENB

	public final int RW_LIST_RRU = 1015;// 入网新增基站RRU

	public final int RW_SIGN_UNIT = 1016;// 入网签名施工单位

	public final int RW_SIGN_RESULT = 1017;// 入网签名结论

	public final int SFDY_SEARCH = 1018;// 室分单验搜索

	public final int SFDY_PHONE = 1019;// 室分单验测试人员手机

	public final int SFDY_EXPLAIN = 1020;// 室分单验遗留问题

	public final int SFDY_TEST_PHONE = 1021;// 室分单验测试手机

	public final int SFDY_DESIGN_SUN = 1022;// 外引天线总量

	public final int SFDY_LYMC = 1023;// 室分单验楼宇名称

	public final int SFDY_JTWZ = 1024;// 室分单验楼宇具体位置

	public final int HZDY_SEARCH = 1025;// 宏站单验搜索

	public final int HZDY_PERSON = 1026;// 宏站单验测试人员

	public final int HZDY_PHONE = 1027;// 宏站单验测试人员手机

	public final int HZDY_QUESTION = 1028;// 宏站单验遗漏问题

	public final int HZDY_VOICE_PHONE = 1029;// 宏站单验语音电话

	public final int HZDY_VEDIO_PHONE = 1030;// 宏站单验视频电话

	public final int HZDY_CONN_RATE = 1031;// 宏站单验视频电话接通率

	public final int HZDY_REMARK = 1032;// 宏站单验签名备注

	public final int GIS_SEARCH_SITE_KEYWORD_OR_ENB_OR_CI = 1033;// GIS基站定位关键字或enbid/CI

	public final int TC_PHONE = 1035;// 网络挑刺测试手机

	public final int TC_OTHER = 1036;// 网络挑刺其它问题

	public final int SFDY_REMARK = 1037;// 室分单验签名备注

	public final int RW_LIST_SEARCH_SF = 1038;// 室分入网搜索

	public final int RW_LIST_NAME_SF = 1039;// 室分入网新增基站名

	public final int RW_LIST_ENB_SF = 1040;// 室分入网新增基站ENB

	public final int RW_LIST_RRU_SF = 1041;// 室分入网新增基站RRU

	public final int RW_SIGN_UNIT_SF = 1042;// 室分入网签名施工单位

	public final int RW_SIGN_RESULT_SF = 1043;// 室分入网签名结论

	public final int GCXJ_HEIGHT = 1044;// 工参巡检挂高

	public final int GCXJ_SEARCH_TZ = 1045;// 工参调整搜索

	public final int GCXJ_NAME_TZ = 1046;// 工参调整增加站名

	public final int GCXJ_ENB_TZ = 1047;// 工参调整增加EnbId

	public final int GCXJ_HEIGHT_TZ = 1048;// 工参调整挂高

	public final int GCXJ_SIGN_TZ = 1049;// 工参调整签名

	public final int GCXJ_SIGN_XN = 1050;// 性能测试签名

	public final int FAULT_LOCATE = 1051;// 室分图纸设备位置

	public final int FAULT_RANGE = 1052;// 室分图纸覆盖范围

	public final int SNSL_WYD_SEARCH = 1053;// 室内扫楼物业点搜索框

	public final int TC_TEST_PHONE = 1054;// 网络挑刺具体测试手机

	public final int TC_LOCATE = 1055;// 网络挑刺具体位置

	public final int XNCS_LOCATE = 1056;// 定点测试具体位置

	public final int TX_CI_SITE = 1058;//天线巡警首页CI及小区名搜索记录

	public final int HISTORY_PCI_YS = 1059;//网优自动化-pci运算查询

	public final int HISTORY_PCI_XG = 1060;//网优自动化-pci修改查询

	public final int HISTORY_WYZDH_ORDERS = 1061;//网优自动化-接单页

	public final int HISTORY_WYZDH_MYWORK = 1062;//网优自动化-我的工单

	public final int HISTORY_GCTZ_SEND_SEARCH = 1063;//工参调整-工单派发-工单搜索

	public final int PARAMETER_ALERT = 1064;//参数修改

	public final int GCXJ_SIGN_PARAMETER = 1065;// 参数签名

	public final int YJZD_CI_ADDRESS_SEARCH_HISTORY = 1066;//一键诊断-CI/地址搜索记录

	public final int YJZD_WORK_ORDER = 1067;// 一键诊断-工单号输入历史记录

	public final int PARAMETER_ALERT_CELL_SEARCH = 1068;//参数修改---小区搜索

	public final int PICTURE_CELL_SEARCH = 1069;//现场拍照---小区搜索

	public final int HISTORY_LOGIN_USER = 1071;// 登录模块-用户

	public final int TSCS_HISTORY_SEARCH = 1072;// 投诉测试--工单查询 历史记录

	public final int NBTEST_HISTORY_COMMAND = 1073;// NB测试--指令历史记录

	public final int HISTORY_JZDH_SEARCH = 1074;// 基站导航-基站搜索

	public final int TKZYH_HISTORY_JZSS_SEARCH = 1075;//天馈自优化-基站搜索

	public final int YCDT_HISTORY_DTCX_SEARCH = 1076;//远程电调-电调查询

	public final int YCDT_HISTORY_WDDT_SEARCH = 1077;//远程电调-我的电调查询

	public final int HISTORY_LOGIN_WANGYOUBAO = 1078;// 登录账号-网优宝账号登录

	public final int HISTORY_LOGIN_4A = 1079;// 登录账号-4A

	public final int MGB_WORK_LIST_SEARCH = 1080;//满格宝-工单搜索

	public final int HISTORY_SEARCH_NOTIFY = 1081;//公告-搜索历史

	public final int HISTORY_JDYK_WORK_SERACH = 1082;//简单硬扩-我的工单搜索历史

	public final int ADDRESS_SEARCH = 1083;//地址搜索

	public final int EMERGENT_DEVICE_SEARCH = 1084;//应急宝-设备搜索

	public final int EMERGENT_BOX_SEARCH = 1085;//应急盒子-设备搜索

	public final int HISTORY_SEARCH_ATTENDANCE_APPROVE = 1086;// 人员考勤审批搜索历史

	public final int HISTORY_SEARCH_GIS_DDGJZ = 1087;//地图地点关键字搜索历史

	public final int EMERGENT_DEVICE_DEPLOY_SEARCH = 1088;//应急宝-部署时长搜索

	public final int HISTORY_ZDKZ_WORK_SERACH = 1089;//自动开站-我的工单搜索历史

	public final int HISTORY_ZDKZ_LIST_SERACH = 1090;//自动开站-工单列表搜索历史

	public final int HISTORY_ZDKZ_WORK_NR_SERACH = 1091;//自动开站nr-我的工单搜索历史

	public final int HISTORY_ZDKZ_LIST_NR_SERACH = 1092;//自动开站nr-工单列表搜索历史

	public final int HISTORY_GCZYH_WT = 1093;//工参自优化-签名遗留问题

	public final int HISTORY_GCZYH_HEIGHT = 1094;//工参自优化-挂高

	public final int HISTORY_FTP_SEARTH = 1095;//速率测试历史记录搜索

	public final int HISTORY_NR_CELL_ACTIVATION = 1096;//5G小区激活搜索记录

	public final int HISTORY_JZRW_NR_HZ = 1097;//集中入网5G宏站搜索记录

	public final int HISTORY_JZRW_NR_SF = 1098;//集中入网5G室分搜索记录

	public final int HISTORY_JZRW_NR_ADD_STATION_NAME_HZ = 1099;//集中入网5G添加站点-名称-宏站

	public final int HISTORY_JZRW_NR_ADD_STATION_GNBID_HZ = 1100;//集中入网5G添加站点-GNBID-宏站

	public final int HISTORY_JZRW_NR_ADD_STATION_NAME_SF = 1101;//集中入网5G添加站点-名称-室分

	public final int HISTORY_JZRW_NR_ADD_STATION_GNBID_SF = 1102;//集中入网5G添加站点-GNBID-室分

	public final int HISTORY_JZRW_NR_SIGN_SGDW_HZ = 1103;//集中入网5G-签名施工单位-宏站

	public final int HISTORY_JZRW_NR_SIGN_JL_HZ = 1104;//集中入网5G-签名结论-宏站

	public final int HISTORY_JZRW_NR_SIGN_SGDW_SF = 1105;//集中入网5G-签名施工单位-室分

	public final int HISTORY_JZRW_NR_SIGN_JL_SF = 1106;//集中入网5G-签名结论-室分

	public final int HISTORY_CUSTOM_SET_DOWNLOAD = 1107;

	public final int HISTORY_CUSTOM_SET_UPLOAD = 1108;
	// ------------------------------------历史记录-搜索记录KEY-----end----------------
	public final int HANDLER_OUT_TIME = -999;

	/**
	 * GIS图层是否显示基站名称
	 * 
	 * @return
	 */
	public String IS_TEXTOVER() {
		return "isTextOver";
	}

	/**
	 * 道路测试测试模式
	 * 
	 * @return
	 */
	public String ROAD_TEST_MODE() {
		return "RoadTestMode";
	}

	/**
	 * 道路测试切换事件
	 * 
	 * @return
	 */
	public String IS_SWITCH_HAND() {
		return "isSwitchHand";
	}

	/**
	 * 锁定北向
	 * 
	 * @return
	 */
	public String IS_LOCK_NORTH() {
		return "isLockNorth";
	}

	/**
	 * 道路测试速率测试模式
	 * 
	 * @return
	 */
	public String GIS_RATE_TEST_MODE() {
		return "gisRateTestMode";
	}

	/**
	 * 获取屏幕的宽度像素
	 * 
	 * @return
	 */
	public String WIDTH() {
		return WIDTH;
	}

	/**
	 * 获取手机屏幕的高度像素
	 * 
	 * @return
	 */
	public String HEIGHT() {
		return HEIGHT;
	}

	/**
	 * GIS地图图层对应的数据地址路径
	 * 
	 * @return
	 */
	public String CODE_PATH() {
		return CODE_PATH;
	}

	/**
	 * PCI地图图层对应的数据地址路径
	 * 
	 * @return
	 */
	public String CODE_PCI_PATH() {
		return CODE_PCI_PATH;
	}

	/**
	 * 分割符号。。。下倾角、方位角
	 * 
	 * @return
	 */
	public String SPLIT_JD() {
		return SPLIT_JD;
	}

	/**
	 * 首页是否退出了---目的退出被动测试
	 * 
	 * @return
	 */
	public String IS_MAIN_FINISH() {
		return IS_MAIN_FINISH;
	}

	/**
	 * 拍照像素值大小--高度
	 * 
	 * @return
	 */
	public String CAMERA_XS_HEIGHT() {
		return CAMERA_XS_HEIGHT;
	}

	/**
	 * 是否桌面启动
	 * 
	 * @return
	 */
	public String IS_LAUNCHER() {
		return IS_LAUNCHER;
	}

	/**
	 * 渠道类型：0网优宝，1易运维
	 * 
	 * @return
	 */
	public String CHANNEL_TYPE() {
		return CHANNEL_TYPE;
	}

	/**
	 * 拍照像素值大小
	 * 
	 * @return
	 */
	public String CAMERA_DX_SIZE() {
		return CAMERA_DX_SIZE;
	}

	/**
	 * 拍照浏览值大小
	 * 
	 * @return
	 */
	public String CAMERA_XS_SIZE() {
		return CAMERA_XS_SIZE;
	}

	/**
	 * 遍历测试页面数据广播
	 * 
	 * @return
	 */
	public String ACTION_TRAVERSE() {
		return "com.wyb.traverse.update";
	}

	/**
	 * 遍历测试页面数据广播
	 * 
	 * @return
	 */
	public String ACTION_CAMERA_ONE() {
		return "com.wyb.camera.update";
	}

	/**
	 * 工参巡检--签名返回广播
	 * 
	 * @return
	 */
	public String ACTION_GCWH_SIGN() {
		return ACTION_GCWH_SIGN;
	}

	/**
	 * 性能测试--签名返回广播
	 * 
	 * @return
	 */
	public String ACTION_XNCS_SIGN() {
		return ACTION_XNCS_SIGN;
	}

	/**
	 * 服务上传数据广播
	 * 
	 * @return
	 */
	public String ACTION_SERVICE_UPDATE() {
		return "com.wyb.update";
	}

	/**
	 * 室分单验数据更新同步广播
	 * 
	 * @return
	 */
	public String ACTION_SFDY_UPDATE() {
		return "com.wyb.sfdy.update";
	}

	/**
	 * 宏站单验数据更新同步广播
	 * 
	 * @return
	 */
	public String ACTION_HZDY_UPDATE() {
		return "com.wyb.hzdy.update";
	}

	/**
	 * 性能测试-- 数据更新广播
	 * 
	 * @return
	 */
	public String ACTION_XNCS_DATAUPDATE() {
		return ACTION_XNCS_DATAUPDATE;
	}

	/**
	 * 室内扫楼-- 数据更新广播
	 * 
	 * @return
	 */
	public String ACTION_SNSL_DATAUPDATE() {
		return ACTION_SNSL_DATAUPDATE;
	}

	/**
	 * 工参巡检拍照 --当前的id值
	 * 
	 * @return
	 */
	public String GCXJ_NOWCAMERA_RRUID() {
		return GCXJ_NOWCAMERA_RRUID;
	}

	/**
	 * 工参巡检拍照 --类型 2下倾角 1方位角
	 * 
	 * @return
	 */
	public String GCXJ_CAMERA() {
		return GCXJ_CAMERA;
	}

	/**
	 * 工参巡检拍照GCXJ_CAMERA对应值
	 * 
	 * @return
	 */
	public String GCXJ_VALUE() {
		return GCXJ_VALUE;
	}

	/**
	 * 信号检测---网络模式
	 * 
	 * @return
	 */
	public String TEST_TYPENTE() {
		return TEST_TYPENTE;
	}
	/**
	 * 信号检测---CGI
	 * 
	 * @return
	 */
	public String TEST_CGI() {
		return TEST_CGI;
	}

	/**
	 * 信号检测---RSRP
	 * 
	 * @return
	 */
	public String TEST_RSRP() {
		return TEST_RSRP;
	}

	/**
	 * 信号检测---SINR
	 * 
	 * @return
	 */
	public String TEST_SINR() {
		return TEST_SINR;
	}

	/**
	 * RSRP测试时间
	 * 
	 * @return
	 */
	public String TIME_RSRP() {
		return TIME_RSRP;
	}

	/**
	 * 是否刷新工参巡检列表（代维巡检）
	 * 
	 * @return
	 */
	public String IS_UPDATE_GCXJ() {
		return IS_UPDATE_GCXJ;
	}

	/**
	 * 屏幕状态 0开启 1关闭 2其他
	 * 
	 * @return
	 */
	public String OFF_STATE() {
		return OFF_STATE;
	}

	/**
	 * GIS正在测试信号
	 * 
	 * @return
	 */
	public String SIGN_GIS() {
		return SIGN_GIS;
	}

	/**
	 * 工程设计，是否刷新
	 * 
	 * @return
	 */
	public String GCSJ() {
		return GCSJ;
	}

	/**
	 * 上传锁---信号
	 * 
	 * @return
	 */
	public String OBJECT_LOCK() {
		return OBJECT_LOCK;
	}

	/**
	 * 定位经度
	 * 
	 * @return
	 */
	public String GPS_LON() {
		return GPS_LON;
	}

	/**
	 * 定位纬度
	 * 
	 * @return
	 */
	public String GPS_LAT() {
		return GPS_LAT;
	}

	/**
	 * 定位类型
	 * 
	 * @return
	 */
	public String GPS_TYPE() {
		return GPS_TYPE;
	}

	/**
	 * 唯一的设备UUID
	 * 
	 * @return
	 */
	public String PHONE_UUID() {
		return PHONE_UUID;
	}

	/**
	 * 唯一的设备ID
	 * 
	 * @return
	 */
	public String PHONE_IMEI() {
		return PHONE_IMEI;
	}

	/**
	 * 唯一的用户ID--手机
	 * 
	 * @return
	 */
	public String PHONE_IMSI() {
		return PHONE_IMSI;
	}

	/**
	 * 手机型号 如 三星 S3
	 * 
	 * @return
	 */
	public String PHONE_MODEL() {
		return PHONE_MODEL;
	}

	/**
	 * 手机版本号 如4。1.1
	 * 
	 * @return
	 */
	public String PHONE_RELEASE() {
		return PHONE_RELEASE;
	}

	/**
	 * 上传设置WIFI状态 true只允许wifi
	 * 
	 * @return
	 */
	public String SET_UPLOAD_WIFI() {
		return SET_UPLOAD_WIFI;
	}

	/**
	 * 上传设置时间段
	 * 
	 * @return
	 */
	public String SET_UPLOAD_TIME() {
		return SET_UPLOAD_TIME;
	}

	/**
	 * 上传时间---起始 12:00
	 * 
	 * @return
	 */
	public String SET_UPLOAD_TIME_START() {
		return SET_UPLOAD_TIME_START;
	}

	/**
	 * 上传时间---结束 11:11
	 * 
	 * @return
	 */
	public String SET_UPLOAD_TIME_END() {
		return SET_UPLOAD_TIME_END;
	}

	/**
	 * 当前用户手机号码
	 * 
	 * @return
	 */
	public String USER_PHONE() {
		return USER_PHONE;
	}

	/**
	 * 当前登录帐号
	 * 
	 * @return
	 */
	public String LOGIN_NAME() {
		return LOGIN_NAME;
	}

	/**
	 * 当前登录用户名--名字
	 * 
	 * @return
	 */
	public String LOGIN_MZ() {
		return LOGIN_MZ;
	}

	/**
	 * 当前登录密码
	 * 
	 * @return
	 */
	public String LOGIN_PASSWD() {
		return LOGIN_PASSWD;
	}

	/**
	 * 当前登录地市权限depart
	 * 
	 * @return
	 */
	public String LOGIN_DEPART() {
		return USER_DEPART;
	}

	/**
	 * 当前登录用户ID
	 * 
	 * @return
	 */
	public String LOGIN_ID() {
		return LOGIN_ID;
	}

	/**
	 * 当前登录用户的菜单权限
	 * 
	 * @return
	 */
	public String LOGIN_MENU() {
		return LOGIN_MENU;
	}

	/**
	 * 当前登录是否是子票据验证登录
	 * 
	 * @return LOGIN_TOKEN
	 */
	public String LOGIN_TOKEN() {
		return LOGIN_TOKEN;
	}

	/**
	 * 性能测试速率下载
	 * 
	 * @return LOGIN_TOKEN
	 */
	public String XNCS_SL_DOWN() {
		return XNCS_SL_DOWN;
	}

	/**
	 * 性能测试速率上传
	 * 
	 * @return LOGIN_TOKEN
	 */
	public String XNCS_SL_UPLOAD() {
		return XNCS_SL_UPLOAD;
	}

	/**
	 * 定点测试、扫楼图纸打点
	 * 
	 * @return XNCS_SL_DRAW
	 */
	public String XNCS_SL_DRAW() {
		return XNCS_SL_DRAW;
	}

	public String HZDY_SL_DOWN() {
		return HZDY_SL_DOWN;
	}

	public String HZDY_SL_UPLOAD() {
		return HZDY_SL_UPLOAD;
	}

	public String HZDY_ENB() {
		return HZDY_ENB;
	}

	public String HZDY_IS_SPECIAL() {
		return HZDY_IS_SPECIAL;
	}

	public String getPCI_PINDIAN() {
		return PCI_PINDIAN;
	}

	public String getEMERGENCY_DEVICE_TYPE() {
		return EMERGENCY_DEVICE_TYPE;
	}

	public String getPCI_SHOW_BRACER_HINT() {
		return PCI_SHOW_BRACER_HINT;
	}

	public String ACTION_PARAMETER_DATAUPDATE() {
		return ACTION_PARAMETER_DATAUPDATE;
	}
	public String ACTION_PARAMETER_CONFIG_DATAUPDATE() {
		return ACTION_PARAMETER_CONFIG_DATAUPDATE;
	}

	public String ACTION_PARAMETER_SIGN(){
		return ACTION_PARAMETER_SIGN;
	}

	public String getACTION_PARAMETER_MAP_DATAUPDATE() {
		return ACTION_PARAMETER_MAP_DATAUPDATE;
	}

	public String APP_CONF_DICT_TIME() {
		return APP_CONF_DICT_TIME;
	}

	public String APP_CONF_FTP_TIME() {
		return APP_CONF_FTP_TIME;
	}




}
