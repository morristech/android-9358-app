package com.xmd.chat.xmdchat.contract;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.Pageable;
import com.xmd.chat.viewmodel.ConversationViewModel;

import java.util.List;

/**
 * Created by Lhj on 18-1-24.
 */

public interface XmdConversationManagerInterface<T> {

    void init();

    void loadConversationList(final boolean forceLoadUserInfo, final Callback<Pageable<ConversationViewModel>> callback, int page, int pageSize);

    ConversationViewModel getConversationData(String chatId);

    List<ConversationViewModel> listConversationData();

    List<ConversationViewModel> listConversationData(String key);

    void deleteConversation(String chatId);

    void markAllMessagesRead(String chatId);

    T getLastMessage();


}
