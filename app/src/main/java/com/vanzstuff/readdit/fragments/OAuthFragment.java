package com.vanzstuff.readdit.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.redditapp.R;

public class OAuthFragment extends DialogFragment {

    private static final String ARG_URL = "arg_url";
    private WebView mWebView;

    public static OAuthFragment newInstance(){
        OAuthFragment instance = new OAuthFragment();
        Bundle args = new Bundle(1);
        args.putString(ARG_URL, "https://ssl.reddit.com/api/v1/authorize" );
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.oauth_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.oauth_dialog_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ( url.startsWith(RedditApiUtils.REDIRECT_URI)) {
                    if (url.contains(RedditApiUtils.AUTHORIZATION_RESPONSE_ACCESS_TOKEN))
                        saveAccessToken(url);
                }
                return false;
            }
        });
        if(getArguments().containsKey(ARG_URL))
            mWebView.loadUrl(getArguments().getString(ARG_URL));
        else
            throw new UnsupportedOperationException("Authorization url not found");
    }

    private void saveAccessToken(String url) {
        String accessToken = Uri.parse(url).getQueryParameter(RedditApiUtils.AUTHORIZATION_RESPONSE_ACCESS_TOKEN);
        UserSession.getInstance().setAccessToken(accessToken);
        //TODO - get the username
        this.dismiss();
    }
}
