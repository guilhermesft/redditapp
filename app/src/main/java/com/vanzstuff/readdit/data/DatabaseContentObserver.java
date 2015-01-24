package com.vanzstuff.readdit.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.redditapp.R;

public class DatabaseContentObserver extends ContentObserver {
    private final Context mContext;
    private final Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DatabaseContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logger.d("Mudança em " + uri.toString());
        if ( mHandler != null )
            mHandler.dispatchMessage(new Message());
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        mContext.getContentResolver().requestSync(getSyncAccount(), mContext.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @return a fake account.
     */
    private Account getSyncAccount() {
        // Create the account type and default account
        Account newAccount = new Account(
                "vanz", mContext.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) mContext.getSystemService(
                        Activity.ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, "", null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            mContext.getContentResolver().setIsSyncable(newAccount, mContext.getString(R.string.content_authority), 1);
            return newAccount;
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            return null;
        }
    }
}
