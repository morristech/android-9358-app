package com.xmd.chat.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.Pageable;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.BaseFragment;
import com.xmd.app.BaseViewModel;
import com.xmd.app.CommonRecyclerViewAdapter;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.UserInfoChangedEvent;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.app.widget.GlideCircleTransform;
import com.xmd.chat.BR;
import com.xmd.chat.ChatAccountManager;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-6-21.
 * 会话对话框
 */

public class ConversationListFragment extends BaseFragment {
    private FragmentConversationBinding mBinding;
    private CommonRecyclerViewAdapter<ConversationViewModel> mAdapter;

    public ObservableBoolean isLoading = new ObservableBoolean();
    public ObservableField<String> showError = new ObservableField<>();

    private List<ConversationViewModel> conversationViewModelList = new ArrayList<>();
    private ConversationManager conversationManager = ConversationManager.getInstance();
    protected static final String ARG_TITLE = "title";

    private boolean paused;
    private boolean needReload;
    private int page = 0;
    private final static int PAGE_SIZE = 10;
    private boolean hasMoreData;
    private LinearLayoutManager layoutManager;

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
        layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerView.setLayoutManager(layoutManager);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(getArguments().getString(ARG_TITLE));
        mAdapter = new CommonRecyclerViewAdapter<>();
        mAdapter.setFooter(R.layout.list_item_load_more, BR.data, this);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int last = layoutManager.findLastVisibleItemPosition();
                    int total = layoutManager.getItemCount();
                    if (hasMoreData && last + 1 == total) {
                        loadConversation(false);
                    }
                }
            }
        });
        mBinding.refreshLayout.setColorSchemeColors(0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff, 0xff000000);
        mBinding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadConversation(true);
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

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (needReload) {
            reloadConversation(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    private void reloadConversation(boolean refresh) {
        page = 0;
        loadConversation(refresh);
    }

    public boolean getShowSelfAvatar() {
        return false;
    }

    @Subscribe
    public void loadData(EventChatLoginSuccess event) {
        reloadConversation(false);
    }

    public boolean isHasMoreData() {
        return hasMoreData;
    }

    private void loadConversation(final boolean forceLoadUserInfo) {
        if (isLoading.get()) {
            return;
        }
        isLoading.set(true);
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                conversationManager.loadConversationList(forceLoadUserInfo, new Callback<Pageable<ConversationViewModel>>() {
                    @Override
                    public void onResponse(final Pageable<ConversationViewModel> result, Throwable error) {
                        if (error != null) {
                            isLoading.set(false);
                            mBinding.refreshLayout.setRefreshing(false);
                            XToast.show("数据加载失败：" + error.getMessage());
                            return;
                        }
                        ThreadPoolManager.postToUI(new Runnable() {
                            @Override
                            public void run() {
                                isLoading.set(false);
                                mBinding.refreshLayout.setRefreshing(false);
                                if (result.getPage() == 0) {
                                    conversationViewModelList.clear();
                                }
                                conversationViewModelList.addAll(result.getData());
                                mAdapter.setData(R.layout.list_item_conversation, BR.data, conversationViewModelList);
                                mAdapter.notifyDataSetChanged();
                                hasMoreData = page != result.getTotalPage() && page != result.getTotalPage() - 1;
                                page++;
                            }
                        });
                    }
                }, page, PAGE_SIZE);
            }
        });
    }

    private void processMessageReceiveOrSend(ChatMessage chatMessage) {
        if (paused) {
            XLogger.d("do not update when paused!");
            needReload = true;
            return;
        }
        ConversationViewModel data = conversationManager.getConversationData(chatMessage.getRemoteChatId());
        if (data == null) {
            loadData(null);
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
        for (Object message : messages.getList()) {
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
            mAdapter.notifyItemRemoved(position);
        } else {
            ThreadPoolManager.postToUI(new Runnable() {
                @Override
                public void run() {
                    reloadConversation(true);
                }
            });
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

    public String getUserAvatar() {
        User user = ChatAccountManager.getInstance().getUser();
        return user == null ? null : user.getAvatar();
    }

    public void bindAvatar(ImageView view, String avatar) {
        BaseViewModel.bindCircleImage(view, avatar);
    }

    public User getUser() {
        return UserInfoServiceImpl.getInstance().getCurrentUser();
    }

    @Subscribe
    public void userInfoChangedEvent(UserInfoChangedEvent event) {
        if (!TextUtils.isEmpty(event.userHeadUrl)) {
            Glide.with(getActivity()).load(event.userHeadUrl).transform(new GlideCircleTransform(getActivity())).error(R.drawable.img_default_avatar).into(mBinding.circleAvatar);
        }
    }
}
