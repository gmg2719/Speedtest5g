package cn.nokia.speedtest5g.speedtest.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.dialog.BaseAlertDialog;
import cn.nokia.speedtest5g.speedtest.bean.BeanBaseConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanRkConfig;
import cn.nokia.speedtest5g.speedtest.bean.BeanWlConfig;

/**
 * 速率介绍弹窗
 * @author JQJ
 *
 */
public class SpeedTestPresentDialog extends BaseAlertDialog{

	private ImageView mIvImage = null;
	private TextView mTvValue_1 = null;
	private TextView mTvValue_2 = null;
	private TextView mTvValue_3 = null;
	private float mAvgDwonload = 0;
	private String mNetType = "";
	private ProgressBar mProgressBar = null;
	private int mRankValue = 1;
	private Button mBtn = null;

	public SpeedTestPresentDialog(Context context) {
		super(context);
	}

	public void show(float avgDownload, String netType) {
		super.show();
		this.mAvgDwonload = avgDownload;
		this.mNetType = netType;

		if(mTvValue_1 != null){
			mTvValue_1.setText(String.valueOf(mAvgDwonload));
		}

		updateImage();
		updateRank();

		if(mProgressBar != null){
			mProgressBar.setProgress(mRankValue);
		}
	}

	/**
	 * 更新图片
	 */
	private void updateImage(){
		try{
			if(mAvgDwonload <= 0){
				mTvValue_2.setText("较慢");
				return;
			}
			boolean isGoto = false;
			String imgType = "";
			BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
			if(beanBaseConfig != null){
				ArrayList<BeanWlConfig> list = beanBaseConfig.wlConfigs;
				if(list != null){
					for(BeanWlConfig config : list){
						if(mAvgDwonload >= config.threshold && mAvgDwonload < config.endThreshold){ //在这区间
							if(mNetType.equalsIgnoreCase("2G") || mNetType.equalsIgnoreCase("3G") || mNetType.equalsIgnoreCase("4G")){
								if("4G".equals(config.netType)){
									isGoto = true;
									imgType = config.img;
									mTvValue_2.setText(config.showDesc);
									break;
								}
							}else if(mNetType.equalsIgnoreCase("5G") || mNetType.equalsIgnoreCase("WiFi")){
								if("5G".equals(config.netType)){
									isGoto = true;
									imgType = config.img;
									mTvValue_2.setText(config.showDesc);
									break;
								}
							}
						}
					}
					if(!isGoto){
						mTvValue_2.setText("较慢");
					}
				}
				int drawableId = R.drawable.icon_speed_test_present_bike_flag;
				if(imgType.equals("4G_BICYCLE") || imgType.equals("5G_BICYCLE")){
					drawableId = R.drawable.icon_speed_test_present_bike_flag;
				}else if(imgType.equals("4G_CAR") || imgType.equals("5G_CAR")){
					drawableId = R.drawable.icon_speed_test_present_car_flag;
				}else if(imgType.equals("4G_ROCKET") || imgType.equals("5G_ROCKET")){
					drawableId = R.drawable.icon_speed_test_present_rocket_flag;
				}
				mIvImage.setImageResource(drawableId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 超过用户数
	 */
	private void updateRank(){
		try{
			if(mAvgDwonload <= 0){
				mRankValue = 1;
				mTvValue_3.setText("1");
				return;
			}
			boolean isGoto = false;
			BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;
			if(beanBaseConfig != null){
				ArrayList<BeanRkConfig> list = beanBaseConfig.rkConf;
				if(list != null){
					for(BeanRkConfig config : list){
						if(mAvgDwonload >= config.threshold && mAvgDwonload < config.endThreshold){ //在这区间
							isGoto = true;
							mRankValue = Integer.parseInt(config.id);
							mTvValue_3.setText(config.id);
							return;
						}
					}
					if(!isGoto){
						mRankValue = 99;
						mTvValue_3.setText("99");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_speed_test_present_dialog);

		mIvImage = (ImageView)findViewById(R.id.speed_test_present_iv_image);
		mTvValue_1 = (TextView)findViewById(R.id.speed_test_present_tv_value_1);
		mTvValue_2 = (TextView)findViewById(R.id.speed_test_present_tv_value_2);
		mTvValue_3 = (TextView)findViewById(R.id.speed_test_present_tv_value_3);
		mBtn = (Button)findViewById(R.id.speed_test_present_btn);
		mBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SpeedTestPresentDialog.this.dismiss();
			}
		});
		mProgressBar = (ProgressBar)findViewById(R.id.speed_test_present_progressbar);

        TextView mTvConent = (TextView)findViewById(R.id.speed_test_present_tv_content);
        mTvConent.setText("一般是你从服务器到终端的下载速度。\n" +
                "1B=8b  1B/s=8b/s（或1Bps=8bps）\n" +
                "1KB=1024B  1KB/s=1024B/s\n" +
                "1MB=1024KB  1MB/s=1024KB/s。");
	}

}
