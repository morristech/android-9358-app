package com.xmd.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.hyphenate.util.HanziToPinyin;
import com.xmd.manager.chat.EmchatUser;
import com.xmd.manager.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class EmchatUserDao extends Dao {

    private static final String TABLE_NAME = DatabaseTableColumns.CHAT_USER_TABLE_NAME;
    private static final String ID = DatabaseTableColumns.CHAT_USER_COLUMN_NAME_ID;
    private static final String NICK = DatabaseTableColumns.CHAT_USER_COLUMN_NAME_NICK;
    private static final String AVATAR = DatabaseTableColumns.CHAT_USER_COLUMN_NAME_AVATAR;

    private static final String SQL_QUERY = "select * from " + TABLE_NAME + " order by " + ID;
    private static final String SQL_QUERY_BY_ID = "select * from " + TABLE_NAME + " where " + ID + " = ? ";

    public EmchatUserDao(Context context) {
        super(context);
    }

    public void saveOrUpdate(EmchatUser emchatUser) {
        EmchatUser user = getById(emchatUser.getUsername());
        if (user != null) {
            update(emchatUser.getUsername(),
                    Utils.isEmpty(emchatUser.getNick()) ? user.getNick() : emchatUser.getNick(),
                    Utils.isEmpty(emchatUser.getAvatar()) ? user.getAvatar() : emchatUser.getAvatar());
        } else {
            insert(emchatUser.getUsername(), emchatUser.getNick(), emchatUser.getAvatar());
        }
    }

    private void insert(String emchatId, String emchatNick, String emchatAvatar) {
        ContentValues cv = new ContentValues();
        cv.put(ID, emchatId);
        cv.put(NICK, emchatNick);
        cv.put(AVATAR, emchatAvatar);
        insert(TABLE_NAME, cv);
    }

    public void delete(String emchatId) {
        delete(TABLE_NAME, ID + " = ? ", new String[]{emchatId});
    }

    public void delete(String[] emchatIds) {
        StringBuilder sb = new StringBuilder(ID);
        sb.append(" in (");
        for (int i = 0; i < emchatIds.length; i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" )");
        delete(TABLE_NAME, sb.toString(), emchatIds);
    }


    private void update(String emchatId, String emchatNick, String emchatAvatar) {
        ContentValues cv = new ContentValues();
        cv.put(NICK, emchatNick);
        cv.put(AVATAR, emchatAvatar);
        update(TABLE_NAME, cv, ID + " = ? ", new String[]{emchatId});
    }

    public EmchatUser getById(String id) {
        Cursor cursor = query(SQL_QUERY_BY_ID, new String[]{id});
        EmchatUser emchatUser = getDetailFromCursor(cursor);
        cursor.close();
        return emchatUser;
    }

    /**
     * 返回所有书签和目录
     *
     * @return
     */
    public List<EmchatUser> getAll() {
        Cursor cursor = query(SQL_QUERY);
        List<EmchatUser> list = getDetailsFromCursor(cursor);
        cursor.close();
        return list;
    }

    private EmchatUser getDetailFromCursor(Cursor cursor) {
        EmchatUser emchatUser = null;
        if (cursor == null) {

        } else if (!cursor.moveToFirst()) {

        } else {
            int columnId = cursor.getColumnIndex(ID);
            int columnNick = cursor.getColumnIndex(NICK);
            int columnAvatar = cursor.getColumnIndex(AVATAR);
            do {
                String emchatId = cursor.getString(columnId);
                String nick = cursor.getString(columnNick);
                String avatar = cursor.getString(columnAvatar);
                emchatUser = new EmchatUser(emchatId, nick, avatar);
                break;

            } while (cursor.moveToNext());
        }
        return emchatUser;
    }

    private List<EmchatUser> getDetailsFromCursor(Cursor cursor) {
        List<EmchatUser> list = new ArrayList<>();
        if (cursor == null) {

        } else if (!cursor.moveToFirst()) {

        } else {
            int columnId = cursor.getColumnIndex(ID);
            int columnNick = cursor.getColumnIndex(NICK);
            int columnAvatar = cursor.getColumnIndex(AVATAR);

            do {
                String emchatId = cursor.getString(columnId);
                String nick = cursor.getString(columnNick);
                String avatar = cursor.getString(columnAvatar);
                EmchatUser user = new EmchatUser(emchatId, nick, avatar);
                String headerName = null;
                if (Utils.isNotEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }
                if (Character.isDigit(headerName.charAt(0))) {
                    user.setInitialLetter("#");
                } else {
                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
                            .get(0).target.substring(0, 1).toUpperCase());
                    char header = user.getInitialLetter().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setInitialLetter("#");
                    }
                }
                list.add(new EmchatUser(emchatId, nick, avatar));
            } while (cursor.moveToNext());
        }
        return list;
    }
}
