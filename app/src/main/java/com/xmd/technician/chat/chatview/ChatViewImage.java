package com.xmd.technician.chat.chatview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.ImageUtils;
import com.xmd.technician.R;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.common.Util;
import com.xmd.technician.bean.ImageCache;
import com.xmd.technician.window.ShowBigImageActivity;

import java.io.File;

/**
 * Created by sdcm on 16-4-11.
 */
public class ChatViewImage extends BaseChatView {

    protected ImageView imageView;
    private EMImageMessageBody imgBody;
    protected EMCallBack messageReceiveCallback;

    public ChatViewImage(Context context, EMMessage.Direct direct) {
        super(context, direct);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(mDirect == EMMessage.Direct.RECEIVE ?
                R.layout.chat_received_item : R.layout.chat_sent_item, this);
    }

    @Override
    protected void onFindViewById() {
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setVisibility(VISIBLE);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowBigImageActivity.class);
                File file = new File(imgBody.getLocalUrl());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    intent.putExtra("uri", uri);
                } else {
                    // The local full size pic does not exist yet.
                    // ShowBigImage needs to download it from the server
                    // first
                    intent.putExtra("secret", imgBody.getSecret());
                    intent.putExtra("remotepath", imgBody.getRemoteUrl());
                    intent.putExtra("localUrl", imgBody.getLocalUrl());
                }
                if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                        && message.getChatType() == EMMessage.ChatType.Chat) {
                    try {
                        EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();
        // 接收方向的消息
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.icon08);
                setMessageReceiveCallback();
            } else {
                imageView.setImageResource(R.drawable.icon08);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    // 兼容旧版SDK收到的thumbnail
                    thumbPath = CommonUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                }
                showImageView(thumbPath, imageView, imgBody.getLocalUrl(), message);
            }
            return;
        }

        String filePath = imgBody.getLocalUrl();
        String thumbPath = CommonUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, imageView, filePath, message);
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return ImageUtils.decodeScaleImage(thumbernailPath, 240, 240);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return ImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), 240, 240);
                    }
                    else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return ImageUtils.decodeScaleImage(localFullSizePath, 240, 240);
                            } else {

                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        iv.setImageBitmap(image);
                        ImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (Util.isNetWorkConnected(activity)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }

    protected void updateView() {
    }

    /**
     * 设置消息接收callback
     */
    protected void setMessageReceiveCallback(){
        if(messageReceiveCallback == null){
            messageReceiveCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }

}
