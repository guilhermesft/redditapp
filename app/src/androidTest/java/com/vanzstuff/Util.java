package com.vanzstuff;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by vanz on 30/11/14.
 */
public class Util {

    public static Map<String, String> getPostParams(String postBody){
        HashMap<String, String> postParams = new HashMap<String, String>();
        String[] params = postBody.split("&");
        for(String param : params){
            String[] parts = param.split("=");
            postParams.put(parts[0], parts[1]);
        }
        return postParams;
    }
}
