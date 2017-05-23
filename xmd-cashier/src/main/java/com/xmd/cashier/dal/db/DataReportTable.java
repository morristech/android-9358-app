package com.xmd.cashier.dal.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xmd.cashier.dal.bean.ReportData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-9-8.
 */

public class DataReportTable implements ITable {
    private final static String TABLE_NAME = "pay_data_report";
    private final static String C_TRADE_NO = "trade_no";
    private final static String C_REPORT_DATA = "report_data";

    @Override
    public String getCreateSql() {
        return "CREATE TABLE " + TABLE_NAME + "("
                + C_TRADE_NO + " TEXT PRIMARY KEY,"
                + C_REPORT_DATA + " BLOB)";
    }

    public static void insert(ReportData reportData) {
        SQLiteDatabase db = DBManager.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(C_TRADE_NO, reportData.tradeNo);
        contentValues.put(C_REPORT_DATA, reportData.data);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public static List<ReportData> query() {
        List<ReportData> result = new ArrayList<>();
        SQLiteDatabase db = DBManager.getInstance().getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            ReportData data = new ReportData();
            data.tradeNo = cursor.getString(cursor.getColumnIndex(C_TRADE_NO));
            data.data = cursor.getBlob(cursor.getColumnIndex(C_REPORT_DATA));
            result.add(data);
        }
        cursor.close();
        return result;
    }

    public static boolean isDataExist(String tradeNo) {
        boolean result = false;
        SQLiteDatabase db = DBManager.getInstance().getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, C_TRADE_NO + "=?", new String[]{tradeNo}, null, null, null, null);
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }


    public static void delete(String tradeNo) {
        SQLiteDatabase db = DBManager.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, C_TRADE_NO + "=?", new String[]{tradeNo});
    }
}
