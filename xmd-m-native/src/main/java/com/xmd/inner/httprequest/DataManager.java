package com.xmd.inner.httprequest;

import com.xmd.inner.httprequest.response.RoomOrderInfoResult;
import com.xmd.inner.httprequest.response.RoomSeatListResult;
import com.xmd.inner.httprequest.response.RoomSettingResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Subscription;

/**
 * Created by Lhj on 17-11-22.
 * 用于发起网络请求
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {

    }

    public Subscription getRoomSeatInfoList(String status, String roomName, NetworkSubscriber<RoomSeatListResult> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRoomSeatInfoList(status, roomName), listener);
    }

    public Subscription getOrderInfoByName(String roomId, NetworkSubscriber<RoomOrderInfoResult> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getOrderInfoByRoom(roomId), listener);
    }

    public Subscription setRoomStatus(String roomId, String status, NetworkSubscriber<BaseBean> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).setRoomStatus(roomId, status), listener);
    }

    public Subscription saveRoomSeatBook(String roomId, String seatId, String telephone, String appointTime, NetworkSubscriber<BaseBean> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).saveRoomSeatBook(roomId, seatId, telephone, appointTime), listener);
    }

    public Subscription cancelRoomSeatBook(String seatId, NetworkSubscriber<BaseBean> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).cancelRoomSeatBook(seatId), listener);
    }

    public Subscription getRoomStatistics(NetworkSubscriber<RoomStatisticResult> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRoomStatistics(), listener);
    }

    public Subscription getRoomSetting(NetworkSubscriber<RoomSettingResult> listener) {
        return XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getRoomSetting(), listener);
    }
}
