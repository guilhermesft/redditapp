package com.vanzstuff.readdit.data;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;

public class DataHelper {

    public static int setLinkRead(Context ctx, long linkID) throws RemoteException {
        ContentValues values = new ContentValues(1);
        values.put(ReadditContract.Link.COLUMN_READ, 1);
        return ctx.getContentResolver().update(ReadditContract.Link.CONTENT_URI, values,
                ReadditContract.Link._ID + "=?",
                new String[]{String.valueOf(linkID)});
    }

    /**
     * get the id of given tag
     * @param tagName
     * @return tag id
     */
    public static long getTagId(Context ctx, String tagName) throws RemoteException {
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(ReadditContract.Tag.CONTENT_URI,
                    new String[]{ReadditContract.Tag._ID},
                    ReadditContract.Tag.COLUMN_NAME + "=?",
                    new String[]{tagName}, null);
            if (cursor.moveToFirst())
                return cursor.getLong(0);
        }finally {
            if (cursor!=null)
                cursor.close();
        }
        return 0;
    }

}
