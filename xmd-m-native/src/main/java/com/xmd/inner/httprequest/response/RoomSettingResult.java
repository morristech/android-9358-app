package com.xmd.inner.httprequest.response;

import com.xmd.inner.bean.RoomSettingInfo;
import com.xmd.m.network.BaseBean;

import java.util.List;

/**
 * Created by zr on 17-12-5.
 */

public class RoomSettingResult extends BaseBean<RoomSettingResult.RespData> {
    public class RespData {
        public List<RoomSettingInfo> statusList;
    }
}
