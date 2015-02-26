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
import com.vanzstuff.readdit.sync.SyncAdapter;

/**
 * ContentProvider manages the app data
 */
public class RedditDataProvider extends ContentProvider {

    /* Possibles matches for UriMatcher */
    private static final int TAG = 100;
    private static final int LINK = 101;
    private static final int COMMENT = 102;
    private static final int USER = 103;
    private static final int SUBREDDIT = 104;
    private static final int LINK_BY_TAG = 105;
    private static final int ADD_TAG_TO_LINK = 106;
    private static final int LINK_BY_TAGID = 107;
    private static final int ADD_TAG_NAME_TO_LINK = 108;
    private static final int VOTE = 110;
    private static final int LINK_BY_SUBREDDIT = 111;
    private static final int TAG_X_LINK_PREDEFINED = 112;
    private static final int TAG_X_LINK = 113;
    private static final int COMMENT_BY_LINK_ID = 114;


    private SQLiteOpenHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String sWhereLinkByTag = ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_TAG + " = ?";
    private static final String sWhereTagId = ReadditContract.Tag.COLUMN_NAME + " = ?";
    private static final String sWhereTagXLinkPredefined = ReadditContract.Tag.TABLE_NAME + "." + ReadditContract.Tag.COLUMN_PREDEFINED + " = ?";
    private static final String sWhereCommentByLinkId = ReadditContract.Link.TABLE_NAME + "." + ReadditContract.Link._ID + " =?";
    private static final SQLiteQueryBuilder sLinkByTagQueryBuilder;
    private static final SQLiteQueryBuilder sTagId;
    private static final SQLiteQueryBuilder sTagXLinkPredefined;
    private static final SQLiteQueryBuilder sCommentByLinkId;

    static {
        sLinkByTagQueryBuilder = new SQLiteQueryBuilder();
        sLinkByTagQueryBuilder.setTables(ReadditContract.Link.TABLE_NAME + " LEFT OUTER JOIN " + ReadditContract.TagXPost.TABLE_NAME + " ON (" +
                ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_LINK + " = " + ReadditContract.Link.TABLE_NAME + "." + ReadditContract.Link._ID + ")");
        sTagId = new SQLiteQueryBuilder();
        sTagId.setTables(ReadditContract.Tag.TABLE_NAME);
        sTagXLinkPredefined = new SQLiteQueryBuilder();
        sTagXLinkPredefined.setTables(ReadditContract.TagXPost.TABLE_NAME + " LEFT JOIN " + ReadditContract.Tag.TABLE_NAME + " ON ( " +
                ReadditContract.Tag.TABLE_NAME + "." + ReadditContract.Tag.COLUMN_PREDEFINED + "=1 " +
                "AND " + ReadditContract.Tag.TABLE_NAME + "." + ReadditContract.Tag._ID + " = " + ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_TAG + ")" +
                " LEFT JOIN " + ReadditContract.Link.TABLE_NAME + " ON ( " + ReadditContract.Link.TABLE_NAME + "." + ReadditContract.Link._ID + " = " + ReadditContract.TagXPost.TABLE_NAME + "." + ReadditContract.TagXPost.COLUMN_LINK + " )");
        sCommentByLinkId = new SQLiteQueryBuilder();
        sCommentByLinkId.setTables(ReadditContract.Comment.TABLE_NAME + " LEFT JOIN " + ReadditContract.Link.TABLE_NAME + " ON ( " +
                ReadditContract.Link.TABLE_NAME + "." + ReadditContract.Link.COLUMN_NAME + " = " + ReadditContract.Comment.TABLE_NAME + "." + ReadditContract.Comment.COLUMN_LINK_ID + " )");
    }

    /**
     * Build the UriMatcher to match if the expected Uris
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_TAG,  TAG);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK, LINK);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK_BY_TAG + "/*", LINK_BY_TAG);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK_BY_TAGID + "/#", LINK_BY_TAGID);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_ADD_TAG_TO_LINK + "/#/#", ADD_TAG_TO_LINK);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_ADD_TAG_NAME_TO_LINK + "/#/*", ADD_TAG_NAME_TO_LINK);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_COMMENT,  COMMENT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_COMMENT_LINK + "/#",  COMMENT_BY_LINK_ID);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_USER,  USER);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_SUBREDDIT, SUBREDDIT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_VOTE, VOTE);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_LINK_BY_SUBREDDIT + "/*", LINK_BY_SUBREDDIT);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_TAGXLINK_PREDEFINED, TAG_X_LINK_PREDEFINED);
        matcher.addURI(ReadditContract.CONTENT_AUTHORITY, ReadditContract.PATH_TAGXLINK, TAG_X_LINK);
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
            case LINK:{
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
            case LINK_BY_TAG: {
                cursor = getPostByTag(uri, projection, sortOrder);
                break;
            }
            case LINK_BY_TAGID: {
                cursor = sLinkByTagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                        sWhereLinkByTag,
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
            case LINK_BY_SUBREDDIT: {
                cursor = db.query(ReadditContract.Link.TABLE_NAME,
                        projection,
                        ReadditContract.Link.COLUMN_SUBREDDIT + "=? AND " + ReadditContract.Link.COLUMN_READ + "=?",
                        new String[]{ReadditContract.Link.getLinkBySubredditDisplayName(uri), ReadditContract.Link.getReadFlagBySubredditDisplayName(uri)},
                        null,
                        null,
                        sortOrder);
                break;

            }
            case TAG_X_LINK_PREDEFINED:{
                cursor = sTagXLinkPredefined.query(mOpenHelper.getReadableDatabase(),projection,
                        sWhereTagXLinkPredefined,
                        new String[]{"1"},
                        null,null,sortOrder);
                break;
            }
            case COMMENT_BY_LINK_ID: {
                cursor = sCommentByLinkId.query(mOpenHelper.getReadableDatabase(), projection,
                        sWhereCommentByLinkId,
                        new String[]{uri.getPathSegments().get(1)},
                        null, null, null);
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
                return sLinkByTagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sWhereLinkByTag,
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
            case LINK:
                return ReadditContract.Link.CONTENT_TYPE;
            case COMMENT:
                return ReadditContract.Comment.CONTENT_TYPE;
            case SUBREDDIT:
                return ReadditContract.Subreddit.CONTENT_TYPE;
            case LINK_BY_TAG:
                return ReadditContract.Link.CONTENT_TYPE_POST_BY_TAG;
            case VOTE:
                return ReadditContract.Vote.CONTENT_TYPE;
            case TAG_X_LINK_PREDEFINED:
                return ReadditContract.TagXPost.CONTENT_TYPE_TAGXLINK_PREDEFINED;
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
                    if (id > 0) {
                        returnUri = ReadditContract.User.buildUserUri(id);
                        SyncAdapter.syncNow(getContext().getApplicationContext(), SyncAdapter.SYNC_TYPE_ALL);
                    } else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    break;
                }
            case LINK:{
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
            case ADD_TAG_TO_LINK:{
                returnUri = insertTagToPost(db, uri);
                break;
            }
            case ADD_TAG_NAME_TO_LINK: {
                returnUri = insertTagNameToPost(db, uri);
                break;

            }
            case VOTE: {
                long id = db.insertOrThrow(ReadditContract.Vote.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ReadditContract.Vote.buildVoteUri(id);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TAG_X_LINK: {
                long id = db.insertOrThrow(ReadditContract.TagXPost.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ReadditContract.TagXPost.buildTagXLinkUri(id);
                } else
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
        insertValues.put(ReadditContract.TagXPost.COLUMN_LINK, uriValues[0]);
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
            case LINK:{
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
            case TAG_X_LINK:{
                rowsDeleted = db.delete(ReadditContract.TagXPost.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER: {
                rowsDeleted = db.delete(ReadditContract.User.TABLE_NAME, selection, selectionArgs);
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
            case LINK:{
                if (!values.containsKey(ReadditContract.Link.COLUMN_SYNC_STATUS)) {
                    values.put(ReadditContract.Link.COLUMN_SYNC_STATUS, SyncAdapter.SYNC_STATUS_UPDATE);
                }
                rowsUpdated = db.update(ReadditContract.Link.TABLE_NAME, values, selection, selectionArgs );
                if (!uri.getQueryParameterNames().contains(ReadditContract.START_SYNC) || "1".equals(uri.getQueryParameter(ReadditContract.START_SYNC)))
                    SyncAdapter.syncNow(getContext(), SyncAdapter.SYNC_TYPE_LINKS);
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
            case TAG_X_LINK:{
                rowsUpdated = db.update(ReadditContract.TagXPost.TABLE_NAME, values, selection, selectionArgs );
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
            case LINK:{
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
