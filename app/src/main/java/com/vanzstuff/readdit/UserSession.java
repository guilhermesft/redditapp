package com.vanzstuff.readdit;

import android.content.Context;
import android.database.Cursor;

import com.vanzstuff.readdit.data.ReadditContract;


public class UserSession {

    public static User getUser(Context context){
        Cursor cursor = context.getContentResolver().query(ReadditContract.User.CONTENT_URI, new String[]{ReadditContract.User.COLUMN_NAME,
                ReadditContract.User.COLUMN_ACCESSTOKEN, ReadditContract.User.COLUMN_CURRENT}, ReadditContract.User.COLUMN_CURRENT + "=?", new String[]{"1"}, null);
        try {
            if (cursor.moveToFirst()) {
                User user = new User();
                user.name = cursor.getString(0);
                user.accessToken = cursor.getString(1);
                user.currentUser = cursor.getInt(2) == 1 ? true : false;
                return user;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static boolean isLogged(Context context) {
        Cursor cursor = context.getContentResolver().query(ReadditContract.User.CONTENT_URI, new String[]{ ReadditContract.User.COLUMN_CURRENT}, ReadditContract.User.COLUMN_CURRENT + "=?", new String[]{"1"}, null);
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }
}
