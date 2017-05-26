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
import com.xmd.cashier.activity.CashierActivity;
import com.xmd.cashier.activity.ConfirmActivity;
import com.xmd.cashier.activity.LoginActivity;
import com.xmd.cashier.activity.MainActivity;
import com.xmd.cashier.activity.MemberPayActivity;
import com.xmd.cashier.activity.OnlinePayActivity;
import com.xmd.cashier.activity.OrderRecordActivity;
import com.xmd.cashier.activity.PayTypeChoiceActivity;
import com.xmd.cashier.activity.PointsPhoneActivity;
import com.xmd.cashier.activity.RecordNavigationActivity;
import com.xmd.cashier.activity.ScanPayActivity;
import com.xmd.cashier.activity.ScanPayResultActivity;
import com.xmd.cashier.activity.SettingActivity;
import com.xmd.cashier.activity.SettleCurrentActivity;
import com.xmd.cashier.activity.SettleDetailActivity;
import com.xmd.cashier.activity.SettleRecordActivity;
import com.xmd.cashier.activity.VerificationActivity;
import com.xmd.cashier.activity.VerificationItemDetailActivity;
import com.xmd.cashier.activity.VerifyCheckInfoActivity;
import com.xmd.cashier.activity.VerifyCommonActivity;
import com.xmd.cashier.activity.VerifyCouponActivity;
import com.xmd.cashier.activity.VerifyOrderActivity;
import com.xmd.cashier.activity.VerifyPrizeActivity;
import com.xmd.cashier.activity.VerifyRecordActivity;
import com.xmd.cashier.activity.VerifyRecordDetailActivity;
import com.xmd.cashier.activity.VerifyServiceActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.PrizeInfo;
import com.xmd.cashier.dal.bean.VerificationItem;

public class UiNavigation {
    public static final int REQUEST_CODE_MEMBER_SCAN = 1;
    public static final int REQUEST_CODE_SET_COUPON = 2;

    public static void gotoMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void gotoVerificationActivity(Context context) {
        ((Activity) context).startActivityForResult(new Intent(context, VerificationActivity.class), REQUEST_CODE_SET_COUPON);
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

    public static void gotoVerificationItemDetailActivity(Context context, VerificationItem item) {
        Intent intent = new Intent(context, VerificationItemDetailActivity.class);
        intent.putExtra(VerificationItemDetailActivity.EXTRA_VERIFICATION_ITEM, item);
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

    public static void gotoMemberPayActivity(Context context) {
        Intent intent = new Intent(context, MemberPayActivity.class);
        context.startActivity(intent);
    }

    public static void gotoPointsPhoneActivity(Context context) {
        Intent intent = new Intent(context, PointsPhoneActivity.class);
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
    public static void gotoVerifyNormalCouponActivity(Context context, CouponInfo info,boolean isShow) {
        Intent intent = new Intent(context, VerifyCouponActivity.class);
        intent.putExtra(AppConstants.EXTRA_NORMAL_COUPON_INFO, info);
        intent.putExtra(AppConstants.EXTRA_IS_SHOW, isShow);
        context.startActivity(intent);
    }

    // 预约订单
    public static void gotoVerifyOrderActivity(Context context, OrderInfo info,boolean isShow) {
        Intent intent = new Intent(context, VerifyOrderActivity.class);
        intent.putExtra(AppConstants.EXTRA_ORDER_VERIFY_INFO, info);
        intent.putExtra(AppConstants.EXTRA_IS_SHOW, isShow);
        context.startActivity(intent);
    }

    // 项目券
    public static void gotoVerifyServiceCouponActivity(Context context, CouponInfo info) {
        Intent intent = new Intent(context, VerifyServiceActivity.class);
        intent.putExtra(AppConstants.EXTRA_SERVICE_COUPON_INFO, info);
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
}

