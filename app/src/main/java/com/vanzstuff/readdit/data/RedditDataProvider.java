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
    private static final int USER = 103;
    private static final int SUBREDDIT = 104;
    private static final int POST_BY_TAG = 105;
    private static final int ADD_TAG_TO_POST = 106;
    private static final int POST_BY_TAGID = 107;
    private static final int ADD_TAG_NAME_TO_POST = 108;
    private static final int VOTE = 110;


    private SQLiteOpenHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String sWherePostByTag = ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_TAG + " = ?";
    private static final String sWhereTagId = ReadditContract.Tag.COLUMN_NAME + " = ?";
    private static final SQLiteQueryBuilder sPostByTagQueryBuilder;
    private static final SQLiteQueryBuilder sTagId;

    static {
        sPostByTagQueryBuilder = new SQLiteQueryBuilder();
        sPostByTagQueryBuilder.setTables(ReadditContract.Link.TABLE_NAME + " LEFT OUTER JOIN " + ReadditContract.TagXPost.TABLE_NAME + " ON (" +
                ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_POST + " = " + ReadditContract.Link.TABLE_NAME + "." + ReadditContract.Link._ID + ")");
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
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK,  POST);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK_BY_TAG + "/*",  POST_BY_TAG);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK_BY_TAGID + "/#",  POST_BY_TAGID);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_ADD_TAG_TO_LINK + "/#/#",  ADD_TAG_TO_POST);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_ADD_TAG_NAME_TO_LINK + "/#/*",  ADD_TAG_NAME_TO_POST);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_COMMENT,  COMMENT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_USER,  USER);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_SUBREDDIT, SUBREDDIT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_VOTE, VOTE);
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
            case USER:{
                cursor = db.query(ReadditContract.User.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case POST:{
                cursor = db.query(ReadditContract.Link.TABLE_NAME,
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
            case POST_BY_TAGID: {
                cursor = sPostByTagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            sWherePostByTag,
                            new String[]{ReadditContract.Link.getTagIdFromUri(uri)},
                            null,
                            null,
                            sortOrder);
                break;
            }
            case VOTE: {
                cursor = db.query(ReadditContract.Vote.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
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
        String tag = ReadditContract.Link.getTagIdFromUri(uri);
        if (Utils.stringNotNullOrEmpty(tag)){
            Cursor cursor = query(ReadditContract.Tag.CONTENT_URI, new String[]{ReadditContract.Tag._ID}, sWhereTagId, new String[]{tag}, null);
            if (cursor.moveToFirst()){
                tag = cursor.getString(0);
                cursor.close();
                return sPostByTagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sWherePostByTag,
                        new String[]{tag},
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
            case USER:
                return ReadditContract.User.CONTENT_TYPE;
            case POST:
                return ReadditContract.Link.CONTENT_TYPE;
            case COMMENT:
                return ReadditContract.Comment.CONTENT_TYPE;
            case SUBREDDIT:
                return ReadditContract.Subreddit.CONTENT_TYPE;
            case POST_BY_TAG:
                return ReadditContract.Link.CONTENT_TYPE_POST_BY_TAG;
            case VOTE:
                return ReadditContract.Vote.CONTENT_TYPE;
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
            case USER: {
                    long id = db.insertOrThrow(ReadditContract.User.TABLE_NAME, null, values);
                    if (id > 0)
                        returnUri = ReadditContract.User.buildUserUri(id);

                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    break;
                }
            case POST:{
                long id = db.insertOrThrow(ReadditContract.Link.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Link.buildPostUri(id);
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
            case ADD_TAG_TO_POST:{
                returnUri = insertTagToPost(db, uri);
                break;
            }
            case ADD_TAG_NAME_TO_POST: {
                returnUri = insertTagNameToPost(db, uri);
                break;

            }
            case VOTE: {
                long id = db.insertOrThrow(ReadditContract.Vote.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ReadditContract.Vote.buildVoteUri(id);
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

    private Uri insertTagNameToPost(SQLiteDatabase db, Uri uri) {
        //check if tag already exists
        Cursor cursor = query(ReadditContract.Tag.CONTENT_URI,
                new String[]{ReadditContract.Tag._ID},
                ReadditContract.Tag.COLUMN_NAME + " = ?",
                new String[]{ReadditContract.Link.getTagFromUri(uri)},
                null);
        if ( cursor.moveToFirst() ){
            //tag already exists.
            Uri ret = insert(ReadditContract.Link.buildAddTagUri(
                    Long.parseLong(ReadditContract.Link.getTagNameFromUri(uri)[0]), cursor.getLong(0)), null);
            cursor.close();
            return ret;
        } else {
            //tag is not exists
            ContentValues insertValues = new ContentValues();
            insertValues.put(ReadditContract.Tag.COLUMN_NAME, ReadditContract.Link.getTagFromUri(uri));
            Uri retUri = insert(ReadditContract.Tag.CONTENT_URI, insertValues);
            long tagId = ReadditContract.Tag.getTagId(retUri);
            if ( tagId > 0) {
                return insert(ReadditContract.Link.buildAddTagUri(
                        Long.parseLong(ReadditContract.Link.getTagNameFromUri(uri)[0]), tagId), null);
            }
        }
        cursor.close();
        return null;
    }

    /**
     * Method add a tag to post and notify the change
     * @param db database to user
     * @param uri uri with the tag and the post ids
     * @return the return Uri with the post id altered
     */
    private Uri insertTagToPost(SQLiteDatabase db, Uri uri) {
        long[] uriValues = ReadditContract.Link.getTagIdAndLinkIdFromUri(uri);
        ContentValues insertValues = new ContentValues();
        insertValues.put(ReadditContract.TagXPost.COLUMN_POST, uriValues[0]);
        insertValues.put(ReadditContract.TagXPost.COLUMN_TAG, uriValues[1]);
        long id = db.insertOrThrow(ReadditContract.TagXPost.TABLE_NAME, null, insertValues);
        Uri returnUri;
        if (id > 0)
            returnUri = ReadditContract.Link.buildPostUri(uriValues[0]); //TODO - Maybe is good idea create a custom Uri
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        //notify the cursor
        Cursor tagCursor = getContext().getContentResolver().query(ReadditContract.Tag.CONTENT_URI,
                new String[]{ReadditContract.Tag._ID,
                        ReadditContract.Tag.COLUMN_NAME},
                ReadditContract.Tag._ID + " = ?",
                new String[]{String.valueOf(uriValues[1])},
                null);
        if (tagCursor.moveToFirst()) {
            getContext().getContentResolver().notifyChange(ReadditContract.Link.buildLinkByTagUri(tagCursor.getString(1)), null);
            getContext().getContentResolver().notifyChange(ReadditContract.Link.buildLinkByTagIdUri(tagCursor.getLong(0)), null);
        }
        tagCursor.close();
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
                rowsDeleted = db.delete(ReadditContract.Link.TABLE_NAME, selection, selectionArgs);
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
            case VOTE:{
                rowsDeleted = db.delete(ReadditContract.Vote.TABLE_NAME, selection, selectionArgs);
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
            case USER:{
                rowsUpdated = db.update(ReadditContract.User.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            case POST:{
                rowsUpdated = db.update(ReadditContract.Link.TABLE_NAME, values, selection, selectionArgs );
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
            case VOTE:{
                rowsUpdated = db.update(ReadditContract.Vote.TABLE_NAME, values, selection, selectionArgs );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case POST:{
                int returnCount = 0;
                db.beginTransaction();
                try{
                    for( ContentValues value : values ){
                        long id = db.insertOrThrow(ReadditContract.Link.TABLE_NAME, null, value);
                        if ( id != -1 )
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
