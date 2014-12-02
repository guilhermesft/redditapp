package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to get all subreddits
 * http://www.reddit.com/dev/api#GET_subreddits_{where}
 * Created by vanz on 24/11/14.
 */
public class SubredditRequest extends BaseRedditApiJsonRequest implements Listing{

    public static final String URL_POPULAR = "subreddits/popular";
    public static final String URL_NEW = "subreddits/new";

    public SubredditRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.GET, path, null, listener, errorListener, params);
    }

}
