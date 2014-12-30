package com.vanzstuff.readdit.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * TODO
 */
public class RedditSync extends AbstractThreadedSyncAdapter{



    public RedditSync(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public RedditSync(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    }
}
