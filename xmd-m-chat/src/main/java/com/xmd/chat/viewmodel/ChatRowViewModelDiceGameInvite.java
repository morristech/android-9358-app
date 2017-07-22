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
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.AccountManager;
import com.xmd.chat.GameManager;
import com.xmd.chat.MessageManager;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.beans.DiceGameResult;
import com.xmd.chat.databinding.ChatRowDiceGameInviteBinding;
import com.xmd.chat.event.EventGameDiceAccept;
import com.xmd.chat.event.EventGameDiceRefuse;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;

import rx.Observable;


/**
 * Created by mo on 17-7-1.
 * 骰子游戏消息 -- 邀请界面, 处理邀请，取消邀请，邀请被接受、被拒绝、超时
 */

public class ChatRowViewModelDiceGameInvite extends ChatRowViewModel {
    private DiceGameChatMessage message;
    private String invitedName;

    private ChatRowDiceGameInviteBinding binding;
    public ObservableBoolean inProcess = new ObservableBoolean();

    public ChatRowViewModelDiceGameInvite(ChatMessage chatMessage) {
        super(chatMessage);
        message = (DiceGameChatMessage) chatMessage;
        User invitedUser = UserInfoServiceImpl.getInstance().getUserByChatId(message.getRemoteChatId());
        invitedName = invitedUser.getShowName();
    }

    public static View createView(ViewGroup parent) {
        ChatRowDiceGameInviteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.chat_row_dice_game_invite, parent, false);
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

    public String getInviteName() {
        return message.getUserName();
    }

    public String getInvitedName() {
        return invitedName;
    }

    public String getCredit() {
        return message.getCredit();
    }

    public String getInviteAvatar() {
        return message.getUserAvatar();
    }

    public boolean isShowCancel() {
        return message.getInnerProcessed() == null && DiceGameChatMessage.STATUS_REQUEST.equals(message.getGameStatus());
    }

    public String checkTimeout() {
        if (message.getEmMessage().getMsgTime() + GameManager.getInstance().getDiceExpireTime() < System.currentTimeMillis()) {
            message.setInnerProcessed("已超时");
            return "已超时";
        }
        return null;
    }

    public String getStatus() {
        if (message.getInnerProcessed() != null || checkTimeout() != null) {
            return message.getInnerProcessed();
        }
        return DiceGameChatMessage.getStatusText(message.getGameStatus());
    }

    //取消邀请
    public void onCancel(View v) {
        //检查是否超时
        if (message.getInnerProcessed() != null || checkTimeout() != null) {
            binding.setData(this);
            binding.executePendingBindings();
            return;
        }
        //发送取消指令
        if (inProcess.get()) {
            return;
        }
        inProcess.set(true);
        Observable<BaseBean<DiceGameResult>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .diceGamePlayOrCancel(message.getGameId(), DiceGameChatMessage.STATUS_CANCEL);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameResult>>() {
            @Override
            public void onCallbackSuccess(BaseBean<DiceGameResult> result) {
                inProcess.set(false);
                message.setInnerProcessed(DiceGameChatMessage.getStatusText(result.getRespData().getStatus()));
                binding.setData(ChatRowViewModelDiceGameInvite.this);
                binding.executePendingBindings();

                //发送取消
                if (DiceGameChatMessage.STATUS_CANCEL.equals(result.getRespData().getStatus())) {
                    DiceGameChatMessage cancelMessage = DiceGameChatMessage.createMessage(
                            DiceGameChatMessage.STATUS_CANCEL,
                            AccountManager.getInstance().getChatId(),
                            message.getRemoteChatId(),
                            message.getGameId(),
                            Integer.parseInt(message.getCredit()));
                    MessageManager.getInstance().sendMessage(cancelMessage, false);
                    MessageManager.getInstance().removeMessage(cancelMessage);
                }
                //显示提示消息
                TipChatMessage tipChatMessage = TipChatMessage.create(message.getRemoteChatId(), "取消游戏，返还" + getCredit() + "积分");
                MessageManager.getInstance().saveMessage(tipChatMessage);
            }

            @Override
            public void onCallbackError(Throwable e) {
                inProcess.set(false);
                XToast.show("取消失败：" + e.getMessage());
            }
        });
    }

    @Subscribe
    public void onAccept(EventGameDiceAccept event) {
        if (event.getGameId() != null && event.getGameId().equals(message.getGameId())) {
            binding.setData(this);
        }
    }

    @Subscribe
    public void onRefuse(EventGameDiceRefuse event) {
        if (event.getGameId() != null && event.getGameId().equals(message.getGameId())) {
            binding.setData(this);
        }
    }
}
