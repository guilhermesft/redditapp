package com.vanzstuff.readdit;

import android.app.Application;
import android.content.ContentValues;
import android.os.AsyncTask;

import com.vanzstuff.readdit.data.ReadditContract;

/**
 * Created by vanz on 18/11/14.
 */
public class ReadditApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyWrapper.getInstance(this);
        getDatabasePath("readdit.db").delete(); //TODO - remove this line. There is just for test
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                ContentValues tagInsert = new ContentValues();
                tagInsert.put(ReadditContract.Tag.COLUMN_NAME, "tag-1");
                long tagID1 = ReadditContract.Tag.getTagId(getContentResolver().insert(ReadditContract.Tag.CONTENT_URI, tagInsert));
                tagInsert = new ContentValues();
                tagInsert.put(ReadditContract.Tag.COLUMN_NAME, "tag-2");
                long tagID2 = ReadditContract.Tag.getTagId(getContentResolver().insert(ReadditContract.Tag.CONTENT_URI, tagInsert));
                ContentValues insertValues = new ContentValues();
                for ( int i = 0; i < 50; i++) {
                    insertValues.put(ReadditContract.Post.COLUMN_DATE, java.util.Calendar.getInstance().getTimeInMillis());
                    insertValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content" + i);
                    insertValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "redditdev");
                    insertValues.put(ReadditContract.Post.COLUMN_USER, "fuser");
                    insertValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_THREADS, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_TITLE, "title" + i);
                    long postID = ReadditContract.Post.getPostId(getContentResolver().insert(ReadditContract.Post.CONTENT_URI, insertValues));
                    if (postID % 2 == 0)
                        getContentResolver().insert(ReadditContract.Post.buildAddTagUri(postID, tagID1), null);
                    else
                        getContentResolver().insert(ReadditContract.Post.buildAddTagUri(postID, tagID2), null);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        task.execute(null);
    }
}
