package cn.nokia.speedtest5g.app.uitl;

import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

/**
 * 地图常用的工具类 
 * 
 * 根据经纬度查询对应的附近poi数据
 * 
 * 根据经纬度取当前位置地理信息
 * @author zwq
 *
 */
@SuppressWarnings("deprecation")
public class PoiSearchGetUtil implements OnGetPoiSearchResultListener,OnGetGeoCoderResultListener{
	
	private static PoiSearchGetUtil psgu = null;
	//附近信息搜索
	private PoiSearch mPoiSearch = null;
	//经纬度转地理位置
	private GeoCoder mGeoCoder;
		
	private ListenerBack mListenerBack = null;
	
	public static synchronized PoiSearchGetUtil getInstances(){
		if (psgu == null) {
			psgu = new PoiSearchGetUtil();
		}
		return psgu;
	}
	
	public void cancelListener(){
		this.mListenerBack = null;
		if (mPoiSearch != null) {
			mPoiSearch.destroy();
		}
		
		if (mGeoCoder != null) {
			mGeoCoder.destroy();
		}
	}
	
	/*-------------------------------------------start搜索附近小区---------------------------------------*/
	/**
	 * 根据关键字和经纬度查询附近的小区(5KM内)
	 * @param strSearch
	 * @param latLng
	 * @param listenerBack
	 */
	public void searchPoi(String strSearch,LatLng latLng,ListenerBack listenerBack){
		initPoiSearch();
		this.mListenerBack = listenerBack;
		mPoiSearch.searchNearby(new PoiNearbySearchOption()
		.keyword(strSearch)
		.location(latLng)
		.pageCapacity(20)
		.radius(5000)
		.sortType(PoiSortType.distance_from_near_to_far));
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailSearchResult arg0) {
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			if (mListenerBack != null) {
				mListenerBack.onListener(EnumRequest.OTHER_POI_SEARCH.toInt(), "未找到结果", false);
			}
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			if (mListenerBack != null) {
				mListenerBack.onListener(EnumRequest.OTHER_POI_SEARCH.toInt(),result.getAllPoi(), true);
			}
			return;
		}
		if (mListenerBack != null) {
			mListenerBack.onListener(EnumRequest.OTHER_POI_SEARCH.toInt(), "未找到结果", false);
		}
	}

	private void initPoiSearch(){
		if (mPoiSearch == null) {
			// 初始化搜索模块，注册搜索事件监听
			mPoiSearch = PoiSearch.newInstance();
			mPoiSearch.setOnGetPoiSearchResultListener(this);
		}
	}
	
	/*-------------------------------------------end搜索附近小区---------------------------------------*/

	/*-------------------------------------------start解析地址---------------------------------------*/
	/**
	 * 根据经纬度解析地理位置信息
	 * @param latLngGeo
	 * @param listenerBack
	 */
	public void toGeoCoder(LatLng latLngGeo,ListenerBack listenerBack){
		if (latLngGeo == null || listenerBack == null) {
			return;
		}
		initGeoCoder();
		this.mListenerBack = listenerBack;
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLngGeo));
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		AddressComponent address = result.getAddressDetail();
		if(address==null) {
			if (mListenerBack != null) {
				mListenerBack.onListener(EnumRequest.OTHER_GEO_SEARCH.toInt(), "未找到结果", false);
			}
			return;
		}
		
		if(address.city == null || address.city.length() < 1 || 
				address.province == null || address.province.length() < 1 || 
						address.district == null || address.district.length() < 1) {
			if (mListenerBack != null) {
				mListenerBack.onListener(EnumRequest.OTHER_GEO_SEARCH.toInt(), "未找到结果", false);
			}
			return;
		}
		if (mListenerBack != null) {
			mListenerBack.onListener(EnumRequest.OTHER_GEO_SEARCH.toInt(), result, true);
		}
		//当前位置省份
//		province = province.substring(0, province.length()-1);
//		nowCity = nowCity.substring(0, nowCity.length()-1);
//		distrct = distrct.substring(0, distrct.length()-1);
	}
	
	private void initGeoCoder(){
		if (mGeoCoder == null) {
			mGeoCoder = GeoCoder.newInstance();
			mGeoCoder.setOnGetGeoCodeResultListener(PoiSearchGetUtil.this);
		}
	}
	/*-------------------------------------------end解析地址---------------------------------------*/

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/*----------------------------------地点关键字搜索----------------------------------*/
	
	private SuggestionSearch mSuggestionSearch;
	
	private ListenerBack mListenerBackSuggestion;
	
	/**
	 * 地点关键字搜索
	 * @param value
	 * @param listenerBack
	 */
	public void requestSuggestion(String value,ListenerBack listenerBack){
		this.mListenerBackSuggestion = listenerBack;
		if (mSuggestionSearch == null) {
			mSuggestionSearch = SuggestionSearch.newInstance();
			mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
				
				@Override
				public void onGetSuggestionResult(SuggestionResult suggestionResult) {
					if (suggestionResult == null || suggestionResult.getAllSuggestions() == null || suggestionResult.getAllSuggestions().size() <= 0) {
						if (mListenerBackSuggestion != null) {
							mListenerBackSuggestion.onListener(EnumRequest.OTHER_DDGJZ_SEARCH.toInt(), "未找到结果", false);
						}
					}else {
						if (mListenerBackSuggestion != null) {
							mListenerBackSuggestion.onListener(EnumRequest.OTHER_DDGJZ_SEARCH.toInt(), suggestionResult.getAllSuggestions(), true);
						}
					}
				}
			});
		}
		mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().city("全国").keyword(value));
	}
	
	/**
	 * 关闭地点关键字搜索
	 */
	public void destroySuggestion(){
		this.mListenerBackSuggestion = null;
		if (mSuggestionSearch != null) {
			mSuggestionSearch.destroy();
			mSuggestionSearch = null;
		}
	}
}
