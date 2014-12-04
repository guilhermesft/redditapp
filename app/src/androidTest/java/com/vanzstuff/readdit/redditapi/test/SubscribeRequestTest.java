package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.HttpStackMock;
import com.vanzstuff.Util;
import com.vanzstuff.readdit.redditapi.SubscribeRequest;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class SubscribeRequestTest extends AndroidTestCase {

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
        mFakeParams.put(SubscribeRequest.PARAM_ACTION, SubscribeRequest.ACTION_SUBSCRIBE);
        mFakeParams.put(SubscribeRequest.PARAM_SR, "subreddit");
        mFakeParams.put(SubscribeRequest.PARAM_UH, "faketoken");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        SubscribeRequest request = new SubscribeRequest(new Response.Listener<JSONObject>() {
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
        assertEquals( "/api/subscribe", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Map<String, String> postParams = Util.getPostParams(new String(mMockStack.getLastRequest().getBody()));
        assertEquals(3, postParams.size());
        assertEquals(mFakeParams.get(SubscribeRequest.PARAM_ACTION), postParams.get(SubscribeRequest.PARAM_ACTION));
        assertEquals(mFakeParams.get(SubscribeRequest.PARAM_SR), postParams.get(SubscribeRequest.PARAM_SR));
        assertEquals(mFakeParams.get(SubscribeRequest.PARAM_UH), postParams.get(SubscribeRequest.PARAM_UH));
    }
}
