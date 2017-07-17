package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.Callback;
import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.chat.BR;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.R;
import com.xmd.chat.databinding.FragmentConversationBinding;
import com.xmd.chat.event.EventChatLoginSuccess;
import com.xmd.chat.event.EventDeleteConversation;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.event.EventUnreadCount;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.viewmodel.ConversationViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by mo on 17-6-21.
 * 会话对话框
 */

public class ConversationListFragment extends BaseFragment {
    private FragmentConversationBinding mBinding;
    private CommonRecyclerViewAdapter<ConversationViewModel> mAdapter;

    public ObservableBoolean showLoading = new ObservableBoolean();
    public ObservableField<String> showError = new ObservableField<>();

    private List<ConversationViewModel> conversationViewModelList;

    private ConversationManager conversationManager = ConversationManager.getInstance();

    private static final String ARG_TITLE = "title";

    public static ConversationListFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        ConversationListFragment fragment = new ConversationListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getArguments().getString(ARG_TITLE));

        mAdapter = new CommonRecyclerViewAdapter<ConversationViewModel>() {
            @Override
            public void onViewRecycled(ViewHolder holder) {
                super.onViewRecycled(holder);
            }
        };
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.refreshLayout.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(null);
            }
        });

        loadData(null);
        EventBusSafeRegister.register(this);

        mBinding.setData(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusSafeRegister.unregister(this);
    }

    @Subscribe
    public void loadData(EventChatLoginSuccess event) {
        showLoading.set(true);
        conversationManager.loadConversationList(new Callback<List<ConversationViewModel>>() {
            @Override
            public void onResponse(List<ConversationViewModel> result, Throwable error) {
                mBinding.refreshLayout.setRefreshing(false);
                showLoading.set(false);
                conversationViewModelList = result;
                mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationViewModelList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void processMessageReceiveOrSend(ChatMessage chatMessage) {
        ConversationViewModel data = conversationManager.getConversationData(chatMessage.getRemoteChatId());
        if (data == null) {
            //new conversation,refresh all list
            loadData(null);
            return;
        } else {
            data.setLastMessage(chatMessage);
            int fromPosition = mAdapter.getDataList().indexOf(data);
            mAdapter.notifyItemChanged(fromPosition);
            conversationViewModelList.remove(data);
            conversationViewModelList.add(0, data);
            if (mAdapter.getDataList() != conversationViewModelList) {
                mAdapter.getDataList().remove(fromPosition);
                mAdapter.getDataList().add(0, data);
            }

            if (fromPosition > 0) {
                mAdapter.notifyItemMoved(fromPosition, 0);
            }
        }
    }

    @Subscribe
    public void onReceiveNewMessages(EventNewMessages messages) {
        for (EMMessage message : messages.getList()) {
            processMessageReceiveOrSend(new ChatMessage(message));
        }
    }

    @Subscribe
    public void onSendMessage(EventSendMessage eventSendMessage) {
        processMessageReceiveOrSend(eventSendMessage.getChatMessage());
    }

    @Subscribe
    public void onDeleteConversation(EventDeleteConversation event) {
        int position;
        ConversationViewModel data = event.getData();
        if (mAdapter.getDataList() != conversationViewModelList) {
            //当前处于搜索状态
            position = mAdapter.getDataList().indexOf(data);
            if (position >= 0) {
                mAdapter.getDataList().remove(position);
            }
        } else {
            position = event.getPosition();
        }
        if (position >= 0) {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Subscribe
    public void onUnreadCountEvent(EventUnreadCount event) {
        int position = mAdapter.getDataList().indexOf(event.getConversationViewModel());
        if (position >= 0) {
            mAdapter.notifyItemChanged(position);
        }
    }

    //搜索
    public void onSearch(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            mBinding.refreshLayout.setEnabled(false);
        } else {
            mBinding.refreshLayout.setEnabled(true);
        }
        mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationManager.listConversationData(s.toString()));
        mAdapter.notifyDataSetChanged();
    }
}
