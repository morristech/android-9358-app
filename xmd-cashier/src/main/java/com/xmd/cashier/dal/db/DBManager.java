package com.xmd.cashier.dal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by heyangya on 16-9-8.
 */

public class DBManager extends SQLiteOpenHelper {
    private static DBManager mInstance;
    private static final int CUR_VERSION = 1;
    private static final String DB_NAME = "cashier.db";

    private DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void init(Context context) {
        mInstance = new DBManager(context, DB_NAME, null, CUR_VERSION);
    }

    public static DBManager getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new DataReportTable().getCreateSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
