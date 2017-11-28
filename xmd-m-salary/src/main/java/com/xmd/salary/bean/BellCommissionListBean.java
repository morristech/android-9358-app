package com.xmd.salary.bean;

/**
 * Created by Lhj on 17-11-23.
 */

public class BellCommissionListBean {

    /**
     * bellId : 1
     * bellName : 轮钟
     * commissionId : 171
     * commission : 800
     */
    public int bellId;
    public String bellName;
    public String commissionId;
    public int commission;

    public BellCommissionListBean(int bellId, int commission, String bellName) {
        this.bellId = bellId;
        this.commission = commission;
        this.bellName = bellName;
    }
}
