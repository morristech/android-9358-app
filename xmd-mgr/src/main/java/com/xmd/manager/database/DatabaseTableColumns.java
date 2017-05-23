package com.xmd.manager.database;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class DatabaseTableColumns {

    public static final String CHAT_USER_TABLE_NAME = "emchat_users";
    public static final String CHAT_USER_COLUMN_NAME_ID = "emchat_id";
    public static final String CHAT_USER_COLUMN_NAME_NICK = "emchat_nick";
    public static final String CHAT_USER_COLUMN_NAME_AVATAR = "emchat_avatar";

    public static final String MANAGER_DATA_TABLE_NAME = "manager_data";
    public static final String MANAGER_DATA_TABLE_ID = "data_id";
    public static final String MANAGER_DATA_USER_COUNT = "userCount";
    public static final String MANAGER_DATA_TOTAL_WIFI_COUNT = "totalWifiCount";
    public static final String MANAGER_DATA_COUPON_GET_COUNT = "couponGetCount";
    public static final String MANAGER_DATA_TOTAL_UV = "totalUv";
    public static final String MANAGER_DATA_TOTAL_USER_COUNT = "totalUserCount";
    public static final String MANAGER_DATA_WIFI_COUNT = "wifiCount";
    public static final String MANAGER_DATA_ACCEPT_COUNT = "acceptCount";
    public static final String MANAGER_DATA_TOTAL_COUPON_GET_COUNT = "totalCouponGetCount";
    public static final String MANAGER_DATA_UV = "uv";
    public static final String MANAGER_DATA_SUBMIT_COUNT = "submitCount";
    public static final String MANAGER_DATA_COMPLETE_COUNT = "completeCount";


    public static final String EMCHAT_USER_TABLE_DROP = "DROP TABLE IF EXISTS " + CHAT_USER_TABLE_NAME;
    public static final String MANAGER_DATA_TABLE_DROP = "DROP TABLE IF EXISTS " + MANAGER_DATA_TABLE_NAME;

    public static final String EMCHAT_USER_TABLE_CREATE = "CREATE TABLE "
            + CHAT_USER_TABLE_NAME + " ("
            + CHAT_USER_COLUMN_NAME_ID + " TEXT PRIMARY KEY,"
            + CHAT_USER_COLUMN_NAME_NICK + " TEXT, "
            + CHAT_USER_COLUMN_NAME_AVATAR + " TEXT);";

    public static final String MANAGER_DATA_TABLE_CREATE = "CREATE TABLE "
            + MANAGER_DATA_TABLE_NAME + " ("
            + MANAGER_DATA_TABLE_ID + " TEXT PRIMARY KEY,"
            + MANAGER_DATA_USER_COUNT + " TEXT, "
            + MANAGER_DATA_TOTAL_WIFI_COUNT + " TEXT, "
            + MANAGER_DATA_COUPON_GET_COUNT + " TEXT, "
            + MANAGER_DATA_TOTAL_UV + " TEXT, "
            + MANAGER_DATA_TOTAL_USER_COUNT + " TEXT, "
            + MANAGER_DATA_WIFI_COUNT + " TEXT, "
            + MANAGER_DATA_ACCEPT_COUNT + " TEXT, "
            + MANAGER_DATA_TOTAL_COUPON_GET_COUNT + " TEXT, "
            + MANAGER_DATA_UV + " TEXT, "
            + MANAGER_DATA_SUBMIT_COUNT + " TEXT, "
            + MANAGER_DATA_COMPLETE_COUNT + " TEXT);";


}
