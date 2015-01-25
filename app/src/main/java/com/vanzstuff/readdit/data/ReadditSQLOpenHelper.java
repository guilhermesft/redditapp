package com.vanzstuff.readdit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                ReadditContract.Tag.COLUMN_NAME + " TEXT UNIQUE NOT NULL );";
        final String CREATE_LINK = "CREATE TABLE " + ReadditContract.Link.TABLE_NAME + " ( " +
                ReadditContract.Link._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Link.COLUMN_AUTHOR + " TEXT, " +
                ReadditContract.Link.COLUMN_AUTHOR_FLAIR_CSS_CLASS + " TEXT, " +
                ReadditContract.Link.COLUMN_AUTHOR_FLAIR_TEXT + " TEXT, " +
                ReadditContract.Link.COLUMN_CLICKED + " INTEGER, " +
                ReadditContract.Link.COLUMN_DOMAIN + " TEXT, " +
                ReadditContract.Link.COLUMN_HIDDEN + " INTEGER, " +
                ReadditContract.Link.COLUMN_IS_SELF + " INTEGER, " +
                ReadditContract.Link.COLUMN_LINK_FLAIR_CSS_CLASS + " TEXT, " +
                ReadditContract.Link.COLUMN_LINK_FLAIR_TEXT + " TEXT, " +
                ReadditContract.Link.COLUMN_MEDIA + " TEXT, " + //TODO
                ReadditContract.Link.COLUMN_MEDIA_EMBED + " TEXT, " + //TODO
                ReadditContract.Link.COLUMN_NUM_COMMENTS + " INTEGER, " +
                ReadditContract.Link.COLUMN_OVER_18 + " INTEGER, " +
                ReadditContract.Link.COLUMN_PERMALINK + " TEXT, " +
                ReadditContract.Link.COLUMN_SAVED + " INTEGER, " +
                ReadditContract.Link.COLUMN_SCORE + " INTEGER, " +
                ReadditContract.Link.COLUMN_SELFTEXT + " TEXT, " +
                ReadditContract.Link.COLUMN_SELFTEXT_HTML + " TEXT, " +
                ReadditContract.Link.COLUMN_SUBREDDIT + " TEXT, " +
                ReadditContract.Link.COLUMN_SUBREDDIT_ID + " TEXT, " +
                ReadditContract.Link.COLUMN_THUMBNAIL + " TEXT, " +
                ReadditContract.Link.COLUMN_TITLE + " TEXT, " +
                ReadditContract.Link.COLUMN_URL + " TEXT, " +
                ReadditContract.Link.COLUMN_EDITED + " INTEGER, " +
                ReadditContract.Link.COLUMN_DISTINGUISHED + " TEXT, " +
                ReadditContract.Link.COLUMN_STICKIED + " INTEGER, " +
                ReadditContract.Link.COLUMN_CREATED + " INTEGER, " +
                ReadditContract.Link.COLUMN_CREATED_UTC + " INTEGER, " +
                ReadditContract.Link.COLUMN_UPS + " INTEGER, " +
                ReadditContract.Link.COLUMN_DOWNS + " INTEGER, " +
                ReadditContract.Link.COLUMN_BANNED_BY + " TEXT, " +
                ReadditContract.Link.COLUMN_ID + " TEXT, " +
                ReadditContract.Link.COLUMN_APPROVED_BY + " TEXT, " +
                ReadditContract.Link.COLUMN_NAME + " TEXT, " +
                ReadditContract.Link.COLUMN_VISITED + " INTEGER, " +
                ReadditContract.Link.COLUMN_GILDED + " INTEGER, " +
                ReadditContract.Link.COLUMN_LIKES + " INTEGER);";
        final String CREATE_TAG_X_POST = "CREATE TABLE " + ReadditContract.TagXPost.TABLE_NAME + " ( " +
                ReadditContract.TagXPost.COLUMN_TAG + " INTEGER REFERENCES " + ReadditContract.Tag.TABLE_NAME + "( " + ReadditContract.Tag._ID + ") , " +
                ReadditContract.TagXPost.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Link.TABLE_NAME + "( " + ReadditContract.Link._ID + "), " +
                "PRIMARY KEY ( " + ReadditContract.TagXPost.COLUMN_TAG + ", " + ReadditContract.TagXPost.COLUMN_POST + " ));";
        final String CREATE_COMMENT = "CREATE TABLE " + ReadditContract.Comment.TABLE_NAME + " ( " +
                ReadditContract.Comment._ID + " INTEGER PRIMARY KEY," +
                ReadditContract.Comment.COLUMN_PARENT + " INTEGER REFERENCES " + ReadditContract.Comment.TABLE_NAME + " ( " + ReadditContract.Comment._ID + " )," +
                ReadditContract.Comment.COLUMN_CONTENT + " TEXT NOT NULL," +
                ReadditContract.Comment.COLUMN_DATE + " INTEGER NOT NULL, " +
                ReadditContract.Comment.COLUMN_USER + " TEXT NOT NULL, " +
                ReadditContract.Comment.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Link.TABLE_NAME + " ( " + ReadditContract.Link._ID + " ));";
        final String CREATE_SUBSCRIBE = "CREATE TABLE " + ReadditContract.Subreddit.TABLE_NAME + " ( " +
                ReadditContract.Subreddit._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Subreddit.COLUMN_SYNC_STATUS + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_ACCOUNTS_ACTIVE + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_COMMENT_SCORE_HIDE_MINS + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_DESCRIPTION + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_DESCRIPTION_HTML + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_DISPLAY_NAME + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_HEADER_IMG + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_HEADER_WIDTH + " INTEGER, " +
                ReadditContract.Subreddit.COLUMN_HEADER_HEIGHT + " INTEGER, " +
                ReadditContract.Subreddit.COLUMN_HEADER_TITLE + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_OVER18 + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_PUBLIC_TRAFFIC + " INTEGER, " +
                ReadditContract.Subreddit.COLUMN_SUBSCRIBERS + " INTEGER, " +
                ReadditContract.Subreddit.COLUMN_SUBMISSION_TYPE + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_SUBMIT_LINK_LABEL + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_LABEL + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_SUBREDDIT_TYPE + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_TITLE + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_URL + " TEXT, " +
                ReadditContract.Subreddit.COLUMN_USER_IS_BANNED + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_USER_IS_CONTRIBUTOR + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_USER_IS_MODERATOR + " INTEGER DEFAULT 0, " +
                ReadditContract.Subreddit.COLUMN_USER_IS_SUBSCRIBER + " INTEGER DEFAULT 0," +
                ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_HTML + " TEXT," +
                ReadditContract.Subreddit.COLUMN_ID + " TEXT," +
                ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT + " TEXT," +
                ReadditContract.Subreddit.COLUMN_COLLAPSE_DELETED_COMMENTS + " INTEGER DEFAULT 0," +
                ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION_HTML + " TEXT," +
                ReadditContract.Subreddit.COLUMN_NAME + " TEXT," +
                ReadditContract.Subreddit.COLUMN_CREATED + " INTEGER," +
                ReadditContract.Subreddit.COLUMN_CREATED_UTC + " INTEGER," +
                ReadditContract.Subreddit.COLUMN_USER + " INTEGER REFERENCES " + ReadditContract.User.TABLE_NAME + "(" + ReadditContract.User._ID + "));";
        final String CREATE_VOTE = "CREATE TABLE " + ReadditContract.Vote.TABLE_NAME + " ( " +
                ReadditContract.Vote._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Vote.COLUMN_USER + " TEXT NOT NULL , " +
                ReadditContract.Vote.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Link.TABLE_NAME + "( " + ReadditContract.Link._ID + ") , " +
                ReadditContract.Vote.COLUMN_DIRECTION + " INTEGER NOT NULL DEFAULT 0 );";
        final String CREATE_USER = "CREATE TABLE " + ReadditContract.User.TABLE_NAME + " ( " +
                ReadditContract.User._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.User.COLUMN_NAME + " TEXT NOT NULL , " +
                ReadditContract.User.COLUMN_CURRENT + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_IS_FRIEND + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_GOLD_CREDDITS + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_MODHASH + " TEXT DEFAULT 0 , " +
                ReadditContract.User.COLUMN_HAS_VERIFIED_EMAIL + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_CREATED_UTC + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_HIDE_FROM_ROBOTS + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_COMMENT_KARMA + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_OVER_18 + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_GOLD_EXPIRATION + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_CREATED + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_IS_GOLD + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_IS_MOD + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_LINK_KARMA + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_ID + " TEXT UNIQUE , " +
                ReadditContract.User.COLUMN_HAS_MAIL + " INTEGER , " +
                ReadditContract.User.COLUMN_HAS_MOD_MAIL + " INTEGER , " +
                ReadditContract.User.COLUMN_SYNC_STATUS + " INTEGER DEFAULT 0 , " +
                ReadditContract.User.COLUMN_ACCESSTOKEN + " TEXT );";

        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_TAG);
        db.execSQL(CREATE_LINK);
        db.execSQL(CREATE_TAG_X_POST);
        db.execSQL(CREATE_COMMENT);
        db.execSQL(CREATE_SUBSCRIBE);
        db.execSQL(CREATE_VOTE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //is not necessary, for while
    }
}
