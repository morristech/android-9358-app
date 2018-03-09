package com.xmd.chat.xmdchat.present;

import android.databinding.ObservableBoolean;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatAccountManager;
import com.xmd.chat.ChatSettingManager;
import com.xmd.chat.NetService;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.xmdchat.contract.XmdChatRowViewModelInterface;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;

/**
 * Created by Lhj on 18-1-23.
 */

public class EmChatRowViewModelPresent implements XmdChatRowViewModelInterface {
    private ChatMessage<EMMessage> chatMessage;
    private ObservableBoolean progress;
    public EmChatRowViewModelPresent(){

    }

    @Override
    public void init(ChatMessage message, final ObservableBoolean progress, final ObservableBoolean error, ObservableBoolean showTime) {
        this.chatMessage = message;
        this.progress = progress;
        chatMessage.getMessage().setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                progress.set(false);
                error.set(false);
                if(ChatMessage.MSG_TAG_HELLO.equals(chatMessage.getTag())){
                    return;
                }
                String msgType =chatMessage.getMsgType();
                //通知服务器有新的消息
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .notifyServerChatMessage(
                                ChatAccountManager.getInstance().getChatId(),
                                ChatAccountManager.getInstance().getUserType(),
                                chatMessage.getRemoteChatId(),
                                UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getRemoteChatId()).getUserType(),
                                chatMessage.getMessage().getMsgId(),
                                msgType, chatMessage.getContentText().toString());
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        XLogger.d("notifyServerChatMessage success");
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XLogger.d("notifyServerChatMessage failed:" + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                //当技师被用户拉入黑名单时不进行错误提示
                switch (i) {
                    case 201:
                        XToast.show("聊天帐号登录失败，请重新登录");
                        progress.set(false);
                        error.set(true);
                        break;
                    case 210:
                        ChatSettingManager.getInstance().judgeInCustomerBlack(chatMessage.getToChatId(), true);
                        chatMessage.getMessage().setStatus(EMMessage.Status.SUCCESS);
                        progress.set(false);
                        error.set(false);
                        break;
                    default:
                        XToast.show("发送失败：" + s);
                        progress.set(false);
                        error.set(true);
                        break;
                }
            }

            @Override
            public void onProgress(int i, String s) {
                progress.set(true);
            }
        });

        error.set(false);
        progress.set(false);
        EMMessage.Status status = chatMessage.getMessage().status();
        switch (status) {
            case SUCCESS:
                error.set(false);
                break;
            case FAIL:
                if (ChatSettingManager.getInstance().isInCustomerBlackList(chatMessage.getToChatId())) {
                    error.set(false);
                } else {
                    error.set(true);
                }

                break;
            case CREATE:
            case INPROGRESS:
                progress.set(true);
                break;
        }
    }

    @Override
    public long getTime() {
        return chatMessage.getMessage().getMsgTime();
    }
}



