package com.vanzstuff.readdit;

import android.util.Log;

/**
 * Android default logger wrapper. This class was created to facilitate the logging, specially to use
 * corrent tag
 */
public final class Logger {

    private static final String TAG = "ReadditApp";

    /**
     * Print debug log message
     * @param msg mesage to print
     */
    public static final void d(String msg){
        Log.d(TAG, msg);
    }
}
