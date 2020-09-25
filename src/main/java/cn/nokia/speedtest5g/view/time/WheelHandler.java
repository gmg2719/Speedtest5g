package cn.nokia.speedtest5g.view.time;

import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.ArrayList;
import java.util.Calendar;

import cn.nokia.speedtest5g.R;

public class WheelHandler implements OnWheelCallback{

	private static WheelHandler handler = null;
	/**
	 * 是否更新日数组-默认更新（一些自定义日期数组无需更新可设置为false）
	 */
	private boolean isUpdateDayArr = true;
	/**
	 * 当前View
	 */
	private View mView;
	
	public static synchronized WheelHandler getHandler(){
		if (handler == null) {
			handler = new WheelHandler();
		}
		mArrYmdDate = null;
		return handler;
	}
	
	public WheelHandler setUpdateDayArr(boolean isUpdateDayArr) {
		this.isUpdateDayArr = isUpdateDayArr;
		return handler;
	}



	/**
	 * 初始化控件
	 * @param id 控件ID
	 * @param strContents 内容
	 * @param position 游标
	 * @param cyclic 是否循环
	 */
	private void initWheel(int id,String[] strContents,int position,boolean cyclic) {
        WheelView wheel = (WheelView) mView.findViewById(id);
        if (wheel != null) {
        	 wheel.setAdapter(new StrericWheelAdapter(strContents));
             wheel.setCurrentItem(position);
             wheel.setCyclic(false);//不循环
             wheel.setInterpolator(new AnticipateOvershootInterpolator());
             wheel.setListener(WheelHandler.this);
		}
    }
	
	/**
	 *  描述	：获取当前值
	 */
	 public String getWheelValue(int id) {
		 String currentItemValue = ((WheelView)mView.findViewById(id)).getCurrentItemValue();
	     return currentItemValue;
	 }
	 
	 /**
	 *  描述	：获取当前值游标
	 */
	 public int getWheelPosition(int id) {
	     return ((WheelView)mView.findViewById(id)).getCurrentItem();
	 }
	 
	 /**
	 * 其他内容
	 * @param v
	 * @param strContents
	 */
	public void initOther(View v,String[] strContents,boolean cyclic){
		this.mView = v;
		initWheel(R.id.wheel_other, strContents, 0, cyclic);
	}
	//---------------------------处理时间相关控件Start------------------------------------
	public void initYear(View v, String[] strContents, int position, boolean cyclic) {
		this.mView = v;
		initWheel(R.id.wheel_year, strContents, position, cyclic);
	}
	
	public void initMounth(View v, String[] strContents, int position, boolean cyclic) {
		this.mView = v;
		initWheel(R.id.wheel_month, strContents, position, cyclic);
	}
	
	public void initDay(View v, String[] strContents, int position, boolean cyclic) {
		this.mView = v;
		initWheel(R.id.wheel_day, strContents, position, cyclic);
	}
	
//	public void initHour(View v, String[] strContents, int position, boolean cyclic) {
//		this.mView = v;
//		initWheel(R.id.wheel_hour, strContents, position, cyclic);
//	}
//
//	public void initMin(View v, String[] strContents, int position, boolean cyclic) {
//		this.mView = v;
//		initWheel(R.id.wheel_minute, strContents, position, cyclic);
//	}
//
//	public void initSec(View v, String[] strContents, int position, boolean cyclic) {
//		this.mView = v;
//		initWheel(R.id.wheel_second, strContents, position, cyclic);
//	}
//
//	public void initAMPM(View v, String[] strContents, int position, boolean cyclic) {
//		this.mView = v;
//		initWheel(R.id.wheel_am_pm, strContents, position, cyclic);
//	}
	//---------------------------处理时间相关控件End------------------------------------
	 
	private int dateOfMonth;
	/**
	 * 显示年，月，日
	  */
	public void initTimeA(View v){
		this.mView = v;
		ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH); 
		int day = ca.get(Calendar.DAY_OF_MONTH);
		ca.set(Calendar.YEAR,year); 
		ca.set(Calendar.MONTH, month);//Java月份才0开始算 
		dateOfMonth = ca.getActualMaximum(Calendar.DATE); 
		String[] arrYear =  new String[4];
		int p = 0;
		//年
		for (int i = 3; i >= 0; i--) {
			arrYear[p] = String.valueOf(year-i);
			p++;
		}
		//年
		initWheel(R.id.wheel_year, arrYear, 3, true);
		//月
		initWheel(R.id.wheel_month, new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"},month, true);
		
		String[] arrDay = new String[dateOfMonth];
		for (int i = 0; i < dateOfMonth; i++) {
			if (i<9) {
				arrDay[i] = "0" + (i + 1);
			}else {
				arrDay[i] = String.valueOf(i + 1);
			}
		}
		initWheel(R.id.wheel_day, arrDay,day - 1, true);
	}
	
	/**
	 * 显示年，月，日（默认开始时间为明天）
	 */
	public void initTimeStartTomorrow(View v) {
		this.mView = v;
		ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		ca.set(Calendar.YEAR, year);
		ca.set(Calendar.MONTH, month);// Java月份才0开始算
		dateOfMonth = ca.getActualMaximum(Calendar.DATE);
		String[] arrYear = new String[15];
		// 年
		for (int i = 0; i < arrYear.length; i++) {
			arrYear[i] = String.valueOf(year + i);
		}
		initWheel(R.id.wheel_year, arrYear, 0, true);
		// 月
		initWheel(R.id.wheel_month, new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }, month, true);
		// 日
		String[] arrDay = new String[dateOfMonth];
		for (int i = 0; i < dateOfMonth; i++) {
			if (i < 9) {
				arrDay[i] = "0" + (i + 1);
			} else {
				arrDay[i] = String.valueOf(i + 1);
			}
		}
		initWheel(R.id.wheel_day, arrDay, day, true);
	}
	
	private static ArrayList<Year> mArrYmdDate;
	/**
	 * 显示最近多少天内的日期（年，月，日）
	 * @param v
	 * @param startDay 起始天数
	 * @param maxDay 显示最近天数
	 */
	public void initTimeLately(View v,int startDay,int maxDay){
		this.mView = v;
		ca = Calendar.getInstance();
		int year,month,day;
		boolean isExit;
		mArrYmdDate  = new ArrayList<Year>();
		Year mYear   = null;
		Month mMonth = null;
		if (startDay != 0) {
			ca.add (Calendar.DATE, startDay);
		}
		for (int i = 0; i < maxDay; i++) {
			year = ca.get(Calendar.YEAR);
			month = ca.get(Calendar.MONTH) + 1; 
			day = ca.get(Calendar.DAY_OF_MONTH);
			//年
			isExit = false;
			for (Year yearItem : mArrYmdDate) {
				if (String.valueOf(year).equals(yearItem.year)) {
					mYear  = yearItem;
					isExit = true;
					break;
				}
			}
			if (!isExit) {
				mYear = new Year();
				mYear.year = String.valueOf(year);
				mYear.arrMonth = new ArrayList<Month>();
				mArrYmdDate.add(mYear);
			}
			//月
			isExit = false;
			for (Month monthItem : mYear.arrMonth) {
				if (month == Integer.parseInt(monthItem.month)) {
					mMonth = monthItem;
					isExit = true;
					break;
				}
			}
			if (!isExit) {
				mMonth = new Month();
				mMonth.month = month < 10 ? "0" + month : String.valueOf(month);
				mMonth.arrDay = new ArrayList<String>();
				mYear.arrMonth.add(mMonth);
			}
			//日
			mMonth.arrDay.add(day < 10 ? "0" + day : String.valueOf(day));
			ca.add (Calendar.DATE, -1);
		}
		//年
		String[] arrYear = new String[mArrYmdDate.size()];
		//月
		String[] arrMonth = null;
		//日
		String[] arrDay = null;
		for (int i = 0; i < arrYear.length; i++) {
			mYear = mArrYmdDate.get(arrYear.length - 1 - i);
			arrYear[i] = mYear.year;
			if (i == arrYear.length - 1) {
				arrMonth = new String[mYear.arrMonth.size()];
				for (int j = 0; j < arrMonth.length; j++) {
					mMonth = mYear.arrMonth.get(arrMonth.length -1 - j);
					arrMonth[j] = mMonth.month;
					if (j == arrMonth.length -1) {
						arrDay = new String[mMonth.arrDay.size()];
						for (int j2 = 0; j2 < arrDay.length; j2++) {
							arrDay[j2] = mMonth.arrDay.get(arrDay.length - 1 - j2);
						}
					}
				}
			}
		}
		initWheel(R.id.wheel_year, arrYear,arrYear.length - 1, true);
		initWheel(R.id.wheel_month, arrMonth,arrMonth.length - 1, true);
		initWheel(R.id.wheel_day, arrDay,arrDay.length - 1, true);
	}
	
	private class Year{
		public String year;
		public ArrayList<Month> arrMonth;
	}
	
	private class Month{
		public String month;
		public ArrayList<String> arrDay;
	}
	 
	private Calendar ca = Calendar.getInstance();
	@Override
	public void onCall(int id,String value,int position) {
        if (id == R.id.wheel_year) {
            if (mArrYmdDate != null) {
                //月
                String[] arrMonth = null;
                //日
                String[] arrDay = null;
                for (Year yearItem : mArrYmdDate) {
                    if (yearItem.year.equals(value)) {
                        Month mMonth;
                        arrMonth = new String[yearItem.arrMonth.size()];
                        for (int j = 0; j < arrMonth.length; j++) {
                            mMonth = yearItem.arrMonth.get(arrMonth.length - 1 - j);
                            arrMonth[j] = mMonth.month;
                            if (j == arrMonth.length - 1) {
                                arrDay = new String[mMonth.arrDay.size()];
                                for (int j2 = 0; j2 < arrDay.length; j2++) {
                                    arrDay[j2] = mMonth.arrDay.get(arrDay.length - 1 - j2);
                                }
                            }
                        }
                        break;
                    }
                }
                initWheel(R.id.wheel_month, arrMonth, arrMonth.length - 1, true);
                initWheel(R.id.wheel_day, arrDay, arrDay.length - 1, true);
                return;
            }
            String month = getWheelValue(R.id.wheel_month);
            ca.set(Calendar.YEAR, Integer.parseInt(value));
            ca.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            if (isUpdateDayArr) {
                setDay();
            }
        } else if (id == R.id.wheel_month) {
            String year = getWheelValue(R.id.wheel_year);
            if (mArrYmdDate != null) {
                //日
                String[] arrDay = null;
                for (Year yearItem : mArrYmdDate) {
                    if (yearItem.year.equals(year)) {
                        for (Month monthItem : yearItem.arrMonth) {
                            if (monthItem.month.equals(value)) {
                                arrDay = new String[monthItem.arrDay.size()];
                                for (int j2 = 0; j2 < arrDay.length; j2++) {
                                    arrDay[j2] = monthItem.arrDay.get(arrDay.length - 1 - j2);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                initWheel(R.id.wheel_day, arrDay, arrDay.length - 1, true);
                return;
            }
            ca.set(Calendar.YEAR, Integer.parseInt(year));
            ca.set(Calendar.MONTH, Integer.parseInt(value) - 1);
            if (isUpdateDayArr) {
                setDay();
            }
        }
	}
	
	private void setDay() {
		String yearStr = getWheelValue(R.id.wheel_year);
		String monthStr = getWheelValue(R.id.wheel_month);
		int year = Integer.valueOf(yearStr);
		int month = Integer.valueOf(monthStr) - 1;
		ca.set(Calendar.YEAR, year);
		ca.set(Calendar.MONTH, month);
		// 但当前日期为29、30、31日的时候，切换到2月，上面一行代码ca.set(Calendar.MONTH,
		// month)这个设置是无效的。
		// ca.get(Calendar.MONTH)取出来值=2（=1才是对的），所以dateOfMonth值始终是31。
		// 所以通过以下判断,重新初初始化日期，来解决上述情况下，切到2月份的时候，日期不会变化的问题
		if (month != ca.get(Calendar.MONTH)) {
			ca = null;
			ca = Calendar.getInstance();
			ca.set(Calendar.YEAR, year);
			ca.set(Calendar.MONTH, month);
			ca.set(Calendar.DAY_OF_MONTH, 28);
		}
		int dateOfMonth = ca.getActualMaximum(Calendar.DATE);
		String[] arrDay = new String[dateOfMonth];
		for (int i = 0; i < dateOfMonth; i++) {
			if (i < 9) {
				arrDay[i] = "0" + (i + 1);
			} else {
				arrDay[i] = String.valueOf(i + 1);
			}
		}
		String wheelValue = getWheelValue(R.id.wheel_day);
		int parseInt = Integer.parseInt(wheelValue);
		if (arrDay.length < parseInt) {
			parseInt = arrDay.length - 1;
		} else {
			parseInt--;
		}
		initWheel(R.id.wheel_day, arrDay, parseInt, true);
	}
	
}
