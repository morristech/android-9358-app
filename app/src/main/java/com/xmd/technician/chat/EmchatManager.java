package com.xmd.technician.chat;

import android.util.Pair;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-6.
 */
public class EmchatManager {

    private static class EmchatManagerHolder {
        private static EmchatManager sInstance = new EmchatManager();
    }

    private EmchatManager() {

    }

    public static EmchatManager getInstance() {
        return EmchatManagerHolder.sInstance;
    }

    /**
     * 获取会话列表
     * @return
     */
    public List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // 根据最后一条消息的时间排序
            Collections.sort(sortList, (con1, con2) -> {
                        if (con1.first == con2.first) {
                            return 0;
                        } else if (con2.first > con1.first) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

}
