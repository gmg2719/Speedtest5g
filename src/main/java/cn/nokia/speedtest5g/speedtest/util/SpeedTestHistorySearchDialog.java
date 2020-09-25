package cn.nokia.speedtest5g.speedtest.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.dialog.WheelTimeDialog;
import cn.nokia.speedtest5g.speedtest.bean.RequestFtpHistory;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.MyAutoCompleteTextView;

/**
 * GIS图层设置条件
 *
 * @author jinhaizheng
 */
@SuppressLint("InflateParams")
public class SpeedTestHistorySearchDialog implements OnClickListener, OnDismissListener, ListenerBack {

	private static SpeedTestHistorySearchDialog dialog = null;
	private PopupWindow mPopupWindow;
	private ListenerBack mListener;
	private LayoutParams mWindowLp;
	private BaseActionBarHandlerActivity mActivity;

	private MyAutoCompleteTextView tvSearchKey;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private Button btnSearch;

	public static synchronized SpeedTestHistorySearchDialog getInstance() {
		synchronized (SpeedTestHistorySearchDialog.class) {
			if (dialog == null) {
				dialog = new SpeedTestHistorySearchDialog();
			}
			return dialog;
		}
	}

	public void show(BaseActionBarHandlerActivity act, View view, ListenerBack listener) {
		mActivity = act;
		this.mListener = listener;
		if (mPopupWindow == null) {
			View contentView = LayoutInflater.from(act).inflate(R.layout.dialog_speed_test_history_search, null);
			tvSearchKey = (MyAutoCompleteTextView) contentView.findViewById(R.id.tv_search_key);
			tvStartTime = (TextView) contentView.findViewById(R.id.tv_search_start_time);
			tvEndTime = (TextView) contentView.findViewById(R.id.tv_search_end_time);
			btnSearch = (Button) contentView.findViewById(R.id.btn_search);
			tvSearchKey.setDrawableRightClose(R.drawable.clear);
			contentView.findViewById(R.id.ll_search_start_time).setOnClickListener(this);
			contentView.findViewById(R.id.ll_search_end_time).setOnClickListener(this);
			btnSearch.setOnClickListener(this);

			mPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(-00000));// 点击PopupWindow
			// 外的屏幕，PopupWindow依然会消失
			mPopupWindow.setFocusable(true);
			mPopupWindow.setAnimationStyle(R.style.PopupTitleAnimation);
			mPopupWindow.update();
			mPopupWindow.setOnDismissListener(this);

		}
		if (mPopupWindow.isShowing()) {
			return;
		}

		mWindowLp = mActivity.getWindow().getAttributes();
		mWindowLp.alpha = 0.7f;
		mActivity.getWindow().setAttributes(mWindowLp);

		mPopupWindow.showAsDropDown(view, 0, 0);
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_search_start_time) {
            new WheelTimeDialog(mActivity).show(tvStartTime.getHint().toString(), v.getId(), this, false);
        } else if (id == R.id.ll_search_end_time) {
            new WheelTimeDialog(mActivity).show(tvEndTime.getHint().toString(), v.getId(), this, false);
        } else if (id == R.id.btn_search) {
            if (mListener != null) {
                RequestFtpHistory request = null;
                if (TextUtils.isEmpty(tvSearchKey.getText()) && TextUtils.isEmpty(tvStartTime.getText()) && TextUtils.isEmpty(tvEndTime.getText())) {
                    request = null;

                } else {
                    request = new RequestFtpHistory();
                    request.key = tvSearchKey.getText().toString();
                    request.startTime = tvStartTime.getText().toString();
                    request.endTime = tvEndTime.getText().toString();
                }

                mListener.onListener(R.id.btn_search, request, true);
            }
            if (mPopupWindow != null || mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == R.id.ll_search_start_time) {// 开始时间
			if (!TextUtils.isEmpty(tvEndTime.getText()) && TimeUtil.getInstance().stringToLong(tvEndTime.getText().toString(), "yyyy-MM-dd") < TimeUtil.getInstance().stringToLong(object.toString(), "yyyy-MM-dd")) {
				mActivity.showCommon("开始时间必须小于结束时间");
				return;
			}
			tvStartTime.setText(object.toString());

		} else if (type == R.id.ll_search_end_time) {// 结束时间
			if (!TextUtils.isEmpty(tvStartTime.getText()) && TimeUtil.getInstance().stringToLong(tvStartTime.getText().toString(), "yyyy-MM-dd") > TimeUtil.getInstance().stringToLong(object.toString(), "yyyy-MM-dd")) {
				mActivity.showCommon("结束时间必须大于开始时间");
				return;
			}
			tvEndTime.setText(object.toString());
		}
	}

	@Override
	public void onDismiss() {
		mWindowLp.alpha = 1f;
		mActivity.getWindow().setAttributes(mWindowLp);
		dialog = null;
	}
}
