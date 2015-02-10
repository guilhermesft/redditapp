package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Request used to get the messages
 * http://www.reddit.com/dev/api#GET_message_inbox
 */
public class MessageRequest extends BaseRedditApiJsonRequest implements Listing{

    public static final String PARAM_MARK = "mark";
    public static final String PARAM_MID = "mid";
    public static final String PATH_MESSAGE_INBOX = "message/inbox";
    public static final String PATH_MESSAGE_UNREAD = "message/unread";
    public static final String PATH_MESSAGE_SENT = "message/sent";
    public static final String DEFAULT_SHOW = "all";

    public static MessageRequest newInstance(String path, String mark, String mid, String before, String after, String count, String limit, String show, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String accessToken){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_BEFORE, before);
        params.put(PARAM_AFTER, after);
        params.put(PARAM_COUNT, count);
        params.put(PARAM_LIMIT, limit);
        params.put(PARAM_SHOW, show);
        params.put(PARAM_MARK, mark);
        params.put(PARAM_MID, mid);
        MessageRequest instance = new MessageRequest(path, listener, errorListener, params, accessToken);
        return instance;
    }

    public MessageRequest(String path, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.GET, path , null, listener, errorListener, params, accessToken);
    }

}
