package cn.nokia.speedtest5g.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

/**
 * 水波扩散按钮
 *
 * @author jinhaizheng
 */
public class WaveButton extends View {
	private Paint mPaint;
	private int mRadius;// 里面圆圈的半径
	private Context mContext;
	private int mWidth;// 控件的宽度
	private int mStrokeWidth;
	private int mFillColor;
	private int mCircleStrokeColor;
	private int mTextSize;
	private int gapSize;// 圆圈间隙大小
	private int firstRadius;// 第一个圆圈的半径
	private int numberOfCircle;
	private int mLineColor;
	private String text;
	private int mTextColor;
	private Paint mTextPaint;
	private boolean isFirstTime = true;

	private Bitmap mBitmap1 = null;
	private int mBitmapWidth1 = 0;
	private int mBitmapHeight1 = 0;
	private Bitmap mBitmap2 = null;
	private int mBitmapWidth2 = 0;
	private int mBitmapHeight2 = 0;

	private Bitmap mBitmap = null;
	private int mBitmapWidth = 0;
	private int mBitmapHeight = 0;
	private int mType = 1; //默认大

	public WaveButton(Context context) {
		this(context, null);
	}

	public WaveButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaveButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveButton);
		text = ta.getString(R.styleable.WaveButton_text);
		if (text == null)
			text = "";
		mTextColor = ta.getColor(R.styleable.WaveButton_textColor, Color.BLACK);
		mTextSize = ta.getDimensionPixelSize(R.styleable.WaveButton_textSize, 50);
		mFillColor = ta.getColor(R.styleable.WaveButton_fillColor, Color.WHITE);
		mLineColor = ta.getColor(R.styleable.WaveButton_waveColor, Color.BLACK);
		mCircleStrokeColor = ta.getColor(R.styleable.WaveButton_wbStrokeColor, mFillColor);
		ta.recycle(); // 注意回收

		init(context);
	}

	private void init(Context context) {
		mContext = context;

		mWidth = UtilHandler.getInstance().dpTopx(400);
		mStrokeWidth = 4;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStyle(Paint.Style.STROKE);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(mTextColor);
		numberOfCircle = 4;

		mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_speed_test_wave_btn_bg_1);
		mBitmapWidth1 = mBitmap1.getWidth();
		mBitmapHeight1 = mBitmap1.getHeight();
		mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_speed_test_wave_btn_bg_2);
		mBitmapWidth2 = mBitmap2.getWidth();
		mBitmapHeight2 = mBitmap2.getHeight();
	}

	public void setType(int type){ //type 1为大  2为小
		this.mType = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		switch (widthMode) {
		case MeasureSpec.EXACTLY:
			// match_parent 或者 精确的数值
			mWidth = width;
			break;
		}
		switch (heightMode) {
		case MeasureSpec.EXACTLY:
			mWidth = Math.min(mWidth, height);
			break;
		}
		mRadius = mWidth / 4;
		gapSize = mRadius / numberOfCircle;
		firstRadius = mRadius + gapSize;

		setMeasuredDimension(mWidth, mWidth);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(mWidth / 2, mWidth / 2);// 平移

		if(mType == 1){
			mBitmap = mBitmap1;
			mBitmapWidth = mBitmapWidth1;
			mBitmapHeight = mBitmapHeight1;
		}else if(mType == 2){
			mBitmap = mBitmap2;
			mBitmapWidth = mBitmapWidth2;
			mBitmapHeight = mBitmapHeight2;
		}

		// 画中间的圆
		mPaint.setAlpha(255);
		mPaint.setColor(mFillColor);
		mPaint.setStyle(Paint.Style.FILL);
		//		canvas.drawCircle(0, 0, mRadius, mPaint);
		canvas.drawBitmap(mBitmap, -mWidth / 2 + mBitmapWidth/3f, -mWidth / 2 + mBitmapHeight/3f, mPaint);

		// 画圆的边
		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setColor(mCircleStrokeColor);
		mPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(0, 0, mRadius, mPaint);
		// 画文字
		Rect rect = new Rect();// 文字的区域
		mTextPaint.getTextBounds(text, 0, text.length(), rect);
		int height = rect.height();
		int width = rect.width();
		canvas.drawText(text, -width / 2, height / 2, mTextPaint);

		// 画周围的波浪
		// firstRadius += 3;// 每次刷新半径增加3像素
		firstRadius %= (mWidth / 2);// 控制在控件的范围中
		if (firstRadius < mRadius) {
			isFirstTime = false;
		}
		firstRadius = checkRadius(firstRadius);// 检查半径的范围
		mPaint.setColor(mLineColor);
		mPaint.setStyle(Paint.Style.STROKE);
		// 画波浪
		for (int i = 0; i < numberOfCircle; i++) {
			int radius = (firstRadius + i * gapSize) % (mWidth / 2);
			if (isFirstTime && radius > firstRadius) {
				continue;
			}
			radius = checkRadius(radius);// 检查半径的范围
			// 用半径来计算透明度 半径越大 越透明
			double x = (mWidth / 2 - radius) * 1.0 / (mWidth / 2 - mRadius);
			mPaint.setAlpha((int) (255 * x));
			canvas.drawCircle(0, 0, radius, mPaint);
		}
	}

	// 检查波浪的半径 如果小于圆圈，那么加上圆圈的半径
	private int checkRadius(int radius) {
		if (radius < mRadius) {
			return radius + mRadius + gapSize;
		}
		return radius;
	}

	private Timer timer;
	private boolean isPlayAnimation;

	public void startAnimation() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				firstRadius += 2;// 每次刷新半径增加2像素
				postInvalidate();
			}
		}, 0, 60);// 每60ms刷新刷图扩散firstRadius像素,决定扩散速度
		isPlayAnimation = true;
	}

	public void stopAnimation() {
		if (timer != null && isPlayAnimation) {
			timer.cancel();
			timer = null;
			isPlayAnimation = false;

			invalidate();
		}
	}

	public boolean isPlayAnimation() {
		return isPlayAnimation;
	}
}
