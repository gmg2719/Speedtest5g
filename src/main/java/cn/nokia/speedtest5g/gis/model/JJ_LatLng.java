package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;
/**
 * 地图打点经纬度传值
 * @author xujianjun
 *
 */
@SuppressWarnings("serial")
public class JJ_LatLng implements Serializable{
	private double latitude;
	private double longitude;
	public double getLat() {
		return latitude;
	}
	public double getLng() {
		return longitude;
	}
	public void setLat(double lat) {
		this.latitude = lat;
	}
	public void setLng(double lng) {
		this.longitude = lng;
	}
}
