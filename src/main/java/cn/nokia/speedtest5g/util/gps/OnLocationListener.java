package cn.nokia.speedtest5g.util.gps;

/**
 * 经纬度回调事件
 * @author zwq
 *
 */
public interface OnLocationListener {

	//定位返回
	void onLocation(LocationBean location);
	
	/**
	 * 地址解析，文件解密等回调
	 * @param status 100：经纬度逆地址
	 * 				 101：根据权限返回文件地址
	 * 				 102：解密完成
	 * @param obj
	 */
	void onAnalysis(int status, Object... obj);
}
