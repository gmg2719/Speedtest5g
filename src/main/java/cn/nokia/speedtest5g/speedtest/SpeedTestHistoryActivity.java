package cn.nokia.speedtest5g.speedtest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.respon.FtpSpeedHistoryResponse;
import cn.nokia.speedtest5g.app.tx.activity.PullToRefreshListView;
import cn.nokia.speedtest5g.app.tx.activity.PullToRefreshListView.OnLoadListener;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedTestHistoryAdapter;
import cn.nokia.speedtest5g.speedtest.bean.RequestFtpHistory;
import cn.nokia.speedtest5g.speedtest.bean.RequestRemoveLog;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 速率测试历史数据列表
 * @author JQJ
 *
 */
public class SpeedTestHistoryActivity extends BaseActionBarActivity{

	public static final String ON_RESUME_UPDATE = "cn.nokia.speedtest5g.ftphistory.onresume";
	public static final String ON_UPDATE_HISTORY = "cn.nokia.speedtest5g.ftphistory.onupdate.history";

	public static final int WHAT_ITEM_ON_CLICK = -100;
	public static final int WHAT_ITEM_ON_LONG_CLICK = -200;

	private PullToRefreshListView lvFtpHistory = null;
	private View llNoData = null;
	private List<Db_JJ_FTPTestInfo> mList = new ArrayList<Db_JJ_FTPTestInfo>();
	private SpeedTestHistoryAdapter mAdapter = null;
	private View footView = null;	
	private LinearLayout mLlNoNet = null;	
	private TextView mTvGotoSetting = null;

	private long mCurrentUserId = -1;
	private int mPagerNo = 1;
	private boolean mIsAddDelete = false;

	private UpdateConfigReceiver mReceiver = new UpdateConfigReceiver();
	private OnResumeReceiver mOnResumeReceiver = new OnResumeReceiver();
	private OnUpdateHistoryReceiver mOnUpdateHistoryReceiver = new OnUpdateHistoryReceiver();
	private Db_JJ_FTPTestInfo mDeleteInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_history_activity);
        mBgTopColor = R.color.bm_bg_color;
        mBgParentColor = R.color.bm_bg_color;
        mTitleTextColor = R.color.black_small;

		init("测试历史", true);

		registerReceiver(mReceiver, new IntentFilter(TypeKey.getInstance().ACTION_MAINHOME_SUPER));
		registerReceiver(mOnResumeReceiver, new IntentFilter(ON_RESUME_UPDATE));
		registerReceiver(mOnUpdateHistoryReceiver, new IntentFilter(ON_UPDATE_HISTORY));

		initData();
//		loadData();
	}

	@SuppressLint("InflateParams")
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		lvFtpHistory = (PullToRefreshListView) findViewById(R.id.speed_test_history_lv_ftp_history);
		llNoData = findViewById(R.id.speed_test_history_ll_no_data);
		footView = LayoutInflater.from(SpeedTestHistoryActivity.this).inflate(R.layout.jj_footview_no_data2, null);

		mLlNoNet = (LinearLayout)findViewById(R.id.speed_test_history_ll_no_net);
		mTvGotoSetting = (TextView)findViewById(R.id.speed_test_history_tv_settings);
		mTvGotoSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Settings.ACTION_SETTINGS));				
			}
		});

		mAdapter = new SpeedTestHistoryAdapter(mActivity, SpeedTestHistoryActivity.this);
		lvFtpHistory.setAdapter(mAdapter);

		lvFtpHistory.removeHeader();
		lvFtpHistory.setPullEnable(true);
		lvFtpHistory.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {
				mPagerNo++;
				requestServerLogData(mPagerNo);
			}
		});
	}

	/**
	 * 请求服务器测速历史记录
	 * @param pageNo
	 */
	public void requestServerLogData(int pageNo){
		RequestFtpHistory request = new RequestFtpHistory();
		//用户id
		mCurrentUserId = UtilHandler.getInstance().toLong(getUserID(), 0);
		request.setUserid(mCurrentUserId);
		request.setPageNo(pageNo);
		NetWorkUtilNow.getInstances().readNetworkPostJsonObject(
				NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_SPEED_TEST_LOG),
				JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

					@Override
					public void onListener(int type, Object object, boolean isTrue) {
						FtpSpeedHistoryResponse response = JsonHandler.getHandler().getTarget(object.toString(),
								FtpSpeedHistoryResponse.class);
						if (response != null) {
							if (response.isRs()) {
								//根据id去除重复数据
								List<Db_JJ_FTPTestInfo> newData = new ArrayList<Db_JJ_FTPTestInfo>();
								List<Db_JJ_FTPTestInfo> serverList = SpeedTestDataSet.mServerList;
								List<Db_JJ_FTPTestInfo> datas = response.getDatas();
								if(datas !=null && datas.size() > 0){
                                    if(mPagerNo == 1){
                                        SpeedTestDataSet.mServerList.clear();
                                        SpeedTestDataSet.mServerList.addAll(datas);
                                        loadData();
                                    }else{
                                        for(Db_JJ_FTPTestInfo temp : datas){
                                            boolean isExist = false;
                                            for(Db_JJ_FTPTestInfo info : serverList){
                                                if(temp.id == info.id){
                                                    isExist = true;
                                                    break; //跳出内部循环
                                                }
                                            }
                                            if(!isExist){
                                                newData.add(temp);
                                            }
                                        }
                                        if(newData.size() > 0){ //有新数据才刷新
                                            SpeedTestDataSet.mServerList.addAll(newData);
                                            loadData();
                                        }
                                    }
								}else{
									if(mPagerNo > 1){
										lvFtpHistory.removeFooterView(footView);
										lvFtpHistory.addFooterView(footView);
										lvFtpHistory.setLoadEnable(false);
									}else{
										loadData();
									}
								}
								lvFtpHistory.onLoadComplete();
							}else{
								showCommon(response.getMsg());
							}
						}
					}
				});
	}

	//加载数据
	private void loadData(){
		mList.clear();
		mList.addAll(SpeedTestDataSet.mServerList);
		lvFtpHistory.setVisibility(View.VISIBLE);
		mLlNoNet.setVisibility(View.GONE);
		if(mList.size() > 0){
			if(!mIsAddDelete){
				actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_speed_test_delete_flag, EnumRequest.MENU_SELECT_ONE.toInt(), ""));
				mIsAddDelete = true;
			}
			llNoData.setVisibility(View.GONE);
		}else{
			actionBar.removeAllActions();
			mIsAddDelete = false;
			llNoData.setVisibility(View.VISIBLE);
		}
		mAdapter.updateData(mList);	
	}

	/**
	 * 登陆登出更新广播
	 * @author JQJ
	 *
	 */
	private class UpdateConfigReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(arg1.getAction().equals(TypeKey.getInstance().ACTION_MAINHOME_SUPER)){
				int type = arg1.getIntExtra("type", -1);
				if(type == -1000){
					//判断userid不一致 则清空历史记录
					if(mCurrentUserId != UtilHandler.getInstance().toLong(getUserID(), 0)){
						SpeedTestDataSet.mServerList.clear();
						mPagerNo = 1;
						//清空历史记录
						loadData();
					}

					//刷新水印
					WaterMarkUtil.showWatermarkView(SpeedTestHistoryActivity.this, getUser());
				}
			}
		}
	}

	/**
	 * 更新广播   onResume  高版本系统  在tabActivit框架中不会调用
	 * @author JQJ
	 *
	 */
	private class OnResumeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(ON_RESUME_UPDATE)){
                initData();
			}
		}
	}

	private void initData(){
        //是否有网络
        if(NetInfoUtil.isNetworkConnected(mActivity)){
            requestServerLogData(1);
        }else{
            SpeedTestDataSet.mServerList.clear();
            mLlNoNet.setVisibility(View.VISIBLE);
            lvFtpHistory.setVisibility(View.GONE);
            llNoData.setVisibility(View.GONE);
        }
    }

	/**
	 * 删除日志后本地刷新日志
	 * @author JQJ
	 *
	 */
	private class OnUpdateHistoryReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ON_UPDATE_HISTORY)){
				loadData();
			}
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if(type == WHAT_ITEM_ON_CLICK) { //单条点击
			Db_JJ_FTPTestInfo info = (Db_JJ_FTPTestInfo) object;
			startSpeedTestDetailActivity(info);
		} else if(type == EnumRequest.MENU_SELECT_ONE.toInt()){ //删除全部
			new CommonDialog(SpeedTestHistoryActivity.this).setListener(SpeedTestHistoryActivity.this)
			.setButtonText("取消", "确定").show("是否删除所有记录？",
					EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
		} else if(type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()){
			if(!isTrue){
				removeAllLog();
			}
		} else if(type == WHAT_ITEM_ON_LONG_CLICK){ //长按删除
			mDeleteInfo = (Db_JJ_FTPTestInfo) object;
			if(mDeleteInfo != null){
				new CommonDialog(SpeedTestHistoryActivity.this).setListener(SpeedTestHistoryActivity.this)
				.setButtonText("取消", "确定").show( "是否删除记录？",
						EnumRequest.DIALOG_TOAST_BTN_TWO.toInt());
			}
		} else if(type == EnumRequest.DIALOG_TOAST_BTN_TWO.toInt()){
			if(!isTrue){
				removeSingleLog();
			}
		}
	}

	/**
	 * 删除日志
	 */
	private void removeSingleLog(){
		showMyDialog();
		RequestRemoveLog request = new RequestRemoveLog();
		request.id = mDeleteInfo.id;
		request.type=("SINGLE");
		request.userId=(UtilHandler.getInstance().toLong(getUserID(), 0));
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(
				NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_REMOVE_SPEED_TEST_LOG),
				JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

					@Override
					public void onListener(int type, Object object,
							boolean isTrue) {
						dismissMyDialog();
						if(isTrue){
							BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
							if(response != null){
								if(response.isRs()){ //删除成功
									List<Db_JJ_FTPTestInfo> list = SpeedTestDataSet.mServerList;
									if(list != null){
										for(Db_JJ_FTPTestInfo info : list){ //移除本地日志
											if(info.id == mDeleteInfo.id){
												list.remove(info);
												break;
											}
										}
									}
									showCommon("删除成功!");
									//通知刷新
									Intent intent = new Intent(SpeedTestHistoryActivity.ON_UPDATE_HISTORY);
									sendBroadcast(intent);
								}else{
									showCommon(response.getMsg());
								}
							}else{
								showCommon("删除失败");
							}
						}else{
							showCommon("删除失败");
						}
					}
				});
	}

	/**
	 * 删除日志
	 */
	private void removeAllLog(){
		showMyDialog();
		RequestRemoveLog request = new RequestRemoveLog();
		request.userId=(UtilHandler.getInstance().toLong(getUserID(), 0));
		request.type=("ALL");
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(
				NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_REMOVE_SPEED_TEST_LOG),
				JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

					@Override
					public void onListener(int type, Object object,
							boolean isTrue) {
						dismissMyDialog();
						if(isTrue){
							BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
							if(response != null){
								if(response.isRs()){ //删除成功
									showCommon("删除成功!");
									SpeedTestDataSet.mServerList.clear();
									loadData();
								}else{
									showCommon(response.getMsg());
								}
							}else{
								showCommon("删除失败");
							}
						}else{
							showCommon("删除失败");
						}
					}
				});
	}

	private void startSpeedTestDetailActivity(Db_JJ_FTPTestInfo info) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("fromWhere", true);
		bundle.putSerializable("mFtpTestInfo", info);
		goIntent(SpeedTestDetailActivity.class, bundle, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
            SpeedTestDataSet.mServerList.clear();
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
			if(mOnResumeReceiver != null){
				unregisterReceiver(mOnResumeReceiver);
			}
			if(mOnUpdateHistoryReceiver != null){
				unregisterReceiver(mOnUpdateHistoryReceiver);
			}
		}catch(Exception e){
		}
	}
}