package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

public class AboutRequest extends BaseRedditApiJsonRequest{

    public static AboutRequest newInstance(String username, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        return new AboutRequest(String.format("user/%s/about.json", username), listener, errorListener, accessToken );
    }

    public AboutRequest(String path,  Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        super(Method.GET, path, null, listener, errorListener, accessToken);
    }
}
