package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Adapter.RecentlyVisitorAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.SayHiVisitorResult;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.model.HelloSettingManager;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;


public class RecentlyVisitorFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshWidget;

    private Subscription mGetRecentlyVisitorSubscription;
    private Subscription mSayHiVisitorResultSubscription;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> mSayHiParams = new HashMap<>();
    private String lastTime;
    private boolean isRefresh;
    private RecentlyVisitorAdapter<RecentlyVisitorBean> adapter;
    private List<RecentlyVisitorBean> mVisitors;
    private int position;
    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private boolean mIsLoadingMore = false;
    private int pageSize = 20;
    private int currentGetSize = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_visitor, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mVisitors = new ArrayList<>();
        swipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary);
        mLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new RecentlyVisitorAdapter<RecentlyVisitorBean>(getActivity(), mVisitors);
        adapter.setCallbackListener(inteface);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setHasFixedSize(true);
        list.setLayoutManager(mLayoutManager);
        list.setAdapter(adapter);
        swipeRefreshWidget.setOnRefreshListener(this);

        mSayHiVisitorResultSubscription = RxBus.getInstance().toObservable(SayHiVisitorResult.class).subscribe(
                result -> handlerSayHiVisitorResult(result)
        );
        mGetRecentlyVisitorSubscription = RxBus.getInstance().toObservable(RecentlyVisitorResult.class).subscribe(
                result -> handlerClubInfoList(result)
        );
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == adapter.getItemCount()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void loadMore() {
        if (getListSafe()) {
            //上拉刷新，加载更多数据
            swipeRefreshWidget.setRefreshing(true);
            mIsLoadingMore = true;
        }
    }

    private boolean getListSafe() {
        if (currentGetSize == pageSize) {
            dispatchRequest();
            return true;
        }
        return false;
    }

    private void handlerSayHiVisitorResult(SayHiVisitorResult result) {
        if (result.statusCode == 200) {
            position = Integer.parseInt(result.position);
            if (position != -1) {
                mVisitors.get(position).canSayHello = "0";
                adapter.notifyItemChanged(position);
            }

            // 环信打招呼
            HelloSettingManager.getInstance().sendHelloTemplate(result.userName, result.userEmchatId, result.userAvatar, result.userType);

            Map<String, String> saveParams = new HashMap<>();
            saveParams.put(RequestConstant.KEY_FRIEND_CHAT_ID, result.userEmchatId);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT, saveParams);
        }

    }

    private void handlerClubInfoList(RecentlyVisitorResult result) {
        swipeRefreshWidget.setRefreshing(false);
        if (result.statusCode == 200) {
            if (result.isMainPage.equals("0")) {
                onRefresh();
                return;
            }
            currentGetSize = result.respData.size();
            if (result.respData != null) {
                if (result.respData.size() > 0) {
                    for (int i = 0; i < result.respData.size(); i++) {
                        if (Utils.isNotEmpty(result.respData.get(i).emchatId)) {
                            ChatUser user;
                            user = new ChatUser(result.respData.get(i).emchatId);
                            user.setAvatar(result.respData.get(i).avatarUrl);
                            user.setNick(result.respData.get(i).userName);
                            UserUtils.saveUser(user);
                        }
                    }
                    lastTime = String.valueOf(result.respData.get(result.respData.size() - 1).createdAt);
                }
                if (isRefresh) {
                    mVisitors.clear();
                }
                mVisitors.addAll(result.respData);
                adapter.setIsNoMore(currentGetSize < pageSize);
                adapter.setData(mVisitors);
            }
        }
        isRefresh = false;
    }

    private void dispatchRequest() {
        params.clear();
        if (TextUtils.isEmpty(lastTime) || isRefresh) {
            lastTime = "";
        }
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, "");
        params.put(RequestConstant.KEY_LAST_TIME, lastTime);
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(pageSize));
        params.put(RequestConstant.KEY_IS_MAIN_PAGE, "1");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_RECENTLY_VISITOR, params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mGetRecentlyVisitorSubscription, mSayHiVisitorResultSubscription);
    }

    private RecentlyVisitorAdapter.CallbackInterface<RecentlyVisitorBean> inteface = new RecentlyVisitorAdapter.CallbackInterface<RecentlyVisitorBean>() {
        @Override
        public void onSayHiButtonClicked(RecentlyVisitorBean bean, int position) {
            sayHiRequest(bean.userId, bean.userName, bean.avatarUrl, bean.customerType, bean.emchatId, String.valueOf(position));
        }

        @Override
        public void onItemClicked(RecentlyVisitorBean bean, int position) {
            if (Long.parseLong(bean.userId) > 0) {
                Intent intent = new Intent(getActivity(), ContactInformationDetailActivity.class);
                intent.putExtra(RequestConstant.KEY_USER_ID, bean.userId);
                intent.putExtra(RequestConstant.KEY_CONTACT_TYPE, Constant.CONTACT_INFO_DETAIL_TYPE_CUSTOMER);
                startActivity(intent);
            } else {
                Utils.makeShortToast(getActivity(), ResourceUtils.getString(R.string.visitor_has_no_message));
            }
        }

        @Override
        public void onLoadMoreButtonClicked() {
            dispatchRequest();
        }
    };

    private void sayHiRequest(String userId, String userName, String avatarUrl, String userType, String userEmchatId, String position) {
        if (!LoginTechnician.getInstance().checkAndLoginEmchat()) {
            ((BaseActivity) getActivity()).showToast("聊天系统正在初始化，请稍后再试!");
            return;
        }
        mSayHiParams.clear();
        params.put(RequestConstant.KEY_REQUEST_SAY_HI_TYPE, Constant.REQUEST_SAY_HI_TYPE_VISITOR);
        params.put(RequestConstant.KEY_NEW_CUSTOMER_ID, userId);
        params.put(RequestConstant.KEY_USERNAME, userName);
        params.put(RequestConstant.KEY_USER_AVATAR, avatarUrl);
        params.put(RequestConstant.KEY_USER_TYPE, userType);
        params.put(RequestConstant.KEY_GAME_USER_EMCHAT_ID, userEmchatId);
        params.put(ChatConstant.KEY_SAY_HI_POSITION, position);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SAY_HELLO, params);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        dispatchRequest();
    }
}