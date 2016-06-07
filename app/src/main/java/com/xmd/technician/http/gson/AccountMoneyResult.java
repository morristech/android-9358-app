package com.xmd.technician.http.gson;

/**
 * Created by sdcm on 16-3-31.
 */
public class AccountMoneyResult extends BaseResult {

    public Content respData;

    public class Content{
        public float rewardMoney;
        public float redPack;
        public float paidMoney;
        public float orderMoney;
        public String withdrawal;
    }
}
