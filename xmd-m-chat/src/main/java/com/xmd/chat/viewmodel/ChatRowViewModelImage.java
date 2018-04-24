package com.xmd.chat.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
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
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatRowImageBinding;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.xmdchat.model.XmdChatModel;

import java.io.File;
import java.util.List;


/**
 * Created by mo on 17-7-1.
 * 图片消息
 */

public class ChatRowViewModelImage extends ChatRowViewModel {
    public ObservableBoolean loading = new ObservableBoolean();
    public static int maxWidth;
    public static int maxHeight;

    public ChatRowViewModelImage(ChatMessage chatMessage) {
        super(chatMessage);
        if (maxWidth == 0) {
            maxWidth = ScreenUtils.getScreenWidth() >> 1;
            maxHeight = ScreenUtils.getScreenHeight() >> 2;
        }
    }

    public static View createView(ViewGroup parent) {
        ChatRowImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chat_row_image, parent, false);
        return binding.getRoot();
    }

    @Override
    public ViewDataBinding onBindView(View view) {
        if (chatMessage.getMessage() instanceof TIMMessage && ((TIMMessage) chatMessage.getMessage()).status() == TIMMessageStatus.HasRevoked) {
            return null;
        }
        ChatRowImageBinding binding = DataBindingUtil.getBinding(view);
        int width = 0;
        int height = 0;
        ViewGroup.LayoutParams lp = binding.imageView.getLayoutParams();
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMImageMessageBody body = (EMImageMessageBody) ((EMMessage) chatMessage.getMessage()).getBody();
            width = body.getWidth();
            height = body.getHeight();
        } else {
            TIMMessage timMessage = (TIMMessage) chatMessage.getMessage();
            if (timMessage.getElement(1) instanceof TIMImageElem) {
                TIMImageElem elem = (TIMImageElem) timMessage.getElement(1);
                List<TIMImage> timImage = elem.getImageList();
                if (timImage.size() >= 2) {
                    height = (int) timImage.get(1).getHeight();
                    width = (int) timImage.get(1).getWidth();
                } else if (timImage.size() == 1) {
                    height = (int) timImage.get(0).getHeight();
                    width = (int) timImage.get(0).getWidth();
                } else {
                    XLogger.e("imageList.size is 0!");
                    width = maxWidth;
                    height = maxHeight;
                }
            } else {
                return null;
            }

        }
        if (width > height && width > maxWidth) {
            lp.height = maxWidth * height / width;
            lp.width = maxWidth;
        } else if (height > width && height > maxHeight) {
            lp.width = width * maxHeight / height;
            lp.height = maxHeight;
        } else {
            lp.width = width;
            lp.height = height;
        }
        binding.setData(this);
        return binding;
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
        final AlertDialog dialog = new AlertDialog
                .Builder(v.getContext(), R.style.AppTheme_Dialog_Alert)
                .setView(imageView)
                .create();
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = ScreenUtils.getScreenWidth();
        lp.height = ScreenUtils.getScreenHeight();
        lp.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        imageView.getLayoutParams().width = lp.width;
        imageView.getLayoutParams().height = lp.height;
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMImageMessageBody body = (EMImageMessageBody) ((EMMessage) chatMessage.getMessage()).getBody();
            if (new File(body.getLocalUrl()).exists()) {
                Glide.with(v.getContext()).load(body.getLocalUrl()).into(imageView);
            } else {
                Glide.with(v.getContext()).load(body.getRemoteUrl()).into(imageView);
            }
        } else {
            TIMMessage message = (TIMMessage) chatMessage.getMessage();
            TIMImageElem imageElem = (TIMImageElem) message.getElement(1);
            List<TIMImage> imageList = imageElem.getImageList();
            if (imageList.size() == 0) {
                return;
            }
            TIMImage image = imageList.get(0);
            if (new File(imageElem.getPath()).exists()) {
                Glide.with(v.getContext()).load(imageElem.getPath()).into(imageView);
            } else {
                Glide.with(v.getContext()).load(image.getUrl()).into(imageView);
            }
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @BindingAdapter("image")
    public static void bindImage(ImageView imageView, final ChatRowViewModelImage data) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMImageMessageBody body = (EMImageMessageBody) ((EMMessage) data.chatMessage.getMessage()).getBody();
            if (data.chatMessage.isReceivedMessage()) {
                if (body.thumbnailDownloadStatus().equals(EMImageMessageBody.EMDownloadStatus.SUCCESSED)) {
                    Glide.with(imageView.getContext()).load(body.thumbnailLocalPath()).into(imageView);
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
                    }).into(imageView);
                }
            } else {
                if (body.getLocalUrl() != null) {
                    Glide.with(imageView.getContext()).load(body.getLocalUrl()).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.chat_default_image);
                }
            }
        } else {
            if (data == null || data.chatMessage == null) {
                Glide.with(imageView.getContext()).load(R.drawable.chat_default_image).into(imageView);
                return;
            }
            TIMMessage message = (TIMMessage) data.chatMessage.getMessage();
            TIMImageElem imageElem = (TIMImageElem) message.getElement(1);
            List<TIMImage> imageList = imageElem.getImageList();
            if (imageList.size() > 1) {
                TIMImage image = imageList.get(1);
                Glide.with(imageView.getContext()).load(image.getUrl()).into(imageView);
            } else {
                XLogger.e("imageList.size is 0!");
            }
        }

    }

}
