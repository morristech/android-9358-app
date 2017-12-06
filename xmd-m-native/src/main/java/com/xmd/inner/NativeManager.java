package com.xmd.inner;

import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.bean.RoomSettingInfo;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.RoomSettingResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zr on 17-12-6.
 */

public class NativeManager {
    private Map<String, Integer> mStatusTypeMap = new LinkedHashMap<>();

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

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        getNativeSetting();

        // TODO 轮询 获取统计的数据
        loopNativeStatistics();
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

    private void loopNativeStatistics() {
        DataManager.getInstance().getRoomStatistics(new NetworkSubscriber<RoomStatisticResult>() {
            @Override
            public void onCallbackSuccess(RoomStatisticResult result) {
                EventBus.getDefault().postSticky(result);
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
