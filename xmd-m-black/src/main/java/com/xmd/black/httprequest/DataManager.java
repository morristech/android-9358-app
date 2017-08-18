package com.xmd.black.httprequest;

import com.xmd.black.bean.AddToBlacklistResult;
import com.xmd.black.bean.BlackListResult;
import com.xmd.black.bean.CreateCustomerResult;
import com.xmd.black.bean.EditCustomerResult;
import com.xmd.black.bean.InBlacklistResult;
import com.xmd.black.bean.InUserBlacklistResult;
import com.xmd.black.bean.ManagerEditCustomerResult;
import com.xmd.black.bean.MarkResult;
import com.xmd.black.bean.RemoveFromBlacklistResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Subscription;

/**
 * Created by Lhj on 17-7-22.
 */

public class DataManager {
    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public Subscription mUserBlackList;
    public Subscription mBooleanInBlackList;
    public Subscription mRemoveBlackList;
    public Subscription mAddBlackList;
    public Subscription mInCustomerBlackList;
    public Subscription mLoadImpression;
    public Subscription mCreateCustomer;
    public Subscription mEditOrAddCustomer;
    public Subscription mManagerEditCustomer;

    public void loadUserBlackList(String page, String pageSize, NetworkSubscriber<BlackListResult> listener) {
        mUserBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getBlacklist(page, pageSize), listener);

    }

    public void loadBooleanInBlackList(String friendId, NetworkSubscriber<InBlacklistResult> listener) {
        mBooleanInBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).inBlacklist(friendId), listener);
    }

    public void removeFromBlackList(String friendId, NetworkSubscriber<RemoveFromBlacklistResult> listener) {
        mRemoveBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).removeFromBlacklist(friendId), listener);
    }

    public void addToBlackList(String friendId, NetworkSubscriber<AddToBlacklistResult> listener) {
        mAddBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).addToBlacklist(friendId), listener);
    }

    public void judgeInCustomerBlackList(String chatId, NetworkSubscriber<InUserBlacklistResult> listener) {
        mInCustomerBlackList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).inUserBlacklist(chatId), listener);
    }

    public void getImpressionDetail(NetworkSubscriber<MarkResult> listener) {
        mLoadImpression = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getContactMark("tech_customer"), listener);
    }

    public void doCreateCustomer(String noteName, String phoneNum, String impression, String remark, NetworkSubscriber<CreateCustomerResult> listener) {
        mCreateCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).addCreateCustomer(noteName, phoneNum, impression, remark), listener);
    }

    public void SaveCustomerRemark(String userId, String id, String phoneNum, String remark, String noteName, String impression, NetworkSubscriber<EditCustomerResult> listener) {
        mEditOrAddCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).addOrEditCustomer(userId, id, phoneNum, remark, noteName, impression), listener);
    }

    public void managerEditRemark(String id, String noteName, String phoneNum, String remark, NetworkSubscriber<ManagerEditCustomerResult> listener) {
        mManagerEditCustomer = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).managerEditCustomer(id, noteName, phoneNum, remark), listener);
    }
}
