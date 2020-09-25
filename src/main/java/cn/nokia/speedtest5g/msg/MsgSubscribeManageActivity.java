package cn.nokia.speedtest5g.msg;

import java.util.ArrayList;
import java.util.List;
import com.android.volley.util.JsonHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.gridview.util.Item;
import cn.nokia.speedtest5g.app.uitl.ModeClickHandler;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.msg.request.RequestMsgList;
import cn.nokia.speedtest5g.msg.request.RequestMsgManage;
import cn.nokia.speedtest5g.msg.respone.MsgDetail;
import cn.nokia.speedtest5g.msg.respone.ResponseMsgList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MsgSubscribeManageActivity extends BaseActionBarActivity {
	private ListView unsubscribedMsgTypeListView;
	private MsgTypeAdapter msgAdapter;
	private List<MsgDetail> msgList = new ArrayList<>();
	private int pageNum = 1;// 页码
	private MsgDetail currentOpMsgDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_subscribe_manage);

		init(getString(R.string.msg_notify), true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		unsubscribedMsgTypeListView = (ListView) findViewById(R.id.lv_msg_type_unsubscribed_list);
		msgAdapter = new MsgTypeAdapter(this, msgList);
		unsubscribedMsgTypeListView.setAdapter(msgAdapter);

		requestUnSubscribedMsgTypeList();
	}

	private void requestUnSubscribedMsgTypeList() {
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_MSG_UNSUBSCRIBED_TYPE_LIST);
		RequestMsgList request = new RequestMsgList();
		request.page_num = pageNum;
		request.page_size = 300;
		String jsonData = JsonHandler.getHandler().toJson(request);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(requestUrl, jsonData, R.id.net_msg_unsubscribed_type_list, this);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (!isTrue) {
			return;
		}

		if (type == EnumRequest.MENU_SELECT_ONE.toInt()) {
			Intent intent = new Intent(this, MsgSubscribeManageActivity.class);
			goIntent(intent, false);

		} else if (type == R.id.net_msg_unsubscribed_type_list) {
			dismissMyDialog();
			ResponseMsgList response = JsonHandler.getHandler().getTarget(object.toString(), ResponseMsgList.class);
			if (response != null && response.isRs()) {
				List<MsgDetail> msgDetails = response.datas;
				if (msgDetails != null && msgDetails.size() > 0) {
					msgList.addAll(msgDetails);
					msgAdapter.updateData(msgList);
					pageNum++;

				} else {
					showCommon(response.getMsg());
				}

			} else {
				String errorMsg = response == null ? object.toString() : response.getMsg();
				showCommon(errorMsg);
			}

		} else if (type == R.id.net_msg_manage) {
			dismissMyDialog();
			ResponseMsgList response = JsonHandler.getHandler().getTarget(object.toString(), ResponseMsgList.class);
			if (response != null && response.isRs()) {
				msgList.remove(currentOpMsgDetail);
				msgAdapter.updateData(msgList);
				setResult(RESULT_OK);

			} else {
				String errorMsg = response == null ? object.toString() : response.getMsg();
				showCommon(errorMsg);
			}
		}
	}

	private class MsgTypeAdapter extends CommonListAdapter<MsgDetail> {

		public MsgTypeAdapter(Context context, List<MsgDetail> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = ViewHolder.getInstance(mContext, convertView, R.layout.item_unsubscribed_msg_type);
			initConvertView(vh, position);
			return vh.getConvertView();
		}

		private void initConvertView(ViewHolder vh, int position) {
			TextView tvAppName = vh.findViewById(R.id.tv_item_msg_source_app_name);
			Button reSubscribeBtn = vh.findViewById(R.id.btn_item_msg_resubscribe);

			final MsgDetail msgDetail = (MsgDetail) getItem(position);
			Item item = ModeClickHandler.getInstances(MsgSubscribeManageActivity.this).getAppByMenuCode(msgDetail.menuCode);
			String appName = "";
			if (item != null) {
				appName = item.getName();

			} else {
				appName = msgDetail.menuCode;
			}
			tvAppName.setText(appName);
			reSubscribeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					requestMsgManage(3, msgDetail);
				}
			});
		}
	}

	private void requestMsgManage(int opType, MsgDetail msgDetail) {
		this.currentOpMsgDetail = msgDetail;
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_MSG_MANAGE);
		RequestMsgManage request = new RequestMsgManage();
		request.opType = opType;
		request.msgId = msgDetail.id;
		String jsonData = JsonHandler.getHandler().toJson(request);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(requestUrl, jsonData, R.id.net_msg_manage, this);
	}
}
