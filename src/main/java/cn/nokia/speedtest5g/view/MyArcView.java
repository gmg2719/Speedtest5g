package cn.nokia.speedtest5g.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 扇形渲染
 * @author zwq
 *
 */
public class MyArcView extends View {
	
	private Paint mPaint;
	private RectF oval;  
	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);  //设置画笔为无锯齿  
		mPaint.setColor(Color.WHITE); //设置画笔颜色  
		mPaint.setAlpha(12);
		mPaint.setStyle(Style.FILL);  
		oval = new RectF(0,0,getHeight(),getWidth());
	}

	public MyArcView(Context context) {
		super(context);
		init();
	}
	
	public MyArcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public MyArcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void initOval(){
		if (oval == null || oval.bottom <= 0) {
			float oldH = (getHeight() - getWidth())/2;
			oval = new RectF(0,oldH,getWidth(),getHeight() - oldH);
		}
	}
	
	private float mAngle = 90;
	public void setAngle(float angle){
		this.mAngle = Math.abs(angle + 90);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initOval();
	    canvas.drawArc(oval, 90, mAngle, true, mPaint);  
	}
}
