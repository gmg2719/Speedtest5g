package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * String类型，JSON格式请求
 * @author zwq
 *
 */
public class VolleyJsonToStringRequest extends JsonRequest<String> {

    private MyListener onResponseHeadersListener = null;

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param requestBody A {@link String} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VolleyJsonToStringRequest(int method, String url, String requestBody,
                             MyListener<String> listener, ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        onResponseHeadersListener = listener;
    }

    /**
     * Creates a new request.
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VolleyJsonToStringRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VolleyJsonToStringRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
    }

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public VolleyJsonToStringRequest(int method, String url, JSONObject jsonRequest,
            Listener<String> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     */
    public VolleyJsonToStringRequest(String url, JSONObject jsonRequest, Listener<String> listener,
            ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
    	if (onResponseHeadersListener != null) {
            onResponseHeadersListener.onResponseHeaders(response.headers);
        }
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
