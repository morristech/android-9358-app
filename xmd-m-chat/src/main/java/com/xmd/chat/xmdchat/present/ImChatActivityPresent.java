package com.xmd.chat.xmdchat.present;

import com.shidou.commonlibrary.helper.XLogger;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.xmd.app.user.User;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.xmdchat.contract.XmdChatActivityInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 18-1-25.
 */

public class ImChatActivityPresent implements XmdChatActivityInterface {

    private final int LAST_MESSAGE_NUM = 20;

    @Override
    public boolean conversationIsEmpty(User mRemoteUser) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mRemoteUser.getChatId());
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        return timConversationExt.getLastMsgs(10).size() == 0;
    }

    @Override
    public boolean messageIsEmpty(User mRemoteUser) {
        return false;
    }

    @Override
    public List<ChatRowViewModel> getNewDataList(User mRemoteUser, String messageId, int PageSize, final List<ChatRowViewModel> mDataList){
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mRemoteUser.getChatId());
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);

        timConversationExt.getMessage(LAST_MESSAGE_NUM, null, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {
                XLogger.i(">>>", "获取会话列表失败");
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {
              for(TIMMessage message : timMessages){
                  ChatMessage<TIMMessage> chatMessage = ChatMessageFactory.createMessage(message);
                  ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
                 mDataList.add(data);
              }
                XLogger.i(">>>","getSuccess");
            }
        });
        return new ArrayList<ChatRowViewModel>();
    }

}
