package com.vanzstuff.readdit.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.PredefinedTags;
import com.vanzstuff.readdit.R;
import com.vanzstuff.readdit.UserSession;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.readdit.data.DataHelper;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.VoteRequest;


/**
 * Fragment that show the detail info about some reddit post
 */
public class DetailFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_LINK_ID = "post_id";
    private static final int LINK_LOADER = 2;
    /*Activity that holds the fragment*/
    private long mLinkID;
    private String mFullname;
    private boolean mSaved;
    private boolean mHidden;
    private int mLikes;

    /**
     * Factory method to create a new instance of this fragment
     * @param linkID post's id to load
     * @return a DetailFragment new instance
     */
    public static DetailFragment newInstance(long linkID) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle(1);
        args.putLong(DetailFragment.ARG_LINK_ID, linkID);
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
        mLinkID = getArguments().getLong(ARG_LINK_ID, -1);
        try {
            DataHelper.setLinkRead(getActivity(), mLinkID);
        } catch (RemoteException e) {
            Logger.e(e.getLocalizedMessage(), e);
        }
    }

    /**
     * populate the view using the cursor from the loader
     * @param cursor
     */
    private void populateView(Cursor cursor) {
        if ( !cursor.moveToFirst() )
            return;
        mFullname = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_NAME));
        mSaved = cursor.getInt(cursor.getColumnIndex(ReadditContract.Link.COLUMN_SAVED)) == 1;
        mHidden = cursor.getInt(cursor.getColumnIndex(ReadditContract.Link.COLUMN_HIDDEN)) == 1;
        mLikes = cursor.getInt(cursor.getColumnIndex(ReadditContract.Link.COLUMN_LIKES));
        String selfText = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_SELFTEXT));
        String title = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_TITLE));
        final String url = cursor.getString(cursor.getColumnIndex(ReadditContract.Link.COLUMN_URL));
        getActivity().getActionBar().setTitle(title);
        View contentView = null;
        if (Utils.stringNotNullOrEmpty(selfText)) {
            //is a text post
            TextView txt = new TextView(getActivity());
            int defaultPadding = (int) getResources().getDimension(R.dimen.default_padding);
            txt.setPadding(defaultPadding,defaultPadding,defaultPadding,defaultPadding);
            txt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            txt.setTextAppearance(getActivity(), R.style.link_text);
            txt.setText(selfText);
            ScrollView scrollView = new ScrollView(getActivity());
            scrollView.addView(txt);
            contentView = scrollView;
        } else if ( Utils.isImageUrl(url) ){
            NetworkImageView networkImageView = new NetworkImageView(getActivity());
            networkImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            networkImageView.setImageUrl(url, VolleyWrapper.getInstance().getImageLoader());
            contentView = networkImageView;
        }else{
            WebView webView = new WebView(getActivity());
            webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlLoading) {
                    if (url.equals(urlLoading))
                        return false;
                    return true;
                }
            });
            webView.loadUrl(url);
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
            toggleSave();
        } else if ( v.getId() == R.id.action_menu_hide){
            //add the tag hidden to the post
            toggleHide();
        } else if ( v.getId() == R.id.action_menu_label){
            addTag();
        }
    }

    /**
     * Add tags to the current link
     */
    public void addTag() {
        InputTagFragment.newInstance(mLinkID).show(getActivity().getSupportFragmentManager(), "InputTagFragment");
    }

    /**
     * Hide the current link
     */
    public void toggleHide() {
        ContentValues values = new ContentValues(1);
        mHidden = !mHidden;
        values.put(ReadditContract.Link.COLUMN_HIDDEN, mHidden ? 1 : 0);
        if (mHidden) {
            getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mLinkID, PredefinedTags.HIDDEN.getName()), null);
        } else {
            DataHelper.removeTag(getActivity(), PredefinedTags.HIDDEN.getName(), mLinkID);
        }
        getActivity().getContentResolver().update(ReadditContract.Link.CONTENT_URI, values, ReadditContract.Link.COLUMN_NAME + "=?", new String[]{mFullname});
    }

    /**
     * Save the current link
     */
    public void toggleSave() {
        ContentValues values = new ContentValues(1);
        mSaved = !mSaved;
        values.put(ReadditContract.Link.COLUMN_SAVED, mSaved ? 1 : 0);
        if (mSaved) {
            getActivity().getContentResolver().insert(ReadditContract.Link.buildAddTagUri(mLinkID, PredefinedTags.SAVED.getName()), null);
        } else {
            DataHelper.removeTag(getActivity(),PredefinedTags.SAVED.getName(), mLinkID);
        }
        getActivity().getContentResolver().update(ReadditContract.Link.CONTENT_URI, values, ReadditContract.Link.COLUMN_NAME + "=?", new String[]{mFullname});
    }

    /**
     * Method insert or update the user vote in the mLinkID
     * @param voteDirection vote direction from VoteRequest.VOTE_UP or VoteRequest.VOTE_DOWN
     */
    public void vote(int voteDirection) {
        if ((voteDirection > 0 && mLikes > 0) || ((voteDirection < 0 && mLikes < 0)))
            voteDirection = 0;
        ContentValues values = new ContentValues(1);
        values.put(ReadditContract.Link.COLUMN_LIKES, voteDirection);
        getActivity().getContentResolver().update(ReadditContract.Link.CONTENT_URI, values,
                ReadditContract.Link.COLUMN_NAME + "=?", new String[]{mFullname});
        mLikes = voteDirection;
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

    public boolean isSaved() {
        return mSaved;
    }

    public int getLikes() {
        return mLikes;
    }

    public boolean isLinkHidden() {
        return mHidden;
    }
}
