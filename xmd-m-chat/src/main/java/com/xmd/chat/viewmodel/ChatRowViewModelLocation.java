package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowLocationBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CustomLocationMessage;


/**
 * Created by mo on 17-7-1.
 * 17-10-27,将webView 该为imageView
 * 位置消息
 */

public class ChatRowViewModelLocation extends ChatRowViewModel {

    public ChatRowViewModelLocation(ChatMessage chatMessage) {
        super(chatMessage);
    }

    public static View createView(ViewGroup parent) {
        ChatRowLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_location, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        if (chatMessage.getMessage() instanceof TIMMessage && ((TIMMessage) chatMessage.getMessage()).status() == TIMMessageStatus.HasRevoked) {
            return null;
        }
        ChatRowLocationBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
        return binding;
    }

    @Override
    public void onUnbindView() {

    }

    public String getAddress() {
        return ((CustomLocationMessage) chatMessage).getAddress();
    }

    @BindingAdapter("map")
    public static void bindMap(ImageView locationView, ChatRowViewModelLocation data) {
        CustomLocationMessage locationMessage = (CustomLocationMessage) data.getChatMessage();
        Glide.with(locationView.getContext()).load(locationMessage.getMapUrl()).into(locationView);
    }

    @Override
    public Drawable getContentViewBackground(Context context) {
        return null;
    }

}
