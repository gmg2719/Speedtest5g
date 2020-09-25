package com.android.volley.util;

import java.math.BigDecimal;
import android.content.Context;

/**
 * 四舍五入计算，小数点保留
 */
public class ConversionUtil {

	private static ConversionUtil conversionUtil = null;
	
	public synchronized static ConversionUtil getInstances(){
		if (conversionUtil == null) {
			conversionUtil = new ConversionUtil();
		}
		return conversionUtil;
	}
	
	/**
	 * sp转px
	 * @param unit
	 * @param size
	 * @return
	 */
	public float getSPtoPx(Context context,float spSize) {
		try {
			final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
	        return (int) (spSize * fontScale + 0.5f); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return spSize;
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public int getDPtoPx(Context context,float dpValue) {  
    	try {
    		 final float scale = context.getResources().getDisplayMetrics().density;  
    	        return (int) (dpValue * scale + 0.5f);  
		} catch (Exception e) {
			// TODO: handle exception
		}
       return (int) dpValue;
    }  
    
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public int getPXtodp(Context context,float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	
	/**
	 * 保留几位小数，取绝对值
	 * @param d1
	 * @param d2
	 * @param Digit 小数点位数
	 * @return
	 */
	public double getDecimalDouble(double d1,double d2,int Digit){
		BigDecimal bgNum1 = new BigDecimal(Math.abs(d1 - d2));
		BigDecimal bgNum2 = new BigDecimal("1");
		return bgNum1.divide(bgNum2, Digit, BigDecimal.ROUND_HALF_UP).doubleValue();	
	}
	
	/**
	 * 保留几位小数，
	 * @param d1
	 * @param Digit 小数点位数
	 * @return
	 */
	public double getDecimalDouble(double d,int Digit){
		BigDecimal bgNum1 = new BigDecimal(d);
		BigDecimal bgNum2 = new BigDecimal("1");
		return bgNum1.divide(bgNum2, Digit, BigDecimal.ROUND_HALF_UP).doubleValue();	
	}
	
	/**
	 * 分母除以分子取小数
	 * @param dDenominator 分母
	 * @param dMember 分子
	 * @param Digit 小数位
	 * @return
	 */
	public double getDivide(double dDenominator ,double dMember,int Digit){
		try {
			BigDecimal bgNum1 = new BigDecimal(dDenominator/dMember);
			BigDecimal bgNum2 = new BigDecimal("1");
			return bgNum1.divide(bgNum2, Digit, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 四舍五入取整数
	 * @param d
	 * @return
	 */
	public int toIntScale(double d){
		 try {
			return new BigDecimal(d).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		} catch (Exception e) {
			// TODO: handle exception
		}
		 return (int)d;
	}
	
	public double toDouble(String str){
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public double toDouble(String str,double rD){
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return rD;
		}
	}
	
	public int toInt(String str,int rI){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return rI;
		}
	}
	
	public int toInt(String str){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public float toFloat(String str){
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public float toFloat(String str,float rF){
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return rF;
		}
	}
}
