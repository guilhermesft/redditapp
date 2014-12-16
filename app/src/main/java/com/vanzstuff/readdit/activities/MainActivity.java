package com.vanzstuff.readdit.activities;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

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
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                ContentValues insertValues = new ContentValues();
                for ( int i = 0; i < 50; i++) {
                    insertValues.put(ReadditContract.Post.COLUMN_DATE, java.util.Calendar.getInstance().getTimeInMillis());
                    insertValues.put(ReadditContract.Post.COLUMN_CONTENT, "fake content");
                    insertValues.put(ReadditContract.Post.COLUMN_SUBREDDIT, "redditdev");
                    insertValues.put(ReadditContract.Post.COLUMN_USER, "fuser");
                    insertValues.put(ReadditContract.Post.COLUMN_VOTES, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_THREADS, 2);
                    insertValues.put(ReadditContract.Post.COLUMN_TITLE, "title");
                    getContentResolver().insert(ReadditContract.Post.CONTENT_URI, insertValues);
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
        Toast.makeText(this, "Post ID = " + postID, Toast.LENGTH_SHORT).show();
    }
}
