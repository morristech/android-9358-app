package com.xmd.contact;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.contact.bean.ContactRegister;
import com.xmd.contact.bean.ContactRegisterListResult;
import com.xmd.contact.event.ContactUmengStatisticsEvent;
import com.xmd.contact.event.SwitchTableToMarketingEvent;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.contact.httprequest.RequestConstant;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsRegisterFragment extends BaseListFragment<ContactRegister> {

    Unbinder unbinder;
    private View view;

    private String mCustomerLevel;
    private String mCustomerType;
    private String mCustomerRemark;
    private String mCustomerTechId;
    private String mCustomerUserGroup;
    private String mCustomerUserName;
    private Map<String, String> params;
    private boolean isSearchOrFilter = false;
    protected LinearLayout mLlContactNone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_register, container, false);
        mLlContactNone = (LinearLayout) view.findViewById(R.id.ll_contact_none);
        initView();
        unbinder = ButterKnife.bind(this, view);
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
        DataManager.getInstance().loadRegisterCustomer(params, new NetworkSubscriber<ContactRegisterListResult>() {
            @Override
            public void onCallbackSuccess(ContactRegisterListResult result) {
                handlerContactRegisterListResult(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                onGetListFailed(e.getLocalizedMessage());
            }
        });
    }

    private void handlerContactRegisterListResult(ContactRegisterListResult result) {
        if (result.getRespData() == null) {
            return;
        }
        if (result.getRespData().userList.size() == 0) {
            if (isSearchOrFilter) {
                mSwipeRefreshLayout.setRefreshing(false);
                XToast.show("未查到相关联系人");
                mLlContactNone.setVisibility(View.GONE);
                return;
            } else {
                mLlContactNone.setVisibility(View.VISIBLE);
            }
        } else {
            mLlContactNone.setVisibility(View.GONE);
        }
        onGetListSucceeded(result.getPageCount(), result.getRespData().userList, false);
    }


    public void filterOrSearchCustomer(String searchText, String tagName, String userGroup, String customerLevel, String customerType, String serialNo) {
        if (TextUtils.isEmpty(searchText) && TextUtils.isEmpty(tagName) && TextUtils.isEmpty(userGroup) && TextUtils.isEmpty(customerLevel)
                && TextUtils.isEmpty(customerType) && TextUtils.isEmpty(serialNo)) {
            isSearchOrFilter = false;
        } else {
            isSearchOrFilter = true;
        }
        resetData();
        mCustomerUserName = searchText;
        mCustomerLevel = customerLevel;
        mCustomerType = customerType;
        mCustomerRemark = tagName;
        mCustomerTechId = serialNo;
        mCustomerUserGroup = userGroup;
        if (!TextUtils.isEmpty(mCustomerRemark)) {
            EventBus.getDefault().post(new ContactUmengStatisticsEvent(Constants.UMENG_STATISTICS_FILTER_CUSTOMER_CHOOSE));
        }
        if (!TextUtils.isEmpty(mCustomerLevel)) {
            EventBus.getDefault().post(new ContactUmengStatisticsEvent(Constants.UMENG_STATISTICS_FILTER_VIP_CHOOSE));
        }
        if (!TextUtils.isEmpty(customerType)) {
            EventBus.getDefault().post(new ContactUmengStatisticsEvent(Constants.UMENG_STATISTICS_FILTER_TYPE_CHOOSE));
        }
        if (!TextUtils.isEmpty(userGroup)) {
            EventBus.getDefault().post(new ContactUmengStatisticsEvent(Constants.UMENG_STATISTICS_CUSTOMER_GROUP));
        }
        onRefresh();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }

    @Override
    public void onItemClicked(ContactRegister bean, String type) {
        super.onItemClicked(bean, type);
        if (TextUtils.isEmpty(bean.userId) && TextUtils.isEmpty(bean.id)) {
            XToast.show("该用户无详情信息");
        } else {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), TextUtils.isEmpty(bean.userId) ? bean.id : bean.userId, ConstantResources.APP_TYPE_TECH, false);
        }
    }

    @OnClick(R2.id.btn_nearby_people)
    public void onNearbyPeopleClicked() {
        EventBus.getDefault().post(new SwitchTableToMarketingEvent());
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((TechContactFragment) getParentFragment()).showOrHideFilterButton(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
