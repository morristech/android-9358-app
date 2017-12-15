package com.xmd.chat.viewmodel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
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
import com.xmd.chat.ChatAccountManager;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.ChatSettingManager;
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
                progress.set(false);
                error.set(false);
                if(ChatMessage.MSG_TAG_HELLO.equals(chatMessage.getTag())){
                    return;
                }
                String msgType =chatMessage.getMsgType();
                //通知服务器有新的消息
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .notifyServerChatMessage(
                                ChatAccountManager.getInstance().getChatId(),
                                ChatAccountManager.getInstance().getUserType(),
                                chatMessage.getRemoteChatId(),
                                UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getRemoteChatId()).getUserType(),
                                chatMessage.getEmMessage().getMsgId(),
                                msgType, chatMessage.getContentText().toString());
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
                //当技师被用户拉入黑名单时不进行错误提示
                switch (i) {
                    case 201:
                        XToast.show("聊天帐号登录失败，请重新登录");
                        progress.set(false);
                        error.set(true);
                        break;
                    case 210:
                        ChatSettingManager.getInstance().judgeInCustomerBlack(chatMessage.getToChatId(), true);
                        chatMessage.getEmMessage().setStatus(EMMessage.Status.SUCCESS);
                        progress.set(false);
                        error.set(false);
                        break;
                    default:
                        XToast.show("发送失败：" + s);
                        progress.set(false);
                        error.set(true);
                        break;
                }
            }

            @Override
            public void onProgress(int i, String s) {
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
                if (ChatSettingManager.getInstance().isInCustomerBlackList(chatMessage.getToChatId())) {
                    error.set(false);
                } else {
                    error.set(true);
                }

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
        textView.setText(data.getChatMessage().getChatRelativeTime());
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void reSend() {
        error.set(false);
        progress.set(true);
        ChatMessageManager.getInstance().resendMessage(chatMessage);
    }

    public boolean isReceiveMessage() {
        return chatMessage.isReceivedMessage();
    }

    public String getInnerProcessed() {
        return chatMessage.getInnerProcessed();
    }

    protected void addMenuItems(Menu menu) {
        menu.add(Menu.NONE, R.id.menu_copy, Menu.NONE, "复制");
        menu.add(Menu.NONE, R.id.menu_delete, Menu.NONE, "删除");
        if (!isReceiveMessage()) {
            menu.add(Menu.NONE, R.id.menu_revoke, Menu.NONE, "撤回");
        }
    }

    public boolean onLongClick(final View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        addMenuItems(popupMenu.getMenu());
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
                            ChatMessageManager.getInstance().removeMessage(chatMessage);
                            EventBus.getDefault().post(new EventDeleteMessage(ChatRowViewModel.this));
                        }
                    })
                    .create()
                    .show();
            return true;
        } else if (i == R.id.menu_revoke) {
            //撤回
            EventBus.getDefault().post(new EventRevokeMessage(this));
            return true;
        } else if (i == R.id.menu_copy) {
            //复制
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("", getCopyData());
            cm.setPrimaryClip(clipData);
            XToast.show("信息已复制");
            return true;
        }
        return false;
    }

    protected CharSequence getCopyData() {
        return chatMessage.getContentText();
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
