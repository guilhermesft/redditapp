package com.vanzstuff.readdit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;

public class TreeView extends AbsListView {

    public TreeView(Context context) {
        super(context);
    }

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ListAdapter getAdapter() {
        return null;
    }

    @Override
    public void setSelection(int position) {

    }
}
