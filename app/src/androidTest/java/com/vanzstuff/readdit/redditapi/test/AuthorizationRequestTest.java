package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.HttpStackMock;
import com.vanzstuff.readdit.redditapi.AuthorizationRequest;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by vanz on 26/11/14.
 */
public class AuthorizationRequestTest extends AndroidTestCase {

    private HttpStackMock mMockStack;
    private RequestQueue mQueue;
    private Map<String, Object> mFakeParams;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMockStack = new HttpStackMock();
        mQueue = Volley.newRequestQueue(getContext(), mMockStack);
        mQueue.start();
        mFakeParams= new HashMap<String, Object>();
        mFakeParams.put(AuthorizationRequest.PARAM_CLIENT_ID, java.util.UUID.randomUUID().toString());
        mFakeParams.put(AuthorizationRequest.PARAM_REDIRECT_URI, "http://127.0.0.1/test");
        mFakeParams.put(AuthorizationRequest.PARAM_DURATION, AuthorizationRequest.DURATION_PERMANENT);
        mFakeParams.put(AuthorizationRequest.PARAM_RESPONSE_TYPE, AuthorizationRequest.DEFAULT_RESPONSE_TYPE);
        mFakeParams.put(AuthorizationRequest.PARAM_SCOPE, getAllScopes());
        mFakeParams.put(AuthorizationRequest.PARAM_STATE, java.util.UUID.randomUUID().toString());
    }

    public void testRequest() throws AuthFailureError, UnsupportedEncodingException {
        AuthorizationRequest request = new AuthorizationRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                assertNotNull(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fail(error.getMessage());
            }
        }, mFakeParams);
        mMockStack.setResponse(new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")));
        mQueue.add(request);
        while (mMockStack.getLastRequest() == null) ;
        Uri requestUrl = Uri.parse(mMockStack.getLastRequest().getUrl());
        assertEquals("ssl.reddit.com", requestUrl.getAuthority());
        assertEquals( "/api/v1/authorize", requestUrl.getPath());
        assertEquals( "https", requestUrl.getScheme());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", mMockStack.getLastRequest().getBodyContentType());
        Set<String> fields = requestUrl.getQueryParameterNames();
        assertEquals(6, fields.size());
        for (String field : fields) {
            if (field.equals(AuthorizationRequest.PARAM_CLIENT_ID)) {
                assertEquals(requestUrl.getQueryParameter(field), mFakeParams.get(AuthorizationRequest.PARAM_CLIENT_ID));
                continue;
            } else if (field.equals(AuthorizationRequest.PARAM_REDIRECT_URI)) {
                assertEquals(requestUrl.getQueryParameter(field), mFakeParams.get(AuthorizationRequest.PARAM_REDIRECT_URI));
                continue;
            } else if (field.equals(AuthorizationRequest.PARAM_DURATION)) {
                assertEquals(requestUrl.getQueryParameter(field), mFakeParams.get(AuthorizationRequest.PARAM_DURATION));
                continue;
            } else if (field.equals(AuthorizationRequest.PARAM_RESPONSE_TYPE)) {
                assertEquals(requestUrl.getQueryParameter(field), mFakeParams.get(AuthorizationRequest.PARAM_RESPONSE_TYPE));
                continue;
            } else if (field.equals(AuthorizationRequest.PARAM_SCOPE)) {
                assertEquals(getStringScopeList(), requestUrl.getQueryParameter(field) );
                continue;
            } else if (field.equals(AuthorizationRequest.PARAM_STATE)) {
                assertEquals(requestUrl.getQueryParameter(field), mFakeParams.get(AuthorizationRequest.PARAM_STATE));
                continue;
            } else {
                fail("Unexpected Field");
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mQueue.stop();
    }

    private String[] getAllScopes() {
        String[] allScopes ={ AuthorizationRequest.SCOPE_EDIT,
        AuthorizationRequest.SCOPE_FLAIR,
        AuthorizationRequest.SCOPE_HISTORY,
        AuthorizationRequest.SCOPE_IDENTITY,
        AuthorizationRequest.SCOPE_MODCONFIG,
        AuthorizationRequest.SCOPE_MODFLAIR,
        AuthorizationRequest.SCOPE_MODLOG,
        AuthorizationRequest.SCOPE_MODPOSTS,
        AuthorizationRequest.SCOPE_MODWIKI,
        AuthorizationRequest.SCOPE_IDENTITY,
        AuthorizationRequest.SCOPE_MYSUBREDDITS,
        AuthorizationRequest.SCOPE_READ,
        AuthorizationRequest.SCOPE_REPORT,
        AuthorizationRequest.SCOPE_SAVE,
        AuthorizationRequest.SCOPE_SUBMIT,
        AuthorizationRequest.SCOPE_SUBSCRIBE,
        AuthorizationRequest.SCOPE_WIKIEDIT,
        AuthorizationRequest.SCOPE_WIKIREAD,
        AuthorizationRequest.SCOPE_VOTE };
        return allScopes;
    }

    private String getStringScopeList(){
        StringBuilder listParam = new StringBuilder();
        for( String scope : getAllScopes()){
            listParam.append(scope).append(",");
        }
        return listParam.substring(0, listParam.length()-1).toString();
    }
}

