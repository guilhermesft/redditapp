package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request to unhide a link
 * http://www.reddit.com/dev/api#POST_api_unhide
 * Created by vanz on 24/11/14.
 */
public class UnhideRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public static UnhideRequest newInstance(String id, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<>();
        if(Utils.stringNotNullOrEmpty(id))
            params.put(PARAM_ID, id);
        return new UnhideRequest(listener, errorListener, params, accessToken);
    }

    public UnhideRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/unhide", null, listener, errorListener, params, accessToken);
    }

}
