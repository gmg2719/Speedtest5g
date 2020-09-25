package cn.nokia.speedtest5g.app.cast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.listener.MyLocationListener;
import cn.nokia.speedtest5g.app.uitl.PoiSearchGetUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.util.gps.OnLocationListener;
import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.utils.DistanceUtil;
import com.fjmcc.wangyoubao.app.signal.BandUtil;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;
import com.fjmcc.wangyoubao.app.signal.OnChangSignalListener;
import com.fjmcc.wangyoubao.app.signal.ServiceSignalSim1;
import com.fjmcc.wangyoubao.app.signal.ServiceSignalSim2;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;
import com.fjmcc.wangyoubao.app.signal.bean.TDCell;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * sim信号处理类
 * @author zwq
 *	
 *	使用流程：
 *		注册(一般onCreate方法调用)：SimSignalHandler.getInstances().addListener(listenerBack, locationListener)
 *		注销(一般onDestroy方法调用)：SimSignalHandler.getInstances().removeListener(listenerBack, locationListener);
 *
 *	信号回调方法onSignalListener(int netType<主网类型>,int adjType<邻区类型>,int simPosition<卡1=0 ，卡2=1>,boolean isTestSim<是否是默认卡>,Object... obj<[0]主体 [1]邻区>)
 *	ps:此方法已包含基站名称相关注册，无需另外调用。
 *
 *  经纬度回调方法onLocation(LocationBean location)
 */
public class SimSignalHandler implements ListenerBack{

	private static SimSignalHandler mSsh = null;
	
	public static synchronized SimSignalHandler getInstances(){
		if (mSsh == null) {
			mSsh = new SimSignalHandler();
		}
		return mSsh;
	}
	
	private List<OnChangSignalListener> mListenerSignalList = new ArrayList<>();
	
	private LatLng mLastLatLng;
	//符合条件NrCell
	private NrCell mNearestNrCell = null;
	//带基站名的对象-LTE
	private List<LTECell> mListLteName = new ArrayList<LTECell>();
	//带基站名的对象-Gsm
	private List<GSMCell> mListGsmName = new ArrayList<GSMCell>();
	//当前是否是游客状态 true非游客 false游客
	private boolean isUserStatus = false;
	
	private List<OnLocationListener> mOnLocationListListener = new ArrayList<>();
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		private boolean isOkFile = false;
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 101://地址返回
				if (mOnLocationListListener.size() > 0) {
					for (int i = 0; i < mOnLocationListListener.size(); i++) {
						mOnLocationListListener.get(i).onAnalysis(101, msg.obj.toString(),isOkFile);
					}
				}
//				readFile(msg.obj.toString());
				break;
			case 102://解密完成
//				fileRunnable = null;
				String sPath = msg.obj.toString();
				WybLog.syso("解密完成...." + sPath);
				if (!sPath.isEmpty()) {
					isOkFile = true;
//					WQ_GisDbHandler.getIntances().setOldPath(sPath);
				}
				if (mOnLocationListListener.size() > 0) {
					for (int i = 0; i < mOnLocationListListener.size(); i++) {
						mOnLocationListListener.get(i).onAnalysis(102, sPath);
					}
				}
				break;
			case 103://LTE读取返回---获取基站名称
				if (mListLteName.size() > 10) {
					mListLteName.remove(0);
				}
				LTECell mLteCellName = (LTECell) msg.obj;
				if (!TextUtils.isEmpty(mLteCellName.name)) {
					mListLteName.add(mLteCellName);
				}
				break;
			case 105://GSM读取返回---获取基站名称
				if (mListGsmName.size() > 10) {
					mListGsmName.remove(0);
				}
				GSMCell mGSMCell = (GSMCell) msg.obj;
				if (!TextUtils.isEmpty(mGSMCell.name)) {
					mListGsmName.add(mGSMCell);
				}
				break;
			case 106: //最近小区
				mNearestNrCell = (NrCell)msg.obj;
				break;
			default:
				break;
			}
			return true;
		}
	});
	
	private int mNetworkType;
	
	public void onReceive(Intent intent){
		//经纬度返回且当非游客状态时才取数据		
		if (TypeKey.getInstance().PACKAGE_GPS.equals(intent.getAction())) {
			Bundle bundle = intent.getExtras();
			LocationBean mLocation = (LocationBean) bundle.getSerializable("location");
			if (mLocation == null) {
				return;
			}
			if (mOnLocationListListener.size() > 0) {
				for (int i = 0; i < mOnLocationListListener.size(); i++) {
					mOnLocationListListener.get(i).onLocation(mLocation);
				}
			}else {
				return;
			}
			if (isUserStatus) {
				LatLng nowLatLng = GPSUtil.getInstances().toBdLatlng(new LatLng(mLocation.Latitude, mLocation.Longitude));
				if (mLastLatLng != null) {
					if (DistanceUtil.getDistance(nowLatLng, mLastLatLng) > 100) {
						//获取当前地图的中心点，然后去解析地理位置MapStatus
						PoiSearchGetUtil.getInstances().toGeoCoder(nowLatLng, SimSignalHandler.this);
					}
				}else {
					//获取当前地图的中心点，然后去解析地理位置MapStatus
					PoiSearchGetUtil.getInstances().toGeoCoder(nowLatLng, SimSignalHandler.this);
				}
				mLastLatLng = nowLatLng;
			}
		}else if (mListenerSignalList.size() <= 0) {
			return;
		}
		//卡1返回
		if (ServiceSignalSim1.ACTION_SIGNAL_SIM1.equals(intent.getAction())) {
			onSignalInit(intent, 0);
		//卡2返回
		}else if (ServiceSignalSim2.ACTION_SIGNAL_SIM2.equals(intent.getAction())) {
			onSignalInit(intent, 1);
		}
	}
	
	private void onSignalInit(Intent intent,int position){
		mNetworkType = intent.getIntExtra("NetworkType", CellUtil.NETWORK_UNKNOW);
		boolean isTestSim = intent.getBooleanExtra("isTestSim", false);
		if (mNetworkType == CellUtil.NETWORK_LTE) {
			LTECell mLteCell = (LTECell) intent.getSerializableExtra("LTECell");
			if (mLteCell == null){
    			onListenerBack(CellUtil.NETWORK_UNKNOW, CellUtil.NETWORK_UNKNOW, position, isTestSim);
                return;
            }
			mLteCell.band1  = BandUtil.getInstance().getBand(mLteCell.band);
			if (isUserStatus && NetWorkStateUtil.isCmccNet(mLteCell.getMnc()) <= 0) {
				mLteCell = toLteCellName(mLteCell);
				if (TextUtils.isEmpty(mLteCell.name)) {
					readCellName(null, mLteCell, null);
				}
			}
			Serializable listLte = intent.getSerializableExtra("listLTE");
            Serializable listTd = intent.getSerializableExtra("listTD");
            Serializable listGsm = intent.getSerializableExtra("listGSM");
            if(listLte != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_LTE, position, isTestSim, mLteCell, listLte);
            }else if(listTd != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_TD, position, isTestSim,mLteCell, listTd);
            }else if(listGsm != null){
    			onListenerBack(mNetworkType,CellUtil.NETWORK_GSM, position, isTestSim, mLteCell,listGsm);
            }else{
    			onListenerBack(mNetworkType, CellUtil.NETWORK_UNKNOW, position, isTestSim,mLteCell);
            }
		}else if (mNetworkType == CellUtil.NETWORK_TD) {
			TDCell mTDCell = (TDCell) intent.getSerializableExtra("TDCell");
			if (mTDCell == null){
    			onListenerBack(CellUtil.NETWORK_UNKNOW, CellUtil.NETWORK_UNKNOW, position, isTestSim);
                return;
            }
			Serializable listTd = intent.getSerializableExtra("listTD");
            Serializable listGsm = intent.getSerializableExtra("listGSM");
            if(listTd != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_TD, position, isTestSim,mTDCell, listTd);
            }else if(listGsm != null){
    			onListenerBack(mNetworkType,CellUtil.NETWORK_GSM, position, isTestSim, mTDCell,listGsm);
            }else{
    			onListenerBack(mNetworkType, CellUtil.NETWORK_UNKNOW, position, isTestSim,mTDCell);
            }
		}else if (mNetworkType == CellUtil.NETWORK_GSM) {
			GSMCell mGSMCell = (GSMCell) intent.getSerializableExtra("GSMCell");
			if (mGSMCell == null){
    			onListenerBack(CellUtil.NETWORK_UNKNOW, CellUtil.NETWORK_UNKNOW, position, isTestSim);
                return;
            }
			if (isUserStatus && NetWorkStateUtil.isCmccNet(mGSMCell.getMnc()) <= 0) {
				mGSMCell = toGsmCellName(mGSMCell);
				if (TextUtils.isEmpty(mGSMCell.name)) {
					readCellName(null, null, mGSMCell);
				}
			}
			Serializable listGsm = intent.getSerializableExtra("listGSM");
            if(listGsm != null){
    			onListenerBack(mNetworkType,CellUtil.NETWORK_GSM, position, isTestSim, mGSMCell,listGsm);
            }else{
    			onListenerBack(mNetworkType, CellUtil.NETWORK_UNKNOW, position, isTestSim,mGSMCell);
            }
		}else if (mNetworkType == CellUtil.NETWORK_NR) {
			NrCell mNrCell = (NrCell) intent.getSerializableExtra("NRCell");
			LTECell mLteCell = (LTECell) intent.getSerializableExtra("LTECell");
            if (mNrCell == null && mLteCell == null){
    			onListenerBack(CellUtil.NETWORK_UNKNOW, CellUtil.NETWORK_UNKNOW, position, isTestSim);
                return;
            }
            
            if(isUserStatus && mNrCell != null){
            	mNrCell = toNrCellName(mNrCell);
				if (TextUtils.isEmpty(mNrCell.name)) {
					readCellName(mNrCell, null, null);
				}
            }
            
			if(mLteCell != null){
				mLteCell.band1  = BandUtil.getInstance().getBand(mLteCell.band);
				if (isUserStatus && NetWorkStateUtil.isCmccNet(mLteCell.getMnc()) <= 0) {
					mLteCell = toLteCellName(mLteCell);
					if (TextUtils.isEmpty(mLteCell.name)) {
						readCellName(null, mLteCell, null);
					}
				}
			}
            
            Serializable listNr = intent.getSerializableExtra("listNR");
            Serializable listLte = intent.getSerializableExtra("listLTE");
            Serializable listTd = intent.getSerializableExtra("listTD");
            Serializable listGsm = intent.getSerializableExtra("listGSM");
            if (listNr != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_NR, position, isTestSim, mNrCell, mLteCell, listNr);
            }else if(listLte != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_LTE, position, isTestSim, mNrCell, mLteCell, listLte);
            }else if(listTd != null){
            	onListenerBack(mNetworkType,CellUtil.NETWORK_TD, position, isTestSim,mNrCell, mLteCell, listTd);
            }else if(listGsm != null){
    			onListenerBack(mNetworkType,CellUtil.NETWORK_GSM, position, isTestSim, mNrCell, mLteCell,listGsm);
            }else{
    			onListenerBack(mNetworkType, CellUtil.NETWORK_UNKNOW, position, isTestSim,mNrCell, mLteCell);
            }
		}else {
			onListenerBack(CellUtil.NETWORK_UNKNOW, CellUtil.NETWORK_UNKNOW, position, isTestSim);
		}
	}
	
	/**
	 * 判断是否已获取基站名称
	 * @param cell
	 * @return
	 */
	private NrCell toNrCellName(NrCell cell){
		if (mNearestNrCell != null) {
			if (mNearestNrCell.pci == UtilHandler.getInstance().toInt(cell.getPci(), 0)) {
				cell.name = mNearestNrCell.name;
				return cell;
			}
		}
		return cell;
	}
	
	/**
	 * 判断是否已获取基站名称
	 * @param cell
	 * @return
	 */
	private LTECell toLteCellName(LTECell cell){
		if (mListLteName.size() > 0) {
			for (LTECell item : mListLteName) {
				if (item.ci == cell.ci && item.ci != Integer.MAX_VALUE) {
					cell.name = item.name;
					cell.net = item.net;
					cell.band = item.band;
					cell.band1 = item.band1;
					cell.pci = item.pci;
					cell.asu = item.asu;
					cell.db_id  = item.db_id;
					cell.db_lat = item.db_lat;
					cell.db_lon = item.db_lon;
					cell.db_direction = item.db_direction;
					cell.db_keyId = item.db_keyId;
					cell.db_type = item.db_type;
					return cell;
				}
			}
		}
		return cell;
	}
	
	/**
	 * 判断是否已获取基站名称
	 * @param cell
	 * @return
	 */
	private GSMCell toGsmCellName(GSMCell cell){
		if (mListGsmName.size() > 0) {
			for (GSMCell item : mListGsmName) {
				if (item.lac == cell.lac && item.cid == cell.cid && item.lac != Integer.MAX_VALUE && item.cid != Integer.MAX_VALUE) {
					cell.name = item.name;
					cell.net = item.net;
					cell.asu = item.asu;
					cell.db_id  = item.db_id;
					cell.db_lat = item.db_lat;
					cell.db_lon = item.db_lon;
					cell.db_direction = item.db_direction;
					cell.db_keyId = item.db_keyId;
					cell.db_type = item.db_type;
					return cell;
				}
			}
		}
		return cell;
	}
	
//	public ReadCellRunnable nrReadCellRunnable, lteReadCellRunnable,gsmReadCellRunnable;
	/**
	 * 获取对应基站的名称
	 * @param lteCell
	 * @param gsmCell
	 */
	private void readCellName(NrCell nrCell, LTECell lteCell, GSMCell gsmCell){
		if (mOnLocationListListener == null || mOnLocationListListener.size() <= 0) {
			return;
		}
//		if (nrCell != null){
//			if (nrReadCellRunnable == null || nrReadCellRunnable.isFish()) {
//				nrReadCellRunnable = new ReadCellRunnable(nrCell, null, null, null, mHandler);
//				new Thread(nrReadCellRunnable).start();
//			}
//		}else if (lteCell != null) {
//			if (lteReadCellRunnable == null || lteReadCellRunnable.isFish()) {
//				lteReadCellRunnable = new ReadCellRunnable(null, lteCell, null, null, mHandler);
//				new Thread(lteReadCellRunnable).start();
//			}
//		}else if (gsmCell != null) {
//			if (gsmReadCellRunnable == null || gsmReadCellRunnable.isFish()) {
//				gsmReadCellRunnable = new ReadCellRunnable(null, null, null, gsmCell, mHandler);
//				new Thread(gsmReadCellRunnable).start();
//			}
//		}
	}
	
	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		//经纬度定位解析地址返回
		if (type == EnumRequest.OTHER_GEO_SEARCH.toInt()) {
			if (object != null && object instanceof ReverseGeoCodeResult) {
				AddressComponent address = ((ReverseGeoCodeResult) object).getAddressDetail();
				String nowCity = address.city;
				//当前位置省份
				String province = address.province;
				//当前位置区县名称
				String distrct = address.district;
				if(nowCity.length() < 1 || distrct.length() < 1 || province.length() < 1) {
					return;
				}
				province = province.substring(0, province.length()-1);
				nowCity = nowCity.substring(0, nowCity.length()-1);
				distrct = distrct.substring(0, distrct.length()-1);
				if (mOnLocationListListener.size() > 0) {
					for (int i = 0; i < mOnLocationListListener.size(); i++) {
						mOnLocationListListener.get(i).onAnalysis(100, province, nowCity, distrct, address.province, address.city, address.district);
					}
				}
//				new Thread(new Wq_AddressRunnable(mHandler, province, nowCity, distrct, 101)).start();
			}
		}
	}
	
	/**
	 * 信号回调
	 * @param netType 主网类型
	 * @param adjType 邻区类型
	 * @param simPosition 卡1=0 ，卡2=1
	 * @param isTestSim 是否是默认卡
	 * @param obj [0]主体 [1]邻区
	 */
	private void onListenerBack(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj){
		if (mListenerSignalList.size() > 0) {
			for (int i = 0; i < mListenerSignalList.size(); i++) {
				mListenerSignalList.get(i).onSignalListener(netType,adjType, simPosition, isTestSim, obj);
			}
		}
	}
	
//	private Wq_FileRunnable fileRunnable = null;
//	/**
//	 * 读取文件并解析
//	 * @param path
//	 */
//	private void readFile(String path){
//		if (fileRunnable == null || !fileRunnable.isRuning) {
//			File file = new File(SharedPreHandler.getShared(MyApplication.getContext()).getStringShared(TypeKey.getInstance().CODE_PATH(), ""));
//			if (!file.exists()) {
//				fileRunnable = new Wq_FileRunnable(mHandler, path, 102);
//				new Thread(fileRunnable).start();
//			}
//		}
//	}
	
	/**
	 * 添加回调事件
	 * @param listenerBack
	 */
	public void addListener(OnChangSignalListener listenerBack,OnLocationListener locationListener){
		isUserStatus = !"00".equals(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), ""));
		boolean isLocationExit = false;
		if (mOnLocationListListener.size() > 0 && locationListener != null) {
			for (OnLocationListener item : mOnLocationListListener) {
				if (item.hashCode() == locationListener.hashCode()) {
					isLocationExit = true;
				}
			}
		}
		if (!isLocationExit && locationListener != null) {
			mOnLocationListListener.add(locationListener);
		}
		
		if (mListenerSignalList.size() > 0 && listenerBack != null) {
			for (OnChangSignalListener item : mListenerSignalList) {
				if (item.hashCode() == listenerBack.hashCode()) {
					return;
				}
			}
		}
		if (listenerBack != null) {
			mListenerSignalList.add(listenerBack);
		}
		
		if (mOnLocationListListener.size() == 1) {
			MyLocationListener.getInstances().setTime(1000);
		}
	}
	
	/**
	 * 移除回调事件
	 * @param listenerBack
	 */
	public void removeListener(OnChangSignalListener listenerBack,OnLocationListener locationListener){
		removeListener(listenerBack, locationListener, true);
	}
	
	/**
	 * 移除回调事件
	 * @param listenerBack
	 */
	public void removeListener(OnChangSignalListener listenerBack,OnLocationListener locationListener,boolean isDeleteDb){
		if (mListenerSignalList.size() > 0 && listenerBack != null) {
			for (int i = 0; i < mListenerSignalList.size(); i++) {
				if (mListenerSignalList.get(i).hashCode() == listenerBack.hashCode()) {
					mListenerSignalList.remove(i);
					break;
				}
			}
		}
		
		if (mOnLocationListListener.size() > 0 && locationListener != null) {
			for (int i = 0; i < mOnLocationListListener.size(); i++) {
				if (mOnLocationListListener.get(i).hashCode() == locationListener.hashCode()) {
					mOnLocationListListener.remove(i);
					break;
				}
			}
		}
		
		if (mOnLocationListListener.size() <= 0) {
			try {
				MyLocationListener.getInstances().setTime(5000);
				mLastLatLng = null;
//				if (lteReadCellRunnable != null) {
//					lteReadCellRunnable.close();
//				}
//				if (gsmReadCellRunnable != null) {
//					gsmReadCellRunnable.close();
//				}
//				if (fileRunnable != null) {
//					fileRunnable.mHandler = null;
//				}
//				if (isDeleteDb) {
//					// 删除缓存
//					new DeleteDbFileThread().start();
//				}
			} catch (Exception e) {
			}
		}
	}
	
	public ListenerBack getListenerBack(){
		return this;
	}
}
