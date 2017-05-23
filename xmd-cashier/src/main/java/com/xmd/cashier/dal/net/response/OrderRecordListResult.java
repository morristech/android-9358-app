package com.xmd.cashier.dal.net.response;

import com.xmd.cashier.dal.bean.OrderRecordInfo;

import java.util.List;

/**
 * Created by zr on 17-4-11.
 */

public class OrderRecordListResult extends BaseResult {
    public int pageCount;
    public List<OrderRecordInfo> respData;
}
