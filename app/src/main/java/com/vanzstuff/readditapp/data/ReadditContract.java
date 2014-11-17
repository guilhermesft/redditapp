package com.vanzstuff.redditapp.data;

import android.provider.BaseColumns;

/**
 * Created by vanz on 16/11/14.
 */
public final class ReadditContract {

    /**
     * table stores all tags created by the user
     */
    public final class Tag implements BaseColumns{
        public static final String TABLE_NAME = "tag";
        public static final String COLUMN_NAME = "name";
    }

    /**
     * table stores all post tagged/saved by the user
     */
    public final class Post implements BaseColumns{
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_SUBREDDIT = "subreddit";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_VOTES = "votes";
        public static final String COLUMN_DATE = "date";
    }

    public final class TagVsPost implements BaseColumns{
        public static final String TABLE_NAME = "tag_vs_post";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_POST = "post";
    }

    /**
     * table stores all comments ( and its parents ) saved by the user
     */
    public final class Comment implements BaseColumns{
        public static final String TABLE_NAME = "comment";
        public static final String COLUMN_PARENT = "parent";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_POST = "post";
        public static final String COLUMN_DATE = "date";
    }

    /**
     * table stores the subreddit of tagged/saved posts by the user
     */
    public final class Subreddit implements BaseColumns{
        public static final String TABLE_NAME = "subreddit";
        public static final String COLUMN_NAME = "name";

    }

    /**
     * table stores all user subscribes
     */
    public final class Subscribe implements BaseColumns{
        public static final String TABLE_NAME = "subscribe";
        public static final String COLUMN_SUBREDDIT = "subreddit";
    }
}
