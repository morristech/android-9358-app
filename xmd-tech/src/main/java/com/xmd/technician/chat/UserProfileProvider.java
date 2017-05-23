package com.xmd.technician.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.HanziToPinyin;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.db.ChatDBManager;
import com.xmd.technician.chat.db.DbOpenHelper;
import com.xmd.technician.chat.event.EventEmChatLoginSuccess;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.msgctrl.RxBus;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Lhj on 16-4-12.
 */
public class UserProfileProvider {

    public static final String TABLE_NAME = "chat_users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_TYPE = "type";

    private DbOpenHelper dbHelper;
    private static UserProfileProvider userProvider;
    private Map<String, ChatUser> mLocalUsers;
    private ChatUser mCurrentUser;

    public static UserProfileProvider getInstance() {
        if (userProvider == null) {
            synchronized (UserProfileProvider.class) {
                if (userProvider == null) {
                    userProvider = new UserProfileProvider();
                }
            }
        }
        return userProvider;
    }

    private UserProfileProvider() {
        dbHelper = DbOpenHelper.getInstance(TechApplication.getAppContext());

        //注册监听环信登录消息
        RxBus.getInstance().toObservable(EventEmChatLoginSuccess.class).subscribe(
                eventEmChatLogin -> {
                    //初始化联系人列表
                    initContactList();
                }
        );
    }

    public void initContactList() {
        if (mLocalUsers == null) {
            ThreadPoolManager.run(new Runnable() {
                @Override
                public void run() {
                    mLocalUsers = getContactList();
                }
            });
        }
    }

    public ChatUser getChatUserInfo(String username) {
        if (username.equals(SharedPreferenceHelper.getEmchatId())) {
            return getCurrentUserInfo();
        }
        ChatUser user = getChatUserList().get(username);
        if (user == null) {
            user = new ChatUser(username);
        }

        return user;
    }

    public synchronized ChatUser getCurrentUserInfo() {
        if (mCurrentUser == null || !mCurrentUser.getUsername().equals(SharedPreferenceHelper.getEmchatId())) {
            String username = SharedPreferenceHelper.getEmchatId();
            mCurrentUser = new ChatUser(username);
            String nick = SharedPreferenceHelper.getUserName();
            mCurrentUser.setNickname(!TextUtils.isEmpty(nick) ? nick : username);
            mCurrentUser.setAvatar(SharedPreferenceHelper.getUserAvatar());
        }
        return mCurrentUser;
    }

    public synchronized void updateCurrentUserInfo(String nick, String avatarUrl) {
        SharedPreferenceHelper.setUserName(nick);
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
        if (mCurrentUser != null && mCurrentUser.getUsername().equals(SharedPreferenceHelper.getEmchatId())) {
            mCurrentUser.setNickname(nick);
            mCurrentUser.setAvatar(avatarUrl);
            mCurrentUser.setUserType(ChatConstant.TO_CHAT_USER_TYPE_TECH);
        }
    }

    public boolean userExisted(String username) {
        return getChatUserList().containsKey(username);
    }

    public void deleteChatUser(String username) {
        if (userExisted(username)) {
            ChatDBManager.getInstance().deleteContact(username);
        }
    }

    public Map<String, ChatUser> getChatUserList() {
        if (EMClient.getInstance().isLoggedInBefore() && mLocalUsers == null) {
            ThreadPoolManager.run(new Runnable() {
                @Override
                public void run() {
                    mLocalUsers = getContactList();
                }
            });
        }
        if (mLocalUsers == null) {
            mLocalUsers = new Hashtable<String, ChatUser>();
        }

        return mLocalUsers;
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    synchronized private void saveContact(ChatUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null)
            values.put(COLUMN_NAME_NICK, user.getNick());
        if (user.getAvatar() != null)
            values.put(COLUMN_NAME_AVATAR, user.getAvatar());
        if (user.getAvatar() != null) {
            values.put(COLUMN_NAME_TYPE, user.getAvatar());
        } else {
            values.put(COLUMN_NAME_TYPE, ChatConstant.TO_CHAT_USER_TYPE_CUSTOMER);
        }
        if (db.isOpen()) {
            db.replace(TABLE_NAME, null, values);
        }
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    public void saveContactInfo(ChatUser user) {
        ChatUser chatUser = getChatUserList().get(user.getUsername());
        if (chatUser == null || !chatUser.equals(user)) {
            getChatUserList().put(user.getUsername(), user);
            ThreadPoolManager.run(new Runnable() {
                @Override
                public void run() {
                    saveContact(user);
                }
            });
        } else {
            updateContactInfo(user);
        }
    }

    /**
     * 更新联系人信息
     *
     * @param user
     */
    public void updateContactInfo(ChatUser user) {
        ChatUser chatUser = getChatUserList().get(user.getUsername());
        if (chatUser == null || !chatUser.exactlyEquals(user)) {
            getChatUserList().put(user.getUsername(), user);
            ThreadPoolManager.run(new Runnable() {
                @Override
                public void run() {
                    saveContact(user);
                }
            });
        }
    }

    /**
     * 获取好友list
     *
     * @return
     */
    synchronized private Map<String, ChatUser> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, ChatUser> users = new Hashtable<String, ChatUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
                ChatUser user = new ChatUser(username);
                user.setNickname(nick);
                user.setAvatar(avatar);
                user.setUserType(type);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNickname())) {
                    headerName = user.getNickname();
                } else if (TextUtils.isEmpty(user.getUsername())) {
                    headerName = user.getUsername();
                } else {
                    headerName = "匿名用户";
                }

                if (Character.isDigit(headerName.charAt(0))) {
                    user.setInitialLetter("#");
                } else {
                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.trim().substring(0, 1))
                            .get(0).target.substring(0, 1).toUpperCase());
                    char header = user.getInitialLetter().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setInitialLetter("#");
                    }
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    synchronized public void reset() {
        mCurrentUser = null;

        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        userProvider = null;
    }
}
