package com.vanzstuff.readdit.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * TODO
 */
public class ReaditAuthenticatorService extends Service{

    private RedditAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new RedditAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
