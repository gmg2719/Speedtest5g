package cn.nokia.speedtest5g.gis.model;

import java.util.HashMap;

/**
 * LTE 频段选择数据
 * @author zwq
 *
 */
public class LteBandItem {

	//是否全新
	public boolean isAllSelect;
	
	public String keyGroup;
	//排序游标
	public int sortPosition;
	
	//key=频段+频点  value=是否选中
	public HashMap<String, LteBandChild> childMap = new HashMap<>();
	
	public LteBandItem(String WORKPD,String earfcn,String keyGroup,int sortPosition){
		this.sortPosition = sortPosition;
		this.isAllSelect = true;
		this.keyGroup = keyGroup;
		childMap.put(WORKPD + earfcn, new LteBandChild(WORKPD, earfcn));
	}
}
