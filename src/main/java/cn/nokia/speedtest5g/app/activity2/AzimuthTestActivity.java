package cn.nokia.speedtest5g.app.activity2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.util.gps.LocationBean;

/**
 * 方位角测试
 * @author zwq
 */
@SuppressWarnings("deprecation")
public class AzimuthTestActivity extends BaseActionBarActivity {

	//是否采集数据
	private boolean isCollect =true;
	//传感器
	private SensorManager mSensorManager = null;
	//方位传感器  
	private Sensor mAzimuthSensor; 
	//加速度传感器（倾斜滚动小球）
	private Sensor mAccelerationSensor;
	//方位角，方向，经度，纬度,提示信息
	private TextView mTvAzimuth,mTvDirection,mTvLon,mTvLat,mTvToast;
	//罗盘图
	private ImageView mIvDisc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_azimuthtest);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		
		init("罗盘仪", true);
	}
	
	@Override
	public void init(Object titleId, boolean isBack) {
		// TODO Auto-generated method stub
		super.init(titleId, isBack);
		mTvAzimuth 	 = (TextView) findViewById(R.id.azimuthTest_tv_azimuth);
		mTvDirection = (TextView) findViewById(R.id.azimuthTest_tv_direction);
		mTvLon 		 = (TextView) findViewById(R.id.azimuthTest_tv_lon);
		mTvLat 		 = (TextView) findViewById(R.id.azimuthTest_tv_lat);
		mTvToast 	 = (TextView) findViewById(R.id.azimuthTest_tv_toast);
		mIvDisc		 = (ImageView) findViewById(R.id.azimuthTest_iv_disc);
		
		//注册传感器
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//初始化传感器  
		mAzimuthSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);  
		//初始化加速度传感器
		mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
		registerReceiver(receiverLocation, new IntentFilter(TypeKey.getInstance().PACKAGE_GPS));
		super.onResume();
	}
	
	@Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        // 解除注册  
		if (mSensorManager != null) {
			 mSensorManager.unregisterListener(mListener_ori);  
		}
		unregisterReceiver(receiverLocation);
        super.onPause();  
    }  
	
	public void onClickListener(View v){
        if (v.getId() == R.id.azimuthTest_view_layout) {//点击页面触发是否测试
            if (isCollect) {
                mTvToast.setText(getString(R.string.testStart));
            } else {
                mTvToast.setText(getString(R.string.testLocking));
            }
            isCollect = !isCollect;
        }
	}
	
	private SensorEventListener mListener_ori = new SensorEventListener () {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		@Override
        public void onSensorChanged(SensorEvent event) {  
			if (isCollect) {
				//方位角
				if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
					calculateOrientation((int)event.values[0]);
				}
			}
        }  
	};
	
	private float currentDegree = 0f;  
	// 计算方向  
    private void calculateOrientation(int azimuth) {  
        if (azimuth >= 338 || azimuth < 22) { 
        	mTvDirection.setText("正北");  
        } else if (azimuth >= 22 && azimuth < 68) {  
        	mTvDirection.setText("东北");  
        } else if (azimuth >= 68 && azimuth <= 112) {  
        	mTvDirection.setText("正东");  
        } else if (azimuth >= 112 && azimuth < 158) {  
        	mTvDirection.setText("东南");  
        } else if (azimuth >= 158 && azimuth < 202) {  
        	mTvDirection.setText("正南");  
        } else if (azimuth >= 202 && azimuth < 248) {  
        	mTvDirection.setText("西南");  
        } else if (azimuth >= 248 && azimuth < 292) {  
        	mTvDirection.setText("正西");  
        } else if (azimuth >= 292 && azimuth < 338) {  
        	mTvDirection.setText("西北");  
        } 
    	mTvAzimuth.setText(azimuth + "°");
    	
        
        /* 
        RotateAnimation类：旋转变化动画类 
          
        	参数说明: 

        fromDegrees：旋转的开始角度。 
        toDegrees：旋转的结束角度。 
        pivotXType：X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。 
        pivotXValue：X坐标的伸缩值。 
        pivotYType：Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。 
        pivotYValue：Y坐标的伸缩值 
        */  
        RotateAnimation ra = new RotateAnimation(currentDegree, -azimuth,  
                Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f);  
        //旋转过程持续时间  
        ra.setDuration(200);  
        ra.setFillAfter(true);
        //罗盘图片使用旋转动画  
        mIvDisc.startAnimation(ra);  
        //记录上一次的选择度数
        currentDegree = -azimuth;  
    }
	
	/**
	 * 定位广播
	 */
	private BroadcastReceiver receiverLocation = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (isCollect && intent != null) {
				LocationBean location = (LocationBean) intent.getSerializableExtra("location");
				mTvLat.setText("纬度：" + location.Latitude);
				mTvLon.setText("经度：" + location.Longitude);
			}
		}
	};
	
	@Override
	public void initStatistics() {
		installStatistics(R.string.code_fwj);
	}
}
