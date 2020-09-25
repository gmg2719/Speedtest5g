package cn.nokia.speedtest5g.view;

import java.util.ArrayList;
import java.util.List;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * 小区切换动画
 * @author zwq
 */
public class CellSwitchView extends View {
	
	private Paint mPaint;
	
	private int mPositionLoading = 0;
	
	private final String[] strLoading = {"小区切换中","小区切换中.","小区切换中..","小区切换中..."};
	
	private Bitmap mBitmapCellSelect,mBitmapCell,mBitmapArrowSelect,mBitmapArrow;
	//文本X坐标，文本Y坐标
	private int xText,yText;
	
	private BeanInfo startBeanInfo,endBeanInfo;
	
	private List<BeanInfo> mArrBean;
	
	public CellSwitchView(Context context) {
		super(context);
		init();
	}
	
	public CellSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp_15));
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(true);
        mPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        
        mBitmapCellSelect  = BitmapFactory.decodeResource(getResources(), R.drawable.icon_jzxh_cell_select);
        mBitmapCell		   = BitmapFactory.decodeResource(getResources(), R.drawable.icon_jzxh_cell_un);
        mBitmapArrowSelect = BitmapFactory.decodeResource(getResources(), R.drawable.icon_jzxh_arrow_select);
        mBitmapArrow	   = BitmapFactory.decodeResource(getResources(), R.drawable.icon_jzxh_arrow);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initArr();
	}
	
	private void initArr() {
		if (mArrBean == null || yText <= 0) {
			int xCenter    = getWidth()/2;
			int heightText = (int) UtilHandler.getInstance().getPaintFontHeight(mPaint);
			xText      = (int) (xCenter - UtilHandler.getInstance().getFontlength(mPaint, strLoading[strLoading.length - 1])/2);
			yText      = getHeight()/2;
			
			startBeanInfo = new BeanInfo(xCenter - mBitmapArrow.getWidth() * 4.6f - mBitmapCell.getWidth(), yText - mBitmapCell.getHeight() - heightText, mBitmapCellSelect);
			endBeanInfo = new BeanInfo(xCenter + mBitmapArrow.getWidth() * 4.6f, yText - mBitmapCell.getHeight() - heightText, mBitmapCell);
			mArrBean = new ArrayList<>();
			mArrBean.add(new BeanInfo(xCenter - mBitmapArrow.getWidth() * 3.6f, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
			mArrBean.add(new BeanInfo(xCenter - mBitmapArrow.getWidth() * 2.4f, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
			mArrBean.add(new BeanInfo(xCenter - mBitmapArrow.getWidth() * 1.2f, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
			mArrBean.add(new BeanInfo(xCenter, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
			mArrBean.add(new BeanInfo(xCenter + mBitmapArrow.getWidth() * 1.2f, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
			mArrBean.add(new BeanInfo(xCenter + mBitmapArrow.getWidth() * 2.2f, yText - mBitmapCell.getHeight()/2 - heightText - mBitmapArrow.getHeight()/2, mBitmapArrow));
		}
	}

	@Override
	public void setVisibility(int visibility) {
		removeCallbacks(mUpdateRunnable);
		super.setVisibility(visibility);
		if (View.VISIBLE == visibility) {
			postDelayed(mUpdateRunnable,500);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mArrBean != null && mArrBean.size() > 0) {
			canvas.drawText(strLoading[mPositionLoading],xText , yText, mPaint);
			canvas.drawBitmap(startBeanInfo.bit, startBeanInfo.x ,startBeanInfo.y, mPaint);
			for (BeanInfo item : mArrBean) {
				canvas.drawBitmap(item.bit, item.x ,item.y, mPaint);
			}
			canvas.drawBitmap(endBeanInfo.bit, endBeanInfo.x ,endBeanInfo.y, mPaint);
		}
	}
	
	private class BeanInfo{
		float x;
		float y;
		Bitmap bit;
		
		public BeanInfo(float x,int y,Bitmap bit){
			this.x = x;
			this.y = y;
			this.bit = bit;
		}
	}
	
	
	private int mPosition = 0;
	
	private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
        	initArr();
            if (mPosition >= mArrBean.size()){
            	mPosition = -1;
            	for (int i = 0; i < mArrBean.size(); i++) {
					mArrBean.get(i).bit = mBitmapArrow;
				}
            }else {
            	mArrBean.get(mPosition).bit = mBitmapArrowSelect;
			}
            if (mPositionLoading + 1 >= strLoading.length) {
            	mPositionLoading = -1;
			}
            mPositionLoading += 1;
            mPosition += 1;
            postDelayed(mUpdateRunnable,500);
            postInvalidate();
        }
    };
}
