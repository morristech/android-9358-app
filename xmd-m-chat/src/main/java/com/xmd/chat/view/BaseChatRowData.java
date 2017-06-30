package com.xmd.chat.view;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.message.ChatMessage;

/**
 * Created by mo on 17-6-30.
 * 基本聊天视图
 */

public class BaseChatRowData {
    private ChatMessage chatMessage;

    public ObservableBoolean progress = new ObservableBoolean();
    public ObservableBoolean error = new ObservableBoolean();

    public BaseChatRowData(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        error.set(false);
        progress.set(false);
        EMMessage.Status status = chatMessage.getEmMessage().status();
        switch (status) {
            case SUCCESS:
                error.set(false);
                break;
            case FAIL:
                error.set(true);
                break;
            case INPROGRESS:
                progress.set(true);
                break;
        }

        chatMessage.getEmMessage().setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                progress.set(false);
                error.set(false);
            }

            @Override
            public void onError(int i, String s) {
                progress.set(false);
                error.set(true);
            }

            @Override
            public void onProgress(int i, String s) {
                progress.set(true);
            }
        });
    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, BaseChatRowData data) {
        String url = data.getChatMessage().getUserAvatar();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .into(imageView);
        } else {
//            imageView.setImageResource(com.xmd.app.R.drawable.img_default_avatar);
        }
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
