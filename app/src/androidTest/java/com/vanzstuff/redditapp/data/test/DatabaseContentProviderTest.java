package com.vanzstuff.redditapp.data.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
                ReadditContract.Tag._ID + "=?",
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
                ReadditContract.Tag._ID + "=?",
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
                ReadditContract.Tag._ID + "=?",
                new String[]{String.valueOf(retUri.getPathSegments().get(1))},
                null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(insertValues.get(ReadditContract.Subreddit.COLUMN_SUBREDDIT), cursor.getString(cursor.getColumnIndex(ReadditContract.Subreddit.COLUMN_SUBREDDIT)));
        cursor.close();
        db.close();
    }


    /**
     * Prepare fake data for delete, update and query test
     */
    private void prepareFakeData() {
        mTagFakeValues = new ContentValues();
        mTagFakeValues.put(ReadditContract.Tag.COLUMN_NAME, "tag1");
    }

}
