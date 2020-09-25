package cn.nokia.speedtest5g.speedtest;

import java.util.List;

import com.android.volley.util.InputMethodUtil;
import com.android.volley.util.JsonHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.speedtest.bean.RequestRemark;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 备注修改
 * @author JQJ
 *
 */
public class SpeedTestRemarkActivity extends BaseActionBarActivity implements OnClickListener{

	private EditText mEtContent = null;
	private TextView mTvTip = null;
	private Button mBtnOk = null;

	private Db_JJ_FTPTestInfo mDb_JJ_FTPTestInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_remark_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		mDb_JJ_FTPTestInfo = (Db_JJ_FTPTestInfo)getIntent().getSerializableExtra("Db_JJ_FTPTestInfo");

		init("备注", true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		mEtContent = (EditText)findViewById(R.id.speed_test_remark_et_content);
		mTvTip = (TextView)findViewById(R.id.speed_test_remark_tv_tip);
		mBtnOk = (Button)findViewById(R.id.speed_test_remark_btn_ok);
		mBtnOk.setOnClickListener(this);

		if(mDb_JJ_FTPTestInfo != null){
			if(!TextUtils.isEmpty(mDb_JJ_FTPTestInfo.remarks)){
				mEtContent.setText(mDb_JJ_FTPTestInfo.remarks);
				mTvTip.setText(mDb_JJ_FTPTestInfo.remarks.length() + "/15");
			}
		}

		mEtContent.addTextChangedListener( new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String value = s.toString();
				if(TextUtils.isEmpty(value)){
					mTvTip.setText("");
				}else{
					mTvTip.setText(value.length() + "/15");
				}
			}
		} );
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.speed_test_remark_btn_ok){ //完成
			final String value = mEtContent.getText().toString();
			if(TextUtils.isEmpty(value)){
				return;
			}

			InputMethodUtil.getInstances().inputMethod(mActivity, v.getWindowToken());
			RequestRemark request = new RequestRemark();
			request.id = mDb_JJ_FTPTestInfo.id;
			request.remarks = value;
			NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_SAVE_SPEED_TEST_REMARKS),
					JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					BaseRespon response = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
					if (response != null){
						if(response.isRs()) {
							//修改列表中对应对象数值
							List<Db_JJ_FTPTestInfo> list = SpeedTestDataSet.mServerList;
							if(list != null){
								for(Db_JJ_FTPTestInfo info : list){
									if(info.id == mDb_JJ_FTPTestInfo.id){
										info.remarks = value;
										break;
									}
								}
							}
							//通知刷新历史列表

							//通知详情页面更新
							Intent intent = new Intent(SpeedTestDetailActivity.ON_UPDATE_REMARK);
							intent.putExtra("REMARK", value);
							sendBroadcast(intent);

							SpeedTestRemarkActivity.this.finish();

						}else{
							showCommon(response.getMsg());
						}
					} else {
						showCommon("修改失败");
					}
				}
			});
		}
	}
}
