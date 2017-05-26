package com.xmd.cashier.manager;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.BillInfo;
import com.xmd.cashier.dal.bean.Trade;

import java.util.Date;

/**
 * Created by zr on 17-5-26.
 * 处理打印
 */

public class PrintManager {
    private IPos mPos;
    private static PrintManager mInstance = new PrintManager();

    public static PrintManager getInstance() {
        return mInstance;
    }

    private PrintManager() {
        mPos = PosFactory.getCurrentCashier();
    }
    
    // 打印在线买单交易信息
    public void printOnlinePay(Trade trade) {
        if (trade.tradeStatus != AppConstants.TRADE_STATUS_SUCCESS) {
            return;
        }
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(结账单)");
        mPos.printDivide();

        mPos.printText("消费", Utils.moneyToStringEx(trade.getOriginMoney()) + " 元");
        mPos.printText("减免", Utils.moneyToStringEx(trade.getCouponDiscountMoney() + trade.getUserDiscountMoney()) + " 元");

        mPos.printDivide();
        mPos.printRight("实收 " + Utils.moneyToStringEx(trade.getOnlinePayPaidMoney()) + " 元");
        mPos.printDivide();

        mPos.printText("交易号:", trade.tradeNo);
        mPos.printText("付款方式:", "小摩豆在线买单");
        mPos.printText("交易时间:", trade.tradeTime);
        mPos.printText("打印时间:", DateUtils.doDate2String(new Date()));
        mPos.printText("收银员:", AccountManager.getInstance().getUser().userName);

        if (trade.qrCodeBytes != null) {
            mPos.printBitmap(trade.qrCodeBytes);
        }
        mPos.printCenter("-- 微信扫码，选技师，抢优惠 --");
        mPos.printEnd();
    }

    // 打印交易信息
    public void print(Trade trade) {
        if (trade.tradeStatus != AppConstants.TRADE_STATUS_SUCCESS) {
            return;
        }
        XLogger.i("print trade info ....");
        mPos.printText("交易号 ：" + trade.tradeNo);
        mPos.printText("订单金额：", "￥" + Utils.moneyToStringEx(trade.getOriginMoney()));
        mPos.printText("减免金额：", "￥" + Utils.moneyToStringEx(trade.getReallyDiscountMoney() + trade.getMemberPaidDiscountMoney()));
        XLogger.i("减扣类型：" + trade.getDiscountType());
        switch (trade.getDiscountType()) {
            case Trade.DISCOUNT_TYPE_COUPON:
                mPos.printText("|--优惠金额：", "￥" + Utils.moneyToStringEx(trade.getCouponDiscountMoney()));
                break;
            case Trade.DISCOUNT_TYPE_USER:
                mPos.printText("|--手动减免：", "￥" + Utils.moneyToStringEx(trade.getUserDiscountMoney()));
                break;
            case Trade.DISCOUNT_TYPE_NONE:
                mPos.printText("|--其他优惠：", "￥" + Utils.moneyToStringEx(trade.getCouponDiscountMoney() + trade.getUserDiscountMoney()));
            default:
                break;
        }
        mPos.printText("|--会员折扣：", "￥" + Utils.moneyToStringEx(trade.getMemberPaidDiscountMoney()));
        mPos.printText("实收金额：", "￥" + Utils.moneyToStringEx(trade.getPosMoney() + trade.getMemberPaidMoney()));
        mPos.printText("|--" + (TextUtils.isEmpty(trade.getPosPayTypeString()) ? "其他支付" : trade.getPosPayTypeString()) + "：", "￥" + Utils.moneyToStringEx(trade.getPosMoney()));
        mPos.printText("|--会员支付：", "￥" + Utils.moneyToStringEx(trade.getMemberPaidMoney()));

        if (trade.memberPoints > 0) {
            mPos.printText("获赠会员积分：" + trade.memberPoints);
        }
        mPos.printText("交易时间：" + trade.tradeTime);
        mPos.printText("收银员  ：" + AccountManager.getInstance().getUser().userName);
        if (trade.qrCodeBytes != null) {
            mPos.printBitmap(trade.qrCodeBytes);
        }
        if (trade.posPoints > 0) {
            if (trade.posPointsPhone == null) {
                mPos.printCenter("微信扫描二维码，立即领取" + trade.posPoints + "积分");
            } else {
                mPos.printCenter("已赠送" + trade.posPoints + "积分到"
                        + Utils.getSecretFormatPhoneNumber(trade.posPointsPhone) + ",关注9358立即查看");
            }
        } else {
            mPos.printCenter("扫一扫，关注9358，约技师，享优惠");
        }
        mPos.printEnd();
    }

    // 补打交易流水
    public void print(BillInfo info) {
        String format = MainApplication.getInstance().getString(R.string.cashier_money);
        mPos.printCenter("深圳市小摩豆网络科技");
        mPos.printCenter("(消费单)");
        mPos.printCenter("--重打小票--");

        mPos.printDivide();
        mPos.printText("订单金额: ", String.format(format, Utils.moneyToStringEx(info.originMoney)));

        mPos.printText("减免金额: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.memberPayDiscountMoney + info.couponDiscountMoney)));
        switch (info.discountType) {
            case Trade.DISCOUNT_TYPE_COUPON:
                mPos.printText("|--优惠金额: ", String.format(format, Utils.moneyToStringEx(info.couponDiscountMoney)));
                break;
            case Trade.DISCOUNT_TYPE_USER:
                mPos.printText("|--手动减免: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney)));
                break;
            case Trade.DISCOUNT_TYPE_NONE:
            default:
                mPos.printText("|--其他优惠: ", String.format(format, Utils.moneyToStringEx(info.userDiscountMoney + info.couponDiscountMoney)));
                break;
        }
        mPos.printText("|--会员折扣: ", String.format(format, Utils.moneyToStringEx(info.memberPayDiscountMoney)));

        mPos.printText("实收金额: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney)));
        mPos.printText("|--" + Utils.getPayTypeString(info.posPayType) + ": ", String.format(format, Utils.moneyToStringEx(info.posPayMoney)));
        mPos.printText("|--会员支付: ", String.format(format, Utils.moneyToStringEx(info.memberPayMoney)));

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPos.printText("退款金额: ", String.format(format, Utils.moneyToStringEx(info.refundMoney)));
        }
        mPos.printDivide();

        mPos.printText("交易号: " + info.tradeNo);
        mPos.printText("收款方式: " + Utils.getPayTypeString(info.posPayType));
        mPos.printText("创建时间: " + DateUtils.doLong2String(Long.parseLong(info.payDate)));
        mPos.printText("收款人: " + (TextUtils.isEmpty(info.payOperator) ? "匿名" : info.payOperator));
        mPos.printCenter("\n");

        if (info.status == AppConstants.PAY_STATUS_REFUND) {
            mPos.printText("退款单号: " + info.refundNo);
            mPos.printText("退款时间: " + DateUtils.doLong2String(Long.parseLong(info.refundDate)));
            mPos.printText("退款人: " + (TextUtils.isEmpty(info.refundOperator) ? "匿名" : info.refundOperator));
        }
        mPos.printBitmap(TradeManager.getInstance().getClubQRCodeSync());
        mPos.printCenter("扫一扫，关注9358，约技师，享优惠");
        mPos.printEnd();
    }
}
