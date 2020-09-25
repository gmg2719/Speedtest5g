package cn.nokia.speedtest5g.dialog.downdprogress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
/**
 * 自定义带百分百文字
 */
public class MyTextProgressBar extends ProgressBar {

	 private String text;
	 private Paint mPaint;
	
	public MyTextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
//        mPaint.setTextSize(24f);
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        mPaint.setTextSize(14f * fontScale + 0.5f);
	}
	
	@Override
	public synchronized void setProgress(int progress) {
	    this.text = String.valueOf(progress) + "%";
		super.setProgress(progress);
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
	}
	
	
}
