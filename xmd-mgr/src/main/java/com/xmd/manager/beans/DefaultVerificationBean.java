package com.xmd.manager.beans;

import java.io.Serializable;


/**
 * Created by lhj on 2016/12/9.
 */

public class DefaultVerificationBean implements Serializable {

    /**
     * needAmount : false
     * title : 抢项目
     * code : 146660128445
     * type : paid_service_item
     * info : [{"title":"活动名称","text":"压背"},{"title":"抢购价格","text":"10积分"},{"title":"券有效期","text":"2016-12-02 00:00:00至2017-01-20 23:59:59"},{"title":"使用时段","text":"周一，周二，周三，周四，周五，周六，周日"}]
     */

    public boolean needAmount;
    public String title;
    public String code;
    public String type;
    public VerificationInfo info;

}
