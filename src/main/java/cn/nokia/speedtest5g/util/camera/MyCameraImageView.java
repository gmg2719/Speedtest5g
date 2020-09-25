package cn.nokia.speedtest5g.util.camera;

import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import com.android.volley.util.ConversionUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 方位角、下倾角线条模式
 * @author zwq
 *
 */
@SuppressLint("AppCompatCustomView")
public class MyCameraImageView extends ImageView {

	public MyCameraImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyCameraImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyCameraImageView(Context context) {
		super(context);
	}

	private TextView tv;
	private Paint pXQJ = null;
	// 起始坐标点X，Y，结束坐标点X,Y
	private float mStartX, mStartY;
	//下倾角角度
	private double downtilt;
	
	public float mStopX, mStopY;
	//判断当前下倾角是自动还是手动 true自动  false手动
	private boolean mXqjIsAuto = true;

	// 设置下倾角显示
	public void setText(TextView t) {
		this.tv = t;
		initpXQJ();
	}
	
	//初始化下倾角线条
	private void initpXQJ(){
		if (this.pXQJ == null) {
			this.pXQJ = new Paint();
			this.pXQJ.setColor(Color.YELLOW);
			this.pXQJ.setStrokeWidth(3f);
			this.pXQJ.setAntiAlias(true);// 锯齿
			this.pXQJ.setDither(true);// 抖动效果
			this.mStartX = getWidth()/2;
			this.mStartY = getHeight()/2;
			this.mStopX = getWidth()/2 + getWidth()/4;
			this.mStopY = getHeight()/4;
		}
	}
	
	private Paint mPaintMode = null,mPaintModeXqjCenter;
	
	private int mModeCamera;
	/**
	 * 设置拍照类型 0普通 1方位角 2下倾角
	 * @param modeCamera
	 */
	public void setModeCamera(int modeCamera){
		this.mModeCamera = modeCamera;
		if (mPaintMode == null) {
			mPaintMode = new Paint();
			mPaintMode.setColor(Color.RED);
			mPaintMode.setStrokeWidth(0.8f);
			mPaintMode.setAntiAlias(true);// 锯齿
			mPaintMode.setDither(true);// 抖动效果
		}
		if (modeCamera == 2 && mPaintModeXqjCenter == null) {
			mPaintModeXqjCenter = new Paint();
			mPaintModeXqjCenter.setColor(Color.RED);
			mPaintModeXqjCenter.setStrokeWidth(3f);
			mPaintModeXqjCenter.setAntiAlias(true);// 锯齿
			mPaintModeXqjCenter.setDither(true);// 抖动效果
		}
	}
	
	//下倾角
	private String mDowntilt;
	
	/**
	 * 根据下倾角角度画线
	 * @param isAuto true自动 false手动
	 */
	public void setDowntilt(String downtilt,boolean isAuto){
		if (TextUtils.isEmpty(downtilt)) {
			downtilt = "0";
		}
		this.mDowntilt = downtilt;
		this.mXqjIsAuto = isAuto;
		this.mStartX = getWidth()/2;
		this.mStartY = getHeight()/2;
		double ae = UtilHandler.getInstance().toDouble(downtilt, 0);
		float fae = ae >=0 ? (float)((90-ae) * Math.PI / 180):(float)((-ae-90) * Math.PI / 180) ;
		int l = (int)(getWidth()*0.35);
		this.mStopX = (float) (this.mStartX + Math.cos(fae) * l);
		this.mStopY = (float) (this.mStartY - Math.sin(fae) * l);
		if (this.tv != null) {
			this.tv.setText("下倾角：" + downtilt + "°" + (isAuto ? "" : "(手动)"));
			this.tv.setVisibility(View.VISIBLE);
		}
		invalidate();
	}
	
	public String getDowntilt(){
		return mDowntilt;
	}

	private boolean isTouch = false;
	/**
	 * 注册触摸
	 */
	public void registerTouch(){
		initpXQJ();
		this.isTouch = true;
		if (this.tv != null) {
			this.tv.setVisibility(View.VISIBLE);
		}
		invalidate();
	}
	
	/**
	 * 清除触摸事件
	 */
	public void unregisterTouch(){
		this.isTouch = false;
		if (this.tv != null) {
			this.tv.setVisibility(View.GONE);
		}
		invalidate();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (isTouch) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.mStartX = getWidth()/2;
				this.mStartY = getHeight()/2;
				break;
			case MotionEvent.ACTION_MOVE:
				// 这里是实现指标移动
				this.mStopX = event.getX();
				this.mStopY = event.getY();
				if (this.mStopX <= this.mStartX) {
					if (this.mStopY >= this.mStartY) {
						downtilt = 0 - (90
								- Math.atan((this.mStartY - this.mStopY)
										/ (this.mStopX - this.mStartX))
								/ Math.PI * 180);
					} else {
						downtilt = 90
								+ Math.atan((this.mStartY - this.mStopY)
										/ (this.mStopX - this.mStartX))
								/ Math.PI * 180;
					}
				} else {
					if (this.mStopY >= this.mStartY) {
						downtilt = 0 - (90
								+ Math.atan((this.mStartY - this.mStopY)
										/ (this.mStopX - this.mStartX))
								/ Math.PI * 180);
					} else {
						downtilt = 90
								- Math.atan((this.mStartY - this.mStopY)
										/ (this.mStopX - this.mStartX))
								/ Math.PI * 180;
					}
				}
				this.mDowntilt = String.valueOf(ConversionUtil.getInstances().toIntScale(downtilt));
				tv.setText("下倾角：" + this.mDowntilt + "°");
				invalidate();
				getParent().requestDisallowInterceptTouchEvent(true);
				break;
			default:
				break;
			}
			return true;
		}
		return super.dispatchTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//方位角
		if (mModeCamera == 1) {
			//中上线条
			canvas.drawLine(0, getHeight()/2 - getHeight()/10, getWidth(), getHeight()/2 - getHeight()/10, mPaintMode);
			//中下线条
			canvas.drawLine(0, getHeight()/2 + getHeight()/10, getWidth(), getHeight()/2 + getHeight()/10, mPaintMode);
			//中左线条
			canvas.drawLine(getWidth()/2 - getWidth()/7.5f, 0, getWidth()/2 - getWidth()/7.5f, getHeight(), mPaintMode);
			//中右线条
			canvas.drawLine(getWidth()/2 + getWidth()/7.5f, 0, getWidth()/2 + getWidth()/7.5f, getHeight(), mPaintMode);
		//下倾角	
		}else if (mModeCamera == 2) {
			//中上线条1
			canvas.drawLine(0, getHeight()/2 - getHeight()/9.2f, getWidth(), getHeight()/2 - getHeight()/9.2f, mPaintMode);
			//中上线条2
			canvas.drawLine(0, getHeight()/2 - getHeight()/4.7f, getWidth(), getHeight()/2 - getHeight()/4.7f, mPaintMode);
			//中下线条1
			canvas.drawLine(0, getHeight()/2 + getHeight()/9.2f, getWidth(), getHeight()/2 + getHeight()/9.2f, mPaintMode);
			//中下线条2
			canvas.drawLine(0, getHeight()/2 + getHeight()/4.7f, getWidth(), getHeight()/2 + getHeight()/4.7f, mPaintMode);
			//中间左1
			canvas.drawLine(getWidth()/2 - getWidth()/7.6f, getHeight()/2 - getHeight()/4.7f, getWidth()/2 - getWidth()/7.6f, getHeight()/2 + getHeight()/4.7f, mPaintMode);
			//中间左2
			canvas.drawLine(getWidth()/2 - getWidth()/3.8f, getHeight()/2 - getHeight()/4.7f, getWidth()/2 - getWidth()/3.8f, getHeight()/2 + getHeight()/4.7f, mPaintMode);
			//中间右1
			canvas.drawLine(getWidth()/2 + getWidth()/7.6f, getHeight()/2 - getHeight()/4.7f, getWidth()/2 + getWidth()/7.6f, getHeight()/2 + getHeight()/4.7f, mPaintMode);
			//中间右2
			canvas.drawLine(getWidth()/2 + getWidth()/3.8f, getHeight()/2 - getHeight()/4.7f, getWidth()/2 + getWidth()/3.8f, getHeight()/2 + getHeight()/4.7f, mPaintMode);
			//中间横线
			canvas.drawLine(getWidth()/2 - getWidth()/3.8f, getHeight()/2, getWidth()/2 + getWidth()/3.8f, getHeight()/2, mPaintModeXqjCenter);
			//中间竖线
			canvas.drawLine(getWidth()/2, getHeight()/2 - getHeight()/4.7f, getWidth()/2, getHeight()/2 + getHeight()/4.7f, mPaintModeXqjCenter);
			//画出角度线
			if (pXQJ != null && (isTouch || !mXqjIsAuto)) {
				canvas.drawLine(mStartX, mStartY, mStopX, mStopY, pXQJ);
			}
		}
		canvas.restore();
	}
}
