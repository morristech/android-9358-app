package com.xmd.manager.service.response;

import java.util.List;

/**
 * Created by Lhj on 17-4-25.
 */

public class AccountDataResult extends BaseResult {

    /**
     * respData : {"amountList":[{"accountType":"fast_pay","categoryType":"fast_pay","totalAmount":77105,"amount":0,"dealDate":null,"accountTypeName":"在线买单"},{"accountType":"sale_amount","categoryType":null,"totalAmount":1230822,"amount":0,"dealDate":null,"accountTypeName":""}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        public int totalAmount;
        public List<AmountListBean> amountList;

        public static class AmountListBean {
            /**
             * accountType : fast_pay
             * categoryType : fast_pay
             * totalAmount : 77105
             * amount : 0
             * dealDate : null
             * accountTypeName : 在线买单
             */

            public String accountType;
            public String categoryType;
            public int totalAmount;
            public int amount;
            public Object dealDate;
            public String accountTypeName;
        }
    }
}
