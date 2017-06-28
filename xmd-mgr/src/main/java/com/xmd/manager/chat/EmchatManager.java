package com.xmd.manager.chat;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.Pair;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.manager.Constant;
import com.xmd.manager.ManagerApplication;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;
import com.xmd.manager.database.EmchatUserDao;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class EmchatManager {

    public interface IGetEmchatUserCallback {
        void onGetUserFromDb(EmchatUser emchatUser);
    }

    private static final int MSG_GET_CONVERSATION_LIST = 0x0001;
    private static final int MSG_SAVE_CHAT_USER = 0x0002;
    private static final int MSG_GET_CHAT_USER = 0x0003;
    private static final int MSG_DELETE_CONVERSION = 0x0004;

    private Handler mHandler;

    private static class EmchatManagerHolder {
        private static EmchatManager sInstance = new EmchatManager();
    }

    private EmchatManager() {
        EmchatManagerHandlerThread handlerThread = new EmchatManagerHandlerThread("EmchatDatabaseManager");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), handlerThread);
    }

    public static EmchatManager getInstance() {
        return EmchatManagerHolder.sInstance;
    }

    /**
     * 获取会话列表
     *
     * @return
     */
    public void loadConversationList(String nickname) {
        Message msg = Message.obtain();
        msg.what = MSG_GET_CONVERSATION_LIST;
        msg.obj = nickname;
        mHandler.sendMessage(msg);
    }

    /**
     * @param params
     */
    public void getEmchatUser(Map<String, Object> params) {
        Message msg = Message.obtain();
        msg.what = MSG_GET_CHAT_USER;
        msg.obj = params;
        mHandler.sendMessage(msg);
    }

    /**
     * @param emchatUser
     */
    public void saveChatUser(EmchatUser emchatUser) {
        Message msg = Message.obtain();
        msg.what = MSG_SAVE_CHAT_USER;
        msg.obj = emchatUser;
        mHandler.sendMessage(msg);
    }

    public void deleteChatConversion(String userName) {
        Message msg = Message.obtain();
        msg.what = MSG_DELETE_CONVERSION;
        msg.obj = userName;
        mHandler.sendMessage(msg);
    }

    /**
     * Do the Database Operation in another thread
     */
    class EmchatManagerHandlerThread extends HandlerThread implements Handler.Callback {

        private EmchatUserDao mEmchatUserDao;

        public EmchatManagerHandlerThread(String name) {
            super(name);
            mEmchatUserDao = new EmchatUserDao(ManagerApplication.getAppContext());
        }

        @SuppressWarnings("unchecked")
        public boolean handleMessage(Message msg) {
            //result
            switch (msg.what) {
                case MSG_GET_CONVERSATION_LIST:
                    doGetConvervationList((String) msg.obj);
                    break;
                case MSG_SAVE_CHAT_USER:
                    mEmchatUserDao.saveOrUpdate((EmchatUser) msg.obj);
                    break;
                case MSG_GET_CHAT_USER:
                    doGetEmchatUserWithCallback((Map<String, Object>) msg.obj);
                    break;
                case MSG_DELETE_CONVERSION:
                    deleteConversion((String) msg.obj, true);
                    break;

            }
            return true;
        }

        private void doGetEmchatUserWithCallback(Map<String, Object> params) {
            String emchatId = (String) params.get(Constant.KEY_ID);
            IGetEmchatUserCallback callback = (IGetEmchatUserCallback) params.get(Constant.KEY_CALLBACK);
            EmchatUser emchatUser = mEmchatUserDao.getById(emchatId);
            if (emchatUser != null) {
                callback.onGetUserFromDb(emchatUser);
            }
        }

        private void doGetConvervationList(String nickname) {
            // 获取所有会话，包括陌生人
            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            // 过滤掉messages size为0的conversation
            /**
             * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
             * 影响排序过程，Collection.sort会产生异常
             * 保证Conversation在Sort过程中最后一条消息的时间不变
             * 避免并发问题
             */
            List<Pair<Long, EMConversation>> sortList = new ArrayList<>();
            synchronized (conversations) {
                for (EMConversation conversation : conversations.values()) {
                    try {
                        if (conversation.getAllMessages().size() != 0 && Utils.isEmpty(conversation.getLastMessage().getStringAttribute(EmchatConstant.GROUP_MESSAGE_ID))) {
                            sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                        }
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        if (Utils.isNotEmpty(conversation.getLastMessage().getBody().toString())) {
                            sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                        }

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
                Logger.e(e.getMessage());
            }
            List<EMConversation> list = new ArrayList<>();
            for (Pair<Long, EMConversation> sortItem : sortList) {
                if (Utils.isNotEmpty(nickname)) {
//                    EmchatUser user = mEmchatUserDao.getById(sortItem.second.getUserName());
//                    if (user != null && user.getNick().contains(nickname)) {
//                        list.add(sortItem.second);
//                    }
                } else {
                    list.add(sortItem.second);
                }
            }
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST_FROM_DB, list);
        }


    }

    public static void deleteConversion(String userName, boolean deleteMessages) {
        EMClient.getInstance().chatManager().deleteConversation(userName, deleteMessages);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);

    }


}
