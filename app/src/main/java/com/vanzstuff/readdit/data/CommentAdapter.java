package com.vanzstuff.readdit.data;


import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vanzstuff.redditapp.R;

/**
 * This Adapter is used in the ExpandableListView to show the link's comments
 */
public class CommentAdapter extends CursorTreeAdapter {

    private final Context mContext;

    public CommentAdapter(Context context, Cursor cursor) {
        super(cursor, context);
        mContext = context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return mContext.getContentResolver().query(ReadditContract.Comment.CONTENT_URI, null,
                ReadditContract.Comment.COLUMN_PARENT_ID + "=?",
                new String[] { groupCursor.getString(groupCursor.getColumnIndex(ReadditContract.Comment.COLUMN_NAME)) },
                null);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        return inflateNewView(context, cursor, parent);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        bindData(view, cursor);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        return inflateNewView(context, cursor, parent);
    }

    private void bindData(View v, Cursor cursor) {
        TextView user = (TextView) v.findViewById(R.id.comment_user);
        TextView time = (TextView) v.findViewById(R.id.comment_time);
        TextView content = (TextView) v.findViewById(R.id.comment_content);
        user.setText(cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_AUTHOR)));
        long timestamp = cursor.getLong(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_CREATED));
        time.setText(DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE));
        content.setText(cursor.getString(cursor.getColumnIndex(ReadditContract.Comment.COLUMN_BODY)));
    }

    private View inflateNewView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.comment_view, null);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        bindData( view, cursor);
        Cursor childrenCursor = getChildrenCursor(cursor);
        if (childrenCursor.getCount() > 0){
            ExpandableListView childrenList = new ExpandableListView(context);
            childrenList.setTag("children");
            childrenList.setAdapter(new CommentAdapter(context, childrenCursor));
            childrenList.setLayoutParams(new ExpandableListView.LayoutParams(ExpandableListView.LayoutParams.WRAP_CONTENT, childrenList.getMeasuredHeight()));

            ((RelativeLayout)view.findViewById(R.id.comment_root)).addView(childrenList);
        }else{
            RelativeLayout root = (RelativeLayout) view.findViewById(R.id.comment_root);
            root.removeView(root.findViewWithTag("children"));
        }
    }
}
