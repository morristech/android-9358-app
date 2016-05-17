package com.xmd.technician.database;

import com.xmd.technician.chat.UserProfileProvider;

/**
 * Created by linms@xiaomodo.com on 16-5-6.
 */
public class DatabaseTableColumns {

    public static final String CHAT_USER_TABLE_NAME = "chat_users";
    public static final String CHAT_USER_USER_ID = "userId";
    public static final String CHAT_USER_COLUMN_EMCHAT_ID = "emchatId";
    public static final String CHAT_USER_COLUMN_NICKNAME = "nickname";
    public static final String CHAT_USER_COLUMN_AVATAR_URL = "avatarUrl";

    public static final String SQL_CHAT_USER_CREATE = "CREATE TABLE "
            + CHAT_USER_TABLE_NAME + " ("
            + CHAT_USER_COLUMN_NAME_NICK + " TEXT, "
            + CHAT_USER_COLUMN_NAME_AVATAR + " TEXT, "
            + CHAT_USER_COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
}
