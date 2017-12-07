package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by Lhj on 17-12-5.
 */

public class NativeCreateBill {

    private List<NativeItemBean> itemList;
    private long roomId;
    private long seatId;
    private String userIdentify;

    public NativeCreateBill(){

    }

    public NativeCreateBill(List<NativeItemBean> itemList, String userIdentify, int rooId, int seatId) {
        this.itemList = itemList;
        this.userIdentify = userIdentify;
        this.roomId = rooId;
        this.seatId = seatId;
    }

    public List<NativeItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<NativeItemBean> itemList) {
        this.itemList = itemList;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long rooId) {
        this.roomId = rooId;
    }

    public long getSeatId() {
        return seatId;
    }

    public void setSeatId(long seatId) {
        this.seatId = seatId;
    }

    public String getUserIdentify() {
        return userIdentify;
    }

    public void setUserIdentify(String userIdentify) {
        this.userIdentify = userIdentify;
    }
}
