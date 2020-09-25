package cn.nokia.speedtest5g.speedtest.pk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.JJ_BaseSignalActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_AppWarningLog;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FtpAddressGroupInfo;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FtpAddressInfo;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.request.JJ_RequestAppWarning;
import cn.nokia.speedtest5g.app.request.JJ_RequestFtpTest;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedPkGroupMemberAdapter;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestInitiateSpeedTest;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkModifyGroupStatus;
import cn.nokia.speedtest5g.speedtest.bean.ResponseInitiateSpeedTest;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkGroup;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDetailChartView;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.MyScrollyGridView;
import cn.nokia.speedtest5g.view.WaveView;

/**
 * 速率测试PK
 * @author JQJ
 *
 */
public class SpeedPkActivity extends JJ_BaseSignalActivity implements OnClickListener {
	private final int HANDLER_QUERY_FTP_UPLOAD_DATA = 2000;
	private final int HANDLER_QUERY_APP_WARNING_UPLOAD_DATA = 2001;
	private final int START_SPEED_PK_RESULT_ACTIVITY = 2002;

	private ImageView iv_speed_pk_bg;
	private WaveView ww_speed_pk_wave_view;

	private TextView tv_speed_pk_group_name;
	private TextView tv_speed_pk_group_test_status;
	private TextView tv_ftp_test_switch;

	private MyScrollyGridView gv_speed_pk_group_member_grid_view;
	private SpeedPkGroupMemberAdapter groupMemberAdapter;
	// 回放布局
	private TextView tvPlayBackCellName;
	// LTE信号
	private TableLayout tlSignalInfoPlayBack;
	private TextView tvPlayBackTac;
	private TextView tvPlayBackPci;
	private TextView tvPlayBackCGI;
	private TextView tvPlayBackENB;
	private TextView tvPlayBackCELLID;
	private TextView tvPlayBackPindian;
	private TextView tvPlayBackRSRP;
	private TextView tvPlayBackSINR;
	// GSM信号
	private TableLayout tlSignalInfoGsmPlayBack;
	private TextView tvPlayBackTacGSM;
	private TextView tvPlayBackCELLIDGSM;
	private TextView tvPlayBackBcchPDGSM;
	private TextView tvPlayBackRXLGSM;

	private TextView tvSpeedTestPlayBackType;
	private TextView tvSpeedTestPlayBackData;
	private TextView tvSpeedTestPlayBackGsmType;
	private TextView tvSpeedTestPlayBackGsmData;
	// 回放布局结束
	private View v_ftp_test_down_or_up_color;
	private TextView tv_ftp_test_down_or_up;
	private LinearLayout ll_speed_test_sinr;
	private LinearLayout ll_speed_test_rsrp;
	private ImageView iv_speed_test_sinr;
	private ImageView iv_speed_test_rsrp;

	private FrameLayout flSpeedView;
	//特殊处理 实际测试10S
	private SpeedTestDetailChartView mNewCharView;
	//	private TextView tv_speed_test_server_name_and_ip;

	private boolean isAlive;

	private Signal mLastSignal = null;

	private Db_JJ_AppWarningLog mAppWarningInfo = null;// APP告警数据
	private List<Db_JJ_AppWarningLog> mAppWarningList = null;
	private StringBuffer appTest = null;// FTP测试信息

	private List<Signal> mSignalTestDownList = new ArrayList<Signal>();
	private List<Signal> mSignalTestUploadList = new ArrayList<Signal>();
	// FTP测试结果
	private List<Db_JJ_FTPTestInfo> mUploadFtpTestList = null;// 需要上传的测试结果列表

	private Db_JJ_FTPTestInfo mFtpTestInfo = null;
	private ArrayList<Db_JJ_FtpAddressGroupInfo> mFtpAddressList = null;
	private BeanSpeedPkGroupInfo speedPkGroupInfo = null;
	private int mPackageType = PACAKGE_SIZE_LARGE; //默认推荐大包
	private Handler mChildHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pk_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		initHandler();

		mFtpAddressList = getIntent().getParcelableArrayListExtra("mFtpAddressList");
		mFtpTestInfo = (Db_JJ_FTPTestInfo) getIntent().getSerializableExtra("mFtpTestInfo");
		speedPkGroupInfo = (BeanSpeedPkGroupInfo) getIntent().getSerializableExtra("speedPkGroupInfo");
		init(R.string.speed_pk, true);
		startSpeedTest();
	}

	@SuppressLint("CutPasteId")
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		iv_speed_pk_bg = (ImageView) findViewById(R.id.iv_speed_pk_bg);
		ww_speed_pk_wave_view = (WaveView) findViewById(R.id.ww_speed_pk_wave_view);
		tv_speed_pk_group_name = (TextView) findViewById(R.id.tv_speed_pk_group_name);
		tv_speed_pk_group_test_status = (TextView) findViewById(R.id.tv_speed_pk_group_test_status);
		tv_ftp_test_switch = (TextView) findViewById(R.id.tv_ftp_test_switch);
		gv_speed_pk_group_member_grid_view = (MyScrollyGridView) findViewById(R.id.gv_speed_pk_group_member_grid_view);
		// 回放-----------------------------------------------------------------------
		tvPlayBackCellName = (TextView) findViewById(R.id.tv_speed_test_play_back_cell_name);
		tlSignalInfoPlayBack = (TableLayout) findViewById(R.id.tl_signal_info_play_back);
		tvPlayBackTac = (TextView) findViewById(R.id.tv_speed_test_play_back_tac);
		tvPlayBackPci = (TextView) findViewById(R.id.tv_speed_test_play_back_pci);
		tvPlayBackCGI = (TextView) findViewById(R.id.tv_speed_test_play_back_cgi);

		tvPlayBackENB = (TextView) findViewById(R.id.tv_speed_test_play_back_enb);
		tvPlayBackCELLID = (TextView) findViewById(R.id.tv_speed_test_play_back_cellid);
		tvPlayBackPindian = (TextView) findViewById(R.id.tv_speed_test_play_back_pingdian);
		tvPlayBackRSRP = (TextView) findViewById(R.id.tv_speed_test_play_back_rsrp);
		tvPlayBackSINR = (TextView) findViewById(R.id.tv_speed_test_play_back_sinr);

		tlSignalInfoGsmPlayBack = (TableLayout) findViewById(R.id.tl_signal_info_play_back_gsm);
		tvPlayBackTacGSM = (TextView) findViewById(R.id.tv_speed_test_play_back_tac_gsm);
		tvPlayBackCELLIDGSM = (TextView) findViewById(R.id.tv_speed_test_play_back_cellid_gsm);
		tvPlayBackBcchPDGSM = (TextView) findViewById(R.id.tv_speed_test_play_back_bcchPD_gsm);
		tvPlayBackRXLGSM = (TextView) findViewById(R.id.tv_speed_test_play_back_rxl_gsm);

		tvSpeedTestPlayBackType = (TextView) findViewById(R.id.tv_speed_test_play_back_type);
		tvSpeedTestPlayBackData = (TextView) findViewById(R.id.tv_speed_test_play_back_data);
		tvSpeedTestPlayBackGsmType = (TextView) findViewById(R.id.tv_speed_test_play_back_gsm_type);
		tvSpeedTestPlayBackGsmData = (TextView) findViewById(R.id.tv_speed_test_play_back_gsm_data);
		// 回放结束-----------------------------------------------------------------------

		v_ftp_test_down_or_up_color = findViewById(R.id.v_ftp_test_down_or_up_color);
		tv_ftp_test_down_or_up = (TextView) findViewById(R.id.tv_ftp_test_down_or_up);
		ll_speed_test_sinr = (LinearLayout) findViewById(R.id.ll_speed_test_sinr);
		ll_speed_test_rsrp = (LinearLayout) findViewById(R.id.ll_speed_test_rsrp);
		iv_speed_test_sinr = (ImageView) findViewById(R.id.iv_speed_test_sinr);
		iv_speed_test_rsrp = (ImageView) findViewById(R.id.iv_speed_test_rsrp);
		flSpeedView = (FrameLayout) findViewById(R.id.fl_speed_view);
		//		tv_speed_test_server_name_and_ip = (TextView) findViewById(R.id.tv_speed_test_server_name_and_ip);

		tv_ftp_test_switch.setOnClickListener(this);
		ll_speed_test_sinr.setOnClickListener(this);
		ll_speed_test_rsrp.setOnClickListener(this);

		groupMemberAdapter = new SpeedPkGroupMemberAdapter(this, speedPkGroupInfo.groupMemberList);
		gv_speed_pk_group_member_grid_view.setAdapter(groupMemberAdapter);
		tv_speed_pk_group_name.setText(speedPkGroupInfo.rateGroupName);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotation);
		animation.setInterpolator(new LinearInterpolator());
		iv_speed_pk_bg.startAnimation(animation);
		ww_speed_pk_wave_view.startAnimation();
		mNewCharView = new SpeedTestDetailChartView(mActivity, 20, this);
		flSpeedView.addView(mNewCharView);// 初始化曲线图
		mNewCharView.setEnabled(false);
		tv_ftp_test_switch.setVisibility(View.GONE);

		requestUpdatePkGroupInfo();

		updateFlowConfig();
	}

	/**
	 * handler处理
	 */
	private void initHandler(){
		mChildHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if(msg.what == EnumRequest.TASK_PING.toInt()){
					mPingInfo = (PingInfo)msg.obj;

					if(mPingInfo == null && mCurrentPingCount < mPingCount){
						startPing();
						return true;
					}

					//ping失败  或值为0 则重试
					if(!mPingInfo.isState() && mCurrentPingCount < mPingCount){
						startPing();
						return true;
					}

					if(mPingInfo.getTimes() == -2 && mPingInfo.shake == 0 &&
							mCurrentPingCount < mPingCount){
						startPing();
						return true;
					}

				}
				return true;
			}
		});
	}

	private void updateFlowConfig(){
		mPackageType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared("PackageType", PACAKGE_SIZE_LARGE);
		long allTestTime = mAllTestTimeForLarge;
		long updateTime = mUpdateTimeForLarge;
		if(mPackageType == PACAKGE_SIZE_LARGE){ //大包
			allTestTime = mAllTestTimeForLarge;
			updateTime = mUpdateTimeForLarge;
		}else if(mPackageType == PACAKGE_SIZE_MIDDLE){
			allTestTime = mAllTestTimeForMiddle;
			updateTime = mUpdateTimeForMiddle;
		}else if(mPackageType == PACAKGE_SIZE_SMALL){
			allTestTime = mAllTestTimeForSmall;
			updateTime = mUpdateTimeForSmall;
		}

		//设置测速配置
		setTimeOut(allTestTime);
		setUpdateTime(updateTime);
	}

	/**
	 * 更新PK组信息
	 */
	private void requestUpdatePkGroupInfo() {
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_GROUP_UPDATE_STATUS);
		RequestSpeedPkGroupInfo requset = new RequestSpeedPkGroupInfo();
		requset.id = speedPkGroupInfo.id;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(url, jsonData, R.id.net_speed_pk_group_update_info, this);
	}

	private Signal receiverSignal;

	@Override
	public void onSignalReceiver(Signal signal) {
		if (!isFallback()) {
			setSignalData(signal);
		}
	}

	private void setSignalData(Signal signal) {
		receiverSignal = signal;
		mLastSignal = signal;
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_ftp_test_switch) {
            if (tv_ftp_test_switch.getText().equals(getString(R.string.download))) {// 切换到上传
                if (mSignalTestUploadList == null || mSignalTestUploadList.size() == 0) {
                    showCommon("本次未进行上传速率测试");
                    return;
                }
                tv_ftp_test_switch.setText(getString(R.string.upload));
                tv_ftp_test_switch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_upload, 0, 0, 0);
                updateView(false);
                if (mSignalTestUploadList.size() > 0) {
                    setSignalDataPlayBack(mSignalTestUploadList.get(0), 0);
                }

            } else if (tv_ftp_test_switch.getText().equals(getString(R.string.upload))) {// 切换到下载
                if (mSignalTestDownList == null || mSignalTestDownList.size() == 0) {
                    showCommon("本次未进行下载速率测试");
                    return;
                }
                tv_ftp_test_switch.setText(getString(R.string.download));
                tv_ftp_test_switch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_download, 0, 0, 0);
                updateView(true);
                if (mSignalTestDownList.size() > 0) {
                    setSignalDataPlayBack(mSignalTestDownList.get(0), 0);
                }
            }
        } else if (id == R.id.ll_speed_test_sinr) {
            mNewCharView.showRightDataAxis(R.id.ll_speed_test_sinr);
            iv_speed_test_sinr.setImageResource(R.drawable.icon_speed_test_sinr_selected);
            iv_speed_test_rsrp.setImageResource(R.drawable.icon_speed_test_sinr_rsrp_normal);
        } else if (id == R.id.ll_speed_test_rsrp) {
            mNewCharView.showRightDataAxis(R.id.ll_speed_test_rsrp);
            iv_speed_test_sinr.setImageResource(R.drawable.icon_speed_test_sinr_rsrp_normal);
            iv_speed_test_rsrp.setImageResource(R.drawable.icon_speed_test_rsrp_selected);
        }
	}

	/**
	 * 点击开始测试
	 */
	private void startSpeedTest() {
		clearSpeedTestData();
		tv_speed_pk_group_test_status.setText("");
		mNewCharView.setEnabled(false);

		BeanAppFtpConfig beanAppFtpConfig = getSelectServer();

		//自定义服务器 只ping下载的
		startPing();

		if(beanAppFtpConfig.serverType){
			initiateSpeedTest();
		}else{ //自定义服务器不调用
			startFtpTest();
		}
	}

	/**
	 * app发起速率测试
	 */
	private void initiateSpeedTest(){
		try{
			BeanAppFtpConfig config = getSelectServer();
			RequestInitiateSpeedTest request = new RequestInitiateSpeedTest();
			request.apnftpId = config.iD;
			request.userId = getUserID();
			request.operator = config.operator;
			request.province = config.province;
			request.city = config.city;
			request.ip = config.ip;
			request.port = config.port;
			request.ftpType = config.ftpType;
			NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
					NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_INITIATE_SPEED_TEST), JsonHandler.getHandler().toJson(request), 
					-1, new ListenerBack() {

						@Override
						public void onListener(int type, Object object, boolean isTrue) {
							try{
								ResponseInitiateSpeedTest response = JsonHandler.getHandler().getTarget(object.toString(), ResponseInitiateSpeedTest.class);
								if (response != null) {
									if (response.isRs()) {
										mLogId = response.datas.logId;
										startFtpTest();
									} else {
										showCommon(response.getMsg());
									}
								} else {
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String regionName;

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == EnumRequest.MENU_BACK.toInt()) {
			onBackPressed();

		} else if (type == EnumRequest.OTHER_GEO_SEARCH.toInt()) {// 根据经纬度解析地理位置信息
			if (isTrue) {
				AddressComponent address = ((ReverseGeoCodeResult) object).getAddressDetail();
				if (address == null) {
					return;
				}
				// 市区
				regionName = address.city.substring(0, address.city.length() - 1);
			}
			super.onListener(type, object, isTrue);

		} else if (type == EnumRequest.OTHER_SPEED_CLICK.toInt()) {
			int position = (int) object;
			if (isDownload) {
				if (mSignalTestDownList != null && mSignalTestDownList.size() > position) {
					setSignalDataPlayBack(mSignalTestDownList.get(position), position);
				}

			} else {
				if (mSignalTestUploadList != null && mSignalTestUploadList.size() > position) {
					setSignalDataPlayBack(mSignalTestUploadList.get(position), position);
				}
			}
		} else if (type == EnumRequest.OTHER_SPEED_SWITCH.toInt()) {
			mNewCharView.setTestDownOrUp();
			updateView(isTrue);
		} else if (type == EnumRequest.NET_SAVE_FTP_SPEED_TEST_LOG.toInt()) {// 速率测试上传结果
			dismissMyDialog();
			if (isTrue) {
				BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
				if (response != null) {
					if (response.isRs()) {
						new Thread(new Runnable() {
							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								List<Db_JJ_FTPTestInfo> list = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance()
										.queryObj(Db_JJ_FTPTestInfo.class, "isUpload =? and userid=?", new Object[] {
											false, UtilHandler.getInstance().toInt(getUserID(), 0) });
								if (list != null && list.size() > 0) {
									for (Db_JJ_FTPTestInfo info : list) {
										info.setUpload(true);
										DbHandler.getInstance().updateObj(info);

										mHandler.sendEmptyMessage(START_SPEED_PK_RESULT_ACTIVITY);
									}
								}
							}
						}).start();

					} else {
						showCommon(response.getMsg());
					}
				} else {
					showCommon(object.toString());
				}
			}

		} else if (type == EnumRequest.NET_SAVE_APP_WARNING_LOG.toInt()) {
			if (isTrue) {
				BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
				if (response != null) {
					if (response.isRs()) {
						new Thread(new Runnable() {

							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								List<Db_JJ_AppWarningLog> list = (List<Db_JJ_AppWarningLog>) DbHandler.getInstance()
										.queryObj(Db_JJ_AppWarningLog.class, "isUpload =?", new Object[] { false });
								if (list != null && list.size() > 0) {
									for (Db_JJ_AppWarningLog info : list) {
										info.setUpload(true);
										DbHandler.getInstance().updateObj(info);
									}
								}
							}
						}).start();
					} else {
						showCommon(response.getMsg());
					}
				} else {
					showCommon(object.toString());
				}
			}

		} else if (type == R.id.net_speed_pk_group_modify_status) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				setResult(RESULT_OK);
				finish();

			} else {
				String errorMsg = response == null ? "操作失败" : response.getMsg();
				showCommon(errorMsg);
			}
			dismissMyDialog();

		} else if (type == R.id.net_speed_pk_group_update_info) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				BeanSpeedPkGroupInfo groupInfo = response.datas;
				if (groupInfo.groupMemberList != null && groupInfo.groupMemberList.size() > 0) {
					speedPkGroupInfo.groupMemberList.clear();
					speedPkGroupInfo.groupMemberList.addAll(groupInfo.groupMemberList);
					groupMemberAdapter.updateData(speedPkGroupInfo.groupMemberList);
				}

			} else {
				String errorMsg = response == null ? "获取PK组信息失败" : response.getMsg();
				showCommon(errorMsg);
			}

		} else {
			super.onListener(type, object, isTrue);
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == HANDLER_QUERY_FTP_UPLOAD_DATA) {// 上传测试
				if (NetInfoUtil.isNetworkConnected(SpeedPkActivity.this)) {
					if (mUploadFtpTestList != null && mUploadFtpTestList.size() > 0) {
						JJ_RequestFtpTest request = new JJ_RequestFtpTest();
						request.setDatas(mUploadFtpTestList);
						NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
								NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SAVE_SPEED_TEST_LOG),
								JsonHandler.getHandler().toJson(request), EnumRequest.NET_SAVE_FTP_SPEED_TEST_LOG.toInt(),
								SpeedPkActivity.this);
					}
				} else {
					new CommonDialog(SpeedPkActivity.this).setTitle(getString(R.string.attendance_tips)).setListener(new ListenerBack() {
						@Override
						public void onListener(int type, Object object, boolean isTrue) {
							if (isTrue) {
								mHandler.sendEmptyMessage(HANDLER_QUERY_FTP_UPLOAD_DATA);
							}
						}
					}).setButtonText("重新上传", "取消").show("当前无网络，本次PK测试结果无法上传，请检查网络后，点击重新上传。");
				}

			} else if (msg.what == HANDLER_QUERY_APP_WARNING_UPLOAD_DATA) {
				JJ_RequestAppWarning request = new JJ_RequestAppWarning();
				request.setDatas(mAppWarningList);
				NetWorkUtilNow.getInstances().readNetworkPostJsonObject(
						NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SAVE_APP_WARNING_LOG),
						JsonHandler.getHandler().toJson(request), EnumRequest.NET_SAVE_APP_WARNING_LOG.toInt(),
						SpeedPkActivity.this);

			} else if (msg.what == START_SPEED_PK_RESULT_ACTIVITY) {
				Bundle extras = new Bundle();
				extras.putLong("groupId", mFtpTestInfo.id);
				goIntent(SpeedPkResultActivity.class, extras, true);
			}
			return true;
		}
	});

	// 开始测试FTP
	private void startFtpTest() {
		initializationFtp();
	}

	// 速率测试
	@Override
	public void initialization(String downName, String sDownPath, String sUploadPath) {
		inits(downName, sDownPath, sUploadPath);
	}

	/**
	 * 开始ping
	 */
	private void startPing(){

		new Thread(){
			@Override
			public void run() {
				SystemClock.sleep(500);

				BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
				if(beanAppFtpConfig != null){
					mPingInfo = new PingInfo();
					if(beanAppFtpConfig.serverType){
						mPingInfo.setIp(beanAppFtpConfig.ip);
					}else{
						mPingInfo.setIp(beanAppFtpConfig.customDownIp);
					}

					PingThread pingThread  = new PingThread(mPingInfo, 0, mChildHandler, 4, 5);//发包4次 超时5S
					pingThread.start();
				}
			};
		}.start();
	}

	@Override
	public void initializationFtp(int position) {
		initialization("500M.rar", "/download/", "/upload/");
		setMaxDownSize(1024 * 1024 * 500);

		Db_JJ_FtpAddressGroupInfo info = null;
		for (int i = 0; i < mFtpAddressList.size(); i++) {
			if (mFtpAddressList.get(i).getHostType().equals("2")) {
				info = mFtpAddressList.get(i);
				break;
			}
		}

		String DownIp = "", DownUser = "", DownPasswd = "", DownPath = "", UploadIp = "", UploadUser = "",
				UploadPasswd = "", downHostType = "", upHostType = "";
		int DownPort = -1, DownThread = 3, UploadPort = -1, UploadThread = 1, UploadSize = -1, DownLenght = -1;
		if (info != null) {
			Db_JJ_FtpAddressInfo addressInfo = null;
			for (int i = 0; i < info.getFtpList().size(); i++) {
				if (info.getFtpList().get(i).isDownSelect()) {
					addressInfo = info.getFtpList().get(i);
					break;
				}
			}
			if (addressInfo != null) {
				DownIp = addressInfo.getIp();
				UploadIp = addressInfo.getIp();
				DownPort = addressInfo.getPort();
				UploadPort = addressInfo.getPort();
				DownUser = addressInfo.getAccount();
				UploadUser = addressInfo.getAccount();
				DownPasswd = addressInfo.getPassword();
				UploadPasswd = addressInfo.getPassword();
				downHostType = info.getHostType();
				upHostType = info.getHostType();

				DownPath = addressInfo.getFilename();

				appTest = new StringBuffer();
				appTest.append(" downIp=" + DownIp);
				appTest.append(" DownPort=" + DownPort);
				appTest.append(" DownUser=" + DownUser);
				appTest.append(" DownPasswd=" + DownPasswd);
				appTest.append(" DownPath=" + DownPath);
				appTest.append(" downHostType=" + downHostType);
				appTest.append(" UploadIp=" + UploadIp);
				appTest.append(" UploadPort=" + UploadPort);
				appTest.append(" UploadUser=" + UploadUser);
				appTest.append(" UploadPasswd=" + UploadPasswd);
				appTest.append(" upHostType=" + upHostType);
				initFtpTestLogInfo(DownIp, DownPath, DownPort, UploadIp, UploadPort, downHostType, upHostType,
						info.getHostTypeName(), info.getHostTypeName());

				WybLog.syso("下载1ip：" + DownIp + "-下载端口：" + DownPort + "-下载帐号：" + DownUser + "-下载密码：" + DownPasswd
						+ "-下载线程：" + DownThread + "-下载地址：" + DownPath + "-下载大小：" + DownLenght + "上传IP：" + UploadIp
						+ "-上传端口：" + UploadPort + "-上传帐号：" + UploadUser + "-上传密码：" + UploadPasswd + "-上传线程："
						+ UploadThread + "-上传大小：" + UploadSize);
				initFtpNew(DownIp, DownPort, DownUser, DownPasswd, DownThread, DownPath, DownLenght, UploadIp,
						UploadPort, UploadUser, UploadPasswd, UploadThread, UploadSize);
			}

		}

	}

	@Override
	public void initializationFtp() {
		try{
			String DownIp = "", DownUser = "", DownPasswd = "", DownPath = "", UploadIp = "", UploadUser = "",
					UploadPasswd = "", downHostType = "", upHostType = "", downHostName = "", upHostName = "";
			int DownPort = -1, DownThread = 3, UploadPort = -1, UploadThread = 1, UploadSize = -1, DownLenght = -1;

			BeanAppFtpConfig beanAppFtpConfig = getSelectServer();
			boolean FTP_DOWN_OPEN = false;
			boolean FTP_UPLOAD_OPEN = false;
			if(beanAppFtpConfig != null){
				if(FTP_TYPE_ALL.equals(beanAppFtpConfig.ftpType)){
					FTP_DOWN_OPEN = true;
					FTP_UPLOAD_OPEN = true;
				}else if(FTP_TYPE_DOWNLOAD.equals(beanAppFtpConfig.ftpType)){
					FTP_DOWN_OPEN = true;
					FTP_UPLOAD_OPEN = false;
				}else if(FTP_TYPE_UPLOAD.equals(beanAppFtpConfig.ftpType)){
					FTP_DOWN_OPEN = false;
					FTP_UPLOAD_OPEN = true;
				}

				//保存是否下载上传
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().FTP_DOWN_OPEN, FTP_DOWN_OPEN);
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().FTP_UPLOAD_OPEN, FTP_UPLOAD_OPEN);

				if(beanAppFtpConfig.serverType){
					DownUser = beanAppFtpConfig.downAccount;
					DownPasswd = beanAppFtpConfig.downPassword;
					DownIp = beanAppFtpConfig.ip;
					DownPath = beanAppFtpConfig.filename;
					if(!TextUtils.isEmpty(beanAppFtpConfig.port)){
						DownPort = Integer.parseInt(beanAppFtpConfig.port);
					}
					downHostType = "";
					downHostName = beanAppFtpConfig.hostType;
					UploadUser = beanAppFtpConfig.upAccount;
					UploadPasswd = beanAppFtpConfig.upPassword;
					UploadIp = beanAppFtpConfig.ip;
					if(!TextUtils.isEmpty(beanAppFtpConfig.port)){
						UploadPort = Integer.parseInt(beanAppFtpConfig.port);
					}
					upHostType = "";
					upHostName = beanAppFtpConfig.hostType;
					if(!TextUtils.isEmpty(beanAppFtpConfig.uploadSize)){
						UploadSize = Integer.parseInt(beanAppFtpConfig.uploadSize);
					}
				}else{ //自定义服务器
					DownUser = beanAppFtpConfig.customDownAccount;
					DownPasswd = beanAppFtpConfig.customDownPassword;
					DownIp = beanAppFtpConfig.customDownIp;
					DownPath = beanAppFtpConfig.customDownPath;
					if(!TextUtils.isEmpty(beanAppFtpConfig.customDownPort)){
						DownPort = Integer.parseInt(beanAppFtpConfig.customDownPort);
					}
					downHostType = "";
					downHostName = beanAppFtpConfig.hostType;
					UploadUser = beanAppFtpConfig.customUpAccount;
					UploadPasswd = beanAppFtpConfig.customUpPassword;
					UploadIp = beanAppFtpConfig.customUpIp;
					if(!TextUtils.isEmpty(beanAppFtpConfig.customUpPort)){
						UploadPort = Integer.parseInt(beanAppFtpConfig.customUpPort);
					}
					upHostType = "";
					upHostName = beanAppFtpConfig.hostType;
					if(!TextUtils.isEmpty(beanAppFtpConfig.customUpSize)){
						UploadSize = Integer.parseInt(beanAppFtpConfig.customUpSize);
					}
				}
			}

			initFtpTestLogInfo(DownIp, DownPath, DownPort, UploadIp, UploadPort, downHostType, upHostType, downHostName, upHostName);
			appTest = new StringBuffer();
			appTest.append(" downIp=" + DownIp);
			appTest.append(" DownPort=" + DownPort);
			appTest.append(" DownUser=" + DownUser);
			appTest.append(" DownPasswd=" + DownPasswd);
			appTest.append(" DownPath=" + DownPath);
			appTest.append(" downHostType=" + downHostType);
			appTest.append(" UploadIp=" + UploadIp);
			appTest.append(" UploadPort=" + UploadPort);
			appTest.append(" UploadUser=" + UploadUser);
			appTest.append(" UploadPasswd=" + UploadPasswd);
			appTest.append(" upHostType=" + upHostType);

			WybLog.syso("下载ip：" + DownIp + "-下载端口：" + DownPort + "-下载帐号：" + DownUser + "-下载密码：" + DownPasswd + "-下载线程："
					+ DownThread + "-下载地址：" + DownPath + "-下载大小：" + DownLenght + "上传IP：" + UploadIp + "-上传端口：" + UploadPort
					+ "-上传帐号：" + UploadUser + "-上传密码：" + UploadPasswd + "-上传线程：" + UploadThread + "-上传大小：" + UploadSize);
			boolean isSucceed = false;
			isSucceed = initFtp(DownIp, DownPort, DownUser, DownPasswd, DownThread, DownPath, DownLenght, UploadIp,
					UploadPort, UploadUser, UploadPasswd, UploadThread, UploadSize);

			if (!isSucceed) {
				showCommon("请勿输入符号 \\");
				closeFtp(true);
				tv_speed_pk_group_test_status.setText("");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void connected(boolean isDown, int connectedType) {
		// 0准备下载 1连接成功 2下载错误/上传错误 -1超时 3正在上传下载 4完成
		switch (connectedType) {
		case 0:
			isAlive = true;
			tv_speed_pk_group_test_status.setText(isDown ? "下载正在连接ftp" : "上传正在连接ftp");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.ui_gray));
			break;
		case 1:
			tv_speed_pk_group_test_status.setText(isDown ? "正在下载测试" : "正在上传测试");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.ui_gray));
			break;
		case 2:
			isAlive = false;
			tv_speed_pk_group_test_status.setText(isDown ? "下载错误" : "上传错误");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.red));
			saveAppWarningLog(isDown ? "下载错误" : "上传错误");
			break;
		case 3:
			tv_speed_pk_group_test_status.setText(isDown ? "正在下载" : "正在上传");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.ui_gray));
			break;
		case 4:
			isAlive = false;
			tv_speed_pk_group_test_status.setText("测试完成");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.ui_gray));
			break;
		case 5:
			isAlive = false;
			tv_speed_pk_group_test_status.setText("下载文件不存在");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.red));
			saveAppWarningLog("下载文件不存在");
			break;
		case 6:
			isAlive = true;
			tv_speed_pk_group_test_status.setText("上传数据准备中");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.ui_gray));
			break;
		case -1:
			isAlive = false;
			tv_speed_pk_group_test_status.setText("测试超时");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.red));
			saveAppWarningLog("测试超时");
			break;
		case -2:
			isAlive = false;
			tv_speed_pk_group_test_status.setText("ftp连接失败");
			tv_speed_pk_group_test_status.setTextColor(getResources().getColor(R.color.red));
			saveAppWarningLog("ftp连接失败");
			break;
		default:
			break;
		}
	}

	@Override
	public void updateProgress(boolean isDown, float secondSize) {
		if (receiverSignal == null) {
			mLastSignal = new Signal(this, getUserID());
		}
		mLastSignal.setUserId(getUserID());
		mLastSignal.rate = (float) UtilHandler.getInstance().toDfSum(secondSize * 8 / 1024f / 1024f, "00");
		mLastSignal.netType = NetInfoUtil.getOperatorNetworkType(this);
		String operatorNetworkType = mLastSignal.netType + "：";
		tvPlayBackCellName.setText(operatorNetworkType);
		if (mLastSignal.getTypeNet().equals("NR") || mLastSignal.getTypeNet().equals("LTE")) {
            if(mLastSignal.getTypeNet().equals("NR")){
                if(mLastSignal.isSA()){
                    mLastSignal.cellId = "460-00-" + mLastSignal.lte_gnb_nr + "-" + mLastSignal.lte_cellid_nr;
                    tvPlayBackCellName.setText(operatorNetworkType + mLastSignal.lte_name_nr);
                }else{
                    mLastSignal.cellId = "460-00-" + mLastSignal.getLte_enb() + "-" + mLastSignal.getLte_cid();
                    tvPlayBackCellName.setText(operatorNetworkType + mLastSignal.getLte_name());
                }
            }else{
                mLastSignal.cellId = "460-00-" + mLastSignal.getLte_enb() + "-" + mLastSignal.getLte_cid();
                tvPlayBackCellName.setText(operatorNetworkType + mLastSignal.getLte_name());
            }

			tlSignalInfoPlayBack.setVisibility(View.VISIBLE);
			tlSignalInfoGsmPlayBack.setVisibility(View.GONE);
			tvPlayBackTac.setText(mLastSignal.getLte_tac());
			tvPlayBackPci.setText(mLastSignal.getLte_pci());
			tvPlayBackCGI.setText(mLastSignal.getLte_cgi());
			tvPlayBackENB.setText(mLastSignal.getLte_enb());
			tvPlayBackCELLID.setText(mLastSignal.getLte_cid());
			tvPlayBackPindian.setText(mLastSignal.getLte_band());
			tvPlayBackRSRP.setText(mLastSignal.getLte_rsrp());
			tvPlayBackSINR.setText(mLastSignal.getLte_sinr());

		} else if (mLastSignal.getTypeNet().equals("GSM")) {
			mLastSignal.cellId = mLastSignal.getGsm_cid();

			tlSignalInfoPlayBack.setVisibility(View.GONE);
			tlSignalInfoGsmPlayBack.setVisibility(View.VISIBLE);
			tvPlayBackCellName.setText(operatorNetworkType + mLastSignal.getGsm_name());
			tvPlayBackTacGSM.setText(mLastSignal.getGsm_lac());
			tvPlayBackCELLIDGSM.setText(mLastSignal.getGsm_cid());
			// 频段
			if (!TextUtils.isEmpty(mLastSignal.getTd_pccpch())) {
				tvPlayBackBcchPDGSM.setText(mLastSignal.getTd_pccpch());
			} else {
				tvPlayBackBcchPDGSM.setText("");
			}
			tvPlayBackRXLGSM.setText(mLastSignal.getGsm_rxl());
		}
		tvSpeedTestPlayBackData.setText((mLastSignal.rate > 0 ? mLastSignal.rate : 0) + "");
		tvSpeedTestPlayBackGsmData.setText((mLastSignal.rate > 0 ? mLastSignal.rate : 0) + "");
		if (isDown) {
			mLastSignal.testType = 1;
			try {
				mSignalTestDownList.add((Signal)mLastSignal.clone());
			} catch (CloneNotSupportedException e) {
				mSignalTestDownList.add(mLastSignal);
			}
		} else {
			mLastSignal.testType = 2;
			try {
				mSignalTestUploadList.add((Signal)mLastSignal.clone());
			} catch (CloneNotSupportedException e) {
				mSignalTestUploadList.add(mLastSignal);
			}
		}
		mNewCharView.addValue(mLastSignal, secondSize * 8 / 1024 / 1024, isDown);
		updateView(isDown);
		setSignalDataPlayBack(mLastSignal, -1);
	}

	private boolean isDownload;

	private void updateView(boolean isDown) {
		isDownload = isDown;
		if (isDown) {// 下载
			mNewCharView.setTestedDownOrUpData(mSignalTestDownList, isDown);
			tvSpeedTestPlayBackType.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
			tvSpeedTestPlayBackData.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
			tvSpeedTestPlayBackGsmType.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
			tvSpeedTestPlayBackGsmData.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
			v_ftp_test_down_or_up_color.setBackgroundResource(R.color.ftp_download_fea916);
			tv_ftp_test_down_or_up.setTextColor(getResources().getColor(R.color.ftp_download_fea916));
			tvSpeedTestPlayBackType.setText("下载");
			tvSpeedTestPlayBackGsmType.setText("下载");
			tv_ftp_test_down_or_up.setText("下载");
			//			tv_speed_test_server_name_and_ip.setText(mFtpTestInfo.getDownHostTypeName() + "  " + UtilHandler.getInstance().ipFormat(mFtpTestInfo.getDownHostAddr()));

		} else {// 上传
			mNewCharView.setTestedDownOrUpData(mSignalTestUploadList, isDown);
			tvSpeedTestPlayBackType.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
			tvSpeedTestPlayBackData.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
			tvSpeedTestPlayBackGsmType.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
			tvSpeedTestPlayBackGsmData.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
			v_ftp_test_down_or_up_color.setBackgroundResource(R.color.ftp_upload_65c40b);
			tv_ftp_test_down_or_up.setTextColor(getResources().getColor(R.color.ftp_upload_65c40b));
			tvSpeedTestPlayBackType.setText("上传");
			tvSpeedTestPlayBackGsmType.setText("上传");
			tv_ftp_test_down_or_up.setText("上传");
			//			tv_speed_test_server_name_and_ip.setText(mFtpTestInfo.getUpHostTypeName() + "  " + UtilHandler.getInstance().ipFormat(mFtpTestInfo.getUpHostAddr()));
		}
	}

	private void setSignalDataPlayBack(Signal signal, int position) {
		if (position != -1) {
			tvPlayBackCellName.setText(signal.netType + "第" + (position + 1) + "秒：");
		} else {
			tvPlayBackCellName.setText(signal.netType + "：");
		}
		if (signal != null) {
			float rate = signal.rate;
			if (signal.getTypeNet() != null && (signal.getTypeNet().equals("NR") || signal.getTypeNet().equals("LTE"))) {
				if (position != -1) {
					tvPlayBackCellName.setText("第" + (position + 1) + "秒(" + signal.netType + ")：" + signal.getLte_name());
				} else {
					tvPlayBackCellName.setText(signal != null ? signal.netType + "：" + signal.getLte_name() : signal.netType);
				}
				tvPlayBackTac.setText(signal.getLte_tac());
				tvPlayBackPci.setText(signal.getLte_pci());
				tvPlayBackCGI.setText(signal.getLte_cgi());
				tvPlayBackENB.setText(signal.getLte_enb());
				tvPlayBackCELLID.setText(signal.getLte_cid());
				tvPlayBackPindian.setText(signal.getLte_band());
				tvPlayBackRSRP.setText(signal.getLte_rsrp());
				tvPlayBackSINR.setText(signal.getLte_sinr());
				tlSignalInfoPlayBack.setVisibility(View.VISIBLE);
				tlSignalInfoGsmPlayBack.setVisibility(View.GONE);
				tvSpeedTestPlayBackData.setText((rate > 0 ? rate : 0) + "");

			} else if (signal.getTypeNet() != null && signal.getTypeNet().equals("GSM")) {
				if (position != -1) {
					tvPlayBackCellName.setText("第" + (position + 1) + "秒(" + signal.netType + ")：" + signal.getGsm_name());
				} else {
					tvPlayBackCellName.setText(signal != null ? signal.netType + "：" + signal.getGsm_name() : signal.netType);
				}
				tvPlayBackTacGSM.setText(signal.getGsm_lac());
				tvPlayBackCELLIDGSM.setText(signal.getGsm_cid());
				// 频段
				if (!TextUtils.isEmpty(signal.getTd_pccpch())) {
					tvPlayBackBcchPDGSM.setText(signal.getTd_pccpch());
				} else {
					tvPlayBackBcchPDGSM.setText("");
				}
				tvPlayBackRXLGSM.setText(signal.getGsm_rxl());
				tlSignalInfoPlayBack.setVisibility(View.GONE);
				tlSignalInfoGsmPlayBack.setVisibility(View.VISIBLE);
				tvSpeedTestPlayBackGsmData.setText((rate > 0 ? rate : 0) + "");
			}
		}
	}

	@Override
	public void onProgressSecond(boolean isDown, float max, float min, float avg, long duration, double length) {
		super.onProgressSecond(isDown, max, min, avg, duration, length);
	}

	@Override
	public void progressOk(float maxDown, float minDown, float avgDown, int downTime, float sumDowns, float maxUpload, float minUpload, float avgUpload, int uploadTime, float sumUploads) {
		stopAnimation();
		mNewCharView.setEnabled(true);

		if ((mSignalTestDownList == null || mSignalTestDownList.size() == 0) && mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {
			initCharView(false);
		} else {
			initCharView(true);
		}
		saveFtpTestLog(mLastSignal, avgDown, maxDown, avgUpload, maxUpload);
	}

	private void initCharView(boolean isDown) {
		tv_ftp_test_switch.setVisibility(View.VISIBLE);
		if (isDown) {
			tv_ftp_test_switch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_download, 0, 0, 0);
			if (mSignalTestDownList != null && mSignalTestDownList.size() > 0) {
				setSignalDataPlayBack(mSignalTestDownList.get(0), 0);
				mNewCharView.setTestedDownOrUpData(mSignalTestDownList, isDown);
			}

		} else {
			tv_ftp_test_switch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_speed_test_upload, 0, 0, 0);
			if (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {
				setSignalDataPlayBack(mSignalTestUploadList.get(0), 0);
				mNewCharView.setTestedDownOrUpData(mSignalTestUploadList, isDown);
			}
		}
		updateView(isDown);
	}

	@Override
	public void onBackPressed() {
		new CommonDialog(SpeedPkActivity.this).setButtonText("退出", "取消").setListener(new ListenerBack() {
			@Override
			public void onListener(int type, Object object, boolean isTrue) {
				if (isTrue) {
					requestModifyPkGroupStatus(SpeedPkGroupActivity.OP_TYPE_COMPLETE); // 完成测试
				}
			}
		}).show("正在测试中,是否退出?");
	}

	/**
	 * 修改PK组状态
	 * 
	 * @param status
	 */
	private void requestModifyPkGroupStatus(int status) {
		showMyDialog();
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_GROUP_MODIFY_STATUS);
		RequestSpeedPkModifyGroupStatus requset = new RequestSpeedPkModifyGroupStatus();
		requset.status = status;
		requset.id = mFtpTestInfo.id;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(url, jsonData, R.id.net_speed_pk_group_modify_status, this);
	}

	private void clearSpeedTestData() {
		mPingInfo = null;
		mLogId = 0;
		mNewCharView.clearData();

		mSignalTestDownList.clear();
		mSignalTestUploadList.clear();
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_sl);
	}

	@Override
	public void singleResult(boolean isDown, float avg, float sum) {
		super.singleResult(isDown, avg, sum);
		if(isDown){
			mAllDownload = sum;
		}else{
			mAllUpload = sum;
		}
	}

	private void saveFtpTestLog(final Signal signal, final double downSpeedAvg, final double downSpeedMax, final double upSpeedAvg, final double upSpeedMax) {
		showMyDialog();
		//以下做上传数据处理
		new Thread(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				if(DEBUG){
					//统计平均sinr rsrp
					int sumDownRsrp = 0, sumDownSinr = 0;
					int sumUploadRsrp = 0, sumUploadSinr = 0;
					int sumCountDownRsrp = 0, sumCountDownSinr = 0;
					int sumCountUploadRsrp = 0, sumCountUploadSinr = 0;
					for(Signal downSignal : mSignalTestDownList){
						if(!TextUtils.isEmpty(downSignal.getLte_rsrp())){
							sumDownRsrp += Integer.parseInt(downSignal.getLte_rsrp());
							sumCountDownRsrp++;
						}
						if(!TextUtils.isEmpty(downSignal.getLte_sinr())){
							sumDownSinr += Integer.parseInt(downSignal.getLte_sinr());
							sumCountDownSinr++;
						}
					}
					for(Signal uploadSignal : mSignalTestUploadList){
						if(!TextUtils.isEmpty(uploadSignal.getLte_rsrp())){
							sumUploadRsrp += Integer.parseInt(uploadSignal.getLte_rsrp());
							sumCountUploadRsrp++;
						}
						if(!TextUtils.isEmpty(uploadSignal.getLte_sinr())){
							sumUploadSinr += Integer.parseInt(uploadSignal.getLte_sinr());
							sumCountUploadSinr++;
						}
					}

					int averageDownRsrp = 0;
					int averageDownSinr = 0;
					int averageUploadRsrp = 0;
					int averageUploadSinr = 0;
					if(sumCountDownRsrp > 0){
						averageDownRsrp = sumDownRsrp / sumCountDownRsrp;
					}
					if(sumCountDownSinr > 0){
						averageDownSinr = sumDownSinr / sumCountDownSinr;
					}
					if(sumCountUploadRsrp > 0){
						averageUploadRsrp = sumUploadRsrp / sumCountUploadRsrp;
					}
					if(sumCountUploadSinr > 0){
						averageUploadSinr = sumUploadSinr / sumCountUploadSinr;
					}
					SpeedTestUtil.getInstance().writeToText("pk测速--下载平均Rsrp,Sinr:" + averageDownRsrp+","+averageDownSinr+
							"上传平均Rsrp,Sinr:" + averageUploadRsrp+","+averageUploadSinr, "");
				}

				double mDownSpeedMax = downSpeedMax;
				double mUpSpeedMax = upSpeedMax;

				if (mSignalTestDownList != null && mSignalTestDownList.size() > 0) {

					if(isAlgorithm){
						algorithm1(mSignalTestDownList);
					}else{
						algorithm2(mSignalTestDownList);
					}

					Signal signalRrateMax = Collections.max(mSignalTestDownList, new Comparator<Signal>() {
						@Override
						public int compare(Signal lhs, Signal rhs) {
							if (lhs.rate > rhs.rate) {
								return 1;
							} else if (lhs.rate < rhs.rate) {
								return -1;
							} else {
								return 0;
							}
						}
					});
					WybLog.i("zjh", "downSpeedMax == " + downSpeedMax);
					WybLog.i("zjh", "signalRrateMax.rate == " + signalRrateMax.rate);
					if (downSpeedMax > signalRrateMax.rate) {
						mDownSpeedMax = signalRrateMax.rate;
					}
				}
				if (mSignalTestUploadList != null && mSignalTestUploadList.size() > 0) {

					if(isAlgorithm){
						algorithm1(mSignalTestUploadList);
					}else{
						algorithm2(mSignalTestUploadList);
					}

					Signal signalRrateMax = Collections.max(mSignalTestUploadList, new Comparator<Signal>() {
						@Override
						public int compare(Signal lhs, Signal rhs) {
							if (lhs.rate > rhs.rate) {
								return 1;
							} else if (lhs.rate < rhs.rate) {
								return -1;
							} else {
								return 0;
							}
						}
					});
					WybLog.i("zjh", "upSpeedMax == " + upSpeedMax);
					WybLog.i("zjh", "signalRrateMax.rate == " + signalRrateMax.rate);
					if (upSpeedMax > signalRrateMax.rate) {
						mUpSpeedMax = signalRrateMax.rate;
					}
				}
				BeanAppFtpConfig config = getSelectServer();
				mFtpTestInfo.logId = mLogId;
				mFtpTestInfo.operator = config.operator;
				mFtpTestInfo.downEstimateFlow = String.valueOf(SpeedTestDataSet.mDownFlow);
				mFtpTestInfo.upEstimateFlow = String.valueOf(SpeedTestDataSet.mUpFlow);
				mFtpTestInfo.downRealFlow = String.valueOf(mAllDownload);
				mFtpTestInfo.upRealFlow = String.valueOf(mAllUpload);
				mFtpTestInfo.setDownSpeedAvg(UtilHandler.getInstance().toDfSum(downSpeedAvg, "00"));
				mFtpTestInfo.setDownSpeedMax(UtilHandler.getInstance().toDfSum(mDownSpeedMax, "00"));
				mFtpTestInfo.setUpSpeedAvg(UtilHandler.getInstance().toDfSum(upSpeedAvg, "00"));
				mFtpTestInfo.setUpSpeedMax(UtilHandler.getInstance().toDfSum(mUpSpeedMax, "00"));
				mFtpTestInfo.downloadTestList = mSignalTestDownList;
				mFtpTestInfo.uploadTestList = mSignalTestUploadList;
				if(mPingInfo != null){
					mFtpTestInfo.ping = String.valueOf(mPingInfo.getTimes());
					mFtpTestInfo.jitter= String.valueOf(mPingInfo.shake);
					mFtpTestInfo.packetLoss = String.valueOf(mPingInfo.packetLoss);
				}

				if (signal != null) {
					if (signal.getTypeNet().equals("NR") || signal.getTypeNet().equals("LTE")) {
                        if(signal.getTypeNet().equals("NR")){
                            if(signal.isSA()){
                                mFtpTestInfo.setCellId("460-00-" + signal.lte_gnb_nr + "-" + signal.lte_cellid_nr);
                                mFtpTestInfo.setLac(signal.lte_tac_nr);// Lac/Tac
                                mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.lte_pci_nr, 0));// pci
                                mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.lte_band_nr, 0));// 频点
                                mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.lte_rsrp_nr, 0));// rsrp/exl
                                mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.lte_sinr_nr, 0));// sinr
                                mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.lte_rsrq_nr, 0));// rsrq
                                mFtpTestInfo.setCellName(signal.lte_name_nr);
                            }else{
                                mFtpTestInfo.setCellId("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                                mFtpTestInfo.setLac(signal.getLte_tac());// Lac/Tac
                                mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.getLte_pci(), 0));// pci
                                mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getLte_band(), 0));// 频点
                                mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getLte_rsrp(), 0));// rsrp/exl
                                mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.getLte_sinr(), 0));// sinr
                                mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.getLte_rsrq(), 0));// rsrq
                                mFtpTestInfo.setCellName(signal.getLte_name());
                            }
                        }else{
                            mFtpTestInfo.setCellId("460-00-" + signal.getLte_enb() + "-" + signal.getLte_cid());
                            mFtpTestInfo.setLac(signal.getLte_tac());// Lac/Tac
                            mFtpTestInfo.setPci(UtilHandler.getInstance().toInt(signal.getLte_pci(), 0));// pci
                            mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getLte_band(), 0));// 频点
                            mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getLte_rsrp(), 0));// rsrp/exl
                            mFtpTestInfo.setSinr(UtilHandler.getInstance().toInt(signal.getLte_sinr(), 0));// sinr
                            mFtpTestInfo.setRsrq(UtilHandler.getInstance().toInt(signal.getLte_rsrq(), 0));// rsrq
                            mFtpTestInfo.setCellName(signal.getLte_name());
                        }
					} else if (signal.getTypeNet().equals("GSM")) {
						mFtpTestInfo.setCellId(signal.getGsm_cid());
						mFtpTestInfo.setLac(signal.getGsm_lac());
						mFtpTestInfo.setEarfcn(UtilHandler.getInstance().toInt(signal.getTd_pccpch(), 0));
						mFtpTestInfo.setRsrp(UtilHandler.getInstance().toInt(signal.getGsm_rxl(), 0));
						mFtpTestInfo.setCellName(signal.getGsm_name());
					}
					mFtpTestInfo.setSignalType(signal.getTypeNet());
				}
				mFtpTestInfo.setNetType(NetInfoUtil.getOperatorNetworkType(SpeedPkActivity.this));
				mFtpTestInfo.loginName = getUser();
				mFtpTestInfo.userName = getUserName();
				String isp = SystemUtil.getInstance().getISP(SpeedPkActivity.this);
				mFtpTestInfo.operator = TextUtils.isEmpty(isp) ? "未检测到SIM卡" : isp;
				mFtpTestInfo.phoneModel = SystemUtil.getInstance().getSystemModel();
				mFtpTestInfo.regionName = regionName;

				if (gpsLatlng != null) {
					mFtpTestInfo.setLatitude(gpsLatlng.latitude);
					mFtpTestInfo.setLongitude(gpsLatlng.longitude);
				}

				mFtpTestInfo.setTestEnd(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));


				List<Db_JJ_FTPTestInfo> list = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance()
						.queryObj(Db_JJ_FTPTestInfo.class, "testBegin =? and isUpload =? and userid =?", new Object[] {
							mFtpTestInfo.getTestBegin(), false, UtilHandler.getInstance().toInt(getUserID(), 0) });
				if (list == null || list.size() == 0) {
					DbHandler.getInstance().insert(mFtpTestInfo);
					mUploadFtpTestList = (List<Db_JJ_FTPTestInfo>) DbHandler.getInstance().queryObj(
							Db_JJ_FTPTestInfo.class, "isUpload =? and userid =?",
							new Object[] { false, UtilHandler.getInstance().toInt(getUserID(), 0) });

					for (int i = 0; i < mUploadFtpTestList.size(); i++) {
						Db_JJ_FTPTestInfo ftpTestInfo = mUploadFtpTestList.get(i);
						if (ftpTestInfo.getTestBegin().equals(mFtpTestInfo.getTestBegin())) {
							// 存储每秒下载(和上传)详情
							for (Signal signal : mSignalTestDownList) {
								signal.lastId = ftpTestInfo.get_id();
								signal.testBegin = ftpTestInfo.getTestBegin();
								signal.testEnd = ftpTestInfo.getTestEnd();
								DbHandler.getInstance().save(signal);

							}
							for (Signal signal : mSignalTestUploadList) {
								signal.lastId = ftpTestInfo.get_id();
								signal.testBegin = ftpTestInfo.getTestBegin();
								signal.testEnd = ftpTestInfo.getTestEnd();
								DbHandler.getInstance().save(signal);
							}
						}
						SystemClock.sleep(10);
						ftpTestInfo.downloadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
								"lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
								new Object[] { ftpTestInfo.get_id(), getUserID(), 1, ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd() });
						ftpTestInfo.uploadTestList = (List<Signal>) DbHandler.getInstance().queryObj(Signal.class,
								"lastId=? and userId=? and testType=? and testBegin=? and testEnd=?",
								new Object[] { ftpTestInfo.get_id(), getUserID(), 2, ftpTestInfo.getTestBegin(), ftpTestInfo.getTestEnd() });

						WybLog.i("zjh", "ftpTestInfo.downloadTestList.size = " + ftpTestInfo.downloadTestList.size());
						WybLog.i("zjh", "ftpTestInfo.uploadTestList.size = " + ftpTestInfo.uploadTestList.size());
					}
					mHandler.sendEmptyMessage(HANDLER_QUERY_FTP_UPLOAD_DATA);
				}
			}
		}).start();
	}

	private void saveAppWarningLog(String reason) {
		stopAnimation();
		mAppWarningInfo = new Db_JJ_AppWarningLog();
		mAppWarningInfo.setClassName(getClass().getSimpleName());
		mAppWarningInfo.setOperateInfo(getResources().getString(R.string.speed_pk));
		mAppWarningInfo.setMobileType(android.os.Build.MODEL);
		mAppWarningInfo.setWarningInfo(reason + " " + (appTest == null ? "" : appTest.toString()));
		mAppWarningInfo.setWarningTime(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));
		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				DbHandler.getInstance().insert(mAppWarningInfo);
				mAppWarningList = (List<Db_JJ_AppWarningLog>) DbHandler.getInstance()
						.queryObj(Db_JJ_AppWarningLog.class, "isUpload =?", new Object[] { false });
				mHandler.sendEmptyMessage(HANDLER_QUERY_APP_WARNING_UPLOAD_DATA);
			}
		}).start();
	}

	private void initFtpTestLogInfo(String downHostAddr, String downPath, Integer downPort, String upHostAddr,
			Integer upPort, String downHostType, String upHostType, String donwHostName, String upHostName) {
		// mFtpTestInfo = new Db_JJ_FTPTestInfo();
		if(mFtpTestInfo != null){
			mFtpTestInfo.setUserId(UtilHandler.getInstance().toInt(getUserID(), 0));
			mFtpTestInfo.setDownHostAddr(downHostAddr);
			mFtpTestInfo.setDownPath(downPath);
			mFtpTestInfo.setDownPort(downPort == null ? 21 : downPort);
			mFtpTestInfo.setSourceType(getResources().getString(R.string.speed_pk));
			mFtpTestInfo.setTestBegin(TimeUtil.getInstance().getNowTimeSS(System.currentTimeMillis()));
			mFtpTestInfo.setUpHostAddr(upHostAddr);
			mFtpTestInfo.setUpPort(upPort == null ? 21 : upPort);
			mFtpTestInfo.setDownHostType(downHostType);
			mFtpTestInfo.setUpHostType(upHostType);
			mFtpTestInfo.setDownHostTypeName(donwHostName);
			mFtpTestInfo.setUpHostTypeName(upHostName);
		}
	}

	// 采集点信息上传
	private ArrayList<HashMap<String, Object>> mArrSignalUp = new ArrayList<HashMap<String, Object>>();
	// 采集点信息下载
	private ArrayList<HashMap<String, Object>> mArrSignalDown = new ArrayList<HashMap<String, Object>>();

	@Override
	public void updateSignal(boolean isDown, long[] result) {
		String value = String.valueOf((float) UtilHandler.getInstance().toDfSum(result[1] / 1024 * 8.0, "00"));
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("flow_time", result[0]);
		map.put("flow", result[1]);
		if (mLastSignal != null) {
			if (isDown) {
				mLastSignal.setDl(value);
				map.put("signal", mLastSignal);
				mArrSignalDown.add(map);
			} else {
				mLastSignal.setUl(value);
				map.put("signal", mLastSignal);
				mArrSignalUp.add(map);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ww_speed_pk_wave_view.pauseAnimation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ww_speed_pk_wave_view.resumeAnimation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAnimation();
	}

	private void stopAnimation() {
		iv_speed_pk_bg.clearAnimation();
		ww_speed_pk_wave_view.stopAnimation();
	}
}
