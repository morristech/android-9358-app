package com.xmd.technician.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.xmd.technician.bean.ConversationListResult;
import com.xmd.technician.chat.chatview.ChatUser;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.ChatActivity;

import java.util.Map;

/**
 * Created by sdcm on 16-4-14.
 */
public class ChatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MsgDef.MSG_DEF_START_CHAT:
                doStartChatActivity((Map<String, String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_USER:
                doSaveChatUser((Map<String, String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CONVERSATION_LIST:
                doGetConversationList();
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
        ChatUser chatUser = new ChatUser(emchatId);
        chatUser.setNick(emchatNickname);
        chatUser.setAvatar(emchatAvatar);
        UserProfileProvider.getInstance().saveContactOrUpdate(chatUser);

        Activity activity = ActivityHelper.getInstance().getCurrentActivity();
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatConstant.EMCHAT_ID, emchatId);
        activity.startActivity(intent);
    }

    private void doSaveChatUser(Map<String, String> params) {

    }

    private void doGetConversationList() {
        RxBus.getInstance().post(new ConversationListResult(EmchatManager.getInstance().loadConversationList()));
    }


}
