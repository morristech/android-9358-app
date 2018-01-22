package com.xmd.cashier.presenter;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.SettleRecordContract;
import com.xmd.cashier.dal.bean.SettleRecordInfo;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.SettleManager;

import java.util.Calendar;
import java.util.Date;

import rx.Subscription;

/**
 * Created by zr on 17-3-29.
 */

public class SettleRecordPresenter implements SettleRecordContract.Presenter {
    private Context mContext;
    private SettleRecordContract.View mView;

    private Subscription mGetSettleRecordSubscription;

    private TimePickerView mPickerView;

    private int mPageIndex;
    private boolean hasMore;
    private boolean isLoadingMore;
    private String mSettleYM;

    public SettleRecordPresenter(Context context, SettleRecordContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mSettleYM = null;
        initTimePicker();
        loadInit();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
    }

    private void initTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, 2014);
        startDate.set(Calendar.MONTH, 0);

        mPickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mSettleYM = DateUtils.doDate2String(date, DateUtils.DF_YEAR_MONTH);
                loadInit();
                mPickerView.dismiss();
            }
        })
                .setDate(currentDate)
                .setRangDate(startDate, currentDate)
                .setLayoutRes(R.layout.layout_picker_view, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        TextView tvChoice = (TextView) v.findViewById(R.id.tv_picker_choice);
                        tvChoice.setText("全部");
                        TextView tvFinish = (TextView) v.findViewById(R.id.tv_picker_finish);
                        tvChoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSettleYM = null;
                                loadInit();
                                mPickerView.dismiss();
                            }
                        });

                        tvFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mPickerView.returnData();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, false, false, false, false})
                .isCenterLabel(false)
                .setTextColorCenter(ResourceUtils.getColor(R.color.colorPink))
                .setDividerColor(ResourceUtils.getColor(R.color.colorPink))
                .build();
    }

    @Override
    public void loadInit() {
        mView.clearData();
        mView.showLoadIng();
        mPageIndex = AppConstants.APP_LIST_DEFAULT_PAGE;
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showLoadNoNetwork();
            return;
        }
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
        mGetSettleRecordSubscription = SettleManager.getInstance().getSettleRecord(mSettleYM, String.valueOf(mPageIndex), new Callback<SettleRecordResult>() {
            @Override
            public void onSuccess(SettleRecordResult o) {
                mView.hideLoading();
                if (o.getRespData() != null && !o.getRespData().isEmpty()) {
                    mView.showLoadSuccess();
                    if (mPageIndex < o.getPageCount()) {
                        hasMore = true;
                        mPageIndex++;
                        mView.showMoreSuccess();
                    } else {
                        hasMore = false;
                        mView.showMoreNone();
                    }
                    mView.showData(o.getRespData());
                } else {
                    mView.showLoadEmpty();
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showLoadError();
            }
        });
    }

    @Override
    public void loadMore() {
        if (!hasMore || isLoadingMore) {
            return;
        }
        mView.showMoreIng();
        isLoadingMore = true;
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showMoreNoNetwork();
            return;
        }
        if (mGetSettleRecordSubscription != null) {
            mGetSettleRecordSubscription.unsubscribe();
        }
        mGetSettleRecordSubscription = SettleManager.getInstance().getSettleRecord(mSettleYM, String.valueOf(mPageIndex), new Callback<SettleRecordResult>() {
            @Override
            public void onSuccess(SettleRecordResult o) {
                isLoadingMore = false;
                if (mPageIndex < o.getPageCount()) {
                    mPageIndex++;
                    hasMore = true;
                    mView.showMoreSuccess();
                } else {
                    hasMore = false;
                    mView.showMoreNone();
                }
                mView.showData(o.getRespData());
            }

            @Override
            public void onError(String error) {
                isLoadingMore = false;
                mView.showMoreError();
            }
        });
    }

    @Override
    public void onRecordClick(SettleRecordInfo recordInfo) {
        UiNavigation.gotoSettleDetailActivity(mContext, recordInfo);
    }

    @Override
    public void onPickView() {
        mPickerView.show();
    }
}
