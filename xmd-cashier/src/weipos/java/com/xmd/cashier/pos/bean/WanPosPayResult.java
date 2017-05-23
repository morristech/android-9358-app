package com.xmd.cashier.pos.bean;

/**
 * Created by heyangya on 16-8-30.
 */

public class WanPosPayResult {
    public String errCode;
    public String errMsg;
    public String out_trade_no;
    public String trade_status;
    public String cashier_trade_no;
    public String operator;
    public String pay_type;
    public String pay_info;
    public String buy_user_info;

    public BuyUserInfo mBuyUserInfo;


    public static class BuyUserInfo {
        public String voucher_no;
        public String bank_no;
        public String ref_no;
        public String buyer_user;
        public String third_serial_no;
    }

    @Override
    public String toString() {
        return "errCode:" + errCode + ",errMsg:" + errMsg + ",out_trade_no:" + out_trade_no + ",trade_status:" + trade_status + ",cashier_trade_no:" + cashier_trade_no
                + ",operator:" + operator + ",pay_type:" + pay_type + ",pay_info:" + pay_info + ",buy_user_info:" + buy_user_info;
    }

    public static final String PAY_INFO_CANCEL = "取消交易";

    /*
    {"errCode":"-1","errMsg":"取消交易","out_trade_no":"1472550287794","trade_status":null,"input_charset":"UTF-8","cashier_trade_no":null,"pay_type":null,"pay_info":"取消交易"}
    {"errCode":"0","errMsg":"支付成功","out_trade_no":"1472551645306","trade_status":"PAY","input_charset":"UTF-8","cashier_trade_no":"10001528812016083000000010","pay_type":"1003","pay_info":"支付成功","buy_user_info":"{\"buyer_user\":\"oLEzzjpvLtQlOWeP2rR0j-zJa968\",\"third_serial_no\":\"4007502001201608302630775800\"}"}
     */
}
