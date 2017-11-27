package com.xmd.manager.service.response;

import com.xmd.manager.beans.CommissionAmountInfo;

import java.util.List;

/**
 * Created by zr on 17-11-24.
 */

public class CommissionAmountListResult extends BaseResult {
    public List<CommissionAmountInfo> respData;

    public String eventType;
}
