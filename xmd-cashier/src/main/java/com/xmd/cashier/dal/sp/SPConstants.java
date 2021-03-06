package com.xmd.cashier.dal.sp;

/**
 * Created by heyangya on 16-8-23.
 */

public class SPConstants {
    public static String SERVER_ADDRESS = "7231b88f589e7bb752a0c115f960e6b5";

    // ------ 小票打印开关 -----
    public static String GLOBAL_PRINT_CLIENT_SWITCH = "c166f934763810cccfb73c207bdbd364";   //打印客户联

    public static String ORDER_ACCEPT_PRINT_SWITCH = "908fa11351d7004e90bfce845c50bc95";    //订单接受
    public static String ORDER_REJECT_PRINT_SWITCH = "3ee68d40c7d2fbae2dfae18be80a83f2";    //订单拒绝
    public static String ONLINE_PASS_PRINT_SWITCH = "6a3891bc01605d89c8ccdd4c1b822de3";     //买单确认
    public static String ONLINE_UNPASS_PRINT_SWITCH = "3286de45a71ed53bc6eb24a9fd0e3278";   //买单异常

    public static String FASTPAY_PUSH_TAG = "42f8ab3f8a309888af0bb6275abef2ef";
    public static String ORDER_PUSH_TAG = "79c5273b8d7b56703921f63d4c56ffbd";

    public static String STATISTICS_START_TIME = "66d381965d0c2f37a070cef5f8657ee2";    //统计开始时间
    public static String STATISTICS_END_TIME = "cb8157df0c78fd7a0c31575d83b008e1";      //统计截止时间
    public static String STATISTICS_FIRST_SETTING = "074f1a261d36515cd2117f812ed2852a"; //是否设置过时间

    public static String LAST_UPLOAD_TIME = "81b4446f2465af17a01c611e8b636622";     //最近一次上传日志的时间

    public static String ONLINE_PAY_PRIORITY = "5f67ebf1b442b35c391dc504b7365a11";    //微信支付宝支付优先级: auth | bitmap
}
