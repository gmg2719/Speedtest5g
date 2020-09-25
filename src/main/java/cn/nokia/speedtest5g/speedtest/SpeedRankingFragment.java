package cn.nokia.speedtest5g.speedtest;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.util.JsonHandler;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedRankingAdapter;
import cn.nokia.speedtest5g.speedtest.adapter.SpeedRankingMainAdapter;
import cn.nokia.speedtest5g.speedtest.bean.BeanSpeedRanking;
import cn.nokia.speedtest5g.speedtest.bean.RequestSpeedRanking;
import cn.nokia.speedtest5g.speedtest.bean.ResponseSpeedRanking;
import cn.nokia.speedtest5g.view.viewpager.LazyFragment;

/**
 * 测速排行fragment
 * @author JQJ
 *
 */
public class SpeedRankingFragment extends LazyFragment{

	private String mType = SpeedRankingMainAdapter.WHAT_TYPE_WIFI;
	private ResponseSpeedRanking mResponse = null;

	private ListView mLvContent = null;
	private SpeedRankingAdapter mAdapter = null;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.jqj_speed_ranking_fragment);
		Bundle bundle =  getArguments();
		if(bundle != null){
			mType = bundle.getString("data");
		}

		mLvContent = (ListView)findViewById(R.id.speed_ranking_fragment_lv);
		mAdapter = new SpeedRankingAdapter(getActivity());
		mLvContent.setAdapter(mAdapter);

		getDataFromServer();

	}

	private void getDataFromServer(){
		((SpeedRankingMainActivity)getActivity()).showMyDialog();
		String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_GET_SPEED_TOP_VERSION);
		RequestSpeedRanking request = new RequestSpeedRanking();
		request.netType = mType;
		String jsonData = JsonHandler.getHandler().toJson(request);
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(requestUrl, jsonData, 
				-1, new ListenerBack() {

			@Override
			public void onListener(int type, Object object, boolean isTrue) {
				((SpeedRankingMainActivity)getActivity()).dismissMyDialog();
				if(isTrue){
					mResponse = JsonHandler.getHandler().getTarget(object.toString(), ResponseSpeedRanking.class);
					if(mResponse.isRs()){
						ArrayList<BeanSpeedRanking> list = mResponse.datas;
						mAdapter.updateData(list);
					}else{
						mAdapter.updateData(new ArrayList<BeanSpeedRanking>());
					}
				}else{
					mAdapter.updateData(new ArrayList<BeanSpeedRanking>());
				}
			}
		});
	}

	@Override
	protected void onResumeLazy() {
	    super.onResumeLazy();
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
	}
}
