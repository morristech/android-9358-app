package com.xmd.chat.viewmodel;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.xmd.app.user.User;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.R;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.view.ChatActivity;
import com.xmd.chat.xmdchat.model.XmdChatModel;


/**
 * Created by mo on 17-6-21.
 * 会话数据
 */

public class ConversationViewModel<T> {
    private User user;
    private T conversation;
    private ChatMessage lastMessage;

    public ConversationViewModel(User user, T conversation) {
        this.user = user;
        this.conversation = conversation;
        if (conversation instanceof EMConversation) {
            this.lastMessage = ChatMessageFactory.createMessage(((EMConversation) conversation).getLastMessage());
        } else {
            this.lastMessage = ChatMessageFactory.createMessage(new TIMConversationExt((TIMConversation) conversation).getLastMsgs(1).get(0));
        }

    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.img_default_avatar);
        }
    }

    @BindingAdapter("time")
    public static void bindTime(TextView textView, ConversationViewModel data) {
        textView.setText(data.getFormatTime());
    }

    public boolean onLongClick(View view) {
        new AlertDialog.Builder(view.getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage("删除与" + user.getShowName() + "的聊天会话?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConversationManager.getInstance().deleteConversation(user.getChatId());
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
        return true;
    }

    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, getChatId());
        view.getContext().startActivity(intent);
    }

    public User getUser() {
        return user;
    }

    public String getChatId() {
        return user.getChatId();
    }

    public String getName() {
        return user.getShowName();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getFormatTime() {
        return lastMessage.getFormatTime();
    }

    public CharSequence getMessage() {
        return lastMessage.getContentText();
    }

    public long getTime() {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            return ((EMMessage) (lastMessage.getMessage())).getMsgTime();
        } else {
            return new TIMConversationExt((TIMConversation) conversation).getLastMsgs(1).get(0).getMsg().time();
        }
    }

    public int getUnReadMsgCount() {
        if (conversation instanceof EMConversation) {
            return ((EMConversation) conversation).getUnreadMsgCount();
        } else {
            TIMConversationExt ext = new TIMConversationExt((TIMConversation) conversation);
            return (int) ext.getUnreadMessageNum();
        }
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public T getConversation() {
        return conversation;
    }

}
