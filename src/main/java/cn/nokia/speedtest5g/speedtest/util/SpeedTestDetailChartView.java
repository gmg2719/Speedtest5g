package cn.nokia.speedtest5g.speedtest.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PlotPointPosition;
import org.xclcharts.event.click.PositionRecord;
import org.xclcharts.renderer.LnChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.CrurveLineStyle;
import org.xclcharts.renderer.XEnum.DotStyle;
import org.xclcharts.view.ChartView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;

/**
 * 速率测试混合曲线图
 *
 * @author jinhaizheng
 */
public class SpeedTestDetailChartView extends ChartView {
	private double yMin = 0;// y轴最大值
	private double yMax = 100;// y轴最小值
	private double yPerStep = (yMax - yMin) / 10;// y轴每格大小（10等分）

	private String[] speedStrY = getResources().getStringArray(R.array.arrSpeedTestTickLabel);

	private SplineChart rsrpSplinenChart = new SplineChart(SpeedTest5g.getContext());
	private SplineChart rsrpNrSplinenChart = new SplineChart(SpeedTest5g.getContext());
	private SplineChart sinrSplineChart = new SplineChart(SpeedTest5g.getContext());
	private SplineChart sinrNrSplineChart = new SplineChart(SpeedTest5g.getContext());
	private AreaChart areaChart = new AreaChart();

	private SplineData rsrpSplineData;
	private SplineData rsrpNrSplineData;
	private SplineData sinrSplineData;
	private SplineData sinrNrSplineData;

	private AreaData downloadAreaData;
	private AreaData uploadAreaData;
	private LinkedList<AreaData> areaList = new LinkedList<AreaData>();

	private ListenerBack mListenerBack;

	private int mClickPostion = -1;

	private boolean isDownload;
	private int xCount = 10; //X轴个数  默认10

	public SpeedTestDetailChartView(Context context, int count, ListenerBack back) {
		super(context);
		this.xCount = count;
		this.mListenerBack = back;
		initChart();
	}

	public SpeedTestDetailChartView(Context context, int count) {
		super(context);
		this.xCount = count;
		initChart();
	}

	private void initChart() {
		// 初始化数据
		initData();

		// 数据绑定
		chartRenderArea(areaChart, areaList);
		chartRenderLine(rsrpSplinenChart, rsrpSplineData, TypeKey.getInstance().RSRP_MIN, TypeKey.getInstance().RSRP_MAX, 10, R.id.ll_speed_test_rsrp,true);
		chartRenderLine(rsrpNrSplinenChart, rsrpNrSplineData, TypeKey.getInstance().RSRP_MIN, TypeKey.getInstance().RSRP_MAX, 10, R.id.ll_speed_test_rsrp,false);
		chartRenderLine(sinrSplineChart, sinrSplineData, TypeKey.getInstance().SINR_MIN, TypeKey.getInstance().SINR_MAX, 10, R.id.ll_speed_test_sinr,true);
		chartRenderLine(sinrNrSplineChart, sinrNrSplineData, TypeKey.getInstance().SINR_MIN, TypeKey.getInstance().SINR_MAX, 10, R.id.ll_speed_test_sinr,false);
	}

	private void initData() {
		int transparentColor = getResources().getColor(R.color.transparent);
		rsrpSplineData = new SplineData("RSRP", new ArrayList<PointD>(), getResources().getColor(R.color.transparent));
		rsrpSplineData.getLinePaint().setStrokeWidth(3);
		rsrpSplineData.setDotStyle(DotStyle.DOT);
		rsrpSplineData.getDotPaint().setColor(transparentColor);

		rsrpNrSplineData = new SplineData("RSRP_NR", new ArrayList<PointD>(), getResources().getColor(R.color.transparent));
		rsrpNrSplineData.getLinePaint().setStrokeWidth(3);
		rsrpNrSplineData.setDotStyle(DotStyle.DOT);
		rsrpNrSplineData.getDotPaint().setColor(transparentColor);
		rsrpNrSplineData.setLineStyle(XEnum.LineStyle.DASH);

		sinrSplineData = new SplineData("SINR", new ArrayList<PointD>(), getResources().getColor(R.color.transparent));
		sinrSplineData.getLinePaint().setStrokeWidth(3);
		sinrSplineData.setDotStyle(DotStyle.DOT);
		sinrSplineData.getDotPaint().setColor(transparentColor);

		sinrNrSplineData = new SplineData("SINR_NR", new ArrayList<PointD>(), getResources().getColor(R.color.transparent));
		sinrNrSplineData.getLinePaint().setStrokeWidth(3);
		sinrNrSplineData.setDotStyle(DotStyle.DOT);
		sinrNrSplineData.getDotPaint().setColor(transparentColor);
		sinrNrSplineData.setLineStyle(XEnum.LineStyle.DASH);

		int downAreaColor = getResources().getColor(R.color.download_text_color);
		downloadAreaData = new AreaData("下载", new ArrayList<Double>(), downAreaColor, downAreaColor);
		downloadAreaData.setDotStyle(DotStyle.RING);
		downloadAreaData.getLinePaint().setStrokeWidth(2.0F);
		downloadAreaData.getDotPaint().setColor(downAreaColor);
		downloadAreaData.setApplayGradient(true);
		// downloadAreaData.setAreaFillColor(downAreaColor);

		int uploadAreaColor = getResources().getColor(R.color.upload_text_color);
		uploadAreaData = new AreaData("上传", new ArrayList<Double>(), uploadAreaColor, uploadAreaColor);
		uploadAreaData.setDotStyle(DotStyle.RING);
		uploadAreaData.getLinePaint().setStrokeWidth(2.0F);
		uploadAreaData.getDotPaint().setColor(uploadAreaColor);
		uploadAreaData.setApplayGradient(true);
		// uploadAreaData.setAreaFillColor(uploadAreaColor);
	}

	private void chartRenderLine(SplineChart chart, SplineData data, double min, double max, double step, final int type,boolean isXyLabel) {
		try {
			setChartPadding(chart);
			// 数据源
			chart.setCategories(getLabels());
			List<SplineData> listData = new ArrayList<>();
			listData.add(data);
			chart.setDataSource(listData);
			// 坐标系Y
			// 数据轴最大值
			chart.getDataAxis().setAxisMax(max);
			chart.getDataAxis().setAxisMin(min);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(step);
			// Y轴标签字体颜色
			if (isXyLabel) {
				if (type == R.id.ll_speed_test_sinr) {
					chart.getDataAxis().getTickLabelPaint().setColor(getResources().getColor(R.color.transparent));
					chart.getDataAxis().show();
				} else if (type == R.id.ll_speed_test_rsrp) {
					chart.getDataAxis().getTickLabelPaint().setColor(getResources().getColor(R.color.transparent));
					chart.getDataAxis().hide();
				}
			}else {
				chart.getDataAxis().hide();
			}
			// Y标签显示位置
			chart.getDataAxis().setHorizontalTickAlign(Align.RIGHT);
			chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.RIGHT);// 刻度字体右对齐
			chart.setDataAxisLocation(XEnum.AxisLocation.RIGHT);
			chart.getDataAxis().setTickLabelMargin(DensityUtil.dip2px(this.getContext(), 15f));
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {
				@Override
				public String textFormatter(String value) {
					double doubleValue = Double.valueOf(value).doubleValue();
					if ((type == R.id.ll_speed_test_sinr && doubleValue == Double.valueOf(TypeKey.getInstance().SINR_MIN).doubleValue()) ||
							(type == R.id.ll_speed_test_rsrp && doubleValue == Double.valueOf(TypeKey.getInstance().RSRP_MIN).doubleValue())) {
						value = "";
					} else {
						value = String.valueOf((long) doubleValue);
					}
					return value;
				}
			});

			// 标签轴最大值X
			chart.setCategoryAxisMax(xCount-1);
			// 标签轴最小值
			chart.setCategoryAxisMin(0);
			// chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴

			chart.getPlotGrid().hideHorizontalLines();
			// 绘制十字交叉线
			chart.showDyLine();
			chart.getDyLine().getLinePaint().setColor(Color.parseColor("#777777"));
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			// 为了让触发更灵敏，可以扩大5px的点击监听范围
			chart.extPointClickRange(5);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
			if (isXyLabel) {
				chart.getCategoryAxis().showAxisLabels();
			}else {
				chart.getCategoryAxis().hideAxisLabels();
			}
			// 激活点击监听
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

			// 不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();
			// 不显示线条类型
			chart.getPlotLegend().hide();
			// 隐藏边框
			chart.hideBorder();

			// 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为: x值,y值
			// 请自行分析定制
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					String label = "[" + value + "]";
					return (label);
				}

			});
			// 设置不显示平滑曲线，否则虚线无法实现
			chart.setCrurveLineStyle(CrurveLineStyle.BEELINE);
		} catch (Exception var2) {
			WybLog.syso("JJ_BarCharView错误：" + var2.getMessage());
		}
	}

	private void chartRenderArea(AreaChart chart, LinkedList<AreaData> list) {
		try {
			setChartPadding(chart);

			int lineColor = Color.parseColor("#f0f1f0");
			int tvColor = getResources().getColor(R.color.black_micro);

			chart.setCategories(addCategories());
			chart.setDataSource(list);
			chart.setCrurveLineStyle(CrurveLineStyle.BEZIERCURVE);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.SOLID);
            chart.getPlotGrid().setVerticalLineStyle(XEnum.LineStyle.SOLID);
			chart.getDataAxis().setAxisMax(yMax);
			chart.getDataAxis().setAxisMin(yMin);
			chart.getDataAxis().setAxisSteps(yPerStep);
			chart.getPlotGrid().showHorizontalLines();
			chart.getPlotGrid().showVerticalLines();

			chart.getPlotGrid().getHorizontalLinePaint().setColor(lineColor);
            chart.getPlotGrid().getVerticalLinePaint().setColor(lineColor);
			chart.getDataAxis().getTickLabelPaint().setColor(tvColor);
			chart.getCategoryAxis().getTickLabelPaint().setColor(tvColor);

			chart.setAreaAlpha(100);
			chart.disablePanMode();
			chart.disableScale();
			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();
			chart.getCategoryAxis().hideAxisLine();
			chart.getCategoryAxis().hideTickMarks();
			chart.ActiveListenItemClick();
			chart.extPointClickRange(5);
			chart.showClikedFocus();
			chart.getPlotLegend().hide();
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {
				public String textFormatter(String value) {
					double tmp = Double.parseDouble(value);
					return tmp == 0.0D ? "" : getSpeedValue(tmp);
				}
			});
			// 隐藏边框
			chart.hideBorder();
			// 不使用精确计算，忽略Java计算误差,提高性能
			// chartArea.disableHighPrecision();
			chart.enabledHighPrecision();

		} catch (Exception var2) {
			WybLog.syso("view错误：" + var2.getMessage());
		}

	}

	private void setChartPadding(LnChart chart) {
		int padding = DensityUtil.dip2px(this.getContext(), 25F);
		chart.setPadding(padding, padding, padding, padding);

	}

	private String getSpeedValue(double value) {
		int i = (int) ((value - areaChart.getDataAxis().getAxisSteps()) / areaChart.getDataAxis().getAxisSteps());
		return speedStrY[i];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LinkedList<String> addCategories() {
		LinkedList mLabels = new LinkedList(); 
		for (int i = 0; i < xCount; ++i) {
			//		mLabels.add(i + 1 + "S"); 
			mLabels.add(""); 
		}
		return mLabels;
	}

	public LinkedList<String> getLabels() {
		LinkedList<String> data = new LinkedList<>();
		for (int i = 0; i < xCount; i++) {
			data.add("");
		}
		return data;
	}

	/**
	 * 添加指标值
	 */
	public synchronized void addValue(Signal signal, double speed, boolean isDown) {
		isDownload = isDown;
		if (isDown) {
			if (areaList.size() == 0) {
				areaList.add(downloadAreaData);
				if (mListenerBack != null) {
					mListenerBack.onListener(EnumRequest.OTHER_SPEED_SWITCH.toInt(), null, true);
				}
			}
		} else {
			if (areaList.size() == 0) {
				areaList.add(uploadAreaData);
				if (mListenerBack != null) {
					mListenerBack.onListener(EnumRequest.OTHER_SPEED_SWITCH.toInt(), null, false);
				}
			} else {
				if (areaList.get(0).getLineKey().equals("下载")) {
					clearSignalData();
					areaList.clear();
					areaList.add(uploadAreaData);
					if (mListenerBack != null) {
						mListenerBack.onListener(EnumRequest.OTHER_SPEED_SWITCH.toInt(), null, false);
					}
				}

			}
		}
		if (signal != null) {
			if (signal.getTypeNet().equals("NR") || signal.getTypeNet().equals("LTE")) {
				if (mIsRsrp) {
					rsrpSplineData.getLineDataSet().add(new PointD(rsrpSplineData.getLineDataSet().size(),
							UtilHandler.getInstance().toInt(signal.getLte_rsrp(), TypeKey.getInstance().RSRP_MIN)));
				}
				if (mIsSinr) {
					sinrSplineData.getLineDataSet().add(new PointD(sinrSplineData.getLineDataSet().size(),
							UtilHandler.getInstance().toInt(signal.getLte_sinr(), TypeKey.getInstance().SINR_MIN)));
				}
				if (signal.getTypeNet().equals("NR")) {
					if (mIsRsrpNr) {
						rsrpNrSplineData.getLineDataSet().add(new PointD(rsrpNrSplineData.getLineDataSet().size(),
								UtilHandler.getInstance().toInt(signal.getLte_rsrp_nr(), TypeKey.getInstance().RSRP_MIN)));						
					}
					if (mIsSinrNr) {
						sinrNrSplineData.getLineDataSet().add(new PointD(sinrNrSplineData.getLineDataSet().size(),
								UtilHandler.getInstance().toInt(signal.getLte_sinr_nr(), TypeKey.getInstance().SINR_MIN)));
					}

				}
			} else {
				if (mIsRsrp) {
					rsrpSplineData.getLineDataSet().add(new PointD(rsrpSplineData.getLineDataSet().size(), TypeKey.getInstance().RSRP_MIN + 10));
				}
				if (mIsSinr) {
					sinrSplineData.getLineDataSet().add(new PointD(sinrSplineData.getLineDataSet().size(), TypeKey.getInstance().SINR_MIN));
				}
			}
		} else {
			if (mIsRsrp) {
				rsrpSplineData.getLineDataSet().add(new PointD(rsrpSplineData.getLineDataSet().size(), TypeKey.getInstance().RSRP_MIN + 10));
			}
			if (mIsSinr) {
				sinrSplineData.getLineDataSet().add(new PointD(sinrSplineData.getLineDataSet().size(), TypeKey.getInstance().SINR_MIN));
			}
		}

		if (isDown) {
			downloadAreaData.getLinePoint().add(downloadAreaData.getLinePoint().size(), UtilHandler.getInstance().speed2yAxisValue(speed, yPerStep));
		} else {
			uploadAreaData.getLinePoint().add(uploadAreaData.getLinePoint().size(), UtilHandler.getInstance().speed2yAxisValue(speed, yPerStep));
		}
		invalidate();
	}

	// 设置是否是下载
	public void setTestDownOrUp() {
		if (rsrpSplineData != null && rsrpSplineData.getLineDataSet() != null && rsrpSplineData.getLineDataSet().size() > 0) {
			rsrpSplineData.getLineDataSet().clear();
		}
		if (rsrpNrSplineData != null && rsrpNrSplineData.getLineDataSet() != null && rsrpNrSplineData.getLineDataSet().size() > 0) {
			rsrpNrSplineData.getLineDataSet().clear();
		}
		if (sinrSplineData != null && sinrSplineData.getLineDataSet() != null && sinrSplineData.getLineDataSet().size() > 0) {
			sinrSplineData.getLineDataSet().clear();
		}
		if (sinrNrSplineData != null && sinrNrSplineData.getLineDataSet() != null && sinrNrSplineData.getLineDataSet().size() > 0) {
			sinrNrSplineData.getLineDataSet().clear();
		}

		areaList.clear();
		if (isDownload) {
			areaList.add(downloadAreaData);
		} else {
			areaList.add(uploadAreaData);
		}
		invalidate();
	}

	private List<Signal> mTemSignalList;
	public void setTestedDownOrUpData(List<Signal> signalList, boolean isDown) {
		this.mTemSignalList = signalList;
		isDownload = isDown;
		areaList.clear();
		rsrpSplinenChart.hideDyLine();
		if (isDownload) {
			areaList.add(downloadAreaData);
		} else {
			areaList.add(uploadAreaData);
		}
		mClickPostion = -1;

		if (signalList != null) {
			List<PointD> rsrpLineDataSet = rsrpSplineData.getLineDataSet();
			List<PointD> sinrLineDataSet = sinrSplineData.getLineDataSet();
			List<PointD> rsrpNrLineDataSet = rsrpNrSplineData.getLineDataSet();
			List<PointD> sinrNrLineDataSet = sinrNrSplineData.getLineDataSet();
			rsrpLineDataSet.clear();
			sinrLineDataSet.clear();
			rsrpNrLineDataSet.clear();
			sinrNrLineDataSet.clear();
			for (int i = 0; i < signalList.size(); i++) {
				Signal signal = signalList.get(i);
				if (signal != null && signal.getTypeNet() != null && (signal.getTypeNet().equals("NR") || signal.getTypeNet().equals("LTE"))) {
					if (mIsRsrp) {
						rsrpLineDataSet.add(new PointD(rsrpLineDataSet.size(), UtilHandler.getInstance().toInt(signal.getLte_rsrp(), TypeKey.getInstance().RSRP_MIN)));
					}
					if (mIsSinr) {
						sinrLineDataSet.add(new PointD(sinrLineDataSet.size(), UtilHandler.getInstance().toInt(signal.getLte_sinr(), TypeKey.getInstance().SINR_MIN)));
					}
					if (signal.getTypeNet().equals("NR")) {
						if (mIsRsrpNr) {
							rsrpNrLineDataSet.add(new PointD(rsrpNrLineDataSet.size(), UtilHandler.getInstance().toInt(signal.getLte_rsrp_nr(), TypeKey.getInstance().RSRP_MIN)));
						}
						if (mIsSinrNr) {
							sinrNrLineDataSet.add(new PointD(sinrNrLineDataSet.size(), UtilHandler.getInstance().toInt(signal.getLte_sinr_nr(), TypeKey.getInstance().SINR_MIN)));
						}
					}
				} else {
					if (mIsRsrp) {
						rsrpLineDataSet.add(new PointD(rsrpLineDataSet.size(), TypeKey.getInstance().RSRP_MIN));
					}
					if (mIsSinr) {
						sinrLineDataSet.add(new PointD(sinrLineDataSet.size(), TypeKey.getInstance().SINR_MIN));
					}
				}
			}
		}
		invalidate();
	}

	public void showRightDataAxis(int resId) {
		if (rsrpSplinenChart.getDyLineVisible()) {
			triggerClick(currentTouchX, currentTouchY);
		}
		if (resId == R.id.ll_speed_test_rsrp) {
			rsrpSplinenChart.getDataAxis().show();
			sinrSplineChart.getDataAxis().hide();
		} else if (resId == R.id.ll_speed_test_sinr) {
			rsrpSplinenChart.getDataAxis().hide();
			sinrSplineChart.getDataAxis().show();
		}
		invalidate();
	}

	private boolean mIsSinr = true;

	private boolean mIsRsrp = true;

	private boolean mIsSinrNr = false;

	private boolean mIsRsrpNr = false;

	/**
	 * 设置指定曲线显示与隐藏
	 * @param isSinr
	 * @param isRsrp
	 * @param isSinrNr
	 * @param isRsrpNr
	 */
	public void showChartType(boolean isSinr,boolean isRsrp,boolean isSinrNr,boolean isRsrpNr){
		this.mIsSinr = isSinr;
		this.mIsSinrNr = isSinrNr;
		this.mIsRsrp = isRsrp;
		this.mIsRsrpNr = isRsrpNr;
		setTestedDownOrUpData(mTemSignalList, isDownload);
	}

	public void clearData() {
		clearSignalData();
		mClickPostion = -1;
		if (downloadAreaData != null && downloadAreaData.getLinePoint() != null && downloadAreaData.getLinePoint().size() > 0) {
			downloadAreaData.getLinePoint().clear();
		}

		if (uploadAreaData != null && uploadAreaData.getLinePoint() != null && uploadAreaData.getLinePoint().size() > 0) {
			uploadAreaData.getLinePoint().clear();
		}

		areaList.clear();
		rsrpSplinenChart.hideDyLine();

		invalidate();
	}

	private void clearSignalData() {
		if (rsrpSplineData != null && rsrpSplineData.getLineDataSet() != null) {
			rsrpSplineData.getLineDataSet().clear();
		}
		if (sinrSplineData != null && sinrSplineData.getLineDataSet() != null) {
			sinrSplineData.getLineDataSet().clear();
		}
		if (rsrpNrSplineData != null && rsrpNrSplineData.getLineDataSet() != null) {
			rsrpNrSplineData.getLineDataSet().clear();
		}
		if (sinrNrSplineData != null && sinrNrSplineData.getLineDataSet() != null) {
			sinrNrSplineData.getLineDataSet().clear();
		}
	}

	float currentTouchX;
	float currentTouchY;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			if (isEnabled()) {
				currentTouchX = event.getX();
				currentTouchY = event.getY();
				triggerClick(currentTouchX, currentTouchY);
			}

		}
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// // triggerClick(event.getX(), event.getY());
		// performClick();
		// }
		return true;
	}

	// 触发监听
	private synchronized void triggerClick(float x, float y) {
		float toX = x;
		float toY = y;
		mClickPostion = -1;
		if (!rsrpSplinenChart.getListenItemClickStatus())
			return;
		// 获取当前绘制线的坐标
		double mOneWidth = rsrpSplinenChart.getWidth() / xCount;
		// RSRP选中值
		ArrayList<PositionRecord> arrPosition = rsrpSplinenChart.getPositionRecordset();
		if (arrPosition != null && arrPosition.size() > 0) {
			PlotPointPosition record;
			for (int i = 0; i < arrPosition.size(); i++) {
				record = (PlotPointPosition) arrPosition.get(i);
				if (record.getPosition().x >= x - mOneWidth / 2 && record.getPosition().x <= x + mOneWidth / 2) {
					rsrpSplinenChart.showFocusPointF(record.getPosition(), rsrpSplineData.getLinePaint().getStrokeWidth() * 3);
					rsrpSplinenChart.getFocusPaint().setStyle(Paint.Style.FILL);
					rsrpSplinenChart.getFocusPaint().setStrokeWidth(2);
					rsrpSplinenChart.getFocusPaint().setColor(rsrpSplineData.getLinePaint().getColor());
					toX = record.getPosition().x;
					toY = record.getPosition().y;
					if (i != mClickPostion && mClickPostion == -1) {
						mClickPostion = i;
						if (mListenerBack != null) {
							mListenerBack.onListener(EnumRequest.OTHER_SPEED_CLICK.toInt(), i, true);
						}
					}
					break;
				}
			}
		}
		if (mIsRsrpNr) {
			//RSRP_NR选中值
			arrPosition = rsrpNrSplinenChart.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0) {
				PlotPointPosition record;
				for (int i = 0; i < arrPosition.size(); i++) {
					record = (PlotPointPosition) arrPosition.get(i);
					if (record.getPosition().x >= x - mOneWidth / 2 && record.getPosition().x <= x + mOneWidth / 2) {
						rsrpNrSplinenChart.showFocusPointF(record.getPosition(), rsrpNrSplineData.getLinePaint().getStrokeWidth() * 3);
						rsrpNrSplinenChart.getFocusPaint().setStyle(Paint.Style.FILL);
						rsrpNrSplinenChart.getFocusPaint().setStrokeWidth(2);
						rsrpNrSplinenChart.getFocusPaint().setColor(rsrpNrSplineData.getLinePaint().getColor());
						toX = record.getPosition().x;
						toY = record.getPosition().y;
						if (i != mClickPostion && mClickPostion == -1) {
							mClickPostion = i;
							if (mListenerBack != null) {
								mListenerBack.onListener(EnumRequest.OTHER_SPEED_CLICK.toInt(), i, true);
							}
						}
						break;
					}
				}
			}
		}
		// SINR选中值
		arrPosition = sinrSplineChart.getPositionRecordset();
		if (arrPosition != null && arrPosition.size() > 0) {
			PlotPointPosition record;
			for (int i = 0; i < arrPosition.size(); i++) {
				record = (PlotPointPosition) arrPosition.get(i);
				if (record.getPosition().x >= x - mOneWidth / 2 && record.getPosition().x <= x + mOneWidth / 2) {
					sinrSplineChart.showFocusPointF(record.getPosition(), sinrSplineData.getLinePaint().getStrokeWidth() * 3);
					sinrSplineChart.getFocusPaint().setStyle(Paint.Style.FILL);
					sinrSplineChart.getFocusPaint().setColor(sinrSplineData.getLinePaint().getColor());
					sinrSplineChart.getFocusPaint().setStrokeWidth(2);
					toX = record.getPosition().x;
					toY = record.getPosition().y;
					if (i != mClickPostion && mClickPostion == -1) {
						mClickPostion = i;
						if (mListenerBack != null) {
							mListenerBack.onListener(EnumRequest.OTHER_SPEED_CLICK.toInt(), i, true);
						}
					}
					break;
				}
			}
		}
		if (mIsSinrNr) {
			// SINR选中值
			arrPosition = sinrNrSplineChart.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0) {
				PlotPointPosition record;
				for (int i = 0; i < arrPosition.size(); i++) {
					record = (PlotPointPosition) arrPosition.get(i);
					if (record.getPosition().x >= x - mOneWidth / 2 && record.getPosition().x <= x + mOneWidth / 2) {
						sinrNrSplineChart.showFocusPointF(record.getPosition(), sinrNrSplineData.getLinePaint().getStrokeWidth() * 3);
						sinrNrSplineChart.getFocusPaint().setStyle(Paint.Style.FILL);
						sinrNrSplineChart.getFocusPaint().setColor(sinrNrSplineData.getLinePaint().getColor());
						sinrNrSplineChart.getFocusPaint().setStrokeWidth(2);
						toX = record.getPosition().x;
						toY = record.getPosition().y;
						if (i != mClickPostion && mClickPostion == -1) {
							mClickPostion = i;
							if (mListenerBack != null) {
								mListenerBack.onListener(EnumRequest.OTHER_SPEED_CLICK.toInt(), i, true);
							}
						}
						break;
					}
				}
			}
		}
		// 下载/上传选中值
		arrPosition = areaChart.getPositionRecordset();
		if (arrPosition != null && arrPosition.size() > 0) {
			PlotPointPosition record;
			for (int i = 0; i < arrPosition.size(); i++) {
				record = (PlotPointPosition) arrPosition.get(i);
				if (record.getPosition().x >= x - mOneWidth / 2 && record.getPosition().x <= x + mOneWidth / 2) {
					areaChart.getFocusPaint().setStyle(Paint.Style.FILL);
					if (isDownload) {
						areaChart.showFocusPointF(record.getPosition(), downloadAreaData.getLinePaint().getStrokeWidth() * 3);
						areaChart.getFocusPaint().setColor(downloadAreaData.getLineColor());

					} else {
						areaChart.showFocusPointF(record.getPosition(), uploadAreaData.getLinePaint().getStrokeWidth() * 3);
						areaChart.getFocusPaint().setColor(uploadAreaData.getLineColor());
					}
					areaChart.getFocusPaint().setStrokeWidth(2);
					toX = record.getPosition().x;
					toY = record.getPosition().y;
					if (i != mClickPostion && mClickPostion == -1) {
						mClickPostion = i;
						if (mListenerBack != null) {
							mListenerBack.onListener(EnumRequest.OTHER_SPEED_CLICK.toInt(), i, true);
						}
					}
					break;
				}
			}
		}
		// 交叉线
		// if (lnRsrpChart.getDyLineVisible()) {
		rsrpSplinenChart.showDyLine();
		rsrpSplinenChart.getDyLine().setCurrentXY(toX, toY);
		// }
		this.invalidate();
	}

	// 绘画图表
	@Override
	public void render(Canvas canvas) {
		try {
			areaChart.render(canvas);
			rsrpSplinenChart.render(canvas);
			sinrSplineChart.render(canvas);
			if (mIsRsrpNr) {
				rsrpNrSplinenChart.render(canvas);
			}
			if (mIsSinrNr) {
				sinrNrSplineChart.render(canvas);
			}
		} catch (Exception e) {
		}
	}

	// 设置图表大小
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		rsrpSplinenChart.setChartRange(w, h);
		rsrpNrSplinenChart.setChartRange(w, h);
		sinrSplineChart.setChartRange(w, h);
		sinrNrSplineChart.setChartRange(w, h);
		areaChart.setChartRange(w, h);
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

}
