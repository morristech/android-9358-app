package com.xmd.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.xmd.app.R;
import com.xmd.app.event.EventClickAvatar;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mo on 17-6-15.
 * 用户头像
 */

public class CircleAvatarView extends android.support.v7.widget.AppCompatImageView {
    private User user;
    public CircleAvatarView(Context context) {
        super(context);
        init();
    }

    public CircleAvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleAvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        setImageResource(R.drawable.img_default_avatar);
    }

    //设置用户信息
    public void setUserInfo(final User user) {
        this.user = user;
        if (!TextUtils.isEmpty(user.getAvatar())) {
            Glide.with(getContext()).load(user.getAvatar()).transform(new GlideCircleTransform(getContext())).error(R.drawable.img_default_avatar).into(this);
        } else {
            setImageResource(R.drawable.img_default_avatar);
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventClickAvatar(user));
            }
        });
    }

    @Deprecated
    public void setUserInfo(final String userId, String avatarUrl, boolean isTech) {
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(getContext()).load(avatarUrl).transform(new GlideCircleTransform(getContext())).error(R.drawable.img_default_avatar).into(this);
        } else {
            setImageResource(R.drawable.img_default_avatar);
        }
        if (!TextUtils.isEmpty(userId)) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventClickAvatar(UserInfoServiceImpl.getInstance().getUserByUserId(userId)));
                }
            });
        }
    }
}
