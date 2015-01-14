package com.vanzstuff.readdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton responsable to keep the reference to Volley queue during the
 * app execution
 */
public class VolleyWrapper {

    private static VolleyWrapper mInstance;
    private RequestQueue mVolleyRequestQueue;
    private ImageLoader mImageLoader;

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
        mImageLoader = new ImageLoader(mVolleyRequestQueue, new VolleyCacheImage());
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

    /**
     * Get the ImageLoader object used for the VolleyWraper
     * @return ImageLoader current used
     * @see com.android.volley.toolbox.ImageLoader
     */
    public ImageLoader getImageLoader(){ return mImageLoader; }

    /**
     *  com.android.volley.toolbox.ImageLoader.ImageCache implementation to use with com.android.volley.toolbox.ImageLoader
     *  @see com.android.volley.toolbox.ImageLoader
     */
    private final class VolleyCacheImage implements ImageLoader.ImageCache{

        private LruCache<String, Bitmap> mCache;

        public VolleyCacheImage(){
            mCache = new LruCache<>(15);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }
    }
}
