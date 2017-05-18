package com.xmd.technician.chat.chatview;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatImageCache;
import com.xmd.technician.chat.utils.ChatImageUtils;
import com.xmd.technician.chat.utils.EaseCommonUtils;
import com.xmd.technician.window.ShowBigImageActivity;

import java.io.File;

/**
 * Created by Lhj on 17-3-30.
 */

public class ChatRowImageView extends ChatRowFileView {

    protected ImageView imageView;
    private EMImageMessageBody imgBody;

    public ChatRowImageView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(mEMMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.chat_row_received_picture : R.layout.chat_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        mPercentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) mEMMessage.getBody();
        if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.icon08);
                setMessageReceiveCallback();
            } else {
                mProgressBar.setVisibility(View.GONE);
                mPercentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.icon08);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    // to make it compatible with thumbnail received in previous version
                    thumbPath = ChatImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                }
                showImageView(thumbPath, imageView, imgBody.getLocalUrl(), mEMMessage);
            }
            return;
        }
        String filePath = imgBody.getLocalUrl();
        String thumbPath = ChatImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, imageView, filePath, mEMMessage);
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(mContext, ShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            String msgId = mEMMessage.getMsgId();
            intent.putExtra("messageId", msgId);
            intent.putExtra("localUrl", imgBody.getLocalUrl().toString());
        }
        if (mEMMessage != null && mEMMessage.direct() == EMMessage.Direct.RECEIVE && !mEMMessage.isAcked()
                && mEMMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mEMMessage.getFrom(), mEMMessage.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mContext.startActivity(intent);
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @param
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ChatImageCache.getInstance().get(thumbernailPath);
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
                        return ChatImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return ChatImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), 160, 160);
                    } else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return ChatImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
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
                        ChatImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(mActivity)) {
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
}
