package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.redditapi.MessageRequest;
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
public class MessageRequestTest extends AndroidTestCase {

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

    public void testRequest(){
        mFakeParams.put(MessageRequest.PARAM_MARK, true);
        mFakeParams.put(MessageRequest.PARAM_MID, "mid?");
        mFakeParams.put(MessageRequest.PARAM_AFTER, "thing_after");
        mFakeParams.put(MessageRequest.PARAM_BEFORE, "thing_before");
        mFakeParams.put(MessageRequest.PARAM_COUNT, 0);
        mFakeParams.put(MessageRequest.PARAM_LIMIT, 100);
        mFakeParams.put(MessageRequest.PARAM_SHOW, MessageRequest.DEFAULT_SHOW);
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        MessageRequest request = new MessageRequest(MessageRequest.PATH_MESSAGE_INBOX,
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
        assertEquals( "/message/inbox", requestUrl.getPath());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        assertEquals(7, requestUrl.getQueryParameterNames().size());
        for( String key : requestUrl.getQueryParameterNames()){
            if ( MessageRequest.PARAM_MARK.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_MARK), Boolean.parseBoolean(requestUrl.getQueryParameter(key)));
                continue;
            }else if ( MessageRequest.PARAM_MID.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_MID), requestUrl.getQueryParameter(key));
                continue;
            }else if ( MessageRequest.PARAM_AFTER.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_AFTER), requestUrl.getQueryParameter(key));
                continue;
            }else if ( MessageRequest.PARAM_BEFORE.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_BEFORE), requestUrl.getQueryParameter(key));
                continue;
            }else if ( MessageRequest.PARAM_COUNT.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_COUNT), Integer.parseInt(requestUrl.getQueryParameter(key)));
                continue;
            }else if ( MessageRequest.PARAM_LIMIT.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_LIMIT), Integer.parseInt(requestUrl.getQueryParameter(key)));
                continue;
            }else if ( MessageRequest.PARAM_SHOW.equals(key)){
                assertEquals(mFakeParams.get(MessageRequest.PARAM_SHOW), requestUrl.getQueryParameter(key));
                continue;
            }else {
                fail("Unexpected field");
            }
        }
    }
}
