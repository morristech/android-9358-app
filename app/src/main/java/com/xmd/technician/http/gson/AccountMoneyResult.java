package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-31.
 */
public class AccountMoneyResult extends BaseResult {

    public Content respData;

    public class Content{
        public int rewardMoney;
        public int redPack;
        public int paidMoney;
        public String withdrawal;
    }
}
