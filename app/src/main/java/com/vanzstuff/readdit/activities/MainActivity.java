package com.vanzstuff.readdit.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.data.DataHelper;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.data.SubredditLoader;
import com.vanzstuff.readdit.data.TagsLoader;
import com.vanzstuff.readdit.fragments.AboutFragment;
import com.vanzstuff.readdit.fragments.CommentFragment;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.fragments.FeedsFragment;
import com.vanzstuff.readdit.redditapi.VoteRequest;
import com.vanzstuff.readdit.sync.SyncAdapter;

public class MainActivity extends FragmentActivity implements FeedsFragment.CallBack, ListView.OnItemClickListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

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
    private Button mAbout;
    private FeedsFragment mFeedsFragment;
    private FloatingActionButton mCommentsButton;
    private FloatingActionsMenu mFloatingMenu;
    private DetailFragment mDetailFragment;
    private FloatingActionButton mButtonSave;
    private FloatingActionButton mButtonLabel;
    private FloatingActionButton mButtonHide;
    private FloatingActionButton mButtonUp;
    private FloatingActionButton mButtonDown;
    /**
     * Current link in the detail fragment
     */
    private long mLinkID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null && Utils.stringNotNullOrEmpty(savedInstanceState.getString(FeedsFragment.ARG_URI))) {
            mFeedsFragment = FeedsFragment.newInstance(Uri.parse(savedInstanceState.getString(FeedsFragment.ARG_URI)));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.feeds_fragments_container, mFeedsFragment, null)
                    .commit();
        }
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
        mAbout = (Button) findViewById(R.id.drawer_about);
        mAbout.setOnClickListener(this);
        findViewById(R.id.drawer_profile_container).setOnClickListener(this);
        getActionBar().setHomeButtonEnabled(true);
        mFloatingMenu = (FloatingActionsMenu) findViewById(R.id.floating_menu);
        mFloatingMenu.setOnFloatingActionsMenuUpdateListener(this);
        mCommentsButton = (FloatingActionButton) findViewById(R.id.action_menu_comments);
        mCommentsButton.setOnClickListener(this);
        mButtonSave = (FloatingActionButton) findViewById(R.id.action_menu_save);
        mButtonSave.setOnClickListener(this);
        mButtonLabel = (FloatingActionButton) findViewById(R.id.action_menu_label);
        mButtonLabel.setOnClickListener(this);
        mButtonHide = (FloatingActionButton) findViewById(R.id.action_menu_hide);
        mButtonHide.setOnClickListener(this);
        mButtonUp = (FloatingActionButton) findViewById(R.id.action_menu_up_vote);
        mButtonUp.setOnClickListener(this);
        mButtonDown = (FloatingActionButton) findViewById(R.id.action_menu_down_vote);
        mButtonDown.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(USERNAME_LOADER, null, this);
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
    public void onItemSelected(long linkID) {
        if ( mIsTwoPanelLayout ){
            if (!DataHelper.linksHasComments(this, linkID))
                mCommentsButton.setVisibility(View.GONE);
            else
                mCommentsButton.setVisibility(View.VISIBLE);
            mDetailFragment = DetailFragment.newInstance(linkID);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
            mLinkID = linkID;
            loadMenuIcon();
        }else{
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailActivity.EXTRA_LINK_ID, linkID);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( parent.getId() == R.id.drawer_tags) {
            //user clicked in a tag
            mFeedsFragment = FeedsFragment.newInstance(ReadditContract.Link.buildLinkByTagIdUri(id));
        } else {
            //user clicked in a subreddit
            String subreddit = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
            mFeedsFragment = FeedsFragment.newInstance(ReadditContract.Link.buildLinkBySubredditDisplayName(subreddit, true));
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.feeds_fragments_container, mFeedsFragment, null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_menu_comments: {
                mFloatingMenu.collapse();
                mFloatingMenu.setVisibility(View.INVISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.detail_activity_container, CommentFragment.newInstance(mLinkID), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
            case R.id.action_menu_save: {
                mDetailFragment.toggleSave();
                break;
            }
            case R.id.action_menu_hide: {
                mDetailFragment.toggleHide();
                break;
            }
            case R.id.action_menu_label: {
                mDetailFragment.addTag();
                break;
            }
            case R.id.action_menu_up_vote: {
                mDetailFragment.vote(VoteRequest.VOTE_UP);
                break;
            }
            case R.id.action_menu_down_vote: {
                DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                if (detail != null)
                    detail.vote(VoteRequest.VOTE_DOWN);
                break;
            }
            case R.id.drawer_profile_container: {
                Logger.d("Profile");
                Intent intent = new Intent(this, OAuthActivity.class);
                startActivityForResult(intent, OAuthActivity.REQUEST_LOGIN);
                break;
            }
            case R.id.drawer_about: {
                Logger.d("About");
                SyncAdapter.syncNow(this, SyncAdapter.SYNC_TYPE_SUBREDDIT | SyncAdapter.SYNC_TYPE_ALL);
                new AboutFragment().show(getSupportFragmentManager(), "about");
            }
        }
        loadMenuIcon();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mFeedsFragment != null && mFeedsFragment.getUri() != null)
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

    private void loadMenuIcon() {
        Drawable icon = getResources().getDrawable(mDetailFragment.isSaved() ? R.drawable.ic_action_save_on : R.drawable.ic_action_save);
        mButtonSave.setIconDrawable(icon);
        icon = getResources().getDrawable(mDetailFragment.isLinkHidden() ? R.drawable.ic_action_remove_on : R.drawable.ic_action_remove);
        mButtonHide.setIconDrawable(icon);
        toggleLikesIcons();
    }

    /**
     * Set right images to up and down button in the floating menu
     */
    private void toggleLikesIcons() {
        Drawable iconUp = null;
        Drawable iconDown = null;
        if (mDetailFragment.getLikes() > 0) {
            iconUp = getResources().getDrawable(R.drawable.ic_action_good_on);
            iconDown = getResources().getDrawable(R.drawable.ic_action_bad);
        } else if (mDetailFragment.getLikes() < 0) {
            iconUp = getResources().getDrawable(R.drawable.ic_action_good);
            iconDown = getResources().getDrawable(R.drawable.ic_action_bad_on);
        } else {
            iconUp = getResources().getDrawable(R.drawable.ic_action_good);
            iconDown = getResources().getDrawable(R.drawable.ic_action_bad);
        }
        mButtonUp.setIconDrawable(iconUp);
        mButtonDown.setIconDrawable(iconDown);
    }

    @Override
    public void onMenuExpanded() {

    }

    @Override
    public void onMenuCollapsed() {

    }
}

