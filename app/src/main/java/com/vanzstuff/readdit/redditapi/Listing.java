package com.vanzstuff.readdit.redditapi;

/**
 * Interface provides to the request the protocol for control pagination
 * http://www.reddit.com/dev/api#listings
 * Created by vanz on 24/11/14.
 */
public interface Listing {

    public static final String PARAM_AFTER = "after";
    public static final String PARAM_BEFORE = "before";
    public static final String PARAM_COUNT = "count";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_SHOW = "show";
    public static final String DEFAULT_SHOW = "all";

}

