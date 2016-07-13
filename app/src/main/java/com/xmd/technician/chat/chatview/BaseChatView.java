package com.xmd.technician.chat.chatview;

import java.util.Date;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.window.BaseFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseChatView extends LinearLayout {

    protected LayoutInflater inflater;
    protected Context context;
    protected EMMessage message;
    protected EMMessage.Direct mDirect;

    protected TextView timeStampView;
    protected ImageView userAvatarView;

    private ProgressBar progressBar;
    private ImageView statusView;

    protected Activity activity;
    private EMCallBack messageSendCallback;

    protected EMessageListItemClickListener itemClickListener;

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

    /**
     * 根据当前message和position设置控件属性等
     *
     * @param message
     */
    public void setUpView(EMMessage message) {
        this.message = message;

        setUpBaseView();
        onSetUpView();
        setupMessageStatusView();
        setClickListener();
    }

    public void setUpView(EMMessage message, EMessageListItemClickListener listener) {
        this.itemClickListener = listener;

        setUpView(message);
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.time);
        if (timestamp != null) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        }
        //设置头像和nick
        if(message.direct() == Direct.SEND){
            UserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
        }else{
            UserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
        }
    }

    private void setupMessageStatusView() {
        if(message.direct() == Direct.SEND){
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
    protected void setMessageSendCallback(){
        if(messageSendCallback == null){
            messageSendCallback = new EMCallBack() {

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
        message.setMessageStatusCallback(messageSendCallback);
    }

    private void setClickListener(){
        if (statusView != null) {
            statusView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onResendClick(message);
                }
            });
        }
    }

    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status() == EMMessage.Status.FAIL) {
                    if (message.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                        ((BaseFragmentActivity)activity).makeShortToast(activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content));
                    } else {
                        ((BaseFragmentActivity)activity).makeShortToast(activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast));
                    }
                }
                setupMessageStatusView();
            }
        });

    }

    /**
     * 填充layout
     */
    protected void onInflateView(){
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
    protected abstract void onSetUpView();
}
