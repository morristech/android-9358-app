package com.xmd.technician.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.common.Logger;

/**
 * Created by Lhj on 17-4-6.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_TYPE + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";


    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return ChatHelper.getInstance().getCurrentUserName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_TYPE + " TEXT ;");
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}

