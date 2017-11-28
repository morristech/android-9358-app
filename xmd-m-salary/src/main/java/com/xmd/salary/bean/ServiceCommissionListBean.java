package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 */

public class ServiceCommissionListBean {
    /**
     * categoryDto : {"id":"851634869048975360","name":"大赛的看","image":"73623","scope":"spa","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/74/ooYBAFKcgpyASBWjAAAAAlscE2s187.flv?st=Ir_0LAp4kG9hpZW5W7rLYg&e=1513931286"}
     * commissionList : [{"businessName":"按到心痛","businessId":"926008795287527424","priceName":"标准价","priceId":252,"primaryPrice":1100,"primaryUnitName":"100分钟","extendPrice":1000,"extendUnitName":"100分钟","bellCommissionList":[{"bellId":1,"bellName":"轮钟","commissionId":"171","commission":800},{"bellId":2,"bellName":"点钟","commissionId":"172","commission":800},{"bellId":3,"bellName":"call钟","commissionId":"173","commission":800}]}]
     */

    public CategoryDtoBeanX categoryDto;
    public List<CommissionListBeanX> commissionList;

}
