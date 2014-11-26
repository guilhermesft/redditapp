package com.vanzstuff.readdit;

import android.util.ArrayMap;

import java.util.Collections;
import java.util.Map;

/**
 * Created by vanz on 24/11/14.
 */
public class Utils {

    public static final boolean stringNotNullOrEmpty(String s){
        return !(s == null || s.isEmpty());
    }

    /**
     * Convert all parameteres in objParams to a valid string to use in the request
     * @param objParams map with parameteres object to convert
     * @return a map with string values
     */
    public static Map<String, String> parserParamsToString(Map<String, Object> objParams){
        if( objParams == null || objParams.size() == 0)
            return Collections.emptyMap();
        Map<String, String> parserParams = new ArrayMap<String, String>(objParams.size());
        if(objParams != null ) {
            for (String key : objParams.keySet()) {
                parserParams.put(key, String.valueOf(objParams.get(key)));
            }
        }
        return parserParams;

    }
}
