package com.vanzstuff.readditapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 16/11/14.
 */
public class ReadditSQLOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "readdit.db";

    public ReadditSQLOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TAG = "CREATE TABLE " + ReadditContract.Tag.TABLE_NAME + " ( " +
                ReadditContract.Tag._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Tag.COLUMN_NAME + " TEXT NOT NULL );";
        final String CREATE_SUBREDDIT = "CREATE TABLE " + ReadditContract.Subreddit.TABLE_NAME + " ( " +
                ReadditContract.Subreddit._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Subreddit.COLUMN_NAME + " TEXT NOT NULL UNIQUE);";
        final String CREATE_POST = "CREATE TABLE " + ReadditContract.Post.TABLE_NAME + " ( " +
                ReadditContract.Post._ID + " INTERGER PRIMARY KEY, " +
                ReadditContract.Post.COLUMN_CONTENT + " TEXT NOT NULL" +
                ReadditContract.Post.COLUMN_DATE + " TEXT NOT NULL" + //TODO text type is the best choice?
                ReadditContract.Post.COLUMN_SUBREDDIT + "INTEGER REFERENCES " + ReadditContract.Subreddit.TABLE_NAME + " ( " + ReadditContract.Subreddit._ID + " )," +
                ReadditContract.Post.COLUMN_USER +  " TEXT NOT NULL," +
                ReadditContract.Post.COLUMN_VOTES + " INTEGER DEFAULT 0);";
        final String CREATE_TAG_X_POST = "CREATE TABLE " + ReadditContract.TagVsPost.TABLE_NAME + " ( " +
                ReadditContract.TagVsPost.COLUMN_TAG + " INTEGER REFERENCES " + ReadditContract.Tag.TABLE_NAME + "( " + ReadditContract.Tag._ID + ") , " +
                ReadditContract.TagVsPost.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Post.TABLE_NAME + "( " + ReadditContract.Post._ID + "));";
        final String CREATE_COMMENT = "CREATE TABLE " + ReadditContract.Comment.TABLE_NAME + " ( " +
                ReadditContract.Comment._ID + " INTEGER PRIMARY KEY," +
                ReadditContract.Comment.COLUMN_PARENT + " INTEGER REFERENCES " + ReadditContract.Comment.TABLE_NAME + " ( " + ReadditContract.Comment._ID + " )," +
                ReadditContract.Comment.COLUMN_CONTENT + " TEXT NOT NULL," +
                ReadditContract.Comment.COLUMN_DATE + " TEXT NOT NULL, " +
                ReadditContract.Comment.COLUMN_USER + " TEXT NOT NULL, " +
                ReadditContract.Comment.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Post.TABLE_NAME + " ( " + ReadditContract.Post._ID + " ));";

        db.execSQL(CREATE_TAG);
        db.execSQL(CREATE_SUBREDDIT);
        db.execSQL(CREATE_POST);
        db.execSQL(CREATE_TAG_X_POST);
        db.execSQL(CREATE_COMMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //is not necessary, for while
    }
}
