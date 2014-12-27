package com.vanzstuff.readdit.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.redditapp.R;

/**
 * Fragment that show the detail info about some reddit post
 */
public class DetailFragment extends Fragment {

    private static final String ARG_POST_ID = "post_id";
    /*Activity that holds the fragment*/
    private Callback mCallback;
    private long mPostID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (Callback) activity;
        } catch (ClassCastException e){
            throw new ClassCastException("The activity " + activity.getClass().getCanonicalName() + "must implement " + Callback.class.getCanonicalName());
        }
        mPostID = getArguments().getLong(ARG_POST_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    /**
     * Method to load detail about post of postID
     */
    private void load() {
        Logger.d("Loading " + mPostID);
        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(ReadditContract.Post.CONTENT_URI,
                    null,
                    ReadditContract.Post._ID + " = ?",
                    new String[]{String.valueOf(mPostID)},
                    null);
            if (cursor.moveToFirst()) {
                TextView txt = new TextView(getActivity());
                txt.setText(cursor.getString(cursor.getColumnIndex(ReadditContract.Post.COLUMN_CONTENT)));
                FrameLayout container = (FrameLayout) getView().findViewById(R.id.content_container);
                container.addView(txt);
            }
        } finally {
            if ( cursor != null )
                cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( mPostID > 0 )
            load();
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

    /**
     * Callback interface to holder activity
     */
    public static interface Callback {
        //TODO
    }
}
