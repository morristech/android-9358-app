package com.xmd.chat.xmdchat.present;


import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.Pageable;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.XmdChat;
import com.xmd.chat.event.EventDeleteConversation;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.event.EventUnreadCount;
import com.xmd.chat.viewmodel.ConversationViewModel;
import com.xmd.chat.xmdchat.contract.XmdConversationManagerInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lhj on 18-1-24.
 */

public class EmConversationManagerPresent implements XmdConversationManagerInterface<EMMessage> {

    private static EmConversationManagerPresent mPresent;

    private EmConversationManagerPresent() {

    }

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private List<ConversationViewModel> mConversationList; //会话列表，按时间倒序排列
    private int loadCount;
    private List<MyConversion> conversationList = new ArrayList<>();

    public static EmConversationManagerPresent getInstance() {
        if (mPresent == null) {
            synchronized (EmConversationManagerPresent.class) {
                if (mPresent == null) {
                    mPresent = new EmConversationManagerPresent();
                }
            }
        }
        return mPresent;
    }

    @Override
    public void init() {
        mConversationList = new ArrayList<>();
    }

    @Override
    public void loadConversationList(final boolean forceLoadUserInfo, final Callback<Pageable<ConversationViewModel>> callback, int page, int pageSize) {
        if (page == 0) {
            Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
            XLogger.d("total conversation count: " + conversationMap.size());
            if (conversationMap.size() == 0) {
                callback.onResponse(new Pageable<ConversationViewModel>(), null);
                return;
            }
            conversationList.clear();
            for (Map.Entry<String, EMConversation> entry : conversationMap.entrySet()) {
                conversationList.add(new MyConversion(entry.getKey(), entry.getValue()));
            }
            sortConversationByTime(conversationList);
        }
        int maxIndex = (page + 1) * pageSize;
        loadCount = maxIndex <= conversationList.size() ? pageSize : conversationList.size() - (maxIndex - pageSize);
        int loadIndex = 0;
        final Pageable<ConversationViewModel> pageable = new Pageable<>();
        pageable.setPage(page);
        pageable.setPageSize(pageSize);
        pageable.setTotalSize(conversationList.size());
        pageable.setTotalPage((pageable.getTotalSize() + pageSize - 1) / pageSize);
        pageable.setData(new ArrayList<ConversationViewModel>(loadCount));
        for (final MyConversion myConversion : conversationList) {
            if (loadIndex < page * pageSize) {
                loadIndex++;
                continue;
            }
            if (loadIndex >= (page + 1) * pageSize) {
                break;
            }
            loadIndex++;
            final EMConversation conversation = myConversion.getConversation();
            if (!TextUtils.isEmpty(myConversion.getChatId()) && conversation != null) {
                final User user = userInfoService.getUserByChatId(myConversion.getChatId());
                if (forceLoadUserInfo || user == null || user.getContactPermission() == null || !user.getContactPermission().isEchat()) {
                    XLogger.d("load user " + myConversion.getChatId() + " from server");
                    userInfoService.loadUserInfoByChatIdFromServer(myConversion.getChatId(), new Callback<User>() {
                        @Override
                        public void onResponse(User result, Throwable error) {
                            if (error != null) {
                                XLogger.e(XmdChat.TAG, " not found user by chatId:" + myConversion.getChatId());
                                if (forceLoadUserInfo && user != null && user.getContactPermission() != null) {
                                    XLogger.i("load user info from server failed, use cache instead!");
                                    error = null;
                                    result = user;
                                }
                            }

                            onLoadFinish(error != null, result, conversation, callback, pageable);
                        }
                    });
                } else {
                    XLogger.d("load user " + myConversion.getChatId() + " from cache");
                    onLoadFinish(false, user, conversation, callback, pageable);
                }
            }
        }
    }

    @Override
    public ConversationViewModel getConversationData(String chatId) {
        for (ConversationViewModel data : mConversationList) {
            if (data.getChatId().equals(chatId)) {
                return data;
            }
        }
        return null;
    }

    @Override
    public List<ConversationViewModel> listConversationData() {
        return mConversationList;
    }

    @Override
    public List<ConversationViewModel> listConversationData(String key) {
        if (TextUtils.isEmpty(key)) {
            return mConversationList;
        }
        List<ConversationViewModel> list = new ArrayList<>();
        for (ConversationViewModel data : mConversationList) {
            if (data.getName().contains(key)) {
                list.add(data);
            }
        }
        return list;
    }

    @Override
    public void deleteConversation(String chatId) {
        ConversationManager.getInstance().markAllMessagesRead(chatId);
        int position = getConversationDataPosition(chatId);
        if (position == -1) {
            return;
        }
        ConversationViewModel data = mConversationList.get(position);
        EventBus.getDefault().post(new EventDeleteConversation(position, data));
        deleteConversationInner(chatId);
    }

    @Override
    public void markAllMessagesRead(String chatId) {
        ConversationViewModel conversationViewModel = getConversationData(chatId);
        if (conversationViewModel != null) {
            ((EMConversation)conversationViewModel.getConversation()).markAllMessagesAsRead();
        }
        EventBus.getDefault().post(new EventUnreadCount(conversationViewModel));
        EventBus.getDefault().post(new EventTotalUnreadCount(EMClient.getInstance().chatManager().getUnreadMessageCount()));
    }

    @Override
    public EMMessage getLastMessage() {
        return null;
    }

    private void onLoadFinish(boolean error, User user, EMConversation conversation,
                              Callback<Pageable<ConversationViewModel>> callback,
                              Pageable<ConversationViewModel> pageable) {
        if (!error) {
            if (!user.getContactPermission().isEchat()) {
                EMClient.getInstance().chatManager().getAllConversations().remove(user.getChatId());
                EMClient.getInstance().chatManager().deleteConversation(user.getChatId(), true);
                XLogger.i(XmdChat.TAG, "delete conversation:" + user.getChatId());
            } else {
                ConversationViewModel data = new ConversationViewModel(user, conversation);
                pageable.getData().add(data);
            }
        }
        loadCount--;
        if (loadCount == 0) {
            if (pageable.getPage() == 0) {
                mConversationList.clear();
            }
            sortConversationViewModelByTime(pageable.getData());
            mConversationList.addAll(pageable.getData());
            callback.onResponse(pageable, null);
        }
    }

    private int getConversationDataPosition(String chatId) {
        for (int i = 0; i < mConversationList.size(); i++) {
            ConversationViewModel data = mConversationList.get(i);
            if (data.getChatId().equals(chatId)) {
                return i;
            }
        }
        return -1;
    }

    private void deleteConversationInner(String chatId) {
        removeConversationData(chatId);
        EMClient.getInstance().chatManager().getAllConversations().remove(chatId);
        EMClient.getInstance().chatManager().deleteConversation(chatId, true);
    }

    private void removeConversationData(String chatId) {
        Iterator<ConversationViewModel> dataIterator = mConversationList.iterator();
        while (dataIterator.hasNext()) {
            ConversationViewModel data = dataIterator.next();
            if (data.getChatId().equals(chatId)) {
                dataIterator.remove();
            }
        }
    }

    private void sortConversationByTime(List<MyConversion> dataList) {
        Collections.sort(dataList, new Comparator<MyConversion>() {
            @Override
            public int compare(MyConversion o1, MyConversion o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                EMMessage last1 = o1.getConversation().getLastMessage();
                EMMessage last2 = o2.getConversation().getLastMessage();
                if (last1 == null) {
                    return -1;
                }
                if (last2 == null) {
                    return 1;
                }
                if (last1.getMsgTime() > last2.getMsgTime()) {
                    return -1;
                }
                if (last2.getMsgTime() > last1.getMsgTime()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private void sortConversationViewModelByTime(List<ConversationViewModel> dataList) {
        Collections.sort(dataList, new Comparator<ConversationViewModel>() {
            @Override
            public int compare(ConversationViewModel o1, ConversationViewModel o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                if (o1.getTime() > o2.getTime()) {
                    return -1;
                }
                if (o2.getTime() > o1.getTime()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static class MyConversion {
        private String chatId;
        private EMConversation conversation;

        public MyConversion(String chatId, EMConversation conversation) {
            this.chatId = chatId;
            this.conversation = conversation;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public EMConversation getConversation() {
            return conversation;
        }

        public void setConversation(EMConversation conversation) {
            this.conversation = conversation;
        }

        @Override
        public int hashCode() {
            return chatId.hashCode() + conversation.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MyConversion)) {
                return false;
            }
            MyConversion o = (MyConversion) obj;
            return chatId.equals(o.chatId) && conversation.equals(o.getConversation());
        }
    }
}
