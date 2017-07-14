package com.xmd.technician.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Dao {

    private SQLiteDatabase mDatabase;

    protected Dao(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        mDatabase = dbHelper.getWritableDatabase();
    }

    protected boolean insert(String tableName, ContentValues contentValues) {
        mDatabase.insert(tableName, null, contentValues);
        return true;
    }

    protected boolean update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        mDatabase.update(tableName, contentValues, whereClause, whereArgs);
        return true;
    }

    protected boolean delete(String tableName, String whereClause, String[] whereArgs) {
        mDatabase.delete(tableName, whereClause, whereArgs);
        return true;
    }

    protected Cursor query(String sql) {
        Cursor cursor = query(sql, null);
        return cursor;
    }

    protected Cursor query(String sql, String[] args) {
        Cursor cursor = mDatabase.rawQuery(sql, args);
        return cursor;
    }

    protected void close() {
        mDatabase.close();
    }
}
