package cn.nokia.speedtest5g.app.activity2;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarHandlerActivity;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.view.MyArcView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 下倾角测试
 * @author zwq
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class DowntiltTestActivity extends BaseActionBarHandlerActivity {

	//下倾角线条,重力进度条，重力小球
	private ImageView mIvDowntilt,mIvProgress,mIvRound;
	//下倾角度数,提示信息,不在范围内提示
	private TextView mTvDowntilt,mTvToast,mTvError;
	private MyArcView mArcView;
	//是否采集数据
	private boolean isCollect =true;
	//传感器
	private SensorManager mSensorManager = null;
	//方位传感器  
	private Sensor mAzimuthSensor; 
	//加速度传感器（倾斜滚动小球）
	private Sensor mAccelerationSensor;
	//下倾角测量值
	//	private int downtiltValue = -999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downtilttest);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init(R.string.downangel, true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		mIvDowntilt    = (ImageView) findViewById(R.id.downtiltTest_iv_arrow_left);
		mIvProgress    = (ImageView) findViewById(R.id.downtiltTest_iv_progress);
		mIvRound	   = (ImageView) findViewById(R.id.downtiltTest_iv_round);
		mTvDowntilt    = (TextView) findViewById(R.id.downtiltTest_tv_downtilt);
		mTvToast       = (TextView) findViewById(R.id.downtiltTest_tv_toast);
		mArcView	   = (MyArcView) findViewById(R.id.downtiltTest_view_arc);
		mTvError	   = (TextView) findViewById(R.id.downtiltTest_tv_error);
		mIvDowntilt.setTag(R.id.idPosition, 0);
		//注册传感器
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//初始化传感器  
		mAzimuthSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);  
		//初始化加速度传感器
		mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	public void onClickListener(View v){
		if (v.getId() == R.id.downtiltTest_view_layout) {
			if (isCollect) {
				mTvToast.setText(getString(R.string.testStart));
			}else {
				mTvToast.setText(getString(R.string.testLocking));
			}
			isCollect = !isCollect;
		}
	}

	@Override
	protected void onResume() {
		if (mSensorManager != null) {
			// 注册监听  
			if (mAzimuthSensor != null) {
				mSensorManager.registerListener(mListener_ori, mAzimuthSensor, SensorManager.SENSOR_DELAY_UI);
			}
			if (mAccelerationSensor != null) {
				mSensorManager.registerListener(mListener_ori, mAccelerationSensor,SensorManager.SENSOR_DELAY_UI);
			}
		}
		super.onResume();
	}

	@Override  
	protected void onPause() {  
		// TODO Auto-generated method stub  
		// 解除注册  
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(mListener_ori);  
		}
		super.onPause();  
	}  

	private SensorEventListener mListener_ori = new SensorEventListener () {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		@Override
		public void onSensorChanged(SensorEvent event) {  
			if (isCollect) {
				//方位角，下倾角返回
				if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
					calculateOrientation((int)event.values[1]);
					//加速度
				}else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					if (Math.abs(event.values[0]) > 0.5) {
						if (isUpdateRound) {
							mHandler.removeMessages(1);
						}
						isUpdateRound = false;
						if (event.values[0] * 3 + mIvRound.getX() >= mIvProgress.getWidth() - mIvProgress.getX() * 2) {
							mIvRound.setX(mIvProgress.getWidth() - mIvProgress.getX() * 2);
						}else if (event.values[0] * 3 + mIvRound.getX() <= mIvProgress.getX() * 2) {
							mIvRound.setX(mIvProgress.getX() * 2);
						}else {
							mIvRound.setX(event.values[0] * 3 + mIvRound.getX());
						}

						if (mIvRound.getX() < mIvProgress.getWidth()/2 - mIvProgress.getWidth()/8 ||
								mIvRound.getX() > mIvProgress.getWidth()/2 + mIvProgress.getWidth()/8) {
							mTvError.setVisibility(View.VISIBLE);
						}else {
							mTvError.setVisibility(View.GONE);
						}
					}else {
						if (!isUpdateRound) {
							Message msg = mHandler.obtainMessage();
							msg.obj = new MyEvents(ModeEnum.OTHER,R.id.downtiltTest_iv_round);
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
						isUpdateRound = true;
					}
				}
			}
		}  
	};

	//当前是否在移动小球
	private boolean isUpdateRound;
	private long updateTimeUi;
	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case OTHER:
			//小球移动控制
			if (events.getType() == R.id.downtiltTest_iv_round) {
				if (mIvRound.getX() - 10 > mIvProgress.getWidth()/2 || mIvRound.getX() + 10 < mIvProgress.getWidth()/2) {
					updateTimeUi = (long) Math.abs(50 - (mIvProgress.getWidth()/2 - mIvRound.getX())/10);
					if (mIvRound.getX() < mIvProgress.getWidth()/2) {
						mIvRound.setX(mIvRound.getX() + 5);
					}else {
						mIvRound.setX(mIvRound.getX() - 5);
					}
					Message msg = mHandler.obtainMessage();
					msg.obj = new MyEvents(ModeEnum.OTHER,R.id.downtiltTest_iv_round);
					msg.what = 1;
					mHandler.sendMessageDelayed(msg, updateTimeUi);
					if (mIvRound.getX() < mIvProgress.getWidth()/2 - mIvProgress.getWidth()/2/3 ||
							mIvRound.getX() > mIvProgress.getWidth()/2 + mIvProgress.getX() * 2) {
						mTvError.setVisibility(View.VISIBLE);
					}else {
						mTvError.setVisibility(View.GONE);
					}
				}
			}
			break;

		default:
			break;
		}
	}

	// 计算方向  
	private void calculateOrientation(int downtilt) {
		//    	if (downtilt > 0) {
		//    		downtiltValue = downtilt - 90;
		//		}else {
		//			downtiltValue = downtilt + 90;
		//		}
		//    	downtilt = 0 -downtilt;
		//    	if (downtilt < 0) {
		//    		downtilt = Math.abs(downtilt);
		//    		if (downtilt > 90) {
		//    			downtilt = 90;
		//			}
		//		}else {
		//			downtilt = 0 - downtilt;
		//			if (downtilt < -90) {
		//				downtilt = -90;
		//			}
		//		}

		downtilt = downtilt + 90;
		if ((downtilt <= 270 && downtilt >= 180) || downtilt <= -90) {
			downtilt = -90;
		}else if (downtilt >= 90) {
			downtilt = 90;
		}
		mTvDowntilt.setText(downtilt + "°");
		downtiltAnim(downtilt);
	}

	//旋转动画效果
	private void downtiltAnim(int downtilt){
		if (downtilt < 0) {
			downtilt = Math.abs(downtilt);
		}else {
			downtilt = 0 - downtilt;
		}
		Integer startRotate = (Integer) mIvDowntilt.getTag(R.id.idPosition);
		//设置旋转变化动画对象  
		//旋转的开始角度.旋转的结束角度.X轴的伸缩模式.X坐标的伸缩值.Y轴的伸缩模式.Y坐标的伸缩值
		Animation rotateAnimation = new RotateAnimation(startRotate,downtilt,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f); 
		rotateAnimation.setDuration(150);
		rotateAnimation.setFillAfter(true);
		mIvDowntilt.setTag(R.id.idPosition, downtilt);
		mIvDowntilt.startAnimation(rotateAnimation);
		mArcView.setAngle(downtilt);
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_xqj);
	}
}
