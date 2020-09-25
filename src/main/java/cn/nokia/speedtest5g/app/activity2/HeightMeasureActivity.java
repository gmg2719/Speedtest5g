package cn.nokia.speedtest5g.app.activity2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.util.InputMethodUtil;

import org.xclcharts.common.MathHelper;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.activity.BookWebActivity;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.dialog.PopHeightMoreUtil;
import cn.nokia.speedtest5g.dialog.WheelOtherStrDialog;
import cn.nokia.speedtest5g.util.camera.CameraPreview;
import cn.nokia.speedtest5g.view.VerticalSeekBar;

/**
 * 高度测量工具
 * 
 * @author xujianjun
 *
 */
@SuppressLint("NewApi")
public class HeightMeasureActivity extends BaseActionBarActivity
		implements OnClickListener, SensorEventListener, ListenerBack {

	private final String hintInit = "请输入测试终端相对地面高度";
	private final String hintBottom = "设定摄像镜高度后，请瞄准目标的【底部】";
	private final String hintTop = "设定摄像镜高度后，请瞄准目标的【顶部】";

	private FrameLayout flPreview;
	private ImageView ivDial;
	private LinearLayout llTop;
	private TextView tvHint;
	private Button btnUnit;
	private ImageView ivFlash;
	private ImageView ivMore;

	private VerticalSeekBar seekBar;
	private EditText edtSeekValue;
	private TextView tvUnit;

	private TextView tvLockWidth, tvLockHeight;
	private TextView tvWidth, tvHeight;

	private LinearLayout llSave;

	// 相机surface类
	private CameraPreview mPreview;

	// 传感器
	private SensorManager mSensorManager = null;
	// 方位传感器
	private Sensor mAzimuthSensor;
	private Sensor mAccelerationSensor;

	private boolean isLockWidth = false, isLockHeight = false;

	private double bottomHeight = 0;
	private double buildingWidth, buildingHeight;
	private float angle = 0;
	private boolean isHorizontal = false;

	// 单位类型 米 厘米
	private int unitType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_height_measure);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		
		init("高度测量", true);
		
		initView(savedInstanceState);
		bindEvent();

	}

	@SuppressWarnings("deprecation")
	private void initView(Bundle savedInstanceState) {
		flPreview = (FrameLayout) findViewById(R.id.fl_preview);
		ivDial = (ImageView) findViewById(R.id.iv_dial);

		llTop = (LinearLayout) findViewById(R.id.ll_top);
		tvHint = (TextView) findViewById(R.id.tv_hint);

		btnUnit = (Button) findViewById(R.id.btn_distance_unit);
		ivFlash = (ImageView) findViewById(R.id.iv_flash);
		ivMore = (ImageView) findViewById(R.id.iv_more);

		seekBar = (VerticalSeekBar) findViewById(R.id.sb_height);
		edtSeekValue = (EditText) findViewById(R.id.edt_seek_value);
		tvUnit = (TextView) findViewById(R.id.tv_unit);

		tvLockWidth = (TextView) findViewById(R.id.tv_lock_width);
		tvLockHeight = (TextView) findViewById(R.id.tv_lock_height);

		tvWidth = (TextView) findViewById(R.id.tv_width);
		tvHeight = (TextView) findViewById(R.id.tv_height);

		llSave = (LinearLayout) findViewById(R.id.ll_save);

		mPreview = new CameraPreview(this, savedInstanceState);
		flPreview.addView(mPreview);

		ivDial.setTag(R.id.idPosition, 0);
		edtSeekValue.setText("1.2");
		bottomHeight = 1.2;
		edtSeekValue.setSelection(edtSeekValue.getText().toString().length());

		// 注册传感器
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// 初始化传感器
		mAzimuthSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//初始化加速度传感器
		mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	private void bindEvent() {
		tvLockWidth.setOnClickListener(this);
		tvLockHeight.setOnClickListener(this);
		ivFlash.setOnClickListener(this);
		btnUnit.setOnClickListener(this);
		ivMore.setOnClickListener(this);

		llSave.setOnClickListener(this);

		edtSeekValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (isLockWidth) {
					return;
				}
				if (TextUtils.isEmpty(s.toString())) {
					bottomHeight = 0;
					tvHint.setText(hintInit);
				} else {
					bottomHeight = UtilHandler.getInstance().toDouble(s.toString(), 0);
					tvHint.setText(hintBottom);
				}

				if (unitType == 0) {
					double data = UtilHandler.getInstance().toDouble(s.toString(), bottomHeight);
					if (data > 2.5) {
						edtSeekValue.setText("2.5");
						edtSeekValue.setSelection(edtSeekValue.getText().toString().length());
					}
				} else {
					double data = UtilHandler.getInstance().toDouble(s.toString(), bottomHeight);
					if (data > 250) {
						edtSeekValue.setText("250");
						edtSeekValue.setSelection(edtSeekValue.getText().toString().length());
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				InputMethodUtil.getInstances().inputMethod(HeightMeasureActivity.this, edtSeekValue.getWindowToken());
				if (isLockWidth) {
					return;
				}
				if (unitType == 0) {
					edtSeekValue.setText(progress / 100.0 + "");
					bottomHeight = progress / 100.0;
				} else {
					edtSeekValue.setText(progress + "");
					bottomHeight = progress;
				}

				if (progress == 0) {
					tvHint.setText(hintInit);
				} else {
					tvHint.setText(hintBottom);
				}

				edtSeekValue.setSelection(edtSeekValue.getText().toString().length());

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		InputMethodUtil.getInstances().inputMethod(HeightMeasureActivity.this, edtSeekValue.getWindowToken());
        int id = v.getId();
        if (id == R.id.ll_save) {
        } else if (id == R.id.btn_distance_unit) {
            if (!isLockWidth) {
                new WheelOtherStrDialog(this).show("单位类型", getArrStr(R.array.array_unit_height_measure), v.getId(), new ListenerBack() {

                    @Override
                    public void onListener(int type, Object object, boolean isTrue) {
                        // TODO Auto-generated method stub
                        Integer types = (Integer) object;
                        if (unitType == types) {
                            return;
                        }
                        unitType = types;
                        String typeName = getArrStr(R.array.array_unit_height_measure)[types];
                        btnUnit.setText(typeName);
                        if (unitType == 0) {
                            tvUnit.setText("m");
                            bottomHeight = bottomHeight / 100.0;
                            edtSeekValue.setText(bottomHeight + "");
                            edtSeekValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            edtSeekValue.setMaxEms(4);
                        } else {
                            tvUnit.setText("cm");
                            bottomHeight = bottomHeight * 100;
                            edtSeekValue.setText((int) bottomHeight + "");
                            edtSeekValue.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                            edtSeekValue.setMaxEms(3);
                        }

                    }
                }, false).isPostion();
            } else {
                showCommon("请解除锁定后再修改单位");
            }
        } else if (id == R.id.iv_more) {
            WindowManager wm = (WindowManager) HeightMeasureActivity.this.getSystemService(Context.WINDOW_SERVICE);
            @SuppressWarnings("deprecation")
            int width = wm.getDefaultDisplay().getWidth();
            PopHeightMoreUtil.getInstances().show(HeightMeasureActivity.this, llTop, HeightMeasureActivity.this, width, 0);
        } else if (id == R.id.iv_flash) {
            if (mPreview.isOpenFlash()) {
                mPreview.closeFlash();
                ivFlash.setImageDrawable(getResources().getDrawable(R.drawable.icon_flash_close));
            } else {
                mPreview.openFlash();
                ivFlash.setImageDrawable(getResources().getDrawable(R.drawable.icon_flash_open));
            }
        } else if (id == R.id.tv_lock_width) {
            if ((angle >= 1 && isHorizontal) && !isLockWidth) {
                showCommon("请保持水平");
                return;
            }
            if (!isLockHeight) {
                isLockWidth = !isLockWidth;

                if (isLockWidth) {
                    tvLockWidth.setText("解除");
                    tvLockWidth.setTextColor(getResources().getColor(R.color.gray));
                    tvLockWidth.setBackground(getResources().getDrawable(R.drawable.bg_gray_gray));
                    tvHint.setText(hintTop);
                    bottomHeight = UtilHandler.getInstance().toDouble(edtSeekValue.getText().toString(), bottomHeight);
                    seekBar.setEnabled(false);
                    edtSeekValue.setEnabled(false);
                } else {
                    tvLockWidth.setText("锁定");
                    tvLockWidth.setTextColor(getResources().getColor(R.color.black));
                    tvLockWidth.setBackground(getResources().getDrawable(R.drawable.bg_blue));
                    tvHint.setText(hintBottom);
                    seekBar.setEnabled(true);
                    edtSeekValue.setEnabled(true);
                    tvHeight.setText("");
                }

            }
        } else if (id == R.id.tv_lock_height) {
            if ((angle >= 1 && isHorizontal) && !isLockHeight) {
                showCommon("请保持水平");
                return;
            }

            if (isLockWidth) {
                isLockHeight = !isLockHeight;
                if (isLockHeight) {
                    tvLockHeight.setText("解除");
                    tvLockHeight.setTextColor(getResources().getColor(R.color.gray));
                    tvLockHeight.setBackground(getResources().getDrawable(R.drawable.bg_gray_gray));
                } else {
                    tvLockHeight.setText("锁定");
                    tvLockHeight.setTextColor(getResources().getColor(R.color.black));
                    tvLockHeight.setBackground(getResources().getDrawable(R.drawable.bg_blue));
                }
            }
        }

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// 方位角，下倾角返回
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			if (isLockWidth && isLockHeight) {
				return;
			}
			startAnim((int) event.values[2]);
			angle = Math.abs(event.values[2]);
			if (!isLockWidth) {
				buildingWidth = getBuildingWidth(
						UtilHandler.getInstance().toDouble(edtSeekValue.getText().toString(), bottomHeight),
						event.values[1]);
				if (unitType == 0) {
					tvWidth.setText(buildingWidth + "m");
				} else {
					tvWidth.setText((int) buildingWidth + "cm");
				}

			} else if (!isLockHeight) {
				buildingHeight = getBuildingHeight(buildingWidth, event.values[1]);
				if (unitType == 0) {
					tvHeight.setText(buildingHeight + "m");
				} else {
					tvHeight.setText((int) buildingHeight + "cm");
				}
			}
			WybLog.syso("---angle:"+angle +","+isHorizontal);

		}else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//加速度
			if (Math.abs(event.values[0]) > 0.5) {
				if(isHorizontal){
					isHorizontal = false;
				}
			}else {
				if(!isHorizontal){
					isHorizontal = true;
				}
				
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	// 旋转动画效果
	private void startAnim(int end) {
		ivDial.clearAnimation();

		AnimationSet animationSet = new AnimationSet(true);

		/**
		 * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
		 */
		int begin = (Integer) ivDial.getTag(R.id.idPosition);
		RotateAnimation rotateAnimation = new RotateAnimation(begin, end, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);

		rotateAnimation.setDuration(100);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setFillEnabled(true);
		animationSet.addAnimation(rotateAnimation);
		ivDial.startAnimation(rotateAnimation);
		ivDial.setTag(R.id.idPosition, end);
	}

	@Override
	protected void onResume() {
		if (mSensorManager != null) {
			// 注册监听
			if (mAzimuthSensor != null) {
				mSensorManager.registerListener(this, mAzimuthSensor, SensorManager.SENSOR_DELAY_UI);
			}
			if (mAccelerationSensor != null) {
		        mSensorManager.registerListener(this, mAccelerationSensor,SensorManager.SENSOR_DELAY_UI);
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// 解除注册
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
		super.onPause();
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_zg);
	}

	/**
	 * 计算建筑物的距离
	 * 
	 * @param height
	 * @return
	 */
	private double getBuildingWidth(double height, float angle1) {
		int angle = (int) Math.abs(angle1);
		// float width = (float) (bottomHeight * Math.tan(angle * Math.PI /
		// 180));
		double width = 0;

		if (angle >= 90) {
			return 100000;
		} else {
			width = (double) (height * Math.tan(angle * Math.PI / 180));
		}
		return MathHelper.getInstance().round(width, 2);

		// return 0;

	}

	/**
	 * 计算建筑物的高度
	 * 
	 * @param width
	 * @return
	 */
	private double getBuildingHeight(double width, float angle1) {
		int angle = (int) Math.abs(angle1);
		double height = 0;
		if (angle <= 90) {
			return bottomHeight;
		} else {
			height = (float) (width * Math.tan((angle - 90) * Math.PI / 180)) + bottomHeight;
		}

		return MathHelper.getInstance().round(height, 2);

	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		// TODO Auto-generated method stub
		super.onListener(type, object, isTrue);
		if (type == R.id.tv_guide && isTrue) {
			Intent intentUpdata = new Intent(HeightMeasureActivity.this, BookWebActivity.class);
			intentUpdata.putExtra("Url", NetWorkUtilNow.getInstances().getToIp() + getString(R.string.HEIGHT_MEASURE_GUIDE));
			intentUpdata.putExtra("Title", "指南");
			goIntent(intentUpdata, false);
		} else if (type == R.id.tv_about && isTrue) {
			Intent intentUpdata = new Intent(HeightMeasureActivity.this, BookWebActivity.class);
			intentUpdata.putExtra("Url", NetWorkUtilNow.getInstances().getToIp() + getString(R.string.HEIGHT_MEASURE_ABOUT));
			intentUpdata.putExtra("Title", "关于");
			goIntent(intentUpdata, false);
		}
	}

}
