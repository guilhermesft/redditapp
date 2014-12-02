package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request to mark all messages for a user as read
 * http://www.reddit.com/dev/api#POST_api_read_all_messages
 * Created by vanz on 24/11/14.
 */
public class ReadMessageRequest extends BaseRedditApiJsonRequest{

    public static final String PARAM_UH = "uh";

    public ReadMessageRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "api/read_all_messages", null, listener, errorListener, params);
    }

}
