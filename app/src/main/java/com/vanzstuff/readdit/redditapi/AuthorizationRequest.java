package com.vanzstuff.readdit.redditapi;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import org.apache.http.auth.Credentials;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 25/11/14.
 */
public class AuthorizationRequest extends BaseRedditApiJsonRequest {

    public static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String PARAM_DEVICE_ID = "device_id";
    public static final String PARAM_AUTHORIZATION_CODE = "authorization_code";
    public static final String DEFAULT_GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private Credentials mCredentials;

    public AuthorizationRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Credentials credentialAuth, Map<String, Object> params) {
        super(Method.POST, "https://ssl.reddit.com", "api/v1/access_token",  null, listener, errorListener, params);
        mCredentials = credentialAuth;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        String creds = String.format("%s:%s", mCredentials.getUserPrincipal().getName(),mCredentials.getPassword());
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put(HEADER_AUTHORIZATION, auth);
        return params;
    }
}

