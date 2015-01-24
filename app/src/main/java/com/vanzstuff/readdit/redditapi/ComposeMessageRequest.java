package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Request used to handle message composition
 * http://www.reddit.com/dev/api#POST_api_compose
 * Created by vanz on 24/11/14.
 */
public class ComposeMessageRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_CAPTCHA = "captcha";
    public static final String PARAM_FROM_SR = "from_sr";
    public static final String PARAM_IDEN = "iden";
    public static final String PARAM_SUBJECT = "subject";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_TO = "to";
    public static final String PARAM_UH = "uh";
    public static final String PARAM_API_TYPE = "api_type";
    public static final String DEFAULT_API_TYPE = "json";

    public ComposeMessageRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params, String accessToken) {
        super(Method.POST, "api/compose", null, listener, errorListener, params, accessToken);
    }

}
