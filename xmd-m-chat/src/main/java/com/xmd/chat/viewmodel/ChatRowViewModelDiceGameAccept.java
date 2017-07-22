package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowDiceGameAcceptBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.DiceGameChatMessage;


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

    }

    public String getCredit() {
        return message.getCredit();
    }

    public void onClickReject() {
        chatMessage.setInnerProcessed("已拒绝");
        binding.setData(this);
        binding.executePendingBindings();
    }

    public void onClickAccept() {

    }
}
