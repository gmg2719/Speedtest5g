package cn.nokia.speedtest5g.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PlotPointPosition;
import org.xclcharts.event.click.PositionRecord;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * wifi历史强度曲线图
 * @author 
 */
public class WifiHistoryChartView  extends ChartView {
	
	public SplineChart chartWifi = new SplineChart(SpeedTest5g.getContext());
	//分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private SplineData mSplineDataWifi;

	// splinechart支持横向和竖向定制线
	private List<CustomLineData> mXCustomLineDataset = new ArrayList<CustomLineData>();

	//X轴最大值--动态
	public double mMaxX = 60;
	//X轴最大值--静态
	private final int MAX_X_FINAL = 30 * 60;
	
	private List<PointD> mListTemPointWifi = new ArrayList<>();
	
	public ListenerBack mListenerBack = null;
	//当前选中的Wifi游标,当前选中的SINR游标
	private int mWifiPositionSelect = -1;
	//是否重定向Wifi游标
	private boolean isToWifi;

	public WifiHistoryChartView(Context context) {
		super(context);
		initView();
	}

	public WifiHistoryChartView(Context context, AttributeSet attrs){
	    super(context, attrs);
	    initView();
	}

	public WifiHistoryChartView(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		 initView();
	}
	
	/**
	 * 添加指标值 
	 * @param tag 标签值--如时间
	 */
	public int[] addValue(Integer dbm,String tag){
		int[] arrCode = {Integer.MAX_VALUE,Integer.MAX_VALUE};
		PointD pointD;
		if (mSplineDataWifi != null && dbm != Integer.MAX_VALUE) {
			pointD = new PointD(mSplineDataWifi.getLineDataSet().size(), dbm,tag,0);
			arrCode[0] = pointD.hashCode();
			addWifiItem(pointD);
		}
		invalidate();
		if (isToWifi) {
			setLastSelectData(true);
			isToWifi = false;
		}
		setSelectWifi();
		invalidate();
		return arrCode;
	 }
	
	/**
	 * 更新切换图标
	 */
	public void updateValue(int hashCode,int switchTypeResources,boolean isWifi){
		if (hashCode != Integer.MAX_VALUE) {
			if (isWifi) {
				if (mSplineDataWifi != null && mSplineDataWifi.getLineDataSet() != null && mSplineDataWifi.getLineDataSet().size() >= 0) {
					for (int i = mSplineDataWifi.getLineDataSet().size() - 1; i >= 0; i--) {
						if (hashCode == mSplineDataWifi.getLineDataSet().get(i).hashCode()) {
							int mLastSwitchResourcesWifi = mSplineDataWifi.getLineDataSet().get(i).switchTypeResources;
							if (mLastSwitchResourcesWifi != 0) {
								mSplineDataWifi.getLineDataSet().get(i).switchTypeResources = switchTypeResources;
								invalidate();
								return;
							}
						}
					}
				}
			}
		}
	}
	
	//设置上一个点游标
	private void setLastSelectData(boolean isWifi){
		if (isWifi) {
			if (mRecordWifi != null && mWifiPositionSelect - 1 >= 0) {
				//Wifi选中值
				ArrayList<PositionRecord> arrPosition = chartWifi.getPositionRecordset();
				if (arrPosition != null && arrPosition.size() > 0 && arrPosition.size() > mWifiPositionSelect){
					mWifiPositionSelect -= 1;
					mRecordWifi = (PlotPointPosition) arrPosition.get(mWifiPositionSelect);
					//这里取上一次点坐标（问题点，目前先这样处理）
					mRecordWifi.getPosition().y = ((PlotPointPosition) arrPosition.get(mWifiPositionSelect + 1)).getPosition().y;
					mLastToX = mRecordWifi.getPosition().x;
					mLastToY = mRecordWifi.getPosition().y;
					chartWifi.getDyLine().setCurrentXY(mLastToX,mLastToY);
				}
			}else if (mRecordWifi != null) {
				mRecordWifi = null;
				chartWifi.showFocusPointF(null, 0);
				chartWifi.getDyLine().setCurrentXY(-1,-1);
				onBackListener(EnumRequest.OTHER_CHART_Wifi_RETURN.toInt(), null);
			}
		}
	}
	
	public void onDestroy(){
		if (chartWifi != null) {
			chartWifi.clearBitmap();
		}
	}
	
	/**
	 * 清除数据
	 * @param removeWifi
	 * @param removeSinr
	 */
	public void clearData(boolean removeWifi,boolean removeSinr){
		if (removeWifi) {
			mListTemPointWifi.clear();
			mSplineDataWifi.getLineDataSet().clear();
		}
		clearSelectData();
		invalidate();
	}
	
	private PointD mPointDtem;
	private void addWifiItem(PointD pointD){
		if (mListTemPointWifi.size() >= MAX_X_FINAL) {
			mListTemPointWifi.remove(0);
		}
		mListTemPointWifi.add(pointD);
		if (mSplineDataWifi.getLineDataSet().size() >= mMaxX) {
			isToWifi = true;
			mSplineDataWifi.getLineDataSet().clear();
			for (int i = mListTemPointWifi.size() - (int)mMaxX ; i < mListTemPointWifi.size(); i++) {
				mPointDtem = mListTemPointWifi.get(i);
				mPointDtem.x = mSplineDataWifi.getLineDataSet().size();
				mSplineDataWifi.getLineDataSet().add(mPointDtem);
			}
		}else {
			mSplineDataWifi.getLineDataSet().add(pointD);
		}
	}
	
	/**
	 * 设置标签X轴
	 * @param type 1：1分钟
	 * 			   5：5分钟
	 * 			   10：10分钟
	 * 			   30：30分钟
	 */
	public void setLab(int type){
		if (mMaxX == type * 60) {
			return;
		}
		labels.clear();
		mMaxX = type * 60;
		switch (type) {
		case 1:
			labels.add("0s");
			labels.add("10s");
			labels.add("20s");
			labels.add("30s");
			labels.add("40s");
			labels.add("50s");
			labels.add("60s");
			break;
		case 3:
			labels.add("0s");
			labels.add("30s");
			labels.add("60s");
			labels.add("90s");
			labels.add("120s");
			labels.add("150s");
			labels.add("180s");
			break;
		case 5:
			labels.add("0min");
			labels.add("1min");
			labels.add("2min");
			labels.add("3min");
			labels.add("4min");
			labels.add("5min");
			break;
		case 10:
			labels.add("0min");
			labels.add("2min");
			labels.add("4min");
			labels.add("6min");
			labels.add("8min");
			labels.add("10min");
			break;
		case 20:
			labels.add("0min");
			labels.add("4min");
			labels.add("8min");
			labels.add("12min");
			labels.add("16min");
			labels.add("20min");
			break;
		case 30:
			labels.add("0min");
			labels.add("6min");
			labels.add("12min");
			labels.add("18min");
			labels.add("24min");
			labels.add("30min");
			break;
		default:
			break;
		}
		chartWifi.setCategoryAxisMax(mMaxX);
		if (mSplineDataWifi.getLineDataSet().size() >= mMaxX) {
			mSplineDataWifi.getLineDataSet().clear();
			for (int i = mListTemPointWifi.size() - (int)mMaxX ; i < mListTemPointWifi.size(); i++) {
				mPointDtem = mListTemPointWifi.get(i);
				mPointDtem.x = mSplineDataWifi.getLineDataSet().size();
				mSplineDataWifi.getLineDataSet().add(mPointDtem);
			}
		}else if (mSplineDataWifi.getLineDataSet().size() < mListTemPointWifi.size()) {
			mSplineDataWifi.getLineDataSet().clear();
			for (int i = (mListTemPointWifi.size() < mMaxX ? 0 : mListTemPointWifi.size() - (int)mMaxX) ; i < mListTemPointWifi.size(); i++) {
				mPointDtem = mListTemPointWifi.get(i);
				mPointDtem.x = mSplineDataWifi.getLineDataSet().size();
				mSplineDataWifi.getLineDataSet().add(mPointDtem);
			}
		}
		clearSelectData();
		invalidate();
	}
	
	//清除选中数据
	private void clearSelectData(){
		if (mRecordWifi != null) {
			mRecordWifi = null;
			chartWifi.getDyLine().setCurrentXY(-1,-1);
			onBackListener(EnumRequest.OTHER_CHART_Wifi_RETURN.toInt(), null);
		}
	}

	 private void initView(){
			chartLabels();
			chartDataSet();
			if (mSplineDataWifi != null) {
				setChartInit(chartWifi,true,mSplineDataWifi,XEnum.AxisLocation.LEFT,Color.parseColor("#0097E8"),-20,-120,25);
			}
			//綁定手势滑动事件
			this.bindTouch(this,chartWifi);
	 }


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    //图所占范围大小
	    chartWifi.setChartRange(w,h);
	}

	/**
	 * @param chart
	 * @param isClick 是否可点击
	 * @param alignLabY Y轴标签显示位置
	 * @param colorLabY Y轴标签颜色
	 * @param max 最大值Y
	 * @param min 最小值X
	 */
	private void setChartInit(SplineChart chart,boolean isClick,SplineData splineData,XEnum.AxisLocation alignLabY,int colorLabY,int max,int min,int steps){
		try {
			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int dp25 = DensityUtil.dip2px(getContext(), 25.0F);
			chart.setPadding(dp25 * 1.2f, dp25, dp25, dp25/2);
			//数据源
			chart.setCategories(labels);
			List<SplineData> listData = new ArrayList<>();
			listData.add(splineData);
			chart.setDataSource(listData);
			//坐标系Y
			//数据轴最大值
			chart.getDataAxis().setAxisMax(max);
			chart.getDataAxis().setAxisMin(min);
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(steps);
			//Y轴标签字体颜色
			chart.getDataAxis().getTickLabelPaint().setColor(colorLabY);
			//Y标签显示位置
			if (alignLabY == XEnum.AxisLocation.RIGHT) {
				chart.getDataAxis().setHorizontalTickAlign(Align.RIGHT);
				chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.LEFT);
			}
			chart.setDataAxisLocation(alignLabY);
			//隐藏Y轴标签
			chart.getDataAxis().hide();
			
			//标签轴最大值X
			chart.setCategoryAxisMax(mMaxX);
			//标签轴最小值
			chart.setCategoryAxisMin(0);
			chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴

			if (isClick) {
				chart.getPlotGrid().showHorizontalLines();
				//绘制十字交叉线
//				chart.showDyLine();
				chart.getDyLine().getLinePaint().setColor(Color.parseColor("#777777"));
				chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
				//为了让触发更灵敏，可以扩大5px的点击监听范围
				chart.extPointClickRange(5);
//				chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
//				chart.getCategoryAxis().showAxisLabels();
			}else {
				chart.DeactiveListenItemClick();
			}
			chart.getCategoryAxis().hideAxisLabels();
			chart.getPlotGrid().hideHorizontalLines();
			chart.hideDyLine();
			//激活点击监听
			chart.ActiveListenItemClick();
			chart.showClikedFocus();
			chart.setBackgroundColor(Color.parseColor("#00000000"));
			chart.getPlotGrid().hideVerticalLines();

			chart.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.5F);

			chart.getPlotGrid().getVerticalLinePaint().setAlpha(100);

			chart.disablePanMode();

			chart.disableScale();

			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();
			chart.getCategoryAxis().hideAxisLine();
			chart.getCategoryAxis().hideTickMarks();
			//不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();
			//不显示线条类型
			chart.getPlotLegend().hide();
			//隐藏边框
			chart.hideBorder();

			//定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
			//请自行分析定制
			chart.setDotLabelFormatter(new IFormatterTextCallBack(){

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
//					String label = "["+value+"]";
					return ("");
				}

			});
			//显示平滑曲线
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);
		} catch (Exception e) {
			
		}
	}
	
	private void chartDataSet(){
		//线1的数据集
		mSplineDataWifi = new SplineData("Wifi",new ArrayList<PointD>(),Color.parseColor("#0097E8"));
		//把线弄细点
		mSplineDataWifi.getLinePaint().setStrokeWidth(3);
		mSplineDataWifi.setDotStyle(XEnum.DotStyle.DOT);
		mSplineDataWifi.getDotPaint().setColor(Color.parseColor("#00000000"));
	}

	private void chartLabels(){
		labels.add("0s");
		labels.add("10s");
		labels.add("20s");
		labels.add("30s");
		labels.add("40s");
		labels.add("50s");
		labels.add("60s");
	}

	@Override
	public void render(Canvas canvas) {
	    try{
	        chartWifi.render(canvas);
	    } catch (Exception e){
	    	
	    }
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			triggerClick(event.getX(),event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}

	private float mLastToX,mLastToY;
	private PlotPointPosition mRecordWifi;
	//触发监听
	private void triggerClick(double x,double y){
		float toX = -1;
		float toY = -1;
        if(!chartWifi.getListenItemClickStatus()) return;
		//获取当前绘制线的坐标
		double mOneWidth = chartWifi.getWidth()/mMaxX;
		//Wifi选中值
		ArrayList<PositionRecord> arrPosition = chartWifi.getPositionRecordset();
		if (arrPosition != null && arrPosition.size() > 0){
			chartWifi.showFocusPointF(null, 0);
			for (int i = 0; i < arrPosition.size(); i++) {
				mRecordWifi = (PlotPointPosition) arrPosition.get(i);
				if (mRecordWifi.getPosition().x >= x - mOneWidth/2 && mRecordWifi.getPosition().x <= x + mOneWidth/2) {
					if (mLastToX != mRecordWifi.getPosition().x) {
						mWifiPositionSelect = i;
						toX = mRecordWifi.getPosition().x;
						toY = mRecordWifi.getPosition().y;
						setSelectWifi();
						PointD pointD = chartWifi.getDataSource().get(mRecordWifi.getDataID()).getLineDataSet().get(mRecordWifi.getDataChildID());
						onBackListener(EnumRequest.OTHER_CHART_Wifi_RETURN.toInt(), pointD);
					}
					break;
				}
			}
		}
		//交叉线
		if(chartWifi.getDyLineVisible()){
			if (toX == -1 && toY == -1) {
				mRecordWifi = null;
				onBackListener(EnumRequest.OTHER_CHART_Wifi_RETURN.toInt(), null);
			}
			mLastToX = toX;
			mLastToY = toY;
			chartWifi.getDyLine().setCurrentXY(toX,toY);
		}
		this.invalidate();
	}
	
	/**
	 * 设置上一个或下一个切换游标
	 * @param hashCodeWifi
	 * @param hastCodeSinr
	 */
	public void setNextOrLastClick(int hashCodeWifi,int hastCodeSinr){
		//过滤Wifi线条
		if (chartWifi != null && hashCodeWifi != 0) {
			ArrayList<PositionRecord> arrPosition = chartWifi.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				for (int i = 0; i < arrPosition.size(); i++) {
					PlotPointPosition mRecordWifi1 = (PlotPointPosition) arrPosition.get(i);
					if (chartWifi.getDataSource().get(mRecordWifi1.getDataID()).getLineDataSet().get(mRecordWifi1.getDataChildID()).hashCode() == hashCodeWifi) {
						triggerClick(mRecordWifi1.getPosition().x,mRecordWifi1.getPosition().y);
						return;
					}
				}
			}
		}
	}
	
	//设置Wifi选中点
	private void setSelectWifi(){
		if (mRecordWifi != null) {
			chartWifi.showFocusPointF(mRecordWifi.getPosition(),mSplineDataWifi.getLinePaint().getStrokeWidth() * 3,true);
			chartWifi.getFocusPaint().setStyle(Paint.Style.FILL);
			chartWifi.getFocusPaint().setStrokeWidth(2);
			chartWifi.getFocusPaint().setColor(mSplineDataWifi.getLinePaint().getColor());
		}
	}
	
	/**
	 * 设置回调
	 * @param type EnumRequest.OTHER_CHART_Wifi_RETURN.toInt() 
	 * @param pointD
	 */
	private void onBackListener(int type,PointD pointD){
		if (mListenerBack != null) {
			if (pointD != null) {
				mListenerBack.onListener(type, pointD, true);
			}else {
				mListenerBack.onListener(type, null, false);
			}
		}
	}
}
