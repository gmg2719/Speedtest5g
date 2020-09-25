package cn.nokia.speedtest5g.app.uitl;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import org.xclcharts.common.MathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 类型转换
 * @author zwq
 *
 */
public class UtilHandler {

	private static UtilHandler uh = null;

	public synchronized static UtilHandler getInstance(){
		if (uh == null) {
			uh = new UtilHandler();
		}
		return uh;
	}

	/**
	 * 得到单个字的高度
	 * @param paint 画笔
	 * @return 高度
	 */
	public float getPaintFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent);
	}

	/**
	 * @param paint
	 * @param str
	 * @return 返回指定笔和指定字符串的长度
	 */
	public float getFontlength(Paint paint, String str) {
		return paint.measureText(str);
	}

	/**
	 * sp转px
	 * @param size
	 * @return
	 */
	public float getSPtoPx(float size) {
		Resources r;

		if (SpeedTest5g.getContext() == null)
			r = Resources.getSystem();
		else
			r = SpeedTest5g.getContext().getResources();

		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, r.getDisplayMetrics());
	}

	/** 
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 */  
	public int dpTopx(float dpValue) {  
		final float scale = SpeedTest5g.getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);  
	} 

	/** 
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 */  
	public int pxTodp(float pxValue) {  
		final float scale = SpeedTest5g.getContext().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}  

	public double toDouble(String str,double d){
		try {
			return Double.parseDouble(str.trim());
		} catch (Exception e) {
			return d;
		}
	}

	public float toFloat(String str,float f){
		try {
			return Float.parseFloat(str.trim());
		} catch (Exception e) {
			return f;
		}
	}

	public int toInt(String str,int toI){
		try {
			return Integer.parseInt(str.trim());
		} catch (Exception e) {
			return toI;
		}
	}

	public long toLong(String str,long l){
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			// TODO: handle exception
			return l;
		}
	}

	private DecimalFormat df,dfLonLat;
	/**
	 * 转换数字，保留一位小数
	 * @param object
	 * @return
	 */
	public double toDf(Object object){
		if (df == null) {
			df = new DecimalFormat("#.0");
			df.getRoundingMode();
		}
		try {
			return Double.parseDouble(df.format(object));
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}
		return 0;
	}

	/**
	 * 转换数字，保留N位小数
	 * @param object
	 * @param str 填0 ，一个0代表一位小数  2个0代表两位小数
	 * @return
	 */
	public double toDfSum(Object object,String str){
		DecimalFormat dfSumD = new DecimalFormat("#." + str);
		dfSumD.getRoundingMode();
		try {
			return Double.parseDouble(dfSumD.format(object));
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}
		return 0;
	}

	public String toDfStr(Object object,String str){
		DecimalFormat dfSumD = new DecimalFormat("#." + str);
		dfSumD.getRoundingMode();
		try {
			String format = dfSumD.format(object);
			return (format.startsWith(".") ? "0" : "") + format;
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}
		return "0";
	}

    /**
     * 转换数字，保留6位小数
     * @param latOrLon
     * @return
     */
	public double toDfLl(double latOrLon){
		if (dfLonLat == null) {
			dfLonLat = new DecimalFormat("#.000000");
			dfLonLat.getRoundingMode();
		}
		try {
			String format = dfLonLat.format(latOrLon);
			while (format.endsWith("0")) {
				format = format.substring(0, format.length() - 1);
			}
			return Double.parseDouble(format);
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}
		return 0;
	}

	/**
	 * 获取字符串包含多少个匹配字符
	 * @param str
	 * @param ch
	 * @return
	 */
	public int queryCharCount(String str,char ch) {
		// 将字符串转换为字符数组
		char[] chs = str.toCharArray();
		// 定义变量count存储字符串出现次数
		int count = 0;
		for(int i = 0;i < chs.length;i++) {
			if(chs[i] == ch) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 获取某个字符串某个字符的位置
	 * @param str 字符串
	 * @param start 起点
	 * @param ch 匹配字符
	 * @return
	 */
	public int queryCharPosition(String str,int start,char ch) {
		// 将字符串转换为字符数组
		char[] chs = str.toCharArray();
		for(int i = start;i < chs.length;i++) {
			if(chs[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取字符串对应某个字符后的数据
	 * @param str
	 * @param poistion
	 * @param ch
	 * @return
	 */
	public String getToCharStr(String str,int poistion,char ch){
		// 将字符串转换为字符数组
		char[] chs = str.toCharArray();
		// 定义变量count存储字符串出现次数
		int count = 0;
		for(int i = 0;i < chs.length;i++) {
			if(chs[i] == ch) {
				count++;
				if (poistion == 0) {
					return str.substring(0, i);
				}else if (poistion == count) {
					for (int j = i + 1; j < chs.length; j++) {
						if(chs[j] == ch) {
							return str.substring(i + 1, j);
						}
					}
					return str.substring(i + 1);
				}
			}
		}
		return "";
	}

	/**
	 * 获取文件的MD5值
	 * @param file
	 * @return
	 */
	public String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bytesToHexString(digest.digest());
	}

	/**
	 * 获取字节的MD5值
	 * @return
	 */
	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取批次号
	 * @return
	 */
	public String getTestId(){
		String str = Secure.getString(SpeedTest5g.getContext().getContentResolver(), Secure.ANDROID_ID) + TimeUtil.getInstance().getNowTimeYear();
		if (str.length() > 30) {
			str = str.substring(str.length() - 30, str.length());
		}
		return str;
	}

	/**
	 * 隐藏手机号中间四位，显示前3后4个字符
	 * 
	 * @return
	 */
	public String hidePhoneNoMid4(String phoneNoStr) {
		if (!TextUtils.isEmpty(phoneNoStr)) {
			// 去除所有空格
			phoneNoStr = phoneNoStr.replace(" ", "");
			if (phoneNoStr.length() >= 7) {
				phoneNoStr = phoneNoStr.substring(0, 3) + "****" + phoneNoStr.substring(7, phoneNoStr.length());
			}
		}
		return phoneNoStr;
	}

	/**
	 * 设置TextView drawable颜色
	 * 
	 * @param textView
	 * @param colorString
	 *            颜色值，如:"#6ABD20"
	 * @param index
	 *            textView_drawable的位置 0:Left；1:Top；2:Right；3:Bottom
	 */
	public void setDrawableColor(TextView textView, String colorString, int index) {
		try {
			GradientDrawable gradientDrawable = (GradientDrawable) textView.getCompoundDrawables()[index];
			gradientDrawable.setColor(Color.parseColor(colorString));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将速率换算成在y轴上的值
	 * 
	 * @param speed
	 *            速率值-单位Mbps(同Mb/s) 如：80Mbps-表示表示每秒80Mbit= 10MB
	 *            b表示bit（位），B表示Byte（字节）
	 *            1b*8 = 1B
	 *            1B*1024 = 1KB
	 *            1KB*1024 =1MB
	 * @param yPerStep
	 *            y轴每格大小 ，
	 *            yPerStep / (step2 - step1)表示：每格再按区间值均等分，以此类推
	 * @return
	 */
	public double speed2yAxisValue(double speed, double yPerStep) {
		double yAxisValue;
		speed = MathHelper.getInstance().round(speed, 2);
		String[] tickLabelArr = SpeedTest5g.getContext().getResources().getStringArray(R.array.arrSpeedTestTickLabel);
		double[] stepDoubleArr = new double[tickLabelArr.length];
		for (int i = 0; i < tickLabelArr.length; i++) {
			String tempStr = tickLabelArr[i];
			double tempDouble = Double.valueOf(tempStr.substring(0, tempStr.length() - 1));
			if (tempStr.endsWith("K")) {// KB转换MB，除以1024
				stepDoubleArr[i] = tempDouble / 1024D;
			} else if (tempStr.endsWith("M")) {
				stepDoubleArr[i] = tempDouble;
			} else if (tempStr.endsWith("G")) {// GB转换MB，乘以1024
				stepDoubleArr[i] = tempDouble * 1024D;
			}
		}
		double step0 = 0;
		double step1 = stepDoubleArr[0];
		double step2 = stepDoubleArr[1];
		double step3 = stepDoubleArr[2];
		double step4 = stepDoubleArr[3];
		double step5 = stepDoubleArr[4];
		double step6 = stepDoubleArr[5];
		double step7 = stepDoubleArr[6];
		double step8 = stepDoubleArr[7];
		double step9 = stepDoubleArr[8];
		double step10 = stepDoubleArr[9];
		if (speed <= step0) {
			yAxisValue = step0;
		} else if (speed <= step1) {
			yAxisValue = (speed) * yPerStep / step1;
		} else if (speed <= step2) {
			yAxisValue = (speed - step1) * yPerStep / (step2 - step1) + 1 * yPerStep;
		} else if (speed <= step3) {
			yAxisValue = (speed - step2) * yPerStep / (step3 - step2) + 2 * yPerStep;
		} else if (speed <= step4) {
			yAxisValue = (speed - step3) * yPerStep / (step4 - step3) + 3 * yPerStep;
		} else if (speed <= step5) {
			yAxisValue = (speed - step4) * yPerStep / (step5 - step4) + 4 * yPerStep;
		} else if (speed <= step6) {
			yAxisValue = (speed - step5) * yPerStep / (step6 - step5) + 5 * yPerStep;
		} else if (speed <= step7) {
			yAxisValue = (speed - step6) * yPerStep / (step7 - step6) + 6 * yPerStep;
		} else if (speed <= step8) {
			yAxisValue = (speed - step7) * yPerStep / (step8 - step7) + 7 * yPerStep;
		} else if (speed <= step9) {
			yAxisValue = (speed - step8) * yPerStep / (step9 - step8) + 8 * yPerStep;
		} else {
			yAxisValue = (speed - step9) * yPerStep / (step10 - step9) + 9 * yPerStep;
		}
		return yAxisValue;
	}

	/**
	 * 域名解析
	 * 
	 * @param domainName
	 * @return ipAddress
	 * @throws UnknownHostException
	 */
	public String parseDomainName(String domainName) throws UnknownHostException {
		String ipAddress = "";
		String DNS = "114.114.114.114";
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		System.setProperty("sun.net.spi.nameservice.nameservers", DNS);
		InetAddress[] addresses = InetAddress.getAllByName(domainName);
		if (addresses != null && addresses.length > 0) {
			ipAddress = addresses[0].getHostAddress();
		}
		return ipAddress;
	}

	/**
	 * IP地址检验
	 * 
	 * @param ipAddress
	 * @return
	 */
	public boolean isIpAddress(String ipAddress) {
		if(TextUtils.isEmpty(ipAddress)){
			return false;
		}
		String regularExpression = "^(([01]?[0-9]?[0-9]|2([0-4][0-9]|5[0-5]))\\.){3}([01]?[0-9]?[0-9]|2([0-4][0-9]|5[0-5]))$";
		Pattern pattern = Pattern.compile(regularExpression, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(ipAddress);

		return matcher.find();
	}

	/**
	 * ip转换 192.168.2.5 --> *.*.*.5
	 * @param ip
	 * @return
	 */
	public String ipFormat(String ip){
		if(ip == null){
			return null;
		}
		String[] ipArr = ip.split("\\.");
		if(ipArr.length < 4){
			return null;
		}
		return "*.*.*." + ipArr[3];
	}

	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
}
