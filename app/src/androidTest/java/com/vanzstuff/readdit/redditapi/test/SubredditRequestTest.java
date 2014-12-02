package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.SearchRequest;
import com.vanzstuff.readdit.redditapi.SubredditRequest;
import com.vanzstuff.readditapp.test.mocks.HttpStackMock;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class SubredditRequestTest extends AndroidTestCase{

    private HttpStackMock mMockStack;
    private RequestQueue mQueue;
    private Map<String, Object> mFakeParams;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMockStack = new HttpStackMock();
        mQueue = Volley.newRequestQueue(getContext(), mMockStack);
        mQueue.start();
        mFakeParams = new HashMap<String, Object>();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mQueue.stop();
    }

    public void testRequest() throws AuthFailureError {
        mFakeParams.put(SubredditRequest.PARAM_SHOW, SubredditRequest.DEFAULT_SHOW);
        mFakeParams.put(SubredditRequest.PARAM_LIMIT, 100);
        mFakeParams.put(SubredditRequest.PARAM_COUNT, 0);
        mFakeParams.put(SubredditRequest.PARAM_BEFORE, "before");
        mFakeParams.put(SubredditRequest.PARAM_AFTER, "after");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        SubredditRequest request = new SubredditRequest(SubredditRequest.URL_POPULAR,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                assertNotNull(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fail(error.getMessage());
            }
        }, mFakeParams);
        mQueue.add(request);
        while ( mMockStack.getLastRequest() == null);
        Uri requestUrl = Uri.parse(mMockStack.getLastRequest().getUrl());
        assertEquals( "https", requestUrl.getScheme());
        assertEquals("oauth.reddit.com", requestUrl.getAuthority());
        assertEquals( "/subreddits/popular", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        assertEquals(5, requestUrl.getQueryParameterNames().size());
        assertEquals(mFakeParams.get(SubredditRequest.PARAM_SHOW), requestUrl.getQueryParameter(SubredditRequest.PARAM_SHOW));
        assertEquals(mFakeParams.get(SubredditRequest.PARAM_LIMIT), Integer.parseInt(requestUrl.getQueryParameter(SubredditRequest.PARAM_LIMIT)));
        assertEquals(mFakeParams.get(SubredditRequest.PARAM_COUNT), Integer.parseInt(requestUrl.getQueryParameter(SubredditRequest.PARAM_COUNT)));
        assertEquals(mFakeParams.get(SubredditRequest.PARAM_BEFORE), requestUrl.getQueryParameter(SubredditRequest.PARAM_BEFORE));
        assertEquals(mFakeParams.get(SubredditRequest.PARAM_AFTER), requestUrl.getQueryParameter(SubredditRequest.PARAM_AFTER));
    }
}
