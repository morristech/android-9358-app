package com.xmd.chat.xmdchat.present;

import android.databinding.ObservableBoolean;

import com.shidou.commonlibrary.helper.XLogger;
import com.tencent.imsdk.TIMMessage;
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

public class ImChatRowViewModelPresent implements XmdChatRowViewModelInterface {
    private ChatMessage<TIMMessage> mMessage;

    @Override
    public void init(ChatMessage message, ObservableBoolean progress, ObservableBoolean error, ObservableBoolean showTime) {
        this.mMessage = message;
        error.set(false);
        progress.set(false);
        switch (mMessage.getMessage().status()){
            case HasRevoked:
                progress.set(false);
                error.set(false);
                return;
                //break;
            case SendSucc:
                progress.set(false);
                error.set(false);
                if(message.getTag() != null && ChatMessage.MSG_TAG_HELLO.equals(message.getTag())){
                    return;
                }
                String msgType = message.getMsgType();
                //通知服务器有新的消息
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .notifyServerChatMessage(
                                ChatAccountManager.getInstance().getChatId(),
                                ChatAccountManager.getInstance().getUserType(),
                                mMessage.getRemoteChatId(),
                                UserInfoServiceImpl.getInstance().getUserByChatId(message.getRemoteChatId()).getUserType(),
                                ((TIMMessage)message.getMessage()).getMsgId(),
                                msgType, message.getContentText().toString());
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
                break;
            case Sending:
                progress.set(true);
                error.set(false);
                break;
            case SendFail:
                progress.set(false);
                error.set(true);
                if(ChatSettingManager.getInstance().isInCustomerBlackList(message.getToChatId())){
                    error.set(false);
                }
                break;

        }
    }

    @Override
    public long getTime() {
        return mMessage.getMessage().timestamp()*1000;
    }
}
