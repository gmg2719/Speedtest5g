package cn.nokia.speedtest5g.app.uitl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.android.volley.util.BasicUtil;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.WybBitmapDb;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.upload.UploadNowTypeStr;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

import android.text.TextUtils;

/**
 *  文件名称	：HttpPostUtil.java<p>
 *  内容摘要	：<p> 通过HttpPOST访问目标地址
 *  作者	：zwq
 */
public class HttpPostUtil {
	private static HttpPostUtil httpGetUtil;
	
	public static HttpPostUtil getHttpGetUtil(){
		httpGetUtil = new HttpPostUtil();
		return httpGetUtil;
	}
	
	private DefaultHttpClient client;
	
	public void close(){
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}
	
	/**
	 *  函数名称 : doPost
	 *  功能描述 :  通过HttpPOST访问目标地址
	 *  参数说明：
	 *  	@param url
	 *  	@return
	 */
	@SuppressWarnings("unchecked")
	public String doPost(String url,Object nameValuePair){
		String content = "";
		try {
			client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (nameValuePair instanceof Map) {//如果是宏站单验
				Map<String, String> headers = (Map<String, String>) nameValuePair;
//				if (headers != null) {
//					Set<String> keys = headers.keySet();
//					for (Iterator<String> i = keys.iterator(); i.hasNext();) {
//						String key = (String) i.next();
//						httpPost.addHeader(key, headers.get(key));
//					}
//				}
				ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
				if (headers != null) {
					Set<String> keys = headers.keySet();
					for (Iterator<String> i = keys.iterator(); i.hasNext();) {
						String key = (String) i.next();
						pairs.add(new BasicNameValuePair(key, headers.get(key)));
					}
				}
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			} else {
				httpPost.addHeader("authenticatorSource",NetWorkUtilNow.getInstances().parseByte2HexStr());
				httpPost.addHeader("sourceAddr","testAdress");
				httpPost.addHeader("timeStamp",TimeUtil.getInstance().getNowTimeN());
//				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//				nameValuePair.add(new BasicNameValuePair("datas", datas));
				//设置条件参数
				httpPost.setEntity(new UrlEncodedFormEntity((List<NameValuePair>) nameValuePair,HTTP.UTF_8));
				
			}
			if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
				httpPost.addHeader("sid", NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
				httpPost.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
			}
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60*1000);
			//执行请求
			HttpResponse response = client.execute(httpPost);
			
			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK){
				return NetWorkUtilNow.getInstances().errCodeToStr(code);
			}
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						content = Base64Utils.decryptorDes3(content); 
						break;
					}
				}
			}
			httpPost.abort();
		} catch (Exception e) {
			WybLog.syso("post错误:" + e.getMessage());
			return "错误" + e.getMessage();
		}finally{
			close();
		}
		return content;
	}
	
	/**
	 *  函数名称 : doPost
	 *  功能描述 :  通过HttpPost访问目标地址
	 *  参数说明：
	 *  	@param url
	 *  	@return
	 */
	public String doPostJson(String url,String data){
		try {
			WybLog.syso("HttpPostUtil", url + "==" + data);
			HttpPost method = new HttpPost(url);
			method.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
			StringEntity se = new StringEntity(data,"utf-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;utf-8"));
            method.setEntity(se);
            if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
            	method.addHeader("sid", NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
            	method.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
			}
			client = new DefaultHttpClient();
			
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 12*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12*1000);
			HttpResponse response = client.execute(method);
			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK){
				return NetWorkUtilNow.getInstances().errCodeToStr(code);
			}
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						return Base64Utils.decryptorDes3(EntityUtils.toString(response.getEntity(), "UTF-8")); 
					}
				}
			}
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
			return "错误" + e.getMessage();
		}finally{
			close();
		}
	}
	
	
	/**
	 * 百度图片识别--无需des进行加解密
	 * @param requestUrl
	 * @param accessToken
	 * @param params
	 * @return
	 */
	public String doPostBdBit(String requestUrl, String accessToken, String params) {
		String result = "";
		try {
			String generalUrl = requestUrl + "?access_token=" + accessToken;
	        URL url = new URL(generalUrl);
	        // 打开和URL之间的连接
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        // 设置通用的请求属性
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestProperty("Connection", "Keep-Alive");
	        connection.setUseCaches(false);
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	        // 得到请求的输出流对象
	        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
	        out.writeBytes(params);
	        out.flush();
	        out.close();
	        // 建立实际的连接
	        connection.connect();
	        // 获取所有响应头字段
//	        Map<String, List<String>> headers = connection.getHeaderFields();
	        // 遍历所有的响应头字段
//	        for (String key : headers.keySet()) {
//	            WybLog.syso(key + "--->" + headers.get(key));
//	        }
	        // 定义 BufferedReader输入流来读取URL的响应
	        BufferedReader in = null;
	        if (requestUrl.contains("nlp")){
	        	in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
	        } else{
	            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	        }
	        String getLine;
	        while ((getLine = in.readLine()) != null) {
	            result += getLine;
	        }
	        in.close();
	        WybLog.syso("result:" + result);
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}finally{
			
		}
        return result;
    }
	
	/**
	 * 百度识别-简单硬扩卡槽位识别--无需des进行加解密
	 * @param generalUrl
	 * @param contentType
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public String postGeneralUrl(String generalUrl, String contentType, String params, String encoding){
		String result = "";
        try {
        	URL url = new URL(generalUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 得到请求的输出流对象
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(params.getBytes(encoding));
            out.flush();
            out.close();

            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> headers = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : headers.keySet()) {
//                System.err.println(key + "--->" + headers.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = null;
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), encoding));
            String getLine;
            while ((getLine = in.readLine()) != null) {
                result += getLine;
            }
            in.close();
		} catch (Exception e) {
			WybLog.syso("错误：" + e.getMessage());
		}
        return result;
    }
	
	public String doPostJsonZip(String url,String strJson){
		String content = "";
		try {
			WybLog.syso("postZip请求:" + url + "===数据：" + strJson);
			client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("wybZip", "true");
			httpPost.addHeader("authenticatorSource",NetWorkUtilNow.getInstances().parseByte2HexStr());
			httpPost.addHeader("sourceAddr","testAdress");
			httpPost.addHeader("timeStamp",TimeUtil.getInstance().getNowTimeN());
			if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
				httpPost.addHeader("sid", NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
				httpPost.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  			
			GZIPOutputStream gs = new GZIPOutputStream(baos);
			gs.write(strJson.getBytes());
			gs.finish();
			//设置条件参数
			httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 90*1000);
			//执行请求
			HttpResponse response = client.execute(httpPost);
			
			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK){
				return NetWorkUtilNow.getInstances().errCodeToStr(code);
			}
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						content = Base64Utils.decryptorDes3(content); 
						break;
					}
				}
			}
			httpPost.abort();
		} catch (Exception e) {
			WybLog.syso(url + "错误:" + e.getMessage());
			return "错误:" + e.getMessage();
		}finally{
			close();
		}
		return content;
	}
	
	public String doPostJson(String url,String strJson, int type){
		String content = "";
		try {
			WybLog.syso("post请求:" + url + "===数据：" + strJson);
			client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
				httpPost.addHeader("sid", NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
				httpPost.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
			}
			//设置条件参数
			if (type == 5000) {
				httpPost.addHeader("authenticatorSource",NetWorkUtilNow.getInstances().parseByte2HexStr());
				httpPost.addHeader("sourceAddr","testAdress");
				httpPost.addHeader("timeStamp",TimeUtil.getInstance().getNowTimeN());
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("datas", strJson));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));
			} else if (type == 5007) {
				httpPost.addHeader("content-type", "text/html; charset=utf-8");
				httpPost.setEntity(new StringEntity(strJson, HTTP.UTF_8));
			//基站信号数据上传
			}else if (type == EnumRequest.NET_GIS_SIGNAL.toInt()) {
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("datas", strJson));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));
			} else {
				// if (type == 5001)
				httpPost.setEntity(new StringEntity(strJson, HTTP.UTF_8));
			}
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60*1000);
			//执行请求
			HttpResponse response = client.execute(httpPost);
			
			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK){
				return NetWorkUtilNow.getInstances().errCodeToStr(code);
			}
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						content = Base64Utils.decryptorDes3(content); 
						break;
					}
				}
			}
			httpPost.abort();
		} catch (Exception e) {
			WybLog.syso("post错误:" + e.getMessage());
			return "错误" + e.getMessage();
		}finally{
			close();
		}
		return content;
	}
	
	/**
	 * 加密接口请求
	 * @param url
	 * @param strJson
	 * @return
	 */
	public String doPostJsonDes(String url,String strJson) {
		WybLog.syso(url + "数据：" + strJson);
		String content = "";
		try {
			client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("en", "true");//数据需加密上传
			if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
				httpPost.addHeader("sid", NetWorkUtilNow.getInstances().getLoginSessionId());
				httpPost.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
			}
			httpPost.addHeader("content-type", "application/json");
			//设置条件参数
		    httpPost.setEntity(new StringEntity(Base64Utils.encrytorDes3(strJson), HTTP.UTF_8));
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60*1000);
			//执行请求
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK) {
				throw new Exception("");
			}
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						content = Base64Utils.decryptorDes3(content); 
						break;
					}
				}
			}
			httpPost.abort();
		} catch (Exception e)  {
			WybLog.syso("post错误:" + e.getMessage());
			return null;
		} finally {
			close();
		}
		return content;
	}




	/**
	 * 上传文件
	 * @param url
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String doPostFile(String url,Object object){
		String content = "";
		GZIPOutputStream gs = null;
		FileInputStream ins = null;
		BufferedInputStream br = null;
		HttpPost httpPost = new HttpPost(url);
		if (!TextUtils.isEmpty(NetWorkUtilNow.getInstances().getLoginSessionId())) {
			httpPost.addHeader("sid", NetWorkUtilNow.getInstances().LOGIN_SESSION_ID);
			httpPost.addHeader("provinceTag",NetWorkUtilNow.getInstances().getLoginProvinceTag());
		}
		String userid = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_ID(), "");
		try {
			if (object instanceof WybBitmapDb) {//自动纠偏图片,网优自动化等
				//自动纠偏图片,网优自动化等
				WybBitmapDb bitmapDb = (WybBitmapDb) object;
				WybLog.syso("postZip请求:" + url + "===数据地址：" + bitmapDb.getPath() + bitmapDb.getName());
				File file = new File(bitmapDb.getPath() + bitmapDb.getName());
				String fileMD5 = UtilHandler.getInstance().getFileMD5(file);
				if (TextUtils.isEmpty(fileMD5)) {
					return "本地获取文件MD5失败";
				}
				if (TextUtils.isEmpty(userid)) {
					userid = bitmapDb.getUserId();
				}
				if(bitmapDb.getType()==901){
					httpPost.addHeader("version_code", String.valueOf(BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false)));
					httpPost.addHeader("version_name", TypeKey.getInstance().VerName);
					httpPost.addHeader("userid", userid);
					httpPost.addHeader("attachPath", "yjzkt/");
					httpPost.addHeader("attachType", "901");
					httpPost.addHeader("testId",TimeUtil.getInstance().getNowTime(TimeUtil.getInstance().stringToLong(bitmapDb.getTime())));
					httpPost.addHeader("authCode", NetWorkUtilNow.getInstances().parseByte2HexStr(userid+bitmapDb.getName()+"I0Qx4BpN4uMi3FVS"));
					httpPost.addHeader("fileName", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getName()));
//					httpPost.addHeader("type", gcxj.getNetWork());
					httpPost.addHeader("siteId", bitmapDb.getMsg1());
//					httpPost.addHeader("siteName",  bitmapDb.get);
					httpPost.setHeader("fileHashCode", fileMD5);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ins = new FileInputStream(file);//定位流
					br = new BufferedInputStream(ins);//读取流
					byte b[] = new byte[1024];
					int len = 0;
					while((len = br.read(b)) != -1){
						baos.write(b,0,len);
					}
					//设置条件参数
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}else if(bitmapDb.getType()==902){
					httpPost.addHeader("version_code", String.valueOf(BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false)));
					httpPost.addHeader("version_name", TypeKey.getInstance().VerName);
					httpPost.addHeader("userid", userid);
					httpPost.addHeader("attachPath", "yjhzjj/");
					httpPost.addHeader("attachType", "902");
					httpPost.addHeader("testId",bitmapDb.getMsg1());
					httpPost.addHeader("authCode", NetWorkUtilNow.getInstances().parseByte2HexStr(userid+bitmapDb.getName()+"I0Qx4BpN4uMi3FVS"));
					httpPost.addHeader("fileName", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getName()));
//					httpPost.addHeader("type", gcxj.getNetWork());
					httpPost.addHeader("siteId", bitmapDb.getEnbId());
					httpPost.addHeader("siteName",  NetWorkUtilNow.getInstances().getUtf(bitmapDb.getCellName()));
					httpPost.setHeader("fileHashCode", fileMD5);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ins = new FileInputStream(file);//定位流
					br = new BufferedInputStream(ins);//读取流
					byte b[] = new byte[1024];
					int len = 0;
					while((len = br.read(b)) != -1){
						baos.write(b,0,len);
					}
					//设置条件参数
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}else if(bitmapDb.getType()==903){
					httpPost.addHeader("version_code", String.valueOf(BasicUtil.getUtil().getVersion(SpeedTest5g.getContext(), false)));
					httpPost.addHeader("version_name", TypeKey.getInstance().VerName);
					httpPost.addHeader("userid", userid);
					httpPost.addHeader("attachPath", "yjhzyj/");
					httpPost.addHeader("attachType", "903");
					httpPost.addHeader("testId",bitmapDb.getMsg1());
					httpPost.addHeader("authCode", NetWorkUtilNow.getInstances().parseByte2HexStr(userid+bitmapDb.getName()+"I0Qx4BpN4uMi3FVS"));
					httpPost.addHeader("fileName", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getName()));
//					httpPost.addHeader("type", gcxj.getNetWork());
					httpPost.addHeader("siteId", bitmapDb.getEnbId());
					httpPost.addHeader("siteName",  NetWorkUtilNow.getInstances().getUtf(bitmapDb.getCellName()));
					httpPost.setHeader("fileHashCode", fileMD5);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ins = new FileInputStream(file);//定位流
					br = new BufferedInputStream(ins);//读取流
					byte b[] = new byte[1024];
					int len = 0;
					while((len = br.read(b)) != -1){
						baos.write(b,0,len);
					}
					//设置条件参数
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}else if(bitmapDb.getType() == 501 || bitmapDb.getType() == 500){
					httpPost.addHeader("authenticatorSource",NetWorkUtilNow.getInstances().parseByte2HexStr());
					httpPost.addHeader("sourceAddr","testAdress");
					httpPost.addHeader("timeStamp",TimeUtil.getInstance().getNowTimeN());
					httpPost.setHeader("fileName", bitmapDb.getName());
					httpPost.setHeader("USER_ID", bitmapDb.getUserId());
					httpPost.setHeader("ATTACHE_FILE", String.valueOf(bitmapDb.getType()));
					if (bitmapDb.getType() == 501 || bitmapDb.getType() == 500) {
						httpPost.setHeader("phoneRSRP", bitmapDb.getRsrpOld());
						httpPost.setHeader("phoneSINR", bitmapDb.getSinrOld());
						if (bitmapDb.getType() == 501) {
							httpPost.setHeader("ocrResult", bitmapDb.getStrMsg());
						}
					}
					httpPost.setHeader("appFileMd5", fileMD5);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gs = new GZIPOutputStream(baos);
					ins = new FileInputStream(file);//定位流
					br = new BufferedInputStream(ins);//读取流
					byte b[] = new byte[1024];
					int len = 0;
					while((len = br.read(b)) != -1){
						gs.write(b,0,len);
					}
					gs.finish();
					//设置条件参数
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}else {
					httpPost.addHeader("attach_name", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getName()));//附件名称
					httpPost.addHeader("attach_file", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getPath().replaceAll(PathUtil.getInstances().getCurrentPath(), "")));//附件路径
					if (bitmapDb.getType() < UploadNowTypeStr.FILE_VALUE_MIN_JDYK || bitmapDb.getType() > UploadNowTypeStr.FILE_VALUE_MAX_JDYK) {
						httpPost.addHeader("file_path", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getPath().replaceAll(PathUtil.getInstances().getCurrentPath(), ""))); // 服务端附件路径
					}
					httpPost.addHeader("uploader", userid);//附件上传人
					httpPost.addHeader("attach_type", (bitmapDb.getName().endsWith(".jpg") || bitmapDb.getName().endsWith(".png")) ? "1" : "2");//附件类型:1-照片 2-CSV 3-其他
					httpPost.addHeader("business_type", String.valueOf(bitmapDb.getType()));//附件所属业务
					//头像上传
					if(bitmapDb.getType() == UploadNowTypeStr.FILE_VALUE_HEAD){
						httpPost.addHeader("module_type", UploadNowTypeStr.FILE_VALUE_ONE_MODULE_TYPE_HEAD);//附件所属模块
						httpPost.addHeader("batch_no", bitmapDb.getRemarks());//批次号
						httpPost.addHeader("file_path", NetWorkUtilNow.getInstances().getUtf(bitmapDb.getPath().replaceAll(PathUtil.getInstances().getCurrentPath(), ""))); // 服务端附件路径
					}
					httpPost.setHeader("appFileMd5", fileMD5);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gs = new GZIPOutputStream(baos);
					ins = new FileInputStream(file);//定位流
					br = new BufferedInputStream(ins);//读取流
					byte b[] = new byte[1024];
					int len = 0;
					while((len = br.read(b)) != -1){
						gs.write(b,0,len);
					}
					gs.finish();
					//设置条件参数
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
				}
			}
			client = new DefaultHttpClient();
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300*1000);
			//执行请求
			HttpResponse response = client.execute(httpPost);

			int code = response.getStatusLine().getStatusCode();
			if (code != HttpStatus.SC_OK){
				return NetWorkUtilNow.getInstances().errCodeToStr(code);
			}
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
			Header[] allHeaders = response.getAllHeaders();
			if (allHeaders != null && allHeaders.length > 0) {
				for (Header header : allHeaders) {
					if ("en".equals(header.getName()) && "true".equals(header.getValue())) {
						content = Base64Utils.decryptorDes3(content);
						break;
					}
				}
			}
			httpPost.abort();

		} catch (Exception e) {
			WybLog.syso(url + "错误:" + e.getMessage());
			return "附件上传异常-错误信息：" + e.getMessage();
		}finally{
			try {
				if (gs != null) {
					gs.close();
				}
				if (ins != null) {
					ins.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			close();
		}
		return content;
	}
}
