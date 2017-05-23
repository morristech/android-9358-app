package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 16-12-9.
 * 通用核销
 */

public class CommonVerifyInfo implements Serializable {
    // 核销码
    public String code;
    // 核销类型标题
    public String title;
    // 核销码类型
    public String type;
    // 是否需要补充金额参数
    public boolean needAmount;
    // 核销单信息项
    public CommonVerifyMsgInfo info;

    public class CommonVerifyMsgInfo implements Serializable {
        // 核销单金额
        public String amount;
        // 列表单信息头
        public String first;
        // 备注说明
        public String remark;
        // 列表单信息
        public List<CommonVerifyMsgItem> list;
    }

    public class CommonVerifyMsgItem implements Serializable {
        // 项内容
        public String text;
        // 项标题
        public String title;
    }
}
