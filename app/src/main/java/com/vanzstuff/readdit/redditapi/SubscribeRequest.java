package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to subscribe/unsubscriba from a subreddit
 * http://www.reddit.com/dev/api#POST_api_subscribe
 * Created by vanz on 24/11/14.
 */
public class SubscribeRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ACTION = "action";
    public static final String PARAM_SR = "sr";
    public static final String PARAM_UH = "uh";
    public static final String ACTION_SUBSCRIBE = "sub";
    public static final String ACTION_UNSUBSCRIBE = "usub";

    public SubscribeRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/subscribe", null, listener, errorListener, params, accessToken);
    }

}
