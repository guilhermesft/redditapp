package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.CommentReplyRequest;
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
public class CommentReplyRequestTest extends AndroidTestCase {

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
        mFakeParams.put(CommentReplyRequest.PARAM_TEXT, "category");
        mFakeParams.put(CommentReplyRequest.PARAM_API_TYPE, CommentReplyRequest.DEFAULT_API_TYPE);
        mFakeParams.put(CommentReplyRequest.PARAM_THING_ID, "thing");
        mFakeParams.put(CommentReplyRequest.PARAM_UH, "fakenToken");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        CommentReplyRequest request = new CommentReplyRequest(new Response.Listener<JSONObject>() {
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
        assertEquals( "/api/comment", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Map<String, String> postParams = Util.getPostParams(new String(mMockStack.getLastRequest().getBody()));
        assertEquals(4, postParams.size());
        assertEquals(mFakeParams.get(CommentReplyRequest.PARAM_UH), postParams.get(CommentReplyRequest.PARAM_UH));
        assertEquals(mFakeParams.get(CommentReplyRequest.PARAM_THING_ID), postParams.get(CommentReplyRequest.PARAM_THING_ID));
        assertEquals(mFakeParams.get(CommentReplyRequest.PARAM_API_TYPE), postParams.get(CommentReplyRequest.PARAM_API_TYPE));
        assertEquals(mFakeParams.get(CommentReplyRequest.PARAM_TEXT), postParams.get(CommentReplyRequest.PARAM_TEXT));
    }
}
