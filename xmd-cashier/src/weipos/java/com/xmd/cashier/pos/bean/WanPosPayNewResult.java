package com.xmd.cashier.pos.bean;

/**
 * Created by zr on 18-2-7.
 */

public class WanPosPayNewResult<T> {
    public int errCode;
    public String errMsg;
    public T data;

    public class RespData {
        public String out_trade_no; //	商户订单号	商户系统内部订单号，确保在商户体系内部唯一		String(32)
        public String trade_status;    //交易状态	详见附录-交易状态		String
        public String cashier_trade_no;    //旺收银订单号	实际为旺收银的流水号		String
        public long discount_platform;    //平台优惠			long
        public long discount_channel;    //渠道优惠			long
        public int pay_type;    //支付方式			int
        public String attach;    //附加数据			String
        public String en;    //设备号			String
        public String operator;    //操作人员			String
        public BankRespData bank;    //银行卡交易信息	银行卡支付返回，Json字符串		String
        public WxRespData wxpay;    //微信交易信息	微信支付返回，Json字符串		String
        public AliRespData alipay;    //支付宝交易信息	支付宝支付返回，Json字符串		String
        public String es_url;    //电子签购单	电子签购单Url		String
        public long total_fee;    //订单金额	订单金额，单位(分)
    }

    public class BankRespData {
        public String voucher_no;    //凭证号		String
        public String bank_no;    //银行卡号		String
        public String ref_no;    //参考号		String
    }

    public class WxRespData {
        public String open_id;    //用户在商户appid下的唯一标识		String
        public String transaction_id;    //微信支付订单号		String
        public String mch_id;    //微信商户号		String
    }

    public class AliRespData {
        public String seller_id;    //卖家-收款支付宝账号对应的支付宝唯一用户号		String
        public String buyer_logon_id;    //买家-支付宝登陆账号		String
        public String trade_no;    //支付宝交易号		String
    }
}


/*
{
    "data": {
    "out_trade_no": "20180207154847"
    },
    "errCode": 30002,
    "errMsg": "操作员手动取消"
}

{"errCode":-1,"data":"","errMsg":"旺收银服务已断开连接!"}

{"errCode":30001,"errMsg":"有交易正在进行, 请稍后重试","data":"{}"}

{"data":{"out_trade_no":"9611748449410088960000"},"errCode":7016,"errMsg":"核心交易异常:签到失败:06:C:错误"}
*/

/*
{
    "data": {
        "cashier_trade_no": "3000117695720180207000000001",
        "discount_platform": 0,
        "pay_type": 2,
        "discount_channel": 0,
        "attach": "我是一条胖胖的附加数据，从哪里来，返回到哪里去!",
        "total_fee": 100,
        "es_url": "http://signcashier3.wangpos.com/signature-service/s?signNo=NawBVYMLhL",
        "en": "24970af6",
        "out_trade_no": "20180207154847",
        "trade_status": 101,
        "operator": "Kevin"
    },
    "errCode": 0,
    "errMsg": "成功"
}
*/
