package com.vanzstuff.readdit.redditapi;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;


public class GetMeRequest extends BaseRedditApiJsonRequest{
    private final String mToken;

    public GetMeRequest(String accessToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "api/v1/me", null, listener, errorListener);
        mToken = accessToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "bearer " + mToken);
        return headers;
    }
}
