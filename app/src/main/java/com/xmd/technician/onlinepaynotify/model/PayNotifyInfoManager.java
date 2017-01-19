package com.xmd.technician.onlinepaynotify.model;

import com.xmd.technician.common.Callback;
import com.xmd.technician.common.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-17.
 */
public class PayNotifyInfoManager {
    private static PayNotifyInfoManager ourInstance = new PayNotifyInfoManager();

    public static PayNotifyInfoManager getInstance() {
        return ourInstance;
    }

    private PayNotifyInfoManager() {
        mData = new ArrayList<>();
    }

    private List<PayNotifyInfo> mData;
    private long mCurrentStartTime = -1;
    private long mCurrentEndTime = -1;

    // [startTime,endTime)
    public void getNotifyInfo(final long startTime, final long endTime, final int status, Callback<List<PayNotifyInfo>> callback) {
        if (mCurrentStartTime == -1 || startTime < mCurrentStartTime || endTime > mCurrentEndTime) {
            loadDataFromNetwork(startTime, endTime, new Callback<List<PayNotifyInfo>>() {
                @Override
                public void onResult(Throwable error, List<PayNotifyInfo> result) {
                    if (error == null) {
                        if (result.size() > 0) {
                            mData.clear();
                            mData.addAll(result);
                            result = getDataByFilter(startTime, endTime, status);
                        }
                        mCurrentStartTime = startTime;
                        mCurrentEndTime = endTime;
                    }
                    callback.onResult(error, result);
                }
            });
        } else {
            callback.onResult(null, getDataByFilter(startTime, endTime, status));
        }
    }

    private List<PayNotifyInfo> getDataByFilter(long startTime, long endTime, int status) {
        List<PayNotifyInfo> result = new ArrayList<>();
        for (PayNotifyInfo info : mData) {
            if (info.payTime >= startTime && info.payTime < endTime && (info.status | status) != 0) {
                result.add(info);
            }
        }
        return result;
    }

    //从网络加载数据
    public void loadDataFromNetwork(long startTime, long endTime, Callback<List<PayNotifyInfo>> callback) {
        List<PayNotifyInfo> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PayNotifyInfo info = new PayNotifyInfo();
            info.userName = "顾客" + i;
            info.amount = (long) (Math.random() * 1000);
            info.isArchived = false;
            long currentTime = System.currentTimeMillis();
            if (i % 3 == 0) {
                info.payTime = currentTime - 35L * DateUtils.DAY_MILLIS_SECOND;
            } else if (i % 3 == 1) {
                info.payTime = currentTime - 10L * DateUtils.DAY_MILLIS_SECOND;
            } else {
                info.payTime = currentTime + 35L * DateUtils.DAY_MILLIS_SECOND;
            }

            info.status = 1 + i % 2;
            info.userAvatar = "http://img3.duitang.com/uploads/item/201608/21/20160821200538_vHxLi.thumb.700_0.jpeg";
            info.combineTechs = new ArrayList<>();
            info.combineTechs.add("A00" + i);
            info.combineTechs.add("A100");
            data.add(info);
        }
        callback.onResult(null, data);
    }
}
