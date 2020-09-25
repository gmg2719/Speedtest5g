package cn.nokia.speedtest5g.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;

/**
 *  速测曲线
 * @author JQJ
 *
 */
public class SpeedTestCurveView extends View {

	public static final int RECT_SIZE = 10;  

	//枚举实现坐标桌面的样式风格
	public static enum Mstyle {
		Line,Curve
	}

	private Mstyle mstyle = Mstyle.Line;

	private Context mContext;
	int c = 0;
	int resid = 0;

	//数据
	private ArrayList<Double> mList = new ArrayList<Double>();

	//视图距上边缘距离
	private int leftMargin = 10;
	private int rightMargin = 10;

	private int xCount = 19; //x轴暂定19个点
	private float yMinValue = 0;// y轴最小值
	private float yMaxValue = 20;// y轴最大值 默认20   根据速率最大值动态取值
	private float yPerStep = (yMaxValue - yMinValue) / 10;// y轴每格大小（10等分）
	private boolean mIsDownload = false; //当前下载还是上传

	/**
	 * 设置X轴点个数
	 * @param xCount
	 */
	public void setCountForX(int xCount){
		this.xCount = xCount;
	}

	public void clearData(){
		if(mList != null){
			mList.clear();
		}
		invalidate();
	}

	public SpeedTestCurveView(Context context) {
		super(context);
		this.mContext=context;
	}

	public SpeedTestCurveView(Context context, AttributeSet attrs) {
		super( context, attrs );
		this.mContext=context;
	}

	public SpeedTestCurveView(Context context, AttributeSet attrs, int defStyle) {		 
		super( context, attrs, defStyle );
		this.mContext=context;
	}

	/**
	 * 
	 * @param speed 速率
	 * @param isDown
	 */
	public void notifyDataSetChange(double speed, boolean isDown) {
		if(mList != null){
			mIsDownload = isDown;
			speed = UtilHandler.getInstance().speed2yAxisValue(speed, yPerStep);
			mList.add(speed);

			//取最大值处理
			double maxValue = Collections.max(mList);
			if(yMaxValue < maxValue){
				yMaxValue = (float)maxValue + 5;
			}

			invalidate();
		}
	}  

	/**
	 * 加载数据集合
	 * @param speedList
	 * @param isDown
	 */
	public void loadDataSet(List<Double> speedList, boolean isDown){
		if(speedList != null && speedList.size() > 0){
			mIsDownload = isDown;
			setCountForX(speedList.size() - 1);
			for(Double speed : speedList){
				speed = UtilHandler.getInstance().speed2yAxisValue(speed, yPerStep);
				mList.add(speed);
			}

			//取最大值处理
			double maxValue = Collections.max(mList);
			if(yMaxValue < maxValue){
				yMaxValue = (float)maxValue + 5;
			}

			invalidate();
		}
	}

	@SuppressLint("DrawAllocation")
	@Override  
	protected void onDraw(Canvas canvas) {  
		super.onDraw(canvas);  

		if(c != 0){
			this.setbg(c);
		}
		if(resid != 0){
			this.setBackgroundResource(resid);
		}

		//----------------- lyq 背景宽高定义 start ------------------------
		int TotalWidth = getWidth();
		int TotalHeight = getHeight();

		int LeftMargin = dip2px(mContext,leftMargin);
		int RightMargin = dip2px(mContext,rightMargin);
		int bmgWidth =  TotalWidth - LeftMargin - RightMargin;
		int bmgHeight = TotalHeight;

		Paint xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
		xPaint.setColor(getResources().getColor(R.color.gridview_line_color));
		xPaint.setStrokeWidth(1);  
		xPaint.setStyle(Style.STROKE);
		xPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
		//画网格线
		int gridXSize = TotalWidth / dip2px(mContext, 6);
		int gridYSize = TotalHeight / dip2px(mContext, 6);
		for(int i = 0;i <= gridXSize;i ++) {
			canvas.drawLine((TotalWidth/gridXSize)*i,0,(TotalWidth/gridXSize)*i,TotalHeight,xPaint);
		}

		for(int i = 0;i <= gridYSize;i ++) {
			canvas.drawLine(0,(TotalHeight/gridYSize)*i,TotalWidth,(TotalHeight/gridYSize)*i,xPaint);
		}

		//没有数据不绘制
		if(mList == null || mList.size() == 0) {
			return;
		}

		ArrayList<Point> dataList= getPoints(mList,bmgWidth,bmgHeight,LeftMargin);

		//绘制填充的区域 EEF2F5
		Paint paintB = new Paint();  
		int colorStart = Color.parseColor("#C49C3A"); //顶部颜色
		int color1 = Color.parseColor("#8C7C69");
        int colorEnd = Color.parseColor("#2F333B");
		if(mIsDownload){
			colorStart = Color.parseColor("#fae4b7"); //顶部颜色
			color1 = Color.parseColor("#f9eed6");
            colorEnd = Color.parseColor("#f8f6f0");
		}else{
			colorStart = Color.parseColor("#c1e7e0"); //顶部颜色
			color1 = Color.parseColor("#d3ede8");
            colorEnd = Color.parseColor("#f1f8f7");
		}

		paintB.setStyle(Style.FILL);
		LinearGradient backGradient = new LinearGradient(0, 0, 0, bmgHeight, new int[]{colorStart, color1, colorEnd}, 
				null, Shader.TileMode.CLAMP);
		paintB.setShader(backGradient);                       
		Point point = new Point(LeftMargin, bmgHeight);
		Point pointF = new Point(dataList.get(dataList.size() - 1).x, bmgHeight);
		drawBrokenBmg(point,pointF,dataList,canvas,paintB);

		//绘制折线图
		Paint xPaintSinr = new Paint(Paint.ANTI_ALIAS_FLAG);  
		if(mIsDownload){
			xPaintSinr.setColor(getResources().getColor(R.color.download_text_color));
		}else{
			xPaintSinr.setColor(getResources().getColor(R.color.upload_text_color));
		}
		xPaintSinr.setStrokeWidth(3);  
		xPaintSinr.setStyle(Style.STROKE);
		drawBrokenLine(dataList, canvas, xPaintSinr);
	}  

	//画曲线
	private void drawBrokenLine(ArrayList<Point> list,Canvas canvas,Paint paint){
		Point startp = new Point();
		Point endp = new Point();
		for(int i=0; i<list.size()-1; i++){
			startp = list.get(i);
			endp = list.get(i+1);
			int wt = (startp.x + endp.x)/2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;

			Path path = new Path();  
			path.moveTo(startp.x,startp.y); 
			path.cubicTo(p3.x, p3.y, p4.x, p4.y,endp.x, endp.y); 
			canvas.drawPath(path, paint);
		}
	}

	//填充背景
	private void drawBrokenBmg(Point origin,Point finial,ArrayList<Point> ps,Canvas canvas,Paint paint)
	{
		Path path = new Path();  
		path.moveTo(origin.x, origin.y); 
		Point point0 = ps.get(0);
		path.lineTo(origin.x, point0.y);
		for(int i = 0;i < ps.size()-1;i ++)
		{	
			Point startp = ps.get(i);
			Point endp = ps.get(i+1);
			int wt = (startp.x + endp.x)/2;
			Point p3 = new Point();
			Point p4 = new Point();
			p3.y = startp.y;
			p3.x = wt;
			p4.y = endp.y;
			p4.x = wt;
			path.cubicTo(p3.x, p3.y, p4.x, p4.y,endp.x, endp.y);
		}
		Point endp = ps.get(ps.size()-1); //再加一个点
		path.lineTo(endp.x, endp.y); 
		path.lineTo(finial.x,finial.y);  
		canvas.drawPath(path, paint); 
	}

	//max:Y轴最大值,h:绘制背景总高度
	private ArrayList<Point> getPoints(ArrayList<Double> list,int bmgWidth,int bmgHeight,int leftMargin) {
		ArrayList<Point> pointList= new ArrayList<Point>();
		for(int i=0; i<list.size(); i++) {
			Double yValue = list.get(i);
			double bY = yValue/yMaxValue;
			int tempY = bmgHeight -(int)(bmgHeight*bY);
			int tempX = (int)(i*(bmgWidth/xCount));
			Point point = new Point(tempX +leftMargin, tempY);
			pointList.add(point);
		}
		return pointList;
	}

	public  int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public  int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public void setbg(int c) {
		this.setBackgroundColor(c);
	}

	public void setMstyle(Mstyle mstyle) {
		this.mstyle = mstyle;
	}

	public Mstyle getMstyle() {
		return mstyle;
	}

	public void setC(int c) {
		this.c = c;
	}

	public int getC() {
		return c;
	}

	public void setResid(int resid) {
		this.resid = resid;
	}

	public int getResid() {
		return resid;
	}

}  
