package com.vanzstuff.readdit;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by vanz on 18/11/14.
 */
public class VolleyWrapper {

    private static VolleyWrapper mInstance;
    private RequestQueue mVolleyRequestQueue;

    public static VolleyWrapper getInstance(Context ctx){
        if( mInstance == null ){
            mInstance = new VolleyWrapper(ctx);
        }
        return mInstance;
    }

    private VolleyWrapper(Context ctx){
        mVolleyRequestQueue = Volley.newRequestQueue(ctx);
        mVolleyRequestQueue.start();
    }

    public RequestQueue getRequestQueue(){
        return mVolleyRequestQueue;
    }

    public void addToRequestQueue(Request request){
        mVolleyRequestQueue.add(request);
    }
}
