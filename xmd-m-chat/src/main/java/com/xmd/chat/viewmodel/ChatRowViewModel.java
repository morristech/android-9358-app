package com.xmd.chat.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mo on 17-6-30.
 * 用于绑定基本聊天视图
 */

public abstract class ChatRowViewModel {
    protected ChatMessage chatMessage;

    public ObservableBoolean progress = new ObservableBoolean();
    public ObservableBoolean error = new ObservableBoolean();
    public ObservableBoolean showTime = new ObservableBoolean();

    public ChatRowViewModel(ChatMessage chatMessage) {
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
            case CREATE:
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

    public long getTime() {
        return chatMessage.getEmMessage().getMsgTime();
    }

    public Drawable getContentViewBackground(Context context) {
        return chatMessage.isReceivedMessage() ?
                context.getResources().getDrawable(R.drawable.recv_message)
                : context.getResources().getDrawable(R.drawable.sent_message);
    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, ChatRowViewModel data) {
        String url = data.getChatMessage().getUserAvatar();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .into(imageView);
        } else {
            imageView.setImageResource(com.xmd.app.R.drawable.img_default_avatar);
        }
    }

    @BindingAdapter("time")
    public static void bindTime(TextView textView, ChatRowViewModel data) {
        textView.setText(data.getChatMessage().getFormatTime());
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void send() {
        error.set(false);
        progress.set(true);
        MessageManager.getInstance().sendMessage(chatMessage);
    }

    public boolean onLongClick(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(chatMessage.isReceivedMessage() ? R.menu.message_receive_actions : R.menu.message_send_actions);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onClickMenu(item);
            }
        });
        popupMenu.show();
        return true;
    }

    protected boolean onClickMenu(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_delete) {
            EventBus.getDefault().post(new EventDeleteMessage(this));
            return true;
        } else if (i == R.id.menu_revoke) {
            EventBus.getDefault().post(new EventRevokeMessage(this));
            return true;
        }
        return false;
    }

    public abstract void onBindView(View view);

    public abstract void onUnbindView();
}
