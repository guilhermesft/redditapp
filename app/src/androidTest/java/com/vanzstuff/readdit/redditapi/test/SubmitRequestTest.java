package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.SubmitRequest;
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
public class SubmitRequestTest extends AndroidTestCase {

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
        mFakeParams.put(SubmitRequest.PARAM_CAPTCHA, "PARAM_CAPTCHA");
        mFakeParams.put(SubmitRequest.PARAM_EXTENSION, "PARAM_EXTENSION");
        mFakeParams.put(SubmitRequest.PARAM_IDEN, "iden");
        mFakeParams.put(SubmitRequest.PARAM_KIND, SubmitRequest.KIND_LINK);
        mFakeParams.put(SubmitRequest.PARAM_RESUBMIT, true);
        mFakeParams.put(SubmitRequest.PARAM_SENDREPLIES, true);
        mFakeParams.put(SubmitRequest.PARAM_SR, "subreddit");
        mFakeParams.put(SubmitRequest.PARAM_TEXT, "text");
        mFakeParams.put(SubmitRequest.PARAM_THEN, SubmitRequest.THEN_COMMENTS);
        mFakeParams.put(SubmitRequest.PARAM_TITLE, "title");
        mFakeParams.put(SubmitRequest.PARAM_UH, "faketoken");
        mFakeParams.put(SubmitRequest.PARAM_URL, "http://127.0.0.1/test");
        mFakeParams.put(SubmitRequest.PARAM_API_TYPE, SubmitRequest.DEFAULT_API_TYPE);
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        SubmitRequest request = new SubmitRequest(new Response.Listener<JSONObject>() {
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
        assertEquals( "/api/submit", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Map<String, String> postParams = Util.getPostParams(new String(mMockStack.getLastRequest().getBody()));
        assertEquals(13, postParams.size());
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_API_TYPE), postParams.get(SubmitRequest.PARAM_API_TYPE));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_CAPTCHA), postParams.get(SubmitRequest.PARAM_CAPTCHA));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_EXTENSION), postParams.get(SubmitRequest.PARAM_EXTENSION));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_IDEN), postParams.get(SubmitRequest.PARAM_IDEN));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_KIND), postParams.get(SubmitRequest.PARAM_KIND));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_RESUBMIT), Boolean.parseBoolean(postParams.get(SubmitRequest.PARAM_RESUBMIT)));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_SENDREPLIES), Boolean.parseBoolean(postParams.get(SubmitRequest.PARAM_SENDREPLIES)));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_SR), postParams.get(SubmitRequest.PARAM_SR));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_TEXT), postParams.get(SubmitRequest.PARAM_TEXT));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_THEN), postParams.get(SubmitRequest.PARAM_THEN));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_TITLE), postParams.get(SubmitRequest.PARAM_TITLE));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_UH), postParams.get(SubmitRequest.PARAM_UH));
        assertEquals(mFakeParams.get(SubmitRequest.PARAM_URL), postParams.get(SubmitRequest.PARAM_URL));
    }
}
