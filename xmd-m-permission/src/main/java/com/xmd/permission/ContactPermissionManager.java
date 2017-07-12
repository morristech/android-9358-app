package com.xmd.permission;

import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by mo on 17-6-15.
 * 技师与客户的联系权限
 */

public class ContactPermissionManager {
    private static final String CACHE_NAME = "contact_permission";
    private DiskCacheManager cacheManager = DiskCacheManager.getInstance();
    private static final ContactPermissionManager ourInstance = new ContactPermissionManager();
    private Map<String, ContactPermissionInfo> mPermissionMap = new HashMap<>();
    private Map<String, Subscription> mSubscriptionMap = new HashMap<>();
    private List<String> mPermissionKeys;

    public static ContactPermissionManager getInstance() {
        return ourInstance;
    }

    private ContactPermissionManager() {
        mPermissionKeys = (List<String>) cacheManager.get(CACHE_NAME + "_keys");
        if (mPermissionKeys == null) {
            mPermissionKeys = new ArrayList<>();
        } else {
            for (String key : mPermissionKeys) {
                ContactPermissionInfo info = (ContactPermissionInfo) cacheManager.get(CACHE_NAME + "_" + key);
                if (info != null) {
                    mPermissionMap.put(key, info);
                }
            }
            XLogger.i(CACHE_NAME + " cache size:" + mPermissionMap.size());
        }
    }

    /**
     * 获取权限
     *
     * @param customerId 客户userId
     * @param listener   回调方法
     */
    public Subscription getPermission(final String customerId, final NetworkSubscriber<ContactPermissionInfo> listener) {
        ContactPermissionInfo info = mPermissionMap.get(customerId);
        if (info != null && info.echat) {
            //具有全部权限，直接返回
//            XLogger.d("get permission from cache, customerId:" + customerId);
            listener.onCallbackSuccess(info);
        } else {
            //从网上加载
            Subscription subscription = mSubscriptionMap.get(customerId);
            if (subscription != null) {
                subscription.unsubscribe();
            }
            subscription =
                    XmdNetwork.getInstance().request(
                            XmdNetwork.getInstance().getService(NetService.class).getContactPermissionInfo(customerId, null),
                            new NetworkSubscriber<BaseBean<ContactPermissionInfo>>() {
                                @Override
                                public void onCallbackSuccess(BaseBean<ContactPermissionInfo> result) {
                                    mPermissionMap.put(customerId, result.getRespData());
                                    if (!mPermissionKeys.contains(customerId)) {
                                        mPermissionKeys.add(customerId);
                                        cacheManager.put(CACHE_NAME + "_keys", mPermissionKeys);
                                    }
                                    cacheManager.put(CACHE_NAME + "_" + customerId, result.getRespData());
                                    listener.onCallbackSuccess(result.getRespData());
                                }

                                @Override
                                public void onCallbackError(Throwable e) {
                                    listener.onCallbackError(e);
                                }
                            });
            mSubscriptionMap.put(customerId, subscription);
            return subscription;
        }
        return null;
    }
}
