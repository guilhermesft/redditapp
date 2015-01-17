package com.vanzstuff.readdit;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorTreeAdapter;

import com.vanzstuff.readdit.data.ReadditContract;

public class CommentListAdapter extends SimpleCursorTreeAdapter {

    private Context mContext;

    public CommentListAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        mContext = context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return mContext.getContentResolver().query(ReadditContract.Comment.CONTENT_URI, new String[]{
                ReadditContract.Comment._ID,
                ReadditContract.Comment.COLUMN_USER,
                ReadditContract.Comment.COLUMN_DATE,
                ReadditContract.Comment.COLUMN_CONTENT},
                ReadditContract.Comment.COLUMN_PARENT + "=?",
                new String[]{groupCursor.getString(groupCursor.getColumnIndex(ReadditContract.Comment._ID))},
                null);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        super.bindGroupView(view, context, cursor, isExpanded);
        Logger.d("bindGroupView");
    }


}
