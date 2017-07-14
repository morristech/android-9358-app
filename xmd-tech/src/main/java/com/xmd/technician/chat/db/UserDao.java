package com.xmd.technician.chat.db;

import android.content.Context;

import com.xmd.technician.chat.ChatUser;

import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 17-4-6.
 */

public class UserDao {

    public static final String TABLE_NAME = "chat_users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_TYPE = "type";

    public UserDao(Context context) {

    }

    /**
     * 保存联系人列表
     *
     * @param contactList
     */
    public void saveContactList(List<ChatUser> contactList) {
        ChatDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取联系人列表
     *
     * @return
     */
    public Map<String, ChatUser> getContactList() {

        return ChatDBManager.getInstance().getContactList();
    }

    /**
     * 删除联系人
     *
     * @param username
     */
    public void deleteContact(String username) {
        ChatDBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存联系人
     *
     * @param user
     */
    public void saveContact(ChatUser user) {
        ChatDBManager.getInstance().saveContact(user);
    }

}

