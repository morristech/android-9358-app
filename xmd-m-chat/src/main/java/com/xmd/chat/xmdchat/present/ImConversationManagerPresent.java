package com.xmd.chat.xmdchat.present;

import android.text.TextUtils;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.Pageable;
import com.shidou.commonlibrary.helper.XLogger;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
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
import java.util.List;

/**
 * Created by Lhj on 18-1-24.
 */

public class ImConversationManagerPresent implements XmdConversationManagerInterface<TIMMessage> {

    private static ImConversationManagerPresent mPresent;

    private ImConversationManagerPresent() {

    }

    public static ImConversationManagerPresent getInstance() {
        if (mPresent == null) {
            synchronized (EmConversationManagerPresent.class) {
                if (mPresent == null) {
                    mPresent = new ImConversationManagerPresent();
                }
            }
        }
        return mPresent;
    }

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private List<ConversationViewModel> mConversationList; //会话列表，按时间倒序排列
    private int loadCount;
    private List<UserConversion> mUserConversions = new ArrayList<>();

    @Override
    public void init() {
        mConversationList = new ArrayList<>();
    }

    @Override
    public void loadConversationList(final boolean forceLoadUserInfo, final Callback<Pageable<ConversationViewModel>> callback, int page, int pageSize) {
        if (page == 0) {
            List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
            if (list.size() == 0) {
                callback.onResponse(new Pageable<ConversationViewModel>(), null);
                return;
            }
            mUserConversions.clear();
            for (TIMConversation conversation : list) {
                mUserConversions.add(new UserConversion(conversation.getPeer(), conversation));
            }
            sortConversationByTime(mUserConversions);
        }
        int maxIndex = (page + 1) * pageSize;
        loadCount = maxIndex <= mUserConversions.size() ? pageSize : mUserConversions.size() - (maxIndex - pageSize);
        int loadIndex = 0;
        final Pageable<ConversationViewModel> pageable = new Pageable<>();
        pageable.setPage(page);
        pageable.setPageSize(pageSize);
        pageable.setTotalSize(mUserConversions.size());
        pageable.setTotalPage((pageable.getTotalSize() + pageSize - 1) / pageSize);
        pageable.setData(new ArrayList<ConversationViewModel>(loadCount));
        for (final UserConversion conversion : mUserConversions) {
            if (loadIndex < page * pageSize) {
                loadIndex++;
                continue;
            }
            if (loadIndex > (page + 1) * pageSize) {
                break;
            }
            loadIndex++;
            final TIMConversation conversation = conversion.getConversation();
            if (!TextUtils.isEmpty(conversion.getChatId()) && conversation != null) {
                final User user = userInfoService.getUserByChatId(conversion.getChatId());
                if (forceLoadUserInfo || user == null || user.getContactPermission() == null || !user.getContactPermission().isEchat()) {
                    XLogger.d("load user " + conversion.getChatId() + " from server");
                    userInfoService.loadUserInfoByChatIdFromServer(conversion.getChatId(), new Callback<User>() {
                        @Override
                        public void onResponse(User result, Throwable error) {
                            if (error != null) {
                                XLogger.e(XmdChat.TAG, " not found user by chatId:" + conversion.getChatId());
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
                    XLogger.d("load user " + conversion.getChatId() + " from cache");
                    onLoadFinish(false, user, conversation, callback, pageable);
                }
            }
        }
    }

    private void onLoadFinish(boolean error, User user, TIMConversation conversation,
                              Callback<Pageable<ConversationViewModel>> callback,
                              Pageable<ConversationViewModel> pageable) {
        if (!error) {
            //判断用户是否跟技师有聊天权限，无聊天权限则会在在列表被删除
            if (user.getContactPermission() == null || !user.getContactPermission().isEchat()) {
                TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, user.getChatId());
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
        TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, chatId);
        int position = getConversationDataPosition(chatId);
        if (position == -1) {
            return;
        }
        ConversationViewModel<TIMMessage> data = mConversationList.get(position);
        EventBus.getDefault().post(new EventDeleteConversation(position, data));
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

    @Override
    public void markAllMessagesRead(String chatId) {
        ConversationViewModel<TIMMessage> conversationViewModel = getConversationData(chatId);
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, chatId);
        TIMConversationExt timConversationExt = new TIMConversationExt(conversation);
        timConversationExt.setReadMessage(null, null);
        EventBus.getDefault().post(new EventUnreadCount(conversationViewModel));
        //发送通知更新未读消息数
        EventBus.getDefault().post(new EventTotalUnreadCount(ImChatMessageManagerPresent.getInstance().getUnReadMessageTotal()));
    }

    @Override
    public TIMMessage getLastMessage() {
        return null;
    }

    private void sortConversationByTime(List<UserConversion> dataList) {
        Collections.sort(dataList, new Comparator<UserConversion>() {
            @Override
            public int compare(UserConversion o1, UserConversion o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                TIMMessage last1 = new TIMConversationExt(o1.getConversation()).getLastMsgs(1).get(0);
                TIMMessage last2 = new TIMConversationExt(o2.getConversation()).getLastMsgs(1).get(0);
                if (last1 == null) {
                    return -1;
                }
                if (last2 == null) {
                    return 1;
                }
                if (last1.timestamp() > last2.timestamp()) {
                    return -1;
                }
                if (last2.timestamp() > last1.timestamp()) {
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

    public static class UserConversion {
        private String chatId;
        private TIMConversation conversation;

        public UserConversion(String chatId, TIMConversation conversation) {
            this.chatId = chatId;
            this.conversation = conversation;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public TIMConversation getConversation() {
            return conversation;
        }

        public void setConversation(TIMConversation conversation) {
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
            if (!(obj instanceof ImConversationManagerPresent.UserConversion)) {
                return false;
            }
            ImConversationManagerPresent.UserConversion o = (ImConversationManagerPresent.UserConversion) obj;
            return chatId.equals(o.chatId) && conversation.equals(o.getConversation());
        }
    }
}
