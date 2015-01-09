package com.vanzstuff.readdit.activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.fragments.FeedsFragment;
import com.vanzstuff.redditapp.R;

public class MainActivity extends FragmentActivity implements FeedsFragment.CallBack, DetailFragment.Callback, ListView.OnItemClickListener{

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    /* News feeds fragment. When the user select an item the activity should load the post in
         * the detail fragment */
    private FeedsFragment mFeedsFragment;
    /* Indicate if is two panel layout or not */
    private boolean mIsTwoPanelLayout = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeedsFragment = (FeedsFragment) getSupportFragmentManager().findFragmentById(R.id.feeds_fragment);
        if (findViewById(R.id.detail_fragment_container) != null )
            mIsTwoPanelLayout = true;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                getContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null),
                new String[]{ReadditContract.Tag.COLUMN_NAME},
                new int[]{android.R.id.text1}, 0));
        mDrawerList.setOnItemClickListener(this);


}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.action_login ){
            Logger.d("Menu login clicked");
            return true;
        }
        return false;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mFeedsFragment.loadDataUri(ReadditContract.Post.buildPostByTagIdUri(id));
    }
}
