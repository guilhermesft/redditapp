package com.vanzstuff.readdit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.readdit.service.OAuthService;

import java.util.UUID;

public class OAuthActivity extends FragmentActivity {

    public static final int RESULT_OAUTH_FAILED = 2;
    public static final int REQUEST_LOGIN = 1;
    private WebView mWebView;
    private String mState;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        mWebView = (WebView) findViewById(R.id.oauth_dialog_webview);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri pUrl = Uri.parse(url);
                if (url.startsWith(RedditApiUtils.REDIRECT_URI)) {
                    if (pUrl.getQueryParameterNames().contains("error") || !mState.equals(pUrl.getQueryParameter("state"))) {
                        //handle error
                        setResult(RESULT_OAUTH_FAILED);
                        finish();
                        return false;
                    } else {
                        mProgressDialog = ProgressDialog.show(OAuthActivity.this, "Wait a minute...", "I'm talking with reddit's servers");
                        getAccessToken(pUrl.getQueryParameter("code"));
                        mWebView.loadUrl("about:blank");
                        return false;
                    }
                }
                if ("www.reddit.com".equals(pUrl.getAuthority())){
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });
        mState = UUID.randomUUID().toString();
        mWebView.loadUrl(RedditApiUtils.generateAuthorizationUri(mState, RedditApiUtils.SCOPE_HISTORY,
                RedditApiUtils.SCOPE_IDENTITY, RedditApiUtils.SCOPE_MYSUBREDDITS, RedditApiUtils.SCOPE_READ, RedditApiUtils.SCOPE_VOTE,
                RedditApiUtils.SCOPE_SAVE, RedditApiUtils.SCOPE_REPORT).toString());

    }

    private void getAccessToken(String code) {
        Intent intent = new Intent(this, OAuthService.class);
        intent.putExtra(OAuthService.EXTRA_CODE, code);
        intent.putExtra(OAuthService.EXTRA_RESULT_RECIEVER, new ResultReceiver(null){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                mProgressDialog.dismiss();
                if (resultCode == OAuthService.RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_OAUTH_FAILED);
                    finish();
                }
            }
        });
        startService(intent);
    }
}
