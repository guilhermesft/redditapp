package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

/**
 * Request to get all subreddits
 * http://www.reddit.com/dev/api#GET_subreddits_{where}
 */
public class SubredditRequest extends BaseRedditApiJsonRequest implements Listing{

    public static final String PATH_POPULAR = "subreddits/popular";
    public static final String PATH_NEW = "subreddits/new";

    public static SubredditRequest newInstance(String path, String before, String after, int count, int limit, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_BEFORE, before);
        params.put(PARAM_AFTER, after);
        params.put(PARAM_COUNT, count <= 0 ? 0 : count);
        params.put(PARAM_LIMIT, limit);
        params.put(PARAM_SHOW, "all");
        SubredditRequest instance = new SubredditRequest(path, listener, errorListener, params, accessToken);
        return instance;
    }

    public SubredditRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.GET, path, null, listener, errorListener, params, accessToken);
    }

}
