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
    private ItemSelectedListener mListener;

    public PostListAdapter(Cursor cursor, ItemSelectedListener listener ){
        mCursor = cursor;
        mListener = listener;
        setHasStableIds(true);
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

    @Override
    public long getItemId(int position) {
        if(mCursor.moveToPosition(position))
            return mCursor.getLong(mCursor.getColumnIndex(ReadditContract.Post._ID));
        return super.getItemId(position);
    }

    public final class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTxtTitle;
        public TextView mTxtVotes;
        public TextView mTxtThread;
        public TextView mTxtUser;
        public TextView mTxtTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPostClicked(getItemId());
                }
            });
            mTxtTitle = (TextView) itemView.findViewById(R.id.post_item_title);
            mTxtVotes = (TextView) itemView.findViewById(R.id.post_item_votes);
            mTxtThread = (TextView) itemView.findViewById(R.id.post_item_thread);
            mTxtUser = (TextView) itemView.findViewById(R.id.post_item_user);
            mTxtTime = (TextView) itemView.findViewById(R.id.post_item_time);
        }
    }

    /**
     * Interface used to callback when an item is clicked
     */
    public interface ItemSelectedListener {
        /**
         * Method called when a item in the RecyclerView is clicked
         * @param postId clicked item position
         */
        public void onPostClicked(long postId);
    }
}
