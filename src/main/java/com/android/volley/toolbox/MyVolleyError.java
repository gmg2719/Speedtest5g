package com.android.volley.toolbox;

import android.util.Log;

import com.android.volley.VolleyError;

/**
 * @author JQJ
 **/
public class MyVolleyError {

    /**
     * 错误提示的内容
     */
    public String msg;

    private VolleyError mError = null;

    public MyVolleyError(VolleyError error){
        if(error != null){
            mError = error;
            getError(error.getCause());
        }
    }

    /**
     * 获取请求错误text
     *
     * @param arg0
     * @return
     */
    private void getError(Throwable arg0) {
        Log.e("VolleyError", toString());
        if (arg0 != null) {
            if (arg0.getCause() != null) {
                if (arg0.getCause().toString().indexOf("refused") != -1) {
                    setMsg("执行访问被禁止");
                } else if (arg0.getCause().toString().indexOf("timed out") != -1) {
                    setMsg("连接超时");
                }
            } else {
                setMsg(read(arg0.getMessage()));
            }
        }else if (this.toString().indexOf("TimeoutError")  != -1) {
            setMsg("连接超时");
        }else if (mError != null && mError.networkResponse != null) {
            setMsg(read(String.valueOf(mError.networkResponse.statusCode)));
        }else {
            setMsg("服务器连接异常，请检查网络是否通畅");
        }
    }

    /**
     * 进行IO流读写
     *
     * @return oStream.toString() or “文件读写失败”
     */
    public static String read(String error) {
        if (error == null || error.length() <= 0) {
            return "服务器连接异常，请检查网络是否通畅";
        }
        Log.e("ERROR", error);
        try {
            String[] firstCode = code.split("\n");
            for (int i = 0; i < firstCode.length; i++) {
                if (firstCode[i].indexOf(error) != -1) {
                    String[] split = firstCode[i].split(":");
                    return split[split.length - 1];
                }
            }
            return error;
        } catch (Exception e) {
            return error;
        }
    }

    private final static String code = "400:Bad Request:请求无效\n" +
            "401:Unauthorized:未授权\n" +
            "402:Payment Required:需要付款\n" +
            "403:Forbidden:禁止访问\n" +
            "404:Not Found:请求资源不存在\n" +
            "405:Method Not Allowed:资源被禁止\n" +
            "406:Not Acceptable:无法接受\n" +
            "407:Proxy Authentication Required:要求进行代理身份验证\n" +
            "408:Request Time-out:请求超时\n" +
            "409:Conflict:资源冲突\n" +
            "410:Gone:永远不可用\n" +
            "411:Length Required:需要内容长度头\n" +
            "412:Precondition Failed:先决条件失败\n" +
            "413:Request Entity Too Large:请求实体太大\n" +
            "414:Request-URI Too Large:请求URI太长\n" +
            "415:Unsupported Media Type:不支持的媒体类型\n" +
            "416:Requested range not satisfiable:所请求的范围无法满足\n" +
            "417:Expectation Failed:执行失败\n" +
            "500:Internal Server Error:内部服务器错误\n" +
            "501:Not Implemented:未实现的配置\n" +
            "502:Bad Gateway:网关错误\n" +
            "503:Service Unavailable:服务不可用\n" +
            "504:Gateway Time-out:网关超时\n" +
            "505:HTTP Version not supported:HTTP版本不受支持\n" +
            "500:Content-Type not allowed!:内部服务器错误\n" +
            "404:None, or more than one, Content-Type Header found!:请求资源不存在";

    /**
     * 错误提示的内容
     */
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
