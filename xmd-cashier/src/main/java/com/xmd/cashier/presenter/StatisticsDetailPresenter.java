package com.xmd.cashier.presenter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.StatisticsDetailContract;
import com.xmd.cashier.dal.bean.OfflineStatisticInfo;
import com.xmd.cashier.dal.bean.OnlineStatisticInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.StatisticsResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Calendar;
import java.util.Date;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-9-19.
 */

public class StatisticsDetailPresenter implements StatisticsDetailContract.Presenter {
    private IPos mPos;

    private Subscription mGetStatisticsSubscription;

    public static final int PAGE_FILTER_DAY = 0;
    public static final int PAGE_FILTER_WEEK = 1;
    public static final int PAGE_FILTER_MONTH = 2;
    public static final int PAGE_FILTER_TOTAL = 3;
    public static final int PAGE_FILTER_CUSTOM = 4;

    public static final int STYLE_SETTLE = 0;
    public static final int STYLE_MONEY = 1;

    private StatisticsDetailContract.View mView;
    private Context mContext;
    private String mStartDate = null;
    private String mEndDate = null;
    private String mStartTime = null;
    private String mEndTime = null;

    private TimePickerView mPickerView;

    private OnlineStatisticInfo mOnlineInfo;
    private OfflineStatisticInfo mOfflineInfo;

    private int mStyleType = STYLE_SETTLE;

    public StatisticsDetailPresenter(Context context, StatisticsDetailContract.View view) {
        mContext = context;
        mView = view;
        mPos = PosFactory.getCurrentCashier();
        mView.setPresenter(this);
    }

    private void initTimePicker() {
        mPickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                // 自定义时间
                String time = DateUtils.doDate2String(date);
                String currentTime = DateUtils.doDate2String(new Date());
                String createTime = AccountManager.getInstance().getClubCreateTime();
                switch (v.getId()) {
                    case R.id.tv_custom_start:
                        mStartTime = time;
                        if (compareDate(mStartTime, createTime, DateUtils.DF_DEFAULT) <= 0) {
                            mStartTime = createTime;
                        }
                        if (compareDate(mStartTime, currentTime, DateUtils.DF_DEFAULT) >= 0) {
                            mStartTime = currentTime;
                        }
                        mView.setCustomStart(mStartTime);
                        break;
                    case R.id.tv_custom_end:
                        mEndTime = time;
                        if (compareDate(mEndTime, createTime, DateUtils.DF_DEFAULT) <= 0) {
                            mEndTime = createTime;
                        }
                        if (compareDate(mEndTime, currentTime, DateUtils.DF_DEFAULT) >= 0) {
                            mEndTime = currentTime;
                        }
                        mView.setCustomEnd(mEndTime);
                        break;
                    default:
                        break;
                }
            }
        })
                .setLayoutRes(R.layout.layout_picker_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView tvChoice = (TextView) v.findViewById(R.id.tv_picker_choice);
                        tvChoice.setText("返回");
                        TextView tvFinish = (TextView) v.findViewById(R.id.tv_picker_finish);
                        tvChoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.dismiss();
                            }
                        });

                        tvFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.dismiss();
                                mPickerView.returnData();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, true})
                .isCenterLabel(false)
                .setTextColorCenter(ResourceUtils.getColor(R.color.colorPink))
                .setDividerColor(ResourceUtils.getColor(R.color.colorPink))
                .build();
    }

    @Override
    public void onCreate() {
        initTimePicker();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetStatisticsSubscription != null) {
            mGetStatisticsSubscription.unsubscribe();
        }
    }

    @Override
    public void initDate(int bizType) {
        switch (bizType) {
            case PAGE_FILTER_DAY:   //time
                String currentDay = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
                mStartTime = currentDay + " " + SPManager.getInstance().getStatisticsStart();
                if (compareDate(SPManager.getInstance().getStatisticsStart(), SPManager.getInstance().getStatisticsEnd(), DateUtils.DF_HOUR_MIN_SEC) >= 0) {
                    mEndTime = getNextDay(currentDay, DateUtils.DF_YEAR_MONTH_DAY) + " " + SPManager.getInstance().getStatisticsEnd();
                } else {
                    mEndTime = currentDay + " " + SPManager.getInstance().getStatisticsEnd();
                }
                mView.initDayDate();
                mView.setDayPlusEnable(false);
                mView.setDayDate(currentDay);
                break;
            case PAGE_FILTER_WEEK:  //date
                mEndDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
                mStartDate = getFirstDayOfWeek(mEndDate);
                mView.initWeekDate();
                mView.setWeekPlusEnable(false);
                mView.setWeekDate(String.format("%1$s ~ %2$s", mStartDate, mEndDate));
                break;
            case PAGE_FILTER_MONTH: //date
                mEndDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
                mStartDate = getFirstDayOfMonth(mEndDate);
                mView.initMonthDate();
                mView.setMonthPlusEnable(false);
                mView.setMonthDate(DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH));   //显示月份
                break;
            case PAGE_FILTER_TOTAL: //time
                mStartTime = AccountManager.getInstance().getClubCreateTime();
                mEndTime = DateUtils.doDate2String(new Date());
                mView.initTotalDate();
                mView.setTotalDate(String.format("%1$s ~ %2$s", DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY), DateUtils.doString2String(mEndTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY)));
                break;
            case PAGE_FILTER_CUSTOM:    //time
                String currentDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
                mStartTime = DateUtils.doString2String(currentDate, DateUtils.DF_YEAR_MONTH_DAY, DateUtils.DF_DEFAULT);
                mEndTime = DateUtils.doDate2String(new Date());
                mView.initCustomDate();
                mView.setCustomStart(mStartTime);
                mView.setCustomEnd(mEndTime);
                break;
            default:
                break;
        }
    }

    @Override
    public void loadData() {
        mView.showLoading();
        Observable<StatisticsResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTotalStatistics(AccountManager.getInstance().getToken(), mStartDate, mEndDate, mStartTime, mEndTime);
        mGetStatisticsSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<StatisticsResult>() {
            @Override
            public void onCallbackSuccess(StatisticsResult result) {
                mView.hideLoading();
                mView.initDataLayout();
                mOfflineInfo = result.getRespData().offline;
                mOnlineInfo = result.getRespData().online;
                mView.setDataNormal(mOnlineInfo, mOfflineInfo);
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.initDataLayout();
                mView.setDataError(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void loadCustomData() {
        if (TextUtils.isEmpty(mStartTime) || TextUtils.isEmpty(mEndTime) || compareDate(mStartTime, mEndTime, DateUtils.DF_DEFAULT) >= 0) {
            mView.showToast("请设置正确的时间");
            return;
        }
        loadData();
    }

    @Override
    public void onCustomStartPick(View view) {
        // 自定义开始时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(mStartTime));
        mPickerView.setDate(calendar);
        mPickerView.show(view);
    }

    @Override
    public void onCustomEndPick(View view) {
        // 自定义截止时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(mEndTime));
        mPickerView.setDate(calendar);
        mPickerView.show(view);
    }

    @Override
    public void plusDay() {
        // 下一天
        String currentDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
        mStartTime = getNextDay(mStartTime, DateUtils.DF_DEFAULT);
        mEndTime = getNextDay(mEndTime, DateUtils.DF_DEFAULT);
        String selectDate = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
        if (compareDate(selectDate, currentDate, DateUtils.DF_YEAR_MONTH_DAY) >= 0) {
            mView.setDayPlusEnable(false);
        } else {
            mView.setDayPlusEnable(true);
        }
        mView.setDayMinusEnable(true);
        mView.setDayDate(selectDate);
        loadData();
    }

    @Override
    public void minusDay() {
        // 上一天
        String createDate = DateUtils.doString2String(AccountManager.getInstance().getClubCreateTime(), DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
        mStartTime = getLastDay(mStartTime, DateUtils.DF_DEFAULT);
        mEndTime = getLastDay(mEndTime, DateUtils.DF_DEFAULT);
        String selectDate = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
        if (compareDate(selectDate, createDate, DateUtils.DF_YEAR_MONTH_DAY) <= 0) {
            mView.setDayMinusEnable(false);
        } else {
            mView.setDayMinusEnable(true);
        }
        mView.setDayPlusEnable(true);
        mView.setDayDate(selectDate);
        loadData();
    }

    @Override
    public void plusWeek() {
        // 下周
        String currentDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
        mStartDate = getFirstDayOfNextWeek(mStartDate);
        mEndDate = getLastDayOfWeek(mStartDate);
        if (compareDate(mEndDate, currentDate, DateUtils.DF_YEAR_MONTH_DAY) >= 0) {
            mEndDate = currentDate;
            mView.setWeekPlusEnable(false);
        } else {
            mView.setWeekPlusEnable(true);
        }
        mView.setWeekMinusEnable(true);
        mView.setWeekDate(String.format("%1$s ~ %2$s", mStartDate, mEndDate));
        loadData();
    }

    @Override
    public void minusWeek() {
        // 上周
        String createDate = DateUtils.doString2String(AccountManager.getInstance().getClubCreateTime(), DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
        mStartDate = getFirstDayOfLastWeek(mStartDate);
        mEndDate = getLastDayOfWeek(mStartDate);
        if (compareDate(mStartDate, createDate, DateUtils.DF_YEAR_MONTH_DAY) <= 0) {
            mStartDate = createDate;
            mView.setWeekMinusEnable(false);
        } else {
            mView.setWeekMinusEnable(true);
        }
        mView.setWeekPlusEnable(true);
        mView.setWeekDate(String.format("%1$s ~ %2$s", mStartDate, mEndDate));
        loadData();
    }

    @Override
    public void plusMonth() {
        // 下个月
        String currentDate = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
        mStartDate = getFirstDayOfNextMonth(mStartDate);
        mEndDate = getLastDayOfMonth(mStartDate);
        if (compareDate(mEndDate, currentDate, DateUtils.DF_YEAR_MONTH) >= 0) {
            mEndDate = currentDate;
            mView.setMonthPlusEnable(false);
        } else {
            mView.setMonthPlusEnable(true);
        }
        mView.setMonthMinusEnable(true);
        mView.setMonthDate(DateUtils.doString2String(mStartDate, DateUtils.DF_YEAR_MONTH_DAY, DateUtils.DF_YEAR_MONTH));
        loadData();
    }

    @Override
    public void minusMonth() {
        // 上个月
        String createDate = DateUtils.doString2String(AccountManager.getInstance().getClubCreateTime(), DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
        mStartDate = getFirstDayOfLastMonth(mStartDate);
        mEndDate = getLastDayOfMonth(mStartDate);
        if (compareDate(mStartDate, createDate, DateUtils.DF_YEAR_MONTH) <= 0) {
            mStartDate = createDate;
            mView.setMonthMinusEnable(false);
        } else {
            mView.setMonthMinusEnable(true);
        }
        mView.setMonthPlusEnable(true);
        mView.setMonthDate(DateUtils.doString2String(mEndDate, DateUtils.DF_YEAR_MONTH_DAY, DateUtils.DF_YEAR_MONTH));
        loadData();
    }

    @Override
    public void onPrint(int bizType) {
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("对账单");
        mPos.printDivide();
        switch (mStyleType) {
            case STYLE_SETTLE:
                mPos.printBoldText("小摩豆结算营业额汇总", formatAmount(mOnlineInfo.totalSettleAmount));
                mPos.printText("  微信：", formatAmount(mOnlineInfo.totalWx));
                mPos.printText("  支付宝：", formatAmount(mOnlineInfo.totalAli));
                mPos.printText("  银联：", formatAmount(mOnlineInfo.totalUnion));
                mPos.printBoldText("其他交易记账", formatAmount(mOfflineInfo.totalRecharge + mOfflineInfo.cashPos));
                mPos.printText("  微信(记账)：", formatAmount(mOfflineInfo.wxMember));
                mPos.printText("  支付宝(记账)：", formatAmount(mOfflineInfo.aliMember));
                mPos.printText("  银联(记账)：", formatAmount(mOfflineInfo.unionMember));
                mPos.printText("  现金(记账)：", formatAmount(mOfflineInfo.cashMember + mOfflineInfo.cashPos));
                mPos.printText("  其他(记账)：", formatAmount(mOfflineInfo.otherMember));
                mPos.printDivide();
                break;
            case STYLE_MONEY:
                mPos.printBoldText("小摩豆结算营业额", formatAmount(mOnlineInfo.totalAmount));
                mPos.printText("  优惠减免：", "-￥" + Utils.moneyToStringEx(Math.abs(mOnlineInfo.totalDiscount)));
                mPos.printText("  奖金提成(小摩豆代付部分)：", "-￥" + Utils.moneyToStringEx(Math.abs(mOnlineInfo.internalCommission)));
                mPos.printText("  手续费：", "-￥" + Utils.moneyToStringEx(Math.abs(mOnlineInfo.totalSettleFee)));
                mPos.printText("  退款：", "-￥" + Utils.moneyToStringEx(Math.abs(mOnlineInfo.totalRefund)));
                mPos.printText("  光大结算打款金额", formatAmount(mOnlineInfo.totalSettleAmount));
                mPos.printText("    在线买单", formatAmount(mOnlineInfo.fastPay));
                mPos.printText("      工牌扫码买单：", formatAmount(mOnlineInfo.qrTech));
                mPos.printText("      收银水牌买单：", formatAmount(mOnlineInfo.qrClub));
                mPos.printText("      POS买单：", formatAmount(mOnlineInfo.qrPos));
                mPos.printText("    会员充值", formatAmount(mOnlineInfo.recharge));
                mPos.printText("    付费预约", formatAmount(mOnlineInfo.paidOrder));
                mPos.printText("    营销活动收入", formatAmount(mOnlineInfo.marketing));
                mPos.printText("      点钟券：", formatAmount(mOnlineInfo.paidCoupon));
                mPos.printText("      限时抢：", formatAmount(mOnlineInfo.paidServiceItem));
                mPos.printText("    特惠商城收入", formatAmount(mOnlineInfo.mall));
                mPos.printText("      项目次卡：", formatAmount(mOnlineInfo.itemCard));
                mPos.printText("      混合套餐：", formatAmount(mOnlineInfo.itemPackage));
                mPos.printText("  POS结算打款金额", formatAmount(mOnlineInfo.totalUnion));
                mPos.printText("    银联支付：", formatAmount(mOnlineInfo.totalUnion));
                mPos.printBoldText("其他交易金额", formatAmount(mOfflineInfo.totalRecharge + mOfflineInfo.cashPos + mOfflineInfo.totalDiscount + mOfflineInfo.totalRefund));
                mPos.printText("  优惠减免：", "-￥" + Utils.moneyToStringEx(Math.abs(mOfflineInfo.totalDiscount)));
                mPos.printText("  退款：", "-￥" + Utils.moneyToStringEx(Math.abs(mOfflineInfo.totalRefund)));
                mPos.printText("  POS收银", formatAmount(mOfflineInfo.cashPos));
                mPos.printText("    现金支付：", formatAmount(mOfflineInfo.cashPos));
                mPos.printText("  管理者后台(会员充值)", formatAmount(mOfflineInfo.totalRecharge));
                mPos.printText("    微信(记账)：", formatAmount(mOfflineInfo.wxMember));
                mPos.printText("    支付宝(记账)：", formatAmount(mOfflineInfo.aliMember));
                mPos.printText("    银联(记账)：", formatAmount(mOfflineInfo.unionMember));
                mPos.printText("    现金(记账)：", formatAmount(mOfflineInfo.cashMember));
                mPos.printText("    其他(记账)：", formatAmount(mOfflineInfo.otherMember));
                mPos.printDivide();
                break;
            default:
                break;
        }

        switch (bizType) {
            case PAGE_FILTER_DAY:   //天1
            case PAGE_FILTER_CUSTOM:    //自定义
            case PAGE_FILTER_TOTAL: //累计
                mPos.printText("开始时间：", mStartTime);
                mPos.printText("截止时间：", mEndTime);
                break;
            case PAGE_FILTER_WEEK:  // 周
            case PAGE_FILTER_MONTH: // 月
                mPos.printText("开始时间：", mStartDate + " " + AppConstants.STATISTICS_DEFAULT_TIME);
                mPos.printText("截止时间：", getNextDay(mEndDate, DateUtils.DF_YEAR_MONTH_DAY) + " " + AppConstants.STATISTICS_DEFAULT_TIME);
                break;
            default:
                break;
        }
        mPos.printText("收款人员：", AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));
        mPos.printEnd();
    }


    @Override
    public void styleSettle() {
        mView.showSettleStyle();
        mStyleType = STYLE_SETTLE;
    }

    @Override
    public void styleMoney() {
        mView.showMoneyStyle();
        mStyleType = STYLE_MONEY;
    }

    @Override
    public void setStyle() {
        switch (mStyleType) {
            case STYLE_MONEY:
                mView.showMoneyStyle();
                break;
            case STYLE_SETTLE:
            default:
                mView.showSettleStyle();
                break;
        }
    }

    @Override
    public String formatAmount(long amount) {
        return ((amount < 0) ? "-￥" : "￥") + Utils.moneyToStringEx(Math.abs(amount));
    }


    /*********************************************Utils********************************************/
    /**
     * 根据某个日期获取"当月"第一天日期
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getFirstDayOfMonth(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    /**
     * 根据某个日期获取"上个月"第一天日期
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getFirstDayOfLastMonth(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    private String getFirstDayOfNextMonth(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    /**
     * 根据某个日期获取"当月"最后一天
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getLastDayOfMonth(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    /**
     * 根据某个日期获取"本周"周一日期
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getFirstDayOfWeek(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    /**
     * 根据某个日期获取"上周"周一日期
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getFirstDayOfLastWeek(String dateString) {
        Long currentDate = DateUtils.doString2Long(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        currentDate -= DateUtils.WEEK_TIME_MS;
        return getFirstDayOfWeek(DateUtils.doLong2String(currentDate, DateUtils.DF_YEAR_MONTH_DAY));
    }

    private String getFirstDayOfNextWeek(String dateString) {
        Long currentDate = DateUtils.doString2Long(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        currentDate += DateUtils.WEEK_TIME_MS;
        return getFirstDayOfWeek(DateUtils.doLong2String(currentDate, DateUtils.DF_YEAR_MONTH_DAY));
    }

    /**
     * 根据某个日期获取"本周"周日
     *
     * @param dateString yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    private String getLastDayOfWeek(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
        calendar.add(Calendar.DATE, 6);
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

    private String getLastDay(String dateString, String format) {
        Long currentDate = DateUtils.doString2Long(dateString, format);
        currentDate -= DateUtils.DAY_TIME_MS;
        return DateUtils.doLong2String(currentDate, format);
    }

    private String getNextDay(String dateString, String format) {
        Long currentDate = DateUtils.doString2Long(dateString, format);
        currentDate += DateUtils.DAY_TIME_MS;
        return DateUtils.doLong2String(currentDate, format);
    }

    private int compareDate(String date, String otherDate, String format) {
        Long lDate = DateUtils.doString2Long(date, format);
        Long lOtherDate = DateUtils.doString2Long(otherDate, format);
        if (lDate > lOtherDate) {
            return 1;
        } else if (lDate < lOtherDate) {
            return -1;
        } else {
            return 0;
        }
    }
}
