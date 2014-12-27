package com.vanzstuff.readdit.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.redditapp.R;

public class DetailActivity extends FragmentActivity implements DetailFragment.Callback {

    public static final String EXTRA_POST_ID = "post_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        long postId = -1;
        if ( getIntent().getExtras().containsKey(EXTRA_POST_ID)){
            postId = getIntent().getLongExtra(EXTRA_POST_ID, -1);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detail_activity_container, DetailFragment.newInstance(postId))
                .commit();
    }
}
