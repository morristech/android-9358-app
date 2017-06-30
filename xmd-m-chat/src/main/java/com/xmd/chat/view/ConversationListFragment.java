package com.xmd.chat;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.Callback;
import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.chat.databinding.FragmentConversationBinding;
import com.xmd.chat.event.EventChatLoginSuccess;
import com.xmd.chat.event.EventDeleteConversation;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.message.ChatMessage;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by mo on 17-6-21.
 * 会话对话框
 */

public class ConversationListFragment extends BaseFragment {
    private FragmentConversationBinding mBinding;
    private CommonRecyclerViewAdapter<ConversationData> mAdapter;

    public ObservableBoolean showLoading = new ObservableBoolean();
    public ObservableField<String> showError = new ObservableField<>();

    private List<ConversationData> conversationDataList;

    private ConversationManager conversationManager = ConversationManager.getInstance();

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
        mAdapter = new CommonRecyclerViewAdapter<ConversationData>() {
            @Override
            public void onViewRecycled(ViewHolder holder) {
                super.onViewRecycled(holder);
            }
        };
        mBinding.recyclerView.setAdapter(mAdapter);

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
        conversationManager.loadConversationList(new Callback<List<ConversationData>>() {
            @Override
            public void onResponse(List<ConversationData> result, Throwable error) {
                showLoading.set(false);
                conversationDataList = result;
                mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationDataList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onReceiveNewMessages(EventNewMessages messages) {
        for (EMMessage message : messages.getList()) {
            ConversationData data = conversationManager.getConversationData(message.getFrom());
            if (data == null) {
                //new conversation,refresh all list
                loadData(null);
                return;
            } else {
                ChatMessage chatMessage = ChatMessageFactory.get(message);
                data.setLastMessage(chatMessage);
                int fromPosition = mAdapter.getDataList().indexOf(data);
                mAdapter.notifyItemChanged(fromPosition);
                conversationDataList.remove(data);
                conversationDataList.add(0, data);
                if (mAdapter.getDataList() != conversationDataList) {
                    mAdapter.getDataList().remove(fromPosition);
                    mAdapter.getDataList().add(0, data);
                }

                if (fromPosition > 0) {
                    mAdapter.notifyItemMoved(fromPosition, 0);
                }
            }
        }
    }

    @Subscribe
    public void onDeleteConversation(EventDeleteConversation event) {
        int position;
        ConversationData data = event.getData();
        if (mAdapter.getDataList() != conversationDataList) {
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

    //搜索
    public void onSearch(Editable s) {
        mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationManager.listConversationData(s.toString()));
        mAdapter.notifyDataSetChanged();
    }
}
