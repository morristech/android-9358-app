package com.xmd.technician.chat;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.ConversationListResult;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.event.EventEmChatLogin;
import com.xmd.technician.http.gson.SystemNoticeResult;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.Map;

/**
 * Created by sdcm on 16-4-14.
 */
public class ChatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_LOGIN_EMCHAT:
                doLoginEmchat(msg.obj);
                break;
            case MsgDef.MSG_DEF_START_CHAT:
                doStartChatActivity((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_USER:
                doSaveChatUser((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CONVERSATION_LIST:
                doGetConversationList();
                break;
            case MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY:
                systemNoticeNotify();
                break;
            case MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB:
                EMConversation conversation = null;
                if (msg.obj != null) {
                    conversation = (EMConversation) msg.obj;
                }
                doDeleteConversation(conversation.getUserName(), true);
                break;
        }

        return true;
    }

    /**
     * @param params
     */
    private void doStartChatActivity(Map<String, Object> params) {

        String emchatId = (String) params.get(ChatConstant.EMCHAT_ID);
        String emchatNickname = (String) params.get(ChatConstant.EMCHAT_NICKNAME);
        String emchatAvatar = (String) params.get(ChatConstant.EMCHAT_AVATAR);
        String emchatUserType = (String) params.get(ChatConstant.EMCHAT_USER_TYPE);
        String emchatIsTech = (String) params.get(ChatConstant.EMCHAT_IS_TECH);

        if (TextUtils.isEmpty(emchatId)) {
            return;
        }

        ChatUser chatUser = new ChatUser(emchatId);
        chatUser.setNick(emchatNickname);
        chatUser.setAvatar(emchatAvatar);
        chatUser.setUserType(emchatUserType);
        UserUtils.saveUser(chatUser);

        Intent intent = new Intent("com.xmd.technician.action.START_CHAT");
        intent.putExtra(ChatConstant.EMCHAT_ID, emchatId);
        intent.putExtra(ChatConstant.EMCHAT_IS_TECH, emchatIsTech);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TechApplication.getAppContext().startActivity(intent);
    }

    private void doSaveChatUser(Map<String, String> params) {

    }

    private void doGetConversationList() {
        RxBus.getInstance().post(new ConversationListResult(EmchatManager.getInstance().loadConversationList()));
    }

    private void systemNoticeNotify() {
        RxBus.getInstance().post(new SystemNoticeResult());
    }

    private void doDeleteConversation(String userName, boolean deleteMessages) {
        EMClient.getInstance().chatManager().deleteConversation(userName, deleteMessages);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
    }

    private void doLoginEmchat(Object runnable) {
        if (!TextUtils.isEmpty(SharedPreferenceHelper.getEmchatId())
                && !TextUtils.isEmpty(SharedPreferenceHelper.getEMchatPassword())) {
            EMClient.getInstance().login(SharedPreferenceHelper.getEmchatId(),
                    SharedPreferenceHelper.getEMchatPassword(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            UserProfileProvider.getInstance().initContactList();
                            Logger.v("ChatController.doLoginEmchat : success");
                            // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                            EMClient.getInstance().updateCurrentUserNick(SharedPreferenceHelper.getUserName());
                            if (runnable instanceof Runnable) {
                                ((Runnable) runnable).run();
                            }
                            RxBus.getInstance().post(new EventEmChatLogin(true));
                        }

                        @Override
                        public void onError(int i, String s) {
                            Logger.e("onError:" + i + ", " + s);
                            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TechApplication.getAppContext(), "初始化聊天系统失败:" + i + "," + s, Toast.LENGTH_SHORT).show();
                                }
                            });
                            RxBus.getInstance().post(new EventEmChatLogin(false));
                        }

                        @Override
                        public void onProgress(int i, String s) {
                        }
                    });
        }
    }
}
