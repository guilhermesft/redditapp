package com.vanzstuff.readdit.data;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.vanzstuff.readdit.FeedsItemTouchListener;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.Utils;
import com.vanzstuff.readdit.VolleyWrapper;
import com.vanzstuff.redditapp.R;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {

    private static final int TYPE_WITH_THUMBNAIL = 1;
    private static final int TYPE_WITHOUT_THUMBNAIL = 2;
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
        View view = null;
        if ( viewType == TYPE_WITHOUT_THUMBNAIL){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item_with_thumbnail, parent, false);
        }
        return new ViewHolder(view , viewType);
    }

    @Override
    public int getItemViewType(int position) {
        mCursor.moveToPosition(position);
        String thumbnailUrl = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_THUMBNAIL));
        String url = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_URL));
        if (Utils.isImageUrl(thumbnailUrl) || Utils.isImageUrl(url))
            return TYPE_WITH_THUMBNAIL;
        return TYPE_WITHOUT_THUMBNAIL;
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
        if( holder.mViewType == TYPE_WITH_THUMBNAIL) {
            String thumbnailUrl = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_THUMBNAIL));
            String url = mCursor.getString(mCursor.getColumnIndex(ReadditContract.Link.COLUMN_URL));
            if (Utils.isImageUrl(thumbnailUrl)) {
                holder.mThumbnail.setImageUrl(thumbnailUrl, VolleyWrapper.getInstance().getImageLoader());
            } else  {
                holder.mThumbnail.setImageUrl(url, VolleyWrapper.getInstance().getImageLoader());
            }
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
        public NetworkImageView mThumbnail;
        public int mViewType;
        private GestureDetectorCompat mDetector;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.invalidate();
            mViewType = viewType;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPostClicked(getItemId());
                }
            });
            mDetector = new GestureDetectorCompat(mContext,new GestureListener(itemView));
            itemView.setOnTouchListener(new FeedsItemTouchListener());
            mThumbnail = (NetworkImageView) itemView.findViewById(R.id.link_item_thumbnail);
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

    class GestureListener extends GestureDetector.SimpleOnGestureListener{

        private static final float SWIPE_MAX_OFF_PATH = 50;
        private static final float SWIPE_MIN_DISTANCE = 30;
        private static final float SWIPE_THRESHOLD_VELOCITY = 10;

        private View mView;

        public GestureListener(View view){
            mView = view;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Logger.d("Left Swipe");
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Logger.d("Right Swipe");
                }
            } catch (Exception e) {
                Logger.e(e.getLocalizedMessage(), e);
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
