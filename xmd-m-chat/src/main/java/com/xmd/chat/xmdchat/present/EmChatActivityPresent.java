package com.xmd.chat.xmdchat.present;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.xmdchat.contract.XmdChatActivityInterface;

import java.util.ArrayList;
import java.util.List;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

/**
 * Created by Lhj on 18-1-24.
 */

public class EmChatActivityPresent implements XmdChatActivityInterface {

    @Override
    public boolean conversationIsEmpty(User mRemoteUser) {
      EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mRemoteUser.getChatId());

        if(conversation == null || conversation.getAllMsgCount() == 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean messageIsEmpty(User mRemoteUser) {
        return false;
    }

    @Override
    public List<ChatRowViewModel> getNewDataList(User mRemoteUser, String msgId, int PageSize, List<ChatRowViewModel> mDataList) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mRemoteUser.getChatId());
        List<ChatRowViewModel> newDataList = new ArrayList<>();
        List<EMMessage> messageList = conversation.loadMoreMsgFromDB(msgId, PAGE_SIZE);
        if (msgId != null && messageList.size() == 0) {
            XToast.show("没有更多消息了");
            return newDataList;
        }
        ChatRowViewModel beforeData = null;
        for (EMMessage message : messageList) {
            ChatMessage chatMessage = ChatMessageFactory.createMessage(message);
            ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
            setShowTime(beforeData, data);
            beforeData = data;
            newDataList.add(data);
        }
        if (mDataList.size() > 0 && newDataList.size() > 0) {
            beforeData = newDataList.get(newDataList.size() - 1);
            ChatRowViewModel current = mDataList.get(0);
            setShowTime(beforeData, current);
        }
        return newDataList;
    }




    /**
     * 设置是否需要显示时间
     *
     * @param before  前一个气泡
     * @param current 当前气泡
     */
    private void setShowTime(ChatRowViewModel before, ChatRowViewModel current) {
        if (before == null) {
            current.showTime.set(true);
            return;
        }
        current.showTime.set(current.getTime() - before.getTime() > ChatConstants.TIME_SHOW_INTERVAL);
    }

}
