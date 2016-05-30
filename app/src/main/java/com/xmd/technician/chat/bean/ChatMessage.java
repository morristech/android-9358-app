package com.xmd.technician.chat.bean;

import android.annotation.SuppressLint;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.message.EMAMessage;


/**
 * Created by sdcm on 16-5-27.
 */
@SuppressLint("ParcelCreator")
public class ChatMessage extends EMMessage {

    EMConversation mEmConversation;
    private String mAvatarUrl;
    private String mNickName;


    public ChatMessage(EMAMessage emaMessage) {
        super(emaMessage);
    }
}
