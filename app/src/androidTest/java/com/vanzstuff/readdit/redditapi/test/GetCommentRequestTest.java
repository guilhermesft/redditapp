package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.GetCommentRequest;
import com.vanzstuff.readdit.redditapi.SaveRequest;
import com.vanzstuff.readditapp.test.mocks.HttpStackMock;
import com.vanzstuff.readditapp.test.mocks.Util;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class GetCommentRequestTest extends AndroidTestCase {
    private static final String FAKE_SUBREDDIT = "";
    private static final String FAKE_ARTICLE = "";
    private static final String FAKE_LINK = "";
    private static final String FAKE_COMMENT = "";
    private static final int FAKE_CONTEXT = 0;
    private static final int FAKE_DEPTH = 1;
    private static final int FAKE_LIMIT = 1;
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
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        GetCommentRequest request = GetCommentRequest.newInstance(FAKE_SUBREDDIT, FAKE_ARTICLE, FAKE_LINK, FAKE_COMMENT, FAKE_CONTEXT, FAKE_DEPTH, FAKE_LIMIT, GetCommentRequest.PARAM_SORT_HOT, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                assertNotNull(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fail(error.getMessage());
            }
        });
        mQueue.add(request);
        while ( mMockStack.getLastRequest() == null);
        Uri requestUrl = Uri.parse(mMockStack.getLastRequest().getUrl());
        assertEquals( "https", requestUrl.getScheme());
        assertEquals("oauth.reddit.com", requestUrl.getAuthority());
        assertEquals( "/r/" + FAKE_SUBREDDIT + "/comments/" + FAKE_ARTICLE, requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        assertEquals(5, requestUrl.getQueryParameterNames().size());
        assertEquals(FAKE_LINK, requestUrl.getQueryParameter("link"));
        assertEquals(FAKE_COMMENT, requestUrl.getQueryParameter("comment"));
        assertEquals(FAKE_CONTEXT, requestUrl.getQueryParameter("context"));
        assertEquals(FAKE_DEPTH, requestUrl.getQueryParameter("depth"));
        assertEquals(FAKE_LIMIT, requestUrl.getQueryParameter("limit"));
    }
}
