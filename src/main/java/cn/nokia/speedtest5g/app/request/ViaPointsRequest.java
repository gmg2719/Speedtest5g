package cn.nokia.speedtest5g.app.request;

import java.util.List;
/**
 * 导航途径点
 * @author zwq
 */
public class ViaPointsRequest {

	private List<ViaPointsItem> viaPoints;
	
	public ViaPointsRequest(List<ViaPointsItem> viaPoints){
		this.viaPoints = viaPoints;
	}

	public List<ViaPointsItem> getViaPoints() {
		return viaPoints;
	}

	public void setViaPoints(List<ViaPointsItem> viaPoints) {
		this.viaPoints = viaPoints;
	}
}
