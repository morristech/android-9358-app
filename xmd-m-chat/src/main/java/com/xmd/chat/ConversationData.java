package com.xmd.chat;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.user.User;
import com.xmd.app.widget.GlideCircleTransform;

/**
 * Created by mo on 17-6-21.
 * 会话数据
 */

public class ConversationData {
    private String name;
    private String avatar;
    private String message;
    private long time;

    public ConversationData(User user, String message, long time) {
        name = user.getName();
        avatar = user.getAvatar();
        this.message = message;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
