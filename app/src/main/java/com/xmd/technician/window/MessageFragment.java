package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Adapter.MsgListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-23.
 */
public class MessageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,MsgListAdapter.onMsgItemClickListener {

    @Bind(R.id.swipe_refresh_widget) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.message_list) RecyclerView mMsgListView;
    @Bind(R.id.header_container) FrameLayout mHeadContainer;

    private MsgListAdapter mAdapter;
    protected List<EMConversation> mConversationList = new ArrayList<EMConversation>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMessage();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            refreshMessage();
        }
    }

    private void initView(){
        ((TextView)getView().findViewById(R.id.toolbar_title)).setText(R.string.message_fragment_title);
        mMsgListView.setHasFixedSize(true);
        mMsgListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mConversationList.addAll(loadConversationList());
        mAdapter = new MsgListAdapter(getContext(), mConversationList,this);
        mMsgListView.setAdapter(mAdapter);

        View searchView = getActivity().getLayoutInflater().inflate(R.layout.search_bar, mHeadContainer, false);
        mHeadContainer.addView(searchView);

        ((TextView)searchView.findViewById(R.id.search_word)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorMain);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onMsgItemClick(EMConversation conversation) {
        String username = conversation.getUserName();
        String a = EMClient.getInstance().getCurrentUser();
        if (username.equals(SharedPreferenceHelper.getEmchatId()))
            ((BaseActivity)getActivity()).makeShortToast(ResourceUtils.getString(R.string.cant_chat_with_yourself));
        else {
            // 进入聊天页面
            Intent intent = new Intent(getContext(), ChatActivity.class);
            if(conversation.isGroup()){
                if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                    // it's group chat
                    intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_CHATROOM);
                }else{
                    intent.putExtra(ChatConstant.EXTRA_CHAT_TYPE, ChatConstant.CHATTYPE_GROUP);
                }

            }
            // it's single chat
            intent.putExtra(ChatConstant.EXTRA_USER_ID, username);
            startActivity(intent);
        }
    }

    public void refreshMessage(){
        mConversationList.clear();
        mConversationList.addAll(loadConversationList());
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
            @Override
            public void run() {
                mAdapter.setData(mConversationList);
            }
        });
    }

    /**
     * 获取会话列表
     * @return
     */
    protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
