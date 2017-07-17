package com.xmd.chat;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.event.EventDeleteConversation;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.event.EventUnreadCount;
import com.xmd.chat.message.ChatMessage;
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

    private List<String> needDeleteConversationList = new ArrayList<>();
    private int needFilterCount;

    public void init() {
        mConversationList = new ArrayList<>();
    }

    //加载会话列表
    public void loadConversationList(final Callback<List<ConversationViewModel>> callback) {
        mConversationList.clear();
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        if (conversationMap.size() == 0) {
            callback.onResponse(new ArrayList<ConversationViewModel>(), null);
        }
        for (String key : conversationMap.keySet()) {
            EMConversation conversation = conversationMap.get(key);
            if (!TextUtils.isEmpty(key) && conversation != null) {
                User user = userInfoService.getUserByChatId(key);
                if (user == null) {
                    XLogger.e(XmdChat.TAG, " not found user by chatId:" + key);
                    needDeleteConversationList.add(key);
                    continue;
                }
                ConversationViewModel data = new ConversationViewModel(user, conversation);
                mConversationList.add(data);
            }
        }

        deleteConversations(needDeleteConversationList);

        //filter
        if (filter != null) {
            needFilterCount = mConversationList.size();
            for (final ConversationViewModel data : mConversationList) {
                filter.filter(data, new Callback<Boolean>() {
                    @Override
                    public void onResponse(Boolean result, Throwable error) {
                        if (error != null) {
                            //此会话检查失败，暂时移除不显示
                            removeConversationData(data.getChatId());
                        } else if (!result) {
                            //检查不通过，那么删除此会话
                            needDeleteConversationList.add(data.getChatId());
                        }
                        if (--needFilterCount == 0) {
                            //全部检查完毕
                            deleteConversations(needDeleteConversationList);
                            sortConversationByTime(mConversationList);
                            callback.onResponse(mConversationList, null);
                        }
                    }
                });
            }
        } else {
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

    //设置过滤器
    public void setFilter(ConversationFilter filter) {
        this.filter = filter;
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
                return (int) (o2.getTime() - o1.getTime());
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
    private ConversationFilter filter;

    public interface ConversationFilter {
        void filter(ConversationViewModel data, Callback<Boolean> listener);
    }

    public ConversationFilter getFilter() {
        return filter;
    }

    //从会话中解析出用户信息
    public User parseUserFromConversation(EMConversation conversation) {
        EMMessage emMessage = conversation.getLatestMessageFromOthers();
        if (emMessage != null) {
            ChatMessage chatMessage = new ChatMessage(emMessage);
            if (chatMessage.getUserId() != null) {
                User user = new User(chatMessage.getUserId());
                user.setName(chatMessage.getUserName());
                user.setAvatar(chatMessage.getUserAvatar());
                user.setChatId(chatMessage.getEmMessage().getFrom());
                return user;
            }
        }
        return null;
    }
}
