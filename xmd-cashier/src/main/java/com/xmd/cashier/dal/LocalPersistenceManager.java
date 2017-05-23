package com.xmd.cashier.dal;

import android.content.Context;

import com.xmd.cashier.dal.bean.ClubQrcodeBytes;
import com.xmd.cashier.dal.bean.User;

/**
 * Created by heyangya on 16-9-1.
 */

public class LocalPersistenceManager {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    private static final String FILE_NAME_USER = "object_user";

    public static User readUser() {
        return (User) LocalPersistence.readObjectFromFile(mContext, FILE_NAME_USER);
    }

    public static void writeUser(User user) {
        LocalPersistence.writeObjectToFile(mContext, user, FILE_NAME_USER);
    }


    public static ClubQrcodeBytes getClubQrcode(String clubId) {
        return (ClubQrcodeBytes) LocalPersistence.readObjectFromFile(mContext, clubId);
    }

    public static void writeClubQrcodeBytes(String clubId, ClubQrcodeBytes object) {
        LocalPersistence.writeObjectToFile(mContext, object, clubId);
    }

    public static void clearClubQrcodeBytes(String clubId) {
        LocalPersistence.clearObject(clubId);
    }
}
