package com.vanzstuff.readdit.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.PostListAdapter;
import com.vanzstuff.redditapp.R;
import com.vanzstuff.readdit.data.ReadditContract;

import android.net.Uri;

/**
 * Fragment that encapsulate all logic to show a list with all post acquire from a given Uri
 */
public class PostListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,PostListAdapter.ItemSelectedListener {

    private static final int POST_INIT_CURSOR_LOADER = 0;

    /** RecyclerView responsable to show all post */
    private RecyclerView mRecyclerView;
    /** Current Uri used to retrieve the post */
    private Uri mCurrentDataUri;
    /** Activity listener */
    private CallBack mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentDataUri = ReadditContract.Post.CONTENT_URI;
        getLoaderManager().initLoader(POST_INIT_CURSOR_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_post_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), mCurrentDataUri, null, null, null, ReadditContract.Post.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerView.swapAdapter(new PostListAdapter(data, this), false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.swapAdapter(null, false);
    }

    @Override
    public void onPostClicked(long postId) {
        mCallback.onItemSelected(postId);
    }

    public void loadDataUri(Uri dataUri){
        mCurrentDataUri = dataUri;
        getLoaderManager().restartLoader(POST_INIT_CURSOR_LOADER, null, this);
    }

    /**
     * Register the callback listerner. If the activity would like to recive the click events it should
     * registers itself like a callback
     * @param callBack
     */
    public void registerCallback(CallBack callBack){
        mCallback = callBack;
    }

    /**
     * Interface to enable communication between the fragment and the activity
     */
    public interface CallBack {
        /**
         * Method called when an item is clicked
         * @param postID ID of the post selected
         */
        public void onItemSelected(long postID);
    }
}
