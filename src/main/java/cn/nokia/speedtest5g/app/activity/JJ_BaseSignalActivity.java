package cn.nokia.speedtest5g.app.activity;

import android.text.TextUtils;

import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.model.LatLng;
import com.fjmcc.wangyoubao.app.signal.OnChangSignalListener;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;
import com.fjmcc.wangyoubao.app.signal.bean.TDCell;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.cast.SimSignalHandler;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.util.gps.OnLocationListener;

/**
 * 信号回调基类
 * @author xujianjun
 *
 */
public abstract class JJ_BaseSignalActivity extends BaseFtpActivity implements OnLocationListener,OnChangSignalListener{

	protected LatLng gpsLatlng;

	protected final String FTP_TYPE_ALL = "ALL";
	protected final String FTP_TYPE_DOWNLOAD = "DOWNLOAD";
	protected final String FTP_TYPE_UPLOAD = "UPLOAD";
	protected int mLogId = 0;
	protected float mAllDownload = 0; //下载总流量
	protected float mAllUpload = 0; //上传总流量
	protected PingInfo mPingInfo = null;
	protected int mPingCount = 3; //ping重试次数
	protected int mCurrentPingCount = 0;

	//是否是回放
	private boolean isFallback = false;

	public abstract void onSignalReceiver(Signal signal);

	public boolean isClearCache = true;

	private String appVersionCode;

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		appVersionCode = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().VERSION_CODE, "");
		initSignal();
	}


	public void initSignal(){
		hideFallback();
	}

	private LocationBean mLocationBean;
	//经纬度回调
	@Override
	public void onLocation(LocationBean location) {
		mLocationBean = location;
		if (location != null) {
			gpsLatlng = new LatLng(location.Latitude, location.Longitude);
		}
	}

	private Signal mSignal;
	@Override
	public void onSignalListener(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj) {
		if (!isTestSim) {
			return;
		}
		switch (netType) {
		case CellUtil.NETWORK_NR:
			initNewSignal();
			NrCell mNrCell = (NrCell) obj[0];
			if (mNrCell != null) {
				mSignal.setTypeNet("NR");
				mSignal.setMcc(mNrCell.getMcc());
				mSignal.setMnc(mNrCell.getMnc());
				mSignal.lte_pci_nr = mNrCell.getPci();
				mSignal.lte_rsrp_nr = mNrCell.getRsrp();
				mSignal.lte_rsrq_nr = mNrCell.getRsrq();
				mSignal.lte_sinr_nr = mNrCell.getSinr();
				mSignal.lte_rssi_nr = mNrCell.getRssi();
				mSignal.lte_frequency_nr = mNrCell.getfrequency();
				mSignal.lte_earfcn_nr = mNrCell.earfcn;
				mSignal.lte_band_nr = mNrCell.band;
				mSignal.lte_name_nr = mNrCell.name;
                mSignal.lte_gnb_nr = mNrCell.getGnb();
                mSignal.lte_cellid_nr = mNrCell.getCellid();
                mSignal.lte_tac_nr = mNrCell.getTac();
                mSignal.lte_nci_nr = mNrCell.getNci();
			}
			LTECell mLTECellMd = (LTECell) obj[1];
			if (mLTECellMd != null) {
				if (!TextUtils.isEmpty(mLTECellMd.getCi())) {
					mSignal.setTypeNet("NR");
					mSignal.setMcc(mLTECellMd.getMcc());
					mSignal.setMnc(mLTECellMd.getMnc());
					mSignal.setLte_cgi(mLTECellMd.getCi());
					mSignal.setLte_pci(mLTECellMd.getPci());
					mSignal.setLte_rsrp(mLTECellMd.getRsrp());
					mSignal.setLte_rsrq(mLTECellMd.getRsrq());
					mSignal.setLte_sinr(mLTECellMd.getSinr());
					mSignal.setLte_tac(mLTECellMd.getTac());
					mSignal.setLte_enb(mLTECellMd.getEnb());
					mSignal.setLte_cid(mLTECellMd.getCellid());
					mSignal.lte_pd = mLTECellMd.band1;
					mSignal.setLte_name(mLTECellMd.name);
					mSignal.setLte_band(mLTECellMd.band);
					mSignal.setLte_pci(mLTECellMd.getPci());
				}
			}
			if (mSignal == null) {
				return;
			}
			break;
		case CellUtil.NETWORK_LTE:
			LTECell mLTECell = (LTECell) obj[0];
			if (mLTECell != null) {
				if (mLTECell.enb * 256 + mLTECell.cellid < 0) {
					return;
				}
				initNewSignal();
				mSignal.setTypeNet("LTE");
				mSignal.setMcc(mLTECell.getMcc());
				mSignal.setMnc(mLTECell.getMnc());
				mSignal.setLte_cgi(mLTECell.getCi());
				mSignal.setLte_pci(mLTECell.getPci());
				mSignal.setLte_rsrp(mLTECell.getRsrp());
				mSignal.setLte_rsrq(mLTECell.getRsrq());
				mSignal.setLte_sinr(mLTECell.getSinr());
				mSignal.setLte_tac(mLTECell.getTac());
				mSignal.setLte_enb(mLTECell.getEnb());
				mSignal.setLte_cid(mLTECell.getCellid());
				mSignal.lte_pd = mLTECell.band1;
				mSignal.setLte_name(mLTECell.name);
				mSignal.setLte_band(mLTECell.band);
				mSignal.setLte_pci(mLTECell.getPci());
			}
			break;
		case CellUtil.NETWORK_TD:
			TDCell mTDCell = (TDCell) obj[0];
			if (mTDCell != null) {
				initNewSignal();
				mSignal.setTypeNet("TD");
				mSignal.setMcc(mTDCell.getMcc());
				mSignal.setMnc(mTDCell.getMnc());
				mSignal.setTd_ci(mTDCell.getCi());
				mSignal.setTd_lac(mTDCell.getLac());
				mSignal.setTd_pccpch(mTDCell.getSign());
			}
			break;
		case CellUtil.NETWORK_GSM:
			GSMCell mGSMCell = (GSMCell) obj[0];
			if (mGSMCell != null) {
				initNewSignal();
				mSignal.setTypeNet("GSM");
				mSignal.setMcc(mGSMCell.getMcc());
				mSignal.setMnc(mGSMCell.getMnc());
				mSignal.setGsm_cid(mGSMCell.getCid());
				mSignal.setGsm_lac(mGSMCell.getLac());
				mSignal.setGsm_rxl(mGSMCell.getSign());
				if (mGSMCell.bcch > -1) {
					mSignal.setTd_pccpch(mGSMCell.getBcch());// 借用TD_pccpch字段用作
				}
				mSignal.setGsm_name(mGSMCell.name);
			}
			break;
		default:
			mSignal = null;
			break;
		}

		if (!isFallback) {
			onSignalReceiver(mSignal);
		}
	}

	@Override
	public void onAnalysis(int status,Object... obj) {

	}

	private void initNewSignal(){
		mSignal = new Signal();
		mSignal.setPhoneDes3(getUserPhoneDes());
		mSignal.setUserId(getUserID());
		mSignal.setNumberCode(appVersionCode);
		if (mLocationBean != null) {
			mSignal.setLat(String.valueOf(mLocationBean.Latitude));
			mSignal.setLon(String.valueOf(mLocationBean.Longitude));
			mSignal.setTypeGps(mLocationBean.Provider);
		}
	}

	/**
	 * 显示回访记录信号
	 */
	public void showFallback(){
		this.isFallback = true;
		SimSignalHandler.getInstances().removeListener(this, this);
	}

	/**
	 * 隐藏回放记录---显示正常信号
	 */
	public void hideFallback(){
		this.isFallback = false;
		SimSignalHandler.getInstances().addListener(this, this);
	}

	public boolean isFallback() {
		return isFallback;
	}

	@Override
	protected void onDestroy() {
		SimSignalHandler.getInstances().removeListener(this, this, isClearCache);
		super.onDestroy();
	}
}
