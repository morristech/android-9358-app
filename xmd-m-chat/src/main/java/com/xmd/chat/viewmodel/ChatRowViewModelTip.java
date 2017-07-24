package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowTipBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.TipChatMessage;


/**
 * Created by mo on 17-7-1.
 * 位置消息
 */

public class ChatRowViewModelTip extends ChatRowViewModel {

    public ChatRowViewModelTip(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowTipBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_tip, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        ChatRowTipBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        if (((TipChatMessage) chatMessage).needSetMovementMethod()) {
            ((TextView) view.findViewById(R.id.tipView)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

    public CharSequence getTip() {
        return ((TipChatMessage) chatMessage).getTip();
    }

    public Drawable getTipIcon(Context context) {
        int id = ((TipChatMessage) chatMessage).getIconResourcesId();
        if (id <= 0) {
            return null;
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
