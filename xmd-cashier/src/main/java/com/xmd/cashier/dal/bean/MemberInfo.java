package com.xmd.cashier.dal.bean;

/**
 * Created by heyangya on 16-9-19.
 */

public class MemberInfo {
    public String token; //会员标识
    public int balance; //账户佘额
    public int discount; //折扣率 [0-1000)
    public String phone; //手机号
    public String cardNo; //会员卡号
    public String memberTypeName; //会员类型

    @Override
    public String toString() {
        return "token:" + token + ",cardNo:" + cardNo + ",balance:" + balance + ",discount:" + discount + ",phone:" + phone + ",memberTypeName:" + memberTypeName;
    }
}
