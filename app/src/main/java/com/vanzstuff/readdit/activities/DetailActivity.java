package com.vanzstuff.readdit.activities;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.CommentFragment;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.redditapi.VoteRequest;

public class DetailActivity extends FragmentActivity implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    public static final String EXTRA_LINK_ID = "post_id";
    private static final String DETAIL_FRAGMENT_TAG = "detail";
    private FloatingActionsMenu mFloatingMenu;
    private long mLinkID;
    private FloatingActionButton mCommentsButton;
    private DetailFragment mDetailFragment;
    private FloatingActionButton mButtonSave;
    private FloatingActionButton mButtonLabel;
    private FloatingActionButton mButtonHide;
    private FloatingActionButton mButtonUp;
    private FloatingActionButton mButtonDown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if ( getIntent().getExtras().containsKey(EXTRA_LINK_ID)){
            mLinkID = getIntent().getLongExtra(EXTRA_LINK_ID, -1);
        }
        mDetailFragment = DetailFragment.newInstance(mLinkID);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detail_activity_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
        mFloatingMenu = (FloatingActionsMenu) findViewById(R.id.floating_menu);
        mFloatingMenu.setOnFloatingActionsMenuUpdateListener(this);
        mCommentsButton = (FloatingActionButton) findViewById(R.id.action_menu_comments);
        if (!linksHasComments())
            mFloatingMenu.removeView(mCommentsButton);
        else
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

    private void loadMenuIcon() {
        Drawable icon = getResources().getDrawable(mDetailFragment.isSaved() ? R.drawable.ic_action_save_on : R.drawable.ic_action_save);
        mButtonSave.setIconDrawable(icon);
        icon = getResources().getDrawable(mDetailFragment.isLinkHidden() ? R.drawable.ic_action_remove_on : R.drawable.ic_action_remove);
        mButtonHide.setIconDrawable(icon);
        toggleLikesIcons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home == item.getItemId()){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                finish();
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
        }
        loadMenuIcon();
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

    /**
     * Check if the links has some comments
     * @return true if link has comments. Otherwise, return false
     */
    private boolean linksHasComments() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ReadditContract.Link.CONTENT_URI,
                    new String[]{ReadditContract.Link.COLUMN_NUM_COMMENTS},
                    ReadditContract.Link._ID + "=?",
                    new String[]{String.valueOf(mLinkID)}, null);
            if (cursor.moveToFirst())
                return cursor.getInt(0) > 0;
        } finally {
          if (cursor != null && !cursor.isClosed())
              cursor.close();
        }
        return false;
    }

    @Override
    public void onMenuExpanded() {
        loadMenuIcon();
    }

    @Override
    public void onMenuCollapsed() {
    }
}
