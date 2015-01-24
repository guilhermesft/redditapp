package com.vanzstuff.readdit.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.AboutRequest;
import com.vanzstuff.redditapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private RequestQueue mVolleyQueue;

    /*Synchronization status for control by syncadapters*/
    public static final int SYNC_STATUS_NONE = 0x0;
    public static final int SYNC_STATUS_RUNNING = 0x1;
    public static final int SYNC_STATUS_UPDATE = 0x2;
    public static final int SYNC_STATUS_DELETE = 0x4;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init();
    }

    private void init() {
        mVolleyQueue = Volley.newRequestQueue(getContext());
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Logger.w("### SYNC ADAPTER ###");
        syncUser(provider);
    }

    /**
     * Sync info about users
     * @param provider provider to access database
     */
    private void syncUser(ContentProviderClient provider){
        Logger.w("### syncUser ###");
        Cursor cursor = null;
        try {
            cursor = provider.query(ReadditContract.User.CONTENT_URI, new String[] {
                    ReadditContract.User._ID, ReadditContract.User.COLUMN_NAME, ReadditContract.User.COLUMN_ACCESSTOKEN } , null, null, null);
            while (cursor.move(1)){
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Logger.d("updating " + name);
                String accessToken = cursor.getString(2);
                ContentValues content = new ContentValues(1);
                content.put(ReadditContract.User.COLUMN_SYNC_STATUS, SYNC_STATUS_RUNNING);
                provider.update(ReadditContract.User.CONTENT_URI, content, ReadditContract.User._ID + "=?", new String[]{String.valueOf(id)});
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                AboutRequest request = AboutRequest.newInstance(name, future, future, accessToken);
                mVolleyQueue.add(request);
                JSONObject response = future.get();
                content = new ContentValues(15);
                Logger.d(response.toString());
                response = response.getJSONObject("data");
                content.put(ReadditContract.User.COLUMN_IS_FRIEND, response.getBoolean("is_friend"));
                if ( !response.isNull("gold_expiration"))
                    content.put(ReadditContract.User.COLUMN_GOLD_EXPIRATION, response.getInt("gold_expiration"));
                content.put(ReadditContract.User.COLUMN_MODHASH, response.getString("modhash"));
                content.put(ReadditContract.User.COLUMN_HAS_VERIFIED_EMAIL, response.getBoolean("has_verified_email"));
                content.put(ReadditContract.User.COLUMN_CREATED_UTC, response.getInt("created_utc"));
                content.put(ReadditContract.User.COLUMN_ID, response.getString("id"));
                content.put(ReadditContract.User.COLUMN_HIDE_FROM_ROBOTS, response.getBoolean("hide_from_robots"));
                content.put(ReadditContract.User.COLUMN_COMMENT_KARMA, response.getInt("comment_karma"));
                content.put(ReadditContract.User.COLUMN_OVER_18, response.getBoolean("over_18"));
                content.put(ReadditContract.User.COLUMN_GOLD_CREDDITS, response.getInt("gold_creddits"));
                content.put(ReadditContract.User.COLUMN_CREATED, response.getInt("created"));
                content.put(ReadditContract.User.COLUMN_IS_GOLD, response.getBoolean("is_gold"));
                content.put(ReadditContract.User.COLUMN_NAME, response.getString("name"));
                content.put(ReadditContract.User.COLUMN_IS_MOD, response.getBoolean("is_mod"));
                content.put(ReadditContract.User.COLUMN_LINK_KARMA, response.getInt("link_karma"));
                content.put(ReadditContract.User.COLUMN_SYNC_STATUS, SYNC_STATUS_NONE);
                provider.update(ReadditContract.User.CONTENT_URI, content,ReadditContract.User._ID + "=?", new String[]{String.valueOf(id)} );
                Logger.d("User " + name + " updated");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.e(e.getLocalizedMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            VolleyError volleyError = (VolleyError) e.getCause();
            Logger.e(new String(volleyError.getNetworkResponse().data), e.getCause());
        } catch (RemoteException e) {
            e.printStackTrace();
            Logger.e(e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null)
                cursor.close();
        }
    }

    public static void syncNow(Context context){
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        context.getContentResolver().requestSync(SyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }
        return newAccount;
    }
}
