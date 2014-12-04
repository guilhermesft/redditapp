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
import com.vanzstuff.readdit.redditapi.ComposeMessageRequest;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class ComposeMessageRequestTest extends AndroidTestCase {

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
        mFakeParams.put(ComposeMessageRequest.PARAM_API_TYPE, ComposeMessageRequest.DEFAULT_API_TYPE);
        mFakeParams.put(ComposeMessageRequest.PARAM_CAPTCHA, "captcha");
        mFakeParams.put(ComposeMessageRequest.PARAM_FROM_SR, "subreddit");
        mFakeParams.put(ComposeMessageRequest.PARAM_IDEN, "iden");
        mFakeParams.put(ComposeMessageRequest.PARAM_SUBJECT, "subject");
        mFakeParams.put(ComposeMessageRequest.PARAM_TEXT, "blablablab");
        mFakeParams.put(ComposeMessageRequest.PARAM_TO, "user");
        mFakeParams.put(ComposeMessageRequest.PARAM_UH, "fakeToken");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        ComposeMessageRequest request = new ComposeMessageRequest(new Response.Listener<JSONObject>() {
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
        assertEquals( "/api/compose", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Map<String, String> postParams = Util.getPostParams(new String(mMockStack.getLastRequest().getBody()));
        assertEquals(8, postParams.size());
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_API_TYPE), postParams.get(ComposeMessageRequest.PARAM_API_TYPE));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_CAPTCHA), postParams.get(ComposeMessageRequest.PARAM_CAPTCHA));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_FROM_SR), postParams.get(ComposeMessageRequest.PARAM_FROM_SR));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_IDEN), postParams.get(ComposeMessageRequest.PARAM_IDEN));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_SUBJECT), postParams.get(ComposeMessageRequest.PARAM_SUBJECT));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_TEXT), postParams.get(ComposeMessageRequest.PARAM_TEXT));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_TO), postParams.get(ComposeMessageRequest.PARAM_TO));
        assertEquals(mFakeParams.get(ComposeMessageRequest.PARAM_UH), postParams.get(ComposeMessageRequest.PARAM_UH));
    }
}
