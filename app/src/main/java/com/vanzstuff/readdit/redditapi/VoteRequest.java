package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request to vote on a thing
 * http://www.reddit.com/dev/api#POST_api_vote
 */
public class VoteRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_DIR = "dir";
    public static final String PARAM_ID = "id";
//    public static final String PARAM_UH = "uh";
    public static final int VOTE_UP = 1;
    public static final int VOTE_NONE = 0;
    public static final int VOTE_DOWN = -1;

    public static VoteRequest newInstance(int voteDirection, String id, String accessToken, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_DIR, voteDirection);
        params.put(PARAM_ID, id);
        return new VoteRequest(listener, errorListener, params, accessToken);
    }

    public VoteRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/vote" , null, listener, errorListener, params, accessToken);
    }
}

