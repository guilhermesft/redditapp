package com.vanzstuff.readdit.fragments;

import android.content.ContentValues;
import android.database.Cursor;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.vanzstuff.readdit.DividerItemDecoration;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.User;
import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.data.CommentAdapter;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.VoteRequest;
import com.vanzstuff.readdit.sync.SyncAdapter;
import com.vanzstuff.redditapp.R;

import java.util.List;
import java.util.ArrayList;

/**
 * Fragment that show the detail info about some reddit post
 */
public class DetailFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_LINK_ID = "post_id";
    private static final int LINK_LOADER = 2;
    /*Activity that holds the fragment*/
    private long mPostID;
    private String mFullname;

    /**
     * Factory method to create a new instance of this fragment
     * @param postID post's id to load
     * @return a DetailFragment new instance
     */
    public static DetailFragment newInstance(long postID) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle(1);
        args.putLong(DetailFragment.ARG_LINK_ID, postID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LINK_LOADER, getArguments(), this);
        mPostID = getArguments().getLong(ARG_LINK_ID, -1);
    }

    /**
     * populate the view using the cursor from the loader
     * @param cursor
     */
    private void populateView(Cursor cursor) {
        if ( !cursor.moveToFirst() )
            return;
        mFullname = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_NAME));
        String selfText = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_SELFTEXT));
        final String url = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_URL));
        View contentView = null;
        if (Utils.stringNotNullOrEmpty(selfText)) {
            //is a text post
            TextView txt = new TextView(getActivity());
            txt.setText(selfText);
            contentView = txt;
        } else if ( Utils.isImageUrl(url) ){
            NetworkImageView networkImageView = new NetworkImageView(getActivity());
            networkImageView.setImageUrl(url, VolleyWrapper.getInstance().getImageLoader());
            contentView = networkImageView;
        }else{
            WebView webView = new WebView(getActivity());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlLoading) {
                    return url.equals(urlLoading);
                }
            });
            webView.loadUrl( url );
            contentView = webView;
        }
        FrameLayout container = (FrameLayout) getView().findViewById(R.id.content_container);
        container.addView(contentView);
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
        } else if ( v.getId() == R.id.action_menu_save){
            //add the tag saved to the post
            save();
        } else if ( v.getId() == R.id.action_menu_hide){
            //add the tag hidden to the post
            hide();
        } else if ( v.getId() == R.id.action_menu_label){
            InputTagFragment.newInstance(mPostID).show(getActivity().getSupportFragmentManager(), "InputTagFragment");
        }
    }

    public void hide() {
        getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mPostID, PredefinedTags.HIDDEN.getName()), null);
    }

    public void save() {
        getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mPostID, PredefinedTags.SAVED.getName()), null);
    }

    /**
     * Method insert or update the user vote in the mPostID
     * @param voteDirection vote direction from VoteRequest.VOTE_UP or VoteRequest.VOTE_DOWN
     */
    public void vote(int voteDirection){
        User user = UserSession.getUser(getActivity());
        ContentValues values = new ContentValues(2);
        values.put(ReadditContract.Vote.COLUMN_USER, user.name);
        values.put(ReadditContract.Vote.COLUMN_THING_FULLNAME, mFullname);
        values.put(ReadditContract.Vote.COLUMN_DIRECTION, voteDirection);
        values.put(ReadditContract.Vote.COLUMN_SYNC_STATUS, SyncAdapter.SYNC_STATUS_UPDATE);
        getActivity().getContentResolver().insert(ReadditContract.Vote.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       if (id == LINK_LOADER )
            return new CursorLoader(getActivity(), ReadditContract.Link.CONTENT_URI, null,
                    ReadditContract.Link._ID + " = ?",
                    new String[]{String.valueOf(args.getLong(ARG_LINK_ID, -1))},
                    null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if ( loader.getId() == LINK_LOADER ) {
            populateView(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setHomeButtonEnabled(true);
    }
}
