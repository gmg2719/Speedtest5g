package cn.nokia.speedtest5g.speedtest.pk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FtpAddressGroupInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.MyTextWatcherCommon;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedPkGroupMemberAdapter;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedPkGroupInfo;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkCreateGroup;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedPkGroupList;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkGroup;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedPkGroupList;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.MyAutoCompleteTextView;
import cn.nokia.speedtest5g.view.MyScrollyGridView;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 速率PK组列表页
 * @author JQJ
 *
 */
public class SpeedPkGroupListActivity extends BaseActionBarHandlerActivity implements OnClickListener {

	private final int UPDATE_PK_GROUP_LIST = 101;
	private TextView tv_speed_pk_group_null;
	private ListView lv_speed_pk_group_list_view;
	private Button btn_speed_pk_create_group;
	private Button btn_speed_pk_update_group;

	private ArrayList<Db_JJ_FtpAddressGroupInfo> mFtpAddressList;

	private SpeedPkGroupAdapter speedPkGroupAdapter;
	private List<BeanSpeedPkGroupInfo> speedPkGroupList = new ArrayList<>();
	private List<BeanSpeedPkGroupInfo> searchList = new ArrayList<>();

	private int currentPageNum;
	private int searchPageNum;
	private boolean isAllLoadedSearch = false;
	private boolean isAllLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pk_group_list_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		mFtpAddressList = getIntent().getParcelableArrayListExtra("mFtpAddressList");
		init(R.string.speed_pk, true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_search, EnumRequest.MENU_SELECT_ONE.toInt(), ""));
		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_speed_test_pk_history_flag, EnumRequest.MENU_SELECT_TWO.toInt(), ""));
		MyAutoCompleteTextView etSearch = actionBar.getEtSearch();
		etSearch.setDrawableRightClose(R.drawable.clear);
		etSearch.addTextChangedListener(new MyTextWatcherCommon(R.id.actionbar_et_search, mHandler));

		tv_speed_pk_group_null = (TextView) findViewById(R.id.tv_speed_pk_group_null);
		btn_speed_pk_create_group = (Button) findViewById(R.id.btn_speed_pk_create_group);
		btn_speed_pk_update_group = (Button) findViewById(R.id.btn_speed_pk_update_group);
		lv_speed_pk_group_list_view = (ListView) findViewById(R.id.lv_speed_pk_group_list_view);
		lv_speed_pk_group_list_view.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 滑动底部加载更多
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (actionBar.isSearch() && isAllLoadedSearch) {
							return;
						}
						if (!actionBar.isSearch() && isAllLoaded) {
							return;
						}
						requestSpeedPkGroupList();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		speedPkGroupAdapter = new SpeedPkGroupAdapter(this, speedPkGroupList);
		lv_speed_pk_group_list_view.setAdapter(speedPkGroupAdapter);
		btn_speed_pk_create_group.setOnClickListener(this);
		btn_speed_pk_update_group.setOnClickListener(this);

		requestSpeedPkGroupList();
	}

	private void requestSpeedPkGroupList() {
		showMyDialog();
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_GROUP_LIST);
		RequestSpeedPkGroupList requset = new RequestSpeedPkGroupList();
		if (actionBar.isSearch()) {
			searchPageNum++;
			requset.page_num = searchPageNum;
			requset.key = actionBar.getSearch();

		} else {
			searchPageNum = 0;
			searchList.clear();
			currentPageNum++;
			requset.page_num = currentPageNum;
		}
		requset.page_size = 10;
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(url, jsonData, R.id.net_speed_pk_group_list, this);
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_speed_pk_create_group) {
            final CommonDialog commonDialog = new CommonDialog(this, R.layout.dialog_speed_pk_create_group, false);
            commonDialog.setCancelable(false);
            commonDialog.setAotoDismiss(false);
            View view = commonDialog.getCustomContentView();
            final EditText etGroupName = (EditText) view.findViewById(R.id.et_speed_pk_group_name);
            etGroupName.setText("PK组-" + TimeUtil.getInstance().getNowTimeYear_second());
            etGroupName.setSelection(etGroupName.getText().length());
            ImageView ivClear = (ImageView) view.findViewById(R.id.iv_speed_pk_clear_group_name);
            ivClear.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    etGroupName.setText("");
                }
            });
            commonDialog.setListener(new ListenerBack() {
                @Override
                public void onListener(int type, Object object, boolean isTrue) {
                    if (isTrue) {
                        if (TextUtils.isEmpty(etGroupName.getText())) {
                            showCommon(getResources().getString(R.string.speed_pk_edit_group_name_hint));
                            return;

                        } else {
                            if (etGroupName.getText().length() > 25) {
                                showCommon(getResources().getString(R.string.speed_pk_edit_group_name_length_hint));
                                return;
                            }
                            requestCreatePkGroup(etGroupName);
                            commonDialog.dismiss();
                        }
                    }
                }
            });
            commonDialog.show();
        } else if (id == R.id.btn_speed_pk_update_group) {
            currentPageNum = 0;
            speedPkGroupList.clear();
            requestSpeedPkGroupList();
        }
	}

	private void requestCreatePkGroup(final EditText etGroupName) {
		String url = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SPEED_PK_CREATE_GROUP);
		RequestSpeedPkCreateGroup requset = new RequestSpeedPkCreateGroup();
		requset.rateGroupName = etGroupName.getText().toString();
		String jsonData = JsonHandler.getHandler().toJson(requset);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(url, jsonData, R.id.net_speed_pk_create_group, this);
	}

	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case OTHER:
			if (events.getType() == R.id.actionbar_et_search) {// 搜索框输入监听回调
				searchList.clear();
				if (!TextUtils.isEmpty(actionBar.getSearch())) {
					showSoftInput();
					searchPageNum = 0;
					requestSpeedPkGroupList();

				} else {
					if (actionBar.isSearch()) {
						updateView();
					}
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == EnumRequest.MENU_BACK.toInt()) {// 返回
			if (!isTrue || actionBar.isSearch()) {
				btn_speed_pk_create_group.setVisibility(View.VISIBLE);
				btn_speed_pk_update_group.setVisibility(View.VISIBLE);
				updateView();
				requestSpeedPkGroupList();

			} else {
				super.onListener(type, object, isTrue);
			}
			return;
		}
		if (!isTrue) {
			showCommon(object.toString());
			dismissMyDialog();
			return;
		}
		if (type == EnumRequest.MENU_SELECT_ONE.toInt()) { // 进入搜索状态
			actionBar.showSearch("请输入搜索关键字", true);
			btn_speed_pk_create_group.setVisibility(View.GONE);
			btn_speed_pk_update_group.setVisibility(View.GONE);

		} else if (type == EnumRequest.MENU_SELECT_TWO.toInt()) { // PK历史
			goIntentRequest(SpeedPkHistoryListActivity.class, null, UPDATE_PK_GROUP_LIST);

		} else if (type == R.id.actionbar_btn_search) {// actionBar搜索按钮
			if (TextUtils.isEmpty(object.toString())) {
				showCommon("请输入搜索内容");
				return;
			}
			searchPageNum = 0;
			searchList.clear();
			requestSpeedPkGroupList();

		} else if (type == R.id.net_speed_pk_group_list) {
			ResponseSpeedPkGroupList response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroupList.class);
			if (response != null && response.isRs()) {
				List<BeanSpeedPkGroupInfo> responseList = response.datas;
				if (responseList != null && responseList.size() > 0) {
					if (actionBar.isSearch()) {
						searchList.addAll(responseList);
					} else {
						speedPkGroupList.addAll(responseList);
					}

				} else {
					if (actionBar.isSearch()) {
						isAllLoadedSearch = true;
					} else {
						isAllLoaded = true;
					}
				}
				updateView();

			} else {
				String errorMsg = response == null ? "获取速率PK组失败" : response.getMsg();
				showCommon(errorMsg);
			}
			dismissMyDialog();

		} else if (type == R.id.net_speed_pk_create_group) {
			ResponseSpeedPkGroup response = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedPkGroup.class);
			if (response != null && response.isRs()) {
				startSpeedPkGroupActivity(response.datas);

			} else {
				String errorMsg = response == null ? "创建速率PK组失败" : response.getMsg();
				showCommon(errorMsg);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (actionBar.isSearch()) {
			actionBar.hintSearch();
			btn_speed_pk_create_group.setVisibility(View.VISIBLE);
			btn_speed_pk_update_group.setVisibility(View.VISIBLE);
			updateView();

		} else {
			super.onBackPressed();
		}
	}

	private void updateView() {
		if (actionBar.isSearch()) {
			speedPkGroupAdapter.updateData(searchList);
			tv_speed_pk_group_null.setVisibility(View.GONE);
			lv_speed_pk_group_list_view.setVisibility(View.VISIBLE);

		} else {
			speedPkGroupAdapter.updateData(speedPkGroupList);
			if (speedPkGroupList != null && speedPkGroupList.size() > 0) {
				tv_speed_pk_group_null.setVisibility(View.GONE);
				lv_speed_pk_group_list_view.setVisibility(View.VISIBLE);
			} else {
				tv_speed_pk_group_null.setVisibility(View.VISIBLE);
				lv_speed_pk_group_list_view.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_speed_pk);
	}

	/**
	 * 速率pk组列表适配器
	 *
	 * @author jinhaizheng
	 */
	private class SpeedPkGroupAdapter extends CommonListAdapter<BeanSpeedPkGroupInfo> {

		public SpeedPkGroupAdapter(Context context, List<BeanSpeedPkGroupInfo> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = ViewHolder.getInstance(mContext, convertView, R.layout.jqj_speed_test_pk_group_item_adapter);
			initConvertView(viewHolder, position);
			return viewHolder.getConvertView();
		}

		private void initConvertView(ViewHolder viewHolder, int position) {
			final BeanSpeedPkGroupInfo item = (BeanSpeedPkGroupInfo) getItem(position);
			TextView tv_speed_pk_group_name = viewHolder.findViewById(R.id.tv_speed_pk_group_name);
			MyScrollyGridView gv_speed_pk_group_member_grid_view = viewHolder.findViewById(R.id.gv_speed_pk_group_member_grid_view);
			TextView tv_speed_pk_group_status = viewHolder.findViewById(R.id.tv_speed_pk_group_status);

			tv_speed_pk_group_name.setText(item.rateGroupName);
			SpeedPkGroupMemberAdapter groupMemberAdapter = new SpeedPkGroupMemberAdapter(mContext, item.groupMemberList);
			gv_speed_pk_group_member_grid_view.setAdapter(groupMemberAdapter);
			if (item.rateGroupStatus == 1) {
				tv_speed_pk_group_status.setText("即将开始");
			} else if (item.rateGroupStatus == 2) {
				tv_speed_pk_group_status.setText("正在进行中……");
			} else if (item.rateGroupStatus == 3) {
				tv_speed_pk_group_status.setText("测试完成");
			}
			tv_speed_pk_group_status.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (item.rateGroupStatus == 1) {
						startSpeedPkGroupActivity(item);

					} else if (item.rateGroupStatus == 2) {
						showCommon("该组正在进行PK，请选择其它PK小组");
					}
				}
			});
		}
	}

	private void startSpeedPkGroupActivity(BeanSpeedPkGroupInfo pkGroupInfo) {
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("mFtpAddressList", mFtpAddressList);
		bundle.putSerializable("speedPkGroupInfo", pkGroupInfo);
		goIntentRequest(SpeedPkGroupActivity.class, bundle, UPDATE_PK_GROUP_LIST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == UPDATE_PK_GROUP_LIST) {
			currentPageNum = 0;
			speedPkGroupList.clear();
			searchPageNum = 0;
			searchList.clear();
			requestSpeedPkGroupList();
		}
	}

	private InputMethodManager imm;

	private void showSoftInput() {
		if (imm == null) {
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		imm.showSoftInput(actionBar.getEtSearch(), InputMethodManager.SHOW_FORCED);
	}
}
