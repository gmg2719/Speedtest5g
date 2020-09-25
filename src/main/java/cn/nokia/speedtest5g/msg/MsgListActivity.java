package cn.nokia.speedtest5g.msg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.activity.BookWebActivity;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.gridview.util.Item;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;
import cn.nokia.speedtest5g.app.uitl.ModeClickHandler;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.msg.request.RequestMsgList;
import cn.nokia.speedtest5g.msg.request.RequestMsgManage;
import cn.nokia.speedtest5g.msg.respone.MsgDetail;
import cn.nokia.speedtest5g.msg.respone.ResponseMsgList;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

public class MsgListActivity extends BaseActionBarActivity implements OnItemClickListener, OnItemLongClickListener {
	private final int REQUEST_CODE_MSG_SUBSCRIBE_MANAGE = 100;
	private ListView lvMsgListView;
	private MsgAdapter msgAdapter;
	private List<MsgDetail> msgList = new ArrayList<>();
	private boolean isAllLoaded;
	private int pageNum = 1;// 页码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_list);

		init(getString(R.string.msg_notify), true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		actionBar.addAction(new MyActionBar.MenuAction(-1, EnumRequest.MENU_SELECT_ONE.toInt(), "管理"));

		lvMsgListView = (ListView) findViewById(R.id.lv_msg_list);
		msgAdapter = new MsgAdapter(this, msgList);
		lvMsgListView.setAdapter(msgAdapter);
		lvMsgListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 滑动底部加载更多
					if (!isAllLoaded && view.getLastVisiblePosition() == view.getCount() - 1) {
						requestMsgList();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		lvMsgListView.setOnItemClickListener(this);
		lvMsgListView.setOnItemLongClickListener(this);

		requestMsgList();
	}

	private void requestMsgList() {
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_MSG_LIST);
		RequestMsgList request = new RequestMsgList();
		request.page_num = pageNum;
		String jsonData = JsonHandler.getHandler().toJson(request);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(requestUrl, jsonData, R.id.net_msg_list, this);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (!isTrue) {
			return;
		}
		if (type == EnumRequest.MENU_SELECT_ONE.toInt()) {// 进入息息订阅管理页面
			goIntentRequest(MsgSubscribeManageActivity.class, null, REQUEST_CODE_MSG_SUBSCRIBE_MANAGE);

		} else if (type == R.id.net_msg_list) {
			dismissMyDialog();
			ResponseMsgList response = JsonHandler.getHandler().getTarget(object.toString(), ResponseMsgList.class);
			if (response != null && response.isRs()) {
				List<MsgDetail> msgDetails = response.datas;
				if (msgDetails != null) {
					if (pageNum == 1 && msgDetails.size() == 0) {
						msgList.clear();
					} else {
						msgList.addAll(msgDetails);
					}
					msgAdapter.updateData(msgList);
					pageNum++;

				} else {
					isAllLoaded = true;
					showCommon(response.getMsg());
				}

			} else {
				String errorMsg = response == null ? object.toString() : response.getMsg();
				showCommon(errorMsg);
			}

		} else if (type == R.id.net_msg_manage) {
			dismissMyDialog();
			BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
			if (response != null && response.isRs()) {
				pageNum = 1;
				msgList.clear();
				requestMsgList();
				setResult(RESULT_OK);

			} else {
				String errorMsg = response == null ? object.toString() : response.getMsg();
				showCommon(errorMsg);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_MSG_SUBSCRIBE_MANAGE) {
			pageNum = 1;
			msgList.clear();
			requestMsgList();
			setResult(RESULT_OK);
		}
	}

	private class MsgAdapter extends CommonListAdapter<MsgDetail> {

		public MsgAdapter(Context context, List<MsgDetail> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = ViewHolder.getInstance(mContext, convertView, R.layout.item_msg_list);
			initConvertView(vh, position);
			return vh.getConvertView();
		}

		private void initConvertView(ViewHolder vh, int position) {
			TextView tvMsgTime = vh.findViewById(R.id.tv_item_msg_creat_time);
			ImageView ivAppIcon = vh.findViewById(R.id.iv_item_msg_source_app_icon);
			TextView tvAppName = vh.findViewById(R.id.tv_item_msg_source_app_name);
			TextView tvGotoAppName = vh.findViewById(R.id.tv_item_msg_goto_app);
			TextView tvMsgTitle = vh.findViewById(R.id.tv_item_msg_title);
			TextView tvMsgContent = vh.findViewById(R.id.tv_item_msg_content);

			MsgDetail msgDetail = (MsgDetail) getItem(position);
			Item item = ModeClickHandler.getInstances(MsgListActivity.this).getAppByMenuCode(msgDetail.menuCode);
			tvMsgTime.setText(msgDetail.createTime);
			String appName = "";
			if (item != null) {
				appName = item.getName();
				if (item.getDrawable() == -1) {
					ImageLoader.getInstance().displayImage(item.drawableUrl, ivAppIcon, ImageOptionsUtil.getInstances().getIconOption());
					
				}else {
					ivAppIcon.setImageResource(item.getDrawable());
				}

			} else {
				ivAppIcon.setImageResource(R.drawable.icon_notify_unread_msg);
				appName = msgDetail.menuCode;
			}
			tvAppName.setText(appName);
			tvGotoAppName.setText("进入" + appName);
			tvMsgTitle.setText(msgDetail.title);
			tvMsgContent.setText(msgDetail.content);

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MsgDetail msgDetail = (MsgDetail) parent.getItemAtPosition(position);
		Item item;
		if (msgDetail.type == 1) {
			item = ModeClickHandler.getInstances(this).getAppByMenuCode(msgDetail.subMenuCode);
			if (item == null) {
				showCommon(getString(R.string.msg_open_view_hint));
			} else {
				ModeClickHandler.getInstances(this).clickMode(item, false);
			}

		} else if (msgDetail.type == 2) {
			item = ModeClickHandler.getInstances(this).getAppByMenuCode(msgDetail.menuCode);
			Intent intentUpdata = new Intent(this, BookWebActivity.class);
			intentUpdata.putExtra("Url", msgDetail.subMenuCode);
			intentUpdata.putExtra("Title", msgDetail.title);
			goIntent(intentUpdata, false);

		} else {
			showCommon(getString(R.string.msg_open_view_hint));
		}
	}

	private MsgDetail currentOpItem;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		currentOpItem = (MsgDetail) parent.getItemAtPosition(position);
		final CommonDialog commonDialog = new CommonDialog(MsgListActivity.this, R.layout.dialog_msg_manage, false);
		commonDialog.setNoTitle(true);
		commonDialog.setCancelable(true);
		commonDialog.setAotoDismiss(false);
		commonDialog.setBtnLayoutHide(true);
		View dialogContentView = commonDialog.getCustomContentView();
		final TextView btnUnsubscribe = (TextView) dialogContentView.findViewById(R.id.tv_msg_manage_unsubscribe);
		final TextView btnDelete = (TextView) dialogContentView.findViewById(R.id.tv_msg_manage_delete);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
                int vId = v.getId();
                if (vId == R.id.tv_msg_manage_unsubscribe) {
                    requestMsgManage(2, currentOpItem.id);
                    commonDialog.dismiss();
                } else if (vId == R.id.tv_msg_manage_delete) {
                    requestMsgManage(1, currentOpItem.id);
                    commonDialog.dismiss();
                }
			}
		};
		btnUnsubscribe.setOnClickListener(listener);
		btnDelete.setOnClickListener(listener);
		commonDialog.show();
		return true;
	}

	private void requestMsgManage(int opType, long msgId) {
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_MSG_MANAGE);
		RequestMsgManage request = new RequestMsgManage();
		request.opType = opType;
		request.msgId = msgId;
		String jsonData = JsonHandler.getHandler().toJson(request);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(requestUrl, jsonData, R.id.net_msg_manage, this);
	}
}
