package com.example.xmd_m_comment.bean;

/**
 * Created by Lhj on 17-7-10.
 */

public class ManagerMemberInfoBean {
    /**
     * id : 5
     * name : 算法的技术
     * phoneNum : 13137558109
     * cardNo : 5895681612000001
     * accountId : 807043347448360960
     * accountAmount : 999000
     * memberTypeId : 13
     * memberTypeName : 普通
     * freezeAmount : 0
     * discount : 1000
     * clubId : 601679316694081536
     * createTime : 2016-12-09 10:05:02
     * modifyTime : null
     * operatorName : null
     * clubName : null
     * clubImage : null
     * styleId : 1
     * description : 请在会员结账时主动提供会员支付二维码
     * amount : 999000
     */

    public int id;
    public String name;
    public String phoneNum;
    public String cardNo;
    public String accountId;
    public int accountAmount;
    public int memberTypeId;
    public String memberTypeName;
    public int freezeAmount;
    public int discount;
    public String clubId;
    public String createTime;
    public String modifyTime;
    public String operatorName;
    public String clubName;
    public String clubImage;
    public int styleId;
    public String description;
    public int amount;
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
}
