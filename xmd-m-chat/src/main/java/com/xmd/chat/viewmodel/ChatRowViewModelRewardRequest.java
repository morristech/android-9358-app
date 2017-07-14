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
 * 求打赏消息
 */

public class ChatRowViewModelRewardRequest extends ChatRowViewModelText {

    public ChatRowViewModelRewardRequest(ChatMessage chatMessage) {
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
        if (!chatMessage.isReceivedMessage()) {
            return context.getResources().getDrawable(R.drawable.send_wrapper_reward_request);
        }
        return super.getContentViewBackground(context);
    }
}
