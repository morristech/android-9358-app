package com.xmd.contact.httprequest;


import com.xmd.contact.bean.ClubEmployeeListResult;
import com.xmd.contact.bean.ContactAllListResult;
import com.xmd.contact.bean.ContactRecentListResult;
import com.xmd.contact.bean.ContactRegisterListResult;
import com.xmd.contact.bean.ManagerContactAllListResult;
import com.xmd.contact.bean.ManagerContactRecentListResult;
import com.xmd.contact.bean.NearbyCusCountResult;
import com.xmd.contact.bean.TagListResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-7-1.
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {

    }

    private Subscription mNearByCustomerCount;
    private Subscription mLoadClubEmployeeList;
    private Subscription mLoadAllCustomer;
    private Subscription mLoadRegisterCustomer;
    private Subscription mLoadRecentCustomer;
    private Subscription mLoadClubALlCustomer;
    private Subscription mLoadClubRecentVisitorCustomer;
    private Subscription mLoadAllTags;


    //附近的人数目
    public void loadNearByCustomerCount(NetworkSubscriber<NearbyCusCountResult> listener) {
        mNearByCustomerCount = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getNearbyCusCount(), listener);
    }

    //技师所有联系人列表
    public void loadAllCustomer(Map<String, String> params, NetworkSubscriber<ContactAllListResult> listener) {
        mLoadClubEmployeeList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getAllContactList(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_CUSTOMER_LEVEL), params.get(RequestConstant.KEY_CUSTOMER_TYPE), params.get(RequestConstant.KEY_REMARK), params.get(RequestConstant.KEY_TECH_NO),
                params.get(RequestConstant.KEY_USER_GROUP), params.get(RequestConstant.KEY_USER_NAME)), listener);
    }

    //技师拓客列表
    public void loadRegisterCustomer(Map<String, String> params, NetworkSubscriber<ContactRegisterListResult> listener) {
        mLoadRegisterCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRegisterContactList(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_CUSTOMER_LEVEL), params.get(RequestConstant.KEY_CUSTOMER_TYPE), params.get(RequestConstant.KEY_REMARK), params.get(RequestConstant.KEY_TECH_NO),
                params.get(RequestConstant.KEY_USER_GROUP), params.get(RequestConstant.KEY_USER_NAME)), listener);
    }

    //技师最近访客
    public void loadRecentCustomer(NetworkSubscriber<ContactRecentListResult> listener) {
        mLoadRecentCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRecentContactList(), listener);
    }

    //技师及管理者会所联系人共用
    public void loadClubEmployeeList(NetworkSubscriber<ClubEmployeeListResult> listener) {
        mLoadClubEmployeeList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).clubEmployeeList(), listener);
    }

    //管理者所有联系人列表
    public void loadClubAllCustomer(Map<String, String> params, NetworkSubscriber<ManagerContactAllListResult> listener) {
        mLoadClubEmployeeList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getManagerAllContactList(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_CUSTOMER_LEVEL), params.get(RequestConstant.KEY_CUSTOMER_TYPE), params.get(RequestConstant.KEY_REMARK), params.get(RequestConstant.KEY_TECH_NO),
                params.get(RequestConstant.KEY_USER_GROUP), params.get(RequestConstant.KEY_USER_NAME)), listener);
    }

    //管理者最近访客
    public void loadClubRecentCustomer(NetworkSubscriber<ManagerContactRecentListResult> listener) {
        mLoadClubRecentVisitorCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getManagerRecentContactList(), listener);
    }

    //搜索标签
    public void loadAllTags(NetworkSubscriber<TagListResult> listener) {
        mLoadAllTags = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getTagList(), listener);
    }

}
