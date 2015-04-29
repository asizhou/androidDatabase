package com.asi.notedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ::暂时想不到有什么用
 * Created by asizhou on 2015/4/24.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static String DB_NAME    = "asi_db";
    private static String TAG        = "DbHelper";
    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1/**version >= 1*/);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	DbUtils.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	DbUtils.d(TAG, "onUpgrade");
    }
}
