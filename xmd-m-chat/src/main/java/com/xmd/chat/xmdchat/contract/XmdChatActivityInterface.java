package com.xmd.chat.xmdchat.contract;

import com.xmd.app.user.User;
import com.xmd.chat.viewmodel.ChatRowViewModel;

import java.util.List;

/**
 * Created by Lhj on 18-1-24.
 */

public interface XmdChatActivityInterface {

    boolean conversationIsEmpty(User mRemoteUser);

    boolean messageIsEmpty(User mRemoteUser);

    List<ChatRowViewModel> getNewDataList(User mRemoteUser,String messageId,int PageSize,List<ChatRowViewModel> mDataList);


//    void onRevokeMessage(EventRevokeMessage event);
}
