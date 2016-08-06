package com.xmd.technician.chat;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.ConversationListResult;
import com.xmd.technician.common.Logger;
import com.xmd.technician.http.gson.SystemNoticeResult;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;

import java.util.Map;

/**
 * Created by sdcm on 16-4-14.
 */
public class ChatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MsgDef.MSG_DEF_LOGIN_EMCHAT:
                doLoginEmchat(msg.obj);
                break;
            case MsgDef.MSG_DEF_START_CHAT:
                doStartChatActivity((Map<String, String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_USER:
                doSaveChatUser((Map<String, String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CONVERSATION_LIST:
                doGetConversationList();
                break;
            case MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY:
                systemNoticeNotify();
                break;
        }

        return true;
    }

    /**
     *
     * @param params
     */
    private void doStartChatActivity(Map<String, String> params) {

        String emchatId = params.get(ChatConstant.EMCHAT_ID);
        String emchatNickname = params.get(ChatConstant.EMCHAT_NICKNAME);
        String emchatAvatar = params.get(ChatConstant.EMCHAT_AVATAR);
        String emchatUserType = params.get(ChatConstant.EMCHAT_USER_TYPE);

        if(TextUtils.isEmpty(emchatId)){
            return;
        }

        ChatUser chatUser = new ChatUser(emchatId);
        chatUser.setNick(emchatNickname);
        chatUser.setAvatar(emchatAvatar);
        chatUser.setUserType(emchatUserType);
        UserUtils.saveUser(chatUser);

        Intent intent = new Intent("com.xmd.technician.action.START_CHAT");
        intent.putExtra(ChatConstant.EMCHAT_ID, emchatId);
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
                                ((Runnable)runnable).run();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Logger.v("onError:" + i + ", " + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {
                        }
                    });
        }
    }
}
