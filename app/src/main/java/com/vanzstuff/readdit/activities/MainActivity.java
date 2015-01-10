package com.vanzstuff.readdit.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.fragments.FeedsFragment;
import com.vanzstuff.redditapp.R;

public class MainActivity extends FragmentActivity implements FeedsFragment.CallBack, DetailFragment.Callback, ListView.OnItemClickListener, View.OnClickListener {

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    /* News feeds fragment. When the user select an item the activity should load the post in
         * the detail fragment */
    private FeedsFragment mFeedsFragment;
    /* Indicate if is two panel layout or not */
    private boolean mIsTwoPanelLayout = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mTagList;
    private Button mSettings;
    private Button mFriends;
    private Button mMessages;
    private Button mAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeedsFragment = (FeedsFragment) getSupportFragmentManager().findFragmentById(R.id.feeds_fragment);
        if (findViewById(R.id.detail_fragment_container) != null )
            mIsTwoPanelLayout = true;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(getString(R.string.open_drawer));
                getActionBar().setDisplayHomeAsUpEnabled(true);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(getString(R.string.title_activity_main));
                getActionBar().setDisplayHomeAsUpEnabled(false);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mTagList = (ListView) findViewById(R.id.drawer_tags);
        mTagList.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                getContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null),
                new String[]{ReadditContract.Tag.COLUMN_NAME},
                new int[]{android.R.id.text1}, 0));
        mTagList.setOnItemClickListener(this);
        mSettings = (Button) findViewById(R.id.drawer_settings);
        mSettings.setOnClickListener(this);
        mFriends = (Button) findViewById(R.id.drawer_friends);
        mFriends.setOnClickListener(this);
        mMessages = (Button) findViewById(R.id.drawer_messages);
        mMessages.setOnClickListener(this);
        mAbout = (Button) findViewById(R.id.drawer_about);
        mAbout.setOnClickListener(this);
        getActionBar().setHomeButtonEnabled(true);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if ( item.getItemId() == R.id.action_login ){
            Logger.d("Menu login clicked");
            return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    @Override
    public void onClick(View v) {
        //let's verify which button has been clicked
        if ( v.getId() == R.id.drawer_friends){
            //TODO
        }else if ( v.getId() == R.id.drawer_messages){
            //TODO
        }else if ( v.getId() == R.id.drawer_settings){
            //open settgins activity
            startActivity(new Intent(this, SettingsActivity.class));
        }else if ( v.getId() == R.id.drawer_about){
            //TODO - Open the about screen ( dialog or activity )
        }
    }
}

