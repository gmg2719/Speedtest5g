package cn.nokia.speedtest5g.view;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellDetailInfo;
import cn.nokia.speedtest5g.util.TimeUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 应急站指标时间自定义控件
 * 
 * @author xujianjun
 *
 */
public class JJ_EmergentQuotaTimeView extends LinearLayout {
	private Context mContext;
	private View llBackground;
	private TextView tvTimeDay;
	private TextView tvTimeDayUnit;
	private TextView tvTimeMonth;
	public boolean isCheck;

	private JJ_EmergentQuotaCellDetailInfo mInfo;

	public JJ_EmergentQuotaTimeView(Context context) {
		super(context);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		initView(mContext);
	}

	public JJ_EmergentQuotaTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView(mContext);
		// TODO Auto-generated constructor stub
	}

	public JJ_EmergentQuotaTimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView(mContext);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		super.onDraw(canvas);
		
//		setStytle();
//		canvas.drawCircle(getWidth() / 2, getHeight() / 2, 30, mPaintBackground);
		
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.jj_fn_emergent_quota_time, this, true);
		llBackground = findViewById(R.id.ll_fn_emergent_quota_time);
		tvTimeDay = (TextView) findViewById(R.id.tv_fn_emergent_quota_time_day);
		tvTimeDayUnit = (TextView) findViewById(R.id.tv_fn_emergent_quota_time_day_unit);
		tvTimeMonth = (TextView) findViewById(R.id.tv_fn_emergent_quota_time_month);

	}

	public void setData(JJ_EmergentQuotaCellDetailInfo info) {
		this.mInfo = info;
		setTextData(mInfo);
		setCheck(isCheck);
		invalidate();
	}

	private void setTextData(JJ_EmergentQuotaCellDetailInfo info) {
		if (info == null)
			return;

		String day = TimeUtil.getInstance().getToTime(TimeUtil.getInstance().stringToLong(mInfo.getPmCellTime()), "dd");
		String month = TimeUtil.getInstance().getToTime(TimeUtil.getInstance().stringToLong(mInfo.getPmCellTime()),
				"MM月");

		tvTimeDay.setText(day);
		tvTimeDayUnit.setText("日");

		tvTimeMonth.setText(month);
		
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
		if(isCheck){
			llBackground.setBackgroundResource(R.drawable.jj_emergent_quota_time_bg);
			tvTimeDay.setTextColor(mContext.getResources().getColor(R.color.white));
			tvTimeDayUnit.setTextColor(mContext.getResources().getColor(R.color.white));
		}else{
			llBackground.setBackgroundResource(R.color.white);
			tvTimeDay.setTextColor(mContext.getResources().getColor(R.color.black_half));
			tvTimeDayUnit.setTextColor(mContext.getResources().getColor(R.color.black_half));
		}
		invalidate();
	}

}
