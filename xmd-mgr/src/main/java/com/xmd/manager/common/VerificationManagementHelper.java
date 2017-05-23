package com.xmd.manager.common;

import android.app.Activity;

import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AwardVerificationResult;
import com.xmd.manager.service.response.DefaultVerificationDetailResult;
import com.xmd.manager.service.response.PayOrderDetailResult;
import com.xmd.manager.service.response.VerificationCouponDetailResult;
import com.xmd.manager.service.response.VerificationServiceCouponResult;
import com.xmd.manager.verification.VerificationListActivity;
import com.xmd.manager.window.ManagerVerificationActivity;
import com.xmd.manager.window.PayActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Administrator on 2016/12/7.
 */

public class VerificationManagementHelper {

    public static String VERIFICATION_COUPON_TYPE = "coupon";//优惠券
    public static String VERIFICATION_ORDER_TYPE = "order";//订单
    public static String VERIFICATION_PAY_FOR_OTHER_TYPE = "pay_for_other";//请客
    public static String VERIFICATION_PAID_SERVICE_ITEM_TYPE = "paid_service_item"; //旧抢项目
    public static String VERIFICATION_SERVICE_ITEM_COUPON_TYPE = "service_item_coupon";//项目券
    public static String VERIFICATION_PHONE_TYPE = "phone";//电话
    public static String VERIFICATION_AWARD_TYPE = "lucky_wheel";//奖品
    public static String VERIFICATION_COUPON_ACTION = "coupon_action";//活动
    public static String VERIFICATION_DEFAULT_TYPE = "default_type";//默认未知类型
    public static String VERIFICATION_TYPE = "verification_type";
    public static String VERIFICATION_OBJECT = "verification_object";
    public static String VERIFICATION_INFO_LIST = "info_list";

    private Activity mActivity;

    private Subscription mVerificationCouponSubscription;
    private Subscription mVerificationOrderSubscription;
    private Subscription mVerificationDefaultSubscription;
    private Subscription mVerificationServiceItemCouponType;
    private Subscription mVerificationLuckyWheelSubscription;


    public static void checkVerificationType(String code) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CHECK_INFO_TYPE_GET, code);
    }

    public static VerificationManagementHelper getInstance() {
        return new VerificationManagementHelper();
    }

    public void initializeHelper(Activity activity) {
        this.mActivity = activity;
        toSubscriptionInfo();
    }

    public void toSubscriptionInfo() {
        mVerificationCouponSubscription = RxBus.getInstance().toObservable(VerificationCouponDetailResult.class).subscribe(
                couponResult -> {
                    ManagerVerificationActivity.startManagerVerificationActivity(mActivity, VERIFICATION_COUPON_TYPE, couponResult.respData);
                }
        );
        mVerificationOrderSubscription = RxBus.getInstance().toObservable(PayOrderDetailResult.class).subscribe(
                orderResult -> {
                    ManagerVerificationActivity.startManagerVerificationActivity(mActivity, VERIFICATION_ORDER_TYPE, orderResult.respData);
                }
        );
        mVerificationDefaultSubscription = RxBus.getInstance().toObservable(DefaultVerificationDetailResult.class).subscribe(
                defaultResult -> {
                    if (defaultResult.statusCode == 200) {
                        if (defaultResult.respData.type.equals(VERIFICATION_PAY_FOR_OTHER_TYPE)) {
                            PayActivity.startPayActivity(mActivity, defaultResult.respData.code);
                        } else {
                            ManagerVerificationActivity.startManagerVerificationActivity(mActivity, VERIFICATION_DEFAULT_TYPE, defaultResult.respData);
                        }
                    }
                }
        );
        mVerificationServiceItemCouponType = RxBus.getInstance().toObservable(VerificationServiceCouponResult.class).subscribe(
                serviceResult -> {
                    ManagerVerificationActivity.startManagerVerificationActivity(mActivity, VERIFICATION_SERVICE_ITEM_COUPON_TYPE, serviceResult.respData);
                }
        );
        mVerificationLuckyWheelSubscription = RxBus.getInstance().toObservable(AwardVerificationResult.class).subscribe(
                awardVerificationResult -> {
                    ManagerVerificationActivity.startManagerVerificationActivity(mActivity, VERIFICATION_AWARD_TYPE, awardVerificationResult.respData);
                }

        );

    }

    public void destroySubscription() {
        RxBus.getInstance().unsubscribe(mVerificationCouponSubscription, mVerificationOrderSubscription, mVerificationDefaultSubscription, mVerificationServiceItemCouponType, mVerificationLuckyWheelSubscription);
    }

    public static void handlerVerificationType(Activity activity, String resultType, String code) {

        if (resultType.equals(VERIFICATION_COUPON_TYPE)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CHECK_INFO_COUPON_DETAIL, code);
            return;
        } else if (resultType.equals(VERIFICATION_ORDER_TYPE)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PAY_ORDER_DETAIL, code);
            return;
        } else if (resultType.equals(VERIFICATION_SERVICE_ITEM_COUPON_TYPE)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON, code);
            return;
        } else if (resultType.equals(VERIFICATION_PHONE_TYPE)) {
            VerificationListActivity.startCustomerCouponListActivity(activity, code);
            return;
        } else if (resultType.equals(VERIFICATION_AWARD_TYPE)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VERIFICATION_AWARD_DETAIL, code);
        } else {
            Map<String, String> params = new HashMap<>();
            params.put(RequestConstant.KEY_VERIFICATION_CODE, code);
            params.put(RequestConstant.KEY_VERIFICATION_TYPE, resultType);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_VERIFICATION_COMMON_DETAIL, params);
        }
    }

}
