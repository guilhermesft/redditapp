package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Get subreddits the user has a relationship with
 */
public class MySubredditRequest extends BaseRedditApiJsonRequest implements Listing{

    public static final String PATH_SUBSCRIBER = "subreddits/mine/subscriber";
    public static final String PATH_CONTRIBUTOR = "subreddits/mine/contributor";
    public static final String PATH_MODERATOR = "subreddits/mine/moderator";

    public static MySubredditRequest newInstance(String path, String before, String after, int count, int limit, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<String, Object>();
        if( before != null )
            params.put(PARAM_BEFORE, before);
        if ( after != null)
            params.put(PARAM_AFTER, after);
        if ( count >= 0 )
            params.put(PARAM_COUNT, count);
        if ( limit >= 0 )
            params.put(PARAM_LIMIT, limit);
        return new MySubredditRequest(path, listener, errorListener, params, accessToken);
    }


    public MySubredditRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.GET, path, null, listener, errorListener, params, accessToken);
    }
}
