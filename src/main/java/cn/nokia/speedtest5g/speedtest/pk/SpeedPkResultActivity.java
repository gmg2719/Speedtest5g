package cn.nokia.speedtest5g.speedtest.pk;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.speedtest.SpeedTestDetailActivity;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkGroup;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 速率PK组 PK结果页面
 * @author JQJ
 *
 */
public class SpeedPkResultActivity extends BaseActionBarActivity implements OnClickListener, OnItemClickListener {

	private TextView tv_speed_pk_group_name;
	private TextView tv_speed_pk_start_time;

	private ImageView iv_speed_pk_download_operator_logo;
	private TextView tv_speed_pk_download_operator_name;
	private TextView tv_speed_pk_download_fastest;
	private Button btn_speed_pk_download_fastest;

	private ImageView iv_speed_pk_upload_operator_logo;
	private TextView tv_speed_pk_upload_operator_name;
	private TextView tv_speed_pk_upload_fastest;
	private Button btn_speed_pk_upload_fastest;

	private ListView lv_speed_pk_result_list_view;

	private SpeedPkResultAdapter adapter;
	private BeanSpeedPkGroupInfo speedPkGroupInfo;
	private long groupId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pk_result_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		speedPkGroupInfo = (BeanSpeedPkGroupInfo) getIntent().getSerializableExtra("speedPkGroupInfo");
		groupId = getIntent().getLongExtra("groupId", -1);
		init(R.string.speed_pk_result, true);

	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		actionBar.addAction(new MyActionBar.MenuAction(-1, EnumRequest.MENU_SELECT_ONE.toInt(), "刷新"));

		tv_speed_pk_group_name = (TextView) findViewById(R.id.tv_speed_pk_group_name);
		tv_speed_pk_start_time = (TextView) findViewById(R.id.tv_speed_pk_start_time);
		iv_speed_pk_download_operator_logo = (ImageView) findViewById(R.id.iv_speed_pk_download_operator_logo);
		tv_speed_pk_download_operator_name = (TextView) findViewById(R.id.tv_speed_pk_download_operator_name);
		tv_speed_pk_download_fastest = (TextView) findViewById(R.id.tv_speed_pk_download_fastest);
		btn_speed_pk_download_fastest = (Button) findViewById(R.id.btn_speed_pk_download_fastest);

		iv_speed_pk_upload_operator_logo = (ImageView) findViewById(R.id.iv_speed_pk_upload_operator_logo);
		tv_speed_pk_upload_operator_name = (TextView) findViewById(R.id.tv_speed_pk_upload_operator_name);
		tv_speed_pk_upload_fastest = (TextView) findViewById(R.id.tv_speed_pk_upload_fastest);
		btn_speed_pk_upload_fastest = (Button) findViewById(R.id.btn_speed_pk_upload_fastest);

		lv_speed_pk_result_list_view = (ListView) findViewById(R.id.lv_speed_pk_result_list_view);

		updateView();
		btn_speed_pk_download_fastest.setOnClickListener(this);
		btn_speed_pk_upload_fastest.setOnClickListener(this);

		adapter = new SpeedPkResultAdapter(this, speedPkGroupInfo == null ? null : speedPkGroupInfo.groupMemberList);
		lv_speed_pk_result_list_view.setAdapter(adapter);
		lv_speed_pk_result_list_view.setOnItemClickListener(this);

		requestSpeedPkResult();
	}

	private void requestSpeedPkResult() {
		showMyDialog();
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_RESULT);
		RequestSpeedPkGroupInfo requset = new RequestSpeedPkGroupInfo();
		requset.id = speedPkGroupInfo == null ? groupId : speedPkGroupInfo.id;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(url, jsonData, R.id.net_speed_pk_result, this);
	}

	@Override
	public void onClick(View v) {
		sortGroupMemberList(v.getId());
	}

	private void sortGroupMemberList(int viewId) {
        if (viewId == R.id.btn_speed_pk_download_fastest) {
            btn_speed_pk_download_fastest.setBackgroundResource(R.drawable.drawable_switch_model_btn_left_select);
            btn_speed_pk_download_fastest.setTextColor(getResources().getColor(R.color.white_edeeee));
            btn_speed_pk_upload_fastest.setBackgroundResource(R.drawable.drawable_switch_model_btn_right);
            btn_speed_pk_upload_fastest.setTextColor(getResources().getColor(R.color.gray_c0c0c3));

            Collections.sort(speedPkGroupInfo.groupMemberList, new Comparator<Db_JJ_FTPTestInfo>() {
                @Override
                public int compare(Db_JJ_FTPTestInfo lhs, Db_JJ_FTPTestInfo rhs) {
                    if (lhs.getDownSpeedAvg() > rhs.getDownSpeedAvg()) {
                        return -1;
                    } else if (lhs.getDownSpeedAvg() < rhs.getDownSpeedAvg()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            adapter.updateData(speedPkGroupInfo.groupMemberList);
        } else if (viewId == R.id.btn_speed_pk_upload_fastest) {
            btn_speed_pk_download_fastest.setBackgroundResource(R.drawable.drawable_switch_model_btn_left);
            btn_speed_pk_download_fastest.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
            btn_speed_pk_upload_fastest.setBackgroundResource(R.drawable.drawable_switch_model_btn_right_select);
            btn_speed_pk_upload_fastest.setTextColor(getResources().getColor(R.color.white_edeeee));

            Collections.sort(speedPkGroupInfo.groupMemberList, new Comparator<Db_JJ_FTPTestInfo>() {
                @Override
                public int compare(Db_JJ_FTPTestInfo lhs, Db_JJ_FTPTestInfo rhs) {
                    if (lhs.getUpSpeedAvg() > rhs.getUpSpeedAvg()) {
                        return -1;
                    } else if (lhs.getUpSpeedAvg() < rhs.getUpSpeedAvg()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            adapter.updateData(speedPkGroupInfo.groupMemberList);
        }

	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (!isTrue) {
			dismissMyDialog();
			return;
		}
		if (type == EnumRequest.MENU_SELECT_ONE.toInt()) {
			requestSpeedPkResult();

		} else if (type == R.id.net_speed_pk_result) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				speedPkGroupInfo = response.datas;
				updateView();
				sortGroupMemberList(R.id.btn_speed_pk_download_fastest);

			} else {
				String errorMsg = response == null ? "获取速率PK组失败" : response.getMsg();
				showCommon(errorMsg);
			}
			dismissMyDialog();
		}
	}

	private void updateView() {
		tv_speed_pk_group_name.setText(speedPkGroupInfo == null ? "" : speedPkGroupInfo.rateGroupName);
		tv_speed_pk_start_time.setText(speedPkGroupInfo == null ? "" : speedPkGroupInfo.startTime);

		String operator = speedPkGroupInfo == null ? "" : speedPkGroupInfo.downloadOperator;
		int operatorLogoResId = SystemUtil.getInstance().getOperatorLogoWhiteBgResId(operator);
		iv_speed_pk_download_operator_logo.setImageResource(operatorLogoResId);
		tv_speed_pk_download_operator_name.setText(operator);
		tv_speed_pk_download_operator_name.setVisibility(TextUtils.isEmpty(operator) ? View.INVISIBLE : View.VISIBLE);
		tv_speed_pk_download_fastest.setText(speedPkGroupInfo == null ? "——" : speedPkGroupInfo.downloadMax);

		operator = speedPkGroupInfo == null ? "" : speedPkGroupInfo.uploadOperator;
		operatorLogoResId = SystemUtil.getInstance().getOperatorLogoWhiteBgResId(operator);
		iv_speed_pk_upload_operator_logo.setImageResource(operatorLogoResId);
		tv_speed_pk_upload_operator_name.setText(operator);
		tv_speed_pk_upload_operator_name.setVisibility(TextUtils.isEmpty(operator) ? View.INVISIBLE : View.VISIBLE);
		tv_speed_pk_upload_fastest.setText(speedPkGroupInfo == null ? "——" : speedPkGroupInfo.uploadMax);
	}

	/**
	 * 速率pk组列表适配器
	 *
	 * @author jinhaizheng
	 */
	private class SpeedPkResultAdapter extends CommonListAdapter<Db_JJ_FTPTestInfo> {

		public SpeedPkResultAdapter(Context context, List<Db_JJ_FTPTestInfo> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = ViewHolder.getInstance(mContext, convertView, R.layout.jqj_speed_test_pk_result_item_adapter);
			initConvertView(viewHolder, position);
			return viewHolder.getConvertView();
		}

		private void initConvertView(ViewHolder viewHolder, int position) {
			final Db_JJ_FTPTestInfo item = (Db_JJ_FTPTestInfo) getItem(position);
			ImageView iv_speed_pk_operator_icon = viewHolder.findViewById(R.id.iv_speed_pk_operator_icon);
			TextView tv_speed_pk_user_name = viewHolder.findViewById(R.id.tv_speed_pk_user_name);
			TextView tv_speed_pk_login_name = viewHolder.findViewById(R.id.tv_speed_pk_login_name);
			TextView tv_speed_download_avg = viewHolder.findViewById(R.id.tv_speed_download_avg);
			TextView tv_speed_pk_upload_avg = viewHolder.findViewById(R.id.tv_speed_pk_upload_avg);

			String operator = item.operator;
			int operatorLogoResId = SystemUtil.getInstance().getOperatorLogoBlueBgResId(operator);
			iv_speed_pk_operator_icon.setImageResource(operatorLogoResId);
			if(item.loginName.equals(getUser())){
				tv_speed_pk_user_name.setText(item.userName + "(我)");
			}else{
				tv_speed_pk_user_name.setText(item.userName);
			}
			tv_speed_pk_login_name.setText(item.loginName);
			double downAvg = UtilHandler.getInstance().toDfSum(item.getDownSpeedAvg(), "00");
			tv_speed_download_avg.setText(downAvg == 0 ? "——" : downAvg + "");
			double uploadAvg = UtilHandler.getInstance().toDfSum(item.getUpSpeedAvg(), "00");
			tv_speed_pk_upload_avg.setText(uploadAvg == 0 ? "——" : uploadAvg + "");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Db_JJ_FTPTestInfo item = (Db_JJ_FTPTestInfo) adapter.getItem(position);
		item.setSourceType(getString(R.string.speed_pk));
		TextView tv_speed_download_avg = (TextView) view.findViewById(R.id.tv_speed_download_avg);
		TextView tv_speed_pk_upload_avg = (TextView) view.findViewById(R.id.tv_speed_pk_upload_avg);
		if ("——".equals(tv_speed_download_avg.getText().toString()) && "——".equals(tv_speed_pk_upload_avg.getText().toString())) {
			showCommon("该成员未完成或中途退出了速率测试，请刷新PK明细");
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("fromWhere", false);
		bundle.putSerializable("mFtpTestInfo", item);
		goIntent(SpeedTestDetailActivity.class, bundle, false);
	}
}
