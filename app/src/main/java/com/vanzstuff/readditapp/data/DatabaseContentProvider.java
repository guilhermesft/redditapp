package com.vanzstuff.readditapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 01/12/14.
 */
public class DatabaseContentProvider extends ContentProvider {

    private static final int TAG = 100;
    private static final int POST = 101;
    private static final int COMMENT = 102;
    private static final int SUBREDDIT = 103;
    private static final int SUBSCRIBE = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static SQLiteQueryBuilder sQueryBuilder;

    private SQLiteOpenHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        return null;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ReadditSQLOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
       final int match = sUriMatcher.match(uri);
        switch (match){
            case TAG:
                return ReadditContract.Tag.CONTENT_TYPE;
            case POST:
                return ReadditContract.Post.CONTENT_TYPE;
            case COMMENT:
                return ReadditContract.Comment.CONTENT_TYPE;
            case SUBREDDIT:
                return ReadditContract.Subreddit.CONTENT_TYPE;
            case SUBSCRIBE:
                return ReadditContract.Subscribe.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TAG: {
                long id = db.insert(ReadditContract.Tag.TABLE_NAME, null, values);
                if (id > 0){
                    return ReadditContract.Tag.buildTagUri(id);
                }
            }
            case POST:{
                long id = db.insert(ReadditContract.Post.TABLE_NAME, null, values);
                if (id > 0){
                    return ReadditContract.Post.buildPostUri(id);
                }
            }
            case COMMENT:{
                long id = db.insert(ReadditContract.Comment.TABLE_NAME, null, values);
                if (id > 0){
                    return ReadditContract.Comment.buildCommentUri(id);
                }
            }
            case SUBREDDIT: {
                long id = db.insert(ReadditContract.Subreddit.TABLE_NAME, null, values);
                if (id > 0) {
                    return ReadditContract.Subreddit.buildSubredditUri(id);
                }
            }
            case SUBSCRIBE:{
                long id = db.insert(ReadditContract.Subscribe.TABLE_NAME, null, values);
                if (id > 0) {
                    return ReadditContract.Subscribe.buildSubscribeUri(id);
                }
            }
            default:
                throw new UnsupportedOperationException("Unsopported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
