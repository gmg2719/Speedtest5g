package cn.nokia.speedtest5g.app.bean;

import java.io.Serializable;

import cn.nokia.speedtest5g.app.uitl.UtilHandler;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * ping数据对象
 * 
 * @author xujianjun
 *
 */
public class PingInfo implements Serializable, Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 连接名称（如：百度、淘宝），ip， 域名
	private String name, ip, domainName;
	// 连接时间
	private long times = -2;
	// 连接状态
	private boolean state;
	private String strState = "", strIp; // 状态 中文描述
	// 状态颜色值
	private int colorState;

	public String imgUrl;

	public long shake; //抖动  最大时延-最小时延 
	public float packetLoss; //丢包

	//2测一测 1正在刷新  0常态  状态码
	public static final byte STATUS_0 = (byte)0;
	public static final byte STATUS_1 = (byte)1;
	public static final byte STATUS_2 = (byte)2;
	public byte status = STATUS_0; 

	public String getDomainName() {
		return domainName;
	}

	/**
	 * ping速度转化为中文描述
	 */
	public void toStrState() {
		if (state) {
			status = STATUS_0;

			//读取配置对比
			//			BeanBaseConfig beanBaseConfig = SpeedTestDataSet.mBeanBaseConfig;

			// 50毫秒内
			if(times <= 50){
				setStrState("优秀");
				setColorState(Color.parseColor("#529d07"));
			}else if (times <= 100) {
				setStrState("良好");
				setColorState(Color.parseColor("#199eed"));
			} else if (times <= 100000) {
				setStrState("一般");
				setColorState(Color.parseColor("#f58916"));
			} else {
				setStrState("极差");
				setColorState(Color.parseColor("#ff4646"));
			}
		} else {
			status = STATUS_2;
			if (times > -1) {
				setStrState("失败");
				setColorState(Color.RED);
			}
		}
		if (times == -3) {
			setStrIp(TextUtils.isEmpty(getDomainName()) ? getIp() : getDomainName());
		} else if (times == -2) {
			if (!state) {
				setStrState("失败");
				setColorState(Color.RED);
			}
			setStrIp(TextUtils.isEmpty(getDomainName()) ? getIp() : getDomainName());

		} else if (times == -1) {
			setStrIp("正在测试...");

		} else {
			setStrIp("延时:" + getTimes() + "ms");
		}
	}

	public void toStrState2() {
		if (state) {
			status = STATUS_0;
			// 50毫秒内
			if(times <= 50){
				setStrState("优秀");
				setColorState(Color.parseColor("#529d07"));
			}else if (times <= 100) {
				setStrState("良好");
				setColorState(Color.parseColor("#199eed"));
			} else if (times <= 100000) {
				setStrState("一般");
				setColorState(Color.parseColor("#f58916"));
			} else {
				setStrState("极差");
				setColorState(Color.parseColor("#ff4646"));
			}
		} else {
			status = STATUS_2;
			if (times > -1) {
				setStrState("失败");
				setColorState(Color.RED);
			}
		}

		if (times == -2 && state) {
			setStrIp("");
		} else if (times == -2 && !state) {
			setStrIp("失败");
		} else if (times == -1) {
			setStrIp("正在测试...");
		} else {
			setStrIp(getTimes() + "ms");
		}
	}

	public String getStrIp() {
		return strIp;
	}

	public void setStrIp(String strIp) {
		this.strIp = strIp;
	}

	public int getColorState() {
		return colorState;
	}

	public void setColorState(int colorState) {
		this.colorState = colorState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		if (UtilHandler.getInstance().isIpAddress(ip)) {
			this.ip = ip;

		} else {// 如果不是ip，则是域名
			this.domainName = ip;
		}
	}

	public String getStrState() {
		return strState;
	}

	public void setStrState(String strState) {
		this.strState = strState;
	}

	public long getTimes() {
		return times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(ip);
		dest.writeString(domainName);
		dest.writeLong(times);
		dest.writeString(strState);
		dest.writeString(imgUrl);
		dest.writeInt(colorState);
		dest.writeByte(status);
	}

	public PingInfo(){}

	private PingInfo(Parcel source) {
		name = source.readString();
		ip = source.readString();
		domainName = source.readString();
		times = source.readLong();
		strState = source.readString();
		imgUrl = source.readString();
		colorState = source.readInt();
		status = source.readByte();
	}

	public static final Creator<PingInfo> CREATOR = new Creator<PingInfo>() {

		@Override
		public PingInfo createFromParcel(Parcel source) {
			return new PingInfo(source);
		}

		@Override
		public PingInfo[] newArray(int size) {
			return new PingInfo[size];
		}
	};
}
