package com.xmd.manager.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.ConversationListResult;
import com.xmd.manager.beans.CouponSelectResult;
import com.xmd.manager.common.ActivityHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.AbstractController;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.window.ChatActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */
public class ChatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_LOGIN_EMCHAT:
                doLoginEmchat(msg.obj);
                break;
            case MsgDef.MSG_DEF_LOGOUT_EMCHAT:
                doLogoutEmchat();
                break;
            case MsgDef.MSG_DEF_START_CHAT:
                doStartChatActivity((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_USER:
                EmchatManager.getInstance().saveChatUser((EmchatUser) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CONVERSATION_LIST:
                String nickname = "";
                if (msg.obj != null) {
                    nickname = (String) msg.obj;
                }
                EmchatManager.getInstance().loadConversationList(nickname);
                break;
            case MsgDef.MSG_DEF_GET_CONVERSATION_LIST_FROM_DB:
                RxBus.getInstance().post(new ConversationListResult((List<EMConversation>) msg.obj));
                break;
            case MsgDef.MSG_DEF_GET_CHAT_USER:
                EmchatManager.getInstance().getEmchatUser((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB:
                EMConversation conversation = null;
                if (msg.obj != null) {
                    conversation = (EMConversation) msg.obj;
                }
                EmchatManager.getInstance().deleteChatConversion(conversation.getUserName());
                break;
        }
        return true;
    }


    private void doLoginEmchat(Object runnable) {
        if (Utils.isNotEmpty(SharedPreferenceHelper.getEmchatId())
                && Utils.isNotEmpty(SharedPreferenceHelper.getEmchatPassword())) {
            EMClient.getInstance().login(SharedPreferenceHelper.getEmchatId(),
                    SharedPreferenceHelper.getEmchatPassword(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Logger.v("ChatController.doLoginEmchat : success");
                            // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                            EMClient.getInstance().updateCurrentUserNick(SharedPreferenceHelper.getUserName());
                            if (runnable instanceof Runnable) {
                                ((Runnable) runnable).run();
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

    private void doLogoutEmchat() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.v("ChatController.doLogoutEmchat.onSuccess : true");
            }

            @Override
            public void onError(int i, String s) {
                Logger.v("ChatController.doLogoutEmchat.onError : " + i + " : " + s);
            }

            @Override
            public void onProgress(int i, String s) {
                Logger.v("ChatController.doLogoutEmchat.onProgress : " + i + " : " + s);
            }
        });
    }

    /**
     * @param params
     */
    private void doStartChatActivity(Map<String, Object> params) {
        Object objId = params.get(EmchatConstant.EMCHAT_ID);
        if (objId != null) {
            String emchatId = objId.toString();
            String emchatNickname = (String) params.get(EmchatConstant.EMCHAT_NICKNAME);
            String emchatAvatar = (String) params.get(EmchatConstant.EMCHAT_AVATAR);
            String emchatUserType = (String) params.get(EmchatConstant.MESSAGE_CHAT_USER_TYPE);

            if (Utils.isNotEmpty(emchatNickname) || Utils.isNotEmpty(emchatAvatar)) {
                EmchatUser emchatUser = new EmchatUser(emchatId);
                emchatUser.setNick(emchatNickname);
                emchatUser.setAvatar(emchatAvatar);
                EmchatManager.getInstance().saveChatUser(emchatUser);
            }

            Activity activity = ActivityHelper.getInstance().getCurrentActivity();
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(EmchatConstant.EMCHAT_ID, emchatId);
            intent.putExtra(EmchatConstant.EMCHAT_OBJECT, (CouponSelectResult) params.get(EmchatConstant.EMCHAT_OBJECT));
            intent.putExtra(EmchatConstant.EMCHAT_NICKNAME, emchatNickname);
            intent.putExtra(EmchatConstant.EMCHAT_AVATAR, emchatAvatar);
            intent.putExtra(EmchatConstant.MESSAGE_CHAT_USER_TYPE, emchatUserType);

            activity.startActivity(intent);
        }
    }
}
