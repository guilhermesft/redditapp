package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by vanz on 24/11/14.
 */
public class SubscribeRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_SR = "sr";
    public static final String PARAM_UH = "uh";

    public SubscribeRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "/api/subscribe", null, listener, errorListener, params);
    }

}
