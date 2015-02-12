package com.vanzstuff.readdit.data;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

/**
 * This Adapter is used in the ExpandableListView to show the link's comments
 */
public class CommentAdapter extends SimpleCursorTreeAdapter {


    public CommentAdapter(Context context, Cursor cursor, int collapsedGroupLayout, int expandedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, collapsedGroupLayout, expandedGroupLayout, groupFrom, groupTo, childLayout, lastChildLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return null;
    }
}
