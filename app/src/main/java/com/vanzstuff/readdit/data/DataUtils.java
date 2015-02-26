package com.vanzstuff.readdit.data;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;

public class DataUtils {

    public static int setLinkRead(Context ctx, long linkID) {
        ContentValues values = new ContentValues(1);
        values.put(ReadditContract.Link.COLUMN_READ, 1);
        return ctx.getContentResolver().update(ReadditContract.Link.CONTENT_URI, values,
                ReadditContract.Link._ID + "=?",
                new String[]{String.valueOf(linkID)});
    }

}
