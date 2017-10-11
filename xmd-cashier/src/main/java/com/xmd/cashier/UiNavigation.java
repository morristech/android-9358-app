package com.xmd.cashier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.xmd.cashier.activity.BillDetailActivity;
import com.xmd.cashier.activity.BillRecordActivity;
import com.xmd.cashier.activity.BillSearchActivity;
import com.xmd.cashier.activity.CashPayActivity;
import com.xmd.cashier.activity.CashierActivity;
import com.xmd.cashier.activity.ConfigurationActivity;
import com.xmd.cashier.activity.ConfirmActivity;
import com.xmd.cashier.activity.DiscountCouponActivity;
import com.xmd.cashier.activity.GiftActActivity;
import com.xmd.cashier.activity.LoginActivity;
import com.xmd.cashier.activity.MainActivity;
import com.xmd.cashier.activity.McardInfoActivity;
import com.xmd.cashier.activity.McardPhoneActivity;
import com.xmd.cashier.activity.McardSuccessActivity;
import com.xmd.cashier.activity.MemberCashierActivity;
import com.xmd.cashier.activity.MemberNavigationActivity;
import com.xmd.cashier.activity.MemberReadActivity;
import com.xmd.cashier.activity.MemberRechargeActivity;
import com.xmd.cashier.activity.MemberRecordActivity;
import com.xmd.cashier.activity.MemberScanActivity;
import com.xmd.cashier.activity.OnlinePayActivity;
import com.xmd.cashier.activity.OrderRecordActivity;
import com.xmd.cashier.activity.PayTypeChoiceActivity;
import com.xmd.cashier.activity.RecordNavigationActivity;
import com.xmd.cashier.activity.ScanPayActivity;
import com.xmd.cashier.activity.ScanPayResultActivity;
import com.xmd.cashier.activity.SettingActivity;
import com.xmd.cashier.activity.SettleCurrentActivity;
import com.xmd.cashier.activity.SettleDetailActivity;
import com.xmd.cashier.activity.SettleRecordActivity;
import com.xmd.cashier.activity.StatisticsActivity;
import com.xmd.cashier.activity.StatisticsSettingActivity;
import com.xmd.cashier.activity.TechnicianActivity;
import com.xmd.cashier.activity.VerificationActivity;
import com.xmd.cashier.activity.VerifyCheckInfoActivity;
import com.xmd.cashier.activity.VerifyCommonActivity;
import com.xmd.cashier.activity.VerifyCouponActivity;
import com.xmd.cashier.activity.VerifyOrderActivity;
import com.xmd.cashier.activity.VerifyPrizeActivity;
import com.xmd.cashier.activity.VerifyRecordActivity;
import com.xmd.cashier.activity.VerifyRecordDetailActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.GiftActivityInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.PrizeInfo;

public class UiNavigation {
    public static final int REQUEST_CODE_MEMBER_SCAN = 1;

    public static void gotoMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void gotoVerificationActivity(Context context) {
        context.startActivity(new Intent(context, VerificationActivity.class));
    }

    public static void gotoScanCodeActivity(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.FORMATS, BarcodeFormat.QR_CODE);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_MEMBER_SCAN);
    }

    public static void gotoLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void gotoConfirmActivity(Context context, String showMessage) {
        Intent intent = new Intent(context, ConfirmActivity.class);
        intent.putExtra(ConfirmActivity.EXTRA_MESSAGE, showMessage);
        context.startActivity(intent);
    }

    public static void gotoConfirmActivity(Context context) {
        gotoConfirmActivity(context, null);
    }

    public static void gotoMemberCashierActivity(Context context) {
        Intent intent = new Intent(context, MemberCashierActivity.class);
        context.startActivity(intent);
    }

    public static void gotoPayTypeChoice(Context context) {
        Intent intent = new Intent(context, PayTypeChoiceActivity.class);
        context.startActivity(intent);
    }

    // 交易流水
    public static void gotoBillRecordActivity(Context context) {
        Intent intent = new Intent(context, BillRecordActivity.class);
        context.startActivity(intent);
    }

    // 交易详情
    public static void gotoBillDetailActivity(Context context, BillInfo info) {
        Intent intent = new Intent(context, BillDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_BILL_INFO, info);
        context.startActivity(intent);
    }

    // 交易查询
    public static void gotoBillSearchActivity(Context context) {
        Intent intent = new Intent(context, BillSearchActivity.class);
        context.startActivity(intent);
    }

    // 核销优惠券和付费预约
    public static void gotoVerifyCheckInfoActivity(Context context, String phone) {
        Intent intent = new Intent(context, VerifyCheckInfoActivity.class);
        intent.putExtra(AppConstants.EXTRA_PHONE_VERIFY, phone);
        context.startActivity(intent);
    }

    // 券
    public static void gotoVerifyCouponActivity(Context context, CouponInfo info, boolean isShow) {
        Intent intent = new Intent(context, VerifyCouponActivity.class);
        intent.putExtra(AppConstants.EXTRA_COUPON_VERIFY_INFO, info);
        intent.putExtra(AppConstants.EXTRA_IS_SHOW, isShow);
        context.startActivity(intent);
    }

    // 预约订单
    public static void gotoVerifyOrderActivity(Context context, OrderInfo info, boolean isShow) {
        Intent intent = new Intent(context, VerifyOrderActivity.class);
        intent.putExtra(AppConstants.EXTRA_ORDER_VERIFY_INFO, info);
        intent.putExtra(AppConstants.EXTRA_IS_SHOW, isShow);
        context.startActivity(intent);
    }

    // 奖品
    public static void gotoVerifyPrizeActivity(Context context, PrizeInfo info) {
        Intent intent = new Intent(context, VerifyPrizeActivity.class);
        intent.putExtra(AppConstants.EXTRA_PRIZE_VERIFY_INFO, info);
        context.startActivity(intent);
    }

    // 通用
    public static void gotoVerifyCommonActivity(Context context, CommonVerifyInfo info) {
        Intent intent = new Intent(context, VerifyCommonActivity.class);
        intent.putExtra(AppConstants.EXTRA_COMMON_VERIFY_INFO, info);
        context.startActivity(intent);
    }

    // 在线买单
    public static void gotoOnlinePayActivity(Context context) {
        Intent intent = new Intent(context, OnlinePayActivity.class);
        context.startActivity(intent);
    }

    // 订单列表
    public static void gotoOrderRecordActivity(Context context) {
        Intent intent = new Intent(context, OrderRecordActivity.class);
        context.startActivity(intent);
    }

    // 收银
    public static void gotoCashierActivity(Context context) {
        Intent intent = new Intent(context, CashierActivity.class);
        context.startActivity(intent);
    }

    public static void gotoSettleCurrentActivity(Context context) {
        Intent intent = new Intent(context, SettleCurrentActivity.class);
        context.startActivity(intent);
    }

    public static void gotoSettleRecordActivity(Context context) {
        Intent intent = new Intent(context, SettleRecordActivity.class);
        context.startActivity(intent);
    }

    public static void gotoSettleDetailActivity(Context context, String param) {
        Intent intent = new Intent(context, SettleDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_RECORD_ID, param);
        context.startActivity(intent);
    }

    public static void gotoRecordNavigationActivity(Context context) {
        Intent intent = new Intent(context, RecordNavigationActivity.class);
        context.startActivity(intent);
    }

    // 核销记录
    public static void gotoVerifyRecordActivity(Context context) {
        Intent intent = new Intent(context, VerifyRecordActivity.class);
        context.startActivity(intent);
    }

    // 核销详情
    public static void gotoVerifyDetailActivity(Context context, String param) {
        Intent intent = new Intent(context, VerifyRecordDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_RECORD_ID, param);
        context.startActivity(intent);
    }

    // 在线买单扫码
    public static void gotoScanPayActivity(Context context) {
        Intent intent = new Intent(context, ScanPayActivity.class);
        context.startActivity(intent);
    }

    // 在线买单结果
    public static void gotoScanPayResultActivity(Context context, OnlinePayInfo info) {
        Intent intent = new Intent(context, ScanPayResultActivity.class);
        intent.putExtra(AppConstants.EXTRA_ONLINE_PAY_INFO, info);
        context.startActivity(intent);
    }

    // 系统设置
    public static void gotoSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    // ------------------会员------------------
    public static void gotoMemberNavigationActivity(Context context) {
        Intent intent = new Intent(context, MemberNavigationActivity.class);
        context.startActivity(intent);
    }

    // 会员账户记录
    public static void gotoMemberRecordActivity(Context context) {
        Intent intent = new Intent(context, MemberRecordActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMcardPhoneActivity(Context context) {
        Intent intent = new Intent(context, McardPhoneActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMcardInfoActivity(Context context) {
        Intent intent = new Intent(context, McardInfoActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMemberReadActivity(Context context, String bizType) {
        Intent intent = new Intent(context, MemberReadActivity.class);
        intent.putExtra(AppConstants.EXTRA_MEMBER_BUSINESS_TYPE, bizType);
        context.startActivity(intent);
    }

    public static void gotoMemberRechargeActivity(Context context) {
        Intent intent = new Intent(context, MemberRechargeActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMcardSuccessActivity(Context context) {
        Intent intent = new Intent(context, McardSuccessActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMemberScanActivity(Context context, String channel) {
        Intent intent = new Intent(context, MemberScanActivity.class);
        intent.putExtra(AppConstants.EXTRA_MEMBER_CASHIER_METHOD, channel);
        context.startActivity(intent);
    }

    public static void gotoTechnicianActivity(Context context) {
        Intent intent = new Intent(context, TechnicianActivity.class);
        context.startActivity(intent);
    }

    public static void gotoDiscountCouponActivity(Context context, String code) {
        Intent intent = new Intent(context, DiscountCouponActivity.class);
        intent.putExtra(AppConstants.EXTRA_COUPON_CODE, code);
        context.startActivity(intent);
    }

    public static void gotoConfigurationActivity(Context context) {
        Intent intent = new Intent(context, ConfigurationActivity.class);
        context.startActivity(intent);
    }

    public static void gotoCashPayActivity(Context context, int amount) {
        Intent intent = new Intent(context, CashPayActivity.class);
        intent.putExtra(AppConstants.EXTRA_CASH_AMOUNT, amount);
        context.startActivity(intent);
    }

    public static void gotoGiftActActivity(Context context, GiftActivityInfo info) {
        Intent intent = new Intent(context, GiftActActivity.class);
        intent.putExtra(AppConstants.EXTRA_GIFT_ACTIVITY_INFO, info);
        context.startActivity(intent);
    }

    public static void gotoStatisticsActivity(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);
    }

    public static void gotoStatisticsSettingActivity(Context context) {
        Intent intent = new Intent(context, StatisticsSettingActivity.class);
        context.startActivity(intent);
    }
}

