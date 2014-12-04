package com.vanzstuff;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vanz on 17/11/14.
 */
public class HttpStackMock implements HttpStack {

    private HttpResponse mResponse;
    private Request mLastRequest;
    private Map<String, String> mLastAdditionalHeaders;

    public void setResponse(HttpResponse response){
        mResponse = response;
    }

    public Map<String, String> getLastAdditionalHeaders() {
        return mLastAdditionalHeaders;
    }

    public Request getLastRequest() {
        return mLastRequest;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        mLastRequest = request;
        mLastAdditionalHeaders = additionalHeaders;
        return mResponse;
    }
}
