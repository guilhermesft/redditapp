package com.vanzstuff.redditapp.data.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.RedditDataProvider;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.SubmitRequest;
import com.vanzstuff.readdit.redditapi.VoteRequest;

public class RedditDataProviderTest extends ProviderTestCase2<RedditDataProvider> {

    public static final String DATABASE_NAME = "readdit.db";
    private ContentValues mTagFakeValues;
    private ContentValues mPostFakeValues;
    private ContentValues mCommentFakeValues;
    private ContentValues mSubredditFakeValues;
    private ContentValues mVoteFakeValues;

    /**
     * Constructor.
     */
    public RedditDataProviderTest() {
        super(RedditDataProvider.class, ReadditContract.CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getMockContext().getDatabasePath(DATABASE_NAME).delete();
        //call the query method for ensure database creation
        getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getMockContext().getDatabasePath(DATABASE_NAME).delete();
    }

    /**
     * Test the method GetType of the DatabaseContentProvider
     */
    public void testGetType(){
        assertEquals(ReadditContract.Tag.CONTENT_TYPE, getProvider().getType(ReadditContract.Tag.CONTENT_URI));
        assertEquals(ReadditContract.Post.CONTENT_TYPE, getProvider().getType(ReadditContract.Post.CONTENT_URI));
        assertEquals(ReadditContract.Comment.CONTENT_TYPE, getProvider().getType(ReadditContract.Comment.CONTENT_URI));
        assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, getProvider().getType(ReadditContract.Subreddit.CONTENT_URI));
        assertEquals(ReadditContract.Post.CONTENT_TYPE_POST_BY_TAG, getProvider().getType(ReadditContract.Post.buildPostByTagUri("tag")));
        try {
            getProvider().getType(ReadditContract.BASE_CONTENT_URI.buildUpon().appendPath("xpto").build());
        }catch (Exception e){
            assertEquals(UnsupportedOperationException.class, e.getClass());
        }
    }

    /**
     * Test the method insert of the DatabaseContentProvider
     */
    public void testInsert(){
        //teste insert new tags
        ContentValues insertValues = new ContentValues();
        insertValues.put(ReadditContract.Tag.COLUMN_NAME, "fake");
        Uri retUri = getProvider().insert(ReadditContract.Tag.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_TAG, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                new String[]{ReadditContract.Tag._ID,
                        ReadditContract.Tag.COLUMN_NAME},
                ReadditContract.Tag._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Tag.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Tag.COLUMN_NAME)));
        cursor.close();
        //test insert post
        insertValues.clear();
        insertValues.put(ReadditContract.Post.COLUMN_DATE, java.util.Calendar.getInstance().getTimeInMillis());
        insertValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
        insertValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "redditdev");
        insertValues.put(ReadditContract.Post.COLUMN_USER, "fuser");
        insertValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
        insertValues.put(ReadditContract.Post.COLUMN_THREADS, 2);
        insertValues.put(ReadditContract.Post.COLUMN_TITLE, "title");
        insertValues.put(ReadditContract.Post.COLUMN_CONTENT_TYPE, "text/plain");
        retUri = getProvider().insert(ReadditContract.Post.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_POST, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        long postID = Long.parseLong(retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                new String[]{ReadditContract.Post._ID,
                        ReadditContract.Post.COLUMN_DATE,
                        ReadditContract.Post.COLUMN_CONTENT,
                        ReadditContract.Post.COLUMN_SUBREDDIT,
                        ReadditContract.Post.COLUMN_USER,
                        ReadditContract.Post.COLUMN_TITLE,
                        ReadditContract.Post.COLUMN_THREADS,
                        ReadditContract.Post.COLUMN_VOTES},
                ReadditContract.Post._ID + "=?",
                new String[]{String.valueOf(postID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_VOTES), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_THREADS), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_THREADS)));
        assertEquals(insertValues.get(ReadditContract.Post.COLUMN_TITLE), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_TITLE)));
        cursor.close();
        //test insert comment\
        //insert comment without parent
        insertValues.clear();
        insertValues.put(ReadditContract.Comment.COLUMN_DATE, java.util.Calendar.getInstance().getTimeInMillis());
        insertValues.put(ReadditContract.Comment.COLUMN_CONTENT, "blablablablablab");
        insertValues.put(ReadditContract.Comment.COLUMN_POST, postID);
        insertValues.put(ReadditContract.Comment.COLUMN_USER, "fuser");
        retUri = getProvider().insert(ReadditContract.Comment.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_COMMENT, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        long parentCommentID = Long.parseLong(retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                new String[]{ReadditContract.Comment._ID,
                        ReadditContract.Comment.COLUMN_DATE,
                        ReadditContract.Comment.COLUMN_CONTENT,
                        ReadditContract.Comment.COLUMN_POST,
                        ReadditContract.Comment.COLUMN_USER,
                        ReadditContract.Comment.COLUMN_PARENT},
                ReadditContract.Comment._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_DATE)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CONTENT)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_POST), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_POST)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_USER)));
        assertEquals(null, cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT)));
        cursor.close();
        //insert comment with parent
        insertValues.clear();
        insertValues.put(ReadditContract.Comment.COLUMN_DATE, java.util.Calendar.getInstance().getTimeInMillis());
        insertValues.put(ReadditContract.Comment.COLUMN_CONTENT, "blablablablablab");
        insertValues.put(ReadditContract.Comment.COLUMN_PARENT, parentCommentID);
        insertValues.put(ReadditContract.Comment.COLUMN_POST, postID);
        insertValues.put(ReadditContract.Comment.COLUMN_USER, "fuser");
        retUri = getProvider().insert(ReadditContract.Comment.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_COMMENT, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        insertValues.put(ReadditContract.Comment.COLUMN_USER, "fuser");
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                new String[]{ReadditContract.Comment._ID,
                        ReadditContract.Comment.COLUMN_DATE,
                        ReadditContract.Comment.COLUMN_CONTENT,
                        ReadditContract.Comment.COLUMN_POST,
                        ReadditContract.Comment.COLUMN_USER,
                        ReadditContract.Comment.COLUMN_PARENT},
                ReadditContract.Comment._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_DATE)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CONTENT)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_POST), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_POST)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_USER)));
        assertEquals(insertValues.get(ReadditContract.Comment.COLUMN_PARENT), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT)));
        cursor.close();
        //test insert subreddit
        insertValues.clear();
        insertValues.put(ReadditContract.Subreddit.COLUMN_NAME, "fakesubreddit");
        retUri = getProvider().insert(ReadditContract.Subreddit.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_SUBREDDIT, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                new String[]{ReadditContract.Subreddit._ID, ReadditContract.Subreddit.COLUMN_NAME},
                ReadditContract.Subreddit._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Subreddit.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_NAME)));
        cursor.close();
        insertValues.clear();
        //test insert vote rows
        insertValues.put(ReadditContract.Vote.COLUMN_POST, postID);
        insertValues.put(ReadditContract.Vote.COLUMN_USER, "vanz");
        insertValues.put(ReadditContract.Vote.COLUMN_DIRECTION, VoteRequest.VOTE_UP);
        retUri = getProvider().insert(ReadditContract.Vote.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.PATH_VOTE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Vote.TABLE_NAME,
                new String[]{ReadditContract.Vote._ID, ReadditContract.Vote.COLUMN_POST, ReadditContract.Vote.COLUMN_DIRECTION, ReadditContract.Vote.COLUMN_USER},
                ReadditContract.Vote._ID + "=?",
                new String[]{retUri.getPathSegments().get(1)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Vote.COLUMN_DIRECTION), cursor.getInt(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_DIRECTION)));
        assertEquals(insertValues.get(ReadditContract.Vote.COLUMN_POST), cursor.getLong(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_POST)));
        assertEquals(insertValues.get(ReadditContract.Vote.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_USER)));
        assertEquals(ReadditContract.Vote.getVoteId(retUri), cursor.getLong(cursor.getColumnIndex(ReadditContract.Vote._ID)));
        cursor.close();
        db.close();
    }

    /**
     * test delete method of the content provider
     */
    public void testDelete(){
        insertFakeData();
        //call the query method for ensure database creation
        getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        //test delete vote rows
        assertEquals(1, db.query(ReadditContract.Vote.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getMockContentResolver().delete(ReadditContract.Vote.CONTENT_URI, "1", null));
        Cursor cursor = db.query(ReadditContract.Vote.TABLE_NAME,
                null,
                ReadditContract.Vote._ID + "=?",
                new String[]{String.valueOf(mVoteFakeValues.get(ReadditContract.Vote._ID))},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        //test delete tag rows
        assertEquals(1, db.query(ReadditContract.Tag.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getMockContentResolver().delete(ReadditContract.Tag.CONTENT_URI, "1", null));
        cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                null,
                ReadditContract.Tag._ID + "=?",
                new String[]{String.valueOf(mTagFakeValues.get(ReadditContract.Tag._ID))},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        //test delete post rows
        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getMockContentResolver().delete(ReadditContract.Post.CONTENT_URI, "1", null));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                null,
                ReadditContract.Post._ID + "=?",
                new String[]{String.valueOf(mPostFakeValues.get(ReadditContract.Post._ID))},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        //test delete comment row
        assertEquals(1, db.query(ReadditContract.Comment.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getMockContentResolver().delete(ReadditContract.Comment.CONTENT_URI, "1", null));
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                null,
                ReadditContract.Comment._ID + "=?",
                new String[]{String.valueOf(mCommentFakeValues.get(ReadditContract.Comment._ID))},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        //test delete subreddit row
        assertEquals(1, db.query(ReadditContract.Subreddit.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getMockContentResolver().delete(ReadditContract.Subreddit.CONTENT_URI, "1", null));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                null,
                ReadditContract.Subreddit._ID + "=?",
                new String[]{String.valueOf(mSubredditFakeValues.get(ReadditContract.Subreddit._ID))},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        db.close();

    }

    /**
     * test update method of the content provider
     */
    public void testUpdate(){
        insertFakeData();
        //call the query method for ensure database creation
        getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        assertEquals(1, db.query(ReadditContract.Tag.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        ContentValues newFakeData = new ContentValues(mTagFakeValues);
        newFakeData.put(ReadditContract.Tag.COLUMN_NAME, "newName");
        assertEquals(1, getMockContentResolver().update(ReadditContract.Tag.CONTENT_URI, newFakeData, ReadditContract.Tag._ID + "=?", new String[]{mTagFakeValues.getAsString(ReadditContract.Tag._ID)}));
        Cursor cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                null,
                ReadditContract.Tag._ID + "=?",
                new String[]{mTagFakeValues.getAsString(ReadditContract.Tag._ID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mTagFakeValues.getAsLong(ReadditContract.Tag._ID).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Tag._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Tag.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Tag.COLUMN_NAME)));
        cursor.close();

        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mPostFakeValues);
        newFakeData.put(ReadditContract.Post.COLUMN_VOTES, 999);
        assertEquals(1, getMockContentResolver().update(ReadditContract.Post.CONTENT_URI, newFakeData, ReadditContract.Post._ID + "=?", new String[]{mPostFakeValues.getAsString(ReadditContract.Post._ID)}));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                null,
                ReadditContract.Post._ID + "=?",
                new String[]{mPostFakeValues.getAsString(ReadditContract.Post._ID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mPostFakeValues.getAsLong(ReadditContract.Post._ID).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(newFakeData.getAsInteger(ReadditContract.Post.COLUMN_VOTES).intValue(), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Post.COLUMN_DATE).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        cursor.close();

        assertEquals(1, db.query(ReadditContract.Comment.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mCommentFakeValues);
        newFakeData.put(ReadditContract.Comment.COLUMN_USER, "newUser");
        newFakeData.put(ReadditContract.Comment.COLUMN_POST, mPostFakeValues.getAsLong(ReadditContract.Post._ID).longValue());
        assertEquals(1, getMockContentResolver().update(ReadditContract.Comment.CONTENT_URI, newFakeData, ReadditContract.Comment._ID + "=?", new String[]{mCommentFakeValues.getAsString(ReadditContract.Comment._ID)}));
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                null,
                ReadditContract.Comment._ID + "=?",
                new String[]{mCommentFakeValues.getAsString(ReadditContract.Comment._ID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mCommentFakeValues.getAsLong(ReadditContract.Comment._ID).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment._ID)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_DATE).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_DATE)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_PARENT).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT)));
        assertEquals(newFakeData.getAsString(ReadditContract.Comment.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_USER)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_POST).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_POST)));
        assertEquals(newFakeData.getAsString(ReadditContract.Comment.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CONTENT)));
        cursor.close();

        assertEquals(1, db.query(ReadditContract.Subreddit.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mSubredditFakeValues);
        newFakeData.put(ReadditContract.Subreddit.COLUMN_NAME, "newSubreddit");
        assertEquals(1, getMockContentResolver().update(ReadditContract.Subreddit.CONTENT_URI, newFakeData, ReadditContract.Subreddit._ID + "=?", new String[]{mSubredditFakeValues.getAsString(ReadditContract.Subreddit._ID)}));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                null,
                ReadditContract.Subreddit._ID + "=?",
                new String[]{mSubredditFakeValues.getAsString(ReadditContract.Subreddit._ID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mSubredditFakeValues.getAsLong(ReadditContract.Subreddit._ID).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Subreddit._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Subreddit.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_NAME)));
        cursor.close();

        assertEquals(1, db.query(ReadditContract.Vote.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mVoteFakeValues);
        newFakeData.put(ReadditContract.Vote.COLUMN_DIRECTION, VoteRequest.VOTE_DOWN);
        assertEquals(1, getMockContentResolver().update(ReadditContract.Vote.CONTENT_URI, newFakeData, ReadditContract.Vote._ID + "=?", new String[]{mVoteFakeValues.getAsString(ReadditContract.Vote._ID)}));
        cursor = db.query(ReadditContract.Vote.TABLE_NAME,
                null,
                ReadditContract.Vote._ID + "=?",
                new String[]{mVoteFakeValues.getAsString(ReadditContract.Vote._ID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mVoteFakeValues.getAsLong(ReadditContract.Vote._ID).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Vote._ID)));
        assertEquals(newFakeData.getAsInteger(ReadditContract.Vote.COLUMN_DIRECTION).intValue(), cursor.getInt(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_DIRECTION)));
        assertEquals(newFakeData.getAsInteger(ReadditContract.Vote.COLUMN_USER).intValue(), cursor.getInt(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_USER)));
        assertEquals(newFakeData.getAsInteger(ReadditContract.Vote.COLUMN_POST).intValue(), cursor.getInt(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_POST)));
        cursor.close();
        db.close();
    }

    /**
     * test basic query method of the content provider
     */
    public void testQuery(){
        //call the query method for ensure database creation
        getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
        insertFakeData();
        Cursor cursor = getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mTagFakeValues.get(ReadditContract.Tag.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Tag.COLUMN_NAME)));
        cursor.close();
        cursor = getMockContentResolver().query(ReadditContract.Post.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mPostFakeValues.get(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(mPostFakeValues.get(ReadditContract.Post.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        assertEquals(mPostFakeValues.get(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(mPostFakeValues.get(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(mPostFakeValues.get(ReadditContract.Post.COLUMN_VOTES), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        cursor.close();
        cursor = getMockContentResolver().query(ReadditContract.Comment.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mCommentFakeValues.get(ReadditContract.Comment.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CONTENT)));
        assertEquals(mCommentFakeValues.get(ReadditContract.Comment.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_DATE)));
        assertEquals(mCommentFakeValues.get(ReadditContract.Comment.COLUMN_PARENT), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT)));
        assertEquals(mCommentFakeValues.get(ReadditContract.Comment.COLUMN_POST), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_POST)));
        assertEquals(mCommentFakeValues.get(ReadditContract.Comment.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_USER)));
        cursor.close();
        cursor = getMockContentResolver().query(ReadditContract.Subreddit.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mSubredditFakeValues.get(ReadditContract.Subreddit.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_NAME)));
        cursor.close();
        //test query vote table
        cursor = getMockContentResolver().query(ReadditContract.Vote.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(true, cursor.moveToFirst());
        assertEquals(mVoteFakeValues.get(ReadditContract.Vote.COLUMN_DIRECTION), cursor.getInt(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_DIRECTION)));
        assertEquals(mVoteFakeValues.get(ReadditContract.Vote.COLUMN_POST), cursor.getLong(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_POST)));
        assertEquals(mVoteFakeValues.get(ReadditContract.Vote.COLUMN_USER), cursor.getLong(cursor.getColumnIndex(ReadditContract.Vote.COLUMN_USER)));
        cursor.close();
    }

    /**
     * Test the query method with the Uri to retrieve all post from a tag
     */
    public void testQueryPostByTag(){
        //call the query method for ensure database creation
        getMockContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null);
        //let's insert some fake data to test
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues fakeTagValues = new ContentValues();
        ContentValues fakePostValues = new ContentValues();
        ContentValues fakeTagXPostValues = new ContentValues();
        fakeTagValues.put(ReadditContract.Tag.COLUMN_NAME, "tag1");
        long tagID = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, fakeTagValues);
        assertTrue(tagID > -1);
        fakePostValues.put(ReadditContract.Post.COLUMN_DATE, System.currentTimeMillis());
        fakePostValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "fakeSubreddit");
        fakePostValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
        fakePostValues.put(ReadditContract.Post.COLUMN_USER, "fakeUser");
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
        fakePostValues.put(ReadditContract.Post.COLUMN_TITLE, "fake title");
        fakePostValues.put(ReadditContract.Post.COLUMN_THREADS, 5);
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT_TYPE, "text/plain");
        long postID = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, fakePostValues);
        assertTrue(postID > -1);
        fakeTagXPostValues.put(ReadditContract.TagXPost.COLUMN_TAG, tagID);
        fakeTagXPostValues.put(ReadditContract.TagXPost.COLUMN_POST, postID);
        assertTrue(db.insertOrThrow(ReadditContract.TagXPost.TABLE_NAME, null, fakeTagXPostValues) > -1);
        //validate the first inserts
        Cursor cursor = getMockContentResolver().query(ReadditContract.Post.buildPostByTagUri(fakeTagValues.getAsString(ReadditContract.Tag.COLUMN_NAME)), null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(postID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Post._ID)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_VOTES), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_TITLE), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_TITLE)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_THREADS), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_THREADS)));
        //insert a second post with second tag
        fakeTagValues.clear();
        fakeTagValues.put(ReadditContract.Tag.COLUMN_NAME, "tag2");
        tagID = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, fakeTagValues);
        assertTrue(tagID > -1);
        fakePostValues.clear();
        fakePostValues.put(ReadditContract.Post.COLUMN_DATE, System.currentTimeMillis());
        fakePostValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "fakeSubreddit2");
        fakePostValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
        fakePostValues.put(ReadditContract.Post.COLUMN_USER, "fakeUser2");
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content2");
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT_TYPE, "text/plain");
        fakePostValues.put(ReadditContract.Post.COLUMN_TITLE, "fake title2");
        fakePostValues.put(ReadditContract.Post.COLUMN_THREADS, 5);
        postID = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, fakePostValues);
        assertTrue(postID > -1);
        fakeTagXPostValues.clear();
        fakeTagXPostValues.put(ReadditContract.TagXPost.COLUMN_TAG, tagID);
        fakeTagXPostValues.put(ReadditContract.TagXPost.COLUMN_POST, postID);
        assertTrue(db.insertOrThrow(ReadditContract.TagXPost.TABLE_NAME, null, fakeTagXPostValues) > -1);
        //validate the second inserts
        cursor = getMockContentResolver().query(ReadditContract.Post.buildPostByTagUri(fakeTagValues.getAsString(ReadditContract.Tag.COLUMN_NAME)), null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(postID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Post._ID)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_DATE), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_VOTES), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_TITLE), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_TITLE)));
        assertEquals(fakePostValues.get(ReadditContract.Post.COLUMN_THREADS), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_THREADS)));
        assertEquals(2, getMockContentResolver().query(ReadditContract.Post.CONTENT_URI, null, null, null, null).getCount());
    }

    /**
     * Test add a tag to a post
     */
    public void testAddTagToPost() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues fakeTagValues = new ContentValues();
        ContentValues fakePostValues = new ContentValues();
        fakeTagValues.put(ReadditContract.Tag.COLUMN_NAME, "tag1");
        long tagid = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, fakeTagValues);
        assertFalse(tagid == -1);
        fakePostValues.put(ReadditContract.Post.COLUMN_DATE, System.currentTimeMillis());
        fakePostValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "fakeSubreddit");
        fakePostValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
        fakePostValues.put(ReadditContract.Post.COLUMN_USER, "fakeUser");
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
        fakePostValues.put(ReadditContract.Post.COLUMN_TITLE, "fake title");
        fakePostValues.put(ReadditContract.Post.COLUMN_THREADS, 5);
        fakePostValues.put(ReadditContract.Post.COLUMN_CONTENT_TYPE, "text/plain");
        long postid = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, fakePostValues);
        assertFalse(postid == -1);
        Uri ret = getMockContentResolver().insert(ReadditContract.Post.buildAddTagUri(postid, tagid), null);
        assertEquals(postid, ReadditContract.Post.getPostId(ret));
        //test add a tag is not exists to a post.
        String tag = "fake";
        ret = getMockContentResolver().insert(ReadditContract.Post.buildAddTagUri(postid, tag), null);
        assertEquals(postid, ReadditContract.Post.getPostId(ret));
        Cursor cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                new String[]{ReadditContract.Tag._ID,
                        ReadditContract.Tag.COLUMN_NAME},
                ReadditContract.Tag.COLUMN_NAME + "=?",
                new String[]{tag},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(tag, cursor.getString(cursor.getColumnIndex(ReadditContract.Tag.COLUMN_NAME)));
        cursor.close();

    }

    /**
     * Insert some fake data used in the tests
     */
    private void insertFakeData() {
        prepareFakeData();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getMockContext().getDatabasePath(DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        mTagFakeValues.put(ReadditContract.Tag._ID, db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, mTagFakeValues));
        mPostFakeValues.put(ReadditContract.Post._ID, db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, mPostFakeValues));
        mCommentFakeValues.put(ReadditContract.Comment._ID, db.insertOrThrow(ReadditContract.Comment.TABLE_NAME, null, mCommentFakeValues));
        mSubredditFakeValues.put(ReadditContract.Subreddit._ID, db.insertOrThrow(ReadditContract.Subreddit.TABLE_NAME, null, mSubredditFakeValues));
        mVoteFakeValues.put(ReadditContract.Vote._ID, db.insertOrThrow(ReadditContract.Vote.TABLE_NAME, null, mVoteFakeValues));
    }

    /**
     * Prepare fake data for delete, update and query test
     */
    private void prepareFakeData() {
        mTagFakeValues = new ContentValues();
        mTagFakeValues.put(ReadditContract.Tag.COLUMN_NAME, "tag1");
        mPostFakeValues = new ContentValues();
        mPostFakeValues.put(ReadditContract.Post.COLUMN_DATE, System.currentTimeMillis());
        mPostFakeValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "fakeSubreddit");
        mPostFakeValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
        mPostFakeValues.put(ReadditContract.Post.COLUMN_USER, "fakeUser");
        mPostFakeValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
        mPostFakeValues.put(ReadditContract.Post.COLUMN_TITLE, "fake title");
        mPostFakeValues.put(ReadditContract.Post.COLUMN_THREADS, 5);
        mPostFakeValues.put(ReadditContract.Post.COLUMN_CONTENT_TYPE, "text/plain");
        mCommentFakeValues = new ContentValues();
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_CONTENT, "Great!");
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_DATE, System.currentTimeMillis());
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_PARENT, 1l);
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_POST, 3l);
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_USER, "me");
        mSubredditFakeValues = new ContentValues();
        mSubredditFakeValues.put(ReadditContract.Subreddit.COLUMN_NAME, "mysubreddit");
        mVoteFakeValues = new ContentValues();
        mVoteFakeValues.put(ReadditContract.Vote.COLUMN_DIRECTION, VoteRequest.VOTE_UP);
        mVoteFakeValues.put(ReadditContract.Vote.COLUMN_POST, 1l);
        mVoteFakeValues.put(ReadditContract.Vote.COLUMN_USER, 1l);
    }

}
