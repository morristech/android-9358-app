package com.xmd.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.xmd.manager.service.response.MainPageData;

/**
 * Created by Administrator on 2016/9/14.
 */
public class ManagerDataDao extends Dao {

    private static final String TABLE_NAME = DatabaseTableColumns.MANAGER_DATA_TABLE_NAME;
    private static final String ID = DatabaseTableColumns.MANAGER_DATA_TABLE_ID;
    public static final String USER_COUNT = DatabaseTableColumns.MANAGER_DATA_USER_COUNT;
    public static final String TOTAL_WIFI_COUNT = DatabaseTableColumns.MANAGER_DATA_TOTAL_WIFI_COUNT;
    public static final String COUPON_GET_COUNT = DatabaseTableColumns.MANAGER_DATA_COUPON_GET_COUNT;
    public static final String TOTAL_UV = DatabaseTableColumns.MANAGER_DATA_TOTAL_UV;
    public static final String TOTAL_USER_COUNT = DatabaseTableColumns.MANAGER_DATA_TOTAL_USER_COUNT;
    public static final String WIFI_COUNT = DatabaseTableColumns.MANAGER_DATA_WIFI_COUNT;
    public static final String ACCEPT_COUNT = DatabaseTableColumns.MANAGER_DATA_ACCEPT_COUNT;
    public static final String TOTAL_COUPON_GET_COUNT = DatabaseTableColumns.MANAGER_DATA_TOTAL_COUPON_GET_COUNT;
    public static final String UV = DatabaseTableColumns.MANAGER_DATA_UV;
    public static final String SUBMIT_COUNT = DatabaseTableColumns.MANAGER_DATA_SUBMIT_COUNT;
    public static final String COMPLETE_COUNT = DatabaseTableColumns.MANAGER_DATA_COMPLETE_COUNT;

    private static final String SQL_QUERY_BY_ID = "select * from " + TABLE_NAME + " where " + ID + " = ? ";

    public ManagerDataDao(Context context) {
        super(context);
    }

    public void saveOrUpdate(MainPageData pageData) {
        MainPageData data = getById(pageData.id);
        if (data != null) {

            update(pageData.id, pageData.userCount, pageData.totalWifiCount, pageData.couponGetCount, pageData.totalUv, pageData.totalUserCount, pageData.wifiCount,
                    pageData.acceptCount, pageData.totalCouponGetCount, pageData.uv, pageData.submitCount, pageData.completeCount);
        } else {
            insert(pageData.id, pageData.userCount, pageData.totalWifiCount, pageData.couponGetCount, pageData.totalUv, pageData.totalUserCount, pageData.wifiCount,
                    pageData.acceptCount, pageData.totalCouponGetCount, pageData.uv, pageData.submitCount, pageData.completeCount);
        }
    }

    private void update(String userId, String userCount, String totalWifiCount, String couponGetCount,
                        String totalUv, String totalUserCount, String wifiCount,
                        String acceptCount, String totalCouponGetCount, String uv,
                        String submitCount, String completeCount) {
        ContentValues cv = new ContentValues();
        cv.put(USER_COUNT, userCount);
        cv.put(TOTAL_WIFI_COUNT, totalWifiCount);
        cv.put(COUPON_GET_COUNT, couponGetCount);
        cv.put(TOTAL_UV, totalUv);
        cv.put(TOTAL_USER_COUNT, totalUserCount);
        cv.put(WIFI_COUNT, wifiCount);
        cv.put(ACCEPT_COUNT, acceptCount);
        cv.put(TOTAL_COUPON_GET_COUNT, totalCouponGetCount);
        cv.put(UV, uv);
        cv.put(SUBMIT_COUNT, submitCount);
        cv.put(COMPLETE_COUNT, completeCount);

        update(TABLE_NAME, cv, ID + " = ? ", new String[]{userId});
    }

    public MainPageData getById(String id) {
        Cursor cursor = query(SQL_QUERY_BY_ID, new String[]{id});
        MainPageData data = getDetailFromCursor(cursor);
        cursor.close();
        return data;
    }

    private MainPageData getDetailFromCursor(Cursor cursor) {
        MainPageData data = null;
        if (cursor == null) {

        } else if (!cursor.moveToFirst()) {

        } else {
            int columnId = cursor.getColumnIndex(ID);
            int userCount = cursor.getColumnIndex(USER_COUNT);
            int totalWifiCount = cursor.getColumnIndex(TOTAL_WIFI_COUNT);
            int couponGetCount = cursor.getColumnIndex(COUPON_GET_COUNT);
            int totalUv = cursor.getColumnIndex(TOTAL_UV);
            int totalUserCount = cursor.getColumnIndex(TOTAL_USER_COUNT);
            int wifiCount = cursor.getColumnIndex(WIFI_COUNT);
            int acceptCount = cursor.getColumnIndex(ACCEPT_COUNT);
            int totalCouponGetCount = cursor.getColumnIndex(TOTAL_COUPON_GET_COUNT);
            int uv = cursor.getColumnIndex(UV);
            int submitCount = cursor.getColumnIndex(SUBMIT_COUNT);
            int completeCount = cursor.getColumnIndex(COMPLETE_COUNT);

            String dataId = cursor.getString(columnId);
            String dataUserCount = cursor.getString(userCount);
            String dataTotalWifiCount = cursor.getString(totalWifiCount);
            String dataCouponGetCount = cursor.getString(couponGetCount);
            String dataTotalUv = cursor.getString(totalUv);
            String dataTotalUserCount = cursor.getString(totalUserCount);
            String dataWifiCount = cursor.getString(wifiCount);
            String dataAcceptCount = cursor.getString(acceptCount);
            String dataTotalCouponGetCount = cursor.getString(totalCouponGetCount);
            String dataUv = cursor.getString(uv);
            String dataSubmitCount = cursor.getString(submitCount);
            String dataCompleteCount = cursor.getString(completeCount);

            data = new MainPageData(dataId, dataUserCount, dataTotalWifiCount, dataCouponGetCount, dataTotalUv, dataTotalUserCount,
                    dataWifiCount, dataAcceptCount, dataTotalCouponGetCount, dataUv, dataSubmitCount, dataCompleteCount);

        }
        return data;
    }

    private void insert(String id, String userCount, String totalWifiCount, String couponGetCount,
                        String totalUv, String totalUserCount, String wifiCount,
                        String acceptCount, String totalCouponGetCount, String uv,
                        String submitCount, String completeCount) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(USER_COUNT, userCount);
        cv.put(TOTAL_WIFI_COUNT, totalWifiCount);
        cv.put(COUPON_GET_COUNT, couponGetCount);
        cv.put(TOTAL_UV, totalUv);
        cv.put(TOTAL_USER_COUNT, totalUserCount);
        cv.put(WIFI_COUNT, wifiCount);
        cv.put(ACCEPT_COUNT, acceptCount);
        cv.put(TOTAL_COUPON_GET_COUNT, totalCouponGetCount);
        cv.put(UV, uv);
        cv.put(SUBMIT_COUNT, submitCount);
        cv.put(COMPLETE_COUNT, completeCount);

        insert(TABLE_NAME, cv);
    }

    public void delete(String userId) {
        delete(TABLE_NAME, ID + " = ?", new String[]{userId});

    }
}
