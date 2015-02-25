package com.vanzstuff.readdit.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.GetMeRequest;
import com.vanzstuff.readdit.redditapi.OAuthTokenRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class OAuthService extends IntentService implements Response.ErrorListener
{

    public static final String EXTRA_CODE = "code";
    public static final String EXTRA_RESULT_RECIEVER = "result_reciever";
    private static final String RESULT_RESULT_ERROR_MESSAGE = "error_msg";
    public static final int RESULT_OK = 1;
    public static final int RESULT_FAILED = -1;
    private ResultReceiver mResultReciever;
    private String mAccessToken;
    private String mTokenType;
    private String mExpiresIn;
    private String mScope;
    private String mRefreshToken;

    public OAuthService() {
        super("OAuthService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mResultReciever = (ResultReceiver) intent.getParcelableExtra(EXTRA_RESULT_RECIEVER);
        OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.newInstance(intent.getStringExtra(EXTRA_CODE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("error")) {
                        Bundle error = new Bundle(1);
                        error.putString(RESULT_RESULT_ERROR_MESSAGE, response.getString("error"));
                        mResultReciever.send(RESULT_FAILED, error);
                        return;
                    }
                    //the app allow just one logged user
                    getContentResolver().delete(ReadditContract.User.CONTENT_URI, null, null);
                    mAccessToken = response.getString("access_token");
                    mTokenType = response.getString("token_type");
                    mExpiresIn = response.getString("expires_in");
                    mScope = response.getString("scope");
                    mRefreshToken = response.getString("refresh_token");
                    GetMeRequest getMeRequest = new GetMeRequest(mAccessToken, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("name")) {
                                    ContentValues content = new ContentValues();
                                    content.put(ReadditContract.User.COLUMN_CURRENT, 0);
                                    content.put(ReadditContract.User.COLUMN_ACCESSTOKEN, mAccessToken);
                                    content.put(ReadditContract.User.COLUMN_TOKEN_TYPE, mTokenType);
                                    content.put(ReadditContract.User.COLUMN_EXPIRES_IN, mExpiresIn);
                                    content.put(ReadditContract.User.COLUMN_SCOPE, mScope);
                                    content.put(ReadditContract.User.COLUMN_REFRESH_TOKEN, mRefreshToken);
                                    //new user, insert he/she in the database
                                    content.put(ReadditContract.User.COLUMN_NAME, response.getString("name"));
                                    content.put(ReadditContract.User.COLUMN_CURRENT, 1);
                                    getContentResolver().insert(ReadditContract.User.CONTENT_URI, content);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logger.e(e.getLocalizedMessage(), e);
                                mResultReciever.send(RESULT_FAILED, null);
                            }
                            mResultReciever.send(RESULT_OK, null);
                        }
                    }, OAuthService.this);
                    VolleyWrapper.getInstance().getRequestQueue().add(getMeRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
        VolleyWrapper.getInstance().getRequestQueue().add(oAuthTokenRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Logger.e(error.getLocalizedMessage(), error);
        mResultReciever.send(RESULT_FAILED, null);
    }
}
