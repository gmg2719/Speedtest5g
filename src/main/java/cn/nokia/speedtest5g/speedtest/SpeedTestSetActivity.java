package cn.nokia.speedtest5g.speedtest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.util.InputMethodUtil;
import com.android.volley.util.SharedPreHandler;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedTestSetAdapter;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.bean.ResponseAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;

/**
 * 更换服务器
 * @author JQJ
 *
 */
public class SpeedTestSetActivity extends BaseActionBarActivity implements OnClickListener{

	private EditText mEtContent = null;
	private TextView mTvAuto = null;
	private ListView mLvContent = null;
	private SpeedTestSetAdapter mSetAdapter = null;
	private boolean mIsUpdate = false;
	private Button mBtnSearch = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_set_activity);
        mBgTopColor = R.color.bm_bg_color;
        mBgParentColor = R.color.bm_bg_color;
        mTitleTextColor = R.color.black_small;

		init("更换服务器", true);

//		actionBar.addAction(new MyActionBar.MenuAction(-1, EnumRequest.MENU_SELECT_ONE.toInt(), "个性化"));
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		mEtContent = (EditText) findViewById(R.id.speed_test_set_et_content);
		mTvAuto = (TextView) findViewById(R.id.speed_test_set_tv_auto);
		mLvContent = (ListView) findViewById(R.id.speed_test_set_lv_content);
		mTvAuto.setOnClickListener(this);
		mBtnSearch = (Button) findViewById(R.id.speed_test_set_btn_search);
		mBtnSearch.setOnClickListener(this);

		mLvContent.setEmptyView(findViewById(R.id.speed_test_set_tv_nodata));

		mEtContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s.toString())){
					ResponseAppFtpConfig responseAppFtpConfig = SpeedTestDataSet.mResponseAppFtpConfig;
					ArrayList<BeanAppFtpConfig> list = null;
					if(responseAppFtpConfig != null){
						list = responseAppFtpConfig.datas;
					}
					mSetAdapter.updateData(list);
				}
			}
		});

		mSetAdapter = new SpeedTestSetAdapter(this, this);
		mLvContent.setAdapter(mSetAdapter);

		loadData();
	}

	private void searchData(){
		ResponseAppFtpConfig responseAppFtpConfig = SpeedTestDataSet.mResponseAppFtpConfig;
		ArrayList<BeanAppFtpConfig> list = null;
		if(responseAppFtpConfig != null){
			list = responseAppFtpConfig.datas;
			if(list != null){
				ArrayList<BeanAppFtpConfig> tempList = new ArrayList<BeanAppFtpConfig>();
				String key = mEtContent.getText().toString();
				for(BeanAppFtpConfig config : list){
					if(config.hostType.contains(key) || config.operator.contains(key)){
						tempList.add(config);
					}
				}
				mSetAdapter.updateData(tempList);
			}
		}
	}

	private void loadData(){
		ResponseAppFtpConfig responseAppFtpConfig = SpeedTestDataSet.mResponseAppFtpConfig;
		if(responseAppFtpConfig != null){
			ArrayList<BeanAppFtpConfig> list = responseAppFtpConfig.datas;
			mSetAdapter.updateData(list);
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if(type == R.id.speed_test_set_adapter_ll_content){ //点击服务里列表返回
			if(isTrue){
				BeanAppFtpConfig beanAppFtpConfig = (BeanAppFtpConfig)object;
				if(SpeedTestDataSet.mBeanAppFtpConfig != null){
					//判断是否更新
					if(beanAppFtpConfig.iD.equals(SpeedTestDataSet.mBeanAppFtpConfig.iD)){
						mIsUpdate = false;
						SpeedTestSetActivity.this.finish();
						return;
					}
				}
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("SPEED_TEST_HOST_ID", beanAppFtpConfig.iD);
				SpeedTestDataSet.mBeanAppFtpConfig = beanAppFtpConfig;
				mIsUpdate = true;
				SpeedTestSetActivity.this.finish();
			}
		}else if(type == EnumRequest.MENU_SELECT_ONE.toInt()){ //个性化
			Intent intent = new Intent(SpeedTestSetActivity.this, SpeedTestCustomSetActivity.class);
			goIntent(intent, false);
		}
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.speed_test_set_tv_auto) { //自动选择  选择第一个
            try {
                ResponseAppFtpConfig responseAppFtpConfig = SpeedTestDataSet.mResponseAppFtpConfig;
                if (responseAppFtpConfig != null) {
                    ArrayList<BeanAppFtpConfig> list = responseAppFtpConfig.datas;
                    if (list != null && list.size() > 0) {
                        BeanAppFtpConfig config = list.get(0);
                        if (SpeedTestDataSet.mBeanAppFtpConfig != null) {
                            if (config.iD.equals(SpeedTestDataSet.mBeanAppFtpConfig.iD)) {//无需更新
                                mIsUpdate = false;
                                SpeedTestSetActivity.this.finish();
                                return;
                            }
                        }
                        //需要更新
                        SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("SPEED_TEST_HOST_ID", config.iD);
                        SpeedTestDataSet.mBeanAppFtpConfig = config;
                        mIsUpdate = true;
                        SpeedTestSetActivity.this.finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.speed_test_set_btn_search) {
            InputMethodUtil.getInstances().inputMethod(this, v.getWindowToken());
            searchData();
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mIsUpdate){ //发广播通知
			Intent intent = new Intent(SpeedTestMainActivity.ON_UPDATE_FTP);
			sendBroadcast(intent);
		}
	}
}
