package com.xmd.technician.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.technician.R;
import com.xmd.technician.common.UINavigation;

/**
 * Created by mo on 17-6-15.
 * 用户头像
 */

public class CircleAvatarView extends android.support.v7.widget.AppCompatImageView {
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

    public void setUserInfo(String userId, String avatarUrl) {
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(getContext()).load(avatarUrl).transform(new GlideCircleTransform(getContext())).into(this);
        } else {
            setImageResource(R.drawable.img_default_avatar);
        }
        if (!TextUtils.isEmpty(userId)) {
            setOnClickListener((v) -> UINavigation.gotoCustomerDetailActivity(v.getContext(), userId));
        }
    }
}
