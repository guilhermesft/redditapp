package com.vanzstuff.readdit;

import android.app.Application;

public class ReaditApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyWrapper.getInstance(this);
    }
}
