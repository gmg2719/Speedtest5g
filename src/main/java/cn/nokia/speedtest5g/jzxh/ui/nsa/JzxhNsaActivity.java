package cn.nokia.speedtest5g.jzxh.ui.nsa;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.MarqueesTextView;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;
import com.fjmcc.wangyoubao.app.signal.OnChangSignalListener;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarFragmentActivity;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.cast.SimSignalHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.gis.model.ColorFragmentAdapter;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.util.gps.OnLocationListener;

/**
 * 基站信号nsa展示页
 * @author JQJ
 *
 */
@SuppressWarnings("unchecked")
public class JzxhNsaActivity extends BaseActionBarFragmentActivity implements ViewPager.OnPageChangeListener, OnLocationListener, OnChangSignalListener{

	//定位类型，MCC,MNC,信号制式，经度，纬度，时速，上传，下载
	private TextView mTvLocType,mTvMcc,mTvMnc,mTvNet,mTvLon,mTvLat,mTvSpeed,mTvUl,mTvDl;
	//基站名称
	private MarqueesTextView mTvCellName = null;
	//LTE-ENB,LTE-CELLID,LTE-PCI,LTE-BAND,LTE-频点，LTE-TAC,LTE-RSRP,LTE-RSRQ,LTE-SINR
	private TextView mTvLteEnb,mTvLteCellid,mTvLtePci,mTvLteBand,mTvLtePd,mTvLteTac,mTvLteRsrp,mTvLteRsrq,mTvLteSinr;
	//信号类型
	private ImageView mIvCellType = null;

	private TextView mNsaTvNet,mNsaTvPci,mNsaTvFrequency,mNsaTvRsrp,mNsaTvRsrq,mNsaTvSinr,mNsaTvEarfcn,mNsaTvBand;
	private TextView mNsaTvCellName = null;

	private ViewPager mViewPager = null;

	private LinearLayout mLayoutPage = null;
	//适配器
	private ColorFragmentAdapter mAdapter = null;

	private HashMap<Integer, Fragment> mHashMapFragment = null;

	//上一次上传，下载，时间
	private long mLastDl,mLastUl,mLastTimeMillis;
	private double mSpeedDl,mSpeedUl;
	private int mLastY = 0,mLastPosition = 0;
	//时速
	private double mLastSpeed;
	private int mLastAdjType;
	private Signal mSignalNow = null;
	private LocationBean mLastLocationBean = null;

	//当前测试卡槽
	private int mSimPosition = 0;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (isFinishing()) {
				return true;
			}
			if (msg.obj instanceof MyEvents) {
				MyEvents events = (MyEvents) msg.obj;
				if (events.getType() == EnumRequest.OTHER_LINQU.toInt()) {
					((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((ArrayList<Object>) events.getObject());
				}
			}
			return true;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_jzxh_nsa_activity);
		mSimPosition = getIntent().getIntExtra("position", 0);
		init("NSA网络信息", true);
		SimSignalHandler.getInstances().addListener(this, this);

		//		JzxhNsaDownloadModule.getInstances(this).startDownload();
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		mViewPager	  = (ViewPager) findViewById(R.id.jzxh_nsa_viewPager);
		mLayoutPage   = (LinearLayout) findViewById(R.id.jzxh_nsa_layout_page);
		mTvCellName   = (MarqueesTextView) findViewById(R.id.jzxh_nsa_tv_cellname);
		mNsaTvCellName= (MarqueesTextView) findViewById(R.id.jzxh_nsa_tv_nr_cellname);
		mTvMcc		  = (TextView) findViewById(R.id.jzxh_nsa_tv_mcc);
		mTvMnc		  = (TextView) findViewById(R.id.jzxh_nsa_tv_mnc);
		mTvNet		  = (TextView) findViewById(R.id.jzxh_nsa_tv_net);
		mTvLon		  = (TextView) findViewById(R.id.jzxh_nsa_tv_lon);
		mTvLat		  = (TextView) findViewById(R.id.jzxh_nsa_tv_lat);
		mTvSpeed	  = (TextView) findViewById(R.id.jzxh_nsa_tv_speed);
		mTvLocType    = (TextView) findViewById(R.id.jzxh_nsa_tv_locationType);
		mTvUl		  = (TextView) findViewById(R.id.jzxh_nsa_tv_ul);
		mTvDl		  = (TextView) findViewById(R.id.jzxh_nsa_tv_dl);
		mTvLteEnb 	  = (TextView) findViewById(R.id.jzxh_nsa_tv_lteEnb);
		mTvLteCellid  = (TextView) findViewById(R.id.jzxh_nsa_tv_lteCellid);
		mTvLtePci 	  = (TextView) findViewById(R.id.jzxh_nsa_tv_ltePci);
		mTvLteBand 	  = (TextView) findViewById(R.id.jzxh_nsa_tv_lteBand);
		mTvLtePd 	  = (TextView) findViewById(R.id.jzxh_nsa_tv_ltePd);
		mTvLteTac 	  = (TextView) findViewById(R.id.jzxh_nsa_tv_lteTac);
		mTvLteRsrp    = (TextView) findViewById(R.id.jzxh_nsa_tv_lteRsrp);
		mTvLteRsrq    = (TextView) findViewById(R.id.jzxh_nsa_tv_lteRsrq);
		mTvLteSinr    = (TextView) findViewById(R.id.jzxh_nsa_tv_lteSinr);
		mIvCellType	  = (ImageView) findViewById(R.id.jzxh_nsa_iv_cellType);

		mNsaTvNet	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_net);
		mNsaTvPci	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_pci);
		mNsaTvFrequency	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_frequency);
		mNsaTvRsrp	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_rsrp);
		mNsaTvRsrq	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_rsrq);
		mNsaTvSinr	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_sinr);
		mNsaTvEarfcn  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_earfcn);
		mNsaTvBand	  = (TextView) findViewById(R.id.jzxh_nsa_nr_tv_band);

		mHashMapFragment = new HashMap<>();
		mHashMapFragment.put(0, new JzxhNsaChartFrament());
		mHashMapFragment.put(1, new JzxhNsaListFrament());
		mAdapter = new ColorFragmentAdapter(getSupportFragmentManager(), mHashMapFragment);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(mHashMapFragment.size());
		mViewPager.addOnPageChangeListener(this);
		int dp3 = UtilHandler.getInstance().dpTopx(3);
		for (int i = 0; i < mHashMapFragment.size(); i++) {
			ImageView iv = new ImageView(this);
			iv.setPadding(dp3, 0, dp3,0);
			if (mLayoutPage.getChildCount() <= 0) {
				iv.setImageResource(R.drawable.icon_cursor_select);
			}else {
				iv.setImageResource(R.drawable.icon_cursor_un);
			}
			mLayoutPage.addView(iv);
		}
	}

	/**
	 * 经纬度返回
	 */
	@Override
	public void onLocation(LocationBean location) {
		String mLocType = "";
		if (location.Provider.equals("gps")) {
			mLocType = "GPS定位";
			mLastSpeed = UtilHandler.getInstance().toDfSum(location.speed, "00");
			mTvSpeed.setText(mLastSpeed + "km/h");
		} else if (location.Provider.equals("network")) {
			mLastSpeed = 0;
			mLocType = "网络定位";
			mTvSpeed.setText("--");
		}
		mTvLocType.setText(mLocType);
		mTvLon.setText(String.valueOf(UtilHandler.getInstance().toDfLl(location.Longitude)));
		mTvLat.setText(String.valueOf(UtilHandler.getInstance().toDfLl(location.Latitude)));
		mLastLocationBean = location;
		updateSl();
	}

	//获取当前网络速度
	private void updateSl(){
		long timeMillis = System.currentTimeMillis();
		//下载
		long dl = TrafficStats.getTotalRxBytes();
		//上传
		long ul = TrafficStats.getTotalTxBytes();
		if (mLastTimeMillis != 0) {
			long toDl = dl - mLastDl;
			long toUl = ul - mLastUl;
			mSpeedDl = (toDl * 1000) / (timeMillis - mLastTimeMillis)/1024D;//毫秒转换
			mSpeedUl = (toUl * 1000) / (timeMillis - mLastTimeMillis)/1024D;//毫秒转换
			mTvDl.setText(UtilHandler.getInstance().toDfSum(mSpeedDl, "00") + "kb/s");
			mTvUl.setText(UtilHandler.getInstance().toDfSum(mSpeedUl, "00") + "kb/s");
		}else {
			mSpeedUl = 0;
			mSpeedDl = 0;
			mTvDl.setText("--");
			mTvUl.setText("--");
		}
		mLastTimeMillis = timeMillis;
		mLastDl = dl;
		mLastUl = ul;
	}

	/**
	 * 锚点
	 * @param lteCell
	 */
	private void setMdLte(LTECell lteCell){
		if(lteCell != null){
			mTvMcc.setText(lteCell.getMcc());
			mTvMnc.setText(lteCell.getMnc());
			setCellType(lteCell.getMnc(), mIvCellType);
			if (!mTvCellName.getText().toString().equals(lteCell.name)) {
				mTvCellName.setText(lteCell.name);
			}
			mTvLteBand.setText(lteCell.band1);
			mTvLtePd.setText(lteCell.band);
			int pci = UtilHandler.getInstance().toInt(lteCell.getPci(), Integer.MAX_VALUE);
			if (pci != Integer.MAX_VALUE) {
				switch (pci % 3) {
				case 0:
					mTvLtePci.setText(lteCell.getPci() + "⓿");
					break;
				case 1:
					mTvLtePci.setText(lteCell.getPci() + "❶");
					break;
				case 2:
					mTvLtePci.setText(lteCell.getPci() + "❷");
					break;
				default:
					mTvLtePci.setText(lteCell.getPci());
					break;
				}
			}else {
				mTvLtePci.setText(lteCell.getPci());
			}
			mTvLteTac.setText(lteCell.getTac());
			mTvLteEnb.setText(lteCell.getEnb());
			mTvLteCellid.setText(lteCell.getCellid());

			mTvLteRsrp.setText(lteCell.getRsrp() + " dBm");
			mTvLteRsrq.setText(lteCell.getRsrq() + " db");
			mTvLteSinr.setText(lteCell.getSinr());
			mTvNet.setText(lteCell.net);
		}
	}

	/**
	 * NR站
	 * @param nrCell
	 */
	private void setNr(NrCell nrCell) {
		if (nrCell != null) {
			mNsaTvNet.setText("NR");
			mNsaTvPci.setText(nrCell.getPci());
			mNsaTvFrequency.setText(nrCell.getfrequency()); //频率
			mNsaTvRsrp.setText(nrCell.getRsrp() + " dBm");
			mNsaTvRsrq.setText(nrCell.getRsrq() + " db");
			mNsaTvSinr.setText(nrCell.getSinr());
			mNsaTvEarfcn.setText(nrCell.earfcn); //频点
			mNsaTvBand.setText(nrCell.band); //频段
			if(TextUtils.isEmpty(nrCell.name)){
				mNsaTvCellName.setText("");
			}else{
				mNsaTvCellName.setText(nrCell.name + "(模糊匹配)");
			}
		}else{
			mNsaTvNet.setText("--");
			mNsaTvPci.setText("--");
			mNsaTvFrequency.setText("--");
			mNsaTvRsrp.setText("--");
			mNsaTvRsrq.setText("--");
			mNsaTvSinr.setText("--");
			mNsaTvEarfcn.setText("--");
			mNsaTvBand.setText("--");
		}
	}

	/**
	 * 设置曲线数据
	 * @param lteCell
	 * @param nrCell
	 */
	private void setChartData(NrCell nrCell, LTECell lteCell){
		mSignalNow = new Signal();
		if (mLastLocationBean != null) {
			mSignalNow.setLon(mTvLon.getText().toString());
			mSignalNow.setLat(mTvLat.getText().toString());
			mSignalNow.setTypeGps(mLastLocationBean.Provider);
			if (4.9E-324 != mLastLocationBean.Altitude) {
				mSignalNow.altitude = String.valueOf(mLastLocationBean.Altitude);
			}
		}

		mSignalNow.setUl(String.valueOf(mSpeedUl));
		mSignalNow.setDl(String.valueOf(mSpeedDl));
		mSignalNow.setSpeed(String.valueOf(mLastSpeed));
		mSignalNow.setDUAL(mTvNet.getText().toString());

		if(nrCell != null && lteCell != null){
			mSignalNow.setTypeNet("NR");
		}else if(nrCell == null && lteCell != null){
			mSignalNow.setTypeNet("LTE");
		}else{
			mSignalNow.setTypeNet("NR");
		}

		if (nrCell != null) {
			mSignalNow.lte_pci_nr = nrCell.getPci();
			mSignalNow.lte_rsrp_nr = nrCell.getRsrp();
			mSignalNow.lte_rsrq_nr = nrCell.getRsrq();
			mSignalNow.lte_sinr_nr = nrCell.getSinr();
			mSignalNow.lte_earfcn_nr = nrCell.earfcn;
			mSignalNow.lte_band_nr = nrCell.band;
			mSignalNow.setTime(TimeUtil.getInstance().getNowTimeSS(nrCell.time));
			mSignalNow.timeHH = TimeUtil.getInstance().getTimeH(nrCell.time);
		}

		if (lteCell != null) {
			mSignalNow.setMcc(lteCell.getMcc());
			mSignalNow.setMnc(lteCell.getMnc());
			mSignalNow.setLte_band(lteCell.band1);
			mSignalNow.lte_pd = lteCell.band;
			mSignalNow.setLte_cgi(lteCell.getCi());
			mSignalNow.setLte_cid(lteCell.getCellid());
			mSignalNow.setLte_enb(lteCell.getEnb());
			mSignalNow.setLte_name(lteCell.name);
			mSignalNow.setLte_pci(lteCell.getPci());
			mSignalNow.setLte_rsrp(lteCell.getRsrp());
			mSignalNow.setLte_rsrq(lteCell.getRsrq());
			mSignalNow.setLte_sinr(lteCell.getSinr());
			mSignalNow.setLte_tac(lteCell.getTac());
			mSignalNow.setTime(TimeUtil.getInstance().getNowTimeSS(lteCell.time));
			mSignalNow.timeHH = TimeUtil.getInstance().getTimeH(lteCell.time);
			mSignalNow.DB_ID = lteCell.asu;
		}

		((JzxhNsaChartFrament)mHashMapFragment.get(0)).addSignalItem(mSignalNow);
		((JzxhNsaListFrament)mHashMapFragment.get(1)).addSignalItem(mSignalNow);
	}

	@Override
	public void onPageSelected(int position) {
		if (mHashMapFragment != null && mHashMapFragment.get(0) != null) {
			if (mLastPosition == 0) {
				mLastY = ((JzxhNsaChartFrament)mHashMapFragment.get(0)).mScrollView.getScrollY();
			}
			if (position == 0) {
				((JzxhNsaChartFrament)mHashMapFragment.get(0)).setScrollTo(666, mLastY);
			}
			mLastPosition = position;
		}
		for (int i = 0; i < mLayoutPage.getChildCount(); i++) {
			ImageView iv = (ImageView) mLayoutPage.getChildAt(i);
			if (position == i) {
				iv.setImageResource(R.drawable.icon_cursor_select);
			} else {
				iv.setImageResource(R.drawable.icon_cursor_un);
			}
		}
	}

	/**
	 * 设置接收信号运营商类型
	 */
	private void setCellType(String mnc, ImageView iv) {
		if (NetWorkStateUtil.isCmccNet(mnc) == 0) {
			iv.setImageResource(R.drawable.icon_jzxh_yd);
		} else if (NetWorkStateUtil.isCmccNet(mnc) == 1) {
			iv.setImageResource(R.drawable.icon_jzxh_dx);
		} else if (NetWorkStateUtil.isCmccNet(mnc) == 2) {
			iv.setImageResource(R.drawable.icon_jzxh_lt);
		} else {
			iv.setImageResource(R.drawable.icon_jzxh_other);
		}
	}

	/**
	 * 信号回调
	 * @param netType 主网类型
	 * @param adjType 邻区类型
	 * @param simPosition 卡1=0 ，卡2=1
	 * @param isTestSim 是否是默认卡
	 * @param obj [0]锚点 [1]NR站 [2]邻区
	 */
	@Override
	public void onSignalListener(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj) {
		if (simPosition != mSimPosition) {
			return;
		}

		try{
			if(netType == CellUtil.NETWORK_NR){
				//5G
				setMdLte((LTECell) obj[1]);
				setNr((NrCell) obj[0]);
				setChartData((NrCell) obj[0], (LTECell) obj[1]);
				if (obj.length > 2) {
					if (adjType == CellUtil.NETWORK_NR || adjType == CellUtil.NETWORK_TD || NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
					}else if (adjType == CellUtil.NETWORK_LTE) {
						if (mLastAdjType != adjType) {
							((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<LTECell>)obj[2],(NrCell) obj[0], mHandler);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[2],(NrCell) obj[0], mHandler);
					}
				}else {
					((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
			}else if(netType == CellUtil.NETWORK_LTE){
				//4G
				setMdLte((LTECell) obj[0]);
				setNr(null);
				setChartData(null, (LTECell) obj[0]);
				if (obj.length > 1) {
					if (adjType == CellUtil.NETWORK_TD || NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
					}else if (adjType == CellUtil.NETWORK_LTE) {
						if (mLastAdjType != adjType) {
							((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<LTECell>)obj[1],(LTECell) obj[0], mHandler);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[1],(LTECell) obj[0], mHandler);
					}
				}else {
					((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
			}else{
				mTvLteEnb.setText("");
				mTvLteCellid.setText("");
				mTvLtePci.setText("");
				mTvLteBand.setText("");
				mTvLtePd.setText("");
				mTvLteTac.setText("");
				mTvLteRsrp.setText("");
				mTvLteRsrq.setText("");
				mTvLteSinr.setText("");

				setCellType("-1", mIvCellType);
				mTvCellName.setText("");
				mNsaTvCellName.setText("");

				mTvMcc.setText("");
				mTvMnc.setText("");
				mTvNet.setText("");

				mNsaTvNet.setText("--");
				mNsaTvPci.setText("--");
				mNsaTvFrequency.setText("--");
				mNsaTvRsrp.setText("--");
				mNsaTvRsrq.setText("--");
				mNsaTvSinr.setText("--");
				mNsaTvEarfcn.setText("--");
				mNsaTvBand.setText("--");

				((JzxhNsaChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				((JzxhNsaChartFrament)mHashMapFragment.get(0)).addSignalItem(null);
			}
			mLastAdjType = adjType;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		//		JzxhNsaDownloadModule.getInstances(this).stopDownload();
		SimSignalHandler.getInstances().removeListener(this, this);
		super.onDestroy();
	}

	@Override
	public void onAnalysis(int status,Object... obj) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}
}
