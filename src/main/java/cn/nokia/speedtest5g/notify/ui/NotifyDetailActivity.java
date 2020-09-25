package cn.nokia.speedtest5g.notify.ui;

import com.android.volley.util.JsonHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.notify.request.RequestNotifySetReadOrDelete;
import cn.nokia.speedtest5g.notify.response.NotifyInfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 公告详情页
 *
 * @author jinhaizheng
 */
public class NotifyDetailActivity extends BaseActionBarActivity {
	private NotifyInfo notifyChildItem;

	private LinearLayout llDetailDesc, llRegion, llRange, llStartTime, llEndTime, llRecoverTime, llPhenomenon, llCaliber;
	private TextView tvTitle, tvTime, tvContent, tvregion, tvRange, tvStartTime, tvEndTime, tvRecoverTime, tvPhenomenon, tvCaliber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notify_detail);

		init(R.string.notify, true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		notifyChildItem = (NotifyInfo) getIntent().getSerializableExtra("notifyInfo");
		if (notifyChildItem == null) {
			showCommon("公告已被删除或不存在");
			finish();
		}

		tvTitle = (TextView) findViewById(R.id.tv_notify_detail_title);
		tvTime = (TextView) findViewById(R.id.tv_notify_detail_time);
		tvContent = (TextView) findViewById(R.id.tv_notify_detail_content);

		llDetailDesc = (LinearLayout) findViewById(R.id.ll_notify_detail_desc);
		llRegion = (LinearLayout) findViewById(R.id.ll_notify_detail_region);
		tvregion = (TextView) findViewById(R.id.tv_notify_detail_region_content);
		llRange = (LinearLayout) findViewById(R.id.ll_notify_detail_range);
		tvRange = (TextView) findViewById(R.id.tv_notify_detail_range_content);
		llStartTime = (LinearLayout) findViewById(R.id.ll_notify_detail_start_time);
		tvStartTime = (TextView) findViewById(R.id.tv_notify_detail_start_time_content);
		llEndTime = (LinearLayout) findViewById(R.id.ll_notify_detail_end_time);
		tvEndTime = (TextView) findViewById(R.id.tv_notify_detail_end_time_content);
		llRecoverTime = (LinearLayout) findViewById(R.id.ll_notify_detail_recover_time);
		tvRecoverTime = (TextView) findViewById(R.id.tv_notify_detail_recover_time_content);
		llPhenomenon = (LinearLayout) findViewById(R.id.ll_notify_detail_phenomenon);
		tvPhenomenon = (TextView) findViewById(R.id.ll_notify_detail_phenomenon_content);
		llCaliber = (LinearLayout) findViewById(R.id.ll_notify_detail_caliber);
		tvCaliber = (TextView) findViewById(R.id.tv_notify_detail_caliber_content);

		if (!notifyChildItem.isRead()) {
			setNotifyRead();
		}
		tvTitle.setText(notifyChildItem.getNOTICE_TITLE());
		tvTime.setText(notifyChildItem.getUPDATE_TIME());
		tvContent.setText(notifyChildItem.getNOTICE_CONTENT());
		if (notifyChildItem.getNOTICE_TYPE().equals("1")) {// 系统公告
			llDetailDesc.setVisibility(View.GONE);

		} else {// 其它类型公告
			llDetailDesc.setVisibility(View.VISIBLE);
			// 影响地区
			if (TextUtils.isEmpty(notifyChildItem.getREGION())) {
				llRegion.setVisibility(View.GONE);
			} else {
				llRegion.setVisibility(View.VISIBLE);
				tvregion.setText(notifyChildItem.getREGION());
			}
			// 影响范围
			if (TextUtils.isEmpty(notifyChildItem.getRANGE())) {
				llRange.setVisibility(View.GONE);
			} else {
				llRange.setVisibility(View.VISIBLE);
				tvRange.setText(notifyChildItem.getRANGE());
			}
			// 起始时间
			if (TextUtils.isEmpty(notifyChildItem.getSTART_TIME())) {
				llStartTime.setVisibility(View.GONE);
			} else {
				llStartTime.setVisibility(View.VISIBLE);
				tvStartTime.setText(notifyChildItem.getSTART_TIME());
			}
			// 预计恢复时间
			if (TextUtils.isEmpty(notifyChildItem.getEND_TIME())) {
				llEndTime.setVisibility(View.GONE);
			} else {
				llEndTime.setVisibility(View.VISIBLE);
				tvEndTime.setText(notifyChildItem.getEND_TIME());
			}
			// 实际恢复时间
			if (TextUtils.isEmpty(notifyChildItem.getRECOVER_TIME())) {
				llRecoverTime.setVisibility(View.GONE);
			} else {
				llRecoverTime.setVisibility(View.VISIBLE);
				tvRecoverTime.setText(notifyChildItem.getEND_TIME());
			}
			// 受影响现象
			if (TextUtils.isEmpty(notifyChildItem.getPHENOMENON())) {
				llPhenomenon.setVisibility(View.GONE);
			} else {
				llPhenomenon.setVisibility(View.VISIBLE);
				tvPhenomenon.setText(notifyChildItem.getPHENOMENON());
			}
			// 解释口径
			if (TextUtils.isEmpty(notifyChildItem.getCALIBER())) {
				llCaliber.setVisibility(View.GONE);
			} else {
				llCaliber.setVisibility(View.VISIBLE);
				tvCaliber.setText(notifyChildItem.getCALIBER());
			}
		}
	}

	/**
	 * 设置公告为已读
	 */
	private void setNotifyRead() {
		showMyDialog();
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_NOTIFY_SET_READ);
		RequestNotifySetReadOrDelete request = new RequestNotifySetReadOrDelete();
		request.setId(notifyChildItem.getID());
		NetWorkUtilNow.getInstances().readNetworkPostDes(requestUrl, JsonHandler.getHandler().toJson(request), EnumRequest.NET_NOTIFY_SET_READ.toInt(), this);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (!isTrue) {
			dismissMyDialog();
			String errorMsg = object.toString();
			showCommon(errorMsg);
			return;
		}
		if (type == EnumRequest.NET_NOTIFY_SET_READ.toInt()) {
			dismissMyDialog();
			BaseRespon respon = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
			if (respon != null && respon.isRs()) {
				Intent data = new Intent();
				data.putExtra("notifyIds", notifyChildItem.getID());
				setResult(RESULT_OK, data);

			} else {
				String errorMsg = respon == null ? "标记为已读失败" : respon.getMsg();
				showCommon(errorMsg);
			}
		}
	}
}
