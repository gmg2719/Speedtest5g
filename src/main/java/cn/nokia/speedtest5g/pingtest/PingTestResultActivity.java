package cn.nokia.speedtest5g.pingtest;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.bean.PingInfo;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.thread.PingThread;
import cn.nokia.speedtest5g.pingtest.util.PingTestResultAdapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

/**
 * ping测试结果页面
 * @author JQJ
 *
 */
public class PingTestResultActivity extends BaseActionBarActivity {

	private static final int WHAT_ITEM_ONE = 100;

	private ListView mListView = null;
	private PingTestResultAdapter mAdapter = null;
	private List<PingInfo> mDataList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_ping_test_result_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		String MENU_NAME = getIntent().getStringExtra("MENU_NAME");
		init(MENU_NAME, true);

		mDataList = getIntent().getParcelableArrayListExtra("data");

		mListView = (ListView)findViewById(R.id.ping_test_result_lv);
		mAdapter = new PingTestResultAdapter(this, mDataList, this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if(isTrue){
			if(type == R.id.pingtest_result_adapter_iv_refresh || 
					type == R.id.pingtest_result_adapter_tv_retry){
				int position = (int)object;
				if(mDataList != null){
					PingInfo pingInfo = mDataList.get(position);
					pingInfo.status = PingInfo.STATUS_1;
					mAdapter.updateChangedItem(mListView, position, pingInfo);

					Message msg = mHandler.obtainMessage();
					msg.what = WHAT_ITEM_ONE;
					msg.obj = pingInfo;
					msg.arg1 = position;
					mHandler.sendMessageDelayed(msg, 500);
				}
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == EnumRequest.TASK_PING.toInt()){
				PingInfo pingInfo = (PingInfo)msg.obj;
				pingInfo.toStrState();
				int position = msg.arg1;
				mAdapter.updateChangedItem(mListView, position, pingInfo);
			}else if(msg.what == WHAT_ITEM_ONE){
				PingInfo pingInfo = (PingInfo)msg.obj;
				int position = msg.arg1;
				PingThread pingThread  = new PingThread(pingInfo, position, mHandler, 1, 5);
				pingThread.start();
			}
		};
	};

}
