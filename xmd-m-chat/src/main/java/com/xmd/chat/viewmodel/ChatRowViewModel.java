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

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseViewModel;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.R;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.xmdchat.contract.XmdChatRowViewModelInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmChatRowViewModelPresent;
import com.xmd.chat.xmdchat.present.ImChatRowViewModelPresent;

import org.greenrobot.eventbus.EventBus;

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
    private XmdChatRowViewModelInterface viewModelInterface;
    public ChatRowViewModel(final ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        if(XmdChatModel.getInstance().chatModelIsEm()){
            viewModelInterface = new EmChatRowViewModelPresent();
        }else{
            viewModelInterface = new ImChatRowViewModelPresent();
        }
        viewModelInterface.init(chatMessage,progress,error,showTime);
    }

    public long getTime() {
        return viewModelInterface.getTime();
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
           // onUnbindView();
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
