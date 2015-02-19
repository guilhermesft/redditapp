package com.vanzstuff.readdit.fragments;

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

import com.vanzstuff.readdit.DividerItemDecoration;
import com.vanzstuff.readdit.data.CommentAdapter;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.redditapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_LINK_ID = "post_id";
    private static final int COMMENT_LOADER = 1;
    private RecyclerView mCommentList;

    /**
     * Factory method to create a new instance of this fragment
     * @param postID post's id to load
     * @return a DetailFragment new instance
     */
    public static CommentFragment newInstance(long postID) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle(1);
        args.putLong(CommentFragment.ARG_LINK_ID, postID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        mCommentList = (RecyclerView) v.findViewById(R.id.comment_list);
        mCommentList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mCommentList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(COMMENT_LOADER, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( id == COMMENT_LOADER)
            return new CursorLoader(getActivity(), ReadditContract.Comment.buildCommentByLinkIdUri(args.getLong(ARG_LINK_ID, -1)),
                    new String[]{ ReadditContract.Comment.TABLE_NAME + ".*" },
                    null, null, null);
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<CommentAdapter.Comment> comments = new ArrayList<>(data.getCount());
        int idPos = data.getColumnIndex(ReadditContract.Comment._ID);
        int bodyPos = data.getColumnIndex(ReadditContract.Comment.COLUMN_BODY);
        int timePos = data.getColumnIndex(ReadditContract.Comment.COLUMN_CREATED_UTC);
        int userPos = data.getColumnIndex(ReadditContract.Comment.COLUMN_AUTHOR);
        int parentPos = data.getColumnIndex(ReadditContract.Comment.COLUMN_PARENT_ID);
        int namePos = data.getColumnIndex(ReadditContract.Comment.COLUMN_NAME);
        while ( data.move(1)){
            CommentAdapter.Comment c = new CommentAdapter.Comment();
            c.id = data.getLong(idPos);
            c.timestamp = data.getLong(timePos);
            c.user = data.getString(userPos);
            c.parent = data.getString(parentPos);
            c.name = data.getString(namePos);
            c.content = data.getString(bodyPos);
            comments.add(c);
        }
        RecyclerView.Adapter commentAdapter = new CommentAdapter(getActivity(), comments);
        mCommentList.swapAdapter(commentAdapter, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCommentList.swapAdapter(null, false);
    }
}
