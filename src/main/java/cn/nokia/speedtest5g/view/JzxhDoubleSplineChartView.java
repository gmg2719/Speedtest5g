package cn.nokia.speedtest5g.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.DrawHelper;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PlotPointPosition;
import org.xclcharts.event.click.PositionRecord;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import cn.nokia.speedtest5g.R;
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
 * 基站信号曲线图 -- 支持四条曲线
 * @author JQJ
 *
 */
public class JzxhDoubleSplineChartView  extends ChartView {

	public static final int TYPE_RSRP = 1;
	public static final int TYPE_SINR = 2;
	public static final int TYPE_DOUBLE_RSRP = 3;
	public static final int TYPE_DOUBLE_SINR = 4;

	private SplineChart chartRsrp = new SplineChart(SpeedTest5g.getContext());
	private SplineChart chartSinr = new SplineChart(SpeedTest5g.getContext());
	private SplineChart chartDoubleRsrp = new SplineChart(SpeedTest5g.getContext());
	private SplineChart chartDoubleSinr = new SplineChart(SpeedTest5g.getContext());
	//分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private SplineData mSplineDataRsrp,mSplineDataSinr,mDoubleSplineDataRsrp,mDoubleSplineDataSinr;

	// splinechart支持横向和竖向定制线
	private List<CustomLineData> mXCustomLineDataset = new ArrayList<CustomLineData>();

	//X轴最大值--动态
	public double mMaxX = 60;
	//X轴最大值--静态
	private final int MAX_X_FINAL = 30 * 60;

	private List<PointD> mListTemPointRsrp = new ArrayList<PointD>();
	private List<PointD> mListTemPointSinr = new ArrayList<PointD>();
	private List<PointD> mDoubleListTemPointRsrp = new ArrayList<PointD>();
	private List<PointD> mDoubleListTemPointSinr = new ArrayList<PointD>();

	private float mLastToX,mLastToY;
	private PlotPointPosition mRecordRsrp,mRecordSinr,mDoubleRecordRsrp,mDoubleRecordSinr;
	//当前选中的RSRP游标,当前选中的SINR游标
	private int mRsrpPositionSelect = -1,mSinrPositionSelect = -1,
			mDoubleRsrpPositionSelect = -1,mDoubleSinrPositionSelect = -1;

	public ListenerBack mListenerBack = null;
	public boolean mIsCanTouch = true;

	//是否重定向RSRP游标，是否重定向SINR游标
	private boolean isToRsrp,isToSinr,isDoubleToRsrp,isDoubleToSinr;

	public JzxhDoubleSplineChartView(Context context) {
		super(context);
		initView();

	}

	public JzxhDoubleSplineChartView(Context context, AttributeSet attrs){
		super(context, attrs);
		initView();
	}

	public JzxhDoubleSplineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/**
	 * 显示或隐藏线条
	 * @param type
	 * @param isHide true 隐藏
	 */
	public void hideOrShowLine(int type, boolean isHide){
		if(type == TYPE_RSRP){
			if(chartRsrp != null){
				chartRsrp.isHideLine(isHide);
			}
		}else if(type == TYPE_SINR){
			if(chartSinr != null){
				chartSinr.isHideLine(isHide);
			}
		}else if(type == TYPE_DOUBLE_RSRP){
			if(chartDoubleRsrp != null){
				chartDoubleRsrp.isHideLine(isHide);
			}
		}else if(type == TYPE_DOUBLE_SINR){
			if(chartDoubleSinr != null){
				chartDoubleSinr.isHideLine(isHide);
			}
		}

		invalidate();
	}

	/**
	 * 添加指标值 
	 * @param rsrp
	 * @param sinr
	 * @param tag 标签值--如时间
	 * @param switchType 切换类型 0无切换 1握手切换
	 */
	public int[] addValue(Integer rsrp,Integer sinr,Integer double_rsrp,Integer double_sinr,String tag,int switchType){
		int[] arrCode = {Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
		PointD pointD;
		if (mSplineDataRsrp != null) {
			pointD = new PointD(mSplineDataRsrp.getLineDataSet().size(), rsrp,tag,switchType);
			arrCode[0] = pointD.hashCode();
			addRsrpItem(pointD);
			switchType = 0;
		}
		if (mSplineDataSinr != null) {
			pointD = new PointD(mSplineDataSinr.getLineDataSet().size(), sinr,tag,switchType);
			arrCode[1] = pointD.hashCode();
			addSinrItem(pointD);
		}
		if (mDoubleSplineDataRsrp != null) {
			pointD = new PointD(mDoubleSplineDataRsrp.getLineDataSet().size(), double_rsrp,tag,switchType);
			arrCode[2] = pointD.hashCode();
			addDoubleRsrpItem(pointD);
			switchType = 0;
		}
		if (mDoubleSplineDataSinr != null) {
			pointD = new PointD(mDoubleSplineDataSinr.getLineDataSet().size(), double_sinr,tag,switchType);
			arrCode[3] = pointD.hashCode();
			addDoubleSinrItem(pointD);
		}
		invalidate();
		if (isToRsrp) {
			if(!chartRsrp.getIsHideLine()){
				setLastSelectData(TYPE_RSRP);
			}
			isToRsrp = false;
		}
		if (isToSinr) {
			if(!chartSinr.getIsHideLine()){
				setLastSelectData(TYPE_SINR);
			}
			isToSinr = false;
		}
		if (isDoubleToRsrp) {
			if(!chartDoubleRsrp.getIsHideLine()){
				setLastSelectData(TYPE_DOUBLE_RSRP);
			}
			isDoubleToRsrp = false;
		}
		if (isDoubleToSinr) {
			if(!chartDoubleSinr.getIsHideLine()){
				setLastSelectData(TYPE_DOUBLE_SINR);
			}
			isDoubleToSinr = false;
		}
		setSelectRsrp();
		setSelectSinr();
		setSelectDoubleRsrp();
		setSelectDoubleSinr();
		invalidate();
		return arrCode;
	}

	/**
	 * 回退原有图标
	 */
	public void backResources(){
		for (int i = 0; i < mListTemPointRsrp.size(); i++) {
			if (mListTemPointRsrp.get(i).switchTypeResources != 0) {
				if (mListTemPointRsrp.get(i).switchTypeResources == R.drawable.cell_reselect_select) {
					mListTemPointRsrp.get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
				}
			}
		}

		if (mSplineDataRsrp != null && mSplineDataRsrp.getLineDataSet() != null && mSplineDataRsrp.getLineDataSet().size() >= 0) {
			for (int i = 0; i < mSplineDataRsrp.getLineDataSet().size(); i++) {
				if (mSplineDataRsrp.getLineDataSet().get(i).switchTypeResources != 0) {
					if (mSplineDataRsrp.getLineDataSet().get(i).switchTypeResources == R.drawable.cell_reselect_select) {
						mSplineDataRsrp.getLineDataSet().get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
					}
				}
			}

		}

		for (int i = 0; i < mListTemPointSinr.size(); i++) {
			if (mListTemPointSinr.get(i).switchTypeResources != 0) {
				if (mListTemPointSinr.get(i).switchTypeResources == R.drawable.cell_reselect_select) {
					mListTemPointSinr.get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
				}
			}
		}

		if (mSplineDataSinr != null && mSplineDataSinr.getLineDataSet() != null && mSplineDataSinr.getLineDataSet().size() >= 0) {
			for (int i = 0; i < mSplineDataSinr.getLineDataSet().size(); i++) {
				if (mSplineDataSinr.getLineDataSet().get(i).switchTypeResources != 0) {
					if (mSplineDataSinr.getLineDataSet().get(i).switchTypeResources == R.drawable.cell_reselect_select) {
						mSplineDataSinr.getLineDataSet().get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
					}
				}
			}
		}

		for (int i = 0; i < mDoubleListTemPointRsrp.size(); i++) {
			if (mDoubleListTemPointRsrp.get(i).switchTypeResources != 0) {
				if (mDoubleListTemPointRsrp.get(i).switchTypeResources == R.drawable.cell_reselect_select) {
					mDoubleListTemPointRsrp.get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
				}
			}
		}

		if (mDoubleSplineDataRsrp != null && mDoubleSplineDataRsrp.getLineDataSet() != null && mDoubleSplineDataRsrp.getLineDataSet().size() >= 0) {
			for (int i = 0; i < mDoubleSplineDataRsrp.getLineDataSet().size(); i++) {
				if (mDoubleSplineDataRsrp.getLineDataSet().get(i).switchTypeResources != 0) {
					if (mDoubleSplineDataRsrp.getLineDataSet().get(i).switchTypeResources == R.drawable.cell_reselect_select) {
						mDoubleSplineDataRsrp.getLineDataSet().get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
					}
				}
			}

		}

		for (int i = 0; i < mDoubleListTemPointSinr.size(); i++) {
			if (mDoubleListTemPointSinr.get(i).switchTypeResources != 0) {
				if (mDoubleListTemPointSinr.get(i).switchTypeResources == R.drawable.cell_reselect_select) {
					mDoubleListTemPointSinr.get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
				}
			}
		}

		if (mDoubleSplineDataSinr != null && mDoubleSplineDataSinr.getLineDataSet() != null && mDoubleSplineDataSinr.getLineDataSet().size() >= 0) {
			for (int i = 0; i < mDoubleSplineDataSinr.getLineDataSet().size(); i++) {
				if (mDoubleSplineDataSinr.getLineDataSet().get(i).switchTypeResources != 0) {
					if (mDoubleSplineDataSinr.getLineDataSet().get(i).switchTypeResources == R.drawable.cell_reselect_select) {
						mDoubleSplineDataSinr.getLineDataSet().get(i).switchTypeResources = R.drawable.icon_ho_no_in_call;
					}
				}
			}
		}

		clearSelectData();
		invalidate();
	}

	/**
	 * 更新切换图标
	 */
	public void updateValue(int hashCode,int switchTypeResources,int type){
		if (hashCode != Integer.MAX_VALUE) {
			if (type == TYPE_RSRP) {
				if (mSplineDataRsrp != null && mSplineDataRsrp.getLineDataSet() != null && mSplineDataRsrp.getLineDataSet().size() >= 0) {
					for (int i = mSplineDataRsrp.getLineDataSet().size() - 1; i >= 0; i--) {
						if (hashCode == mSplineDataRsrp.getLineDataSet().get(i).hashCode()) {
							int mLastSwitchResourcesRsrp = mSplineDataRsrp.getLineDataSet().get(i).switchTypeResources;
							if (mLastSwitchResourcesRsrp != 0) {
								mSplineDataRsrp.getLineDataSet().get(i).switchTypeResources = switchTypeResources;
								invalidate();
								return;
							}
						}
					}
				}
			}else if(type == TYPE_SINR){
				if (mSplineDataSinr != null && mSplineDataSinr.getLineDataSet() != null && mSplineDataSinr.getLineDataSet().size() >= 0) {
					for (int i = mSplineDataSinr.getLineDataSet().size() - 1; i >= 0; i--) {
						if (hashCode == mSplineDataSinr.getLineDataSet().get(i).hashCode()) {
							int mLastSwitchResourcesSinr = mSplineDataSinr.getLineDataSet().get(i).switchTypeResources;
							if (mLastSwitchResourcesSinr != 0) {
								mSplineDataSinr.getLineDataSet().get(i).switchTypeResources = switchTypeResources;
								invalidate();
								return;
							}
						}
					}
				}
			}else if (type == TYPE_DOUBLE_RSRP) {
				if (mDoubleSplineDataRsrp != null && mDoubleSplineDataRsrp.getLineDataSet() != null && mDoubleSplineDataRsrp.getLineDataSet().size() >= 0) {
					for (int i = mDoubleSplineDataRsrp.getLineDataSet().size() - 1; i >= 0; i--) {
						if (hashCode == mDoubleSplineDataRsrp.getLineDataSet().get(i).hashCode()) {
							int mLastSwitchResourcesRsrp = mDoubleSplineDataRsrp.getLineDataSet().get(i).switchTypeResources;
							if (mLastSwitchResourcesRsrp != 0) {
								mDoubleSplineDataRsrp.getLineDataSet().get(i).switchTypeResources = switchTypeResources;
								invalidate();
								return;
							}
						}
					}
				}
			}else if(type == TYPE_DOUBLE_SINR){
				if (mDoubleSplineDataSinr != null && mDoubleSplineDataSinr.getLineDataSet() != null && mDoubleSplineDataSinr.getLineDataSet().size() >= 0) {
					for (int i = mDoubleSplineDataSinr.getLineDataSet().size() - 1; i >= 0; i--) {
						if (hashCode == mDoubleSplineDataSinr.getLineDataSet().get(i).hashCode()) {
							int mLastSwitchResourcesSinr = mDoubleSplineDataSinr.getLineDataSet().get(i).switchTypeResources;
							if (mLastSwitchResourcesSinr != 0) {
								mDoubleSplineDataSinr.getLineDataSet().get(i).switchTypeResources = switchTypeResources;
								invalidate();
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 设置上一个点游标
	 * @param type 1rsrp 2sinr 3double_rsrp 4double_sinr 
	 */
	private void setLastSelectData(int type){
		if (type == TYPE_RSRP) {
			if (mRecordRsrp != null && mRsrpPositionSelect - 1 >= 0) {
				//RSRP选中值
				ArrayList<PositionRecord> arrPosition = chartRsrp.getPositionRecordset();
				if (arrPosition != null && arrPosition.size() > 0 && arrPosition.size() > mRsrpPositionSelect){
					mRsrpPositionSelect -= 1;
					mRecordRsrp = (PlotPointPosition) arrPosition.get(mRsrpPositionSelect);
					//这里取上一次点坐标（问题点，目前先这样处理）
					mRecordRsrp.getPosition().y = ((PlotPointPosition) arrPosition.get(mRsrpPositionSelect + 1)).getPosition().y;
					mLastToX = mRecordRsrp.getPosition().x;
					mLastToY = mRecordRsrp.getPosition().y-10;
					chartRsrp.getDyLine().setCurrentXY(mLastToX,mLastToY);
				}
			}else if (mRecordRsrp != null) {
				mRecordRsrp = null;
				chartRsrp.showFocusPointF(null, 0);
				chartRsrp.getDyLine().setCurrentXY(-1,-1);
				onBackListener(EnumRequest.OTHER_CHART_RSRP_RETURN.toInt(), null);
			}
		}else if(type == TYPE_SINR){
			if (mRecordSinr != null && mSinrPositionSelect - 1 >= 0) {
				//RSRP选中值
				ArrayList<PositionRecord> arrPosition = chartSinr.getPositionRecordset();
				if (arrPosition != null && arrPosition.size() > 0 && arrPosition.size() > mSinrPositionSelect){
					mSinrPositionSelect -= 1;
					mRecordSinr = (PlotPointPosition) arrPosition.get(mSinrPositionSelect);
					//这里取上一次点坐标（问题点，目前先这样处理）
					mRecordSinr.getPosition().y = ((PlotPointPosition) arrPosition.get(mSinrPositionSelect + 1)).getPosition().y;
					if(mLastToX <= -1){
						mLastToX = mRecordSinr.getPosition().x;
					}
					if(mLastToY <= -1){
						mLastToY = mRecordSinr.getPosition().y-10;
					}
					chartRsrp.getDyLine().setCurrentXY(mLastToX,mLastToY);
				}
			}else if (mRecordSinr != null) {
				mRecordSinr = null;
				chartRsrp.getDyLine().setCurrentXY(-1,-1);
				chartSinr.showFocusPointF(null, 0);
				onBackListener(EnumRequest.OTHER_CHART_SINR_RETURN.toInt(), null);
			}
		}else if(type == TYPE_DOUBLE_RSRP){
			if (mDoubleRecordRsrp != null && mDoubleRsrpPositionSelect - 1 >= 0) {
				//RSRP选中值
				ArrayList<PositionRecord> arrPosition = chartDoubleRsrp.getPositionRecordset();
				if (arrPosition != null && arrPosition.size() > 0 && arrPosition.size() > mDoubleRsrpPositionSelect){
					mDoubleRsrpPositionSelect -= 1;
					mDoubleRecordRsrp = (PlotPointPosition) arrPosition.get(mDoubleRsrpPositionSelect);
					//这里取上一次点坐标（问题点，目前先这样处理）
					mDoubleRecordRsrp.getPosition().y = ((PlotPointPosition) arrPosition.get(mDoubleRsrpPositionSelect + 1)).getPosition().y;
					if(mLastToX <= -1){
						mLastToX = mDoubleRecordRsrp.getPosition().x;
					}
					if(mLastToY <= -1){
						mLastToY = mDoubleRecordRsrp.getPosition().y-10;
					}
					chartRsrp.getDyLine().setCurrentXY(mLastToX,mLastToY);
				}
			}else if (mDoubleRecordRsrp != null) {
				mDoubleRecordRsrp = null;
				chartRsrp.getDyLine().setCurrentXY(-1,-1);
				chartDoubleRsrp.showFocusPointF(null, 0);
				onBackListener(EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt(), null);
			}
		}else if(type == TYPE_DOUBLE_SINR){
			if (mDoubleRecordSinr != null && mDoubleSinrPositionSelect - 1 >= 0) {
				//RSRP选中值
				ArrayList<PositionRecord> arrPosition = chartDoubleSinr.getPositionRecordset();
				if (arrPosition != null && arrPosition.size() > 0 && arrPosition.size() > mDoubleSinrPositionSelect){
					mDoubleSinrPositionSelect -= 1;
					mDoubleRecordSinr = (PlotPointPosition) arrPosition.get(mDoubleSinrPositionSelect);
					//这里取上一次点坐标（问题点，目前先这样处理）
					mDoubleRecordSinr.getPosition().y = ((PlotPointPosition) arrPosition.get(mDoubleSinrPositionSelect + 1)).getPosition().y;
					if(mLastToX <= -1){
						mLastToX = mDoubleRecordSinr.getPosition().x;
					}
					if(mLastToY <= -1){
						mLastToY = mDoubleRecordSinr.getPosition().y-10;
					}
					chartRsrp.getDyLine().setCurrentXY(mLastToX,mLastToY);
				}
			}else if (mDoubleRecordSinr != null) {
				mDoubleRecordSinr = null;
				chartRsrp.getDyLine().setCurrentXY(-1,-1);
				chartDoubleSinr.showFocusPointF(null, 0);
				onBackListener(EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt(), null);
			}
		}
	}

	public void onDestroy(){
		if (chartRsrp != null) {
			chartRsrp.clearBitmap();
		}
		if (chartSinr != null) {
			chartSinr.clearBitmap();
		}
		if (chartDoubleRsrp != null) {
			chartDoubleRsrp.clearBitmap();
		}
		if (chartDoubleSinr != null) {
			chartDoubleSinr.clearBitmap();
		}
	}

	private PointD mPointDtem = null;
	private void addRsrpItem(PointD pointD){
		if (mListTemPointRsrp.size() >= MAX_X_FINAL) {
			mListTemPointRsrp.remove(0);
		}
		mListTemPointRsrp.add(pointD);
		if (mSplineDataRsrp.getLineDataSet().size() >= mMaxX) {
			isToRsrp = true;
			mSplineDataRsrp.getLineDataSet().clear();
			for (int i = mListTemPointRsrp.size() - (int)mMaxX ; i < mListTemPointRsrp.size(); i++) {
				mPointDtem = mListTemPointRsrp.get(i);
				mPointDtem.x = mSplineDataRsrp.getLineDataSet().size();
				mSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}else {
			mSplineDataRsrp.getLineDataSet().add(pointD);
		}
	}

	private void addSinrItem(PointD pointD){
		if (mListTemPointSinr.size() >= MAX_X_FINAL) {
			mListTemPointSinr.remove(0);
		}
		mListTemPointSinr.add(pointD);
		if (mSplineDataSinr.getLineDataSet().size() >= mMaxX) {
			isToSinr = true;
			mSplineDataSinr.getLineDataSet().clear();
			for (int i = mListTemPointSinr.size() - (int)mMaxX ; i < mListTemPointSinr.size(); i++) {
				mPointDtem = mListTemPointSinr.get(i);
				mPointDtem.x = mSplineDataSinr.getLineDataSet().size();
				mSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}else {
			mSplineDataSinr.getLineDataSet().add(pointD);
		}
	}

	/**
	 * nr rsrp
	 * @param pointD
	 */
	private void addDoubleRsrpItem(PointD pointD){
		if (mDoubleListTemPointRsrp.size() >= MAX_X_FINAL) {
			mDoubleListTemPointRsrp.remove(0);
		}
		mDoubleListTemPointRsrp.add(pointD);
		if (mDoubleSplineDataRsrp.getLineDataSet().size() >= mMaxX) {
			isDoubleToRsrp = true;
			mDoubleSplineDataRsrp.getLineDataSet().clear();
			for (int i = mDoubleListTemPointRsrp.size() - (int)mMaxX ; i < mDoubleListTemPointRsrp.size(); i++) {
				mPointDtem = mDoubleListTemPointRsrp.get(i);
				mPointDtem.x = mDoubleSplineDataRsrp.getLineDataSet().size();
				mDoubleSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}else {
			mDoubleSplineDataRsrp.getLineDataSet().add(pointD);
		}
	}

	/**
	 * nr sinr
	 * @param pointD
	 */
	private void addDoubleSinrItem(PointD pointD){
		if (mDoubleListTemPointSinr.size() >= MAX_X_FINAL) {
			mDoubleListTemPointSinr.remove(0);
		}
		mDoubleListTemPointSinr.add(pointD);
		if (mDoubleSplineDataSinr.getLineDataSet().size() >= mMaxX) {
			isDoubleToSinr = true;
			mDoubleSplineDataSinr.getLineDataSet().clear();
			for (int i = mDoubleListTemPointSinr.size() - (int)mMaxX ; i < mDoubleListTemPointSinr.size(); i++) {
				mPointDtem = mDoubleListTemPointSinr.get(i);
				mPointDtem.x = mDoubleSplineDataSinr.getLineDataSet().size();
				mDoubleSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}else {
			mDoubleSplineDataSinr.getLineDataSet().add(pointD);
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
		chartRsrp.setCategoryAxisMax(mMaxX);
		chartSinr.setCategoryAxisMax(mMaxX);
		chartDoubleRsrp.setCategoryAxisMax(mMaxX);
		chartDoubleSinr.setCategoryAxisMax(mMaxX);
		if (mSplineDataSinr.getLineDataSet().size() >= mMaxX) {
			mSplineDataSinr.getLineDataSet().clear();
			for (int i = mListTemPointSinr.size() - (int)mMaxX ; i < mListTemPointSinr.size(); i++) {
				mPointDtem = mListTemPointSinr.get(i);
				mPointDtem.x = mSplineDataSinr.getLineDataSet().size();
				mSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}else if (mSplineDataSinr.getLineDataSet().size() < mListTemPointSinr.size()) {
			mSplineDataSinr.getLineDataSet().clear();
			for (int i = (mListTemPointSinr.size() < mMaxX ? 0 : mListTemPointSinr.size() - (int)mMaxX) ; i < mListTemPointSinr.size(); i++) {
				mPointDtem = mListTemPointSinr.get(i);
				mPointDtem.x = mSplineDataSinr.getLineDataSet().size();
				mSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}

		if (mSplineDataRsrp.getLineDataSet().size() >= mMaxX) {
			mSplineDataRsrp.getLineDataSet().clear();
			for (int i = mListTemPointRsrp.size() - (int)mMaxX ; i < mListTemPointRsrp.size(); i++) {
				mPointDtem = mListTemPointRsrp.get(i);
				mPointDtem.x = mSplineDataRsrp.getLineDataSet().size();
				mSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}else if (mSplineDataRsrp.getLineDataSet().size() < mListTemPointRsrp.size()) {
			mSplineDataRsrp.getLineDataSet().clear();
			for (int i = (mListTemPointRsrp.size() < mMaxX ? 0 : mListTemPointRsrp.size() - (int)mMaxX) ; i < mListTemPointRsrp.size(); i++) {
				mPointDtem = mListTemPointRsrp.get(i);
				mPointDtem.x = mSplineDataRsrp.getLineDataSet().size();
				mSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}

		if (mDoubleSplineDataSinr.getLineDataSet().size() >= mMaxX) {
			mDoubleSplineDataSinr.getLineDataSet().clear();
			for (int i = mDoubleListTemPointSinr.size() - (int)mMaxX ; i < mDoubleListTemPointSinr.size(); i++) {
				mPointDtem = mDoubleListTemPointSinr.get(i);
				mPointDtem.x = mDoubleSplineDataSinr.getLineDataSet().size();
				mDoubleSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}else if (mDoubleSplineDataSinr.getLineDataSet().size() < mDoubleListTemPointSinr.size()) {
			mDoubleSplineDataSinr.getLineDataSet().clear();
			for (int i = (mDoubleListTemPointSinr.size() < mMaxX ? 0 : mDoubleListTemPointSinr.size() - (int)mMaxX) ; i < mDoubleListTemPointSinr.size(); i++) {
				mPointDtem = mDoubleListTemPointSinr.get(i);
				mPointDtem.x = mDoubleSplineDataSinr.getLineDataSet().size();
				mDoubleSplineDataSinr.getLineDataSet().add(mPointDtem);
			}
		}

		if (mDoubleSplineDataRsrp.getLineDataSet().size() >= mMaxX) {
			mDoubleSplineDataRsrp.getLineDataSet().clear();
			for (int i = mDoubleListTemPointRsrp.size() - (int)mMaxX ; i < mDoubleListTemPointRsrp.size(); i++) {
				mPointDtem = mDoubleListTemPointRsrp.get(i);
				mPointDtem.x = mDoubleSplineDataRsrp.getLineDataSet().size();
				mDoubleSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}else if (mDoubleSplineDataRsrp.getLineDataSet().size() < mDoubleListTemPointRsrp.size()) {
			mDoubleSplineDataRsrp.getLineDataSet().clear();
			for (int i = (mDoubleListTemPointRsrp.size() < mMaxX ? 0 : mDoubleListTemPointRsrp.size() - (int)mMaxX) ; i < mDoubleListTemPointRsrp.size(); i++) {
				mPointDtem = mDoubleListTemPointRsrp.get(i);
				mPointDtem.x = mDoubleSplineDataRsrp.getLineDataSet().size();
				mDoubleSplineDataRsrp.getLineDataSet().add(mPointDtem);
			}
		}
		clearSelectData();
		invalidate();
	}

	//清除选中数据
	private void clearSelectData(){
		if (mRecordRsrp != null || mRecordSinr != null || mDoubleRecordRsrp != null || mDoubleRecordSinr != null) {
			mRecordRsrp = null;
			mRecordSinr = null;
			mDoubleRecordRsrp = null;
			mDoubleRecordSinr = null;
			chartRsrp.getDyLine().setCurrentXY(-1,-1);
			onBackListener(EnumRequest.OTHER_CHART_RSRP_RETURN.toInt(), null);
			onBackListener(EnumRequest.OTHER_CHART_SINR_RETURN.toInt(), null);
			onBackListener(EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt(), null);
			onBackListener(EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt(), null);
		}
	}

	private void initView(){
		chartLabels();
		chartDataSet();
		if (mSplineDataRsrp != null) {
			setChartInit(chartRsrp,true,mSplineDataRsrp,XEnum.AxisLocation.LEFT,Color.parseColor("#0097E8"),-50,-130,10);
		}
		if (mSplineDataSinr != null) {
			setChartInit(chartSinr,false,mSplineDataSinr,XEnum.AxisLocation.RIGHT,Color.parseColor("#FF0504"),40,-40,10);
		}
		if (mDoubleSplineDataRsrp != null) {
			setChartInit(chartDoubleRsrp,false,mDoubleSplineDataRsrp,XEnum.AxisLocation.LEFT,Color.parseColor("#0097E8"),-50,-130,10);
		}
		if (mDoubleSplineDataSinr != null) {
			setChartInit(chartDoubleSinr,false,mDoubleSplineDataSinr,XEnum.AxisLocation.RIGHT,Color.parseColor("#FF0504"),40,-40,10);
		}
		//綁定手势滑动事件
		this.bindTouch(this,chartRsrp);
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//图所占范围大小
		chartRsrp.setChartRange(w,h);
		chartSinr.setChartRange(w,h);
		chartDoubleRsrp.setChartRange(w,h);
		chartDoubleSinr.setChartRange(w,h);
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
			chart.setPadding(dp25 * 1.2f, dp25, dp25, dp25);
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
			chart.getDataAxis().getAxisPaint().setColor(getResources().getColor(R.color.gray_c0c0c3));
			chart.getCategoryAxis().getTickLabelPaint().setColor(getResources().getColor(R.color.gray_c0c0c3));
			//Y轴标签字体颜色
			chart.getDataAxis().getTickLabelPaint().setColor(colorLabY);
			//Y标签显示位置
			if (alignLabY == XEnum.AxisLocation.RIGHT) {
				chart.getDataAxis().setHorizontalTickAlign(Align.RIGHT);
				chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.LEFT);
			}
			chart.setDataAxisLocation(alignLabY);

			//标签轴最大值X
			chart.setCategoryAxisMax(mMaxX);
			//标签轴最小值
			chart.setCategoryAxisMin(0);
			chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴

			if (isClick) {
				chart.getPlotGrid().showHorizontalLines();
				//绘制十字交叉线
				chart.showDyLine();
				chart.getDyLine().getLinePaint().setColor(Color.parseColor("#777777"));
				chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
				//为了让触发更灵敏，可以扩大5px的点击监听范围
				chart.extPointClickRange(5);
				chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
				chart.getCategoryAxis().showAxisLabels();
			}else {
				chart.getCategoryAxis().hideAxisLabels();
				chart.getPlotGrid().hideHorizontalLines();
				chart.hideDyLine();
				chart.DeactiveListenItemClick();
			}
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
					String label = "["+value+"]";
					return (label);
				}

			});
			//显示平滑曲线
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void chartDataSet(){
		//线1的数据集
		mSplineDataRsrp = new SplineData("RSRP",new ArrayList<PointD>(),Color.parseColor("#0097E8"));
		//把线弄细点
		mSplineDataRsrp.getLinePaint().setStrokeWidth(3);
		mSplineDataRsrp.setDotStyle(XEnum.DotStyle.DOT);
		mSplineDataRsrp.getDotPaint().setColor(Color.parseColor("#00000000"));

		//线2的数据集
		mSplineDataSinr = new SplineData("SINR",new ArrayList<PointD>(),Color.parseColor("#FF0504"));
		mSplineDataSinr.getLinePaint().setStrokeWidth(3);
		mSplineDataSinr.setDotStyle(XEnum.DotStyle.DOT);
		mSplineDataSinr.getDotPaint().setColor(Color.parseColor("#00000000"));

		//线3的数据集
		mDoubleSplineDataRsrp = new SplineData("RSRP_DOUBLE",new ArrayList<PointD>(),Color.parseColor("#0097E8"));
		mDoubleSplineDataRsrp.getLinePaint().setStrokeWidth(3);
		//虚实线
		mDoubleSplineDataRsrp.getLinePaint().setPathEffect(DrawHelper.getInstance().getDashLineStyle());
		mDoubleSplineDataRsrp.setDotStyle(XEnum.DotStyle.DOT);
		mDoubleSplineDataRsrp.getDotPaint().setColor(Color.parseColor("#00000000"));

		//线4的数据集
		mDoubleSplineDataSinr = new SplineData("SINR_DOUBLE",new ArrayList<PointD>(),Color.parseColor("#FF0504"));
		mDoubleSplineDataSinr.getLinePaint().setStrokeWidth(3);
		//虚实线
		mDoubleSplineDataSinr.getLinePaint().setPathEffect(DrawHelper.getInstance().getDashLineStyle());
		mDoubleSplineDataSinr.setDotStyle(XEnum.DotStyle.DOT);
		mDoubleSplineDataSinr.getDotPaint().setColor(Color.parseColor("#00000000"));
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
			chartRsrp.render(canvas);
			chartSinr.render(canvas);
			chartDoubleRsrp.render(canvas);
			chartDoubleSinr.render(canvas);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mIsCanTouch){ //编辑模式下不可触摸
			return true;
		}
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			triggerClick(event.getX(),event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}

	//触发监听
	private void triggerClick(double x,double y){
		float toX = -1;
		float toY = -1;
		if(!chartRsrp.getListenItemClickStatus()){
			return;
		}
		//获取当前绘制线的坐标
		double mOneWidth = chartRsrp.getWidth()/mMaxX;
		ArrayList<PositionRecord> arrPosition = null;
		//RSRP选中值
		if(!chartRsrp.getIsHideLine()){
			arrPosition = chartRsrp.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				chartRsrp.showFocusPointF(null, 0);
				for (int i = 0; i < arrPosition.size(); i++) {
					mRecordRsrp = (PlotPointPosition) arrPosition.get(i);
					if (mRecordRsrp.getPosition().x >= x - mOneWidth/2 && mRecordRsrp.getPosition().x <= x + mOneWidth/2) {
						if (mLastToX != mRecordRsrp.getPosition().x) {
							mRsrpPositionSelect = i;
							toX = mRecordRsrp.getPosition().x;
							toY = mRecordRsrp.getPosition().y;
							setSelectRsrp();
							PointD pointD = chartRsrp.getDataSource().get(mRecordRsrp.getDataID()).getLineDataSet().get(mRecordRsrp.getDataChildID());
							onBackListener(EnumRequest.OTHER_CHART_RSRP_RETURN.toInt(), pointD);
						}
						break;
					}
				}
			}
		}
		//SINR选中值
		if(!chartSinr.getIsHideLine()){
			arrPosition = chartSinr.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				chartSinr.showFocusPointF(null, 0);
				for (int i = 0; i < arrPosition.size(); i++) {
					mRecordSinr = (PlotPointPosition) arrPosition.get(i);
					if (mRecordSinr.getPosition().x >= x - mOneWidth/2 && mRecordSinr.getPosition().x <= x + mOneWidth/2) {
						if (mLastToX != mRecordSinr.getPosition().x) {
							mSinrPositionSelect = i;
							if(toX <= -1){
								toX = mRecordSinr.getPosition().x;
							}
							if (toY <= -1) {
								toY = mRecordSinr.getPosition().y;
							}
							setSelectSinr();
							PointD pointD = chartSinr.getDataSource().get(mRecordSinr.getDataID()).getLineDataSet().get(mRecordSinr.getDataChildID());
							onBackListener(EnumRequest.OTHER_CHART_SINR_RETURN.toInt(), pointD);
						}else if (mRecordRsrp != null) {
							toX = -1 ;
							toY = -1;
							chartRsrp.showFocusPointF(null, 0);
						}
						break;
					}
				}
			}
		}
		//DOUBLE_RSRP选中值
		if(!chartDoubleRsrp.getIsHideLine()){
			arrPosition = chartDoubleRsrp.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				chartDoubleRsrp.showFocusPointF(null, 0);
				for (int i = 0; i < arrPosition.size(); i++) {
					mDoubleRecordRsrp = (PlotPointPosition) arrPosition.get(i);
					if (mDoubleRecordRsrp.getPosition().x >= x - mOneWidth/2 && mDoubleRecordRsrp.getPosition().x <= x + mOneWidth/2) {
						if (mLastToX != mDoubleRecordRsrp.getPosition().x) {
							mDoubleRsrpPositionSelect = i;
							if(toX <= -1){
								toX = mDoubleRecordRsrp.getPosition().x;
							}
							if (toY <= -1) {
								toY = mDoubleRecordRsrp.getPosition().y;
							}
							setSelectDoubleRsrp();
							PointD pointD = chartDoubleRsrp.getDataSource().get(mDoubleRecordRsrp.getDataID()).getLineDataSet().get(mDoubleRecordRsrp.getDataChildID());
							onBackListener(EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt(), pointD);
						}
						break;
					}
				}
			}
		}
		//DOUBLE_SINR选中值
		if(!chartDoubleSinr.getIsHideLine()){
			arrPosition = chartDoubleSinr.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				chartDoubleSinr.showFocusPointF(null, 0);
				for (int i = 0; i < arrPosition.size(); i++) {
					mDoubleRecordSinr = (PlotPointPosition) arrPosition.get(i);
					if (mDoubleRecordSinr.getPosition().x >= x - mOneWidth/2 && mDoubleRecordSinr.getPosition().x <= x + mOneWidth/2) {
						if (mLastToX != mDoubleRecordSinr.getPosition().x) {
							mDoubleSinrPositionSelect = i;
							if(toX <= -1){
								toX = mDoubleRecordSinr.getPosition().x;
							}
							if (toY <= -1) {
								toY = mDoubleRecordSinr.getPosition().y;
							}
							setSelectDoubleSinr();
							PointD pointD = chartDoubleSinr.getDataSource().get(mDoubleRecordSinr.getDataID()).getLineDataSet().get(mDoubleRecordSinr.getDataChildID());
							onBackListener(EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt(), pointD);
						}else if (mDoubleRecordRsrp != null) {
							toX = -1 ;
							toY = -1;
							chartDoubleRsrp.showFocusPointF(null, 0);
						}
						break;
					}
				}
			}
		}
		//交叉线
		if(chartRsrp.getDyLineVisible()){
			if (toX == -1 && toY == -1) {
				mRecordRsrp = null;
				mRecordSinr = null;
				mDoubleRecordRsrp = null;
				mDoubleRecordSinr = null;
				onBackListener(EnumRequest.OTHER_CHART_RSRP_RETURN.toInt(), null);
				onBackListener(EnumRequest.OTHER_CHART_SINR_RETURN.toInt(), null);
				onBackListener(EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt(), null);
				onBackListener(EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt(), null);
			}
			toY -= 10; //避免Y值过小，竖线不绘制
			mLastToX = toX;
			mLastToY = toY;
			chartRsrp.getDyLine().setCurrentXY(toX,toY);
		}
		this.invalidate();
	}

	/**
	 * 设置上一个或下一个切换游标
	 * @param hashCodeRsrp
	 * @param hastCodeSinr
	 */
	public void setNextOrLastClick(int hashCodeRsrp,int hastCodeSinr){
		//过滤RSRP线条
		if (chartRsrp != null && hashCodeRsrp != 0) {
			ArrayList<PositionRecord> arrPosition = chartRsrp.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				for (int i = 0; i < arrPosition.size(); i++) {
					PlotPointPosition mRecordRsrp1 = (PlotPointPosition) arrPosition.get(i);
					if (chartRsrp.getDataSource().get(mRecordRsrp1.getDataID()).getLineDataSet().get(mRecordRsrp1.getDataChildID()).hashCode() == hashCodeRsrp) {
						triggerClick(mRecordRsrp1.getPosition().x,mRecordRsrp1.getPosition().y);
						return;
					}
				}
			}
		}

		//过滤SINR线条
		if (chartSinr != null && hastCodeSinr != 0) {
			ArrayList<PositionRecord> arrPosition = chartSinr.getPositionRecordset();
			if (arrPosition != null && arrPosition.size() > 0){
				for (int i = 0; i < arrPosition.size(); i++) {
					PlotPointPosition mRecordSinr1 = (PlotPointPosition) arrPosition.get(i);
					if (chartSinr.getDataSource().get(mRecordSinr1.getDataID()).getLineDataSet().get(mRecordSinr1.getDataChildID()).hashCode() == hastCodeSinr) {
						triggerClick(mRecordSinr1.getPosition().x,mRecordSinr1.getPosition().y);
						return;
					}
				}
			}
		}
	}

	//设置RSRP选中点
	private void setSelectRsrp(){
		if(chartRsrp.getIsHideLine()){
			return;
		}
		if (mRecordRsrp != null) {
			chartRsrp.showFocusPointF(mRecordRsrp.getPosition(),mSplineDataRsrp.getLinePaint().getStrokeWidth() * 3,true);
			chartRsrp.getFocusPaint().setStyle(Paint.Style.FILL);
			chartRsrp.getFocusPaint().setStrokeWidth(2);
			chartRsrp.getFocusPaint().setColor(mSplineDataRsrp.getLinePaint().getColor());
		}
	}

	//设置SINR选中点
	private void setSelectSinr(){
		if(chartSinr.getIsHideLine()){
			return;
		}
		if (mRecordSinr != null) {
			chartSinr.showFocusPointF(mRecordSinr.getPosition(),mSplineDataSinr.getLinePaint().getStrokeWidth() * 3,true);
			chartSinr.getFocusPaint().setStyle(Paint.Style.FILL);
			chartSinr.getFocusPaint().setColor(mSplineDataSinr.getLinePaint().getColor());
			chartSinr.getFocusPaint().setStrokeWidth(2);
		}
	}

	//设置DOUBLE_RSRP选中点
	private void setSelectDoubleRsrp(){
		if(chartDoubleRsrp.getIsHideLine()){
			return;
		}
		if (mDoubleRecordRsrp != null) {
			chartDoubleRsrp.showFocusPointF(mDoubleRecordRsrp.getPosition(),mDoubleSplineDataRsrp.getLinePaint().getStrokeWidth() * 3,true);
			chartDoubleRsrp.getFocusPaint().setStyle(Paint.Style.FILL);
			chartDoubleRsrp.getFocusPaint().setStrokeWidth(2);
			chartDoubleRsrp.getFocusPaint().setColor(mDoubleSplineDataRsrp.getLinePaint().getColor());
		}
	}

	//设置DOUBLE_SINR选中点
	private void setSelectDoubleSinr(){
		if(chartDoubleSinr.getIsHideLine()){
			return;
		}
		if (mDoubleRecordSinr != null) {
			chartDoubleSinr.showFocusPointF(mDoubleRecordSinr.getPosition(),mDoubleSplineDataSinr.getLinePaint().getStrokeWidth() * 3,true);
			chartDoubleSinr.getFocusPaint().setStyle(Paint.Style.FILL);
			chartDoubleSinr.getFocusPaint().setColor(mDoubleSplineDataSinr.getLinePaint().getColor());
			chartDoubleSinr.getFocusPaint().setStrokeWidth(2);
		}
	}

	/**
	 * 设置回调
	 * @param type EnumRequest.OTHER_CHART_RSRP_RETURN.toInt() 
	 * 			   EnumRequest.OTHER_CHART_SINR_RETURN.toInt()
	 * 			   EnumRequest.OTHER_CHART_DOUBLE_RSRP_RETURN.toInt() 
	 * 			   EnumRequest.OTHER_CHART_DOUBLE_SINR_RETURN.toInt()
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
