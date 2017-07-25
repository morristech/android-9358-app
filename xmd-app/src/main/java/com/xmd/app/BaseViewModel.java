package com.xmd.app;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.app.user.User;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.app.widget.GlideCircleTransform;

/**
 * Created by mo on 17-7-11.
 * 提供基础的数据绑定
 */

public class BaseViewModel {

    @BindingAdapter("circle_image")
    public static void bindCircleImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_default_avatar);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(R.drawable.img_default_avatar)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .into(imageView);
        }
    }

    @BindingAdapter("user")
    public static void bindAvatar(CircleAvatarView imageView, User user) {
        imageView.setUserInfo(user);
    }

    @BindingAdapter("avatar")
    public static void bindAvatar(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_default_avatar);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(R.drawable.img_default_avatar)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .into(imageView);
        }
    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_default);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(R.drawable.img_default)
                    .into(imageView);
        }
    }
}
