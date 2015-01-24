package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request used to submit a new comment or reply a message
 * http://www.reddit.com/dev/api#POST_api_comment
 * Created by vanz on 24/11/14.
 */
public class CommentReplyRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_TEXT = "text";
    public static final String PARAM_API_TYPE = "api_type";
    public static final String PARAM_THING_ID = "thing_id";
    public static final String PARAM_UH = "uh";
    public static final String DEFAULT_API_TYPE = "json";

    public CommentReplyRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/comment", null, listener, errorListener, params, accessToken);
    }
}
