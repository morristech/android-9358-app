package com.xmd.chat;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.Pageable;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.viewmodel.ConversationViewModel;
import com.xmd.chat.xmdchat.contract.XmdConversationManagerInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmConversationManagerPresent;
import com.xmd.chat.xmdchat.present.ImConversationManagerPresent;

import java.util.List;

/**
 * Created by mo on 17-6-30.
 * 会话管理
 */

public class ConversationManager {

    private static final ConversationManager ourInstance = new ConversationManager();

    public static ConversationManager getInstance() {
        return ourInstance;
    }

    private XmdConversationManagerInterface mInterface;

    private ConversationManager() {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            mInterface = EmConversationManagerPresent.getInstance();
        } else {
            mInterface = ImConversationManagerPresent.getInstance();
        }
    }

    public void init() {
        mInterface.init();
    }

    //加载会话列表
    public void loadConversationList(final boolean forceLoadUserInfo, final Callback<Pageable<ConversationViewModel>> callback, int page, int pageSize) {
        try {
            mInterface.loadConversationList(forceLoadUserInfo, callback, page, pageSize);
        } catch (Exception e) {
            XLogger.e("loadConversationList error:", "", e);
            callback.onResponse(new Pageable<ConversationViewModel>(), e);
        }
    }

    //获取会话数据
    public ConversationViewModel getConversationData(String chatId) {
        return mInterface.getConversationData(chatId);
    }

    //获取会话列表
    public List<ConversationViewModel> listConversationData() {
        return mInterface.listConversationData();
    }

    //获取会话列表，返回新的列表
    public List<ConversationViewModel> listConversationData(String key) {
        return mInterface.listConversationData(key);
    }

    //删除会话
    public void deleteConversation(String chatId) {
        mInterface.deleteConversation(chatId);
    }

    //消息标记为已读
    public void markAllMessagesRead(String chatId) {
        mInterface.markAllMessagesRead(chatId);
    }

}
