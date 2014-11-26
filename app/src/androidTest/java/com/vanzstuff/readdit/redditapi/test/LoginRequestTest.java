package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.LoginRequest;
import com.vanzstuff.readditapp.test.mocks.HttpStackMock;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vanz on 20/11/14.
 */
public class LoginRequestTest extends AndroidTestCase{

    private Map<String, Object> mFakeParams;
    private HttpStackMock mMockStack;


    @Override
    protected void setUp() throws Exception {
        mFakeParams = new HashMap<String, Object>();
        mFakeParams.put("int", 1);
        mFakeParams.put("double", 1.0);
        mFakeParams.put("string", "string");
        mMockStack = new HttpStackMock();
    }

    public void testRequest() throws AuthFailureError {
        RequestQueue queue = Volley.newRequestQueue(getContext(), mMockStack);
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), HttpStatus.SC_OK, "OK")));
        LoginRequest fakeRequest = new LoginRequest(null, null, mFakeParams);
        Uri requestUrl = Uri.parse(fakeRequest.getUrl());
        assertEquals("http", requestUrl.getScheme());
        assertEquals("www.reddit.com", requestUrl.getAuthority());
        assertEquals("/api/login", requestUrl.getPath());
        assertEquals(0, requestUrl.getQueryParameterNames().size());
        queue.start();
        queue.add(fakeRequest);
        while (mMockStack.getLastRequest() == null);
        assertNotNull(mMockStack.getLastRequest().getBody());
        String body = new String(mMockStack.getLastRequest().getBody());
        MoreAsserts.assertContainsRegex("(int=1|double=1\\.0|string=string)&(int=1|double=1\\.0|string=string)&(int=1|double=1\\.0|string=string)", body);
    }


}
