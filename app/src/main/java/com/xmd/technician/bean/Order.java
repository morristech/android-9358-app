package com.xmd.technician.bean;

/**
 * Created by sdcm on 16-4-7.
 */

import java.io.Serializable;

/**
 * Created by sdcm on 15-11-23.
 */
public class Order implements Serializable {

    public String orderId;

    public String headImgUrl;

    public String customerName;

    public String userName;

    /**
     * 不为空则是内网订单
     */
    public String innerProvider;

    /**
     * 环信聊天id
     */
    public String emchatId;

    public String formatCreateTime;

    public String formatAppointTime;

    public String appointTime;
    public String createAt;

    public String phoneNum;

    public String orderType;

    public String status;

    public String statusName;

    public String remainTime;

    public int downPayment;

    /**
     * 服务项目
     */
    public String serviceName;

    /**
     * 项目价格
     */
    public String servicePrice;

    /**
     * 评论
     */
    public String comment;

    /**
     * 订单打赏金额
     */
    public int rewardAmount;

    /**
     * 评分
     */
    public int rating;

}
