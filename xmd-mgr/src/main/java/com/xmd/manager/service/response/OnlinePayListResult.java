package com.xmd.manager.service.response;

import com.xmd.manager.beans.OnlinePayBean;

import java.util.List;

/**
 * Created by sdcm on 17-4-26.
 */

public class OnlinePayListResult extends BaseResult {
    public RespDataBean respData;
    public String isSearch;

    public static class RespDataBean {

        public int payAmountSum;
        public List<OnlinePayBean> fastPayOrderList;


    }
}
