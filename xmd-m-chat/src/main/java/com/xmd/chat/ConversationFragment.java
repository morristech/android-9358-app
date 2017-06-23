package com.xmd.chat;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.databinding.FragmentConversationBinding;
import com.xmd.chat.message.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mo on 17-6-21.
 * 会话对话框
 */

public class ConversationFragment extends BaseFragment {
    private FragmentConversationBinding mBinding;

    private ConversationFilter filter;

    private List<ConversationData> conversationList = new ArrayList<>();
    private CommonRecyclerViewAdapter<ConversationData> mAdapter;

    public ObservableBoolean showLoading=new ObservableBoolean();
    public ObservableBoolean showEmpty=new ObservableBoolean();
    public ObservableField<String> showError=new ObservableField<>();

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
        mBinding.setData(this);
        mAdapter = new CommonRecyclerViewAdapter<>();
        mBinding.recyclerView.setAdapter(mAdapter);
        conversationList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> list = new ArrayList<>();
        list.addAll(conversationMap.values());
        if (filter != null) {
            showLoading.set(true);
            showError.set(null);
            filter.filter(list, new Callback<List<EMConversation>>() {
                @Override
                public void onResponse(List<EMConversation> result, Throwable error) {
                    showLoading.set(false);
                    if (error != null) {
                        XToast.show(error.getMessage());
                        showError.set(error.getMessage());
                        return;
                    }
                    showData(result);
                }
            });
        } else {
            showData(list);
        }
    }

    private void showData(List<EMConversation> dataList) {
        conversationList.clear();
        for (EMConversation conversation : dataList) {
            ChatMessage chatMessage=ChatMessageFactory.get(conversation.getLastMessage());
            String remoteId=chatMessage.getRemoteChatId();
            User user= UserInfoServiceImpl.getInstance().getUserByChatId(remoteId);
            if(user==null){
                continue;
            }
            conversationList.add(new ConversationData(user,chatMessage.getContentText(),chatMessage.getEmMessage().getMsgTime()));
        }
        mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationList);
        mAdapter.notifyDataSetChanged();
    }


    interface ConversationFilter {
        void filter(List<EMConversation> conversations, Callback<List<EMConversation>> listener);
    }

    public ConversationFilter getFilter() {
        return filter;
    }

    public void setFilter(ConversationFilter filter) {
        this.filter = filter;
    }
}
