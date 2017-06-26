package com.xmd.technician.permission.contact;

import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.net.BaseBean;
import com.xmd.app.net.NetworkEngine;
import com.xmd.app.net.NetworkSubscriber;
import com.xmd.app.net.RetrofitFactory;
import com.xmd.technician.bean.ContactPermissionInfo;
import com.xmd.technician.http.RequestConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscription;

/**
 * Created by mo on 17-6-15.
 * 技师与客服的联系权限
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
    public Subscription getPermission(String customerId, NetworkSubscriber<ContactPermissionInfo> listener) {
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
                    NetworkEngine.doRequest(
                            RetrofitFactory.getService(ContactPermissionNet.class).getContactPermissionInfo(customerId, null),
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

    interface ContactPermissionNet {
        /**
         * @param id     路径参数，默认为客户ID
         * @param idType ID类型，可选，customer:客户ID，emchat:环信ID
         * @return
         */
        @GET("/spa-manager/api/v2/tech/contact/permission/{id}")
        Observable<BaseBean<ContactPermissionInfo>> getContactPermissionInfo(@Path(RequestConstant.KEY_ID) String id,
                                                                             @Query(RequestConstant.KEY_CONTACT_ID_TYPE) String idType);
    }
}
