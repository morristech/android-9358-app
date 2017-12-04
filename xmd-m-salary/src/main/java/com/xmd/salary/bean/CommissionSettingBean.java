package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 * 工资说明
 */

public class CommissionSettingBean {

    /**
     * orderParameter : {"id":"843753674936164352","downPayment":2,"clubId":"770559669897797632","startTime":"00:00","endTime":"00:00","techCommission":1,"expireCommission":1,"retentionTime":0,"createDate":null,"modifyDate":"2017-11-20 17:33:41","appointType":"paid","description":"1、预约支付的金额，将可在消费后抵现；<br>2、不设找赎，过期不退；<br>3、请在预约时间前到店以体验更好服务；<br>4、本会所保留在法律许可的范围内对上述规则作出解释的权利；"}
     * salesCommissionList : [{"categoryDto":{"id":"925572478758035456","name":"零食","image":"73592","scope":"goods","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/75/oYYBAFKce26APrYSAAAAAlscE2s575.flv?st=E_9nrUAbqN-pQiQXQtaSGg&e=1513931287"},"commissionList":[{"businessName":"开心鬼片","businessId":"930000414760247296","priceName":"标准价","priceId":249,"primaryPrice":1000,"primaryUnitName":"1包","extendPrice":0,"extendUnitName":null,"commissionId":"164","commission":500},{"businessName":"酒鬼花生","businessId":"925572769356193792","priceName":"标准价","priceId":223,"primaryPrice":300,"primaryUnitName":"1包","extendPrice":0,"extendUnitName":null,"commissionId":"1","commission":100}]}]
     * memberActivityDetail : {"id":30,"name":"12332","type":1,"startTime":null,"endTime":null,"status":1,"operatorId":"770559670128480256","operatorName":null,"businessNo":"MC171110002","packageList":[{"id":68,"activityId":30,"type":1,"name":"1","clubId":"770559669897797632","amount":1000,"commissionAmount":100,"packageItems":[{"id":97,"packageId":68,"type":4,"name":"100","itemId":"","oriAmount":10000,"itemCount":1},{"id":98,"packageId":68,"type":3,"name":"按摩比赛的向１","itemId":"873024640442703872","oriAmount":200,"itemCount":1}]},{"id":69,"activityId":30,"type":1,"name":"2","clubId":"770559669897797632","amount":10000,"commissionAmount":1000,"packageItems":[{"id":99,"packageId":69,"type":1,"name":"礼品券副标题","itemId":"897298622775828480","oriAmount":0,"itemCount":1},{"id":100,"packageId":69,"type":2,"name":"折扣券8折","itemId":"897298460233965568","oriAmount":0,"itemCount":1}]},{"id":70,"activityId":30,"type":1,"name":"3","clubId":"770559669897797632","amount":20000,"commissionAmount":10000,"packageItems":[]}],"subStatus":2}
     * bellList : [{"id":1,"clubId":"0","name":"轮钟","type":"round","createTime":1509521292000,"modifyTime":null,"operatorId":null},{"id":2,"clubId":"0","name":"点钟","type":"specify","createTime":1509521292000,"modifyTime":null,"operatorId":null},{"id":3,"clubId":"0","name":"call钟","type":"call","createTime":1509521292000,"modifyTime":null,"operatorId":null},{"id":4,"clubId":"0","name":"加钟","type":"extend","createTime":1509521292000,"modifyTime":null,"operatorId":null},{"id":32,"clubId":"770559669897797632","name":"123","type":"custom_123","createTime":1510055757000,"modifyTime":null,"operatorId":"752371867058180096"},{"id":50,"clubId":"770559669897797632","name":"那不就好棒棒","type":"custom_那不就好棒棒","createTime":1510661062000,"modifyTime":null,"operatorId":"770559670128480256"}]
     * serviceCommissionList : [{"categoryDto":{"id":"851634869048975360","name":"大赛的看","image":"73623","scope":"spa","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/74/ooYBAFKcgpyASBWjAAAAAlscE2s187.flv?st=Ir_0LAp4kG9hpZW5W7rLYg&e=1513931286"},"commissionList":[{"businessName":"按到心痛","businessId":"926008795287527424","priceName":"标准价","priceId":252,"primaryPrice":1100,"primaryUnitName":"100分钟","extendPrice":1000,"extendUnitName":"100分钟","bellCommissionList":[{"bellId":1,"bellName":"轮钟","commissionId":"171","commission":800},{"bellId":2,"bellName":"点钟","commissionId":"172","commission":800},{"bellId":3,"bellName":"call钟","commissionId":"173","commission":800}]}]},{"categoryDto":{"id":"851635245856858112","name":"特人人","image":"73580","scope":"spa","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/75/oYYBAFKceJ-ALToCAAAAAlscE2s703.flv?st=oC9_Io_5nFjnbq9BTCFAhg&e=1513931286"},"commissionList":[{"businessName":"spa","businessId":"851641724034883584","priceName":"标准价","priceId":104,"primaryPrice":300,"primaryUnitName":"50分钟","extendPrice":0,"extendUnitName":null,"bellCommissionList":[{"bellId":1,"bellName":"轮钟","commissionId":"49","commission":100},{"bellId":2,"bellName":"点钟","commissionId":"50","commission":100},{"bellId":3,"bellName":"call钟","commissionId":"51","commission":100}]}]},{"categoryDto":{"id":"857786708865851392","name":"辐射服金额","image":"73630","scope":"spa","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/76/oIYBAFKchFaAVWSKAAAAAlscE2s049.flv?st=0GBxKGJTHQ0tqPXb3W_2ag&e=1513931286"},"commissionList":[{"businessName":"xm123456","businessId":"897299336759615488","priceName":"标准价","priceId":121,"primaryPrice":10000,"primaryUnitName":"60分钟","extendPrice":0,"extendUnitName":null,"bellCommissionList":[{"bellId":1,"bellName":"轮钟","commissionId":"56","commission":2000}]},{"businessName":"按摩洗脚","businessId":"897299431479582720","priceName":"标准价","priceId":122,"primaryPrice":5000,"primaryUnitName":"60分钟","extendPrice":0,"extendUnitName":null,"bellCommissionList":[{"bellId":1,"bellName":"轮钟","commissionId":"63","commission":1000},{"bellId":2,"bellName":"点钟","commissionId":"64","commission":2000},{"bellId":3,"bellName":"call钟","commissionId":"65","commission":12000}]}]}]
     * discountMallSwitch : [{"code":"item_card","name":"项目次卡","status":"Y"},{"code":"item_package","name":"混合套餐","status":"Y"}]
     */

    public OrderParameterBean orderParameter; //预约参数
    public MemberActivityDetailBean memberActivityDetail; //会员充值活动详情
    public List<SalesCommissionListBean> salesCommissionList;//推销提成列表
    public List<BellListBean> bellList;  //会所上钟类型列表
    public List<ServiceCommissionListBean> serviceCommissionList; //服务提成列表
    public List<DiscountMallSwitchBean> discountMallSwitch; //特惠商城列表
}