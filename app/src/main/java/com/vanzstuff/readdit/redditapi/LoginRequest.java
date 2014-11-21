package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;
import java.util.Map;

/**
 * Created by vanz on 20/11/14.
 */
public class LoginRequest extends BaseRedditApiJsonRequest {

    public LoginRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "api/login" , null, listener, errorListener, params);
    }
}
