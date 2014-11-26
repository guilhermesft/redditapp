package com.vanzstuff.readdit.redditapi;

import android.util.ArrayMap;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.auth.Credentials;
import org.json.JSONObject;

import java.util.Map;
import java.util.Collections;

/**
 * Created by vanz on 25/11/14.
 */
public class AuthorizationRequest extends JsonObjectRequest {

    private static final String PARAM_GRANT_TYPE = "grant_type";
    public static final String PARAM_DEVICE_ID = "device_id";
    private static final String DEFAULT_GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private String mDeviceId;
    private Credentials mCredentials;

    public AuthorizationRequest(JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Credentials credentialAuth, String deviceId) {
        super(Method.POST, "https://ssl.reddit.com/api/v1/access_token", jsonRequest, listener, errorListener);
        mDeviceId = deviceId;
        mCredentials = credentialAuth;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new ArrayMap<String, String>();
        params.put(PARAM_GRANT_TYPE, DEFAULT_GRANT_TYPE);
        params.put(PARAM_DEVICE_ID, mDeviceId);
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        ArrayMap<String, String> params = new ArrayMap<String, String>();
        String creds = String.format("%s:%s", mCredentials.getUserPrincipal().getName(),mCredentials.getPassword());
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        return params;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded";
    }
}
