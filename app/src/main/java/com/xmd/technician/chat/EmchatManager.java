package com.xmd.technician.chat;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.chat.event.EventLoginSuccess;
import com.xmd.technician.chat.event.EventReceiveMessage;
import com.xmd.technician.chat.event.EventUnreadMessageCount;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-6.
 */
public class EmchatManager implements IEmchat {
    private static final String TAG = "EmchatManager";

    private static class EmchatManagerHolder {
        private static EmchatManager sInstance = new EmchatManager();
    }

    private EmchatManager() {

    }

    public static EmchatManager getInstance() {
        return EmchatManagerHolder.sInstance;
    }

    private boolean login; //是否登录
    private boolean isCalledLogin; //是否正在登录

    /**
     * 初始化环信
     *
     * @param context 应用上下文
     * @param debug   调试模式
     */
    @Override
    public void init(Context context, String appKey,boolean debug) {
        EMOptions emOptions = new EMOptions();
        if(!TextUtils.isEmpty(appKey)) {
            emOptions.setAppKey(appKey);
        }
        emOptions.setAutoLogin(true);
        emOptions.setSortMessageByServerTime(true); //消息按时间排序
        EMClient.getInstance().init(context, emOptions);
        EMClient.getInstance().setDebugMode(debug);
    }

    @Override
    public void login(final String name, final String password) {
        if (isCalledLogin) {
            XLogger.d("is called login in!");
            return;
        }
        isCalledLogin = true;
        if (!EMClient.getInstance().isLoggedInBefore()) {
            XLogger.i(TAG, "login:" + name + "," + password);
            EMClient.getInstance().login(name, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    XLogger.i(TAG, "login success");
                    //加载会话消息
                    long t1 = SystemClock.elapsedRealtime();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    XLogger.i(TAG, "load groups and conversations cost:" + (SystemClock.elapsedRealtime() - t1));
                    //注册消息监听器
                    EMClient.getInstance().chatManager().addMessageListener(messageListener);
                    //发送登录成功消息
                    RxBus.getInstance().post(new EventLoginSuccess());
                }

                @Override
                public void onError(int i, String s) {
                    XLogger.e(TAG, "login failed:" + i + "," + s);
                    //retry
                    if (i == EMError.NETWORK_ERROR
                            || i == EMError.SERVER_TIMEOUT
                            || i == EMError.SERVER_BUSY
                            || i == EMError.SERVER_GET_DNSLIST_FAILED
                            || i == EMError.SERVER_NOT_REACHABLE
                            || i == EMError.SERVER_UNKNOWN_ERROR) {
                        XLogger.i("retry ... login");
                        isCalledLogin = false;
                        login(name, password);
                    } else {
                        XToast.show("无法初始化聊天系统:" + i + "," + s);
                    }
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } else {
            XLogger.i(TAG,"is login before ");
            //注册消息监听器
            EMClient.getInstance().chatManager().addMessageListener(messageListener);
            //发送登录成功消息
            RxBus.getInstance().post(new EventLoginSuccess());
        }
    }

    @Override
    public void updateNickName(String nickName) {
        EMClient.getInstance().pushManager().updatePushNickname(nickName);
    }

    @Override
    public void logout() {
        if (isConnected()) {
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    XLogger.i(TAG, "logout");
                    login = false;
                    isCalledLogin = false;
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }


    /**
     * 获取会话列表
     *
     * @return
     */
    public List<EMConversation> getAllConversationList() {
        //获取所有的会话消息
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMsgCount() > 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                } else {
                    sortList.add(new Pair<>(0L, conversation));
                }
            }
        }
        try {
            // 根据最后一条消息的时间排序
            Collections.sort(sortList, (con1, con2) -> {
                        if (con1.first.equals(con2.first)) {
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

    @Override
    public int getUnreadMessageCount() {
        return EMClient.getInstance().chatManager().getUnreadMessageCount();
    }

    @Override
    public void clearUnreadMessage(String userName) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userName);
        clearUnreadMessage(conversation);
    }

    @Override
    public void clearUnreadMessage(EMConversation conversation) {
        conversation.markAllMessagesAsRead();
        RxBus.getInstance().post(new EventUnreadMessageCount(getUnreadMessageCount()));
    }

    public boolean isConnected() {
        return EMClient.getInstance().isConnected();
    }

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            XLogger.d("new message:" + list.size() + ", unread:" + getUnreadMessageCount());
            RxBus.getInstance().post(new EventReceiveMessage(list));
            RxBus.getInstance().post(new EventUnreadMessageCount(getUnreadMessageCount()));
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
