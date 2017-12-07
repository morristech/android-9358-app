package com.xmd.inner;

import com.shidou.commonlibrary.helper.RetryPool;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.bean.RoomSettingInfo;
import com.xmd.inner.bean.RoomStatisticInfo;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.RoomSettingResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by zr on 17-12-6.
 */

public class NativeManager {
    private Map<String, Integer> mStatusTypeMap = new LinkedHashMap<>();
    private List<RoomStatisticInfo> roomStatisticInfos = new ArrayList<>();
    private int usingSeatCount = 0;

    private static final NativeManager ourInstance = new NativeManager();

    public static NativeManager getInstance() {
        return ourInstance;
    }

    private NativeManager() {

    }

    public void init() {
        EventBusSafeRegister.register(this);
    }

    public int getStatusColor(String status) {
        try {
            return ConstantResource.STATUS_TYPE_COLOR.get(mStatusTypeMap.get(status));
        } catch (Exception e) {
            return ResourceUtils.getColor(R.color.status_color_green);
        }
    }

    public List<RoomStatisticInfo> getRoomStatisticInfos() {
        return roomStatisticInfos;
    }

    public int getUsingSeatCount() {
        return usingSeatCount;
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        getNativeSetting();
    }

    @Subscribe(sticky = true)
    public void onLogout(EventLogout eventLogout) {
        // 登出
        stopLoopNativeStatistics();
    }

    private void getNativeSetting() {
        DataManager.getInstance().getRoomSetting(new NetworkSubscriber<RoomSettingResult>() {
            @Override
            public void onCallbackSuccess(RoomSettingResult result) {
                Map<String, Integer> tempMap = new LinkedHashMap<>();
                for (RoomSettingInfo settingInfo : result.getRespData().statusList) {
                    tempMap.put(settingInfo.code, settingInfo.color);
                }
                mStatusTypeMap = tempMap;
            }

            @Override
            public void onCallbackError(Throwable e) {
                mStatusTypeMap = ConstantResource.DEFAULT_STATUS_COLOR_TYPE;
            }
        });
    }


    private Subscription mStatisticsSubscription;
    private RetryPool.RetryRunnable mStatisticsRunnable;

    public void startLoopNativeStatistics() {
        if (mStatisticsRunnable == null) {
            mStatisticsRunnable = new RetryPool.RetryRunnable(6 * 10 * 1000, 1f, new RetryPool.RetryExecutor() {
                @Override
                public boolean run() {
                    getNativeStatistics();
                    return false;
                }
            });
            RetryPool.getInstance().postWork(mStatisticsRunnable);
        }
    }

    public void stopLoopNativeStatistics() {
        if (mStatisticsSubscription != null) {
            mStatisticsSubscription.unsubscribe();
        }
        if (mStatisticsRunnable != null) {
            RetryPool.getInstance().removeWork(mStatisticsRunnable);
            mStatisticsRunnable = null;
        }
    }

    private void getNativeStatistics() {
        if (mStatisticsSubscription != null) {
            mStatisticsSubscription.unsubscribe();
        }
        mStatisticsSubscription = DataManager.getInstance().getRoomStatistics(new NetworkSubscriber<RoomStatisticResult>() {
            @Override
            public void onCallbackSuccess(RoomStatisticResult result) {
                roomStatisticInfos = result.getRespData().statusList;
                usingSeatCount = result.getRespData().usingSeatCount;
                EventBus.getDefault().post(result);
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
