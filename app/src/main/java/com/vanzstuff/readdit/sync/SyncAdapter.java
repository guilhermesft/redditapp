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
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.AboutRequest;
import com.vanzstuff.readdit.redditapi.GetCommentRequest;
import com.vanzstuff.readdit.redditapi.GetLinks;
import com.vanzstuff.readdit.redditapi.MySubredditRequest;
import com.vanzstuff.readdit.redditapi.RedditApiUtils;
import com.vanzstuff.redditapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.Set;
import java.util.HashSet;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private RequestQueue mVolleyQueue;
    private String mAccessToken;

    /*Synchronization status for control by syncadapters*/
    public static final int SYNC_STATUS_NONE = 0x0;
    public static final int SYNC_STATUS_RUNNING = 0x1;
    public static final int SYNC_STATUS_UPDATE = 0x2;
    public static final int SYNC_STATUS_DELETE = 0x4;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    private void init(Context context) {
        mVolleyQueue = Volley.newRequestQueue(getContext());
        Cursor cursor = null;
        try{
            cursor = context.getContentResolver().query(ReadditContract.User.CONTENT_URI, new String[]{ReadditContract.User.COLUMN_ACCESSTOKEN},
                    ReadditContract.User.COLUMN_CURRENT + "=?", new String[]{"1"}, null);
            if (cursor.moveToFirst())
                mAccessToken = cursor.getString(0);
        } finally {
            if ( cursor != null )
                cursor.close();
        }
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Logger.w("### SYNC ADAPTER ###");
        syncUser(provider);
        syncSubreddit(provider);
        syncLinks(provider);
        syncComment(provider);
    }

    private void syncComment(ContentProviderClient provider) {
        Logger.i("### syncComment ###");
        Cursor cursor = null;
        try {
            cursor = provider.query(ReadditContract.Link.CONTENT_URI, new String[]{
                    ReadditContract.Link.COLUMN_ID,
                    ReadditContract.Link.COLUMN_SUBREDDIT,
            }, null, null, null);
            while (cursor.move(1)) {
                String subreddit = cursor.getString(1);
                String article = cursor.getString(0);
                RequestFuture<JSONArray> future = RequestFuture.newFuture();
                mVolleyQueue.add(GetCommentRequest.newInstance(subreddit, article, 0, -1, -1, true, true, GetCommentRequest.PARAM_SORT_NEW, future, future, mAccessToken));
                JSONArray result = future.get();
                ContentValues[] comments = loadComments(result);
                Set<ContentValues> newComments = new HashSet<ContentValues>();
                for (ContentValues value : comments) {
                    String commentID = value.getAsString(ReadditContract.Comment.COLUMN_ID);
                    if (isCommentInDatabase(provider, commentID)) {
                        if (provider.update(ReadditContract.Comment.CONTENT_URI, value, ReadditContract.Comment.COLUMN_ID + "=?", new String[]{commentID}) > 0) {
                            Logger.i(commentID + " updated");
                        }
                    } else {
                        newComments.add(value);
                    }
                }
                    int ret = provider.bulkInsert(ReadditContract.Comment.CONTENT_URI, newComments.toArray(new ContentValues[newComments.size()]));
                    Logger.i(ret + " comments inserted");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private boolean isCommentInDatabase(ContentProviderClient provider, String commentID) {
        Cursor cursor = null;
        try {
            cursor = provider.query(ReadditContract.Comment.CONTENT_URI, new String[]{ReadditContract.Comment.COLUMN_ID},
                    ReadditContract.Comment.COLUMN_ID + "=?", new String[]{commentID}, null);
            return cursor.moveToFirst();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if(cursor!=null)
                cursor.close();
        }
        return false;
    }

    private ContentValues[] loadComments(JSONArray result) {
        Set<ContentValues> valuesSet = new HashSet<ContentValues>();
        try {
            for (int i = 0; i < result.length(); i++) {
                valuesSet.addAll(parseComments(result.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return valuesSet.toArray(new ContentValues[]{});
    }

    private Set<ContentValues> parseComments(JSONObject obj) throws JSONException {
        Set<ContentValues> comments = new HashSet<ContentValues>();
        //TODO - handler replies
        //user_reports, report_reasons, mod_reports is being ignore
        if ((!RedditApiUtils.KIND_LISTING.equals(obj.getString("kind"))) || obj.isNull("data")) {
            return comments;
        }
        JSONArray children = obj.getJSONObject("data").getJSONArray("children");
        for(int ci = 0; ci < children.length(); ci++) {
            JSONObject child = children.getJSONObject(ci);
            if (!RedditApiUtils.KIND_COMMENT.equals(child.getString("kind")))
                continue;
            //ok, it's a comment. Let's load it
            child = child.getJSONObject("data");
            ContentValues comment = new ContentValues();
            if( !obj.isNull("replies"))
                comments.addAll(parseComments(obj.getJSONObject("replies")));
            comment.put(ReadditContract.Comment.COLUMN_SUBREDDIT_ID, child.getString("subreddit_id"));
            if ( child.has("banned_by"))
                comment.put(ReadditContract.Comment.COLUMN_BANNED_BY, child.getString("banned_by"));
            comment.put(ReadditContract.Comment.COLUMN_LINK_ID, child.getString("link_id"));
            if ( child.isNull("likes"))
                comment.put(ReadditContract.Comment.COLUMN_LIKES, 0);
            else
                comment.put(ReadditContract.Comment.COLUMN_LIKES, child.getBoolean("likes")?1:-1);
            comment.put(ReadditContract.Comment.COLUMN_SAVED, child.getBoolean("saved"));
            comment.put(ReadditContract.Comment.COLUMN_ID, child.getString("id"));
            comment.put(ReadditContract.Comment.COLUMN_GILDED, child.getString("gilded"));
            comment.put(ReadditContract.Comment.COLUMN_AUTHOR, child.getString("author"));
            comment.put(ReadditContract.Comment.COLUMN_PARENT_ID, child.getString("parent_id"));
            comment.put(ReadditContract.Comment.COLUMN_SCORE, child.getInt("score"));
            if(child.has("approved_by"))
                comment.put(ReadditContract.Comment.COLUMN_APPROVED_BY, child.getString("approved_by"));
            comment.put(ReadditContract.Comment.COLUMN_CONTROVERSIALITY, child.getInt("controversiality"));
            comment.put(ReadditContract.Comment.COLUMN_BODY, child.getString("body"));
            comment.put(ReadditContract.Comment.COLUMN_EDITED, child.optInt("edited"));
            if(child.has("author_flair_css_class"))
                comment.put(ReadditContract.Comment.COLUMN_AUTHOR_CSS_CLASS, child.getString("author_flair_css_class"));
            comment.put(ReadditContract.Comment.COLUMN_DOWNS, child.getInt("downs"));
            comment.put(ReadditContract.Comment.COLUMN_BODY_HTML, child.getString("body_html"));
            comment.put(ReadditContract.Comment.COLUMN_SUBREDDIT, child.getString("subreddit"));
            comment.put(ReadditContract.Comment.COLUMN_SCORE_HIDDEN, child.getBoolean("score_hidden"));
            comment.put(ReadditContract.Comment.COLUMN_NAME, child.getString("name"));
            comment.put(ReadditContract.Comment.COLUMN_CREATED, child.getInt("created"));
            if(child.has("author_flair_text"))
                comment.put(ReadditContract.Comment.COLUMN_AUTHOR_FLAIR_TEXT, child.getString("author_flair_text"));
            comment.put(ReadditContract.Comment.COLUMN_CREATED_UTC, child.getInt("created_utc"));
            comment.put(ReadditContract.Comment.COLUMN_UPS, child.getInt("ups"));
            comment.put(ReadditContract.Comment.COLUMN_NUM_REPORTS, child.optInt("num_reports"));
            if(child.has("distinguished"))
                comment.put(ReadditContract.Comment.COLUMN_DISTINGUISHED, child.optString("distinguished"));
            comments.add(comment);
        }
        return comments;
    }

    private void syncLinks(ContentProviderClient provider) {
        Logger.i("### syncLinks ###");
        Cursor cursor = null;
        try {
            Cursor subredditCursor = null;
            subredditCursor = provider.query(ReadditContract.Subreddit.CONTENT_URI, null, null, null, null);
            while (subredditCursor.move(1)) {
                String subreddit = subredditCursor.getString(subredditCursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_DISPLAY_NAME));
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                mVolleyQueue.add(GetLinks.newInstance(subreddit, null, null, -1, 50, mAccessToken, future, future));
                JSONObject result = future.get();
                Logger.d(result.toString());
                ContentValues[] linksValues = loadLinks(result);
                Set<ContentValues> newLinks = new HashSet<ContentValues>();
                for(ContentValues value : linksValues ){
                    String linkID = value.getAsString(ReadditContract.Link.COLUMN_ID);
                    if(isLinkInDatabase(provider, linkID)){
                        if (provider.update(ReadditContract.Link.CONTENT_URI, value, ReadditContract.Link.COLUMN_ID + "=?", new String[]{linkID}) > 0)
                            Logger.i(linkID + " updated");

                    } else
                        newLinks.add(value);
                }
                int ret = provider.bulkInsert(ReadditContract.Link.CONTENT_URI, newLinks.toArray(new ContentValues[newLinks.size()]));
                Logger.i(ret + " link inserted");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null){
                cursor.close();
            }
        }
    }

    private boolean isLinkInDatabase(ContentProviderClient provider, String linkID) {
        Cursor cursor = null;
        try{
            cursor = provider.query(ReadditContract.Link.CONTENT_URI, new String[]{ReadditContract.Link.COLUMN_ID},
                    ReadditContract.Link.COLUMN_ID + "=?", new String[]{linkID}, null);
            return cursor.moveToFirst();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if(cursor!=null)
                cursor.close();
        }
        return false;
    }

    private ContentValues[] loadLinks(JSONObject result) throws JSONException {
        JSONArray links = result.getJSONObject("data").getJSONArray("children");
        ContentValues[] values = new ContentValues[links.length()];
        for (int i = 0; i < links.length(); i++) {
            //TODO - check the fields user_reports, report_reasons, mod_reports, num_reports, media_embed, media,  secure_media_embed and secure_media
            values[i] = new ContentValues();
            JSONObject link = links.getJSONObject(i).getJSONObject("data");
            values[i].put(ReadditContract.Link.COLUMN_DOMAIN, link.getString("domain"));
            values[i].put(ReadditContract.Link.COLUMN_BANNED_BY, link.getString("banned_by"));
            values[i].put(ReadditContract.Link.COLUMN_SUBREDDIT, link.getString("subreddit"));
            values[i].put(ReadditContract.Link.COLUMN_SELFTEXT_HTML, link.optString("selftext_html"));
            values[i].put(ReadditContract.Link.COLUMN_SELFTEXT, link.optString("selftext"));
            values[i].put(ReadditContract.Link.COLUMN_LIKES, link.optBoolean("likes"));
            values[i].put(ReadditContract.Link.COLUMN_LINK_FLAIR_TEXT, link.optString("link_flair_text"));
            values[i].put(ReadditContract.Link.COLUMN_ID, link.getString("id"));
            values[i].put(ReadditContract.Link.COLUMN_GILDED, link.getInt("gilded"));
            values[i].put(ReadditContract.Link.COLUMN_CLICKED, link.getBoolean("clicked"));
            values[i].put(ReadditContract.Link.COLUMN_AUTHOR, link.optString("author"));
            values[i].put(ReadditContract.Link.COLUMN_SCORE, link.getInt("score"));
            values[i].put(ReadditContract.Link.COLUMN_APPROVED_BY, link.optString("approved_by"));
            values[i].put(ReadditContract.Link.COLUMN_OVER_18, link.getBoolean("over_18"));
            values[i].put(ReadditContract.Link.COLUMN_HIDDEN, link.getBoolean("hidden"));
            values[i].put(ReadditContract.Link.COLUMN_NUM_COMMENTS, link.getInt("num_comments"));
            values[i].put(ReadditContract.Link.COLUMN_THUMBNAIL, link.getString("thumbnail"));
            values[i].put(ReadditContract.Link.COLUMN_SUBREDDIT_ID, link.getString("subreddit_id"));
            values[i].put(ReadditContract.Link.COLUMN_EDITED, link.optInt("edited"));
            values[i].put(ReadditContract.Link.COLUMN_LINK_FLAIR_CSS_CLASS, link.optString("link_flair_css_class"));
            values[i].put(ReadditContract.Link.COLUMN_AUTHOR_FLAIR_CSS_CLASS, link.optString("author_flair_css_class"));
            values[i].put(ReadditContract.Link.COLUMN_DOWNS, link.getInt("downs"));
            values[i].put(ReadditContract.Link.COLUMN_SAVED, link.getBoolean("saved"));
            values[i].put(ReadditContract.Link.COLUMN_STICKIED, link.getBoolean("stickied"));
            values[i].put(ReadditContract.Link.COLUMN_IS_SELF, link.getBoolean("is_self"));
            values[i].put(ReadditContract.Link.COLUMN_PERMALINK, link.getString("permalink"));
            values[i].put(ReadditContract.Link.COLUMN_NAME, link.getString("name"));
            values[i].put(ReadditContract.Link.COLUMN_CREATED, link.getInt("created"));
            values[i].put(ReadditContract.Link.COLUMN_URL, link.getString("url"));
            values[i].put(ReadditContract.Link.COLUMN_AUTHOR_FLAIR_TEXT, link.optString("author_flair_text"));
            values[i].put(ReadditContract.Link.COLUMN_TITLE, link.getString("title"));
            values[i].put(ReadditContract.Link.COLUMN_CREATED_UTC, link.getInt("created_utc"));
            values[i].put(ReadditContract.Link.COLUMN_DISTINGUISHED, link.getString("distinguished"));
            values[i].put(ReadditContract.Link.COLUMN_VISITED, link.getBoolean("visited"));
            values[i].put(ReadditContract.Link.COLUMN_UPS, link.getInt("ups"));
        }
        return values;
    }

    private void syncSubreddit(ContentProviderClient provider) {
        Logger.i("### syncSubreddit ###");
        Cursor cursor = null;
        try {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            mVolleyQueue.add(MySubredditRequest.newInstance(MySubredditRequest.PATH_SUBSCRIBER, null, null, -1, 50, future, future, mAccessToken));
            JSONObject result = future.get();
            ContentValues[] subredditsValues = loadSubreddits(result);
            Set<ContentValues> newSubreddits = new HashSet<ContentValues>();
            //we need to update subreddit that already in the database and insert new ones
            for( ContentValues value : subredditsValues){
                String subredditId = value.getAsString(ReadditContract.Subreddit.COLUMN_ID);
                if(isSubredditInDatabase(provider, subredditId)) {
                    provider.update(ReadditContract.Subreddit.CONTENT_URI, value, ReadditContract.Subreddit.COLUMN_ID + "=?", new String[]{subredditId});
                    Logger.i(subredditId + " updated");
                } else
                    newSubreddits.add(value);

            }
            int rowsCount = provider.bulkInsert(ReadditContract.Subreddit.CONTENT_URI, newSubreddits.toArray(new ContentValues[newSubreddits.size()]));
            Logger.i(rowsCount + " subreddits inserted");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Logger.e(e.getCause().getLocalizedMessage(), e.getCause());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null )
                cursor.close();
        }
    }

    private boolean isSubredditInDatabase(ContentProviderClient provider, String subreddit) {
        Cursor cursor = null;
        try{
            cursor = provider.query(ReadditContract.Subreddit.CONTENT_URI, new String[]{ReadditContract.Subreddit.COLUMN_ID},
                    ReadditContract.Subreddit.COLUMN_ID + "=?", new String[]{subreddit}, null);
            return cursor.moveToFirst();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if( cursor != null)
                cursor.close();
        }
        return false;
    }

    private ContentValues[] loadSubreddits(JSONObject result) throws JSONException {
        JSONArray subreddits = result.getJSONObject("data").getJSONArray("children");
        ContentValues[] values = new ContentValues[subreddits.length()];
        for (int i = 0; i < subreddits.length(); i++){
            values[i] = new ContentValues();
            JSONObject subreddit = subreddits.getJSONObject(i).getJSONObject("data");
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_HTML, subreddit.optString("submit_text_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_BANNED, subreddit.getBoolean("user_is_banned") );
            values[i].put(ReadditContract.Subreddit.COLUMN_ID, subreddit.getString("id") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT, subreddit.optString("submit_text") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DISPLAY_NAME, subreddit.getString("display_name") );
            values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_IMG, subreddit.optString("header_img") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DESCRIPTION_HTML, subreddit.optString("description_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_TITLE, subreddit.optString("title") );
            values[i].put(ReadditContract.Subreddit.COLUMN_COLLAPSE_DELETED_COMMENTS, subreddit.optBoolean("collapse_deleted_comments") );
            values[i].put(ReadditContract.Subreddit.COLUMN_OVER18, subreddit.optBoolean("over18") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION_HTML, subreddit.optString("public_description_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_TITLE, subreddit.optString("header_title") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DESCRIPTION, subreddit.getString("description") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_LINK_LABEL, subreddit.optString("submit_link_label") );
            values[i].put(ReadditContract.Subreddit.COLUMN_ACCOUNTS_ACTIVE, subreddit.optInt("accounts_active") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_TRAFFIC, subreddit.optBoolean("public_traffic") );
            if ( subreddit.has("header_size") && !subreddit.isNull("header_size")){
                JSONArray sizes = subreddit.getJSONArray("header_size");
                values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_WIDTH, sizes.getInt(0) );
                values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_HEIGHT, sizes.getInt(1) );
            }
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBSCRIBERS, subreddit.getInt("subscribers") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_LABEL, subreddit.optString("submit_text_label") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_MODERATOR, subreddit.optBoolean("user_is_moderator") );
            values[i].put(ReadditContract.Subreddit.COLUMN_NAME, subreddit.getString("name") );
            values[i].put(ReadditContract.Subreddit.COLUMN_CREATED, subreddit.getInt("created") );
            values[i].put(ReadditContract.Subreddit.COLUMN_URL, subreddit.getString("url") );
            values[i].put(ReadditContract.Subreddit.COLUMN_CREATED_UTC, subreddit.getInt("created_utc") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_CONTRIBUTOR, subreddit.getBoolean("user_is_contributor") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION, subreddit.optString("public_description") );
            values[i].put(ReadditContract.Subreddit.COLUMN_COMMENT_SCORE_HIDE_MINS, subreddit.getInt("comment_score_hide_mins") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBREDDIT_TYPE, subreddit.getString("subreddit_type") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMISSION_TYPE, subreddit.getString("submission_type") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_SUBSCRIBER, subreddit.getBoolean("user_is_subscriber") );
        }
        return values;
    }

    /**
     * Sync info about users
     * @param provider provider to access database
     */
    private void syncUser(ContentProviderClient provider){
        Logger.i("### syncUser ###");
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
                mVolleyQueue.add(AboutRequest.newInstance(name, future, future, accessToken));
                JSONObject response = future.get();
                content = new ContentValues(15);
                Logger.d(response.toString());
                response = response.getJSONObject("data");
                content.put(ReadditContract.User.COLUMN_IS_FRIEND, response.optBoolean("is_friend"));
                if ( !response.isNull("gold_expiration"))
                    content.put(ReadditContract.User.COLUMN_GOLD_EXPIRATION, response.optInt("gold_expiration"));
                content.put(ReadditContract.User.COLUMN_MODHASH, response.optString("modhash"));
                content.put(ReadditContract.User.COLUMN_HAS_VERIFIED_EMAIL, response.optBoolean("has_verified_email"));
                content.put(ReadditContract.User.COLUMN_CREATED_UTC, response.optInt("created_utc"));
                content.put(ReadditContract.User.COLUMN_ID, response.optString("id"));
                content.put(ReadditContract.User.COLUMN_HIDE_FROM_ROBOTS, response.optBoolean("hide_from_robots"));
                content.put(ReadditContract.User.COLUMN_COMMENT_KARMA, response.optInt("comment_karma"));
                content.put(ReadditContract.User.COLUMN_OVER_18, response.optBoolean("over_18"));
                content.put(ReadditContract.User.COLUMN_GOLD_CREDDITS, response.optInt("gold_creddits"));
                content.put(ReadditContract.User.COLUMN_CREATED, response.optInt("created"));
                content.put(ReadditContract.User.COLUMN_IS_GOLD, response.optBoolean("is_gold"));
                content.put(ReadditContract.User.COLUMN_NAME, response.optString("name"));
                content.put(ReadditContract.User.COLUMN_IS_MOD, response.optBoolean("is_mod"));
                content.put(ReadditContract.User.COLUMN_LINK_KARMA, response.optInt("link_karma"));
                content.put(ReadditContract.User.COLUMN_HAS_MAIL, response.optInt("has_mail"));
                content.put(ReadditContract.User.COLUMN_HAS_MOD_MAIL, response.optInt("has_mod_mail"));
                content.put(ReadditContract.User.COLUMN_SYNC_STATUS, SYNC_STATUS_NONE);
                provider.update(ReadditContract.User.CONTENT_URI, content,ReadditContract.User._ID + "=?", new String[]{String.valueOf(id)} );
                Logger.d("User " + name + " updated");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.e(e.getLocalizedMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
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