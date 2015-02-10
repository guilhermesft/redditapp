package com.vanzstuff.readdit.redditapi;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Reddit API requests
 */
public class BaseRedditApiJsonRequest extends JsonObjectRequest {

    protected static final String DEFAULT_BASE_URL = "https://oauth.reddit.com";
    protected static final String HEADER_AUTHORIZATION = "Authorization";
    private Map<String, String> mParams;
    private String mAccessToken;

    public BaseRedditApiJsonRequest(int method, String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        this(method, DEFAULT_BASE_URL,  path, jsonRequest, listener, errorListener, null, accessToken);
    }

    public BaseRedditApiJsonRequest(int method, String baseUrl,  String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(method, Uri.parse(baseUrl).buildUpon().appendPath(path).build().toString(), jsonRequest, listener, errorListener);
        mParams = RedditApiUtils.parserParamsToString(params);
        mAccessToken = accessToken;
    }

    public BaseRedditApiJsonRequest(String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        this(path, jsonRequest, listener, errorListener, null, accessToken);
    }

    public BaseRedditApiJsonRequest(String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        this(jsonRequest == null ? Method.GET : Method.POST, DEFAULT_BASE_URL, path, jsonRequest, listener, errorListener, params, accessToken);
    }

    public BaseRedditApiJsonRequest(int method, String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        this(method, DEFAULT_BASE_URL, path, jsonRequest, listener, errorListener, params, accessToken);
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
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap(1);
        headers.put(HEADER_AUTHORIZATION, "bearer " + mAccessToken);
        return headers;
    }
}
