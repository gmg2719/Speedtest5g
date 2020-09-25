package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class LinquInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public String neighbor_ci = "";
	public String line_color = "";
	public String line_type = "";
	// gsm或者lte类型
	public String net_type = "";

	public List<LatLng> pointsLst;
	public float disrection;
	public LatLng latLng;

	// 是否需要在地图图层画出改邻区的扇形
	public boolean isNeedDraw = false;
	// 小区名称
	public String enbaj02 = "";
	// 切换出成功次数
	public String succ_num = "";

	public String distance = "";// 与原小区距离
	public String antDirctAngle = "";// 方位角
	public String electricityAngle = "";// 电下倾角
	public String bcch = ""; // 频点
	public String anteHeight = "";// 天线高度
	public String mechanineAngle = "";// 机械下倾角
	public String pci = "";

	public String getType() {
		if (neighbor_ci.equals("")) {
			return "";
		}
		String ttString = neighbor_ci.replace("-", ",");
		String[] num = ttString.split(",");

		if (num == null || num.length <= 1) {
			return "gsm";
		} else {
			return "lte";
		}
	}
}