package com.xmd.technician.chat.db;

import android.content.ContentValues;
import android.content.Context;

import com.xmd.technician.chat.domain.InviteMessage;

import java.util.List;

/**
 * Created by Lhjon 17-4-6.
 */
//好友邀请
public class InviteMessageDao {
    static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_FROM = "username";
    static final String COLUMN_NAME_GROUP_ID = "groupid";
    static final String COLUMN_NAME_GROUP_Name = "groupname";

    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";


    public InviteMessageDao(Context context){
    }

    /**
     * save message
     * @param message
     * @return  return cursor of the message
     */
    public Integer saveMessage(InviteMessage message){
        return ChatDBManager.getInstance().saveMessage(message);
    }

    /**
     * update message
     * @param msgId
     * @param values
     */
    public void updateMessage(int msgId,ContentValues values){
        ChatDBManager.getInstance().updateMessage(msgId, values);
    }

    /**
     * get messges
     * @return
     */
    public List<InviteMessage> getMessagesList(){
        return ChatDBManager.getInstance().getMessagesList();
    }

    public void deleteMessage(String from){
        ChatDBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount(){
        return ChatDBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count){
        ChatDBManager.getInstance().setUnreadNotifyCount(count);
    }
}

