package cn.nokia.speedtest5g.gis.model;

public class LteBandChild {
	
	public boolean isChildSelect;
	//频段
	public String WORKPD;
	//频点
	public String earfcn;
	
	public LteBandChild(String WORKPD,String earfcn){
		this.WORKPD = WORKPD;
		this.earfcn = earfcn;
		isChildSelect = true;
	}
}
