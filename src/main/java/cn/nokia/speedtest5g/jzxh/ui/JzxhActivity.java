package cn.nokia.speedtest5g.jzxh.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.MarqueesTextView;
import com.fjmcc.wangyoubao.app.signal.MyServiceSignal;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;
import com.fjmcc.wangyoubao.app.signal.OnChangSignalListener;
import com.fjmcc.wangyoubao.app.signal.bean.CellUtil;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;
import com.fjmcc.wangyoubao.app.signal.bean.TDCell;

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
import cn.nokia.speedtest5g.gis.activity.LayerDetailActivity;
import cn.nokia.speedtest5g.gis.activity.LayerDetailGsmActivity;
import cn.nokia.speedtest5g.gis.model.ColorFragmentAdapter;
import cn.nokia.speedtest5g.gis.model.WqGisLayer;
import cn.nokia.speedtest5g.gis.model.WqGisLayerInfo;
import cn.nokia.speedtest5g.gis.util.WQ_GisDbHandler;
import cn.nokia.speedtest5g.jzxh.fragment.JzxhChartFrament;
import cn.nokia.speedtest5g.jzxh.fragment.JzxhIndexFrament;
import cn.nokia.speedtest5g.jzxh.fragment.JzxhListFrament;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.gps.LocationBean;
import cn.nokia.speedtest5g.util.gps.OnLocationListener;

/**
 * 基站信号首页
 * @author zwq
 */
@SuppressWarnings("unchecked")
public class JzxhActivity extends BaseActionBarFragmentActivity implements ViewPager.OnPageChangeListener,OnChangSignalListener,OnLocationListener{

	//定位类型，MCC,MNC,信号制式，经度，纬度，时速，上传，下载
	private TextView mTvLocType,mTvMcc,mTvMnc,mTvNet,mTvLon,mTvLat,mTvSpeed,mTvUl,mTvDl,mTvEebKey;
	//基站名称
	private MarqueesTextView mTvCellName;
	//LTE-ENB,LTE-CELLID,LTE-PCI,LTE-BAND,LTE-频点，LTE-TAC,LTE-RSRP,LTE-RSRQ,LTE-SINR
	private TextView mTvLteEnb,mTvLteCellid,mTvLtePci,mTvLteBand,mTvLtePd,mTvLteTac,mTvLteRsrp,mTvLteRsrq,mTvLteSinr;
	//GSM-LAT,GSM-CI,GSM-RXLEV
	private TextView mTvGsmLac,mTvGsmCi,mTvGsmRxlev;
	//TD-LAC,TD-CI,TD-PCCPCH
	private TextView mTvTdLac,mTvTdCi,mTvTdPccpch;
	//GSM布局，TD布局，LTE布局1(enb,cellid,pci),LTE布局2(band,频点,tac),LTE布局3(RSRP,RSRQ,SINR),基站名称布局
	private View mViewGsm,mViewTd,mViewLte1,mViewLte2,mViewLte3,mViewCellName;
	//信号类型
	private ImageView mIvCellType;

	private ViewPager mViewPager;

	private LinearLayout mLayoutPage;
	//适配器
	private ColorFragmentAdapter mAdapter;

	protected HashMap<Integer, Fragment> mHashMapFragment;
	//当前测试卡槽
	public int mSimPosition = 0;

	private String strSubSinr = null;//副卡sinr rsrp
	private String strSubRsrp = null;

	private final String ACTION_CLEAR_ALL = "cn.nokia.speedtest5g.jzxh.clear.all"; //清空所有
	private final String ACTION_CLEAR_ONE = "cn.nokia.speedtest5g.jzxh.clear.one"; //清空卡槽1
	private final String ACTION_CLEAR_TWO = "cn.nokia.speedtest5g.jzxh.clear.two"; //清空卡槽2
	private ClearListDataReceiver receiver = new ClearListDataReceiver();

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (isFinishing()) {
				return true;
			}
			if (msg.obj instanceof MyEvents) {
				MyEvents events = (MyEvents) msg.obj;
				if (events.getType() == EnumRequest.OTHER_LINQU.toInt()) {
					((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((ArrayList<Object>) events.getObject());
				}
			}
			return true;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jzxh_activity_main);
		mSimPosition = getIntent().getIntExtra("position", 0);
		init("基站信号", true);
		SimSignalHandler.getInstances().addListener(this,this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CLEAR_ALL);
		filter.addAction(ACTION_CLEAR_ONE);
		filter.addAction(ACTION_CLEAR_TWO);
		registerReceiver(receiver, filter);
	}

	//清空容器数据广播接收器
	private class ClearListDataReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_CLEAR_ALL)){
				if(mHashMapFragment != null){
					((JzxhListFrament)mHashMapFragment.get(2)).clearListData();
				}
			}else if(intent.getAction().equals(ACTION_CLEAR_ONE)){
				if(JzxhActivity.this instanceof JzxhSim1Activity){
					((JzxhListFrament)mHashMapFragment.get(2)).clearListData();
				}
			}else if(intent.getAction().equals(ACTION_CLEAR_TWO)){
				if(JzxhActivity.this instanceof JzxhSim2Activity){
					((JzxhListFrament)mHashMapFragment.get(2)).clearListData();
				}
			}
		}
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		mViewPager	  = (ViewPager) findViewById(R.id.jzxhMain_viewPager);
		mLayoutPage   = (LinearLayout) findViewById(R.id.jzxhMain_layout_page);
		mViewGsm	  = findViewById(R.id.jzxhMain_view_gsm_lcr);
		mViewTd		  = findViewById(R.id.jzxhMain_view_td_lcs);
		mViewLte1	  = findViewById(R.id.jzxhMain_view_lte_ecp);
		mViewLte2	  = findViewById(R.id.jzxhMain_view_lte_bpt);
		mViewLte3	  = findViewById(R.id.jzxhMain_view_lte_rrs);
		mTvCellName   = (MarqueesTextView) findViewById(R.id.jzxhMain_tv_cellname);
		mTvMcc		  = (TextView) findViewById(R.id.jzxhMain_tv_mcc);
		mTvMnc		  = (TextView) findViewById(R.id.jzxhMain_tv_mnc);
		mTvNet		  = (TextView) findViewById(R.id.jzxhMain_tv_net);
		mTvLon		  = (TextView) findViewById(R.id.jzxhMain_tv_lon);
		mTvLat		  = (TextView) findViewById(R.id.jzxhMain_tv_lat);
		mTvSpeed	  = (TextView) findViewById(R.id.jzxhMain_tv_speed);
		mTvLocType    = (TextView) findViewById(R.id.jzxhMain_tv_locationType);
		mTvUl		  = (TextView) findViewById(R.id.jzxhMain_tv_ul);
		mTvDl		  = (TextView) findViewById(R.id.jzxhMain_tv_dl);
		mTvLteEnb 	  = (TextView) findViewById(R.id.jzxhMain_tv_lteEnb);
		mTvLteCellid  = (TextView) findViewById(R.id.jzxhMain_tv_lteCellid);
		mTvLtePci 	  = (TextView) findViewById(R.id.jzxhMain_tv_ltePci);
		mTvLteBand 	  = (TextView) findViewById(R.id.jzxhMain_tv_lteBand);
		mTvLtePd 	  = (TextView) findViewById(R.id.jzxhMain_tv_ltePd);
		mTvLteTac 	  = (TextView) findViewById(R.id.jzxhMain_tv_lteTac);
		mTvLteRsrp    = (TextView) findViewById(R.id.jzxhMain_tv_lteRsrp);
		mTvLteRsrq    = (TextView) findViewById(R.id.jzxhMain_tv_lteRsrq);
		mTvLteSinr    = (TextView) findViewById(R.id.jzxhMain_tv_lteSinr);
		mTvGsmLac     = (TextView) findViewById(R.id.jzxhMain_tv_gsmLac);
		mTvGsmCi      = (TextView) findViewById(R.id.jzxhMain_tv_gsmCi);
		mTvGsmRxlev   = (TextView) findViewById(R.id.jzxhMain_tv_gsmRxlev);
		mTvTdLac 	  = (TextView) findViewById(R.id.jzxhMain_tv_tdLac);
		mTvTdCi 	  = (TextView) findViewById(R.id.jzxhMain_tv_tdCi);
		mTvTdPccpch	  = (TextView) findViewById(R.id.jzxhMain_tv_tdPccpch);
		mIvCellType	  = (ImageView) findViewById(R.id.jzxhMain_iv_cellType);
		mViewCellName = findViewById(R.id.jzxhMain_view_cellName);
		mTvEebKey	  = (TextView) findViewById(R.id.jzxhMain_tv_enbKey);

		if (MyServiceSignal.isMoreSim(this)) {
			mViewCellName.setVisibility(View.GONE);
		}

		mHashMapFragment = new HashMap<>();
		mHashMapFragment.put(0, new JzxhChartFrament());
		mHashMapFragment.put(1, new JzxhIndexFrament());
		mHashMapFragment.put(2, new JzxhListFrament());
		mAdapter = new ColorFragmentAdapter(getSupportFragmentManager(), mHashMapFragment);
		mViewPager.setAdapter(mAdapter);
		//添加事件-滑倒后选择...
		mViewPager.addOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(mHashMapFragment.size());
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

	public void onClickListener(View v){
        if (v.getId() == R.id.jzxhMain_tv_cellname) {//基站名点击
            if (mTvCellName.getText().toString().length() > 0) {
                Integer ids = (Integer) mTvCellName.getTag(R.id.idIds);
                if (ids > 0) {
                    WqGisLayerInfo mWqGisLayerInfo = new WqGisLayerInfo();
                    mWqGisLayerInfo.setList(new ArrayList<WqGisLayer>());
                    WqGisLayer mWqGisLayer = new WqGisLayer();
                    mWqGisLayer.set_id(ids);
                    String strNet = mTvCellName.getTag(R.id.idType).toString();
                    if ("LTE".equals(strNet)) {
                        mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_LTE_CELL());
                        mWqGisLayer.setType(2);
                    } else if ("GSM".equals(strNet)) {
                        mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_GSM_CELL());
                        mWqGisLayer.setType(0);
}else if ("NR".equals(strNet)) {
						mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_NR_CELL());
						mWqGisLayer.setType(26);
                    } else {
                        mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_TDS_CELL());
                        mWqGisLayer.setType(1);
                    }
                    mWqGisLayerInfo.getList().add(mWqGisLayer);
                    Intent intent = new Intent(this, "LTE".equals(strNet) ? LayerDetailActivity.class : LayerDetailGsmActivity.class);
                    intent.putExtra("data", mWqGisLayerInfo);
                    startActivity(intent);
                }
            }
        }
	}

	//时速
	private double mLastSpeed;
	//上一次定位
	private LocationBean mLastLocationBean;
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

	//上一次上传，下载，时间
	private long mLastDl,mLastUl,mLastTimeMillis;
	private double mSpeedDl,mSpeedUl;
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

	private void setNrLte(NrCell nrCell, LTECell lteCell) {
		if (lteCell != null) {
			mTvMcc.setText(lteCell.getMcc());
			mTvMnc.setText(lteCell.getMnc());
			setCellType(lteCell.getMnc(), mIvCellType);
			if (mViewCellName.getVisibility() != View.VISIBLE) {
				if (getParent() instanceof JzxhTabActivity) {
					((JzxhTabActivity)getParent()).setCellName(lteCell, mSimPosition);
				}
			}else if (!mTvCellName.getText().toString().equals(lteCell.name)) {
				mTvCellName.setText(lteCell.name);
				mTvCellName.setTag(R.id.idIds, lteCell.asu);
				mTvCellName.setTag(R.id.idType, "LTE");
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
			mTvEebKey.setText("eNB:");
			toItemSignal(nrCell,lteCell, null, null);
		}else if (nrCell != null) {
			mTvMcc.setText(nrCell.getMcc());
			mTvMnc.setText(nrCell.getMnc());
			setCellType(nrCell.getMnc(), mIvCellType);
			if (mViewCellName.getVisibility() != View.VISIBLE) {
				if (getParent() instanceof JzxhTabActivity) {
					((JzxhTabActivity)getParent()).setCellName(nrCell, mSimPosition);
				}
			}else if (!mTvCellName.getText().toString().equals(nrCell.name)) {
				mTvCellName.setText(nrCell.name);
				mTvCellName.setTag(R.id.idIds, nrCell.asu);
				mTvCellName.setTag(R.id.idType, "NR");
			}
			mTvLteBand.setText(nrCell.band);
			mTvLtePd.setText(nrCell.earfcn);
			int pci = UtilHandler.getInstance().toInt(nrCell.getPci(), Integer.MAX_VALUE);
			if (pci != Integer.MAX_VALUE) {
				switch (pci % 3) {
				case 0:
					mTvLtePci.setText(nrCell.getPci() + "⓿");
					break;
				case 1:
					mTvLtePci.setText(nrCell.getPci() + "❶");
					break;
				case 2:
					mTvLtePci.setText(nrCell.getPci() + "❷");
					break;
				default:
					mTvLtePci.setText(nrCell.getPci());
					break;
				}
			}else {
				mTvLtePci.setText(nrCell.getPci());
			}
			mTvLteTac.setText(nrCell.getTac());
			mTvLteEnb.setText(nrCell.getGnb());
			mTvLteCellid.setText(nrCell.getCellid());

			mTvLteRsrp.setText(nrCell.getRsrp() + " dBm");
			mTvLteRsrq.setText(nrCell.getRsrq() + " db");
			mTvLteSinr.setText(nrCell.getSinr());
			mTvNet.setText("NR_SA");
			mTvEebKey.setText("gNB:");
			toItemSignal(nrCell,null, null, null);
		}
	}

	private void setGsm(GSMCell cell) {
		if (cell != null) {
			if (mViewCellName.getVisibility() != View.VISIBLE) {
				if (getParent() instanceof JzxhTabActivity) {
					((JzxhTabActivity)getParent()).setCellName(cell, mSimPosition);
				}
			}else if (!mTvCellName.getText().toString().equals(cell.name)) {
				mTvCellName.setText(cell.name);
				mTvCellName.setTag(R.id.idIds, cell.asu);
				mTvCellName.setTag(R.id.idType, "GSM");
			}
			mTvMcc.setText(cell.getMcc());
			mTvMnc.setText(cell.getMnc());
			setCellType(cell.getMnc(), mIvCellType);
			mTvGsmLac.setText(cell.getLac());
			mTvGsmCi.setText(cell.getCid());
			mTvGsmRxlev.setText(cell.getSign());
			//			mTvNet.setText(cell.net);
			toItemSignal(null,null, null, cell);
		}
	}

	private void setTd(TDCell cell) {
		if (cell != null) {
			if (mViewCellName.getVisibility() != View.VISIBLE) {
				if (getParent() instanceof JzxhTabActivity) {
					((JzxhTabActivity)getParent()).setCellName(null, mSimPosition);
				}
			}else if (!mTvCellName.getText().toString().equals(cell.name)) {
				//				mTvCellName.setText(cell.name);
				//				mTvCellName.setTag(R.id.idIds, cell.asu);
				//				mTvCellName.setTag(R.id.idType, "TD");
				mTvCellName.setText("");
			}
			mTvMcc.setText(cell.getMcc());
			mTvMnc.setText(cell.getMnc());
			setCellType(cell.getMnc(), mIvCellType);
			mTvTdLac.setText(cell.getLac());
			mTvTdCi.setText(cell.getCi());
			mTvTdPccpch.setText(cell.getSign());
			//			mTvNet.setText(cell.net);
			toItemSignal(null,null, cell, null);
		}
	}

	private Signal mSignalNow;
	//设置当前基站
	private void toItemSignal(NrCell nrCell, LTECell lteCell,TDCell tdCell,GSMCell gsmCell){
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

		if (nrCell != null) {
			mSignalNow.lte_pci_nr = nrCell.getPci();
			mSignalNow.lte_rsrp_nr = nrCell.getRsrp();
			mSignalNow.lte_rsrq_nr = nrCell.getRsrq();
			mSignalNow.lte_sinr_nr = nrCell.getSinr();
			mSignalNow.lte_earfcn_nr = nrCell.earfcn;
			mSignalNow.lte_band_nr = nrCell.band;
			mSignalNow.lte_frequency_nr = nrCell.frequency;
			mSignalNow.lte_name_nr = nrCell.name;
			mSignalNow.lte_rssi_nr = nrCell.getRssi();
			mSignalNow.lte_tac_nr = nrCell.getTac();
			mSignalNow.lte_nci_nr = nrCell.getNci();
			mSignalNow.lte_gnb_nr = nrCell.getGnb();
			mSignalNow.lte_cellid_nr = nrCell.getCellid();

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
				mSignalNow.setTypeNet("LTE");
			}else {
				mSignalNow.DB_ID = nrCell.db_id;
				mSignalNow.setTypeNet("NR");
				mSignalNow.setTime(TimeUtil.getInstance().getNowTimeSS(nrCell.time));
				mSignalNow.timeHH = TimeUtil.getInstance().getTimeH(nrCell.time);
			}
		}else if (lteCell != null) {
			mSignalNow.setTypeNet("LTE");
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
		}else if (gsmCell != null) {
			mSignalNow.setTypeNet("GSM");
			mSignalNow.setMcc(gsmCell.getMcc());
			mSignalNow.setMnc(gsmCell.getMnc());
			mSignalNow.setGsm_cid(gsmCell.getCid());
			mSignalNow.setGsm_lac(gsmCell.getLac());
			mSignalNow.setGsm_name(gsmCell.name);
			mSignalNow.setGsm_rxl(gsmCell.getSign());
			mSignalNow.setTime(TimeUtil.getInstance().getNowTimeSS(gsmCell.time));
			mSignalNow.timeHH = TimeUtil.getInstance().getTimeH(gsmCell.time);
			mSignalNow.DB_ID = gsmCell.asu;
		}else if (tdCell != null && NetWorkStateUtil.isCmccNet(tdCell.getMnc()) != 0) {
			mSignalNow.setTypeNet("TD");
			mSignalNow.setMcc(tdCell.getMcc());
			mSignalNow.setMnc(tdCell.getMnc());
			mSignalNow.setTd_ci(tdCell.getCi());
			mSignalNow.setTd_lac(tdCell.getLac());
			mSignalNow.setTd_name(tdCell.name);
			mSignalNow.setTd_pccpch(tdCell.getSign());
			mSignalNow.setTime(TimeUtil.getInstance().getNowTimeSS(tdCell.time));
			mSignalNow.timeHH = TimeUtil.getInstance().getTimeH(tdCell.time);
			mSignalNow.DB_ID = tdCell.asu;
		}else {
			return;
		}

		mSignalNow.subSinr = strSubSinr;
		mSignalNow.subRsrp = strSubRsrp;

		((JzxhChartFrament)mHashMapFragment.get(0)).addSignalItem(mSignalNow);
		((JzxhIndexFrament)mHashMapFragment.get(1)).addSignalItem(mSignalNow);
		((JzxhListFrament)mHashMapFragment.get(2)).addSignalItem(mSignalNow);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	private int mLastY = 0,mLastPosition = 0;

	@Override
	public void onPageSelected(int position) {
		if (mHashMapFragment != null && mHashMapFragment.get(0) != null) {
			if (mLastPosition == 0) {
				mLastY = ((JzxhChartFrament)mHashMapFragment.get(0)).mScrollView.getScrollY();
			}
			if (position == 0) {
				((JzxhChartFrament)mHashMapFragment.get(0)).setScrollTo(666,mLastY);
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

	@Override
	protected void onDestroy() {
		SimSignalHandler.getInstances().removeListener(this,this);
		super.onDestroy();
	}

	/**
	 * 设置接收信号运营商类型
	 */
	private void setCellType(String mnc, ImageView iv) {
		if (mViewCellName.getVisibility() != View.VISIBLE) {
			if (getParent() instanceof JzxhTabActivity) {
				((JzxhTabActivity)getParent()).setCellType(mnc, mSimPosition);
			}
			return;
		}
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

	private int mLastAdjType;
	@Override
	public void onSignalListener(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj) {
		if(this instanceof JzxhSim1Activity){
			if(simPosition == 0){ //主卡
				mainCardHandle(netType, adjType, simPosition, isTestSim, obj);
			}else{ //副卡
				subCardHandle(netType, adjType, simPosition, isTestSim, obj);
			}
		}else if(this instanceof JzxhSim2Activity){
			if(simPosition == 1){ //主卡
				mainCardHandle(netType, adjType, simPosition, isTestSim, obj);
			}else{ //副卡
				subCardHandle(netType, adjType, simPosition, isTestSim, obj);
			}
		}
	}

	/**
	 * 主卡处理
	 * @param netType
	 * @param adjType
	 * @param simPosition
	 * @param isTestSim
	 * @param obj
	 */
	protected void mainCardHandle(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj){
		try {
			//5G
			if (netType == CellUtil.NETWORK_NR) {
				mViewLte1.setVisibility(View.VISIBLE);
				mViewLte2.setVisibility(View.VISIBLE);
				mViewLte3.setVisibility(View.VISIBLE);
				mViewGsm.setVisibility(View.GONE);
				mViewTd.setVisibility(View.GONE);
				setNrLte((NrCell) obj[0], (LTECell) obj[1]);
				if (obj.length > 2) {
					if (adjType == CellUtil.NETWORK_NR || adjType == CellUtil.NETWORK_TD || NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
					}else if (adjType == CellUtil.NETWORK_LTE) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<LTECell>)obj[2],(NrCell) obj[0], mHandler);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[2]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[2],(NrCell) obj[0], mHandler);
					}
				}else {
					((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
				//LTE
			}else if (netType == CellUtil.NETWORK_LTE) {
				mViewLte1.setVisibility(View.VISIBLE);
				mViewLte2.setVisibility(View.VISIBLE);
				mViewLte3.setVisibility(View.VISIBLE);
				mViewGsm.setVisibility(View.GONE);
				mViewTd.setVisibility(View.GONE);
				setNrLte(null, (LTECell) obj[0]);
				if (obj.length > 1) {
					if (adjType == CellUtil.NETWORK_TD || NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
					}else if (adjType == CellUtil.NETWORK_LTE) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<LTECell>)obj[1],(LTECell) obj[0], mHandler);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[1],(LTECell) obj[0], mHandler);
					}
				}else {
					((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
				//TD	
			} else if (netType == CellUtil.NETWORK_TD) {
				mViewLte1.setVisibility(View.GONE);
				mViewLte2.setVisibility(View.GONE);
				mViewLte3.setVisibility(View.GONE);
				mViewGsm.setVisibility(View.GONE);
				mViewTd.setVisibility(View.VISIBLE);
				mTvNet.setText("TD");
				setTd((TDCell) obj[0]);
				if (obj.length > 1) {
					if (adjType == CellUtil.NETWORK_TD || NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[1],(TDCell) obj[0], mHandler);
					}
				}else {
					((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
				//GSM	
			} else if (netType == CellUtil.NETWORK_GSM) {
				mViewLte1.setVisibility(View.GONE);
				mViewLte2.setVisibility(View.GONE);
				mViewLte3.setVisibility(View.GONE);
				mViewGsm.setVisibility(View.VISIBLE);
				mViewTd.setVisibility(View.GONE);
				mTvNet.setText("GSM");
				setGsm((GSMCell) obj[0]);
				if (obj.length > 1) {
					if (NetWorkStateUtil.isCmccNet(mTvMnc.getText().toString()) != 0) {
						((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
					}else if (adjType == CellUtil.NETWORK_GSM) {
						if (mLastAdjType != adjType) {
							((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell((List<Object>) obj[1]);
						}
//						Wq_ReadOtherCellNameUtil.getIntances().readCellNameOther((List<GSMCell>)obj[1],(GSMCell) obj[0], mHandler);
					}
				}else {
					((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				}
			}else {
				if (mViewGsm.getVisibility() == View.VISIBLE) {
					mTvGsmLac.setText("");
					mTvGsmCi.setText("");
					mTvGsmRxlev.setText("");
				}else if (mViewTd.getVisibility() == View.VISIBLE) {
					mTvTdLac.setText("");
					mTvTdCi.setText("");
					mTvTdPccpch.setText("");
				}else {
					mTvLteEnb.setText("");
					mTvLteCellid.setText("");
					mTvLtePci.setText("");
					mTvLteBand.setText("");
					mTvLtePd.setText("");
					mTvLteTac.setText("");
					mTvLteRsrp.setText("");
					mTvLteRsrq.setText("");
					mTvLteSinr.setText("");
				}
				if (mViewCellName.getVisibility() != View.VISIBLE) {
					if (getParent() instanceof JzxhTabActivity) {
						((JzxhTabActivity)getParent()).setCellName(null, mSimPosition);
						setCellType("-1", mIvCellType);
					}
				}else {
					mTvCellName.setText("");
				}
				mTvMcc.setText("");
				mTvMnc.setText("");
				mTvNet.setText("");
				((JzxhChartFrament)mHashMapFragment.get(0)).setAdjacentCell(null);
				((JzxhChartFrament)mHashMapFragment.get(0)).addSignalItem(null);
				((JzxhIndexFrament)mHashMapFragment.get(1)).addSignalItem(null);
			}
			mLastAdjType = adjType;
		} catch (Exception e) {
		}
	}

	/**
	 * 副卡处理
	 * @param netType
	 * @param adjType
	 * @param simPosition
	 * @param isTestSim
	 * @param obj
	 */
	protected void subCardHandle(int netType,int adjType,int simPosition,boolean isTestSim,Object... obj){
		try{
			if(netType == CellUtil.NETWORK_NR){
				if((obj.length <= 1 || obj[1] == null) && obj[0] instanceof NrCell){
					NrCell nrCell = (NrCell) obj[0];
					if(nrCell != null){
						strSubSinr = nrCell.getSinr();
						strSubRsrp = nrCell.getRsrp();
					}
				}else if(obj[1] instanceof LTECell){
					LTECell lteCellMd = (LTECell) obj[1];
					if(lteCellMd != null){
						strSubSinr = lteCellMd.getSinr();
						strSubRsrp = lteCellMd.getRsrp();
					}
				}
			}else if(netType == CellUtil.NETWORK_LTE){
				if(obj[0] instanceof LTECell){
					LTECell lteCell = (LTECell) obj[0];
					if(lteCell != null){
						strSubSinr = lteCell.getSinr();
						strSubRsrp = lteCell.getRsrp();
					}
				}
			}else{
				strSubSinr = null;
				strSubRsrp = null;
			}
		}catch(Exception e){
			
		}
	}

	@Override
	public void onAnalysis(int status,Object... obj) {

	}
}
