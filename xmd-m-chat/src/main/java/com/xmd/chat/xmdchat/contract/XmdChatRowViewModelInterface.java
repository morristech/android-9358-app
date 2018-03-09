package com.xmd.chat.xmdchat.contract;

import android.databinding.ObservableBoolean;

import com.xmd.chat.message.ChatMessage;

/**
 * Created by Lhj on 18-1-23.
 */

public interface XmdChatRowViewModelInterface {
    void init(ChatMessage message, ObservableBoolean progress,ObservableBoolean error,ObservableBoolean showTime);

    long getTime();
}
