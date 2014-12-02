package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to search subreddits by title and description
 * http://www.reddit.com/dev/api#GET_subreddits_search
 * Created by vanz on 24/11/14.
 */
public class SearchRequest extends BaseRedditApiJsonRequest implements Listing {

    public static final String PARAM_QUERY = "q";

    public SearchRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.GET, "subreddits/search", null, listener, errorListener, params);
    }

}
