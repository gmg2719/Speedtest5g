package com.android.volley.util;

import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 *  描述	:SharedPreferences---XML数据存储
 */
public class SharedPreHandler {
	
	private static SharedPreHandler shared = null;
	
	private static SharedPreferences sp = null;
	
	private Editor edit;
	
	public static synchronized SharedPreHandler getShared(Context context){
		if (shared == null) {
			shared = new SharedPreHandler();
		}
		if (sp == null) {
			String[] split = context.getPackageName().split("\\.");
			if (split.length>0) {
				sp = context.getSharedPreferences(split[split.length-1], Context.MODE_PRIVATE);
			}else {
				sp = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
			}
		}
		return shared;
	}
	
	/**
	 * 调用此方法后需要调用init方法初始化数据
	 * @return
	 */
	@Deprecated
	public static synchronized SharedPreHandler getShared(){
		if (shared == null) {
			shared = new SharedPreHandler();
		}
		return shared;
	}
	
	public void init(Context context){
		if (sp == null) {
			String[] split = context.getPackageName().split("\\.");
			if (split.length>0) {
				sp = context.getSharedPreferences(split[split.length-1], Context.MODE_PRIVATE);
			}else {
				sp = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
			}
		}
	}
	
	/*---------------------------------------对历史数据进行加密-------------------------------*/
	public void initEncryption(){
		if (!getBooleanShared("isInitEncryption", false)) {
			Map<String, ?> all = sp.getAll();
	        if (all != null && all.size() > 0){
	            for(Map.Entry<String, ?> entry : all.entrySet()){
	                if (entry.getValue() instanceof String){
	                	setStrShared(entry.getKey(), entry.getValue().toString());
	                }
	            }
	        }
	        setBooleanShared("isInitEncryption", true);
		}
	}
	
	/*---------------------------------------数据存储----------------------------------------*/
	public void setStrShared(String key,String value){
		if (null != sp) {
			edit = sp.edit();
			edit.putString(key, EncryptionUtil.encode(value));
			edit.commit();
		}
	}
	
	public void setIntShared(String key,int value){
		if (null != sp) {
			edit = sp.edit();
			edit.putInt(key, value);
			edit.commit();
		}
	}
	
	public void setBooleanShared(String key,boolean value){
		if (null != sp) {
			edit = sp.edit();
			edit.putBoolean(key, value);
			edit.commit();
		}
	}
	
	public void setLongShared(String key,long value){
		if (null != sp) {
			edit = sp.edit();
			edit.putLong(key, value);
			edit.commit();
		}
	}
	
	public void setFloatShared(String key,float value){
		if (null != sp) {
			edit = sp.edit();
			edit.putFloat(key, value);
			edit.commit();
		}
	}
	/*---------------------------------------数据读取----------------------------------------*/
	/**
	 *  参数说明：
	 *  	@param key 字段名
	 *  	@param defValue 若数据为空时返回的值
	 *  	@return
	 */
	public String getStringShared(String key,String defValue){
		if (null != sp) {
			String str = sp.getString(key, defValue);
			if (!TextUtils.isEmpty(str)) {
				if (!str.equals(defValue)) {
					return EncryptionUtil.decode(str);
				}
			}
		}
		return defValue;
	}
	
	/**
	 *  参数说明：
	 *  	@param key 字段名
	 *  	@param defValue 若数据为空时返回的值
	 *  	@return
	 */
	public int getIntShared(String key,int defValue){
		return (null != sp) ? sp.getInt(key, defValue) : defValue;
	}
	
	/**
	 *  参数说明：
	 *  	@param key 字段名
	 *  	@param defValue 若数据为空时返回的值
	 *  	@return
	 */
	public boolean getBooleanShared(String key,boolean defValue){
		return (null != sp) ? sp.getBoolean(key, defValue) : defValue;
	}
	
	/**
	 *  参数说明：
	 *  	@param key 字段名
	 *  	@param defValue 若数据为空时返回的值
	 *  	@return
	 */
	public long getLongShared(String key,long defValue){
		return (null != sp) ? sp.getLong(key, defValue) : defValue;
	}
	
	/**
	 *  参数说明：
	 *  	@param key 字段名
	 *  	@param defValue 若数据为空时返回的值
	 *  	@return
	 */
	public float getFloatShared(String key,float defValue){
		return (null != sp) ? sp.getFloat(key, defValue) : defValue;
	}
}
