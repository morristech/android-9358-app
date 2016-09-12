package com.xmd.technician.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.HanziToPinyin;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.DbOpenHelper;
import com.xmd.technician.common.ThreadManager;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by sdcm on 16-4-12.
 */
public class UserProfileProvider {

    public static final String TABLE_NAME = "chat_users";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    
    private DbOpenHelper dbHelper;
    private static UserProfileProvider userProvider;
    private Map<String, ChatUser> mLocalUsers ;
    private ChatUser mCurrentUser;

    public static UserProfileProvider getInstance(){
        if(userProvider == null){
            synchronized (UserProfileProvider.class){
                if(userProvider == null){
                    userProvider = new UserProfileProvider();
                }
            }
        }
        return userProvider;
    }

    private UserProfileProvider(){
        dbHelper = DbOpenHelper.getInstance(TechApplication.getAppContext());
    }

    public void initContactList(){
        if(mLocalUsers == null){
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                @Override
                public void run() {
                    mLocalUsers = getContactList();
                }
            });
        }
    }

    public ChatUser getChatUserInfo(String username){
        if(username.equals(SharedPreferenceHelper.getEmchatId())){
            return getCurrentUserInfo();
        }
        ChatUser user = getChatUserList().get(username);

            return user;
    }

    public synchronized ChatUser getCurrentUserInfo() {
        if (mCurrentUser == null || !mCurrentUser.getUsername().equals(SharedPreferenceHelper.getEmchatId())) {
            String username = SharedPreferenceHelper.getEmchatId();
            mCurrentUser = new ChatUser(username);
            String nick = SharedPreferenceHelper.getUserName();
            mCurrentUser.setNick((nick != null) ? nick : username);
            mCurrentUser.setAvatar(SharedPreferenceHelper.getUserAvatar());
        }
        return mCurrentUser;
    }

    public synchronized void updateCurrentUserInfo(String nick, String avatarUrl) {

        SharedPreferenceHelper.setUserName(nick);
        SharedPreferenceHelper.setUserAvatar(avatarUrl);
        if (mCurrentUser != null && mCurrentUser.getUsername().equals(SharedPreferenceHelper.getEmchatId())) {
            mCurrentUser.setNick(nick);
            mCurrentUser.setAvatar(avatarUrl);
        }
    }

    public boolean userExisted(String username){
        return getChatUserList().containsKey(username);
    }

    public Map<String, ChatUser> getChatUserList() {
        if (EMClient.getInstance().isLoggedInBefore() && mLocalUsers == null) {
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                @Override
                public void run() {
                    mLocalUsers = getContactList();
                }
            });
        }

        // return a empty non-null object to avoid app crash
        if(mLocalUsers == null){
            mLocalUsers = new Hashtable<String, ChatUser>();
        }

        return mLocalUsers;
    }

    /**
     * 保存一个联系人
     * @param user
     */
    synchronized private void saveContact(ChatUser user){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, user.getUsername());
        if(user.getNick() != null)
            values.put(COLUMN_NAME_NICK, user.getNick());
        if(user.getAvatar() != null)
            values.put(COLUMN_NAME_AVATAR, user.getAvatar());
        if(db.isOpen()){
            db.replace(TABLE_NAME, null, values);
        }
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContactInfo(ChatUser user){
        ChatUser chatUser = getChatUserList().get(user.getUsername());
        if(chatUser == null || !chatUser.equals(user)){
            getChatUserList().put(user.getUsername(), user);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                @Override
                public void run() {
                    saveContact(user);
                }
            });
        }else{
            updateContactInfo(user);
        }
    }

    /**
     * 更新联系人信息
     * @param user
     */
    public void updateContactInfo(ChatUser user){
        ChatUser chatUser = getChatUserList().get(user.getUsername());
        if(chatUser == null || !chatUser.exactlyEquals(user)){
            getChatUserList().put(user.getUsername(), user);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
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
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
                ChatUser user = new ChatUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }

                /*if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM)|| username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else*/ if (Character.isDigit(headerName.charAt(0))) {
                    user.setInitialLetter("#");
                } else {
                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
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

    synchronized public void reset(){
        mCurrentUser = null;

        if(dbHelper != null){
            dbHelper.closeDB();
        }
        userProvider = null;
    }
}
