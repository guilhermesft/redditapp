package com.vanzstuff.readdit.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanzstuff.readdit.CommentListAdapter;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.User;
import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.VoteRequest;
import com.vanzstuff.redditapp.R;

/**
 * Fragment that show the detail info about some reddit post
 */
public class DetailFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_POST_ID = "post_id";
    private static final int COMMENT_CURSOR = 1;
    /*Activity that holds the fragment*/
    private ImageButton mUpVoteButton;
    private ImageButton mDownVoteButton;
    private ImageButton mSaveButton;
    private ImageButton mHideButton;
    private ImageButton mLabelButton;
    private ExpandableListView mCommentList;
    private long mPostID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        mUpVoteButton = (ImageButton) v.findViewById(R.id.action_menu_up_vote);
        mUpVoteButton.setOnClickListener(this);
        mDownVoteButton= (ImageButton) v.findViewById(R.id.action_menu_down_vote);
        mDownVoteButton.setOnClickListener(this);
        mSaveButton = (ImageButton) v.findViewById(R.id.action_menu_save);
        mSaveButton.setOnClickListener(this);
        mHideButton = (ImageButton) v.findViewById(R.id.action_menu_hide);
        mHideButton.setOnClickListener(this);
        mLabelButton = (ImageButton) v.findViewById(R.id.action_menu_label);
        mLabelButton.setOnClickListener(this);
        mCommentList = (ExpandableListView) v.findViewById(R.id.comment_list);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(COMMENT_CURSOR, getArguments(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPostID = getArguments().getLong(ARG_POST_ID, -1);
        Cursor cursor = null;
        try {
            //TODO - review
            cursor = getActivity().getContentResolver().query(ReadditContract.Link.CONTENT_URI, null,
                    ReadditContract.Link._ID + " = ?",
                    new String[]{String.valueOf(String.valueOf(mPostID))},
                    null);
            if (cursor.moveToFirst()) {
                final String contentType = cursor.getString(0);
                View contentView = null;
                if ( contentType.equals("text/")) {
                    //is a text post
                    TextView txt = new TextView(getActivity());
                    txt.setText(cursor.getString(1));
                    contentView = txt;
                } else if( contentType.startsWith("image/")) {
                    ImageView iv = new ImageView(getActivity());
                    iv.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(1)));
                    contentView = iv;
                } else if ( contentType.equals("url")){
                    WebView wv = new WebView(getActivity());
                    wv.loadUrl(cursor.getString(1));
                    contentView = wv;
                }
                FrameLayout container = (FrameLayout) getView().findViewById(R.id.content_container);
                container.addView(contentView);
            }
        } finally {
            if ( cursor != null )
                cursor.close();
        }
    }

    /**
     * Factory method to create a new instance of this fragment
     * @param postID post's id to load
     * @return a DetailFragment new instance
     */
    public static DetailFragment newInstance(long postID) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle(1);
        args.putLong(DetailFragment.ARG_POST_ID, postID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.action_menu_up_vote){
            if( UserSession.isLogged(getActivity()) )
                vote(VoteRequest.VOTE_UP);
            else
                Toast.makeText(getActivity(), getString(R.string.need_login), Toast.LENGTH_LONG).show();
        } else if ( v.getId() == R.id.action_menu_down_vote){
            if( UserSession.isLogged(getActivity()) )
                vote(VoteRequest.VOTE_DOWN);
            else
                Toast.makeText(getActivity(), getString(R.string.need_login), Toast.LENGTH_LONG).show();
        }else if ( v.getId() == R.id.action_menu_save){
            //add the tag saved to the post
            getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mPostID, PredefinedTags.SAVED.getName()), null);
        }else if ( v.getId() == R.id.action_menu_hide){
            //add the tag hidden to the post
            getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mPostID, PredefinedTags.HIDDEN.getName()), null);
        }else if ( v.getId() == R.id.action_menu_label){
            InputTagFragment.newInstance(mPostID).show(getActivity().getSupportFragmentManager(), "InputTagFragment");
        }
    }

    /**
     * Method insert or update the user vote in the mPostID
     * @param voteDirection vote direction from VoteRequest.VOTE_UP or VoteRequest.VOTE_DOWN
     */
    private void vote(int voteDirection){
        User user = UserSession.getUser(getActivity());
        Cursor cursor = getActivity().getContentResolver().query(ReadditContract.Vote.CONTENT_URI, new String[]{ReadditContract.Vote._ID},
                ReadditContract.Vote.COLUMN_POST + "=? AND " + ReadditContract.Vote.COLUMN_USER + "=?",
                new String[]{String.valueOf(mPostID), String.valueOf(user.name)},
                null);
        ContentValues values = new ContentValues(2);
        values.put(ReadditContract.Vote.COLUMN_USER, user.name);
        values.put(ReadditContract.Vote.COLUMN_POST, mPostID);
        values.put(ReadditContract.Vote.COLUMN_DIRECTION, voteDirection);
        if ( cursor.moveToFirst()){
            //user already has voted in the post. Update de vote
            getActivity().getContentResolver().update(ReadditContract.Vote.CONTENT_URI, values,ReadditContract.Vote.COLUMN_POST + "=? AND " + ReadditContract.Vote.COLUMN_USER + "=?",
                    new String[]{String.valueOf(mPostID), String.valueOf(user.name)});
        } else {
            //user has not voted in the post. Insert a vote
            getActivity().getContentResolver().insert(ReadditContract.Vote.CONTENT_URI, values);
        }
        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ReadditContract.Comment.CONTENT_URI, null,
                ReadditContract.Comment.COLUMN_POST + "=? AND " + ReadditContract.Comment.COLUMN_PARENT + " is NULL", new String[]{String.valueOf(args.getLong(ARG_POST_ID, 0))}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCommentList.setAdapter(new CommentListAdapter(getActivity(), data, R.layout.comment_item,
                new String[]{ ReadditContract.Comment.COLUMN_USER, ReadditContract.Comment.COLUMN_DATE, ReadditContract.Comment.COLUMN_CONTENT},
                new int[]{R.id.comment_user_item, R.id.comment_user_time, R.id.comment_item_content},
                R.layout.comment_item,
                new String[]{ ReadditContract.Comment.COLUMN_USER, ReadditContract.Comment.COLUMN_DATE, ReadditContract.Comment.COLUMN_CONTENT},
                new int[]{R.id.comment_user_item, R.id.comment_user_time, R.id.comment_item_content}));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCommentList.setAdapter((ExpandableListAdapter)null);
    }
}
