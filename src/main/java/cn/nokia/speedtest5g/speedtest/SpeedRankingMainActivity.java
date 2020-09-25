package cn.nokia.speedtest5g.speedtest;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseFragmentActivity;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedRankingMainAdapter;
import cn.nokia.speedtest5g.view.viewpager.IndicatorViewPager;
import cn.nokia.speedtest5g.view.viewpager.OnTransitionTextListener;
import cn.nokia.speedtest5g.view.viewpager.ScrollIndicatorView;
import cn.nokia.speedtest5g.view.viewpager.TextWidthColorBar;

/**
 * 速测排行
 * @author JQJ
 *
 */
public class SpeedRankingMainActivity extends BaseFragmentActivity {

	private ScrollIndicatorView scrollIndicatorView = null;
	private ViewPager mViewPager = null;
	private IndicatorViewPager mIndicatorViewPager = null;
	private SpeedRankingMainAdapter mTopAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_ranking_main_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("测速排行", true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		mViewPager = (ViewPager) findViewById(R.id.speed_ranking_main_viewPager);
		scrollIndicatorView = (ScrollIndicatorView) findViewById(R.id.speed_ranking_main_indicator);
		TextWidthColorBar textWidthColorBar = new TextWidthColorBar(this, scrollIndicatorView, ContextCompat.getColor(SpeedRankingMainActivity.this, R.color.text_blue_color), 5);
		scrollIndicatorView.setScrollBar(textWidthColorBar);
		// 设置滚动监听
		scrollIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColorId(this, R.color.text_blue_color, R.color.gray_929aac));

		mViewPager.setOffscreenPageLimit(3);
		mIndicatorViewPager = new IndicatorViewPager(scrollIndicatorView, mViewPager);
		mTopAdapter = new SpeedRankingMainAdapter(this, getSupportFragmentManager(), getLayoutInflater(), this);
		mIndicatorViewPager.setAdapter(mTopAdapter);
	}
}
