package cn.nokia.speedtest5g.app.uitl;

import java.util.HashMap;
import android.text.TextUtils;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.util.gps.GisLayerKey;

/**
 * 分割转map
 * @author zwq
 *
 */
public class MyToSpile {

	private static MyToSpile mts = null;
	
	public static synchronized MyToSpile getInstances(){
		if (mts == null) {
			mts = new MyToSpile();
		}
		return mts;
	}
	
	public void clear(){
		mMapMenuCode = null;
		mMapDepartCode = null;
		mts = null;
	}
	
	private HashMap<String, String> mMapMenuCode,mMapDepartCode;
	/**
	 * 权限分割成hashmap
	 * @param type 0菜单 1工参
	 * @return
	 */
	public HashMap<String, String> getToCode(int type){
		String code;
		//菜单
		if (type == 0) {
			if (mMapMenuCode == null || mMapMenuCode.size() <= 0) {
				mMapMenuCode = new HashMap<String, String>();
				code = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_MENU(), "");
				if (!TextUtils.isEmpty(code)) {
					String[] split = code.split(",");
					String strSplit;
					//菜单权限分配
					for (int i = 0; i < split.length; i++) {
						strSplit = split[i];
						mMapMenuCode.put(strSplit, strSplit);
					}
				}
			}
			return mMapMenuCode;
		}else {
			if (mMapDepartCode == null || mMapDepartCode.size() <= 0) {
				mMapDepartCode = new HashMap<String, String>();
				code = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_DEPART(), "");
				if (!TextUtils.isEmpty(code)) {
					String[] split = code.split(",");
					String strSplit;
					//菜单权限分配
					for (int i = 0; i < split.length; i++) {
						strSplit = split[i];
						mMapDepartCode.put(strSplit, strSplit);
					}
				}
			}
			return mMapDepartCode;
		}
	}
	
	/**
	 * 根据模块权限代码判断是否有权限访问
	 * @param permissionsCode
	 * @return
	 */
	public boolean isAuthorityMenu(String permissionsCode){
		if (TextUtils.isEmpty(permissionsCode)) {
			return false;
		}
		getToCode(0);
		return mMapMenuCode.get(permissionsCode) != null;
	}
	
	/**
	 * GIS菜单是否有权限
	 * @param type 关联GisViewInfo.type
	 * @return
	 */
	public boolean isGisAuthorityMenu(int type){
		//GSM小区
		if (type == 0) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_Gsm));
		//LTE小区  
		}else if (type == 2) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_Lte));
		//LTE规划	
		}else if (type == 3) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_LteGh));
		//铁塔（竞对基站）	
		}else if (type >= 4 && type <= 16) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_jdjz));
		//邻区	
		}else if (type == 17) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_lqgx));
		//LTE RRU
		}else if (type == 18) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_LteRru));
		//退服告警
		}else if (type == 19) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_tfgj));
		//室分图纸	
		}else if (type == 20) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_sftz));
		//OTT覆盖	
		}else if (type == 22) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_ott));
		//应急站	
		}else if (type == 23) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_yjb));
		//基站导航	
		}else if (type == 24) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_jzdh));
		//共站信息	
		}else if (type == GisLayerKey.KEY_LAYER_TYPE_GZXX) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_Gzxx));
		//5G小区	
		}else if (type == 26) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_5G));
		//NB图层	
		}else if (type == GisLayerKey.KEY_LAYER_TYPE_NB) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_nb));
		//空间退服告警
		}else if (type == GisLayerKey.KEY_LAYER_TYPE_KJTFGJ) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_kjtfgj));
		//高铁
		}else if (type == GisLayerKey.KEY_LAYER_TYPE_GAO_TIE) {
			return isAuthorityMenu(SpeedTest5g.getContext().getString(R.string.permissionsGis_gt));
		}
		return false;
	}
}
