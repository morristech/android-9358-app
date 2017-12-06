package com.xmd.inner.httprequest.response;

import com.xmd.inner.bean.RoomStatisticInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-12-5.
 */

public class RoomStatisticResult extends BaseBean<RoomStatisticResult.RespData> {
    public class RespData {
        public List<RoomStatisticInfo> statusList;
        public int usingSeatCount;
    }
}
