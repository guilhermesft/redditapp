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

    /**
     * Print warn log message
     * @param msg mesage to print
     */
    public static final void w(String msg) {
        Log.w(TAG, msg);
    }

    /**
     * Print error log message
     * @param msg mesage to print
     * @param error error threw
     */
    public static void e(String msg, Throwable error) {
        Log.e(TAG, msg, error);
    }

    /**
     * Print error log message
     * @param msg mesage to print
     */
    public static void e(String msg) {
        Logger.e(msg, null);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }
}
