package com.xmd.inner;

import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.event.EventLogin;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.inner.bean.RoomStatisticInfo;
import com.xmd.inner.httprequest.DataManager;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.NetworkSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zr on 17-12-6.
 */

public class NativeManager {
    private Map<String, Integer> mStatusTypeMap = new HashMap<>();
    private List<RoomStatisticInfo> roomStatisticInfos = new ArrayList<>();
    private int usingSeatCount = 0;

    private static final NativeManager ourInstance = new NativeManager();

    public static NativeManager getInstance() {
        return ourInstance;
    }

    private NativeManager() {
        mStatusTypeMap = ConstantResource.DEFAULT_STATUS_COLOR_TYPE;
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

    public Map<String, Integer> getStatusTypeMap() {
        return mStatusTypeMap;
    }

    public void setRoomStatisticInfos(List<RoomStatisticInfo> list) {
        roomStatisticInfos = list;
    }

    public int getUsingSeatCount() {
        return usingSeatCount;
    }

    public void setUsingSeatCount(int count) {
        usingSeatCount = count;
    }

    @Subscribe(sticky = true)
    public void onLogin(EventLogin eventLogin) {
        refreshNativeStatistics();
    }

    public void refreshNativeStatistics() {
        DataManager.getInstance().getRoomStatistics(new NetworkSubscriber<RoomStatisticResult>() {
            @Override
            public void onCallbackSuccess(RoomStatisticResult result) {
                if (result.getRespData().statusList != null && !result.getRespData().statusList.isEmpty()) {
                    Iterator<RoomStatisticInfo> it = result.getRespData().statusList.iterator();
                    while (it.hasNext()) {
                        RoomStatisticInfo info = it.next();
                        mStatusTypeMap.put(info.code, info.color);
                        if (!ConstantResource.RESPONSE_YES.equals(info.status)) {
                            it.remove();
                        }
                    }
                }
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
