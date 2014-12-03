package com.vanzstuff.redditapp.data.test;

import android.content.ContentValues;
import android.net.Uri;
import android.test.MoreAsserts;
import android.test.ProviderTestCase2;

import com.vanzstuff.readditapp.data.DatabaseContentProvider;
import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 02/12/14.
 */
public class DatabaseContentProviderTest extends ProviderTestCase2<DatabaseContentProvider> {

    /**
     * Constructor.
     */
    public DatabaseContentProviderTest() {
        super(DatabaseContentProvider.class, ReadditContract.CONTENT_AUTHORITY);
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
        //TODO
        //TODO validate data inserted
        //teste insert new tags
        ContentValues insertValues = new ContentValues();
        insertValues.put(ReadditContract.Tag.COLUMN_NAME, "fake");
        Uri retUri = getProvider().insert(ReadditContract.Tag.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.Tag.CONTENT_TYPE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
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
        //test insert subreddit
        insertValues.clear();
        insertValues.put(ReadditContract.Subreddit.COLUMN_SUBREDDIT, "fakesubreddit");
        retUri = getProvider().insert(ReadditContract.Subreddit.CONTENT_URI, insertValues);
        assertEquals(ReadditContract.CONTENT_AUTHORITY, retUri.getAuthority());
        assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, retUri.getPathSegments().get(0));
        MoreAsserts.assertMatchesRegex("\\d+", retUri.getPathSegments().get(1));
    }
}
