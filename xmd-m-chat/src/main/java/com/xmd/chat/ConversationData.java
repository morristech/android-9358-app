package com.xmd.chat;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.user.User;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.message.ChatMessage;

/**
 * Created by mo on 17-6-21.
 * 会话数据
 */

public class ConversationData {
    private User user;
    private EMConversation conversation;
    private ChatMessage lastMessage;

    public ConversationData(User user, EMConversation conversation, ChatMessage chatMessage) {
        this.user = user;
        this.conversation = conversation;
        this.lastMessage = chatMessage;
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
    public static void bindTime(TextView textView, long time) {
        if (System.currentTimeMillis() - time > 2 * DateUtils.DAY_TIME_MS) {
            textView.setText(DateUtils.doLong2String(time));
        } else if (System.currentTimeMillis() - time > DateUtils.DAY_TIME_MS) {
            textView.setText(DateUtils.doLong2String(time, "昨天 HH:mm"));
        } else {
            textView.setText(DateUtils.doLong2String(time, "HH:mm"));
        }
    }

    public String getName() {
        return user.getName();
    }

    public String getAvatar() {
        return user.getAvatar();
    }

    public String getMessage() {
        return lastMessage.getContentText();
    }

    public long getTime() {
        return lastMessage.getEmMessage().getMsgTime();
    }

    public int getUnReadMsgCount() {
        return conversation.getUnreadMsgCount();
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
}
