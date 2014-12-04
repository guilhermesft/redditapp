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
import com.vanzstuff.readdit.redditapi.VoteRequest;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class VoteRequestTest extends AndroidTestCase {

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

    public void testRequestVote() throws AuthFailureError {
        mFakeParams.put(VoteRequest.PARAM_DIR, VoteRequest.VOTE_UP);
        mFakeParams.put(VoteRequest.PARAM_ID, "fakeThing");
        mFakeParams.put(VoteRequest.PARAM_UH, "fakeToken");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        VoteRequest request = new VoteRequest(new Response.Listener<JSONObject>() {
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
        while (mMockStack.getLastRequest() == null);
        Uri requestUrl = Uri.parse(mMockStack.getLastRequest().getUrl());
        assertEquals( "https", requestUrl.getScheme());
        assertEquals("oauth.reddit.com", requestUrl.getAuthority());
        assertEquals( "/api/vote", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Map<String, String> postParams = Util.getPostParams(new String(mMockStack.getLastRequest().getBody()));
        assertEquals(3, postParams.size());
        assertEquals(mFakeParams.get(VoteRequest.PARAM_DIR), Integer.parseInt(postParams.get(VoteRequest.PARAM_DIR)));
        assertEquals(mFakeParams.get(VoteRequest.PARAM_ID), postParams.get(VoteRequest.PARAM_ID));
        assertEquals(mFakeParams.get(VoteRequest.PARAM_UH), postParams.get(VoteRequest.PARAM_UH));
    }
}
