package cn.nokia.speedtest5g.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity2.FeedBackActivity;

/**
 * 设置页面
 * @author JQJ
 *
 */
public class SettingsActivity extends BaseActionBarActivity {

	private String str53 = null; //版本信息 链接
	private String str54 = null; //帮助信息  链接

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_settings_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("设置", true);

		str53 = getIntent().getStringExtra("str53");
		str54 = getIntent().getStringExtra("str54");
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		if (!SharedPreHandler.getShared(this).getBooleanShared(TypeKey.getInstance().IS_LAUNCHER(), false)) {
			findViewById(R.id.settings_btn_bbxx).setVisibility(View.GONE);
		}

		findViewById(R.id.settings_btn_sign).setBackgroundResource(
				SharedPreHandler.getShared(this).getBooleanShared(TypeKey.getInstance().SET_UPLOAD_WIFI(), false)
				? R.drawable.icon_settings_rb_open : R.drawable.icon_settings_rb_close);

		//		findViewById(R.id.settings_btn_sswsjc).setBackgroundResource(false
		//				? R.drawable.icon_settings_rb_open : R.drawable.icon_settings_rb_close);
	}

	public void onListener(View v) {
        int id = v.getId();
        if (id == R.id.settings_btn_sign) {//wifi菜单选择
            boolean wifis = SharedPreHandler.getShared(this).getBooleanShared(TypeKey.getInstance().SET_UPLOAD_WIFI(), false);
            v.setBackgroundResource(wifis ? R.drawable.icon_settings_rb_close : R.drawable.icon_settings_rb_open);
            SharedPreHandler.getShared(this).setBooleanShared(TypeKey.getInstance().SET_UPLOAD_WIFI(), !wifis);
        } else if (id == R.id.settings_btn_sswsjc) {//实时网速检测
            showCommon("功能后续开放");
        } else if (id == R.id.settings_btn_bbxx) {// 版本信息
            Intent intentUpdata = new Intent(SettingsActivity.this, BookWebActivity.class);
            intentUpdata.putExtra("Url", str53);
            intentUpdata.putExtra("Title", "版本信息");
            goIntent(intentUpdata, false);
        } else if (id == R.id.settings_btn_yjfk) {// 意见反馈
            goIntent(FeedBackActivity.class, null, false);
        } else if (id == R.id.settings_btn_cssz) { //测速设置
            showCommon("功能后续开放");
        }
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_qtsz);
	}
}
