package com.xmd.manager.chat.chatview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.util.DateUtils;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseChatView extends LinearLayout {

    protected LayoutInflater inflater;
    protected Context context;
    protected EMMessage message;
    protected EMMessage.Direct mDirect;

    protected TextView timeStampView;
    protected ImageView userAvatarView;

    private ProgressBar progressBar;
    private ImageView statusView;
    private Map<String, String> mParams;
    protected Activity activity;
    private EMCallBack messageSendCallback;

    public BaseChatView(Context context, EMMessage.Direct direct) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.mDirect = direct;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.time);
        userAvatarView = (ImageView) findViewById(R.id.avatar);
        progressBar = (ProgressBar) findViewById(R.id.status_progressbar);
        statusView = (ImageView) findViewById(R.id.status_failed);
        onFindViewById();
    }

    private void sendMessageCallBack(EMMessage emMessage) {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, emMessage.getTo());
        mParams.put(RequestConstant.KEY_CHAT_MSG_ID, emMessage.getMsgId());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, mParams);
    }

    /**
     * 根据当前message和position设置控件属性等
     *
     * @param message
     */
    public void setupView(EMMessage message) {
        this.message = message;
        setupBaseView();
        onSetupView();
        setupMessageStatusView();
    }

    private void setupBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.time);
        if (timestamp != null) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        }
        //设置头像和nick
        if (message.direct() == Direct.RECEIVE) {
            EmchatUserHelper.setUserAvatarAndNick(context, message.getFrom(), userAvatarView, null);
        } else {
            Glide.with(context).load(SharedPreferenceHelper.getUserAvatar()).into(userAvatarView);
        }
    }

    private void setupMessageStatusView() {
        if (message.direct() == Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
//                sendMsgInBackground(message);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置消息发送callback
     */
    protected void setMessageSendCallback() {
        if (messageSendCallback == null) {
            messageSendCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    sendMessageCallBack(message);
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
        message.setMessageStatusCallback(messageSendCallback);
    }

    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                setupMessageStatusView();
            }
        });
    }

    /**
     * 填充layout
     */
    protected void onInflateView() {
        LayoutInflater.from(context).inflate(mDirect == EMMessage.Direct.RECEIVE ?
                R.layout.chat_received_item : R.layout.chat_sent_item, this);
    }

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetupView();
}
