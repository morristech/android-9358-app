package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 */

public class SalesCommissionListBean {
    /**
     * categoryDto : {"id":"925572478758035456","name":"零食","image":"73592","scope":"goods","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/75/oYYBAFKce26APrYSAAAAAlscE2s575.flv?st=E_9nrUAbqN-pQiQXQtaSGg&e=1513931287"}
     * commissionList : [{"businessName":"开心鬼片","businessId":"930000414760247296","priceName":"标准价","priceId":249,"primaryPrice":1000,"primaryUnitName":"1包","extendPrice":0,"extendUnitName":null,"commissionId":"164","commission":500},{"businessName":"酒鬼花生","businessId":"925572769356193792","priceName":"标准价","priceId":223,"primaryPrice":300,"primaryUnitName":"1包","extendPrice":0,"extendUnitName":null,"commissionId":"1","commission":100}]
     */

    public CategoryDtoBean categoryDto;
    public List<CommissionListBean> commissionList;

}
