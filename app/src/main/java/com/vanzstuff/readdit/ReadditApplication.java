package com.vanzstuff.readdit;

import android.app.Application;

/**
 * Created by vanz on 18/11/14.
 */
public class ReadditApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyWrapper.getInstance(this);
    }
}
