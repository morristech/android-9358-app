package com.xmd.cashier.dal.net.response;

import com.xmd.m.network.BaseBean;

/**
 * Created by heyangya on 16-9-19.
 */

public class GetMemberInfo extends BaseBean<GetMemberInfo.OldMemberInfo> {
    public class OldMemberInfo {
        public String token; //会员标识
        public int balance; //账户佘额
        public String name;
        public int discount; //折扣率 [0-1000)
        public String phone; //手机号
        public String cardNo; //会员卡号
        public String memberTypeName; //会员类型
    }
}
