package cn.nokia.speedtest5g.pingtest;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.view.SpeedTestIpEditText;

/**
 * ping测试
 * @author JQJ
 *
 */
public class PingTestActivity extends BaseActionBarActivity implements OnClickListener{

	private final int TYPE_WEB = 1;
	private final int TYPE_IP = 2;

	private final int STATUS_INIT = 1; //初始状态
	private final int STATUS_WEB_TESTING = 2; //web测试中
	private final int STATUS_IP_TESTING = 3; //web测试中
	private final int STATUS_TEST_SUCCESS = 4; //成功
	private final int STATUS_TEST_FAIL = 5; //ping不通或无网络

	private Button mBtnWeb, mBtnIp, mBtnWebPing, mBtnIpPing;
	private TextView mTvTitleWeb1, mTvTitleWeb2, mTvTitleWeb3, mTvTitleWeb4, mTvTitleWeb5, mTvTitleWeb6;
	private TextView mTvTitleIp1, mTvTitleIp2, mTvTitleIp3, mTvTitleIp4, mTvTitleIp5, mTvTitleIp6;
	private ImageView mIvFlag = null;
	private LinearLayout mLlModuleWeb, mLlModuleIp;
	private EditText mEditWeb = null;
	private SpeedTestIpEditText mSpeedTestIpEditText = null;

	private LinearLayout mLlWebTitle4, mLlIpTitle4;

	private int mType = TYPE_WEB;
	private int mStatus = STATUS_INIT;
	private Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_ping_test_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("Ping测试", true);
		initHandler();
	}

	@SuppressLint({"HandlerLeak", "NewApi"}) 
	private void initHandler(){
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == EnumRequest.TASK_PING.toInt()){
					PingInfo pingInfo = (PingInfo)msg.obj;
					pingInfo.toStrState();

					GradientDrawable drawable = new GradientDrawable();
					drawable.setShape(GradientDrawable.RECTANGLE);
					drawable.setColor(pingInfo.getColorState());
					drawable.setCornerRadius(5);

					if(pingInfo.isState()){
						updateStatus(STATUS_TEST_SUCCESS);
						if(mType == TYPE_WEB){
							mTvTitleWeb4.setText(String.valueOf(pingInfo.getTimes()));
							mTvTitleWeb5.setText(pingInfo.getStrState());
							mTvTitleWeb5.setBackground(drawable);
						}else if(mType == TYPE_IP){
							mTvTitleIp4.setText(String.valueOf(pingInfo.getTimes()));
							mTvTitleIp5.setText(pingInfo.getStrState());
							mTvTitleIp5.setBackground(drawable);
						}
					}else{
						updateStatus(STATUS_TEST_FAIL);
					}
				}
			}
		};
	}

	@SuppressLint("InflateParams")
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		mBtnWeb = (Button)findViewById(R.id.ping_test_btn_web); 
		mBtnIp = (Button)findViewById(R.id.ping_test_btn_ip); 
		mBtnWebPing = (Button)findViewById(R.id.ping_test_btn_web_ping); 
		mBtnIpPing = (Button)findViewById(R.id.ping_test_btn_ip_ping); 
		mTvTitleWeb1 = (TextView)findViewById(R.id.ping_test_iv_web_title_1);  
		mTvTitleWeb2 = (TextView)findViewById(R.id.ping_test_iv_web_title_2); 
		mTvTitleWeb3 = (TextView)findViewById(R.id.ping_test_iv_web_title_3);  
		mTvTitleWeb4 = (TextView)findViewById(R.id.ping_test_iv_web_title_4);  
		mTvTitleWeb5 = (TextView)findViewById(R.id.ping_test_iv_web_title_5); 
		mTvTitleWeb6 = (TextView)findViewById(R.id.ping_test_iv_web_title_6);
		mTvTitleIp1 = (TextView)findViewById(R.id.ping_test_iv_ip_title_1);  
		mTvTitleIp2 = (TextView)findViewById(R.id.ping_test_iv_ip_title_2); 
		mTvTitleIp3 = (TextView)findViewById(R.id.ping_test_iv_ip_title_3);  
		mTvTitleIp4 = (TextView)findViewById(R.id.ping_test_iv_ip_title_4);  
		mTvTitleIp5 = (TextView)findViewById(R.id.ping_test_iv_ip_title_5); 
		mTvTitleIp6 = (TextView)findViewById(R.id.ping_test_iv_ip_title_6); 
		mIvFlag = (ImageView)findViewById(R.id.ping_test_iv_flag); 

		mLlWebTitle4 = (LinearLayout)findViewById(R.id.ping_test_iv_web_ll_titie_4);
		mLlIpTitle4 = (LinearLayout)findViewById(R.id.ping_test_iv_ip_ll_titie_4);

		mLlModuleWeb = (LinearLayout)findViewById(R.id.ping_test_ll_module_web);
		mLlModuleIp = (LinearLayout)findViewById(R.id.ping_test_ll_module_ip);
		mEditWeb = (EditText)findViewById(R.id.ping_test_edit_web);  
		mSpeedTestIpEditText = (SpeedTestIpEditText)findViewById(R.id.ping_test_edit_ip);
		//设置默认地址
		mSpeedTestIpEditText.setIpText("202.108.22.5");

		mEditWeb.addTextChangedListener(new MyTextWatcher(mEditWeb));
		mSpeedTestIpEditText.setListenerBack(this);

		updateStatus(STATUS_INIT);

		mBtnWeb.setOnClickListener(this);
		mBtnIp.setOnClickListener(this);
		mBtnWebPing.setOnClickListener(this);
		mBtnIpPing.setOnClickListener(this);
	}

	private void updateStatus(int status){
		mStatus = status;
		if(mStatus == STATUS_INIT){
			if(mType == TYPE_WEB){
				mBtnWebPing.setVisibility(View.VISIBLE);
				mBtnIpPing.setVisibility(View.GONE);
				mTvTitleWeb1.setVisibility(View.VISIBLE); 
				mTvTitleWeb2.setVisibility(View.VISIBLE);
				mTvTitleWeb3.setVisibility(View.GONE);
				mLlWebTitle4.setVisibility(View.GONE); 
				mTvTitleWeb5.setVisibility(View.GONE);
				mTvTitleWeb6.setVisibility(View.GONE);
			}else if(mType == TYPE_IP){
				mBtnWebPing.setVisibility(View.GONE);
				mBtnIpPing.setVisibility(View.VISIBLE);
				mTvTitleIp1.setVisibility(View.VISIBLE);
				mTvTitleIp2.setVisibility(View.VISIBLE);
				mTvTitleIp3.setVisibility(View.GONE);
				mLlIpTitle4.setVisibility(View.GONE);
				mTvTitleIp5.setVisibility(View.GONE);
				mTvTitleIp6.setVisibility(View.GONE);
			}
		}else if(mStatus == STATUS_WEB_TESTING || mStatus == STATUS_IP_TESTING){
			if(mType == TYPE_WEB){
				mBtnWebPing.setVisibility(View.VISIBLE);
				mBtnIpPing.setVisibility(View.GONE);
				mTvTitleWeb1.setVisibility(View.GONE); 
				mTvTitleWeb2.setVisibility(View.GONE);
				mTvTitleWeb3.setVisibility(View.VISIBLE);
				mLlWebTitle4.setVisibility(View.GONE); 
				mTvTitleWeb5.setVisibility(View.GONE);
				mTvTitleWeb6.setVisibility(View.GONE);
			}else if(mType == TYPE_IP){
				mBtnWebPing.setVisibility(View.GONE);
				mBtnIpPing.setVisibility(View.VISIBLE);
				mTvTitleIp1.setVisibility(View.GONE);
				mTvTitleIp2.setVisibility(View.GONE);
				mTvTitleIp3.setVisibility(View.VISIBLE);
				mLlIpTitle4.setVisibility(View.GONE);
				mTvTitleIp5.setVisibility(View.GONE);
				mTvTitleIp6.setVisibility(View.GONE);
			}
		}else if(mStatus == STATUS_TEST_SUCCESS){
			if(mType == TYPE_WEB){
				mBtnWebPing.setVisibility(View.VISIBLE);
				mBtnIpPing.setVisibility(View.GONE);
				mTvTitleWeb1.setVisibility(View.GONE); 
				mTvTitleWeb2.setVisibility(View.GONE);
				mTvTitleWeb3.setVisibility(View.GONE);
				mLlWebTitle4.setVisibility(View.VISIBLE); 
				mTvTitleWeb5.setVisibility(View.VISIBLE);
				mTvTitleWeb6.setVisibility(View.GONE);
			}else if(mType == TYPE_IP){
				mBtnWebPing.setVisibility(View.GONE);
				mBtnIpPing.setVisibility(View.VISIBLE);
				mTvTitleIp1.setVisibility(View.GONE);
				mTvTitleIp2.setVisibility(View.GONE);
				mTvTitleIp3.setVisibility(View.GONE);
				mLlIpTitle4.setVisibility(View.VISIBLE);
				mTvTitleIp5.setVisibility(View.VISIBLE);
				mTvTitleIp6.setVisibility(View.GONE);
			}
		}else if(mStatus == STATUS_TEST_FAIL){
			if(mType == TYPE_WEB){
				mBtnWebPing.setVisibility(View.VISIBLE);
				mBtnIpPing.setVisibility(View.GONE);
				mTvTitleWeb1.setVisibility(View.GONE); 
				mTvTitleWeb2.setVisibility(View.GONE);
				mTvTitleWeb3.setVisibility(View.GONE);
				mLlWebTitle4.setVisibility(View.GONE); 
				mTvTitleWeb5.setVisibility(View.GONE);
				mTvTitleWeb6.setVisibility(View.VISIBLE);
			}else if(mType == TYPE_IP){
				mBtnWebPing.setVisibility(View.GONE);
				mBtnIpPing.setVisibility(View.VISIBLE);
				mTvTitleIp1.setVisibility(View.GONE);
				mTvTitleIp2.setVisibility(View.GONE);
				mTvTitleIp3.setVisibility(View.GONE);
				mLlIpTitle4.setVisibility(View.GONE);
				mTvTitleIp5.setVisibility(View.GONE);
				mTvTitleIp6.setVisibility(View.VISIBLE);
			}
		}
	}

	private void updateUi(int type){
		if(mStatus != STATUS_WEB_TESTING && mStatus != STATUS_IP_TESTING){
			mType = type;
		}
		if(type == TYPE_WEB){
			mBtnWebPing.setVisibility(View.VISIBLE);
			mBtnIpPing.setVisibility(View.GONE);
			mLlModuleWeb.setVisibility(View.VISIBLE);
			mLlModuleIp.setVisibility(View.GONE);
			mEditWeb.setVisibility(View.VISIBLE);
			mSpeedTestIpEditText.setVisibility(View.GONE);
			mIvFlag.setImageResource(R.drawable.icon_ping_test_web_flag);
			mBtnWeb.setBackgroundResource(R.drawable.drawable_switch_model_btn_left_select);
			mBtnWeb.setTextColor(getResources().getColor(R.color.white_edeeee));
			mBtnIp.setBackgroundResource(R.drawable.drawable_switch_model_btn_right);
			mBtnIp.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
		}else if(type == TYPE_IP){
			mBtnWebPing.setVisibility(View.GONE);
			mBtnIpPing.setVisibility(View.VISIBLE);
			mLlModuleWeb.setVisibility(View.GONE);
			mLlModuleIp.setVisibility(View.VISIBLE);
			mEditWeb.setVisibility(View.GONE);
			mSpeedTestIpEditText.setVisibility(View.VISIBLE);
			mIvFlag.setImageResource(R.drawable.icon_ping_test_ip_flag);
			mBtnWeb.setBackgroundResource(R.drawable.drawable_switch_model_btn_left);
			mBtnWeb.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
			mBtnIp.setBackgroundResource(R.drawable.drawable_switch_model_btn_right_select);
			mBtnIp.setTextColor(getResources().getColor(R.color.white_edeeee));
		}
	}

	private void startPing(){
		String ip = null;
		if(mType == TYPE_WEB){
			ip = mEditWeb.getText().toString().trim();
		}else if(mType == TYPE_IP){
			ip = mSpeedTestIpEditText.getIpText();
		}

		if(TextUtils.isEmpty(ip)){
			return;
		}

		if(mType == TYPE_IP){
			if(!UtilHandler.getInstance().isIpAddress(ip)){ //非IP
				return;
			}
		}

		if(mType == TYPE_WEB){
			updateStatus(STATUS_WEB_TESTING);
		}else if(mType == TYPE_IP){
			updateStatus(STATUS_IP_TESTING);
		}

		PingInfo pingInfo = new PingInfo();
		pingInfo.setIp(ip);
		PingThread pingThread  = new PingThread(pingInfo, 0, mHandler, 4, 5);//发包4次 超时5S
		pingThread.start();

	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ping_test_btn_web) {
            updateUi(TYPE_WEB);
        } else if (id == R.id.ping_test_btn_ip) {
            updateUi(TYPE_IP);
        } else if (id == R.id.ping_test_btn_web_ping) {
            if (mStatus == STATUS_WEB_TESTING) { //测试中 返回
                return;
            }
            startPing();
        } else if (id == R.id.ping_test_btn_ip_ping) {
            if (mStatus == STATUS_IP_TESTING) { //测试中 返回
                return;
            }
            startPing();
        }
	}

	private class MyTextWatcher implements TextWatcher{

		private EditText mEditText = null;

		public MyTextWatcher(EditText editText){
			mEditText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if(mEditText.getId() == R.id.ping_test_edit_web){
				String value = mEditText.getText().toString();
				if(TextUtils.isEmpty(value)){ //有数值
					mBtnWebPing.setBackgroundResource(R.drawable.shape_radius_20dp_gray_outer_gray_inter);
				}else{
					mBtnWebPing.setBackgroundResource(R.drawable.drawable_login_btn_bg2);
				}
			}
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if(isTrue){
			mBtnIpPing.setBackgroundResource(R.drawable.drawable_login_btn_bg2);
		}else{
			mBtnIpPing.setBackgroundResource(R.drawable.shape_radius_20dp_gray_outer_gray_inter);
		}
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_ping_test);
	}
}
