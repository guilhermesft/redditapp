package com.vanzstuff.readdit.redditapi;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenRequest extends BaseRedditApiJsonRequest {

    public static RefreshTokenRequest newInstance(String refreshToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);
        return new RefreshTokenRequest(listener, errorListener, params);
    }


    public RefreshTokenRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "https://www.reddit.com", "api/v1/access_token", null, listener, errorListener, params, null);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        String creds = String.format("%s:%s", RedditApiUtils.CLIENT_ID,null);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        headers.put("Authorization", auth);
        headers.put("User-agent", "com.vanzstuff.redditapp:v0.9 (by /u/jvanz");
        return headers;
    }

}
