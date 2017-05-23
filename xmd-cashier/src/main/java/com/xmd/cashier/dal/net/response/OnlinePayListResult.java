package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.OnlinePayInfo;

import java.util.List;

/**
 * Created by zr on 17-4-11.
 */

public class OnlinePayListResult extends BaseResult {
    public int pageCount;
    public List<OnlinePayInfo> respData;
}
