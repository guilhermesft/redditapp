package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request to toggleHide a link
 * http://www.reddit.com/dev/api#POST_api_hide
 */
public class HideLinkRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public static HideLinkRequest newInstance(String id, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<>();
        if(Utils.stringNotNullOrEmpty(id))
            params.put(PARAM_ID, id);
        return new HideLinkRequest(listener, errorListener, params, accessToken);
    }

    public HideLinkRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/hide", null, listener, errorListener, params, accessToken);
    }
}
