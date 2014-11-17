package com.vanzstuff.readdit.network;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

/**
 * Created by vanz on 17/11/14.
 */
public class BaseJsonRequest extends JsonObjectRequest {

    protected final static String BASE_URL = "www.reddit.com";
    protected ArrayMap<String, Object> mParams;

    public BaseJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, ArrayMap<String, Object> params) {
        super(method, url, jsonRequest, listener, errorListener);
        mParams = params;
    }

}
