package com.vanzstuff.readdit;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vanzstuff.redditapp.R;
import com.vanzstuff.readdit.data.ReadditContract;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

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
        holder.mTxtVotes.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_VOTES)));
        holder.mTxtThread.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Post.COLUMN_THREADS)));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTxtTitle;
        public TextView mTxtVotes;
        public TextView mTxtThread;
        public TextView mTxtUser;
        public TextView mTxtTime;
        public ViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = (TextView) itemView.findViewById(R.id.post_item_title);
            mTxtVotes = (TextView) itemView.findViewById(R.id.post_item_votes);
            mTxtThread = (TextView) itemView.findViewById(R.id.post_item_thread);
            mTxtUser = (TextView) itemView.findViewById(R.id.post_item_user);
            mTxtTime = (TextView) itemView.findViewById(R.id.post_item_time);
        }
    }
}
