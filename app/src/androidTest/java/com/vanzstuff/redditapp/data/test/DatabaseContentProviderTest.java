package com.vanzstuff.redditapp.data.test;

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
        assertEquals(ReadditContract.Subscribe.CONTENT_TYPE, getProvider().getType(ReadditContract.Subscribe.CONTENT_URI));
        assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, getProvider().getType(ReadditContract.Subreddit.CONTENT_URI));
        try {
            assertEquals(ReadditContract.Subreddit.CONTENT_TYPE, getProvider().getType(ReadditContract.BASE_CONTENT_URI.buildUpon().appendPath("xpto").build()));
        }catch (Exception e){
            assertEquals(UnsupportedOperationException.class, e.getClass());
        }
    }
}
