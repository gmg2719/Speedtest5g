package com.android.volley.toolbox;

import com.android.volley.Response;

import java.util.Map;

/**
 * @author JQJ
 **/
public interface MyListener<T> extends Response.Listener<T>{

    public void onResponseHeaders(Map<String, String> var1);

}
