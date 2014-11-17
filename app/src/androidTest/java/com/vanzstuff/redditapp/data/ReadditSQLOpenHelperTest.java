package com.vanzstuff.redditapp.data;

import android.test.AndroidTestCase;

import com.vanzstuff.readditapp.data.ReadditSQLOpenHelper;

/**
 * Created by vanz on 16/11/14.
 */
public class ReadditSQLOpenHelperTest extends AndroidTestCase{

    public void testOnCreate(){
        ReadditSQLOpenHelper dbHelper = new ReadditSQLOpenHelper(getContext());
    }
}
