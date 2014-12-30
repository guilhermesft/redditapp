package com.vanzstuff.readdit.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service allows the system get the binder to @RedditSync and call onPerformSync method
 */
public class RedditSyncService extends Service {

    /* SyncAdapter*/
    private static RedditSync mRedditSync;
    /* Lock object*/
    private static final Object mLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (mLock){
            if ( mRedditSync == null )
                mRedditSync = new RedditSync(getApplicationContext(), true);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mRedditSync.getSyncAdapterBinder();
    }
}
