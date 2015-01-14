package com.vanzstuff.readdit.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.data.ReadditContract;
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
            //TODO
        } else if ( v.getId() == R.id.action_menu_down_vote){
            Logger.d("down vote");
            //TODO
        }else if ( v.getId() == R.id.action_menu_save){
            //add the tag saved to the post
            getActivity().getContentResolver().insert(ReadditContract.Post.buildAddTagUri(mPostID, PredefinedTags.SAVED.getName()), null);
        }else if ( v.getId() == R.id.action_menu_hide){
            //add the tag hidden to the post
            getActivity().getContentResolver().insert(ReadditContract.Post.buildAddTagUri(mPostID, PredefinedTags.HIDDEN.getName()), null);
        }else if ( v.getId() == R.id.action_menu_label){
            Logger.d("label vote");
            InputTagFragment.newInstance(mPostID).show(getActivity().getSupportFragmentManager(), "InputTagFragment");
        }else{
            Logger.d("What?");
        }
    }

    /**
     * This method open a dialog for the user enter the labels and persistem in the database
     */
    private void addLabels() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    }

    /**
     * Callback interface to holder activity
     */
    public static interface Callback {
        //TODO
    }
}
