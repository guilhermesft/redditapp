package com.vanzstuff.readdit.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.fragments.FeedsFragment;
import com.vanzstuff.redditapp.R;

public class MainActivity extends FragmentActivity implements FeedsFragment.CallBack, DetailFragment.Callback {

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    /* News feeds fragment. When the user select an item the activity should load the post in
         * the detail fragment */
    private FeedsFragment mFeedsFragment;
    /* Indicate if is two panel layout or not */
    private boolean mIsTwoPanelLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeedsFragment = (FeedsFragment) getSupportFragmentManager().findFragmentById(R.id.feeds_fragment);
        if (findViewById(R.id.detail_fragment_container) != null )
            mIsTwoPanelLayout = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFeedsFragment.loadDataUri(ReadditContract.Post.CONTENT_URI);
    }

    @Override
    public void onItemSelected(long postID) {
        if ( mIsTwoPanelLayout ){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, DetailFragment.newInstance(postID), DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailActivity.EXTRA_POST_ID, postID);
            startActivity(intent);
        }
    }
}
