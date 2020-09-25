package cn.nokia.speedtest5g.speedtest.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

/**
 * 速率测试仪表盘
 *
 * Created by jinhaizheng on 2019/05/31.
 */
public class SpeedTestDialView extends View {
	private int diameter; // 直径
	private float radius;// 半径
	private float tickMarkOutRadius;// 刻度线外半径（内半径 = tickMarkOutRadius-
	// tickMarkLength）
	private float tickLableRadius;// 刻度标签半径
	private float pointerRadius;// 指针半径

	private float tickMarkLength = UtilHandler.getInstance().dpTopx(15);// 刻度线长度
	private float outerRingWidth = UtilHandler.getInstance().dpTopx(10);// 刻度线长度

	private int mCount = 100;
	private float angle = 270f / 100;// 每小格刻度
	private float speed = 0;
	private float lastSpeed = 0;
	// 刻度标签
	private String[] tickLabelArr = getResources().getStringArray(R.array.arrSpeedTestTickLabel);

	private Paint outerRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 外圈圆环画笔
	private Paint tickMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 刻度线画笔
	private Paint tickLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 刻度标签画笔
	private Paint pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 指针画笔
	private Paint bottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 底部文字画笔

    private int[] mColorSet = new int[] {Color.parseColor("#E1E1E1"), Color.parseColor("#B5B5B5"),
            Color.parseColor("#939393"), Color.parseColor("#77777B"),
            Color.parseColor("#5C5D61"), Color.parseColor("#414149"),
            Color.parseColor("#3A3B42")};

	// 中心点坐标
	private float centerX;
	private float centerY;

	private int tickMarkColor = getResources().getColor(R.color.ftp_download_fea916);

	public SpeedTestDialView(Context context) {
		this(context, null);
	}

	public SpeedTestDialView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SpeedTestDialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		// 外圈圆环
		outerRingPaint.setStrokeWidth(outerRingWidth);
		outerRingPaint.setStyle(Paint.Style.STROKE);
		outerRingPaint.setStrokeCap(Paint.Cap.BUTT);
		outerRingPaint.setColor(Color.parseColor("#eff0f0"));
		outerRingPaint.setAntiAlias(true);
		outerRingPaint.setDither(true);
		// 刻度线
		tickMarkPaint.setColor(getResources().getColor(R.color.black_small));
		tickMarkPaint.setStyle(Paint.Style.FILL);
		tickMarkPaint.setStrokeWidth(UtilHandler.getInstance().dpTopx(2));
		tickMarkPaint.setAntiAlias(true);
		tickMarkPaint.setDither(true);
		// 刻度标签
		tickLabelPaint.setTextSize(UtilHandler.getInstance().getSPtoPx(10));
		tickLabelPaint.setTextAlign(Paint.Align.CENTER);
		tickLabelPaint.setColor(Color.LTGRAY);
		tickLabelPaint.setStyle(Paint.Style.FILL);
		tickLabelPaint.setAntiAlias(true);
		tickLabelPaint.setDither(true);
		// 指针
		pointerPaint.setStyle(Paint.Style.FILL);
		pointerPaint.setStrokeCap(Paint.Cap.BUTT);
		pointerPaint.setStrokeWidth(UtilHandler.getInstance().dpTopx(1));
		pointerPaint.setAntiAlias(true);
		pointerPaint.setDither(true);
		// 底部文字
		bottomTextPaint.setTextSize(UtilHandler.getInstance().getSPtoPx(25));
		bottomTextPaint.setTextAlign(Paint.Align.CENTER);
		bottomTextPaint.setColor(getResources().getColor(R.color.black_half));
		bottomTextPaint.setStrokeCap(Paint.Cap.BUTT);
		bottomTextPaint.setStyle(Paint.Style.FILL);
		bottomTextPaint.setAntiAlias(true);
		bottomTextPaint.setDither(true);
	}

	/**
	 * 设置速率值
	 *
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
		invalidate();
	}

	public void setCurrentStatus(float speed) {
		this.speed = speed;
		// 设置动画
		ObjectAnimator animator = ObjectAnimator.ofFloat(this, "speed", lastSpeed, speed);
		animator.setDuration(500);
		animator.setInterpolator(new FastOutSlowInInterpolator());
		animator.start();
		invalidate();
	}

	public void setRoundColor(int tickMarkColor) {
		this.tickMarkColor = tickMarkColor;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// animator.end();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 计算直径，取视图控件长宽小的一边
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		switch (widthMode) {
		case MeasureSpec.EXACTLY:
			// match_parent 或者 精确的数值
			diameter = width;
			break;
		}
		switch (heightMode) {
		case MeasureSpec.EXACTLY:
			diameter = Math.min(diameter, height);
			break;
		}
		// 中心点
		centerX = width / 2f;
		centerY = height / 2f;

		radius = diameter / 2f;
		tickMarkOutRadius = radius - outerRingWidth;
		tickLableRadius = tickMarkOutRadius - tickMarkLength - UtilHandler.getInstance().dpTopx(15);
		pointerRadius = tickLableRadius - UtilHandler.getInstance().dpTopx(5);
		setMeasuredDimension(diameter, diameter);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawCircle(canvas);
		// 刻度线
		drawTickMark(canvas);
		// 刻度标签
		drawTickLabel(canvas);
		// 指针
		drawPointer(canvas);
		// 下方文字
		drawBottomText(canvas);
	}

	/**
	 * 画外环
	 *
	 * @param canvas
	 */
	private void drawCircle(Canvas canvas) {
		canvas.save();

		RectF rectF = new RectF(centerX - radius + UtilHandler.getInstance().dpTopx(5),
				centerY - radius + UtilHandler.getInstance().dpTopx(5),
				centerX + radius - UtilHandler.getInstance().dpTopx(5),
				centerY + radius - UtilHandler.getInstance().dpTopx(5));
		canvas.drawArc(rectF, 135, 270, false, outerRingPaint);
		canvas.restore();
	}

	/**
	 * 画刻度线
	 *
	 * @param canvas
	 */
	private void drawTickMark(Canvas canvas) {
		canvas.save();
		for (int i = 0; i <= mCount; i++) {
			// 指针划过部分颜色(渐变色 )
			tickMarkPaint.setColor(getColorChanges(i));
			// 刻度盘 未划过部分
			if (i > (int) ((float) UtilHandler.getInstance().speed2yAxisValue(speed, 10))) {
				tickMarkPaint.setColor(getResources().getColor(R.color.black_small));
			}
			// if (i % 10 == 0) { // 逢10，刻度线加长3dp
			// canvas.drawLine((float) (centerX - Math.sin(angleToRadian(45)) *
			// radius),
			// (float) (centerY + Math.sin(angleToRadian(45)) * radius),
			// (float) (centerX - Math.sin(angleToRadian(45)) * (radius -
			// tickMarkLength - UtilHandler.getInstance().dpTopx(3))),
			// (float) (centerY + Math.sin(angleToRadian(45)) * (radius -
			// tickMarkLength - UtilHandler.getInstance().dpTopx(3))),
			// tickMarkPaint);
			// } else {
			canvas.drawLine((float) (centerX - Math.sin(angleToRadian(45)) * tickMarkOutRadius),
					(float) (centerY + Math.sin(angleToRadian(45)) * tickMarkOutRadius),
					(float) (centerX - Math.sin(angleToRadian(45)) * (tickMarkOutRadius - tickMarkLength)),
					(float) (centerY + Math.sin(angleToRadian(45)) * (tickMarkOutRadius - tickMarkLength)),
					tickMarkPaint);
			// }
			canvas.rotate(angle, centerX, centerY);
		}
		canvas.restore();
	}

	/**
	 * 画刻度标签
	 *
	 * @param canvas
	 */
	private void drawTickLabel(Canvas canvas) {
		canvas.save();
		float realProgress = (float) UtilHandler.getInstance().speed2yAxisValue(speed, 10);
		for (int i = 0; i <= 100; i++) {
			// 指针划过部分颜色(渐变色 )
			tickLabelPaint.setColor(getColorChanges(i));
			// 指针未划过部分颜色
			if (i > realProgress) {
				tickLabelPaint.setColor(getResources().getColor(R.color.black_small));
			}
			if (i == 0) {
				canvas.drawText("0",
						(float) (centerX - Math.cos(angleToRadian(45)) * tickLableRadius),
						(float) (centerY + Math.sin(angleToRadian(45)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 10) {
				canvas.drawText(tickLabelArr[0],
						(float) (centerX - Math.cos(angleToRadian(18)) * tickLableRadius),
						(float) (centerY + Math.sin(angleToRadian(18)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 20) {
				canvas.drawText(tickLabelArr[1],
						(float) (centerX - Math.cos(angleToRadian(9)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(9)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 30) {
				canvas.drawText(tickLabelArr[2],
						(float) (centerX - Math.cos(angleToRadian(36)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(36)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 40) {
				canvas.drawText(tickLabelArr[3],
						(float) (centerX - Math.cos(angleToRadian(63)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(63)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 50) {
				canvas.drawText(tickLabelArr[4],
						(float) (centerX - Math.cos(angleToRadian(90)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(90)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 60) {
				canvas.drawText(tickLabelArr[5],
						(float) (centerX + Math.cos(angleToRadian(63)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(63)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 70) {
				canvas.drawText(tickLabelArr[6],
						(float) (centerX + Math.cos(angleToRadian(36)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(36)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 80) {
				canvas.drawText(tickLabelArr[7],
						(float) (centerX + Math.cos(angleToRadian(9)) * tickLableRadius),
						(float) (centerY - Math.sin(angleToRadian(9)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 90) {
				canvas.drawText(tickLabelArr[8],
						(float) (centerX + Math.cos(angleToRadian(18)) * tickLableRadius),
						(float) (centerY + Math.sin(angleToRadian(18)) * tickLableRadius),
						tickLabelPaint);

			} else if (i == 100) {
				canvas.drawText(tickLabelArr[9],
						(float) (centerX + Math.cos(angleToRadian(45)) * tickLableRadius),
						(float) (centerY + Math.sin(angleToRadian(45)) * tickLableRadius),
						tickLabelPaint);
			}
		}
		canvas.restore();
	}
	
	public void setPointerColor(int[] colorSet){
		mColorSet = colorSet;
	}

	/**
	 * 画指针
	 *
	 * @param canvas
	 */
	private void drawPointer(Canvas canvas) {
		canvas.save();

		canvas.rotate((float) (angle * UtilHandler.getInstance().speed2yAxisValue(speed, 10)), centerX, centerY);// 先旋转到当前进度再画指针
		// 指针路径
		Path pointerPath = new Path();
		pointerPath.moveTo((float) (centerX - Math.cos(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)),
				(float) (centerY - Math.sin(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)));
		pointerPath.lineTo((float) (centerX + Math.cos(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)),
				(float) (centerY + Math.sin(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)));
		pointerPath.lineTo((float) (centerX - Math.cos(angleToRadian(45)) * pointerRadius),
				(float) (centerY + Math.sin(angleToRadian(45)) * pointerRadius));
		// pointerPath.close();

		// 表针渐变效果
		LinearGradient linearGradient = new LinearGradient(
				(float) (centerX - Math.cos(angleToRadian(45)) * tickLableRadius),
				(float) (centerY + Math.sin(angleToRadian(45)) * tickLableRadius),
				(float) (centerX + Math.cos(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)),
				(float) (centerY - Math.sin(angleToRadian(45)) * UtilHandler.getInstance().dpTopx(7)),
				mColorSet, null, Shader.TileMode.REPEAT);
		pointerPaint.setShader(linearGradient);
		canvas.drawPath(pointerPath, pointerPaint);

		canvas.restore();
	}

	/**
	 * 画底部文字
	 *
	 * @param canvas
	 */
	private void drawBottomText(Canvas canvas) {
		canvas.save();
		lastSpeed = speed;
		canvas.drawText(UtilHandler.getInstance().toDfSum(speed, "00") + "", centerX, centerY + UtilHandler.getInstance().dpTopx(83), bottomTextPaint);
	}

	/**
	 * 角度转>弧度
	 *
	 * @param value
	 * @return
	 */
	private double angleToRadian(double value) {
		return value * Math.PI / 180;
	}

	/**
	 * 设置渐变颜色
	 * 
	 * 
	 * @return
	 */
	public int getColorChanges(float runNum) {
		float r1, g1, b1, r2, g2, b2;
		r1 = Color.red(tickMarkColor);
		g1 = Color.green(tickMarkColor);
		b1 = Color.blue(tickMarkColor);

		r2 = Color.red(Color.RED);
		g2 = Color.green(Color.RED);
		b2 = Color.blue(Color.RED);
		if (runNum > (mCount / 5)) {
			r1 += ((r2 - r1) / 100) * runNum;
			g1 += ((g2 - g1) / 100) * runNum;
			b1 += ((b2 - b1) / 100) * runNum;
		}
		return Color.rgb((int) r1, (int) g1, (int) b1);
	}
}
