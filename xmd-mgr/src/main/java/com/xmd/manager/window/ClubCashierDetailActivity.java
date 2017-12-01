package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportDetailAdapter;
import com.xmd.manager.beans.CashierClubDetailInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CashierClubDetailListResult;
import com.xmd.manager.service.response.CashierStatisticResult;
import com.xmd.manager.widget.CustomRecycleViewDecoration;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by zr on 17-11-28.
 * 某会所某天买单收银明细
 */

public class ClubCashierDetailActivity extends BaseActivity {
    public static final String EXTRA_CURRENT_TIME = "current_time";
    private static final String EVENT_TYPE = "ClubCashierDetailActivity";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String REQUEST_TYPE_INIT = "init";
    private static final String REQUEST_TYPE_LOAD_MORE = "loadmore";

    private static final String SCOPE_TYPE_SPA = "spa";
    private static final String SCOPE_TYPE_GOODS = "goods";

    private ReportDetailAdapter<CashierClubDetailInfo> mAdapter;

    private String mCurrentDate;
    private int mCurrentPage;
    private int mPageSize;
    private String mScope;
    private String mRequestType;

    private boolean mSpaSelected;
    private boolean mGoodsSelected;

    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoadMore;
    private boolean hasMore;

    private Subscription mGetCashierStatisticAmountSubscription;
    private Subscription mGetCashierClubDetailListSubscription;

    @BindView(R.id.ev_empty)
    EmptyView mEmptyView;

    @BindView(R.id.layout_amount)
    LinearLayout mAmountLayout;
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

    @BindView(R.id.rv_cashier_day_data)
    RecyclerView mCashierDayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_cashier_detail);
        mCurrentDate = getIntent().getStringExtra(EXTRA_CURRENT_TIME);
        ButterKnife.bind(this);
        setTitle(mCurrentDate);

        mScope = null;
        mRequestType = REQUEST_TYPE_INIT;
        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_cashier_sum_title));
        mSpaTitle.setText(ResourceUtils.getString(R.string.report_spa_title));
        mGoodsTitle.setText(ResourceUtils.getString(R.string.report_goods_title));

        mLayoutManager = new LinearLayoutManager(this);
        mCashierDayList.setLayoutManager(mLayoutManager);
        mCashierDayList.setHasFixedSize(true);
        mCashierDayList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mAdapter = new ReportDetailAdapter<>(this);
        mAdapter.setCallBack(new ReportDetailAdapter.CallBack() {
            @Override
            public void onLoadMore() {
                mRequestType = REQUEST_TYPE_LOAD_MORE;
                requestDetailList();
            }

            @Override
            public void onItemClick(Object info) {
                CashierClubDetailInfo detailInfo = (CashierClubDetailInfo) info;
                Intent intent = new Intent(ClubCashierDetailActivity.this, ReportDetailDialogActivity.class);
                intent.putExtra(ReportDetailDialogActivity.EXTRA_TYPE_DETAIL, ReportDetailDialogActivity.TYPE_DETAIL_CASHIER);
                intent.putExtra(ReportDetailDialogActivity.EXTRA_CASHIER_DETAIL_INFO, detailInfo);
                startActivity(intent);
            }
        });
        mCashierDayList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mRequestType = REQUEST_TYPE_LOAD_MORE;
                    requestDetailList();    //获取明细列表
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mCashierDayList.setAdapter(mAdapter);

        mGetCashierStatisticAmountSubscription = RxBus.getInstance().toObservable(CashierStatisticResult.class).subscribe(
                result -> handleCashierStatistic(result)
        );

        mGetCashierClubDetailListSubscription = RxBus.getInstance().toObservable(CashierClubDetailListResult.class).subscribe(
                cashierClubDetailListResult -> handleCashierDetailList(cashierClubDetailListResult)
        );

        requestAmount();
        requestDetailList();
    }

    private void requestAmount() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CASHIER_STATISTIC_INFO, params);
    }

    private void requestDetailList() {
        if (REQUEST_TYPE_LOAD_MORE.equals(mRequestType)) {
            if (isLoadMore || !hasMore) {
                return;
            } else {
                isLoadMore = true;
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_END_DATE, mCurrentDate);
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mCurrentPage));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(mPageSize));
        params.put(RequestConstant.KEY_SCOPE, mScope);
        params.put(RequestConstant.KEY_REQUEST_TYPE, mRequestType);
        params.put(RequestConstant.KEY_EVENT_TYPE, EVENT_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CASHIER_CLUB_DETAIL_LIST, params);
    }

    private void handleCashierStatistic(CashierStatisticResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            if (result.statusCode == 200) {
                mAmountLayout.setVisibility(View.VISIBLE);
                mTotalAmount.setText(Utils.moneyToStringEx(result.respData.amount));
                mSpaAmount.setText("￥" + Utils.moneyToStringEx(result.respData.spaAmount));
                mGoodsAmount.setText("￥" + Utils.moneyToStringEx(result.respData.goodsAmount));
            } else {
                mAmountLayout.setVisibility(View.GONE);
            }
        }
    }

    private void handleCashierDetailList(CashierClubDetailListResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            if (result.statusCode == 200) {
                switch (result.requestType) {
                    case REQUEST_TYPE_INIT:
                        mEmptyView.setVisibility(View.GONE);
                        mAdapter.clearData();
                        mCashierDayList.removeAllViews();
                        if (result.respData != null && !result.respData.isEmpty()) {
                            mCashierDayList.setVisibility(View.VISIBLE);
                            if (mCurrentPage < result.pageCount) {
                                mCurrentPage++;
                                hasMore = true;
                                mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_SUCCESS);
                            } else {
                                hasMore = false;
                                mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_NONE);
                            }
                            mAdapter.setData(result.respData);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mCashierDayList.setVisibility(View.GONE);
                        }
                        break;
                    case REQUEST_TYPE_LOAD_MORE:
                        if (mCurrentPage < result.pageCount) {
                            mCurrentPage++;
                            hasMore = true;
                            mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_SUCCESS);
                        } else {
                            hasMore = false;
                            mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_NONE);
                        }
                        isLoadMore = false;
                        mAdapter.setData(result.respData);
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            } else {
                switch (result.requestType) {
                    case REQUEST_TYPE_INIT:
                        mEmptyView.setVisibility(View.GONE);
                        mCashierDayList.removeAllViews();
                        mCashierDayList.setVisibility(View.GONE);
                        break;
                    case REQUEST_TYPE_LOAD_MORE:
                        mAdapter.setStatus(ReportDetailAdapter.FOOTER_STATUS_ERROR);
                        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCashierClubDetailListSubscription, mGetCashierStatisticAmountSubscription);
    }

    @OnClick({R.id.layout_left_data, R.id.layout_right_data})
    public void onLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.layout_left_data:
                mSpaSelected = !mSpaSelected;
                if (mSpaSelected) {
                    mGoodsSelected = !mSpaSelected;
                    mScope = SCOPE_TYPE_SPA;
                } else {
                    mScope = null;
                }
                break;
            case R.id.layout_right_data:
                mGoodsSelected = !mGoodsSelected;
                if (mGoodsSelected) {
                    mSpaSelected = !mGoodsSelected;
                    mScope = SCOPE_TYPE_GOODS;
                } else {
                    mScope = null;
                }
                break;
            default:
                break;
        }
        updateSpaLayout(mSpaSelected);
        updateGoodsLayout(mGoodsSelected);

        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;
        mRequestType = REQUEST_TYPE_INIT;
        requestDetailList();
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
}
