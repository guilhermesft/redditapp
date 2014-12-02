package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to save a link or comment
 * http://www.reddit.com/dev/api#POST_api_save
 * Created by vanz on 21/11/14.
 */
public class SaveRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public SaveRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "api/save" , null, listener, errorListener, params);
    }

}
