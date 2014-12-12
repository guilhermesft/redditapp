package com.vanzstuff;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.redditapp.R;
import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 11/12/14.
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private static final int POST_TYPE_IMAGE = 0;
    private static final int POST_TYPE_LINK = 1;
    private static final int POST_TYPE_TEXT = 2;

    private Cursor mCursor;

    public PostListAdapter(Cursor cursor){
        mCursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mTxtTitle.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_TITLE)));
        holder.mTxtUser.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_USER)));
        holder.mTxtTime.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_DATE)));
        holder.mTxtVotes.setText(mCursor.getInt(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        holder.mTxtThread.setText(mCursor.getInt(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_THREADS)));
        if(getItemViewType(position) == POST_TYPE_IMAGE){
            //TODO - verify if the image is not downloaded ( saved post )
            holder.mImgPost.setImageUrl(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_IMAGE)),
                    VolleyWrapper.getInstance(null).getImageLoader());
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTxtTitle;
        public NetworkImageView mImgPost;
        public TextView mTxtVotes;
        public TextView mTxtThread;
        public TextView mTxtUser;
        public TextView mTxtTime;
        public ViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = (TextView) itemView.findViewById(R.id.post_item_title);
            mImgPost = (NetworkImageView) itemView.findViewById(R.id.post_item_img);
            mTxtVotes = (TextView) itemView.findViewById(R.id.post_item_votes);
            mTxtThread = (TextView) itemView.findViewById(R.id.post_item_thread);
            mTxtUser = (TextView) itemView.findViewById(R.id.post_item_user);
            mTxtTime = (TextView) itemView.findViewById(R.id.post_item_time);
        }
    }
}
