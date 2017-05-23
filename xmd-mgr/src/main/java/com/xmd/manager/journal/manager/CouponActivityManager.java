package com.xmd.manager.journal.manager;

import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.CouponActivity;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.JournalCouponActivityListResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-11-18.
 */

public class CouponActivityManager {
    private static CouponActivityManager mInstance = new CouponActivityManager();
    private List<CouponActivity> mCouponActivityList;
    private boolean mIsLoaded;
    private Subscription mSubscription;

    private CouponActivityManager() {
        mCouponActivityList = new ArrayList<>();
    }

    public static CouponActivityManager getInstance() {
        return mInstance;
    }

    public Subscription loadData(Callback<List<CouponActivity>> callback) {
        if (mIsLoaded) {
            if (callback != null) {
                callback.onResult(null, mCouponActivityList);
            }
        } else {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
            mSubscription = RetrofitServiceFactory.getSpaService().getJournalCouponActivities(SharedPreferenceHelper.getUserToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetworkSubscriber<>(new Callback<JournalCouponActivityListResult>() {
                        @Override
                        public void onResult(Throwable error, JournalCouponActivityListResult result) {
                            if (error == null) {
                                mSubscription = null;
                                mIsLoaded = true;
                                mCouponActivityList.clear();
                                for (JournalCouponActivityListResult.Category c : result.respData) {
                                    CouponActivity couponActivity = new CouponActivity();
                                    couponActivity.setCategory(c.actType);
                                    couponActivity.setName(c.actTypeName);
                                    if (c.details != null) {
                                        for (JournalCouponActivityListResult.Item item : c.details) {
                                            if (item.value.contains(":")) {
                                                String values[] = item.value.split(":");
                                                if (values.length > 1) {
                                                    item.value = values[1];
                                                }
                                            }
                                            couponActivity.getData().add(new CouponActivity.Item(item.desc, item.value));
                                        }
                                    }
                                    mCouponActivityList.add(couponActivity);
                                }
                            }
                            if (callback != null) {
                                callback.onResult(error, mCouponActivityList);
                            }
                        }
                    }));
        }
        return mSubscription;
    }

    public void clear() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        mIsLoaded = false;
        mCouponActivityList.clear();
    }

    public CouponActivity getCouponActivity(String category, String value) {
        for (CouponActivity couponActivity : mCouponActivityList) {
            if (couponActivity.getCategory().equals(category)) {
                for (CouponActivity.Item item : couponActivity.getData()) {
                    if (item.getValue().equals(value)) {
                        return couponActivity;
                    }
                }
                break;
            }
        }
        return null;
    }
}
