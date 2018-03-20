package com.xmd.chat.message;

import com.tencent.imsdk.TIMMessage;
import com.xmd.app.user.User;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.messagebean.GroupImageBean;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;


public class GroupImageMessage<T> extends ChatMessage {

    private static final String ATTR_URL = "url";

    public GroupImageMessage(T message) {
        super(message);
    }

    public static GroupImageMessage create(User remoteUser, String url) {
        GroupImageBean bean = new GroupImageBean();
        TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean, XmdMessageType.GROUP_IMAGE_TYPE, null, null);
        GroupImageMessage imageMessage = new GroupImageMessage(message);
        return imageMessage;
    }


    public String getImageUrl() {
        return getSafeStringAttribute(ATTR_URL);
    }
}
