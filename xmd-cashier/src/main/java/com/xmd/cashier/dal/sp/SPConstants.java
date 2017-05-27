package com.xmd.cashier.dal.sp;

/**
 * Created by heyangya on 16-8-23.
 */

public class SPConstants {
    public static String SERVER_ADDRESS = "7231b88f589e7bb752a0c115f960e6b5";

    // ------ 小票打印开关 -----
    public static String VERIFY_SUCCESS_PRINT_SWITCH = "d51bcf1643d9648f0b7d1e943c556005";  //核销成功
    public static String ORDER_ACCEPT_PRINT_SWITCH = "908fa11351d7004e90bfce845c50bc95";    //订单接受
    public static String ORDER_REJECT_PRINT_SWITCH = "3ee68d40c7d2fbae2dfae18be80a83f2";    //订单拒绝
    public static String ONLINE_PASS_PRINT_SWITCH = "6a3891bc01605d89c8ccdd4c1b822de3";     //买单确认
    public static String ONLINE_UNPASS_PRINT_SWITCH = "3286de45a71ed53bc6eb24a9fd0e3278";   //买单异常
    public static String ONLINE_PAY_PRINT_SWITCH = "c68b6a54fb8bc80e64fe6aa9aa4c3a39";      //微信买单
}
