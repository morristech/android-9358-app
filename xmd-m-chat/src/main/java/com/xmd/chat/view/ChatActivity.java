package com.xmd.chat.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.ExCommonRecyclerViewAdapter;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.BR;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMenu;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.XmdChat;
import com.xmd.chat.databinding.ChatActivityBinding;
import com.xmd.chat.event.EventDeleteMessage;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventRevokeMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity {
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private ChatActivityBinding mBinding;

    private List<ChatRowViewModel> mDataList = new ArrayList<>();

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User mRemoteUser;

    private final int PAGE_SIZE = 20;

    private ImageView mFocusMenuView;

    public ObservableField<String> textMessageContent = new ObservableField<>();
    public ObservableBoolean showSubMenu = new ObservableBoolean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.chat_activity);

        String chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        if (TextUtils.isEmpty(chatId)) {
            XToast.show("必须传入聊天ID!");
            finish();
            return;
        }

        mRemoteUser = userInfoService.getUserByChatId(chatId);
        if (mRemoteUser == null) {
            XToast.show("无法找到用户信息!");
            finish();
            return;
        }

        setTitle(mRemoteUser.getShowName());

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(mAdapter.getDataList().get(0).getChatMessage().getEmMessage().getMsgId());
                mBinding.refreshLayout.setRefreshing(false);
            }
        });

        mBinding.setData(this);

        mBinding.pageIndicator.setViewPager(mBinding.submenuLayout, R.drawable.chat_menu_indicator_normal, R.drawable.chat_menu_indicator_focus);
        mBinding.submenuLayout.addOnPageChangeListener(mBinding.pageIndicator);
        initMenu();

        loadData(null);
    }

    private EMConversation getConversation() {
        return EMClient.getInstance().chatManager().getConversation(mRemoteUser.getChatId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!XmdChat.getInstance().getMenuFactory().onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadData(String msgId) {
        EMConversation conversation = getConversation();
        if (conversation == null || conversation.getAllMsgCount() == 0) {
            mAdapter.setData(BR.data, mDataList);
            mAdapter.notifyDataSetChanged();
            return;
        }

        List<ChatRowViewModel> newDataList = new ArrayList<>();
        List<EMMessage> messageList = conversation.loadMoreMsgFromDB(msgId, PAGE_SIZE);
        if (msgId != null && messageList.size() == 0) {
            XToast.show("没有更多消息了");
            return;
        }
        ChatRowViewModel beforeData = null;
        for (EMMessage message : messageList) {
            ChatRowViewModel data = ChatRowViewFactory.createViewModel(ChatMessageFactory.get(message));
            setShowTime(beforeData, data);
            beforeData = data;
            newDataList.add(data);
        }
        if (mDataList.size() > 0 && newDataList.size() > 0) {
            beforeData = newDataList.get(newDataList.size() - 1);
            ChatRowViewModel current = mDataList.get(0);
            setShowTime(beforeData, current);
        }
        mDataList.addAll(0, newDataList);
        mAdapter.setData(BR.data, mDataList);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.scrollToPosition(newDataList.size() - 1);
    }


    private ExCommonRecyclerViewAdapter<ChatRowViewModel> mAdapter = new ExCommonRecyclerViewAdapter<ChatRowViewModel>() {

        @Override
        public int getViewType(int position) {
            ChatRowViewModel data = getDataList().get(position);
            return ChatRowViewFactory.getViewType(data.getChatMessage());
        }

        @Override
        public ViewDataBinding createViewDataBinding(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ViewDataBinding binding;
            binding = DataBindingUtil.inflate(layoutInflater, ChatRowViewFactory.getWrapperLayout(viewType), parent, false);
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            layout.addView(ChatRowViewFactory.createView(parent, viewType));
            return binding;
        }

        @Override
        public void onDataBinding(ViewDataBinding binding, int position) {
            ChatRowViewModel data = getDataList().get(position);
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            data.onBindView(layout.getChildAt(0));
        }

        @Override
        public void onDataUnBinding(ViewDataBinding binding, int position) {
//            ChatRowViewModel data = getDataList().get(position);
//            data.onUnbindView();
        }
    };

    //处理新消息
    @Subscribe
    public void onNewMessages(EventNewMessages newMessages) {
        for (EMMessage message : newMessages.getList()) {
            if (message.getFrom().equals(mRemoteUser.getChatId())) {
                addNewChatMessageToUi(ChatMessageFactory.get(message));
            }
        }
    }

    //处理删除消息
    @Subscribe
    public void onDeleteMessage(final EventDeleteMessage deleteMessage) {
        new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                .setMessage("删除后将不会出现在你的消息记录中，确实删除？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getConversation().removeMessage(deleteMessage.getChatRowViewModel().getChatMessage().getEmMessage().getMsgId());
                        removeMessageFromUi(deleteMessage.getChatRowViewModel());
                    }
                })
                .create()
                .show();
    }

    //处理撤回消息
    @Subscribe
    public void onRevokeMessage(EventRevokeMessage event) {
        ChatMessage revokeMsg = event.getChatRowViewModel().getChatMessage();
        long originMsgTime = revokeMsg.getEmMessage().getMsgTime();
        if (System.currentTimeMillis() - originMsgTime > ChatConstants.REVOKE_LIMIT_TIME) {
            XToast.show("发送时间超过" + ChatConstants.REVOKE_LIMIT_TIME / 1000 / 60 + "分钟的消息不能被撤回");
            return;
        }
        //移除原来消息
        int index = mDataList.indexOf(event.getChatRowViewModel());

        String msgId = revokeMsg.getEmMessage().getMsgId();
        getConversation().removeMessage(msgId);
        removeMessageFromUi(event.getChatRowViewModel());
        //发送撤回命令
        MessageManager.getInstance().sendRevokeMessage(mRemoteUser.getChatId(), msgId);
        //增加提示信息
        ChatMessage chatMessage = MessageManager.getInstance().sendTipMessage(getConversation(), mRemoteUser, "你撤回了一条消息");
        chatMessage.getEmMessage().setMsgTime(revokeMsg.getEmMessage().getMsgTime());
        insertNewChatMessageToUi(index, chatMessage);
    }

    /**
     * 设置是否需要显示时间
     *
     * @param before  前一个汽泡
     * @param current 当前汽泡
     */
    private void setShowTime(ChatRowViewModel before, ChatRowViewModel current) {
        if (before == null) {
            current.showTime.set(true);
            return;
        }
        current.showTime.set(current.getTime() - before.getTime() > ChatConstants.TIME_SHOW_INTERVAL);
    }

    //设置要发送的文本消息
    public void setTextMessageContent(Editable s) {
        textMessageContent.set(s.toString());
    }

    public void sendTextMessage() {
        ChatMessage chatMessage = MessageManager.getInstance().sendTextMessage(mRemoteUser.getChatId(), textMessageContent.get());
        if (chatMessage != null) {
            addNewChatMessageToUi(chatMessage);
            textMessageContent.set(null);
            mBinding.sendEditText.getText().clear();
        }
    }

    //增加一条消息到列表
    public void addNewChatMessageToUi(ChatMessage chatMessage) {
        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        setShowTime(mDataList.size() > 0 ? mDataList.get(mDataList.size() - 1) : null, data);
        mDataList.add(data);
        mAdapter.notifyItemInserted(mDataList.size() - 1);
        mBinding.recyclerView.scrollToPosition(mDataList.size() - 1);
    }

    //增加一条消息到列表
    public void insertNewChatMessageToUi(int position, ChatMessage chatMessage) {
        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        mDataList.add(position, data);
        setShowTime(position - 1 >= 0 ? mDataList.get(position - 1) : null, data);
        mAdapter.notifyItemInserted(position);
    }

    //从列表删除一条消息
    public void removeMessageFromUi(ChatRowViewModel chatRowViewModel) {
        int index = mDataList.indexOf(chatRowViewModel);
        mDataList.remove(index);
        if (mDataList.size() > index) {
            setShowTime(index - 1 >= 0 ? mDataList.get(index - 1) : null, mDataList.get(index));
        }
        mAdapter.notifyItemRemoved(index);
    }

    //初始化菜单
    public void initMenu() {
        List<ChatMenu> chatMenuList = XmdChat.getInstance().getMenuFactory()
                .createMenuList(this, mRemoteUser, mBinding.sendEditText.getText());
        for (final ChatMenu chatMenu : chatMenuList) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(chatMenu.icon);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatMenu.subMenuList != null && chatMenu.subMenuList.size() > 0) {
                        showSubMenu(imageView, chatMenu);
                    } else {
                        if (mFocusMenuView != null) {
                            mFocusMenuView.setSelected(false);
                            mFocusMenuView = null;
                            showSubMenu.set(false);
                        }
                    }
                    if (chatMenu.listener != null) {
                        chatMenu.listener.onClick(v);
                    }
                }
            });
            mBinding.menuLayout.addView(imageView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(24);
            layoutParams.height = ScreenUtils.dpToPx(24);
            layoutParams.leftMargin = ScreenUtils.dpToPx(8);
            layoutParams.rightMargin = ScreenUtils.dpToPx(8);
        }
    }

    public void showSubMenu(ImageView menuView, final ChatMenu chatMenu) {
        if (mFocusMenuView != null) {
            mFocusMenuView.setSelected(false);
        }
        if (mFocusMenuView == menuView) {
            mFocusMenuView = null;
            showSubMenu.set(false);
            return;
        }

        mBinding.submenuLayout.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return chatMenu.subMenuList.get(position);
            }

            @Override
            public int getCount() {
                return chatMenu.subMenuList.size();
            }
        });
        if (chatMenu.subMenuList.size() > 1) {
            mBinding.pageIndicator.setVisibility(View.VISIBLE);
            mBinding.pageIndicator.drawIcons(chatMenu.subMenuList.size());
        } else {
            mBinding.pageIndicator.setVisibility(View.GONE);
        }

        mFocusMenuView = menuView;
        mFocusMenuView.setSelected(true);
        showSubMenu.set(true);
        mBinding.recyclerView.scrollToPosition(mDataList.size() - 1);
    }
}
