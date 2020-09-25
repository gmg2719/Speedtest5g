package cn.nokia.speedtest5g.speedtest;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

/**
 * ip查询
 * @author JQJ
 *
 */
public class SpeedTestIpSearchActivity extends BaseActionBarActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_ip_search_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("IP查询", true);
	}

	@SuppressLint("InflateParams")
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);


	}
}
