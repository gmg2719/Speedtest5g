package cn.nokia.speedtest5g.util.gps;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LocationBean implements Serializable{

	/**
	 * 定位来源
	 * default = "null"
	 * null, gps, network
	 */
	public String Provider = "null";
	/**
	 * 纬度
	 * default = "0"
	 */
	public double Latitude = 0;
	/**
	 * 经度
	 * default = "0"
	 */
	public double Longitude = 0;
	/**
	 * 精度
	 * default = "-1"
	 */
	public float Accuracy = -1;
	/**
	 * 海拔
	 * default = "-1"
	 */
	public double Altitude = -1;
	/**
	 * 时速
	 */
	public float speed;
	/**
	 * 旋转角度 GPS有效
	 */
	public float Direction;
}
