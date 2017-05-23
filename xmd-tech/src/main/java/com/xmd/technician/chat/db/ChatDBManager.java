package com.xmd.technician.chat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.utils.EaseCommonUtils;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Created by Lhj on 17-4-6.
 */

public class ChatDBManager {

    static private ChatDBManager dbMgr = new ChatDBManager();
    private DbOpenHelper dbHelper;

    private ChatDBManager() {
        dbHelper = DbOpenHelper.getInstance(TechApplication.getAppContext());
    }

    public static synchronized ChatDBManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new ChatDBManager();
        }
        return dbMgr;
    }

    /**
     * save contact list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<ChatUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (ChatUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                if (user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                if (user.getUserType() != null) {
                    values.put(UserDao.COLUMN_NAME_TYPE, user.getUserType());
                }else{
                    values.put(UserDao.COLUMN_NAME_TYPE, ChatConstant.CHAT_USER_TYPE_CUSTOMER);
                }
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<String, ChatUser> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, ChatUser> users = new Hashtable<String, ChatUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_TYPE));
                ChatUser user = new ChatUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                user.setUserType(type);
                if (username.equals(ChatConstant.NEW_FRIENDS_USERNAME) || username.equals(ChatConstant.GROUP_USERNAME)
                        || username.equals(ChatConstant.CHAT_ROOM) || username.equals(ChatConstant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    EaseCommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * save a contact
     *
     * @param user
     */
    synchronized public void saveContact(ChatUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        if (user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if (user.getUserType() != null) {
            values.put(UserDao.COLUMN_NAME_TYPE, user.getUserType());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }


}


