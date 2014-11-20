package com.vanzstuff.readdit.redditapi.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.vanzstuff.readdit.redditapi.BaseRedditApiJsonRequest;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by vanz on 20/11/14.
 */
public class BaseRedditApiJsonRequestTest extends AndroidTestCase {

    private Map<String, Object> mFakeParams;
    private final static String FAKE_BASE_URL = "http://www.reddit.com/dev/api";

    @Override
    protected void setUp() throws Exception {
        mFakeParams = new HashMap<String, Object>();
        mFakeParams.put("int", 1);
        mFakeParams.put("double", 1.0);
        mFakeParams.put("string", "string");
    }

    public void testParseParams(){
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(FAKE_BASE_URL, null, null, null, null);
        Map<String, String> params = fakeRequest.parserParamsToString(mFakeParams);
        assertEquals("1", params.get("int"));
        assertEquals("1.0", params.get("double"));
        assertEquals("string", params.get("string"));
    }

    public void testGetUrl(){
        BaseRedditApiJsonRequest fakeRequest = new BaseRedditApiJsonRequest(FAKE_BASE_URL, null, null, null, mFakeParams);
        Uri requestUrl = Uri.parse(fakeRequest.getUrl());
        assertEquals("http", requestUrl.getScheme());
        assertEquals("www.reddit.com", requestUrl.getAuthority());
        assertEquals("dev/api", requestUrl.getPath());
        assertEquals(3, requestUrl.getQueryParameterNames().size());
        assertEquals(1, Integer.getInteger(requestUrl.getQueryParameter("int")).intValue());
        assertEquals(1.0, Double.valueOf(requestUrl.getQueryParameter("double")).doubleValue());
        assertEquals("string", requestUrl.getQueryParameter("string"));
        }
}

