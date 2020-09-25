package cn.nokia.speedtest5g.speedtest.pk;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FtpAddressGroupInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedPkGroupMemberAdapter;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkModifyGroupStatus;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkGroup;
import cn.nokia.speedtest5g.view.MyScrollyGridView;
import cn.nokia.speedtest5g.view.WaveButton;

/**
 * 速率PK组页面
 * @author JQJ
 *
 */
public class SpeedPkGroupActivity extends BaseActionBarHandlerActivity implements OnClickListener {
	
	private final int UPDATE_GROUP_INFO = 1;
	private int opType = -1;// 操作类型
	/**
	 * 解散小组
	 */
	public final static int OP_TYPE_DISBAND = 0;
	/**
	 * 退出小组
	 */
	public final static int OP_TYPE_EXIT = 2;
	/**
	 * 开始PK
	 */
	public final static int OP_TYPE_START_PK = 3;
	/**
	 * 加入
	 */
	public final static int OP_TYPE_JOIN = 4;
	/**
	 * 完成PK（速率测试开始后，中途退出测试）
	 */
	public final static int OP_TYPE_COMPLETE = 5;

	private TextView tv_speed_pk_group_name;
	private TextView tv_speed_pk_group_status;
	private MyScrollyGridView gv_speed_pk_group_member_grid_view;
	private WaveButton wb_speed_pk_group_join_start_btn;
	private TextView tv_speed_pk_group_cancel_or_exit;

	private SpeedPkGroupMemberAdapter groupMemberAdapter;
	private ArrayList<Db_JJ_FtpAddressGroupInfo> mFtpAddressList;
	private BeanSpeedPkGroupInfo speedPkGroupInfo;

	private boolean isJoined = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pk_group_activity);

		mFtpAddressList = getIntent().getParcelableArrayListExtra("mFtpAddressList");
		speedPkGroupInfo = (BeanSpeedPkGroupInfo) getIntent().getSerializableExtra("speedPkGroupInfo");
		init(R.string.speed_pk, true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		tv_speed_pk_group_name = (TextView) findViewById(R.id.tv_speed_pk_group_name);
		tv_speed_pk_group_status = (TextView) findViewById(R.id.tv_speed_pk_group_status);
		gv_speed_pk_group_member_grid_view = (MyScrollyGridView) findViewById(R.id.gv_speed_pk_group_member_grid_view);
		wb_speed_pk_group_join_start_btn = (WaveButton) findViewById(R.id.wb_speed_pk_group_join_start_btn);
		tv_speed_pk_group_cancel_or_exit = (TextView) findViewById(R.id.tv_speed_pk_group_cancel_or_exit);

		tv_speed_pk_group_name.setText(speedPkGroupInfo.rateGroupName);
		groupMemberAdapter = new SpeedPkGroupMemberAdapter(this, speedPkGroupInfo.groupMemberList);
		gv_speed_pk_group_member_grid_view.setAdapter(groupMemberAdapter);
		wb_speed_pk_group_join_start_btn.startAnimation();
		wb_speed_pk_group_join_start_btn.setOnClickListener(this);
		tv_speed_pk_group_cancel_or_exit.setOnClickListener(this);
		if (speedPkGroupInfo.creator.equals(getUser())) {
			wb_speed_pk_group_join_start_btn.setText(getString(R.string.start));

		} else {
			wb_speed_pk_group_join_start_btn.setText(getString(R.string.speed_pk_join));
		}
		updateBackBtn();
		if (speedPkGroupInfo.rateGroupStatus == 1) {
			sendMessage(new MyEvents(ModeEnum.OTHER, UPDATE_GROUP_INFO));
			tv_speed_pk_group_status.setText("准备开始……");
		} else if (speedPkGroupInfo.rateGroupStatus == 2) {
			tv_speed_pk_group_status.setText("正在进行PK……");
		} else if (speedPkGroupInfo.rateGroupStatus == 3) {
			tv_speed_pk_group_status.setText("完成PK……");
		}
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.wb_speed_pk_group_join_start_btn) {
            if (speedPkGroupInfo.creator.equals(getUser())) {
                requestModifyPkGroupStatus(OP_TYPE_START_PK); // 开始PK
            } else {
                requestModifyPkGroupStatus(OP_TYPE_JOIN); // 加入PK组
            }
        } else if (id == R.id.tv_speed_pk_group_cancel_or_exit) {
            doOnback();
        }
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
		opType = status;
		requset.status = status;
		requset.id = speedPkGroupInfo.id;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(url, jsonData, R.id.net_speed_pk_group_modify_status, this);
	}

	/**
	 * 更新PK组信息
	 */
	private void requestUpdatePkGroupInfo() {
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_GROUP_UPDATE_STATUS);
		RequestSpeedPkGroupInfo requset = new RequestSpeedPkGroupInfo();
		requset.id = speedPkGroupInfo.id;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonObjectNoCancel(url, jsonData, R.id.net_speed_pk_group_update_info, this);
	}

	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case OTHER:
			if (events.getType() == UPDATE_GROUP_INFO) {
				requestUpdatePkGroupInfo();
				sendMessage(new MyEvents(ModeEnum.OTHER, UPDATE_GROUP_INFO), 5000);
			}
			break;

		default:
			break;
		}
	}

	private CommonDialog commonDialog;

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (!isTrue) {
			dismissMyDialog();
			return;
		}
		if (type == EnumRequest.MENU_BACK.toInt()) {
			doOnback();

		} else if (type == R.id.net_speed_pk_group_update_info) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				BeanSpeedPkGroupInfo groupInfo = response.datas;
				speedPkGroupInfo.isCancel = groupInfo.isCancel;
				speedPkGroupInfo.isStart = groupInfo.isStart;
				speedPkGroupInfo.isJoined = groupInfo.isJoined;
				speedPkGroupInfo.groupMemberList.clear();
				speedPkGroupInfo.groupMemberList.addAll(groupInfo.groupMemberList);
				groupMemberAdapter.updateData(speedPkGroupInfo.groupMemberList);
				isJoined = speedPkGroupInfo.isJoined;
				if (speedPkGroupInfo.isCancel) {
					NetWorkUtilNow.getInstances().cancelRquest();
					if (commonDialog != null && commonDialog.isShowing()) {
						return;
					}
					commonDialog = new CommonDialog(this, false).setButtomOnlyShowOk(true).setListener(new ListenerBack() {
						@Override
						public void onListener(int type, Object object, boolean isTrue) {
							if (isTrue) {
								setResult(RESULT_OK);
								finish();
							}
						}
					});
					commonDialog.show(response.getMsg());
				}
				if (speedPkGroupInfo.isStart || speedPkGroupInfo.rateGroupStatus == 2) {
					if (isJoined) {
						startSpeedTest();

					} else {
						tv_speed_pk_group_status.setText("正在进行PK……");
					}
				}
				updateBackBtn();

			} else {
				String errorMsg = response == null ? "获取PK组信息失败" : response.getMsg();
				showCommon(errorMsg);
			}

		} else if (type == R.id.net_speed_pk_group_modify_status) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				if (opType == OP_TYPE_DISBAND || opType == OP_TYPE_EXIT) {
					setResult(RESULT_OK);
					finish();

				} else if (opType == OP_TYPE_JOIN) {
					isJoined = response.datas.isJoined;
					showCommon(response.getMsg());
					NetWorkUtilNow.getInstances().cancelRquest();
					updateBackBtn();
					requestUpdatePkGroupInfo();

				} else if (opType == OP_TYPE_START_PK && response.datas != null && response.datas.isStart) {
					startSpeedTest();
				}

			} else {
				String errorMsg = response == null ? "操作失败" : response.getMsg();
				showCommon(errorMsg);
			}
			dismissMyDialog();
		}
	}

	private void updateBackBtn() {
		if (speedPkGroupInfo.creator.equals(getUser())) {
			tv_speed_pk_group_cancel_or_exit.setText(getString(R.string.speed_pk_disband));

		} else {
			if (isJoined) {
				tv_speed_pk_group_cancel_or_exit.setText(getString(R.string.speed_pk_exit));
			} else {
				tv_speed_pk_group_cancel_or_exit.setText(getString(R.string.back));
			}
		}
	}

	private void doOnback() {
		if (speedPkGroupInfo.creator.equals(getUser())) {
			NetWorkUtilNow.getInstances().cancelRquest();
			new CommonDialog(this, false).setListener(new ListenerBack() {
				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					if (isTrue) {
						requestModifyPkGroupStatus(OP_TYPE_DISBAND);
					}
				}
			}).show("您将解散当前速率测试组，参与成员也会同步退出。");

		} else if (!isJoined) {
			setResult(RESULT_OK);
			finish();

		} else {
			new CommonDialog(this, false).setListener(new ListenerBack() {

				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					if (isTrue) {
						requestModifyPkGroupStatus(OP_TYPE_EXIT);
					}
				}
			}).show("您将退出当前速率测试组");
		}
	}

	private void startSpeedTest() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("speedPkGroupInfo", speedPkGroupInfo);
		bundle.putParcelableArrayList("mFtpAddressList", mFtpAddressList);
		for (int i = 0; i < speedPkGroupInfo.groupMemberList.size(); i++) {
			Db_JJ_FTPTestInfo mFtpTestInfo = speedPkGroupInfo.groupMemberList.get(i);
			mFtpTestInfo.id = speedPkGroupInfo.id;
			if (mFtpTestInfo.loginName.equals(getUser())) {
				bundle.putSerializable("mFtpTestInfo", mFtpTestInfo);
				break;
			}
		}
		NetWorkUtilNow.getInstances().cancelRquest();
		setResult(RESULT_OK);
		goIntent(SpeedPkActivity.class, bundle, true);
	}

	@Override
	public void onBackPressed() {
		doOnback();
	}

	@Override
	protected void onDestroy() {
		NetWorkUtilNow.getInstances().cancelRquest();
		super.onDestroy();
	}
}
