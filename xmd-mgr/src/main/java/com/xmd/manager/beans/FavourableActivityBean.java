package com.xmd.manager.beans;

/**
 * Created by Administrator on 2016/11/10.
 */
public class FavourableActivityBean {

    /**
     * name : 现金券-我是最新现金券
     * actId : 793645394889285632
     * actName : 我是最新现金券
     * msg : <i>现金券</i><span>10</span>元<b>领取后3天生效，至2016-12-30有效！</b>
     * msgType : ordinaryCoupon
     */

    public String name;
    public String actId;
    public String actName;
    public String msg;
    public String msgType;

    public FavourableActivityBean(String name, String id) {
        this.name = name;
        this.actId = id;
    }

}
