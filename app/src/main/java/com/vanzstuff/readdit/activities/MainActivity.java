package com.vanzstuff.readdit.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.data.SubredditLoader;
import com.vanzstuff.readdit.data.TagsLoader;
import com.vanzstuff.readdit.fragments.AboutFragment;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.fragments.FeedsFragment;
import com.vanzstuff.readdit.sync.SyncAdapter;

public class MainActivity extends FragmentActivity implements FeedsFragment.CallBack, ListView.OnItemClickListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private static final int SUBREDDIT_LOADER = 0;
    private static final int USERNAME_LOADER = 0;
    private static final int TAGS_LOADER = 1;
    /* Indicate if is two panel layout or not */
    private boolean mIsTwoPanelLayout = false;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /* Navigation drawer views */
    private ListView mTagList;
    private ListView mSubredditList;
    private Button mSettings;
    private Button mAbout;
    private FeedsFragment mFeedsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFeedsFragment = (FeedsFragment) getSupportFragmentManager().findFragmentById(R.id.feeds_fragments);
        if (savedInstanceState != null && Utils.stringNotNullOrEmpty(savedInstanceState.getString(FeedsFragment.ARG_URI)))
            mFeedsFragment.loadUri(Uri.parse(savedInstanceState.getString(FeedsFragment.ARG_URI)));
        View detailFragContainer = findViewById(R.id.detail_fragment_container);
        mIsTwoPanelLayout = detailFragContainer != null && detailFragContainer.getVisibility() == View.VISIBLE;
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
        mSubredditList = (ListView) findViewById(R.id.drawer_subreddits);
        SimpleCursorAdapter subredditAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                getContentResolver().query(ReadditContract.Subreddit.CONTENT_URI, null, null, null, null),
                new String[]{ReadditContract.Subreddit.COLUMN_DISPLAY_NAME},
                new int[]{android.R.id.text1}, 0);
        getSupportLoaderManager().initLoader(SUBREDDIT_LOADER, null, new SubredditLoader(this, subredditAdapter));
        mSubredditList.setAdapter(subredditAdapter);
        mSubredditList.setOnItemClickListener(this);
        mTagList = (ListView) findViewById(R.id.drawer_tags);
        SimpleCursorAdapter tagAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                getContentResolver().query(ReadditContract.Tag.CONTENT_URI, null, null, null, null),
                new String[]{ReadditContract.Tag.COLUMN_NAME},
                new int[]{android.R.id.text1}, 0);
        getSupportLoaderManager().initLoader(TAGS_LOADER, null, new TagsLoader(this, tagAdapter));
        mTagList.setAdapter(tagAdapter);
        mTagList.setOnItemClickListener(this);
        mSettings = (Button) findViewById(R.id.drawer_settings);
        mSettings.setOnClickListener(this);
        mAbout = (Button) findViewById(R.id.drawer_about);
        mAbout.setOnClickListener(this);
        findViewById(R.id.drawer_profile_container).setOnClickListener(this);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        if( parent.getId() == R.id.drawer_tags) {
            //user clicked in a tag
            mFeedsFragment.loadUri(ReadditContract.Link.buildLinkByTagIdUri(id));
        } else {
            //user clicked in a subreddit
            String subreddit = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
            mFeedsFragment.loadUri(ReadditContract.Link.buildLinkBySubredditDisplayName(subreddit));
        }
    }

    @Override
    public void onClick(View v) {
        //let's verify which button has been clicked
       if ( v.getId() == R.id.drawer_profile_container) {
            Logger.d("Profile");
            Intent intent = new Intent(this, OAuthActivity.class);
            startActivityForResult(intent, OAuthActivity.REQUEST_LOGIN);
        } else if ( v.getId() == R.id.drawer_settings) {
            //open settgins activity
            startActivity(new Intent(this, SettingsActivity.class));
        } else if ( v.getId() == R.id.drawer_about){
            Logger.d("About");
           SyncAdapter.syncNow(this, SyncAdapter.SYNC_TYPE_SUBREDDIT | SyncAdapter.SYNC_TYPE_LINKS);
            new AboutFragment().show(getSupportFragmentManager(), "about");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mFeedsFragment.getUri() != null)
            outState.putString(FeedsFragment.ARG_URI, mFeedsFragment.getUri().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OAuthActivity.REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                getLoaderManager().initLoader(USERNAME_LOADER, null, this);
                Toast.makeText(this, "You're logged! =)", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Something wrong happend. =/", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> loader = null;
        if (id == USERNAME_LOADER) {
            loader = new CursorLoader(this, ReadditContract.User.CONTENT_URI, new String[]{ReadditContract.User.COLUMN_NAME}, null, null, null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == USERNAME_LOADER && data.moveToFirst())
            ((TextView)findViewById(R.id.drawer_username)).setText(data.getString(0));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}

