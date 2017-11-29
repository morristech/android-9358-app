package com.xmd.manager.service.response;

import com.xmd.manager.beans.CashierClubDetailInfo;

import java.util.List;

/**
 * Created by zr on 17-11-29.
 */

public class CashierClubDetailListResult extends BaseResult{
    public List<CashierClubDetailInfo> respData;
    public String eventType;
    public String requestType;
}
