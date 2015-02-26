package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request to toggleSave a link or comment
 * http://www.reddit.com/dev/api#POST_api_save
 */
public class SaveRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_ID = "id";
    public static final String PARAM_UH = "uh";

    public static SaveRequest newInstance(String id, String category, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<String, Object>();
        if(Utils.stringNotNullOrEmpty(id))
            params.put(PARAM_ID, id);
        if(Utils.stringNotNullOrEmpty(category))
            params.put(PARAM_CATEGORY, category);
        return new SaveRequest(listener, errorListener, params, accessToken);
    }

    public SaveRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/save" , null, listener, errorListener, params, accessToken);
    }

}
