package com.vanzstuff.readdit.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.redditapp.R;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private ItemSelectedListener mListener;

    public FeedsAdapter(Cursor cursor, ItemSelectedListener listener, Context context){
        mCursor = cursor;
        mListener = listener;
        mContext = context;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mTxtTitle.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_TITLE)));
        holder.mTxtUser.setText(Html.fromHtml(String.format(mContext.getString(R.string.link_item_user_in_subreddit),
                mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_AUTHOR)),
                mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_SUBREDDIT)))));
        long timestamp = mCursor.getLong(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_CREATED));
        holder.mTxtTime.setText(DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE));
        holder.mTxtVotes.setText(String.format(mContext.getString(R.string.link_item_ups), mCursor.getInt(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_UPS))));
        holder.mTxtComments.setText(String.format(mContext.getString(R.string.link_item_comments), mCursor.getInt(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_NUM_COMMENTS))));
        holder.mTxtDomain.setText(mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_DOMAIN)));
        String thumbnail = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_THUMBNAIL));
        String url = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_URL));
        ImageView thumnailView = null;
        if(Utils.isImageUrl(thumbnail)){
            NetworkImageView niv = new NetworkImageView(mContext);
            niv.setImageUrl(thumbnail, VolleyWrapper.getInstance().getImageLoader());
            thumnailView = niv;
        } else if ( Utils.isImageUrl(url) ){
            NetworkImageView niv = new NetworkImageView(mContext);
            niv.setImageUrl(url, VolleyWrapper.getInstance().getImageLoader());
            thumnailView = niv;
        }
        if( thumnailView != null ) {
            thumnailView.setMaxHeight((int) mContext.getResources().getDimension(R.dimen.default_thumnail_height));
            thumnailView.setMaxWidth((int) mContext.getResources().getDimension(R.dimen.default_thumnail_width));
            holder.mThumnailContainer.addView(thumnailView);
        }

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if(mCursor.moveToPosition(position))
            return mCursor.getLong(mCursor.getColumnIndex(ReadditContract.Link._ID));
        return super.getItemId(position);
    }

    public final class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTxtTitle;
        public TextView mTxtVotes;
        public TextView mTxtComments;
        public TextView mTxtUser;
        public TextView mTxtTime;
        public TextView mTxtDomain;
        public FrameLayout mThumnailContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPostClicked(getItemId());
                }
            });
            mThumnailContainer = (FrameLayout) itemView.findViewById(R.id.link_item_thumbnail_container);
            mTxtTitle = (TextView) itemView.findViewById(R.id.link_item_title);
            mTxtVotes = (TextView) itemView.findViewById(R.id.link_item_votes);
            mTxtComments = (TextView) itemView.findViewById(R.id.link_item_comments);
            mTxtUser = (TextView) itemView.findViewById(R.id.link_item_user);
            mTxtTime = (TextView) itemView.findViewById(R.id.link_item_time);
            mTxtDomain = (TextView) itemView.findViewById(R.id.link_item_domain);
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
