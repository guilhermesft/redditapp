package com.vanzstuff.redditapp.data.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;

import com.vanzstuff.readditapp.data.DatabaseContentProvider;
import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 02/12/14.
 */
public class DatabaseContentProviderTest extends ProviderTestCase2<DatabaseContentProvider> {

    public static final String TEST_DATABASE_NAME = "test.readdit.db";
    private ContentValues mTagFakeValues;
    private ContentValues mPostFakeValues;
    private ContentValues mCommentFakeValues;
    private ContentValues mSubredditFakeValues;

    /**
     * Constructor.
     */
    public DatabaseContentProviderTest() {
        super(DatabaseContentProvider.class, ReadditContract.CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cleanDatabase();
    }

    private void cleanDatabase() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getContext().getDatabasePath(TEST_DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.delete(ReadditContract.Tag.TABLE_NAME, null, null);
    }

    /**
     * Test the method GetType of the DatabaseContentProvider
     */
    public void testGetType(){
        assertEquals(ReadditContract.Tag.CONTENT_TYPE, getProvider().getType(ReadditContract.Tag.CONTENT_URI));
        assertEquals(ReadditContract.Post.CONTENT_TYPE, getProvider().getType(ReadditContract.Post.CONTENT_URI));
        assertEquals(ReadditContract.Comment.CONTENT_TYPE, getProvider().getType(ReadditContract.Comment.CONTENT_URI));
        assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, getProvider().getType(ReadditContract.Subreddit.CONTENT_URI));
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
        assertEquals(ReadditContract.Tag.CONTENT_TYPE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getContext().getDatabasePath(TEST_DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY);
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
        retUri = getProvider().insert(ReadditContract.Post.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.Post.CONTENT_TYPE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        long postID = Long.parseLong(retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                new String[]{ReadditContract.Post._ID,
                        ReadditContract.Post.COLUMN_DATE,
                        ReadditContract.Post.COLUMN_CONTENT,
                        ReadditContract.Post.COLUMN_SUBREDDIT,
                        ReadditContract.Post.COLUMN_USER,
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
        assertEquals(ReadditContract.Comment.CONTENT_TYPE, retUri.getPathSegments().get(0));
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
        assertEquals(ReadditContract.Comment.CONTENT_TYPE, retUri.getPathSegments().get(0));
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
        insertValues.put(ReadditContract.Subreddit.COLUMN_SUBREDDIT, "fakesubreddit");
        retUri = getProvider().insert(ReadditContract.Subreddit.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                new String[]{ReadditContract.Subreddit._ID, ReadditContract.Subreddit.COLUMN_SUBREDDIT},
                ReadditContract.Subreddit._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Subreddit.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_SUBREDDIT)));
        cursor.close();
        db.close();
    }

    /**
     * teste delete method of the content provider
     */
    public void testDelete(){
        prepareFakeData();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getContext().getDatabasePath(TEST_DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        long tagID = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, mTagFakeValues);
        assertEquals(1, db.query(ReadditContract.Tag.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getProvider().delete(ReadditContract.Tag.CONTENT_URI, ReadditContract.Tag._ID + "=?", new String[]{String.valueOf(tagID)}));
        Cursor cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                null,
                ReadditContract.Tag._ID + "=?",
                new String[]{String.valueOf(tagID)},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        long postID = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, mPostFakeValues);
        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getProvider().delete(ReadditContract.Post.CONTENT_URI, ReadditContract.Post._ID + "=?", new String[]{String.valueOf(postID)}));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                null,
                ReadditContract.Post._ID + "=?",
                new String[]{String.valueOf(postID)},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        long commentID = db.insertOrThrow(ReadditContract.Comment.TABLE_NAME, null, mCommentFakeValues);
        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getProvider().delete(ReadditContract.Comment.CONTENT_URI, ReadditContract.Comment._ID + "=?", new String[]{String.valueOf(commentID)}));
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                null,
                ReadditContract.Comment._ID + "=?",
                new String[]{String.valueOf(commentID)},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        long subredditID = db.insertOrThrow(ReadditContract.Subreddit.TABLE_NAME, null, mSubredditFakeValues);
        assertEquals(1, db.query(ReadditContract.Subreddit.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        assertEquals(1, getProvider().delete(ReadditContract.Subreddit.CONTENT_URI, ReadditContract.Subreddit._ID + "=?", new String[]{String.valueOf(subredditID)}));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                null,
                ReadditContract.Subreddit._ID + "=?",
                new String[]{String.valueOf(subredditID)},
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        db.close();
    }

    /**
     * teste update method of the content provider
     */
    public void testUpdate(){
        prepareFakeData();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getContext().getDatabasePath(TEST_DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        long tagID = db.insertOrThrow(ReadditContract.Tag.TABLE_NAME, null, mTagFakeValues);
        assertEquals(1, db.query(ReadditContract.Tag.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        ContentValues newFakeData = new ContentValues(mTagFakeValues);
        newFakeData.put(ReadditContract.Tag.COLUMN_NAME, "newName");
        assertEquals(1, getProvider().update(ReadditContract.Tag.CONTENT_URI, newFakeData, ReadditContract.Tag._ID + "=?", new String[]{String.valueOf(tagID)}));
        Cursor cursor = db.query(ReadditContract.Tag.TABLE_NAME,
                null,
                ReadditContract.Tag._ID + "=?",
                new String[]{String.valueOf(tagID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(tagID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Tag._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Tag.COLUMN_NAME), cursor.getString(cursor.getColumnIndex(ReadditContract.Tag.COLUMN_NAME)));
        cursor.close();

        long postID = db.insertOrThrow(ReadditContract.Post.TABLE_NAME, null, mPostFakeValues);
        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mPostFakeValues);
        newFakeData.put(ReadditContract.Post.COLUMN_VOTES, 999);
        assertEquals(1, getProvider().update(ReadditContract.Post.CONTENT_URI, newFakeData, ReadditContract.Post._ID + "=?", new String[]{String.valueOf(postID)}));
        cursor = db.query(ReadditContract.Post.TABLE_NAME,
                null,
                ReadditContract.Post._ID + "=?",
                new String[]{String.valueOf(postID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(postID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Post._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_SUBREDDIT)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        assertEquals(newFakeData.getAsInteger(ReadditContract.Post.COLUMN_VOTES).intValue(), cursor.getInt(cursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        assertEquals(newFakeData.getAsString(ReadditContract.Post.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Post.COLUMN_DATE).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        cursor.close();

        long commentID = db.insertOrThrow(ReadditContract.Comment.TABLE_NAME, null, mCommentFakeValues);
        assertEquals(1, db.query(ReadditContract.Post.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mCommentFakeValues);
        newFakeData.put(ReadditContract.Comment.COLUMN_USER, "newUser");
        assertEquals(1, getProvider().update(ReadditContract.Comment.CONTENT_URI, newFakeData, ReadditContract.Comment._ID + "=?", new String[]{String.valueOf(commentID)}));
        cursor = db.query(ReadditContract.Comment.TABLE_NAME,
                null,
                ReadditContract.Comment._ID + "=?",
                new String[]{String.valueOf(commentID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(commentID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment._ID)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_DATE).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_DATE)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_PARENT).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT)));
        assertEquals(newFakeData.getAsString(ReadditContract.Comment.COLUMN_USER), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_USER)));
        assertEquals(newFakeData.getAsLong(ReadditContract.Comment.COLUMN_POST).longValue(), cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_POST)));
        assertEquals(newFakeData.getAsString(ReadditContract.Comment.COLUMN_CONTENT), cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CONTENT)));
        cursor.close();

        long subredditID = db.insertOrThrow(ReadditContract.Subreddit.TABLE_NAME, null, mSubredditFakeValues);
        assertEquals(1, db.query(ReadditContract.Subreddit.TABLE_NAME, null, null, null, null, null, null, null).getCount());
        newFakeData = new ContentValues(mSubredditFakeValues);
        newFakeData.put(ReadditContract.Subreddit.COLUMN_SUBREDDIT, "newSubreddit");
        assertEquals(1, getProvider().update(ReadditContract.Subreddit.CONTENT_URI, newFakeData, ReadditContract.Subreddit._ID + "=?", new String[]{String.valueOf(subredditID)}));
        cursor = db.query(ReadditContract.Subreddit.TABLE_NAME,
                null,
                ReadditContract.Subreddit._ID + "=?",
                new String[]{String.valueOf(subredditID)},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertEquals(subredditID, cursor.getLong(cursor.getColumnIndex(ReadditContract.Subreddit._ID)));
        assertEquals(newFakeData.getAsString(ReadditContract.Subreddit.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_SUBREDDIT)));
        cursor.close();
        db.close();
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
        mCommentFakeValues = new ContentValues();
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_CONTENT, "Great!");
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_DATE, System.currentTimeMillis());
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_PARENT, 1);
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_POST, 3);
        mCommentFakeValues.put(ReadditContract.Comment.COLUMN_USER, "me");
        mSubredditFakeValues = new ContentValues();
        mSubredditFakeValues.put(ReadditContract.Subreddit.COLUMN_SUBREDDIT, "mysubreddit");
    }

}
