package com.vanzstuff.readdit.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.vanzstuff.readdit.Utils;

/**
 * ContentProvider manages the app data
 */
public class RedditDataProvider extends ContentProvider {

    /* Possibles matches for UriMatcher */
    private static final int TAG = 100;
    private static final int POST = 101;
    private static final int COMMENT = 102;
    private static final int SUBREDDIT = 104;
    private static final int POST_BY_TAG = 105;

    private SQLiteOpenHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String sWherePostByTag = ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_TAG + " = ?";
    private static final String sWhereTagId = ReadditContract.Tag.COLUMN_NAME + " = ?";
    private static final SQLiteQueryBuilder sPostByTagQueryBuilder;
    private static final SQLiteQueryBuilder sTagId;

    static {
        sPostByTagQueryBuilder = new SQLiteQueryBuilder();
        sPostByTagQueryBuilder.setTables(ReadditContract.Post.TABLE_NAME + " LEFT OUTER JOIN " + ReadditContract.TagXPost.TABLE_NAME + " ON (" +
                ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_POST + " = " + ReadditContract.Post.TABLE_NAME + "." + ReadditContract.Post._ID + ")");
        sTagId = new SQLiteQueryBuilder();
        sTagId.setTables(ReadditContract.Tag.TABLE_NAME);
    }

    /**
     * Build the UriMatcher to match if the expected Uris
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_TAG,  TAG);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_POST,  POST);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_POST + "/" + ReadditContract.PATH_POST_BY_TAG + "/*",  POST_BY_TAG);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_COMMENT,  COMMENT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_SUBREDDIT, SUBREDDIT);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ReadditSQLOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case TAG:{
                cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case POST:{
                cursor = db.query(ReadditContract.Post.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case COMMENT:{
                cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case SUBREDDIT:{
                cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case POST_BY_TAG: {
                cursor = getPostByTag(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Method retrieve the post from a given tag
     * @param uri
     * @param projection
     * @param sortOrder
     * @return
     */
    private Cursor getPostByTag(Uri uri, String[] projection, String sortOrder) {
        String tag = ReadditContract.Post.getTagFromUri(uri);
        if (Utils.stringNotNullOrEmpty(tag)){
            Cursor cursor = query(ReadditContract.Tag.CONTENT_URI, new String[]{ReadditContract.Tag._ID}, sWhereTagId, new String[]{tag}, null);
            if (cursor.moveToFirst()){
                return sPostByTagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sWherePostByTag,
                        new String[]{cursor.getString(0)},
                        null,
                        null,
                        sortOrder);
            }
        }
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
            case POST_BY_TAG:
                return ReadditContract.Post.CONTENT_TYPE_POST_BY_TAG;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case TAG: {
                long id = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Tag.buildTagUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case POST:{
                long id = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Post.buildPostUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COMMENT:{
                long id = db.insertOrThrow(ReadditContract.Comment.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Comment.buildCommentUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUBREDDIT:{
                long id = db.insertOrThrow(ReadditContract.Subreddit.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Subreddit.buildSubscribeUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match){
            case TAG:{
                rowsDeleted = db.delete(ReadditContract.Tag.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case POST:{
                rowsDeleted = db.delete(ReadditContract.Post.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case COMMENT:{
                rowsDeleted = db.delete(ReadditContract.Comment.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SUBREDDIT:{
                rowsDeleted = db.delete(ReadditContract.Subreddit.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match){
            case TAG:{
                rowsUpdated = db.update(ReadditContract.Tag.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            case POST:{
                rowsUpdated = db.update(ReadditContract.Post.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            case COMMENT:{
                rowsUpdated = db.update(ReadditContract.Comment.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            case SUBREDDIT:{
                rowsUpdated = db.update(ReadditContract.Subreddit.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
