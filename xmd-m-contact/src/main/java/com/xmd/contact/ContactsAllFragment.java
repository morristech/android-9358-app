package com.xmd.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.UserInfoServiceImpl;
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

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Lhj on 17-7-26.
 */

public class ContactsAllFragment extends BaseListFragment<ContactAllBean> implements SwipeRefreshLayout.OnRefreshListener {

    Unbinder unbinder;
    private View view;
    private List<ContactAllBean> mContacts;
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
        view = inflater.inflate(R.layout.fragment_contact_all, container, false);
        mLlContactNone = (LinearLayout) view.findViewById(R.id.ll_contact_none);
        initView();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
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
        params.put(RequestConstant.KEY_TECH_ID, mCustomerTechId);
        params.put(RequestConstant.KEY_USER_GROUP, mCustomerUserGroup);
        params.put(RequestConstant.KEY_USER_NAME, mCustomerUserName);
        DataManager.getInstance().loadAllCustomer(params, new NetworkSubscriber<ContactAllListResult>() {
            @Override
            public void onCallbackSuccess(ContactAllListResult result) {
                handlerContactAllListResult(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                onGetListFailed(e.getLocalizedMessage());
            }
        });
    }

    private void handlerContactAllListResult(ContactAllListResult result) {
        if (result.getRespData() == null || result.getRespData().userList == null) {
            return;
        }
        if (result.getRespData().userList.size() == 0) {
            if (isSearchOrFilter) {
                XToast.show("未查到相关联系人");
                ((TechContactFragment) getParentFragment()).showOrHideFilterButton(true);
                mSwipeRefreshLayout.setRefreshing(false);
                mLlContactNone.setVisibility(View.GONE);
                return;
            } else {
                ((TechContactFragment) getParentFragment()).showOrHideFilterButton(false);
                if (TextUtils.isEmpty(UserInfoServiceImpl.getInstance().getCurrentUser().getClubId())) {
                    mLlContactNone.setVisibility(View.GONE); //尚未加入会所，在联系人为0时不显示拓客页面
                } else {
                    mLlContactNone.setVisibility(View.VISIBLE);
                }

            }
        } else {
            ((TechContactFragment) getParentFragment()).showOrHideFilterButton(true);
            mLlContactNone.setVisibility(View.GONE);
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
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    @OnClick(R2.id.btn_nearby_people)
    public void onNearbyPeopleClicked() {
        EventBus.getDefault().post(new SwitchTableToMarketingEvent());
    }
}
