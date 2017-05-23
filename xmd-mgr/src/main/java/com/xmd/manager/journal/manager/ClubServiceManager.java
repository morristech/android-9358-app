package com.xmd.manager.journal.manager;

import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.ServiceItem;
import com.xmd.manager.beans.ServiceItemInfo;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.ServiceItemListResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/10.
 */
public class ClubServiceManager {

    private static ClubServiceManager instance = new ClubServiceManager();
    private List<ServiceItemInfo> mServiceList;
    private Subscription mLoadServiceSubscription;
    private boolean mIsLoadData;

    private ClubServiceManager() {
        mServiceList = new ArrayList<>();
    }

    public static ClubServiceManager getInstance() {
        return instance;
    }

    public Subscription loadService(Callback<List<ServiceItemInfo>> callback) {
        if (mIsLoadData) {
            if (callback != null) {
                callback.onResult(null, mServiceList);
            }
            return null;
        }
        if (mLoadServiceSubscription != null) {
            mLoadServiceSubscription.unsubscribe();
        }
        mLoadServiceSubscription = RetrofitServiceFactory.getSpaService().getServiceList(SharedPreferenceHelper.getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<ServiceItemListResult>() {
                    @Override
                    public void onResult(Throwable error, ServiceItemListResult result) {
                        mLoadServiceSubscription = null;
                        if (error == null) {
                            mIsLoadData = true;
                            mServiceList.clear();
                            mServiceList.addAll(result.respData);
                        }
                        if (callback != null) {
                            callback.onResult(error, mServiceList);
                        }
                    }
                }));
        return mLoadServiceSubscription;
    }


    public ServiceItem getServiceItem(String id) {
        for (ServiceItemInfo item : mServiceList) {
            for (ServiceItem subItem : item.serviceItems) {
                if (subItem.id.equals(id)) {
                    return subItem;
                }
            }
        }
        return null;
    }

    public void clear() {
        mIsLoadData = false;
        mServiceList.clear();
    }

}

