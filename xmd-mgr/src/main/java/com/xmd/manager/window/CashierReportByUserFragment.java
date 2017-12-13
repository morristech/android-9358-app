package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportNormalAdapter;
import com.xmd.manager.beans.CashierNormalInfo;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CashierStatisticResult;
import com.xmd.manager.widget.CustomRecycleViewDecoration;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by zr on 17-11-21.
 * 买单收银按自定义时间查看
 */

public class CashierReportByUserFragment extends BaseFragment {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final String EVENT_TYPE = "CashierReportByUserFragment";
    private Unbinder unbinder;
    private View view;

    @BindView(R.id.tv_start_time)
    TextView mStartTime;
    @BindView(R.id.tv_end_time)
    TextView mEndTime;

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

    @BindView(R.id.layout_left_data)
    LinearLayout mSpaLayout;
    @BindView(R.id.tv_left_title)
    TextView mSpaTitle;
    @BindView(R.id.tv_left_content)
    TextView mSpaAmount;
    @BindView(R.id.layout_right_data)
    LinearLayout mGoodsLayout;
    @BindView(R.id.tv_right_title)
    TextView mGoodsTitle;
    @BindView(R.id.tv_right_content)
    TextView mGoodsAmount;
    @BindView(R.id.tv_total_title)
    TextView mTotalTitle;
    @BindView(R.id.tv_total_content)
    TextView mTotalAmount;

    @BindView(R.id.rv_cashier_custom_data)
    RecyclerView mCashierCustomList;

    private boolean isInit;
    private boolean isLoad;

    private String mStartDate;
    private String mEndDate;

    private boolean mSpaSelected;
    private boolean mGoodsSelected;

    private ReportNormalAdapter<CashierNormalInfo> mAdapter;
    private Map<String, String> mParams;

    private Subscription mGetCashierStatisticSubscription;

    private List<CashierNormalInfo> mDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cashier_report_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        isInit = true;
        return view;
    }

    @Override
    protected void initView() {
        mEndDate = DateUtil.getCurrentDate();
        mStartDate = DateUtil.getFirstDayOfMonth(mEndDate, FORMAT);
        mStartTime.setText(mStartDate);
        mEndTime.setText(mEndDate);

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mEmptyView.setOnRefreshListener(() -> {
            mEmptyView.setStatus(EmptyView.Status.Loading);
            dispatchRequest();
        });

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_cashier_sum_title));
        mSpaTitle.setText(ResourceUtils.getString(R.string.report_spa_title));
        mGoodsTitle.setText(ResourceUtils.getString(R.string.report_goods_title));

        mCashierCustomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCashierCustomList.setHasFixedSize(true);
        mCashierCustomList.addItemDecoration(new CustomRecycleViewDecoration(2));
        mAdapter = new ReportNormalAdapter<>(getActivity());
        mAdapter.setCallBack(date -> {
            Intent intent = new Intent(getActivity(), ClubCashierDetailActivity.class);
            intent.putExtra(ClubCashierDetailActivity.EXTRA_CURRENT_TIME, date);
            startActivity(intent);
        });
        mCashierCustomList.setAdapter(mAdapter);

        mGetCashierStatisticSubscription = RxBus.getInstance().toObservable(CashierStatisticResult.class).subscribe(
                result -> handleCashierStatisticResult(result)
        );

        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        initData();
    }

    private void initData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint() && !isLoad) {
            isLoad = true;
            dispatchRequest();
        }
    }

    private void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_START_DATE, mStartDate);
        mParams.put(RequestConstant.KEY_END_DATE, mEndDate);
        mParams.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CASHIER_STATISTIC_INFO, mParams);
    }

    private void handleCashierStatisticResult(CashierStatisticResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            if (result.statusCode == 200) {
                mDataList.addAll(result.respData.list);
                mEmptyView.setVisibility(View.GONE);
                mTotalAmount.setText(Utils.moneyToStringEx(result.respData.amount));
                mSpaAmount.setText(Utils.moneyToStringEx(result.respData.spaAmount));
                mGoodsAmount.setText(Utils.moneyToStringEx(result.respData.goodsAmount));
                mCashierCustomList.removeAllViews();
                mAdapter.clearData();
                mAdapter.setScope(ReportNormalAdapter.SCOPE_TYPE_ALL);
                mAdapter.setData(mDataList);
            } else {
                mEmptyView.setStatus(EmptyView.Status.Failed);
                mEmptyView.setEmptyTip(result.msg);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        RxBus.getInstance().unsubscribe(mGetCashierStatisticSubscription);
        unbinder.unbind();
    }

    @OnClick(R.id.tv_start_time)
    public void onStartClick() {
        DateTimePickDialog startPicker = new DateTimePickDialog(getActivity(), mStartTime.getText().toString());
        startPicker.dateTimePicKDialog(mStartTime);
    }

    @OnClick(R.id.tv_end_time)
    public void onEndClick() {
        DateTimePickDialog endPicker = new DateTimePickDialog(getActivity(), mEndTime.getText().toString());
        endPicker.dateTimePicKDialog(mEndTime);
    }

    @OnClick(R.id.btn_confirm_time)
    public void onTimeConfirm() {
        mStartDate = mStartTime.getText().toString();
        mEndDate = mEndTime.getText().toString();
        long start = DateUtil.dateToLong(mStartDate);
        long end = DateUtil.dateToLong(mEndDate);
        if (start > end) {
            XToast.show("开始时间大于结束时间");
            return;
        } else {
            mSpaSelected = false;
            mGoodsSelected = false;
            updateGoodsLayout(mGoodsSelected);
            updateSpaLayout(mSpaSelected);
            mCashierCustomList.removeAllViews();
            mDataList.clear();
            mAdapter.clearData();
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setStatus(EmptyView.Status.Loading);
            dispatchRequest();
        }
    }

    @OnClick({R.id.layout_left_data, R.id.layout_right_data})
    public void onLayoutClick(View view) {
        mCashierCustomList.removeAllViews();
        mAdapter.clearData();
        switch (view.getId()) {
            case R.id.layout_left_data:
                mSpaSelected = !mSpaSelected;
                if (mSpaSelected) {
                    mGoodsSelected = !mSpaSelected;
                    mAdapter.setScope(ReportNormalAdapter.SCOPE_TYPE_SPA);
                    mAdapter.setData(getSpaCashierDataList(mDataList));
                } else {
                    mAdapter.setScope(ReportNormalAdapter.SCOPE_TYPE_ALL);
                    mAdapter.setData(mDataList);
                }
                break;
            case R.id.layout_right_data:
                mGoodsSelected = !mGoodsSelected;
                if (mGoodsSelected) {
                    mSpaSelected = !mGoodsSelected;
                    mAdapter.setScope(ReportNormalAdapter.SCOPE_TYPE_GOODS);
                    mAdapter.setData(getGoodsCashierDataList(mDataList));
                } else {
                    mAdapter.setScope(ReportNormalAdapter.SCOPE_TYPE_ALL);
                    mAdapter.setData(mDataList);
                }
                break;
            default:
                break;
        }
        updateSpaLayout(mSpaSelected);
        updateGoodsLayout(mGoodsSelected);
    }

    private void updateSpaLayout(boolean selected) {
        if (selected) {
            mSpaLayout.setBackgroundResource(R.drawable.bg_report_select);
            mSpaAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mSpaTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mSpaLayout.setBackgroundResource(R.color.colorWhite);
            mSpaAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mSpaTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }

    private void updateGoodsLayout(boolean selected) {
        if (selected) {
            mGoodsLayout.setBackgroundResource(R.drawable.bg_report_select);
            mGoodsAmount.setTextColor(ResourceUtils.getColor(R.color.colorStatusYellow));
            mGoodsTitle.setTextColor(ResourceUtils.getColor(R.color.colorText5));
        } else {
            mGoodsLayout.setBackgroundResource(R.color.colorWhite);
            mGoodsAmount.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
            mGoodsTitle.setTextColor(ResourceUtils.getColor(R.color.colorText3));
        }
    }

    private List<CashierNormalInfo> getSpaCashierDataList(List<CashierNormalInfo> list) {
        List<CashierNormalInfo> tempList = new ArrayList<>();
        for (CashierNormalInfo cashierNormalInfo : list) {
            if (cashierNormalInfo.spaAmount > 0) {
                tempList.add(cashierNormalInfo);
            }
        }
        return tempList;
    }

    private List<CashierNormalInfo> getGoodsCashierDataList(List<CashierNormalInfo> list) {
        List<CashierNormalInfo> tempList = new ArrayList<>();
        for (CashierNormalInfo cashierNormalInfo : list) {
            if (cashierNormalInfo.goodsAmount > 0) {
                tempList.add(cashierNormalInfo);
            }
        }
        return tempList;
    }
}
