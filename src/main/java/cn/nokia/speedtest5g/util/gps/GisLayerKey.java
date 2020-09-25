package cn.nokia.speedtest5g.util.gps;

/**
 * 地图图层相关处理
 * 1.DbCreatOrUpdate类型插入菜单数据
 * 2.arrays-arrGisMenuName、arrGisMenuType配置菜单
 * 3.string_code 配置权限
 * 4.MyToSpile类处理权限
 * @author zwq
 *
 */
public class GisLayerKey {
	
	//-------------------------type--------------------------------------
	/**
	 * LTE RRU
	 */
	public static final int KEY_LAYER_TYPE_LTE_RRU = 19;
	/**
	 * 共站信息
	 */
	public static final int KEY_LAYER_TYPE_GZXX = 25;
	/**
	 * 5G小区
	 */
	public static final int KEY_LAYER_TYPE_5G = 26;
	/**
	 * nb图层
	 */
	public static final int KEY_LAYER_TYPE_NB = 27;
	/**
	 * 空间退服告警
	 */
	public static final int KEY_LAYER_TYPE_KJTFGJ = 28;
	/**
	 * 应急盒子-网桥
	 */
	public static final int KEY_LAYER_TYPE_BOX_BRIDGE = 29;
	/**
	 * 高铁---旧高铁为13
	 */
	public static final int KEY_LAYER_TYPE_GAO_TIE = 30;
	
	
	//--------------------------keyId-------------------------------------
	/**
	 * 5G小区
	 */
	public static final int KEY_LAYER_CHILE_5G = 60;
	/**
	 * 共站信息
	 */
	public static final int KEY_LAYER_CHILE_GZXX = 61;
	/**
	 * nb图层-室外
	 */
	public static final int KEY_LAYER_CHILE_NB_SW = 62;
	/**
	 * nb图层-室内
	 */
	public static final int KEY_LAYER_CHILE_NB_SN = 63;
}
