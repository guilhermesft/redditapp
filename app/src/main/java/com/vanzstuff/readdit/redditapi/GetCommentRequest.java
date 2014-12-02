package com.vanzstuff.readdit.redditapi;

import android.util.ArrayMap;

import com.android.volley.Response;
import com.vanzstuff.readdit.Utils;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to retrieve the comment tree for a given article
 * http://www.reddit.com/dev/api#GET_comments_{article}
 * Created by vanz on 24/11/14.
 */
public class GetCommentRequest extends BaseRedditApiJsonRequest {

    protected static final String PARAM_ARTICLE = "article";
    protected static final String PARAM_COMMENT = "comment";
    protected static final String PARAM_CONTEXT = "context";
    protected static final String PARAM_DEPTH = "depth";
    protected static final String PARAM_LIMIT = "limit";
    protected static final String PARAM_SORT = "sort";
    public static final String PARAM_SORT_CONFIDENCE = "sort";
    public static final String PARAM_SORT_TOP = "top";
    public static final String PARAM_SORT_NEW = "new";
    public static final String PARAM_SORT_HOT = "hot";
    public static final String PARAM_SORT_CONTROVERSIAL = "controversial";
    public static final String PARAM_SORT_OLD = "old";
    public static final String PARAM_SORT_RANDOM = "random";

    public static GetCommentRequest newInstance(String subreddit, String article, String ID36Article, String ID36comment, int context, int depth, int limit, String sortOrder,  Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        Map<String,Object> params = new ArrayMap<String, Object>();
        if(Utils.stringNotNullOrEmpty(ID36Article))
            params.put(PARAM_ARTICLE, ID36Article);
        if(Utils.stringNotNullOrEmpty(ID36comment))
            params.put(PARAM_COMMENT, ID36comment);
        if(context >= 0 && context <= 8)
            params.put(PARAM_CONTEXT, context);
        if( depth > 0)
            params.put(PARAM_DEPTH, depth);
        if( limit > 0)
            params.put(PARAM_LIMIT, limit);
        if(Utils.stringNotNullOrEmpty(sortOrder)){
            boolean validSort = false;
            if (PARAM_SORT_CONFIDENCE.equals(sortOrder) ||
                PARAM_SORT_TOP.equals(sortOrder) ||
                PARAM_SORT_NEW.equals(sortOrder) ||
                PARAM_SORT_HOT.equals(sortOrder) ||
                PARAM_SORT_CONTROVERSIAL.equals(sortOrder) ||
                PARAM_SORT_OLD.equals(sortOrder) ||
                PARAM_SORT_RANDOM.equals(sortOrder))
                    validSort = true;
            if(validSort)
                params.put(PARAM_SORT, sortOrder);
        }
        return new GetCommentRequest("r/" + subreddit + "/comments/" + article, listener, errorListener, params );
    }

    protected GetCommentRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.GET, path, null, listener, errorListener, params);
    }
}
