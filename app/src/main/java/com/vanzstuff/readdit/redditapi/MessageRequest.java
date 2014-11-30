package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by vanz on 24/11/14.
 */
public class MessageRequest extends BaseRedditApiJsonRequest implements Listing{

    public static final String PARAM_MARK = "mark";
    public static final String PARAM_MID = "mid";
    public static final String PATH_MESSAGE_INBOX = "/message/inbox";
    public static final String PATH_MESSAGE_UNREAD = "/message/unread";
    public static final String PATH_MESSAGE_SENT = "/message/sent";
    public static final String DEFAULT_SHOW = "all";


    public MessageRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.GET, path , null, listener, errorListener, params);
    }

}
