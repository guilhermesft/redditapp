package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Get all link from a given subreddit
 */
public class GetLinks extends BaseRedditApiJsonRequest  implements Listing{

    public static GetLinks newInstance(String subreddit, String before, String after, int count, int limit, String accessToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        Map<String, Object> params = new HashMap<String, Object>();
        if( before != null )
            params.put(PARAM_BEFORE, before);
        if ( after != null)
            params.put(PARAM_AFTER, after);
        if ( count >= 0 )
            params.put(PARAM_COUNT, count);
        if ( limit >= 0 )
            params.put(PARAM_LIMIT, limit);
        return new GetLinks(String.format("r/%s", subreddit), null, listener, errorListener, accessToken);
    }

    public GetLinks( String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        super(Method.GET, path, jsonRequest, listener, errorListener, accessToken);
    }
}
