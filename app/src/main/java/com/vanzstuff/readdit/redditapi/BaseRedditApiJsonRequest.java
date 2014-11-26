package com.vanzstuff.readdit.redditapi;

import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vanzstuff.readdit.Utils;

import java.util.Map;
import java.util.Collections;

import org.json.JSONObject;

/**
 * Created by vanz on 18/11/14.
 */
public class BaseRedditApiJsonRequest extends JsonObjectRequest {

    protected static final String BASE_URL = "http://www.reddit.com";
    private Map<String, String> mParams;

    public BaseRedditApiJsonRequest(int method, String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(method, path, jsonRequest, listener, errorListener, null);
    }

    public BaseRedditApiJsonRequest(int method, String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(method, Uri.parse(BASE_URL).buildUpon().appendPath(path).build().toString(), jsonRequest, listener, errorListener);
        mParams = Utils.parserParamsToString(params);
    }

    public BaseRedditApiJsonRequest(String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(path, jsonRequest, listener, errorListener, null);
    }

    public BaseRedditApiJsonRequest(String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        this(jsonRequest == null ? Method.GET : Method.POST,path, jsonRequest, listener, errorListener, params);
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

    @Override
    public byte[] getBody() {
        StringBuilder builder = new StringBuilder();
        for(String key : mParams.keySet()){
            builder.append(key + "=" + mParams.get(key) + "&");
        }
        return builder.substring(0, builder.length()-1).getBytes();
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded";
    }
}
