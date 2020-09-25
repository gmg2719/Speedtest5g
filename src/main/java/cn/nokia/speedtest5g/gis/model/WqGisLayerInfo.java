package cn.nokia.speedtest5g.gis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class WqGisLayerInfo  implements Serializable{

	private List<WqGisLayer> list = new ArrayList<WqGisLayer>();
	
	public int px, py, cx, cy;
	public double lat, lng;
	public int n;
	
	
	public WqGisLayerInfo(int px, int py, int cx, int cy) {
		this.px = px;
		this.py = py;
		this.cx = cx;
		this.cy = cy;
	}
	
	public WqGisLayerInfo() {
	}
	
	public void addItem(WqGisLayer item) {
		list.add(item);
		n = list.size();
	}

	public List<WqGisLayer> getList() {
		return list;
	}

	public void setList(List<WqGisLayer> list) {
		this.list = list;
	}
}
