package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

public class GetMeRequest extends BaseRedditApiJsonRequest{
    public GetMeRequest(String accessToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "api/v1/me", null, listener, errorListener, accessToken);
    }
}
