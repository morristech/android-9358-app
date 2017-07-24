package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by heyangya on 16-9-19.
 */

public class MemberInfo implements Serializable {
    // New
    public int amount;
    public int freezeAmount;
    public String accountId;
    public long id;
    public String cardNo;
    public int memberTypeId;
    public String memberTypeName;
    public int discount;
    public String userId;
    public String name;
    public String phoneNum;
    public String birth;
    public String gender;
}
