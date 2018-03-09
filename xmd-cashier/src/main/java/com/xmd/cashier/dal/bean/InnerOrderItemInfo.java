package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-11-2.
 * 内网订单消费项
 */

public class InnerOrderItemInfo implements Serializable {
    public String createTime;    //创建时间
    public String endTime;        //预计结束时间
    public long id;    //账单项ID
    public int itemAmount;    //该项价格	单位为分
    public int itemCount;    //消费数量
    public String itemId;    //项目ID
    public String itemName;    //项目名称
    public String itemType;    //项目类型	spa-水疗项目;goods-实物商品
    public String itemUnit;     //项目单位
    public long modifyTime;    //修改时间
    public long orderId;    //关联账单ID
    public int status;    //账单项状态

    public List<InnerEmployeeInfo> employeeList;
}
