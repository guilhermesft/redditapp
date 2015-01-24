package com.vanzstuff.readdit.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.AboutRequest;
import com.vanzstuff.readdit.redditapi.GetMeRequest;
import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.redditapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OAuthActivity extends FragmentActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String REQUEST_TAG = "request_tag";
    private WebView mWebView;
    private String mAccessToken;
    private ProgressDialog mProgresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        mWebView = (WebView) findViewById(R.id.oauth_dialog_webview);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //TODO - handle access denied and other errors
                if ( url.startsWith(RedditApiUtils.REDIRECT_URI)) {
                    if (url.contains(RedditApiUtils.AUTHORIZATION_RESPONSE_ACCESS_TOKEN)){
                        mWebView.stopLoading();
                        saveAccessToken(url);
                    }
                    return false;
                }
                mWebView.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl( RedditApiUtils.generateAuthorizationUri(UUID.randomUUID().toString(), RedditApiUtils.SCOPE_EDIT,
                RedditApiUtils.SCOPE_FLAIR, RedditApiUtils.SCOPE_MODLOG, RedditApiUtils.SCOPE_MODFLAIR, RedditApiUtils.SCOPE_MODCONFIG, RedditApiUtils.SCOPE_HISTORY,
                RedditApiUtils.SCOPE_IDENTITY, RedditApiUtils.SCOPE_MYSUBREDDITS, RedditApiUtils.SCOPE_READ).toString());

    }

    private void saveAccessToken(String url) {
        Pattern p = Pattern.compile("#access_token=([\\w|-]+)&" );
        Matcher matcher = p.matcher(url);
        if ( matcher.find() ){
            mAccessToken = matcher.group(1);
            GetMeRequest request = new GetMeRequest(mAccessToken, this, this);
            request.setTag(REQUEST_TAG);
            VolleyWrapper.getInstance().addToRequestQueue(request);
            mProgresDialog = ProgressDialog.show(this, "We're knowing you", "Wait just a minute. We need to know at least your name");
        }
    }

    /**
     * Check if the user is already register in the database
     * @param user
     * @return
     */
    private boolean isUserAlreadyRegister(String user){
        Cursor cursor = getContentResolver().query(ReadditContract.User.CONTENT_URI, new String[]{ReadditContract.User.COLUMN_NAME}, ReadditContract.User.COLUMN_NAME + "=?", new String[]{user}, null);
        try{
            return cursor.moveToFirst();
        }finally {
            cursor.close();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyWrapper.getInstance().cancel(REQUEST_TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Logger.e(error.getMessage(), error);
        Toast.makeText(this, "Ooops! Something wrong happened. =(", Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            Logger.d(response.toString());
            if ( response.has( "name") ) {
                ContentValues content = new ContentValues();
                if ( isUserAlreadyRegister(response.getString("name")) ){
                    //if the user is already register make he/she the current user
                    content.put(ReadditContract.User.COLUMN_CURRENT, 1);
                    getContentResolver().update(ReadditContract.User.CONTENT_URI, content, ReadditContract.User.COLUMN_NAME + "=?", new String[]{response.getString("name")});
                }else {
                    //new user, insert he/she in the database
                    content.put(ReadditContract.User.COLUMN_NAME, response.getString("name"));
                    content.put(ReadditContract.User.COLUMN_ACCESSTOKEN, mAccessToken);
                    content.put(ReadditContract.User.COLUMN_CURRENT, 1);
                    getContentResolver().insert(ReadditContract.User.CONTENT_URI, content);
                    setResult(RESULT_OK);
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
            setResult(RESULT_CANCELED);
            finish();
        }  finally {
            mProgresDialog.dismiss();
        }
        finish();
    }
}
