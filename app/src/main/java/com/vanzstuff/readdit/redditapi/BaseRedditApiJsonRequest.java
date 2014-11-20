package com.vanzstuff.readdit.redditapi;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.Map;
import java.util.Collections;

import org.json.JSONObject;

/**
 * Created by vanz on 18/11/14.
 */
public class BaseRedditApiJsonRequest extends JsonObjectRequest {

    private Map<String, String> mParams;

    public BaseRedditApiJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(method, url, jsonRequest, listener, errorListener, null);
    }

    public BaseRedditApiJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(method, url, jsonRequest, listener, errorListener);
        mParams = parserParamsToString(params);
    }

    public BaseRedditApiJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(url, jsonRequest, listener, errorListener, null);
    }

    public BaseRedditApiJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        this(jsonRequest == null ? Method.GET : Method.POST,url, jsonRequest, listener, errorListener, params);
    }

    /**
     * Convert all parameteres in objParams to a valid string to use in the request
     * @param objParams map with parameteres object to convert
     * @return a map with string values
     */
    public Map<String, String> parserParamsToString(Map<String, Object> objParams){
        Map<String, String> parserParams = Collections.emptyMap();
        if(objParams != null ) {
            for (String key : objParams.keySet()) {
                parserParams.put(key, String.valueOf(objParams.get(key)));
            }
        }
        return parserParams;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public String getUrl() {
        String url = super.getUrl();
        if (getMethod() == Method.GET){
            Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
            for(String key : mParams.keySet()){
                uriBuilder.appendQueryParameter(key, mParams.get(key));
            }
            url = uriBuilder.build().toString();
        }
        return url;

    }
}
