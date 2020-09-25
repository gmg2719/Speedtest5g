package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 * 速测 满意度评价
 * @author JQJ
 *
 */
public class SpeedTestEvaluateDialog extends BaseAlertDialog implements RatingBar.OnRatingBarChangeListener{

	private TextView mTvAvgDownload = null;
	private TextView mTvAvgUpload = null;
	private RatingBar mRbEvaluate = null;
	private TextView mTvMark = null;
	private float mAvgDownload = 0;
	private float mAvgUpload = 0;
	private String mRankValue = "1";
	private TextView mTvIsp = null;
	private TextView mTvRank = null;

	public SpeedTestEvaluateDialog(Context context) {
		super(context);
	}

	public SpeedTestEvaluateDialog setValue(float avgDownload, float avgUpload, String rankValue){
		this.mAvgDownload = avgDownload;
		this.mAvgUpload = avgUpload;
		this.mRankValue = rankValue;
		return this;
	}

	@Override
	public void show() {
		super.show();
		if(mTvAvgDownload != null){
			if(mAvgDownload == 0){
				mTvAvgDownload.setText("——");
			}else{
				mTvAvgDownload.setText(String.valueOf(mAvgDownload));
			}
		}
		if(mTvAvgUpload != null){
			if(mAvgUpload == 0){
				mTvAvgUpload.setText("——");
			}else{
				mTvAvgUpload.setText(String.valueOf(mAvgUpload));
			}
		}

		if(mTvRank != null){
			mTvRank.setText(mRankValue+"%");
		}

		//运营商
		String isp = SystemUtil.getInstance().getISPNew(SpeedTest5g.getContext());
		mTvIsp.setText("请给运营商(" + (TextUtils.isEmpty(isp)?"——":isp) + ")打分");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_evaluate_dialog);
		setCanceledOnTouchOutside(false);
		mTvAvgDownload = (TextView)findViewById(R.id.speedtest_evaluate_tv_avg_download);
		mTvAvgUpload = (TextView)findViewById(R.id.speedtest_evaluate_tv_avg_upload);
		Button btnYes = (Button)findViewById(R.id.speedtest_evaluate_btn_yes);
		btnYes.setOnClickListener(clickListener);
		Button btnNo = (Button)findViewById(R.id.speedtest_evaluate_btn_no);
		btnNo.setOnClickListener(clickListener);

		mTvRank = (TextView)findViewById(R.id.speedtest_evaluate_tv_value1);
		mRbEvaluate = (RatingBar)findViewById(R.id.speedtest_evaluate_rb_evaluate);
		mTvMark = (TextView)findViewById(R.id.speedtest_evaluate_tv_mark);
		mTvIsp = (TextView)findViewById(R.id.speedtest_evaluate_tv_isp);

		mRbEvaluate.setOnRatingBarChangeListener(this);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.speedtest_evaluate_btn_yes) {
                SpeedTestEvaluateDialog.this.dismiss();
            } else if (id == R.id.speedtest_evaluate_btn_no) { //残忍拒绝
                SpeedTestEvaluateDialog.this.dismiss();
            }
		}
	};

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		if(mTvMark != null){
			int numStars = ratingBar.getNumStars();
			int value = (int)((rating / numStars) * 100f);
			mTvMark.setText(value + "分");
		}
	}
}
