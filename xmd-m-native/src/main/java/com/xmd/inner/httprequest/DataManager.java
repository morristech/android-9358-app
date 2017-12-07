package com.xmd.inner.httprequest;

import com.xmd.inner.bean.NativeCreateBill;
import com.xmd.inner.bean.NativeUpdateBill;
import com.xmd.inner.httprequest.response.CreateSeatOrderResult;
import com.xmd.inner.httprequest.response.HaveIdentifyResult;
import com.xmd.inner.httprequest.response.OrderItemUpdateResult;
import com.xmd.inner.httprequest.response.OrderTimeListResult;
import com.xmd.inner.httprequest.response.ProjectListAvailableResult;
import com.xmd.inner.httprequest.response.RoomOrderInfoResult;
import com.xmd.inner.httprequest.response.RoomSeatListResult;
import com.xmd.inner.httprequest.response.RoomSettingResult;
import com.xmd.inner.httprequest.response.RoomStatisticResult;
import com.xmd.inner.httprequest.response.TechnicianListResult;
import com.xmd.inner.httprequest.response.UserIdentifyListResult;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.List;

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

    private Subscription mAvailableProjectListSubscription;
    private Subscription mLoadTechnicianList;
    private Subscription mLoadOrderItemBellList;
    private Subscription mCreateSeatOrder;
    private Subscription mUserHaveIdentify;
    private Subscription mUserIdentifyList;
    private Subscription mUpdateOrderBillItem;

    public void getAvailableProjectList(NetworkSubscriber<ProjectListAvailableResult> listener) {
        cancelGetAvailableProjectList();
        mAvailableProjectListSubscription = XmdNetwork.getInstance().request(
                XmdNetwork.getInstance().getService(NetService.class).getAvailableProjectList(), listener);
    }

    public void cancelGetAvailableProjectList() {
        if (mAvailableProjectListSubscription != null) {
            mAvailableProjectListSubscription.unsubscribe();
            mAvailableProjectListSubscription = null;
        }
    }

    public void loadTechnicianList(final NetworkSubscriber<TechnicianListResult> listener) {
        cancelLoadTechnicianList();
        mLoadTechnicianList = XmdNetwork.getInstance().request(
                XmdNetwork.getInstance().getService(NetService.class).getTechnicianList("tech"), listener);
    }

    public void cancelLoadTechnicianList() {
        if (mLoadTechnicianList != null) {
            mLoadTechnicianList.unsubscribe();
            mLoadTechnicianList = null;
        }
    }

    public void loadOrderItemBellList(NetworkSubscriber<OrderTimeListResult> listener) {
        cancelLoadOrderItemBellList();
        mLoadOrderItemBellList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getOrderItemList(), listener);
    }

    public void cancelLoadOrderItemBellList() {
        if (mLoadOrderItemBellList != null) {
            mLoadOrderItemBellList.unsubscribe();
            mLoadOrderItemBellList = null;
        }
    }

    public void createSeatOrder(List<NativeCreateBill> bills, NetworkSubscriber<CreateSeatOrderResult> listener) {
        cancelCreateSeatOrder();
        mCreateSeatOrder = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).createSeatOrder(bills), listener);
    }

    public void cancelCreateSeatOrder() {
        if (mCreateSeatOrder != null) {
            mCreateSeatOrder.unsubscribe();
            mCreateSeatOrder = null;
        }
    }

    public void userIdentifyHave(NetworkSubscriber<HaveIdentifyResult> listener) {
        cancelUserIdentifyHave();
        mUserHaveIdentify = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).userIdentifyHave(), listener);
    }

    public void cancelUserIdentifyHave() {
        if (mUserHaveIdentify != null) {
            mUserHaveIdentify.unsubscribe();
            mUserHaveIdentify = null;
        }
    }

    public void getIdentifyList(NetworkSubscriber<UserIdentifyListResult> listener) {
        cancelUserIdentifyList();
        mUserIdentifyList = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).getUserIdentifyList(), listener);
    }

    public void cancelUserIdentifyList() {
        if (mUserIdentifyList != null) {
            mUserIdentifyList.unsubscribe();
            mUserIdentifyList = null;
        }
    }

    public void updateOrderBillItem(NativeUpdateBill bills, NetworkSubscriber<OrderItemUpdateResult> listener) {
        cancelUpdateOrderBillItem();
        mUpdateOrderBillItem = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class).updateOrderItem(bills), listener);
    }

    public void cancelUpdateOrderBillItem() {
        if (mUpdateOrderBillItem != null) {
            mUpdateOrderBillItem.unsubscribe();
            mUpdateOrderBillItem = null;
        }
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
