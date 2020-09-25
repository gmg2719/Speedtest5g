package cn.nokia.speedtest5g.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum.CrurveLineStyle;
import org.xclcharts.renderer.XEnum.DotStyle;
import org.xclcharts.renderer.XEnum.LineStyle;
import org.xclcharts.view.ChartView;

import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 应急站 指标统计折线图
 * 
 * @author xujianjun
 *
 */
public class JJ_LineCharView extends ChartView {
	public AreaChart chart;
	// 数据集合
	private LinkedList<AreaData> mDataset = new LinkedList<AreaData>();
	private List<String> mTime = new ArrayList<String>();
	private double mMin;
	private double mMax;
	private double mStep;
//	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);
//	private Paint mPaintTooltipsSmall = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ListenerBack mListenerBack;
	private int mType ;

	public JJ_LineCharView(Context context, double min, double max, double step,ListenerBack back,int type) {
		super(context);
		this.mMin = min;
		this.mMax = max;
		this.mStep = step;
		this.mListenerBack = back;
		this.mType = type;
		init();
		// TODO Auto-generated constructor stub
	}

	public JJ_LineCharView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	public JJ_LineCharView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	private void init() {
		this.chart = new AreaChart();

		this.chartRender(mMin, mMax, mStep);
		List<Double> list = new ArrayList<>();
		List<String> list1 = new ArrayList<>();
		this.setDataset(list,list1);
	}

	public LinkedList<String> getLabels() {
		LinkedList<String> data = new LinkedList<>();
		for (int i = 0; i < 24; i++) {
			if (i <10) {
				data.add("0"+i);
			} else {
				data.add(i+"");
			}
		}
		return data;
	}

	public void setDataset(List<Double> list,List<String> times) {
		AreaData line = new AreaData("", list, Color.rgb(152, 187, 208), Color.rgb(152, 187, 208));
		List<String> listName = new ArrayList<>();
		listName.add("");
		line.setStrValueList(listName);
		line.setOriginalValueList(list);
		line.setDotStyle(DotStyle.HIDE);
		line.setDotRadius(15);
		line.getLinePaint().setStrokeWidth(3.0F);
		line.setApplayGradient(true);
		this.mDataset.clear();
		this.mTime.clear();
		this.mDataset.add(line);
		this.mTime = times;
		this.invalidate();
	}
	
	/**
	 * @param list
	 * @param times
	 * @param colorLine 线条颜色
	 * @param colorFill 填充颜色
	 */
	public void setDataset(List<Double> list,List<String> times,int colorLine,int colorFill) {
		AreaData line = new AreaData("", list, colorLine, colorFill);
		List<String> listName = new ArrayList<>();
		listName.add("");
		line.setStrValueList(listName);
		line.setOriginalValueList(list);
		line.setDotStyle(DotStyle.HIDE);
		line.setDotRadius(15);
		line.getLinePaint().setStrokeWidth(3.0F);
		line.setApplayGradient(true);
		this.mDataset.clear();
		this.mTime.clear();
		this.mDataset.add(line);
		this.mTime = times;
		this.invalidate();
	}

	private void chartRender(double min, double max, double step) {
		try {

			int[] e = new int[] { DensityUtil.dip2px(this.getContext(), 30.0F),
					DensityUtil.dip2px(this.getContext(), 25.0F), DensityUtil.dip2px(this.getContext(), 10.0F),
					DensityUtil.dip2px(this.getContext(), 25.0F) };
			this.chart.setPadding((float) e[0], (float) e[1], (float) e[2], (float) e[3]);

			// X轴
			this.chart.setCategories(getLabels());
			this.chart.setDataSource(this.mDataset);
			this.chart.setCrurveLineStyle(CrurveLineStyle.BEELINE);

			// 设置绘图区的背景色
			chart.getPlotArea().setBackgroundColor(true, Color.WHITE);

			// 数据轴最大值
			this.chart.getDataAxis().setAxisMax(max);
			this.chart.getDataAxis().setAxisMin(min);
			// 数据间隔
			this.chart.getDataAxis().setAxisSteps(step);
			// 显示横线
			this.chart.getPlotGrid().showHorizontalLines();
			// 隐藏竖线
			this.chart.getPlotGrid().hideVerticalLines();

			this.chart.getPlotGrid().setHorizontalLineStyle(LineStyle.DASH);
			this.chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(0.1F);
			this.chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.rgb(221, 221, 221));
			this.chart.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.0F);
			this.chart.getPlotGrid().getVerticalLinePaint().setAlpha(100);
			this.chart.setAreaAlpha(100);
			this.chart.disablePanMode();
			this.chart.disableScale();
			this.chart.getDataAxis().hideAxisLine();
			this.chart.getDataAxis().hideTickMarks();
			this.chart.getCategoryAxis().hideAxisLine();
			this.chart.getCategoryAxis().hideTickMarks();
			this.chart.ActiveListenItemClick();
			this.chart.showClikedFocus();
			this.chart.extPointClickRange(20);
			this.chart.getClipExt().setExtTop(150.f);
			//提高性能
//			this.chart.enabledHighPrecision();
			
			// this.chart.getPlotLegend().show();//图例

			this.chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {
				public String textFormatter(String value) {
					Double tmp = Double.valueOf(Double.parseDouble(value));
					if (tmp.doubleValue() == 0.0D) {
						return "";
					}
					return JJ_LineCharView.this.getTo(tmp.doubleValue());
				}
			});
		} catch (Exception var2) {
			WybLog.syso("JJ_BarCharView错误：" + var2.getMessage());
		}

	}

	// 绘画图表
	@Override
	public void render(Canvas canvas) {
		try {
			chart.render(canvas);
		} catch (Exception e) {
		}
	}

	// 设置图表大小
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	public String getTo(double data) {
		if(data==((int)data)){
			return (int)data + "";
		}else{
			if(mType==0 ||mType==2 || mType==7 ||mType==9||mType==12 ||mType==13){
				return (int)data + "";
			}else{
				return (UtilHandler.getInstance().toDfSum(data, "00")) + "";
			}
			
		}
	}
	
	@Override
	public boolean performClick() {
		// TODO Auto-generated method stub
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
			performClick();
		}
		return true;
	}

	// 触发监听
	private void triggerClick(float x, float y) {
		PointPosition record = chart.getPositionRecord(x,y);			
		if (null == record){
			chart.showFocusRectF(null);
			chart.getFocusPaint().setAlpha(0);
//			chart.getToolTip().hideBackground();
//			chart.getToolTip().hideBorder();
			invalidate();
			if(mListenerBack!=null){
				mListenerBack.onListener(mType, null, false);
			}
			return;
		}

		AreaData lData = mDataset.get(record.getDataID());
		double lValue = lData.getLinePoint().get(record.getDataChildID());	
		String time = mTime.get(record.getDataChildID());

		float r = record.getRadius();
		chart.showFocusPointF(record.getPosition(),1.5f*Math.abs(r));		
		chart.getFocusPaint().setStyle(Style.FILL);
//		chart.getFocusPaint().setStrokeWidth(3);		
		chart.getFocusPaint().setColor(Color.rgb(249, 150, 32));
		chart.getFocusPaint().setTextAlign(Align.CENTER);
		
		
		//在点击处显示tooltip
//		mPaintTooltips.setColor(Color.WHITE);	
//		mPaintTooltips.setTextSize(40);
//		
//		mPaintTooltipsSmall.setColor(Color.WHITE);	
//		mPaintTooltipsSmall.setTextSize(30);
//		
//		chart.getToolTip().showBackground();
//		chart.getToolTip().showBorder();
//		chart.getToolTip().getBackgroundPaint().setColor(Color.rgb(131, 188, 201));
//		chart.getToolTip().getBackgroundPaint().setAlpha(150);
//		chart.getToolTip().getBorderPaint().setColor(Color.rgb(131, 188, 201));
//		chart.getToolTip().setCurrentXY(100,120);
////		chart.getToolTip().setCurrentXY(record.getPosition().x,record.getPosition().y); 
//		chart.getToolTip().setStyle(DyInfoStyle.RECT);	
//		
//		chart.getToolTip().addToolTip(""+(int)lValue,mPaintTooltips);
//		chart.getToolTip().addToolTip(time,mPaintTooltipsSmall);
//
//		
//		chart.getToolTip().setAlign(Align.RIGHT);
		if(mListenerBack!=null){
			Bundle bundle = new Bundle();
			bundle.putString("time", time);
			bundle.putDouble("value", lValue);
			mListenerBack.onListener(mType, bundle, true);
		}
					
		this.invalidate();
	}

}
