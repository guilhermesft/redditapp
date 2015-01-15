package com.vanzstuff.readdit.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.VoteRequest;
import com.vanzstuff.redditapp.R;

/**
 * Fragment that show the detail info about some reddit post
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_POST_ID = "post_id";
    /*Activity that holds the fragment*/
    private Callback mCallback;
    private ImageButton mUpVoteButton;
    private ImageButton mDownVoteButton;
    private ImageButton mSaveButton;
    private ImageButton mHideButton;
    private ImageButton mLabelButton;
    private long mPostID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (Callback) activity;
        } catch (ClassCastException e){
            throw new ClassCastException("The activity " + activity.getClass().getCanonicalName() + "must implement " + Callback.class.getCanonicalName());
        }
    }

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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPostID = getArguments().getLong(ARG_POST_ID, -1);
        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(ReadditContract.Post.CONTENT_URI,
                    new String[]{ ReadditContract.Post.COLUMN_CONTENT_TYPE,
                            ReadditContract.Post.COLUMN_CONTENT},
                    ReadditContract.Post._ID + " = ?",
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
            Logger.d("up vote");
            vote(VoteRequest.VOTE_UP);
        } else if ( v.getId() == R.id.action_menu_down_vote){
            Logger.d("down vote");
            vote(VoteRequest.VOTE_DOWN);
        }else if ( v.getId() == R.id.action_menu_save){
            //add the tag saved to the post
            getActivity().getContentResolver().insert(ReadditContract.Post.buildAddTagUri(mPostID, PredefinedTags.SAVED.getName()), null);
        }else if ( v.getId() == R.id.action_menu_hide){
            //add the tag hidden to the post
            getActivity().getContentResolver().insert(ReadditContract.Post.buildAddTagUri(mPostID, PredefinedTags.HIDDEN.getName()), null);
        }else if ( v.getId() == R.id.action_menu_label){
            Logger.d("label vote");
            InputTagFragment.newInstance(mPostID).show(getActivity().getSupportFragmentManager(), "InputTagFragment");
        }
    }

    /**
     * Method insert or update the user vote in the mPostID
     * @param voteDirection vote direction from VoteRequest.VOTE_UP or VoteRequest.VOTE_DOWN
     */
    private void vote(int voteDirection){
        Cursor cursor = getActivity().getContentResolver().query(ReadditContract.Vote.CONTENT_URI, new String[]{ReadditContract.Vote._ID},
                ReadditContract.Vote.COLUMN_POST + "=? AND " + ReadditContract.Vote.COLUMN_USER + "=?",
                new String[]{String.valueOf(mPostID), String.valueOf(UserSession.getInstance().getCurrentUserInfo().id)},
                null);
        ContentValues values = new ContentValues(2);
        values.put(ReadditContract.Vote.COLUMN_USER, UserSession.getInstance().getCurrentUserInfo().id);
        values.put(ReadditContract.Vote.COLUMN_POST, mPostID);
        values.put(ReadditContract.Vote.COLUMN_DIRECTION, voteDirection);
        if ( cursor.moveToFirst()){
            //user already has voted in the post. Update de vote
            getActivity().getContentResolver().update(ReadditContract.Vote.CONTENT_URI, values,ReadditContract.Vote.COLUMN_POST + "=? AND " + ReadditContract.Vote.COLUMN_USER + "=?",
                    new String[]{String.valueOf(mPostID), String.valueOf(UserSession.getInstance().getCurrentUserInfo().id)});
        } else {
            //user has not voted in the post. Insert a vote
            getActivity().getContentResolver().insert(ReadditContract.Vote.CONTENT_URI, values);
        }
        cursor.close();
    }

    /**
     * Callback interface to holder activity
     */
    public static interface Callback {
        //TODO
    }
}
