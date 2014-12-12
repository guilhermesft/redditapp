package com.vanzstuff;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vanzstuff.redditapp.R;

import java.net.URI;

/**
 * Fragment that show a list of the reddit posts
 */
public class PostListFragment extends Fragment {

    private static final String PARAM_CURSOR = "PARAM_CURSOR";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mPostAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static PostListFragment newInstance(URI postURI) {
        Bundle args = new Bundle(1);
        args.putSerializable(PARAM_CURSOR, postURI);
        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_post_list_recycler_view);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Uri uri = (Uri) getArguments().getSerializable(PARAM_CURSOR);
        mPostAdapter = new PostListAdapter(getActivity().getContentResolver().query(uri, null, null, null, null));
        mRecyclerView.setAdapter(mPostAdapter);
    }

}
