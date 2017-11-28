package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 */

public class CommissionListBeanX {
    /**
     * businessName : 按到心痛
     * businessId : 926008795287527424
     * priceName : 标准价
     * priceId : 252
     * primaryPrice : 1100
     * primaryUnitName : 100分钟
     * extendPrice : 1000
     * extendUnitName : 100分钟
     * bellCommissionList : [{"bellId":1,"bellName":"轮钟","commissionId":"171","commission":800},{"bellId":2,"bellName":"点钟","commissionId":"172","commission":800},{"bellId":3,"bellName":"call钟","commissionId":"173","commission":800}]
     */

    public String businessName;
    public String businessId;
    public String priceName;
    public int priceId;
    public int primaryPrice;
    public String primaryUnitName;
    public int extendPrice;
    public String extendUnitName;
    public List<BellCommissionListBean> bellCommissionList;

}
