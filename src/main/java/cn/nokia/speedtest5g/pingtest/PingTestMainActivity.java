package cn.nokia.speedtest5g.pingtest;

import java.util.ArrayList;

import com.android.volley.util.JsonHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.pingtest.util.PingTestConfigBean;
import cn.nokia.speedtest5g.pingtest.util.PingTestConfigRequest;
import cn.nokia.speedtest5g.pingtest.util.PingTestConfigResponse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ping测试主页  --电商测速 游戏测速等
 * @author JQJ
 *
 */
public class PingTestMainActivity extends BaseActionBarActivity {

	private static final int WHAT_ITEM_ONE = 100;

	private ArrayList<PingInfo> mDataList = new ArrayList<PingInfo>();
	private boolean mIsOver = false;
	private int mSize = 0;
	private TextView mTvName = null;
	private TextView mTvValue = null;
	private TextView mTvTitle = null;
	private RelativeLayout mRlContent = null;
	private ImageView mIvTitle = null;
	private int mDrawableId = R.drawable.icon_speed_test_ping_title_yxcs;
	private String mMenuId = null;
	private boolean mTestOver = false; //测试是否结束

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_ping_test_main_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		mRlContent = (RelativeLayout)findViewById(R.id.pingtest_main_rl_content);
		mTvName = (TextView)findViewById(R.id.pingtest_main_tv_name);
		mTvValue = (TextView)findViewById(R.id.pingtest_main_tv_value);
		mTvTitle = (TextView)findViewById(R.id.pingtest_main_tv_title);
		mIvTitle = (ImageView)findViewById(R.id.pingtest_main_iv_title);

		initData();
		initNet();
	}

	/**
	 * 判断是否有网络
	 */
	private void initNet(){
		if(NetInfoUtil.isNetworkConnected(mActivity)){
			getIpConfigFromServer();
		}else{
			mTvTitle.setVisibility(View.VISIBLE);
			mRlContent.setVisibility(View.GONE);
		}
	}

	private void initData(){
		mMenuId = getIntent().getStringExtra("MENU_ID");
		String MENU_NAME = getIntent().getStringExtra("MENU_NAME");
		if("1101".equals(mMenuId)){ //游戏测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_yxcs;
		}else if("1102".equals(mMenuId)){ //ip查询
			mDrawableId = R.drawable.icon_speed_test_ping_title_ipcx;
		}else if("1103".equals(mMenuId)){ //ping测试
			mDrawableId = R.drawable.icon_speed_test_ping_title_pingcs;
		}else if("1104".equals(mMenuId)){ //电话归属
			mDrawableId = R.drawable.icon_speed_test_ping_title_dhgs;
		}else if("1105".equals(mMenuId)){ //电商测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_dscs;
		}else if("1106".equals(mMenuId)){ //购票测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_gpcs;
		}else if("1107".equals(mMenuId)){ //抢红包测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_qhb;
		}else if("1108".equals(mMenuId)){ //视频测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_spcs;
		}else if("1109".equals(mMenuId)){ //搜索测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_sscs;
		}else if("1110".equals(mMenuId)){ //信号探测
			mDrawableId = R.drawable.icon_speed_test_ping_title_xhcsy;
		}else if("1111".equals(mMenuId)){ //直播测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_zbcs;
		}else if("1112".equals(mMenuId)){ //咨讯测速
			mDrawableId = R.drawable.icon_speed_test_ping_title_zxcs;
		}else if("1113".equals(mMenuId)){ //即时通信
			mDrawableId = R.drawable.icon_speed_test_ping_title_jstx;
		}

		init(MENU_NAME, true);
		mIvTitle.setImageDrawable(getResources().getDrawable(mDrawableId));
	}

	/**
	 * 获取服务器ip地址配置
	 */
	private void getIpConfigFromServer(){
		PingTestConfigRequest request = new PingTestConfigRequest();
		request.menuId=mMenuId; 
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_CONVENIENT_TOOLS), 
				JsonHandler.getHandler().toJson(request), -1, new ListenerBack() {

			@Override
			public void onListener(int type, Object object, boolean isTrue) {
				if(isTrue){
					PingTestConfigResponse response = JsonHandler.getHandler().getTarget(object.toString(), PingTestConfigResponse.class);
					if(response != null){
						if(response.isRs()){
							initData(response);
						}else{
							showCommon(response.getMsg());
							PingTestMainActivity.this.finish();
						}
					}else{
						showCommon("获取配置失败");
						PingTestMainActivity.this.finish();
					}
				}else{
					showCommon(object.toString());
					PingTestMainActivity.this.finish();
				}
			}
		});
	}

	private void pingOneByOne(final int index){
		new Thread(){
			@Override
			public void run() {
				try {
					if(mTestOver){
						return;
					}
					SystemClock.sleep(10);
					if(index < mDataList.size()){
						PingInfo pingInfo = mDataList.get(index);
						PingThread pingThread  = new PingThread(pingInfo, index, mHandler, 2, 2);
						pingThread.start();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			};
		}.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == EnumRequest.TASK_PING.toInt()){
				PingInfo pingInfo = (PingInfo)msg.obj;
				pingInfo.toStrState();
				int index = msg.arg1;
				mDataList.set(index, pingInfo);

				index++;
				int progress = (int)((index/(float)mSize) * 100);
				mTvValue.setText(progress+"%");
				mTvName.setText(pingInfo.getName());

				if(index >= mDataList.size() && !mIsOver){
					mIsOver = true;
					mHandler.sendEmptyMessageDelayed(WHAT_ITEM_ONE, 200);
				}else{
					pingOneByOne(index);
				}
			}else if(msg.what == WHAT_ITEM_ONE){
				if(PingTestMainActivity.this.isFinishing()){
					return;
				}

				//进入数据展示页面
				Intent intent = getIntent();
				intent.putParcelableArrayListExtra("data", mDataList);
				intent.setClass(PingTestMainActivity.this, PingTestResultActivity.class);
				PingTestMainActivity.this.startActivity(intent);
				PingTestMainActivity.this.finish();
			}
		};
	};

	/**
	 * 初始化数据
	 * @param response
	 */
	private void initData(PingTestConfigResponse response){
		ArrayList<PingTestConfigBean> data = response.datas;
		if(data != null && data.size() > 0){
			for(PingTestConfigBean bean : data){
				PingInfo pingInfo = new PingInfo();
				pingInfo.setIp(bean.TOOL_URL);
				pingInfo.setName(bean.TOOL_NAME);
				pingInfo.imgUrl=(bean.TOOL_IMG);

				mDataList.add(pingInfo);
			}
			mSize = mDataList.size();

			pingOneByOne(0);
		}else{
			showCommon("配置为空");
			PingTestMainActivity.this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTestOver =true;
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_ping_test_main);
	}
}
