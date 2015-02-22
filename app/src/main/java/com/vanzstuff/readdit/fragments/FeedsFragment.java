package com.vanzstuff.readdit.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vanzstuff.readdit.DividerItemDecoration;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.data.FeedsAdapter;
import com.vanzstuff.readdit.data.ReadditContract;

/**
 * Fragment that encapsulate all logic to show a list with all post acquire from a given Uri
 */
public class FeedsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,FeedsAdapter.ItemSelectedListener {

    private static final int LINK_INIT_CURSOR_LOADER = 0;
    public static final String ARG_URI = "arg_uri";

    private Uri mUri;
    /** RecyclerView responsable to show all links */
    private RecyclerView mRecyclerView;
    /** Activity listener */
    private CallBack mCallback;

    /**
     * Factory method to build a new FeedFragment
     * @param linkUri uri used to load the posts
     * @return new FeedsFragment instance
     */
    public static final FeedsFragment newInstance(Uri linkUri){
        Bundle args = new Bundle(1);
        if ( linkUri != null )
            args.putString(ARG_URI, linkUri.toString());
        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (CallBack) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("The activity " + activity.getClass().getCanonicalName() + "must implement " + CallBack.class.getCanonicalName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feeds, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_feeds_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getLoaderManager().getLoader(LINK_INIT_CURSOR_LOADER) != null)
            getLoaderManager().getLoader(LINK_INIT_CURSOR_LOADER).stopLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LINK_INIT_CURSOR_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LINK_INIT_CURSOR_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( mUri != null)
            return new CursorLoader(getActivity(), mUri, null, null, null, ReadditContract.Link.COLUMN_CREATED);
        else
            return new CursorLoader(getActivity(), ReadditContract.Link.CONTENT_URI, null, null, null, ReadditContract.Link.COLUMN_CREATED);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerView.swapAdapter(new FeedsAdapter(data, this, mRecyclerView, getActivity()), false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.stopLoading();
        mRecyclerView.swapAdapter(null, false);
    }

    @Override
    public void onLinkClicked(long linkID) {
        mCallback.onItemSelected(linkID);
    }

    @Override
    public void onLinkSaved(long linkID) {
        //remove tag
        removeLinkTag(PredefinedTags.HIDDEN.getName(), linkID);
        addLinkTag(PredefinedTags.SAVED.getName(), linkID);
        setSavedHiddenLink(1, 0, linkID);
    }

    private void setSavedHiddenLink(int saved, int hidden, long linkID) {
        ContentValues values = new ContentValues(2);
        values.put(ReadditContract.Link.COLUMN_HIDDEN, hidden);
        values.put(ReadditContract.Link.COLUMN_SAVED, saved);
        getActivity().getContentResolver().update(ReadditContract.Link.CONTENT_URI, values,
                ReadditContract.Link._ID + "=?",
                new String[]{String.valueOf(linkID)});
    }

    private void addLinkTag(String tagName, long linkID) {
        ContentValues values = new ContentValues(2);
        values.put(ReadditContract.TagXPost.COLUMN_TAG, getTagID(tagName));
        values.put(ReadditContract.TagXPost.COLUMN_LINK, linkID);
        getActivity().getContentResolver().insert(ReadditContract.TagXPost.CONTENT_URI, values);
    }

    private void removeLinkTag(String tagName, long linkID) {
        getActivity().getContentResolver().delete(ReadditContract.TagXPost.CONTENT_URI,
                ReadditContract.TagXPost.COLUMN_TAG + "=? AND " + ReadditContract.TagXPost.COLUMN_LINK + "=?",
                new String[]{String.valueOf(getTagID(tagName)), String.valueOf(linkID)});
    }

    private long getTagID(String tagName) {
        Cursor cursor = null;
        try{
            cursor = getActivity().getContentResolver().query(ReadditContract.Tag.CONTENT_URI,
                    new String[]{ReadditContract.Tag._ID},
                    ReadditContract.Tag.COLUMN_NAME + "=?",
                    new String[]{tagName}, null);
            if (cursor.moveToFirst())
                return cursor.getLong(0);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return 0;
    }

    @Override
    public void onLinkHidden(long linkID) {
        removeLinkTag(PredefinedTags.SAVED.getName(), linkID);
        addLinkTag(PredefinedTags.HIDDEN.getName(), linkID);
        setSavedHiddenLink(0, 1, linkID);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(ARG_URI) )
            mUri = Uri.parse(getArguments().getString(ARG_URI));
    }

    public void loadUri(Uri uri){
        mUri = uri;
        getLoaderManager().restartLoader(LINK_INIT_CURSOR_LOADER, null, this);
    }

    public Uri getUri() {
        return mUri;
    }



    /**
     * Interface to enable communication between the fragment and the activity
     */
    public static interface CallBack {

        /**
         * Method called when an item is clicked
         * @param postID ID of the post selected
         */
        public void onItemSelected(long postID);
    }
}
