package cn.nokia.speedtest5g.app.uitl;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.MyListener;
import com.android.volley.toolbox.MyStringRequest;
import com.android.volley.toolbox.MyVolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.VolleyJsonToStringRequest;
import com.android.volley.util.SharedPreHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.LoginHeartbeat;
import cn.nokia.speedtest5g.app.thread.MyLoginExitThread;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 网络请求相关
 */
public class NetWorkUtilNow {
	private static NetWorkUtilNow nwUtil;

	public synchronized static NetWorkUtilNow getInstances() {
		if (nwUtil == null)
			nwUtil = new NetWorkUtilNow();

		return nwUtil;
	}

	private String TO_IP = "";

	private String bindIp = "";
	/**
	 * 获取当前主测IP地址
	 * @return
	 */
	public String getToIp(){
		if (TextUtils.isEmpty(TO_IP) || TextUtils.isEmpty(bindIp)) {
			bindIp = SpeedTestDataSet.mBindIp;
			//预发布
			if ("2".equals(bindIp)) {
				TO_IP = SpeedTest5g.getContext().getString(R.string.URL2);
				//测试	
			}else if ("3".equals(bindIp)) {
				TO_IP = SpeedTest5g.getContext().getString(R.string.URL3);
				//正式	
			}else {
				TO_IP = SpeedTest5g.getContext().getString(R.string.URL1);
			}
		}
		return TO_IP;
	}

	/**
	 *  获取当前主测IP地址
	 * @param bindIp 1-正式地址、2-预发布地址、3-测试地址、默认空也是正式地址
	 * @return
	 */
	public String getToIp(String bindIp){
		//预发布
		if ("2".equals(bindIp)) {
			return SpeedTest5g.getContext().getString(R.string.URL2);
			//测试	
		}else if ("3".equals(bindIp)) {
			return SpeedTest5g.getContext().getString(R.string.URL3);
			//正式	
		}else {
			return SpeedTest5g.getContext().getString(R.string.URL1);
		}
	}

	/**
	 * 清除IP、登录会话ID
	 */
	public void clearToIp(){
		if (!TextUtils.isEmpty(LOGIN_SESSION_ID) || !TextUtils.isEmpty(TO_IP) || !TextUtils.isEmpty(LOGIN_PROVINCE_TAG)) {
			WybLog.syso("清除IP");
			TO_IP = "";
			LOGIN_SESSION_ID = "";
			LOGIN_PROVINCE_TAG = "";
            SpeedTestDataSet.mBindIp = "";
			SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, "");
			SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_PROVINCE_TAG, "");
		}
	}

	//登录会话ID
	public String LOGIN_SESSION_ID = "";
	/**
	 * 获取当前用户的会话ID
	 * @return
	 */
	public String getLoginSessionId(){
		LOGIN_SESSION_ID = Base64Utils.encrytorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_SESSION_ID, ""));
		return LOGIN_SESSION_ID;
	}

	//登录切换tag
	public String LOGIN_PROVINCE_TAG = "";
	/**
	 * 获取当前用户的归属tag
	 * @return
	 */
	public String getLoginProvinceTag(){
		if (TextUtils.isEmpty(LOGIN_PROVINCE_TAG)) {
			LOGIN_PROVINCE_TAG = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_PROVINCE_TAG, "");
		}
		return LOGIN_PROVINCE_TAG;
	}

	// 网络数据请求
	private static RequestQueue requestQueue;
	// 请求的方式
	private StringRequest stringRequest;

	public void readNetworkGet(String url, final int type, final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		cancelRquest();
		initV();
		stringRequest = new MyStringRequest(Method.GET, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				listenerBack.onListener(type,isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.syso("返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("authenticatorSource", parseByte2HexStr());
				headers.put("sourceAddr", "testAdress");
				headers.put("timeStamp", TimeUtil.getInstance().getNowTimeN());
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				if (headers.size() > 0) {
					return headers;
				}
				return super.getHeaders();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	public void readNetworkGetNoLastCancel(String url, final int type, final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		initV();
		stringRequest = new MyStringRequest(Method.GET, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.syso("返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("authenticatorSource", parseByte2HexStr());
				headers.put("sourceAddr", "testAdress");
				headers.put("timeStamp", TimeUtil.getInstance().getNowTimeN());
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				if (headers.size() > 0) {
					return headers;
				}
				return super.getHeaders();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	/**
	 * Post请求，参数为json格式
	 * 
	 * @param url
	 * @param jsonData (未加密数据) Base64Utils.encrytorDes3()
	 * @param type
	 * 
	 * Base64Utils.decryptorDes3
	 */
	public void readNetworkPostJsonOfStringDes(String url, String jsonData, final int type, final ListenerBack listenerBack) {
		readNetworkPostJsonOfStringDes(url, jsonData, type, listenerBack, 90 * 1000);
	}

	/**
	 * Post请求，参数为json格式
	 * 
	 * @param url
	 * @param jsonData (未加密数据) Base64Utils.encrytorDes3()
	 * @param type
	 * @param timeOut 超时时长，默认90秒
	 * 
	 * Base64Utils.decryptorDes3
	 */
	public void readNetworkPostJsonOfStringDes(String url, String jsonData, final int type, final ListenerBack listenerBack,int timeOut) {
		WybLog.syso("请求地址：" + url + "==数据：" + jsonData);
		cancelRquest();
		initV();
		// 先转换成UTF-8
		VolleyJsonToStringRequest jsonRequest = new VolleyJsonToStringRequest(Method.POST, url, Base64Utils.encrytorDes3(jsonData),
				new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				// 数据回调
				if (listenerBack != null) {
					listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
				}
			}
			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (listenerBack != null) {
					if (error.getMsg() != null && (error.getMsg().indexOf("请求资源不存在") != -1
							|| error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
						setErrorMsg("正在升级维护,请稍等再试!");
					} else {
						setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
					}
					WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
					listenerBack.onListener(type, getErrorMsg(), false);
				}
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("en", "true");
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				return headers;
			}
		};

		// 设置不使用缓存
		jsonRequest.setShouldCache(false);
		// 设置超时
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut, 0, 1.0f));
		requestQueue.add(jsonRequest);
	}

	/**
	 * 请求数据加密，不取消上一次
	 * @param url
	 * @param jsonData
	 * @param type
	 * @param listenerBack
	 */
	public void readNetworkPostJsonOfStringDesNoCancel(String url, String jsonData, final int type, final ListenerBack listenerBack){
		WybLog.syso("请求地址：" + url + "==数据：" + jsonData);
		initV();
		// 先转换成UTF-8
		jsonRequest = new VolleyJsonToStringRequest(Method.POST, url, Base64Utils.encrytorDes3(jsonData),
				new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null && (error.getMsg().indexOf("请求资源不存在") != -1
						|| error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("en", "true");
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				return headers;
			}
		};

		// 设置不使用缓存
		jsonRequest.setShouldCache(false);
		// 设置超时
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(jsonRequest);
	}

	private VolleyJsonToStringRequest jsonRequest;

	/**
	 * JSON数据请求
	 * 
	 * @param url
	 * @param jsonData
	 * @param type
	 */
	public void readNetworkPostJsonObject(String url, String jsonData, int type,
			ListenerBack listenerBack) {
		readNetworkPostJsonObject(url, jsonData, type, listenerBack, 90 * 1000);
	}

	/**
	 * post请求 json数据
	 * @param url
	 * @param jsonData
	 * @param type
	 * @param listenerBack
	 * @param initialTimeoutMs 请求超时时间
	 */
	public void readNetworkPostJsonObject(String url, String jsonData, final int type,
			final ListenerBack listenerBack,int initialTimeoutMs) {
		WybLog.syso("请求地址：" + url + "==数据：" + jsonData);
		cancelRquest();
		initV();
		// 先转换成UTF-8

		jsonRequest = new VolleyJsonToStringRequest(Method.POST, url, jsonData,
				new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null && (error.getMsg().indexOf("请求资源不存在") != -1
						|| error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
					return headers;
				}
				return super.getHeaders();
			}
		};

		// 设置不使用缓存
		jsonRequest.setShouldCache(false);
		// 设置超时
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, 0, 1.0f));
		requestQueue.add(jsonRequest);
	}

	/**
	 * JSON数据请求，不取消前面的请求，用于多接口同时请求数据
	 * 
	 * @param url
	 * @param jsonData
	 * @param type
	 */
	public void readNetworkPostJsonObjectNoCancel(String url, String jsonData, final int type,
			final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url + "==数据：" + jsonData);
		initV();
		//		cancelRquest();
		// 先转换成UTF-8

		jsonRequest = new VolleyJsonToStringRequest(Method.POST, url, jsonData,
				new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0.toString()) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null && (error.getMsg().indexOf("请求资源不存在") != -1
						|| error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
					return headers;
				}
				return super.getHeaders();
			}
		};

		// 设置不使用缓存
		jsonRequest.setShouldCache(false);
		// 设置超时
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(jsonRequest);
	}

	/**
	 * 不建议使用的接口，请使用以下接口
	 * 1.readNetworkPostJsonObject（数据无加密时）
	 * 2.readNetworkPostJsonObjectNoCancel（数据无加密时）
	 * 3.readNetworkPostJsonOfStringDes（数据需加密时-传入数据无需加密，方法自动加密）
	 * 4.readNetworkPostJsonOfStringDesNoCancel（数据需加密时-传入数据无需加密，方法自动加密）
	 * -----------------------------------------------------------------------------------
	 * JSON数据请求,以datas方式键值传输
	 * 
	 * @param url
	 * @param jsonData 2.5.7新增可传入Map数据
	 * @param type
	 */
	@Deprecated
	public void readNetworkPost(String url, final Object jsonData, final int type, final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		cancelRquest();
		initV();
		// 先转换成UTF-8
		stringRequest = new MyStringRequest(Method.POST, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
					return headers;
				}
				return super.getHeaders();
			}

			@SuppressWarnings("unchecked")
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (jsonData instanceof Map) {
					Map<String, String> map = (Map<String, String>) jsonData;
					if (map.size() > 0) {
						return map;
					}
				} else {
					WybLog.syso("请求参数：" + jsonData.toString());
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("datas", jsonData.toString());
					if (headers.size() > 0) {
						return headers;
					}
				}

				return super.getParams();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	/**
	 * 不建议使用的接口，请使用以下接口
	 * 1.readNetworkPostJsonObject（数据无加密时）
	 * 2.readNetworkPostJsonObjectNoCancel（数据无加密时）
	 * 3.readNetworkPostJsonOfStringDes（数据需加密时-传入数据无需加密，方法自动加密）
	 * 4.readNetworkPostJsonOfStringDesNoCancel（数据需加密时-传入数据无需加密，方法自动加密）
	 * -----------------------------------------------------------------------------------
	 * JSON数据请求,以datas方式键值传输
	 * 
	 * @param url
	 * @param jsonData 2.5.7新增可传入Map数据
	 * @param type
	 */
	@Deprecated
	public void readNetworkPostDes(String url, final Object jsonData, final int type, final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		cancelRquest();
		initV();
		// 先转换成UTF-8
		stringRequest = new MyStringRequest(Method.POST, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("en", "true");
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				return headers;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (jsonData instanceof Map) {
					Map<String, String> map = (Map<String, String>) jsonData;
					if (map.size() > 0) {
						return map;
					}
				} else {
					WybLog.syso("请求参数：" + jsonData.toString());
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("datas", Base64Utils.encrytorDes3(jsonData.toString()));
					if (headers.size() > 0) {
						return headers;
					}
				}

				return super.getParams();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	/**
	 *  不建议使用的接口，请使用以下接口
	 * 1.readNetworkPostJsonObject（数据无加密时）
	 * 2.readNetworkPostJsonObjectNoCancel（数据无加密时）
	 * 3.readNetworkPostJsonOfStringDes（数据需加密时-传入数据无需加密，方法自动加密）
	 * 4.readNetworkPostJsonOfStringDesNoCancel（数据需加密时-传入数据无需加密，方法自动加密）
	 * -----------------------------------------------------------------------------------
	 * JSON数据请求 ,请求头为“data”，此接口是兼容某些特殊接口而写，新接口不建议，统一风格下
	 * 
	 * @param url
	 * @param jsonData
	 * @param type
	 */
	@Deprecated
	public void readNetworkPostData(String url, final Object jsonData, final int type, final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		cancelRquest();
		initV();
		// 先转换成UTF-8
		stringRequest = new MyStringRequest(Method.POST, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
					return headers;
				}
				return super.getHeaders();
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				WybLog.syso(jsonData.toString());
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("data", jsonData.toString());
				if (headers.size() > 0) {
					return headers;
				}
				return super.getParams();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	/**
	 *  不建议使用的接口，请使用以下接口
	 * 1.readNetworkPostJsonObject（数据无加密时）
	 * 2.readNetworkPostJsonObjectNoCancel（数据无加密时）
	 * 3.readNetworkPostJsonOfStringDes（数据需加密时-传入数据无需加密，方法自动加密）
	 * 4.readNetworkPostJsonOfStringDesNoCancel（数据需加密时-传入数据无需加密，方法自动加密）
	 * -----------------------------------------------------------------------------------
	 * JSON数据请求 ,需额外传递extra字段
	 * 
	 * @param url
	 * @param jsonData
	 * @param type
	 */
	@Deprecated
	public void readNetworkPost(String url, final Object jsonData, final Object jsonExtra, final int type,
			final ListenerBack listenerBack) {
		WybLog.syso("请求地址：" + url);
		cancelRquest();
		initV();
		// 先转换成UTF-8
		stringRequest = new MyStringRequest(Method.POST, url, new MyListener<String>() {

			@Override
			public void onResponse(String arg0) {
				WybLog.syso(isDes + "返回s：" + (isDes ? Base64Utils.decryptorDes3(arg0) : arg0));
				// 数据回调
				listenerBack.onListener(type, isDes ? Base64Utils.decryptorDes3(arg0) : arg0, true);
			}

			//是否需要解密
			private boolean isDes; 

			@Override
			public void onResponseHeaders(Map<String, String> headers) {
				if (headers != null && headers.size() > 0) {
					isDes = "true".equals(headers.get("en"));
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
                MyVolleyError error = new MyVolleyError(volleyError);
				if (isCode418(volleyError)) {
					return;
				}
				if (error.getMsg() != null
						&& (error.getMsg().indexOf("请求资源不存在") != -1 || error.getMsg().indexOf("执行访问被禁止") != -1 || error.getMsg().indexOf("Connection refused") != -1)) {
					setErrorMsg("正在升级维护,请稍等再试!");
				} else {
					setErrorMsg(error.getMsg() == null ? "连接服务器失败,请检查网络是否正常!" : error.getMsg());
				}
				WybLog.e("NET_WORK", "返回E：" + getErrorMsg());
				listenerBack.onListener(type, getErrorMsg(), false);
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				if (!TextUtils.isEmpty(getLoginSessionId())) {
					headers.put("sid", LOGIN_SESSION_ID);
					headers.put("provinceTag",getLoginProvinceTag());
				}
				if (headers.size() > 0) {
					return headers;
				}
				return super.getHeaders();
			}
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				WybLog.syso(jsonData.toString());
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("datas", jsonData.toString());
				if (jsonExtra != null) {
					headers.put("extra", jsonExtra.toString());
				}
				if (headers.size() > 0) {
					return headers;
				}
				return super.getParams();
			}
		};
		// 设置不使用缓存
		stringRequest.setShouldCache(false);
		// 设置超时
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
		requestQueue.add(stringRequest);
	}

	private void initV() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(SpeedTest5g.getContext());
			requestQueue.start();
		}
	}

	/**
	 * 清除请求
	 */
	public void cancelRquest() {
		if (requestQueue == null) {
			return;
		}
		if (stringRequest != null && !stringRequest.isCanceled()) {
			stringRequest.cancel();
		}
		if (jsonRequest!=null&&!jsonRequest.isCanceled()) {
			jsonRequest.cancel();
		}
		try {
			requestQueue.cancelAll("tag");
		} catch (Exception e) {

		}
		requestQueue.stop();
		requestQueue = null;
	}

	/**
	 * 错误提示的内容
	 */
	private String errorMsg;

	/**
	 * 错误提示的内容
	 */
	public String getErrorMsg() {
		return errorMsg == null ? "" : errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * 中文乱码转换
	 * 
	 * @param str
	 * @return
	 */
	public String getUtf(String str) {
		try {
			return (str == null || str.isEmpty()) ? "" : URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			WybLog.syse("错误：" + e.getMessage());
		}
		return str;
	}

	private byte[] getMd5(String input) {
		try {
			byte[] by = input.getBytes("UTF-8");
			MessageDigest det = MessageDigest.getInstance("MD5");
			return det.digest(by);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @return MMddHHmmss
	 */
	@SuppressLint("DefaultLocale")
	public String parseByte2HexStr() {
		String input = "testAdress" + "fn123dxz" + TimeUtil.getInstance().getNowTimeN();
		byte[] buf = getMd5(input);
		if (buf == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @return MMddHHmmss
	 */
	@SuppressLint("DefaultLocale")
	public String parseByte2HexStr(String input) {
		byte[] buf = getMd5(input);
		if (buf == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public String getValue(String str) {
		if(str == null || str.length() == 0) {
			return "";
		}
		return str;
	}

	//错误代码转中文
	public String errCodeToStr(int code){
		switch (code) {
		case 400:
			return "请求无效";
		case 401:
			return "未授权";
		case 402:
			return "需要付款";
		case 403:
			return "禁止访问";
		case 404:
			return "请求资源不存在";
		case 405:
			return "资源被禁止";
		case 406:
			return "无法接受";
		case 407:
			return "要求进行代理身份验证";
		case 408:
			return "请求超时";
		case 409:
			return "资源冲突";
		case 410:
			return "永远不可用";
		case 41:
			return "需要内容长度头";
		case 412:
			return "先决条件失败";
		case 413:
			return "请求实体太大";
		case 414:
			return "请求URI太长";
		case 415:
			return "不支持的媒体类型";
		case 416:
			return "所请求的范围无法满足";
		case 417:
			return "执行失败";
		case 418:
			MyLoginExitThread.toGo(new LoginHeartbeat("418"));
			return "会话超时，请重新登录";
		case 500:
			return "内部服务器错误";
		case 501:
			return "未实现的配置";
		case 502:
			return "网关错误";
		case 503:
			return "服务不可用";
		case 504:
			return "网关超时";
		case 505:
			return "HTTP版本不受支持";
		default:
			return "未知错误" + code;
		}
	}

	private boolean isCode418(VolleyError error){
		if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 418) {
			MyLoginExitThread.toGo(new LoginHeartbeat("418"));
			return true;
		}
		return false;
	}
}
