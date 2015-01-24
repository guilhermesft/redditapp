package com.vanzstuff.readdit;

import android.app.Application;
import android.content.ContentValues;
import android.os.AsyncTask;

import com.vanzstuff.readdit.data.DatabaseContentObserver;
import com.vanzstuff.readdit.data.ReadditContract;

public class ReaditApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyWrapper.getInstance(this);
    }
}
