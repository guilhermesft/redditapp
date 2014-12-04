package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.HttpStackMock;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.redditapi.BaseRedditApiJsonRequest;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by vanz on 20/11/14.
 */
public class BaseRedditApiJsonRequestTest extends AndroidTestCase {

    private Map<String, Object> mFakeParams;
    private HttpStackMock mMockStack;
    private RequestQueue mQueue;
    private final static String FAKE_PATH = "dev/api";


    @Override
    protected void setUp() throws Exception {
        mFakeParams = new HashMap<String, Object>();
        mFakeParams.put("int",1);
        mFakeParams.put("double", 1.0);
        mFakeParams.put("string", "string");
        mMockStack = new HttpStackMock();
        mQueue =  Volley.newRequestQueue(getContext(), mMockStack);
        mQueue.start();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mQueue.stop();
    }

    public void testGet(){
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(FAKE_PATH, null, null, null, mFakeParams);
        Uri requestUrl = Uri.parse(fakeRequest.getUrl());
        assertEquals("https", requestUrl.getScheme());
        assertEquals("oauth.reddit.com", requestUrl.getAuthority());
        assertEquals("/" + FAKE_PATH, requestUrl.getPath());
        assertEquals(3, requestUrl.getQueryParameterNames().size());
        assertEquals(1, Integer.parseInt(requestUrl.getQueryParameter("int")));
        assertEquals(1.0, Double.parseDouble(requestUrl.getQueryParameter("double")));
        assertEquals("string", requestUrl.getQueryParameter("string"));
        }

    public void testPost() throws AuthFailureError {
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), HttpStatus.SC_OK, "OK")));
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(Request.Method.POST, FAKE_PATH, null, null, null, mFakeParams);
        Uri requestUrl = Uri.parse(fakeRequest.getUrl());
        assertEquals("https", requestUrl.getScheme());
        assertEquals("oauth.reddit.com", requestUrl.getAuthority());
        assertEquals("/" + FAKE_PATH, requestUrl.getPath());
        assertEquals(0, requestUrl.getQueryParameterNames().size());
        mQueue.start();
        mQueue.add(fakeRequest);
        while (mMockStack.getLastRequest() == null);
        assertNotNull(mMockStack.getLastRequest().getBody());
        String body = new String(mMockStack.getLastRequest().getBody());
        MoreAsserts.assertContainsRegex("(int=1|double=1\\.0|string=string)&(int=1|double=1\\.0|string=string)&(int=1|double=1\\.0|string=string)", body);
    }

    public void testHeaders() throws AuthFailureError {
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), HttpStatus.SC_OK, "OK")));
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(Request.Method.POST, FAKE_PATH, null, null, null, mFakeParams);
        mQueue.add(fakeRequest);
        while (mMockStack.getLastRequest() == null);
        assertEquals( "bearer " + Utils.getAccessToken(), mMockStack.getLastRequest().getHeaders().get("Authorization"));
    }

    public void testBodyContentType() throws AuthFailureError {
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), HttpStatus.SC_OK, "OK")));
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(Request.Method.POST, FAKE_PATH, null, null, null, mFakeParams);
        mQueue.add(fakeRequest);
        while (mMockStack.getLastRequest() == null);
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
    }

}

