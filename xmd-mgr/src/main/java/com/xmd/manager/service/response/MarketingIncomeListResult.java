package com.xmd.manager.service.response;

import com.xmd.manager.beans.MarketingIncomeBean;

import java.util.List;

/**
 * Created by Lhj on 17-4-26.
 */

public class MarketingIncomeListResult extends BaseResult {


    public RespDataBean respData;

    public static class RespDataBean {

        public int allAmount;
        public String itemCardSwitch;
        public List<MarketingIncomeBean> saleList;


    }
}
