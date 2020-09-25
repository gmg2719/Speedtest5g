package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.util.SharedPreHandler;

import java.util.HashMap;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;

/**
 * 速测 包选择弹窗  暂时无用
 * @author JQJ
 *
 */
public class SpeedTestPackSelectDialog extends BaseAlertDialog implements OnClickListener{

	private static final int PACAKGE_SIZE_LARGE = 3;
	private static final int PACAKGE_SIZE_MIDDLE = 2;
	private static final int PACAKGE_SIZE_SMALL = 1;

	private ImageView mIv1 = null;
	private ImageView mIv2 = null;
	private ImageView mIv3 = null;
	private TextView mTvTitle1 = null;
	private TextView mTvTitle2 = null;
	private TextView mTvTitle3 = null;
	private TextView mTvContent1 = null;
	private TextView mTvContent2 = null;
	private TextView mTvContent3 = null;
	private RelativeLayout mRl1 = null;
	private RelativeLayout mRl2 = null;
	private RelativeLayout mRl3 = null;
	private ListenerBack mListenerBack = null;
	private int mPackageType = PACAKGE_SIZE_LARGE;

	public SpeedTestPackSelectDialog(Context context) {
		super(context);
	}

	public SpeedTestPackSelectDialog setListener(ListenerBack listenerBack){
		this.mListenerBack = listenerBack;
		return this;
	}

	public void show(HashMap<String, String> hashMap) {
		super.show();
		int packageType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared("PackageType", PACAKGE_SIZE_LARGE);
		updateUi(packageType);
		updateContent(hashMap);
	}

	private void updateUi(int packageType){
		mPackageType = packageType;
		if(packageType == PACAKGE_SIZE_LARGE){ //大包
			mIv1.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mIv2.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mIv3.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_yes));
			mTvTitle1.setTextColor(Color.parseColor("#696776"));
			mTvTitle2.setTextColor(Color.parseColor("#696776"));
			mTvTitle3.setTextColor(Color.parseColor("#EDEEEE"));
			mTvContent1.setTextColor(Color.parseColor("#696776"));
			mTvContent2.setTextColor(Color.parseColor("#696776"));
			mTvContent3.setTextColor(Color.parseColor("#EDEEEE"));
		}else if(packageType == PACAKGE_SIZE_MIDDLE){ //中包
			mIv1.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mIv2.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_yes));
			mIv3.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mTvTitle1.setTextColor(Color.parseColor("#696776"));
			mTvTitle2.setTextColor(Color.parseColor("#EDEEEE"));
			mTvTitle3.setTextColor(Color.parseColor("#696776"));
			mTvContent1.setTextColor(Color.parseColor("#696776"));
			mTvContent2.setTextColor(Color.parseColor("#EDEEEE"));
			mTvContent3.setTextColor(Color.parseColor("#696776"));
		}else if(packageType == PACAKGE_SIZE_SMALL){ //小包
			mIv1.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_yes));
			mIv2.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mIv3.setImageDrawable(SpeedTest5g.getContext().getResources().getDrawable(R.drawable.icon_speed_test_flow_flag_no));
			mTvTitle1.setTextColor(Color.parseColor("#EDEEEE"));
			mTvTitle2.setTextColor(Color.parseColor("#696776"));
			mTvTitle3.setTextColor(Color.parseColor("#696776"));
			mTvContent1.setTextColor(Color.parseColor("#EDEEEE"));
			mTvContent2.setTextColor(Color.parseColor("#696776"));
			mTvContent3.setTextColor(Color.parseColor("#696776"));
		}
	}

	private void updateContent(HashMap<String, String> hashMap){
		if(hashMap != null){
			if(!TextUtils.isEmpty(hashMap.get("4623"))){
				mTvContent1.setText(hashMap.get("4623"));
			}
			if(!TextUtils.isEmpty(hashMap.get("4624"))){
				mTvContent2.setText(hashMap.get("4624"));
			}
			if(!TextUtils.isEmpty(hashMap.get("4625"))){
				mTvContent3.setText(hashMap.get("4625"));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_pack_select_dialog);
		setCanceledOnTouchOutside(true);

		mIv1 = (ImageView)findViewById(R.id.speed_test_pack_select_iv_1);
		mIv2 = (ImageView)findViewById(R.id.speed_test_pack_select_iv_2);
		mIv3 = (ImageView)findViewById(R.id.speed_test_pack_select_iv_3);
		mTvTitle1 = (TextView)findViewById(R.id.speed_test_pack_select_tv_title_1);
		mTvTitle2 = (TextView)findViewById(R.id.speed_test_pack_select_tv_title_2);
		mTvTitle3 = (TextView)findViewById(R.id.speed_test_pack_select_tv_title_3);
		mTvContent1 = (TextView)findViewById(R.id.speed_test_pack_select_tv_content_1);
		mTvContent2 = (TextView)findViewById(R.id.speed_test_pack_select_tv_content_2);
		mTvContent3 = (TextView)findViewById(R.id.speed_test_pack_select_tv_content_3);
		mRl1 = (RelativeLayout)findViewById(R.id.speed_test_pack_select_rl_1);
		mRl2 = (RelativeLayout)findViewById(R.id.speed_test_pack_select_rl_2);
		mRl3 = (RelativeLayout)findViewById(R.id.speed_test_pack_select_rl_3);

		mRl1.setOnClickListener(this);
		mRl2.setOnClickListener(this);
		mRl3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.speed_test_pack_select_rl_1) {
            if (mPackageType == PACAKGE_SIZE_SMALL) {
                return;
            }
            updateUi(PACAKGE_SIZE_SMALL);
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setIntShared("PackageType", PACAKGE_SIZE_SMALL);
        } else if (id == R.id.speed_test_pack_select_rl_2) {
            if (mPackageType == PACAKGE_SIZE_MIDDLE) {
                return;
            }
            updateUi(PACAKGE_SIZE_MIDDLE);
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setIntShared("PackageType", PACAKGE_SIZE_MIDDLE);
        } else if (id == R.id.speed_test_pack_select_rl_3) {
            if (mPackageType == PACAKGE_SIZE_LARGE) {
                return;
            }
            updateUi(PACAKGE_SIZE_LARGE);
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setIntShared("PackageType", PACAKGE_SIZE_LARGE);
        }

		if(mListenerBack != null){
			mListenerBack.onListener(-10000, null, true);
		}
	}
}
