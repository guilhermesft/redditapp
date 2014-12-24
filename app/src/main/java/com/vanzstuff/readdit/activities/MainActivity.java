package com.vanzstuff.readdit.activities;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.PostListFragment;
import com.vanzstuff.redditapp.R;

public class MainActivity extends FragmentActivity implements PostListFragment.CallBack {

    private PostListFragment mPostListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostListFragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        mPostListFragment.registerCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPostListFragment.loadDataUri(ReadditContract.Post.CONTENT_URI);
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
                    insertValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
                    insertValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "redditdev");
                    insertValues.put(ReadditContract.Post.COLUMN_USER, "fuser");
                    insertValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_THREADS, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_TITLE, "title");
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

    @Override
    public void onItemSelected(long postID) {
        Logger.d("ITEM = " + postID);
    }
}
