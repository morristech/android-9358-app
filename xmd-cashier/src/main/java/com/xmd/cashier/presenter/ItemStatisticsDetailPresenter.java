package com.xmd.cashier.presenter;

import android.content.Context;
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
import com.xmd.cashier.contract.ItemStatisticsDetailContract;
import com.xmd.cashier.dal.bean.ItemStatisticsInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.ItemStatisticsResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-12-11.
 */

public class ItemStatisticsDetailPresenter implements ItemStatisticsDetailContract.Presenter {
    private Context mContext;
    private ItemStatisticsDetailContract.View mView;

    private IPos mPos;
    private Subscription mGetItemStatisticsSubscription;

    private static final int PAGE_FILTER_DAY = 0;
    private static final int PAGE_FILTER_MONTH = 1;
    private static final int PAGE_FILTER_CUSTOM = 2;

    public static final int STYLE_SUMMARY = 0;
    public static final int STYLE_DETAIL = 1;
    private int mStyleTag = STYLE_SUMMARY;

    private TimePickerView mPickerView;

    private String mStartTime;
    private String mEndTime;

    private List<ItemStatisticsInfo> mStatisticsData;

    public ItemStatisticsDetailPresenter(Context context, ItemStatisticsDetailContract.View view) {
        mContext = context;
        mView = view;
        mPos = PosFactory.getCurrentCashier();
        mView.setPresenter(this);
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
        if (mGetItemStatisticsSubscription != null) {
            mGetItemStatisticsSubscription.unsubscribe();
        }
    }


    private void initTimePicker() {
        mPickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtils.doDate2String(date);
                switch (v.getId()) {
                    case R.id.tv_custom_start:
                        mStartTime = time;
                        mView.setCustomStart(mStartTime);
                        break;
                    case R.id.tv_custom_end:
                        mEndTime = time;
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
    public void initData(int bizType) {
        String currentDay = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH_DAY);
        switch (bizType) {
            case PAGE_FILTER_DAY:
                mStartTime = currentDay + " " + InnerManager.getInstance().getStartTime();
                mEndTime = getNextDay(currentDay, DateUtils.DF_YEAR_MONTH_DAY) + " " + InnerManager.getInstance().getStartTime();
                mView.initDay(currentDay, mStartTime, mEndTime);
                break;
            case PAGE_FILTER_MONTH:
                String currentMonth = DateUtils.doDate2String(new Date(), DateUtils.DF_YEAR_MONTH);
                mStartTime = getFirstDayOfMonth(currentDay) + " " + InnerManager.getInstance().getStartTime();
                mEndTime = getFirstDayOfNextMonth(currentDay) + " " + InnerManager.getInstance().getStartTime();
                mView.initMonth(currentMonth, mStartTime, mEndTime);
                break;
            case PAGE_FILTER_CUSTOM:
                mStartTime = getFirstDayOfMonth(currentDay) + " " + InnerManager.getInstance().getStartTime();
                mEndTime = currentDay + " " + InnerManager.getInstance().getStartTime();
                mView.initCustom(mStartTime, mEndTime);
                break;
        }
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

    private String getFirstDayOfMonth(String dateString) {
        Date date = DateUtils.doString2Date(dateString, DateUtils.DF_YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return DateUtils.doDate2String(calendar.getTime(), DateUtils.DF_YEAR_MONTH_DAY);
    }

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

    @Override
    public void loadData() {
        mView.showLoading();
        Observable<ItemStatisticsResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getItemStatistics(AccountManager.getInstance().getToken(), mStartTime, mEndTime);
        mGetItemStatisticsSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<ItemStatisticsResult>() {
            @Override
            public void onCallbackSuccess(ItemStatisticsResult result) {
                mView.hideLoading();
                mStatisticsData = result.getRespData();
                if (mStatisticsData != null && !mStatisticsData.isEmpty()) {
                    mView.showDataSuccess();
                    mView.showStyle(mStyleTag);
                    formatData(mStatisticsData);
                    mView.showData(mStatisticsData, mStyleTag);
                } else {
                    mView.showDataEmpty();
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.hideLoading();
                mView.showDataError(e.getLocalizedMessage());
            }
        });
    }

    private void formatData(List<ItemStatisticsInfo> list) {
        for (ItemStatisticsInfo itemStatisticsInfo : list) {
            int tempSum = 0;
            long tempAmount = 0;
            for (ItemStatisticsInfo.CategoryItem categoryItem : itemStatisticsInfo.list) {
                tempSum += categoryItem.sum;
                tempAmount += categoryItem.amount;
            }
            itemStatisticsInfo.totalSum = tempSum;
            itemStatisticsInfo.totalAmount = tempAmount;
        }
    }

    @Override
    public void onSelectMinus(int bizType) {
        switch (bizType) {
            case PAGE_FILTER_DAY:
                mStartTime = getLastDay(mStartTime, DateUtils.DF_DEFAULT);
                mEndTime = getLastDay(mEndTime, DateUtils.DF_DEFAULT);
                String showTime = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
                mView.initDay(showTime, mStartTime, mEndTime);
                break;
            case PAGE_FILTER_MONTH:
                String currentMonth = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
                mEndTime = mStartTime;
                mStartTime = getFirstDayOfLastMonth(currentMonth) + " " + InnerManager.getInstance().getStartTime();
                mView.initMonth(DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH), mStartTime, mEndTime);
                break;
            default:
                break;
        }
        loadData();
    }

    @Override
    public void onSelectPlus(int bizType) {
        switch (bizType) {
            case PAGE_FILTER_DAY:
                mStartTime = getNextDay(mStartTime, DateUtils.DF_DEFAULT);
                mEndTime = getNextDay(mEndTime, DateUtils.DF_DEFAULT);
                String showTime = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
                mView.initDay(showTime, mStartTime, mEndTime);
                break;
            case PAGE_FILTER_MONTH:
                mStartTime = mEndTime;
                String nextMonth = DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH_DAY);
                mEndTime = getFirstDayOfNextMonth(nextMonth) + " " + InnerManager.getInstance().getStartTime();
                mView.initMonth(DateUtils.doString2String(mStartTime, DateUtils.DF_DEFAULT, DateUtils.DF_YEAR_MONTH), mStartTime, mEndTime);
                break;
            default:
                break;
        }
        loadData();
    }

    @Override
    public void onCustomStartPicker(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(mStartTime));
        mPickerView.setDate(calendar);
        mPickerView.show(view);
    }

    @Override
    public void onCustomEndPicker(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.doString2Date(mEndTime));
        mPickerView.setDate(calendar);
        mPickerView.show(view);
    }

    @Override
    public void onCustomConfirm() {
        long start = DateUtils.stringDateToLong(mStartTime);
        long end = DateUtils.stringDateToLong(mEndTime);
        if (start > end) {
            mView.showToast("开始时间大于结束时间");
            return;
        } else {
            mView.initCustom(mStartTime, mEndTime);
            loadData();
        }
    }

    @Override
    public void onStyleChange() {
        switch (mStyleTag) {
            case STYLE_DETAIL:
                mStyleTag = STYLE_SUMMARY;
                break;
            case STYLE_SUMMARY:
                mStyleTag = STYLE_DETAIL;
                break;
            default:
                break;
        }
        mView.showStyle(mStyleTag);
        mView.updateDataByStyle(mStyleTag);
    }

    @Override
    public void onPrint() {
        mPos.printCenter("经营项目汇总", true);
        mPos.printText("\n");
        long amount = 0;
        for (ItemStatisticsInfo itemStatisticsInfo : mStatisticsData) {
            amount += itemStatisticsInfo.totalAmount;
        }
        mPos.printText("订单总金额", "￥" + Utils.moneyToStringEx(amount));
        for (ItemStatisticsInfo itemStatisticsInfo : mStatisticsData) {
            mPos.printText(itemStatisticsInfo.categoryName + "   " + itemStatisticsInfo.totalSum, "￥" + Utils.moneyToStringEx(itemStatisticsInfo.totalAmount), true);
            for (ItemStatisticsInfo.CategoryItem categoryItem : itemStatisticsInfo.list) {
                mPos.printText("   " + categoryItem.name + "   " + categoryItem.sum, "￥" + Utils.moneyToStringEx(categoryItem.amount));
                if (mStyleTag == STYLE_DETAIL && AppConstants.INNER_ORDER_ITEM_TYPE_SPA.equals(itemStatisticsInfo.type)) {
                    for (ItemStatisticsInfo.CategoryItemBell categoryItemBell : categoryItem.bellList) {
                        mPos.printText("      " + categoryItemBell.bellName + "   " + categoryItemBell.bellCount);
                    }
                }
            }
        }
        mPos.printText("开始时间：" + mStartTime);
        mPos.printText("结束时间：" + mEndTime);
        mPos.printText("打印时间：" + DateUtils.doDate2String(new Date()));
        mPos.printText("打印人员：" + AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")");
        mPos.printEnd();
    }
}
