package com.vanzstuff.readdit.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.fragments.CommentFragment;
import com.vanzstuff.readdit.fragments.DetailFragment;
import com.vanzstuff.readdit.redditapi.VoteRequest;

public class DetailActivity extends FragmentActivity implements View.OnClickListener {

    public static final String EXTRA_LINK_ID = "post_id";
    private static final String DETAIL_FRAGMENT_TAG = "detail";
    private FloatingActionsMenu mFloatingMenu;
    private long mLinkID;
    private FloatingActionButton mCommentsButton;
    private DetailFragment mDetailFragment;

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
        mCommentsButton = (FloatingActionButton) findViewById(R.id.action_menu_comments);
        if (!linksHasComments())
            mFloatingMenu.removeView(mCommentsButton);
        else
            mCommentsButton.setOnClickListener(this);
        findViewById(R.id.action_menu_save).setOnClickListener(this);
        findViewById(R.id.action_menu_label).setOnClickListener(this);
        findViewById(R.id.action_menu_hide).setOnClickListener(this);
        findViewById(R.id.action_menu_up_vote).setOnClickListener(this);
        findViewById(R.id.action_menu_down_vote).setOnClickListener(this);
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
                DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                if (detail != null )
                    detail.save();
                break;
            }
            case R.id.action_menu_hide: {
                DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                if (detail != null )
                    detail.hide();
                break;
            }
            case R.id.action_menu_label: {
                DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                if (detail != null )
                    detail.addTag();
                break;
            }
            case R.id.action_menu_up_vote: {
                if( UserSession.isLogged(this) ) {
                    DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                    if (detail != null)
                        detail.vote(VoteRequest.VOTE_UP);
                }
                break;
            }
            case R.id.action_menu_down_vote: {
                if( UserSession.isLogged(this) ) {
                    DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                    if (detail != null)
                        detail.vote(VoteRequest.VOTE_DOWN);
                }
                break;
            }
        }
    }

    /**
     * Check if the links has some comments
     * @return true if link has comments. Otherwise, return false
     */
    private boolean linksHasComments() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ReadditContract.Comment.buildCommentByLinkIdUri(mLinkID),
                    new String[]{"count(" + ReadditContract.Comment.TABLE_NAME + "." + ReadditContract.Comment._ID + ")"},
                    null, null, null);
            if (cursor.moveToFirst())
                return cursor.getInt(0) > 0;
        } finally {
          if (cursor != null && !cursor.isClosed())
              cursor.close();
        }
        return false;
    }
}
