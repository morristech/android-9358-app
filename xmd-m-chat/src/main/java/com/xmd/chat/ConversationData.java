package com.xmd.chat;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.user.User;

/**
 * Created by mo on 17-6-21.
 * 会话数据
 */

public class ConversationData {
    private String name;
    private String avatar;
    private String message;
    private String time;

    public ConversationData(User user, String message, long time) {
        name = user.getName();
        avatar = user.getAvatar();
        this.message = message;
        this.time = DateUtils.getSdf("MMdd HH:mm:ss").format(time);
    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, String url) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
