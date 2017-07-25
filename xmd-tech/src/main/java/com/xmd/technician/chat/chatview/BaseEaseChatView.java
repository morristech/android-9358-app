package com.xmd.technician.chat.chatview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.technician.Adapter.EaseMessageAdapter;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lhj on 17-3-30.
 */

public abstract class BaseEaseChatView extends LinearLayout {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected BaseAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected EMMessage mEMMessage;
    protected int mPosition;

    protected TextView mTimeStampView;
    protected CircleAvatarView mUserAvatarView;
    protected View mBubbleLayout;
    protected TextView mUserNickView;

    protected TextView mPercentageView;
    protected ProgressBar mProgressBar;
    protected ImageView mStatusView;
    protected Activity mActivity;

    protected TextView mAckedView;
    protected TextView mDeliveredView;

    protected EMCallBack mMessageSendCallBack;
    protected EMCallBack mMessageReceiveCallBack;

    protected EMessageListItemClickListener itemClickListener;

    protected String localUserAvatar;
    protected String remoteUserAvatar;

    public BaseEaseChatView(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.mContext = context;
        this.mEMMessage = message;
        this.mActivity = (Activity) context;
        this.mPosition = position;
        this.mAdapter = adapter;
        mInflater = LayoutInflater.from(mContext);
        initView();
    }

    void initView() {
        onInflateView();
        mTimeStampView = (TextView) findViewById(R.id.timestamp);
        mUserAvatarView = (CircleAvatarView) findViewById(R.id.avatar);
        mBubbleLayout = findViewById(R.id.bubble);
        mUserNickView = (TextView) findViewById(R.id.tv_userid);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStatusView = (ImageView) findViewById(R.id.msg_status);
        mAckedView = (TextView) findViewById(R.id.tv_ack);
        mDeliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();

    }

    public void setUpView(String localUserAvatar, String remoteUserAvatar, EMMessage message, int position, EMessageListItemClickListener itemClickListener) {
        this.mEMMessage = message;
        this.mPosition = position;
        this.itemClickListener = itemClickListener;
        this.localUserAvatar = localUserAvatar;
        this.remoteUserAvatar = remoteUserAvatar;

        setUpBaseView();
        setClickListener();
        onSetUpView();//FIXME  这里message更新了，但是没有传递到子视图
        onSetUpView(message);
    }

    private void setUpBaseView() {
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (mPosition == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(mEMMessage.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                EMMessage prevMessage = (EMMessage) mAdapter.getItem(mPosition - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(mEMMessage.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(mEMMessage.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //设置头像和nick
//        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
//            mUserAvatarView.setUserInfo(null, localUserAvatar);
//        } else {
//            mUserAvatarView.setUserInfo(ChatMessageFactory.create(mEMMessage).getUserId(), remoteUserAvatar);
//        }

        if (mDeliveredView != null) {
            if (mEMMessage.isDelivered()) {
                mDeliveredView.setVisibility(View.VISIBLE);
            } else {
                mDeliveredView.setVisibility(View.INVISIBLE);
            }
        }
        if (mAckedView != null) {
            if (mEMMessage.isAcked()) {
                if (mDeliveredView != null) {
                    mDeliveredView.setVisibility(View.INVISIBLE);
                }
                mAckedView.setVisibility(View.VISIBLE);
            } else {
                mAckedView.setVisibility(View.INVISIBLE);
            }
        }
        if (mAdapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) mAdapter).isShowAvatar()) {
                mUserAvatarView.setVisibility(View.VISIBLE);
            } else {
                mUserAvatarView.setVisibility(View.GONE);
            }

            if (mUserNickView != null) {
                if (((EaseMessageAdapter) mAdapter).isShowUserNick()) {
                    mUserNickView.setVisibility(View.VISIBLE);
                } else {
                    mUserNickView.setVisibility(View.GONE);
                }
            }

            if (mEMMessage.direct() == EMMessage.Direct.SEND) {
                if (((EaseMessageAdapter) mAdapter).getMyBubbleBg() != null) {
                    mBubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) mAdapter).getMyBubbleBg());
                }
            } else if (mEMMessage.direct() == EMMessage.Direct.RECEIVE) {
                if (((EaseMessageAdapter) mAdapter).getOtherBuddleBg() != null) {
                    mBubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) mAdapter).getOtherBuddleBg());
                }
            }
        }

    }

    protected void saveChatContact(String chatId, String msgId) {
        Map<String, String> saveParams = new HashMap<>();
        saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, chatId);
        saveParams.put(RequestConstant.KEY_CHAT_MSG_ID, msgId);
        saveParams.put(RequestConstant.KEY_SEND_POST, "0");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
    }

    /**
     * set callback for sending message
     */
    protected void setMessageSendCallback() {
        if (mMessageSendCallBack == null) {
            mMessageSendCallBack = new EMCallBack() {
                @Override
                public void onSuccess() {
                    saveChatContact(mEMMessage.getTo(), mEMMessage.getMsgId());
                    ChatHelper.getMessageSeting().add(mEMMessage.getMsgId());
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPercentageView != null)
                                mPercentageView.setText(progress + "%");
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    mEMMessage.setAttribute(ChatConstant.KEY_ERROR_CODE, (EMError.USER_PERMISSION_DENIED == code ? ChatConstant.ERROR_IN_BLACKLIST : ChatConstant.ERROR_SERVER_NOT_REACHABLE));
                    updateView(code, error);
                }
            };
        } else {
            if (!ChatHelper.getMessageSeting().contains(mEMMessage.getMsgId())) {
                saveChatContact(mEMMessage.getTo(), mEMMessage.getMsgId());
                ChatHelper.getMessageSeting().add(mEMMessage.getMsgId());
            }

        }
        mEMMessage.setMessageStatusCallback(mMessageSendCallBack);
    }

    /**
     * set callback for receiving message
     */
    protected void setMessageReceiveCallback() {
        if (mMessageReceiveCallBack == null) {
            mMessageReceiveCallBack = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (mPercentageView != null) {
                                mPercentageView.setText(progress + "%");
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        mEMMessage.setMessageStatusCallback(mMessageReceiveCallBack);
    }

    private void setClickListener() {
        if (mBubbleLayout != null) {
            mBubbleLayout.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    if (!itemClickListener.onBubbleClick(mEMMessage)) {
                        onBubbleClick();
                    }

                }
            });

            mBubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(mEMMessage);
                    }
                    return true;
                }
            });

        }
        if (mStatusView != null) {
            mStatusView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onResendClick(mEMMessage);
                }
            });
        }

//        if (mUserAvatarView != null) {
//            mUserAvatarView.setOnClickListener(v -> {
//                if (itemClickListener != null) {
//                    if (mEMMessage.direct() == EMMessage.Direct.SEND) {
//                        itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
//                    } else {
//                        itemClickListener.onUserAvatarClick(mEMMessage.getFrom());
//                    }
//                }
//            });
//
//            mUserAvatarView.setOnLongClickListener(new OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    if (itemClickListener != null) {
//                        if (mEMMessage.direct() == EMMessage.Direct.SEND) {
//                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
//                        } else {
//                            itemClickListener.onUserAvatarLongClick(mEMMessage.getFrom());
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
    }

    protected void updateView() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mEMMessage.status() == EMMessage.Status.FAIL) {
                    //发送消息失败，请检查网络，稍后重试
                    Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.network_anomaly));
                }

                onUpdateView();
            }
        });
    }

    protected void updateView(final int errorCode, final String desc) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Logger.e("9358", "errorCode>>" + errorCode);
                if (errorCode == EMError.USER_NOT_LOGIN) {
                    Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.user_not_login));
                } else if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.user_login_on_another_device));
                } else if (errorCode == EMError.USER_PERMISSION_DENIED) {
                    //    Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.permission_anomaly));
                } else {
                    Utils.makeShortToast(mContext, ResourceUtils.getString(R.string.network_anomaly));
                }
                onUpdateView();
            }
        });
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh list view when message status change
     */
    protected abstract void onUpdateView();

    /**
     * setup view
     */
    @Deprecated
    protected void onSetUpView() {
    }

    ;

    protected void onSetUpView(EMMessage message) {

    }

    /**
     * on bubble clicked
     */
    protected abstract void onBubbleClick();
}
