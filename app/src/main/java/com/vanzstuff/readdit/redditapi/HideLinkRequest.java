package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to hide a link
 * http://www.reddit.com/dev/api#POST_api_hide
 */
public class HideLinkRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public HideLinkRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/hide", null, listener, errorListener, params, accessToken);
    }
}
