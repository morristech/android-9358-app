package com.xmd.inner.httprequest.response;

import com.xmd.inner.bean.OrderInfo;
import com.xmd.inner.bean.RoomInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-12-2.
 */

public class RoomOrderInfoResult extends BaseBean<RoomOrderInfoResult.RespData> {
    public class RespData {
        public List<OrderInfo> orderList;
        public RoomInfo room;
    }
}
