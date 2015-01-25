package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetLinkSorted extends BaseRedditApiJsonRequest implements Listing{

    public static final String PATH_TOP = "r/%s/top.json";
    public static final String PATH_CONTROVERSIAL = "/%s/controversial.json";
    public static final String T_HOUR = "hour";
    public static final String T_DAY = "day";
    public static final String T_WEEK = "week";
    public static final String T_MONTH = "month";
    public static final String T_YEAR = "year";
    public static final String T_ALL = "all";
    public static final String PARAM_T = "t";


    public static GetLinkSorted newInstance(String path, String subreddit, String t, String before, String after, int count, int limit, String accessToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        Map<String, Object> params = new HashMap<String, Object>();
        if( before != null )
            params.put(PARAM_BEFORE, before);
        if ( after != null)
            params.put(PARAM_AFTER, after);
        if ( count >= 0 )
            params.put(PARAM_COUNT, count);
        if ( limit >= 0 )
            params.put(PARAM_LIMIT, limit);
        if (Utils.stringNotNullOrEmpty(t))
            params.put(PARAM_T, t);
        return new GetLinkSorted(String.format(path, subreddit), null, listener, errorListener, accessToken);
    }

    public GetLinkSorted( String path, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken) {
        super(Method.GET, path, null , listener, errorListener, accessToken);
    }
}
