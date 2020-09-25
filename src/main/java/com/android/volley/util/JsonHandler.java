package com.android.volley.util;

import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON 数据处理类
 * @author Administrator
 *
 */
public class JsonHandler {

	private static JsonHandler handler;
	
	private JsonHandler(){}
	
	public static synchronized JsonHandler getHandler(){
		synchronized (JsonHandler.class) {
			if (handler == null) {
				handler = new JsonHandler();
			}
			return handler;
		}
	}
	
	/**
	 * 对象转换成JSON数据
	 * @param value
	 * @return
	 */
	public String toJson(Object value){
		Gson gson = new Gson();
		return gson.toJson(value);
	}
	
	/**
	 * 返回一个对象 class
	 * @param jsonString
	 * @param cls 对象 如： A.class 则返回的对象T为A
	 * @return
	 */
	public <T> T getTarget(String jsonString, Class<T> cls){
        try {  
            Gson gson = new Gson();  
            return gson.fromJson(jsonString, cls);  
        } catch (Exception e) {  
        	System.err.println("错误：" + e.getMessage());
            return null;
        }  
	}
	
	/**
	 * 将json数据转换成List对象
	 * @param jsonString 要转换的json数据
	 * @param type 如：new TypeToken<List<T>>(){}.getType() ---- new TypeToken<T>(){}.getType()
	 * @return
	 */
	public <T> List<T> getListTarget(String jsonString,Type type){
        try {  
        	//转换器 
        	GsonBuilder builder = new GsonBuilder(); 
        	// 不转换没有 @Expose 注解的字段 
        	builder.excludeFieldsWithoutExposeAnnotation();     
        	Gson gson = builder.create(); 
            return gson.fromJson(jsonString, type); 
        } catch (Exception e) {  
        	System.err.println("错误：" + e.getMessage());
            return null;
        }
	}
}
