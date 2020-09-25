package cn.nokia.speedtest5g.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * 垂直seekbar
 * @author zwqPC
 *
 */
@SuppressLint("AppCompatCustomView")
public class VerticalSeekBar extends SeekBar {

	public VerticalSeekBar(Context context) {
        super(context);
    }
 
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }
	
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }
    
    @Override
    public synchronized void setProgress(int progress) {
    	super.setProgress(progress);
    	onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @SuppressLint("ClickableViewAccessibility") 
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isEnabled())
        {
            return false;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

}
