package cn.nokia.speedtest5g.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.xclcharts.chart.AreaChart;
import org.xclcharts.chart.AreaData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import cn.nokia.speedtest5g.wifi.other.WifiUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * WIWI信道曲线图
 * @author zwq
 *
 */
public class WifiXdChartView extends ChartView {
	
	private AreaChart chart = new AreaChart();	
	//x标签集合
	private LinkedList<String> mLabels = new LinkedList<String>();

	public WifiXdChartView(Context context) {
		super(context);
		initView();
	}

	public WifiXdChartView(Context context, AttributeSet attrs){
	    super(context, attrs);
	    initView();
	}

	public WifiXdChartView(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		 initView();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    //图所占范围大小
	    chart.setChartRange(w,h);
	}
	
	private void initView(){
		chartLabels();
		chartRender();
		//綁定手势滑动事件
		this.bindTouch(this,chart);
	}
	
	private List<Integer> mListXdTem = new ArrayList<>();
	private ScanResult itemResult;
	private final int[] mDefaultXd = new int[]{1,3,6,8,149,153,159,165};
	/**
	 * 添加指标值 
	 */
	public void addValue(List<ScanResult> list, String mBssid){
		chart.getDataSource().clear();
		if (list != null && list.size() > 0) {
			if (TextUtils.isEmpty(mBssid)) {
				mBssid = "";
			}
			mListXdTem.clear();
			for (int i = 0; i < list.size(); i++) {
				itemResult = list.get(i);
	            Object[] arrXdType = WifiUtil.getInstance().getChannelByFrequency(itemResult.frequency);
	            int xd = (int)arrXdType[0];
	            if (!mListXdTem.contains(xd)){
	                mListXdTem.add(xd);
	            }
			}
			if (mListXdTem.size() > 0) {
				//从大到小排序
                Collections.sort(mListXdTem, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        if (o1.intValue() < o2.intValue()) {
                            return 1;
                        }else if (o1.intValue() > o2.intValue()) {
                            return -1;
                        }
                        return 0;
                    }
                });
				mLabels.clear();
				mLabels.add("");
				if (mListXdTem.size() > mDefaultXd.length) {
					for (int i = mListXdTem.size() - 1; i >= 0; i--) {
						mLabels.add(String.valueOf(mListXdTem.get(i)));
					}
				}else {
					for (int i = 0; i < mDefaultXd.length; i++) {
						if (mListXdTem.size() > 0){
							//判断是否有比当前信道还小的
                            if (mListXdTem.get(mListXdTem.size() - 1) <= mDefaultXd[i]){
                            	mLabels.add(String.valueOf(mListXdTem.get(mListXdTem.size() - 1)));
                            	mListXdTem.remove(mListXdTem.size() - 1);
							}else {
								mLabels.add(String.valueOf(mDefaultXd[i]));
							}
						}else {
							mLabels.add(String.valueOf(mDefaultXd[i]));
						}
					}
				}
				mLabels.add("");
				
				int sizeColor = WifiUtil.getInstance().getArrColor().length;
				for (int i = 0; i < list.size(); i++) {
					itemResult = list.get(i);
					Object[] arrXdType = WifiUtil.getInstance().getChannelByFrequency(itemResult.frequency);
		            int xd = (int)arrXdType[0];
		            int lastIndexOf = mLabels.lastIndexOf(String.valueOf(xd));
					if (lastIndexOf <= 0) {
						continue;
					}
					List<Double> dataSeries = new LinkedList<Double>();	
					for (int j = 0; j < lastIndexOf - 1; j++) {
						dataSeries.add((double)Integer.MIN_VALUE);
					}
					dataSeries.add((double)chart.getDataAxis().getAxisMin());
					dataSeries.add((double)itemResult.level);
					dataSeries.add((double)chart.getDataAxis().getAxisMin());
					AreaData mAreaData = new AreaData("",dataSeries,WifiUtil.getInstance().getArrColor()[i%sizeColor],WifiUtil.getInstance().getArrColorApply()[i%sizeColor],WifiUtil.getInstance().getArrColorApply()[i%sizeColor]);
					//设置线上每点对应标签的颜色
					mAreaData.getDotLabelPaint().setColor(mAreaData.getLineColor());
					//设置点标签
					mAreaData.setLabelVisible(true);
					mAreaData.getDotLabelPaint().setTextSize(22);
					mAreaData.setLabValue(itemResult.SSID + (mBssid.equals(itemResult.BSSID) ? "(当前)" : ""));
					//不显示点
					mAreaData.setDotStyle(XEnum.DotStyle.HIDE);
					mAreaData.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
					mAreaData.getLabelOptions().setOffsetY(10.f);
					mAreaData.setApplayGradient(true);
					mAreaData.getLinePaint().setStrokeWidth(2F);
					chart.getDataSource().add(mAreaData);
				}
			}
		}
		invalidate();
	}
	
	private void chartRender() {
		try{												 
			float dp25 = DensityUtil.dip2px(getContext(), 25.0F);
			chart.setPadding(dp25,dp25,dp25,dp25);
			//轴数据源						
			//标签轴
			chart.setCategories(mLabels);
			//数据轴
			chart.setDataSource(new ArrayList<AreaData>(),true);
			//数据轴最大值Y
			chart.getDataAxis().setAxisMax(-20);
			//标签轴最小值y
			chart.getDataAxis().setAxisMin(-120);
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(15);
			//网格
			chart.getPlotGrid().showHorizontalLines();
			chart.getPlotGrid().showVerticalLines();	
			//把顶轴和右轴隐藏
			//chart.hideTopAxis();
			//chart.hideRightAxis();
			//把轴线和刻度线给隐藏起来
			chart.getDataAxis().hideAxisLine();
			chart.getDataAxis().hideTickMarks();		
			chart.getCategoryAxis().hideAxisLine();
			chart.getCategoryAxis().hideTickMarks();				
			//设置为虚线
//			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.parseColor("#484B54"));
			chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(0.5F);
			chart.getPlotGrid().getVerticalLinePaint().setColor(Color.parseColor("#484B54"));
			chart.getPlotGrid().getVerticalLinePaint().setStrokeWidth(0.5F);
			//透明度
			chart.setAreaAlpha(180);
			//显示图例
			chart.getPlotLegend().hide();
			chart.setBackgroundColor(Color.parseColor("#00000000"));
			//Y轴标签字体颜色
			chart.getDataAxis().getTickLabelPaint().setColor(Color.parseColor("#c0c0c3"));
			chart.getCategoryAxis().getTickLabelPaint().setColor(Color.parseColor("#c0c0c3"));
			//禁止点击监听
			chart.DeactiveListenItemClick();
			//定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){
	
				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub		
					Double tmp = Double.parseDouble(value);
					DecimalFormat df=new DecimalFormat("#0");
					String label = df.format(tmp).toString();				
					return (label);
				}
				
			});
			//禁止移动
			chart.disablePanMode();
			chart.disableScale();
			//不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();
			//不显示线条类型
			chart.getPlotLegend().hide();
			//隐藏边框
			chart.hideBorder();
		} catch (Exception e) {
		}
	}

	private void chartLabels(){		
		mLabels.add("");
		for (int i = 1; i <= 14; i++) {
			mLabels.add(String.valueOf(i));
		}
		mLabels.add("");
	}
	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	
        }
    }
}
