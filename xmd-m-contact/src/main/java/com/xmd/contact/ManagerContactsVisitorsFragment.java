package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.CharacterParser;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.contact.bean.ManagerContactRecentBean;
import com.xmd.contact.bean.ManagerContactRecentListResult;
import com.xmd.contact.event.ThanksToChatEvent;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-7-26.
 */

public class ManagerContactsVisitorsFragment extends BaseListFragment<ManagerContactRecentBean> {

    private View view;
    private List<ManagerContactRecentBean> mVisitors;
    private List<ManagerContactRecentBean> mFilterVisitors;
    private CharacterParser characterParser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manager_contact_visitor, container, false);
        initView();
        return view;
    }

    private void initView() {
        mVisitors = new ArrayList<>();
        characterParser = CharacterParser.getInstance();

    }

    public void filterOrSearchCustomer(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            onGetListSucceeded(1, mVisitors, true);
        } else {
            if (mFilterVisitors == null) {
                mFilterVisitors = new ArrayList<>();
            } else {
                mFilterVisitors.clear();
            }
            for (ManagerContactRecentBean recentBean : mVisitors) {
                if (TextUtils.isEmpty(recentBean.name)) {
                    recentBean.name = "游客";
                }
                if ((recentBean.name.indexOf(searchText.toString())) != -1 || characterParser.getSelling(recentBean.name).startsWith(searchText.toString())) {
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
        DataManager.getInstance().loadClubRecentCustomer(new NetworkSubscriber<ManagerContactRecentListResult>() {
            @Override
            public void onCallbackSuccess(ManagerContactRecentListResult result) {
                handlerRecentUserList(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                onGetListFailed(e.getLocalizedMessage());
            }
        });
    }

    private void handlerRecentUserList(ManagerContactRecentListResult result) {
        mSwipeRefreshLayout.setRefreshing(false);
        mVisitors.clear();
        if (result.getRespData().userList.size() > 0) {
            mVisitors.addAll(result.getRespData().userList);
            onGetListSucceeded(1, mVisitors, true);
            for (ManagerContactRecentBean visitor : mVisitors) {
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
    public void onPositiveButtonClicked(ManagerContactRecentBean bean, int position, boolean isThanks) {
        super.onPositiveButtonClicked(bean, position, isThanks);
        if (isThanks) {
            if (TextUtils.isEmpty(bean.emchatId)) {
                XToast.show("缺少用户信息，感谢失败");
            }
            EventBus.getDefault().post(new ThanksToChatEvent(bean.emchatId));
        }
    }

    @Override
    public void onItemClicked(ManagerContactRecentBean bean, String type) {
        super.onItemClicked(bean, type);
        if ((TextUtils.isEmpty(bean.userId) && TextUtils.isEmpty(bean.id) || bean.userId.equals("-1"))) {
            XToast.show("该用户无详情信息");
            return;
        }
        if (TextUtils.isEmpty(bean.name) && TextUtils.isEmpty(bean.emchatId)) {
            XToast.show("该用户无详情信息");
            return;
        }
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.userId, ConstantResources.APP_TYPE_MANAGER, false);
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
    public boolean isPaged() {
        return false;
    }
}
