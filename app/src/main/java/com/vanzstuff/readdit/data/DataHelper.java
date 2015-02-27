package com.vanzstuff.readdit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.vanzstuff.readdit.Logger;

/**
 * Helper class to avoid code duplication
 */
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

    public static void removeTag(Context ctx, String tagName, long linkID) {
        long tagId = 0;
        try {
            tagId = DataHelper.getTagId(ctx, tagName);
        } catch (RemoteException e) {
            Logger.e(e.getLocalizedMessage(), e);
        }
        ctx.getContentResolver().delete(ReadditContract.TagXPost.CONTENT_URI,
                ReadditContract.TagXPost.COLUMN_LINK + "=? AND " + ReadditContract.TagXPost.COLUMN_TAG + "=?",
                new String[]{String.valueOf(linkID), String.valueOf(tagId)});
    }

    public static void removeTag(Context ctx, String tagName, String linkID) {
        Cursor cursor = null;
        try{
            cursor = ctx.getContentResolver().query(ReadditContract.Link.CONTENT_URI, new String[]{ReadditContract.Link._ID},
                    ReadditContract.Link.COLUMN_ID + "=?",
                    new String[]{linkID}, null);
            if (cursor.moveToFirst())
                DataHelper.removeTag(ctx, tagName, cursor.getLong(0));
        }finally {
            if (cursor!=null)
                cursor.close();
        }
    }

    /**
     * Check if the links has some comments
     * @return true if link has comments. Otherwise, return false
     */
    public static boolean linksHasComments(Context ctx, long linkID) {
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(ReadditContract.Link.CONTENT_URI,
                    new String[]{ReadditContract.Link.COLUMN_NUM_COMMENTS},
                    ReadditContract.Link._ID + "=?",
                    new String[]{String.valueOf(linkID)}, null);
            if (cursor.moveToFirst())
                return cursor.getInt(0) > 0;
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return false;
    }
}
