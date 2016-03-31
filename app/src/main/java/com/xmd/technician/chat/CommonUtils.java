package com.xmd.technician.chat;

import android.content.Context;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

/**
 * Created by sdcm on 16-3-22.
 */
public class CommonUtils {
    /**
     * 将应用的会话类型转化为SDK的会话类型
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == ChatConstant.CHATTYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == ChatConstant.CHATTYPE_GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }

    public static String getMessageDigest(EMMessage lastMessage, Context mContext) {
        return "";
    }
}
