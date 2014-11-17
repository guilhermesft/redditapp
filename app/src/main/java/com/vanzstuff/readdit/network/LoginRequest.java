package com.vanzstuff.readdit.network;

import android.util.ArrayMap;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by vanz on 17/11/14.
 */
public class LoginRequest extends BaseJsonRequest {

    public LoginRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, ArrayMap<String, Object> params) {
        super(Method.GET, "/api/login" , null, listener, errorListener, params);
    }

}
