package cn.nokia.speedtest5g.view;

import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 三层波浪动画
 * 
 * @author jinhaizheng
 */
public class WaveView extends View {
	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth;

	/**
	 * 一个屏幕内显示几个周期
	 */
	private int mWaveCount;

	/**
	 * 一个周期波浪的长度
	 */
	private int mWaveLength;

	/**
	 * 振幅（峰值）
	 */
	private int mWaveAmplitude;

	/**
	 * 平移偏移量
	 */
	private int firstOffset;
	private int secondOffset;
	private int thirdOffset;

	/**
	 * 波浪的路径
	 */
	Path firstWavePath;
	Path secondWavePath;
	Path thirdWavePath;

	/**
	 * 波浪的画笔
	 */
	private Paint firstWavePaint;
	private Paint secondWavePaint;
	private Paint thirdWavePaint;

	/**
	 * 波形的颜色
	 */
	private int firstWaveColor = 0xffFFFFFF;
	private int secondWaveColor = 0x99FFFFFF;
	private int thirdWaveColor = 0x33FFFFFF;

	/**
	 * 动画设置
	 */
	private ValueAnimator firstValueAnimator;
	private ValueAnimator secondValueAnimator;
	private ValueAnimator thirdValueAnimator;

	public WaveView(Context context) {
		this(context, null);

	}

	public WaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mWaveAmplitude = UtilHandler.getInstance().dpTopx(20);
		mWaveLength    = UtilHandler.getInstance().dpTopx(500);
		firstWavePath  = new Path();
		secondWavePath = new Path();
		thirdWavePath  = new Path();
		initPaint();
	}
	
	public void setColor(int colorFirst,int colorSecond,int colorThird){
		this.firstWaveColor  = colorFirst;
		this.secondWaveColor = colorSecond;
		this.thirdWaveColor  = colorThird;
		initPaint();
	}
	
	/**
	 * 设置麦克风大小0-100
	 * @param volume
	 */
	public void setVolume(int volume){
		if (volume > 15) {
			volume = volume/5;
		}else {
			volume = 3;
		}
		mWaveAmplitude = UtilHandler.getInstance().dpTopx(volume);
		if (firstValueAnimator == null) {
			long duration  = 3000 - volume * 2;
			initAnimation(duration);
		}
	}

	/**
	 * 初始化画笔
	 */
	private void initPaint() {
		firstWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		firstWavePaint.setColor(firstWaveColor);
		firstWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		firstWavePaint.setAntiAlias(true);

		secondWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		secondWavePaint.setColor(secondWaveColor);
		secondWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		secondWavePaint.setAntiAlias(true);

		thirdWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thirdWavePaint.setColor(thirdWaveColor);
		thirdWavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		thirdWavePaint.setAntiAlias(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mScreenWidth = w;
		// 加上1.5是为了保证至少有两个波形（屏幕外边一个完整的波形，屏幕里边一个完整的波形）
		mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawSinWave(canvas, firstWavePaint, firstWavePath, firstOffset);
		drawSinWave(canvas, secondWavePaint, secondWavePath, secondOffset);
		drawSinWave(canvas, thirdWavePaint, thirdWavePath, thirdOffset);
	}

	/**
	 * 画sin函数图像的波形（正弦波）
	 * 
	 * @param canvas
	 *            画布
	 * @param wavePaint
	 *            画笔
	 * @param wavePath
	 *            波形路径
	 * @param offset
	 *            平移偏移量
	 */
	private void drawSinWave(Canvas canvas, Paint wavePaint, Path wavePath, int offset) {
		wavePath.reset();
//		wavePath.moveTo(-mWaveLength + offset, mWaveAmplitude);
//		for (int i = 0; i < mWaveCount; i++) {
//			// 第一个控制点的坐标为(-mWaveLength * 3 / 4,-mWaveAmplitude)
//			wavePath.quadTo(-mWaveLength * 3 / 4 + offset + i * mWaveLength,
//			        -mWaveAmplitude,
//			        -mWaveLength / 2 + offset + i * mWaveLength,
//			        mWaveAmplitude);
//			// 第二个控制点的坐标为(-mWaveLength / 4,3 * mWaveAmplitude)
//			wavePath.quadTo(-mWaveLength / 4 + offset + i * mWaveLength,
//			        3 * mWaveAmplitude,
//			        offset + i * mWaveLength,
//			        mWaveAmplitude);
//		}
		int absY = getHeight() - mWaveAmplitude * 2;
		wavePath.moveTo(-mWaveLength + offset,mWaveAmplitude + absY);
		for (int i = 0; i < mWaveCount; i++) {
			// 第一个控制点的坐标为(-mWaveLength * 3 / 4,-mWaveAmplitude)
			wavePath.quadTo(-mWaveLength * 3 / 4 + offset + i * mWaveLength,
			        -mWaveAmplitude + absY,
			        -mWaveLength / 2 + offset + i * mWaveLength,
			        mWaveAmplitude + absY);
			// 第二个控制点的坐标为(-mWaveLength / 4,3 * mWaveAmplitude)
			wavePath.quadTo(-mWaveLength / 4 + offset + i * mWaveLength,
			        3 * mWaveAmplitude + absY,
			        offset + i * mWaveLength,
			        mWaveAmplitude + absY);
		}
		wavePath.lineTo(getWidth(), getHeight());
		wavePath.lineTo(0, getHeight());
		wavePath.close();

		canvas.drawPath(wavePath, wavePaint);
	}

	/**
	 * 初始化波形动画
	 */
	private void initAnimation(long duration) {
		// 波形一
		firstValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
		firstValueAnimator.setDuration(duration);
		firstValueAnimator.setStartDelay(300);
		firstValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		firstValueAnimator.setInterpolator(new LinearInterpolator());
		firstValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				firstOffset = (int) animation.getAnimatedValue();
				invalidate();
			}
		});
		firstValueAnimator.start();
		// 波形二
		secondValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
		secondValueAnimator.setDuration(duration + 1000);
		secondValueAnimator.setStartDelay(300);
		secondValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		secondValueAnimator.setInterpolator(new LinearInterpolator());
		secondValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				secondOffset = (int) animation.getAnimatedValue();
				invalidate();
			}
		});
		secondValueAnimator.start();
		// 波形三
		thirdValueAnimator = ValueAnimator.ofInt(0, mWaveLength);
		thirdValueAnimator.setDuration(duration + 2000);
		thirdValueAnimator.setStartDelay(300);
		thirdValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		thirdValueAnimator.setInterpolator(new LinearInterpolator());
		thirdValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				thirdOffset = (int) animation.getAnimatedValue();
				invalidate();
			}
		});
		thirdValueAnimator.start();
	}

	public void startAnimation() {
		initAnimation(3000);
	}

	public void stopAnimation() {
		if (firstValueAnimator != null) {
			firstValueAnimator.cancel();
			firstValueAnimator = null;
		}
		if (secondValueAnimator != null) {
			secondValueAnimator.cancel();
			secondValueAnimator = null;
		}
		if (thirdValueAnimator != null) {
			thirdValueAnimator.cancel();
			thirdValueAnimator = null;
		}
	}

	@SuppressLint("NewApi")
	public void pauseAnimation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (firstValueAnimator != null) {
				firstValueAnimator.pause();
			}
			if (secondValueAnimator != null) {
				secondValueAnimator.pause();
			}
			if (thirdValueAnimator != null) {
				thirdValueAnimator.pause();
			}
		}
	}

	@SuppressLint("NewApi")
	public void resumeAnimation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (firstValueAnimator != null) {
				firstValueAnimator.resume();
			}
			if (secondValueAnimator != null) {
				secondValueAnimator.resume();
			}
			if (thirdValueAnimator != null) {
				thirdValueAnimator.resume();
			}
		}
	}
}
