package cn.nokia.speedtest5g.app.uitl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;

import com.android.volley.util.BasicUtil;
import com.android.volley.util.JsonHandler;
import com.android.volley.util.MyToast;
import com.android.volley.util.SharedPreHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.activity2.AzimuthTestActivity;
import cn.nokia.speedtest5g.app.activity2.BandConversionActivity;
import cn.nokia.speedtest5g.app.activity2.DowntiltTestActivity;
import cn.nokia.speedtest5g.app.activity2.HeightMeasureActivity;
import cn.nokia.speedtest5g.app.activity2.LatLngConversionActivity;
import cn.nokia.speedtest5g.app.activity2.ParamEstimateAcitivity;
import cn.nokia.speedtest5g.app.activity2.PropagationModelActivity;
import cn.nokia.speedtest5g.app.activity2.SystemDeviceActivity;
import cn.nokia.speedtest5g.app.activity2.SystemGpsActivity;
import cn.nokia.speedtest5g.app.bean.Db_Modular;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.gridview.util.Item;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.LoginRespon;
import cn.nokia.speedtest5g.app.thread.DownFileAsyncTask;
import cn.nokia.speedtest5g.app.thread.MyLoginExitThread;
import cn.nokia.speedtest5g.app.thread.StatisticeAsyncTask;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.jzxh.ui.JzxhTabActivity;
import cn.nokia.speedtest5g.pingtest.PingTestActivity;
import cn.nokia.speedtest5g.pingtest.PingTestMainActivity;
import cn.nokia.speedtest5g.speedtest.SpeedRankingMainActivity;
import cn.nokia.speedtest5g.speedtest.SpeedTestMainActivity;
import cn.nokia.speedtest5g.speedtest.SpeedWideMainActivity;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.SecretThirdUtil;
import cn.nokia.speedtest5g.wifi.ui.NetworkDiagnoseActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiGlActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiSdMainActivity;
import cn.nokia.speedtest5g.wifi.ui.WifiSnMainActivity;

/**
 * 模块点击处理事件
 * @author zwq
 * 
 * 新增模块流程：
 * 	1.后台配置权限数据，根据模块大类配置 0集中管理 1集中入网 2集中工参管理 3集中分析优化 4优化工具-基础工具 5优化工具-业务助手 6优化工具-三方应用 100 首页-更多
 *	2.非第三方app情况下，则需在 initModularAllData方法里添加对应的入口类(mMapClass对象添加，如果需要传递数据到下一个类的话，则不用添加直接第三点引用)
 *  3.如果需要传递数据到下一个页面的话，则在initGridView方法里根据权限代码来做判断
 */
public class ModeClickHandler implements ListenerBack{

	public static ModeClickHandler mModeClickHandler = null;

	public static synchronized ModeClickHandler getInstances(BaseActionBarActivity activity){
		if (mModeClickHandler == null) {
			mModeClickHandler = new ModeClickHandler();
			mNowVersion = (Integer) BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false);
		}
		mActivity = activity;
		return mModeClickHandler;
	}

	/**
	 * 模块总数据
	 * 0集中管理
	 * 1集中入网
	 * 2集中工参管理
	 * 3集中分析优化
	 * 4优化工具-基础工具
	 * 5优化工具-业务助手
	 * 6优化工具-三方应用
	 * 100 首页-更多
	 */
	private HashMap<Integer, List<Db_Modular>> mMapArrModular;

	private HashMap<String, Class<?>> mMapClass;
	/**
	 * 初始化设置所有模块数据(需子线程调用)
	 */
	@SuppressWarnings("unchecked")
	public void initModularAllData(){
		mMapArrModular = new HashMap<>();
		//读取数据库数据
		mMapArrModular.put(0, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"0"}, "_id ASC")));
		mMapArrModular.put(1, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"1"}, "_id ASC")));
		mMapArrModular.put(2, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"2"}, "_id ASC")));
		mMapArrModular.put(3, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"3"}, "_id ASC")));
		mMapArrModular.put(4, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"4"}, "_id ASC")));
		mMapArrModular.put(5, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"5"}, "_id ASC")));
		mMapArrModular.put(6, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"6"}, "_id ASC")));
		mMapArrModular.put(100, getForPermissions((List<Db_Modular>) DbHandler.getInstance().queryObj(Db_Modular.class, "ADSCRIPTION_TYPE=?", new String[]{"100"}, "_id ASC")));

		mMapClass = new HashMap<>();
//		mMapClass.put(getString(R.string.permissionsDdcs), JJ_XncsActivity.class);//性能测试
//		mMapClass.put(getString(R.string.permissionsHzdy), HzdyListActivity.class);//宏站单验
//		mMapClass.put(getString(R.string.permissionsSfdy), SfdyListActivity.class);//室分单验
//		mMapClass.put(getString(R.string.permissionsJdyk), JdykActivity.class);//简单硬扩
//		mMapClass.put(getString(R.string.permissionsZdkz), ZdkzActivity.class);//自动开站4G
//		mMapClass.put(getString(R.string.permissionsZdkzNr), ZdkzNrActivity.class);//自动开站5G
//		mMapClass.put(getString(R.string.permissionsTkzyhGc), GctzSendActivity.class);//工单派发-----------修改为：天馈自优化
//		mMapClass.put(getString(R.string.permissionsTxxj), AntennaPatrolActivity.class);//天线巡警-----------修改为：天馈自纠错
//		mMapClass.put(getString(R.string.permissionsYcdt), YcdtHomeActivity.class);//远程电调
//		mMapClass.put(getString(R.string.permissionsMgb), MgbHomeActivity.class);//满格宝
//		mMapClass.put(getString(R.string.permissionsPCI), WyzdhPciDownActivity.class);//PCI优化
//		mMapClass.put(getString(R.string.permissionsCsxg), JJ_ParameterActivity.class);//参数修改
//		mMapClass.put(getString(R.string.permissionsTkys), TkzyhHomeActivity.class);//天馈运算
//		mMapClass.put(getString(R.string.permissionsXqzb), XqzbMainActivity.class);//小区指标
		mMapClass.put(getString(R.string.permissionsXtxx), SystemDeviceActivity.class);//系统信息
		mMapClass.put(getString(R.string.permissionsWzxx), SystemGpsActivity.class);//位置信息
		mMapClass.put(getString(R.string.permissionsXqjcl), DowntiltTestActivity.class);//下倾角测量
		mMapClass.put(getString(R.string.permissionsFwjcl), AzimuthTestActivity.class);//方位角测量
		mMapClass.put(getString(R.string.permissionsZg), HeightMeasureActivity.class);//高度测量
		mMapClass.put(getString(R.string.permissionsPdhs), BandConversionActivity.class);//频点换算
		mMapClass.put(getString(R.string.permissionsJwdzh), LatLngConversionActivity.class);//经纬度转换
		mMapClass.put(getString(R.string.permissionsCbmx), PropagationModelActivity.class);//传播模型
		mMapClass.put(getString(R.string.permissionsLjdd), ParamEstimateAcitivity.class);//楼间对打
		mMapClass.put(getString(R.string.permissionsJzxh), JzxhTabActivity.class);//基站信号
//		mMapClass.put(getString(R.string.permissionsGis), JJ_GisMapActivity.class);//GIS呈现
		mMapClass.put(getString(R.string.permissionsSlcs), SpeedTestMainActivity.class);//速率测试
//		mMapClass.put(getString(R.string.permissionsBlcs), ErgodicMainActivity.class);//遍历测试
//		mMapClass.put(getString(R.string.permissionsZdjp), ZdpzListActivity.class);//自动纠偏
//		mMapClass.put(getString(R.string.permissionsWltc), TiaoCiMainActivity.class);//网络挑刺
//		mMapClass.put(getString(R.string.permissionsXcpz), JJ_PictureActivity.class);//现场拍照
//		mMapClass.put(getString(R.string.permissionsGzlcj), JiFeiListActivity.class);//网优计费系统
//		mMapClass.put(getString(R.string.permissionsComplaint), TscsMyWorkActivity.class);//投诉测试
//		mMapClass.put(getString(R.string.permissionsAttendancePerson), AttendancePersonActivity.class);//考勤系统-人员考勤
//		mMapClass.put(getString(R.string.permissionsAttendanceCarInstrument), AttendanceSystemTabActivity.class);//考勤系统-车辆仪表管理
//		mMapClass.put(getString(R.string.permissionsNbTest), NbTestInitActivity.class);//nb测试
//		mMapClass.put(getString(R.string.permissionsJzxq), JzxqGisActivity.class);//价值小区
//		mMapClass.put(getString(R.string.permissionsTmzh), TmzhHomeActivity.class);//天面整合
//		mMapClass.put(getString(R.string.permissionsNrCell), NrCellActivationActivity.class);//5G小区激活
		mMapClass.put(getString(R.string.permissionsPingTest), PingTestActivity.class);//Ping测试
		mMapClass.put(getString(R.string.permissionsWfxdjc), WifiActivity.class);//WIFI信道检测
        mMapClass.put(getString(R.string.permissionsWfgrjc), WifiGlActivity.class);//WIF干扰分析
        mMapClass.put(getString(R.string.permissionsWfxhjc), WifiSdMainActivity.class);//WIF信号检测
        mMapClass.put(getString(R.string.permissionsWfcwjc), WifiSnMainActivity.class);//蹭网检测
        mMapClass.put(getString(R.string.permissionsWfwlzd), NetworkDiagnoseActivity.class);//网络诊断
        mMapClass.put(getString(R.string.permissionsWfwsph), SpeedRankingMainActivity.class);//网速排行
        mMapClass.put(getString(R.string.permissionsWfzbws), SpeedWideMainActivity.class);//周边网速
	}

	//如果没权限的话，直接过滤掉
	private List<Db_Modular> getForPermissions(List<Db_Modular> list){
		if (list == null || list.size() <= 0) {
			return null;
		}
		for (int i = list.size() - 1; i >= 0; i--) {
			if (!MyToSpile.getInstances().isAuthorityMenu(list.get(i).CODE)) {
				list.remove(i);
			}
		}
		if (list.size() <= 0) {
			return null;
		}
		return list;
	}

	/**
	 * 获取模块集合
	 * @param modularType
	 * @return
	 */
	private List<Db_Modular> getListModularList(int modularType){
		//获取本地是否有模块数据存储
		if (mMapArrModular == null) {
			return null;
		}
		List<Db_Modular> list = mMapArrModular.get(modularType);
		if (list == null || list.size() <= 0) {
			return null;
		}
		return list;
	}

	private static int mNowVersion;

	private Item mClickItem;

	private static BaseActionBarActivity mActivity;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			MyEvents events = (MyEvents) arg0.obj;
			switch (events.getMode()) {
			case TASK:
				//自动偏置数据本地数据获取后返回
				if (events.getType() == UtilHandler.getInstance().toInt(getString(R.string.code_hzdy), 0)
				|| events.getType() == UtilHandler.getInstance().toInt(getString(R.string.code_sfdy), 0)) {
					getZdpzData(events.getType());
					//跳转方案初勘应用	
				}else if (events.getType() == EnumRequest.OTAHER_TO_FACK.toInt()) {
					mActivity.dismissMyDialog();
					if (events.isOK()) {
						startPackage(getModularItem(getModular(getString(R.string.permissionsFack)), null), true);
					} else {
						new CommonDialog(mActivity).setButtonText("前往下载", "取消").setListener(ModeClickHandler.this).show("暂无检测到基站工参数据,是否前往下载？",EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
					}
				}else if (events.getType() == EnumRequest.OTHER_TOAST_MSG.toInt()) {
					if (!events.isOK()) {
						MyToast.getInstance(mActivity).showCommon(events.getObject().toString(), Gravity.CENTER);
					}
				}
				break;
			default:
				break;
			}
			return true;
		}
	});

	/**
	 * 是否提示新功能
	 * @param version 哪个版本号需要提示
	 * @return
	 */
	private boolean isNewMode(int version){
		return mNowVersion == version;
	}

	private void sendMessage(MyEvents events) {
		Message msg = mHandler.obtainMessage();
		msg.obj = events;
		mHandler.sendMessage(msg);
	}

	/**
	 * 模块点击事件处理
	 * @param clickItem
	 * @param isMainUi 是否在主页
	 */
	public void clickMode(Item clickItem,boolean isMainUi){
		mClickItem = clickItem;
		if (mClickItem == null || !mClickItem.isClick()) {
			return;
		}
		//方案初勘
		if (mClickItem.getId() == UtilHandler.getInstance().toInt(getString(R.string.code_fack), 0)) {
			initstartExploration();
			return;
			//道路测试及GIS图层	
		}else if (mClickItem.getId() == UtilHandler.getInstance().toInt(getString(R.string.code_gis), 0) || 
				mClickItem.getId() == UtilHandler.getInstance().toInt(getString(R.string.code_dlcs), 0)) {
			initDepartDbFile(EnumRequest.OTHER_TOAST_MSG.toInt());
		}
		if (TextUtils.isEmpty(mClickItem.strLauchPath)) {
			//宏站单验或室分单验
			if (mClickItem.getId() == UtilHandler.getInstance().toInt(getString(R.string.code_hzdy), 0) || mClickItem.getId() == UtilHandler.getInstance().toInt(getString(R.string.code_sfdy), 0)) {
				getZdpzData(mClickItem.getId());
			}else if (mClickItem.getIntent() == null) {
				mActivity.showCommon("正在完善…");
			}else {
				if (mClickItem.isStartActivityForResult()) {
					if (mActivity.getParent() != null) {
						mActivity.getParent().startActivityForResult(mClickItem.getIntent(), mClickItem.getRequestCode());
					} else {
						mActivity.startActivityForResult(mClickItem.getIntent(), mClickItem.getRequestCode());
					}
				}else {
					mActivity.startActivity(mClickItem.getIntent());
				}
				startDevicePerformanceService();
			}
			if (!TextUtils.isEmpty(mClickItem.getTitleCode())) {
				new StatisticeAsyncTask().execute(mClickItem.getTitleCode(),mClickItem.getCode());
			}
			//先判断本地是否存在	
		}else {
			startPackage(mClickItem, true);
		}
	}

	/**
	 * 启动手机性能参数采集服务
	 */
	private void startDevicePerformanceService() {
		if (mClickItem == null || !mClickItem.isClick()) {
			return;
		}
		//		Intent service = new Intent(mActivity, PhonePerformanceService.class);
		//		service.putExtra("menuCode", mClickItem.menuCode);
		//		service.putExtra("menuName", mClickItem.getName());
		//		mActivity.startService(service);
	}

	/**
	 * 根据包来启动应用
	 * @param items
	 */
	public void startPackage(Item items,boolean version){
		PackageInfo packageInfoSmarttest = null;
		try {
			packageInfoSmarttest = mActivity.getPackageManager().getPackageInfo(items.strPackage, 0);
		} catch (Exception e) {
			packageInfoSmarttest = null;
		}
		if (packageInfoSmarttest == null) {
			new CommonDialog(mActivity, false).setListener(ModeClickHandler.this).setButtonText("立即更新", "取消")
			.show(items.getTestMsg(), EnumRequest.DIALOG_DOWN_OK.toInt());
			return;
		}else if (items.getIntent() == null) {
			PackageManager packageManager = mActivity.getPackageManager();
			mActivity.goIntent(packageManager.getLaunchIntentForPackage(items.strPackage), false);
			startDevicePerformanceService();
			MyLoginExitThread.setNotTatExit(true);
		}else {
			//如果版本和线上不一致，则更新
			if (packageInfoSmarttest.versionCode < items.newAppVersionCode && version) {
				if (items.updageStatus == 2) {
					new CommonDialog(mActivity, false).setListener(ModeClickHandler.this).setButtonText("立即更新", "取消")
					.show(items.getVersionContent(), EnumRequest.DIALOG_DOWN_OK.toInt());
				}else {
					new CommonDialog(mActivity, false).setListener(ModeClickHandler.this).setButtonText("立即更新", "稍后更新")
					.show(items.getVersionContent(), EnumRequest.DIALOG_DOWN_OK_ON.toInt());	
				}
				return;
			}
			try {
				mActivity.startActivity(items.getIntent());
				startDevicePerformanceService();
				MyLoginExitThread.setNotTatExit(true);
			} catch (Exception e) {
				mActivity.showCommon("启动失败!");
			}
		}
		if (!TextUtils.isEmpty(items.getTitleCode())) {
			new StatisticeAsyncTask().execute(items.getTitleCode(),items.getCode());
		}
	}

	/*
	 * 方案初勘进入初始化判断
	 */
	public void initstartExploration(){
		if (SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_DEPART(), "").isEmpty()) {
			mActivity.showCommon("暂无工参数据权限,请联系管理员分配工参权限!");
		}else {
			mActivity.showMyDialog();
			initDepartDbFile(EnumRequest.OTAHER_TO_FACK.toInt());
		}
	}

	/**
	 * 判断当前用户是否有对应的工参权限且是否已经下载
	 * @param what
	 */
	public void initDepartDbFile(final int what){
		new Thread() {
			public void run() {
				HashMap<String, String> departCodeMap = MyToSpile.getInstances().getToCode(1);
				if (departCodeMap != null && departCodeMap.size() > 0) {
					String pathDb = PathUtil.getInstances().getCurrentPath() + PathUtil.getInstances().getDbPath() + "/";
					// 菜单权限分配
					Iterator<Entry<String, String>> iterator = departCodeMap.entrySet().iterator();
					while (iterator.hasNext()) {
						if (PathUtil.getInstances().isExitFile(String.format("%s%s%s", pathDb, iterator.next().getValue(), ".cif"))) {
							sendMessage(new MyEvents(ModeEnum.TASK,what,null,true));
							return;
						}
					}
				}
				sendMessage(new MyEvents(ModeEnum.TASK,what,"当前未下载基站工参,请到工参下载列表进行下载"));
			};
		}.start();
	}

//	//自动偏置数据对象---目前宏站单验和室分单验调用时候用
//	private ZdpzDyDb mZdpzDyDb;
	/**
	 * 获取自动偏置数据
	 * @param valueId 宏站单验或室分单验
	 */
	public void getZdpzData(final int valueId) {
		mActivity.showMyDialog();
		// 如果不为空，则判断是否在有效期内，
		// 如果在有效期内再次判断是否要重新查询数据（与上次查询时间以1分钟间隔）
//		if (mZdpzDyDb != null) {
//			if (mZdpzDyDb.isExist() && System.currentTimeMillis() - mZdpzDyDb.getLastSynchronized() < (60 * 1000)) {
//				goDyActivity(valueId);
//				return;
//			}
//		} else {
//			// 这里查询本地数据是否存在
//			new Thread() {
//				public void run() {
//					@SuppressWarnings("unchecked")
//					ArrayList<ZdpzDyDb> queryZdpzDyDb = (ArrayList<ZdpzDyDb>) DbHandler.getInstance().queryAll(ZdpzDyDb.class);
//					if (queryZdpzDyDb != null && queryZdpzDyDb.size() > 0) {
//						mZdpzDyDb = queryZdpzDyDb.get(0);
//					} else {
//						mZdpzDyDb = new ZdpzDyDb();
//					}
//					sendMessage(new MyEvents(ModeEnum.TASK, valueId, "", true));
//				};
//			}.start();
//			return;
//		}
		// 这里查询接口数据
		NetWorkUtilNow.getInstances().readNetworkGet(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_ZDPZ_QUERY) + mActivity.getUserID() + "&IMEI=" + SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""),valueId, ModeClickHandler.this);
	}

	/**
	 * 跳转单验模块
	 * @param toId
	 */
	public void goDyActivity(int toId){
		mActivity.dismissMyDialog();
//		SharedPreHandler.getShared(MyApplication.getContext()).setBooleanShared(TypeKey.getInstance().IS_RECTIFY, true);
//		Bundle mBundle = new Bundle();
//		mBundle.putSerializable("data", mZdpzDyDb);
//		// 宏站单验
//		if (toId == UtilHandler.getInstance().toInt(getString(R.string.code_hzdy), 0)) {
//			mActivity.goIntent(HzdyListActivity.class, mBundle, false);
//			// 室分单验
//		} else if (toId == UtilHandler.getInstance().toInt(getString(R.string.code_sfdy), 0)) {
//			mActivity.goIntent(SfdyListActivity.class, mBundle, false);
//		}
		startDevicePerformanceService();
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		//应用下载回调
		if (type == EnumRequest.DIALOG_DOWN_OK.toInt()) {
			if (isTrue) {
				new DownFileAsyncTask(mActivity,null, mClickItem.getName() + ".apk", mClickItem.appDownUrl,false,null).execute();
			}
		}else if (type == EnumRequest.DIALOG_DOWN_OK_ON.toInt()) {
			if (isTrue) {
				new DownFileAsyncTask(mActivity,null, mClickItem.getName() + ".apk", mClickItem.appDownUrl,false,null).execute();
			}else {
				startPackage(mClickItem, false);
			}
			//自动偏置接口获取返回	
		}else if (type == UtilHandler.getInstance().toInt(getString(R.string.code_hzdy), 0) ||
				type == UtilHandler.getInstance().toInt(getString(R.string.code_sfdy), 0)) {
			mActivity.dismissMyDialog();
//			if (isTrue) {
//				ZdpzDyResponse response = JsonHandler.getHandler().getTarget(object.toString(), ZdpzDyResponse.class);
//				if (response != null && response.isRs()) {
//					if (response.getDatas() != null && response.getDatas().getBiasOnOrOff() == 1
//							&& !response.getDatas().isExist()) {
//						// 不在有效期
//						showToastDialog("纠偏有效期已过期,请重新选择纠偏");
//						return;
//						// 如果是未启用，则直接跳转
//					} else if (response.getDatas() != null) {
//						mZdpzDyDb = response.getDatas();
//						mZdpzDyDb.setLastSynchronized(System.currentTimeMillis());
//						if (mZdpzDyDb.isExist() && mZdpzDyDb.getBiasOnOrOff() == 1) {
//							mZdpzDyDb.setRectifyRsrp(Math.abs(mZdpzDyDb.getRsrpBias()) > Math.abs(mZdpzDyDb.getRsrpLimit()));
//							mZdpzDyDb.setRectifySinr(Math.abs(mZdpzDyDb.getSinrBias()) > Math.abs(mZdpzDyDb.getSinrLimit()));
//						} else {
//							mZdpzDyDb.setRectifyRsrp(false);
//							mZdpzDyDb.setRectifySinr(false);
//						}
//						// 插入数据，然后跳转
//						new Thread() {
//							public void run() {
//								DbHandler.getInstance().deleteClass(ZdpzDyDb.class);
//								DbHandler.getInstance().insert(mZdpzDyDb);
//							};
//						}.start();
//						goDyActivity(type);
//						return;
//					}
//				}else if (response != null) {
//					showToastDialog(response.getMsg());
//				}else {
//					showToastDialog("获取失败,请检查网络或稍后再试");
//				}
//			} else {
//				showToastDialog(object.toString());
//			}
			//跳转到工参下载页面	
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()) {
//			if (isTrue) {
//				mActivity.goIntent(DownDbActivity.class, null, false);
//			}
		}
	}

	private void showToastDialog(String msg){
		new CommonDialog(mActivity).show(msg);
	}

	//---------------------------控件-------------------------

	private String getString(int resId) {
		if (mActivity != null) {
			return mActivity.getString(resId);
		} else {
			return SpeedTest5g.getContext().getString(resId);
		}
	}; 

	/**
	 * 初始化模块
	 * @param modularType
	 * 0集中管理
	 * 1集中入网
	 * 2集中工参管理
	 * 3集中分析优化
	 * 4优化工具-便民工具
	 * 5优化工具-专业助手
	 * 6优化工具-三方应用
	 * 100 首页-更多
	 */
	public List<Item> initGridView(int modularType){
		List<Item> arrItemsTemporary = new ArrayList<Item>();
		List<Db_Modular> listModularList = getListModularList(modularType);
		if (listModularList == null) {
			return arrItemsTemporary;
		}
		//集中规划
		if (modularType == 0) {
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//室分图纸-----------	
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsGis_sftz))) {
//					Bundle bundleSftz = new Bundle();
//					bundleSftz.putBoolean("showdrawing", true);
//					arrItemsTemporary.add(getModularItem(modularItem, JJ_GisMapActivity.class,bundleSftz));
					//外接应用app	
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//集中入网	
		}else if (modularType == 1) {
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//宏站入网4G-----------
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsHzrw))) {
//					Bundle bundleHzrw = new Bundle();
//					bundleHzrw.putInt("stationType", TypeKey.getInstance().TYPE_OUTSITE);
//					arrItemsTemporary.add(getModularItem(modularItem, LteRwListActivity.class,bundleHzrw));
//					//室分入网4G-----------
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsSfrw))) {
//					Bundle bundleSfrw = new Bundle();
//					bundleSfrw.putInt("stationType", TypeKey.getInstance().TYPE_INSITE);
//					arrItemsTemporary.add(getModularItem(modularItem, LteRwListActivity.class,bundleSfrw));
//					//宏站入网5G
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsNrHzrw))) {
//					Bundle bundleNrHzrw = new Bundle();
//					bundleNrHzrw.putInt("stationType", TypeKey.getInstance().TYPE_OUTSITE);
//					arrItemsTemporary.add(getModularItem(modularItem, NrRwListActivity.class,bundleNrHzrw));
//					//室分入网5G
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsNrSfrw))) {
//					Bundle bundleNrSfrw = new Bundle();
//					bundleNrSfrw.putInt("stationType", TypeKey.getInstance().TYPE_INSITE);
//					arrItemsTemporary.add(getModularItem(modularItem, NrRwListActivity.class,bundleNrSfrw));
//					//应急站点
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsGis_yjb))) {
//					Intent intentEmergent = new Intent(mActivity,JJ_GisMapActivity.class);
//					intentEmergent.putExtra("data",1);
//					arrItemsTemporary.add(getModularItemIntent(modularItem, intentEmergent));
//					//技术验证
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsNrJsyz))) {
//					Intent intentEmergent = new Intent(mActivity,ZdkzNrActivity.class);
//					intentEmergent.putExtra(ZdkzNrUtil.getInstances().KEY_KZ_TYPE,1);
//					arrItemsTemporary.add(getModularItemIntent(modularItem, intentEmergent));
//					//技术验证-福建
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsNrJsyzFj))) {
//					Intent intentEmergent = new Intent(mActivity,ZdkzNrActivity.class);
//					intentEmergent.putExtra(ZdkzNrUtil.getInstances().KEY_KZ_TYPE,2);
//					arrItemsTemporary.add(getModularItemIntent(modularItem, intentEmergent));
					//外接应用app		
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//集中工参管理	
		}else if (modularType == 2) {
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//工参巡检-----------修改为：天馈自巡检	
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsTkzxjGc))) {
//					Bundle bundleGcxj = new Bundle();
//					bundleGcxj.putInt("type", 0);
//					arrItemsTemporary.add(getModularItem(modularItem, GcxjOrGctzActivity.class,bundleGcxj));
					//外接应用app	
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//集中分析优化	
		}else if (modularType == 3) {
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//一键诊断-----------	
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsYjzd))) {
//					Bundle bundleYjzd = new Bundle();
//					bundleYjzd.putInt("initial", 1);
//					arrItemsTemporary.add(getModularItem(modularItem, OneKeyDiagnosisActivity.class, bundleYjzd));
					//外接应用app	
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//优化工具-便民工具	
		}else if (modularType == 4) {
			for (Db_Modular modularItem : listModularList) {
				//便民工具一类ping测速 CODE 1001~1004
				if (getString(R.string.permissionsYxcs).equals(modularItem.CODE) ||
						getString(R.string.permissionsSpcs).equals(modularItem.CODE) ||
						getString(R.string.permissionsZxcs).equals(modularItem.CODE) ||
						getString(R.string.permissionsDscs).equals(modularItem.CODE) ||
						getString(R.string.permissionsJstx).equals(modularItem.CODE)) {
					Intent intent = new Intent(mActivity, PingTestMainActivity.class);
					intent.putExtra("MENU_ID", modularItem.MENU_ID);
					intent.putExtra("MENU_NAME", modularItem.MENU_NAME);
					arrItemsTemporary.add(getModularItemIntent(modularItem, intent));
					//外接应用app
				}else if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//外接应用app
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//优化工具-专业工具
		}else if(modularType == 5){
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//道路测试---------	
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsDlcs))) {
//					Bundle bundleDlcs = new Bundle();
//					bundleDlcs.putBoolean("showsignal", true);
//					arrItemsTemporary.add(getModularItem(modularItem, RoadMainActivity.class, bundleDlcs));
//					//语音测试
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsYycs))) {
//					Intent intent = new Intent(mActivity,RoadVoiceTestActivity.class);
//					intent.putExtra("isOpen", true);
//					arrItemsTemporary.add(getModularItemIntent(modularItem, intent));
//					//公告
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsNotify))) {
//					arrItemsTemporary.add(getModularItem(modularItem, new Intent(mActivity, NotifyMainActivity.class),null,true,EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt()));
//					//共站信息---------
//				}else if (modularItem.CODE.equals(getString(R.string.permissionsGis_Gzxx))) {
//					Intent intentTotalStationt = new Intent(mActivity, JJ_GisMapActivity.class);
//					intentTotalStationt.putExtra("data", 2);
//					arrItemsTemporary.add(getModularItemIntent(modularItem, intentTotalStationt));
					//外接应用app	
				}else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
			//优化工具-三方应用
		}else if(modularType == 6){
			for (Db_Modular modularItem : listModularList) {
				if (mMapClass.get(modularItem.CODE) != null) {
					arrItemsTemporary.add(getModularItem(modularItem, mMapClass.get(modularItem.CODE)));
					//外接应用app	
				} else if(!TextUtils.isEmpty(modularItem.FIRING_PACKAGE) && !TextUtils.isEmpty(modularItem.FIRING_CLASS)){
					arrItemsTemporary.add(getModularItem(modularItem, null));
				}
			}
		} else if (modularType == 100) {// 首页更多
//			for (Db_Modular modularItem : listModularList) {
//				arrItemsTemporary.add(getModularItem(modularItem, new Intent(mActivity, MyAppsActivity.class), null, true, EnumRequest.OTHER_MAINHOME_UPDATE_HOME_INIT_DATA.toInt()));
//				break;
//			}
		}
		return arrItemsTemporary;
	}

	/**
	 * 如果不是multiple的倍数，则进行填充
	 * @param arrItemsTemporary
	 * @return
	 */
	public List<Item> fillItem(List<Item> arrItemsTemporary,int multiple){
		long sumSize = arrItemsTemporary.size();
		if (sumSize%4 != 0) {
			for (int i = 0; i < multiple - (sumSize%multiple); i++) {
				arrItemsTemporary.add(new Item(false));
			}
		}
		return arrItemsTemporary;
	}

	/**
	 * 获取我的应用列表
	 * 
	 * @param isShowMore
	 *            是否显示更多按钮
	 * @return
	 */
	public List<Item> getMyApps(boolean isShowMore) {
		String myAppsIds = SharedPreHandler.getShared(mActivity).getStringShared(TypeKey.getInstance().KEY_MY_APPS_MENU_CODES, "");
		// 先获取全部功能列表
		List<Item> allAppList = getAllApps();
		// 再根据id，我获取我的应用列表
		ArrayList<Item> myAppsList = new ArrayList<>();
		String[] myMenuCodeArray = myAppsIds.split(",");
		for (int i = 0; i < myMenuCodeArray.length; i++) {
			String myMenuCode =myMenuCodeArray[i];
			for (int j = 0; j < allAppList.size(); j++) {
				Item item = allAppList.get(j);
				if (item.menuCode.equals(myMenuCode)) {
					item.isMyApp = true;
					myAppsList.add(item);
				}
			}
		}
		if (isShowMore) {// 添加更多按钮
			myAppsList.addAll(initGridView(100));
		}
		return myAppsList;
	}

	/**
	 * 获取全部应用列表
	 * 
	 * @return
	 */
	public List<Item> getAllApps() {
		ArrayList<Item> allAppList = new ArrayList<>();
		for (int i = 0; i <= 6; i++) {
			allAppList.addAll(initGridView(i));
		}
		return allAppList;
	}

	/**
	 * 通过ID获取应用模块信息
	 * 
	 * @return
	 */
	public Item getAppByMenuCode(String menuCode) {
		if (TextUtils.isEmpty(menuCode)) {
			return null;
		}
		Item item = null;
		List<Item> allAppList = getAllApps();
		for (int i = 0; i < allAppList.size(); i++) {
			if (menuCode.equals(allAppList.get(i).menuCode)) {
				item = allAppList.get(i);
				break;
			}
		}
		return item;
	}

	/*
	 * 获取模块
	 * @return
	 */
	private Item getModularItem(Db_Modular modular,Class<?> c){
		return getModularItem(modular, c , null);
	}

	/*
	 * 获取模块
	 * @return
	 */
	private Item getModularItem(Db_Modular modular,Class<?> c,Bundle bundle){
		return getModularItem(modular,  c != null ? new Intent(mActivity,c) : null, bundle, false, 0);
	}

	/*
	 * 获取模块
	 * @return
	 */
	private Item getModularItemIntent(Db_Modular modular,Intent intent){
		return getModularItem(modular,  intent, null, false, 0);
	}

	/**
	 * 获取模块
	 * @param modular
	 * @param bundle 是否要传递给下一个页面
	 * @param isStartActivityForResult 是否需要回调
	 * @param requestCode 回调类型
	 * @return
	 */
	private Item getModularItem(Db_Modular modular,Intent toIntent,Bundle bundle,boolean isStartActivityForResult,int requestCode){
		mClickItem = new Item(modular.CODE, UtilHandler.getInstance().toInt(modular.DICT_CODE, 0), modular.MENU_NAME, modular.MENU_IMG_URL);
		//是否是第三方app
		if (toIntent == null) {
			Intent intentModular = new Intent();
			intentModular.putExtra("title", modular.MENU_NAME);
			intentModular.setComponent(new ComponentName(modular.FIRING_PACKAGE,modular.FIRING_CLASS));
			//需要传输的用户信息
			LoginRespon mLoginRespon = new LoginRespon();
			mLoginRespon.setUser_id(mActivity.getUserID());
			mLoginRespon.setLogin_name(Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_TO_USER, "")));
			mLoginRespon.setUser_name(mActivity.getUserName());
			mLoginRespon.setPhone(mActivity.getUserPhone());
			mLoginRespon.setDepart_codes(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_DEPART(), ""));
			mLoginRespon.setSession_id(NetWorkUtilNow.getInstances().getLoginSessionId());
			mLoginRespon.provinceTag = NetWorkUtilNow.getInstances().getLoginProvinceTag();
			mLoginRespon.menuCode = modular.CODE;
			//兼容旧版本方案初勘
			if (modular.CODE.equals(getString(R.string.permissionsFack))) {
				intentModular.putExtra("userId", mActivity.getUserID());// 用户ID
				intentModular.putExtra("depart", SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_DEPART(), ""));//工参权限
				intentModular.putExtra("user", mActivity.getUser());// 电话
				intentModular.putExtra("name", SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_MZ(), ""));// 姓名
			}
			//将用户信息加密完后传输给第三方app
			intentModular.putExtra("userInfo", SecretThirdUtil.encode(JsonHandler.getHandler().toJson(mLoginRespon)));
			mClickItem.setIntent(intentModular);
			mClickItem.setThirdPartyApp(modular.getVERSION_NUMBER(), modular.VERSION_UPGRADE, modular.UPGRADE_CONTENT, modular.FIRING_PACKAGE, modular.FIRING_CLASS, modular.MENU_APP_URL);
			mClickItem.updageStatus = "强制性".equals(modular.UPGRADE_TYPE) ? 2 : 0;
		}else{
			mClickItem.setIntent(toIntent);
			if (bundle == null) {
				bundle = new Bundle();
			}
			bundle.putString("title", modular.MENU_NAME);
			mClickItem.getIntent().putExtras(bundle);
			mClickItem.setStartActivityForResult(isStartActivityForResult);
			mClickItem.setRequestCode(requestCode);
		}
		mClickItem.setContent(modular.OUTLINE);
		mClickItem.setTitleCode(modular.DICT_CODE);
		if (!TextUtils.isEmpty(modular.KEYWORDS)) {
			mClickItem.setTagArr(modular.KEYWORDS.split(","));
		}
		mClickItem.setNewVersion(isNewMode(modular.getVERSION_NUMBER()));
		return mClickItem;
	}

	/**
	 * 根据权限获取模块
	 * @param permissionsStr
	 * @return
	 */
	private Db_Modular getModular(String permissionsStr){
		if (mMapArrModular == null || mMapArrModular.size() <= 0) {
			return null;
		}
		Iterator<Entry<Integer, List<Db_Modular>>> iterator = mMapArrModular.entrySet().iterator();
		while (iterator.hasNext()) {
			List<Db_Modular> valueList = iterator.next().getValue();
			if (valueList != null && valueList.size() > 0) {
				for (Db_Modular db_Modular : valueList) {
					if (db_Modular.CODE.equals(permissionsStr)) {
						return db_Modular;
					}
				}
			}
		}
		return null;
	}

	public Intent getPingIntent(String CODE){
		List<Db_Modular> listModularList = getListModularList(4);
		if(listModularList != null){
			for (Db_Modular modularItem : listModularList) {
				if (CODE.equals(modularItem.CODE)) {
					Intent intent = new Intent(mActivity, PingTestMainActivity.class);
					intent.putExtra("MENU_ID", modularItem.MENU_ID);
					intent.putExtra("MENU_NAME", modularItem.MENU_NAME);
					return intent;
				}
			}
		}
		return null;
	}

	public Item getClickItem() {
		return mClickItem;
	}
}
