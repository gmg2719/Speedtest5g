package cn.nokia.speedtest5g.view;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 文本倾斜显示（水印）
 * @author zwq
 *
 */
public class WatermarkTiltText extends View {
	
	//旋转度数
	private final float mRotate = -30f;
	private Context mContext = null;

	public WatermarkTiltText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public WatermarkTiltText(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	
	private Paint mPaint;
	
	private void init(){
		mPaint = new Paint();
		mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.tv_micro_size));
		mPaint.setColor(mContext.getResources().getColor(R.color.black_micro));
		mPaint.setAlpha(35);
	}
	
	private String mStrText = "";
	
	private float mStrWidth;
	
	public void setText(String text){
		this.mStrText  = text;
		this.mStrWidth = UtilHandler.getInstance().getFontlength(mPaint, text);
		invalidate();
	}
	
	
	private float yL1,yL2,yL3,yR3;
	
	private float xL1,xL2,xL3,xR3;
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (xL1 == 0 || yL1 == 0) {
        	yL1 = getMeasuredHeight()/4f;
            yL2 = getMeasuredHeight()/2f;
            yL3 = getMeasuredHeight()/2f + (getMeasuredHeight()/2f - getMeasuredHeight()/4f);
            yR3 = yL3 + yL1;
            xL1 = (float) (yL1 * Math.sin(mRotate * Math.PI / 180f));
            xL2 = (float) (yL2 * Math.sin(mRotate * Math.PI / 180f));
            xL3 = (float) (yL3 * Math.sin(mRotate * Math.PI / 180f));
            xR3 = (float) (yR3 * Math.sin(mRotate * Math.PI / 180f));
            xL1 -= (xL2 - xL1)/8f;
            xL3 += (xL3 - xL2)/8f;
            xR3 += (xR3 - xL2)/8f;
		}
        //将该文字图片逆时针方向倾斜30度
        canvas.rotate(mRotate);
        canvas.drawText(mStrText, xL1, yL1, mPaint);//左上
//        canvas.drawText(mStrText, xL2, yL2, mPaint);//左中
//        canvas.drawText(mStrText, xL3, yL3, mPaint);//左下
//        
//        canvas.drawText(mStrText, xL2 + getMeasuredWidth() - mStrWidth/3f*2f, yL2 - mStrWidth/7f, mPaint);//右上
//        canvas.drawText(mStrText, xL3 + getMeasuredWidth() - mStrWidth/3f*2f, yL3 - mStrWidth/7f, mPaint);//右中
        canvas.drawText(mStrText, xR3 + getMeasuredWidth() - mStrWidth/3f*2f, yR3 - mStrWidth/7f, mPaint);//右下
        canvas.save();
    }
}
