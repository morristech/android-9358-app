package com.xmd.m.comment.bean;

/**
 * Created by Lhj on 17-7-10.
 */

public class ManagerMemberInfoBean {
    /**
     * id : 143
     * name : null
     * phoneNum : null
     * cardNo : 111111111111111111
     * userId : null
     * amount : 172000
     * memberTypeId : 120
     * memberTypeName : 金卡
     * birth : 2017-07-20
     * freezeAmount : 1000
     * discount : 0
     * avatar : null
     * consumeCount : 3
     * consumeAmount : 4000
     * cumulativeAmount : 137000
     * giveAmount : 110000
     * creatorName : dingweifeng
     * createTime : 2017-07-22 15:29:44
     * avatarUrl : null
     */

    public int id;
    public String name;
    public String phoneNum;
    public String cardNo;
    public String userId;
    public int amount;
    public int memberTypeId;
    public String memberTypeName;
    public String birth;
    public int freezeAmount;
    public int discount;
    public String avatar;
    /**
     * 消费笔数
     */
    public int consumeCount;
    /**
     * 消费总金额
     */
    public long consumeAmount;
    /**
     * 充值总金额
     */
    public long cumulativeAmount;
    /**
     * 赠送总金额
     */
    public long giveAmount;
    public String creatorName;
    public String createTime;
    public String avatarUrl;

}
