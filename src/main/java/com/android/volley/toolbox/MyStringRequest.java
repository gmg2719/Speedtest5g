package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

/**
 * @author JQJ
 **/
public class MyStringRequest extends StringRequest{

    private MyListener onResponseHeadersListener = null;

    public MyStringRequest(int method, String url, MyListener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        onResponseHeadersListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (this.onResponseHeadersListener != null) {
            this.onResponseHeadersListener.onResponseHeaders(response.headers);
        }
        return super.parseNetworkResponse(response);
    }
}
