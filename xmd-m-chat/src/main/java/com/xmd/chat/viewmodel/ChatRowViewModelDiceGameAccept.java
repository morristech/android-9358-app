package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.beans.DiceGameResult;
import com.xmd.chat.databinding.ChatRowDiceGameAcceptBinding;
import com.xmd.chat.event.EventGameDiceStatusChange;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import rx.Observable;


/**
 * Created by mo on 17-7-1.
 * 骰子游戏消息： 接受 拒绝
 */

public class ChatRowViewModelDiceGameAccept extends ChatRowViewModel {
    private DiceGameChatMessage message;
    private ChatRowDiceGameAcceptBinding binding;
    public ObservableBoolean inProcess = new ObservableBoolean();

    public ChatRowViewModelDiceGameAccept(ChatMessage chatMessage) {
        super(chatMessage);
        message = (DiceGameChatMessage) chatMessage;
    }

    public static View createView(ViewGroup parent) {
        ChatRowDiceGameAcceptBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.chat_row_dice_game_accept, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        if (message.getInnerProcessed() == null) {
            EventBusSafeRegister.register(this);
        }
        return binding;
    }

    @Override
    protected boolean contentViewMatchParent() {
        return true;
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

    @Override
    public void onUnbindView() {
        EventBusSafeRegister.unregister(this);
    }

    public String getCredit() {
        return message.getCredit();
    }

    public void onClickReject() {
        if (inProcess.get()) {
            return;
        }
        inProcess.set(true);
        Observable<BaseBean<DiceGameResult>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .diceGamePlayOrCancel(message.getGameId(), DiceGameChatMessage.STATUS_REJECT);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameResult>>() {
            @Override
            public void onCallbackSuccess(BaseBean<DiceGameResult> result) {
//                XLogger.d("refuse result:" + result.getRespData());
//                inProcess.set(false);
//                message.setInnerProcessed(DiceGameChatMessage.getStatusText(result.getRespData().getStatus()));
//                binding.setData(ChatRowViewModelDiceGameAccept.this);
//                binding.executePendingBindings();
//
//                //发送拒绝消息
//                if (DiceGameChatMessage.STATUS_REJECT.equals(result.getRespData().getStatus())) {
//                    final DiceGameChatMessage refuseMessage = DiceGameChatMessage.createMessage(
//                            DiceGameChatMessage.STATUS_REJECT,
//                            ChatAccountManager.getInstance().getChatId(),
//                            message.getRemoteChatId(),
//                            message.getGameId(),
//                            Integer.parseInt(message.getCredit()));
//                    refuseMessage.getEmMessage().setMessageStatusCallback(new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            ChatMessageManager.getInstance().removeMessage(refuseMessage);
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//                            ChatMessageManager.getInstance().removeMessage(refuseMessage);
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//
//                        }
//                    });
//                    ChatMessageManager.getInstance().sendMessage(refuseMessage, false);
//                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                inProcess.set(false);
                XToast.show("拒绝失败：" + e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("游戏已结束")) {
                    message.setInnerProcessed("游戏已结束");
                    binding.setData(ChatRowViewModelDiceGameAccept.this);
                    binding.executePendingBindings();
                }
            }
        });
    }

    public void onClickAccept() {
//        if (inProcess.get()) {
//            return;
//        }
//        inProcess.set(true);
//        Observable<BaseBean<DiceGameResult>> observable = XmdNetwork.getInstance()
//                .getService(NetService.class)
//                .diceGamePlayOrCancel(message.getGameId(), DiceGameChatMessage.STATUS_ACCEPT);
//        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameResult>>() {
//            @Override
//            public void onCallbackSuccess(BaseBean<DiceGameResult> result) {
//                XLogger.d("accept result:" + result.getRespData());
//                inProcess.set(false);
//                message.setInnerProcessed(DiceGameChatMessage.getStatusText(result.getRespData().getStatus()));
//                binding.setData(ChatRowViewModelDiceGameAccept.this);
//                binding.executePendingBindings();
//
//                //发送接受消息
//                if (DiceGameChatMessage.STATUS_ACCEPT.equals(result.getRespData().getStatus())) {
//                    final DiceGameChatMessage acceptMessage = DiceGameChatMessage.createMessage(
//                            DiceGameChatMessage.STATUS_ACCEPT,
//                            ChatAccountManager.getInstance().getChatId(),
//                            message.getRemoteChatId(),
//                            message.getGameId(),
//                            Integer.parseInt(message.getCredit()));
//                    acceptMessage.getEmMessage().setMessageStatusCallback(new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            ChatMessageManager.getInstance().removeMessage(acceptMessage);
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//                            ChatMessageManager.getInstance().removeMessage(acceptMessage);
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//
//                        }
//                    });
//                    ChatMessageManager.getInstance().sendMessage(acceptMessage, false);
//
//                    //发送游戏结果
//                    DiceGameChatMessage resultMessage = DiceGameChatMessage.createMessage(
//                            DiceGameChatMessage.STATUS_OVER,
//                            ChatAccountManager.getInstance().getChatId(),
//                            message.getRemoteChatId(),
//                            message.getGameId(),
//                            Integer.parseInt(message.getCredit()),
//                            result.getRespData().getSrcPoint(),
//                            result.getRespData().getDstPoint());
//                    ChatMessageManager.getInstance().sendMessage(resultMessage);
//                }
//            }
//
//            @Override
//            public void onCallbackError(Throwable e) {
//                inProcess.set(false);
//                XToast.show("接受失败：" + e.getMessage());
//                if (e.getMessage() != null && e.getMessage().contains("游戏已结束")) {
//                    message.setInnerProcessed("游戏已结束");
//                    binding.setData(ChatRowViewModelDiceGameAccept.this);
//                    binding.executePendingBindings();
//                }
//            }
//        });
    }

    @Subscribe
    public void onStatusChangedEvent(EventGameDiceStatusChange event) {
        if (event.getGameId() != null && event.getGameId().equals(message.getGameId())) {
            EventBusSafeRegister.unregister(ChatRowViewModelDiceGameAccept.this);
            binding.setData(this);
        }
    }
}
