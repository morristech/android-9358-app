package com.xmd.manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mgr_9358_db";

    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseTableColumns.EMCHAT_USER_TABLE_CREATE);
        db.execSQL(DatabaseTableColumns.MANAGER_DATA_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 2:
                db.execSQL(DatabaseTableColumns.EMCHAT_USER_TABLE_DROP);
                db.execSQL(DatabaseTableColumns.EMCHAT_USER_TABLE_CREATE);
            case 3:
                db.execSQL(DatabaseTableColumns.MANAGER_DATA_TABLE_CREATE);
                break;
        }

    }

}
