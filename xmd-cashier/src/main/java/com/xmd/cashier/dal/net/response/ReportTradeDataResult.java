package com.xmd.cashier.dal.net.response;

/**
 * Created by heyangya on 16-9-18.
 */

public class ReportTradeDataResult extends BaseResult {
    public RespData respData;

    public static class RespData {
        public int cashierPoints;
    }
}