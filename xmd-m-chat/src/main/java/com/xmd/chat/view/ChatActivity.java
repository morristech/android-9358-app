package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseActivity;
import com.xmd.app.ExCommonRecyclerViewAdapter;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.BR;
import com.xmd.chat.ChatConstants;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.MessageManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.ChatActivityBinding;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.BaseChatRowViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends BaseActivity {
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private ChatActivityBinding mBinding;

    private List<BaseChatRowViewModel> mDataList = new ArrayList<>();

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    private User mRemoteUser;

    private EMConversation mConversation;
    private final int PAGE_SIZE = 20;
    private int mPageIndex;

    public ObservableField<String> textMessageContent = new ObservableField<>();

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

        mConversation = EMClient.getInstance().chatManager().getConversation(chatId);
        loadData(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadData(String msgId) {
        if (mConversation.getAllMsgCount() == 0) {
            return;
        }


        List<BaseChatRowViewModel> newDataList = new ArrayList<>();
        List<EMMessage> messageList = mConversation.loadMoreMsgFromDB(msgId, PAGE_SIZE);
        if (msgId != null && messageList.size() == 0) {
            XToast.show("没有更多消息了");
            return;
        }
        BaseChatRowViewModel beforeData = null;
        for (EMMessage message : messageList) {
            BaseChatRowViewModel data = ChatRowViewFactory.createViewModel(ChatMessageFactory.get(message));
            setShowTime(beforeData, data);
            beforeData = data;
            newDataList.add(data);
        }
        if (mDataList.size() > 0 && newDataList.size() > 0) {
            beforeData = newDataList.get(newDataList.size() - 1);
            BaseChatRowViewModel current = mDataList.get(0);
            setShowTime(beforeData, current);
        }
        mDataList.addAll(0, newDataList);
        mAdapter.setData(BR.data, mDataList);
        mAdapter.notifyDataSetChanged();
        mBinding.recyclerView.scrollToPosition(newDataList.size() - 1);
    }


    private ExCommonRecyclerViewAdapter<BaseChatRowViewModel> mAdapter = new ExCommonRecyclerViewAdapter<BaseChatRowViewModel>() {
        private static final int VIEW_TYPE_RECEIVE = 1;
        private static final int VIEW_TYPE_SEND = 2;

        @Override
        public int getViewType(int position) {
            BaseChatRowViewModel data = getDataList().get(position);
            return ChatRowViewFactory.getViewType(data.getChatMessage());
        }

        @Override
        public ViewDataBinding createViewDataBinding(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ViewDataBinding binding;
            if (ChatRowViewFactory.isSendViewType(viewType)) {
                binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_message_send, parent, false);
            } else {
                binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_message_receive, parent, false);
            }
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            layout.addView(ChatRowViewFactory.createView(parent, viewType));
            return binding;
        }

        @Override
        public void onDataBinding(ViewDataBinding binding, int position) {
            BaseChatRowViewModel data = getDataList().get(position);
            FrameLayout layout = (FrameLayout) binding.getRoot().findViewById(R.id.contentView);
            data.bindView(layout.getChildAt(0));
        }
    };

    @Subscribe
    public void onNewMessages(EventNewMessages newMessages) {
        for (EMMessage message : newMessages.getList()) {
            if (message.getFrom().equals(mRemoteUser.getChatId())) {
                addNewChatMessageToUi(ChatMessageFactory.get(message));
            }
        }
    }

    /**
     * 设置是否需要显示时间
     *
     * @param before  前一个汽泡
     * @param current 当前汽泡
     */
    private void setShowTime(BaseChatRowViewModel before, BaseChatRowViewModel current) {
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
        }
    }

    private void addNewChatMessageToUi(ChatMessage chatMessage) {
        BaseChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        setShowTime(mDataList.size() > 0 ? mDataList.get(mDataList.size() - 1) : null, data);
        mDataList.add(data);
        mAdapter.notifyItemInserted(mDataList.size() - 1);
        mBinding.recyclerView.scrollToPosition(mDataList.size() - 1);
    }
}
