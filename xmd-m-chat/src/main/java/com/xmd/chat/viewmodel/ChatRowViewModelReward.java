package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowTextBinding;
import com.xmd.chat.message.ChatMessage;


/**
 * Created by mo on 17-7-1.
 * 打赏消息
 */

public class ChatRowViewModelReward extends ChatRowViewModelText {

    public ChatRowViewModelReward(ChatMessage chatMessage) {
        super(chatMessage);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        ChatRowTextBinding binding = DataBindingUtil.getBinding(view);
        binding.text.setTextColor(0xffffffff);
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        if (chatMessage.isReceivedMessage()) {
            return context.getResources().getDrawable(R.drawable.receive_wrapper_reward);
        }
        return super.getContentViewBackground(context);
    }
}
