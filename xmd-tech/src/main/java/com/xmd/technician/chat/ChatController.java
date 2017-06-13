package com.xmd.technician.chat;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.event.DeleteConversionResult;
import com.xmd.technician.chat.event.EventEmChatLoginSuccess;
import com.xmd.technician.chat.event.EventUnreadMessageCount;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.SystemNoticeResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;

import java.util.Map;

/**
 * Created by sdcm on 16-4-14.
 */
public class ChatController extends AbstractController {
    private LoginTechnician technician = LoginTechnician.getInstance();

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
                doDeleteConversation(conversation.conversationId(), true);
                break;
            case MsgDef.MSG_DEF_LOGOUT_EMCHAT:
                doLogOutEmChat();
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
        String emchatIsTech = (String) params.get(ChatConstant.EMCHAT_IS_TECH);

        if (TextUtils.isEmpty(emchatId)) {
            return;
        }

        ChatUser chatUser = new ChatUser(emchatId);
        chatUser.setNickname(emchatNickname);
        chatUser.setAvatar(emchatAvatar);
        chatUser.setUserType(emchatIsTech);
        UserUtils.saveUser(chatUser);

        Intent intent = new Intent("com.xmd.technician.action.START_CHAT");
        intent.putExtra(ChatConstant.TO_CHAT_USER_ID, emchatId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TechApplication.getAppContext().startActivity(intent);
    }

    private void doSaveChatUser(Map<String, String> params) {

    }

    private void doGetConversationList() {
//        RxBus.getInstance().post(new ConversationListResult(XMDEmChatManager.getInstance().getAllConversationList()));
    }

    private void systemNoticeNotify() {
        RxBus.getInstance().post(new SystemNoticeResult());
    }

    private void doDeleteConversation(String userName, boolean deleteMessages) {
        EMClient.getInstance().chatManager().deleteConversation(userName, deleteMessages);
        // MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
        RxBus.getInstance().post(new DeleteConversionResult());
        RxBus.getInstance().post(new EventUnreadMessageCount(ChatHelper.getInstance().getUnreadMessageCount()));
        UserUtils.deleteUser(userName);
    }

    private void doLogOutEmChat() {
        ChatHelper.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.v("成功退出");
            }

            @Override
            public void onError(int i, String s) {
                Logger.v("退出失败");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void doLoginEmchat(Object runnable) {

        if (Utils.isNotEmpty(SharedPreferenceHelper.getEmchatId())
                && Utils.isNotEmpty(SharedPreferenceHelper.getEMchatPassword())) {
            try {
                EMClient.getInstance().login(SharedPreferenceHelper.getEmchatId(),
                        SharedPreferenceHelper.getEMchatPassword(), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                UserProfileProvider.getInstance().initContactList();
                                Logger.v("ChatController.doLoginEmchat : success");
                                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                                EMClient.getInstance().updateCurrentUserNick(SharedPreferenceHelper.getUserName());
                                if (runnable instanceof Runnable) {
                                    ((Runnable) runnable).run();
                                }
                                RxBus.getInstance().post(new EventEmChatLoginSuccess(true));
                            }

                            @Override
                            public void onError(int i, String s) {
                                Logger.e("onError:" + i + ", " + s);

                                if(i != EMError.USER_ALREADY_LOGIN){
                                    if(technician.isLogin()){
                                        RxBus.getInstance().post(new EventEmChatLoginSuccess(false));
                                    }else{
                                        XToast.showInNotUI("无法初始化聊天系统:" + i + "," + s);
                                        return;
                                    }

                                }
                                XToast.showInNotUI("初始化聊天系统失败:" + i + "," + s);
                            }

                            @Override
                            public void onProgress(int i, String s) {
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("登录异常");
            }
        }
    }
}
