package cn.nokia.speedtest5g.app.enums;

public enum EnumRequest {
	/**
	 * 登陆返回
	 */
	NET_LOGIN(501),

	/**
	 * 登陆验证码获取
	 */
	NET_LOGIN_CODE(502),

	/**
	 * 宏站室分搜索
	 */
	NET_RW_LIST(503),

	/**
	 * 短信登录验证
	 */
	NET_LOGIN_CODE_CONFIRMATION(504),

	/**
	 * 验证子票据
	 */
	NET_SUBTOKEN(505),

	/**
	 * 主动采集上传
	 */
	NET_GIS_SIGNAL(506),
	/**
	 * 意见反馈
	 */
	NET_FEED_BACK(507),
	/**
	 * 版本升级
	 */
	NET_VERSION(508),
	/**
	 * 消息提醒数据请求
	 */
	NET_TOAST_LIST(509),
	/**
	 * 消息提醒详情数据请求
	 */
	NET_TOAST_LIST_DETAILE(510),
	/**
	 * 集中入网自预审
	 */
	NET_RW_PLAN(511),
	/**
	 * 故障定位列表
	 */
	NET_FAULT(512),
	/**
	 * 故障定位详情
	 */
	NET_FAULT_DETAIL(513),
	/**
	 * 故障定位详情-天线编号
	 */
	NET_FAULT_TXBH(514),
	/**
	 * 室分电子化详情-仿真信息
	 */
	NET_FAULT_FZXX(515),
	/**
	 * 室分电子化详情-故障定位图
	 */
	NET_FAULT_GZDWT(516),
	/**
	 * 室分电子化详情-平面图
	 */
	NET_FAULT_PMT(517),
	/**
	 * 室内扫楼楼宇查询，
	 */
	NET_SNSL_SEARCH_NAME(518),
	/**
	 * 室内扫楼需求单查询，
	 */
	NET_SNSL_SEARCH_WORKNO(519),
	/**
	 * 室分单验列表数据
	 */
	NET_SFDY_LIST(520),
	/**
	 * 室分单验异常原因列表
	 */
	NET_SFDY_ABNORMAL_LIST(521),
	/**
	 * 室分电子化详情-其他图片
	 */
	NET_FAULT_PHOTO(522),
	/**
	 * 室分电子化详情-全部图片
	 */
	NET_FAULT_PHOTO_ALL(523),
	/**
	 * 查询对应CI值
	 */
	NET_QUERY_CI(524),
	/**
	 * 自动偏置列表数据请求
	 */
	NET_ZDPZ_LIST(525),
	/**
	 * 工参巡检/调整统计数据
	 */
	NET_GCXJ_COUNT(526),
	/**
	 * 工单派发查询CI
	 */
	NET_WOOUT_CI(527),
	/**
	 * 用户信息查询返回--根据用户ID
	 */
	NET_LOGIN_USER(528),
	/**
	 * 天线巡警等高图获取
	 */
	NET_TXXJ_BIT(529),
	/**
	 * 天线巡警问题小区工单派发
	 */
	NET_TXXJ_DISTRIBUTE(530),
	/**
	 * 天线巡警-工单查询
	 */
	NET_TXXJ_WORK_QUERY(531),
	/**
	 * 网优自动化-我的工单查询
	 */
	NET_WYZDH_MYWORK_QUERY(532),
	/**
	 * 网优自动化-PCI查询-列表
	 */
	NET_WYZDH_PCI_LIST_QUERY(533),
	/**
	 * 网优自动化-PCI查询-详情
	 */
	NET_WYZDH_PCI_DETAILS(534),
	/**
	 * 网优自动化-PCI修改提交(工参修改)
	 */
	NET_WYZDH_PCI_SUBMIT(535),
	/**
	 * 网优自动化-地市接口
	 */
	NET_WYZDH_CITY(536),
	/**
	 * 网优自动化-区县接口
	 */
	NET_WYZDH_COUNTY(537),
	/**
	 * CI/地址模糊搜索
	 */
	NET_CI_ADDRESS_FUZZY_SEARCH(539),
	/**
	 * 一键诊断：按地址查询
	 */
	NET_YJZD_SEARCH_BY_WORK_ORDER(540),
	/**
	 * 一键诊断：按地址查询
	 */
	NET_YJZD_SEARCH_BY_ADDRESS(541),
	/**
	 * 一键诊断：按地址查询
	 */
	NET_YJZD_SEARCH_BY_LON_LAT(542),
	/**
	 * 一键诊断：按地址查询
	 */
	NET_YJZD_SEARCH_BY_CI(543),
	/**
	 * 一键诊断：诊断报告详情
	 */
	NET_YJZD_REPORT_DETAIL(544),
	/**
	 * 网友计费系统-工单基础数据上传
	 */
	NET_JIFEI_DATAUPLOAD(545),

	/**
	 * 网优计费系统-基站数获取
	 */
	NET_JIFEI_SITE_TOTAL(546),
	/**
	 * 网优计费系统-工单状态查询
	 */
	NET_JIFEI_STATUS(547),
	/**
	 * OTT指纹库栅格数据查询
	 */
	NET_MRO_ZW(548),
	/**
	 * OTT指纹库栅格日期查询
	 */
	NET_OTT_GRID_DATE(549),
	/**
	 * OTT指纹库小区日期查询
	 */
	NET_OTT_CELL_DATE(550),
	/**
	 * OTT指纹库小区采样点数据查询
	 */
	NET_OTT_CELL_DATA_LIST(551),
	/**
	 * OTT指纹库小区采样点详情数据查询
	 */
	NET_OTT_CELL_DATA_DETAIL(552),
	/**
	 * 退服告警数据请求
	 */
	NET_STATION_ALARM_LIST(553),
	/**
	 * 退服告警地市区县数量统计数据请求
	 */
	NET_STATION_ALARM_CITY(554),
	/**
	 * 退服告警根据地市区县查询基站数据请求
	 */
	NET_STATION_LIST_SITE(555),
	/**
	 * 获取短信验证码登入，获取网优宝账号绑定的手机号
	 */
	NET_GET_SMS_VERIFICATION_CODE(556),
	/**
	 * 短信验证码登入
	 */
	NET_LOGIN_BY_SMS_CODE(557),
	/**
	 * 网优计费系统-地铁-地市
	 */
	NET_JIFEI_DT_CITY(558),
	/**
	 * 网优计费系统-地铁-线路
	 */
	NET_JIFEI_DT_LINE(559),
	/**
	 * 网优计费系统-地铁-出发站
	 */
	NET_JIFEI_DT_STARTING(560),
	/**
	 * 网优计费系统-地铁-目的站
	 */
	NET_JIFEI_DT_DESTINATION(561),
	/**
	 * 网优计费系统-地铁-区间
	 */
	NET_JIFEI_DT_INTERVAL(562),
	/**
	 * 网优计费系统-地铁-小区
	 */
	NET_JIFEI_DT_CELL(563),
	/**
	 * 仪表管理-仪表种类等查询
	 */
	NET_YBGL_TYPE(572),
	/**
	 * 仪表管理-历史记录查询
	 */
	NET_YBGL_HISTORY(573),
	/**
	 * 仪表管理-用户信息查询
	 */
	NET_YBGL_USER(574),
		/**
	 * 小区指标-帮助信息查询返回
	 */
	NET_XQZB_HELP(575),
	/**
	 * 小区指标-数据查询返回
	 */
	NET_XQZB_QUERY(576),
	/**
	 * 基站导航-搜索
	 */
	NET_JZDH_SEARCH(577),
	/**
	 * 基站导航-线路查询
	 */
	NET_JZDH_LINE(578),
	/**
	 * 基站导航-脚印查询
	 */
	NET_JZDH_JY(579),
	/**
	 * 公告-获取未读信息
	 */
	NET_NOTIFY_GET_UNREAD_MSG(582),
	/**
	 * 公告-获取列表公告列表
	 */
	NET_NOTIFY_GET_LIST(583),
	/**
	 * 公告-获取公告设置
	 */
	NET_NOTIFY_GET_SETTINGS(584),
	/**
	 * 公告-修改公告设置
	 */
	NET_NOTIFY_MODIFY_SETTINGS(585),
	/**
	 * 公告-设置为已读
	 */
	NET_NOTIFY_SET_READ(586),
	/**
	 * 公告-删除
	 */
	NET_NOTIFY_DELETE(587),
	/**
	 * 公告-搜索
	 */
	NET_NOTIFY_SEARCH(589),
	/**
	 * 简单硬扩-搜索
	 */
	NET_JDYK_SEARCH(590),
	/**
	 * 简单硬扩-GIS
	 */
	NET_JDYK_GIS(591),
	/**
	 * 简单硬扩-详情
	 */
	NET_JDYK_DETAILS(592),
	/**
	 * 简单硬扩-统计图表查询
	 */
	NET_JDYK_CHART(593),
	/**
	 * 简单硬扩-接单、撤单
	 */
	NET_JDYK_TO_STATUS(594),
	/**
	 * 简单硬扩-扩展板型号等
	 */
	NET_JDYK_KZBXH(595),
	/**
	 * 参数修改提交
	 */
	NET_JDYK_UPDATE(596),
	/**
	 * 自动扩容提交
	 */
	NET_JDYK_ZDKR(597),
	/**
	 * 价值小区-GIS
	 */
	NET_JZXQ_GIS(599),
	/**
	 * 简单硬扩-后台联系人
	 */
	NET_JDYK_HTLXR(5001),
	/**
	 * 简单硬扩-撤销(参数修改)
	 */
	NET_JDYK_CSXG_CX(5002),
	/**
	 * 简单硬扩-重试
	 */
	NET_JDYK_RETRY(5003),
	/**
	 * 简单硬扩-总览
	 */
	NET_JDYK_OVERVIEW(5004),
	/**
	 * 定时获取同步数据
	 */
	NET_TIME_SYNCHRONIZED(5005),
	/**
	 * 自动开站-搜索
	 */
	NET_ZDKZ_SEARCH(5006),
	/**
	 * 自动开站-GIS
	 */
	NET_ZDKZ_GIS(5007),
	/**
	 * 自动开站-详情
	 */
	NET_ZDKZ_DETAILS(5008),
	/**
	 * 自动开站-统计图表查询
	 */
	NET_ZDKZ_CHART(5009),
	/**
	 * 自动开站-接单、撤单
	 */
	NET_ZDKZ_TO_STATUS(5010),
	/**
	 * 自动开站-BBU型号
	 */
	NET_ZDKZ_SELECT_BBU(5011),
	/**
	 * 自动开站-BBU型号-拍照
	 */
	NET_ZDKZ_SELECT_BBU_CAMERA(5012),
	/**
	 * 自动开站-RRU型号
	 */
	NET_ZDKZ_SELECT_RRU(5013),
	/**
	 * 自动开站-RRU型号-拍照
	 */
	NET_ZDKZ_SELECT_RRU_CAMERA(5014),
	/**
	 * 自动开站-主控板型号
	 */
	NET_ZDKZ_SELECT_ZKB(5015),
	/**
	 * 自动开站-主控板型号-拍照
	 */
	NET_ZDKZ_SELECT_ZKB_CAMERA(5016),
	/**
	 * 自动开站-基带板型号
	 */
	NET_ZDKZ_SELECT_JDB(5017),
	/**
	 * 自动开站-基带板型号-拍照
	 */
	NET_ZDKZ_SELECT_JDB_CAMERA(5018),
	/**
	 * 自动开站-扩展板
	 */
	NET_ZDKZ_SELECT_KZB(5019),
	/**
	 * 自动开站-扩展板-拍照
	 */
	NET_ZDKZ_SELECT_KZB_CAMERA(5020),
	/**
	 * 自动开站-后台联系人
	 */
	NET_ZDKZ_SELECT_HTLXR(5021),
	/**
	 * 自动开站-系统失败原因
	 */
	NET_ZDKZ_SELECT_SYSTEM_ERR(5022),
	/**
	 * 自动开站-提交人工处理数据
	 */
	NET_ZDKZ_TO_RGCL(5023),
	/**
	 * 自动开站-保存开站人员信息
	 */
	NET_ZDKZ_SAVE_KZUSER(5024),
	/**
	 * 自动开站-设备共模类型
	 */
	NET_ZDKZ_SELECT_SBGMLX(5025),
	/**
	 * 自动开站-工作制式
	 */
	NET_ZDKZ_SELECT_GZZS(5026),
	/**
	 * 自动开站-FS板型号
	 */
	NET_ZDKZ_SELECT_FSBXH(5027),
	/**
	 * 自动开站-FS槽位号
	 */
	NET_ZDKZ_SELECT_FSCWH(5028),
	/**
	 * 自动开站-RRU编号(中兴)、RRU链路号（诺西）
	 */
	NET_ZDKZ_SELECT_RRUBH(5029),
	/**
	 * 自动开站-RRU在链中插入的位置
	 */
	NET_ZDKZ_SELECT_RRU_POSITION(5030),
	/**
	 * 自动开站-（华为RRU连接BBU测光口号、中兴4G光纤连接BBU侧光口号、中兴5GAAU连接BBU侧光口号）
	 */
	NET_ZDKZ_SELECT_RRU_TO_BBU(5031),
	/**
	 * 自动开站-主控板槽位号
	 */
	NET_ZDKZ_SELECT_ZKBCWH(5032),
	/**
	 * 自动开站-基带板槽位号
	 */
	NET_ZDKZ_SELECT_JDBCWH(5033),
	/**
	 * 自动开站5G-机框EID-拍照
	 */
	NET_ZDKZ_NR_SELECT_EID_CAMERA(5034),
	/**
	 * 自动开站-新增基带板型号
	 */
	NET_ZDKZ_SELECT_JDB_NEW(5035),
	/**
	 * 自动开站-新增基带板槽位号
	 */
	NET_ZDKZ_SELECT_JDBCWH_NEW(5036),
	/**
	 * 自动开站-（华为光纤连接基带板槽位号、中兴光纤连接基带板槽位号）
	 */
	NET_ZDKZ_SELECT_GXLJJDBCWH(5037),
	/**
	 * 获取模块数据
	 */
	NET_APP_MODULAR(5039),
	/**
	 * 获取自动开站-开站数据
	 */
	NET_ZDKZ_KZSJ(5040),
	/**
	 * 自动开站5G-基带板型号-拍照
	 */
	NET_ZDKZ5G_SELECT_JDB_CAMERA(5041),
	/**
	 * 自动开站5G-ESN-拍照
	 */
	NET_ZDKZ5G_SELECT_ESN_CAMERA(5042),
	/**
	 * GIS-高铁
	 */
	NET_GIS_GAO_TIE(5043),
	/**
	 * 5G小区激活列表请求
	 */
	NET_NR_CELL_ACTIVATION_LIST(5044),
	/**
	 * 5G小区任务状态查看
	 */
	NET_NR_CELL_TASK_STATUS(5045),
	/**
	 * 5G小区状态查看
	 */
	NET_NR_CELL_STATUS_QUERY(5046),
	/**
	 * 5G小区激活
	 */
	NET_NR_ACTIVATION(5047),
	/**
	 * 5G小区去激活
	 */
	NET_NR_GO_ACTIVATION(5048),
	/**
	 * 自动开站-4G基带板型号
	 */
	NET_ZDKZ_SELECT_JDB_MODEL_LTE(5049),
	/**
	 * 自动开站-4G基带板槽位号
	 */
	NET_ZDKZ_SELECT_JDB_CWH_LTE(5050),
	/**
	 * 自动开站-5G基带板2型号
	 */
	NET_ZDKZ_SELECT_JDB_MODEL_NR2(5051),
	/**
	 * 自动开站-5G基带板2槽位号
	 */
	NET_ZDKZ_SELECT_JDB_CWH_NR2(5052),
	/**
	 * 自动开站-4G基带板2型号
	 */
	NET_ZDKZ_SELECT_JDB_MODEL_LTE2(5053),
	/**
	 * 自动开站-4G基带板2槽位号
	 */
	NET_ZDKZ_SELECT_JDB_CWH_LTE2(5054),
	/**
	 * 集中入网5G-AAU序列号识别
	 */
	NET_JZRW_NR_AAU_XLH(5055),
	/**
	 * 自动开站-BBU池
	 */
	NET_ZDKZ_SELECT_BBUC(5056),
    // ---------------------------------
	/**
	 * 颜色修改返回
	 */
	OTHER_CLOLR(600),
	/**
	 * popup对话框关闭
	 */
	OTHER_POPUP_CLOSE(601),
	/**
	 * 室内扫楼 复测按钮点击
	 */
	OTHER_CLICL_FC(602),
	/**
	 * 退服告警地市区县查询基站点击返回
	 */
	REQUESTCODE_TFGJ_CITYSITELIST(603),
	/**
	 * 文件下载回调
	 */
	OTHER_DOWN_FILE_WHAT(604),
	/**
	 * 基站信号曲线图点击回调RSRP
	 */
	OTHER_CHART_RSRP_RETURN(605),
	/**
	 * 基站信号曲线图点击回调SINR
	 */
	OTHER_CHART_SINR_RETURN(606),
	/**
	 * GIS当前服务小区图层返回
	 */
	OTHER_GIS_NOWCELL(607),
	/**
	 * 简单硬扩-OCR识别返回
	 */
	OTHER_JDYK_ORC(608),
	/**
	 * LTE-BAND选择刷新
	 */
	OTHER_LTE_BAND_UPDATE(609),
	/**
	 * 价值小区图层点击过滤查询
	 */
	OTHER_JZXQ_CLICK_POLYGON(610),
	/**
	 * 百度地点关键字搜索
	 */
	OTHER_DDGJZ_SEARCH(611),
	/**
	 * 获取最新数据
	 */
	OTHER_READ_NOWDATA(612),
	/**
	 * 获取当前游标变动
	 */
	OTHER_SELECT_POSITION(613),
	/**
	 * 获取当前游标变动LTE RRU图层
	 */
	OTHER_SELECT_POSITION_GIS_LTE_RRU_SITE(6130),
	/**
	 * 获取当前游标变动邻区关系图层
	 */
	OTHER_SELECT_POSITION_GIS_LINQU(6131),
	/**
	 * 经纬度
	 */
	OTHER_LATLNG_IN(614),
	/**
	 * 获取数据
	 */
	OTHER_READ_DATA(615),
	/**
	 * 空间退服告警
	 */
	OTHER_KJTFGJ(616),
	/**
	 * wifi强度选择返回
	 */
	OTHER_CHART_Wifi_RETURN(617),
	//-----------------------------------
	/**
	 * 摄像头图片保存
	 */
	TASK_SAVE_FILE(700),
	/**
	 * 摄像头图片显示完成
	 */
	TASK_BIT_COM(701),
	/**
	 * 原始图片与水印图合并保存到指定的目录
	 */
	TASK_SAVE_BITMAP(702),
	/**
	 * 下载apk文件
	 */
	TASK_DOWN_APK(703),
	/**
	 * 关闭加载对话框
	 */
	DIALOG_NET_DISMISS(704),
	/**
	 * 菜单返回
	 */
	MENU_BACK(705),
	/**
	 * 菜单选择1
	 */
	MENU_SELECT_ONE(706),
	/**
	 * 菜单选择2
	 */
	MENU_SELECT_TWO(707),
	/**
	 * 菜单选择3
	 */
	MENU_SELECT_THREE(7070),
	/**
	 * 菜单选择4
	 */
	MENU_SELECT_FOUR(7071),
	/**
	 * 根据经纬度或城市搜索附近小区
	 */
	OTHER_POI_SEARCH(708),
	/**
	 * CAT测试
	 */
	OTHER_CQT_TEST(709),
	/**
	 * 根据经纬度获取地理位置
	 */
	OTHER_GEO_SEARCH(710),
	/**
	 * 更新下载确定
	 */
	DIALOG_DOWN_OK(711),
	/**
	 * 更新下载确定
	 */
	DIALOG_DOWN_OK_ON(7111),
	/**
	 * PING回调
	 */
	TASK_PING(712),
	/**
	 * 自动偏置初始化
	 */
	TASK_ZDPZ_INIT(713),
	/**
	 * 自动配置保存数据
	 */
	TASK_ZDPZ_SAVE(714),
	/**
	 * 自动偏置图片上传识别返回
	 */
	TASK_ZDPZ_UPLOAD_BIT(715),
	/**
	 * 提示对话框选择---对话框1
	 */
	DIALOG_TOAST_BTN_ONE(716),
	/**
	 * 提示对话框选择---对话框2
	 */
	DIALOG_TOAST_BTN_TWO(717),
	/**
	 * 提示对话框选择---对话框3
	 */
	DIALOG_TOAST_BTN_THREE(718),
	/**
	 * 提示对话框选择---对话框4
	 */
	DIALOG_TOAST_BTN_FOUR(7190),
	/**
	 * 上传初始化返回handler
	 */
	UPLOAD_INIT(719),
	/**
	 * RSRP/SINR 列表
	 */
	RSRP_SINR_LIST(720),
	/**
	 * 室内扫楼名称
	 */
	SNSL_NAME(721),
	/**
	 * 遍历测试初始化颜色值
	 */
	TASK_TRAVERSE_COLORINIT(726),
	/**
	 * 遍历测试超限
	 */
	OTHER_UPDATE_COLLECT_LIMIT(727),
	/**
	 * 遍历测试采样点采集更新
	 */
	OTHER_UPDATE_UI_NODE(728),
	/**
	 * 开始采集后时间更新
	 */
	OTHER_UPDATE_TIME_RECORD(729),
	/**
	 * 撤销点
	 */
	OTHER_UPDATE_UI_COLLECT(730),
	/**
	 * 类型切换--遍历测试
	 */
	OTHER_UPDATE_UI_CHECK(731),
	/**
	 * 遍历测试记录回放读取--自动
	 */
	TASK_READ_TRAVERSE_AUTO(732),
	/**
	 * 遍历测试记录回放读取--手动
	 */
	TASK_READ_TRAVERSE_MANUAL(733),
	/**
	 * 遍历测试记录回放读取（跳转结果）--自动
	 */
	TASK_READ_TRAVERSE_TO_AUTO(734),
	/**
	 * 遍历测试记录回放读取（跳转结果）--手动
	 */
	TASK_READ_TRAVERSE_TO_MANUAL(735),
	/**
	 * 记录回放时间
	 */
	OTHER_UPDATE_TIME_RECORD_RATE(736),
	/**
	 * 消息提示
	 */
	OTHER_TOAST_MSG(737),
	/**
	 * 下载文件完成
	 */
	TASK_DOWN_FILE_COMPLETE(738),
	/**
	 * 下载进度
	 */

	TASK_DOWN_PROGRESS(739),
	/**
	 * 查询UI top栈顶
	 */
	OTHER_ACTIVITY_TOP_QUERY(740),
	/**
	 * 点击跳转activity
	 */
	CLICK_GO_ACTIVITY(741),
	/**
	 * 天线巡警适配器点击返回切换
	 */
	CLICK_XJ_ADAPTER(744),
	/**
	 * 网优自动化-工单数据更新
	 */
	TASK_WYZDH_WORK_UPDATE(745),
	/**
	 * 网优自动化-进入详情页
	 */
	TASK_WYZDH_GO_DETAILS(746),
	/**
	 * 网优自动化-接单对话框事件回调
	 */
	DIALOG_WYZDH_ORDERS(747),
	/**
	 * 网优自动化-详情测试第一级（属实与非属性选择）初始化
	 */
	TASK_WYZDH_WORKTEST_ONE_INIT(748),
	/**
	 * 网优自动化-详情表更新
	 */
	TASK_WYZDH_WORKDETAILS_UPDATE(749),
	/**
	 * 网优自动化-工单类型选择回调
	 */
	CLICK_WYZDH_WORK_TYPE_SELECT(750),
	/**
	 * 网优自动化-根据工单查询PCI
	 */
	TASK_WYZDH_PCI_THREAD(751),
	/**
	 * 网优自动化-提交确认对话框事件回调
	 */
	DIALOG_WYZDH_SUBMIT(752),
	/**
	 * 网优自动化-返回LTE覆盖率
	 */
	DIALOG_WYZDH_LTE_COVERAGE(753),
	/**
	 * 获取pci频点
	 */
	NET_PCI_PINDIAN(754),
	/**
	 * 单验列表数据状态更新
	 */
	OTHER_LIST_STATUS(755),
	/**
	 * 判断DB文件是否存在
	 */
	TASK_ISDB_INIT(756),
	/**
	 * 设置加载对话框内容
	 */
	DIALOG_MSG(757),
	/**
	 * 根据权限读取PCI-map集合数据
	 */
	TASK_READ_PCI_MAP(758),
	/**
	 * 首页
	 */
	OTHER_MAINHOME_TYPE_HOME(759),
	/**
	 * 主页作业流程
	 */
	OTHER_MAINHOME_TYPE_WORKFLOW(7600),
	/**
	 * 主页优化工具
	 */
	OTHER_MAINHOME_TYPE_OPTIMIZER(7601),
	/**
	 * 主页-公告视图刷新请求码
	 */
	OTHER_MAINHOME_UPDATE_NOTIFY_VIEW(7611),
	/**
	 * 主页-视图刷新请求码
	 */
	OTHER_MAINHOME_UPDATE_VIEW(7612),
	/**
	 * 首页-刷新数据
	 */
	OTHER_MAINHOME_UPDATE_HOME_INIT_DATA(7613),
	/**
	 * 提示当前是游客身份
	 */
	OTHER_MAINHOME_TYPE_USER_TOURIST(762),
	/**
	 * 提示首页个人中心红点 
	 * 
	 * updateDay 上次与今日时间差
	 */
	OTHER_MAINHOME_TYPE_PERSONAL(7620),
	/**
	 * 清空缓存
	 */
	TASK_MAINHOME_CLEARCACHE(763),
	/**
	 * 跳转到登录页面
	 */
	OTHER_MAINHOME_TO_LOGIN(764),
	/**
	 * Listview触摸回调
	 */
	OTHER_TOUCH_BACK(765),
	/**
	 * 跳转到方案初勘
	 */
	OTAHER_TO_FACK(766),
	/**
	 * 常用模块删除后回调
	 */
	OTHER_DETELE_BACK(767),
	/**
	 * 删除
	 */
	OTHER_DELETE(768),
	/**
	 * 详情
	 */
	OTHER_DETAILS(769),
	/**
	 * 初始化
	 */
	OTHER_INIT(770),
	/**
	 * 添加
	 */
	OTHER_ADD(771),
	/**
	 * 查询CI列表
	 */
	OTHER_QUERY_CI(772),
	/***
	 * 更新
	 */
	OTHER_UPDATE(773),
	/**
	 * 天线巡警基站选中返回切换
	 */
	CLICK_XJ_SITE(774),
	/***
	 * 邻区获取返回
	 */
	OTHER_LINQU(775),
	/**
	 * 语音识别结束
	 */
	OTHER_RECOG_BACK(776),
	/**
	 * 语音识别音量大小回调
	 */
	OTHER_RECOG_ING(777),
	/**
	 * 语音识别监听状态回调 0检测到说话 1说话结束
	 */
	OTHER_RECOG_STATUS(7777),
	/***
	 * RRU列表获取返回
	 */
	NET_RRU_LIST(778),
	/***
	 * RRU详情获取返回
	 */
	NET_RRU_DETAIL(779),
	/**
	 * 参数修改--修改提交
	 */
	NET_PARAMETER_ALERT_SUBMIT(780),
	/**
	 * 参数修改--参数配置
	 */
	NET_PARAMETER_CONF(781),
	/**
	 * 参数修改--参数小区配置
	 */
	NET_PARAMETER_VALUE(782),
	/**
	 * 参数修改-提交确认对话框事件回调
	 */
	DIALOG_PARAMETER_SUBMIT(783),
	/**
	 * 参数修改-历史
	 */
	NET_PARAMETER_HISTORY(784),
	/**
	 * 参数修改-结果
	 */
	NET_PARAMETER_RESULT(785),
	/**
	 * 计时器
	 */
	OTHER_TIMER(786),
	/**
	 * 网优计费系统工单完整性验证
	 */
	OTHER_JIFEI_DATA_OVE(787),
	/**
	 * MRO指纹库图层点击事件回调
	 */
	CLICK_MRO_LISTENER(789),
	/**
	 * MRO指纹库小区间切换
	 */
	ADAPTER_MRO_SELECT_CELL(790),
	/**
	 * 基站告警当日统计数据请求
	 */
	STATION_ALARM_STATISTICS_TIME(791),
	/**
	 * 初始化IMSI
	 */
	OTHER_INIT_IMSI(792),
	/**
	 * 初始化IMSI
	 */
	OTHER_PICTURE_EDIT_CQT_TEST(793),
	/**
	 *上传FTP测试log
	 */
	NET_SAVE_FTP_SPEED_TEST_LOG(794),
	/**
	 * 上传APP告警log
	 */
	NET_SAVE_APP_WARNING_LOG(795),
	/**
	 * App配置信息
	 */
	NET_GET_APP_CONFIG(796),
	/**
	 * 速率测试历史
	 */
	NET_GET_SPEED_TEST_LOG(797),
	/**
	 * 速率测试个性化配置--下载
	 */
	OTHER_FTP_CUSTOM_DOWNLOAD(798),
	/**
	 * 速速率测试个性化配置--上传
	 */
	OTHER_FTP_CUSTOM_UPLOAD(799),
	/**
	 * 获取应急站列表
	 */
	NET_EMERGENT_STAION_LIST(800),
	/**
	 * 应急站设置
	 */
	NET_EMERGENT_STAION_SET(801),
	/**
	 * 应急站 激活
	 */
	OTHER_EMERGENT_CELL_ACTIVATION(802),
	/**
	 * 应急站地市数量
	 */
	NET_EMERGENT_STAION_SITE_COUNT(803),
	/**
	 * 应急站 开关锁
	 */
	OTHER_EMERGENT_CELL_SWITCH_LOCK(804),
	/**
	 * 应急站 邻区
	 */
	OTHER_EMERGENT_CELL_ADJACENT_REGION(805),
	/**
	 * 应急站 拍照
	 */
	OTHER_EMERGENT_CELL_TAKE_PHOTO(806),
	/**
	 * 应急站 测试
	 */
	OTHER_EMERGENT_CELL_TEST(807),
	/**
	 * 应急站 指标
	 */
	OTHER_EMERGENT_CELL_QUOTA(808),
	/**
	 * 应急站 邻区列表
	 */
	NET_EMERGENT_CELL_ADJACENT_REGION(809),
	/**
	 * 应急站 邻区切换
	 */
	OTHER_EMERGENT_CELL_ADJACENT_REGION_SWITCH(810),
	/**
	 * 应急站 点击小区名称
	 */
	OTHER_EMERGENT_CELL_ADJACENT_REGION_CELL_NAME(811),
	/**
	 * 应急站 指标列表
	 */
	NET_EMERGENT_CELL_QUOTA_LIST(812),
	/**
	 * 应急站 告警列表
	 */
	NET_EMERGENT_CELL_ALARM_LIST(813),
	/**
	 * 应急站 照片
	 */
	NET_EMERGENT_CELL_PICTURE_LIST(814),
	/**
	 * 应急站 删除照片
	 */
	OTHER_EMERGENT_DELETE_PICTURE(815),
	/**
	 * 应急站 单小区小时指标
	 */
	NET_EMERGENT_CELL_QUOTA_CI_PARAM(816),
	/**
	 * 天馈自优化 上传工单
	 */
	NET_TKZYH_UPLOAD_DATA(817),
	/**
	 * 天馈自优化 历史计算列表
	 */
	NET_TKZYH_HISTORY_DATA(818),
	/**
	 * 天馈自优化 工单详情、集中入网5G详情
	 */
	NET_TKZYH_WORK_DETAILS(819),
	/**
	 * 远程电调数据获取
	 */
	NET_APP_ELECTRIC_INIT(820),
	/**
	 * 远程电调详情数据获取
	 */
	NET_APP_ELECTRIC_DETAIL(821),
	/**
	 * 远程电调授权人获取
	 */
	NET_APP_ELECTRIC_GET_AUTHORI(822),
	/**
	 * 远程电调历史调整记录
	 */
	NET_APP_ELECTRIC_HISTORY(823),
	/**
	 * 远程电调获取验证码
	 */
	NET_APP_ELECTRIC_GET_CODE(824),
	/**
	 * 发送验证码
	 */
	NET_APP_ELECTRIC_MSG(825),
	/**
	 * 我的电调
	 */
	NET_APP_ELECTRIC_ORDER_MODULE(826),
	/**
	 * 电调详情查询
	 */
	NET_APP_ELECTRIC_ORDER_DETAIL(827),
	/**
	 * 电调查询
	 */
	NET_APP_ELECTRIC_SEARCH(828),
	/**
	 * 执行状态查询
	 */
	NET_GET_ELECTRIC_STATUS(829),
	/**
	 * 电调小区聚合查询
	 */
	NET_APP_ELECTRIC_COUNT(830),
	/**
	 * 统计查询
	 */
	NET_GET_APPELECTRIC_STATISTICS(831),
	/**
	 * 应急站 获取应急站操作审核人员列表
	 */
	NET_EMERGENT_AUDIT_USERS(832),
	/**
	 * 应急站 获取应急站操作审核短信
	 */
	NET_EMERGENT_AUDIT_SMS(833),
	/**
	 * 应急站 获取应急站操作审核短信动作 用于事件判断
	 */
	OTHER_EMERGENT_GET_SMS(834),
	/**
	 * 应急站 基站信息
	 */
	OTHER_EMERGENT_SITE_INFO(835),
	/**
	 * 输入历史点击回调
	 */
	OTHER_INPUT_HISTORY_BACK(836),
	/**
	 * 开启GPS定位
	 */
	OTHER_OPEN_GPS(837),
	/**
	 * 速率测试切换回调
	 */
	OTHER_SPEED_SWITCH(838),
	/**
	 * 速率测试切换回调
	 */
	OTHER_SPEED_CLICK(839),
	/**
	 * 检查用户授权状态接口
	 */
	NET_GET_EMERGENCY_AUTH_STATE(840),
	/**
	 * 验证码检查接口
	 */
	NET_CHECK_EMERGENCY_AUDIT_SMS(841),
	/**
	 * 远程开锁操作
	 */
	NET_OPEN_EMERGENCY_LOCK(842),
	/**
	 * 查询开锁日志
	 */
	NET_GET_EMERGENCY_LOCK_LOG(843),
	/**
	 * 查询锁信息
	 */
	NET_GET_EMERGENCY_LOCK_INFO(844),
	/**
	 * 修改站别名、小区别名、锁ID
	 */
	NET_SET_EMERGENCY_PROP(845),
	/**
	 * 库存增加删除操作
	 */
	NET_SET_EMERGENCY_STOCK(846),
	/**
	 * 库存统计信息
	 */
	NET_GET_EMERGENCY_STOCK_STAC(847),
	/**
	 * 库存统计 删除设备
	 */
	OTHER_EMERGENCY_STOCK_STAC_DELETE(848),
	/**
	 * 库存统计 开锁
	 */
	OTHER_EMERGENCY_STOCK_STAC_OPEN(849),
	/**
	 * 地市选择
	 */
	OTHER_CITY_CHOOSE(850),
	/**
	 * 通过ID获取应急站列表
	 */
	NET_EMERGENT_STAION_LIST_BY_ID(851),
	/**
	 * 库存统计列表
	 */
	NET_GET_EMERGENCY_STOCK_LIST(852),
	/**
	 * 修改应急站锁ID
	 */
	NET_SET_EMERGENCY_PROP_LOCK(853),
	/**
	 * 保存应急宝开锁日志
	 */
	NET_SAVE_EMERGENCY_LOCK_LOG(854),
	/**
	 * 应急宝 获取设备类型配置
	 */
	NET_DEVICE_TYPE_CONFIG(855),
	/**
	 * 应急宝  是否有权限
	 */
	NET_EMERGENT_HAS_OPEN_AUTH(856),
	/**
	 * 应急宝开锁操作
	 */
	OTHER_EMERGENT_DEVICE_OPEN_LOCK(857),
	/**
	 * 应急宝删除
	 */
	OTHER_EMERGENT_DEVICE_DELETE(858),
	/**
	* 满格宝初始化小区
	*/
	NET_GET_MANGEBAO_GIS_INFO(859),
	/**
	* 满格宝详情数据
	*/
	NET_GET_MANGEBAO_INFO(860),
	/**
	 * 应急站 操作日志
	 */
	OTHER_EMERGENT_CELL_LOG(861),
	/**
	 * 应急站-应急盒子统计
	 */
	NET_GET_TRANSMIT_BOX_STAC(862),
	/**
	 * 应急站-操作日志列表
	 */
	NET_GET_EMERGENCY_SITE_OPER_LIST(863),
	/**
	 * 应急站-操作日志详情列表
	 */
	NET_GET_EMERGENCY_SITE_OPER_LOG_LIST(864),
	/**
	 * 应急站图层选择结束
	 */
	OTHER_EMERGENCY_SITE_LAYER_FINISH(865),
	/**
	 * 应急站 传输盒子
	 */
	NET_GET_TRANSMIT_BOX_LIST(866),
	/**
	 * 应急盒子 巡检
	 */
	OTHER_TRANSMIT_BOX_INSPECTION(867),
	/**
	 * 应急盒子 上传巡检记录
	 */
	NET_SAVE_TRANSMIT_BOX_LOG(868),
	/**
	 * 应急盒子 巡检历史记录
	 */
	NET_GET_TRANSMIT_BOX_LOG_LIST(869),
	/**
	 * 通过ID获取应急盒子列表
	 */
	NET_EMERGENT_BOX_LIST_BY_ID(870),
	/**
	 * 调度次数分地市统计
	 */
	NET_EMERGENT_GET_YJ_TRACK_STAC(871),
	/**
	 * 调度次数指定地市分应急设备统计
	 */
	NET_EMERGENT_GET_YJ_TRACK_LIST(872),
	/**
	 * 指定应急设备的调度历史记录
	 */
	NET_EMERGENT_GET_YJ_TRACK_DETAIL(873),
	/**
	 * 指定应急设备的最新调度记录
	 */
	NET_EMERGENT_GET_YJ_LAST_TRACK_DETAIL(874),
	/**
	 * 应急盒子详情获取
	 */
	OTHER_TRANSMIT_BOX_INFO_GET(875),
	/**
	 * 应急盒子属性修改
	 */
	OTHER_TRANSMIT_BOX_INFO_MODIFY(876),
	
	/**
	 * 基站信号曲线图点击回调NR RSRP
	 */
	OTHER_CHART_DOUBLE_RSRP_RETURN(877),
	/**
	 * 基站信号曲线图点击回调NR SINR
	 */
	OTHER_CHART_DOUBLE_SINR_RETURN(878),
	/**
	 * 集中入网NR签名图片文件加载
	 */
	OTHER_JZRW_SIGN_INIT(879),
	/**
	 * 集中入网NR子项图片文件加载
	 */
	OTHER_JZRW_CHILD_BIT_INIT(880),
	/**
	 * 集中入网NR获取小区数据
	 */
	OTHER_JZRW_CELL_LIST_INIT(881),
	/**
	 * 集中入网NR获取小区AAU图片(序列号、设备、方位角、下倾角) true完成，false正在进行下一步
	 */
	OTHER_JZRW_CELL_AAU_BIT(882),
	
	/**
	 * 远程电调获取AAU型号对应场景号
	 */
	NET_APP_GET_AAU_SEC(883);
	
	// 定义私有变量
	private int nCode;

	// 构造函数，枚举类型只能为私有
	private EnumRequest(int _nCode) {
		this.nCode = _nCode;
	}

	public int toInt() {
		return nCode;
	}

	public String toStr() {
		return String.valueOf(nCode);
	}
}
