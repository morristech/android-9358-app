package com.xmd.chat;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.event.EventDeleteConversation;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.event.EventUnreadCount;
import com.xmd.chat.viewmodel.ConversationViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mo on 17-6-30.
 * 会话管理
 */

public class ConversationManager {
    private static final ConversationManager ourInstance = new ConversationManager();

    public static ConversationManager getInstance() {
        return ourInstance;
    }

    private ConversationManager() {

    }

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private List<ConversationViewModel> mConversationList; //会话列表，按时间倒序排列
    private int loadCount;

    public void init() {
        mConversationList = new ArrayList<>();
    }

    //加载会话列表
    public void loadConversationList(final boolean forceLoadUserInfo, final Callback<List<ConversationViewModel>> callback) {
        mConversationList.clear();
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        if (conversationMap.size() == 0) {
            callback.onResponse(new ArrayList<ConversationViewModel>(), null);
        }
        loadCount = conversationMap.size();
        for (final String key : conversationMap.keySet()) {
            final EMConversation conversation = conversationMap.get(key);
            if (!TextUtils.isEmpty(key) && conversation != null) {
                final User user = userInfoService.getUserByChatId(key);
                if (forceLoadUserInfo || user == null || user.getContactPermission() == null || !user.getContactPermission().isEchat()) {
                    XLogger.d("load user " + key + " from server");
                    userInfoService.loadUserInfoByChatIdFromServer(key, new Callback<User>() {
                        @Override
                        public void onResponse(User result, Throwable error) {
                            if (error != null) {
                                XLogger.e(XmdChat.TAG, " not found user by chatId:" + key);
                            }
                            if (forceLoadUserInfo && user != null && user.getContactPermission() != null) {
                                XLogger.i("load user info from server failed,use cache instead!");
                                error = null;
                            }
                            onLoadFinish(error != null, user, conversation, callback);
                        }
                    });
                } else {
                    XLogger.d("load user " + key + " from cache");
                    onLoadFinish(false, user, conversation, callback);
                }
            }
        }
    }

    private void onLoadFinish(boolean error, User user, EMConversation conversation, Callback<List<ConversationViewModel>> callback) {
        if (!error) {
            if (!user.getContactPermission().isEchat()) {
                EMClient.getInstance().chatManager().getAllConversations().remove(user.getChatId());
                EMClient.getInstance().chatManager().deleteConversation(user.getChatId(), true);
                XLogger.i(XmdChat.TAG, "delete conversation:" + user.getChatId());
            } else {
                ConversationViewModel data = new ConversationViewModel(user, conversation);
                mConversationList.add(data);
            }
        }
        loadCount--;
        if (loadCount == 0) {
            sortConversationByTime(mConversationList);
            callback.onResponse(mConversationList, null);
        }
    }

    //获取会话数据
    public ConversationViewModel getConversationData(String chatId) {
        for (ConversationViewModel data : mConversationList) {
            if (data.getChatId().equals(chatId)) {
                return data;
            }
        }
        return null;
    }

    //获取会话列表
    public List<ConversationViewModel> listConversationData() {
        return mConversationList;
    }

    //获取会话列表，返回新的列表
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

    //删除会话
    public void deleteConversation(String chatId) {
        int position = getConversationDataPosition(chatId);
        ConversationViewModel data = mConversationList.get(position);
        EventBus.getDefault().post(new EventDeleteConversation(position, data));
        deleteConversationInner(chatId);
    }


    public void markAllMessagesRead(String chatId) {
        ConversationViewModel conversationViewModel = getConversationData(chatId);
        if (conversationViewModel != null) {
            conversationViewModel.getConversation().markAllMessagesAsRead();
        }
        EventBus.getDefault().post(new EventUnreadCount(conversationViewModel));
        EventBus.getDefault().post(new EventTotalUnreadCount(EMClient.getInstance().chatManager().getUnreadMessageCount()));
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

    private void sortConversationByTime(List<ConversationViewModel> dataList) {
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
                    return 1;
                }
                if (o2.getTime() > o1.getTime()) {
                    return -1;
                }
                return 0;
            }
        });
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

    private void deleteConversationInner(String chatId) {
        removeConversationData(chatId);
        EMClient.getInstance().chatManager().getAllConversations().remove(chatId);
        EMClient.getInstance().chatManager().deleteConversation(chatId, true);
        XLogger.i(XmdChat.TAG, "delete conversation:" + chatId);
    }


    private void deleteConversations(List<String> chatIdList) {
        for (String key : chatIdList) {
            deleteConversationInner(key);
        }
    }

    //从chatId获取会话
    public EMConversation getConversation(String chatId) {
        return EMClient.getInstance().chatManager().getConversation(chatId);
    }

    /***************************会话过滤，不通过的会话会被删除**********************/
    //设置过滤器
//    public void setFilter(ConversationFilter filter) {
//        this.filter = filter;
//    }
//    private ConversationFilter filter;
//
//    public interface ConversationFilter {
//        void filter(ConversationViewModel data, Callback<Boolean> listener);
//    }
//
//    public ConversationFilter getFilter() {
//        return filter;
//    }
//
//    //从会话中解析出用户信息
//    public User parseUserFromConversation(EMConversation conversation) {
//        EMMessage emMessage = conversation.getLatestMessageFromOthers();
//        if (emMessage != null) {
//            ChatMessage chatMessage = new ChatMessage(emMessage);
//            if (chatMessage.getUserId() != null) {
//                User user = new User(chatMessage.getUserId());
//                user.setName(chatMessage.getUserName());
//                user.setAvatar(chatMessage.getUserAvatar());
//                user.setChatId(chatMessage.getEmMessage().getFrom());
//                return user;
//            }
//        }
//        return null;
//    }
}
