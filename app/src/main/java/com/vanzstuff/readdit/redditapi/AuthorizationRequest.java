package com.vanzstuff.readdit.redditapi;

import android.net.Uri;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.vanzstuff.readdit.Utils;

import org.apache.http.auth.Credentials;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 25/11/14.
 * https://github.com/reddit/reddit/wiki/OAuth2
 */
public class AuthorizationRequest extends StringRequest {

    public static final String PARAM_STATE = "state";
    public static final String PARAM_DURATION = "duration";
    public static final String PARAM_RESPONSE_TYPE = "response_type";
    public static final String PARAM_SCOPE = "scope";
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_REDIRECT_URI = "redirect_uri";
    public static final String DEFAULT_RESPONSE_TYPE = "code";
    public static final String SCOPE_MODPOSTS = "modposts";
    public static final String SCOPE_IDENTITY = "identity";
    public static final String SCOPE_EDIT = "edit";
    public static final String SCOPE_FLAIR = "flair";
    public static final String SCOPE_HISTORY = "history";
    public static final String SCOPE_MODCONFIG = "modconfig";
    public static final String SCOPE_MODFLAIR = "modflair";
    public static final String SCOPE_MODLOG = "modlog";
    public static final String SCOPE_MODWIKI = "modwiki";
    public static final String SCOPE_MYSUBREDDITS = "mysubreddits";
    public static final String SCOPE_READ = "read";
    public static final String SCOPE_REPORT = "report";
    public static final String SCOPE_SAVE = "save";
    public static final String SCOPE_SUBMIT = "submit";
    public static final String SCOPE_SUBSCRIBE = "subscribe";
    public static final String SCOPE_VOTE = "vote";
    public static final String SCOPE_WIKIEDIT = "wikiedit";
    public static final String SCOPE_WIKIREAD = "wikiread";
    public static final String DURATION_TEMPORARY = "temporary";
    public static final String DURATION_PERMANENT = "permanent";
    private Map<String, String> mParams;

    public AuthorizationRequest(Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, Object> params) {
        super(Method.GET, "https://ssl.reddit.com/api/v1/authorize", listener, errorListener);
        mParams = Utils.parserParamsToString(params);
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
        return url;
    }

}

