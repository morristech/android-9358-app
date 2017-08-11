package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.contact.bean.ManagerContactAllBean;
import com.xmd.contact.bean.ManagerContactAllListResult;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.contact.httprequest.RequestConstant;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lhj on 17-7-26.
 */

public class ManagerContactsAllFragment extends BaseListFragment<ManagerContactAllBean> implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private String mCustomerLevel;
    private String mCustomerType;
    private String mCustomerRemark;
    private String mCustomerTechId;
    private String mCustomerUserGroup;
    private String mCustomerUserName;
    private Map<String, String> params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_all, container, false);
        initView();
        return view;
    }

    private void initView() {
        params = new HashMap<>();
        resetData();
    }

    private void resetData() {
        mCustomerLevel = "";
        mCustomerType = "";
        mCustomerRemark = "";
        mCustomerTechId = "";
        mCustomerUserGroup = "";
        mCustomerUserName = "";
    }

    @Override
    protected void dispatchRequest() {
        params.clear();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_CUSTOMER_LEVEL, mCustomerLevel);
        params.put(RequestConstant.KEY_CUSTOMER_TYPE, mCustomerType);
        params.put(RequestConstant.KEY_REMARK, mCustomerRemark);
        params.put(RequestConstant.KEY_TECH_NO, mCustomerTechId);
        params.put(RequestConstant.KEY_USER_GROUP, mCustomerUserGroup);
        params.put(RequestConstant.KEY_USER_NAME, mCustomerUserName);
        DataManager.getInstance().loadClubAllCustomer(params, new NetworkSubscriber<ManagerContactAllListResult>() {
            @Override
            public void onCallbackSuccess(ManagerContactAllListResult result) {
                handlerContactAllListResult(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void handlerContactAllListResult(ManagerContactAllListResult result) {
        if (result.getRespData() == null) {
            return;
        }
        mLlContactNone.setVisibility(View.GONE);
        onGetListSucceeded(result.getPageCount(), result.getRespData().userList, true);
    }

    public void filterOrSearchCustomer(String searchText, String tagName, String userGroup, String customerLevel, String customerType, String serialNo) {
        resetData();
        mCustomerUserName = searchText;
        mCustomerLevel = customerLevel;
        mCustomerType = customerType;
        mCustomerRemark = tagName;
        mCustomerTechId = serialNo;
        mCustomerUserGroup = userGroup;
        onRefresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((ManagerContactFragment) getParentFragment()).showOrHideFilterButton(!hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    public void onItemClicked(ManagerContactAllBean bean, String type) {
        super.onItemClicked(bean, type);
        if (TextUtils.isEmpty(bean.userId) && TextUtils.isEmpty(bean.id)) {
            XToast.show("该用户无详情信息");
        } else {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.userId, ConstantResources.APP_TYPE_MANAGER, false);
        }

    }


}
