package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.HttpStackMock;
import com.vanzstuff.readdit.redditapi.SearchRequest;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 30/11/14.
 */
public class SearchRequestTest extends AndroidTestCase {

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
        mFakeParams.put(SearchRequest.PARAM_SHOW, SearchRequest.DEFAULT_SHOW);
        mFakeParams.put(SearchRequest.PARAM_LIMIT, 100);
        mFakeParams.put(SearchRequest.PARAM_COUNT, 0);
        mFakeParams.put(SearchRequest.PARAM_BEFORE, "before");
        mFakeParams.put(SearchRequest.PARAM_AFTER, "after");
        mFakeParams.put(SearchRequest.PARAM_QUERY, "fakeQuery");
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        SearchRequest request = new SearchRequest(new Response.Listener<JSONObject>() {
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
        assertEquals( "/subreddits/search", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        assertEquals(6, requestUrl.getQueryParameterNames().size());
        assertEquals(mFakeParams.get(SearchRequest.PARAM_SHOW), requestUrl.getQueryParameter(SearchRequest.PARAM_SHOW));
        assertEquals(mFakeParams.get(SearchRequest.PARAM_LIMIT), Integer.parseInt(requestUrl.getQueryParameter(SearchRequest.PARAM_LIMIT)));
        assertEquals(mFakeParams.get(SearchRequest.PARAM_COUNT), Integer.parseInt(requestUrl.getQueryParameter(SearchRequest.PARAM_COUNT)));
        assertEquals(mFakeParams.get(SearchRequest.PARAM_BEFORE), requestUrl.getQueryParameter(SearchRequest.PARAM_BEFORE));
        assertEquals(mFakeParams.get(SearchRequest.PARAM_AFTER), requestUrl.getQueryParameter(SearchRequest.PARAM_AFTER));
        assertEquals(mFakeParams.get(SearchRequest.PARAM_QUERY), requestUrl.getQueryParameter(SearchRequest.PARAM_QUERY));
    }
}
