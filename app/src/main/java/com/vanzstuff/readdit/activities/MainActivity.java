package com.vanzstuff.readdit.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.vanzstuff.readdit.fragments.PostListFragment;
import com.vanzstuff.redditapp.R;

public class MainActivity extends FragmentActivity {

    private PostListFragment mPostListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostListFragment = (PostListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
    }
}
