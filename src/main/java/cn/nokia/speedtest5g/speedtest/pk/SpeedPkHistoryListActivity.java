package cn.nokia.speedtest5g.speedtest.pk;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.util.JsonHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkGroupList;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkHistoryList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 速率PK历史列表页面
 * @author JQJ
 *
 */
public class SpeedPkHistoryListActivity extends BaseActionBarActivity {
	
	private ListView lv_speed_pk_history_list_view;

	private SpeedPkHistoryAdapter adapter;
	private List<BeanSpeedPkGroupInfo> historyList = new ArrayList<>();
	private int currentPageNum;
	private boolean isAllLoaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pk_history_list_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init(R.string.speed_pk_history, true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		lv_speed_pk_history_list_view = (ListView) findViewById(R.id.lv_speed_pk_history_list_view);
		adapter = new SpeedPkHistoryAdapter(this, historyList);
		lv_speed_pk_history_list_view.setAdapter(adapter);
		lv_speed_pk_history_list_view.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 滑动底部加载更多
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (isAllLoaded) {
							return;
						}
						requestHistoryList();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		requestHistoryList();
	}

	private void requestHistoryList() {
		showMyDialog();
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_HISTORY);
		RequestSpeedPkGroupList requset = new RequestSpeedPkGroupList();
		currentPageNum++;
		requset.page_num = currentPageNum;
		requset.page_size = 10;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(url, jsonData, R.id.net_speed_pk_history, this);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (!isTrue) {
			dismissMyDialog();
			return;
		}
		if (type == EnumRequest.MENU_BACK.toInt()) {
			onBackPressed();

		} else if (type == R.id.net_speed_pk_history) {
			ResponseSpeedPkHistoryList response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkHistoryList.class);
			if (response != null && response.isRs()) {
				List<BeanSpeedPkGroupInfo> responseList = response.datas;
				if (responseList != null && responseList.size() > 0) {
					historyList.addAll(responseList);
					adapter.updateData(historyList);

				} else {
					isAllLoaded = true;
					showCommon(response.getMsg());
				}

			} else {
				String errorMsg = response == null ? "获取速率PK组失败" : response.getMsg();
				showCommon(errorMsg);
			}
			dismissMyDialog();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	/**
	 * 速率pk历史适配器
	 *
	 * @author jinhaizheng
	 */
	private class SpeedPkHistoryAdapter extends CommonListAdapter<BeanSpeedPkGroupInfo> {

		public SpeedPkHistoryAdapter(Context context, List<BeanSpeedPkGroupInfo> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = ViewHolder.getInstance(mContext, convertView, R.layout.jqj_speed_test_pk_history_item_adapter);
			initConvertView(viewHolder, position);
			return viewHolder.getConvertView();
		}

		private void initConvertView(ViewHolder viewHolder, int position) {
			final BeanSpeedPkGroupInfo item = (BeanSpeedPkGroupInfo) getItem(position);
			TextView tv_speed_pk_group_name = viewHolder.findViewById(R.id.tv_speed_pk_group_name);
			TextView tv_speed_pk_time = viewHolder.findViewById(R.id.tv_speed_pk_time);
			ImageView iv_speed_pk_download_max_operator_icon = viewHolder.findViewById(R.id.iv_speed_pk_download_max_operator_icon);
			TextView tv_speed_pk_download_max_user_name = viewHolder.findViewById(R.id.tv_speed_pk_download_max_user_name);
			TextView tv_speed_pk_download_max_login_name = viewHolder.findViewById(R.id.tv_speed_pk_download_max_login_name);
			TextView tv_speed_pk_download_max = viewHolder.findViewById(R.id.tv_speed_pk_download_max);
			ImageView iv_speed_pk_upload_max_operator_icon = viewHolder.findViewById(R.id.iv_speed_pk_upload_max_operator_icon);
			TextView tv_speed_pk_upload_max_user_name = viewHolder.findViewById(R.id.tv_speed_pk_upload_max_user_name);
			TextView tv_speed_pk_upload_max_login_name = viewHolder.findViewById(R.id.tv_speed_pk_upload_max_login_name);
			TextView tv_speed_pk_upload_max = viewHolder.findViewById(R.id.tv_speed_pk_upload_max);
			RelativeLayout rl_speed_pk = viewHolder.findViewById(R.id.rl_speed_pk);

			int resId = SystemUtil.getInstance().getOperatorLogoBlueBgResId(item.downloadOperator);
			iv_speed_pk_download_max_operator_icon.setImageResource(resId);
			tv_speed_pk_group_name.setText(item.rateGroupName);
			tv_speed_pk_time.setText(item.startTime);
			tv_speed_pk_download_max_user_name.setText(item.downloadName);
			tv_speed_pk_download_max_login_name.setText(item.downloadLoginName);
			tv_speed_pk_download_max.setText(item.downloadMax);

			resId = SystemUtil.getInstance().getOperatorLogoBlueBgResId(item.uploadOperator);
			iv_speed_pk_upload_max_operator_icon.setImageResource(resId);
			tv_speed_pk_upload_max_user_name.setText(item.uploadName);
			tv_speed_pk_upload_max_login_name.setText(item.uploadLoginName);
			tv_speed_pk_upload_max.setText(item.uploadMax);
			rl_speed_pk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle extras = new Bundle();
					extras.putSerializable("speedPkGroupInfo", item);
					goIntent(SpeedPkResultActivity.class, extras, false);
				}
			});
		}
	}
}
