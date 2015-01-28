package com.vanzstuff.readdit.redditapi;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

/**
 * Get the comment tree for a given Link article.
 * http://www.reddit.com/dev/api#GET_comments_{article}
 */
public class GetCommentRequest extends JsonArrayRequest {

    protected static final String PARAM_ARTICLE = "article";
    protected static final String PARAM_COMMENT = "comment";
    protected static final String PARAM_CONTEXT = "context";
    protected static final String PARAM_DEPTH = "depth";
    protected static final String PARAM_LIMIT = "limit";
    protected static final String PARAM_SHOW_EDIT = "showedits";
    protected static final String PARAM_SHOW_MORE = "showmore";
    protected static final String PARAM_SORT = "sort";
    public static final String PARAM_SORT_CONFIDENCE = "sort";
    public static final String PARAM_SORT_TOP = "top";
    public static final String PARAM_SORT_NEW = "new";
    public static final String PARAM_SORT_HOT = "hot";
    public static final String PARAM_SORT_CONTROVERSIAL = "controversial";
    public static final String PARAM_SORT_OLD = "old";
    public static final String PARAM_SORT_RANDOM = "random";

    private Map<String, String> mParams;
    private String mAccessToken;

    public static GetCommentRequest newInstance(String subreddit, String article, int context, int depth, int limit, boolean showedit, boolean showmore, String sortOrder, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String,Object> params = new HashMap<String, Object>();
        if(context >= 0 && context <= 8)
            params.put(PARAM_CONTEXT, context);
        if( depth > 0)
            params.put(PARAM_DEPTH, depth);
        if( limit > 0)
            params.put(PARAM_LIMIT, limit);
        params.put(PARAM_SHOW_EDIT, showedit);
        params.put(PARAM_SHOW_MORE, showmore);
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
        return new GetCommentRequest(String.format("r/%s/comments/%s", subreddit, article), listener, errorListener, params, accessToken);
    }

    protected GetCommentRequest(String path, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super( Uri.parse(BaseRedditApiJsonRequest.DEFAULT_BASE_URL).buildUpon().appendPath(path).build().toString(),listener, errorListener);
        mParams = RedditApiUtils.parserParamsToString(params);
        mAccessToken = accessToken;
    }
    @Override
    public String getUrl() {
        String url = super.getUrl();
        if (getMethod() == Method.GET){
            Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
            for(String key : mParams.keySet()){
                uriBuilder.appendQueryParameter(key, mParams.get(key));
            }
            url = uriBuilder.build().toString();
        }
        Logger.d(url);
        return url;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap(1);
        headers.put(BaseRedditApiJsonRequest.HEADER_AUTHORIZATION, "bearer " + mAccessToken);
        return headers;
    }
}
