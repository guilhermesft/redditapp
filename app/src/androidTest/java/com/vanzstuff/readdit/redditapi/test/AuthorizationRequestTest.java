package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.ArrayMap;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.redditapi.AuthorizationRequest;
import com.vanzstuff.readditapp.test.mocks.HttpStackMock;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by vanz on 26/11/14.
 */
public class AuthorizationRequestTest extends AndroidTestCase {

    private static final String FAKE_CREDENTIAL_USERNAME = "test_client_id";
    private static final String FAKE_CREDENTIAL_PASSWORD = "test_password";
    private HttpStackMock mMockStack;
    private RequestQueue mQueue;
    private Map<String, Object> mFakeParams;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMockStack = new HttpStackMock();
        mQueue = Volley.newRequestQueue(getContext(), mMockStack);
        mQueue.start();
        mFakeParams= new ArrayMap<String, Object>();
        mFakeParams.put(AuthorizationRequest.PARAM_DEVICE_ID, java.util.UUID.randomUUID().toString());
        mFakeParams.put(AuthorizationRequest.PARAM_GRANT_TYPE, AuthorizationRequest.DEFAULT_GRANT_TYPE);
    }

    public void testRequest() throws AuthFailureError, UnsupportedEncodingException {
        Credentials credentials = new UsernamePasswordCredentials(FAKE_CREDENTIAL_USERNAME, FAKE_CREDENTIAL_PASSWORD );
        AuthorizationRequest request = new AuthorizationRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fail(error.getMessage());
            }
        }, credentials, mFakeParams);
        mQueue.add(request);
        while(mMockStack.getLastRequest() == null);
        assertEquals("https://ssl.reddit.com/api/v1/access_token", URLDecoder.decode(mMockStack.getLastRequest().getUrl(), "UTF-8"));
        String creds = credentials.getUserPrincipal().getName() + ":" + credentials.getPassword();
        assertEquals("Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT),
                mMockStack.getLastRequest().getHeaders().get("Authorization"));
        assertEquals("application/x-www-form-urlencoded", mMockStack.getLastRequest().getBodyContentType());
        String bodyString = new String(mMockStack.getLastRequest().getBody());
        String[] fields = bodyString.split("&");
        for( String field : fields ){
            if(field.equals("grant_type=https://oauth.reddit.com/grants/installed_client"))
                continue;
            else if (field.equals("device_id=" + mFakeParams.get(AuthorizationRequest.PARAM_DEVICE_ID)))
                continue;
            else
                fail("Unexpected Field");
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mQueue.stop();
    }
}
