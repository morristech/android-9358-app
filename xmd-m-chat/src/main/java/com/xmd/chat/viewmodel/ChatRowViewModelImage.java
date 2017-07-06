package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hyphenate.chat.EMImageMessageBody;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowImageBinding;
import com.xmd.chat.message.ChatMessage;

import java.io.File;


/**
 * Created by mo on 17-7-1.
 * 文本消息
 */

public class ChatRowViewModelImage extends ChatRowViewModel {
    public ObservableBoolean loading = new ObservableBoolean();
    public static int maxWidth;
    public static int maxHeight;

    public ChatRowViewModelImage(ChatMessage chatMessage) {
        super(chatMessage);

    }

    public static View createView(ViewGroup parent) {
        ChatRowImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_image, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onBindView(View view) {
        ChatRowImageBinding binding = DataBindingUtil.getBinding(view);
        binding.setData(this);
    }

    @Override
    public void onUnbindView() {

    }

    public void onClickImage(View v) {
        if (loading.get()) {
            return;
        }
        ImageView imageView = new ImageView(v.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        AlertDialog dialog = new AlertDialog
                .Builder(v.getContext(), R.style.AppTheme_Dialog_Alert)
                .setView(imageView)
                .create();
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = ScreenUtils.getScreenWidth();
        lp.height = ScreenUtils.getScreenHeight();
        lp.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        dialog.getWindow().setAttributes(lp);
        imageView.getLayoutParams().width = lp.width;
        imageView.getLayoutParams().height = lp.height;
        EMImageMessageBody body = (EMImageMessageBody) chatMessage.getEmMessage().getBody();
        if (new File(body.getLocalUrl()).exists()) {
            Glide.with(v.getContext()).load(body.getLocalUrl()).into(imageView);
        } else {
            Glide.with(v.getContext()).load(body.getRemoteUrl()).into(imageView);
        }
    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, final ChatRowViewModelImage data) {
        if (maxWidth == 0) {
            maxWidth = ScreenUtils.getScreenWidth() >> 1;
            maxHeight = ScreenUtils.getScreenHeight() >> 2;
        }
        EMImageMessageBody body = (EMImageMessageBody) data.chatMessage.getEmMessage().getBody();
        int w = body.getWidth();
        int h = body.getHeight();
        if (w > h && w > maxWidth) {
            h = w * h / maxWidth;
            w = maxWidth;
        } else if (h > w && h > ScreenUtils.getScreenHeight() / 2) {
            w = w * h / maxHeight;
            h = maxHeight;
        }
        if (data.chatMessage.isReceivedMessage()) {
            if (body.thumbnailDownloadStatus().equals(EMImageMessageBody.EMDownloadStatus.SUCCESSED)) {
                Glide.with(imageView.getContext()).load(body.thumbnailLocalPath()).override(w, h).into(imageView);
            } else {
                data.loading.set(true);
                Glide.with(imageView.getContext()).load(body.getThumbnailUrl()).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        data.loading.set(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        data.loading.set(false);
                        return false;
                    }
                }).override(w, h).into(imageView);
            }
        } else {
            if (body.getLocalUrl() != null) {
                Glide.with(imageView.getContext()).load(body.getLocalUrl()).override(w, h).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.chat_default_image);
            }
        }
    }
}
