package com.xmd.chat.xmdchat.present;

import android.databinding.ObservableBoolean;

import com.tencent.imsdk.TIMMessage;
import com.xmd.chat.ChatSettingManager;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.xmdchat.contract.XmdChatRowViewModelInterface;

/**
 * Created by Lhj on 18-1-23.
 */

public class ImChatRowViewModelPresent implements XmdChatRowViewModelInterface {
    private ChatMessage<TIMMessage> mMessage;

    @Override
    public void init(ChatMessage message, ObservableBoolean progress, ObservableBoolean error, ObservableBoolean showTime) {
        this.mMessage = message;
        error.set(false);
        progress.set(false);
        switch (mMessage.getMessage().status()){
            case HasRevoked:
                progress.set(false);
                error.set(false);
                return;
                //break;
            case SendSucc:
                progress.set(false);
                error.set(false);
                if(message.getTag() != null && ChatMessage.MSG_TAG_HELLO.equals(message.getTag())){
                    return;
                }

                break;
            case Sending:
                progress.set(true);
                error.set(false);
                break;
            case SendFail:
                progress.set(false);
                error.set(true);
                if(ChatSettingManager.getInstance().isInCustomerBlackList(message.getToChatId())){
                    error.set(false);
                }
                break;

        }
    }

    @Override
    public long getTime() {
        return mMessage.getMessage().timestamp()*1000;
    }
}
