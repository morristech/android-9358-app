package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.CharacterParser;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.contact.bean.ContactRecentBean;
import com.xmd.contact.bean.ContactRecentListResult;
import com.xmd.contact.event.SayHiSuccessEvent;
import com.xmd.contact.event.SayHiToChatEvent;
import com.xmd.contact.event.SwitchTableToMarketingEvent;
import com.xmd.contact.event.ThanksToChatEvent;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsVisitorsFragment extends BaseListFragment<ContactRecentBean> {

    private View view;
    private List<ContactRecentBean> mVisitors;
    private List<ContactRecentBean> mFilterVisitors;
    private String mUserName;
    private CharacterParser characterParser;
    protected LinearLayout mLlContactNone;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_visitor, container, false);
        mLlContactNone = (LinearLayout) view.findViewById(R.id.ll_contact_none);
        initView();
        return view;
    }

    private void initView() {
        mVisitors = new ArrayList<>();
        characterParser = CharacterParser.getInstance();
        mUserName = "";
    }

    public void filterOrSearchCustomer(String searchText) {
        mUserName = searchText;
        if (TextUtils.isEmpty(mUserName)) {
            onGetListSucceeded(1, mVisitors, false);
        } else {
            if (mFilterVisitors == null) {
                mFilterVisitors = new ArrayList<>();
            } else {
                mFilterVisitors.clear();
            }
            for (ContactRecentBean recentBean : mVisitors) {
                if (!TextUtils.isEmpty(recentBean.name)) {
                    recentBean.name = "游客";
                }
                if ((recentBean.name.indexOf(mUserName.toString())) != -1 || characterParser.getSelling(recentBean.name).startsWith(mUserName.toString())) {
                    mFilterVisitors.add(recentBean);
                }
            }
            if (mFilterVisitors.size() > 0) {
                onGetListSucceeded(1, mFilterVisitors, false);
            } else {
                XToast.show("未查到相关联系人");
            }
        }
    }


    @Override
    protected void dispatchRequest() {
        DataManager.getInstance().loadRecentCustomer(new NetworkSubscriber<ContactRecentListResult>() {
            @Override
            public void onCallbackSuccess(ContactRecentListResult result) {
                handlerRecentUserList(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void handlerRecentUserList(ContactRecentListResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        mVisitors.clear();
        if (result.getRespData().userList.size() == 0 && TextUtils.isEmpty(mUserName)) {
            mLlContactNone.setVisibility(View.VISIBLE);
        } else {
            mLlContactNone.setVisibility(View.GONE);
        }
        if (result.getRespData().userList.size() > 0) {
            mVisitors.addAll(result.getRespData().userList);
            onGetListSucceeded(1, mVisitors, false);
            for (ContactRecentBean visitor : mVisitors) {
                User user = new User(visitor.userId);
                user.setChatId(visitor.emchatId);
                user.setName(visitor.name);
                user.setNoteName(visitor.userNoteName);
                user.setAvatar(visitor.avatarUrl);
                UserInfoServiceImpl.getInstance().saveUser(user);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((TechContactFragment) getParentFragment()).showOrHideFilterButton(false);
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Subscribe
    public void onSayHiSuccessed(SayHiSuccessEvent event) {
        if (event.position < 999) {
            mVisitors.get(event.position).canSayHello = "N";
            mListAdapter.notifyItemChanged(event.position, mVisitors.get(event.position));
        } else {
            onRefresh();
        }
    }

    @OnClick(R2.id.btn_nearby_people)
    public void onNearbyPeopleClicked() {
        EventBus.getDefault().post(new SwitchTableToMarketingEvent());
    }

    @Override
    public void onPositiveButtonClicked(ContactRecentBean bean, int position, boolean isThanks) {
        super.onPositiveButtonClicked(bean, position, isThanks);
        if (isThanks) {
            if (TextUtils.isEmpty(bean.emchatId)) {
                XToast.show("缺少用户信息，感谢失败");
            }
            EventBus.getDefault().post(new ThanksToChatEvent(bean.emchatId));
        } else {
            if (TextUtils.isEmpty(bean.emchatId)) {
                XToast.show("缺少用户信息，打招呼失败");
            }
            EventBus.getDefault().post(new SayHiToChatEvent(bean.emchatId, position));

        }
    }

    @Subscribe
    public void addOrRemoveBlackListSubscribe(AddOrRemoveBlackEvent event) {
        if (event.success) {
            onRefresh();
        }
    }

    @Subscribe
    public void onRemarkChangedSubscribe(EditCustomerRemarkSuccessEvent event) {
        onRefresh();
    }

    @Override
    public void onItemClicked(ContactRecentBean bean, String type) {
        super.onItemClicked(bean, type);
        if ((TextUtils.isEmpty(bean.userNoteName) && TextUtils.isEmpty(bean.name))) {
            XToast.show("该用户无详情信息");
            return;
        }
        if ((TextUtils.isEmpty(bean.userId) && TextUtils.isEmpty(bean.id) || bean.userId.equals("-1"))) {
            XToast.show("该用户无详情信息");
            return;
        }
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.userId, ConstantResources.APP_TYPE_TECH, false);
    }
}
