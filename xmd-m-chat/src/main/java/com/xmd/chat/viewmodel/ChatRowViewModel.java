package com.xmd.chat.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseViewModel;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.AccountManager;
import com.xmd.chat.MessageManager;
import com.xmd.chat.NetService;
import com.xmd.chat.R;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;

/**
 * Created by mo on 17-6-30.
 * 用于绑定基本聊天视图
 */

public abstract class ChatRowViewModel extends BaseViewModel {
    protected ChatMessage chatMessage;

    public ObservableBoolean progress = new ObservableBoolean();
    public ObservableBoolean error = new ObservableBoolean();
    public ObservableBoolean showTime = new ObservableBoolean();

    private int paddingWeight = 100;
    private int contentWeight = 0;

    public ChatRowViewModel(final ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        chatMessage.getEmMessage().setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                XLogger.d("onSuccess -- " + getChatMessage().getContentText());
                progress.set(false);
                error.set(false);
                String msgType;
                if (!chatMessage.getEmMessage().getType().equals(EMMessage.Type.TXT)) {
                    msgType = "text";
                } else {
                    msgType = chatMessage.getMsgType();
                    if (msgType.equals(ChatMessage.MSG_TYPE_ORIGIN_TXT)) {
                        msgType = "text";
                    }
                }
                //通知服务器有新的消息
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .notifyServerChatMessage(
                                AccountManager.getInstance().getChatId(),
                                AccountManager.getInstance().getUserType(),
                                chatMessage.getRemoteChatId(),
                                UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getRemoteChatId()).getUserType(),
                                chatMessage.getEmMessage().getMsgId(),
                                msgType);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        XLogger.d("notifyServerChatMessage success");
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XLogger.d("notifyServerChatMessage failed:" + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                XLogger.d("onError -- " + getChatMessage().getContentText());
                XToast.show("发送失败：" + s);
                progress.set(false);
                error.set(true);
            }

            @Override
            public void onProgress(int i, String s) {
                XLogger.d("onProgress -- " + getChatMessage().getContentText());
                progress.set(true);
            }
        });

        error.set(false);
        progress.set(false);
        EMMessage.Status status = chatMessage.getEmMessage().status();
        switch (status) {
            case SUCCESS:
                error.set(false);
                break;
            case FAIL:
                error.set(true);
                break;
            case CREATE:
            case INPROGRESS:
                progress.set(true);
                break;
        }
    }

    public long getTime() {
        return chatMessage.getEmMessage().getMsgTime();
    }

    public Drawable getContentViewBackground(Context context) {
        return chatMessage.isReceivedMessage() ?
                context.getResources().getDrawable(R.drawable.receive_wrapper)
                : context.getResources().getDrawable(R.drawable.send_wrapper);
    }

    public String getAvatar() {
        return getChatMessage().getUserAvatar();
    }

    @BindingAdapter("time")
    public static void bindTime(TextView textView, ChatRowViewModel data) {
        textView.setText(data.getChatMessage().getFormatTime());
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void reSend() {
        error.set(false);
        progress.set(true);
        MessageManager.getInstance().resendMessage(chatMessage);
    }

    public boolean isReceiveMessage() {
        return chatMessage.isReceivedMessage();
    }

    public String getInnerProcessed() {
        return chatMessage.getInnerProcessed();
    }

    protected int getMenuResource() {
        return chatMessage.isReceivedMessage() ? R.menu.message_receive : R.menu.message_send;
    }

    public boolean onLongClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(getMenuResource());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onClickMenu(view.getContext(), item);
            }
        });
        popupMenu.show();
        return true;
    }

    protected boolean onClickMenu(Context context, MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_delete) {
            new AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                    .setMessage("删除后将不会出现在你的消息记录中，确实删除？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MessageManager.getInstance().removeMessage(chatMessage);
                            EventBus.getDefault().post(new EventDeleteMessage(ChatRowViewModel.this));
                        }
                    })
                    .create()
                    .show();
            return true;
        } else if (i == R.id.menu_revoke) {
            EventBus.getDefault().post(new EventRevokeMessage(this));
            return true;
        }
        return false;
    }


    //绑定子view
    public void bindSubView(View view) {
        contentWeight = contentViewMatchParent() ? 1 : 0;
        ViewDataBinding binding = onBindView(view);
        if (binding != null) {
            binding.executePendingBindings();
        }
    }

    public int getContentWeight() {
        return contentWeight;
    }

    //子view是否要match_parent
    protected boolean contentViewMatchParent() {
        return false;
    }

    public abstract ViewDataBinding onBindView(View view);

    public abstract void onUnbindView();

    public User getUser() {
        return UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getRemoteChatId());
    }

    @BindingAdapter("android:layout_weight")
    public static void bindWeight(View view, int weight) {
        ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = weight;
    }
}
