package com.vanzstuff.redditapp.data.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.vanzstuff.readdit.data.ReadditSQLOpenHelper;
import com.vanzstuff.readdit.data.ReadditContract;

/**
 * Created by vanz on 16/11/14.
 */
public class ReadditSQLOpenHelperTest extends AndroidTestCase{

    private static final String DATABASE_NAME = "readdit.db";
    private ReadditSQLOpenHelper mDBHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test.");
        setContext(context);
        getContext().getDatabasePath(DATABASE_NAME).delete();
        mDBHelper= new ReadditSQLOpenHelper(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDBHelper.close();
        mDBHelper = null;
        getContext().getDatabasePath(DATABASE_NAME).delete();
    }

    public void testOnCreate(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM main.sqlite_master WHERE type=?;", new String[]{"table"});
        assertEquals(7, cursor.getCount());
        while (cursor.moveToNext()){
            if ( cursor.getString(1).equals(ReadditContract.Subreddit.TABLE_NAME)){
                assertEquals("CREATE TABLE subreddit ( _id INTEGER PRIMARY KEY, subreddit TEXT NOT NULL )", cursor.getString(4));
            }else if (cursor.getString(1).equals(ReadditContract.Comment.TABLE_NAME)){
                assertEquals("CREATE TABLE comment ( _id INTEGER PRIMARY KEY,parent INTEGER REFERENCES comment ( _id ),content TEXT NOT NULL,date INTEGER NOT NULL, user TEXT NOT NULL, post INTEGER REFERENCES post ( _id ))", cursor.getString(4));
            }else if( cursor.getString(1).equals(ReadditContract.Post.TABLE_NAME)) {
                assertEquals("CREATE TABLE post ( _id INTEGER PRIMARY KEY, title TEXT NOT NULL, content TEXT NOT NULL, date INTEGER NOT NULL, subreddit TEXT NOT NULL, user TEXT NOT NULL, votes INTEGER DEFAULT 0, content_type TEXT NOT NULL, threads INTEGER DEFAULT 0)", cursor.getString(4));
            }else if(cursor.getString(1).equals(ReadditContract.Tag.TABLE_NAME)) {
                assertEquals("CREATE TABLE tag ( _id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL )", cursor.getString(4));
            }else if(cursor.getString(1).equals(ReadditContract.TagXPost.TABLE_NAME)) {
                assertEquals("CREATE TABLE tag_x_post ( tag INTEGER REFERENCES tag( _id) , post INTEGER REFERENCES post( _id), PRIMARY KEY ( tag, post ))", cursor.getString(4));
            }else if (cursor.getString(1).equals("android_metadata")) {
                continue;
            }else if ( cursor.getString(1).equals(ReadditContract.Vote.TABLE_NAME)){
                assertEquals("CREATE TABLE vote ( _id INTEGER PRIMARY KEY, user TEXT NOT NULL DEFAULT 0 , post INTEGER REFERENCES post( _id) , direction INTEGER DEFAULT 0 )", cursor.getString(4));
            } else {
                fail("Unexpected table: " + cursor.getString(0));
            }
        }
    }
}
