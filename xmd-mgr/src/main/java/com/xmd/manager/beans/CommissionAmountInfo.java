package com.xmd.manager.beans;

import java.io.Serializable;

/**
 * Created by zr on 17-11-24.
 * 会所所有技师汇总 和 具体技师汇总可通用
 */

public class CommissionAmountInfo implements Serializable{
    public long salesCommission;
    public long serviceCommission;

    public long getTotalCommission() {
        return salesCommission + serviceCommission;
    }

    // TECH
    public String roleDesc;    //职位描述	技师，楼面等
    public String techDesc;    //技师描述	昵称 （编号）
    public String techId;    //技师id
    public String techName;    //技师名称
    public String techNo;    //技师编号
    public String techPhone;    //技师电话
    public String techAvatar;
}
