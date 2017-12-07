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

import com.xmd.app.Constants;
import com.xmd.manager.R;
import com.xmd.manager.adapter.ReportDetailAdapter;
import com.xmd.manager.beans.CashierClubDetailInfo;
import com.xmd.manager.common.DateUtil;
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
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by zr on 17-11-21.
 * 买单收银按日查看
 */

public class CashierReportByDayFragment extends BaseFragment {
    private static final String FORMAT = "yyyy-MM-dd";
    private static final String EVENT_TYPE = "CashierReportByDayFragment";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String REQUEST_TYPE_INIT = "init";
    private static final String REQUEST_TYPE_LOAD_MORE = "loadmore";

    private static final String SCOPE_TYPE_SPA = "spa";
    private static final String SCOPE_TYPE_GOODS = "goods";

    @BindView(R.id.tv_show_time)
    TextView mShowTime;

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

    private Unbinder unbinder;
    private View view;

    private boolean isInit; //fragment是否已初始化
    private boolean isLoad; //是否正在加载数据

    private boolean mSpaSelected;
    private boolean mGoodsSelected;

    private ReportDetailAdapter<CashierClubDetailInfo> mAdapter;

    private String mCurrentDate;
    private int mCurrentPage;
    private int mPageSize;
    private String mScope;
    private String mRequestType;

    private int mLastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoadMore;
    private boolean hasMore;

    private Subscription mGetCashierStatisticAmountSubscription;
    private Subscription mGetCashierClubDetailListSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cashier_report_day, container, false);
        unbinder = ButterKnife.bind(this, view);
        isInit = true;
        return view;
    }

    @Override
    protected void initView() {
        mScope = null;
        mRequestType = REQUEST_TYPE_INIT;
        mPageSize = DEFAULT_PAGE_SIZE;
        mCurrentPage = 1;
        mCurrentDate = DateUtil.getCurrentDate();
        mShowTime.setText(mCurrentDate);

        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);

        mTotalTitle.setText(ResourceUtils.getString(R.string.report_cashier_sum_title));
        mSpaTitle.setText(ResourceUtils.getString(R.string.report_spa_title));
        mGoodsTitle.setText(ResourceUtils.getString(R.string.report_goods_title));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCashierDayList.setLayoutManager(mLayoutManager);
        mCashierDayList.setHasFixedSize(true);
        mCashierDayList.addItemDecoration(new CustomRecycleViewDecoration(1));
        mAdapter = new ReportDetailAdapter<>(getActivity());
        mAdapter.setCallBack(new ReportDetailAdapter.CallBack() {
            @Override
            public void onLoadMore() {
                mRequestType = REQUEST_TYPE_LOAD_MORE;
                requestDetailList();
            }

            @Override
            public void onItemClick(Object info) {
                CashierClubDetailInfo detailInfo = (CashierClubDetailInfo) info;
                Intent intent = new Intent(getActivity(), ReportDetailDialogActivity.class);
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

        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        initData();
    }

    private void handleCashierStatistic(CashierStatisticResult result) {
        if (EVENT_TYPE.equals(result.eventType)) {
            mEmptyView.setVisibility(View.GONE);
            isLoad = false;
            if (result.statusCode == 200) {
                mAmountLayout.setVisibility(View.VISIBLE);
                mTotalAmount.setText(Utils.moneyToStringEx(result.respData.amount));
                mSpaAmount.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(result.respData.spaAmount));
                mGoodsAmount.setText(Constants.MONEY_TAG + Utils.moneyToStringEx(result.respData.goodsAmount));
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
                        isLoad = false;
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
                        isLoad = false;
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

    private void initData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint() && !isLoad) {
            isLoad = true;
            dispatchRequest();
        }
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

    private void dispatchRequest() {
        requestAmount();
        requestDetailList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        RxBus.getInstance().unsubscribe(mGetCashierClubDetailListSubscription, mGetCashierStatisticAmountSubscription);
        unbinder.unbind();
    }

    @OnClick({R.id.tv_reduce_time, R.id.tv_add_time})
    public void onTimeChange(View view) {
        switch (view.getId()) {
            case R.id.tv_reduce_time:
                mCurrentDate = DateUtil.getLastDate(DateUtil.stringDateToLong(mCurrentDate), FORMAT);
                mShowTime.setText(mCurrentDate);
                break;
            case R.id.tv_add_time:
                mCurrentDate = DateUtil.getNextDate(DateUtil.stringDateToLong(mCurrentDate), FORMAT);
                mShowTime.setText(mCurrentDate);
                break;
            default:
                break;
        }

        updateGoodsLayout(false);
        updateSpaLayout(false);
        mAdapter.clearData();
        mCashierDayList.removeAllViews();
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mCurrentPage = 1;
        mPageSize = DEFAULT_PAGE_SIZE;
        mRequestType = REQUEST_TYPE_INIT;
        mScope = null;
        initData();
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
