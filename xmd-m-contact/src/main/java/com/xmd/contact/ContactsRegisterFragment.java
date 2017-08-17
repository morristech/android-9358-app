package com.xmd.contact;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.black.event.AddOrRemoveBlackEvent;
import com.xmd.black.event.EditCustomerRemarkSuccessEvent;
import com.xmd.contact.bean.ContactAllBean;
import com.xmd.contact.bean.ContactRegisterListResult;
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

import butterknife.OnClick;

/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsRegisterFragment extends BaseListFragment<ContactAllBean> {

    private View view;
    private String mCurrentFilterType;
    private String mCustomerName;
    private boolean hasContacts;
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
        view = inflater.inflate(R.layout.fragment_contact_register, container, false);
        initView();
        return view;
    }

    private void initView() {
        mCustomerName = "";
        mCurrentFilterType = "";
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
                mSwipeRefreshLayout.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void handlerContactRegisterListResult(ContactRegisterListResult result) {
        if (result.getRespData() == null) {
            return;
        }
        if (TextUtils.isEmpty(mCurrentFilterType) && TextUtils.isEmpty(mCustomerName) && result.getRespData().userList.size() == 0) {
            //所有用户为0
            mLlContactNone.setVisibility(View.VISIBLE);
            hasContacts = false;

        } else {
            mLlContactNone.setVisibility(View.GONE);
            hasContacts = true;
        }
        onGetListSucceeded(result.getPageCount(), result.getRespData().userList, false);
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
    public void onRefresh() {
        super.onRefresh();
    }

    @Override
    public void onItemClicked(ContactAllBean bean, String type) {
        super.onItemClicked(bean, type);
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), TextUtils.isEmpty(bean.userId) ? bean.id : bean.userId, ConstantResources.APP_TYPE_TECH, false);
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
}
