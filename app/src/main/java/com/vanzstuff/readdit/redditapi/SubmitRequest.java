package com.vanzstuff.readdit.redditapi;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by vanz on 24/11/14.
 */
public class SubmitRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_CAPTCHA = "captcha";
    public static final String PARAM_EXTENSION = "extension";
    public static final String PARAM_IDEN = "iden";
    public static final String PARAM_KIND = "kind";
    public static final String PARAM_RESUBMIT = "resubmit";
    public static final String PARAM_SENDREPLIES = "sendreplies";
    public static final String PARAM_SR = "sr";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_THEN = "then";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_UH = "uh";
    public static final String PARAM_URL = "url";

    public SubmitRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.POST, "/api/submit", null, listener, errorListener, params);
    }
}
