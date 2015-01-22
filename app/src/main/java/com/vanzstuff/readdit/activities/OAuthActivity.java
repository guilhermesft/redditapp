package com.vanzstuff.readdit.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.GetMeRequest;
import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.redditapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OAuthActivity extends FragmentActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String REQUEST_TAG = "request_tag";
    private WebView mWebView;
    private String mAccessToken;

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
            if ( response.has( "name") ) {
                //shutdown the current logged user session
                ContentValues values = new ContentValues(1);
                values.put(ReadditContract.User.COLUMN_CURRENT, 0);
                getContentResolver().update(ReadditContract.User.CONTENT_URI, values, null, null);
                //login the new user session
                String name = response.getString("name");
                values.put(ReadditContract.User.COLUMN_NAME, name );
                values.put(ReadditContract.User.COLUMN_CURRENT, 1 );
                values.put(ReadditContract.User.COLUMN_ACCESSTOKEN, mAccessToken );
                long userID = ReadditContract.User.getUserId(getContentResolver().insert(ReadditContract.User.CONTENT_URI, values));
                if ( userID < 0 ) {
                    Logger.e("user insert failed");
                }
            }
        } catch (JSONException e) {
            Logger.e(e.getLocalizedMessage(), e);
            e.printStackTrace();
        }
        setResult(RESULT_OK);
        finish();
    }
}
