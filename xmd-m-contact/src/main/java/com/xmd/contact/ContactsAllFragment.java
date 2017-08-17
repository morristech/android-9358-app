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
import com.xmd.contact.bean.ContactAllBean;
import com.xmd.contact.bean.ContactAllListResult;
import com.xmd.contact.event.SwitchTableToMarketingEvent;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.contact.httprequest.DataManager;
import com.xmd.contact.httprequest.RequestConstant;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;


/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsAllFragment extends BaseListFragment<ContactAllBean> implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private String mCustomerName;
    private List<ContactAllBean> mContacts;
    private String mCustomerLevel;
    private String mCustomerType;
    private String mCustomerRemark;
    private String mCustomerTechId;
    private String mCustomerUserGroup;
    private String mCustomerUserName;
    private Map<String, String> params;
    private boolean hasContacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_all, container, false);
        initView();
        return view;
    }

    private void initView() {
        mCustomerName = "";
        mContacts = new ArrayList<>();
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
        DataManager.getInstance().loadAllCustomer(params, new NetworkSubscriber<ContactAllListResult>() {
            @Override
            public void onCallbackSuccess(ContactAllListResult result) {
                handlerContactAllListResult(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                XToast.show(e.getLocalizedMessage());
            }
        });
    }

    private void handlerContactAllListResult(ContactAllListResult result) {
        if (result.getRespData() == null || result.getRespData().userList == null) {
            return;
        }
        if (TextUtils.isEmpty(mCustomerName) && result.getRespData().userList.size() == 0) {
            //所有用户为0
            mLlContactNone.setVisibility(View.VISIBLE);
            hasContacts = false;
        } else {
            mLlContactNone.setVisibility(View.GONE);
            hasContacts = true;
        }
        mContacts.clear();
        if (!TextUtils.isEmpty(result.getRespData().serviceStatus) && result.getRespData().serviceStatus.equals("Y")) {
            for (int i = 0; i < result.getRespData().userList.size(); i++) {
                result.getRespData().userList.get(i).isService = true;
                mContacts.add(result.getRespData().userList.get(i));
            }
        } else {
            mContacts.addAll(result.getRespData().userList);
        }
        onGetListSucceeded(result.getPageCount(), mContacts, false);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClicked(ContactAllBean bean, String type) {
        super.onItemClicked(bean, type);
        if (TextUtils.isEmpty(bean.userId) && TextUtils.isEmpty(bean.id)) {
            XToast.show("该用户无详情信息");
        } else if (TextUtils.isEmpty(bean.userId) && !TextUtils.isEmpty(bean.id)) {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.id, com.xmd.m.comment.httprequest.ConstantResources.CUSTOMER_TYPE_TECH_ADD, false);
        } else {
            CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(getActivity(), bean.userId, ConstantResources.APP_TYPE_TECH, false);
        }

    }

    @OnClick(R2.id.btn_nearby_people)
    public void onNearbyPeopleClicked() {
        EventBus.getDefault().post(new SwitchTableToMarketingEvent());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((TechContactFragment) getParentFragment()).showOrHideFilterButton(true);
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
    public void onRefresh() {
        super.onRefresh();
    }
}
