package cn.nokia.speedtest5g.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 获取时间
 * @author zwq
 *
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	private static TimeUtil tu = null;
	//每次调用的次数
	private static int frequency = 0;

	public synchronized static TimeUtil getInstance(){
		frequency = 0;
		if (tu == null) {
			tu = new TimeUtil();
		}
		return tu;
	}

	/**
	 * 获取MMddHHmmssSSS
	 * @return
	 */
	public String getNowTime(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("MMddHHmmssSSS");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTime();
		}
	}

	/**
	 * 获取MMddHHmmss
	 * @return
	 */
	public String getNowTimeN(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("MMddHHmmss");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTimeN();
		}
	}

	/**
	 * 获取当前时间解析成对应的格式
	 * @param formatType 如：yyyyMMddHHmmssSSS
	 * @return
	 */
	public String getNowTime(String formatType){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern(formatType);
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTime(formatType);
		}
	}

	/**
	 * 获取yyyyMMddHHmmssSSS
	 * @return
	 */
	public String getNowTimeYear(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyyMMddHHmmssSSS");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTimeYear();
		}
	}

	/**
	 * 获取yyyyMMddHHmmss
	 * @return
	 */
	public String getNowTimeYear_second(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyyMMddHHmmss");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTimeYear();
		}
	}

	/**
	 * 获取yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public String getNowTimeGis(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTimeGis();
		}
	}

	/**
	 * 获取yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public String getNowTimeSS(){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
			return sdf.format(System.currentTimeMillis());
		} catch (Exception e) {
			return getNowTimeSS();
		}
	}

	/**
	 * 获取指定时间是星期几
	 * @param time
	 * @return
	 */
	public String getDayOfWeek(long time) {
		Calendar calendar = Calendar.getInstance();
		if (time == 0) {
			calendar.setTimeInMillis(System.currentTimeMillis());
		} else {
			calendar.setTimeInMillis(time);
		}
		String[] arrWeek = SpeedTest5g.getContext().getResources().getStringArray(R.array.arrWeek);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return arrWeek[dayOfWeek];
	}

	/**
	 * 获取时间 自定义格式 如yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public String getNowTimeCustom(long time,String type){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern(type);
			return sdf.format(time);
		} catch (Exception e) {
			return getNowTimeGis();
		}
	}

	/**
	 * 获取当前时间 年-月-日 时：分：秒
	 * @return
	 */
	public String getNowTimeSS(long times){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
			return sdf.format(times);
		} catch (Exception e) {
			return getNowTimeSS(times);
		}
	}

	/**
	 * 获取当前时间 年-月-日
	 * yyyy-MM-dd
	 * @return
	 */
	public String getNowTime(long times){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd");
			return sdf.format(times);
		} catch (Exception e) {
			return getNowTime(times);
		}
	}

	/**
	 * 获取 时：分：秒
	 * HH:mm:ss
	 * @return
	 */
	public String getTimeH(long times){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("HH:mm:ss");
			return sdf.format(times);
		} catch (Exception e) {
			return getTimeH(times);
		}
	}

	/**
	 * 获取 HH:mm:ss
	 * @param time
	 * @return
	 */
	public String Long2Time(long time) {
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			sdf.applyPattern("HH:mm:ss");
			return sdf.format(time);
		} catch (Exception e) {
			return Long2Time(time);
		}
	}

	/**
	 * 获取   yyyy年MM月dd日 E HH时mm分ss秒
	 * @param time
	 * @return
	 */
	public String Long2AllInfo_CN(long time) {
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy年MM月dd日 E HH时mm分ss秒");
			return sdf.format(time);
		} catch (Exception e) {
			return Long2AllInfo_CN(time);
		}
	}

	/**
	 * yyyy年M月d日
	 * @param time
	 * @return
	 */
	public String Long2String(long time) {
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
			return sdf.format(time);
		} catch (Exception e) {
			return Long2String(time);
		}
	}

	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	private Date stringToDate(String strTime, String formatType)
			throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType,Locale.CHINA);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	// strTime要转换的String类型的时间
	// formatType时间格式 yyyy-MM-dd HH:mm:ss
	// strTime的时间格式和formatType的时间格式必须相同
	public long stringToLong(String strTime){
		frequency++;
		if (frequency >= 3) {
			return 0;
		}
		try {
			Date date = stringToDate(strTime, "yyyy-MM-dd HH:mm:ss"); // String类型转成date类型
			if (date == null) {
				return 0;
			} else {
				long currentTime = date.getTime(); // date类型转成long类型
				return currentTime;
			}
		} catch (Exception e) {
			return stringToLong(strTime);
		}
	}

	/**
	 * @param strTime
	 * @param strType yyyy-MM-dd HH:mm:ss  / yyyy-MM-dd
	 * @return
	 */
	public long stringToLong(String strTime,String strType){
		frequency++;
		if (frequency >= 3) {
			return 0;
		}
		try {
			Date date = stringToDate(strTime, strType); // String类型转成date类型
			if (date != null) {
				return date.getTime(); // date类型转成long类型
			}
		} catch (Exception e) {

		}
		return stringToLong(strTime, strType);
	}

	private Calendar mCalendar;
	/**
	 * 取当前时间加减day天时间
	 * @param day
	 * @return
	 */
	public String getDataTime(int day) {
		frequency++;
		if (frequency >= 3) {
			return getNowTime(System.currentTimeMillis());
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd");
			mCalendar = Calendar.getInstance();
			mCalendar.add (Calendar.DATE, day);
			return sdf.format(mCalendar.getTime());
		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getDataTime(day);
		}
	}

	/**
	 * 获取本周周一时间
	 * @param time
	 * @param applyPattern 格式：yyyy-MM-dd
	 * @return
	 */
	public String getThisWeekMonday(String time,String applyPattern) {  
		if (TextUtils.isEmpty(time)) {
			return "";
		}
		try {
			Calendar cal = Calendar.getInstance();  
			cal.setTime(getStrToDate(time, applyPattern));  
			// 获得当前日期是一个星期的第几天  
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);  
			if (1 == dayWeek) {  
				cal.add(Calendar.DAY_OF_MONTH, -1);  
			}  
			// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
			cal.setFirstDayOfWeek(Calendar.MONDAY);  
			// 获得当前日期是一个星期的第几天  
			int day = cal.get(Calendar.DAY_OF_WEEK);  
			// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值  
			cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);  
			return getDateToStr(cal.getTime(),applyPattern);  
		} catch (Exception e) {
			return "";
		}
	}  

	/**
	 * date转字符
	 * @param date
	 * @return
	 */
	private String getDateToStr(Date date,String applyPattern) throws Exception{
		frequency++;
		if (frequency >= 3 || date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(applyPattern);
		return sdf.format(date);
	}

	/**
	 * 时间转date格式
	 * @param time
	 * @param applyPattern
	 * @return
	 */
	private Date getStrToDate(String time,String applyPattern){
		frequency++;
		if (frequency >= 3) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(applyPattern);
			return sdf.parse(time);
		} catch (Exception e) {
			return getStrToDate(time,applyPattern);
		}
	}

	/**
	 * 取当前时间加减day天时间
	 * @param day
	 * @return
	 */
	public long getDataTimeToLong(int day) {
		frequency++;
		if (frequency >= 3) {
			return 0;
		}
		try {
			mCalendar = Calendar.getInstance();
			mCalendar.add (Calendar.DATE, day);
			return mCalendar.getTimeInMillis();
		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getDataTimeToLong(day);
		}
	}

	/**
	 * 取当前时间加减day天时间
	 * @param day
	 * @return
	 */
	public String getDataTime(int day,String timeType) {
		frequency++;
		if (frequency >= 3) {
			return getNowTime(System.currentTimeMillis());
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern(timeType);
			mCalendar = Calendar.getInstance();
			mCalendar.add (Calendar.DATE, day);
			return sdf.format(mCalendar.getTime());
		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getDataTime(day,timeType);
		}
	}

	/**
	 * 取T-day且不跨月的日期，若跨月则取当月第一天，如:当天是5月2日，取值T-2日期为2018年4月30日，跨月了则取2018年5月1日
	 * 
	 * @param days
	 * @param timeType
	 * @return
	 */
	public String getMinusDaysTime(int days, String timeType) {
		frequency++;
		if (frequency >= 3) {
			return getNowTime(System.currentTimeMillis());
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern(timeType);
			mCalendar = Calendar.getInstance();
			// 当前月份
			int currentMonth = mCalendar.get(Calendar.MONTH);
			// T-day后的月份
			mCalendar.add(Calendar.DATE, days);
			int minusMonth = mCalendar.get(Calendar.MONTH);
			Date date = null;
			if (minusMonth < currentMonth) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, 0);
				c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
				date = c.getTime();

			} else {
				date = mCalendar.getTime();
			}
			return sdf.format(date);

		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getMinusDaysTime(days, timeType);
		}
	}

	/**
	 * 取指定时间加减day天时间
	 * @param time 时间
	 * @param day
	 * @param applyPattern
	 * @return
	 */
	public String getDateTime(String time,int day,String applyPattern) {
		frequency++;
		if (frequency >= 3) {
			return getNowTime(System.currentTimeMillis());
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern(applyPattern);
			mCalendar = Calendar.getInstance();
			mCalendar.setTime(getStrToDate(time, applyPattern));
			mCalendar.add (Calendar.DATE, day);
			return sdf.format(mCalendar.getTime());
		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getDateTime(time,day,applyPattern);
		}
	}

	/**
	 * 获取时间格式是否错误，若错误取上一次时间+1秒
	 * @param nowTimes
	 * @param lastTime
	 * @return
	 */
	public String isYesTims(String nowTimes,String lastTime){
		SimpleDateFormat sdf = new SimpleDateFormat();
		String strTimes;
		try {
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			strTimes = sdf.format(getTimeLast(nowTimes));
			if (strTimes.endsWith(".000")) {
				strTimes = strTimes.substring(0, strTimes.length() - 4);
			}
			return strTimes;
		} catch (Exception e) {
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			Date timeLast = getTimeLast(lastTime);
			if (timeLast == null) {
				strTimes = sdf.format(System.currentTimeMillis());
				if (strTimes.endsWith(".000")) {
					strTimes = strTimes.substring(0, strTimes.length() - 4);
				}
				return strTimes;
			}else {
				mCalendar = Calendar.getInstance();
				mCalendar.add (Calendar.SECOND, 1);
				strTimes = sdf.format(timeLast);
				if (strTimes.endsWith(".000")) {
					strTimes = strTimes.substring(0, strTimes.length() - 4);
				}
				return strTimes;
			}
		}
	}

	private Date getTimeLast(String lastTime){
		frequency++;
		if (frequency >= 3) {
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");
			return sdf.parse(lastTime);
		} catch (Exception e) {
			return getTimeLast(lastTime);
		}
	}

	/**
	 * 
	 * @param time
	 * @param applyPattern 如：yyyy-MM-dd HH:mm:ss.SSS
	 * @return
	 */
	public String getToTime(long time,String applyPattern){
		frequency++;
		if (frequency >= 3) {
			return "";
		}
		try {
			Thread.sleep(1);
			SimpleDateFormat sdf = new SimpleDateFormat(applyPattern);
			return sdf.format(time);
		} catch (Exception e) {
			return getToTime(time,applyPattern);
		}
	}

	/**
	 * 取startTime时间加1秒
	 * @param startTime
	 * @return
	 */
	public String getTraverseTime(String startTime) {
		frequency++;
		if (frequency >= 3) {
			return "00:00:00";
		}
		try {
			if (TextUtils.isEmpty(startTime)) {
				startTime = "00:00:00";
			}
			SimpleDateFormat sdf = new SimpleDateFormat();
			if (mCalendar == null) {
				mCalendar = Calendar.getInstance();
			}
			sdf.applyPattern("HH:mm:ss");
			mCalendar.setTime(sdf.parse(startTime));
			mCalendar.add (Calendar.SECOND, 1);
			return sdf.format(mCalendar.getTime());
		} catch (Exception e) {
			WybLog.syso("错误getTraverseTime:" + e.getMessage());
			return getTraverseTime(startTime);
		}
	}

	/**
	 * 从00开始加N秒后
	 * @param second
	 * @return
	 */
	public String getTraverseTimeSum(int second) {
		frequency++;
		if (frequency >= 3) {
			return "00:00:00";
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			if (mCalendar == null) {
				mCalendar = Calendar.getInstance();
			}
			sdf.applyPattern("HH:mm:ss");
			mCalendar.setTime(sdf.parse("00:00:00"));
			mCalendar.add (Calendar.SECOND, second);
			return sdf.format(mCalendar.getTime());
		} catch (Exception e) {
			WybLog.syso("错误:" + e.getMessage());
			return getTraverseTimeSum(second);
		}
	}

	/**
	 * 时间转分钟  
	 * @param time HH:mm:ss
	 * @return
	 */
	public int toMinute(String time){
		int minute = 0;
		if (!TextUtils.isEmpty(time)) {
			String[] split = time.split(":");
			if (split.length >= 3) {
				minute = UtilHandler.getInstance().toInt(split[0], 0) * 60 + 
						UtilHandler.getInstance().toInt(split[1], 0) + 
						(UtilHandler.getInstance().toInt(split[2], 0) > 0 ? 1 : 0);
			}
		}
		return minute;
	}

	/**
	 * 短信验证登入免验期剩余天数
	 * 
	 * @param currentTimeMillis
	 *            当前时间戳
	 * @param lastTimeMillis
	 *            最近一次记录的时间戳
	 * @return 返回短信验证登入免验期剩余天数
	 */
	public int getDaysLeft(long currentTimeMillis, long lastTimeMillis, int limitDays) {
		long limitTimeMillis = limitDays * 24 * 60 * 60 * 1000;
		int diffDays = (int) ((limitTimeMillis - currentTimeMillis + lastTimeMillis) / 1000 / 60 / 60 / 24) + 1;
		return (int) diffDays;
	}

	/**
	 * 判断当前时间和记录时间，是否过了limitDays天
	 * 
	 * @param currentTimeMillis
	 *            当前时间戳
	 * @param lastTimeMillis
	 *            最近一次记录的时间戳
	 * @param limitDays
	 *            限制天数
	 * @return true-表示超出期限，false-表示未超
	 */
	public boolean isOverDays(long currentTimeMillis, long lastTimeMillis, int limitDays) {
		long limitTimeMillis = limitDays * 24 * 60 * 60 * 1000;
		return currentTimeMillis - lastTimeMillis > limitTimeMillis;
	}

	/**
	 * 1毫秒
	 */
	public long ONE_MILLISECOND = 1;
	/**
	 * 1秒
	 */
	public long ONE_SECOND = 1000 * ONE_MILLISECOND;
	/**
	 * 1分钟
	 */
	public long ONE_MINUTE = 60 * ONE_SECOND;
	/**
	 * 1小时
	 */
	public long ONE_HOUR = 60 * ONE_MINUTE;
	/**
	 * 1天
	 */
	public long ONE_DAY = 24 * ONE_HOUR;

	/**
	 * @param currentTime
	 * @return
	 */
	public long getCurrDayBeginMilli(long currentTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currentTime);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
}
