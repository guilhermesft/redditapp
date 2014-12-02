package com.vanzstuff.readdit;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton responsable to keep the reference to Volley queue during the
 * app execution
 * Created by vanz on 18/11/14.
 */
public class VolleyWrapper {

    private static VolleyWrapper mInstance;
    private RequestQueue mVolleyRequestQueue;

    /**
     * Get the VolleyWrapper instance
     * @param ctx
     * @return
     */
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

    /**
     * Get the current volley request
     * @return volley request queue
     */
    public RequestQueue getRequestQueue(){
        return mVolleyRequestQueue;
    }

    /**
     * Add the @request to the current volley queue
     * @param request request to add in the queue
     */
    public void addToRequestQueue(Request request){
        mVolleyRequestQueue.add(request);
    }
}
